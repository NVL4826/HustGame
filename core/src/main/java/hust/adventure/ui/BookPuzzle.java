package hust.adventure.ui;
 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.ui.components.PuzzleBook;
import hust.adventure.ui.components.PuzzleSlot;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
/**
 * UI Component for the Library Book Puzzle.
 * Handles input logic and rendering.
 */
public class BookPuzzle implements Disposable {
    private static final float VIEWPORT_WIDTH = 800f;
    private static final float VIEWPORT_HEIGHT = 600f;
    private static final float BOOK_WIDTH = 100f;
    private static final float BOOK_HEIGHT = 30f;
    private static final float SLOT_WIDTH = 120f;
    private static final float SLOT_HEIGHT = 40f;
 
    private static final Color COLOR_SLOT = Color.YELLOW;
    private static final Color COLOR_BOOK_CORRECT = Color.GREEN;
    private static final Color COLOR_BOOK_DRAGGING = Color.BLUE;
    private static final Color COLOR_BOOK_DEFAULT = Color.BROWN;
 
    private final List<PuzzleBook> books;
    private final List<PuzzleSlot> slots;
    private final BitmapFont font;
    private boolean isSolved = false;
 
    public BookPuzzle(final Map<String, Integer> bookConfigs) {
        if (bookConfigs == null || bookConfigs.isEmpty()) {
            throw new IllegalArgumentException("Book configurations cannot be null or empty");
        }
 
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.books = new ArrayList<>();
        this.slots = new ArrayList<>();
 
        initializePuzzle(bookConfigs);
    }
 
    private void initializePuzzle(final Map<String, Integer> bookConfigs) {
        int i = 0;
        for (final Map.Entry<String, Integer> entry : bookConfigs.entrySet()) {
            final float yPos = 400 - i * 50;
            books.add(new PuzzleBook(entry.getKey(), 50, yPos, BOOK_WIDTH, BOOK_HEIGHT, entry.getValue()));
            slots.add(new PuzzleSlot(i + 1, 300, yPos, SLOT_WIDTH, SLOT_HEIGHT));
            i++;
        }
    }
 
    public void update(final float delta) {
        if (isSolved) {
            return;
        }
 
        final float mx = Gdx.input.getX() * VIEWPORT_WIDTH / Gdx.graphics.getWidth();
        final float my = (Gdx.graphics.getHeight() - Gdx.input.getY()) * VIEWPORT_HEIGHT / Gdx.graphics.getHeight();
 
        handleInput(mx, my);
        checkWinCondition();
    }
 
    private void handleInput(final float mx, final float my) {
        if (Gdx.input.justTouched()) {
            for (final PuzzleBook b : books) {
                if (b.isClicked(mx, my)) {
                    b.setDragging(true);
                    break;
                }
            }
        }
 
        if (Gdx.input.isTouched()) {
            for (final PuzzleBook b : books) {
                if (b.isDragging()) {
                    b.setPosition(mx - b.getRect().width / 2, my - b.getRect().height / 2);
                }
            }
        } else {
            handleDrop();
        }
    }
 
    private void handleDrop() {
        for (final PuzzleBook b : books) {
            if (b.isDragging()) {
                b.setDragging(false);
                boolean placed = false;
                for (final PuzzleSlot s : slots) {
                    if (s.overlaps(b.getRect())) {
                        if (b.getTargetSemester() == s.getExpectedSemester()) {
                            b.snapToSlot(s);
                            placed = true;
                        } else {
                            GameEvent<Float> event = new GameEvent<>(EventType.PUZZLE_FAILED, 10f);
                            EventDispatcher.getInstance().dispatch(event);
                        }
                        break;
                    }
                }
                if (!placed && !b.isPlacedCorrectly()) {
                    b.resetPosition();
                }
            }
        }
    }
 
    private void checkWinCondition() {
        boolean allPlaced = true;
        for (final PuzzleBook b : books) {
            if (!b.isPlacedCorrectly()) {
                allPlaced = false;
                break;
            }
        }
 
        if (allPlaced && !isSolved) {
            isSolved = true;
            GameEvent<Void> event = new GameEvent<>(EventType.PUZZLE_SOLVED, null);
            EventDispatcher.getInstance().dispatch(event);
        }
    }
 
    public void render(final ShapeRenderer shapeRenderer, final SpriteBatch batch) {
        if (isSolved) {
            return;
        }
 
        drawShapes(shapeRenderer);
        drawText(batch);
    }
 
    private void drawShapes(final ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_SLOT);
        for (final PuzzleSlot s : slots) {
            shapeRenderer.rect(s.getX(), s.getY(), s.getWidth(), s.getHeight());
        }
        shapeRenderer.end();
 
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (final PuzzleBook b : books) {
            if (b.isPlacedCorrectly()) {
                shapeRenderer.setColor(COLOR_BOOK_CORRECT);
            } else if (ProgressContext.instance.isHasNao() && b.isDragging()) {
                shapeRenderer.setColor(COLOR_BOOK_DRAGGING);
            } else {
                shapeRenderer.setColor(COLOR_BOOK_DEFAULT);
            }
            shapeRenderer.rect(b.getX(), b.getY(), b.getRect().width, b.getRect().height);
        }
        shapeRenderer.end();
    }
 
    private void drawText(final SpriteBatch batch) {
        batch.begin();
        for (final PuzzleSlot s : slots) {
            font.draw(batch, "HK " + s.getExpectedSemester(), s.getX() + 10, s.getY() + 25);
        }
        for (final PuzzleBook b : books) {
            font.draw(batch, b.getSubjectName(), b.getX() + 5, b.getY() + 20);
        }
        batch.end();
    }
 
    public boolean isSolved() {
        return isSolved;
    }
 
    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
}
