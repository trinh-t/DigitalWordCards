package com.digitalwordcards.data;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Card {

    @Id
    @GeneratedValue
    private UUID id;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;
    private String text;
    private int module;
    private LocalDate displayDate;

}
