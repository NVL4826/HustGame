package vn.hust.hustgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class GameInputHandler implements IPlayerController, InputProcessor {
    private boolean up, down, left, right, running;

    @Override
    public boolean isUp() {
        return up;
    }

    @Override
    public boolean isDown() {
        return down;
    }

    @Override
    public boolean isLeft() {
        return left;
    }

    @Override
    public boolean isRight() {
        return right;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) up = true;
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) down = true;
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) left = true;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) right = true;
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) running = true;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP) up = false;
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) down = false;
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) left = false;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) right = false;
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT) running = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
