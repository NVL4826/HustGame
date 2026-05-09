package hust.adventure.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.environment.WallEntity;

import java.util.List;

/**
 * High-performance collision system using Spatial Hashing and a Bitmask Collision Matrix. This class combines the
 * functionality of the former CollisionManager and CollisionSystem.
 */
public class CollisionManager {
    private final float cellSize;
    private final ObjectMap<Long, Array<Collider>> grid;
    private final Pool<Array<Collider>> arrayPool;
    private final int[] collisionMatrix;
    private final EntityManager entityManager;
    private final Array<GameEntity> allCollidables;
    private final Array<WallEntity> staticWalls;
    private final Rectangle tempRect;
    private float mapWidth, mapHeight;

    public CollisionManager(final EntityManager entityManager, final float cellSize) {
        if (entityManager == null) {
            throw new IllegalArgumentException("EntityManager cannot be null");
        }
        this.entityManager = entityManager;
        this.cellSize = cellSize;
        this.grid = new ObjectMap<>();
        this.allCollidables = new Array<>();
        this.staticWalls = new Array<>();
        this.tempRect = new Rectangle();
        this.arrayPool = new Pool<Array<Collider>>() {
            @Override
            protected Array<Collider> newObject() {
                return new Array<>();
            }
        };
        this.collisionMatrix = new int[32];
        initCollisionMatrix();
    }

    /**
     * Sets the current map and its static walls.
     * 
     * @param map   The current TiledMap.
     * @param walls The list of static walls in the map.
     */
    public void setMap(final TiledMap map, final List<WallEntity> walls) {
        this.staticWalls.clear();
        if (walls != null) {
            for (final WallEntity wall : walls) {
                this.staticWalls.add(wall);
            }
        }

        if (map != null) {
            final Integer w = map.getProperties().get("width", Integer.class);
            final Integer th = map.getProperties().get("tilewidth", Integer.class);
            final Integer h = map.getProperties().get("height", Integer.class);
            final Integer tv = map.getProperties().get("tileheight", Integer.class);
            if (w != null && th != null)
                this.mapWidth = w * th;
            if (h != null && tv != null)
                this.mapHeight = h * tv;
        }
    }

    /**
     * Checks if an entity can move to a specific position without colliding with the map or static walls.
     * 
     * @param entity The entity attempting to move.
     * @param nextX  The target X coordinate.
     * @param nextY  The target Y coordinate.
     * @return True if the move is valid, false otherwise.
     */
    public boolean canMove(final GameEntity entity, final float nextX, final float nextY) {
        // 1. Boundary check
        if (nextX < entity.getWidth() / 2f || nextX > mapWidth - entity.getWidth() / 2f
                || nextY < entity.getHeight() / 2f || nextY > mapHeight - entity.getHeight() / 2f) {
            return false;
        }

        // 2. Wall collision check
        final Rectangle collisionBox = getCollisionBox(entity, nextX, nextY, tempRect);

        for (int i = 0; i < staticWalls.size; i++) {
            if (collisionBox.overlaps(staticWalls.get(i).getBounds())) {
                return false;
            }
        }

        return true;
    }

    private Rectangle getCollisionBox(final GameEntity entity, final float x, final float y, final Rectangle out) {
        if (entity instanceof BaseActor) {
            // Use feet-based collision box for actors
            final float feetWidth = entity.getWidth() * 0.4f;
            final float feetHeight = entity.getHeight() * 0.2f;
            out.set(x - feetWidth / 2f, y - entity.getHeight() / 2f, feetWidth, feetHeight);
        } else {
            // Use default bounds for other entities
            out.set(x - entity.getWidth() / 2f, y - entity.getHeight() / 2f, entity.getWidth(), entity.getHeight());
        }
        return out;
    }

