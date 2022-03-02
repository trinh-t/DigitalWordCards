package com.digitalwordcards.services;
import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.repositories.CardRepository;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.CardDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;


    public CardDto create(CardDto cardDto) {

        final var card = Card.fromDto(cardDto);

        cardRepository.save(card);

        return card.toDto();
    }

    public void delete(UUID cardId) {

        final var optionalCard = cardRepository.findById(cardId);

        if (optionalCard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card does not exist");
        }

        userRepository.findAllByViewedCardsIsContaining(optionalCard.get()).forEach(user -> {

                    user.getViewedCards().remove(optionalCard.get());
                    userRepository.save(user);
                }
        );

        cardRepository.delete(optionalCard.get());

    }

    public List<CardDto> getAll() {
        return cardRepository.findAll().stream().map(Card::toDto).collect(Collectors.toList());
    }

    public CardDto get(UUID cardId) {

        return getEntity(cardId).toDto();

    }

    public List<CardDto> getCardsForModule(int module) {
        return cardRepository.findByModule(module).stream().map(Card::toDto).collect(Collectors.toList());
    }


    public Card getEntity(UUID cardId) {

        final var optionalCard = cardRepository.findById(cardId);

        if (optionalCard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card does not exist");
        }

        return optionalCard.get();
    }

    public double count() {
        return cardRepository.count();
    }


    public void save(Card card) {
        cardRepository.save(card);
    }

    public CardDto create(byte[] image, String text, Integer module) {

        final var card = new Card();

        card.setImage(image);
        card.setText(text);
        card.setModule(module);
        cardRepository.save(card);
        return card.toDto();
    }

    public CardDto modify(UUID cardId, byte[] image, String text, Integer module) {

        final var optionalCard = cardRepository.findById(cardId);

        if (optionalCard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card does not exist");
        }

        final var card = optionalCard.get();

        card.setImage(image);
        card.setText(text);
        card.setModule(module);
        cardRepository.save(card);

        return card.toDto();
    }
}
