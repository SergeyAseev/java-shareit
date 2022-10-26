package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    User user1;
    Item item1;
    User user2;
    Item item2;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user1", "user1@email"));
        item1 = itemRepository.save(new Item(1L, user1, "item1", "description1", true, null,
                null));

        user2 = userRepository.save(new User(2L, "user2", "user2@email"));
        item2 = itemRepository.save(new Item(2L, user2, "item2", "description2", true, null,
                null));
    }

    @Test
    void findByOwner() {
        final List<Item> byOwner = (List<Item>) itemRepository.findByOwnerIdOrderByIdAsc(user1.getId());

        assertNotNull(byOwner);
        assertEquals(1, byOwner.size());
        assertEquals("item1", byOwner.get(0).getName());
    }

    @Test
    void findByTextTest() {

        Collection<Item> itemList = itemRepository.findByKeyword("descrip");
        assertThat(itemList.size(), is(2));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}