package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EntityManager {
    private Array<Entity> entities;

    public EntityManager() {
        entities = new Array<>();
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.removeValue(entity, true);
    }

    public void update(float delta) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update(delta);

            if (entity.isDestroyed()) {
                entity.dispose();
                entities.removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        // Here we could implement Y-sorting as assigned in Ticket 2.2, 
        // but for now, we just draw them as they came.
        for (Entity entity : entities) {
            entity.draw(batch);
        }
    }

    public void dispose() {
        for (Entity entity : entities) {
            entity.dispose();
        }
        entities.clear();
    }
}
