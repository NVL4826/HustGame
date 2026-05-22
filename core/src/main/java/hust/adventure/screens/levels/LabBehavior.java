package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.enemies.StackOverflowEnemy;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.GameEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.events.EventType;
import hust.adventure.items.ItemManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Behavior class for the Lab level, managing custom wave spawns, lighting changes, and USB collection.
 */
public class LabBehavior implements LevelBehavior {
    private static final int MAX_WAVE = 5;
    private static final float SPAWN_USB_X = 385f;
    private static final float SPAWN_USB_Y = 285f;
    private static final float USB_SIZE = 30f;
    private static final float BOSS_SPAWN_X = 400f;
    private static final float BOSS_SPAWN_Y = 100f;
    private static final String BOSS_MAP = "boss_room.tmx";
    
    private static final String ENEMY_NULL_POINTER = "null_pointer";
    private static final String ENEMY_SYNTAX_ERROR = "syntax_error";
    private static final String ENEMY_INFINITE_LOOP = "infinite_loop";
    private static final String ENEMY_STACK_OVERFLOW = "stack_overflow";

    private static final int LIGHTS_OUT_WAVE = 4;
    private static final float LIGHTS_OUT_AMBIENT = 0.15f;
    private static final float WAVE_TRANSITION_DELAY = 3f;

    private int currentWave = 1;
    private boolean waveActive = false;
    private float waveTimer = 2f;
    private boolean labCleared = false;
    private Rectangle usbRect;
    private final List<BaseEnemy> labEnemies = new ArrayList<>();
    private OrthographicCamera uiCam;

    @Override
    public void init(final LevelContext context) {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
        startLabWave(context, currentWave);
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        final Rectangle pBounds = context.getPlayer().getBounds();
        if (labCleared) {
            if (usbRect != null && pBounds.overlaps(usbRect)) {
                ProgressContext.instance.setHasUsb(true);
                ProgressContext.instance.setLabCleared(true);
                
                final MapTransitionData data = new MapTransitionData(BOSS_MAP, BOSS_SPAWN_X, BOSS_SPAWN_Y);
                final GameEvent<MapTransitionData> event = new GameEvent<>(EventType.MAP_TRANSITION, data);
                EventDispatcher.getInstance().dispatch(event);
                
                usbRect = null;
            }
        } else if (waveActive) {
            updateLabWaveLogic(context, delta, pBounds);
        } else {
            waveTimer -= delta;
            if (waveTimer <= 0) {
                if (currentWave < MAX_WAVE) {
                    currentWave++;
                    startLabWave(context, currentWave);
                } else {
                    labCleared = true;
                    usbRect = new Rectangle(SPAWN_USB_X, SPAWN_USB_Y, USB_SIZE, USB_SIZE);
                }
            }
        }
    }

