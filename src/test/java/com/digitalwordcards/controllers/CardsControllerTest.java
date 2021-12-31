package com.digitalwordcards.controllers;

import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.CardAssociation;
import com.digitalwordcards.data.Role;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.AssociationRepository;
import com.digitalwordcards.data.repositories.CardRepository;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.CardCreationRequest;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardsControllerTest {

    @Test
    void createCard() {
        final CardRepository repository = Mockito.mock(CardRepository.class);
        final CardsController cardsController = new CardsController(repository, null, Mockito.mock(AssociationRepository.class));

        final CardCreationRequest request = new CardCreationRequest();
        request.setModule(1);
        request.setData(Mockito.mock(MultipartFile.class));
        request.setText("VALID TEXT");
        request.setDisplayDate(LocalDate.now());

        cardsController.createCard(request);

        Mockito.verify(repository).saveAndFlush(Mockito.any());
    }

    @Test
    void getByModule() {
        final CardRepository repository = Mockito.mock(CardRepository.class);
        final Card card = new Card();
        Mockito.when(repository.findByModule(Mockito.anyInt())).thenReturn(new ArrayList<>() {{ add(card); }});

        final CardsController controller = new CardsController(repository, null, Mockito.mock(AssociationRepository.class));

        assertEquals(card, controller.getByModule(1).get(0));

        Mockito.verify(repository, Mockito.times(1)).findByModule(Mockito.anyInt());
    }

    @Test
    void getViewedCardsByModule() {
        final SecurityContext context = Mockito.mock(SecurityContext.class);
        final Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("admin");

        SecurityContextHolder.setContext(context);

        final User user = new User();
        user.setRole(Role.ADMIN);
        user.setEmail("VALID EMAIL");
        user.setPassword("VALID PASSWORD");
        user.setName("admin");
        user.setViewedCards(new HashSet<>() {{
            final Card module1 = new Card();
            module1.setModule(1);
            final CardAssociation _1 = new CardAssociation();
            _1.setId(UUID.randomUUID());
            _1.setUser(user);
            _1.setCard(module1);
            add(_1);

            final Card module2 = new Card();
            final CardAssociation _2 = new CardAssociation();
            _2.setId(UUID.randomUUID());
            _2.setUser(user);
            _2.setCard(module2);
            module2.setModule(2);
            add(_2);

            final Card module22 = new Card();
            final CardAssociation _3 = new CardAssociation();
            _3.setId(UUID.randomUUID());
            _3.setUser(user);
            _3.setCard(module22);
            module22.setText("text");
            module22.setModule(2);
            add(_3);
        }});

        final UserRepository mock = Mockito.mock(UserRepository.class);
        Mockito.when(mock.findById(Mockito.any())).thenReturn(Optional.of(user));

        final CardsController cardsController = new CardsController(null, mock, Mockito.mock(AssociationRepository.class));

        assertEquals(1, cardsController.getViewedCardsByModule(1).size());
        assertEquals(2, cardsController.getViewedCardsByModule(2).size());

    }

    @Test
    void viewCard() {
        final SecurityContext context = Mockito.mock(SecurityContext.class);
        final Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(context.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("admin");

        SecurityContextHolder.setContext(context);

        final User user = new User();
        user.setRole(Role.ADMIN);
        user.setEmail("VALID EMAIL");
        user.setPassword("VALID PASSWORD");
        user.setName("admin");
        user.setViewedCards(new HashSet<>());

        final UserRepository mock = Mockito.mock(UserRepository.class);
        Mockito.when(mock.findById("admin")).thenReturn(Optional.of(user));

        final UUID uuid = UUID.randomUUID();
        final CardRepository cards = Mockito.mock(CardRepository.class);
        Mockito.when(cards.getById(uuid)).thenReturn(new Card());

        final CardsController controller = new CardsController(cards, mock, Mockito.mock(AssociationRepository.class));
        controller.viewCard(new CardsController.UUIDWrapper(uuid));

        assertEquals(1, user.getViewedCards().size());
        Mockito.verify(mock, Mockito.times(1)).saveAndFlush(Mockito.any());

    }

}