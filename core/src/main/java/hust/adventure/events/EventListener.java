package hust.adventure.events;

public interface EventListener {
    void onEvent(GameEvent<?> event);
}
