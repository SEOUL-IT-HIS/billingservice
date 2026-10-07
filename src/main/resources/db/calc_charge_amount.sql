-- ============================================================================
-- 수가 코드 + 수량(입원일수)으로 단가와 청구 금액을 계산하는 프로시저
--
-- 병동서비스는 feeCode(수가코드)와 quantity(입원일수)만 보내고, 금액 계산은 수납에서 한다.
--   금액 = billing_master.default_price(기본단가) x 입원일수
-- BillingChargeServiceImpl.createCharge()가 BillingDetailMapper.xml의 calcChargeAmount로 호출한다.
--
-- 오류 코드 (Java에서 ORA-번호로 구분해서 처리)
--   -20001 : 사용 중(use_yn='Y')인 수가코드가 없음
--   -20002 : 수량(입원일수)이 없거나 0 이하
--
-- 실행 방법: 수납 DB 계정으로 이 파일 전체를 실행 (CREATE OR REPLACE 라서 다시 실행해도 됨)
-- ============================================================================
CREATE OR REPLACE PROCEDURE calc_charge_amount (
    p_fee_code   IN  VARCHAR2,   -- 수가 코드 (billing_master.fee_code)
    p_quantity   IN  NUMBER,     -- 수량 = 입원일수
    p_unit_price OUT NUMBER,     -- 적용된 기본단가
    p_amount     OUT NUMBER      -- 청구 금액 (기본단가 x 수량)
) AS
BEGIN
    IF p_quantity IS NULL OR p_quantity <= 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Quantity must be greater than 0');
    END IF;

    SELECT default_price
      INTO p_unit_price
      FROM billing_master
     WHERE fee_code = p_fee_code
       AND use_yn = 'Y';

    p_amount := p_unit_price * p_quantity;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'Fee code not found: ' || p_fee_code);
END calc_charge_amount;
/
