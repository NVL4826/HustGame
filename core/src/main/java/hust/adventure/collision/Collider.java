package hust.adventure.collision;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import hust.adventure.entities.base.MapObject;

/**
 * Component for handling physical hitboxes and collision detection.
 */
public class Collider {
    public enum Shape {
        CIRCLE,
        RECTANGLE
    }

    private MapObject owner;
    private int layer;
    private Shape shape;
    private float radius; 
    private Circle circle; 
    private CollisionListener listener;

    public interface CollisionListener {
        void onCollision(MapObject other);
    }

    public Collider(MapObject owner, int layer, Shape shape) {
        this.owner = owner;
        this.layer = layer;
        this.shape = shape;
        if (shape == Shape.CIRCLE) {
            this.radius = Math.max(owner.getWidth(), owner.getHeight()) / 2f;
            this.circle = new Circle(owner.getX(), owner.getY(), radius);
        }
    }

    public Collider(MapObject owner, int layer, Shape shape, float radius) {
        this.owner = owner;
        this.layer = layer;
        this.shape = shape;
        this.radius = radius;
        if (shape == Shape.CIRCLE) {
            this.circle = new Circle(owner.getX(), owner.getY(), radius);
        }
    }

    public MapObject getOwner() {
        return owner;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Shape getShape() {
        return shape;
    }

    public float getRadius() {
        return radius;
    }

    public void setListener(CollisionListener listener) {
        this.listener = listener;
    }

    public void handleCollision(MapObject other) {
        if (listener != null) {
            listener.onCollision(other);
        }
    }

    public boolean intersects(Collider other) {
        if (this.shape == Shape.CIRCLE && other.shape == Shape.CIRCLE) {
            updateCircle();
            other.updateCircle();
            return Intersector.overlaps(this.circle, other.circle);
        } else if (this.shape == Shape.RECTANGLE && other.shape == Shape.RECTANGLE) {
            return owner.getBounds().overlaps(other.getOwner().getBounds());
        } else if (this.shape == Shape.CIRCLE && other.shape == Shape.RECTANGLE) {
            updateCircle();
            return Intersector.overlaps(this.circle, other.getOwner().getBounds());
        } else {
            other.updateCircle();
            return Intersector.overlaps(other.circle, this.owner.getBounds());
        }
    }

    private void updateCircle() {
        if (circle != null) {
            circle.setPosition(owner.getX(), owner.getY());
        }
    }
}
