package hust.adventure.entities.weapons;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.Direction;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.enemies.BaseEnemy;

/**
 * A whip weapon that hits enemies in a rectangular area in front of the player.
 */
public class WhipWeapon extends BaseWeapon {
    private final Rectangle hitArea;
    private static final float WHIP_WIDTH = 80f;
    private static final float WHIP_HEIGHT = 40f;
    private float flashTimer = 0f;
    private static final float FLASH_DURATION = 0.15f;

    public WhipWeapon(final Player owner, final float baseDamage, final float cooldown, final float area) {
        super(owner, "whip", "Roi da", "Tấn công kẻ địch trước mặt theo hình chữ nhật.", baseDamage, cooldown, area);
        this.hitArea = new Rectangle();
    }

    @Override
    protected void executeAttackAction() {
        updateHitArea();
        flashTimer = FLASH_DURATION;
        
        final Array<GameEntity> targets = owner.getCollisionManager().getEntitiesInArea(hitArea, CollisionLayer.ENEMY);
        for (final GameEntity target : targets) {
            if (target instanceof BaseEnemy) {
                ((BaseEnemy) target).takeDamage(baseDamage);
            }
        }
    }

    @Override
    public void updateTimer(float delta) {
        super.updateTimer(delta);
        if (flashTimer > 0) {
            flashTimer -= delta;
        }
    }

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        if (flashTimer > 0) {
            // Draw a semi-transparent rectangle for the whip effect
            owner.drawRect(batch, hitArea.x, hitArea.y, hitArea.width, hitArea.height, new com.badlogic.gdx.graphics.Color(1, 1, 1, 0.5f));
        }
    }

    private void updateHitArea() {
        final float px = owner.getX();
        final float py = owner.getY();
        final Direction dir = owner.getDirection();

        switch (dir) {
            case RIGHT:
                hitArea.set(px + owner.getWidth() / 2f, py - WHIP_HEIGHT / 2f, WHIP_WIDTH, WHIP_HEIGHT);
                break;
            case LEFT:
                hitArea.set(px - owner.getWidth() / 2f - WHIP_WIDTH, py - WHIP_HEIGHT / 2f, WHIP_WIDTH, WHIP_HEIGHT);
                break;
            case UP:
                hitArea.set(px - WHIP_HEIGHT / 2f, py + owner.getHeight() / 2f, WHIP_HEIGHT, WHIP_WIDTH);
                break;
            case DOWN:
                hitArea.set(px - WHIP_HEIGHT / 2f, py - owner.getHeight() / 2f - WHIP_WIDTH, WHIP_HEIGHT, WHIP_WIDTH);
                break;
        }
    }
}
