package com.example.giftcards.giftcards.model;


import java.time.Instant;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(UserService.class)
public class UserServiceTest extends ModelServiceTest<UserVault, Long, UserService> {

    Random rnd = new Random(Instant.now().getEpochSecond());

    @Autowired
    protected UserService service;

    @BeforeEach
    public void setUp() {
        service.findAll().forEach(service::delete);
    }

    @Override
    protected UserVault newSample() {
        return new UserVault("Johnny" + rnd.nextInt(), "tusk");
    }

}
