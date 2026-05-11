package hust.adventure.ui;

import com.badlogic.gdx.utils.Disposable;

/**
 * Manages UI components and their visibility.
 */
public class UIManager implements Disposable {
    private final HUD hud;
    private final InventoryUI inventoryUI;
    private final LevelUpUI levelUpUI;
    private final DamageTextManager damageTextManager;
    private final RouletteUI rouletteUI;

    public UIManager() {
        this.hud = new HUD();
        this.inventoryUI = new InventoryUI();
        this.levelUpUI = new LevelUpUI();
        this.damageTextManager = new DamageTextManager();
        this.rouletteUI = new RouletteUI();
    }

    public void update(final float delta) {
        damageTextManager.update(delta);
        rouletteUI.update(delta);
    }

    public HUD getHud() {
        return hud;
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    public LevelUpUI getLevelUpUI() {
        return levelUpUI;
    }

    public DamageTextManager getDamageTextManager() {
        return damageTextManager;
    }

    public RouletteUI getRouletteUI() {
        return rouletteUI;
    }

    @Override
    public void dispose() {
        hud.dispose();
        inventoryUI.dispose();
        levelUpUI.dispose();
        damageTextManager.dispose();
        rouletteUI.dispose();
    }
}

