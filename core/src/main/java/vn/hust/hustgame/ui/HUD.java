package vn.hust.hustgame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import vn.hust.hustgame.GameState;

public class HUD {
    private OrthographicCamera uiCam;

    public HUD() {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        // Set projection matrix for UI
        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        // 1. Draw HP Bar
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 570, 150, 15);

        float hpPercent = Math.max(0, Math.min(1, GameState.instance.hp / GameState.instance.maxHp));
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

        float staminaPercent = Math.max(0, Math.min(1, GameState.instance.stamina / GameState.instance.maxStamina));
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(20, 550, staminaPercent * 150, 15);

        // 3. Draw Morale Bar (if needed)
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(20, 530, 150, 15);

        float moralePercent = Math.max(0, Math.min(1, GameState.instance.morale / 100f));
        shapeRenderer.setColor(Color.SKY);
        shapeRenderer.rect(20, 530, moralePercent * 150, 15);

        shapeRenderer.end();

        // 4. Draw Labels
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + (int) GameState.instance.hp + "/" + (int) GameState.instance.maxHp, 180, 583);
        font.draw(batch, "SP: " + (int) GameState.instance.stamina + "/" + (int) GameState.instance.maxStamina, 180,
                563);
        font.draw(batch, "Morale: " + (int) GameState.instance.morale + "%", 180, 543);

        // Status artifacts
        if (GameState.instance.hasNao)
            font.draw(batch, "Artifact: Brain 100%", 20, 510);
        if (GameState.instance.hasUsb)
            font.draw(batch, "Artifact: USB", 20, 490);

        batch.end();
    }
}
