package hust.adventure.events;
 
import com.badlogic.gdx.utils.Pool;
import hust.adventure.utils.GamePools;
 
/**
 * Generic event class that supports object pooling.
 * @param <T> The type of data associated with the event.
 */
public class GameEvent<T> implements Pool.Poolable {
    private EventType type;
    private T data;
 
    /**
     * Default constructor for pooling.
     */
    public GameEvent() {
    }
 
    public GameEvent(EventType type, T data) {
        init(type, data);
    }
 
    public void init(EventType type, T data) {
        this.type = type;
        this.data = data;
    }
 
    @Override
    public void reset() {
        if (data instanceof Pool.Poolable) {
            GamePools.free(data);
        }
        this.type = null;
        this.data = null;
    }
 
    public EventType getType() {
        return type;
    }
 
    public T getData() {
        return data;
    }
}
