package com.digitalwordcards.controllers;

import com.digitalwordcards.data.requests.CardDto;
import com.digitalwordcards.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {


    private final CardService cardService;

    @PostMapping
    @PreAuthorize("hasAuthority('TEACHER')")
    public CardDto createCard(
            @RequestParam("image") MultipartFile image,
            @RequestParam("text") String text,
            @RequestParam("module") Integer module) throws IOException {

        return cardService.create(image.getBytes(), text, module);
    }

    @PostMapping("/{cardId}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public CardDto modifyCard(
            @PathVariable UUID cardId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("text") String text,
            @RequestParam("module") Integer module) throws IOException {
        return cardService.modify(cardId, image.getBytes(), text, module);
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasAuthority('TEACHER')")
    public void deleteCard(@PathVariable UUID cardId) {
        cardService.delete(cardId);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('TEACHER', 'STUDENT')")
    public List<CardDto> getCards() {
        return cardService.getAll();
    }

    @GetMapping("/{cardId}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'STUDENT')")
    public CardDto getCard(@PathVariable UUID cardId) {
        return cardService.get(cardId);
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'STUDENT')")
    public List<CardDto> getByModule(@PathVariable int module) {
        return cardService.getCardsForModule(module);
    }

}
