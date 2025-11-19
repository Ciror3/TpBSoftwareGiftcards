package com.example.giftcards.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GiftCardFacade {
    public static final String InvalidUser = "InvalidUser";
    public static final String InvalidMerchant = "InvalidMerchant";
    public static final String InvalidToken = "InvalidToken";


    @Autowired private UserService userService;
    @Autowired private GiftCardService giftCardService;
    @Autowired private MerchantService merchantService;
    @Autowired private Clock clock;

    //Esto si queda en memoria porque la sesiones son temporales
    private Map<UUID, UserSession> sessions = new HashMap();

    public UUID login( String userKey, String pass ) {
        if ( !userService.authenticate(userKey, pass)) {
            throw new RuntimeException( InvalidUser );
        }

        UUID token = UUID.randomUUID();
        sessions.put( token, new UserSession( userKey, clock ) );
        return token;
    }

    public void redeem( UUID token, String cardId ) {
        GiftCard card = giftCardService.getById(cardId);
        card.redeem( findUser( token ));
        giftCardService.save(card);
    }

    public int balance( UUID token, String cardId ) {
        return ownedCard( token, cardId ).balance();
    }

    public void charge( String merchantKey, String cardId, int amount, String description ) {
        if ( !merchantService.exists( merchantKey ) ) throw new RuntimeException( InvalidMerchant );

        GiftCard card = giftCardService.getById( cardId );
        card.charge( amount, description );
        giftCardService.save(card);
    }

    public List<String> details( UUID token, String cardId ) {
        return ownedCard( token, cardId ).charges();
    }

    private GiftCard ownedCard( UUID token, String cardId ) {
        GiftCard card = giftCardService.getById( cardId );
        if ( !card.isOwnedBy( findUser( token ) ) ) throw new RuntimeException( InvalidToken );
        return card;
    }

    private String findUser( UUID token ) {
        return sessions.computeIfAbsent( token, key -> { throw new RuntimeException( InvalidToken ); } )
                       .userAliveAt( clock );
    }
}
