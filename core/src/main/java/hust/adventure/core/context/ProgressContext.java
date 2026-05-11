package hust.adventure.core.context;

import hust.adventure.gamestate.GameState;
import hust.adventure.gamestate.PlayingGameState;
import hust.adventure.inventory.Inventory;

/**
 * Manages the global game data and current game flow state.
 */
public class ProgressContext {
    public static ProgressContext instance = new ProgressContext();

    // Player stats (Encapsulated)
    public float hp = 100f;
    public float maxHp = 100f;
    public float stamina = 100f;
    public float maxStamina = 100f;
    public float morale = 100f;

    public Inventory globalInventory = new Inventory();

    // Artifacts collected
    public boolean hasNao = false;
    public boolean hasUsb = false;
    public int coffeeCount = 0;

    // Progress
    public boolean libraryCleared = false;
    public boolean labCleared = false;
    private String previousScreen = "";

    // Systems
    public boolean isInventoryOpen = false;
    public boolean showDebug = false;

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

    public void reset() {
        hp = 100f;
        maxHp = 100f;
        stamina = 100f;
        maxStamina = 100f;
        morale = 100f;
        hasNao = false;
        hasUsb = false;
        coffeeCount = 0;
        libraryCleared = false;
        labCleared = false;
        setGameState(new PlayingGameState());
    }
}
