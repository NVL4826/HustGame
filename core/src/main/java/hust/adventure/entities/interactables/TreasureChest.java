package hust.adventure.entities.interactables;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.TreasureOpenedEvent;
import hust.adventure.utils.GamePools;

public class TreasureChest extends BaseEntity implements Interactable, Pool.Poolable {
    private Texture texture;
    private boolean opened = false;

    public TreasureChest() {
        super(0, 0, 32, 32);
    }

    public void init(float x, float y, Texture texture) {
        setX(x);
        setY(y);
        this.texture = texture;
        this.opened = false;
        setDestroyed(false);
    }

    @Override
    public void setCollider(hust.adventure.collision.Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                if (other instanceof Player) {
                    interact((Player) other);
                }
            });
        }
    }

    @Override
    public void interact(Player player) {
        if (!opened) {
            opened = true;
            TreasureOpenedEvent data = GamePools.obtain(TreasureOpenedEvent.class);
            data.init();
            GameEvent<TreasureOpenedEvent> event = GamePools.obtainEvent();
            event.init(EventType.TREASURE_OPENED, data);
            EventDispatcher.getInstance().dispatch(event);
            
            destroy();
        }
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!opened && texture != null) {
            batch.setColor(Color.YELLOW);
            batch.draw(texture, getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void reset() {
        setDestroyed(true);
        texture = null;
        opened = false;
        if (getCollider() != null) {
            getCollider().setListener(null);
        }
    }
}
