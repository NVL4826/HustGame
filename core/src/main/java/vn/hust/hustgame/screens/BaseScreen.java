package vn.hust.hustgame.screens;

import com.badlogic.gdx.Screen;

import vn.hust.hustgame.HustGame;

public abstract class BaseScreen implements Screen {
    protected HustGame game;

    public BaseScreen(HustGame game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}
