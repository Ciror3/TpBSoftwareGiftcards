package com.example.giftcards.giftcards.model;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


@Service
public class GiftCardService extends ModelService< GiftCard, String, GiftCardRepository >{
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
