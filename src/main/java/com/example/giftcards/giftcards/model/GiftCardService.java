package com.example.giftcards.giftcards.model;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class GiftCardService {
    @Autowired private GiftCardRepository repository;

    @Transactional(readOnly = true)
    public List<GiftCard> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public GiftCard getById(String id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException(GiftCard.InvalidCard));
    }

    @Transactional
    public GiftCard save(GiftCard giftCard) {
        return repository.save(giftCard);
    }

    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Transactional
    public void delete(GiftCard card) {
        repository.delete(card);
    }


}
