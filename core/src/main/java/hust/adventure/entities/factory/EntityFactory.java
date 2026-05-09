package hust.adventure.entities.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;
import hust.adventure.entities.items.ItemDrop;
import hust.adventure.entities.enemies.FinalBoss;

/**
 * Abstract Factory interface for entity creation.
 */
public interface EntityFactory {
    Player createPlayer(float x, float y, Inventory inventory, PlayerController controller, CollisionManager collisionManager);

    BaseEntity createEnemy(String type, float x, float y, CollisionManager collisionManager);

    Projectile createProjectile(float x, float y, float vx, float vy, float damage, Color color, boolean isPlayer);

    ItemDrop createItemDrop(float x, float y, String type, Color color);

    BaseEntity createLibraryBoss(float x, float y, CollisionManager collisionManager);

    BaseEntity createLibraryArtifact(float x, float y);

    FinalBoss createFinalBoss(float x, float y, CollisionManager collisionManager, Texture texture);
}
