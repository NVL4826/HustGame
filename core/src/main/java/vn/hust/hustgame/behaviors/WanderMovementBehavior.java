package vn.hust.hustgame.behaviors;

import com.badlogic.gdx.math.MathUtils;
import vn.hust.hustgame.entities.*;

public class WanderMovementBehavior implements IMovementBehavior {
    private float wanderTimer;
    private float maxWanderTime;
    private Direction currentWanderDirection;

    public WanderMovementBehavior(float maxWanderTime) {
        this.maxWanderTime = maxWanderTime;
        this.wanderTimer = MathUtils.random(maxWanderTime);
        pickRandomDirection();
    }

    private void pickRandomDirection() {
        int r = MathUtils.random(3);
        switch (r) {
            case 0: currentWanderDirection = Direction.UP; break;
            case 1: currentWanderDirection = Direction.DOWN; break;
            case 2: currentWanderDirection = Direction.LEFT; break;
            case 3: currentWanderDirection = Direction.RIGHT; break;
        }
    }

    @Override
    public void update(Entity entity, float delta) {
        if (!(entity instanceof Actor)) return;
        Actor actor = (Actor) entity;

        wanderTimer -= delta;

        if (wanderTimer <= 0) {
            // Pick a new direction and reset timer or stop moving
            if (MathUtils.randomBoolean(0.5f)) {
                pickRandomDirection();
                actor.setState(EntityState.MOVING);
            } else {
                actor.setState(EntityState.IDLE);
            }
            wanderTimer = MathUtils.random(maxWanderTime);
        }

        if (actor.getState() == EntityState.MOVING) {
            actor.setDirection(currentWanderDirection);
            float newX = actor.getX();
            float newY = actor.getY();

            switch (currentWanderDirection) {
                case UP: newY += actor.getSpeed() * delta; break;
                case DOWN: newY -= actor.getSpeed() * delta; break;
                case LEFT: newX -= actor.getSpeed() * delta; break;
                case RIGHT: newX += actor.getSpeed() * delta; break;
            }

            // Just updating position naively for Wander. Collision could be added.
            actor.setX(newX);
            actor.setY(newY);
        }
    }
}
