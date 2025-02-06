package ddog.payment.presentation;

import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.domain.event.EventPublish;
import ddog.domain.payment.enums.PaymentStatus;
import ddog.domain.payment.enums.ServiceType;
import ddog.notification.application.KakaoNotificationService;
import ddog.payment.application.PaymentService;
import ddog.payment.application.dto.request.CancelPaymentsRequest;
import ddog.payment.application.dto.request.PaymentCallbackReq;
import ddog.payment.application.dto.response.PaymentCallbackResp;
import ddog.payment.application.dto.response.PaymentCancelResp;
import ddog.payment.application.dto.response.PaymentHistoryDetail;
import ddog.payment.application.dto.response.PaymentHistoryListResp;
import ddog.payment.presentation.dto.PaymentApplicationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private final KakaoNotificationService kakaoNotificationService;
    private final Environment environment;

    private final EventPublish<PaymentApplicationEvent> paymentEventPublisher;

    @GetMapping("/study")
    public CommonResponseEntity<String> study() {
        //kakaoNotificationService.sendOneTalk("진명인", "010-9285-4118", environment.getProperty("templateId.REVIEWED"));

        PaymentApplicationEvent event = new PaymentApplicationEvent(
                1L,                // reservationId
                1001L,             // paymentId
                2001L,             // customerId
                "진명인",            // customerName
                "010-9285-4118",    // customerPhoneNumber
                BigDecimal.valueOf(50000),  // paymentAmount
                PaymentStatus.PAYMENT_COMPLETED // 결제 상태
        );

        // 이벤트 발행
        paymentEventPublisher.publishEvent(event);


        return success("SUCCESS");
    }

    @PostMapping("/validate")
    public CompletableFuture<CommonResponseEntity<PaymentCallbackResp>> validationPayment(@RequestBody PaymentCallbackReq paymentCallbackReq) {
        return paymentService.validationPayment(paymentCallbackReq)
                .thenApply(CommonResponseEntity::success);
    }

    @PostMapping("/cancel/{reservationId}")
    public CommonResponseEntity<PaymentCancelResp> cancelReservation(@PathVariable Long reservationId) {
        return success(paymentService.cancelReservation(reservationId));
    }

    @GetMapping("/{serviceType}/history/list")
    public CommonResponseEntity<PaymentHistoryListResp> findPaymentHistoryList(
            PayloadDto payloadDto,
            @PathVariable ServiceType serviceType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return success(paymentService.findPaymentHistoryList(payloadDto.getAccountId(), serviceType, page, size));
    }

    @GetMapping("/history/{reservationId}")
    public CommonResponseEntity<PaymentHistoryDetail> getPaymentHistory(
            @PathVariable Long reservationId) {
        return success(paymentService.getPaymentHistory(reservationId));
    }

    @PostMapping("/cancel/batch")
    public CommonResponseEntity<List<PaymentCancelResp>> cancelReservations(@RequestBody CancelPaymentsRequest request) {
        return success(paymentService.cancelReservations(request.getReservationIds()));
    }
}
