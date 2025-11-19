package com.example.giftcards.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class MerchantService {
    @Autowired private MerchantRepository repository;

    @Transactional(readOnly=true)
    public List<Merchant> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional
    public Merchant save(Merchant merchant) {
        return repository.save(merchant);
    }

    @Transactional(readOnly = true)
    public boolean exists(String code) {
        return repository.existsById(code);
    }

    @Transactional
    public void delete(Merchant merchant) {
        repository.delete(merchant);
    }

}
