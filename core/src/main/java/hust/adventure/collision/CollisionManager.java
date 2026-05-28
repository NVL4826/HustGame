package hust.adventure.collision;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import hust.adventure.entities.EntityManager;
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
    private final int[] collisionMatrix;
    private final EntityManager entityManager;
    private final Array<GameEntity> allCollidables;
    private final Array<WallEntity> staticWalls;
    private final Rectangle tempRect;
    private float mapWidth, mapHeight;
    private boolean isInfinite;

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
            // Support both Integer and Float properties, with a default of 0 if missing
            Object w = map.getProperties().get("width");
            Object h = map.getProperties().get("height");
            Object th = map.getProperties().get("tilewidth");
            Object tv = map.getProperties().get("tileheight");

            float width = 0, height = 0, tileW = 0, tileH = 0;

            if (w instanceof Integer)
                width = (Integer) w;
            else if (w instanceof Float)
                width = (Float) w;
            if (h instanceof Integer)
                height = (Integer) h;
            else if (h instanceof Float)
                height = (Float) h;
            if (th instanceof Integer)
                tileW = (Integer) th;
            else if (th instanceof Float)
                tileW = (Float) th;
            if (tv instanceof Integer)
                tileH = (Integer) tv;
            else if (tv instanceof Float)
                tileH = (Float) tv;

            if (width > 0 && tileW > 0)
                this.mapWidth = width * tileW;
            else
                this.mapWidth = 2000f; // Default fallback

            if (height > 0 && tileH > 0)
                this.mapHeight = height * tileH;
            else
                this.mapHeight = 2000f; // Default fallback

            Gdx.app.log("CollisionManager", "Map bounds set to: " + mapWidth + "x" + mapHeight);
        }
    }

    public void setInfinite(boolean infinite) {
        this.isInfinite = infinite;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
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
        if (!isInfinite && (nextX < entity.getWidth() / 2f || nextX > mapWidth - entity.getWidth() / 2f
                || nextY < entity.getHeight() / 2f || nextY > mapHeight - entity.getHeight() / 2f)) {
            return false;
        }

        // 2. Wall collision check
        final Rectangle collisionBox = entity.getMovementBounds(nextX, nextY, tempRect);

        for (int i = 0; i < staticWalls.size; i++) {
            if (collisionBox.overlaps(staticWalls.get(i).getBounds())) {
                return false;
            }
        }

        return true;
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

            // Handle objects overlapping multiple cells using their bounding box
            float minX, maxX, minY, maxY;
            if (collider.getShape() == Collider.Shape.RECTANGLE) {
                // For rectangles, bounds are (x - width/2, y - height/2) to (x + width/2, y + height/2) based on
                // BaseEntity getBounds()
                minX = entity.getBounds().x;
                maxX = entity.getBounds().x + entity.getBounds().width;
                minY = entity.getBounds().y;
                maxY = entity.getBounds().y + entity.getBounds().height;
            } else {
                // For circles
                float radius = collider.getRadius();
                minX = entity.getX() - radius;
                maxX = entity.getX() + radius;
                minY = entity.getY() - radius;
                maxY = entity.getY() + radius;
            }

            final int cellX1 = (int) (minX / cellSize);
            final int cellY1 = (int) (minY / cellSize);
            final int cellX2 = (int) (maxX / cellSize);
            final int cellY2 = (int) (maxY / cellSize);

            for (int x = cellX1; x <= cellX2; x++) {
                for (int y = cellY1; y <= cellY2; y++) {
                    if (x != cellX || y != cellY) {
                        addColliderToCell(x, y, collider);
                    }
                }
            }
        }
    }

    private void addColliderToCell(final int x, final int y, final Collider collider) {
        final long key = hash(x, y);
        Array<Collider> cell = grid.get(key);
        if (cell == null) {
            cell = new Array<>();
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
                if (c1.getOwner().isDestroyed()) {
                    continue;
                }
                for (int j = i + 1; j < cellContent.size; j++) {
                    final Collider c2 = cellContent.get(j);
                    if (c2.getOwner().isDestroyed()) {
                        continue;
                    }

                    if (canCollide(c1, c2)) {
                        if (c1.intersects(c2)) {
                            try {
                                c1.handleCollision(c2.getOwner());
                                if (!c1.getOwner().isDestroyed() && !c2.getOwner().isDestroyed()) {
                                    c2.handleCollision(c1.getOwner());
                                }
                            } catch (Exception e) {
                                Gdx.app.error("CollisionManager", "Error handling collision", e);
                            }
                        }
                    }
                }
            }
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    private boolean canCollide(final Collider c1, final Collider c2) {
        final int layer1 = c1.getLayer();
        final int layer2 = c2.getLayer();

        return (collisionMatrix[log2(layer1)] & layer2) != 0;
    }

    /**
     * Finds all entities within a rectangular area that match the layer mask.
     */
    public Array<GameEntity> getEntitiesInArea(final Rectangle area, final int layerMask) {
        final Array<GameEntity> result = new Array<>();
        final int startX = (int) (area.x / cellSize);
        final int startY = (int) (area.y / cellSize);
        final int endX = (int) ((area.x + area.width) / cellSize);
        final int endY = (int) ((area.y + area.height) / cellSize);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                final Array<Collider> cell = grid.get(hash(x, y));
                if (cell != null) {
                    for (final Collider c : cell) {
                        if ((c.getLayer() & layerMask) != 0 && c.getOwner().getBounds().overlaps(area)) {
                            if (!result.contains(c.getOwner(), true)) {
                                result.add(c.getOwner());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Finds all entities within a circular radius that match the layer mask.
     */
    public Array<GameEntity> getEntitiesInRadius(final float cx, final float cy, final float radius,
            final int layerMask) {
        final Array<GameEntity> result = new Array<>();
        final int startX = (int) ((cx - radius) / cellSize);
        final int startY = (int) ((cy - radius) / cellSize);
        final int endX = (int) ((cx + radius) / cellSize);
        final int endY = (int) ((cy + radius) / cellSize);

        final float radiusSq = radius * radius;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                final Array<Collider> cell = grid.get(hash(x, y));
                if (cell != null) {
                    for (final Collider c : cell) {
                        if ((c.getLayer() & layerMask) != 0) {
                            final float dx = c.getOwner().getX() - cx;
                            final float dy = c.getOwner().getY() - cy;
                            if (dx * dx + dy * dy <= radiusSq) {
                                if (!result.contains(c.getOwner(), true)) {
                                    result.add(c.getOwner());
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Finds the nearest entity to a point within a maximum range that matches the layer mask. Uses spatial hashing to
     * expand the search radius efficiently.
     */
    public GameEntity getNearestEntity(final float cx, final float cy, final float maxRange, final int layerMask) {
        GameEntity nearest = null;
        float minDistanceSq = maxRange * maxRange;

        final int centerX = (int) (cx / cellSize);
        final int centerY = (int) (cy / cellSize);
        final int rangeInCells = (int) Math.ceil(maxRange / cellSize);

        // Expand search outward from center cell
        for (int r = 0; r <= rangeInCells; r++) {
            boolean foundInRange = false;
            for (int x = centerX - r; x <= centerX + r; x++) {
                for (int y = centerY - r; y <= centerY + r; y++) {
                    // Only check the perimeter of the current "ring" (r)
                    if (Math.abs(x - centerX) == r || Math.abs(y - centerY) == r) {
                        final Array<Collider> cell = grid.get(hash(x, y));
                        if (cell != null) {
                            for (final Collider c : cell) {
                                if ((c.getLayer() & layerMask) != 0) {
                                    final float dx = c.getOwner().getX() - cx;
                                    final float dy = c.getOwner().getY() - cy;
                                    final float distSq = dx * dx + dy * dy;
                                    if (distSq < minDistanceSq) {
                                        minDistanceSq = distSq;
                                        nearest = c.getOwner();
                                        foundInRange = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // If we found something in this ring, and the ring's boundary is further than
            // the min distance found, we can stop.
            if (foundInRange && (r * cellSize) * (r * cellSize) > minDistanceSq) {
                break;
            }
        }

        return nearest;
    }
}
