package hust.adventure.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.entities.base.MapObject;

/**
 * A non-playable character that stays in one place.
 */
public class StaticNPC extends MapObject {
    private final Color color;

    public StaticNPC(final float x, final float y, final String name, final Color color) {
        super(x, y, 32, 32);
        setName(name);
        this.color = color;
    }

    @Override
    public void update(float delta) {
        // NPCs are stationary
    }

    @Override
    public void draw(SpriteBatch batch) {
        // No visual – NPC sprite is rendered via the tilemap layer
    }
}
