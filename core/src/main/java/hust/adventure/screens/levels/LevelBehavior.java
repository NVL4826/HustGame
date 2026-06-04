package hust.adventure.screens.levels;

import hust.adventure.events.EventListener;
import hust.adventure.events.GameEvent;

/**
 * Interface for map-specific game behaviors/scripts.
 */
public interface LevelBehavior extends EventListener {
    default void init(final LevelContext context) {}
    default void update(final LevelContext context, final float delta) {}
    default void draw(final LevelContext context) {}
    default void dispose(final LevelContext context) {}

    /**
     * Default no-op event handler. Override in behaviors that react to game events.
     *
     * @param event the game event to process
     */
    @Override
    default void onEvent(final GameEvent<?> event) {}

    /**
     * Checks if the level allows transitioning to another map.
     * @param context the level context
     * @return true if transitioning is allowed, false otherwise
     */
    default boolean canTransition(final LevelContext context) {
        return true;
    }
}
