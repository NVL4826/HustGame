package vn.hust.hustgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.Map;

import vn.hust.hustgame.GameState;
import vn.hust.hustgame.entities.Player;
import vn.hust.hustgame.world.CoffeeSystem;

public class InventoryUI {
    private OrthographicCamera uiCam;

    public InventoryUI() {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
    }

    public void render(Player player, SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (!GameState.instance.isInventoryOpen) return;

        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Nền đen mờ
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
        shapeRenderer.rect(0, 0, 800, 600);

        // Khung túi đồ
        float panelW = 400;
        float panelH = 300;
        float panelX = (800 - panelW) / 2;
        float panelY = (600 - panelH) / 2;

        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        // Viền
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(panelX, panelY, panelX + panelW, panelY, 3);
        shapeRenderer.rectLine(panelX, panelY + panelH, panelX + panelW, panelY + panelH, 3);
        shapeRenderer.rectLine(panelX, panelY, panelX, panelY + panelH, 3);
        shapeRenderer.rectLine(panelX + panelW, panelY, panelX + panelW, panelY + panelH, 3);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "--- TUI DO CUA SINH VIEN ---", panelX + 110, panelY + panelH - 20);

        font.setColor(Color.WHITE);
        int offsetY = 60;
        Map<String, Integer> items = player.getInventory().getAllItems();

        if (items.isEmpty()) {
            font.draw(batch, "Chua co gi o day ca...", panelX + 50, panelY + panelH - offsetY);
        } else {
            // Xác định phím số nào đang được bấm
            int keyPressed = -1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) keyPressed = 1;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) keyPressed = 2;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) keyPressed = 3;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) keyPressed = 4;
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) keyPressed = 5;

            int itemIndex = 1;
            String itemToConsume = null; // Biến lưu tạm item cần dùng để tránh lỗi ConcurrentModificationException

            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                String itemKey = entry.getKey();
                String itemName = itemKey;

                // Format lại tên cho đẹp
                if (itemKey.equals("coffee_den")) itemName = "Ca phe den (+The luc)";
                if (itemKey.equals("energy_drink")) itemName = "Nuoc tang luc (+40 The luc)";
                if (itemKey.equals("kho_ga")) itemName = "Kho ga la chanh (+25 HP)";

                // Hiển thị dạng: [1] Ca phe den : x2
                font.draw(batch, "[" + itemIndex + "] " + itemName + " :  x" + entry.getValue(), panelX + 50, panelY + panelH - offsetY);

                // Nếu người chơi bấm đúng số thứ tự của item này
                if (keyPressed == itemIndex) {
                    itemToConsume = itemKey;
                }

                offsetY += 30;
                itemIndex++;
            }

            // Xử lý sử dụng vật phẩm (Sau vòng lặp để tránh lỗi mảng đang duyệt bị thay đổi)
            if (itemToConsume != null) {
                if (player.getInventory().removeItem(itemToConsume, 1)) {
                    // Áp dụng tác dụng của từng loại vật phẩm
                    if (itemToConsume.equals("coffee_den")) {
                        GameState.instance.coffeeSystem.consume(CoffeeSystem.CoffeeType.DEN);
                    } else if (itemToConsume.equals("energy_drink")) {
                        GameState.instance.stamina = Math.min(GameState.instance.maxStamina, GameState.instance.stamina + 40);
                    } else if (itemToConsume.equals("kho_ga")) {
                        GameState.instance.hp = Math.min(GameState.instance.maxHp, GameState.instance.hp + 25);
                    }
                }
            }
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[Nhan '1, 2, 3...' de dung]    [Nhan 'I' de dong]", panelX + 60, panelY + 30);
        batch.end();
    }
}
