package hust.adventure.ui.components;

import com.badlogic.gdx.math.Rectangle;

/**
 * Model class for a puzzle slot.
 */
public class PuzzleSlot {
    private final int expectedSemester;
    private final Rectangle rect;

    public PuzzleSlot(final int semester, final float x, final float y, final float width, final float height) {
        this.expectedSemester = semester;
        this.rect = new Rectangle(x, y, width, height);
    }

    public int getExpectedSemester() {
        return expectedSemester;
    }

    public Rectangle getRect() {
        return rect;
    }

    public float getX() {
        return rect.x;
    }

    public float getY() {
        return rect.y;
    }

    public float getWidth() {
        return rect.width;
    }

    public float getHeight() {
        return rect.height;
    }

    public boolean overlaps(final Rectangle other) {
        return rect.overlaps(other);
    }
}
