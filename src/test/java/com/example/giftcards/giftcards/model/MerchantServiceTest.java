package com.example.giftcards.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MerchantService.class)
public class MerchantServiceTest extends ModelServiceTest<Merchant, String, MerchantService> {

    @Autowired
    protected MerchantService service; // viene también del padre, pero lo dejamos explícito

    @Override
    protected Merchant newSample() {
        return new Merchant("M12", "MerchantApi");
    }

    @Test
    public void testExistence() {
        savedSample();
        assertTrue(service.exists("M12"));
    }

    @Test
    public void testFindAllSpecific() {
        Merchant model = savedSample();
        List<Merchant> list = service.findAll();
        assertFalse(list.isEmpty());
        assertTrue(list.contains(model));
    }
}
