package hust.adventure.ui;

/**
 * Interface defining access to UI components. Decouples LevelContext from UIManager.
 */
public interface UIProvider {
    HUD getHud();

    InventoryUI getInventoryUI();

    LevelUpUI getLevelUpUI();

    DamageTextManager getDamageTextManager();

    RouletteUI getRouletteUI();

    DebugUI getDebugUI();
}
