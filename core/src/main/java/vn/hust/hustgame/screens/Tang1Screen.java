package vn.hust.hustgame.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.entities.EntityManager;
import vn.hust.hustgame.entities.Player;
import vn.hust.hustgame.input.GameInputHandler;
import vn.hust.hustgame.ui.InventoryUI;
import vn.hust.hustgame.world.CoffeeSystem;

public class Tang1Screen extends BaseScreen {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private EntityManager entityManager;
    private Player player;
    private GameInputHandler inputHandler;

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch batch;
    private Texture studentTexture;

    private List<Rectangle> coffeeItems;
    private List<Rectangle> noteItems;
    private String activeHint = "";
    private float hintTimer = 0f;

    private InventoryUI inventoryUI;

    public Tang1Screen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        map = new TmxMapLoader().load("tang1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 320);

        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        player = new Player(445f, 100f, GameState.instance.globalInventory, inputHandler, map);
        entityManager.addEntity(player);

        shapeRenderer = new ShapeRenderer();
        batch = game.getSpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        studentTexture = new Texture("4.png");

        coffeeItems = new ArrayList<>();
        coffeeItems.add(new Rectangle(300f, 150f, 20f, 20f));

        noteItems = new ArrayList<>();
        noteItems.add(new Rectangle(500f, 150f, 20f, 20f));

        inventoryUI = new InventoryUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            GameState.instance.isInventoryOpen = !GameState.instance.isInventoryOpen;
        }

        if (!GameState.instance.isInventoryOpen){
            entityManager.update(delta);

            float px = player.getX();
            float py = player.getY();

            for (int i = coffeeItems.size() - 1; i >= 0; i--) {
                if (coffeeItems.get(i).contains(px, py)) {
                    coffeeItems.remove(i);
                    GameState.instance.coffeeSystem.consume(CoffeeSystem.CoffeeType.DEN);
                }
            }

            for (Rectangle note : noteItems) {
                if (note.contains(px, py)) {
                    activeHint = "Nghe noi thu thu hom nay dang rat... kho tinh";
                    hintTimer = 3f;
                }
            }

            if (hintTimer > 0) {
                hintTimer -= delta;
            } else {
                activeHint = "";
            }

            if (py < 65f) {
                game.getScreenTransition().fadeOut(new FinalOutsideScreen(game), 0.5f);
            }

            com.badlogic.gdx.maps.MapLayer s2Layer = map.getLayers().get("Stage2");
            if (s2Layer != null) {
                for (com.badlogic.gdx.maps.MapObject obj : s2Layer.getObjects()) {
                    if (obj instanceof com.badlogic.gdx.maps.objects.RectangleMapObject) {
                        Rectangle rect = ((com.badlogic.gdx.maps.objects.RectangleMapObject) obj).getRectangle();
                        if (rect.contains(px, py)) {
                            game.getScreenTransition().fadeOut(new LibraryScreen(game), 0.5f);
                        }
                    }
                }
            }
        }

        float mapW = 1024f;
        float mapH = 1024f;
        float camX = MathUtils.clamp(player.getX(), camera.viewportWidth / 2f, mapW - camera.viewportWidth / 2f);
        float camY = MathUtils.clamp(player.getY(), camera.viewportHeight / 2f, mapH - camera.viewportHeight / 2f);
        camera.position.set(camX, camY, 0);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        batch.draw(studentTexture, 600f, 200f, 50, 50);
        batch.draw(studentTexture, 400f, 250f, 50, 50);
        batch.setColor(Color.WHITE);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle c : coffeeItems) {
            shapeRenderer.circle(c.x + 10, c.y + 10, 10);
        }
        shapeRenderer.setColor(Color.WHITE);
        for (Rectangle n : noteItems) {
            shapeRenderer.rect(n.x, n.y, n.width, n.height);
        }
        shapeRenderer.end();

        batch.begin();
        entityManager.draw(batch);
        batch.end();

        batch.setProjectionMatrix(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).combined);
        batch.begin();
        if (!activeHint.isEmpty()) {
            font.draw(batch, activeHint, -150, 100);
        }
        batch.end();
        drawHUD();

        inventoryUI.render(player, batch, shapeRenderer, font);
    }

    private void drawHUD() {
        shapeRenderer.setProjectionMatrix(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(-Gdx.graphics.getWidth()/2 + 10, Gdx.graphics.getHeight()/2 - 30, 100, 10);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(-Gdx.graphics.getWidth()/2 + 10, Gdx.graphics.getHeight()/2 - 30, (GameState.instance.hp / GameState.instance.maxHp) * 100, 10);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(-Gdx.graphics.getWidth()/2 + 10, Gdx.graphics.getHeight()/2 - 45, 100, 10);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(-Gdx.graphics.getWidth()/2 + 10, Gdx.graphics.getHeight()/2 - 45, (GameState.instance.stamina / GameState.instance.maxStamina) * 100, 10);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        entityManager.dispose();
        shapeRenderer.dispose();
        font.dispose();
        if (studentTexture != null) studentTexture.dispose();
    }
}
