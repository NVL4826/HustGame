package hust.adventure.core.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

import hust.adventure.core.data.LevelConfig;
import hust.adventure.core.data.LevelDataLoader;

public class GameAssetManager {
    private final AssetManager manager;
    private Texture whitePixel;

    public GameAssetManager() {
        this.manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader());
    }

    public void loadAllAssets(final LevelDataLoader levelDataLoader) {
        // Textures
        manager.load("background.png", Texture.class);
        manager.load("Boss Room.jpg", Texture.class);
        manager.load("Boss THT.png", Texture.class);
        manager.load("Library1.jpg", Texture.class);
        // Character assets
        manager.load("character/atlas.png", Texture.class);
        manager.load("character/enemy/syntax_error.png", Texture.class);
        manager.load("character/enemy/null_pointer.png", Texture.class);
        manager.load("character/enemy/stack_overflow.png", Texture.class);
        manager.load("character/enemy/library_boss/sprite_0000.png", Texture.class);
        manager.load("character/enemy/library_boss/sprite_0001.png", Texture.class);
        manager.load("character/enemy/library_boss/sprite_0002.png", Texture.class);
        manager.load("character/enemy/library_boss/sprite_0003.png", Texture.class);
        manager.load("bkav.png", Texture.class);
        manager.load("unikey.png", Texture.class);
        manager.load("chatgpt.png", Texture.class);
        manager.load("bullet.png", Texture.class);
        manager.load("items/brain.png", Texture.class);
        manager.load("items/coffee.png", Texture.class);
        manager.load("items/energy_drink.png", Texture.class);
        manager.load("items/kho_ga.png", Texture.class);
        manager.load("items/usb.png", Texture.class);

        // Load maps dynamically from level configurations (Single Source of Truth)
        if (levelDataLoader != null) {
            for (final LevelConfig config : levelDataLoader.getAllConfigs()) {
                if (config.getMapPath() != null && !config.getMapPath().isEmpty()) {
                    manager.load(config.getMapPath(), TiledMap.class);
                }
            }
        }

        // Phong_doc textures
        for (int i = 19; i <= 29; i++)
            manager.load("Phong_doc/" + i + ".png", Texture.class);
        for (int i = 47; i <= 51; i++)
            manager.load("Phong_doc/" + i + ".png", Texture.class);
        manager.load("Phong_doc/1.png", Texture.class);

        // Sound effects
        manager.load("audio/sfx/ui_click.wav", Sound.class);
        manager.load("audio/sfx/level_up.wav", Sound.class);
        manager.load("audio/sfx/item_use.wav", Sound.class);

        // New SFX
        manager.load("audio/sfx/enemy/enemy_die.mp3", Sound.class);
        manager.load("audio/sfx/enemy/enemy_hit.mp3", Sound.class);
        manager.load("audio/sfx/enemy/enemy_spawn.mp3", Sound.class);
        manager.load("audio/sfx/enemy/time_alarm.mp3", Sound.class);

        manager.load("audio/sfx/interact/chest_open.mp3", Sound.class);
        manager.load("audio/sfx/interact/pick_up_gem.mp3", Sound.class);
        manager.load("audio/sfx/interact/pickup_item.mp3", Sound.class);

        manager.load("audio/sfx/player/GarlicPulseAura.mp3", Sound.class);
        manager.load("audio/sfx/player/Player Firing Bun Dau.wav", Sound.class);
        manager.load("audio/sfx/player/Player Firing Magic Wand.mp3", Sound.class);
        manager.load("audio/sfx/player/Player Firing Whip.mp3", Sound.class);
        manager.load("audio/sfx/player/Player Hurt.mp3", Sound.class);
        manager.load("audio/sfx/player/Use Consumable (Coffee).mp3", Sound.class);
        manager.load("audio/sfx/player/Use Consumable (Khô gà).mp3", Sound.class);

        manager.load("audio/sfx/puzzle_boss/answer_correct.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/answer_wrong.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/book_drag.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/book_snap.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/dialogue_next.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/paper_spawn.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/puzzle_failed.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/puzzle_solved.mp3", Sound.class);
        manager.load("audio/sfx/puzzle_boss/puzzle_wrong.mp3", Sound.class);

        // Music tracks
        manager.load("audio/music/menu.mp3", Music.class);
        manager.load("audio/music/game_over.mp3", Music.class);
        manager.load("audio/music/win_menu.mp3", Music.class);

        // Load level-specific BGM tracks dynamically from level configurations
        if (levelDataLoader != null) {
            for (final LevelConfig config : levelDataLoader.getAllConfigs()) {
                if (config.getBgmPath() != null && !config.getBgmPath().isEmpty()) {
                    manager.load(config.getBgmPath(), Music.class);
                }
            }
        }
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
