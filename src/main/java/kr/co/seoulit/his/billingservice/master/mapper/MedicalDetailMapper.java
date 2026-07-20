package kr.co.seoulit.his.billingservice.master.mapper;

import kr.co.seoulit.his.billingservice.master.dto.MedicalDetailDto;
import kr.co.seoulit.his.billingservice.master.entity.MedicalDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicalDetailMapper {

    MedicalDetailDto toDto(MedicalDetail medicalDetail);

    /**
     * billingMasterId, useYn, createdAt, updatedAt은
     * 서비스에서 생성/정규화되므로 매핑 대상에서 제외합니다.
     */
    @Mapping(target = "billingMasterId", ignore = true)
    @Mapping(target = "useYn", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MedicalDetail toEntity(MedicalDetailDto medicalDetailDto);
}
