package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected float width, height;
    protected Rectangle bounds;
    protected boolean isDestroyed;
    protected EntityState state;

    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height);
        this.isDestroyed = false;
        this.state = EntityState.IDLE;
    }

    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);
    public abstract void dispose();

    public float getX() { return x; }
    public void setX(float x) { this.x = x; updateBounds(); }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; updateBounds(); }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    
    public Rectangle getBounds() { return bounds; }
    
    protected void updateBounds() {
        this.bounds.setPosition(x, y);
    }
    
    public boolean isDestroyed() { return isDestroyed; }
    public void setDestroyed(boolean destroyed) { this.isDestroyed = destroyed; }
    
    public EntityState getState() { return state; }
    public void setState(EntityState state) { this.state = state; }
}
