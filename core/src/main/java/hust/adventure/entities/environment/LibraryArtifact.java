package hust.adventure.entities.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.collision.Collider;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;

/**
 * Entity representing the artifact dropped by the Library Boss.
 */
public class LibraryArtifact extends BaseEntity {
    public LibraryArtifact(float x, float y) {
        super(x, y, 30, 30);

        Collider c = new Collider(this, CollisionLayer.ITEM, Collider.Shape.RECTANGLE);
        c.setListener(other -> {
            if (other instanceof Player) {
                ProgressContext.instance.setHasNao(true);
                ProgressContext.instance.setLibraryCleared(true);
                destroy();
            }
        });
        setCollider(c);
    }

    @Override
    public void update(float delta) {
        // Static item
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Sprite drawing not implemented
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(Color.GOLD);
        sr.rect(getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight());
    }

    @Override
    public void dispose() {
        // No resources
    }
}
