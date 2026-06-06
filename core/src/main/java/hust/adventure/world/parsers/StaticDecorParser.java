package hust.adventure.world.parsers;

import com.badlogic.gdx.Gdx;
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

        int subZ = 0;
        for (final MapObject obj : layer.getObjects()) {
            if (obj instanceof TiledMapTileMapObject) {
                final TiledMapTileMapObject tileObj = (TiledMapTileMapObject) obj;
                if (tileObj.getTile() != null && tileObj.getTile().getTextureRegion() != null) {
                    final TextureRegion region = tileObj.getTile().getTextureRegion();
                    final float x = tileObj.getX();
                    final float y = tileObj.getY();
                    final float width = tileObj.getProperties().get("width", (float) region.getRegionWidth(), Float.class);
                    final float height = tileObj.getProperties().get("height", (float) region.getRegionHeight(), Float.class);

                    final StaticObject decor = StaticObject.fromBottomLeft(x, y, width, height, region);
                    decor.setSubZIndex(subZ++);
                    if (tileObj.getName() != null) {
                        decor.setName(tileObj.getName());
                    }
                    final Object idProp = tileObj.getProperties().get("id");
                    if (idProp != null) {
                        decor.setId(idProp.toString());
                    }
                    decor.setRotation(tileObj.getRotation());
                    result.addDecorEntity(decor);
                } else {
                    Gdx.app.log("StaticDecorParser", "Skipping tile map object with missing tile/texture region: name=" + obj.getName());
                }
            } else {
                Gdx.app.log("StaticDecorParser", "Skipping unsupported non-tile map object: type=" 
                        + (obj != null ? obj.getClass().getSimpleName() : "null") + ", name=" + (obj != null ? obj.getName() : "null"));
            }
        }
    }
}
