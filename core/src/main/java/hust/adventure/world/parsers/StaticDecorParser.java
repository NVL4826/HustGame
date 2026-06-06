package hust.adventure.world.parsers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import hust.adventure.entities.StaticObject;
import hust.adventure.world.MapObjectParser;
import hust.adventure.world.MapParseResult;

/**
 * Parser for loading decorative tile objects (e.g. chairs, tables, trees) from TMX layers.
 * Supports both Object Layers (TiledMapTileMapObject) and Tile Layers (TiledMapTileLayer).
 */
public class StaticDecorParser implements MapObjectParser {

    @Override
    public void parse(final MapLayer layer, final MapParseResult result) {
        if (layer == null || result == null) {
            return;
        }

        if (layer instanceof TiledMapTileLayer) {
            parseTileLayer((TiledMapTileLayer) layer, result);
        } else {
            parseObjectLayer(layer, result);
        }
    }

    private void parseTileLayer(final TiledMapTileLayer tileLayer, final MapParseResult result) {
        final float tileWidth = tileLayer.getTileWidth();
        final float tileHeight = tileLayer.getTileHeight();
        int subZ = 0;

        for (int x = 0; x < tileLayer.getWidth(); x++) {
            for (int y = 0; y < tileLayer.getHeight(); y++) {
                final Cell cell = tileLayer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    final TextureRegion region = cell.getTile().getTextureRegion();
                    if (region != null) {
                        final float worldX = x * tileWidth;
                        final float worldY = y * tileHeight;

                        final StaticObject decor = StaticObject.fromBottomLeft(worldX, worldY, tileWidth, tileHeight, region);
                        decor.setSubZIndex(subZ++);
                        if (tileLayer.getName() != null) {
                            decor.setLayerName(tileLayer.getName());
                        }
                        result.addDecorEntity(decor);
                    }
                }
            }
        }
        // Hide original layer so OrthogonalTiledMapRenderer doesn't double-render it statically
        tileLayer.setVisible(false);
    }

    private void parseObjectLayer(final MapLayer layer, final MapParseResult result) {
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
                    if (layer.getName() != null) {
                        decor.setLayerName(layer.getName());
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
