package kr.co.seoulit.his.billingservice.charge;

import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingRepository;
import kr.co.seoulit.his.billingservice.businessdelegate.admin.AdminBusinessDelegate;
import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;
import kr.co.seoulit.his.billingservice.charge.service.BillingChargeService;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.master.entity.BillingMasterEntity;
import kr.co.seoulit.his.billingservice.master.repository.BillingMasterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// 병동이 보내는 입원료(수가코드 + 입원일수)가 createCharge -> calc_charge_amount 프로시저를 거쳐
// billing / billing_detail에 올바른 금액으로 저장되는지 실제 DB로 확인한다. @Transactional이라 테스트가 끝나면 롤백된다.
@SpringBootTest(properties = "app.kafka.enabled=false")
@Transactional
class BillingChargeProcedureIT {

    @Autowired BillingChargeService chargeService;
    @Autowired BillingMasterRepository masterRepository;
    @Autowired BillingRepository billingRepository;
    @Autowired JdbcTemplate jdbc;
    @MockitoBean AdminBusinessDelegate adminBusinessDelegate;

    private static final String ADMISSION_ID = "TEST-ADM-" + System.nanoTime();

    private BillingChargeRequestDTO request(BillingMasterEntity master, String quantity) {
        BillingChargeRequestDTO dto = new BillingChargeRequestDTO();
        dto.setPatientId("TEST-PATIENT");
        dto.setAdmissionId(ADMISSION_ID);
        dto.setSourceServiceCode(master.getSourceServiceCode()); // admin 공통코드 FK 때문에 실제 존재하는 코드 사용
        dto.setSourceRecordId("TEST-REC-1");
        dto.setFeeCode(master.getFeeCode());
        dto.setItemName("입원료 테스트");
        dto.setQuantity(quantity);
        dto.setAmount("0"); // 호출자가 보낸 금액은 무시되어야 한다
        return dto;
    }

    @Test
    void 입원일수로_금액이_계산되어_저장되고_같은_입원건은_합산된다() {
        when(adminBusinessDelegate.isActiveCode(anyString(), anyString())).thenReturn(true);
        BillingMasterEntity master = masterRepository.findByFeeCode("FEE011").orElseThrow();
        BigDecimal unit = master.getDefaultPrice();

        chargeService.createCharge(request(master, "3"));

        List<Map<String, Object>> details = jdbc.queryForList(
                "SELECT d.unit_price, d.quantity, d.amount, d.detail_status FROM billing_detail d "
                        + "JOIN billing b ON b.billing_id = d.billing_id WHERE b.admission_id = ?", ADMISSION_ID);
        assertEquals(1, details.size());
        Map<String, Object> d = details.get(0);
        assertEquals(0, unit.compareTo(new BigDecimal(d.get("UNIT_PRICE").toString())));
        assertEquals(0, unit.multiply(BigDecimal.valueOf(3)).compareTo(new BigDecimal(d.get("AMOUNT").toString())));
        System.out.println("[IT] detail row: " + d);

        BillingEntity billing = billingRepository.findByAdmissionIdAndBillingStatus(ADMISSION_ID, "READY").orElseThrow();
        System.out.println("[IT] header after 3 days: total=" + billing.getTotalAmount()
                + " patient=" + billing.getPatientAmount() + " insurance=" + billing.getInsuranceAmount());
        assertEquals(0, unit.multiply(BigDecimal.valueOf(3)).compareTo(new BigDecimal(billing.getTotalAmount())));

        BillingChargeRequestDTO second = request(master, "2");
        second.setSourceRecordId("TEST-REC-2"); // 병동은 건마다 다른 기록ID를 보낸다 (유니크 제약)
        chargeService.createCharge(second); // 같은 입원 건에 2일 더 -> 헤더 합산
        BillingEntity merged = billingRepository.findByAdmissionIdAndBillingStatus(ADMISSION_ID, "READY").orElseThrow();
        System.out.println("[IT] header after +2 days: total=" + merged.getTotalAmount());
        assertEquals(0, unit.multiply(BigDecimal.valueOf(5)).compareTo(new BigDecimal(merged.getTotalAmount())));
    }

    @Test
    void 입원일수가_0이하면_오류() {
        when(adminBusinessDelegate.isActiveCode(anyString(), anyString())).thenReturn(true);
        BillingMasterEntity master = masterRepository.findByFeeCode("FEE011").orElseThrow();
        BusinessException e = assertThrows(BusinessException.class,
                () -> chargeService.createCharge(request(master, "0")));
        assertEquals(ErrorCode.BILLING_CHARGE_QUANTITY_INVALID, e.getErrorCode());
    }

    @Test
    void 없는_수가코드면_오류() {
        when(adminBusinessDelegate.isActiveCode(anyString(), anyString())).thenReturn(true);
        BillingMasterEntity master = masterRepository.findByFeeCode("FEE011").orElseThrow();
        BillingChargeRequestDTO dto = request(master, "3");
        dto.setFeeCode("NO_SUCH_FEE");
        BusinessException e = assertThrows(BusinessException.class, () -> chargeService.createCharge(dto));
        assertEquals(ErrorCode.BILLING_FEE_CODE_NOT_FOUND, e.getErrorCode());
    }
}
