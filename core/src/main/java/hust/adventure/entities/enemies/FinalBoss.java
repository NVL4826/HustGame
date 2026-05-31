package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;

/**
 * Entity representing the final boss (T.H.T) in the Boss Room.
 */
public class FinalBoss extends Enemy {
    private Texture texture;

    /**
     * Default constructor.
     */
    public FinalBoss() {
        super();
    }

    /**
     * Constructs the FinalBoss using a texture and its configuration data.
     *
     * @param x                horizontal spawn position
     * @param y                vertical spawn position
     * @param collisionManager standard collision manager
     * @param texture          the boss's sprite texture
     * @param config           the loaded configuration parameters
     */
    public FinalBoss(final float x, final float y, final CollisionManager collisionManager, final Texture texture,
            final EnemyConfig config) {
        super(x, y, collisionManager, config);
        this.texture = texture;
    }

    /**
     * Sets the texture of the boss.
     *
     * @param texture the texture to use
     */
    public void setTexture(final Texture texture) {
        this.texture = texture;
    }

    @Override
    public void handleUpdate(final float delta, final Player player, final EntityManager entityManager) {
        // Boss logic (stationary for now)
    }

    @Override
    protected void renderSpecific(final SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public boolean isBoss() {
        return true;
    }
}
