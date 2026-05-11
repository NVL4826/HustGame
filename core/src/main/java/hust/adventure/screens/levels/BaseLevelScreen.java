package hust.adventure.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Array;

import hust.adventure.world.InfiniteMapRenderer;
import hust.adventure.world.MapChunk;
import hust.adventure.utils.GamePools;
import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.context.ProgressContext;
import hust.adventure.core.ScenarioService;
import hust.adventure.core.LootDropService;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.factory.EntityFactoryImpl;
import hust.adventure.events.*;
import hust.adventure.gamestate.PlayMode;
import hust.adventure.graphics.CameraManager;
import hust.adventure.graphics.GameRenderer;
import hust.adventure.input.InputReader;
import hust.adventure.entities.weapons.WeaponUpgradeService;
import hust.adventure.screens.BaseScreen;
import hust.adventure.collision.CollisionManager;
import hust.adventure.ui.UIManager;
import hust.adventure.world.WorldManager;
import hust.adventure.graphics.LightingManager;
import hust.adventure.wave.WaveManager;
import hust.adventure.wave.WaveConfig;
import hust.adventure.stats.LevelManager;
import hust.adventure.ui.components.HealAction;
import hust.adventure.ui.components.DamageIncreaseAction;
import hust.adventure.ui.components.UpgradeAction;

/**
 * Base class for all gameplay levels. Manages core systems like entities, collision, and rendering.
 */
public abstract class BaseLevelScreen extends BaseScreen implements EventListener {
    protected final LevelConfig config;
    protected PlayMode state;

    protected final WorldManager worldManager;
    protected final LevelManager levelManager;
    protected final EntityManager entityManager;
    protected final UIManager uiManager;
    protected final InputReader inputReader;
    protected final EntityFactory entityFactory;
    protected final CollisionManager collisionManager;
    protected final WaveManager waveManager;

    protected CameraManager cameraManager;
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected GameRenderer gameRenderer;
    protected LightingManager lightingManager;
    protected Player player;

    protected int[] backgroundLayers;
    protected int[] foregroundLayers;

    private ShaderProgram silhouetteShader;
    private ShaderProgram discardShader;

    protected static final float VIEW_WIDTH = 800f;
    protected static final float VIEW_HEIGHT = 600f;

    public BaseLevelScreen(final HustGame game, final LevelConfig config) {
        super(game);
        this.config = config;
        this.state = PlayMode.RUNNING;

        this.worldManager = new WorldManager();
        this.levelManager = new LevelManager();
        this.entityManager = new EntityManager();
        this.collisionManager = new CollisionManager(entityManager, 64f);

        this.entityFactory = new EntityFactoryImpl(game.getAssetManager(), entityManager, collisionManager);
        this.uiManager = new UIManager();
        this.inputReader = new InputReader();
        this.lightingManager = new LightingManager();

        WaveConfig waveConfig = game.getAssetManager().loadWaveConfig("configs/waves.json");
        this.waveManager = new WaveManager(waveConfig, entityFactory);
        this.uiManager.getHud().setTimeProvider(this.waveManager);

        EventDispatcher.getInstance().addListener(EventType.LEVEL_UP, this);
        EventDispatcher.getInstance().addListener(EventType.TREASURE_OPENED, this);
        EventDispatcher.getInstance().addListener(EventType.REWARD_SELECTED, this);

        initShaders();
    }

