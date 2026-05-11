package hust.adventure.entities.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface defining the behavior of a weapon.
 */
public interface Weaponable {
    /**
     * Attempts to trigger the weapon's attack logic.
     */
    void fire();

    /**
     * Upgrades the weapon with the given statistics.
     * @param nextLevelStats The stats for the next level.
     */
    void upgrade(WeaponStats nextLevelStats);

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
}
