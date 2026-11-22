package com.rupeek.hotelbooking.api;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;

public final class ApiModels {

    private ApiModels() {}

    public record CreateOwnerRequest(@NotBlank String name) {}

    public record RoomTypeRequest(@NotBlank String name, @Min(1) int capacity, @Min(1) int totalRooms, @NotNull @DecimalMin("0.01") BigDecimal pricePerNight) {}

    public record CreatePropertyRequest(@NotBlank String ownerId, @NotBlank String name, @NotBlank String city,
            String locality, @Min(1) @Max(5) int starRating, Set<String> amenities, @NotEmpty List<@NotNull @Valid RoomTypeRequest> roomTypes) {}

    public record SearchRequest(String city, String locality, @NotNull LocalDate checkIn, @NotNull LocalDate checkOut,
            @Min(1) int guests, BigDecimal minPrice, BigDecimal maxPrice, Set<String> amenities, @Min(1) @Max(5) Integer minStars) {}

    public record CreateBookingRequest(@NotBlank String propertyId, @NotBlank String roomTypeId,
            @NotNull LocalDate checkIn, @NotNull LocalDate checkOut, @Min(1) int guests) {}

    public record PayRequest(@NotBlank String method) {}
}
