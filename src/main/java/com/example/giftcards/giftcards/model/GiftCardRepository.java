package com.example.giftcards.giftcards.model;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
public interface GiftCardRepository extends CrudRepository<GiftCard, String> {
    Optional<GiftCard> findByOwner(String owner);

}
