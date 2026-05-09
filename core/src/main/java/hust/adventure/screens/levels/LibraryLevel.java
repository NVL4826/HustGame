package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import hust.adventure.HustGame;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.enemies.LibraryBoss;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.EventListener;
import hust.adventure.ui.BookPuzzle;
import hust.adventure.core.LevelConfig;

/**
 * Library level with puzzle-solving and boss fight.
 */
public class LibraryLevel extends BaseLevelScreen implements EventListener {
    private BookPuzzle puzzle;
    private boolean puzzleSolved = false;
    private float redFlashTimer = 0f;
    private LibraryBoss libraryBoss;

    public LibraryLevel(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        puzzle = new BookPuzzle(this);
        EventDispatcher.getInstance().addListener(EventType.PUZZLE_FAILED, this);
    }

    @Override
    protected void updateLevel(float delta) {
        if (!puzzleSolved) {
            puzzle.update(delta);
        } else {
            if (inputReader.isSpaceJustPressed()) {
                entityManager.addEntity(entityFactory.createProjectile(player.getX(), player.getY() + 20, 0, 400, 20,
                        Color.WHITE, true));
            }
            for (final GameEntity e : entityManager.getEntities()) {
                if (e instanceof BaseEnemy)
                    ((BaseEnemy) e).handleUpdate(delta, player, entityManager);
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

    public void onPuzzleSolved() {
        if (puzzleSolved)
            return;
        puzzleSolved = true;
        libraryBoss = (LibraryBoss) entityFactory.createLibraryBoss(400, 500, collisionManager);
        entityManager.addEntity(libraryBoss);
    }

    public void flashRed() {
        this.redFlashTimer = 0.5f;
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.PUZZLE_FAILED) {
            flashRed();
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_FAILED, this);
        if (puzzle != null)
            puzzle.dispose();
        super.dispose();
    }
}
