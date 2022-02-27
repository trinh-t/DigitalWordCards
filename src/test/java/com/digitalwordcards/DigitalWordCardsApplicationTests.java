package com.digitalwordcards;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import com.digitalwordcards.data.Card;
import com.digitalwordcards.data.User;
import com.digitalwordcards.data.repositories.CardRepository;
import com.digitalwordcards.data.repositories.UserRepository;
import com.digitalwordcards.data.requests.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.digitalwordcards.data.Role.STUDENT;
import static com.digitalwordcards.data.Role.TEACHER;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DigitalWordCardsApplicationTests {


    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext) {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {

        final var jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.execute("delete from application_user_viewed_cards");
        userRepository.deleteAllInBatch();
        cardRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Test creating a user as admin")
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateUser() throws Exception {

        final var userDto = new UserDto();
        // CASE N 1: Everything is fine
        userDto.setClazz("Valid class");
        userDto.setEmail("Valid email");
        userDto.setPassword("Valid password");
        userDto.setName("Valid name");
        userDto.setRole(TEACHER);

        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))).
                andExpect(status().isOk());

        assertEquals(1, userRepository.count());

    }

    @Test
    @DisplayName("Test creating a user not as admin")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testCreateUserNotAdmin() throws Exception {

        final var userDto = new UserDto();
        userDto.setClazz("Valid class");
        userDto.setEmail("Valid email");
        userDto.setPassword("Valid password");
        userDto.setName("Valid name");
        userDto.setRole(TEACHER);

        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto))).
                andExpect(status().isForbidden());

        assertEquals(0, userRepository.count());

    }

    @Test
    @DisplayName("Test creating a user that already exists")
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testCreateUserThatAlreadyExists() throws Exception {

        final var user = new User();
        // CASE N 1: Everything is fine
        user.setClazz("Valid class");
        user.setEmail("Valid email");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(TEACHER);

        userRepository.save(user);

        mockMvc.perform(
                post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user.toDto()))).
                andExpect(status().isBadRequest());

        assertEquals(1, userRepository.count());

    }

    @Test
    @DisplayName("Test deleting a user that doesn't exists")
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testDeleteUserThatDoesNotExists() throws Exception {

        mockMvc.perform(
                delete("/api/users/{email}", "Email That Does Not Exist")).
                andExpect(status().isNotFound());

        assertEquals(0, userRepository.count());

    }

    @Test
    @DisplayName("Test deleting a user that already exists")
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testDeletingAUserThatAlreadyExists() throws Exception {

        final var user = new User();

        user.setClazz("Valid class");
        user.setEmail("Valid email");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(TEACHER);

        userRepository.save(user);

        assertEquals(1, userRepository.count());

        mockMvc.perform(
                delete("/api/users/{email}", user.getEmail())).
                andExpect(status().isOk());

        assertEquals(0, userRepository.count());

    }


    @Test
    @DisplayName("Test fetching all user when there are users")
    @WithMockUser(username = "admin@gmail.com", authorities = "ADMIN")
    void testFetchUsers() throws Exception {

        final var user1 = new User();

        user1.setClazz("Valid class");
        user1.setEmail("Valid email 1");
        user1.setPassword("Valid password");
        user1.setName("Valid name");
        user1.setRole(TEACHER);

        final var user2 = new User();

        user2.setClazz("Valid class");
        user2.setEmail("Valid email 2");
        user2.setPassword("Valid password");
        user2.setName("Valid name");
        user2.setRole(TEACHER);

        userRepository.saveAll(List.of(user1, user2));

        assertEquals(2, userRepository.count());

        mockMvc.perform(
                get("/api/users/")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(2))).
                andExpect(jsonPath("$.[0].email").value(user1.getEmail())).
                andExpect(jsonPath("$.[1].email").value(user2.getEmail()));
    }


    @Test
    @DisplayName("Test creating a card")
    @WithMockUser(username = "teacher@gmail.com", authorities = "TEACHER")
    void testCreateCard() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        mockMvc.perform(multipart("/api/cards").
                file(new MockMultipartFile(
                        "image",
                        "card.jpeg",
                        MediaType.IMAGE_JPEG_VALUE,
                        Files.readAllBytes(testFile)
                )).
                param("text", "VALID TEXT").
                param("module", "1")).
                andExpect(status().isOk());

        assertEquals(1, cardRepository.count());

    }


    @Test
    @DisplayName("Test deleting a card that doesn't exists")
    @WithMockUser(username = "teacher@gmail.com", authorities = "TEACHER")
    void testDeleteCardThatDoesNotExists() throws Exception {

        mockMvc.perform(
                delete("/api/cards/{cardID}", UUID.randomUUID())).
                andExpect(status().isNotFound());

        assertEquals(0, userRepository.count());

    }

    @Test
    @DisplayName("Test deleting a card that already exists")
    @WithMockUser(username = "teacher@gmail.com", authorities = "TEACHER")
    void testDeletingACardThatAlreadyExists() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card = new Card();

        card.setModule(1);
        card.setImage(Files.readAllBytes(testFile));
        card.setText("VALID TEXT");

        cardRepository.save(card);

        assertEquals(1, cardRepository.count());

        mockMvc.perform(
                delete("/api/cards/{cardId}", card.getId())).
                andExpect(status().isOk());

        assertEquals(0, cardRepository.count());

    }

    @Test
    @DisplayName("Test fetching all cards when there are no cards")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testFetchNoCards() throws Exception {


        assertEquals(0, cardRepository.count());

        mockMvc.perform(
                get("/api/cards/")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Test fetching all cards when there are cards")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testFetchCards() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(1);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        mockMvc.perform(
                get("/api/cards/")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(2))).
                andExpect(jsonPath("$.[0].id").value(card1.getId().toString())).
                andExpect(jsonPath("$.[1].id").value(card2.getId().toString()));
    }


    @Test
    @DisplayName("Test getting a single card")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testGettingACardThatAlreadyExists() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card = new Card();

        card.setModule(1);
        card.setImage(Files.readAllBytes(testFile));
        card.setText("VALID TEXT");

        cardRepository.save(card);

        assertEquals(1, cardRepository.count());

        mockMvc.perform(
                get("/api/cards/{cardId}", card.getId())).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.id").value(card.getId().toString())).
                andExpect(jsonPath("$.module").value(card.getModule())).
                andExpect(jsonPath("$.text").value(card.getText()));
    }

    @Test
    @DisplayName("Test fetching all cards for a module")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testFetchCardsForModule() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(2);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        mockMvc.perform(
                get("/api/cards/module/{module}", 1)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(1))).
                andExpect(jsonPath("$.[0].id").value(card1.getId().toString()));
    }


    @Test
    @DisplayName("Test modifying for a card")
    @WithMockUser(username = "teacher@gmail.com", authorities = "TEACHER")
    void testUpdateCardForModule() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card = new Card();

        card.setModule(1);
        card.setImage(Files.readAllBytes(testFile));
        card.setText("VALID TEXT");

        cardRepository.save(card);

        assertEquals(1, cardRepository.count());

        mockMvc.perform(multipart("/api/cards/{cardId}", card.getId()).
                file(new MockMultipartFile(
                        "image",
                        "card.jpeg",
                        MediaType.IMAGE_JPEG_VALUE,
                        Files.readAllBytes(testFile)
                )).
                param("text", "UPDATED TEXT").
                param("module", "1")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.text").value("UPDATED TEXT"));
    }

    @Test
    @DisplayName("Test modifying for a card which does not exist")
    @WithMockUser(username = "teacher@gmail.com", authorities = "TEACHER")
    void testUpdateCardForCardNotExist() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        mockMvc.perform(multipart("/api/cards/{cardId}", UUID.randomUUID().toString()).
                file(new MockMultipartFile(
                        "image",
                        "card.jpeg",
                        MediaType.IMAGE_JPEG_VALUE,
                        Files.readAllBytes(testFile)
                )).
                param("text", "UPDATED TEXT").
                param("module", "1")).
                andExpect(status().isNotFound());

    }


    @Test
    @DisplayName("Test viewing a card for a user")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testViewingACard() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(2);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        final var user = new User();

        user.setClazz("Valid class");
        user.setEmail("student@gmail.com");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(TEACHER);

        userRepository.save(user);

        assertEquals(1, userRepository.count());


        mockMvc.perform(
                put("/api/users/{email}/viewed/{cardId}", user.getEmail(), card1.getId())).
                andExpect(status().isOk());

        final var dbUser = userRepository.getById(user.getEmail());

        assertEquals(1, dbUser.getViewedCards().size());
    }

    @Test
    @DisplayName("Test getting viewed cards for a module")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testGettingViewedCardsForAModule() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(2);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        final var user = new User();

        user.setClazz("Valid class");
        user.setEmail("student@gmail.com");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(TEACHER);

        userRepository.save(user);

        assertEquals(1, userRepository.count());


        mockMvc.perform(
                put("/api/users/{email}/viewed/{cardId}", user.getEmail(), card1.getId())).
                andExpect(status().isOk());

        final var dbUser = userRepository.getById(user.getEmail());

        assertEquals(1, dbUser.getViewedCards().size());

        mockMvc.perform(
                get("/api/users/{email}/module/{module}", user.getEmail(), card1.getModule())).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(1))).
                andExpect(jsonPath("$.[0].text").value(card1.getText()));

    }

    @Test
    @DisplayName("Test viewing a card using another user")
    @WithMockUser(username = "student1@gmail.com", authorities = "STUDENT")
    void testViewingACardWithOtherUser() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(2);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        final var user = new User();

        user.setClazz("Valid class");
        // Note this email address is different from the logged in user.
        user.setEmail("student2@gmail.com");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(TEACHER);

        userRepository.save(user);

        assertEquals(1, userRepository.count());

        mockMvc.perform(
                put("/api/users/{email}/viewed/{cardId}", user.getEmail(), card1.getId())).
                andExpect(status().isForbidden());

        final var dbUser = userRepository.getById(user.getEmail());

        assertEquals(0, dbUser.getViewedCards().size());
    }

    @Test
    @DisplayName("Test viewing for a card which does not exist")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testViewingACardCardDoesNotExist() throws Exception {


        final var user = new User();

        user.setClazz("Valid class");
        user.setEmail("student@gmail.com");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(TEACHER);

        userRepository.save(user);

        assertEquals(1, userRepository.count());

        mockMvc.perform(
                put("/api/users/{email}/viewed/{cardId}", user.getEmail(), UUID.randomUUID())).
                andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Test viewing a card for a user that does n ot exist")
    @WithMockUser(username = "student@gmail.com", authorities = "STUDENT")
    void testViewingACarUserNotExist() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(2);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        mockMvc.perform(
                put("/api/users/{email}/viewed/{cardId}", "student@gmail.com", card1.getId())).
                andExpect(status().isNotFound());

    }


    @Test
    @DisplayName("Test viewing a card report")
    @WithMockUser(username = "teacher@gmail.com", authorities = "TEACHER")
    void testClassReport() throws Exception {

        final var testFile = Paths.get("src/test/resources/card.jpeg");

        final var card1 = new Card();

        card1.setModule(1);
        card1.setImage(Files.readAllBytes(testFile));
        card1.setText("VALID TEXT");

        final var card2 = new Card();

        card2.setModule(2);
        card2.setImage(Files.readAllBytes(testFile));
        card2.setText("VALID TEXT");

        cardRepository.saveAll(List.of(card1, card2));

        assertEquals(2, cardRepository.count());

        final var user = new User();

        user.setClazz("Valid class");
        user.setEmail("Valid email");
        user.setPassword("Valid password");
        user.setName("Valid name");
        user.setRole(STUDENT);

        user.setViewedCards(Set.of(card1));
        userRepository.save(user);

        assertEquals(1, userRepository.count());

        mockMvc.perform(
                get("/api/class/{clazz}", user.getClazz())).
                andExpect(status().isOk()).
                andExpect(content().string("[\"         Valid email (Valid class) - 50,00%\"]"));

    }
}
