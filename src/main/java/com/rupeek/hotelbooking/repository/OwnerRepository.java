package com.rupeek.hotelbooking.repository;
import com.rupeek.hotelbooking.domain.OwnerAccount;
import java.util.*;

public interface OwnerRepository {

    OwnerAccount save(OwnerAccount owner);

    Optional<OwnerAccount> findById(String id);
}
