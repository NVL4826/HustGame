package hust.adventure.events;
 
import hust.adventure.entities.base.GameEntity;
import hust.adventure.items.Item;
 
/**
 * Payload for the ITEM_PICKED_UP event.
 */
public class ItemPickedUpEvent {
    private final Item item;
    private final GameEntity picker;
 
    public ItemPickedUpEvent(Item item, GameEntity picker) {
        if (item == null) throw new NullPointerException("item cannot be null");
        if (picker == null) throw new NullPointerException("picker cannot be null");
        this.item = item;
        this.picker = picker;
    }
 
    public Item getItem() {
        return item;
    }
 
    public GameEntity getPicker() {
        return picker;
    }
}
