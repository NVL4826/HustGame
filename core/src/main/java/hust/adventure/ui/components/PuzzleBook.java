package hust.adventure.ui.components;

import com.badlogic.gdx.math.Rectangle;

/**
 * Model class for a puzzle book.
 */
public class PuzzleBook {
    private final String subjectName;
    private final Rectangle rect;
    private final int targetSemester;
    private final float initialX;
    private final float initialY;

    private boolean placedCorrectly = false;
    private boolean isDragging = false;

    public PuzzleBook(final String name, final float x, final float y, final float width, final float height,
            final int targetSemester) {
        this.subjectName = name;
        this.rect = new Rectangle(x, y, width, height);
        this.targetSemester = targetSemester;
        this.initialX = x;
        this.initialY = y;
    }

    public boolean isClicked(final float mouseX, final float mouseY) {
        return !placedCorrectly && rect.contains(mouseX, mouseY);
    }

    public void snapToSlot(final PuzzleSlot slot) {
        this.rect.setPosition(slot.getX() + 10, slot.getY() + 5);
        this.placedCorrectly = true;
    }

    public void resetPosition() {
        this.rect.setPosition(initialX, initialY);
        this.isDragging = false;
    }

    // Getters and Setters
    public String getSubjectName() {
        return subjectName;
    }

    public Rectangle getRect() {
        return rect;
    }

    public int getTargetSemester() {
        return targetSemester;
    }

    public boolean isPlacedCorrectly() {
        return placedCorrectly;
    }

    public void setPlacedCorrectly(boolean placedCorrectly) {
        this.placedCorrectly = placedCorrectly;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public float getX() {
        return rect.x;
    }

    public float getY() {
        return rect.y;
    }

    public void setPosition(final float x, final float y) {
        rect.setPosition(x, y);
    }
}
