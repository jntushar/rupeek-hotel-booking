package com.rupeek.hotelbooking.service;
import com.rupeek.hotelbooking.domain.Booking;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class StandardCancellationPolicy implements CancellationPolicy {

    public BigDecimal refundAmount(Booking booking) {
        return LocalDate.now().isBefore(booking.getCheckIn()) ? booking.getAmount() : BigDecimal.ZERO;
    }
}
