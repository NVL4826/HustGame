package hust.adventure.items.weapons;

import com.badlogic.gdx.graphics.Color;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.Direction;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;

/**
 * A weapon that fires a projectile in the player's current looking direction when the Spacebar key is pressed.
 */
public class BunDauWeapon extends BaseWeapon {
    private static final float PROJECTILE_SPEED = 350f;
    private static final Color PROJECTILE_COLOR = Color.YELLOW;

    public BunDauWeapon(final Player owner, final float baseDamage, final float cooldown, final float area) {
        super(owner, "bun_dau", "Bun Dau", "Bắn một viên đậu theo hướng nhìn hiện tại khi nhấn phím Space.", baseDamage,
                cooldown, area);
    }

    @Override
    public boolean isAutoFiring() {
        return false;
    }

    @Override
    protected void executeAttackAction() {
        final Direction dir = getOwner().getDirection();
        float vx = 0f;
        float vy = 0f;

        if (dir != null) {
            switch (dir) {
            case RIGHT:
                vx = PROJECTILE_SPEED;
                break;
            case LEFT:
                vx = -PROJECTILE_SPEED;
                break;
            case UP:
                vy = PROJECTILE_SPEED;
                break;
            case DOWN:
                vy = -PROJECTILE_SPEED;
                break;
            }
        }

        if (getOwner().getFactory() != null) {
            getOwner().getFactory().createProjectile(getOwner().getX(), getOwner().getY(), vx, vy, getBaseDamage(),
                    PROJECTILE_COLOR, true);
            EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/shoot.wav"));
        }
    }
}
