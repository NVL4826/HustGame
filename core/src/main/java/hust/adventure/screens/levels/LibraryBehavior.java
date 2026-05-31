package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.items.ItemManager;
import hust.adventure.ui.BookPuzzle;

/**
 * Behavior cho màn Thư Viện. Flow: 1. Puzzle ghép môn học vào ô (BookPuzzle). 2. PUZZLE_SOLVED → spawn LibraryBoss +
 * các quái. 3. Khi hết quái (boss chết) → Não xuất hiện ở giữa map. 4. Player nhặt Não → chuyển sang màn Lab.
 */
public class LibraryBehavior implements LevelBehavior, EventListener {

    // Giữa map library (1344 × 768)
    private static final float CENTER_X = 672f;
    private static final float CENTER_Y = 384f;

    // Spawn boss hơi cao hơn trung tâm
    private static final float BOSS_SPAWN_X = 672f;
    private static final float BOSS_SPAWN_Y = 480f;

    // Target map sau khi nhặt não
    private static final String NEXT_MAP = "lab.tmx";
    private static final float NEXT_SPAWN_X = 400f;
    private static final float NEXT_SPAWN_Y = 300f;

    // Số quái thường spawn cùng boss
    private static final int MINION_COUNT = 4;

    // State
    private LevelContext context;
    private BookPuzzle puzzle;
    private Enemy libraryBoss;

    private boolean puzzleSolved = false;
    private boolean enemiesSpawned = false;
    private boolean brainSpawned = false;
    private boolean libraryCleared = false;

    private float redFlashTimer = 0f;

    // ─────────────────────────────────────────────────────────────────────
    @Override
    public void init(final LevelContext ctx) {
        this.context = ctx;

        // Cấu hình puzzle (các môn học)
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
        EventDispatcher.getInstance().addListener(EventType.ITEM_PICKED_UP, this);
    }

    // ─────────────────────────────────────────────────────────────────────
    @Override
    public void update(final LevelContext ctx, final float delta) {
        if (redFlashTimer > 0)
            redFlashTimer -= delta;
        if (libraryCleared)
            return;

        if (!puzzleSolved) {
            // Giai đoạn puzzle
            puzzle.update(delta);
        } else {
            // Giai đoạn chiến đấu: chờ boss chết → não rơi ra ngay tại chỗ boss
            if (enemiesSpawned && !brainSpawned && libraryBoss != null
                    && (libraryBoss.isDead() || libraryBoss.isDestroyed())) {
                // Não rơi tại vị trí boss (hoặc giữa map nếu boss đã bị xóa)
                float bx = libraryBoss.isDestroyed() ? CENTER_X : libraryBoss.getX();
                float by = libraryBoss.isDestroyed() ? CENTER_Y : libraryBoss.getY();
                ctx.getEntityFactory().createItemDrop(bx, by, ItemManager.instance.getItem("brain"), Color.CYAN);
                brainSpawned = true;
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    @Override
    public void draw(final LevelContext ctx) {
        if (!puzzleSolved) {
            puzzle.render(ctx.getShapeRenderer(), ctx.getBatch());
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    private void onPuzzleSolved() {
        if (puzzleSolved)
            return;
        puzzleSolved = true;

        // Spawn boss
        libraryBoss = (Enemy) context.getEntityFactory().createEnemy("library_boss", BOSS_SPAWN_X, BOSS_SPAWN_Y);

        // Spawn quái thường xung quanh boss
        for (int i = 0; i < MINION_COUNT; i++) {
            float x = 200f + i * 220f;
            float y = MathUtils.random(200f, 550f);
            context.getEntityFactory().createEnemy("null_pointer", x, y);
        }

        enemiesSpawned = true;
    }

    public void flashRed() {
        redFlashTimer = 0.5f;
    }

    // ─────────────────────────────────────────────────────────────────────
    @Override
    public void onEvent(final GameEvent<?> event) {
        switch (event.getType()) {
        case PUZZLE_FAILED:
            flashRed();
            break;

        case PUZZLE_SOLVED:
            onPuzzleSolved();
            break;

        case ITEM_PICKED_UP:
            final ItemPickedUpEvent data = (ItemPickedUpEvent) event.getData();
            if ("brain".equals(data.getItem().getId())) {
                ProgressContext.instance.setHasNao(true);
                ProgressContext.instance.setLibraryCleared(true);
                libraryCleared = true;

                // Chuyển sang màn Lab
                final MapTransitionData trans = new MapTransitionData(NEXT_MAP, NEXT_SPAWN_X, NEXT_SPAWN_Y);
                EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.MAP_TRANSITION, trans));
            }
            break;

        default:
            break;
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    @Override
    public boolean canTransition(final LevelContext ctx) {
        return libraryCleared;
    }

    @Override
    public void dispose(final LevelContext ctx) {
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_FAILED, this);
        EventDispatcher.getInstance().removeListener(EventType.PUZZLE_SOLVED, this);
        EventDispatcher.getInstance().removeListener(EventType.ITEM_PICKED_UP, this);
        if (puzzle != null)
            puzzle.dispose();
    }
}
