package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequester_IdOrderByCreatedAsc(Long userId);

    List<ItemRequest> findByRequester_IdNot(Long userId, Pageable pageable);

    @Query("select i from Item i where i.request in (:reqs)")
    List<Item> findItemsByListOfRequests(@Param("reqs") List<ItemRequest> itemRequests);
}
