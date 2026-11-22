package com.rupeek.hotelbooking.repository;

import com.rupeek.hotelbooking.domain.OwnerAccount;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryOwnerRepository implements OwnerRepository {

    private final Map<String, OwnerAccount> data = new ConcurrentHashMap<>();

    public OwnerAccount save(OwnerAccount value) {
        data.put(value.getId(), value);
        return value;
    }

    public Optional<OwnerAccount> findById(String id) {
        return Optional.ofNullable(data.get(id));
    }
}
