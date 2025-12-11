package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("create(): should create a new item request for the user")
    void testCreateRequest() {
        User user = saveUser("Alex", "alex@mail.com");
        NewItemRequestDto dto = new NewItemRequestDto("Need a drill");

        ItemRequestDto saved = itemRequestService.create(user.getId(), dto);

        assertNotNull(saved.id());
        assertEquals("Need a drill", saved.description());
        assertEquals(user.getId(), saved.requestorId());

        ItemRequest requestInDb = itemRequestRepository.findById(saved.id())
                .orElseThrow();
        assertEquals("Need a drill", requestInDb.getDescription());
        assertEquals(user.getId(), requestInDb.getRequestor().getId());
        assertNotNull(requestInDb.getCreatedAt());
    }

    @Test
    @DisplayName("getAll(): should return all item requests sorted by createdAt DESC")
    void testGetAll() {
        User user = saveUser("Bob", "bob@mail.com");

        ItemRequest r1 = itemRequestRepository.save(
                new ItemRequest(null, "Request A", user, LocalDateTime.now().minusDays(1))
        );
        ItemRequest r2 = itemRequestRepository.save(
                new ItemRequest(null, "Request B", user, LocalDateTime.now())
        );

        Collection<ItemRequestDto> result = itemRequestService.getAll();

        assertEquals(2, result.size());

        List<ItemRequestDto> list = result.stream().toList();

        assertEquals(r2.getId(), list.get(0).id());
        assertEquals(r1.getId(), list.get(1).id());
    }

    @Test
    @DisplayName("getById(): should return item request with its responses")
    void testGetById() {
        User user = saveUser("Chris", "chris@mail.com");

        ItemRequest request = itemRequestRepository.save(
                new ItemRequest(null, "Need a ladder", user, LocalDateTime.now())
        );

        Item item1 = itemRepository.save(
                new Item(null, "Ladder", "Good ladder", true, user, request)
        );

        Item item2 = itemRepository.save(
                new Item(null, "Small Ladder", "Compact", true, user, request)
        );

        ItemRequestWithResponsesDto result = itemRequestService.getById(request.getId());

        assertEquals(request.getId(), result.id());
        assertEquals("Need a ladder", result.description());
        assertEquals(2, result.items().size());

        List<String> itemNames = result.items().stream()
                .map(ItemForRequestDto::name)
                .toList();

        assertTrue(itemNames.contains("Ladder"));
        assertTrue(itemNames.contains("Small Ladder"));
    }

    private User saveUser(String name, String email) {
        return userRepository.save(new User(null, name, email));
    }
}
