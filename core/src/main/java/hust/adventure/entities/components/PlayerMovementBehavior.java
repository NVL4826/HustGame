package hust.adventure.entities.components;

import com.badlogic.gdx.math.MathUtils;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.base.Direction;
import hust.adventure.entities.state.IdleState;
import hust.adventure.entities.state.MovingState;
import hust.adventure.entities.status.StatusFlag;
import hust.adventure.input.PlayerController;

public class PlayerMovementBehavior implements MovementBehavior {
    private PlayerController controller;
    private CollisionManager collisionManager;

    public PlayerMovementBehavior(PlayerController controller, CollisionManager collisionManager) {
        this.controller = controller;
        this.collisionManager = collisionManager;
    }

    public void setCollisionManager(CollisionManager collisionManager) {
        this.collisionManager = collisionManager;
    }

    @Override
    public void update(BaseEntity entity, float delta) {
        if (!(entity instanceof BaseActor))
            return;
        BaseActor actor = (BaseActor) entity;

        float newX = actor.getX();
        float newY = actor.getY();
        boolean moving = false;
        Direction newDirection = actor.getDirection();

        float currentSpeed = actor.getSpeed();
        boolean isSprinting = controller.isRunning() && actor.getStamina() > 0;

        if (actor.getStamina() <= 0) {
            currentSpeed *= 0.6f; // speed -40% when 0 stamina
        } else if (isSprinting) {
            currentSpeed *= 1.8f; // Gấp 1.8 lần khi chạy
            actor.restoreStamina(-10 * delta); // consume stamina
        } else {
            // slowly recover stamina when walking or idle
            actor.restoreStamina(2 * delta);
        }

        boolean moveRight = controller.isRight();
        boolean moveLeft = controller.isLeft();
        boolean moveUp = controller.isUp();
        boolean moveDown = controller.isDown();

        if (actor.hasStatus(StatusFlag.CONFUSED) && MathUtils.random() < 0.15f) {
            boolean tempRight = moveRight;
            boolean tempLeft = moveLeft;
            boolean tempUp = moveUp;
            boolean tempDown = moveDown;
            moveRight = tempLeft;
            moveLeft = tempRight;
            moveUp = tempDown;
            moveDown = tempUp;
        }

        if (moveRight) {
            newX += currentSpeed * delta;
            newDirection = Direction.RIGHT;
            moving = true;
        } else if (moveLeft) {
            newX -= currentSpeed * delta;
            newDirection = Direction.LEFT;
            moving = true;
        } else if (moveUp) {
            newY += currentSpeed * delta;
            newDirection = Direction.UP;
            moving = true;
        } else if (moveDown) {
            newY -= currentSpeed * delta;
            newDirection = Direction.DOWN;
            moving = true;
        }

        actor.setDirection(newDirection);

        if (moving) {
            actor.setState(new MovingState());
            if (collisionManager.canMove(actor, newX, newY)) {
                actor.setX(newX);
                actor.setY(newY);
            }
        } else {
            actor.setState(new IdleState());
        }
    }
}
