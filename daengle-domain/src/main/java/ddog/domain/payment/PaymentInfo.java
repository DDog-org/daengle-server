package ddog.domain.payment;

import ddog.domain.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentInfo {
    private final String impUid;      // PG사 결제 고유번호
    private final PaymentStatus status;      // 결제 상태
    private final BigDecimal amount;  // 결제 금액
}
