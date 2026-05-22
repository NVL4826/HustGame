package hust.adventure.screens.levels;

/**
 * Interface for map-specific game behaviors/scripts.
 */
public interface LevelBehavior {
    void init(final LevelContext context);
    void update(final LevelContext context, final float delta);
    void draw(final LevelContext context);
    void dispose(final LevelContext context);

    /**
     * Checks if the level allows transitioning to another map.
     * @param context the level context
     * @return true if transitioning is allowed, false otherwise
     */
    default boolean canTransition(final LevelContext context) {
        return true;
    }
}
