package com.digitalwordcards.controllers;

import com.digitalwordcards.data.CardAssociation;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.AssociationRepository;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.UserCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    @Autowired
    AssociationRepository associationRepository;

    @PostMapping("/create")
    public User createUser(@RequestBody UserCreationRequest request) {
        final User user = new User();
        repository.findById(request.getEmail()).ifPresentOrElse(user1 -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gebruiker bestaat al");
        }, () -> {
            try {
                final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                try {
                    final Role role = Role.valueOf(request.getRole());
                    if (authentication.getAuthorities().stream().anyMatch(role::canBeGrantedBy)) {
                        user.setEmail(request.getEmail());
                        user.setRole(role);
                        user.setClazz(request.getClazz());
                        user.setName(request.getName());
                        user.setPassword(encoder.encode(request.getPassword()));
                        repository.saveAndFlush(user);
                    } else
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Er kan geen gebruiker aangemaakt worden met " + role.getAuthority() + " als rol");
                } catch (IllegalArgumentException e) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Onbekende gebruikersrol");
                }
            } catch (NullPointerException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Niet geauthenticeerd");
            }
        });
        return user;
    }

    @DeleteMapping
    public void deleteUser(@RequestBody Map<String, String> email) {
        Optional<User> user = repository.findUserByEmail(email.get("email"));
        if (user.isPresent()) {
            List<CardAssociation> cardAssociations = associationRepository.findCardAssociationByUser(user.get());
            for (CardAssociation association : cardAssociations
            ) {
                association.setUser(null);
                association.setCard(null);
                associationRepository.save(association);
            }
            user.get().setViewedCards(null);
            repository.save(user.get());
        }
        repository.deleteById(email.get("email"));
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<User> usersList = repository.findAll();
        return new ResponseEntity<>(usersList, HttpStatus.OK);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return repository.findById(s).orElseThrow(() -> new UsernameNotFoundException("Gebruikersnaam niet gevonden"));
    }


}
