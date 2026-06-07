package hust.adventure.screens.levels;

import com.badlogic.gdx.utils.Array;
import hust.adventure.wave.WaveEntry;
import hust.adventure.wave.WaveManager;

/**
 * Behavior class for Map 1, implementing the standard wave survival logic.
 */
public class Map1Behavior implements LevelBehavior {
    private WaveManager waveManager;

    @Override
    public void init(final LevelContext context) {
        if (context == null) {
            throw new IllegalArgumentException("LevelContext cannot be null");
        }

        final String levelId = context.getConfig().getLevelId();
        final Array<WaveEntry> waves = context.getGame().getWaveDataManager().getWaves(levelId);
        this.waveManager = new WaveManager(waves, context.getEntityFactory());
        context.getUIManager().getHud().setTimeProvider(this.waveManager);
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        if (waveManager != null) {
            waveManager.update(delta, context.getCamera());
        }
    }

    @Override
    public void draw(final LevelContext context) {
        // No custom drawing needed
    }

    @Override
    public boolean canTransition(final LevelContext context) {
        if (waveManager != null) {
            return waveManager.isFinished() && !context.getEntityManager().hasActiveEnemies();
        }
        return true;
    }

    @Override
    public void dispose(final LevelContext context) {
        // Reset the HUD's time provider when leaving Map 1
        if (context != null && context.getUIManager() != null && context.getUIManager().getHud() != null) {
            context.getUIManager().getHud().setTimeProvider(null);
        }
    }
}
