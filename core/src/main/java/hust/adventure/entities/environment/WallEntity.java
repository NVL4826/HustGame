package hust.adventure.entities.environment;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import hust.adventure.collision.Collider;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.base.BaseEntity;

/**
 * Static wall entity for collisions.
 */
public class WallEntity extends BaseEntity {
    public WallEntity(Rectangle rect) {
        // Wall positions in TMX are usually bottom-left, Entity constructor assumes center.
        // We'll adjust to match the TMX Rectangle perfectly.
        super(rect.x + rect.width / 2f, rect.y + rect.height / 2f, rect.width, rect.height);
        setCollider(new Collider(this, CollisionLayer.WALL, Collider.Shape.RECTANGLE));
    }

    @Override
    public void update(float delta) {
        // Walls don't move
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Walls are drawn by the MapRenderer
    }

    @Override
    public void dispose() {
        // No resources
    }
}
