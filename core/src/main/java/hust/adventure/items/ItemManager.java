package hust.adventure.items;

import hust.adventure.items.types.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry and manager for all item definitions.
 */
public class ItemManager {
    public static final ItemManager instance = new ItemManager();
    private final Map<String, Item> items;

    private ItemManager() {
        items = new HashMap<>();
        registerItems();
    }

    private void registerItems() {
        register(new BlackCoffee());
        register(new MilkCoffee());
        register(new IcedCoffee());
        register(new WeaselCoffee());
        register(new EnergyDrink());
        register(new DriedChicken());
        register(new SimpleItem("note", "Ghi chú", "Một mảnh giấy nhỏ có chữ."));
    }

    public void register(Item item) {
        items.put(item.getId(), item);
    }

    public Item getItem(String id) {
        return items.get(id);
    }
}
