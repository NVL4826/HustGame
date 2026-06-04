package hust.adventure.core.context;

import hust.adventure.gamestate.GameState;
import hust.adventure.gamestate.PlayingGameState;
import hust.adventure.inventory.Inventory;
import hust.adventure.core.data.LevelConfig;
import hust.adventure.entities.player.Player;
import hust.adventure.events.EventListener;
import hust.adventure.events.GameEvent;
import hust.adventure.events.EventType;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.items.base.ItemManager;

import java.util.Map;
import java.util.HashMap;

/**
 * Manages the global game data and current game flow state. Refactored to delegate domain-specific states to
 * PlayerStats, DebugContext, and SpellState.
 */
public class ProgressContext implements GameProgressContext, EventListener {
    // Domain sub-contexts (SRP Separation)
    private final PlayerStats playerStats = new PlayerStats();
    private final DebugContext debugContext = new DebugContext();
    private final SpellState spellState = new SpellState();

    private final ItemManager itemManager;
    private final Inventory globalInventory;

    // Artifacts collected
    private boolean hasNao = false;
    private boolean hasUsb = false;
    private int coffeeCount = 0;

    // Progress
    private boolean libraryCleared = false;
    private boolean labCleared = false;
    private String previousScreen = "";

    // Systems
    private boolean isInventoryOpen = false;
    private LevelConfig currentLevelConfig = null; // Current level configuration
    private Player player = null;

    private final Map<String, Integer> weaponLevels = new HashMap<>();
    private final Map<String, Integer> gearLevels = new HashMap<>();

    // State Pattern
    private GameState currentState;

    public ProgressContext(final ItemManager itemManager) {
        this.itemManager = itemManager;
        this.globalInventory = new Inventory(itemManager);
        this.currentState = new PlayingGameState();
    }

    /**
     * Gets the player stats sub-context.
     *
     * @return the PlayerStats instance.
     */
    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    /**
     * Gets the debug options sub-context.
     *
     * @return the DebugContext instance.
     */
    public DebugContext getDebugContext() {
        return debugContext;
    }

    /**
     * Gets the spell states and timers sub-context.
     *
     * @return the SpellState instance.
     */
    public SpellState getSpellState() {
        return spellState;
    }

    public void update(float delta) {
        if (currentState != null) {
            currentState.update(this, delta);
        }
    }

    public void setGameState(GameState newState) {
        if (this.currentState != null) {
            this.currentState.exit(this);
        }
        this.currentState = newState;
        if (this.currentState != null) {
            this.currentState.enter(this);
        }
    }

    public String getPreviousScreen() {
        return previousScreen;
    }

    public void setPreviousScreen(String previousScreen) {
        this.previousScreen = previousScreen;
    }

    public float getHp() {
        return playerStats.getHp();
    }

    public void setHp(float hp) {
        playerStats.setHp(hp);
    }

    public float getMaxHp() {
        return playerStats.getMaxHp();
    }

    public void setMaxHp(float maxHp) {
        playerStats.setMaxHp(maxHp);
    }

    public float getStamina() {
        return playerStats.getStamina();
    }

    public void setStamina(float stamina) {
        playerStats.setStamina(stamina);
    }

    public float getMaxStamina() {
        return playerStats.getMaxStamina();
    }

    public void setMaxStamina(float maxStamina) {
        playerStats.setMaxStamina(maxStamina);
    }

    public float getMorale() {
        return playerStats.getMorale();
    }

    public void setMorale(float morale) {
        playerStats.setMorale(morale);
    }

    public int getLevel() {
        return playerStats.getLevel();
    }

    public void setLevel(int level) {
        playerStats.setLevel(level);
    }

    public float getExp() {
        return playerStats.getExp();
    }

    public void setExp(float exp) {
        playerStats.setExp(exp);
    }

    public float getExpToNextLevel() {
        return playerStats.getExpToNextLevel();
    }

    public void setExpToNextLevel(float expToNextLevel) {
        playerStats.setExpToNextLevel(expToNextLevel);
    }

    public Inventory getGlobalInventory() {
        return globalInventory;
    }

    public boolean isHasNao() {
        return hasNao;
    }

    public void setHasNao(boolean hasNao) {
        this.hasNao = hasNao;
    }

