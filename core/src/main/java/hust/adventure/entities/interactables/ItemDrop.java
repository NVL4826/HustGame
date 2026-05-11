package hust.adventure.entities.interactables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Pool;
import hust.adventure.utils.GamePools;

import hust.adventure.entities.base.BaseEntity;
import hust.adventure.collision.Collider;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.items.Item;

/**
 * Represents a physical item dropped in the world. Decoupled from Inventory: dispatches an event when picked up.
 * Supports object pooling.
 */
public class ItemDrop extends BaseEntity implements Pool.Poolable {
    private Item item;
    private Color color;

    /**
     * Default constructor for memory allocation.
     */
    public ItemDrop() {
        super(0, 0, 15, 15);
    }

    public ItemDrop(float x, float y, Item item, Color color) {
        this();
        init(x, y, item, color);
    }

    /**
     * Runtime Constructor (init). Sets state when obtained from pool.
     */
    public void init(float x, float y, Item item, Color color) {
        setX(x);
        setY(y);
        this.item = item;
        this.color = color;
        setDestroyed(false);
    }

    @Override
    public void reset() {
        setDestroyed(true);
        item = null;
        color = null;
        if (getCollider() != null) {
            getCollider().setListener(null);
        }
    }

    @Override
    public void setCollider(Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                // Obtain pooled events
                ItemPickedUpEvent payload = GamePools.obtain(ItemPickedUpEvent.class);
                payload.init(item, other);

                GameEvent<ItemPickedUpEvent> event = GamePools.obtainEvent();
                event.init(EventType.ITEM_PICKED_UP, payload);

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
