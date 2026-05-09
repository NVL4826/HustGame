package hust.adventure.events;

public class GameEvent<T> {
    private final EventType type;
    private final T data;

    public GameEvent(EventType type, T data) {
        this.type = type;
        this.data = data;
    }

    public EventType getType() {
        return type;
    }

    public T getData() {
        return data;
    }
}
