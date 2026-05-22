package hust.adventure.entities.weapons;

import com.badlogic.gdx.utils.Array;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.enemies.BaseEnemy;

/**
 * A garlic weapon that creates an aura damaging all nearby enemies periodically.
 */
public class GarlicAuraWeapon extends BaseWeapon {

    public GarlicAuraWeapon(final Player owner, final float baseDamage, final float cooldown, final float area) {
        super(owner, "garlic", "Tỏi bảo hộ", "Tạo vòng hào quang gây sát thương xung quanh.", baseDamage, cooldown, area);
    }

    @Override
    protected void executeAttackAction() {
        final float radius = area; // Using 'area' stat as radius
        final Array<GameEntity> targets = owner.getCollisionManager().getEntitiesInRadius(
                owner.getX(), owner.getY(), radius, CollisionLayer.ENEMY);

        for (final GameEntity target : targets) {
            if (target instanceof BaseEnemy) {
                ((BaseEnemy) target).takeDamage(baseDamage);
            }
        }
    }
}
