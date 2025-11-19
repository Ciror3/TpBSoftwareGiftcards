package com.example.giftcards.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(UserService.class)
class UserServiceTest {
    Random rnd = new Random( Instant.now().getEpochSecond() );

    @Autowired protected UserService service;

    @BeforeEach
    public void setUp() {
        service.findAll().forEach(service::delete);
    }

    protected UserVault newSample() {
        return new UserVault( "Johnny" + rnd.nextInt() , "tusk" );
    }

    protected UserVault updateUser( UserVault user ) {
        user.setUsername( "Gyro" );
        return user;
    }

    protected UserVault savedSample() {
        return service.save( newSample() );
    }

    @Test public void testEntitySave() {
        UserVault model = newSample();
        UserVault retrieved = service.save( model );
        assertNotNull( retrieved.getId() );
        assertNotNull( model.getId() );
        assertEquals( retrieved, model );
    }

//    @Test public void testEntityUpdate() {
//        UserVault model = newSample();
//
//        updateUser( model );
//        service.save( model );
//        UserVault retrieved = service.getById( model.getId() );
//        assertEquals( model, retrieved );
//    }

    @Test public void testDeletionByObject() {
        UserVault model = savedSample();

        service.delete( model );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testDeletionById() {
        UserVault model = savedSample();

        service.delete( model.getId() );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testFindAll() {
        UserVault model = savedSample();
        List list = service.findAll();
        assertFalse( list.isEmpty() );
        assertTrue( list.contains( model ) );
    }
}
