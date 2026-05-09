package hust.adventure.entities.base;

/**
 * Interface for objects that need to be updated every frame.
 */
public interface Updatable {
    void update(float delta);
}
