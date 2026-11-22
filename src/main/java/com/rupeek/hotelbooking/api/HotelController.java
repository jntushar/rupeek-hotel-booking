package com.rupeek.hotelbooking.api;

import com.rupeek.hotelbooking.domain.*;
import com.rupeek.hotelbooking.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import static com.rupeek.hotelbooking.api.ApiModels.*;

@RestController
@RequestMapping("/api/v1")
public class HotelController {
    private final HotelService service;
    public HotelController(HotelService service) { this.service = service; }

    @PostMapping("/owners")
    public OwnerAccount createOwner(@Valid @RequestBody CreateOwnerRequest request) {
        return service.createOwner(request.name());
    }

    @PostMapping("/properties")
    public Property addProperty(@Valid @RequestBody CreatePropertyRequest request) {
        List<Property.RoomType> rooms = request.roomTypes().stream()
                .map(r -> new Property.RoomType(r.name(), r.capacity(), r.totalRooms(), r.pricePerNight()))
                .toList();
        return service.addProperty(request.ownerId(), request.name(), request.city(), request.locality(),
                request.starRating(), request.amenities(), rooms);
    }

    @PostMapping("/properties/search")
    public List<Property> search(@Valid @RequestBody SearchRequest r) {
        return service.search(r.city(), r.locality(), r.checkIn(), r.checkOut(), r.guests(),
                r.minPrice(), r.maxPrice(), r.amenities(), r.minStars());
    }

    @PostMapping("/bookings")
    public Booking book(@Valid @RequestBody CreateBookingRequest r) {
        return service.book(r.propertyId(), r.roomTypeId(), r.checkIn(), r.checkOut(), r.guests());
    }

    @PostMapping("/bookings/{id}/payment")
    public Booking pay(@PathVariable String id, @Valid @RequestBody PayRequest request) {
        return service.pay(id, request.method());
    }

    @PostMapping("/bookings/{id}/cancellation")
    public HotelService.CancellationResult cancel(@PathVariable String id) {
        return service.cancel(id);
    }

    @GetMapping("/bookings/{id}")
    public Booking getBooking(@PathVariable String id) {
        return service.getBooking(id);
    }
}
