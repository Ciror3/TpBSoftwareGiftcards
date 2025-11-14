package com.example.giftcards.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    @Autowired private UserRepository repository;

    @Transactional(readOnly = true)
    public List<UserVault> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public UserVault getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException(GiftCardFacade.InvalidToken));
    }

    @Transactional(readOnly = true)
    public UserVault findByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(()-> new RuntimeException(GiftCardFacade.InvalidUser));
    }

    @Transactional
    public UserVault save(UserVault userVault) {
        return repository.save(userVault);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void delete(UserVault userVault) {
        repository.delete(userVault);
    }

    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

}
