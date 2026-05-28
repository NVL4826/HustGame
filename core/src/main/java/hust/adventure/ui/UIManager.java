package hust.adventure.ui;

import com.badlogic.gdx.utils.Disposable;

import hust.adventure.entities.Player;

/**
 * Manages UI components and their visibility.
 */
public class UIManager implements UIProvider, Disposable {
    private final HUD hud;
    private final StatusEffectsHUD statusEffectsHUD;
    private final InventoryUI inventoryUI;
    private final LevelUpUI levelUpUI;
    private final DamageTextManager damageTextManager;
    private final RouletteUI rouletteUI;
    private final DebugUI debugUI;

    public UIManager() {
        this.hud = new HUD();
        this.statusEffectsHUD = new StatusEffectsHUD();
        this.inventoryUI = new InventoryUI();
        this.levelUpUI = new LevelUpUI();
        this.damageTextManager = new DamageTextManager();
        this.rouletteUI = new RouletteUI();
        this.debugUI = new DebugUI();
    }

    public void update(final float delta, final Player player) {
        damageTextManager.update(delta);
        rouletteUI.update(delta);
        inventoryUI.update(player);
        levelUpUI.update(player);
    }

    public HUD getHud() {
        return hud;
    }

    public StatusEffectsHUD getStatusEffectsHUD() {
        return statusEffectsHUD;
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

    public DebugUI getDebugUI() {
        return debugUI;
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

