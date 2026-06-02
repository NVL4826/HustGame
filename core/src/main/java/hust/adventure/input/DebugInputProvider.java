package hust.adventure.input;

/**
 * Interface for checking debug inputs.
 */
public interface DebugInputProvider {
    boolean isDebugJustPressed();

    default boolean isHitboxJustPressed() {
        return false;
    }
}
