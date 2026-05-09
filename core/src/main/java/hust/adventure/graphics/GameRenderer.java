package hust.adventure.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.status.StatusFlag;
import hust.adventure.ui.HUD;
import hust.adventure.ui.InventoryUI;
import hust.adventure.world.LibrarySystem;

/**
 * Centralized renderer for the game, responsible for map, entities, and UI.
 */
public class GameRenderer {
    private final CameraManager cameraManager;
    private final EntityManager entityManager;
    private final SpriteBatch batch;
    private final ShaderProgram silhouetteShader;
    private final ShaderProgram discardShader;
    private final HUD hud;
    private final InventoryUI inventoryUI;
    private static final Matrix4 uiMatrix = new Matrix4();

    public GameRenderer(final CameraManager cameraManager, final EntityManager entityManager, final SpriteBatch batch,
            final ShaderProgram silhouetteShader, final ShaderProgram discardShader, final HUD hud,
            final InventoryUI inventoryUI) {
        this.cameraManager = cameraManager;
        this.entityManager = entityManager;
        this.batch = batch;
        this.silhouetteShader = silhouetteShader;
        this.discardShader = discardShader;
        this.hud = hud;
        this.inventoryUI = inventoryUI;
    }

    /**
     * Main render pass.
     */
    public void render(final float delta, final OrthogonalTiledMapRenderer mapRenderer, final int[] backgroundLayers,
            final int[] foregroundLayers, final Player player, final ShapeRenderer shapeRenderer, final BitmapFont font,
            final LibrarySystem librarySystem) {

        if (cameraManager != null) {
            cameraManager.update();
        }

        if (mapRenderer == null) {
            return;
        }

        // 0. Xóa màu nền, Depth Buffer và Stencil Buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        mapRenderer.setView(cameraManager.getCamera());

        // 1. Vẽ Background map
        mapRenderer.render(backgroundLayers);

        // 2. Vẽ Nhân vật
        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();

        // 2.5 Vẽ thêm sách lơ lửng nếu ở thư viện
        if (librarySystem != null) {
            batch.setProjectionMatrix(cameraManager.getCamera().combined);
            librarySystem.render(batch);
        }

        // 3. Stencil Buffer cho Foreground
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glStencilMask(0xFF);

        mapRenderer.getBatch().setShader(discardShader);
        mapRenderer.render(foregroundLayers);
        mapRenderer.getBatch().setShader(null);

        // 4. Vẽ Silhouette nhân vật
        Gdx.gl.glStencilMask(0x00);
        Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 1, 0xFF);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.setShader(silhouetteShader);
        batch.begin();
        entityManager.draw(batch);
        batch.end();
        batch.setShader(null);

        // Phục hồi OpenGL
        Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 5. Thêm lớp chiếu sáng của thư viện
        if (librarySystem != null) {
            librarySystem.renderLights(cameraManager.getCamera());
        }

        // 6. Render UI
        renderUI(batch, shapeRenderer, font, player);
    }

    private void renderUI(final SpriteBatch batch, final ShapeRenderer shapeRenderer, final BitmapFont font,
            final Player player) {
        if (hud != null) {
            hud.render(batch, shapeRenderer, font);
        }
        if (inventoryUI != null && player != null) {
            inventoryUI.render(player, batch, shapeRenderer, font);
        }

        if (player != null && player.hasStatus(StatusFlag.CONFUSED)) {
            drawVignette(shapeRenderer);
        }
    }

    private void drawVignette(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Use a UI projection for the overlay
        uiMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.setProjectionMatrix(uiMatrix);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.4f, 0.2f, 0f, 0.2f)); // Brown tint
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public CameraManager getcameraManager() {
        return cameraManager;
    }
}
