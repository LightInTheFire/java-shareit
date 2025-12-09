package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long userId);

    @Query("""
                select i from Item i
                where i.available = true
                    and (lower(i.name)) like lower(concat('%', :query, '%'))
                        or lower(i.description) like lower(concat('%', :query, '%'))
            """)
    List<Item> search(String query);

    List<Item> findAllByRequest_Id(Long requestId);

    List<Item> findAllByRequest_IdIn(Collection<Long> requestIds);
}
