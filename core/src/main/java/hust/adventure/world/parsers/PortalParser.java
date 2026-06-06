package hust.adventure.world.parsers;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import hust.adventure.world.MapObjectParser;
import hust.adventure.world.MapParseResult;
import hust.adventure.world.WorldManager.Portal;

/**
 * Parser for loading portal objects from TMX layers.
 */
public class PortalParser implements MapObjectParser {
    @Override
    public void parse(final MapLayer layer, final MapParseResult result) {
        if (layer == null || result == null) {
            return;
        }
        for (final MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                final Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                final String target = obj.getProperties().get("target", String.class);
                final Float spawnX = obj.getProperties().get("spawnX", 0f, Float.class);
                final Float spawnY = obj.getProperties().get("spawnY", 0f, Float.class);
                result.addPortal(new Portal(rect, target, spawnX != null ? spawnX : 0f, spawnY != null ? spawnY : 0f));
            }
        }
    }
}
