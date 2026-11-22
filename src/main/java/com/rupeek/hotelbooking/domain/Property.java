package com.rupeek.hotelbooking.domain;

import java.math.BigDecimal;
import java.util.*;

public class Property {

    private final String id;
    private final String ownerId;
    private final String name;
    private final String city;
    private final String locality;
    private final int starRating;
    private final Set<String> amenities;
    private final Map<String, RoomType> roomTypes;

    public Property(String ownerId, String name, String city, String locality, int starRating,
                    Set<String> amenities, List<RoomType> roomTypes) {
        this.id = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.name = name;
        this.city = city;
        this.locality = locality;
        this.starRating = starRating;
        this.amenities = new HashSet<>(amenities == null ? Set.of() : amenities);
        this.roomTypes = new HashMap<>();
        roomTypes.forEach(room -> this.roomTypes.put(room.getId(), room));
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getLocality() {
        return locality;
    }

    public int getStarRating() {
        return starRating;
    }

    public Set<String> getAmenities() {
        return Collections.unmodifiableSet(amenities);
    }

    public Collection<RoomType> getRoomTypes() {
        return Collections.unmodifiableCollection(roomTypes.values());
    }

    public RoomType getRoomType(String id) {
        return roomTypes.get(id);
    }

    public static class RoomType {

        private final String id = UUID.randomUUID().toString();
        private final String name;
        private final int capacity;
        private final int totalRooms;
        private final BigDecimal pricePerNight;

        public RoomType(String name, int capacity, int totalRooms, BigDecimal pricePerNight) {
            this.name = name; this.capacity = capacity; this.totalRooms = totalRooms; this.pricePerNight = pricePerNight;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getTotalRooms() {
            return totalRooms;
        }

        public BigDecimal getPricePerNight() {
            return pricePerNight;
        }
    }
}
