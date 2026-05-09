package hust.adventure.ui;

import com.badlogic.gdx.utils.Disposable;

/**
 * Manages UI components and their visibility.
 */
public class UIManager implements Disposable {
    private final HUD hud;
    private final InventoryUI inventoryUI;

    public UIManager() {
        this.hud = new HUD();
        this.inventoryUI = new InventoryUI();
    }

    public void update(final float delta) {
        // UI logic update if needed
    }

    public HUD getHud() {
        return hud;
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    @Override
    public void dispose() {
        hud.dispose();
        inventoryUI.dispose();
    }
}
