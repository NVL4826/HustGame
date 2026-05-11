package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.enemies.StackOverflowEnemy;
import java.util.ArrayList;
import java.util.List;
import hust.adventure.items.ItemManager;

/**
 * Lab level with enemy wave mechanics.
 */
public class LabLevel extends BaseLevelScreen {
    private int currentWave = 1;
    private boolean waveActive = false;
    private float waveTimer = 2f;
    private boolean labCleared = false;
    private Rectangle usbRect;
    private boolean isLightsOut = false;
    private float slowMotionTimer = 0f;
    private float stunTimer = 0f;
    private float showEnemiesTimer = 0f;
    private final List<BaseEnemy> labEnemies = new ArrayList<>();

    public LabLevel(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        startLabWave(currentWave);
    }

    @Override
    protected void updateLevel(float delta) {
        float dt = delta;
        if (slowMotionTimer > 0) {
            dt *= 0.3f;
            slowMotionTimer -= delta;
        }
        if (stunTimer > 0)
            stunTimer -= delta;
        if (showEnemiesTimer > 0)
            showEnemiesTimer -= delta;

        final Rectangle pBounds = player.getBounds();
        if (labCleared) {
            if (usbRect != null && pBounds.overlaps(usbRect)) {
                ProgressContext.instance.hasUsb = true;
                ProgressContext.instance.labCleared = true;
                usbRect = null;
            }
        } else if (waveActive) {
            updateLabWaveLogic(dt, pBounds);
        } else {
            waveTimer -= delta;
            if (waveTimer <= 0) {
                if (currentWave < 5) {
                    currentWave++;
                    startLabWave(currentWave);
                } else {
                    labCleared = true;
                    usbRect = new Rectangle(385, 285, 30, 30);
                }
            }
        }
    }

    @Override
    protected void drawLevel() {
        if (usbRect != null) {
            // Visual for USB
        }
    }

    private void startLabWave(int wave) {
        waveActive = true;
        labEnemies.clear();
        isLightsOut = (wave == 4);

        switch (wave) {
        case 1:
            for (int i = 0; i < 5; i++)
                spawnLabEnemy("null_pointer", MathUtils.random(100, 700), MathUtils.random(300, 500));
            break;
        case 2:
            for (int i = 0; i < 3; i++)
                spawnLabEnemy("null_pointer", MathUtils.random(100, 700), MathUtils.random(300, 500));
            for (int i = 0; i < 2; i++)
                spawnLabEnemy("syntax_error", MathUtils.random(100, 700), MathUtils.random(300, 500));
            break;
        case 3:
            spawnLabEnemy("infinite_loop", 400, 400);
            for (int i = 0; i < 4; i++)
                spawnLabEnemy("null_pointer", MathUtils.random(100, 700), MathUtils.random(300, 500));
            break;
        case 4:
            for (int i = 0; i < 2; i++)
                spawnLabEnemy("infinite_loop", MathUtils.random(100, 700), MathUtils.random(300, 500));
            for (int i = 0; i < 2; i++)
                spawnLabEnemy("syntax_error", MathUtils.random(100, 700), MathUtils.random(300, 500));
            break;
        case 5:
            spawnLabEnemy("stack_overflow", 400, 400);
            spawnLabEnemy("null_pointer", 200, 400);
            spawnLabEnemy("syntax_error", 600, 400);
            break;
        }
    }

    private void spawnLabEnemy(final String type, final float x, final float y) {
        final BaseEnemy enemy = (BaseEnemy) entityFactory.createEnemy(type, x, y);
        labEnemies.add(enemy);
    }

    private void updateLabWaveLogic(float dt, final Rectangle pBounds) {
        if (stunTimer <= 0) {
            for (int i = labEnemies.size() - 1; i >= 0; i--) {
                final BaseEnemy e = labEnemies.get(i);
                if (e.isDead() || e.isDestroyed()) {
                    handleEnemyDeath(e);
                    labEnemies.remove(i);
                }
            }
        }
        if (inputReader.isSpaceJustPressed()) {
            entityFactory.createProjectile(player.getX(), player.getY(), 0, 400, 10, Color.YELLOW, true);
        }
        if (labEnemies.isEmpty()) {
            waveActive = false;
            waveTimer = 3f;
        }
    }

    private void handleEnemyDeath(final BaseEnemy e) {
        if (MathUtils.random() < 0.3f) {
            final String[] items = { "coffee_den", "energy_drink", "kho_ga" };
            final Color[] colors = { Color.YELLOW, Color.GREEN, Color.BROWN };
            final int idx = MathUtils.random(0, 2);
            entityFactory.createItemDrop(e.getX(), e.getY(), 
                ItemManager.instance.getItem(items[idx]), colors[idx]);
        }
        if (e instanceof StackOverflowEnemy && !((StackOverflowEnemy) e).isSplit()) {
            spawnLabEnemy("stack_overflow", e.getX() - 30, e.getY());
            spawnLabEnemy("stack_overflow", e.getX() + 30, e.getY());
        }
        e.destroy();
    }
}
