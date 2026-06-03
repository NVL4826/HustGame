package hust.adventure.core.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

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
        for (int i = 4; i <= 25; i++) {
            manager.load(i + ".png", Texture.class);
        }
        manager.load("bullet.png", Texture.class);
        manager.load("items/brain.png", Texture.class);
        manager.load("items/coffee.png", Texture.class);
        manager.load("items/energy_drink.png", Texture.class);
        manager.load("items/kho_ga.png", Texture.class);
        manager.load("items/usb.png", Texture.class);

        // Maps
        manager.load("Final Outside.tmx", TiledMap.class);
        manager.load("tang1.tmx", TiledMap.class);
        manager.load("Phong_doc.tmx", TiledMap.class);
        manager.load("library.tmx", TiledMap.class);
        manager.load("lab.tmx", TiledMap.class);
        manager.load("boss_room.tmx", TiledMap.class);
        manager.load("test.tmx", TiledMap.class);
        manager.load("tsx/map_1.tmx", TiledMap.class);

        // Phong_doc textures
        for (int i = 19; i <= 29; i++)
            manager.load("Phong_doc/" + i + ".png", Texture.class);
        for (int i = 47; i <= 51; i++)
            manager.load("Phong_doc/" + i + ".png", Texture.class);
        manager.load("Phong_doc/1.png", Texture.class);

        // Sound effects
        manager.load("audio/sfx/shoot.wav", Sound.class);
        manager.load("audio/sfx/whip.wav", Sound.class);
        manager.load("audio/sfx/magic.wav", Sound.class);
        manager.load("audio/sfx/player_hit.wav", Sound.class);
        manager.load("audio/sfx/enemy_hit.wav", Sound.class);
        manager.load("audio/sfx/enemy_die.wav", Sound.class);
        manager.load("audio/sfx/level_up.wav", Sound.class);
        manager.load("audio/sfx/pickup.wav", Sound.class);
        manager.load("audio/sfx/item_use.wav", Sound.class);
        manager.load("audio/sfx/ui_click.wav", Sound.class);
        manager.load("audio/sfx/puzzle_solved.wav", Sound.class);
        manager.load("audio/sfx/puzzle_failed.wav", Sound.class);
        manager.load("audio/sfx/game_over.wav", Sound.class);

        // Music tracks
        manager.load("audio/music/menu_theme.wav", Music.class);
        manager.load("audio/music/level_theme.wav", Music.class);
        manager.load("audio/music/boss_theme.wav", Music.class);
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

    public Sound getSound(String name) {
        return manager.get(name, Sound.class);
    }

    public Music getMusic(String name) {
        return manager.get(name, Music.class);
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
