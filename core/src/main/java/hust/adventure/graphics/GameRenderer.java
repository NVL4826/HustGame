package hust.adventure.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.status.StatusFlag;
import hust.adventure.ui.HUD;
import hust.adventure.ui.HUDData;
import hust.adventure.ui.StatusEffectsData;
import hust.adventure.ui.StatusEffectsHUD;
import hust.adventure.ui.InventoryUI;
import hust.adventure.ui.LevelUpUI;
import hust.adventure.ui.DamageTextManager;
import hust.adventure.ui.RouletteUI;
import hust.adventure.ui.DebugUI;
import hust.adventure.world.WorldManager;

/**
 * Centralized renderer for the game, responsible for map, entities, and UI.
 */
public class GameRenderer {
    private final CameraManager cameraManager;
    private final EntityManager entityManager;
    private final SpriteBatch batch;
    private final ShaderProgram silhouetteShader;
    private final ShaderProgram discardShader;
    private final HUD hud;
    private final StatusEffectsHUD statusEffectsHUD;
    private final InventoryUI inventoryUI;
    private final LevelUpUI levelUpUI;
    private final DamageTextManager damageTextManager;
    private final RouletteUI rouletteUI;
    private final DebugUI debugUI;
    private final WorldManager worldManager;
    private static final Matrix4 uiMatrix = new Matrix4();
    private final GlyphLayout layout = new GlyphLayout();
    private static final float ENEMY_NAME_OFFSET_Y = 15f;
    private static final float FLASHLIGHT_CULL_DIST_SQ = 10000f;

    public GameRenderer(final CameraManager cameraManager, final EntityManager entityManager, final SpriteBatch batch,
            final ShaderProgram silhouetteShader, final ShaderProgram discardShader, final HUD hud,
            final StatusEffectsHUD statusEffectsHUD, final InventoryUI inventoryUI, final LevelUpUI levelUpUI,
            final DamageTextManager damageTextManager, final RouletteUI rouletteUI, final DebugUI debugUI,
            final WorldManager worldManager) {
        this.cameraManager = cameraManager;
        this.entityManager = entityManager;
        this.batch = batch;
        this.silhouetteShader = silhouetteShader;
        this.discardShader = discardShader;
        this.hud = hud;
        this.statusEffectsHUD = statusEffectsHUD;
        this.inventoryUI = inventoryUI;
        this.levelUpUI = levelUpUI;
        this.damageTextManager = damageTextManager;
        this.rouletteUI = rouletteUI;
        this.debugUI = debugUI;
        this.worldManager = worldManager;
    }

    /**
     * Main render pass.
     */
    public void render(final float delta, final OrthogonalTiledMapRenderer mapRenderer, final int[] backgroundLayers,
            final int[] foregroundLayers, final Player player, final ShapeRenderer shapeRenderer, final BitmapFont font,
            final LightingManager lightingManager) {

        if (cameraManager != null) {
            cameraManager.update();
        }

        if (mapRenderer == null) {
            return;
        }

        // 0. Xóa màu nền, Depth Buffer và Stencil Buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        mapRenderer.setView(cameraManager.getCamera());

        // 0.5. Vẽ Infinite Background
        if (worldManager != null) {
            batch.setProjectionMatrix(cameraManager.getCamera().combined);
            batch.begin();
            worldManager.renderBackground(batch);
            batch.end();
        }

        // 1. Vẽ Background map
        mapRenderer.render(backgroundLayers);

        // 2. Vẽ Nhân vật và thực thể (Y-sorting)
        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();
        entityManager.draw(batch);

        // Vẽ tên của quái vật
        for (final MapObject entity : entityManager.getEntities()) {
            if (entity instanceof Enemy) {
                final Enemy enemy = (Enemy) entity;
                if (!enemy.isDead()) {
                    // Flashlight culling check in lights out mode
                    if (ProgressContext.instance.isLightsOut()
                            && ProgressContext.instance.getShowEnemiesTimer() <= 0f) {
                        final Player p = ProgressContext.instance.getPlayer();
                        if (p != null) {
                            final float dx = enemy.getX() - p.getX();
                            final float dy = enemy.getY() - p.getY();
                            if ((dx * dx + dy * dy) > FLASHLIGHT_CULL_DIST_SQ) {
                                continue;
                            }
                        }
                    }

                    if (!enemy.hasSprite()) {
                        font.setColor(enemy.getColor());
                        layout.setText(font, enemy.getName());
                        final float tx = enemy.getX() - layout.width / 2f;
                        final float ty = enemy.getY() + enemy.getHeight() / 2f + ENEMY_NAME_OFFSET_Y;
                        font.draw(batch, enemy.getName(), tx, ty);
                    }
                }
            }
        }
        font.setColor(Color.WHITE); // Reset font color

        batch.end();

        // 3. Stencil Buffer cho Foreground
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glStencilMask(0xFF);

        mapRenderer.getBatch().setShader(discardShader);
        mapRenderer.render(foregroundLayers);
        mapRenderer.getBatch().setShader(null);

        // 4. Vẽ Silhouette nhân vật
        Gdx.gl.glStencilMask(0x00);
        Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 1, 0xFF);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setShader(silhouetteShader);
        batch.begin();
        entityManager.draw(batch);
        batch.end();
        batch.setShader(null);

