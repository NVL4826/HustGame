package hust.adventure.entities.components;

import com.badlogic.gdx.math.MathUtils;

import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.base.Direction;
import hust.adventure.entities.state.IdleState;
import hust.adventure.entities.state.MovingState;

public class WanderMovementBehavior implements MovementBehavior {
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
        case 0:
            currentWanderDirection = Direction.UP;
            break;
        case 1:
            currentWanderDirection = Direction.DOWN;
            break;
        case 2:
            currentWanderDirection = Direction.LEFT;
            break;
        case 3:
            currentWanderDirection = Direction.RIGHT;
            break;
        }
    }

    @Override
    public void update(BaseEntity entity, float delta) {
        if (!(entity instanceof BaseActor))
            return;
        BaseActor actor = (BaseActor) entity;

        wanderTimer -= delta;

        if (wanderTimer <= 0) {
            // Pick a new direction and reset timer or stop moving
            if (MathUtils.randomBoolean(0.5f)) {
                pickRandomDirection();
                actor.setState(new MovingState());
            } else {
                actor.setState(new IdleState());
            }
            wanderTimer = MathUtils.random(maxWanderTime);
        }

        if (actor.getState() instanceof MovingState) {
            actor.setDirection(currentWanderDirection);
            float newX = actor.getX();
            float newY = actor.getY();

            switch (currentWanderDirection) {
            case UP:
                newY += actor.getSpeed() * delta;
                break;
            case DOWN:
                newY -= actor.getSpeed() * delta;
                break;
            case LEFT:
                newX -= actor.getSpeed() * delta;
                break;
            case RIGHT:
                newX += actor.getSpeed() * delta;
                break;
            }

            // Di chuyển trực tiếp, không chặn bởi tường
            actor.setX(newX);
            actor.setY(newY);
        }
    }
}
