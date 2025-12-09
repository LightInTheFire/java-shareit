package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(long requestorId, NewItemRequestDto newItemRequestDto) {
        User requestor = getUserOrThrow(requestorId);
        LocalDateTime now = LocalDateTime.now();

        ItemRequest itemRequest = ItemRequestMapper.toEntity(newItemRequestDto, requestor, now);
        ItemRequest savedRequest = requestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public Collection<ItemRequestWithResponsesDto> getAllOfUser(long userId) {
        getUserOrThrow(userId);
        List<ItemRequest> requestsOfUser =
                requestRepository.findAllByRequestor_IdOrderByCreatedAtDesc(userId);
        List<Long> requestsIds = requestsOfUser.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> itemResponsesForRequests = itemRepository.findAllByRequest_IdIn(requestsIds);
        Map<Long, List<Item>> itemsByRequestId = itemResponsesForRequests.stream()
                .collect(Collectors.groupingBy(Item::getId));

        return requestsOfUser.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestWithResponseDto(
                        itemRequest,
                        itemsByRequestId.get(itemRequest.getId())
                ))
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getAll() {
        List<ItemRequest> requests =
                requestRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestWithResponsesDto getById(long requestId) {
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(
                NotFoundException.supplier("Request with id %d not found", requestId)
        );
        List<Item> itemResponses = itemRepository.findAllByRequest_Id(requestId);
        return ItemRequestMapper.toItemRequestWithResponseDto(request, itemResponses);
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId).orElseThrow(
                NotFoundException.supplier("User with id %d not found", userId)
        );
    }
}