    private void initShaders() {
        if (Gdx.files == null)
            return;
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

    protected void loadMap(final LevelConfig config) {
        if (mapRenderer != null)
            mapRenderer.dispose();

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

        // Extract the background texture from the TiledMapImageLayer
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

                        // Hide the layer so the normal OrthogonalTiledMapRenderer doesn't draw it
                        layer.setVisible(false);
                        break; // Only support one infinite background for now
                    }
                }
            }
        }

        if (player == null) {
            player = entityFactory.createPlayer(config.getSpawnX(), config.getSpawnY(),
                    ProgressContext.instance.globalInventory, inputReader);
            cameraManager.setTarget(player);
        } else {
            player.setCollisionContext(collisionManager, config.getSpawnX(), config.getSpawnY());
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
                uiManager.getDamageTextManager(), uiManager.getRouletteUI(), worldManager);

        setupLayerIndices();
    }

    private void setupLayerIndices() {
        final IntArray bg = new IntArray();
        final IntArray fg = new IntArray();

        for (int i = 0; i < worldManager.getCurrentMap().getLayers().size(); i++) {
            final String name = worldManager.getCurrentMap().getLayers().get(i).getName();
            if (isBackgroundLayer(name)) {
                bg.add(i);
            } else {
                fg.add(i);
            }
        }
        backgroundLayers = bg.toArray();
        foregroundLayers = fg.toArray();
    }

    private boolean isBackgroundLayer(final String name) {
        return name.equals("Via He") || name.equals("Duong") || name.equals("Grass") || name.equals("Nha1")
                || name.equals("Background") || name.equals("Floor") || name.equals("Tile Layer 1");
    }

    @Override
    public void render(float delta) {
        if (state == PlayMode.RUNNING && !game.getScreenTransition().isTransitioning()) {
            entityManager.update(delta, entityFactory);

            // Update enemy AI logic
            for (GameEntity e : entityManager.getEntities()) {
                if (e instanceof BaseEnemy) {
                    ((BaseEnemy) e).handleUpdate(delta, player, entityManager);
                }
            }

            lightingManager.update();
            collisionManager.update(worldManager.getWalls());
            waveManager.update(delta, cameraManager.getCamera());
            checkTriggers();
            uiManager.update(delta);
            updateLevel(delta);
        }

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
            ProgressContext.instance.isInventoryOpen = !ProgressContext.instance.isInventoryOpen;
        }
        if (inputReader.isDebugJustPressed()) {
            ProgressContext.instance.showDebug = !ProgressContext.instance.showDebug;
        }
    }

    private void checkTriggers() {
        if (game.getScreenTransition().isTransitioning())
            return;

        for (final WorldManager.Portal portal : worldManager.getPortals()) {
            if (portal.bounds.contains(player.getX(), player.getY())) {
                MapTransitionData data = GamePools.obtain(MapTransitionData.class);
                data.init(portal.targetMap, portal.spawnX, portal.spawnY);

                GameEvent<MapTransitionData> event = GamePools.obtainEvent();
                event.init(EventType.MAP_TRANSITION, data);

                EventDispatcher.getInstance().dispatch(event);
                break;
            }
        }
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.LEVEL_UP) {
            this.state = PlayMode.IN_UI;
            Array<UpgradeAction> choices = new Array<>();
            choices.add(new DamageIncreaseAction());
            choices.add(new HealAction());
            uiManager.getLevelUpUI().setChoices(choices);
            uiManager.getLevelUpUI().setOnResume(() -> {
                this.state = PlayMode.RUNNING;
            });
        } else if (event.getType() == EventType.TREASURE_OPENED) {
            this.state = PlayMode.IN_UI;
        } else if (event.getType() == EventType.REWARD_SELECTED) {
            this.state = PlayMode.RUNNING;
        }
    }

    protected abstract void initLevel();

    protected abstract void updateLevel(float delta);

    protected abstract void drawLevel();

    public OrthographicCamera getCamera() {
        return cameraManager.getCamera();
    }

    public LevelConfig getConfig() {
        return config;
    }

    public void setPlayMode(PlayMode mode) {
        this.state = mode;
    }

    protected ScenarioService scenarioService;
    protected LootDropService lootDropService;
    protected WeaponUpgradeService weaponUpgradeService;

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.LEVEL_UP, this);
        EventDispatcher.getInstance().removeListener(EventType.TREASURE_OPENED, this);
        EventDispatcher.getInstance().removeListener(EventType.REWARD_SELECTED, this);
        levelManager.dispose();
        worldManager.dispose();
        entityManager.dispose();
        uiManager.dispose();
        if (scenarioService != null)
            scenarioService.dispose();
        if (lootDropService != null)
            lootDropService.dispose();
        if (weaponUpgradeService != null)
            weaponUpgradeService.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
        if (silhouetteShader != null)
            silhouetteShader.dispose();
        if (discardShader != null)
            discardShader.dispose();
        if (lightingManager != null)
            lightingManager.dispose();
    }
}
