package hust.adventure.screens.levels;

import hust.adventure.wave.WaveConfig;
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

        final WaveConfig waveConfig = context.getGame().getAssetManager().loadWaveConfig("configs/waves.json");
        this.waveManager = new WaveManager(waveConfig, context.getEntityFactory());
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
    public void dispose(final LevelContext context) {
        // Reset the HUD's time provider when leaving Map 1
        if (context != null && context.getUIManager() != null && context.getUIManager().getHud() != null) {
            context.getUIManager().getHud().setTimeProvider(null);
        }
    }
}
