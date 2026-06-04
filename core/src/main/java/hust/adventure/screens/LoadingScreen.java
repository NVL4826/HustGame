package hust.adventure.screens;

import hust.adventure.HustGame;
import hust.adventure.core.data.LevelConfig;

public class LoadingScreen extends BaseScreen {

    public LoadingScreen(HustGame game) {
        super(game);
        game.getAssetManager().loadAllAssets();
    }

    @Override
    public void render(float delta) {
        drawProgressBar();

        if (game.getAssetManager().update()) {
            LevelConfig template = game.getLevelDataManager().getLevelConfig("FINAL_OUTSIDE");
            LevelConfig config = new LevelConfig("FINAL_OUTSIDE", template.getName(), template.getMapPath(),
                    template.getSpawnX(), template.getSpawnY(), template.getZoom(), template.getBgmPath(),
                    template.getAmbientColor(), template.isInfinite());
            game.setScreen(game.getLevelFactory().createLevel(game, config));
        }
    }

    private void drawProgressBar() {
        // Implement progress bar rendering using game.getAssetManager().getProgress()
        // Mock the functionality for now
    }
}
