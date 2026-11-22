package com.rupeek.hotelbooking.service;

import com.rupeek.hotelbooking.domain.*;
import com.rupeek.hotelbooking.payment.PaymentMethod;
import com.rupeek.hotelbooking.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class HotelService {

    private final PropertyRepository properties;
    private final BookingRepository bookings;
    private final OwnerRepository owners;
    private final Map<String, PaymentMethod> paymentMethods;
    private final CancellationPolicy cancellationPolicy;

    public HotelService(PropertyRepository properties, BookingRepository bookings, OwnerRepository owners,
                        List<PaymentMethod> methods, CancellationPolicy cancellationPolicy) {
        this.properties = properties; this.bookings = bookings; this.owners = owners;
        this.paymentMethods = new HashMap<>(); methods.forEach(m -> paymentMethods.put(m.type(), m));
        this.cancellationPolicy = cancellationPolicy;
    }

    public OwnerAccount createOwner(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Owner name is required");
        return owners.save(new OwnerAccount(name.trim()));
    }

    public Property addProperty(String ownerId, String name, String city, String locality, int stars,
                                Set<String> amenities, List<Property.RoomType> roomTypes) {
        OwnerAccount owner = owners.findById(ownerId).orElseThrow(() -> new NotFoundException("Owner not found"));

        if (name == null || name.isBlank() || city == null || city.isBlank()) throw new IllegalArgumentException("Property name and city are required");
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Star rating must be between 1 and 5");
        if (roomTypes == null || roomTypes.isEmpty()) throw new IllegalArgumentException("At least one room type is required");

        roomTypes.forEach(r -> {
            if (r.getCapacity() < 1 || r.getTotalRooms() < 1 || r.getPricePerNight() == null || r.getPricePerNight().signum() <= 0)
                throw new IllegalArgumentException("Room capacity, inventory and price must be positive");
        });

        Property saved = properties.save(new Property(ownerId, name.trim(), city.trim(), locality, stars, amenities, roomTypes));
        owner.addProperty(saved.getId());

        return saved;
    }

    public List<Property> search(String city, String locality, LocalDate checkIn, LocalDate checkOut,
                                 int guests, BigDecimal minPrice, BigDecimal maxPrice,
                                 Set<String> amenities, Integer minStars) {
        validateDates(checkIn, checkOut);
        if (guests < 1) throw new IllegalArgumentException("Guests must be at least one");

        return properties.findAll().stream().filter(p -> city == null || p.getCity().equalsIgnoreCase(city))
                .filter(p -> locality == null || (p.getLocality() != null && p.getLocality().equalsIgnoreCase(locality)))
                .filter(p -> minStars == null || p.getStarRating() >= minStars)
                .filter(p -> amenities == null || p.getAmenities().containsAll(amenities))
                .filter(p -> p.getRoomTypes().stream().anyMatch(r -> r.getCapacity() >= guests
                        && (minPrice == null || r.getPricePerNight().compareTo(minPrice) >= 0)
                        && (maxPrice == null || r.getPricePerNight().compareTo(maxPrice) <= 0)
                        && available(p, r, checkIn, checkOut)))
                .toList();
    }

    public Booking book(String propertyId, String roomTypeId, LocalDate checkIn, LocalDate checkOut, int guests) {
        validateDates(checkIn, checkOut);
        Property property = properties.findById(propertyId).orElseThrow(() -> new NotFoundException("Property not found"));
        Property.RoomType room = Optional.ofNullable(property.getRoomType(roomTypeId)).orElseThrow(() -> new NotFoundException("Room type not found"));

        if (guests < 1 || guests > room.getCapacity()) throw new IllegalArgumentException("Guest count exceeds room capacity");
        synchronized (room) {
            if (!available(property, room, checkIn, checkOut)) throw new ConflictException("No inventory available for the selected dates");
            BigDecimal amount = room.getPricePerNight().multiply(BigDecimal.valueOf(ChronoUnit.DAYS.between(checkIn, checkOut)));
            return bookings.save(new Booking(propertyId, roomTypeId, checkIn, checkOut, guests, amount));
        }
    }

    public Booking pay(String bookingId, String methodType) {
        Booking booking = getBooking(bookingId);
        synchronized (booking) {
            if (booking.getStatus() == Booking.Status.CONFIRMED) return booking;
            if (booking.getStatus() != Booking.Status.PENDING_PAYMENT) throw new ConflictException("Booking cannot be paid in its current state");
            PaymentMethod method = paymentMethods.get(methodType == null ? "" : methodType.toUpperCase(Locale.ROOT));
            if (method == null) throw new IllegalArgumentException("Unsupported payment method. Use CARD, UPI or WALLET");
            PaymentMethod.PaymentResult result = method.pay(bookingId, booking.getAmount());
            if (result.successful()) booking.confirm(); else booking.paymentFailed();
            return bookings.save(booking);
        }
    }

    public CancellationResult cancel(String bookingId) {
        Booking booking = getBooking(bookingId);

        synchronized (booking) {
            if (booking.getStatus() != Booking.Status.CONFIRMED && booking.getStatus() != Booking.Status.PENDING_PAYMENT)
                throw new ConflictException("Booking cannot be cancelled in its current state");
            BigDecimal refund = booking.getStatus() == Booking.Status.CONFIRMED
                    ? cancellationPolicy.refundAmount(booking) : BigDecimal.ZERO;
            booking.cancel();
            bookings.save(booking);
            return new CancellationResult(booking, refund);
        }
    }

    public Booking getBooking(String id) {
        return bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private boolean available(Property p, Property.RoomType room, LocalDate from, LocalDate to) {
        long occupied = bookings.findByRoomType(room.getId()).stream()
                .filter(b -> b.getStatus() == Booking.Status.PENDING_PAYMENT || b.getStatus() == Booking.Status.CONFIRMED)
                .filter(b -> b.overlaps(from, to)).count();
        return occupied < room.getTotalRooms();
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from == null || to == null || !from.isBefore(to) || from.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Check-in must be today or later and check-out must be after check-in");
    }

    public record CancellationResult(Booking booking, BigDecimal refundAmount) {

    }
}
