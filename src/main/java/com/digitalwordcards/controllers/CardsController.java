package com.digitalwordcards.controllers;

import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.CardAssociation;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.AssociationRepository;
import com.digitalwordcards.data.repositories.CardRepository;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.CardCreationRequest;
import com.digitalwordcards.data.requests.CardModificationRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CardsController {
    private final CardRepository repository;
    private final UserRepository users;
    private final AssociationRepository associationRepository;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Card createCard(@ModelAttribute CardCreationRequest request) {
        final Card card = new Card();
        try {
            card.setText(request.getText());
            card.setImage(request.getData().getBytes());
            card.setModule(request.getModule());
            card.setDisplayDate(request.getDisplayDate());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Controleer de invoer");
        }
        repository.saveAndFlush(card);
        return card;
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteCard(@RequestBody UUIDWrapper id) {
        Optional<Card> card = repository.findById(id.id);
        if (card.isPresent()) {
            List<CardAssociation> cardAss = associationRepository.findCardAssociationByCard(card.get());
            if (cardAss.isEmpty()) {

            } else {
                for (CardAssociation cardAssociation : cardAss
                ) {
                    cardAssociation.setCard(null);
                    cardAssociation.setUser(null);
                    associationRepository.save(cardAssociation);
                    associationRepository.deleteById(cardAssociation.getId());
                }
            }
            repository.deleteById(id.id);
        } else {
            throw new RuntimeException();
        }
    }

    @GetMapping("/see")
    public Card getByID(@RequestBody UUIDWrapper id) {
        return repository.findById(id.id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @GetMapping("/all")
    public List<Card> getAll() {
        return repository.findAll();
    }

    @GetMapping("/module/{module}")
    public List<Card> getByModule(@PathVariable int module) {
        return repository.findByModule(module);
    }

    @GetMapping("/viewed/{module}")
    public Set<Card> getViewedCardsByModule(@PathVariable int module) {
        return users.findById(SecurityContextHolder.getContext().getAuthentication().getName())
                .map(User::getViewedCards)
                .map(it -> it.stream()
                        .map(CardAssociation::getCard)
                        .filter(card -> card.getModule() == module)
                        .collect(Collectors.toSet()))
                .orElse(new HashSet<>());
    }

    @PutMapping("/view")
    public void viewCard(@RequestBody UUIDWrapper id) {
        final String name = SecurityContextHolder.getContext().getAuthentication().getName();
        users.findById(name).ifPresent(user -> {
            final Card card = repository.getById(id.id);
            if (associationRepository.findByCardAndUser(card, user).isEmpty()) {
                System.out.println(id.id);
                final CardAssociation association = new CardAssociation();
                association.setCard(card);
                association.setUser(user);
                association.setId(UUID.randomUUID());
                associationRepository.saveAndFlush(association);
                user.getViewedCards().add(association);
                users.saveAndFlush(user);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Woordkaart is al bekeken");
        });
    }


    @GetMapping("/class/{clazz}")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<String> getViewedPercentageByClass(@PathVariable String clazz) {
        final long total = repository.count();
        final List<User> users = this.users.findByClazz(clazz);
        return users.stream()
                .filter(it -> it.getRole() == Role.STUDENT)
                .map(user -> String.format("%20s - %2.2f%%", user.getEmail(), (double) user.getViewedCards().size() / total * 100))
                .collect(Collectors.toList());
    }


    @PutMapping("/modify")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public void modifyCard(@ModelAttribute CardModificationRequest request) {
        final Card card = repository.getById(request.getId());
        request.getModule().ifPresent(card::setModule);
        request.getData().ifPresent(card::setImage);
        request.getText().ifPresent(card::setText);
        repository.save(card);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class UUIDWrapper {
        @JsonProperty
        private UUID id;
    }
}
