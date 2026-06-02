package hust.adventure.ui;

import com.badlogic.gdx.utils.Disposable;

import hust.adventure.entities.player.Player;

/**
 * Manages UI components and their visibility.
 */
public class UIManager implements UIProvider, Disposable {
    private final HUD hud;
    private final StatusEffectsHUD statusEffectsHUD;
    private final InventoryUI inventoryUI;
    private final LevelUpUI levelUpUI;
    private final DamageTextManager damageTextManager;
    private final DebugUI debugUI;

    public UIManager() {
        this.hud = new HUD();
        this.statusEffectsHUD = new StatusEffectsHUD();
        this.inventoryUI = new InventoryUI();
        this.levelUpUI = new LevelUpUI();
        this.damageTextManager = new DamageTextManager();
        this.debugUI = new DebugUI();
    }

    public void update(final float delta, final Player player, final java.util.function.Consumer<Integer> choiceCallback) {
        damageTextManager.update(delta);

        final int keyPressed = (player != null && player.getController() != null) ? player.getController().getJustPressedNum() : 0;

        final java.util.List<InventoryItemData> itemDataList = new java.util.ArrayList<>();
        if (player != null && player.getInventory() != null) {
            for (final java.util.Map.Entry<hust.adventure.items.base.Item, Integer> entry : player.getInventory().getReadOnlyItems().entrySet()) {
                final hust.adventure.items.base.Item item = entry.getKey();
                itemDataList.add(new InventoryItemData(item.getId(), item.getName(), item.getDescription(), item.getSpritePath(), entry.getValue()));
            }
        }
        final InventoryUIData invData = new InventoryUIData(itemDataList);
        inventoryUI.update(keyPressed, invData);

        levelUpUI.update(keyPressed, choiceCallback);
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

    public DebugUI getDebugUI() {
        return debugUI;
    }

    @Override
    public void dispose() {
        hud.dispose();
        inventoryUI.dispose();
        levelUpUI.dispose();
        damageTextManager.dispose();
    }
}
