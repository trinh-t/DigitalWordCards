package com.digitalwordcards.data;

import com.digitalwordcards.data.requests.CardDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Set;
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

/*    @ManyToMany(cascade = CascadeType.REMOVE)
    private Set<User> viewedBy;*/

    @Transient
    public static Card fromDto(CardDto cardDto)  {
        final var card = new Card();

        card.setDisplayDate(cardDto.getDisplayDate());
        card.setImage(Base64.getMimeDecoder().decode(cardDto.getData()));
        card.setModule(cardDto.getModule());
        card.setText(cardDto.getText());

        return card;
    }

    @Transient
    public CardDto toDto() {

        final var cardDto = new CardDto();
        cardDto.setId(id);
        cardDto.setData(Base64.getMimeEncoder().encodeToString(image));
        cardDto.setModule(module);
        cardDto.setText(text);

        return cardDto;
    }

    public void update(CardDto cardDto) {

        displayDate = cardDto.getDisplayDate();
        image = Base64.getMimeDecoder().decode(cardDto.getData());
        module = cardDto.getModule();
        text = cardDto.getText();
    }
}
