package hust.adventure.entities.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface for background objects that can be rendered without Y-sorting.
 */
public interface BackgroundRenderable {
    void draw(SpriteBatch batch);
}
