package hust.adventure.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hust.adventure.entities.base.MapObject;

/**
 * Generic static entity representing decorative map objects and static NPCs.
 * Handles both visual sprite objects and invisible logical boundaries.
 */
public class StaticObject extends MapObject {
    private final TextureRegion textureRegion;

    /**
     * Constructs a StaticObject with a visual sprite.
     *
     * @param x             bottom-left x coordinate
     * @param y             bottom-left y coordinate
     * @param width         width of the object
     * @param height        height of the object
     * @param textureRegion texture region of the tile sprite, or null if invisible
     */
    public StaticObject(final float x, final float y, final float width, final float height, final TextureRegion textureRegion) {
        // MapObject treats position as center, so we offset bottom-left coordinates.
        super(x + width / 2f, y + height / 2f, width, height);
        this.textureRegion = textureRegion;
    }

    /**
     * Constructs a StaticObject with no direct visual sprite (e.g. static NPC).
     *
     * @param x             center x coordinate
     * @param y             center y coordinate
     * @param width         width of the object
     * @param height        height of the object
     */
    public StaticObject(final float x, final float y, final float width, final float height) {
        super(x, y, width, height);
        this.textureRegion = null;
    }

    @Override
    public void update(float delta) {
        // Static objects do not update
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (textureRegion != null) {
            batch.draw(textureRegion, x - width / 2f, y - height / 2f, width, height);
        }
    }
}
