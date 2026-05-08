package vn.hust.hustgame.input;

public interface IPlayerController {
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
}
