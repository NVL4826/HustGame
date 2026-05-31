package hust.adventure.entities.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.entities.base.MapObject;

/**
 * A non-playable character that stays in one place.
 */
public class StaticNPC extends MapObject {
    private final String name;
    private final Color color;

    public StaticNPC(final float x, final float y, final String name, final Color color) {
        super(x, y, 32, 32);
        this.name = name;
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

    public String getName() {
        return name;
    }
}
