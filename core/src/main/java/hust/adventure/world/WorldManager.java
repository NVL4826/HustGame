package hust.adventure.world;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import hust.adventure.entities.environment.WallEntity;


import java.util.ArrayList;
import java.util.List;

/**
 * Manages the game world state, including the map, collisions, and portals.
 */
public class WorldManager implements Disposable {
    private TiledMap currentMap;
    private final List<WallEntity> walls;
    private final List<Portal> portals;

    public WorldManager() {
        this.walls = new ArrayList<>();
        this.portals = new ArrayList<>();
    }

    public void loadMap(final TiledMap map) {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        this.currentMap = map;

        setupWalls();
        setupPortals();
    }

    private void setupWalls() {
        walls.clear();
        MapLayer objectLayer = currentMap.getLayers().get("Object Layer 1");
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

    public TiledMap getCurrentMap() {
        return currentMap;
    }

    public List<WallEntity> getWalls() {
        return walls;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    @Override
    public void dispose() {
        if (currentMap != null) {
            currentMap.dispose();
        }
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