    @Override
    public void draw(final LevelContext context) {
        if (usbRect != null) {
            final ShapeRenderer sr = context.getShapeRenderer();
            sr.setProjectionMatrix(context.getCamera().combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(Color.CYAN);
            sr.rect(usbRect.x, usbRect.y, usbRect.width, usbRect.height);
            sr.end();
        }

        final SpriteBatch batch = context.getBatch();
        final BitmapFont font = context.getFont();
        batch.setProjectionMatrix(uiCam.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Wave: " + currentWave + "/" + MAX_WAVE, 350, 580);
        batch.end();
    }

    private void startLabWave(final LevelContext context, int wave) {
        waveActive = true;
        labEnemies.clear();
        ProgressContext.instance.setLightsOut(wave == LIGHTS_OUT_WAVE);

        if (wave == LIGHTS_OUT_WAVE) {
            context.getLightingManager().setAmbientLight(new Color(LIGHTS_OUT_AMBIENT, LIGHTS_OUT_AMBIENT, LIGHTS_OUT_AMBIENT, 1f));
        } else {
            context.getLightingManager().setAmbientLight(context.getConfig().getAmbientColor());
        }

        switch (wave) {
            case 1:
                for (int i = 0; i < 5; i++) {
                    spawnLabEnemy(context, ENEMY_NULL_POINTER, MathUtils.random(100, 700), MathUtils.random(300, 500));
                }
                break;
            case 2:
                for (int i = 0; i < 3; i++) {
                    spawnLabEnemy(context, ENEMY_NULL_POINTER, MathUtils.random(100, 700), MathUtils.random(300, 500));
                }
                for (int i = 0; i < 2; i++) {
                    spawnLabEnemy(context, ENEMY_SYNTAX_ERROR, MathUtils.random(100, 700), MathUtils.random(300, 500));
                }
                break;
            case 3:
                spawnLabEnemy(context, ENEMY_INFINITE_LOOP, 400, 400);
                for (int i = 0; i < 4; i++) {
                    spawnLabEnemy(context, ENEMY_NULL_POINTER, MathUtils.random(100, 700), MathUtils.random(300, 500));
                }
                break;
            case 4:
                for (int i = 0; i < 2; i++) {
                    spawnLabEnemy(context, ENEMY_INFINITE_LOOP, MathUtils.random(100, 700), MathUtils.random(300, 500));
                }
                for (int i = 0; i < 2; i++) {
                    spawnLabEnemy(context, ENEMY_SYNTAX_ERROR, MathUtils.random(100, 700), MathUtils.random(300, 500));
                }
                break;
            case 5:
                spawnLabEnemy(context, ENEMY_STACK_OVERFLOW, 400, 400);
                spawnLabEnemy(context, ENEMY_NULL_POINTER, 200, 400);
                spawnLabEnemy(context, ENEMY_SYNTAX_ERROR, 600, 400);
                break;
        }
    }

    private void spawnLabEnemy(final LevelContext context, final String type, final float x, final float y) {
        final BaseEnemy enemy = (BaseEnemy) context.getEntityFactory().createEnemy(type, x, y);
        labEnemies.add(enemy);
    }

    private void updateLabWaveLogic(final LevelContext context, float delta, final Rectangle pBounds) {
        for (int i = labEnemies.size() - 1; i >= 0; i--) {
            final BaseEnemy e = labEnemies.get(i);
            if (e.isDead() || e.isDestroyed()) {
                handleEnemyDeath(context, e);
                labEnemies.remove(i);
            }
        }
        if (context.getInputReader().isSpaceJustPressed()) {
            context.getEntityFactory().createProjectile(context.getPlayer().getX(), context.getPlayer().getY(), 0, 400, 10, Color.YELLOW, true);
        }
        if (labEnemies.isEmpty()) {
            waveActive = false;
            waveTimer = WAVE_TRANSITION_DELAY;
        }
    }

    private void handleEnemyDeath(final LevelContext context, final BaseEnemy e) {
        if (MathUtils.random() < 0.3f) {
            final String[] items = { "coffee_den", "energy_drink", "kho_ga" };
            final Color[] colors = { Color.YELLOW, Color.GREEN, Color.BROWN };
            final int idx = MathUtils.random(0, 2);
            context.getEntityFactory().createItemDrop(e.getX(), e.getY(),
                    ItemManager.instance.getItem(items[idx]), colors[idx]);
        }
        if (e instanceof StackOverflowEnemy && !((StackOverflowEnemy) e).isSplit()) {
            spawnLabEnemy(context, ENEMY_STACK_OVERFLOW, e.getX() - 30, e.getY());
            spawnLabEnemy(context, ENEMY_STACK_OVERFLOW, e.getX() + 30, e.getY());
        }
        e.destroy();
    }

    @Override
    public void dispose(final LevelContext context) {
        labEnemies.clear();
        ProgressContext.instance.setLightsOut(false);
    }

    public boolean isLightsOut() {
        return ProgressContext.instance.isLightsOut();
    }
}
