package com.rupeek.hotelbooking.service;
import com.rupeek.hotelbooking.domain.Booking;
import java.math.BigDecimal;

public interface CancellationPolicy {

    BigDecimal refundAmount(Booking booking);

}
