package hust.adventure.screens;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.config.LevelID;
import hust.adventure.screens.levels.LevelFactory;

public class LoadingScreen extends BaseScreen {

    public LoadingScreen(HustGame game) {
        super(game);
        game.getAssetManager().loadAllAssets();
    }

    @Override
    public void render(float delta) {
        drawProgressBar();

        if (game.getAssetManager().update()) {
            LevelConfig config = new LevelConfig(LevelID.MAP_1, "Final Outside.tmx", 400f, 400f, 1.0f,
                    LevelID.MAP_1.getBgmPath(), LevelID.MAP_1.getAmbientColor());
            game.setScreen(LevelFactory.createLevel(game, config));
        }
    }

    private void drawProgressBar() {
        // Implement progress bar rendering using game.getAssetManager().getProgress()
        // Mock the functionality for now
    }
}
