package hust.adventure.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class GameAssetManager {
    private final AssetManager manager;
    private Texture whitePixel;

    public GameAssetManager() {
        this.manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public void loadAllAssets() {
        // Textures
        manager.load("Lab.jpg", Texture.class);
        manager.load("Boss Room.jpg", Texture.class);
        manager.load("Boss THT.png", Texture.class);
        manager.load("Library1.jpg", Texture.class);
        manager.load("4.png", Texture.class);

        // Maps
        manager.load("Final Outside.tmx", TiledMap.class);
        manager.load("tang1.tmx", TiledMap.class);
        manager.load("Phong_doc.tmx", TiledMap.class);
        manager.load("library.tmx", TiledMap.class);
        manager.load("lab.tmx", TiledMap.class);
        manager.load("boss_room.tmx", TiledMap.class);
        manager.load("test.tmx", TiledMap.class);
    }

    public boolean update() {
        return manager.update();
    }

    public float getProgress() {
        return manager.getProgress();
    }

    public Texture getTexture(String name) {
        return manager.get(name, Texture.class);
    }

    public TiledMap getTiledMap(String name) {
        return manager.get(name, TiledMap.class);
    }

    public Texture getWhitePixel() {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return whitePixel;
    }

    public void dispose() {
        manager.dispose();
        if (whitePixel != null) {
            whitePixel.dispose();
        }
    }

    public AssetManager getManager() {
        return manager;
    }
}
