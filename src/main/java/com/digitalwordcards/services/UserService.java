package com.digitalwordcards.services;
import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.CardDto;
import com.digitalwordcards.data.requests.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final CardService cardService;

    private final PasswordEncoder passwordEncoder;

    public UserDto create(UserDto userDto) {

        final var optionalUser = userRepository.findById(userDto.getEmail());

        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist");
        }

        final var user = User.fromDto(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user.toDto();
    }

    public void delete(String email) {

        final var optionalUser = userRepository.findById(email);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        userRepository.delete(optionalUser.get());

    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(User::toDto).collect(toList());
    }

    @Transactional
    public void viewCard(String email, UUID cardId) {

        final var optionalUser = userRepository.findById(email);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        final var user = optionalUser.get();
        final var card = cardService.getEntity(cardId);

        user.getViewedCards().add(card);

        userRepository.save(user);
        cardService.save(card);
    }

    public List<String> getViewedPercentageByClass(String clazz) {


        final List<User> users = userRepository.findByClazzAndAndRole(clazz, Role.STUDENT);

        return users.stream()
                .map(user -> String.format("%20s (%s) - %2.2f%%", user.getEmail(), clazz,
                        user.getViewedCards().size() / cardService.count() * 100))
                .collect(toList());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findById(email).orElseThrow(() -> {
            throw new UsernameNotFoundException(email);
        });
    }

    public List<CardDto> getViewedCardsForModule(String email, Long module) {


        final var optionalUser = userRepository.findById(email);

        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist");
        }

        return optionalUser.get().getViewedCards().stream().filter(card->card.getModule() == module).
                map(Card::toDto).collect(toList());

    }
}
