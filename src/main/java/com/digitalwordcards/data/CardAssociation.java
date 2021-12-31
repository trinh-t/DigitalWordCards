package com.digitalwordcards.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
public class CardAssociation {
    @Id
    @GeneratedValue
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Card card;
    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    private User user;

}
