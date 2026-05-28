package hust.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;

import hust.adventure.HustGame;
import hust.adventure.collision.CollisionManager;
import hust.adventure.core.LootDropService;
import hust.adventure.core.ScenarioService;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.config.LevelID;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.factory.EntityFactoryImpl;
import hust.adventure.items.weapons.WeaponUpgradeService;
import hust.adventure.events.*;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.graphics.CameraManager;
import hust.adventure.graphics.GameRenderer;
import hust.adventure.graphics.LightingManager;
import hust.adventure.input.InputReader;
import hust.adventure.screens.levels.LevelBehavior;
import hust.adventure.screens.levels.LevelContext;
import hust.adventure.stats.LevelManager;
import hust.adventure.ui.UIManager;
import hust.adventure.ui.DebugUI;
import hust.adventure.ui.components.DamageIncreaseAction;
import hust.adventure.ui.components.HealAction;
import hust.adventure.ui.components.UpgradeAction;
import hust.adventure.items.Item;
import hust.adventure.items.Gear;
import hust.adventure.items.weapons.Weaponable;
import hust.adventure.ui.components.WeaponUpgradeAction;
import hust.adventure.ui.components.GearUpgradeAction;
import hust.adventure.items.ItemManager;
import hust.adventure.world.InfiniteMapRenderer;
import hust.adventure.world.MapChunk;
import hust.adventure.world.WorldManager;

/**
 * Concrete gameplay screen. Manages systems lifecycles and delegates gameplay logic to LevelBehavior.
 */
public class PlayScreen extends BaseScreen implements LevelContext, EventListener {
    private final LevelConfig config;
    private final LevelBehavior behavior;
    private PlayMode state;

    private final WorldManager worldManager;
    private final LevelManager levelManager;
    private final EntityManager entityManager;
    private final UIManager uiManager;
    private final InputReader inputReader;
    private final EntityFactory entityFactory;
    private final CollisionManager collisionManager;

    private CameraManager cameraManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    private GameRenderer gameRenderer;
    private LightingManager lightingManager;
    private Player player;

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private ShaderProgram silhouetteShader;
    private ShaderProgram discardShader;

    private ScenarioService scenarioService;
    private LootDropService lootDropService;
    private WeaponUpgradeService weaponUpgradeService;

    private static final float VIEW_WIDTH = 800f;
    private static final float VIEW_HEIGHT = 600f;

    public PlayScreen(final HustGame game, final LevelConfig config, final LevelBehavior behavior) {
        super(game);
        if (config == null) {
            throw new IllegalArgumentException("LevelConfig cannot be null");
        }
        if (behavior == null) {
            throw new IllegalArgumentException("LevelBehavior cannot be null");
        }
        this.config = config;
        this.behavior = behavior;
        this.state = PlayMode.RUNNING;

        this.worldManager = new WorldManager();
        this.levelManager = new LevelManager();
        this.entityManager = new EntityManager();
        this.collisionManager = new CollisionManager(entityManager, 64f);

        this.entityFactory = new EntityFactoryImpl(game.getAssetManager(), entityManager, collisionManager);
        this.uiManager = new UIManager();
        this.inputReader = new InputReader();
        this.lightingManager = new LightingManager();

        EventDispatcher.getInstance().addListener(EventType.LEVEL_UP, this);
        EventDispatcher.getInstance().addListener(EventType.TREASURE_OPENED, this);
        EventDispatcher.getInstance().addListener(EventType.REWARD_SELECTED, this);

        initShaders();
    }

    private void initShaders() {
        if (Gdx.files == null) {
            return;
        }
        final String vert = Gdx.files.internal("shaders/default.vert").readString();
        final String fragSil = Gdx.files.internal("shaders/silhouette.frag").readString();
        final String fragDisc = Gdx.files.internal("shaders/discard.frag").readString();

        this.silhouetteShader = new ShaderProgram(vert, fragSil);
        this.discardShader = new ShaderProgram(vert, fragDisc);
    }

