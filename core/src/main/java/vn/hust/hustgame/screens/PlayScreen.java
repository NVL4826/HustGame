package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.entities.*;
import vn.hust.hustgame.graphics.GameRenderer;
import vn.hust.hustgame.input.GameInputHandler;
import vn.hust.hustgame.inventory.Inventory;
import vn.hust.hustgame.world.GameCamera;
import vn.hust.hustgame.world.LibrarySystem;

import java.util.ArrayList;
import java.util.List;

public class PlayScreen extends BaseScreen {
    private GameState state;
    private GameRenderer gameRenderer;
    private GameCamera gameCamera;
    private TiledMap map;
    private String currentMapName = "";
    private OrthogonalTiledMapRenderer mapRenderer;
    private EntityManager entityManager;
    private GameInputHandler inputHandler;
    private Player player;
    
    private float lastOutsideX = 1024f;
    private float lastOutsideY = 1024f;
    private float lastTang1X = 240f;
    private float lastTang1Y = 80f;
    
    private LibrarySystem librarySystem;
    private List<Rectangle> stage2Zones = new ArrayList<>();

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private com.badlogic.gdx.graphics.glutils.ShaderProgram silhouetteShader;
    private com.badlogic.gdx.graphics.glutils.ShaderProgram discardShader;

    private static final float VIEW_WIDTH  = 480f;
    private static final float VIEW_HEIGHT = 320f;
    
    private float currentMapW = 128 * 16f;
    private float currentMapH = 128 * 16f;
    private float currentCameraZoom = 1f;

    public PlayScreen(HustGame game) {
        super(game);
        this.state = GameState.RUNNING;
        
        initShaders();
        
        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();
        
        loadMap("Final Outside.tmx", 1024f, 1024f);

        player = new Player(1024f, 1024f, new Inventory(), inputHandler, map);
        entityManager.addEntity(player);
        
        gameCamera = new GameCamera(VIEW_WIDTH, VIEW_HEIGHT);
        gameCamera.setMapBounds(currentMapW, currentMapH);
        gameCamera.setTarget(player);
        
        gameRenderer = new GameRenderer(gameCamera, entityManager, game.getSpriteBatch(), silhouetteShader, discardShader);
    }

    private void initShaders() {
        String vertexShader = "attribute vec4 a_position;\n" +
            "attribute vec4 a_color;\n" +
            "attribute vec2 a_texCoord0;\n" +
            "uniform mat4 u_projTrans;\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "void main() {\n" +
            "    v_color = a_color;\n" +
            "    v_texCoords = a_texCoord0;\n" +
            "    gl_Position = u_projTrans * a_position;\n" +
            "}\n";

        String fragSilhouette = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "    vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
            "    if(texColor.a < 0.1) discard;\n" +
            "    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.6 * texColor.a);\n" +
            "}\n";
            
        silhouetteShader = new com.badlogic.gdx.graphics.glutils.ShaderProgram(vertexShader, fragSilhouette);
        
        String fragDiscard = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "varying vec4 v_color;\n" +
            "varying vec2 v_texCoords;\n" +
            "uniform sampler2D u_texture;\n" +
            "void main() {\n" +
            "    vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
            "    if(texColor.a < 0.1) discard;\n" +
            "    gl_FragColor = v_color * texColor;\n" +
            "}\n";
            
        discardShader = new com.badlogic.gdx.graphics.glutils.ShaderProgram(vertexShader, fragDiscard);
    }

    private void loadMap(String mapFile, float startX, float startY) {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();

        currentMapName = mapFile;
        map = new TmxMapLoader().load(mapFile);
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        if (gameCamera != null) {
            if (mapFile.equals("Phong_doc.tmx")) {
                currentMapW = 1440f;
                currentMapH = 1440f;
                currentCameraZoom = 2.0f;
            } else if (mapFile.equals("tang1.tmx")) {
                currentMapW = 1024f;
                currentMapH = 1024f;
                currentCameraZoom = 1.0f;
            } else {
                currentMapW = 128 * 16f;
                currentMapH = 128 * 16f;
                currentCameraZoom = 1.0f;
            }
            gameCamera.setZoom(currentCameraZoom);
            gameCamera.setMapBounds(currentMapW, currentMapH);
        }

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
        for(int i = 0; i < bgLayersList.size(); i++) backgroundLayers[i] = bgLayersList.get(i);
        foregroundLayers = new int[fgLayersList.size()];
        for(int i = 0; i < fgLayersList.size(); i++) foregroundLayers[i] = fgLayersList.get(i);

        if (player != null) {
            player.setMap(map, startX, startY);
        }

        stage2Zones.clear();
        if (mapFile.equals("tang1.tmx")) {
            com.badlogic.gdx.maps.MapLayer s2Layer = map.getLayers().get("Stage2");
            if (s2Layer != null) {
                for (com.badlogic.gdx.maps.MapObject obj : s2Layer.getObjects()) {
                    if (obj instanceof com.badlogic.gdx.maps.objects.RectangleMapObject) {
                        stage2Zones.add(((com.badlogic.gdx.maps.objects.RectangleMapObject) obj).getRectangle());
                    }
                }
            }
        }

        if (mapFile.equals("Phong_doc.tmx")) {
            if (librarySystem == null) librarySystem = new LibrarySystem();
        } else {
            if (librarySystem != null) {
                librarySystem.dispose();
                librarySystem = null;
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
        if (state == GameState.RUNNING) {
            entityManager.update(delta);
            if (librarySystem != null) librarySystem.update(delta);
            checkTriggers();
        }

        gameCamera.update();
        gameRenderer.render(mapRenderer, backgroundLayers, foregroundLayers, librarySystem);
    }

    private void checkTriggers() {
        if (currentMapName.equals("Final Outside.tmx")) {
            com.badlogic.gdx.maps.tiled.TiledMapTileLayer cuaLayer = (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get("Cua");
            if (cuaLayer != null) {
                int cellX = (int) (player.getX() / 16f);
                int cellY = (int) (player.getY() / 16f);
                com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = cuaLayer.getCell(cellX, cellY);
                if (cell != null && cell.getTile() != null) {
                    lastOutsideX = player.getX();
                    lastOutsideY = player.getY() - 32f;
                    loadMap("tang1.tmx", 445f, 100f);
                }
            }
        } else if (currentMapName.equals("tang1.tmx")) {
            if (player.getY() < 65f) {
                loadMap("Final Outside.tmx", lastOutsideX, lastOutsideY);
            }
            float px = player.getX();
            float py = player.getY();
            for (Rectangle zone : stage2Zones) {
                if (zone.contains(px, py)) {
                    lastTang1X = player.getX();
                    lastTang1Y = 612f;
                    loadMap("Phong_doc.tmx", 240f, 80f);
                    break;
                }
            }
        } else if (currentMapName.equals("Phong_doc.tmx")) {
            if (player.getY() < 65f) {
                loadMap("tang1.tmx", lastTang1X, lastTang1Y);
            }
        }
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (entityManager != null) entityManager.dispose();
        if (silhouetteShader != null) silhouetteShader.dispose();
        if (discardShader != null) discardShader.dispose();
        if (librarySystem != null) librarySystem.dispose();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
