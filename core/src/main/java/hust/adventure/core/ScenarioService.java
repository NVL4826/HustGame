package hust.adventure.core;

import com.badlogic.gdx.graphics.Texture;
import hust.adventure.entities.Player;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;

public class ScenarioService implements EventListener, com.badlogic.gdx.utils.Disposable {
    private static final String BOSS_TEXTURE_NAME = "Boss THT.png";
    private static final float BOSS_SPAWN_OFFSET = 300f;

    private final EntityFactory entityFactory;
    private final Player player;
    private final GameAssetManager assetManager;

    public ScenarioService(EntityFactory entityFactory, Player player, GameAssetManager assetManager) {
        this.entityFactory = entityFactory;
        this.player = player;
        this.assetManager = assetManager;
        EventDispatcher.getInstance().addListener(EventType.TIME_LIMIT_REACHED, this);
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.TIME_LIMIT_REACHED) {
            spawnFinalBoss();
        }
    }

    private void spawnFinalBoss() {
        Texture bossTex = assetManager.getTexture(BOSS_TEXTURE_NAME);

        // Spawn somewhat near the player
        float spawnX = player.getX() + BOSS_SPAWN_OFFSET;
        float spawnY = player.getY() + BOSS_SPAWN_OFFSET;

        entityFactory.createFinalBoss(spawnX, spawnY, bossTex);
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.TIME_LIMIT_REACHED, this);
    }
}
