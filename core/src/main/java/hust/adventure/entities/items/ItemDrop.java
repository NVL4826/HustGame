package hust.adventure.entities.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.Player;
import hust.adventure.collision.Collider;

/**
 * Represents an item dropped in the world.
 */
public class ItemDrop extends BaseEntity {
    private String itemType;
    private Color color;

    public ItemDrop(float x, float y, String itemType, Color color) {
        super(x, y, 15, 15);
        this.itemType = itemType;
        this.color = color;
    }

    @Override
    public void setCollider(Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                if (other instanceof Player) {
                    ((Player) other).getInventory().addItem(itemType, 1);
                    destroy();
                }
            });
        }
    }

    @Override
    public void update(float delta) {
        // Items are stationary for now
    }

    @Override
    public void draw(SpriteBatch batch) {
        drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), color);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(color);
        sr.rect(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void dispose() {
        // No resources
    }

    public String getItemType() {
        return itemType;
    }
}
