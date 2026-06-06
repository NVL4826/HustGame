package hust.adventure.screens.levels;

import com.badlogic.gdx.utils.Array;
import hust.adventure.wave.WaveEntry;
import hust.adventure.wave.WaveManager;

/**
 * Behavior class for the Outside area.
 * Loads and ticks enemy waves configured for the map.
 */
public class OutsideBehavior implements LevelBehavior {
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
        // Cho phép chuyển map (vào portal gototang1) luôn để test/chơi tiếp
        return true;
    }

    @Override
    public void dispose(final LevelContext context) {
        if (context != null && context.getUIManager() != null && context.getUIManager().getHud() != null) {
            context.getUIManager().getHud().setTimeProvider(null);
        }
    }
}
