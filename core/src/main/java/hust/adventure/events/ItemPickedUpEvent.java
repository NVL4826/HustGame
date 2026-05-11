package hust.adventure.events;
 
import com.badlogic.gdx.utils.Pool;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.items.Item;
 
/**
 * Payload for the ITEM_PICKED_UP event, supports object pooling.
 */
public class ItemPickedUpEvent implements Pool.Poolable {
    private Item item;
    private GameEntity picker;
 
    /**
     * Default constructor for pooling.
     */
    public ItemPickedUpEvent() {
    }
 
    public ItemPickedUpEvent(Item item, GameEntity picker) {
        init(item, picker);
    }
 
    public void init(Item item, GameEntity picker) {
        this.item = item;
        this.picker = picker;
    }
 
    @Override
    public void reset() {
        this.item = null;
        this.picker = null;
    }
 
    public Item getItem() {
        return item;
    }
 
    public GameEntity getPicker() {
        return picker;
    }
}