    private void initCollisionMatrix() {
        // Player collides with Enemy, EnemyBullet, Wall, Item
        collisionMatrix[log2(CollisionLayer.PLAYER)] = CollisionLayer.ENEMY | CollisionLayer.ENEMY_BULLET
                | CollisionLayer.WALL | CollisionLayer.ITEM;

        // Enemy collides with Player, PlayerBullet, Wall
        collisionMatrix[log2(CollisionLayer.ENEMY)] = CollisionLayer.PLAYER | CollisionLayer.PLAYER_BULLET
                | CollisionLayer.WALL;

        // PlayerBullet collides with Enemy, Wall
        collisionMatrix[log2(CollisionLayer.PLAYER_BULLET)] = CollisionLayer.ENEMY | CollisionLayer.WALL;

        // EnemyBullet collides with Player, Wall
        collisionMatrix[log2(CollisionLayer.ENEMY_BULLET)] = CollisionLayer.PLAYER | CollisionLayer.WALL;

        // Wall (Passive)
        collisionMatrix[log2(CollisionLayer.WALL)] = CollisionLayer.PLAYER | CollisionLayer.ENEMY
                | CollisionLayer.PLAYER_BULLET | CollisionLayer.ENEMY_BULLET;

        // Item collides with Player
        collisionMatrix[log2(CollisionLayer.ITEM)] = CollisionLayer.PLAYER;
    }

    private int log2(final int bits) {
        if (bits == 0)
            return 0;
        return Integer.numberOfTrailingZeros(bits);
    }

    /**
     * Main update method for the collision system.
     * 
     * @param walls List of static walls to include in collision checks.
     */
    public void update(final List<WallEntity> walls) {
        allCollidables.clear();

        // Add dynamic entities
        final Array<GameEntity> entities = entityManager.getEntities();
        for (int i = 0; i < entities.size; i++) {
            allCollidables.add(entities.get(i));
        }

        // Add static walls
        if (walls != null) {
            for (final WallEntity wall : walls) {
                allCollidables.add(wall);
            }
        }

        rebuildGrid(allCollidables);
        checkCollisions();
    }

    private void rebuildGrid(final Array<GameEntity> entities) {
        // Return existing arrays to the pool
        for (final Array<Collider> cell : grid.values()) {
            cell.clear();
            arrayPool.free(cell);
        }
        grid.clear();

        for (int i = 0; i < entities.size; i++) {
            final GameEntity entity = entities.get(i);
            if (entity.isDestroyed()) {
                continue;
            }

            final Collider collider = entity.getCollider();
            if (collider == null) {
                continue;
            }

            final int cellX = (int) (entity.getX() / cellSize);
            final int cellY = (int) (entity.getY() / cellSize);

            addColliderToCell(cellX, cellY, collider);

            // Handle objects overlapping multiple cells
            if (collider.getShape() == Collider.Shape.RECTANGLE) {
                final float x2 = entity.getX() + entity.getWidth();
                final float y2 = entity.getY() + entity.getHeight();
                final int cellX2 = (int) (x2 / cellSize);
                final int cellY2 = (int) (y2 / cellSize);

                for (int x = cellX; x <= cellX2; x++) {
                    for (int y = cellY; y <= cellY2; y++) {
                        if (x != cellX || y != cellY) {
                            addColliderToCell(x, y, collider);
                        }
                    }
                }
            }
        }
    }

    private void addColliderToCell(final int x, final int y, final Collider collider) {
        final long key = hash(x, y);
        Array<Collider> cell = grid.get(key);
        if (cell == null) {
            cell = arrayPool.obtain();
            grid.put(key, cell);
        }
        cell.add(collider);
    }

    private long hash(final int x, final int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    private void checkCollisions() {
        for (final Array<Collider> cellContent : grid.values()) {
            for (int i = 0; i < cellContent.size; i++) {
                final Collider c1 = cellContent.get(i);
                for (int j = i + 1; j < cellContent.size; j++) {
                    final Collider c2 = cellContent.get(j);

                    if (canCollide(c1, c2)) {
                        if (c1.intersects(c2)) {
                            try {
                                c1.handleCollision(c2.getOwner());
                                c2.handleCollision(c1.getOwner());
                            } catch (Exception e) {
                                Gdx.app.error("CollisionManager", "Error handling collision", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean canCollide(final Collider c1, final Collider c2) {
        final int layer1 = c1.getLayer();
        final int layer2 = c2.getLayer();

        return (collisionMatrix[log2(layer1)] & layer2) != 0;
    }
}
