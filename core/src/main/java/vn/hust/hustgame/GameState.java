package vn.hust.hustgame;

public class GameState {
    public static GameState instance = new GameState();

    // Player stats
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

    // Systems
    public CoffeeSystem coffeeSystem = new CoffeeSystem();
    // Thêm cờ trạng thái UI túi đồ:
    public boolean isInventoryOpen = false;

    // Screen transition
    public String previousScreen = "";

    private GameState() {}

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
        previousScreen = "";
    }
    public String currentHint = "";
    public float hintTimer = 0f;
}
