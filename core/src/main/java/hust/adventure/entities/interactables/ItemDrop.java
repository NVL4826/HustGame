package hust.adventure.entities.interactables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.entities.base.MapObject;
import hust.adventure.collision.Collider;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.items.base.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a physical item dropped in the world. Renders using a per-item sprite from assets/items/ when available,
 * falling back to a colored rectangle.
 */
public class ItemDrop extends MapObject {
    private Item item;
    private Color color;

    /** Shared sprite cache – loaded lazily, disposed with disposeStaticResources(). */
    private static final Map<String, Texture> spriteCache = new HashMap<>();

    private static Texture getSprite(Item item) {
        if (item == null)
            return null;
        String path = item.getSpritePath();
        if (path == null || path.isEmpty())
            return null;
        if (!spriteCache.containsKey(path)) {
            if (Gdx.files.internal(path).exists()) {
                spriteCache.put(path, new Texture(Gdx.files.internal(path)));
            } else {
                spriteCache.put(path, null); // mark missing so we don't retry every frame
            }
        }
        return spriteCache.get(path);
    }

    public static void disposeStaticResources() {
        for (Texture t : spriteCache.values()) {
            if (t != null)
                t.dispose();
        }
        spriteCache.clear();
    }

    // ── Bob animation ────────────────────────────────────────────────────────
    private float bobTimer = 0f;
    private static final float BOB_SPEED = 2.5f;
    private static final float BOB_AMOUNT = 3f;

    public ItemDrop() {
        super(0, 0, 60, 60);
    }

    public ItemDrop(float x, float y, Item item, Color color) {
        super(x, y, 60, 60);
        init(x, y, item, color);
    }

    public void init(float x, float y, Item item, Color color) {
        setX(x);
        setY(y);
        this.item = item;
        this.color = color;
        this.bobTimer = (float) (Math.random() * Math.PI * 2); // random phase
        setDestroyed(false);
        EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.ITEM_DROPPED, this));
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
        bobTimer += delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (item == null)
            return;

        float bob = (float) Math.sin(bobTimer * BOB_SPEED) * BOB_AMOUNT;
        float drawX = getX() - getWidth() / 2f;
        float drawY = getY() - getHeight() / 2f + bob;

        Texture sprite = getSprite(item);
        if (sprite != null) {
            batch.setColor(Color.WHITE);
            batch.draw(sprite, drawX, drawY, getWidth(), getHeight());
        }
        // Items without a dedicated sprite are not drawn (no fallback colored rect)
    }

    public void drawDebug(ShapeRenderer sr) {
        if (color != null) {
            sr.setColor(color);
            sr.rect(getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight());
        }
    }

    @Override
    public void dispose() {
        // Static cache disposed separately
    }

    public Item getItem() {
        return item;
    }
}
