package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import vn.hust.hustgame.behaviors.IMovementBehavior;

public abstract class Actor extends Entity {
    protected IMovementBehavior movementBehavior;
    protected Direction direction;
    protected float speed;

    public Actor(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.direction = Direction.DOWN;
        this.speed = 80f;
    }

    public void setMovementBehavior(IMovementBehavior behavior) {
        this.movementBehavior = behavior;
    }

    @Override
    public void update(float delta) {
        // Assume default state
        state = EntityState.IDLE;
        
        if (movementBehavior != null) {
            movementBehavior.update(this, delta);
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
