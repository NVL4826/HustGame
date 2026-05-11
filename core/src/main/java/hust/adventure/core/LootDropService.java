package hust.adventure.core;

import com.badlogic.gdx.graphics.Texture;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.enemies.FinalBoss;
import hust.adventure.entities.enemies.LibraryBoss;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;

public class LootDropService implements EventListener, com.badlogic.gdx.utils.Disposable {
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
                if (deadEntity instanceof LibraryBoss || deadEntity instanceof FinalBoss) {
                    Texture tex = assetManager.getTexture("character.png"); // placeholder texture for treasure chest
                    entityFactory.createTreasureChest(deadEntity.getX(), deadEntity.getY(), tex);
                } else {
                    entityFactory.createExpGem(deadEntity.getX(), deadEntity.getY(), 10f);
                }
            }
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.ENTITY_DIED, this);
    }
}
