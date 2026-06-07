package hust.adventure.screens.levels;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import hust.adventure.HustGame;
import hust.adventure.core.data.LevelConfig;
import hust.adventure.core.data.WaveDataLoader;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.ui.HUD;
import hust.adventure.ui.UIProvider;
import hust.adventure.wave.WaveEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class Map1BehaviorTest {

    private LevelContext context;
    private HustGame game;
    private WaveDataLoader waveDataLoader;
    private UIProvider uiProvider;
    private HUD hud;
    private EntityManager entityManager;
    private EntityFactory entityFactory;
    private LevelConfig config;
    private Camera camera;

    @BeforeEach
    public void setUp() {
        Gdx.app = mock(Application.class);

        context = mock(LevelContext.class);
        game = mock(HustGame.class);
        waveDataLoader = mock(WaveDataLoader.class);
        uiProvider = mock(UIProvider.class);
        hud = mock(HUD.class);
        entityManager = mock(EntityManager.class);
        entityFactory = mock(EntityFactory.class);
        config = mock(LevelConfig.class);
        camera = new Camera() {
            @Override
            public void update() {}
            @Override
            public void update(boolean updateFrustum) {}
        };
        camera.viewportWidth = 800f;
        camera.viewportHeight = 600f;
        camera.position.set(400f, 300f, 0f);

        when(context.getGame()).thenReturn(game);
        when(game.getWaveDataManager()).thenReturn(waveDataLoader);
        when(context.getUIManager()).thenReturn(uiProvider);
        when(uiProvider.getHud()).thenReturn(hud);
        when(context.getEntityFactory()).thenReturn(entityFactory);
        when(context.getConfig()).thenReturn(config);
        when(context.getEntityManager()).thenReturn(entityManager);
        when(context.getCamera()).thenReturn(camera);
        when(config.getLevelId()).thenReturn("MAP_1");
    }

    private WaveEntry makeWave(float start, float end) throws Exception {
        WaveEntry w = new WaveEntry();
        setField(w, "timeStart", start);
        setField(w, "timeEnd", end);
        setField(w, "spawnInterval", 2f);
        setField(w, "spawnCount", 1);
        setField(w, "pattern", "RANDOM_EDGE");
        setField(w, "enemyType", "bug");
        return w;
    }

    private void setField(Object obj, String name, Object val) throws Exception {
        Field f = WaveEntry.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, val);
    }

    @Test
    public void testCanTransitionDefaultTrueWhenNoWaveManager() {
        Map1Behavior behavior = new Map1Behavior();
        assertTrue(behavior.canTransition(context));
    }

    @Test
    public void testCanTransitionUnderSurvivalConditions() throws Exception {
        Array<WaveEntry> waves = new Array<>();
        waves.add(makeWave(0f, 10f));
        when(waveDataLoader.getWaves("MAP_1")).thenReturn(waves);

        Map1Behavior behavior = new Map1Behavior();
        behavior.init(context);

        // 1. Wave is not finished, no active enemies. should not transition.
        when(entityManager.hasActiveEnemies()).thenReturn(false);
        assertFalse(behavior.canTransition(context));

        // 2. Advance time to 5s. Wave is still not finished, some active enemies. should not transition.
        behavior.update(context, 5f);
        when(entityManager.hasActiveEnemies()).thenReturn(true);
        assertFalse(behavior.canTransition(context));

        // 3. Advance time to 12s. Wave is finished, but has active enemies. should not transition.
        behavior.update(context, 7f);
        when(entityManager.hasActiveEnemies()).thenReturn(true);
        assertFalse(behavior.canTransition(context));

        // 4. Wave is finished, all enemies killed. should transition.
        when(entityManager.hasActiveEnemies()).thenReturn(false);
        assertTrue(behavior.canTransition(context));
    }
}
