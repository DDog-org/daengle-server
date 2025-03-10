package ddog.domain.payment.port;

import ddog.domain.payment.PaymentInfo;
import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentInfo getPaymentInfo(String paymentUid) throws Exception;
    void cancelPayment(String paymentUid, boolean isPartial, BigDecimal cancelAmount) throws Exception;
}