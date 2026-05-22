package hust.adventure.entities.interactables;
 
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
 
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.collision.Collider;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.items.Item;
 
/**
 * Represents a physical item dropped in the world. Decoupled from Inventory: dispatches an event when picked up.
 */
public class ItemDrop extends BaseEntity {
    private Item item;
    private Color color;
 
    public ItemDrop() {
        super(0, 0, 15, 15);
    }
 
    public ItemDrop(float x, float y, Item item, Color color) {
        super(x, y, 15, 15);
        init(x, y, item, color);
    }
 
    /**
     * Sets state for the item drop.
     */
    public void init(float x, float y, Item item, Color color) {
        setX(x);
        setY(y);
        this.item = item;
        this.color = color;
        setDestroyed(false);
    }
 
    @Override
    public void setCollider(Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                ItemPickedUpEvent payload = new ItemPickedUpEvent(item, other);
                GameEvent<ItemPickedUpEvent> event = new GameEvent<>(EventType.ITEM_PICKED_UP, payload);
 
                EventDispatcher.getInstance().dispatch(event);
                destroy();
            });
        }
    }
 
    @Override
    public void update(float delta) {
        // Stationary for now
    }
 
    @Override
    public void draw(SpriteBatch batch) {
        if (color != null) {
            drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), color);
        }
    }
 
    public void drawDebug(ShapeRenderer sr) {
        if (color != null) {
            sr.setColor(color);
            sr.rect(getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight());
        }
    }
 
    @Override
    public void dispose() {
        // No native resources to clean up here
    }
 
    public Item getItem() {
        return item;
    }
}
