package com.duc.hustgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

public class HustGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private TiledMap map;
    private String currentMapName = "";
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private EntityManager entityManager;
    private GameInputHandler inputHandler;
    private Player player;
    private float lastOutsideX = 1024f;
    private float lastOutsideY = 1024f;
    private float lastTang1X = 240f;
    private float lastTang1Y = 80f;
    private LibrarySystem librarySystem;
    private java.util.List<com.badlogic.gdx.math.Rectangle> stage2Zones = new java.util.ArrayList<>();

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private com.badlogic.gdx.graphics.glutils.ShaderProgram silhouetteShader;
    private com.badlogic.gdx.graphics.glutils.ShaderProgram discardShader;

    private static final float VIEW_WIDTH  = 480f;
    private static final float VIEW_HEIGHT = 320f;
    // Dynamic per-map bounds and zoom
    private float currentMapW = 128 * 16f;
    private float currentMapH = 128 * 16f;
    private float currentCameraZoom = 1f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        loadMap("Final Outside.tmx", 1024f, 1024f);

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

        // Shader vẽ bóng đen (Silhouette)
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
        if (!silhouetteShader.isCompiled()) {
            Gdx.app.error("Shader", "Failed to compile silhouetteShader: " + silhouetteShader.getLog());
        }

        // Shader hỗ trợ loại bỏ các pixel trong suốt của vật cản để khỏi cản Depth Buffer
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
        if (!discardShader.isCompiled()) {
            Gdx.app.error("Shader", "Failed to compile discardShader: " + discardShader.getLog());
        }

        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        player = new Player(1024f, 1024f, new Inventory(), inputHandler, map);
        entityManager.addEntity(player);
    }

    private void loadMap(String mapFile, float startX, float startY) {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        
        currentMapName = mapFile;
        map = new TmxMapLoader().load(mapFile);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        
        if (camera == null) {
            camera = new OrthographicCamera();
            camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        }

        int totalLayers = map.getLayers().size();
        java.util.List<Integer> bgLayersList = new java.util.ArrayList<>();
        java.util.List<Integer> fgLayersList = new java.util.ArrayList<>();
        
        // Set per-map world bounds and camera zoom
        if (mapFile.equals("Phong_doc.tmx")) {
            currentMapW = 1440f;
            currentMapH = 1440f;
            currentCameraZoom = 2.0f; // Zoom out for wide reading-room view
        } else if (mapFile.equals("tang1.tmx")) {
            currentMapW = 1024f;
            currentMapH = 1024f;
            currentCameraZoom = 1.0f;
        } else {
            currentMapW = 128 * 16f;
            currentMapH = 128 * 16f;
            currentCameraZoom = 1.0f;
        }
        camera.zoom = currentCameraZoom;

        for (int i = 0; i < totalLayers; i++) {
            String layerName = map.getLayers().get(i).getName();
            // Background layers: rendered normally (no stencil)
            if (layerName.equals("Via He") || layerName.equals("Duong")
                || layerName.equals("Grass") || layerName.equals("Nha1")
                || layerName.equals("Background")) {
                bgLayersList.add(i);
            } else {
                fgLayersList.add(i);
            }
        }
        
        backgroundLayers = new int[bgLayersList.size()];
        for(int i = 0; i < bgLayersList.size(); i++) {
            backgroundLayers[i] = bgLayersList.get(i);
        }
        
        foregroundLayers = new int[fgLayersList.size()];
        for(int i = 0; i < fgLayersList.size(); i++) {
            foregroundLayers[i] = fgLayersList.get(i);
        }

        // Read spawn point from "Spawn" object layer if present, only if we are using the defaults
        com.badlogic.gdx.maps.MapLayer spawnLayer = map.getLayers().get("Spawn");
        if (spawnLayer != null && startX == 1024f && startY == 1024f) {
            for (com.badlogic.gdx.maps.MapObject obj : spawnLayer.getObjects()) {
                if (obj instanceof com.badlogic.gdx.maps.objects.RectangleMapObject) {
                    com.badlogic.gdx.math.Rectangle r =
                        ((com.badlogic.gdx.maps.objects.RectangleMapObject) obj).getRectangle();
                    startX = r.x + r.width / 2f;
                    startY = r.y + r.height / 2f;
                    break;
                }
            }
        }

        if (player != null) {
            player.setMap(map, startX, startY);
        }

        // Load Stage2 trigger zones from tang1.tmx
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
            if (librarySystem == null) {
                librarySystem = new LibrarySystem();
            }
        } else {
            if (librarySystem != null) {
                librarySystem.dispose();
                librarySystem = null;
            }
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        entityManager.update(delta);
        
        if (librarySystem != null) {
            librarySystem.update(delta);
        }

        // Check if player enters library door
        if (currentMapName.equals("Final Outside.tmx")) {
            com.badlogic.gdx.maps.tiled.TiledMapTileLayer cuaLayer = (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) map.getLayers().get("Cua");
            if (cuaLayer != null) {
                int cellX = (int) (player.getX() / 16f);
                int cellY = (int) (player.getY() / 16f);
                com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = cuaLayer.getCell(cellX, cellY);
                if (cell != null && cell.getTile() != null) {
                    // Save return position slightly below the door tile
                    lastOutsideX = player.getX();
                    lastOutsideY = player.getY() - 32f;
                    // Move player into library (spawn at the bottom entrance)
                    loadMap("tang1.tmx", 512f, 100f);
                }
            }
        } else if (currentMapName.equals("tang1.tmx")) {
            // Exit library if they walk down out the door
            if (player.getY() < 65f) {
                loadMap("Final Outside.tmx", lastOutsideX, lastOutsideY);
            }
            // Check if player enters a Stage2 zone -> go to Phong_doc
            float px = player.getX();
            float py = player.getY();
            for (com.badlogic.gdx.math.Rectangle zone : stage2Zones) {
                if (zone.contains(px, py)) {
                    lastTang1X = player.getX();
                    lastTang1Y = zone.y - 32f; // Place safely in the gap
                    loadMap("Phong_doc.tmx", 240f, 80f);
                    break;
                }
            }
        } else if (currentMapName.equals("Phong_doc.tmx")) {
            // Exit Phong_doc if they walk down out the door
            if (player.getY() < 65f) {
                loadMap("tang1.tmx", lastTang1X, lastTang1Y);
            }
        }


        // Zoom-aware camera clamping: half the visible world area
        float halfW = VIEW_WIDTH  * currentCameraZoom / 2f;
        float halfH = VIEW_HEIGHT * currentCameraZoom / 2f;
        float camX = MathUtils.clamp(player.getX(), halfW, currentMapW - halfW);
        float camY = MathUtils.clamp(player.getY(), halfH, currentMapH - halfH);
        camera.position.set(camX, camY, 0);
        camera.update();

        // 0. Xóa màu nền, Depth Buffer và Stencil Buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        mapRenderer.setView(camera);

        // 1. Vẽ Background map bình thường
        mapRenderer.render(backgroundLayers);

        // 2. Vẽ Nhân vật bình thường (Màu thật)
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();

        // 2.5 Vẽ thêm sách lơ lửng, nhân vật ngủ gật nếu đang ở thư viện
        if (librarySystem != null) {
            batch.setProjectionMatrix(camera.combined);
            librarySystem.render(batch);
        }

        // 3. Khởi động Stencil Buffer để đánh dấu vị trí các vật cản (Foreground)
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        
        // Cài đặt: Bất cứ pixel nào Foreground vẽ ra sẽ ghi số '1' vào Stencil Buffer
        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glStencilMask(0xFF); // Cho phép ghi đè lên Stencil
        
        // Vẽ Foreground map
        // Sử dụng discardShader để các điểm ảnh trong suốt không ghi số '1'
        mapRenderer.getBatch().setShader(discardShader);
        mapRenderer.render(foregroundLayers);
        mapRenderer.getBatch().setShader(null);

        // 4. Vẽ Silhouette nhân vật
        // Khóa ghi Stencil, chỉ dùng để check điều kiện (Read-only)
        Gdx.gl.glStencilMask(0x00);
        
        // Cấu hình: Chỉ cho phép vẽ pixel nhân vật nếu khu vực đó Stencil Buffer đang chứa số '1'
        // (Nghĩa là vị trí đó đã bị Foreground đè lên)
        Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 1, 0xFF);

        // Bật trộn màu (Alpha Blending) để tạo độ mờ bóng đen
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setShader(silhouetteShader);
        batch.begin();
        entityManager.draw(batch);
        batch.end();
        batch.setShader(null);

        // Phục hồi cài đặt gốc của OpenGL
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilMask(0xFF); // QUAN TRỌNG: Cần trả lại quyền ghi để hàm glClear ở frame tiếp theo có thể xóa sạch
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 5. Thêm lớp chiếu sáng của thư viện
        if (librarySystem != null) {
            librarySystem.renderLights(camera);
        }
    }

    @Override
    public void dispose() {
        if(batch != null) batch.dispose();
        if(map != null) map.dispose();
        if(mapRenderer != null) mapRenderer.dispose();
        if(entityManager != null) entityManager.dispose();
        if(silhouetteShader != null) silhouetteShader.dispose();
        if(librarySystem != null) librarySystem.dispose();
    }
}
