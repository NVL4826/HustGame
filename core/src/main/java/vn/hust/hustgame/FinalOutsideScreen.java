package vn.hust.hustgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

public class FinalOutsideScreen implements Screen {
    private MainGame game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private EntityManager entityManager;
    private Player player;
    private GameInputHandler inputHandler;

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;

    private int[] backgroundLayers;
    private int[] foregroundLayers;

    private InventoryUI inventoryUI;

    public FinalOutsideScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load("Final Outside.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 320);

        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();

        inventoryUI = new InventoryUI();

        Gdx.input.setInputProcessor(inputHandler);

        // Spawn at map center or near door
        player = new Player(1024f, 1024f, GameState.instance.globalInventory, inputHandler, map);
        entityManager.addEntity(player);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        setupLayers();

        GameState.instance.previousScreen = "FinalOutsideScreen";

    }

    private void setupLayers() {
        java.util.List<Integer> bgLayers = new java.util.ArrayList<>();
        java.util.List<Integer> fgLayers = new java.util.ArrayList<>();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            String name = map.getLayers().get(i).getName();
            if (name.equals("Via He") || name.equals("Duong") || name.equals("Grass") || name.equals("Nha1") || name.equals("Background")) {
                bgLayers.add(i);
            } else {
                fgLayers.add(i);
            }
        }
        backgroundLayers = new int[bgLayers.size()];
        for(int i=0; i<bgLayers.size(); i++) backgroundLayers[i] = bgLayers.get(i);

        foregroundLayers = new int[fgLayers.size()];
        for(int i=0; i<fgLayers.size(); i++) foregroundLayers[i] = fgLayers.get(i);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        entityManager.update(delta);

        // Camera logic
        float mapW = 128 * 16f;
        float mapH = 128 * 16f;
        float camX = MathUtils.clamp(player.getX(), camera.viewportWidth / 2f, mapW - camera.viewportWidth / 2f);
        float camY = MathUtils.clamp(player.getY(), camera.viewportHeight / 2f, mapH - camera.viewportHeight / 2f);
        camera.position.set(camX, camY, 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render(backgroundLayers);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();

        mapRenderer.render(foregroundLayers);

        // Check transition to Tang1Screen (Cua layer)
        TiledMapTileLayer cuaLayer = (TiledMapTileLayer) map.getLayers().get("Cua");
        if (cuaLayer != null) {
            int cellX = (int) (player.getX() / 16f);
            int cellY = (int) (player.getY() / 16f);
            if (cuaLayer.getCell(cellX, cellY) != null) {
                game.screenTransition.fadeOut(new Tang1Screen(game), 0.5f);
            }
        }

        drawHUD();

        inventoryUI.render(player, batch, shapeRenderer, font);
    }

    private void drawHUD() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // HP Bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 20, 100, 10);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 20, (GameState.instance.hp / GameState.instance.maxHp) * 100, 10);

        // Stamina Bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 35, 100, 10);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(10, Gdx.graphics.getHeight() - 35, (GameState.instance.stamina / GameState.instance.maxStamina) * 100, 10);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "HP: " + (int)GameState.instance.hp, 115, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "SP: " + (int)GameState.instance.stamina, 115, Gdx.graphics.getHeight() - 25);
        if (GameState.instance.hasNao) font.draw(batch, "Artifact: Nao 100%", 10, Gdx.graphics.getHeight() - 50);
        if (GameState.instance.hasUsb) font.draw(batch, "Artifact: USB", 10, Gdx.graphics.getHeight() - 70);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        entityManager.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

}
