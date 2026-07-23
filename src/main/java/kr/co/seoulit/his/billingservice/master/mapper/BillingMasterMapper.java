package kr.co.seoulit.his.billingservice.master.mapper;

import kr.co.seoulit.his.billingservice.master.dto.BillingDTO;
import kr.co.seoulit.his.billingservice.master.entity.BillingEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BillingMapper {

    public BillingDTO toDto(BillingEntity entity) {
        return BillingDTO.builder()
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

    public List<BillingDTO> toDto(List<BillingEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BillingEntity toEntity(BillingDTO dto) {
        return BillingEntity.builder()
                .billingMasterId(dto.getBillingMasterId())
                .sourceServiceCode(dto.getSourceServiceCode())
                .feeCode(dto.getFeeCode())
                .feeName(dto.getFeeName())
                .defaultPrice(dto.getDefaultPrice() == null ? null : new BigDecimal(dto.getDefaultPrice()))
                .categoryCode(dto.getCategoryCode())
                .insuranceTypeCode(dto.getInsuranceTypeCode())
                .effectiveFrom(dto.getEffectiveFrom() == null ? null : LocalDateTime.parse(dto.getEffectiveFrom()))
                .effectiveTo(dto.getEffectiveTo() == null ? null : LocalDateTime.parse(dto.getEffectiveTo()))
                .useYn(dto.getUseYn())
                .build();
    }
}

// MapStruct entity<->dto 변환을 위해서 선언