package vn.hust.hustgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class LabScreen implements Screen {
    private MainGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture bgTexture;
    private BitmapFont font;

    private EntityManager entityManager;
    private Player player;
    private GameInputHandler inputHandler;

    private List<BaseEnemy> enemies;
    private List<Rectangle> enemyProjectiles;
    private List<Rectangle> playerBullets;
    private List<ItemDrop> items;

    private int currentWave = 1;
    private boolean waveActive = false;
    private float waveTimer = 2f;
    private boolean labCleared = false;
    private Rectangle usbRect;

    private boolean isLightsOut = false;

    // Skills
    private float slowMotionTimer = 0f;
    private float stunTimer = 0f;
    private float showEnemiesTimer = 0f;

    private InventoryUI inventoryUI;

    class ItemDrop {
        Rectangle rect;
        Color color;
        int type; // 0: Coffee, 1: Energy Drink, 2: Kho Ga
        ItemDrop(float x, float y, int type) {
            this.rect = new Rectangle(x, y, 15, 15);
            this.type = type;
            if (type == 0) color = Color.YELLOW;
            else if (type == 1) color = Color.GREEN;
            else color = Color.BROWN;
        }
    }

    public LabScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        inventoryUI = new InventoryUI();

        try {
            bgTexture = new Texture("Lab.jpg");
        } catch(Exception e) { bgTexture = null; }

        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        player = new Player(400, 100, GameState.instance.globalInventory, inputHandler, null);
        entityManager.addEntity(player);

        enemies = new ArrayList<>();
        enemyProjectiles = new ArrayList<>();
        playerBullets = new ArrayList<>();
        items = new ArrayList<>();

        startWave(currentWave);
    }

    private void startWave(int wave) {
        waveActive = true;
        enemies.clear();
        isLightsOut = (wave == 4);

        switch (wave) {
            case 1:
                for(int i=0; i<5; i++) enemies.add(new NullPointerEnemy(MathUtils.random(100, 700), MathUtils.random(300, 500)));
                break;
            case 2:
                for(int i=0; i<3; i++) enemies.add(new NullPointerEnemy(MathUtils.random(100, 700), MathUtils.random(300, 500)));
                for(int i=0; i<2; i++) enemies.add(new SyntaxErrorEnemy(MathUtils.random(100, 700), MathUtils.random(300, 500)));
                break;
            case 3:
                enemies.add(new InfiniteLoopEnemy(400, 400));
                for(int i=0; i<4; i++) enemies.add(new NullPointerEnemy(MathUtils.random(100, 700), MathUtils.random(300, 500)));
                break;
            case 4:
                for(int i=0; i<2; i++) enemies.add(new InfiniteLoopEnemy(MathUtils.random(100, 700), MathUtils.random(300, 500)));
                for(int i=0; i<2; i++) enemies.add(new SyntaxErrorEnemy(MathUtils.random(100, 700), MathUtils.random(300, 500)));
                break;
            case 5:
                enemies.add(new StackOverflowEnemy(400, 400, false));
                enemies.add(new NullPointerEnemy(200, 400));
                enemies.add(new SyntaxErrorEnemy(600, 400));
                break;
        }
    }

    @Override
    public void render(float delta) {
        float dt = delta;
        // Bắt phím I
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            GameState.instance.isInventoryOpen = !GameState.instance.isInventoryOpen;
        }
        if (!GameState.instance.isInventoryOpen) {
            // Handle time manipulation
            if (!GameState.instance.isInventoryOpen) {
                if (slowMotionTimer > 0) {
                    dt *= 0.3f;
                    slowMotionTimer -= delta;
                }

                if (stunTimer > 0) {
                    stunTimer -= delta;
                }

                if (showEnemiesTimer > 0) {
                    showEnemiesTimer -= delta;
                }
            }

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            entityManager.update(delta); // Player moves at normal speed
            player.setX(MathUtils.clamp(player.getX(), 0, 800));
            player.setY(MathUtils.clamp(player.getY(), 0, 600));

            Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);

            if (labCleared) {
                if (usbRect != null && pBounds.overlaps(usbRect)) {
                    GameState.instance.hasUsb = true;
                    GameState.instance.labCleared = true;
                    game.screenTransition.fadeOut(new BossRoomScreen(game), 1f);
                    usbRect = null;
                }
            } else if (waveActive) {
                if (stunTimer <= 0) {
                    for (int i = enemies.size() - 1; i >= 0; i--) {
                        BaseEnemy e = enemies.get(i);
                        e.update(dt, player, this);
                        if (e.isDead()) {
                            if (MathUtils.random() < 0.3f) {
                                items.add(new ItemDrop(e.x, e.y, MathUtils.random(0, 2)));
                            }
                            if (e instanceof StackOverflowEnemy && !((StackOverflowEnemy) e).isSplit) {
                                enemies.add(new StackOverflowEnemy(e.x - 30, e.y, true));
                                enemies.add(new StackOverflowEnemy(e.x + 30, e.y, true));
                            }
                            enemies.remove(i);
                        } else if (e.bounds.overlaps(pBounds) && !(e instanceof SyntaxErrorEnemy)) { // SyntaxError is ranged
                            GameState.instance.hp -= 10 * dt; // contact damage over time
                        }
                    }
                } else {
                    // Stunned: just update bounds
                    for (BaseEnemy e : enemies) e.bounds.setPosition(e.x, e.y);
                }

                // Player shooting
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    playerBullets.add(new Rectangle(player.getX(), player.getY(), 10, 10)); // Shooting Up for now
                }
                for (int i = playerBullets.size() - 1; i >= 0; i--) {
                    Rectangle b = playerBullets.get(i);
                    b.y += 400 * delta;
                    boolean hit = false;
                    for (BaseEnemy e : enemies) {
                        if (b.overlaps(e.bounds)) {
                            e.takeDamage(10);
                            hit = true;
                            break;
                        }
                    }
                    if (hit || b.y > 600) playerBullets.remove(i);
                }

                // Enemy projectiles
                for (int i = enemyProjectiles.size() - 1; i >= 0; i--) {
                    Rectangle p = enemyProjectiles.get(i);
                    p.y -= 200 * dt;
                    if (p.overlaps(pBounds)) {
                        GameState.instance.hp -= 10;
                        enemyProjectiles.remove(i);
                    } else if (p.y < 0) {
                        enemyProjectiles.remove(i);
                    }
                }

                // Skills
                if (GameState.instance.hasNao && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                    slowMotionTimer = 3f;
                }
                // Add USB logic later if USB is acquired before (but here we get USB at the end)
                // Stun logic can be added if we found the skill book "COMPILE FORCE", press E
                if (Gdx.input.isKeyJustPressed(Input.Keys.E) && GameState.instance.stamina >= 20) {
                    stunTimer = 2f;
                    GameState.instance.stamina -= 20;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.F) && GameState.instance.stamina >= 10) {
                    showEnemiesTimer = 5f;
                    GameState.instance.stamina -= 10;
                }

                if (enemies.isEmpty()) {
                    waveActive = false;
                    waveTimer = 3f;
                }
            } else {
                waveTimer -= delta;
                if (waveTimer <= 0) {
                    if (currentWave < 5) {
                        currentWave++;
                        startWave(currentWave);
                    } else {
                        labCleared = true;
                        usbRect = new Rectangle(385, 285, 30, 30); // Spawn USB in center
                    }
                }
            }

            // Pick up items
            for (int i = items.size() - 1; i >= 0; i--) {
                ItemDrop item = items.get(i);
                if (item.rect.overlaps(pBounds)) {
                    // THAY ĐỔI: Thêm vào túi thay vì cộng thẳng chỉ số
                    if (item.type == 0) {
                        player.getInventory().addItem("coffee_den", 1);
                    } else if (item.type == 1) {
                        player.getInventory().addItem("energy_drink", 1);
                    } else {
                        player.getInventory().addItem("kho_ga", 1);
                    }
                    items.remove(i);
                }
            }
        }

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        if (bgTexture != null) {
            batch.begin();
            batch.setColor(isLightsOut ? 0.3f : 1f, isLightsOut ? 0.3f : 1f, isLightsOut ? 0.3f : 1f, 1f);
            batch.draw(bgTexture, 0, 0, 800, 600);
            batch.setColor(Color.WHITE);
            batch.end();
        }

        batch.begin();
        entityManager.draw(batch);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (ItemDrop item : items) {
            shapeRenderer.setColor(item.color);
            shapeRenderer.rect(item.rect.x, item.rect.y, item.rect.width, item.rect.height);
        }

        if (usbRect != null) {
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.rect(usbRect.x, usbRect.y, usbRect.width, usbRect.height);
        }

        for (Rectangle b : playerBullets) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(b.x, b.y, b.width, b.height);
        }
        for (Rectangle p : enemyProjectiles) {
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(p.x, p.y, p.width, p.height);
        }

        if (!isLightsOut || showEnemiesTimer > 0) {
            for (BaseEnemy e : enemies) {
                e.draw(shapeRenderer, batch, font);
            }
        } else if (isLightsOut) {
            // Flashlight logic (only draw enemies close to player)
            for (BaseEnemy e : enemies) {
                if (e.bounds.overlaps(new Rectangle(player.getX() - 100, player.getY() - 100, 200, 200))) {
                    e.draw(shapeRenderer, batch, font);
                }
            }
        }
        shapeRenderer.end();

        // HUD
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, 580, 100, 10);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(10, 580, (GameState.instance.hp / GameState.instance.maxHp) * 100, 10);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, 565, 100, 10);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(10, 565, (GameState.instance.stamina / GameState.instance.maxStamina) * 100, 10);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Wave: " + currentWave + "/5", 350, 580);
        batch.end();

        inventoryUI.render(player, batch, shapeRenderer, font);
    }

    public void addEnemyProjectile(Rectangle rect) {
        enemyProjectiles.add(rect);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (bgTexture != null) bgTexture.dispose();
        font.dispose();
        entityManager.dispose();
    }

    private void drawInventoryUI() {
        if (!GameState.instance.isInventoryOpen) return;

        // Ép hệ tọa độ về mốc cố định 800x600 để túi đồ luôn nằm giữa màn hình
        OrthographicCamera uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, 800, 600);
        uiCam.update();

        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);

        // Bật chế độ vẽ trong suốt (Alpha Blend)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 1. Vẽ lớp nền đen mờ che toàn màn hình
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
        shapeRenderer.rect(0, 0, 800, 600);

        // 2. Vẽ khung túi đồ màu xám đậm ở giữa màn hình
        float panelW = 400;
        float panelH = 300;
        float panelX = (800 - panelW) / 2;
        float panelY = (600 - panelH) / 2;

        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f)); // Xám đậm
        shapeRenderer.rect(panelX, panelY, panelW, panelH);

        // Vẽ viền ngoài cho đẹp
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(panelX, panelY, panelX + panelW, panelY, 3);
        shapeRenderer.rectLine(panelX, panelY + panelH, panelX + panelW, panelY + panelH, 3);
        shapeRenderer.rectLine(panelX, panelY, panelX, panelY + panelH, 3);
        shapeRenderer.rectLine(panelX + panelW, panelY, panelX + panelW, panelY + panelH, 3);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 3. Vẽ chữ (Danh sách vật phẩm)
        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, "--- TUI DO CUA SINH VIEN ---", panelX + 110, panelY + panelH - 20);

        font.setColor(Color.WHITE);
        int offsetY = 60;
        java.util.Map<String, Integer> items = player.getInventory().getAllItems();

        if (items.isEmpty()) {
            font.draw(batch, "Chua co gi o day ca...", panelX + 50, panelY + panelH - offsetY);
        } else {
            for (java.util.Map.Entry<String, Integer> entry : items.entrySet()) {
                String itemName = entry.getKey();
                // Format lại tên cho đẹp
                if (itemName.equals("coffee_den")) itemName = "Ca phe den";
                if (itemName.equals("energy_drink")) itemName = "Nuoc tang luc";
                if (itemName.equals("kho_ga")) itemName = "Kho ga la chanh";

                font.draw(batch, "> " + itemName + " :  x" + entry.getValue(), panelX + 50, panelY + panelH - offsetY);
                offsetY += 30;
            }
        }

        font.setColor(Color.GRAY);
        font.draw(batch, "[Nhan 'I' de dong]", panelX + 140, panelY + 30);
        batch.end();
    }
}

