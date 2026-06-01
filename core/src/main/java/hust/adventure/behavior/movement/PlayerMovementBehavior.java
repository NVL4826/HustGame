package hust.adventure.behavior.movement;

import com.badlogic.gdx.math.MathUtils;

import hust.adventure.collision.CollisionManager;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.player.Player;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.base.StatusFlag;
import hust.adventure.entities.base.Direction;
import hust.adventure.entities.state.IdleState;
import hust.adventure.entities.state.MovingState;
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
    public void update(MapObject entity, float delta) {
        if (!(entity instanceof Player))
            return;
        Player player = (Player) entity;

        float newX = player.getX();
        float newY = player.getY();
        boolean moving = false;
        Direction newDirection = player.getDirection();

        float currentSpeed = player.getSpeed();
        boolean isSprinting = controller.isRunning() && player.getStamina() > 0;

        if (ProgressContext.instance.isFastRun()) {
            currentSpeed *= 3.0f;
            player.restoreStamina(100f * delta);
        } else if (player.getStamina() <= 0) {
            currentSpeed *= 0.6f; // speed -40% when 0 stamina
        } else if (isSprinting) {
            currentSpeed *= 1.8f; // Gấp 1.8 lần khi chạy
            player.restoreStamina(-10 * delta); // consume stamina
        } else {
            // slowly recover stamina when walking or idle
            player.restoreStamina(2 * delta);
        }

        boolean moveRight = controller.isRight();
        boolean moveLeft = controller.isLeft();
        boolean moveUp = controller.isUp();
        boolean moveDown = controller.isDown();

        if (player.hasStatus(StatusFlag.CONFUSED) && MathUtils.random() < 0.15f) {
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

        player.setDirection(newDirection);

        if (moving) {
            player.setState(new MovingState());
            if (collisionManager.canMove(player, newX, newY)) {
                player.setX(newX);
                player.setY(newY);
            }
        } else {
            player.setState(new IdleState());
        }
    }
}
