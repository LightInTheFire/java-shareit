package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save(): should persist a new user and return correct DTO")
    void testSaveUser() {
        NewUserDto newUser = new NewUserDto("Alex", "alex@mail.com");

        UserDto saved = userService.save(newUser);

        assertNotNull(saved.id());
        assertEquals("Alex", saved.name());
        assertEquals("alex@mail.com", saved.email());

        User userInDb = userRepository.findById(saved.id())
                .orElseThrow();

        assertEquals("Alex", userInDb.getName());
        assertEquals("alex@mail.com", userInDb.getEmail());
    }

    @Test
    @DisplayName("findById(): should return the correct user from the database")
    void testFindById() {
        User user = new User(null, "Maria", "maria@mail.com");
        user = userRepository.save(user);

        UserDto found = userService.findById(user.getId());

        assertEquals(user.getId(), found.id());
        assertEquals("Maria", found.name());
        assertEquals("maria@mail.com", found.email());
    }
}