class NullPointerEnemy extends BaseEnemy {
    public NullPointerEnemy(float x, float y) { super(x, y, 30, 30, 30, "NPE", Color.RED); }
    @Override public float getMaxHp() { return 30; }
    @Override public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - x; float dy = p.getY() - y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist > 0) { x += (dx/dist) * 50 * delta; y += (dy/dist) * 50 * delta; }
        x = MathUtils.clamp(x, 0, 800 - width);
        y = MathUtils.clamp(y, 0, 600 - height);
        bounds.setPosition(x, y);
    }
}

class SyntaxErrorEnemy extends BaseEnemy {
    private float fireTimer = 0;
    public SyntaxErrorEnemy(float x, float y) { super(x, y, 30, 30, 25, "SyntaxErr", Color.ORANGE); }
    @Override public float getMaxHp() { return 25; }
    @Override public void update(float delta, Player p, LabScreen s) {
        // Keep distance and shoot
        float dx = p.getX() - x; float dy = p.getY() - y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist < 200) { x -= (dx/dist) * 30 * delta; y -= (dy/dist) * 30 * delta; }
        x = MathUtils.clamp(x, 0, 800 - width);
        y = MathUtils.clamp(y, 0, 600 - height);
        bounds.setPosition(x, y);
        fireTimer += delta;
        if (fireTimer >= 1.5f) {
            fireTimer = 0;
            s.addEnemyProjectile(new Rectangle(x + 10, y, 10, 10)); // Simple straight down for now
        }
    }
}

