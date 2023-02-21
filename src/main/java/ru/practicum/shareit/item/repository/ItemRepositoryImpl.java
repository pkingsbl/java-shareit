//package ru.practicum.shareit.item.repository;
//
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.model.Item;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Repository
//public class ItemRepositoryImpl implements ItemRepository {
//    private Long idItem = 1L;
//    private final Map<Long, Item> items = new HashMap<>();
//
//    @Override
//    public Item add(Item item) {
//        item.setId(idItem);
//        items.put(idItem, item);
//        idItem++;
//        return item;
//    }
//
//    @Override
//    public Item change(Long itemId, ItemDto itemDto) {
//        if (itemDto.getName() != null) {
//            items.get(itemId).setName(itemDto.getName());
//        }
//        if (itemDto.getDescription() != null) {
//            items.get(itemId).setDescription(itemDto.getDescription());
//        }
//        if (itemDto.getAvailable() != null) {
//            items.get(itemId).setAvailable(itemDto.getAvailable());
//        }
//        return items.get(itemId);
//    }
//
//    @Override
//    public Item getById(Long itemId) {
//        return items.get(itemId);
//    }
//
//    @Override
//    public Collection<Item> get(Long userId) {
//        return items.values()
//                .stream().filter(item -> item.getOwner().getId().equals(userId)).collect(Collectors.toList());
//    }
//
//    @Override
//    public Collection<Item> getSearch(String text) {
//        return items.values().stream().filter(item -> (item.getName().toLowerCase().contains(text)
//                || item.getDescription().toLowerCase().contains(text))
//                && item.getAvailable()).collect(Collectors.toList());
//    }
//
//    @Override
//    public Item deleteById(Long itemId) {
//        return items.remove(itemId);
//    }
//}
