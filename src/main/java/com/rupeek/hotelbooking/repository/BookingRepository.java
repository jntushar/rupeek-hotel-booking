package com.rupeek.hotelbooking.repository;
import com.rupeek.hotelbooking.domain.Booking;
import java.util.*;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(String id);

    List<Booking> findByRoomType(String roomTypeId);
}
