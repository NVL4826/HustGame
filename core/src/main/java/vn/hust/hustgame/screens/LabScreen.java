package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.entities.*;
import vn.hust.hustgame.input.GameInputHandler;
import vn.hust.hustgame.ui.InventoryUI;

public class LabScreen extends BaseScreen {
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

    public LabScreen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        batch = game.getSpriteBatch();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            GameState.instance.isInventoryOpen = !GameState.instance.isInventoryOpen;
        }
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

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            entityManager.update(delta);
            player.setX(MathUtils.clamp(player.getX(), 0, 800));
            player.setY(MathUtils.clamp(player.getY(), 0, 600));

            Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);

            if (labCleared) {
                if (usbRect != null && pBounds.overlaps(usbRect)) {
                    GameState.instance.hasUsb = true;
                    GameState.instance.labCleared = true;
                    game.getScreenTransition().fadeOut(new BossRoomScreen(game), 1f);
                    usbRect = null;
                }
            } else if (waveActive) {
                if (stunTimer <= 0) {
                    for (int i = enemies.size() - 1; i >= 0; i--) {
                        BaseEnemy e = enemies.get(i);
                        e.update(dt, player, this);
                        if (e.isDead()) {
                            if (MathUtils.random() < 0.3f) {
                                items.add(new ItemDrop(e.getX(), e.getY(), MathUtils.random(0, 2)));
                            }
                            if (e instanceof StackOverflowEnemy && !((StackOverflowEnemy) e).isSplit) {
                                enemies.add(new StackOverflowEnemy(e.getX() - 30, e.getY(), true));
                                enemies.add(new StackOverflowEnemy(e.getX() + 30, e.getY(), true));
                            }
                            enemies.remove(i);
                        } else if (e.getBounds().overlaps(pBounds) && !(e instanceof SyntaxErrorEnemy)) {
                            GameState.instance.hp -= 10 * dt;
                        }
                    }
                } else {
                    for (BaseEnemy e : enemies) e.getBounds().setPosition(e.getX(), e.getY());
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    playerBullets.add(new Rectangle(player.getX(), player.getY(), 10, 10));
                }
                for (int i = playerBullets.size() - 1; i >= 0; i--) {
                    Rectangle b = playerBullets.get(i);
                    b.y += 400 * delta;
                    boolean hit = false;
                    for (BaseEnemy e : enemies) {
                        if (b.overlaps(e.getBounds())) {
                            e.takeDamage(10);
                            hit = true;
                            break;
                        }
                    }
                    if (hit || b.y > 600) playerBullets.remove(i);
                }

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

                if (GameState.instance.hasNao && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                    slowMotionTimer = 3f;
                }
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
                        usbRect = new Rectangle(385, 285, 30, 30);
                    }
                }
            }

            for (int i = items.size() - 1; i >= 0; i--) {
                ItemDrop item = items.get(i);
                if (item.rect.overlaps(pBounds)) {
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
            for (BaseEnemy e : enemies) {
                if (e.getBounds().overlaps(new Rectangle(player.getX() - 100, player.getY() - 100, 200, 200))) {
                    e.draw(shapeRenderer, batch, font);
                }
            }
        }
        shapeRenderer.end();

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
    public void dispose() {
        shapeRenderer.dispose();
        if (bgTexture != null) bgTexture.dispose();
        font.dispose();
        entityManager.dispose();
    }
}

class NullPointerEnemy extends BaseEnemy {
    public NullPointerEnemy(float x, float y) { super(x, y, 30, 30, 30, "NPE", Color.RED); }
    @Override public float getMaxHp() { return 30; }
    @Override public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - getX(); float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist > 0) { 
            setX(getX() + (dx/dist) * 50 * delta); 
            setY(getY() + (dy/dist) * 50 * delta); 
        }
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));
        getBounds().setPosition(getX(), getY());
    }
}

class SyntaxErrorEnemy extends BaseEnemy {
    private float fireTimer = 0;
    public SyntaxErrorEnemy(float x, float y) { super(x, y, 30, 30, 25, "SyntaxErr", Color.ORANGE); }
    @Override public float getMaxHp() { return 25; }
    @Override public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - getX(); float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist < 200) { 
            setX(getX() - (dx/dist) * 30 * delta); 
            setY(getY() - (dy/dist) * 30 * delta); 
        }
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));
        getBounds().setPosition(getX(), getY());
        fireTimer += delta;
        if (fireTimer >= 1.5f) {
            fireTimer = 0;
            s.addEnemyProjectile(new Rectangle(getX() + 10, getY(), 10, 10));
        }
    }
}

class InfiniteLoopEnemy extends BaseEnemy {
    private float vx = 70, vy = 70;
    public InfiniteLoopEnemy(float x, float y) { super(x, y, 50, 50, 80, "InfLoop", Color.PURPLE); }
    @Override public float getMaxHp() { return 80; }
    @Override public void update(float delta, Player p, LabScreen s) {
        setX(getX() + vx * delta); setY(getY() + vy * delta);
        if (getX() < 0) { setX(0); vx = -vx; }
        if (getX() > 800 - getWidth()) { setX(800 - getWidth()); vx = -vx; }
        if (getY() < 0) { setY(0); vy = -vy; }
        if (getY() > 600 - getHeight()) { setY(600 - getHeight()); vy = -vy; }
        getBounds().setPosition(getX(), getY());
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
        float dx = p.getX() - getX(); float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        if (dist > 0) { 
            setX(getX() + (dx/dist) * 35 * delta); 
            setY(getY() + (dy/dist) * 35 * delta); 
        }
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));
        getBounds().setPosition(getX(), getY());
    }
}
