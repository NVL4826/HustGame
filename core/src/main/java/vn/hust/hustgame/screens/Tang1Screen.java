package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.world.CoffeeSystem;

public class Tang1Screen extends PlayScreen {
    private Texture studentTexture;
    private List<Rectangle> coffeeItems;
    private List<Rectangle> noteItems;
    private String activeHint = "";
    private float hintTimer = 0f;

    public Tang1Screen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        loadMap("tang1.tmx", 445f, 100f);

        studentTexture = game.getAssetManager().getTexture("4.png");

        coffeeItems = new ArrayList<>();
        coffeeItems.add(new Rectangle(300f, 150f, 20f, 20f));

        noteItems = new ArrayList<>();
        noteItems.add(new Rectangle(500f, 150f, 20f, 20f));
    }

    @Override
    protected void onUpdate(float delta) {
        if (GameState.instance.isInventoryOpen)
            return;

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
    }

    @Override
    protected void onDraw() {
        OrthographicCamera cam = gameCamera.getCamera();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.setColor(0.5f, 0.5f, 0.5f, 1f);
        batch.draw(studentTexture, 600f, 200f, 50, 50);
        batch.draw(studentTexture, 400f, 250f, 50, 50);
        batch.setColor(Color.WHITE);
        batch.end();

        shapeRenderer.setProjectionMatrix(cam.combined);
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

        batch.setProjectionMatrix(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()).combined);
        batch.begin();
        if (!activeHint.isEmpty()) {
            font.draw(batch, activeHint, -150, 100);
        }
        batch.end();
    }
}