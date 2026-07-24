package kr.co.seoulit.his.billingservice.master.mapper;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.master.dto.BillingMasterDTO;
import kr.co.seoulit.his.billingservice.master.entity.BillingMasterEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BillingMasterMapper {

    public BillingMasterDTO toDto(BillingMasterEntity entity) {
        return BillingMasterDTO.builder()
                .billingMasterId(entity.getBillingMasterId())
                .sourceServiceCode(entity.getSourceServiceCode())
                .feeCode(entity.getFeeCode())
                .feeName(entity.getFeeName())
                .defaultPrice(entity.getDefaultPrice() == null ? null : entity.getDefaultPrice().toString())
                .categoryCode(entity.getCategoryCode())
                .insuranceTypeCode(entity.getInsuranceTypeCode())
                .effectiveFrom(entity.getEffectiveFrom() == null ? null : entity.getEffectiveFrom().toString())
                .effectiveTo(entity.getEffectiveTo() == null ? null : entity.getEffectiveTo().toString())
                .useYn(entity.getUseYn())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<BillingMasterDTO> toDto(List<BillingMasterEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BillingMasterEntity toEntity(BillingMasterDTO dto) {
        return BillingMasterEntity.builder()
                .billingMasterId(dto.getBillingMasterId())
                .sourceServiceCode(dto.getSourceServiceCode())
                .feeCode(dto.getFeeCode())
                .feeName(dto.getFeeName())
                .defaultPrice(dto.getDefaultPrice() == null ? null : new BigDecimal(dto.getDefaultPrice()))
                .categoryCode(dto.getCategoryCode())
                .insuranceTypeCode(dto.getInsuranceTypeCode())
                .effectiveFrom(dto.getEffectiveFrom() == null ? null : parseDateTime(dto.getEffectiveFrom()))
                .effectiveTo(dto.getEffectiveTo() == null ? null : parseDateTime(dto.getEffectiveTo()))
                .useYn(dto.getUseYn())
                .build();
    }

    // "yyyy-MM-dd" (날짜만) 와 "yyyy-MM-dd'T'HH:mm:ss" (전체 일시) 형식을 모두 지원.
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value).atStartOfDay();
            } catch (DateTimeParseException e2) {
                throw new BusinessException(ErrorCode.BILLING_DATE_FORMAT_INVALID);
            }
        }
    }
}

// MapStruct entity<->dto 변환을 위해서 선언