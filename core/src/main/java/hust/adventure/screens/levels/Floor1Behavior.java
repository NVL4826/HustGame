package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import hust.adventure.entities.environment.StaticNPC;
import hust.adventure.items.base.ItemManager;

/**
 * Behavior class for Floor 1 (Tang 1) of the game.
 */
public class Floor1Behavior implements LevelBehavior {
    private String activeHint = "";
    private float hintTimer = 0f;

    @Override
    public void init(final LevelContext context) {
        final ItemManager im = ItemManager.instance;

        // Create Coffee Items
        context.getEntityFactory().createItemDrop(300f, 150f, im.getItem("coffee_den"), Color.BROWN);
        context.getEntityFactory().createItemDrop(350f, 180f, im.getItem("coffee_sua"), Color.YELLOW);

        // Create NPCs
        context.getEntityManager().addEntity(new StaticNPC(200f, 300f, "Guard", Color.BLUE));
        context.getEntityManager().addEntity(new StaticNPC(600f, 300f, "Staff", Color.CYAN));
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        if (hintTimer > 0) {
            hintTimer -= delta;
        } else {
            activeHint = "";
        }
    }

    @Override
    public void draw(final LevelContext context) {
        // Floor 1 specific rendering if any
    }

    @Override
    public void dispose(final LevelContext context) {
        // Floor 1 specific cleanup if any
    }

    public String getActiveHint() {
        return activeHint;
    }

    public void setActiveHint(final String hint, final float duration) {
        this.activeHint = hint;
        this.hintTimer = duration;
    }
}
