package com.duc.hustgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class LibraryScreen implements Screen {
    private MainGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture bgTexture;
    
    private EntityManager entityManager;
    private Player player;
    private GameInputHandler inputHandler;
    
    private BookPuzzle puzzle;
    private boolean puzzleSolved = false;
    
    private float redFlashTimer = 0f;
    
    // Boss
    private boolean bossSpawned = false;
    private boolean bossDefeated = false;
    private Rectangle bossRect;
    private float bossHp = 500;
    private float bossAttackTimer = 0f;
    private List<Rectangle> bossProjectiles;
    
    // Player attacks
    private List<Rectangle> bullets;
    
    // Artifact
    private Rectangle artifactRect;
    
    public LibraryScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600); // Fixed resolution for library puzzle
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        // bgTexture = new Texture("Library1.jpg"); // We will catch if texture is not found
        try {
            bgTexture = new Texture("Library1.jpg");
        } catch (Exception e) {
            bgTexture = null;
        }
        
        entityManager = new EntityManager();
        inputHandler = new GameInputHandler();
        Gdx.input.setInputProcessor(inputHandler);
        
        player = new Player(400, 100, new Inventory(), inputHandler, null); // No tiled map
        entityManager.addEntity(player);
        
        puzzle = new BookPuzzle(this);
        
        bossRect = new Rectangle(400, 500, 50, 50);
        bossProjectiles = new ArrayList<>();
        bullets = new ArrayList<>();
    }
    
    public void flashRed() {
        redFlashTimer = 0.2f;
    }
    
    public void onPuzzleSolved() {
        puzzleSolved = true;
        bossSpawned = true;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        entityManager.update(delta);
        
        // Manual bounds check for player
        player.setX(MathUtils.clamp(player.getX(), 0, 800));
        player.setY(MathUtils.clamp(player.getY(), 0, 600));
        
        if (!puzzleSolved) {
            puzzle.update(delta);
        } else if (bossSpawned && !bossDefeated) {
            // Boss Logic
            // Move towards player slowly
            float dx = player.getX() - bossRect.x;
            float dy = player.getY() - bossRect.y;
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            if (dist > 0) {
                bossRect.x += (dx/dist) * 30 * delta;
                bossRect.y += (dy/dist) * 30 * delta;
            }
            
            // Attack
            bossAttackTimer += delta;
            if (bossAttackTimer >= 2f) {
                bossAttackTimer = 0f;
                // Throw projectile
                Rectangle proj = new Rectangle(bossRect.x, bossRect.y, 20, 15);
                bossProjectiles.add(proj);
            }
            
            // Update projectiles
            for (int i = bossProjectiles.size() - 1; i >= 0; i--) {
                Rectangle p = bossProjectiles.get(i);
                p.y -= 150 * delta; // Fall down
                
                Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);
                if (pBounds.overlaps(p)) {
                    GameState.instance.hp -= 15;
                    bossProjectiles.remove(i);
                    flashRed();
                } else if (p.y < 0) {
                    bossProjectiles.remove(i);
                }
            }
            
            // Player shooting
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                bullets.add(new Rectangle(player.getX(), player.getY(), 10, 10));
            }
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Rectangle b = bullets.get(i);
                b.y += 300 * delta;
                if (b.overlaps(bossRect)) {
                    bossHp -= 20;
                    bullets.remove(i);
                } else if (b.y > 600) {
                    bullets.remove(i);
                }
            }
            
            if (bossHp <= 0) {
                bossDefeated = true;
                artifactRect = new Rectangle(bossRect.x, bossRect.y, 30, 30);
            }
        } else if (bossDefeated) {
            // Pick up artifact
            Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);
            if (artifactRect != null && pBounds.overlaps(artifactRect)) {
                GameState.instance.hasNao = true;
                GameState.instance.libraryCleared = true;
                artifactRect = null;
                // Fade to LabScreen
                game.screenTransition.fadeOut(new LabScreen(game), 1f);
            }
        }
        
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        batch.begin();
        if (bgTexture != null) {
            batch.draw(bgTexture, 0, 0, 800, 600);
        }
        entityManager.draw(batch);
        batch.end();
        
        if (!puzzleSolved) {
            puzzle.render(shapeRenderer, batch);
        }
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (bossSpawned && !bossDefeated) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(bossRect.x, bossRect.y, bossRect.width, bossRect.height);
            shapeRenderer.setColor(Color.BROWN);
            for (Rectangle p : bossProjectiles) {
                shapeRenderer.rect(p.x, p.y, p.width, p.height);
            }
            shapeRenderer.setColor(Color.YELLOW);
            for (Rectangle b : bullets) {
                shapeRenderer.rect(b.x, b.y, b.width, b.height);
            }
            // Boss HP
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(bossRect.x, bossRect.y + 60, (bossHp / 500f) * 50, 5);
        } else if (bossDefeated && artifactRect != null) {
            shapeRenderer.setColor(Color.BLUE);
            shapeRenderer.circle(artifactRect.x + 15, artifactRect.y + 15, 15);
        }
        shapeRenderer.end();
        
        if (redFlashTimer > 0) {
            redFlashTimer -= delta;
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(1, 0, 0, 0.3f));
            shapeRenderer.rect(0, 0, 800, 600);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
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
        batch.dispose();
        shapeRenderer.dispose();
        if (bgTexture != null) bgTexture.dispose();
        entityManager.dispose();
        puzzle.dispose();
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }
}
