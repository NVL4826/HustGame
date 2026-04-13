package vn.hust.hustgame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class GameAssetManager {
    private final AssetManager manager;

    public GameAssetManager() {
        this.manager = new AssetManager();
    }

    public void loadAllAssets() {
        // manager.load("example.png", Texture.class);
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

    public void dispose() {
        manager.dispose();
    }

    public AssetManager getManager() {
        return manager;
    }
}
