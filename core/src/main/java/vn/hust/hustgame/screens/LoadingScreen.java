package vn.hust.hustgame.screens;

import vn.hust.hustgame.HustGame;

public class LoadingScreen extends BaseScreen {

    public LoadingScreen(HustGame game) {
        super(game);
        game.getAssetManager().loadAllAssets();
    }

    @Override
    public void render(float delta) {
        drawProgressBar();

        if (game.getAssetManager().update()) {
            game.setScreen(new FinalOutsideScreen(game));
        }
    }

    private void drawProgressBar() {
        // Implement progress bar rendering using game.getAssetManager().getProgress()
        // Mock the functionality for now
    }
}
