package vn.hust.hustgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import vn.hust.hustgame.events.EventDispatcher;
import vn.hust.hustgame.screens.LoadingScreen;

public class HustGame extends Game {
    private SpriteBatch spriteBatch;
    private GameAssetManager assetManager;
    private EventDispatcher eventDispatcher;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        assetManager = new GameAssetManager();
        eventDispatcher = EventDispatcher.getInstance();

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (screen != null) {
            screen.dispose();
        }
        if (spriteBatch != null) {
            spriteBatch.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public GameAssetManager getAssetManager() {
        return assetManager;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
