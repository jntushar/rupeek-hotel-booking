package com.rupeek.hotelbooking.repository;

import com.rupeek.hotelbooking.domain.Property;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPropertyRepository implements PropertyRepository {

    private final Map<String, Property> data = new ConcurrentHashMap<>();

    public Property save(Property value) {
        data.put(value.getId(), value);
        return value;
    }

    public Optional<Property> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<Property> findAll() {
        return List.copyOf(data.values());
    }
}
