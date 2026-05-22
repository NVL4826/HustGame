package hust.adventure.entities.factory;
 
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
 
import hust.adventure.collision.Collider;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.CollisionManager;
import hust.adventure.core.GameAssetManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.entities.enemies.*;
import hust.adventure.entities.components.SimpleSwarmBehavior;
import hust.adventure.entities.environment.LibraryArtifact;
import hust.adventure.entities.interactables.ItemDrop;
import hust.adventure.entities.interactables.ExpGem;
import hust.adventure.entities.interactables.TreasureChest;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;
import hust.adventure.entities.environment.FloatingBook;
import hust.adventure.entities.environment.Candle;
import hust.adventure.graphics.LightProvider;
import hust.adventure.entities.EntityManager;
import hust.adventure.utils.GamePools;
import hust.adventure.items.Item;
 
/**
 * Concrete implementation of the EntityFactory. Uses GamePools for high-frequency objects (Projectiles, ExpGems)
 * and regular instantiation for others.
 */
public class EntityFactoryImpl implements EntityFactory {
    private final GameAssetManager assetManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final Array<Texture> bookTextures = new Array<>();
 
    public EntityFactoryImpl(GameAssetManager assetManager, EntityManager entityManager,
            CollisionManager collisionManager) {
        if (assetManager == null)
            throw new NullPointerException("assetManager cannot be null");
        if (entityManager == null)
            throw new NullPointerException("entityManager cannot be null");
        if (collisionManager == null)
            throw new NullPointerException("collisionManager cannot be null");
 
        this.assetManager = assetManager;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
 
        // Pre-cache book textures for easy random access
        for (int i = 19; i <= 29; i++)
            bookTextures.add(assetManager.getTexture("Phong_doc/" + i + ".png"));
        for (int i = 47; i <= 51; i++)
            bookTextures.add(assetManager.getTexture("Phong_doc/" + i + ".png"));
    }
 
    @Override
    public Player createPlayer(float x, float y, Inventory inventory, PlayerController controller) {
        Player player = new Player(x, y, inventory, controller, collisionManager);
        player.setFactory(this);
        player.setCollider(new Collider(player, CollisionLayer.PLAYER, Collider.Shape.RECTANGLE));
        entityManager.addEntity(player);
        return player;
    }
 
