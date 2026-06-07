package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import hust.adventure.wave.WaveEntry;
import hust.adventure.wave.WaveManager;

/**
 * Behavior class for the Outside area.
 * Loads and ticks enemy waves configured for the map.
 */
public class OutsideBehavior implements LevelBehavior {
    private WaveManager waveManager;
    private boolean messageTriggered = false;
    private float messageTimer = 0f;
    private static final float MESSAGE_DURATION = 4.0f; // 4 seconds total
    private Texture textBoxTexture;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    @Override
    public void init(final LevelContext context) {
        if (context == null) {
            throw new IllegalArgumentException("LevelContext cannot be null");
        }

        final String levelId = context.getConfig().getLevelId();
        final Array<WaveEntry> waves = context.getGame().getWaveDataManager().getWaves(levelId);
        this.waveManager = new WaveManager(waves, context.getEntityFactory());
        context.getUIManager().getHud().setTimeProvider(this.waveManager);

        this.textBoxTexture = context.getGame().getAssetManager().getTexture("text_box.png");
    }

    @Override
    public void update(final LevelContext context, final float delta) {
        if (waveManager != null) {
            waveManager.update(delta, context.getCamera());
            if (waveManager.isFinished() && !context.getEntityManager().hasActiveEnemies() && !messageTriggered) {
                messageTriggered = true;
                messageTimer = MESSAGE_DURATION;
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
            final float alpha = Math.min(1f, progress * 2f); // Fades out in the last 2 seconds

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
            font.setColor(0f, 0f, 0f, alpha); // Black text on textbox

            final String text = "Đã kết thúc tất cả các đợt quái!\nHãy tiến vào Thư viện.";
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
        if (waveManager != null) {
            return waveManager.isFinished() && !context.getEntityManager().hasActiveEnemies();
        }
        return true;
    }

    @Override
    public void dispose(final LevelContext context) {
        if (context != null && context.getUIManager() != null && context.getUIManager().getHud() != null) {
            context.getUIManager().getHud().setTimeProvider(null);
        }
    }
}
