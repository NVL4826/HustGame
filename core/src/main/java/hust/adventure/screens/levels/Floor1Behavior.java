package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import hust.adventure.core.context.GameProgressContext;
import hust.adventure.entities.player.Player;
import hust.adventure.items.base.Item;
import hust.adventure.items.base.ItemManager;
import hust.adventure.progression.MapDirector;
import hust.adventure.wave.WaveEntry;
import hust.adventure.wave.WaveManager;

/**
 * Behavior class for Floor 1 (Map 2 / Tang 1).
 * Coordinates lecture hall classroom Deadlines, coffee items, and Lecture Notes gating.
 */
public class Floor1Behavior implements LevelBehavior {
    private WaveManager waveManager;
    private boolean messageTriggered = false;
    private float messageTimer = 0f;
    private static final float MESSAGE_DURATION = 4.0f;
    private Texture textBoxTexture;
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private boolean notesSpawned = false;

    @Override
    public void init(final LevelContext context) {
        if (context == null) {
            throw new IllegalArgumentException("LevelContext cannot be null");
        }

        final GameProgressContext progress = context.getProgressContext();
        final ItemManager im = progress != null ? progress.getItemManager() : null;

        if (im != null && context.getEntityFactory() != null) {
            // Create ambient Coffee Items in lecture hall
            context.getEntityFactory().createItemDrop(300f, 150f, im.getItem("coffee_den"), Color.BROWN);
            context.getEntityFactory().createItemDrop(350f, 180f, im.getItem("coffee_sua"), Color.YELLOW);

            // Create ambient NPCs
            context.getEntityFactory().createStaticNPC(200f, 300f, "Guard", Color.BLUE);
            context.getEntityFactory().createStaticNPC(600f, 300f, "Staff", Color.CYAN);
        }

        final String levelId = context.getConfig().getLevelId();
        final Array<WaveEntry> waves = context.getGame().getWaveDataManager().getWaves(levelId);
        if (waves != null && waves.size > 0) {
            this.waveManager = new WaveManager(waves, context.getEntityFactory());
            if (context.getUIManager() != null && context.getUIManager().getHud() != null) {
                context.getUIManager().getHud().setTimeProvider(this.waveManager);
            }
        }

        this.textBoxTexture = context.getGame().getAssetManager().getTexture("text_box.png");
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        if (waveManager != null) {
            waveManager.update(delta, context.getCamera());
            if (waveManager.isFinished() && !context.getEntityManager().hasActiveEnemies() && !messageTriggered) {
                messageTriggered = true;
                messageTimer = MESSAGE_DURATION;

                final GameProgressContext progress = context.getProgressContext();
                if (progress != null && progress.getMapDirector() != null) {
                    progress.getMapDirector().onDeadlinesCleared();
                }

                if (!notesSpawned && context.getEntityFactory() != null && progress != null) {
                    notesSpawned = true;
                    final ItemManager itemManager = progress.getItemManager();
                    Item notesItem = itemManager != null ? itemManager.getItem("lecture_notes") : null;
                    if (notesItem == null && itemManager != null) {
                        notesItem = itemManager.getItem("note");
                    }
                    if (notesItem != null) {
                        final Player player = progress.getPlayer();
                        final float spawnX = player != null ? player.getX() + 100f : 500f;
                        final float spawnY = player != null ? player.getY() + 60f : 300f;
                        context.getEntityFactory().createItemDrop(spawnX, spawnY, notesItem, Color.WHITE);
                    }
                }
            }
        }

        if (messageTimer > 0) {
            messageTimer -= delta;
        }
    }

    @Override
    public void draw(final LevelContext context) {
        if (messageTimer > 0) {
            final float progress = messageTimer / MESSAGE_DURATION;
            final float alpha = Math.min(1f, progress * 2f);

            final SpriteBatch batch = context.getBatch();
            final BitmapFont font = context.getFont();

            batch.begin();
            final Color batchColor = batch.getColor();
            final float origBatchR = batchColor.r;
            final float origBatchG = batchColor.g;
            final float origBatchB = batchColor.b;
            final float origBatchA = batchColor.a;

            final Color fontColor = font.getColor();
            final float origFontR = fontColor.r;
            final float origFontG = fontColor.g;
            final float origFontB = fontColor.b;
            final float origFontA = fontColor.a;

            batch.setColor(1f, 1f, 1f, alpha);
            font.setColor(0f, 0f, 0f, alpha);

            final String text = "Đã vượt qua các Deadline trên giảng đường!\nHãy nhặt Bài Giảng và tiến vào Thư viện.";
            glyphLayout.setText(font, text);

            final float boxW = glyphLayout.width + 40f;
            final float boxH = glyphLayout.height + 30f;
            final float boxX = 400f - boxW / 2f;
            final float boxY = 300f - boxH / 2f;

            batch.draw(textBoxTexture, boxX, boxY, boxW, boxH);
            font.draw(batch, text, 400f - glyphLayout.width / 2f, 300f + glyphLayout.height / 2f);

            batch.setColor(origBatchR, origBatchG, origBatchB, origBatchA);
            font.setColor(origFontR, origFontG, origFontB, origFontA);
            batch.end();
        }
    }

    @Override
    public boolean canTransition(final LevelContext context) {
        if (context == null || context.getProgressContext() == null) {
            return false;
        }

        final MapDirector director = context.getProgressContext().getMapDirector();
        if (director == null || !director.canTransition()) {
            return false;
        }

        if (waveManager != null && (!waveManager.isFinished() || context.getEntityManager().hasActiveEnemies())) {
            return false;
        }

        return true;
    }

    @Override
    public void dispose(final LevelContext context) {
        if (context != null && context.getUIManager() != null && context.getUIManager().getHud() != null) {
            context.getUIManager().getHud().setTimeProvider(null);
        }
    }

    @Override
    public boolean isAutoAttackAllowed() {
        if (waveManager != null && !waveManager.isFinished()) {
            return true;
        }
        return false;
    }
}
