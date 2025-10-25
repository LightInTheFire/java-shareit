package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateDataException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class FakeUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();

    @Override
    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public Optional<User> findById(long id) {
        User user = users.get(id);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(new User(
                user.getId(),
                user.getName(),
                user.getEmail()));
    }

    @Override
    public User save(User user) {
        Long id = generateNextId();
        user.setId(id);

        if (userEmails.contains(user.getEmail())) {
            throw new DuplicateDataException("email %s already exists".formatted(user.getEmail()));
        }

        users.put(id, user);
        userEmails.add(user.getEmail());
        return user;
    }

    @Override
    public void update(User user) {
        User currentUser = users.get(user.getId());

        if (!currentUser.getEmail().equals(user.getEmail())) {
            if (userEmails.contains(user.getEmail())) {
                throw new DuplicateDataException("email %s already exists".formatted(user.getEmail()));
            }

            userEmails.remove(currentUser.getEmail());
            userEmails.add(user.getEmail());
        }

        users.put(user.getId(), user);
    }

    @Override
    public void delete(long id) {
        User removedUser = users.remove(id);
        userEmails.remove(removedUser.getEmail());
    }

    private Long generateNextId() {
        Long nextId = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return ++nextId;
    }
}
