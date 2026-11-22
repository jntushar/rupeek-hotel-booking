package com.rupeek.hotelbooking.payment;
import java.math.BigDecimal;

public interface PaymentMethod {

    String type();

    PaymentResult pay(String bookingId, BigDecimal amount);

    record PaymentResult(boolean successful, String reference, String message) {}
}
