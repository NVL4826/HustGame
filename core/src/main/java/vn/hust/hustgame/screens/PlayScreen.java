package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import vn.hust.hustgame.HustGame;

public class PlayScreen extends BaseScreen {
    private GameState state;

    public PlayScreen(HustGame game) {
        super(game);
        this.state = GameState.RUNNING;
    }

    @Override
    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        if (state == GameState.RUNNING) {
            // Update entities and map logic
        } else if (state == GameState.UI_PAUSED) {
            // Do not update logic, only visual/UI interactions
        }

        // Render layers, entities, and UI
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
