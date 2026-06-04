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

import hust.adventure.core.context.GameProgressContext;
import hust.adventure.core.data.LevelConfig;
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
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.gear.Gear;
import hust.adventure.world.WorldManager;
import hust.adventure.entities.ExpGem;
import hust.adventure.items.weapons.impl.GarlicAuraWeapon;
import hust.adventure.items.weapons.impl.WhipWeapon;

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
    private final java.util.List<InventoryItemData> tempInventoryItemsList = new java.util.ArrayList<>();
    private final StringBuilder sb = new StringBuilder();
    private final GameProgressContext progressContext;
    private static final Matrix4 uiMatrix = new Matrix4();
    private final GlyphLayout layout = new GlyphLayout();
    private static final float ENEMY_NAME_OFFSET_Y = 15f;
    private static final float FLASHLIGHT_CULL_DIST_SQ = 10000f;
    private final java.util.Comparator<MapObject> yComparator = (e1, e2) -> Float.compare(e2.getY(), e1.getY());

    @lombok.Builder
    public GameRenderer(final CameraManager cameraManager, final EntityManager entityManager, final SpriteBatch batch,
            final ShaderProgram silhouetteShader, final ShaderProgram discardShader, final HUD hud,
            final StatusEffectsHUD statusEffectsHUD, final InventoryUI inventoryUI, final LevelUpUI levelUpUI,
            final DamageTextManager damageTextManager, final DebugUI debugUI, final WorldManager worldManager,
            final GameProgressContext progressContext) {
        this.cameraManager = cameraManager;
        this.entityManager = entityManager;
        this.batch = batch;
        this.silhouetteShader = silhouetteShader;
        this.discardShader = discardShader;
        this.hud = hud;
        this.statusEffectsHUD = statusEffectsHUD;
        this.inventoryUI = inventoryUI;
        this.progressContext = progressContext;
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
        drawEntities(batch);

        // Vẽ tên của quái vật
        for (final MapObject entity : entityManager.getEntities()) {
            if (entity instanceof Enemy) {
                final Enemy enemy = (Enemy) entity;
                if (!enemy.isDead()) {
                    // Flashlight culling check in lights out mode
                    if (progressContext != null && progressContext.isLightsOut()
                            && progressContext.getShowEnemiesTimer() <= 0f) {
                        final Player p = progressContext.getPlayer();
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

        // Draw weapon effects in presentation layer
        if (player != null && player.getWeaponManager() != null) {
            for (final BaseWeapon weapon : player.getWeaponManager().getWeapons()) {
                if (weapon instanceof GarlicAuraWeapon) {
                    final GarlicAuraWeapon garlic = (GarlicAuraWeapon) weapon;
                    final float px = player.getX();
                    final float py = player.getY();
                    final float baseRadius = garlic.getArea();
                    final float rotationAngle = garlic.getRotationAngle();
                    ShapeDrawUtils.drawDashedCircle(batch, px, py, baseRadius * 0.6f, rotationAngle,
                            new Color(0.85f, 0.95f, 0.75f, 0.3f));
                    ShapeDrawUtils.drawDashedCircle(batch, px, py, baseRadius * 0.8f, -rotationAngle * 0.7f,
                            new Color(0.85f, 0.95f, 0.75f, 0.25f));
                    ShapeDrawUtils.drawDashedCircle(batch, px, py, baseRadius * 1.0f, rotationAngle * 0.4f,
                            new Color(0.85f, 0.95f, 0.75f, 0.15f));
                } else if (weapon instanceof WhipWeapon) {
                    final WhipWeapon whip = (WhipWeapon) weapon;
                    if (whip.getFlashTimer() > 0) {
                        final com.badlogic.gdx.math.Rectangle hitArea = whip.getHitArea();
                        ShapeDrawUtils.drawRect(batch, hitArea.x, hitArea.y, hitArea.width, hitArea.height,
                                new Color(1, 1, 1, 0.5f));
                    }
                }
            }
        }

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
        drawEntities(batch);
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
        if (progressContext != null && progressContext.isShowHitbox()) {
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
            if (progressContext != null) {
                hudData.set(progressContext.getHp(), progressContext.getMaxHp(),
                        progressContext.getStamina(), progressContext.getMaxStamina(),
                        progressContext.getMorale(), progressContext.getExp(),
                        progressContext.getExpToNextLevel(), progressContext.getLevel(), currentTime);
            }
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
            if (progressContext != null) {
                statusData.set(progressContext.isHasNao(), progressContext.isHasUsb(),
                        progressContext.getEnemyTimeScale(), progressContext.getShowEnemiesTimer(),
                        isSpeedBoosted, isHpRegen, isConfused);
            }
            statusEffectsHUD.render(batch, font, statusData);
        }
        if (inventoryUI != null && player != null) {
            tempInventoryItemsList.clear();
            if (player.getInventory() != null) {
                for (final java.util.Map.Entry<hust.adventure.items.base.Item, Integer> entry : player.getInventory()
                        .getReadOnlyItems().entrySet()) {
                    final hust.adventure.items.base.Item item = entry.getKey();
                    tempInventoryItemsList.add(new InventoryItemData(item.getId(), item.getName(), item.getDescription(),
                            item.getSpritePath(), entry.getValue()));
                }
            }
            final InventoryUIData invData = new InventoryUIData(tempInventoryItemsList);
            inventoryUI.render(batch, shapeRenderer, font, invData, progressContext != null && progressContext.isInventoryOpen());
        }
        if (levelUpUI != null) {
            levelUpUI.render(batch, shapeRenderer, font);
        }

        if (debugUI != null) {
            if (progressContext != null && progressContext.isShowHitbox()) {
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
                            sb.setLength(0);
                            sb.append(weapon.getName()).append(" (Lv.").append(weapon.getLevel()).append(", Dmg:");
                            appendFloat(sb, weapon.getEffectiveDamage(), 1);
                            sb.append(", CD:");
                            appendFloat(sb, weapon.getCooldown(), 2);
                            sb.append("s)");
                            tempWeaponsList.add(sb.toString());
                        }
                    }

                    if (player.getGearManager() != null && player.getGearManager().getGears() != null) {
                        for (final Gear gear : player.getGearManager().getGears()) {
                            sb.setLength(0);
                            sb.append(gear.getName()).append(" (Lv.").append(gear.getLevel()).append(")");
                            tempGearsList.add(sb.toString());
                        }
                    }
                }

                String mapName = "Unknown";
                final LevelConfig currentLevelConfig = progressContext != null ? progressContext.getCurrentLevelConfig() : null;
                if (currentLevelConfig != null) {
                    mapName = currentLevelConfig.getName();
                }

                final int fps = Gdx.graphics.getFramesPerSecond();
                final int activeEntitiesCount = entityManager != null && entityManager.getEntities() != null
                        ? entityManager.getEntities().size
                        : 0;

                final Runtime runtime = Runtime.getRuntime();
                final long totalMem = runtime.totalMemory();
                final long freeMem = runtime.freeMemory();
                final long usedMemoryMB = (totalMem - freeMem) / (1024L * 1024L);
                final long totalMemoryMB = totalMem / (1024L * 1024L);

                debugInfoData.set(px, py, speed, powerMultiplier, cooldownMultiplier, areaMultiplier, magnetMultiplier,
                        stateName, mapName, fps, activeEntitiesCount, usedMemoryMB, totalMemoryMB, tempWeaponsList,
                        tempGearsList);

                debugUI.render(batch, shapeRenderer, font, debugInfoData, player,
                        progressContext != null && progressContext.isShowDebug(),
                        progressContext != null && progressContext.isShowHitbox(),
                        progressContext != null && progressContext.isGodMode(),
                        progressContext != null && progressContext.isFastRun());
            } else {
                debugUI.render(batch, shapeRenderer, font, null, player,
                        progressContext != null && progressContext.isShowDebug(),
                        false,
                        progressContext != null && progressContext.isGodMode(),
                        progressContext != null && progressContext.isFastRun());
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

    private void drawEntities(final SpriteBatch batch) {
        final com.badlogic.gdx.utils.Array<MapObject> entities = entityManager.getEntities();
        entities.sort(yComparator);

        for (final MapObject entity : entities) {
            if (entity instanceof ExpGem) {
                ShapeDrawUtils.drawRect(batch, entity.getX() - entity.getWidth() / 2f, entity.getY() - entity.getHeight() / 2f, entity.getWidth(), entity.getHeight(), Color.GREEN);
            } else if (entity instanceof Enemy && !entity.hasSprite()) {
                ShapeDrawUtils.drawRect(batch, entity.getX() - entity.getWidth() / 2f, entity.getY() - entity.getHeight() / 2f, entity.getWidth(), entity.getHeight(), Color.ORANGE);
            } else {
                entity.draw(batch);
            }
        }
    }

    private void appendFloat(final StringBuilder builder, final float val, final int decimals) {
        if (Float.isNaN(val)) {
            builder.append("NaN");
            return;
        }
        if (Float.isInfinite(val)) {
            builder.append(val > 0 ? "Infinity" : "-Infinity");
            return;
        }
        float tempVal = val;
        if (tempVal < 0) {
            builder.append('-');
            tempVal = -tempVal;
        }
        float rounder = 0.5f;
        for (int i = 0; i < decimals; i++) {
            rounder /= 10.0f;
        }
        tempVal += rounder;
        long ipart = (long) tempVal;
        builder.append(ipart);
        if (decimals > 0) {
            builder.append('.');
            float fpart = tempVal - ipart;
            for (int i = 0; i < decimals; i++) {
                fpart *= 10.0f;
                int digit = (int) fpart;
                builder.append(digit);
                fpart -= digit;
            }
        }
    }
}
