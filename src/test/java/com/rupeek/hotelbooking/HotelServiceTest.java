package com.rupeek.hotelbooking;

import com.rupeek.hotelbooking.domain.*;
import com.rupeek.hotelbooking.payment.PaymentMethod;
import com.rupeek.hotelbooking.repository.*;
import com.rupeek.hotelbooking.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HotelServiceTest {
    private HotelService service;
    private Property property;
    private Property.RoomType room;

    @BeforeEach void setUp() {
        PropertyRepository properties = new InMemoryPropertyRepository();
        BookingRepository bookings = new InMemoryBookingRepository();
        OwnerRepository owners = new InMemoryOwnerRepository();
        PaymentMethod card = new PaymentMethod() {
            public String type() { return "CARD"; }
            public PaymentResult pay(String id, BigDecimal amount) { return new PaymentResult(true, "test-ref", "ok"); }
        };
        service = new HotelService(properties, bookings, owners, List.of(card), new StandardCancellationPolicy());
        OwnerAccount owner = service.createOwner("Test owner");
        room = new Property.RoomType("Deluxe", 2, 1, new BigDecimal("2500"));
        property = service.addProperty(owner.getId(), "Test stay", "Agra", "Civil Lines", 4,
                Set.of("WIFI", "PARKING"), List.of(room));
    }

    @Test void bookingIsHeldAndConfirmedAfterPayment() {
        LocalDate from = LocalDate.now().plusDays(5), to = from.plusDays(2);
        Booking booking = service.book(property.getId(), room.getId(), from, to, 2);
        assertEquals(Booking.Status.PENDING_PAYMENT, booking.getStatus());
        assertEquals(new BigDecimal("5000"), booking.getAmount());
        assertEquals(Booking.Status.CONFIRMED, service.pay(booking.getId(), "CARD").getStatus());
    }

    @Test void overlappingBookingIsRejectedButAdjacentDatesAreAllowed() {
        LocalDate from = LocalDate.now().plusDays(5), to = from.plusDays(2);
        Booking first = service.book(property.getId(), room.getId(), from, to, 1);
        assertThrows(ConflictException.class, () -> service.book(property.getId(), room.getId(), from.plusDays(1), to.plusDays(1), 1));
        assertDoesNotThrow(() -> service.book(property.getId(), room.getId(), to, to.plusDays(1), 1));
    }

    @Test void cancelledBookingReleasesInventory() {
        LocalDate from = LocalDate.now().plusDays(5), to = from.plusDays(1);
        Booking booking = service.book(property.getId(), room.getId(), from, to, 1);
        service.pay(booking.getId(), "CARD");
        assertEquals(new BigDecimal("2500"), service.cancel(booking.getId()).refundAmount());
        assertDoesNotThrow(() -> service.book(property.getId(), room.getId(), from, to, 1));
    }

    @Test void searchAppliesLocationAndAmenityFilters() {
        LocalDate from = LocalDate.now().plusDays(5);
        var results = service.search("Agra", null, from, from.plusDays(1), 1, null, null, Set.of("WIFI"), 3);
        assertEquals(1, results.size());
        assertTrue(service.search("Delhi", null, from, from.plusDays(1), 1, null, null, null, null).isEmpty());
    }
}
