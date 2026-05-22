package hust.adventure.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.core.TimeProvider;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.Player;
import hust.adventure.entities.status.StatusFlag;

public class HUD {
    private OrthographicCamera uiCam;

    public HUD() {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
    }

    private TimeProvider timeProvider;

    public void setTimeProvider(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        // Set projection matrix for UI
        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        // 1. Draw HP Bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 570, 150, 15);

        float hpPercent = Math.max(0, Math.min(1, ProgressContext.instance.getHp() / ProgressContext.instance.getMaxHp()));
        if (hpPercent > 0.6f)
            shapeRenderer.setColor(Color.GREEN);
        else if (hpPercent > 0.3f)
            shapeRenderer.setColor(Color.ORANGE);
        else
            shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(20, 570, hpPercent * 150, 15);

        // 2. Draw Stamina Bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 550, 150, 15);

        float staminaPercent = Math.max(0,
                Math.min(1, ProgressContext.instance.getStamina() / ProgressContext.instance.getMaxStamina()));
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(20, 550, staminaPercent * 150, 15);

        // 3. Draw Morale Bar (if needed)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 530, 150, 15);

        float moralePercent = Math.max(0, Math.min(1, ProgressContext.instance.getMorale() / 100f));
        shapeRenderer.setColor(Color.SKY);
        shapeRenderer.rect(20, 530, moralePercent * 150, 15);

        // 4. Draw Exp Bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 510, 150, 15);

        float expPercent = Math.max(0, Math.min(1, ProgressContext.instance.getExp() / ProgressContext.instance.getExpToNextLevel()));
        shapeRenderer.setColor(Color.PURPLE);
        shapeRenderer.rect(20, 510, expPercent * 150, 15);

        shapeRenderer.end();

        // 4. Draw Labels
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + (int) ProgressContext.instance.getHp() + "/" + (int) ProgressContext.instance.getMaxHp(), 180,
                583);
        font.draw(batch,
                "SP: " + (int) ProgressContext.instance.getStamina() + "/" + (int) ProgressContext.instance.getMaxStamina(), 180,
                563);
        font.draw(batch, "Morale: " + (int) ProgressContext.instance.getMorale() + "%", 180, 543);
        font.draw(batch, "LV: " + ProgressContext.instance.getLevel() + " EXP: " + (int) ProgressContext.instance.getExp() + "/" + (int) ProgressContext.instance.getExpToNextLevel(), 180, 523);

        if (timeProvider != null) {
            int totalSeconds = (int) timeProvider.getCurrentTime();
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            String timeString = String.format("%02d:%02d", minutes, seconds);
            font.draw(batch, "Time: " + timeString, 380, 583);
        }

        // Status artifacts
        if (ProgressContext.instance.isHasNao())
            font.draw(batch, "Artifact: Brain 100%", 20, 480);
        if (ProgressContext.instance.isHasUsb())
            font.draw(batch, "Artifact: USB", 20, 460);

        // Active spells notifications
        float spellY = 440;
        if (ProgressContext.instance.getEnemyTimeScale() == 0f) {
            font.setColor(Color.ORANGE);
            font.draw(batch, "STUN ACTIVE!", 20, spellY);
            spellY -= 20;
        } else if (ProgressContext.instance.getEnemyTimeScale() == 0.3f) {
            font.setColor(Color.GOLD);
            font.draw(batch, "SLOW-MOTION ACTIVE!", 20, spellY);
            spellY -= 20;
        }
        if (ProgressContext.instance.getShowEnemiesTimer() > 0f) {
            font.setColor(Color.CYAN);
            font.draw(batch, String.format("RADAR ACTIVE (%.1fs)", ProgressContext.instance.getShowEnemiesTimer()), 20, spellY);
            spellY -= 20;
        }

        // Active status effects notifications
        Player player = ProgressContext.instance.getPlayer();
        if (player != null) {
            if (player.hasStatus(StatusFlag.SPEED_BOOSTED)) {
                font.setColor(Color.CYAN);
                font.draw(batch, "SPEED BOOST ACTIVE!", 20, spellY);
                spellY -= 20;
            }
            if (player.hasStatus(StatusFlag.REGEN_HP)) {
                font.setColor(Color.GREEN);
                font.draw(batch, "HP REGEN ACTIVE!", 20, spellY);
                spellY -= 20;
            }
            if (player.hasStatus(StatusFlag.REGEN_STAMINA)) {
                font.setColor(Color.GOLD);
                font.draw(batch, "STAMINA REGEN ACTIVE!", 20, spellY);
                spellY -= 20;
            }
            if (player.hasStatus(StatusFlag.CONFUSED)) {
                font.setColor(Color.MAGENTA);
                font.draw(batch, "CONFUSION ACTIVE!", 20, spellY);
                spellY -= 20;
            }
        }
        font.setColor(Color.WHITE);

        batch.end();
    }

    public void dispose() {
    }
}
