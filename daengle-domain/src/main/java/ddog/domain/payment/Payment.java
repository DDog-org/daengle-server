package ddog.domain.payment;

import ddog.domain.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    private Long paymentId;
    private Long payerId;
    private Long price;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private String paymentUid;
    private String idempotencyKey;

    public static final String PAYMENT_SUCCESS_STATUS = "paid";

    //결제 완료 확인
    public boolean checkIncompleteBy(PaymentStatus paymentStatus) {
        return paymentStatus != PaymentStatus.PAYMENT_COMPLETED;
    }

    //결제 금액 유효성 검증
    public boolean checkInValidationBy(Long paymentAmount) {
        return !Objects.equals(this.price, paymentAmount);
    }

    public void validationSuccess(String impUid) {
        this.paymentUid = impUid;
        this.status = PaymentStatus.PAYMENT_COMPLETED;
    }

    public void invalidate() {
        this.status = PaymentStatus.PAYMENT_INVALIDATION;
    }

    public void cancel() {
        this.status = PaymentStatus.PAYMENT_CANCELED;
    }

    // 환불 금액 산정
    public BigDecimal calculateRefundAmount(LocalDateTime schedule) {
        long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), schedule);

        if (daysLeft >= 3) {
            return BigDecimal.valueOf(this.price); // 100% 환불
        } else if (daysLeft == 2) {
            return BigDecimal.valueOf(this.price * 0.5); // 50% 환불
        } else if (daysLeft == 1) {
            return BigDecimal.valueOf(this.price * 0.5); // 50% 환불 (기본 정책)
        } else {
            return BigDecimal.ZERO; // 환불 불가
        }
    }
}
