package hust.adventure.entities.interactables;
 
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.TreasureOpenedEvent;
import hust.adventure.collision.Collider;
 
public class TreasureChest extends BaseEntity implements Interactable {
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
    public void setCollider(Collider collider) {
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
            TreasureOpenedEvent data = new TreasureOpenedEvent();
            GameEvent<TreasureOpenedEvent> event = new GameEvent<>(EventType.TREASURE_OPENED, data);
            EventDispatcher.getInstance().dispatch(event);
            EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/chest_open.mp3"));
            
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
}
