package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.GameEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.events.EventType;
import hust.adventure.events.ItemPickedUpEvent;

/**
 * Behavior class for the Lab level, managing custom wave spawns, lighting changes, and USB collection.
 */
public class LabBehavior implements LevelBehavior, EventListener {
    private static final int MAX_WAVE = 5;
    private static final float SPAWN_USB_X = 672f; // giữa map lab (1344 / 2)
    private static final float SPAWN_USB_Y = 384f; // giữa map lab (768 / 2)
    private static final float BOSS_SPAWN_X = 400f;
    private static final float BOSS_SPAWN_Y = 100f;
    private static final String BOSS_MAP = "boss_room.tmx";

    private static final int LIGHTS_OUT_WAVE = 4;
    private static final float LIGHTS_OUT_AMBIENT = 0.15f;
    private static final float WAVE_TRANSITION_DELAY = 3f;

    private int currentWave = 1;
    private boolean waveActive = false;
    private float waveTimer = 2f;
    private boolean labCleared = false;
    private boolean usbSpawned = false;
    private OrthographicCamera uiCam;

    // Blink effect cho wave 3
    private static final float BLINK_INTERVAL = 1.0f; // 1 giây hiện, 1 giây ẩn
    private float blinkTimer = 0f;
    private boolean blinkPhase = true; // true = hiện, false = ẩn
    private LevelContext context;

    @Override
    public void init(final LevelContext context) {
        this.context = context;
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
        startLabWave(context, currentWave);
        EventDispatcher.getInstance().addListener(EventType.ITEM_PICKED_UP, this);
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        if (labCleared) {
            return;
        }

        // ── Blink logic cho wave 3 ────────────────────────────────────────────
        if (currentWave == 3 && waveActive) {
            blinkTimer += delta;
            if (blinkTimer >= BLINK_INTERVAL) {
                blinkTimer = 0f;
                blinkPhase = !blinkPhase;
                context.getProgressContext().setEnemyBlinkVisible(blinkPhase);
            }
        }

        if (waveActive) {
            if (!context.getEntityManager().hasActiveEnemies()) {
                waveActive = false;
                waveTimer = WAVE_TRANSITION_DELAY;
            }
        } else {
            waveTimer -= delta;
            if (waveTimer <= 0) {
                if (currentWave < MAX_WAVE) {
                    currentWave++;
                    startLabWave(context, currentWave);
                } else {
                    if (!usbSpawned) {
                        labCleared = true;
                        context.getEntityFactory().createItemDrop(SPAWN_USB_X, SPAWN_USB_Y,
                                context.getProgressContext().getItemManager().getItem("usb"), Color.CYAN);
                        usbSpawned = true;
                    }
                }
            }
        }
    }

    @Override
    public void draw(final LevelContext context) {
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
        // Reset blink khi vào wave mới (chỉ wave 3 mới blink)
        blinkTimer = 0f;
        blinkPhase = true;
        context.getProgressContext().setEnemyBlinkVisible(true);
        context.getProgressContext().setLightsOut(wave == LIGHTS_OUT_WAVE);

        if (wave == LIGHTS_OUT_WAVE) {
            context.getLightingManager()
                    .setAmbientLight(new Color(LIGHTS_OUT_AMBIENT, LIGHTS_OUT_AMBIENT, LIGHTS_OUT_AMBIENT, 1f));
        } else {
            context.getLightingManager().setAmbientLight(context.getConfig().getAmbientColor());
        }

        switch (wave) {
        case 1:
            for (int i = 0; i < 5; i++) {
                spawnLabEnemy(context, "null_pointer", MathUtils.random(100, 700), MathUtils.random(300, 500));
            }
            break;
        case 2:
            for (int i = 0; i < 3; i++) {
                spawnLabEnemy(context, "null_pointer", MathUtils.random(100, 700), MathUtils.random(300, 500));
            }
            for (int i = 0; i < 2; i++) {
                spawnLabEnemy(context, "syntax_error", MathUtils.random(100, 700), MathUtils.random(300, 500));
            }
            break;
        case 3:
            spawnLabEnemy(context, "infinite_loop", 400, 400);
            for (int i = 0; i < 4; i++) {
                spawnLabEnemy(context, "null_pointer", MathUtils.random(100, 700), MathUtils.random(300, 500));
            }
            break;
        case 4:
            for (int i = 0; i < 2; i++) {
                spawnLabEnemy(context, "infinite_loop", MathUtils.random(100, 700), MathUtils.random(300, 500));
            }
            for (int i = 0; i < 2; i++) {
                spawnLabEnemy(context, "syntax_error", MathUtils.random(100, 700), MathUtils.random(300, 500));
            }
            break;
        case 5:
            spawnLabEnemy(context, "stack_overflow", 400, 400);
            spawnLabEnemy(context, "null_pointer", 200, 400);
            spawnLabEnemy(context, "syntax_error", 600, 400);
            break;
        }
    }

    private void spawnLabEnemy(final LevelContext context, final String type, final float x, final float y) {
        context.getEntityFactory().createEnemy(type, x, y);
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.ITEM_PICKED_UP) {
            final ItemPickedUpEvent data = (ItemPickedUpEvent) event.getData();
            if (data.getItem().getId().equals("usb")) {
                final MapTransitionData transData = new MapTransitionData(BOSS_MAP, BOSS_SPAWN_X, BOSS_SPAWN_Y);
                final GameEvent<MapTransitionData> transEvent = new GameEvent<>(EventType.MAP_TRANSITION, transData);
                EventDispatcher.getInstance().dispatch(transEvent);
            }
        }
    }

    @Override
    public boolean canTransition(final LevelContext context) {
        return labCleared;
    }

    @Override
    public void dispose(final LevelContext context) {
        EventDispatcher.getInstance().removeListener(EventType.ITEM_PICKED_UP, this);
        context.getProgressContext().setLightsOut(false);
        context.getProgressContext().setEnemyBlinkVisible(true); // reset blink khi rời màn lab
    }

    public boolean isLightsOut() {
        return context != null ? context.getProgressContext().isLightsOut() : false;
    }
}
