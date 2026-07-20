package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailRequestDto;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface BillingDetailMapper {

    List<BillingDetailResponseDto> searchDetails(@Param("detailStatus") String detailStatus,
                                                  @Param("from") LocalDate from,
                                                  @Param("to") LocalDate to);

    List<BillingDetailResponseDto> selectDetailsByBillingId(@Param("billingId") String billingId);

    void insertDetail(@Param("billingId") String billingId,
                      @Param("detailId") String detailId,
                      @Param("dto") BillingDetailRequestDto dto);

    void updateDetail(@Param("detailId") String detailId,
                      @Param("dto") BillingDetailRequestDto dto);

    void updateStatus(@Param("detailId") String detailId,
                      @Param("status") String status);
}