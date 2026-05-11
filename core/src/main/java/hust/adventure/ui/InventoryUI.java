package hust.adventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hust.adventure.utils.GamePools;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.Player;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.items.Item;

import java.util.Map;

public class InventoryUI {
    private OrthographicCamera uiCam;

    public InventoryUI() {
        uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();
    }

    public void render(Player player, SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font) {
        if (!ProgressContext.instance.isInventoryOpen)
            return;

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
        
        // Lấy dữ liệu dưới dạng Read-only
        Map<Item, Integer> items = player.getInventory().getReadOnlyItems();

        if (items.isEmpty()) {
            font.draw(batch, "Chua co gi o day ca...", panelX + 50, panelY + panelH - offsetY);
        } else {
            // Xác định phím số nào đang được bấm
            int keyPressed = player.getController().getJustPressedNum();

            int itemIndex = 1;
            String itemToConsumeId = null; 

            for (Map.Entry<Item, Integer> entry : items.entrySet()) {
                Item item = entry.getKey();
                String itemName = item.getName();

                // Hiển thị dạng: [1] Ca phe den : x2
                font.draw(batch, "[" + itemIndex + "] " + itemName + " :  x" + entry.getValue(), panelX + 50,
                        panelY + panelH - offsetY);

                // Nếu người chơi bấm đúng số thứ tự của item này
                if (keyPressed == itemIndex) {
                    itemToConsumeId = item.getId();
                }

                offsetY += 30;
                itemIndex++;
            }

            // Xử lý sử dụng vật phẩm
            if (itemToConsumeId != null) {
                // Dispatch event với ID (Player sẽ resolve lại qua ItemManager)
                GameEvent<String> event = GamePools.obtainEvent();
                event.init(EventType.ITEM_USED, itemToConsumeId);
                EventDispatcher.getInstance().dispatch(event);
            }
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[Nhan '1, 2, 3...' de dung]    [Nhan 'I' de dong]", panelX + 60, panelY + 30);
        batch.end();
    }

    public void dispose() {
    }
}
