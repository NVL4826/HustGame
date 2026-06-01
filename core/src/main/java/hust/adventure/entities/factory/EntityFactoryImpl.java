package hust.adventure.entities.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import hust.adventure.collision.Collider;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.CollisionManager;
import hust.adventure.core.assets.GameAssetManager;
import hust.adventure.core.data.EnemyDataManager;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.entities.enemies.EnemyConfig;
import hust.adventure.entities.interactables.ItemDrop;
import hust.adventure.entities.player.Player;
import hust.adventure.entities.interactables.ExpGem;
import hust.adventure.input.PlayerController;
import hust.adventure.inventory.Inventory;
import hust.adventure.items.base.Item;
import hust.adventure.entities.environment.FloatingBook;
import hust.adventure.entities.environment.Candle;
import hust.adventure.graphics.LightProvider;
import hust.adventure.entities.EntityManager;
import hust.adventure.utils.GamePools;

/**
 * Concrete implementation of the EntityFactory. Uses GamePools for high-frequency objects (Projectiles, ExpGems) and
 * regular instantiation for others.
 */
public class EntityFactoryImpl implements EntityFactory {
    private final GameAssetManager assetManager;
    private final EntityManager entityManager;
    private final CollisionManager collisionManager;
    private final EnemyDataManager enemyDataManager;
    private final Array<Texture> bookTextures = new Array<>();

    /**
     * Constructs the EntityFactoryImpl.
     *
     * @param assetManager     the global asset manager
     * @param entityManager    the global entity manager
     * @param collisionManager the spatial collision manager
     * @param enemyDataManager the configuration loader for enemies
     */
    public EntityFactoryImpl(final GameAssetManager assetManager, final EntityManager entityManager,
            final CollisionManager collisionManager, final EnemyDataManager enemyDataManager) {
        if (assetManager == null)
            throw new NullPointerException("assetManager cannot be null");
        if (entityManager == null)
            throw new NullPointerException("entityManager cannot be null");
        if (collisionManager == null)
            throw new NullPointerException("collisionManager cannot be null");
        if (enemyDataManager == null)
            throw new NullPointerException("enemyDataManager cannot be null");

        this.assetManager = assetManager;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.enemyDataManager = enemyDataManager;

        // Pre-cache book textures for easy random access
        for (int i = 19; i <= 29; i++)
            bookTextures.add(assetManager.getTexture("Phong_doc/" + i + ".png"));
        for (int i = 47; i <= 51; i++)
            bookTextures.add(assetManager.getTexture("Phong_doc/" + i + ".png"));
    }

    @Override
    public Player createPlayer(float x, float y, Inventory inventory, PlayerController controller) {
        Player player = new Player(x, y, inventory, controller, collisionManager, assetManager);
        player.setFactory(this);
        player.setCollider(new Collider(player, CollisionLayer.PLAYER, Collider.Shape.RECTANGLE));
        entityManager.addEntity(player);
        return player;
    }

    @Override
    public MapObject createEnemy(final String type, final float x, final float y) {
        final EnemyConfig config = enemyDataManager.getEnemyConfig(type);
        if (config == null) {
            throw new IllegalArgumentException("Unknown enemy type: " + type);
        }
        final Enemy enemy = new Enemy(x, y, collisionManager, config);

        if (config.getSpritePath() != null && !config.getSpritePath().isEmpty()) {
            try {
                final Texture texture = assetManager.getTexture(config.getSpritePath());
                if (texture != null) {
                    enemy.setStaticSprite(new TextureRegion(texture));
                }
            } catch (final Exception e) {
                com.badlogic.gdx.Gdx.app.error("EntityFactoryImpl",
                        "Failed to load static sprite: " + config.getSpritePath(), e);
            }
        }

        if (config.getAnimationFrames() != null && config.getAnimationFrames().length > 0) {
            try {
                final Array<TextureRegion> frames = new Array<>();
                for (final String framePath : config.getAnimationFrames()) {
                    final Texture texture = assetManager.getTexture(framePath);
                    if (texture != null) {
                        frames.add(new TextureRegion(texture));
                    }
                }
                if (frames.size > 0) {
                    final float dur = config.getFrameDuration() > 0 ? config.getFrameDuration() : 0.1f;
                    enemy.setAnimation(new Animation<>(dur, frames));
                }
            } catch (final Exception e) {
                com.badlogic.gdx.Gdx.app.error("EntityFactoryImpl", "Failed to load animation frames", e);
            }
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
            p.setCollider(p.getCollider());
        }
        entityManager.addEntity(p);
        return p;
    }

    @Override
    public void freeEntity(MapObject entity) {
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
    public MapObject createFloatingBook(float x, float y, LightProvider lightProvider) {
        if (bookTextures.size == 0)
            return null;
        Texture texture = bookTextures.random();
        FloatingBook book = new FloatingBook(x, y, texture, lightProvider);
        entityManager.addEntity(book);
        return book;
    }

    @Override
    public MapObject createCandle(float x, float y, LightProvider lightProvider) {
        Texture texture = assetManager.getTexture("Phong_doc/1.png");
        Candle candle = new Candle(x, y, texture, lightProvider);
        entityManager.addEntity(candle);
        return candle;
    }
}
