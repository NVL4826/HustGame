package hust.adventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import hust.adventure.entities.Player;
import hust.adventure.ui.components.UpgradeAction;

public class LevelUpUI {
    private OrthographicCamera uiCam;
    private Array<UpgradeAction> currentChoices;
    private Runnable onResume;

    public LevelUpUI() {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
        currentChoices = new Array<>();
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setChoices(Array<UpgradeAction> choices) {
        this.currentChoices.clear();
        this.currentChoices.addAll(choices);
    }

    public void render(Player player, SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (currentChoices.isEmpty())
            return;

        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Nền đen mờ
        shapeRenderer.setColor(new Color(0, 0, 0, 0.8f));
        shapeRenderer.rect(0, 0, 800, 600);

        // Khung Level Up
        float panelW = 500;
        float panelH = 400;
        float panelX = (800 - panelW) / 2;
        float panelY = (600 - panelH) / 2;

        shapeRenderer.setColor(new Color(0.1f, 0.2f, 0.4f, 1f));
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        // Viền
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rectLine(panelX, panelY, panelX + panelW, panelY, 4);
        shapeRenderer.rectLine(panelX, panelY + panelH, panelX + panelW, panelY + panelH, 4);
        shapeRenderer.rectLine(panelX, panelY, panelX, panelY + panelH, 4);
        shapeRenderer.rectLine(panelX + panelW, panelY, panelX + panelW, panelY + panelH, 4);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "--- LEVEL UP! ---", panelX + 170, panelY + panelH - 30);
        font.setColor(Color.WHITE);
        font.draw(batch, "Chon 1 phan thuong:", panelX + 50, panelY + panelH - 80);

        int offsetY = 130;

        for (int i = 0; i < currentChoices.size; i++) {
            UpgradeAction action = currentChoices.get(i);
            int choiceNum = i + 1;

            font.setColor(Color.CYAN);
            font.draw(batch, "[" + choiceNum + "] " + action.getName(), panelX + 70, panelY + panelH - offsetY);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "    " + action.getDescription(), panelX + 70, panelY + panelH - offsetY - 25);

            offsetY += 80;
        }

        batch.end();
    }

    public void update(Player player) {
        if (currentChoices.isEmpty() || player == null) {
            return;
        }

        int keyPressed = player.getController().getJustPressedNum();
        if (keyPressed > 0 && keyPressed <= currentChoices.size) {
            UpgradeAction action = currentChoices.get(keyPressed - 1);
            action.execute(player);
            if (onResume != null) {
                onResume.run();
            }
            currentChoices.clear();
        }
    }

    public void dispose() {
    }
}
