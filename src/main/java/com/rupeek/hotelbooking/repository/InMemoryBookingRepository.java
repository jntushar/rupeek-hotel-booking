package com.rupeek.hotelbooking.repository;

import com.rupeek.hotelbooking.domain.Booking;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> data = new ConcurrentHashMap<>();

    public Booking save(Booking value) {
        data.put(value.getId(), value);
        return value;
    }

    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Booking> findByRoomType(String roomTypeId) {
        return data.values().stream().filter(b -> b.getRoomTypeId().equals(roomTypeId)).toList();
    }
}
