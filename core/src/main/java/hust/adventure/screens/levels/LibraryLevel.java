package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.entities.enemies.LibraryBoss;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.ui.BookPuzzle;

/**
 * Library level with puzzle-solving and boss fight.
 */
public class LibraryLevel extends BaseLevelScreen {
    private BookPuzzle puzzle;
    private boolean puzzleSolved = false;
    private float redFlashTimer = 0f;
    private LibraryBoss libraryBoss;

    public LibraryLevel(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        final java.util.Map<String, Integer> bookConfigs = new java.util.LinkedHashMap<>();
        bookConfigs.put("Toan cao cap", 1);
        bookConfigs.put("CTDL & GT", 3);
        bookConfigs.put("Mang may tinh", 5);
        bookConfigs.put("CSDL", 4);
        bookConfigs.put("Lap trinh Java", 4);
        bookConfigs.put("Ky nghe PM", 5);
        bookConfigs.put("AI", 7);
        bookConfigs.put("Do an", 8);

        puzzle = new BookPuzzle(bookConfigs);
        EventDispatcher.getInstance().addListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().addListener(EventType.PUZZLE_SOLVED, this);
    }

    @Override
    protected void updateLevel(float delta) {
        if (!puzzleSolved) {
            puzzle.update(delta);
        } else {
            if (inputReader.isSpaceJustPressed()) {
                entityFactory.createProjectile(player.getX(), player.getY() + 20, 0, 400, 20, Color.WHITE, true);
            }
        }
        if (redFlashTimer > 0)
            redFlashTimer -= delta;
    }

    @Override
    protected void drawLevel() {
        if (!puzzleSolved) {
            puzzle.render(shapeRenderer, batch);
        } else {
            if (libraryBoss != null && !libraryBoss.isDestroyed()) {
                // UI for Boss health would go here
            }
        }
    }

    private void onPuzzleSolved() {
        if (puzzleSolved)
            return;
        puzzleSolved = true;
        libraryBoss = (LibraryBoss) entityFactory.createLibraryBoss(400, 500);
    }

    public void flashRed() {
        this.redFlashTimer = 0.5f;
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.PUZZLE_FAILED) {
            flashRed();
        } else if (event.getType() == EventType.PUZZLE_SOLVED) {
            onPuzzleSolved();
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_SOLVED, this);
        if (puzzle != null)
            puzzle.dispose();
        super.dispose();
    }
}
