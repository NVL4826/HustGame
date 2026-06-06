package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.core.data.LevelConfig;
import hust.adventure.events.MapTransitionData;
import hust.adventure.ui.puzzle.PuzzleSequencer;
import hust.adventure.ui.puzzle.SimonPuzzle;
import hust.adventure.ui.puzzle.MemoryCardPuzzle;
import hust.adventure.ui.puzzle.SpeedMathPuzzle;

/**
 * Behavior for the Library level.
 * Sequence:
 * 1. Sequential Puzzle games (Simon -> Memory Card -> Speed Math) managed by PuzzleSequencer.
 * 2. Completion -> spawn LibraryBoss + null_pointer minions.
 * 3. Boss defeat -> spawns "brain" item at the center.
 * 4. Picking up the brain -> transition to Lab.
 */
public class LibraryBehavior implements LevelBehavior {

    // Midpoint of Library map (1344 x 768)
    private static final float CENTER_X = 672f;
    private static final float CENTER_Y = 384f;

    // Boss spawn location
    private static final float BOSS_SPAWN_X = 672f;
    private static final float BOSS_SPAWN_Y = 480f;

    // Normal enemies spawned alongside boss
    private static final int MINION_COUNT = 4;

    // State fields
    private LevelContext context;
    private PuzzleSequencer sequencer;
    private Enemy libraryBoss;

    private boolean puzzleSolved = false;
    private boolean enemiesSpawned = false;
    private boolean brainSpawned = false;
    private boolean libraryCleared = false;

    @Override
    public void init(final LevelContext ctx) {
        this.context = ctx;

        // Create the sequencer and add the three mini-games
        sequencer = new PuzzleSequencer();
        sequencer.addPuzzle(new SimonPuzzle());
        sequencer.addPuzzle(new MemoryCardPuzzle());
        sequencer.addPuzzle(new SpeedMathPuzzle());
        
        sequencer.init(ctx);

        EventDispatcher.getInstance().addListener(EventType.ITEM_PICKED_UP, this);
    }

    @Override
    public void update(final LevelContext ctx, final float delta) {
        if (libraryCleared) {
            return;
        }

        if (!puzzleSolved) {
            // Update sequencer
            sequencer.update(delta);
            if (sequencer.isAllSolved()) {
                onPuzzleSolved();
            }
        } else {
            // Combat state: Wait for boss defeat to spawn the brain
            if (enemiesSpawned && !brainSpawned && libraryBoss != null
                    && (libraryBoss.isDead() || libraryBoss.isDestroyed())) {
                final float bx = libraryBoss.isDestroyed() ? CENTER_X : libraryBoss.getX();
                final float by = libraryBoss.isDestroyed() ? CENTER_Y : libraryBoss.getY();
                ctx.getEntityFactory().createItemDrop(bx, by,
                        ctx.getProgressContext().getItemManager().getItem("brain"), Color.CYAN);
                brainSpawned = true;
            }
        }
    }

    @Override
    public void draw(final LevelContext ctx) {
        if (!puzzleSolved) {
            sequencer.render(ctx.getShapeRenderer(), ctx.getBatch());
        }
    }

    private void onPuzzleSolved() {
        if (puzzleSolved) {
            return;
        }
        puzzleSolved = true;

        // Spawn boss via EntityFactory (SSOT)
        libraryBoss = (Enemy) context.getEntityFactory().createEnemy("library_boss", BOSS_SPAWN_X, BOSS_SPAWN_Y);

        // Spawn normal minions surrounding the boss area
        for (int i = 0; i < MINION_COUNT; i++) {
            final float x = 200f + i * 220f;
            final float y = MathUtils.random(200f, 550f);
            context.getEntityFactory().createEnemy("null_pointer", x, y);
        }

        enemiesSpawned = true;
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.ITEM_PICKED_UP) {
            final ItemPickedUpEvent data = (ItemPickedUpEvent) event.getData();
            if ("brain".equals(data.getItem().getId())) {
                libraryCleared = true;

                // Transition to the Lab level dynamically using config (Single Source of Truth)
                if (context != null && context.getGame() != null) {
                    final LevelConfig labConfig = context.getGame().getLevelDataManager().getLevelConfig("LAB");
                    if (labConfig != null) {
                        final MapTransitionData trans = new MapTransitionData(
                                labConfig.getMapPath(),
                                labConfig.getSpawnX(),
                                labConfig.getSpawnY()
                        );
                        EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.MAP_TRANSITION, trans));
                    }
                }
            }
        }
    }

    @Override
    public boolean isPuzzleActive() {
        return !puzzleSolved;
    }

    @Override
    public boolean canTransition(final LevelContext ctx) {
        return libraryCleared;
    }

    @Override
    public void dispose(final LevelContext ctx) {
        EventDispatcher.getInstance().removeListener(EventType.ITEM_PICKED_UP, this);
        if (sequencer != null) {
            sequencer.dispose();
            sequencer = null;
        }
    }
}