class InfiniteLoopEnemy extends BaseEnemy {
    private float vx = 70, vy = 70;
    public InfiniteLoopEnemy(float x, float y) { super(x, y, 50, 50, 80, "InfLoop", Color.PURPLE); }
    @Override public float getMaxHp() { return 80; }
    @Override public void update(float delta, Player p, LabScreen s) {
        x += vx * delta; y += vy * delta;
        if (x < 0) { x = 0; vx = -vx; }
        if (x > 800 - width) { x = 800 - width; vx = -vx; }
        if (y < 0) { y = 0; vy = -vy; }
        if (y > 600 - height) { y = 600 - height; vy = -vy; }
        bounds.setPosition(x, y);
    }
}

class StackOverflowEnemy extends BaseEnemy {
    public boolean isSplit;
    public StackOverflowEnemy(float x, float y, boolean isSplit) {
        super(x, y, isSplit ? 40 : 80, isSplit ? 40 : 80, isSplit ? 50 : 100, "StackOver", Color.MAROON);
        this.isSplit = isSplit;
    }
    @Override public float getMaxHp() { return isSplit ? 50 : 100; }
    @Override public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - x; float dy = p.getY() - y;
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist > 0) { x += (dx/dist) * 35 * delta; y += (dy/dist) * 35 * delta; }
        x = MathUtils.clamp(x, 0, 800 - width);
        y = MathUtils.clamp(y, 0, 600 - height);
        bounds.setPosition(x, y);
    }
}
