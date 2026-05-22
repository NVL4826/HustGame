package hust.adventure.screens.levels;

import hust.adventure.entities.enemies.LibraryBoss;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.ui.BookPuzzle;

/**
 * Behavior class for the Library level, managing puzzle and boss fight.
 */
public class LibraryBehavior implements LevelBehavior, EventListener {
    private LevelContext context;
    private BookPuzzle puzzle;
    private boolean puzzleSolved = false;
    private boolean artifactSpawned = false;
    private float redFlashTimer = 0f;
    private LibraryBoss libraryBoss;

    @Override
    public void init(final LevelContext context) {
        if (context == null) {
            throw new IllegalArgumentException("LevelContext cannot be null");
        }
        this.context = context;

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
    public void update(final LevelContext context, final float delta) {
        if (!puzzleSolved) {
            puzzle.update(delta);
        } else {
            if (libraryBoss != null && (libraryBoss.isDead() || libraryBoss.isDestroyed()) && !artifactSpawned) {
                context.getEntityFactory().createLibraryArtifact(libraryBoss.getX(), libraryBoss.getY());
                artifactSpawned = true;
            }
        }
        if (redFlashTimer > 0) {
            redFlashTimer -= delta;
        }
    }

    @Override
    public void draw(final LevelContext context) {
        if (!puzzleSolved) {
            puzzle.render(context.getShapeRenderer(), context.getBatch());
        } else {
            if (libraryBoss != null && !libraryBoss.isDestroyed()) {
                // UI for Boss health would go here
            }
        }
    }

    private void onPuzzleSolved() {
        if (puzzleSolved) {
            return;
        }
        puzzleSolved = true;
        libraryBoss = (LibraryBoss) context.getEntityFactory().createLibraryBoss(400, 500);
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
    public boolean canTransition(final LevelContext context) {
        return puzzleSolved;
    }

    @Override
    public void dispose(final LevelContext context) {
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_SOLVED, this);
        if (puzzle != null) {
            puzzle.dispose();
        }
    }
}
