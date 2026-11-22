package com.rupeek.hotelbooking.payment;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;

@Component
class MockCardPayment implements PaymentMethod {

    public String type() {
        return "CARD";
    }

    public PaymentResult pay(String bookingId, BigDecimal amount) {
        return new PaymentResult(true, "CARD-" + UUID.randomUUID(), "Mock card payment approved");
    }
}
@Component
class MockUpiPayment implements PaymentMethod {

    public String type() {
        return "UPI";
    }

    public PaymentResult pay(String bookingId, BigDecimal amount) {
        return new PaymentResult(true, "UPI-" + UUID.randomUUID(), "Mock UPI payment approved");
    }
}
@Component
class MockWalletPayment implements PaymentMethod {

    public String type() {
        return "WALLET";
    }

    public PaymentResult pay(String bookingId, BigDecimal amount) {
        return new PaymentResult(true, "WALLET-" + UUID.randomUUID(), "Mock wallet payment approved");
    }
}
