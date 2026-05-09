package hust.adventure.entities.state;

import hust.adventure.entities.base.GameEntity;

public class MovingState implements EntityState {
    @Override public void enter(GameEntity entity) {}
    @Override public void update(GameEntity entity, float delta) {}
    @Override public void exit(GameEntity entity) {}
    @Override public String getStateName() { return "MOVING"; }
}