    @Override
    public void show() {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(inputReader);
        Gdx.input.setInputProcessor(multiplexer);

        loadMap(config);
        initLevel();
    }

    private void loadMap(final LevelConfig config) {
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        // Ghi lại màn hiện tại để GameOverScreen biết restart vào đâu
        ProgressContext.instance.setCurrentLevelConfig(config);

        lightingManager.setAmbientLight(config.getAmbientColor());
        worldManager.loadMap(game.getAssetManager().getTiledMap(config.getMapPath()), entityFactory, entityManager,
                lightingManager);
        collisionManager.setMap(worldManager.getCurrentMap(), worldManager.getWalls());
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        final int tileW = worldManager.getCurrentMap().getProperties().get("tilewidth", Integer.class);
        final int tileH = worldManager.getCurrentMap().getProperties().get("tileheight", Integer.class);
        final float mapW = worldManager.getCurrentMap().getProperties().get("width", Integer.class) * (float) tileW;
        final float mapH = worldManager.getCurrentMap().getProperties().get("height", Integer.class) * (float) tileH;
        final float zoom = worldManager.getCurrentMap().getProperties().get("zoom", config.getZoom(), Float.class);

        if (cameraManager == null) {
            cameraManager = new CameraManager(VIEW_WIDTH, VIEW_HEIGHT);
        }
        boolean infinite = config.getLevelId().isInfinite();
        collisionManager.setInfinite(infinite);
        cameraManager.setInfinite(infinite);

        cameraManager.setZoom(zoom);
        cameraManager.setMapBounds(mapW, mapH);

        for (final MapLayer layer : worldManager.getCurrentMap().getLayers()) {
            if (layer instanceof TiledMapImageLayer) {
                final Object repeatXProp = layer.getProperties().get("repeatx");
                final Object repeatYProp = layer.getProperties().get("repeaty");

                final boolean repeatX = "1".equals(repeatXProp) || Boolean.TRUE.equals(repeatXProp);
                final boolean repeatY = "1".equals(repeatYProp) || Boolean.TRUE.equals(repeatYProp);

                if (repeatX || repeatY) {
                    final TextureRegion region = ((TiledMapImageLayer) layer).getTextureRegion();
                    if (region != null) {
                        final MapChunk chunk = new MapChunk(region);
                        final InfiniteMapRenderer infiniteRenderer = new InfiniteMapRenderer(cameraManager, chunk);
                        worldManager.initInfiniteWorld(infiniteRenderer);
                        layer.setVisible(false);
                        break;
                    }
                }
            }
        }

        if (player == null) {
            player = entityFactory.createPlayer(config.getSpawnX(), config.getSpawnY(),
                    ProgressContext.instance.getGlobalInventory(), inputReader);
            cameraManager.setTarget(player);
        } else {
            player.setX(config.getSpawnX());
            player.setY(config.getSpawnY());
            player.setCollisionManager(collisionManager);
        }

        if (scenarioService == null) {
            scenarioService = new ScenarioService(entityFactory, player, game.getAssetManager());
        }

        if (lootDropService == null) {
            lootDropService = new LootDropService(entityFactory, game.getAssetManager());
        }

        if (weaponUpgradeService == null) {
            weaponUpgradeService = new WeaponUpgradeService(player);
        }

        gameRenderer = new GameRenderer(cameraManager, entityManager, game.getSpriteBatch(), silhouetteShader,
                discardShader, uiManager.getHud(), uiManager.getInventoryUI(), uiManager.getLevelUpUI(),
                uiManager.getDamageTextManager(), uiManager.getRouletteUI(), uiManager.getDebugUI(), worldManager);

        setupLayerIndices();

        if (game.getAudioManager() != null) {
            game.getAudioManager().playMusic(getBgmForLevel(config.getLevelId()), true);
        }
    }

