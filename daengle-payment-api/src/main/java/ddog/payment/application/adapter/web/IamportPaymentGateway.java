package ddog.payment.application.adapter.web;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import ddog.domain.payment.PaymentInfo;
import ddog.domain.payment.enums.PaymentStatus;
import ddog.domain.payment.port.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class IamportPaymentGateway implements PaymentGateway {

    private final IamportClient iamportClient;

    @Override
    public PaymentInfo getPaymentInfo(String paymentUid) throws IamportResponseException, IOException {
        Payment payment = iamportClient.paymentByImpUid(paymentUid).getResponse();

        return PaymentInfo.builder()
                .impUid(payment.getImpUid())
                .status(mapToPaymentStatus(payment.getStatus()))
                .amount(payment.getAmount())
                .build();
    }

    @Override
    public void cancelPayment(String paymentUid, boolean isPartial, BigDecimal cancelAmount)
            throws IamportResponseException, IOException {
        CancelData cancelData = new CancelData(paymentUid, isPartial, cancelAmount);
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    private PaymentStatus mapToPaymentStatus(String iamportStatus) {
        switch (iamportStatus) {
            case "ready":
                return PaymentStatus.PAYMENT_READY;
            case "paid":
                return PaymentStatus.PAYMENT_COMPLETED;
            case "cancelled":
                return PaymentStatus.PAYMENT_CANCELED;
            case "failed":
                return PaymentStatus.PAYMENT_INVALIDATION;
            default:
                return PaymentStatus.PAYMENT_GATEWAY_INVALIDATION;
        }
    }
}