package hust.adventure.world.parsers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import hust.adventure.entities.StaticObject;
import hust.adventure.world.MapObjectParser;
import hust.adventure.world.MapParseResult;

/**
 * Parser for loading decorative tile objects (e.g. chairs, tables) from TMX layers.
 */
public class StaticDecorParser implements MapObjectParser {
    @Override
    public void parse(final MapLayer layer, final MapParseResult result) {
        if (layer == null || result == null) {
            return;
        }
        for (final MapObject obj : layer.getObjects()) {
            if (obj instanceof TiledMapTileMapObject) {
                final TiledMapTileMapObject tileObj = (TiledMapTileMapObject) obj;
                if (tileObj.getTile() != null && tileObj.getTile().getTextureRegion() != null) {
                    final TextureRegion region = tileObj.getTile().getTextureRegion();
                    final float x = tileObj.getX();
                    final float y = tileObj.getY();
                    final float width = tileObj.getProperties().get("width", (float) region.getRegionWidth(), Float.class);
                    final float height = tileObj.getProperties().get("height", (float) region.getRegionHeight(), Float.class);

                    result.addDecorEntity(new StaticObject(x, y, width, height, region));
                }
            }
        }
    }
}
