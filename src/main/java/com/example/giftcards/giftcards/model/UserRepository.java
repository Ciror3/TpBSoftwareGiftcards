package com.example.giftcards.giftcards.model;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserVault, Long> {

    Optional<UserVault> findByUsername(String username );

}
