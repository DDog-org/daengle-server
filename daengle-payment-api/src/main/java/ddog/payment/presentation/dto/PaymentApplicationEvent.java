package ddog.payment.presentation.dto;

import ddog.domain.payment.enums.PaymentStatus;

public record PaymentApplicationEvent(
        Long reservationId,
        Long paymentId,
        String customerName,
        String customerPhoneNumber,
        Long paymentAmount,
        PaymentStatus paymentStatus
) {}