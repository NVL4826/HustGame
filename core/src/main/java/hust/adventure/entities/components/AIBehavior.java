package hust.adventure.entities.components;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.BaseEnemy;

/**
 * Interface for AI strategies (Strategy Pattern).
 */
public interface AIBehavior {
    void execute(BaseEnemy enemy, float delta, Player player, EntityManager entityManager);
}
