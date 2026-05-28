package hust.adventure.items.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.Direction;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.base.Damageable;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.GameEvent;
import hust.adventure.events.EventType;

/**
 * A whip weapon that hits enemies in a rectangular area in front of the player.
 * Supports area scaling and burst strikes in alternating directions.
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
        final boolean isSecondStrike = (getAmount() > 1 && getShotsRemaining() == 1);
        updateHitArea(isSecondStrike);
        flashTimer = FLASH_DURATION;
        EventDispatcher.getInstance().dispatch(
            new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/whip.wav")
        );

        final Array<GameEntity> targets = getOwner().getCollisionManager().getEntitiesInArea(hitArea, CollisionLayer.ENEMY);
        for (final GameEntity target : targets) {
            if (target instanceof Damageable) {
                ((Damageable) target).takeDamage(getEffectiveDamage());
            }
        }
    }

    @Override
    public void updateTimer(final float delta) {
        super.updateTimer(delta);
        if (flashTimer > 0) {
            flashTimer -= delta;
        }
    }

    @Override
    public void draw(final SpriteBatch batch) {
        if (flashTimer > 0) {
            // Draw a semi-transparent rectangle for the whip effect
            getOwner().drawRect(batch, hitArea.x, hitArea.y, hitArea.width, hitArea.height, new Color(1, 1, 1, 0.5f));
        }
    }

    private Direction getOppositeDirection(final Direction dir) {
        if (dir == null) {
            return Direction.DOWN;
        }
        switch (dir) {
            case RIGHT: return Direction.LEFT;
            case LEFT: return Direction.RIGHT;
            case UP: return Direction.DOWN;
            case DOWN: return Direction.UP;
            default: return Direction.DOWN;
        }
    }

    private void updateHitArea(final boolean isSecondStrike) {
        final float px = getOwner().getX();
        final float py = getOwner().getY();
        Direction dir = getOwner().getDirection();
        if (isSecondStrike) {
            dir = getOppositeDirection(dir);
        }

        final float scale = getArea();
        final float w = WHIP_WIDTH * scale;
        final float h = WHIP_HEIGHT * scale;

        switch (dir) {
        case RIGHT:
            hitArea.set(px + getOwner().getWidth() / 2f, py - h / 2f, w, h);
            break;
        case LEFT:
            hitArea.set(px - getOwner().getWidth() / 2f - w, py - h / 2f, w, h);
            break;
        case UP:
            hitArea.set(px - h / 2f, py + getOwner().getHeight() / 2f, h, w);
            break;
        case DOWN:
            hitArea.set(px - h / 2f, py - getOwner().getHeight() / 2f - w, h, w);
            break;
        }
    }

    @Override
    public void upgrade(final float damageBonus, final float cooldownReduction) {
        if (getLevel() >= 5) {
            return;
        }
        super.upgrade(damageBonus, cooldownReduction);
        applyLevelStats();
    }

    private void applyLevelStats() {
        switch (getLevel()) {
            case 2:
                setAmount(2);
                break;
            case 3:
                setBaseDamage(15f);
                break;
            case 4:
                setArea(1.1f); // area +10%
                setBaseDamage(20f);
                break;
            case 5:
                setBaseDamage(25f);
                break;
        }
    }
}
