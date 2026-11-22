package com.rupeek.hotelbooking.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.UUID;

public class Booking {

    public enum Status {
        PENDING_PAYMENT,
        CONFIRMED,
        PAYMENT_FAILED,
        CANCELLED
    }

    private final String id;
    private final String propertyId;
    private final String roomTypeId;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int guests;
    private final BigDecimal amount;
    private Status status;
    private Instant createdAt;

    public Booking(String propertyId, String roomTypeId, LocalDate checkIn, LocalDate checkOut,
                   int guests, BigDecimal amount) {
        this.id = UUID.randomUUID().toString();
        this.propertyId = propertyId;
        this.roomTypeId = roomTypeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.amount = amount;
        this.status = Status.PENDING_PAYMENT;
        this.createdAt = Instant.now();
    }

    public boolean overlaps(LocalDate from, LocalDate to) {
        return checkIn.isBefore(to) && from.isBefore(checkOut);
    }

    public void confirm() {
        status = Status.CONFIRMED;
    }

    public void paymentFailed() {
        status = Status.PAYMENT_FAILED;
    }

    public void cancel() {
        status = Status.CANCELLED;
    }

    public String getId() {
        return id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