    private String getBgmForLevel(final LevelID id) {
        if (id == LevelID.BOSS_ROOM) {
            return "audio/music/boss_theme.wav";
        }
        return "audio/music/level_theme.wav";
    }

    private void setupLayerIndices() {
        final IntArray bg = new IntArray();
        final IntArray fg = new IntArray();

        for (int i = 0; i < worldManager.getCurrentMap().getLayers().size(); i++) {
            final MapLayer layer = worldManager.getCurrentMap().getLayers().get(i);
            if (isBackgroundLayer(layer)) {
                bg.add(i);
            } else {
                fg.add(i);
            }
        }
        backgroundLayers = bg.toArray();
        foregroundLayers = fg.toArray();
    }

    private boolean isBackgroundLayer(final MapLayer layer) {
        if (layer == null) {
            return false;
        }
        final Object isBgProp = layer.getProperties().get("isBackground");
        if (isBgProp instanceof Boolean) {
            return (Boolean) isBgProp;
        }
        if (isBgProp instanceof String) {
            return "true".equalsIgnoreCase((String) isBgProp) || "1".equals(isBgProp);
        }
        
        final String name = layer.getName();
        if (name == null) {
            return false;
        }
        return name.equals("Via He") || name.equals("Duong") || name.equals("Grass") || name.equals("Nha1")
                || name.equals("Background") || name.equals("Floor") || name.equals("Tile Layer 1");
    }

    private void initLevel() {
        behavior.init(this);
    }

    private void updateLevel(float delta) {
        behavior.update(this, delta);
    }

    private void drawLevel() {
        behavior.draw(this);
    }

    @Override
    public void render(float delta) {
        // ── Check player death ──────────────────────────────────────────────
        if (player != null && ProgressContext.instance.getHp() <= 0
                && !game.getScreenTransition().isTransitioning()) {
            game.getScreenTransition().fadeOut(new GameOverScreen(game), 0.8f);
            return;
        }

        if (state == PlayMode.RUNNING && !game.getScreenTransition().isTransitioning()) {
            entityManager.update(delta, entityFactory);

            for (GameEntity e : entityManager.getEntities()) {
                if (e instanceof BaseEnemy) {
                    ((BaseEnemy) e).handleUpdate(delta * ProgressContext.instance.getEnemyTimeScale(), player,
                            entityManager);
                }
            }

            lightingManager.update();
            collisionManager.update(worldManager.getWalls());
            checkTriggers();
            updateLevel(delta);
        }

        // uiManager.update chạy MỌI lúc (kể cả IN_UI) để nhận input từ LevelUpUI, InventoryUI
        uiManager.update(delta, player);

        if (gameRenderer != null) {
            gameRenderer.render(delta, mapRenderer, backgroundLayers, foregroundLayers, player, shapeRenderer, font,
                    lightingManager);
        }

        drawLevel();
        handleInput();
        inputReader.update();
    }

