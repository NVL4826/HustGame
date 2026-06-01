package hust.adventure.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import hust.adventure.wave.WaveConfig;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

public class GameAssetManager {
    public static final String CHEST_TEXTURE_PATH = "titled/Objects/chest.png";
    private final AssetManager manager;
    private Texture whitePixel;
    private final Json json;

    public GameAssetManager() {
        this.manager = new AssetManager();
        this.json = new Json();
        manager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public WaveConfig loadWaveConfig(String path) {
        return json.fromJson(WaveConfig.class, Gdx.files.internal(path));
    }


    public void loadAllAssets() {
        // Textures
        manager.load("Lab.jpg", Texture.class);
        manager.load("Boss Room.jpg", Texture.class);
        manager.load("Boss THT.png", Texture.class);
        manager.load("Library1.jpg", Texture.class);
        manager.load("4.png", Texture.class);
        manager.load(CHEST_TEXTURE_PATH, Texture.class);

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
        manager.load("audio/sfx/chest_open.wav", Sound.class);
        manager.load("audio/sfx/puzzle_solved.wav", Sound.class);
        manager.load("audio/sfx/puzzle_failed.wav", Sound.class);
        
        manager.load("audio/sfx/garlic_aura.wav", Sound.class);
        manager.load("audio/sfx/drink.wav", Sound.class);
        manager.load("audio/sfx/skill_q.wav", Sound.class);
        manager.load("audio/sfx/skill_e.wav", Sound.class);
        manager.load("audio/sfx/skill_f.wav", Sound.class);
        manager.load("audio/sfx/enemy_spawn.wav", Sound.class);
        manager.load("audio/sfx/time_alarm.wav", Sound.class);
        manager.load("audio/sfx/pickup_gem.wav", Sound.class);
        manager.load("audio/sfx/pickup_item.wav", Sound.class);
        manager.load("audio/sfx/portal_enter.wav", Sound.class);
        manager.load("audio/sfx/inventory_open.wav", Sound.class);
        manager.load("audio/sfx/ui_hover.wav", Sound.class);
        manager.load("audio/sfx/roulette_spin.wav", Sound.class);
        manager.load("audio/sfx/roulette_result.wav", Sound.class);
        manager.load("audio/sfx/book_drag.wav", Sound.class);
        manager.load("audio/sfx/book_snap.wav", Sound.class);
        manager.load("audio/sfx/puzzle_wrong.wav", Sound.class);
        manager.load("audio/sfx/dialogue_next.wav", Sound.class);
        manager.load("audio/sfx/answer_correct.wav", Sound.class);
        manager.load("audio/sfx/answer_wrong.wav", Sound.class);
        manager.load("audio/sfx/paper_spawn.wav", Sound.class);
        manager.load("audio/sfx/game_over.wav", Sound.class);

        // Music tracks
        manager.load("audio/music/menu_theme.wav", Music.class);
        manager.load("audio/music/level_theme.wav", Music.class);
        manager.load("audio/music/boss_theme.wav", Music.class);
        
        manager.load("audio/music/bg_menu.mp3", Music.class);
        manager.load("audio/music/bg_outside.mp3", Music.class);
        manager.load("audio/music/bg_library.mp3", Music.class);
        manager.load("audio/music/bg_lab.mp3", Music.class);
        manager.load("audio/music/bg_boss.mp3", Music.class);
        manager.load("audio/music/bg_victory.mp3", Music.class);
        manager.load("audio/music/bg_gameover.mp3", Music.class);
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