        // Phục hồi OpenGL
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 4.5 Render Damage Text
        if (damageTextManager != null) {
            batch.setProjectionMatrix(cameraManager.getCamera().combined);
            batch.begin();
            damageTextManager.render(batch, font);
            batch.end();
        }

        // 5. Thêm lớp chiếu sáng (nếu có)
        if (lightingManager != null) {
            lightingManager.render(cameraManager.getCamera());
        }

        // 5.5. Render Debug Hitboxes
        if (ProgressContext.instance.isShowHitbox()) {
            renderDebugHitboxes(shapeRenderer);
        }

        // 6. Render UI
        renderUI(batch, shapeRenderer, font, player);
    }

    private void renderUI(final SpriteBatch batch, final ShapeRenderer shapeRenderer, final BitmapFont font,
            final Player player) {
        if (hud != null) {
            final float currentTime = hud.getTimeProvider() != null ? hud.getTimeProvider().getCurrentTime() : 0f;
            final HUDData hudData = new HUDData(ProgressContext.instance.getHp(), ProgressContext.instance.getMaxHp(),
                    ProgressContext.instance.getStamina(), ProgressContext.instance.getMaxStamina(),
                    ProgressContext.instance.getMorale(), ProgressContext.instance.getExp(),
                    ProgressContext.instance.getExpToNextLevel(), ProgressContext.instance.getLevel(), currentTime);
            hud.render(batch, shapeRenderer, font, hudData);
        }
        if (statusEffectsHUD != null) {
            boolean isSpeedBoosted = false;
            boolean isHpRegen = false;
            boolean isConfused = false;
            if (player != null) {
                isSpeedBoosted = player.hasStatus(StatusFlag.SPEED_BOOSTED);
                isHpRegen = player.hasStatus(StatusFlag.REGEN_HP);
                isConfused = player.hasStatus(StatusFlag.CONFUSED);
            }
            final StatusEffectsData statusData = new StatusEffectsData(ProgressContext.instance.isHasNao(),
                    ProgressContext.instance.isHasUsb(), ProgressContext.instance.getEnemyTimeScale(),
                    ProgressContext.instance.getShowEnemiesTimer(), isSpeedBoosted, isHpRegen, isConfused);
            statusEffectsHUD.render(batch, font, statusData);
        }
        if (inventoryUI != null && player != null) {
            inventoryUI.render(player, batch, shapeRenderer, font);
        }
        if (levelUpUI != null && player != null) {
            levelUpUI.render(player, batch, shapeRenderer, font);
        }
        if (rouletteUI != null) {
            rouletteUI.render(batch, shapeRenderer, font);
        }
        if (debugUI != null) {
            debugUI.render(batch, shapeRenderer, font);
        }

        if (player != null && player.hasStatus(StatusFlag.CONFUSED)) {
            drawVignette(shapeRenderer);
        }
    }

    private void renderDebugHitboxes(final ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(cameraManager.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (final MapObject entity : entityManager.getEntities()) {
            entity.drawHitbox(shapeRenderer);
        }
        shapeRenderer.end();
    }

    private void drawVignette(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Use a UI projection for the overlay
        uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.setProjectionMatrix(uiMatrix);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.4f, 0.2f, 0f, 0.2f)); // Brown tint
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public CameraManager getcameraManager() {
        return cameraManager;
    }
}
