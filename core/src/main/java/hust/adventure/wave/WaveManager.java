package hust.adventure.wave;
 
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.Gdx;
import hust.adventure.core.TimeProvider;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.TimeLimitReachedEvent;
 
/**
 * Manages game time and enemy spawning based on WaveConfig.
 */
public class WaveManager implements TimeProvider {
    private final WaveConfig config;
    private final EntityFactory entityFactory;
    private final Array<ActiveWave> activeWaves;
    private final ObjectMap<String, SpawnStrategy> strategies;
 
    private float gameTime = 0f;
    private float maxTime = 1800f; // 30 minutes
    private boolean limitReached = false;
 
    public WaveManager(WaveConfig config, EntityFactory entityFactory) {
        if (config == null)
            throw new IllegalArgumentException("WaveConfig cannot be null");
        if (entityFactory == null)
            throw new IllegalArgumentException("EntityFactory cannot be null");
 
        this.config = config;
        this.entityFactory = entityFactory;
        this.activeWaves = new Array<>();
        this.strategies = new ObjectMap<>();
 
        // Register default strategies
        strategies.put("RANDOM_EDGE", new RandomEdgeSpawnStrategy());
        strategies.put("CIRCLE_AMBUSH", new CircleAmbushSpawnStrategy());
    }
 
    @Override
    public float getCurrentTime() {
        return gameTime;
    }
 
    @Override
    public float getMaxTime() {
        return maxTime;
    }
 
    public void update(float delta, Camera camera) {
        if (!limitReached) {
            gameTime += delta;
            if (gameTime >= maxTime) {
                limitReached = true;
                gameTime = maxTime;
 
                TimeLimitReachedEvent data = new TimeLimitReachedEvent();
                GameEvent<TimeLimitReachedEvent> event = new GameEvent<>(EventType.TIME_LIMIT_REACHED, data);
                EventDispatcher.getInstance().dispatch(event);
                EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/time_alarm.mp3"));
            }
        }
 
        if (limitReached) {
            return; // Stop normal spawning
        }
 
        // Check for new waves to activate
        for (WaveConfig.WaveEntry entry : config.waves) {
            if (gameTime >= entry.timeStart && gameTime <= entry.timeEnd) {
                if (!isActive(entry)) {
                    activeWaves.add(new ActiveWave(entry));
                }
            }
        }
 
        // Update active waves
        for (int i = activeWaves.size - 1; i >= 0; i--) {
            ActiveWave wave = activeWaves.get(i);
 
            // Deactivate if time passed
            if (gameTime > wave.config.timeEnd) {
                activeWaves.removeIndex(i);
                continue;
            }
 
            wave.update(delta, camera);
        }
    }
 
    private boolean isActive(WaveConfig.WaveEntry entry) {
        for (ActiveWave wave : activeWaves) {
            if (wave.config == entry)
                return true;
        }
        return false;
    }
 
    public float getGameTime() {
        return gameTime;
    }
 
    /**
     * Runtime wrapper for a WaveEntry to track state.
     */
    private class ActiveWave {
        final WaveConfig.WaveEntry config;
        float spawnCooldown = 0f;
 
        ActiveWave(WaveConfig.WaveEntry config) {
            this.config = config;
        }
 
        void update(float delta, Camera camera) {
            if (config.spawnInterval <= 0) {
                // One-shot spawn (if timeEnd == timeStart or interval is 0)
                if (spawnCooldown == 0) {
                    spawn(camera);
                    spawnCooldown = -1f; // Mark as spawned
                }
                return;
            }
 
            spawnCooldown -= delta;
            if (spawnCooldown <= 0) {
                spawn(camera);
                spawnCooldown = config.spawnInterval;
            }
        }
 
        void spawn(Camera camera) {
            SpawnStrategy strategy = strategies.get(config.pattern);
            if (strategy == null) {
                Gdx.app.error("WaveManager", "Unknown pattern: " + config.pattern);
                return;
            }
 
            Array<Vector2> positions = strategy.calculatePositions(camera, config.spawnCount);
            for (Vector2 pos : positions) {
                entityFactory.createEnemy(config.enemyType, pos.x, pos.y);
            }
            if (positions.size > 0) {
                EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/pickup_item.mp3"));
            }
        }
    }
}
