package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(Long userId);

    @Query("select i from Item i where i.available = true " +
            " and (lower(i.name) like lower(concat('%',:keyword,'%')) " +
            " or lower(i.description) like lower(concat('%',:keyword,'%')))")
    Collection<Item> findByKeyword(@Param("keyword") String keyword);

    List<Item> findByRequest_Id(Long id, Sort sort);
}
