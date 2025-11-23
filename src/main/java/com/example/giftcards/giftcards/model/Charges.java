package com.example.giftcards.giftcards.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "charges")
@Getter @Setter
public class Charges extends ModelEntity<Long> {

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giftcard_id")
    private GiftCard giftCard;

    protected Charges() {
    }

    public Charges(int amount, String description, GiftCard giftCard) {
        this.id = Math.abs(new Random().nextLong());

        this.amount = amount;
        this.description = description;
        this.giftCard = giftCard;
        this.date = LocalDateTime.now();
    }
}