package hust.adventure.entities.state;

import hust.adventure.entities.base.GameEntity;

/**
 * Interface for GameEntity states (State Pattern).
 */
public interface EntityState {
    void enter(GameEntity entity);
    void update(GameEntity entity, float delta);
    void exit(GameEntity entity);
    
    // For compatibility with old enum checks if needed, or just use instanceof
    String getStateName();
}
