package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.entities.*;
import vn.hust.hustgame.events.*;
import vn.hust.hustgame.graphics.GameRenderer;
import vn.hust.hustgame.input.GameInputHandler;
import vn.hust.hustgame.ui.HUD;
import vn.hust.hustgame.ui.InventoryUI;
import vn.hust.hustgame.world.GameCamera;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayScreen extends BaseScreen implements EventListener {
    protected ScreenState state;
    protected GameRenderer gameRenderer;
    protected GameCamera gameCamera;
    protected TiledMap map;
    protected String currentMapName = "";
    protected OrthogonalTiledMapRenderer mapRenderer;
    protected EntityManager entityManager;
    protected GameInputHandler inputHandler;
    protected Player player;

    protected ShapeRenderer shapeRenderer;
    protected BitmapFont font;
    protected HUD hud;
    protected InventoryUI inventoryUI;

    protected List<Portal> portals = new ArrayList<>();

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private ShaderProgram silhouetteShader;
    private ShaderProgram discardShader;

    protected static final float VIEW_WIDTH = 800f;
    protected static final float VIEW_HEIGHT = 600f;

    public PlayScreen(HustGame game) {
        super(game);
        this.state = ScreenState.RUNNING;

        initShaders();

        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();

        initCommonUI();

        EventDispatcher.getInstance().addListener(EventType.MAP_TRANSITION, this);
    }

    protected void initCommonUI() {
        if (shapeRenderer == null)
            shapeRenderer = new ShapeRenderer();
        if (font == null) {
            font = new BitmapFont();
            font.setColor(Color.WHITE);
        }
        if (hud == null)
            hud = new HUD();
        if (inventoryUI == null)
            inventoryUI = new InventoryUI();
    }

    private void initShaders() {
        String vert = Gdx.files.internal("shaders/default.vert").readString();
        String fragSil = Gdx.files.internal("shaders/silhouette.frag").readString();
        String fragDisc = Gdx.files.internal("shaders/discard.frag").readString();

        silhouetteShader = new ShaderProgram(vert, fragSil);
        if (!silhouetteShader.isCompiled()) {
            Gdx.app.error("Shaders", "Silhouette shader failed: " + silhouetteShader.getLog());
        }

        discardShader = new ShaderProgram(vert, fragDisc);
        if (!discardShader.isCompiled()) {
            Gdx.app.error("Shaders", "Discard shader failed: " + discardShader.getLog());
        }
    }

    protected void loadMap(String mapFile, float startX, float startY) {
        if (mapRenderer != null)
            mapRenderer.dispose();

        currentMapName = mapFile;
        map = game.getAssetManager().getTiledMap(mapFile);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // Data-driven map properties
        float mapW = map.getProperties().get("width", Integer.class) * 16f;
        float mapH = map.getProperties().get("height", Integer.class) * 16f;
        float zoom = map.getProperties().get("zoom", 1.0f, Float.class);

        if (gameCamera == null) {
            gameCamera = new GameCamera(VIEW_WIDTH, VIEW_HEIGHT);
        }
        gameCamera.setZoom(zoom);
        gameCamera.setMapBounds(mapW, mapH);

        if (player == null) {
            player = EntityFactory.createPlayer(startX, startY, GameState.instance.globalInventory, inputHandler, map);
            entityManager.addEntity(player);
            gameCamera.setTarget(player);
        } else {
            player.setMap(map, startX, startY);
        }

        if (gameRenderer == null) {
            gameRenderer = new GameRenderer(gameCamera, entityManager, game.getSpriteBatch(), silhouetteShader,
                    discardShader);
        }

        setupLayers();
        setupPortals();
    }

    private void setupLayers() {
        List<Integer> bgLayersList = new ArrayList<>();
        List<Integer> fgLayersList = new ArrayList<>();
        for (int i = 0; i < map.getLayers().size(); i++) {
            String layerName = map.getLayers().get(i).getName();
            if (layerName.equals("Via He") || layerName.equals("Duong")
                    || layerName.equals("Grass") || layerName.equals("Nha1")
                    || layerName.equals("Background")) {
                bgLayersList.add(i);
            } else {
                fgLayersList.add(i);
            }
        }

        backgroundLayers = new int[bgLayersList.size()];
        for (int i = 0; i < bgLayersList.size(); i++)
            backgroundLayers[i] = bgLayersList.get(i);
        foregroundLayers = new int[fgLayersList.size()];
        for (int i = 0; i < fgLayersList.size(); i++)
            foregroundLayers[i] = fgLayersList.get(i);
    }

    protected void setupPortals() {
        portals.clear();
        MapLayer portalLayer = map.getLayers().get("Portals");
        if (portalLayer != null) {
            for (MapObject obj : portalLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    String target = obj.getProperties().get("target", String.class);
                    float spawnX = obj.getProperties().get("spawnX", 0f, Float.class);
                    float spawnY = obj.getProperties().get("spawnY", 0f, Float.class);
                    portals.add(new Portal(rect, target, spawnX, spawnY));
                }
            }
        }
    }

    @Override
    public void show() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputHandler);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (state == ScreenState.RUNNING && !game.getScreenTransition().isTransitioning()) {
            entityManager.update(delta);
            onUpdate(delta);
            checkTriggers();
        }

        if (gameCamera != null) {
            gameCamera.update();
        }

        // Only render map if gameRenderer and mapRenderer are available (map-based
        // screens)
        if (gameRenderer != null && mapRenderer != null) {
            gameRenderer.render(mapRenderer, backgroundLayers, foregroundLayers, null);
        }

        onDraw();

        handleCommonInput();
        renderCommonUI();

        inputHandler.update();
    }

    protected abstract void onUpdate(float delta);

    protected abstract void onDraw();

    /** Helper for non-map screens to draw their entities manually */
    protected void drawEntities() {
        if (gameCamera != null) {
            batch.setProjectionMatrix(gameCamera.getCamera().combined);
        }
        batch.begin();
        entityManager.draw(batch);
        batch.end();
    }

    protected void renderCommonUI() {
        if (hud != null)
            hud.render(batch, shapeRenderer, font);
        if (inventoryUI != null && player != null) {
            inventoryUI.render(player, batch, shapeRenderer, font);
        }
    }

    protected void handleCommonInput() {
        if (inputHandler.isInventoryJustPressed()) {
            GameState.instance.isInventoryOpen = !GameState.instance.isInventoryOpen;
        }
    }

    protected void checkTriggers() {
        if (game.getScreenTransition().isTransitioning())
            return;
        for (Portal portal : portals) {
            if (portal.bounds.contains(player.getX(), player.getY())) {
                MapTransitionData data = new MapTransitionData(portal.targetMap, portal.spawnX, portal.spawnY);
                handleMapTransition(data);
                break;
            }
        }
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.MAP_TRANSITION) {
            MapTransitionData data = (MapTransitionData) event.getData();
            handleMapTransition(data);
        }
    }

    /**
     * Resolves a target identifier to a Screen instance.
     * Target can be a TMX filename (e.g. "tang1.tmx") mapped to its Screen class,
     * or a screen name (e.g. "LibraryScreen").
     */
    protected Screen resolveTargetScreen(String target) {
        switch (target) {
            case "Final Outside.tmx":
                return new FinalOutsideScreen(game);
            case "tang1.tmx":
                return new Tang1Screen(game);
            case "library.tmx":
                return new LibraryScreen(game);
            case "lab.tmx":
                return new LabScreen(game);
            case "boss_room.tmx":
                return new BossRoomScreen(game);
            default:
                Gdx.app.error("PlayScreen", "Unknown target screen: " + target);
                return null;
        }
    }

    protected void handleMapTransition(MapTransitionData data) {
        if (game.getScreenTransition().isTransitioning())
            return;

        Screen targetScreen = resolveTargetScreen(data.targetMap);
        if (targetScreen != null) {
            game.getScreenTransition().fadeOut(targetScreen, 0.5f);
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.MAP_TRANSITION, this);
        if (mapRenderer != null)
            mapRenderer.dispose();
        if (entityManager != null)
            entityManager.dispose();
        if (silhouetteShader != null)
            silhouetteShader.dispose();
        if (discardShader != null)
            discardShader.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
        if (font != null)
            font.dispose();
    }

    public ScreenState getState() {
        return state;
    }

    public void setState(ScreenState state) {
        this.state = state;
    }

    public static class Portal {
        public Rectangle bounds;
        public String targetMap;
        public float spawnX;
        public float spawnY;

        public Portal(Rectangle bounds, String targetMap, float spawnX, float spawnY) {
            this.bounds = bounds;
            this.targetMap = targetMap;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
        }
    }
}
