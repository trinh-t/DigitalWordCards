package com.digitalwordcards.controllers;

import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.UserCreationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserRepository repository;

    @Test
    void createUser() {
        final UserController controller = new UserController(repository, new BCryptPasswordEncoder(10));
        final UserCreationRequest request = new UserCreationRequest();

        // Spring Security mocking
        final Authentication auth = Mockito.mock(Authentication.class);
        final SecurityContext context = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);
        Mockito.when(auth.getAuthorities()).thenReturn(new ArrayList() {{ add(new SimpleGrantedAuthority("ADMIN")); }});
        Mockito.when(context.getAuthentication()).thenReturn(auth);

        // CASE N 1: Everything is fine
        request.setClazz("Valid class");
        request.setEmail("Valid email");
        request.setPassword("Valid password");
        request.setName("Valid name");
        request.setRole("TEACHER");

        assertDoesNotThrow(() -> controller.createUser(request));

        // CASE N 2: Invalid role
        request.setClazz("Valid class");
        request.setEmail("Valid email");
        request.setPassword("Valid password");
        request.setName("Valid name");
        request.setRole("INVALID ROLE");

        assertThrows(ResponseStatusException.class, () -> controller.createUser(request), "Unknown user role!!");

        // CASE N 3: Role issue
        Mockito.when(auth.getAuthorities()).thenReturn(new ArrayList() {{ add(new SimpleGrantedAuthority("STUDENT")); }});

        request.setClazz("Valid class");
        request.setEmail("Valid email");
        request.setPassword("Valid password");
        request.setName("Valid name");
        request.setRole("ADMIN");

        assertThrows(ResponseStatusException.class, () -> controller.createUser(request), "You cannot create an user with TEACHER authority!!");

    }
}