package hust.adventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.core.TimeProvider;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.Player;
import hust.adventure.entities.status.StatusFlag;

/**
 * Upgraded HUD – modern bar design with gradient fills, icons, rounded feel,
 * and a low-HP pulse warning.
 */
public class HUD {
    private final OrthographicCamera uiCam;

    // Layout constants
    private static final float PANEL_X  = 14f;
    private static final float PANEL_Y  = 498f;
    private static final float PANEL_W  = 210f;
    private static final float PANEL_H  = 98f;

    private static final float BAR_X    = 42f;
    private static final float BAR_W    = 155f;
    private static final float BAR_H    = 13f;
    private static final float BAR_HP_Y = 575f;
    private static final float BAR_SP_Y = 556f;
    private static final float BAR_MR_Y = 537f;
    private static final float BAR_EX_Y = 518f;

    // White pixel for drawing solid rects via SpriteBatch (avoids ShapeRenderer flush)
    private static Texture whitePixel;

    // Low-HP pulse
    private float pulseTimer = 0f;

    private TimeProvider timeProvider;

    public HUD() {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
    }

    private static Texture getWhitePixel() {
        if (whitePixel == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.WHITE);
            pm.fill();
            whitePixel = new Texture(pm);
            pm.dispose();
        }
        return whitePixel;
    }

    public static void disposeStatic() {
        if (whitePixel != null) { whitePixel.dispose(); whitePixel = null; }
    }

    public void setTimeProvider(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    // ── helpers ────────────────────────────────────────────────────────────

    /** Vẽ thanh bar có nền tối + fill gradient 2 màu (left→right tint). */
    private void drawBar(ShapeRenderer sr, float x, float y, float w, float h,
                         float percent, Color colLeft, Color colRight, float bgAlpha) {
        // Nền
        sr.setColor(0.1f, 0.1f, 0.12f, bgAlpha);
        sr.rect(x, y, w, h);

        // Fill gradient bằng cách vẽ nhiều dải mỏng
        if (percent > 0) {
            float fillW = Math.max(2f, w * percent);
            int steps = (int) fillW;
            for (int i = 0; i < steps; i++) {
                float t = i / (float) steps;
                float r = colLeft.r + (colRight.r - colLeft.r) * t;
                float g = colLeft.g + (colRight.g - colLeft.g) * t;
                float b = colLeft.b + (colRight.b - colLeft.b) * t;
                sr.setColor(r, g, b, 1f);
                sr.rect(x + i, y, 1f, h);
            }
        }

        // Viền mỏng
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1f, 1f, 1f, 0.18f);
        sr.rect(x, y, w, h);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
    }

    /** Vẽ panel nền với viền neon mỏng. */
    private void drawPanel(ShapeRenderer sr, float x, float y, float w, float h) {
        // Nền semi-transparent dark
        sr.setColor(0.04f, 0.04f, 0.10f, 0.82f);
        sr.rect(x, y, w, h);
        // Inner border glow
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(0.3f, 0.5f, 1.0f, 0.5f);
        sr.rect(x, y, w, h);
        sr.setColor(0.2f, 0.3f, 0.7f, 0.25f);
        sr.rect(x + 1, y + 1, w - 2, h - 2);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
    }

    // ── main render ────────────────────────────────────────────────────────
    public void render(SpriteBatch batch, ShapeRenderer sr, BitmapFont font) {
        sr.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        float hpPct = Math.max(0, Math.min(1,
                ProgressContext.instance.getHp() / ProgressContext.instance.getMaxHp()));
        float spPct = Math.max(0, Math.min(1,
                ProgressContext.instance.getStamina() / ProgressContext.instance.getMaxStamina()));
        float mrPct = Math.max(0, Math.min(1, ProgressContext.instance.getMorale() / 100f));
        float exPct = Math.max(0, Math.min(1,
                ProgressContext.instance.getExp() / ProgressContext.instance.getExpToNextLevel()));

        // Pulse timer cho HP thấp
        pulseTimer += Gdx.graphics.getDeltaTime();
        boolean lowHp = hpPct < 0.25f;
        float pulse = (float)(Math.sin(pulseTimer * 5.0) * 0.5 + 0.5); // 0→1

        // ── ShapeRenderer pass ──────────────────────────────────────────────
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // HP bar: xanh lá → vàng → đỏ theo %
        Color hpLeft, hpRight;
        if (hpPct > 0.5f) {
            hpLeft  = new Color(0.1f, 0.9f, 0.3f, 1f);
            hpRight = new Color(0.4f, 1.0f, 0.2f, 1f);
        } else if (hpPct > 0.25f) {
            hpLeft  = new Color(1.0f, 0.65f, 0.0f, 1f);
            hpRight = new Color(1.0f, 0.85f, 0.1f, 1f);
        } else {
            // Pulse đỏ khi HP cực thấp
            float pr = 0.8f + pulse * 0.2f;
            hpLeft  = new Color(pr, 0.05f, 0.05f, 1f);
            hpRight = new Color(1.0f, 0.2f + pulse * 0.2f, 0.0f, 1f);
        }
        drawBar(sr, BAR_X, BAR_HP_Y, BAR_W, BAR_H, hpPct, hpLeft, hpRight, 0.55f);

        // SP bar: tím → xanh cyan
        drawBar(sr, BAR_X, BAR_SP_Y, BAR_W, BAR_H, spPct,
                new Color(0.5f, 0.1f, 0.9f, 1f), new Color(0.1f, 0.7f, 1.0f, 1f), 0.55f);

        // Morale bar: cam → vàng sáng
        drawBar(sr, BAR_X, BAR_MR_Y, BAR_W, BAR_H, mrPct,
                new Color(0.9f, 0.5f, 0.0f, 1f), new Color(1.0f, 0.9f, 0.2f, 1f), 0.55f);

        // EXP bar: tím đậm → hồng
        drawBar(sr, BAR_X, BAR_EX_Y, BAR_W, BAR_H, exPct,
                new Color(0.5f, 0.0f, 0.7f, 1f), new Color(1.0f, 0.3f, 0.9f, 1f), 0.55f);

        // Viền ngoài cùng panel nhấp nháy đỏ khi HP thấp
        if (lowHp) {
            sr.end();
            sr.begin(ShapeRenderer.ShapeType.Line);
            sr.setColor(1f, 0f, 0f, 0.3f + pulse * 0.6f);
            sr.rect(PANEL_X - 1, PANEL_Y - 1, PANEL_W + 2, PANEL_H + 2);
            sr.end();
            sr.begin(ShapeRenderer.ShapeType.Filled);
        }

        sr.end();

        // ── SpriteBatch text pass ───────────────────────────────────────────
        batch.begin();

        // Icon + label icons trái thanh
        font.setColor(1f, 0.4f, 0.4f, 1f);
        font.draw(batch, "HP", PANEL_X + 3, BAR_HP_Y + BAR_H - 1);
        font.setColor(0.6f, 0.4f, 1.0f, 1f);
        font.draw(batch, "SP", PANEL_X + 3, BAR_SP_Y + BAR_H - 1);
        font.setColor(1.0f, 0.8f, 0.2f, 1f);
        font.draw(batch, "ML", PANEL_X + 3, BAR_MR_Y + BAR_H - 1);
        font.setColor(0.9f, 0.4f, 1.0f, 1f);
        font.draw(batch, "EX", PANEL_X + 3, BAR_EX_Y + BAR_H - 1);

        // Giá trị số bên phải
        font.setColor(Color.WHITE);
        font.draw(batch,
                (int)ProgressContext.instance.getHp() + "/" + (int)ProgressContext.instance.getMaxHp(),
                BAR_X + BAR_W + 4, BAR_HP_Y + BAR_H - 1);
        font.draw(batch,
                (int)ProgressContext.instance.getStamina() + "/" + (int)ProgressContext.instance.getMaxStamina(),
                BAR_X + BAR_W + 4, BAR_SP_Y + BAR_H - 1);
        font.draw(batch, (int)ProgressContext.instance.getMorale() + "%",
                BAR_X + BAR_W + 4, BAR_MR_Y + BAR_H - 1);
        font.setColor(0.8f, 0.6f, 1f, 1f);
        font.draw(batch, "LV" + ProgressContext.instance.getLevel(),
                BAR_X + BAR_W + 4, BAR_EX_Y + BAR_H - 1);

        // Timer
        if (timeProvider != null) {
            int totalSeconds = (int) timeProvider.getCurrentTime();
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            font.setColor(0.8f, 0.8f, 0.8f, 1f);
            font.draw(batch, String.format("%02d:%02d", minutes, seconds), 370, 590);
        }

        // Artifacts
        float afx = 14f, afy = 492f;
        if (ProgressContext.instance.isHasNao()) {
            font.setColor(1f, 0.85f, 0.2f, 1f);
            font.draw(batch, "★ Nao 100%", afx, afy);
            afy -= 16f;
        }
        if (ProgressContext.instance.isHasUsb()) {
            font.setColor(0.3f, 0.8f, 1f, 1f);
            font.draw(batch, "★ USB", afx, afy);
            afy -= 16f;
        }

        // Active spells / status effects
        float spellY = afy - 4f;
        if (ProgressContext.instance.getEnemyTimeScale() == 0f) {
            font.setColor(1f, 0.5f, 0f, 1f);
            font.draw(batch, "⚡ STUN!", 14, spellY); spellY -= 16f;
        } else if (ProgressContext.instance.getEnemyTimeScale() == 0.3f) {
            font.setColor(1f, 0.9f, 0f, 1f);
            font.draw(batch, "⏱ SLOW", 14, spellY); spellY -= 16f;
        }
        if (ProgressContext.instance.getShowEnemiesTimer() > 0f) {
            font.setColor(0.3f, 1f, 1f, 1f);
            font.draw(batch, String.format("RADAR %.0fs", ProgressContext.instance.getShowEnemiesTimer()), 14, spellY);
            spellY -= 16f;
        }

        Player player = ProgressContext.instance.getPlayer();
        if (player != null) {
            if (player.hasStatus(StatusFlag.SPEED_BOOSTED)) {
                font.setColor(0.3f, 1f, 0.8f, 1f);
                font.draw(batch, "SPEED+", 14, spellY); spellY -= 16f;
            }
            if (player.hasStatus(StatusFlag.REGEN_HP)) {
                font.setColor(0.3f, 1f, 0.4f, 1f);
                font.draw(batch, "HP REGEN", 14, spellY); spellY -= 16f;
            }
            if (player.hasStatus(StatusFlag.CONFUSED)) {
                font.setColor(1f, 0.2f, 1f, 1f);
                font.draw(batch, "CONFUSED!", 14, spellY); spellY -= 16f;
            }
        }

        font.setColor(Color.WHITE);
        batch.end();
    }

    public void dispose() {
    }
}
