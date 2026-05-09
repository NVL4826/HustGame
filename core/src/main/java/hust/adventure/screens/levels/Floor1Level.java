package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;

import hust.adventure.HustGame;
import hust.adventure.core.LevelConfig;
import hust.adventure.entities.environment.StaticNPC;

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
        // Create Coffee Items
        entityManager.addEntity(entityFactory.createItemDrop(300f, 150f, "coffee_den", Color.BROWN));
        entityManager.addEntity(entityFactory.createItemDrop(350f, 180f, "coffee_sua", Color.YELLOW));

        // Create Note Items
        entityManager.addEntity(entityFactory.createItemDrop(500f, 150f, "note", Color.WHITE));

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
