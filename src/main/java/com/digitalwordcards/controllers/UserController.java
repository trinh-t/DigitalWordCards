package com.digitalwordcards.controllers;

import java.util.List;
import java.util.UUID;

import com.digitalwordcards.data.requests.CardDto;
import com.digitalwordcards.data.requests.UserDto;
import com.digitalwordcards.services.UserService;
import lombok.AllArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable String email) {

        userService.delete(email);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public List<UserDto> getAll() {

        return userService.getAll();

    }

    @PreAuthorize("hasAuthority('STUDENT') and principal.username == #email" )
    @PutMapping("/{email}/viewed/{cardId}")
    public void viewCard(@PathVariable String email, @PathVariable UUID cardId) {

        userService.viewCard(email, cardId);

    }

    @PreAuthorize("hasAuthority('STUDENT') and principal.username == #email" )
    @GetMapping("/{email}/module/{module}")
    public List<CardDto> viewCard(@PathVariable String email, @PathVariable Long module) {

        return userService.getViewedCardsForModule(email, module);

    }
}