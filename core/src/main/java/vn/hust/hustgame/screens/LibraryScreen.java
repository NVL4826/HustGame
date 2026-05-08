package vn.hust.hustgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

import vn.hust.hustgame.HustGame;
import vn.hust.hustgame.GameState;
import vn.hust.hustgame.ui.BookPuzzle;

public class LibraryScreen extends PlayScreen {
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

    public LibraryScreen(HustGame game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();
        loadMap("library.tmx", 400, 100);

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
    protected void onUpdate(float delta) {
        if (GameState.instance.isInventoryOpen)
            return;

        if (!puzzleSolved) {
            puzzle.update(delta);
            if (puzzle.isSolved()) {
                puzzleSolved = true;
                bossSpawned = true;
            }
        } else if (!bossDefeated) {
            updateBossLogic(delta);
        } else {
            updateAfterBossLogic(delta);
        }

        if (redFlashTimer > 0)
            redFlashTimer -= delta;
    }

    private void updateBossLogic(float delta) {
        bossAttackTimer += delta;
        if (bossAttackTimer > 1.5f) {
            bossAttackTimer = 0;
            bossProjectiles.add(new Rectangle(bossRect.x + 10, bossRect.y, 10, 10));
            bossProjectiles.add(new Rectangle(bossRect.x + 30, bossRect.y, 10, 10));
        }

        for (int i = bossProjectiles.size() - 1; i >= 0; i--) {
            Rectangle r = bossProjectiles.get(i);
            r.y -= 200 * delta;
            if (r.y < 0)
                bossProjectiles.remove(i);
            else if (r.overlaps(new Rectangle(player.getX() - 15, player.getY() - 15, 30, 30))) {
                bossProjectiles.remove(i);
                GameState.instance.hp -= 10;
                redFlashTimer = 0.2f;
            }
        }

        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.SPACE)) {
            bullets.add(new Rectangle(player.getX(), player.getY() + 20, 5, 10));
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Rectangle b = bullets.get(i);
            b.y += 400 * delta;
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
    }

    private void updateAfterBossLogic(float delta) {
        Rectangle pBounds = new Rectangle(player.getX() - 25, player.getY() - 25, 50, 50);
        if (artifactRect != null && pBounds.overlaps(artifactRect)) {
            GameState.instance.hasNao = true;
            GameState.instance.libraryCleared = true;
            artifactRect = null;
        }
    }

    @Override
    protected void onDraw() {
        OrthographicCamera cam = gameCamera.getCamera();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);

        // Map handles background via mapRenderer in PlayScreen.render()

        if (!puzzleSolved) {
            puzzle.render(shapeRenderer, batch);
        } else if (!bossDefeated) {
            drawBossBattle();
        } else {
            if (artifactRect != null) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.GOLD);
                shapeRenderer.rect(artifactRect.x, artifactRect.y, artifactRect.width, artifactRect.height);
                shapeRenderer.end();
            }
        }

        if (redFlashTimer > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, 0.3f);
            shapeRenderer.rect(0, 0, 800, 600);
            shapeRenderer.end();
        }

        drawEntities();
    }

    private void drawBossBattle() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(bossRect.x, bossRect.y, bossRect.width, bossRect.height);

        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(300, 570, 200, 10);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(300, 570, (bossHp / 500f) * 200, 10);

        shapeRenderer.setColor(Color.PURPLE);
        for (Rectangle r : bossProjectiles)
            shapeRenderer.rect(r.x, r.y, r.width, r.height);

        shapeRenderer.setColor(Color.WHITE);
        for (Rectangle b : bullets)
            shapeRenderer.rect(b.x, b.y, b.width, b.height);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        if (puzzle != null)
            puzzle.dispose();
        super.dispose();
    }

    public OrthographicCamera getCamera() {
        return gameCamera.getCamera();
    }
}
