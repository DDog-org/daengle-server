package ddog.payment.application.mapper;

import ddog.domain.event.enums.EventType;
import ddog.domain.payment.Payment;
import ddog.domain.payment.Reservation;
import ddog.domain.payment.enums.PaymentStatus;
import ddog.payment.application.dto.event.PaymentApplicationEvent;

public class EventMapper {

    public static PaymentApplicationEvent createBy(Payment payment, Reservation reservation) {
        return new PaymentApplicationEvent (
                reservation.getReservationId(),
                payment.getPaymentId(),
                reservation.getCustomerName(),
                reservation.getCustomerPhoneNumber(),
                payment.getPrice(),
                PaymentStatus.PAYMENT_COMPLETED,
                EventType.PAYMENT
        );
    }
}
