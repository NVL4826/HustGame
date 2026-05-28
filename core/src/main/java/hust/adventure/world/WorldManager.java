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
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.EntityManager;
import hust.adventure.graphics.LightProvider;

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

    public void loadMap(final TiledMap map, EntityFactory factory, EntityManager entityManager,
            LightProvider lightProvider) {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        this.currentMap = map;

        setupWalls();
        setupPortals();
        setupLightingObjects(factory, entityManager, lightProvider);
    }

    private void setupWalls() {
        walls.clear();
        // Thử lần lượt: "collision" (lab/library), "Border" (Final Outside), "Object Layer 1" (map cũ)
        MapLayer objectLayer = currentMap.getLayers().get("collision");
        if (objectLayer == null) {
            objectLayer = currentMap.getLayers().get("Border");
        }
        if (objectLayer == null) {
            objectLayer = currentMap.getLayers().get("Object Layer 1");
        }
        if (objectLayer != null) {
            for (MapObject obj : objectLayer.getObjects()) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                    walls.add(new WallEntity(rect));
                }
            }
        }
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

    private void setupLightingObjects(EntityFactory factory, EntityManager entityManager, LightProvider lightProvider) {
        if (factory == null || entityManager == null || lightProvider == null)
            return;
        MapLayer lightLayer = currentMap.getLayers().get("LightingObjects");
        if (lightLayer != null) {
            for (MapObject obj : lightLayer.getObjects()) {
                float x = obj.getProperties().get("x", 0f, Float.class);
                float y = obj.getProperties().get("y", 0f, Float.class);
                String name = obj.getName();

                if ("Book".equalsIgnoreCase(name)) {
                    factory.createFloatingBook(x, y, lightProvider);
                } else if ("Candle".equalsIgnoreCase(name)) {
                    factory.createCandle(x, y, lightProvider);
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
     * Đọc vị trí spawn của player từ objectgroup "Spawn" trong TMX.
     * Nếu không có thì trả về null (PlayScreen dùng spawn từ LevelConfig).
     */
    public Vector2 getSpawnPoint() {
        if (currentMap == null) return null;
        MapLayer spawnLayer = currentMap.getLayers().get("Spawn");
        if (spawnLayer == null) return null;
        for (MapObject obj : spawnLayer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                // Dùng giữa X, nhưng TOP của rect theo trục Y (libGDX đã flip Y)
                // để tránh spawn bên trong tường đáy của map
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
