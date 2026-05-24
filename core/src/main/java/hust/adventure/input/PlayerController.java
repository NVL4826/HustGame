package hust.adventure.input;

import com.badlogic.gdx.InputProcessor;

public interface PlayerController extends InputProcessor {
    boolean isUp();

    boolean isDown();

    boolean isLeft();

    boolean isRight();

    boolean isRunning();

    boolean isSpaceJustPressed();

    boolean isQJustPressed();

    boolean isEJustPressed();

    boolean isFJustPressed();

    boolean isInventoryJustPressed();

    boolean isEnterJustPressed();

    int getJustPressedNum();
    
    boolean isDebugJustPressed();

    default boolean isHitboxJustPressed() {
        return false;
    }

    default boolean isAttackJustPressed() {
        return isSpaceJustPressed();
    }

    default boolean isSkillQJustPressed() {
        return isQJustPressed();
    }

    default boolean isSkillEJustPressed() {
        return isEJustPressed();
    }

    default boolean isSkillFJustPressed() {
        return isFJustPressed();
    }
}
