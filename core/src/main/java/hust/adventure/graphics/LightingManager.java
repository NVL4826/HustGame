package hust.adventure.graphics;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages the game's lighting system using Box2DLights. Decoupled from specific entities.
 */
public class LightingManager implements LightProvider, Disposable {
    private final World world;
    private final RayHandler rayHandler;

    public LightingManager() {
        // No gravity, only used for lighting grid
        this.world = new World(new Vector2(0, 0), true);
        this.rayHandler = new RayHandler(world);
        this.rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 1f);
        this.rayHandler.setBlur(true);
        this.rayHandler.setBlurNum(1);
        // Use diffuse light to avoid washing out the screen when ambient is high
        RayHandler.useDiffuseLight(true);
    }

    /**
     * Sets the ambient light for the current environment.
     */
    public void setAmbientLight(Color color) {
        if (color != null) {
            rayHandler.setAmbientLight(color);
        }
    }

    @Override
    public PointLight createPointLight(int rays, Color color, float distance, float x, float y) {
        PointLight light = new PointLight(rayHandler, rays, color, distance, x, y);
        light.setSoft(true);
        return light;
    }

    public void update() {
        // We do NOT call world.step() as physics calculations are not needed.
        rayHandler.update();
    }

    public void render(OrthographicCamera camera) {
        if (camera != null) {
            rayHandler.setCombinedMatrix(camera);
            rayHandler.render();
        }
    }

    @Override
    public void dispose() {
        rayHandler.dispose();
        world.dispose();
    }
}