    private void handleInput() {
        if (inputReader.isInventoryJustPressed()) {
            ProgressContext.instance.setInventoryOpen(!ProgressContext.instance.isInventoryOpen());
            EventDispatcher.getInstance().dispatch(
                new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/ui_click.wav")
            );
        }
        if (inputReader.isDebugJustPressed()) {
            ProgressContext.instance.setShowDebug(!ProgressContext.instance.isShowDebug());
            if (!ProgressContext.instance.isShowDebug()) {
                if (uiManager.getDebugUI() != null) {
                    uiManager.getDebugUI().cancelSelection();
                }
                if (state == PlayMode.IN_UI) {
                    state = PlayMode.RUNNING;
                }
            }
        }
        if (inputReader.isHitboxJustPressed()) {
            ProgressContext.instance.setShowHitbox(!ProgressContext.instance.isShowHitbox());
        }

        if (ProgressContext.instance.isShowDebug() && uiManager.getDebugUI() != null) {
            DebugUI debugUI = uiManager.getDebugUI();

            if (debugUI.isActive()) {
                DebugUI.DebugOption selected = debugUI.handleSelectionInput();
                if (selected != null) {
                    executeDebugAction(debugUI.getPreviousMode(), selected);
                    state = PlayMode.RUNNING;
                } else if (!debugUI.isActive()) {
                    // Cancelled selection via ESC
                    state = PlayMode.RUNNING;
                }
                return; // Suppress other actions while selection is active
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
                ProgressContext.instance.setGodMode(!ProgressContext.instance.isGodMode());
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
                ProgressContext.instance.setFastRun(!ProgressContext.instance.isFastRun());
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
                debugUI.startSelection(DebugUI.SelectionMode.MAP);
                state = PlayMode.IN_UI;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F7)) {
                debugUI.startSelection(DebugUI.SelectionMode.ITEM);
                state = PlayMode.IN_UI;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F8)) {
                debugUI.startSelection(DebugUI.SelectionMode.MONSTER);
                state = PlayMode.IN_UI;
            }
        }
    }

    private void executeDebugAction(DebugUI.SelectionMode mode, DebugUI.DebugOption option) {
        if (mode == DebugUI.SelectionMode.MAP) {
            String targetMap = option.id;
            MapTransitionData data = new MapTransitionData(targetMap, 400f, 400f);
            GameEvent<MapTransitionData> event = new GameEvent<>(EventType.MAP_TRANSITION, data);
            EventDispatcher.getInstance().dispatch(event);
        } else if (mode == DebugUI.SelectionMode.ITEM) {
            String itemId = option.id;
            Item item = ItemManager.instance.getItem(itemId);
            if (item != null) {
                entityFactory.createItemDrop(player.getX() + 32f, player.getY(), item, Color.WHITE);
            }
        } else if (mode == DebugUI.SelectionMode.MONSTER) {
            String enemyId = option.id;
            try {
                if (enemyId.equals("libboss")) {
                    entityFactory.createLibraryBoss(player.getX() + 64f, player.getY());
                } else if (enemyId.equals("finalboss")) {
                    entityFactory.createFinalBoss(player.getX() + 64f, player.getY(),
                            game.getAssetManager().getTexture("Boss THT.png"));
                } else {
                    entityFactory.createEnemy(enemyId, player.getX() + 64f, player.getY());
                }
            } catch (Exception e) {
                Gdx.app.log("DebugMode", "Failed to spawn enemy: " + enemyId, e);
            }
        }
    }

    private void checkTriggers() {
        if (game.getScreenTransition().isTransitioning()) {
            return;
        }

        if (behavior != null && !behavior.canTransition(this)) {
            return;
        }

        for (final WorldManager.Portal portal : worldManager.getPortals()) {
            if (portal.bounds.contains(player.getX(), player.getY())) {
                MapTransitionData data = new MapTransitionData(portal.targetMap, portal.spawnX, portal.spawnY);
                GameEvent<MapTransitionData> event = new GameEvent<>(EventType.MAP_TRANSITION, data);

                EventDispatcher.getInstance().dispatch(event);
                break;
            }
        }
    }

    private Array<UpgradeAction> getLevelUpChoices(final Player player) {
        final Array<UpgradeAction> possibleChoices = new Array<>();

        // 1. Gather Weapon Choices
        final String[] weaponIds = {"whip", "magic_wand", "garlic", "bun_dau"};
        for (final String id : weaponIds) {
            Weaponable weapon = null;
            for (final Weaponable w : player.getWeaponManager().getWeapons()) {
                if (w.getId().equalsIgnoreCase(id)) {
                    weapon = w;
                    break;
                }
            }

            if (weapon == null) {
                possibleChoices.add(new WeaponUpgradeAction(id, getWeaponName(id), getWeaponLevelDescription(id, 1), true));
            } else if (weapon.getLevel() < 5) {
                final int nextLevel = weapon.getLevel() + 1;
                possibleChoices.add(new WeaponUpgradeAction(id, getWeaponName(id) + " (Cấp " + nextLevel + ")", getWeaponLevelDescription(id, nextLevel), false));
            }
        }

        // 2. Gather Gear Choices
        final String[] gearIds = {"spinach", "empty_tome", "wings", "hollow_heart", "candelabrador", "attractorb"};
        for (final String id : gearIds) {
            final Gear gear = player.getGearManager().getGear(id);
            if (gear == null) {
                possibleChoices.add(new GearUpgradeAction(id, getGearName(id), getGearLevelDescription(id, 1), true));
            } else if (gear.getLevel() < 5) {
                final int nextLevel = gear.getLevel() + 1;
                possibleChoices.add(new GearUpgradeAction(id, getGearName(id) + " (Cấp " + nextLevel + ")", getGearLevelDescription(id, nextLevel), false));
            }
        }

        // Fallbacks if nothing is available
        if (possibleChoices.size == 0) {
            possibleChoices.add(new HealAction());
            possibleChoices.add(new DamageIncreaseAction());
        }

        possibleChoices.shuffle();
        final Array<UpgradeAction> finalChoices = new Array<>();
        for (int i = 0; i < Math.min(3, possibleChoices.size); i++) {
            finalChoices.add(possibleChoices.get(i));
        }
        return finalChoices;
    }

    private String getWeaponName(final String id) {
        switch (id.toLowerCase()) {
            case "whip": return "Roi Da (Whip)";
            case "magic_wand": return "Gậy Phép (Magic Wand)";
            case "garlic": return "Tỏi Bảo Hộ (Garlic)";
            case "bun_dau": return "Bún Đậu (Knife)";
            default: return id;
        }
    }

    private String getWeaponLevelDescription(final String id, final int level) {
        switch (id.toLowerCase()) {
            case "whip":
                switch (level) {
                    case 1: return "Tấn công theo chiều ngang, xuyên qua mọi kẻ địch.";
                    case 2: return "Tấn công thêm 1 lần (ngược hướng).";
                    case 3: return "Sát thương gốc +5.";
                    case 4: return "Kích thước vùng đánh +10%, Sát thương gốc +5.";
                    case 5: return "Sát thương gốc +5.";
                }
                break;
            case "magic_wand":
                switch (level) {
                    case 1: return "Bắn tự động vào kẻ địch gần nhất.";
                    case 2: return "Bắn thêm 1 tia phép.";
                    case 3: return "Giảm hồi chiêu đi 0.2 giây.";
                    case 4: return "Bắn thêm 1 tia phép.";
                    case 5: return "Sát thương gốc +10.";
                }
                break;
            case "garlic":
                switch (level) {
                    case 1: return "Tạo vòng bảo hộ gây sát thương xung quanh.";
                    case 2: return "Phạm vi +40%, Sát thương gốc +2.";
                    case 3: return "Giảm hồi chiêu đi 0.1s, Sát thương gốc +1.";
                    case 4: return "Phạm vi +20%, Sát thương gốc +1.";
                    case 5: return "Giảm hồi chiêu đi 0.1s, Sát thương gốc +2.";
                }
                break;
            case "bun_dau":
                switch (level) {
                    case 1: return "Bắn theo hướng di chuyển cuối cùng khi bấm Space.";
                    case 2: return "Bắn thêm 1 viên đậu.";
                    case 3: return "Bắn thêm 1 viên đậu, Sát thương gốc +5.";
                    case 4: return "Bắn thêm 1 viên đậu.";
                    case 5: return "Viên đậu xuyên qua thêm 1 mục tiêu.";
                }
                break;
        }
        return "";
    }

    private String getGearName(final String id) {
        switch (id.toLowerCase()) {
            case "spinach": return "Hành Tây (Spinach)";
            case "empty_tome": return "Sách Rỗng (Empty Tome)";
            case "wings": return "Đôi Cánh (Wings)";
            case "hollow_heart": return "Trái Tim Rỗng (Hollow Heart)";
            case "candelabrador": return "Chân Nến (Candelabrador)";
            case "attractorb": return "Nam Châm (Attractorb)";
            default: return id;
        }
    }

    private String getGearLevelDescription(final String id, final int level) {
        switch (id.toLowerCase()) {
            case "spinach": return "Tăng 10% sát thương cho tất cả vũ khí (Cấp " + level + ").";
            case "empty_tome": return "Giảm 8% thời gian hồi chiêu của vũ khí (Cấp " + level + ").";
            case "wings": return "Tăng 10% tốc độ di chuyển của nhân vật (Cấp " + level + ").";
            case "hollow_heart": return "Tăng 20% lượng HP tối đa (+20 HP) (Cấp " + level + ").";
            case "candelabrador": return "Tăng 20% phạm vi tấn công của vũ khí (Cấp " + level + ").";
            case "attractorb": return "Tăng 20% phạm vi hút ngọc kinh nghiệm (Cấp " + level + ").";
        }
        return "";
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.LEVEL_UP) {
            this.state = PlayMode.IN_UI;
            final Array<UpgradeAction> choices = getLevelUpChoices(player);
            uiManager.getLevelUpUI().setChoices(choices);
            uiManager.getLevelUpUI().setOnResume(() -> {
                this.state = PlayMode.RUNNING;
            });
        } else if (event.getType() == EventType.TREASURE_OPENED) {
            this.state = PlayMode.IN_UI;
        } else if (event.getType() == EventType.REWARD_SELECTED) {
            this.state = PlayMode.RUNNING;
        }

        // Delegate to behavior if it listens to events
        if (behavior instanceof EventListener) {
            ((EventListener) behavior).onEvent(event);
        }
    }

    @Override
    public HustGame getGame() {
        return game;
    }

    @Override
    public LevelConfig getConfig() {
        return config;
    }

    @Override
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public UIManager getUIManager() {
        return uiManager;
    }

    @Override
    public InputReader getInputReader() {
        return inputReader;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(final Player player) {
        this.player = player;
    }

    @Override
    public Camera getCamera() {
        return cameraManager.getCamera();
    }

    @Override
    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    @Override
    public BitmapFont getFont() {
        return font;
    }

    @Override
    public PlayMode getState() {
        return state;
    }

    @Override
    public void setState(final PlayMode state) {
        this.state = state;
    }

    public LightingManager getLightingManager() {
        return lightingManager;
    }

    public ScenarioService getScenarioService() {
        return scenarioService;
    }

    public LootDropService getLootDropService() {
        return lootDropService;
    }

    public WeaponUpgradeService getWeaponUpgradeService() {
        return weaponUpgradeService;
    }

    @Override
    public void hide() {
        if (player != null) {
            player.saveWeaponsAndGearsToContext();
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.LEVEL_UP, this);
        EventDispatcher.getInstance().removeListener(EventType.TREASURE_OPENED, this);
        EventDispatcher.getInstance().removeListener(EventType.REWARD_SELECTED, this);

        if (behavior != null) {
            behavior.dispose(this);
        }

        levelManager.dispose();
        worldManager.dispose();
        entityManager.dispose();
        uiManager.dispose();
        if (scenarioService != null) {
            scenarioService.dispose();
        }
        if (lootDropService != null) {
            lootDropService.dispose();
        }
        if (weaponUpgradeService != null) {
            weaponUpgradeService.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        if (silhouetteShader != null) {
            silhouetteShader.dispose();
        }
        if (discardShader != null) {
            discardShader.dispose();
        }
        if (lightingManager != null) {
            lightingManager.dispose();
        }
    }
}
