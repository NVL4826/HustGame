package hust.adventure.core;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.items.ItemManager;
import com.badlogic.gdx.utils.Disposable;

public class LootDropService implements EventListener, Disposable {
    private static final float DEFAULT_EXP_GEM_VALUE = 10f;

    private final EntityFactory entityFactory;
    private final GameAssetManager assetManager;

    public LootDropService(EntityFactory entityFactory, GameAssetManager assetManager) {
        this.entityFactory = entityFactory;
        this.assetManager = assetManager;
        EventDispatcher.getInstance().addListener(EventType.ENTITY_DIED, this);
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.ENTITY_DIED) {
            GameEntity deadEntity = (GameEntity) event.getData();
            
            if (deadEntity instanceof BaseEnemy) {
                // Determine drop
                if (((BaseEnemy) deadEntity).isBoss()) {
                    Texture tex = assetManager.getTexture(GameAssetManager.CHEST_TEXTURE_PATH);
                    entityFactory.createTreasureChest(deadEntity.getX(), deadEntity.getY(), tex);
                } else {
                    entityFactory.createExpGem(deadEntity.getX(), deadEntity.getY(), DEFAULT_EXP_GEM_VALUE);
                    
                    // 30% chance to drop a random consumable (previously in LabBehavior)
                    if (MathUtils.random() < 0.3f) {
                        final String[] items = { "coffee_den", "energy_drink", "kho_ga" };
                        final Color[] colors = { Color.YELLOW, Color.GREEN, Color.BROWN };
                        final int idx = MathUtils.random(0, 2);
                        entityFactory.createItemDrop(deadEntity.getX(), deadEntity.getY(),
                                ItemManager.instance.getItem(items[idx]), colors[idx]);
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.ENTITY_DIED, this);
    }
}
