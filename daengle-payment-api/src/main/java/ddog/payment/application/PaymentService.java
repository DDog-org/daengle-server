package ddog.payment.application;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import ddog.domain.estimate.CareEstimate;
import ddog.domain.estimate.EstimateStatus;
import ddog.domain.estimate.GroomingEstimate;
import ddog.domain.estimate.port.CareEstimatePersist;
import ddog.domain.estimate.port.GroomingEstimatePersist;
import ddog.domain.message.port.MessageSend;
import ddog.domain.payment.Order;
import ddog.domain.payment.Payment;
import ddog.domain.payment.PaymentInfo;
import ddog.domain.payment.Reservation;
import ddog.domain.payment.enums.PaymentStatus;
import ddog.domain.payment.enums.ServiceType;
import ddog.domain.payment.port.OrderPersist;
import ddog.domain.payment.port.PaymentGateway;
import ddog.domain.payment.port.PaymentPersist;
import ddog.domain.payment.port.ReservationPersist;
import ddog.domain.review.port.CareReviewPersist;
import ddog.domain.review.port.GroomingReviewPersist;
import ddog.domain.user.User;
import ddog.domain.user.port.UserPersist;
import ddog.payment.application.adapter.web.out.PaymentEventPublisher;
import ddog.payment.application.dto.message.PaymentTimeoutMessage;
import ddog.payment.application.dto.request.PaymentCallbackReq;
import ddog.payment.application.dto.response.*;
import ddog.payment.application.exception.*;
import ddog.payment.application.mapper.EventMapper;
import ddog.payment.application.mapper.ReservationMapper;
import ddog.payment.application.dto.event.PaymentApplicationEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentGateway paymentGateway;

    private final UserPersist userPersist;
    private final OrderPersist orderPersist;
    private final PaymentPersist paymentPersist;
    private final ReservationPersist reservationPersist;

    private final GroomingEstimatePersist groomingEstimatePersist;
    private final CareEstimatePersist careEstimatePersist;

    private final CareReviewPersist careReviewPersist;
    private final GroomingReviewPersist groomingReviewPersist;

    private final MessageSend messageSend;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    @TimeLimiter(name = "paymentValidation")
    @CircuitBreaker(name = "paymentValidation", fallbackMethod = "handlePaymentValidationFallback")
    public CompletableFuture<PaymentCallbackResp> validationPayment(PaymentCallbackReq paymentCallbackReq) {
        Order savedOrder = orderPersist.findByOrderUid(paymentCallbackReq.getOrderUid())
                .orElseThrow(() -> new OrderException(OrderExceptionType.ORDER_NOT_FOUNDED));

        Payment payment = savedOrder.getPayment();
        Hibernate.initialize(payment);

        return CompletableFuture.supplyAsync(() -> processValidation(paymentCallbackReq, savedOrder, payment));
    }

    @Transactional
    public PaymentCancelResp cancelReservation(Long reservationId) {
        Reservation savedReservation = reservationPersist.findByReservationId(reservationId)
                .orElseThrow(() -> new PaymentException(PaymentExceptionType.PAYMENT_RESERVATION_NOT_FOUND));

        Payment savedPayment = paymentPersist.findByPaymentId(savedReservation.getPaymentId())
                .orElseThrow(() -> new PaymentException(PaymentExceptionType.PAYMENT_NOT_FOUND));

        BigDecimal refundAmount = savedPayment.calculateRefundAmount(savedReservation.getSchedule());

        try {
            if (refundAmount.compareTo(BigDecimal.ZERO) > 0) paymentGateway.cancelPayment(savedPayment.getPaymentUid(), true, refundAmount);

            savedPayment.cancel();
            paymentPersist.save(savedPayment);

            return PaymentCancelResp.builder()
                    .paymentId(savedPayment.getPaymentId())
                    .reservationId(savedReservation.getReservationId())
                    .originDepositAmount(savedPayment.getPrice())
                    .refundAmount(refundAmount)
                    .build();

        } catch (IamportResponseException | IOException e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_PG_INTEGRATION_FAILED);
        } catch (Exception e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_RESERVATION_CANCEL_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public PaymentHistoryDetail getPaymentHistory(Long reservationId) {
        Reservation savedReservation = reservationPersist.findByReservationId(reservationId)
                .orElseThrow(() -> new PaymentException(PaymentExceptionType.PAYMENT_HISTORY_NOT_FOUND));

        Boolean hasWrittenReview = checkIfReviewExistsBy(savedReservation);

        return PaymentHistoryDetail.builder()
                .reservationId(savedReservation.getReservationId())
                .reservationStatus(savedReservation.getReservationStatus())
                .recipientName(savedReservation.getRecipientName())
                .shopName(savedReservation.getShopName())
                .schedule(savedReservation.getSchedule())
                .deposit(savedReservation.getDeposit())
                .customerName(savedReservation.getCustomerName())
                .customerPhoneNumber(savedReservation.getCustomerPhoneNumber())
                .visitorName(savedReservation.getVisitorName())
                .visitorPhoneNumber(savedReservation.getVisitorPhoneNumber())
                .hasWrittenReview(hasWrittenReview)
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentHistoryListResp findPaymentHistoryList(Long accountId, ServiceType serviceType, int page, int size) {
        User savedUser = userPersist.findByAccountId(accountId)
                .orElseThrow(() -> new PaymentException(PaymentExceptionType.PAYMENT_USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservations = reservationPersist.findPaymentHistoryList(savedUser.getAccountId(), serviceType, pageable);

        return mappingToPaymentHistoryListResp(reservations);
    }

    private PaymentCallbackResp processValidation(PaymentCallbackReq paymentCallbackReq, Order savedOrder, Payment payment) {
        validateEstimateBy(savedOrder);
        if (payment.getStatus() == PaymentStatus.PAYMENT_COMPLETED)
            throw new PaymentException(PaymentExceptionType.PAYMENT_ALREADY_COMPLETED);

        try {
            PaymentInfo paymentInfo = paymentGateway.getPaymentInfo(paymentCallbackReq.getPaymentUid());

            // 결제 검증 절차
            PaymentStatus paymentStatus = paymentInfo.getStatus();
            long paymentAmount = paymentInfo.getAmount().longValue();

            if (payment.checkIncompleteBy(paymentStatus)) {     //TODO 결제상태 변경과 영속도 도메인 엔티티에게 위임하기
                payment.invalidate();
                paymentPersist.save(payment);
                throw new PaymentException(PaymentExceptionType.PAYMENT_PG_INCOMPLETE);
            }

            if (payment.checkInValidationBy(paymentAmount)) {   //TODO 결제상태 변경과 영속도 도메인 엔티티에게 위임하기
                payment.invalidate();
                paymentPersist.save(payment);
                paymentGateway.cancelPayment(paymentInfo.getImpUid(), true, new BigDecimal(paymentAmount));
                throw new PaymentException(PaymentExceptionType.PAYMENT_PG_AMOUNT_MISMATCH);
            }

            payment.validationSuccess(paymentInfo.getImpUid());
            paymentPersist.save(payment);

            Reservation reservationToSave = ReservationMapper.createBy(savedOrder, payment);
            Reservation savedReservation = reservationPersist.save(reservationToSave);

            // 이벤트 발행
            PaymentApplicationEvent paymentEvent = EventMapper.createBy(payment, savedReservation);
            paymentEventPublisher.publishEvent(paymentEvent);

            return PaymentCallbackResp.builder()
                    .customerId(savedOrder.getAccountId())
                    .reservationId(savedReservation.getReservationId())
                    .paymentId(payment.getPaymentId())
                    .price(payment.getPrice())
                    .build();

        } catch (IamportResponseException | IOException e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_PG_INTEGRATION_FAILED, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public List<PaymentCancelResp> cancelReservations(List<Long> reservationIds) {
        try {
            return reservationIds.stream()
                    .map(this::cancelReservation)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_CANCEL_BATCH_ERROR);
        }
    }

    @Transactional
    public void refundPayment(String paymentUid, String orderUid) {
        Order savedOrder = orderPersist.findByOrderUid(orderUid)
                .orElseThrow(() -> new OrderException(OrderExceptionType.ORDER_NOT_FOUNDED));

        Payment payment = savedOrder.getPayment();

        BigDecimal refundAmount = BigDecimal.valueOf(payment.getPrice());
        CancelData cancelData = new CancelData(payment.getPaymentUid(), true, refundAmount);

        try {
            paymentGateway.cancelPayment(payment.getPaymentUid(), true, refundAmount);  // 실제 환불 요청
            payment.cancel();
            paymentPersist.save(payment);

        } catch (IamportResponseException | IOException e) {  //TODO 에러 로그 슬랙 연동
            throw new PaymentException(PaymentExceptionType.PAYMENT_PG_INTEGRATION_FAILED);
        } catch (Exception e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_CANCEL_BATCH_ERROR);
        }
    }

    private CompletableFuture<PaymentCallbackResp> handlePaymentValidationFallback(PaymentCallbackReq paymentCallbackReq, Throwable t) {
        Throwable rootCause = findRootCause(t);

        if (rootCause instanceof PaymentException) {
            throw (PaymentException) rootCause;

        } else if (rootCause instanceof TimeoutException) {
            sendPaymentTimeoutMessage(paymentCallbackReq);
            throw new PaymentException(PaymentExceptionType.PAYMENT_PG_TIMEOUT, rootCause);
        } else {
            throw new PaymentException(PaymentExceptionType.PAYMENT_PG_INTEGRATION_FAILED, rootCause);
        }
    }

    private void sendPaymentTimeoutMessage(PaymentCallbackReq paymentCallbackReq) {
        PaymentTimeoutMessage timeoutMessage = new PaymentTimeoutMessage(
                paymentCallbackReq.getPaymentUid(),
                paymentCallbackReq.getOrderUid(),
                paymentCallbackReq.getEstimateId()
        );
        //메시지 브로커에 타임아웃 메시지 전송
        messageSend.send(timeoutMessage);
    }

    private Throwable findRootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }

    private PaymentHistoryListResp mappingToPaymentHistoryListResp(Page<Reservation> reservations) {
        List<PaymentHistorySummaryResp> paymentHistoryList = reservations.stream().map(reservation -> PaymentHistorySummaryResp.builder()
                .reservationId(reservation.getReservationId())
                .recipientImageUrl(reservation.getRecipientImageUrl())
                .recipientName(reservation.getRecipientName())
                .shopName(reservation.getShopName())
                .paymentDate(reservation.getSchedule())
                .status(reservation.getReservationStatus().name())
                .build()).collect(Collectors.toList());

        return PaymentHistoryListResp.builder()
                .paymentHistoryList(paymentHistoryList)
                .build();
    }

    //TODO Estimate 도메인 객체에게 역할 위임하기, 확장성이 있는 유효성 검사 로직을 구현하기 (언제든 새로운 00견적 서비스가 추가될 수 있다)
    private void validateEstimateBy(Order order) {
        if (order.getServiceType().equals(ServiceType.GROOMING)) {
            GroomingEstimate estimate = groomingEstimatePersist.findByEstimateId(order.getEstimateId())
                    .orElseThrow(() -> new GroomingEstimateException(GroomingEstimateExceptionType.GROOMING_ESTIMATE_NOT_FOUND));

            groomingEstimatePersist.updateStatusWithParentId(EstimateStatus.END, estimate.getParentId());

            estimate.updateStatus(EstimateStatus.ON_RESERVATION);
            groomingEstimatePersist.save(estimate);

        } else if (order.getServiceType().equals(ServiceType.CARE)) {
            CareEstimate estimate = careEstimatePersist.findByEstimateId(order.getEstimateId())
                    .orElseThrow(() -> new CareEstimateException(CareEstimateExceptionType.CARE_ESTIMATE_NOT_FOUND));

            careEstimatePersist.updateStatusWithParentId(EstimateStatus.END, estimate.getParentId());

            estimate.updateStatus(EstimateStatus.ON_RESERVATION);
            careEstimatePersist.save(estimate);

        } else {
            throw new IllegalArgumentException("서비스 타입이 올바르지 않습니다.");
        }
    }

    //TODO Review 도메인 객체에게 역할 위임하기, 확장성이 있는 검사 로직을 구현하기 (언제든 새로운 00리뷰 서비스가 추가될 수 있다)
    private Boolean checkIfReviewExistsBy(Reservation reservation) {
        Long reviewerId = reservation.getCustomerId();
        Long reservationId = reservation.getReservationId();

        if (reservation.getServiceType().equals(ServiceType.GROOMING)) {
            return groomingReviewPersist.findByReviewerIdAndReservationId(reviewerId, reservationId).isPresent();

        } else if (reservation.getServiceType().equals(ServiceType.CARE)) {
            return careReviewPersist.findByReviewerIdAndReservationId(reviewerId, reservationId).isPresent();
        }
        return false;
    }
}
