package hust.adventure.entities.components;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.Enemy;

/**
 * Interface for AI strategies (Strategy Pattern).
 */
public interface AIBehavior {
    void execute(Enemy enemy, float delta, Player player, EntityManager entityManager);
}