    @Override
    public BaseEntity createEnemy(String type, float x, float y) {
        BaseEnemy enemy;
        switch (type.toLowerCase()) {
        case "syntax_error":
            enemy = new SyntaxErrorEnemy();
            enemy.init(x, y, 30, 30, 25, "SyntaxErr", Color.ORANGE, collisionManager, 0f);
            enemy.setSpeed(35f);
            enemy.setBehavior(new SimpleSwarmBehavior());
            break;
        case "null_pointer":
            enemy = new NullPointerEnemy();
            enemy.init(x, y, 32, 32, 30, "NullPtr", Color.GREEN, collisionManager, 10f);
            enemy.setSpeed(45f);
            enemy.setBehavior(new SimpleSwarmBehavior());
            break;
        case "infinite_loop":
            enemy = new InfiniteLoopEnemy();
            enemy.init(x, y, 32, 32, 200, "InfLoop", Color.PURPLE, collisionManager, 15f);
            enemy.setSpeed(30f);
            enemy.setBehavior(new SimpleSwarmBehavior());
            break;
        case "stack_overflow":
            enemy = new StackOverflowEnemy();
            enemy.init(x, y, 40, 40, 50, "StackOvfl", Color.RED, collisionManager, 20f);
            enemy.setSpeed(25f);
            enemy.setBehavior(new SimpleSwarmBehavior());
            break;
        default:
            throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
        enemy.setFactory(this);
        enemy.setCollider(new Collider(enemy, CollisionLayer.ENEMY, Collider.Shape.RECTANGLE));
        entityManager.addEntity(enemy);
        return enemy;
    }
 
    @Override
    public Projectile createProjectile(float x, float y, float vx, float vy, float damage, Color color,
            boolean isPlayer) {
        // Projectiles still use pooling
        Projectile p = GamePools.obtain(Projectile.class);
        p.init(x, y, vx, vy, damage, color, isPlayer);
        int layer = isPlayer ? CollisionLayer.PLAYER_BULLET : CollisionLayer.ENEMY_BULLET;
 
        if (p.getCollider() == null) {
            p.setCollider(new Collider(p, layer, Collider.Shape.RECTANGLE, 5f));
        } else {
            p.getCollider().setLayer(layer);
        }
        entityManager.addEntity(p);
        return p;
    }
 
    @Override
    public void freeEntity(BaseEntity entity) {
        // GamePools.free will only actually free if it's Projectile or ExpGem
        GamePools.free(entity);
    }
 
    @Override
    public ItemDrop createItemDrop(float x, float y, Item item, Color color) {
        ItemDrop itemDrop = new ItemDrop(x, y, item, color);
        itemDrop.setCollider(new Collider(itemDrop, CollisionLayer.ITEM, Collider.Shape.RECTANGLE));
        entityManager.addEntity(itemDrop);
        return itemDrop;
    }
 
    @Override
    public ExpGem createExpGem(float x, float y, float amount) {
        // ExpGems still use pooling
        ExpGem gem = GamePools.obtain(ExpGem.class);
        gem.init(x, y, amount);
        gem.setCollider(new Collider(gem, CollisionLayer.ITEM, Collider.Shape.RECTANGLE));
        entityManager.addEntity(gem);
        return gem;
    }
 
    @Override
    public TreasureChest createTreasureChest(float x, float y, Texture texture) {
        TreasureChest chest = new TreasureChest();
        chest.init(x, y, texture);
        chest.setCollider(new Collider(chest, CollisionLayer.ITEM, Collider.Shape.RECTANGLE));
        entityManager.addEntity(chest);
        return chest;
    }
 
    @Override
    public BaseEntity createLibraryBoss(float x, float y) {
        LibraryBoss boss = new LibraryBoss();
        boss.init(x, y, 64, 64, 500, "LibBoss", Color.CYAN, collisionManager, 30f);
        boss.setSpeed(35f);
        boss.setBehavior(new SimpleSwarmBehavior());
        boss.setFactory(this);
        boss.setCollider(new Collider(boss, CollisionLayer.ENEMY, Collider.Shape.RECTANGLE));
        entityManager.addEntity(boss);
        return boss;
    }
 
    @Override
    public BaseEntity createLibraryArtifact(float x, float y) {
        BaseEntity artifact = new LibraryArtifact(x, y);
        artifact.setCollider(new Collider(artifact, CollisionLayer.ITEM, Collider.Shape.RECTANGLE));
        entityManager.addEntity(artifact);
        return artifact;
    }
 
    @Override
    public FinalBoss createFinalBoss(float x, float y, Texture texture) {
        FinalBoss boss = new FinalBoss();
        boss.init(x, y, 100, 100, 2000, "FinalBoss", Color.WHITE, collisionManager, 50f);
        boss.setTexture(texture);
        boss.setFactory(this);
        boss.setCollider(new Collider(boss, CollisionLayer.ENEMY, Collider.Shape.RECTANGLE));
        entityManager.addEntity(boss);
        return boss;
    }
 
    @Override
    public BaseEntity createFloatingBook(float x, float y, LightProvider lightProvider) {
        if (bookTextures.size == 0)
            return null;
        Texture texture = bookTextures.random();
        FloatingBook book = new FloatingBook(x, y, texture, lightProvider);
        entityManager.addEntity(book);
        return book;
    }
 
    @Override
    public BaseEntity createCandle(float x, float y, LightProvider lightProvider) {
        Texture texture = assetManager.getTexture("Phong_doc/1.png");
        Candle candle = new Candle(x, y, texture, lightProvider);
        entityManager.addEntity(candle);
        return candle;
    }
}
