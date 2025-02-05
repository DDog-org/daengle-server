package ddog.payment.presentation.dto;

import ddog.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentApplicationEvent(
        Long reservationId,
        Long paymentId,
        Long customerId,
        String customerName,
        String customerPhoneNumber,
        BigDecimal paymentAmount,
        PaymentStatus paymentStatus
) {}