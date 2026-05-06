package vn.hust.hustgame.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import vn.hust.hustgame.entities.EntityManager;
import vn.hust.hustgame.world.GameCamera;
import vn.hust.hustgame.world.LibrarySystem;

public class GameRenderer {
    private GameCamera gameCamera;
    private EntityManager entityManager;
    private SpriteBatch batch;
    private ShaderProgram silhouetteShader;
    private ShaderProgram discardShader;

    public GameRenderer(GameCamera gameCamera, EntityManager entityManager, SpriteBatch batch,
                        ShaderProgram silhouetteShader, ShaderProgram discardShader) {
        this.gameCamera = gameCamera;
        this.entityManager = entityManager;
        this.batch = batch;
        this.silhouetteShader = silhouetteShader;
        this.discardShader = discardShader;
    }

    public void render(OrthogonalTiledMapRenderer mapRenderer, int[] backgroundLayers, int[] foregroundLayers, LibrarySystem librarySystem) {
        if (mapRenderer == null) return;

        // 0. Xóa màu nền, Depth Buffer và Stencil Buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

        mapRenderer.setView(gameCamera.getCamera());

        // 1. Vẽ Background map
        mapRenderer.render(backgroundLayers);

        // 2. Vẽ Nhân vật
        batch.setProjectionMatrix(gameCamera.getCamera().combined);
        batch.begin();
        entityManager.draw(batch);
        batch.end();

        // 2.5 Vẽ thêm sách lơ lửng nếu ở thư viện
        if (librarySystem != null) {
            batch.setProjectionMatrix(gameCamera.getCamera().combined);
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
            librarySystem.renderLights(gameCamera.getCamera());
        }
    }
}