    public boolean isHasUsb() {
        return hasUsb;
    }

    public void setHasUsb(boolean hasUsb) {
        this.hasUsb = hasUsb;
    }

    public int getCoffeeCount() {
        return coffeeCount;
    }

    public void setCoffeeCount(int coffeeCount) {
        this.coffeeCount = coffeeCount;
    }

    public boolean isLibraryCleared() {
        return libraryCleared;
    }

    public void setLibraryCleared(boolean libraryCleared) {
        this.libraryCleared = libraryCleared;
    }

    public boolean isLabCleared() {
        return labCleared;
    }

    public void setLabCleared(boolean labCleared) {
        this.labCleared = labCleared;
    }

    public boolean isInventoryOpen() {
        return isInventoryOpen;
    }

    public void setInventoryOpen(boolean inventoryOpen) {
        isInventoryOpen = inventoryOpen;
    }

    public boolean isShowDebug() {
        return debugContext.isShowDebug();
    }

    public void setShowDebug(boolean showDebug) {
        debugContext.setShowDebug(showDebug);
    }

    public boolean isGodMode() {
        return debugContext.isGodMode();
    }

    public void setGodMode(boolean godMode) {
        debugContext.setGodMode(godMode);
    }

    public boolean isFastRun() {
        return debugContext.isFastRun();
    }

    public void setFastRun(boolean fastRun) {
        debugContext.setFastRun(fastRun);
    }

    public boolean isShowHitbox() {
        return debugContext.isShowHitbox();
    }

    public void setShowHitbox(boolean showHitbox) {
        debugContext.setShowHitbox(showHitbox);
    }

    public float getEnemyTimeScale() {
        return spellState.getEnemyTimeScale();
    }

    public void setEnemyTimeScale(float enemyTimeScale) {
        spellState.setEnemyTimeScale(enemyTimeScale);
    }

    public boolean isLightsOut() {
        return spellState.isLightsOut();
    }

    public void setLightsOut(boolean lightsOut) {
        spellState.setLightsOut(lightsOut);
    }

    public float getShowEnemiesTimer() {
        return spellState.getShowEnemiesTimer();
    }

    public void setShowEnemiesTimer(float showEnemiesTimer) {
        spellState.setShowEnemiesTimer(showEnemiesTimer);
    }

    public boolean isEnemyBlinkVisible() {
        return spellState.isEnemyBlinkVisible();
    }

    public void setEnemyBlinkVisible(boolean enemyBlinkVisible) {
        spellState.setEnemyBlinkVisible(enemyBlinkVisible);
    }

    public LevelConfig getCurrentLevelConfig() {
        return currentLevelConfig;
    }

    public void setCurrentLevelConfig(LevelConfig currentLevelConfig) {
        this.currentLevelConfig = currentLevelConfig;
    }

    public float getDamageMultiplier() {
        return playerStats.getDamageMultiplier();
    }

    public void setDamageMultiplier(float damageMultiplier) {
        playerStats.setDamageMultiplier(damageMultiplier);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Map<String, Integer> getWeaponLevels() {
        return weaponLevels;
    }

    public Map<String, Integer> getGearLevels() {
        return gearLevels;
    }

    public void reset() {
        playerStats.reset();
        debugContext.reset();
        spellState.reset();
        hasNao = false;
        hasUsb = false;
        coffeeCount = 0;
        libraryCleared = false;
        labCleared = false;
        player = null;
        weaponLevels.clear();
        gearLevels.clear();
        setGameState(new PlayingGameState());
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.ITEM_PICKED_UP) {
            final ItemPickedUpEvent data = (ItemPickedUpEvent) event.getData();
            if (data != null && data.getItem() != null) {
                final String itemId = data.getItem().getId();
                if ("brain".equals(itemId)) {
                    hasNao = true;
                    libraryCleared = true;
                } else if ("usb".equals(itemId)) {
                    hasUsb = true;
                    labCleared = true;
                }
            }
        } else if (event.getType() == EventType.ITEM_USED) {
            final String itemId = (String) event.getData();
            if ("coffee".equals(itemId)) {
                coffeeCount++;
            }
        }
    }

    @Override
    public ItemManager getItemManager() {
        return itemManager;
    }
}
