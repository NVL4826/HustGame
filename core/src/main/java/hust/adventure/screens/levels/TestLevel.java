package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.items.ItemManager;

/**
 * A special level for testing all game entities and items.
 * Spawns all monsters and items in a grid layout.
 */
public class TestLevel extends BaseLevelScreen {

    public TestLevel(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        // Spawn Enemies in a row
        float enemyY = 600f;
        entityFactory.createEnemy("syntax_error", 100f, enemyY);
        entityFactory.createEnemy("null_pointer", 250f, enemyY);
        entityFactory.createEnemy("infinite_loop", 400f, enemyY);
        entityFactory.createEnemy("stack_overflow", 550f, enemyY);

        // Spawn Bosses
        float bossY = 400f;
        entityFactory.createLibraryBoss(200f, bossY);
        // Final Boss requires a texture, we can use a placeholder or load one from assets
        entityFactory.createFinalBoss(500f, bossY, 
                game.getAssetManager().getTexture("Boss THT.png"));

        // Spawn Items in a row
        float itemY = 200f;
        String[] itemIds = {
            "coffee_den", "coffee_sua", "coffee_da", "coffee_chon", 
            "energy_drink", "kho_ga", "note"
        };
        
        for (int i = 0; i < itemIds.length; i++) {
            hust.adventure.items.Item item = ItemManager.instance.getItem(itemIds[i]);
            if (item != null) {
                entityFactory.createItemDrop(100f + i * 100f, itemY, item, Color.WHITE);
            }
        }
    }

    @Override
    protected void updateLevel(float delta) {
        // No special level logic for test level
    }

    @Override
    protected void drawLevel() {
        // No special rendering for test level
    }
}
