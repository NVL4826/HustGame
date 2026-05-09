package hust.adventure.screens;

import hust.adventure.HustGame;
import hust.adventure.core.LevelConfig;
import hust.adventure.core.LevelID;

public class LoadingScreen extends BaseScreen {

    public LoadingScreen(HustGame game) {
        super(game);
        game.getAssetManager().loadAllAssets();
    }

    @Override
    public void render(float delta) {
        drawProgressBar();

        if (game.getAssetManager().update()) {
            LevelConfig config = new LevelConfig(LevelID.FINAL_OUTSIDE, "Final Outside.tmx", 1024f, 1024f);
            game.setScreen(hust.adventure.screens.levels.LevelFactory.createLevel(game, config));
        }
    }

    private void drawProgressBar() {
        // Implement progress bar rendering using game.getAssetManager().getProgress()
        // Mock the functionality for now
    }
}
