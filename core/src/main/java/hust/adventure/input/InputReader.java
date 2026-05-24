package hust.adventure.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * System for handling player input and mapping it to game actions.
 */
public class InputReader implements PlayerController {
    private boolean up, down, left, right, running;
    private boolean spaceJP, qJP, eJP, fJP, iJP, enterJP, debugJP, hitboxJP;
    private int numJP = -1;

    public void update() {
        spaceJP = false;
        qJP = false;
        eJP = false;
        fJP = false;
        iJP = false;
        enterJP = false;
        debugJP = false;
        hitboxJP = false;
        numJP = -1;
    }

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
    public boolean isSpaceJustPressed() {
        return spaceJP;
    }

    @Override
    public boolean isQJustPressed() {
        return qJP;
    }

    @Override
    public boolean isEJustPressed() {
        return eJP;
    }

    @Override
    public boolean isFJustPressed() {
        return fJP;
    }

    @Override
    public boolean isInventoryJustPressed() {
        return iJP;
    }

    @Override
    public boolean isEnterJustPressed() {
        return enterJP;
    }

    @Override
    public boolean isDebugJustPressed() {
        return debugJP;
    }

    @Override
    public boolean isHitboxJustPressed() {
        return hitboxJP;
    }

    @Override
    public int getJustPressedNum() {
        return numJP;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP)
            up = true;
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN)
            down = true;
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT)
            left = true;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT)
            right = true;
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT)
            running = true;
        if (keycode == Input.Keys.SPACE)
            spaceJP = true;
        if (keycode == Input.Keys.Q)
            qJP = true;
        if (keycode == Input.Keys.E)
            eJP = true;
        if (keycode == Input.Keys.F)
            fJP = true;
        if (keycode == Input.Keys.I)
            iJP = true;
        if (keycode == Input.Keys.ENTER)
            enterJP = true;
        if (keycode == Input.Keys.F2)
            debugJP = true;
        if (keycode == Input.Keys.F3)
            hitboxJP = true;
        if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_5)
            numJP = keycode - Input.Keys.NUM_1 + 1;
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W || keycode == Input.Keys.UP)
            up = false;
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN)
            down = false;
        if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT)
            left = false;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT)
            right = false;
        if (keycode == Input.Keys.SHIFT_LEFT || keycode == Input.Keys.SHIFT_RIGHT)
            running = false;
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
