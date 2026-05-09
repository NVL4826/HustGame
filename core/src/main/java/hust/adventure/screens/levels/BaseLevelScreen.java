package hust.adventure.screens.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.IntArray;

import hust.adventure.HustGame;
import hust.adventure.core.ProgressContext;
import hust.adventure.core.LevelConfig;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.factory.EntityFactoryImpl;
import hust.adventure.events.*;
import hust.adventure.graphics.CameraManager;
import hust.adventure.graphics.GameRenderer;
import hust.adventure.input.InputReader;
import hust.adventure.screens.BaseScreen;
import hust.adventure.screens.PlayMode;
import hust.adventure.collision.CollisionManager;
import hust.adventure.ui.UIManager;
import hust.adventure.world.WorldManager;

/**
 * Base class for all gameplay levels. Manages core systems like entities, collision, and rendering.
 */
public abstract class BaseLevelScreen extends BaseScreen {
    protected final LevelConfig config;
    protected PlayMode state;

    protected final WorldManager worldManager;
    protected final EntityManager entityManager;
    protected final UIManager uiManager;
    protected final InputReader inputReader;
    protected final EntityFactory entityFactory;
    protected final CollisionManager collisionManager;

    protected CameraManager cameraManager;
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected GameRenderer gameRenderer;
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
        this.entityManager = new EntityManager();
        this.uiManager = new UIManager();
        this.inputReader = new InputReader();
        this.entityFactory = new EntityFactoryImpl();
        this.collisionManager = new CollisionManager(entityManager, 64f);

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

        worldManager.loadMap(game.getAssetManager().getTiledMap(config.getMapPath()));
        collisionManager.setMap(worldManager.getCurrentMap(), worldManager.getWalls());
        mapRenderer = new OrthogonalTiledMapRenderer(worldManager.getCurrentMap());

        final float mapW = worldManager.getCurrentMap().getProperties().get("width", Integer.class) * 16f;
        final float mapH = worldManager.getCurrentMap().getProperties().get("height", Integer.class) * 16f;
        final float zoom = worldManager.getCurrentMap().getProperties().get("zoom", config.getZoom(), Float.class);

        if (cameraManager == null) {
            cameraManager = new CameraManager(VIEW_WIDTH, VIEW_HEIGHT);
        }
        cameraManager.setZoom(zoom);
        cameraManager.setMapBounds(mapW, mapH);

        if (player == null) {
            player = entityFactory.createPlayer(config.getSpawnX(), config.getSpawnY(),
                    ProgressContext.instance.globalInventory, inputReader, collisionManager);
            entityManager.addEntity(player);
            cameraManager.setTarget(player);
        } else {
            player.setCollisionContext(collisionManager, config.getSpawnX(), config.getSpawnY());
        }

        gameRenderer = new GameRenderer(cameraManager, entityManager, game.getSpriteBatch(), silhouetteShader,
                discardShader, uiManager.getHud(), uiManager.getInventoryUI());

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
                || name.equals("Background");
    }

    @Override
    public void render(float delta) {
        if (state == PlayMode.RUNNING && !game.getScreenTransition().isTransitioning()) {
            entityManager.update(delta);
            collisionManager.update(worldManager.getWalls());
            checkTriggers();
            updateLevel(delta);
        }

        if (gameRenderer != null) {
            gameRenderer.render(delta, mapRenderer, backgroundLayers, foregroundLayers, player, shapeRenderer, font,
                    null);
        }

        drawLevel();
        handleInput();
        inputReader.update();
    }

    private void handleInput() {
        if (inputReader.isInventoryJustPressed()) {
            ProgressContext.instance.isInventoryOpen = !ProgressContext.instance.isInventoryOpen;
        }
    }

    private void checkTriggers() {
        if (game.getScreenTransition().isTransitioning())
            return;

        for (final WorldManager.Portal portal : worldManager.getPortals()) {
            if (portal.bounds.contains(player.getX(), player.getY())) {
                EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.MAP_TRANSITION,
                        new MapTransitionData(portal.targetMap, portal.spawnX, portal.spawnY)));
                break;
            }
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

    @Override
    public void dispose() {
        worldManager.dispose();
        entityManager.dispose();
        uiManager.dispose();
        if (mapRenderer != null)
            mapRenderer.dispose();
        if (silhouetteShader != null)
            silhouetteShader.dispose();
        if (discardShader != null)
            discardShader.dispose();
    }
}
