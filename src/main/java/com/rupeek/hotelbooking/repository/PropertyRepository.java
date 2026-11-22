package com.rupeek.hotelbooking.repository;
import com.rupeek.hotelbooking.domain.Property;
import java.util.*;

public interface PropertyRepository {

    Property save(Property property);

    Optional<Property> findById(String id);

    List<Property> findAll();
}
