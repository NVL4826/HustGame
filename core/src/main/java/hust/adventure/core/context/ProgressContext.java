package hust.adventure.core.context;

import hust.adventure.gamestate.GameState;
import hust.adventure.gamestate.PlayingGameState;
import hust.adventure.inventory.Inventory;
import hust.adventure.entities.Player;
import hust.adventure.core.config.LevelConfig;
import java.util.Map;
import java.util.HashMap;

/**
 * Manages the global game data and current game flow state.
 */
public class ProgressContext {
    public static ProgressContext instance = new ProgressContext();

    // Player stats (Encapsulated)
    private float hp = 100f;
    private float maxHp = 100f;
    private float stamina = 100f;
    private float maxStamina = 100f;
    private float morale = 100f;
    private int level = 1;
    private float exp = 0;
    private float expToNextLevel = 100f;

    private Inventory globalInventory = new Inventory();

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
    private boolean showDebug = false;
    private boolean showHitbox = false;
    private boolean godMode = false;
    private boolean fastRun = false;
    // Global Spells / Timers / Context
    private float enemyTimeScale = 1.0f;
    private boolean lightsOut = false;
    private float showEnemiesTimer = 0f;
    private boolean enemyBlinkVisible = true; // dùng cho hiệu ứng blink ở lab phase 3
    private LevelConfig currentLevelConfig = null; // màn hiện tại
    private float damageMultiplier = 1.0f; // tăng qua level up, reset khi game over
    private Player player = null;

    private final Map<String, Integer> weaponLevels = new HashMap<>();
    private final Map<String, Integer> gearLevels = new HashMap<>();

    // State Pattern
    private GameState currentState;

    private ProgressContext() {
        this.currentState = new PlayingGameState();
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
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(float maxHp) {
        this.maxHp = maxHp;
    }

    public float getStamina() {
        return stamina;
    }

    public void setStamina(float stamina) {
        this.stamina = stamina;
    }

    public float getMaxStamina() {
        return maxStamina;
    }

    public void setMaxStamina(float maxStamina) {
        this.maxStamina = maxStamina;
    }

    public float getMorale() {
        return morale;
    }

    public void setMorale(float morale) {
        this.morale = morale;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getExp() {
        return exp;
    }

    public void setExp(float exp) {
        this.exp = exp;
    }

    public float getExpToNextLevel() {
        return expToNextLevel;
    }

    public void setExpToNextLevel(float expToNextLevel) {
        this.expToNextLevel = expToNextLevel;
    }

    public Inventory getGlobalInventory() {
        return globalInventory;
    }

    public void setGlobalInventory(Inventory globalInventory) {
        this.globalInventory = globalInventory;
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
        return showDebug;
    }

    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    public boolean isGodMode() {
        return godMode;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public boolean isFastRun() {
        return fastRun;
    }

    public void setFastRun(boolean fastRun) {
        this.fastRun = fastRun;
    }

    public boolean isShowHitbox() {
        return showHitbox;
    }

    public void setShowHitbox(boolean showHitbox) {
        this.showHitbox = showHitbox;
    }

    public float getEnemyTimeScale() {
        return enemyTimeScale;
    }

    public void setEnemyTimeScale(float enemyTimeScale) {
        this.enemyTimeScale = enemyTimeScale;
    }

    public boolean isLightsOut() {
        return lightsOut;
    }

    public void setLightsOut(boolean lightsOut) {
        this.lightsOut = lightsOut;
    }

    public float getShowEnemiesTimer() {
        return showEnemiesTimer;
    }

    public void setShowEnemiesTimer(float showEnemiesTimer) {
        this.showEnemiesTimer = showEnemiesTimer;
    }

    public boolean isEnemyBlinkVisible() {
        return enemyBlinkVisible;
    }

    public void setEnemyBlinkVisible(boolean enemyBlinkVisible) {
        this.enemyBlinkVisible = enemyBlinkVisible;
    }

    public LevelConfig getCurrentLevelConfig() {
        return currentLevelConfig;
    }

    public void setCurrentLevelConfig(LevelConfig currentLevelConfig) {
        this.currentLevelConfig = currentLevelConfig;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
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
        hp = 100f;
        maxHp = 100f;
        stamina = 100f;
        maxStamina = 100f;
        morale = 100f;
        level = 1;
        exp = 0;
        expToNextLevel = 100f;
        hasNao = false;
        hasUsb = false;
        coffeeCount = 0;
        libraryCleared = false;
        labCleared = false;
        showHitbox = false;
        godMode = false;
        fastRun = false;
        enemyTimeScale = 1.0f;
        lightsOut = false;
        showEnemiesTimer = 0f;
        enemyBlinkVisible = true;
        damageMultiplier = 1.0f;
        player = null;
        weaponLevels.clear();
        gearLevels.clear();
        setGameState(new PlayingGameState());
    }
}
