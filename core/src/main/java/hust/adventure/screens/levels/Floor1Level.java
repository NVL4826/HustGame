package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.entities.environment.StaticNPC;
import hust.adventure.items.ItemManager;

/**
 * Level 1 (Tang 1) with item collection mechanics.
 */
public class Floor1Level extends BaseLevelScreen {
    private String activeHint = "";
    private float hintTimer = 0f;

    public Floor1Level(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        ItemManager im = ItemManager.instance;

        // Create Coffee Items
        entityFactory.createItemDrop(300f, 150f, im.getItem("coffee_den"), Color.BROWN);
        entityFactory.createItemDrop(350f, 180f, im.getItem("coffee_sua"), Color.YELLOW);

        // Create Note Items
        entityFactory.createItemDrop(500f, 150f, im.getItem("note"), Color.WHITE);

        // Create NPCs
        entityManager.addEntity(new StaticNPC(200f, 300f, "Guard", Color.BLUE));
        entityManager.addEntity(new StaticNPC(600f, 300f, "Staff", Color.CYAN));
    }

    @Override
    protected void updateLevel(float delta) {
        if (hintTimer > 0)
            hintTimer -= delta;
        else
            activeHint = "";
    }

    @Override
    protected void drawLevel() {
        // Tang1 drawing logic (if any)
    }
}
