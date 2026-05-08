package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.entities.*;

public class LabScreen extends PlayScreen {
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

    static class ItemDrop {
        Rectangle rect;
        Color color;
        int type; // 0: Coffee, 1: Energy Drink, 2: Kho Ga

        ItemDrop(float x, float y, int type) {
            this.rect = new Rectangle(x, y, 15, 15);
            this.type = type;
            if (type == 0)
                color = Color.YELLOW;
            else if (type == 1)
                color = Color.GREEN;
            else
                color = Color.BROWN;
        }
    }

    public LabScreen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        loadMap("lab.tmx", 400, 100);

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
                for (int i = 0; i < 5; i++)
                    enemies.add((BaseEnemy) EntityFactory.createEnemy("null_pointer", MathUtils.random(100, 700),
                            MathUtils.random(300, 500), null));
                break;
            case 2:
                for (int i = 0; i < 3; i++)
                    enemies.add((BaseEnemy) EntityFactory.createEnemy("null_pointer", MathUtils.random(100, 700),
                            MathUtils.random(300, 500), null));
                for (int i = 0; i < 2; i++)
                    enemies.add((BaseEnemy) EntityFactory.createEnemy("syntax_error", MathUtils.random(100, 700),
                            MathUtils.random(300, 500), null));
                break;
            case 3:
                enemies.add((BaseEnemy) EntityFactory.createEnemy("infinite_loop", 400, 400, null));
                for (int i = 0; i < 4; i++)
                    enemies.add((BaseEnemy) EntityFactory.createEnemy("null_pointer", MathUtils.random(100, 700),
                            MathUtils.random(300, 500), null));
                break;
            case 4:
                for (int i = 0; i < 2; i++)
                    enemies.add((BaseEnemy) EntityFactory.createEnemy("infinite_loop", MathUtils.random(100, 700),
                            MathUtils.random(300, 500), null));
                for (int i = 0; i < 2; i++)
                    enemies.add((BaseEnemy) EntityFactory.createEnemy("syntax_error", MathUtils.random(100, 700),
                            MathUtils.random(300, 500), null));
                break;
            case 5:
                enemies.add((BaseEnemy) EntityFactory.createEnemy("stack_overflow", 400, 400, null));
                enemies.add((BaseEnemy) EntityFactory.createEnemy("null_pointer", 200, 400, null));
                enemies.add((BaseEnemy) EntityFactory.createEnemy("syntax_error", 600, 400, null));
                break;
        }
    }

    @Override
    protected void onUpdate(float delta) {
        if (GameState.instance.isInventoryOpen)
            return;

        float dt = delta;
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

        player.setX(MathUtils.clamp(player.getX(), 0, 800));
        player.setY(MathUtils.clamp(player.getY(), 0, 600));

        Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);

        if (labCleared) {
            if (usbRect != null && pBounds.overlaps(usbRect)) {
                GameState.instance.hasUsb = true;
                GameState.instance.labCleared = true;
                usbRect = null;
            }
            // Portal from lab.tmx handles spatial transition to BossRoom via
            // checkTriggers()
        } else if (waveActive) {
            updateWaveLogic(dt, pBounds);
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

        updateItemPickups(pBounds);
    }

    private void updateWaveLogic(float dt, Rectangle pBounds) {
        if (stunTimer <= 0) {
            for (int i = enemies.size() - 1; i >= 0; i--) {
                BaseEnemy e = enemies.get(i);
                e.update(dt, player, this);
                if (e.isDead()) {
                    if (MathUtils.random() < 0.3f) {
                        items.add(new ItemDrop(e.getX(), e.getY(), MathUtils.random(0, 2)));
                    }
                    if (e instanceof StackOverflowEnemy && !((StackOverflowEnemy) e).isSplit) {
                        enemies.add(new StackOverflowEnemy(e.getX() - 30, e.getY(), true, map));
                        enemies.add(new StackOverflowEnemy(e.getX() + 30, e.getY(), true, map));
                    }
                    enemies.remove(i);
                } else if (e.getBounds().overlaps(pBounds) && !(e instanceof SyntaxErrorEnemy)) {
                    GameState.instance.hp -= 10 * dt;
                }
            }
        } else {
            for (BaseEnemy e : enemies)
                e.getBounds().setPosition(e.getX(), e.getY());
        }

        if (inputHandler.isSpaceJustPressed()) {
            playerBullets.add(new Rectangle(player.getX(), player.getY(), 10, 10));
        }
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Rectangle b = playerBullets.get(i);
            b.y += 400 * Gdx.graphics.getDeltaTime();
            boolean hit = false;
            for (BaseEnemy e : enemies) {
                if (b.overlaps(e.getBounds())) {
                    e.takeDamage(10);
                    hit = true;
                    break;
                }
            }
            if (hit || b.y > 600)
                playerBullets.remove(i);
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

        if (GameState.instance.hasNao && inputHandler.isQJustPressed())
            slowMotionTimer = 3f;
        if (inputHandler.isEJustPressed() && GameState.instance.stamina >= 20) {
            stunTimer = 2f;
            GameState.instance.stamina -= 20;
        }
        if (inputHandler.isFJustPressed() && GameState.instance.stamina >= 10) {
            showEnemiesTimer = 5f;
            GameState.instance.stamina -= 10;
        }

        if (enemies.isEmpty()) {
            waveActive = false;
            waveTimer = 3f;
        }
    }

    private void updateItemPickups(Rectangle pBounds) {
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemDrop item = items.get(i);
            if (item.rect.overlaps(pBounds)) {
                if (item.type == 0)
                    player.getInventory().addItem("coffee_den", 1);
                else if (item.type == 1)
                    player.getInventory().addItem("energy_drink", 1);
                else
                    player.getInventory().addItem("kho_ga", 1);
                items.remove(i);
            }
        }
    }

    @Override
    protected void onDraw() {
        OrthographicCamera cam = gameCamera.getCamera();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);

        // Map handles background via mapRenderer in PlayScreen.render()

        // Draw player and entities
        drawEntities();

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
            for (BaseEnemy e : enemies)
                e.draw(shapeRenderer, batch, font);
        } else if (isLightsOut) {
            for (BaseEnemy e : enemies) {
                if (e.getBounds().overlaps(new Rectangle(player.getX() - 100, player.getY() - 100, 200, 200))) {
                    e.draw(shapeRenderer, batch, font);
                }
            }
        }
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Wave: " + currentWave + "/5", 350, 580);
        batch.end();
    }

    public void addEnemyProjectile(Rectangle rect) {
        enemyProjectiles.add(rect);
    }
}
