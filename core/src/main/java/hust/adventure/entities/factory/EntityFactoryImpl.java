package hust.adventure.entities.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import hust.adventure.collision.Collider;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.CollisionManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.entities.enemies.*;
import hust.adventure.entities.environment.LibraryArtifact;
import hust.adventure.entities.items.ItemDrop;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;

/**
 * Concrete implementation of the EntityFactory.
 */
public class EntityFactoryImpl implements EntityFactory {

    @Override
    public Player createPlayer(float x, float y, Inventory inventory, PlayerController controller, CollisionManager collisionManager) {
        Player player = new Player(x, y, inventory, controller, collisionManager);
        player.setCollider(new Collider(player, CollisionLayer.PLAYER, Collider.Shape.CIRCLE));
        return player;
    }

    @Override
    public BaseEntity createEnemy(String type, float x, float y, CollisionManager collisionManager) {
        BaseEnemy enemy;
        switch (type.toLowerCase()) {
        case "syntax_error":
            enemy = new SyntaxErrorEnemy(x, y, collisionManager);
            break;
        case "null_pointer":
            enemy = new NullPointerEnemy(x, y, collisionManager);
            break;
        case "infinite_loop":
            enemy = new InfiniteLoopEnemy(x, y, collisionManager);
            break;
        case "stack_overflow":
            enemy = new StackOverflowEnemy(x, y, collisionManager);
            break;
        default:
            throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
        enemy.setFactory(this);
        enemy.setCollider(new Collider(enemy, CollisionLayer.ENEMY, Collider.Shape.RECTANGLE));
        return enemy;
    }

    @Override
    public Projectile createProjectile(float x, float y, float vx, float vy, float damage, Color color,
            boolean isPlayer) {
        Projectile p = new Projectile(x, y, vx, vy, damage, color, isPlayer);
        int layer = isPlayer ? CollisionLayer.PLAYER_BULLET : CollisionLayer.ENEMY_BULLET;
        p.setCollider(new Collider(p, layer, Collider.Shape.CIRCLE, 5f));
        return p;
    }

    @Override
    public ItemDrop createItemDrop(float x, float y, String type, Color color) {
        ItemDrop item = new ItemDrop(x, y, type, color);
        item.setCollider(new Collider(item, CollisionLayer.ITEM, Collider.Shape.RECTANGLE));
        return item;
    }

    @Override
    public BaseEntity createLibraryBoss(float x, float y, CollisionManager collisionManager) {
        LibraryBoss boss = new LibraryBoss(x, y, collisionManager);
        boss.setFactory(this);
        boss.setCollider(new Collider(boss, CollisionLayer.ENEMY, Collider.Shape.RECTANGLE));
        return boss;
    }

    @Override
    public BaseEntity createLibraryArtifact(float x, float y) {
        BaseEntity artifact = new LibraryArtifact(x, y);
        artifact.setCollider(new Collider(artifact, CollisionLayer.ITEM, Collider.Shape.RECTANGLE));
        return artifact;
    }

    @Override
    public FinalBoss createFinalBoss(float x, float y, CollisionManager collisionManager, Texture texture) {
        FinalBoss boss = new FinalBoss(x, y, collisionManager, texture);
        boss.setFactory(this);
        boss.setCollider(new Collider(boss, CollisionLayer.ENEMY, Collider.Shape.RECTANGLE));
        return boss;
    }
}
