package hust.adventure.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.entities.player.Player;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.base.StatusFlag;
import hust.adventure.ui.HUD;
import hust.adventure.ui.HUDData;
import hust.adventure.ui.StatusEffectsData;
import hust.adventure.ui.StatusEffectsHUD;
import hust.adventure.ui.InventoryUI;
import hust.adventure.ui.InventoryItemData;
import hust.adventure.ui.InventoryUIData;
import hust.adventure.ui.LevelUpUI;
import hust.adventure.ui.DamageTextManager;
import hust.adventure.ui.DebugUI;
import hust.adventure.ui.DebugInfoUIData;
import hust.adventure.screens.LevelConfig;
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.gear.Gear;
import hust.adventure.world.WorldManager;

/**
 * Centralized renderer for the game, responsible for map, entities, and UI.
 */
public class GameRenderer {
    private final CameraManager cameraManager;
    private final EntityManager entityManager;
    private final OrthographicCamera uiCam;
    private final SpriteBatch batch;
    private final ShaderProgram silhouetteShader;
    private final ShaderProgram discardShader;
    private final HUD hud;
    private final StatusEffectsHUD statusEffectsHUD;
    private final InventoryUI inventoryUI;
    private final LevelUpUI levelUpUI;
    private final DamageTextManager damageTextManager;
    private final DebugUI debugUI;
    private final WorldManager worldManager;
    private final HUDData hudData;
    private final StatusEffectsData statusData;
    private final DebugInfoUIData debugInfoData;
    private final java.util.List<String> tempWeaponsList = new java.util.ArrayList<>();
    private final java.util.List<String> tempGearsList = new java.util.ArrayList<>();
    private static final Matrix4 uiMatrix = new Matrix4();
    private final GlyphLayout layout = new GlyphLayout();
    private static final float ENEMY_NAME_OFFSET_Y = 15f;
    private static final float FLASHLIGHT_CULL_DIST_SQ = 10000f;

    @lombok.Builder
    public GameRenderer(final CameraManager cameraManager, final EntityManager entityManager, final SpriteBatch batch,
            final ShaderProgram silhouetteShader, final ShaderProgram discardShader, final HUD hud,
            final StatusEffectsHUD statusEffectsHUD, final InventoryUI inventoryUI, final LevelUpUI levelUpUI,
            final DamageTextManager damageTextManager, final DebugUI debugUI, final WorldManager worldManager) {
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
        this.debugUI = debugUI;
        this.worldManager = worldManager;
        this.hudData = new HUDData(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0, 0f);
        this.statusData = new StatusEffectsData(false, false, 1f, 0f, false, false, false);
        this.debugInfoData = new DebugInfoUIData(0f, 0f, 0f, 1f, 1f, 1f, 1f, "None", "None", 0, 0, 0L, 0L, null, null);
        this.uiCam = new OrthographicCamera();
        this.uiCam.setToOrtho(false, 800, 600);
        this.uiCam.update();
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
        shapeRenderer.setProjectionMatrix(uiCam.combined);
        batch.setProjectionMatrix(uiCam.combined);
        renderUI(batch, shapeRenderer, font, player);
    }

    private void renderUI(final SpriteBatch batch, final ShapeRenderer shapeRenderer, final BitmapFont font,
            final Player player) {
        if (hud != null) {
            final float currentTime = hud.getTimeProvider() != null ? hud.getTimeProvider().getCurrentTime() : 0f;
            hudData.set(ProgressContext.instance.getHp(), ProgressContext.instance.getMaxHp(),
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
            statusData.set(ProgressContext.instance.isHasNao(),
                    ProgressContext.instance.isHasUsb(), ProgressContext.instance.getEnemyTimeScale(),
                    ProgressContext.instance.getShowEnemiesTimer(), isSpeedBoosted, isHpRegen, isConfused);
            statusEffectsHUD.render(batch, font, statusData);
        }
        if (inventoryUI != null && player != null) {
            final java.util.List<InventoryItemData> itemDataList = new java.util.ArrayList<>();
            if (player.getInventory() != null) {
                for (final java.util.Map.Entry<hust.adventure.items.base.Item, Integer> entry : player.getInventory().getReadOnlyItems().entrySet()) {
                    final hust.adventure.items.base.Item item = entry.getKey();
                    itemDataList.add(new InventoryItemData(item.getId(), item.getName(), item.getDescription(), item.getSpritePath(), entry.getValue()));
                }
            }
            final InventoryUIData invData = new InventoryUIData(itemDataList);
            inventoryUI.render(batch, shapeRenderer, font, invData);
        }
        if (levelUpUI != null) {
            levelUpUI.render(batch, shapeRenderer, font);
        }

        if (debugUI != null) {
            if (ProgressContext.instance.isShowHitbox()) {
                float px = 0f, py = 0f;
                float speed = 0f;
                float powerMultiplier = 1f;
                float cooldownMultiplier = 1f;
                float areaMultiplier = 1f;
                float magnetMultiplier = 1f;
                String stateName = "None";

                tempWeaponsList.clear();
                tempGearsList.clear();

                if (player != null) {
                    px = player.getX();
                    py = player.getY();
                    speed = player.getSpeed();
                    powerMultiplier = player.getPowerMultiplier();
                    cooldownMultiplier = player.getCooldownMultiplier();
                    areaMultiplier = player.getAreaMultiplier();
                    magnetMultiplier = player.getMagnetMultiplier();
                    stateName = player.getState() != null ? player.getState().getStateName() : "None";

                    if (player.getWeaponManager() != null && player.getWeaponManager().getWeapons() != null) {
                        for (final BaseWeapon weapon : player.getWeaponManager().getWeapons()) {
                            tempWeaponsList.add(String.format("%s (Lv.%d, Dmg:%.1f, CD:%.2fs)",
                                    weapon.getName(), weapon.getLevel(), weapon.getEffectiveDamage(), weapon.getCooldown()));
                        }
                    }

                    if (player.getGearManager() != null && player.getGearManager().getGears() != null) {
                        for (final Gear gear : player.getGearManager().getGears()) {
                            tempGearsList.add(String.format("%s (Lv.%d)", gear.getName(), gear.getLevel()));
                        }
                    }
                }

                String mapName = "Unknown";
                final LevelConfig currentLevelConfig = ProgressContext.instance.getCurrentLevelConfig();
                if (currentLevelConfig != null) {
                    mapName = currentLevelConfig.getName();
                }

                final int fps = Gdx.graphics.getFramesPerSecond();
                final int activeEntitiesCount = entityManager != null && entityManager.getEntities() != null ? entityManager.getEntities().size : 0;

                final Runtime runtime = Runtime.getRuntime();
                final long totalMem = runtime.totalMemory();
                final long freeMem = runtime.freeMemory();
                final long usedMemoryMB = (totalMem - freeMem) / (1024L * 1024L);
                final long totalMemoryMB = totalMem / (1024L * 1024L);

                debugInfoData.set(px, py, speed, powerMultiplier, cooldownMultiplier, areaMultiplier, magnetMultiplier,
                        stateName, mapName, fps, activeEntitiesCount, usedMemoryMB, totalMemoryMB,
                        tempWeaponsList, tempGearsList);

                debugUI.render(batch, shapeRenderer, font, debugInfoData, player);
            } else {
                debugUI.render(batch, shapeRenderer, font, null, player);
            }
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
