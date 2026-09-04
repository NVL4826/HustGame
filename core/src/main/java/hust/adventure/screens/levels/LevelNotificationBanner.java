package hust.adventure.screens.levels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.screens.PlayScreen;

/**
 * Reusable banner component to render level completion and objective notification messages.
 * Encapsulates message duration timing, alpha transparency fading, text measurement,
 * text box background rendering, and centered layout positioning.
 */
public class LevelNotificationBanner {
    public static final float DEFAULT_DURATION = 4.0f;
    public static final float DEFAULT_BOX_PADDING_X = 40f;
    public static final float DEFAULT_BOX_PADDING_Y = 30f;
    public static final float DEFAULT_CENTER_X = 400f;
    public static final float DEFAULT_CENTER_Y = 300f;

    private float timer = 0f;
    private float duration = DEFAULT_DURATION;
    private String message;
    private Texture textBoxTexture;
    private final GlyphLayout glyphLayout;

    public LevelNotificationBanner() {
        this(null, new GlyphLayout());
    }

    public LevelNotificationBanner(final Texture textBoxTexture) {
        this(textBoxTexture, new GlyphLayout());
    }

    public LevelNotificationBanner(final Texture textBoxTexture, final GlyphLayout glyphLayout) {
        this.textBoxTexture = textBoxTexture;
        this.glyphLayout = glyphLayout != null ? glyphLayout : new GlyphLayout();
    }

    public void show(final String message) {
        show(message, this.textBoxTexture, DEFAULT_DURATION);
    }

    public void show(final String message, final float duration) {
        show(message, this.textBoxTexture, duration);
    }

    public void show(final String message, final Texture textBoxTexture) {
        show(message, textBoxTexture, DEFAULT_DURATION);
    }

    public void show(final String message, final Texture textBoxTexture, final float duration) {
        this.message = message;
        if (textBoxTexture != null) {
            this.textBoxTexture = textBoxTexture;
        }
        this.duration = duration > 0f ? duration : DEFAULT_DURATION;
        this.timer = this.duration;
    }

    public void update(final float delta) {
        if (timer > 0f) {
            timer -= delta;
            if (timer < 0f) {
                timer = 0f;
            }
        }
    }

    public void draw(final PlayScreen context) {
        if (context == null || !isVisible()) {
            return;
        }
        draw(context.getBatch(), context.getFont());
    }

    public void draw(final SpriteBatch batch, final BitmapFont font) {
        if (!isVisible() || batch == null || font == null || message == null) {
            return;
        }

        final float alpha = getAlpha();

        final boolean wasDrawing = batch.isDrawing();
        if (!wasDrawing) {
            batch.begin();
        }

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

        glyphLayout.setText(font, message);

        final float boxW = glyphLayout.width + DEFAULT_BOX_PADDING_X;
        final float boxH = glyphLayout.height + DEFAULT_BOX_PADDING_Y;
        final float boxX = DEFAULT_CENTER_X - boxW / 2f;
        final float boxY = DEFAULT_CENTER_Y - boxH / 2f;

        if (textBoxTexture != null) {
            batch.draw(textBoxTexture, boxX, boxY, boxW, boxH);
        }
        font.draw(batch, message, DEFAULT_CENTER_X - glyphLayout.width / 2f, DEFAULT_CENTER_Y + glyphLayout.height / 2f);

        batch.setColor(origBatchR, origBatchG, origBatchB, origBatchA);
        font.setColor(origFontR, origFontG, origFontB, origFontA);

        if (!wasDrawing) {
            batch.end();
        }
    }

    public boolean isVisible() {
        return timer > 0f;
    }

    public boolean isShowing() {
        return isVisible();
    }

    public float getAlpha() {
        if (duration <= 0f || timer <= 0f) {
            return 0f;
        }
        final float progress = timer / duration;
        return Math.max(0f, Math.min(1f, progress * 2f));
    }

    public float getTimer() {
        return timer;
    }

    public float getDuration() {
        return duration;
    }

    public String getMessage() {
        return message;
    }

    public Texture getTexture() {
        return textBoxTexture;
    }

    public void setTexture(final Texture textBoxTexture) {
        this.textBoxTexture = textBoxTexture;
    }

    public GlyphLayout getGlyphLayout() {
        return glyphLayout;
    }

    public void reset() {
        this.timer = 0f;
    }
}
