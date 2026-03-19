package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User user;
    private NewItemRequestDto newRequestDto;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1L, "User", "user@example.com");
        newRequestDto = new NewItemRequestDto("Need a drill");
        request = new ItemRequest(1L, "Need a drill", user, LocalDateTime.now());
    }

    @Test
    @DisplayName("create - should save request successfully")
    void create_ok() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.save(any())).thenReturn(request);

        ItemRequestDto dto = service.create(user.getId(), newRequestDto);

        assertNotNull(dto);
        assertEquals(request.getId(), dto.id());
        verify(requestRepository).save(any());
    }

    @Test
    @DisplayName("create - should throw NotFoundException when user not found")
    void create_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(user.getId(), newRequestDto));
    }

    @Test
    @DisplayName("getAllOfUser - should return requests with items")
    void getAllOfUser_ok() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestor_IdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(List.of(request));
        Item item = new Item(1L, "Drill", "Powerful drill", true, user, request);
        when(itemRepository.findAllByRequest_IdIn(List.of(request.getId())))
                .thenReturn(List.of(item));

        Collection<ItemRequestWithResponsesDto> result = service.getAllOfUser(user.getId());

        assertEquals(1, result.size());
        verify(requestRepository).findAllByRequestor_IdOrderByCreatedAtDesc(user.getId());
    }

    @Test
    @DisplayName("getAll - should return all requests sorted")
    void getAll_ok() {
        when(requestRepository.findAll(Mockito.any(Sort.class)))
                .thenReturn(List.of(request));

        Collection<ItemRequestDto> result = service.getAll();

        assertEquals(1, result.size());
        verify(requestRepository).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("getById - should return request with items")
    void getById_ok() {
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        Item item = new Item(1L, "Drill", "Powerful drill", true, user, request);
        when(itemRepository.findAllByRequest_Id(request.getId())).thenReturn(List.of(item));

        ItemRequestWithResponsesDto dto = service.getById(request.getId());

        assertNotNull(dto);
        assertEquals(request.getId(), dto.id());
        verify(requestRepository).findById(request.getId());
        verify(itemRepository).findAllByRequest_Id(request.getId());
    }

    @Test
    @DisplayName("getById - should throw NotFoundException when request not found")
    void getById_notFound() {
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(request.getId()));
    }
}
