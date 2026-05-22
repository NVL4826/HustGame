package hust.adventure.entities.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.items.Item;

/**
 * Interface defining the behavior of a weapon.
 */
public interface Weaponable extends Item {
    /**
     * Attempts to trigger the weapon's attack logic.
     */
    void fire();

    /**
     * Upgrades the weapon by applying stat bonuses.
     * @param damageBonus Flat damage increase.
     * @param cooldownReduction Cooldown reduction amount.
     */
    void upgrade(float damageBonus, float cooldownReduction);

    /**
     * Updates the weapon's internal timers.
     * @param deltaTime Time elapsed since last frame.
     */
    void updateTimer(float deltaTime);

    /**
     * Draws any visual effects associated with the weapon.
     * @param batch The SpriteBatch to use.
     */
    void draw(SpriteBatch batch);
    
    /**
     * Gets the current level of the weapon.
     * @return The level.
     */
    int getLevel();

    /**
     * Gets the unique name of the weapon type.
     */
    String getName();
}
