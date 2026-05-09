package hust.adventure.world;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class LibrarySystem {
    private World world;
    private RayHandler rayHandler;
    private BitmapFont font;

    private Array<Texture> bookTextures = new Array<>();
    private Texture candleTexture;

    private Array<FloatingBook> books = new Array<>();
    private Array<Candle> candles = new Array<>();

    public LibrarySystem() {
        world = new World(new Vector2(0, 0), true);
        rayHandler = new RayHandler(world);
        // Ánh sáng tối hơn để nến và đèn điểm nổi bật rõ hơn
        rayHandler.setAmbientLight(0.15f, 0.12f, 0.08f, 1f);

        font = new BitmapFont();

        try {
            // Load Books (19-29, 47-51)
            for (int i = 19; i <= 29; i++)
                bookTextures.add(new Texture("Phong_doc/" + i + ".png"));
            for (int i = 47; i <= 51; i++)
                bookTextures.add(new Texture("Phong_doc/" + i + ".png"));

            candleTexture = new Texture("Phong_doc/1.png");

            // === Vị trí cố định trong phòng đọc (map 1440x1440) ===

            // Nến / đèn trên bàn
            spawnCandle(300f, 990f);
            spawnCandle(645f, 855f);
            spawnCandle(990f, 915f);

            // Sách lơ lửng trên kệ
            spawnBook(180f, 1110f);
            spawnBook(450f, 1140f);
            spawnBook(750f, 1170f);
            spawnBook(1080f, 1125f);
            spawnBook(1290f, 1080f);

        } catch (Exception e) {
            Gdx.app.error("LibrarySystem", "Error loading Phong_doc textures: " + e.getMessage());
        }
    }

    public void spawnBook(float x, float y) {
        if (bookTextures.size > 0)
            books.add(new FloatingBook(x, y));
    }

    public void spawnCandle(float x, float y) {
        if (candleTexture != null)
            candles.add(new Candle(x, y));
    }

    public void update(float delta) {
        world.step(delta, 6, 2);
        rayHandler.update();

        for (FloatingBook b : books)
            b.update(delta);
        for (Candle c : candles)
            c.update(delta);
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        for (FloatingBook b : books)
            b.draw(batch);
        for (Candle c : candles)
            c.draw(batch);
        font.setColor(1, 1, 1, 1);
        batch.end();
    }

    public void renderLights(OrthographicCamera camera) {
        rayHandler.setCombinedMatrix(camera);
        rayHandler.render();
    }

    public void dispose() {
        world.dispose();
        rayHandler.dispose();
        font.dispose();
        for (Texture t : bookTextures)
            t.dispose();
        if (candleTexture != null)
            candleTexture.dispose();
    }

    // --- Inner Entity Classes ---

    class FloatingBook {
        float x, baseY, currentY;
        float stateTime;
        PointLight light;
        Texture mySkin;

        public FloatingBook(float x, float y) {
            this.x = x;
            this.baseY = y;
            this.stateTime = MathUtils.random(10f);
            this.mySkin = bookTextures.random();

            light = new PointLight(rayHandler, 32, new Color(1f, 0.8f, 0.4f, 0.6f), 50f, x, y);
            light.setSoft(true);
        }

        public void update(float delta) {
            stateTime += delta;
            currentY = baseY + MathUtils.sin(stateTime * 2f) * 10f;
            light.setPosition(x + mySkin.getWidth() / 2f, currentY + mySkin.getHeight() / 2f);
            light.setDistance(50f + MathUtils.sin(stateTime * 4f) * 5f);
        }

        public void draw(SpriteBatch batch) {
            batch.draw(mySkin, x, currentY);
        }
    }

    class Candle {
        float x, y;
        float stateTime;
        PointLight light;

        public Candle(float x, float y) {
            this.x = x;
            this.y = y;
            this.stateTime = MathUtils.random(10f);
            light = new PointLight(rayHandler, 64, new Color(1f, 0.6f, 0.2f, 0.8f), 60f, x, y);
            light.setSoft(true);
        }

        public void update(float delta) {
            stateTime += delta;
            float flicker = MathUtils.random(-3f, 3f) + (MathUtils.sin(stateTime * 15f) * 4f);
            light.setDistance(60f + flicker);
        }

        public void draw(SpriteBatch batch) {
            batch.draw(candleTexture, x, y);
        }
    }
}
