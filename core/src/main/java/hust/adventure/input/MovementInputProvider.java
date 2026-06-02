package hust.adventure.input;

/**
 * Interface for polling movement-related inputs.
 */
public interface MovementInputProvider {
    boolean isUp();

    boolean isDown();

    boolean isLeft();

    boolean isRight();

    boolean isRunning();
}
