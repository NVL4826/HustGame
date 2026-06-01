package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;

import hust.adventure.items.base.Item;
import hust.adventure.items.base.ItemManager;

/**
 * Behavior class for the Test level, spawning all entities and items for debugging.
 */
public class TestBehavior implements LevelBehavior {
    @Override
    public void init(final LevelContext context) {
        if (context == null) {
            throw new IllegalArgumentException("LevelContext cannot be null");
        }

        // Spawn Enemies in a row
        final float enemyY = 600f;
        context.getEntityFactory().createEnemy("syntax_error", 100f, enemyY);
        context.getEntityFactory().createEnemy("null_pointer", 250f, enemyY);
        context.getEntityFactory().createEnemy("infinite_loop", 400f, enemyY);
        context.getEntityFactory().createEnemy("stack_overflow", 550f, enemyY);

        // Spawn Bosses
        final float bossY = 400f;
        context.getEntityFactory().createEnemy("library_boss", 200f, bossY);
        context.getEntityFactory().createEnemy("final_boss", 500f, bossY);

        // Spawn Items in a row
        final float itemY = 200f;
        int index = 0;
        for (final Item item : ItemManager.instance.getAllItems()) {
            if (item != null && item.getSpritePath() != null && !item.getSpritePath().isEmpty()) {
                context.getEntityFactory().createItemDrop(100f + index * 100f, itemY, item, Color.WHITE);
                index++;
            }
        }
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        // No special logic
    }

    @Override
    public void draw(final LevelContext context) {
        // No special rendering
    }

    @Override
    public void dispose(final LevelContext context) {
        // No special cleanup
    }
}
