package hust.adventure.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import hust.adventure.entities.environment.WallEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manages the game world state, including the map, collisions, and portals.
 */
public class WorldManager implements Disposable {
    private static final Set<String> KNOWN_BACKGROUND_LAYERS = Set.of(
        "Via He", "Duong", "Grass", "Nha1", "Background", "Floor", "Tile Layer 1"
    );

    private TiledMap currentMap;
    private final List<WallEntity> walls;
    private final List<Portal> portals;
    private InfiniteMapRenderer mapRenderer;

    public WorldManager() {
        this.walls = new ArrayList<>();
        this.portals = new ArrayList<>();
    }

    /**
     * Loads the map data including portals and physical walls.
     *
     * @param map the TiledMap instance to load
     */
    public void loadMap(final TiledMap map) {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        this.currentMap = map;

        setupWalls();
        setupPortals();
    }

    private void setupWalls() {
        walls.clear();
        
        // Find collision layers using map properties, layer properties, or fallbacks
        final List<MapLayer> collisionLayers = findCollisionLayers();
        for (final MapLayer layer : collisionLayers) {
            for (final MapObject obj : layer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    final Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    walls.add(new WallEntity(rect));
                }
            }
        }
    }

    private List<MapLayer> findCollisionLayers() {
        final List<MapLayer> foundLayers = new ArrayList<>();
        if (currentMap == null) {
            return foundLayers;
        }

        // 1. Check map properties for a specific collision layer name
        final String customCollisionLayerName = currentMap.getProperties().get("collisionLayer", String.class);
        if (customCollisionLayerName != null) {
            final MapLayer layer = currentMap.getLayers().get(customCollisionLayerName);
            if (layer != null) {
                foundLayers.add(layer);
                return foundLayers;
            }
        }

        // 2. Scan all layers for 'collision' or 'isCollision' boolean/string property
        for (final MapLayer layer : currentMap.getLayers()) {
            Object collProp = layer.getProperties().get("collision");
            if (collProp == null) {
                collProp = layer.getProperties().get("isCollision");
            }
            if (collProp instanceof Boolean && (Boolean) collProp) {
                foundLayers.add(layer);
            } else if (collProp instanceof String && ("true".equalsIgnoreCase((String) collProp) || "1".equals(collProp))) {
                foundLayers.add(layer);
            }
        }
        if (!foundLayers.isEmpty()) {
            return foundLayers;
        }

        // 3. Fallback to default known collision layer names
        final String[] defaultLayerNames = {"collision", "Border", "Object Layer 1"};
        for (final String name : defaultLayerNames) {
            final MapLayer layer = currentMap.getLayers().get(name);
            if (layer != null) {
                foundLayers.add(layer);
                break; // Prioritize the first matching fallback layer
            }
        }

        return foundLayers;
    }

    private void setupPortals() {
        portals.clear();
        MapLayer portalLayer = currentMap.getLayers().get("Portals");
        if (portalLayer != null) {
            for (MapObject obj : portalLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    String target = obj.getProperties().get("target", String.class);
                    float spawnX = obj.getProperties().get("spawnX", 0f, Float.class);
                    float spawnY = obj.getProperties().get("spawnY", 0f, Float.class);
                    portals.add(new Portal(rect, target, spawnX, spawnY));
                }
            }
        }
    }

    public void initInfiniteWorld(final InfiniteMapRenderer mapRenderer) {
        if (mapRenderer == null) {
            throw new IllegalArgumentException("InfiniteMapRenderer cannot be null");
        }
        this.mapRenderer = mapRenderer;
    }

    public void renderBackground(final SpriteBatch batch) {
        if (mapRenderer != null) {
            mapRenderer.draw(batch);
        }
    }

    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public List<WallEntity> getWalls() {
        return walls;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    /**
     * Reads the spawn point of the player from the "Spawn" objectgroup in TMX.
     * Supports custom properties for flexible configuration.
     *
     * @return the spawn point coordinates as a Vector2, or null if not found
     */
    public Vector2 getSpawnPoint() {
        if (currentMap == null) return null;
        
        // Check for custom spawn layer name in map properties, fallback to "Spawn"
        final String spawnLayerName = currentMap.getProperties().get("spawnLayer", "Spawn", String.class);
        MapLayer spawnLayer = currentMap.getLayers().get(spawnLayerName);
        if (spawnLayer == null) {
            // Also try scanning for any layer with a property "isSpawn" or similar
            for (final MapLayer layer : currentMap.getLayers()) {
                if ("true".equalsIgnoreCase(layer.getProperties().get("isSpawn", String.class))
                 || Boolean.TRUE.equals(layer.getProperties().get("isSpawn", Boolean.class))) {
                    spawnLayer = layer;
                    break;
                }
            }
        }

        if (spawnLayer == null) return null;
        for (final MapObject obj : spawnLayer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                // Use the center X, but the top of the rectangle along Y axis (since Y is flipped in libGDX)
                // to avoid spawning inside the bottom collision of the map
                return new Vector2(rect.x + rect.width / 2f, rect.y + rect.height);
            }
        }
        return null;
    }

    /**
     * Analyzes map layers and classifies them into background/foreground.
     * Prioritizes the "isBackground" property on each layer, falling back to a known name list.
     *
     * @return a 2D array where index [0] contains background layers and index [1] contains foreground layers.
     */
    public int[][] classifyLayers() {
        final IntArray bg = new IntArray();
        final IntArray fg = new IntArray();
        if (currentMap != null) {
            for (int i = 0; i < currentMap.getLayers().size(); i++) {
                final MapLayer layer = currentMap.getLayers().get(i);
                if (isBackgroundLayer(layer)) {
                    bg.add(i);
                } else {
                    fg.add(i);
                }
            }
        }
        return new int[][] { bg.toArray(), fg.toArray() };
    }

    private boolean isBackgroundLayer(final MapLayer layer) {
        if (layer == null) {
            return false;
        }
        final Object isBgProp = layer.getProperties().get("isBackground");
        if (isBgProp instanceof Boolean) {
            return (Boolean) isBgProp;
        }
        if (isBgProp instanceof String) {
            return "true".equalsIgnoreCase((String) isBgProp) || "1".equals(isBgProp);
        }
        final String name = layer.getName();
        return name != null && KNOWN_BACKGROUND_LAYERS.contains(name);
    }

    @Override
    public void dispose() {
        // NOTE: currentMap is owned by GameAssetManager and must NOT be disposed here.
        // Disposing it would invalidate the AssetManager's cache and crash on next map load.
        currentMap = null;
        walls.clear();
        portals.clear();
    }

    public static class Portal {
        public final Rectangle bounds;
        public final String targetMap;
        public final float spawnX;
        public final float spawnY;

        public Portal(final Rectangle bounds, final String targetMap, final float spawnX, final float spawnY) {
            this.bounds = bounds;
            this.targetMap = targetMap;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
        }
    }
}
