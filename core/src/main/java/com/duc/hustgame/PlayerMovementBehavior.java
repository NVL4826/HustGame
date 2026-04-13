package com.duc.hustgame;

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

        if (controller.isRight()) {
            newX += actor.getSpeed() * delta;
            newDirection = Direction.RIGHT;
            moving = true;
        } else if (controller.isLeft()) {
            newX -= actor.getSpeed() * delta;
            newDirection = Direction.LEFT;
            moving = true;
        } else if (controller.isUp()) {
            newY += actor.getSpeed() * delta;
            newDirection = Direction.UP;
            moving = true;
        } else if (controller.isDown()) {
            newY -= actor.getSpeed() * delta;
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
        actor.setX(MathUtils.clamp(actor.getX(), actor.getWidth() / 2f, MAP_WIDTH - actor.getWidth() / 2f));
        actor.setY(MathUtils.clamp(actor.getY(), actor.getHeight() / 2f, MAP_HEIGHT - actor.getHeight() / 2f));
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
