package ddog.payment.application.dto.event;

import ddog.domain.event.enums.EventType;
import ddog.domain.payment.enums.PaymentStatus;

public record PaymentApplicationEvent(
        Long reservationId,
        Long paymentId,
        String customerName,
        String customerPhoneNumber,
        Long paymentAmount,
        PaymentStatus paymentStatus,
        EventType eventType
) {}