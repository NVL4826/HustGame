package hust.adventure.entities.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface for objects that can be rendered.
 */
public interface Renderable {
    void draw(SpriteBatch batch);
    float getY(); // For Y-sorting
}
