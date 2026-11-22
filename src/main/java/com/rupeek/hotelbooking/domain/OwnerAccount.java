package com.rupeek.hotelbooking.domain;

import java.util.*;

public class OwnerAccount {

    private final String id = UUID.randomUUID().toString();
    private final String name;
    private final Set<String> propertyIds = new HashSet<>();

    public OwnerAccount(String name) {
        this.name = name;
    }

    public void addProperty(String propertyId) {
        propertyIds.add(propertyId);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getPropertyIds() {
        return Collections.unmodifiableSet(propertyIds);
    }
}
