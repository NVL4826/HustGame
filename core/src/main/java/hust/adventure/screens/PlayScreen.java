package hust.adventure.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;

import hust.adventure.HustGame;
import hust.adventure.collision.CollisionManager;
import hust.adventure.core.LootDropService;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.core.data.EnemyDataLoader;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.factory.EntityFactoryImpl;
import hust.adventure.entities.player.Player;
import hust.adventure.entities.player.PlayerPersistenceService;
import hust.adventure.events.*;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.graphics.CameraManager;
import hust.adventure.graphics.GameRenderer;
import hust.adventure.graphics.LightingManager;
import hust.adventure.input.DebugInputHandler;
import hust.adventure.input.InputReader;
import hust.adventure.screens.levels.LevelBehavior;
import hust.adventure.screens.levels.LevelContext;
import hust.adventure.ui.UIManager;
import hust.adventure.ui.LevelUpChoiceBuilder;
import hust.adventure.ui.DebugOptionRegistry;
import hust.adventure.ui.components.UpgradeAction;
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
    private final DebugInputHandler debugInputHandler;

    private CameraManager cameraManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    private GameRenderer gameRenderer;
    private LightingManager lightingManager;
    private Player player;

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private ShaderProgram silhouetteShader;
    private ShaderProgram discardShader;

    private LootDropService lootDropService;

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

        final EnemyDataLoader enemyDataManager = new EnemyDataLoader("configs/enemies.json");
        DebugOptionRegistry.setEnemyDataManager(enemyDataManager);
        this.entityFactory = new EntityFactoryImpl(game.getAssetManager(), entityManager, collisionManager,
                enemyDataManager);
        this.uiManager = new UIManager();
        this.inputReader = new InputReader();
        this.lightingManager = new LightingManager();
        this.debugInputHandler = new DebugInputHandler(uiManager, entityFactory, game.getAssetManager());
        this.uiManager.getDebugUI().setInputHandler(debugInputHandler);

        EventDispatcher.getInstance().addListener(EventType.LEVEL_UP, this);

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
        worldManager.loadMap(game.getAssetManager().getTiledMap(config.getMapPath()));
        spawnMapLightingObjects();
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
        boolean infinite = config.isInfinite();
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

        // Ưu tiên spawn point từ TMX (objectgroup "Spawn"), fallback sang LevelConfig
        Vector2 tmxSpawn = worldManager.getSpawnPoint();
        float spawnX = (tmxSpawn != null) ? tmxSpawn.x : config.getSpawnX();
        float spawnY = (tmxSpawn != null) ? tmxSpawn.y : config.getSpawnY();

        if (player == null) {
            player = entityFactory.createPlayer(spawnX, spawnY, ProgressContext.instance.getGlobalInventory(),
                    inputReader);
            cameraManager.setTarget(player);
        } else {
            player.setX(spawnX);
            player.setY(spawnY);
            player.setCollisionManager(collisionManager);
        }

        if (lootDropService == null) {
            lootDropService = new LootDropService(entityFactory, game.getAssetManager());
        }

        gameRenderer = new GameRenderer(cameraManager, entityManager, game.getSpriteBatch(), silhouetteShader,
                discardShader, uiManager.getHud(), uiManager.getStatusEffectsHUD(), uiManager.getInventoryUI(),
                uiManager.getLevelUpUI(), uiManager.getDamageTextManager(), uiManager.getDebugUI(), worldManager);

        setupLayerIndices();

        if (game.getAudioManager() != null && config.getBgmPath() != null) {
            game.getAudioManager().playMusic(config.getBgmPath(), true);
        }
    }

    private void setupLayerIndices() {
        final int[][] layers = worldManager.classifyLayers();
        backgroundLayers = layers[0];
        foregroundLayers = layers[1];
    }

    /**
     * Spawns static lighting entities from the "LightingObjects" layer of the map.
     */
    private void spawnMapLightingObjects() {
        final TiledMap map = worldManager.getCurrentMap();
        if (map == null) {
            return;
        }
        final MapLayer lightLayer = map.getLayers().get("LightingObjects");
        if (lightLayer != null) {
            for (final MapObject obj : lightLayer.getObjects()) {
                final float x = obj.getProperties().get("x", 0f, Float.class);
                final float y = obj.getProperties().get("y", 0f, Float.class);
                final String name = obj.getName();

                if ("Book".equalsIgnoreCase(name)) {
                    entityFactory.createFloatingBook(x, y, lightingManager);
                } else if ("Candle".equalsIgnoreCase(name)) {
                    entityFactory.createCandle(x, y, lightingManager);
                }
            }
        }
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
        if (player != null && ProgressContext.instance.getHp() <= 0 && !game.getScreenTransition().isTransitioning()) {
            game.getScreenTransition().fadeOut(new GameOverScreen(game), 0.8f);
            return;
        }

        if (state == PlayMode.RUNNING && !game.getScreenTransition().isTransitioning()) {
            entityManager.update(delta, entityFactory);

            for (var e : entityManager.getEntities()) {
                if (e instanceof Enemy) {
                    ((Enemy) e).handleUpdate(delta * ProgressContext.instance.getEnemyTimeScale(), player,
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
            EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/ui_click.wav"));
        }
        if (inputReader.isDebugJustPressed()) {
            ProgressContext.instance.setShowDebug(!ProgressContext.instance.isShowDebug());
            if (!ProgressContext.instance.isShowDebug()) {
                debugInputHandler.cancelDebug();
                if (state == PlayMode.IN_UI) {
                    state = PlayMode.RUNNING;
                }
            }
        }
        if (inputReader.isHitboxJustPressed()) {
            ProgressContext.instance.setShowHitbox(!ProgressContext.instance.isShowHitbox());
        }

        if (ProgressContext.instance.isShowDebug()) {
            final PlayMode newState = debugInputHandler.handleDebugInput(player, state);
            if (newState != null) {
                state = newState;
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

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.LEVEL_UP) {
            this.state = PlayMode.IN_UI;
            final Array<UpgradeAction> choices = LevelUpChoiceBuilder.getLevelUpChoices(player);
            uiManager.getLevelUpUI().setChoices(choices);
            uiManager.getLevelUpUI().setOnResume(() -> {
                this.state = PlayMode.RUNNING;
            });
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

    public LootDropService getLootDropService() {
        return lootDropService;
    }

    @Override
    public void hide() {
        if (player != null) {
            PlayerPersistenceService.saveWeaponsAndGears(player);
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.LEVEL_UP, this);

        if (behavior != null) {
            behavior.dispose(this);
        }

        levelManager.dispose();
        worldManager.dispose();
        entityManager.dispose();
        uiManager.dispose();
        if (lootDropService != null) {
            lootDropService.dispose();
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
