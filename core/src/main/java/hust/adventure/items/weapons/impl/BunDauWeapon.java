package hust.adventure.items.weapons.impl;

import com.badlogic.gdx.graphics.Color;

import hust.adventure.entities.Projectile;
import hust.adventure.entities.base.Direction;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.weapons.WeaponConfig;
import hust.adventure.entities.player.Player;

/**
 * A weapon that fires a projectile in the player's current looking direction when the Spacebar key is pressed.
 */
public class BunDauWeapon extends BaseWeapon {
    private static final float PROJECTILE_SPEED = 350f;
    private static final Color PROJECTILE_COLOR = Color.YELLOW;

    public BunDauWeapon(final Player owner, final WeaponConfig config) {
        super(owner, config);
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
            final Projectile projectile = getOwner().getFactory().createProjectile(getOwner().getX(), getOwner().getY(),
                    vx, vy, getEffectiveDamage(), PROJECTILE_COLOR, true);
            if (projectile != null) {
                projectile.setPierce(getPierce());
            }
            EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/player/Player Firing Bun Dau.wav"));
        }
    }

}
