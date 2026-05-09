package hust.adventure.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import hust.adventure.entities.base.GameEntity;
import java.util.Comparator;

/**
 * Manages the lifecycle, updates, and rendering of all game entities. Implements Y-sorting for 2D top-down perspective.
 */
public class EntityManager implements Disposable {
    private final Array<GameEntity> entities;
    private final Array<GameEntity> pendingAdd;
    private final Comparator<GameEntity> yComparator;

    public EntityManager() {
        this.entities = new Array<>();
        this.pendingAdd = new Array<>();
        this.yComparator = (e1, e2) -> Float.compare(e2.getY(), e1.getY());
    }

    public void addEntity(final GameEntity entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null");
        pendingAdd.add(entity);
    }

    public void update(final float delta) {
        // Process pending additions
        if (pendingAdd.size > 0) {
            entities.addAll(pendingAdd);
            pendingAdd.clear();
        }

        // Update and cleanup destroyed entities
        for (int i = entities.size - 1; i >= 0; i--) {
            final GameEntity entity = entities.get(i);
            entity.update(delta);

            if (entity.isDestroyed()) {
                entity.dispose();
                entities.removeIndex(i);
            }
        }
    }

    public void draw(final SpriteBatch batch) {
        // Y-sorting for depth perception
        entities.sort(yComparator);

        for (final GameEntity entity : entities) {
            entity.draw(batch);
        }
    }

    @Override
    public void dispose() {
        for (final GameEntity entity : entities) {
            entity.dispose();
        }
        entities.clear();
        pendingAdd.clear();
    }

    public Array<GameEntity> getEntities() {
        return entities;
    }
}
