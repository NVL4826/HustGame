package vn.hust.hustgame;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class PlayerMovementBehavior implements IMovementBehavior {
    private IPlayerController controller;
    private TiledMap map;
    private Rectangle tempBounds;
    
    private static final float MAP_WIDTH  = 128 * 16f;
    private static final float MAP_HEIGHT = 128 * 16f;

    public PlayerMovementBehavior(IPlayerController controller, TiledMap map) {
        this.controller = controller;
        this.map = map;
        this.tempBounds = new Rectangle();
    }

    public void setMap(TiledMap map) {
        this.map = map;
    }

    @Override
    public void update(Entity entity, float delta) {
        if (!(entity instanceof Actor)) return;
        Actor actor = (Actor) entity;

        float newX = actor.getX();
        float newY = actor.getY();
        boolean moving = false;
        Direction newDirection = actor.getDirection();

        float currentSpeed = actor.getSpeed() * GameState.instance.coffeeSystem.getSpeedMultiplier();
        boolean isSprinting = controller.isRunning() && GameState.instance.stamina > 0;
        
        if (GameState.instance.stamina <= 0) {
            currentSpeed *= 0.6f; // speed -40% when 0 stamina
        } else if (isSprinting) {
            currentSpeed *= 1.8f; // Gấp 1.8 lần khi chạy
            GameState.instance.stamina -= 10 * delta; // consume stamina
        } else {
            // slowly recover stamina when walking or idle
            if (GameState.instance.stamina < GameState.instance.maxStamina) {
                GameState.instance.stamina += 2 * delta; 
            }
        }
        
        boolean moveRight = controller.isRight();
        boolean moveLeft = controller.isLeft();
        boolean moveUp = controller.isUp();
        boolean moveDown = controller.isDown();
        
        if (GameState.instance.coffeeSystem.hasReversedInput(MathUtils.random())) {
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
            actor.setState(EntityState.MOVING);
            if (!isColliding(actor, newX, newY)) {
                actor.setX(newX);
                actor.setY(newY);
            }
        } else {
            actor.setState(EntityState.IDLE);
        }

        // Clamp to map
        float mapW = MAP_WIDTH;
        float mapH = MAP_HEIGHT;
        if (map != null) {
            Integer w = map.getProperties().get("width", Integer.class);
            Integer th = map.getProperties().get("tilewidth", Integer.class);
            Integer h = map.getProperties().get("height", Integer.class);
            Integer tv = map.getProperties().get("tileheight", Integer.class);
            if (w != null && th != null) mapW = w * th;
            if (h != null && tv != null) mapH = h * tv;
        }

        actor.setX(MathUtils.clamp(actor.getX(), actor.getWidth() / 2f, mapW - actor.getWidth() / 2f));
        actor.setY(MathUtils.clamp(actor.getY(), actor.getHeight() / 2f, mapH - actor.getHeight() / 2f));
    }

    private boolean isColliding(Actor actor, float newX, float newY) {
        if (map == null || map.getLayers().get("Object Layer 1") == null) return false;

        float feetWidth = actor.getWidth() * 0.4f; // ~20px
        float feetHeight = actor.getHeight() * 0.2f; // ~10px

        float boxX = newX - feetWidth / 2f;
        float boxY = newY - actor.getHeight() / 2f;
        tempBounds.set(boxX, boxY, feetWidth, feetHeight);

        MapObjects objects = map.getLayers().get("Object Layer 1").getObjects();

        for (RectangleMapObject rectObject : objects.getByType(RectangleMapObject.class)) {
            Rectangle rect = rectObject.getRectangle();
            if (tempBounds.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }
}
