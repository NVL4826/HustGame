package hust.adventure;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import hust.adventure.entities.combat.Projectile;
import hust.adventure.events.EventDispatcher;
import hust.adventure.screens.LoadingScreen;
import hust.adventure.screens.ScreenTransition;
import hust.adventure.core.GameAssetManager;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.config.LevelID;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.screens.levels.LevelFactory;
import hust.adventure.screens.GameOverScreen;
import hust.adventure.ui.HUD;
import hust.adventure.ui.InventoryUI;
import hust.adventure.entities.interactables.ItemDrop;
import hust.adventure.graphics.ShapeDrawUtils;
import hust.adventure.core.audio.AudioManager;
import com.badlogic.gdx.Screen;
import hust.adventure.core.ItemDataManager;
import hust.adventure.items.ItemConfig;
import hust.adventure.items.ItemFactory;
import hust.adventure.items.ItemManager;

/**
 * Lớp gốc quản lý vòng đời ứng dụng và lưu trữ các phân hệ trung tâm.
 */
public class HustGame extends Game implements EventListener {
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GameAssetManager assetManager;
    private EventDispatcher eventDispatcher;
    public ScreenTransition screenTransition;
    private AudioManager audioManager;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new GameAssetManager();
        eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.addListener(EventType.MAP_TRANSITION, this);

        audioManager = new AudioManager(assetManager);
        eventDispatcher.addListener(EventType.PLAY_SFX, audioManager);
        eventDispatcher.addListener(EventType.PLAY_BGM, audioManager);
        eventDispatcher.addListener(EventType.LEVEL_UP, audioManager);
        eventDispatcher.addListener(EventType.ITEM_PICKED_UP, audioManager);
        eventDispatcher.addListener(EventType.ITEM_USED, audioManager);
        eventDispatcher.addListener(EventType.TREASURE_OPENED, audioManager);
        eventDispatcher.addListener(EventType.PUZZLE_SOLVED, audioManager);
        eventDispatcher.addListener(EventType.PUZZLE_FAILED, audioManager);
        eventDispatcher.addListener(EventType.ENTITY_DAMAGED, audioManager);
        eventDispatcher.addListener(EventType.ENTITY_DIED, audioManager);

        screenTransition = new ScreenTransition(this);

        // Khởi tạo font hỗ trợ tiếng Việt
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));
        final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 18;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";
        font = generator.generateFont(parameter);
        generator.dispose();

        // Nạp và đăng ký các vật phẩm từ cấu hình JSON
        final ItemDataManager itemDataManager = new ItemDataManager("configs/items.json");
        final ItemFactory itemFactory = new ItemFactory();
        for (final ItemConfig config : itemDataManager.getAllConfigs()) {
            ItemManager.instance.register(itemFactory.createItem(config));
        }

        // Khởi đầu bằng màn hình tải tài nguyên
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event.getType() == EventType.MAP_TRANSITION) {
            handleMapTransition((MapTransitionData) event.getData());
        }
    }

    private void handleMapTransition(final MapTransitionData data) {
        if (screenTransition.isTransitioning())
            return;

        final LevelID nextId = LevelID.fromMapPath(data.getTargetMap());
        if (nextId == null)
            return;

        final LevelConfig config = new LevelConfig(nextId, data.getTargetMap(), data.getSpawnX(), data.getSpawnY(),
                1.0f, nextId.getBgmPath(), nextId.getAmbientColor());
        final Screen nextScreen = LevelFactory.createLevel(this, config);

        if (nextScreen != null) {
            screenTransition.fadeOut(nextScreen, 0.5f);
        }
    }

    @Override
    public void setScreen(final Screen screen) {
        final Screen oldScreen = this.screen;
        super.setScreen(screen);
        if (oldScreen != null && oldScreen != screen) {
            oldScreen.dispose();
        }
    }

    @Override
    public void render() {
        if (audioManager != null) {
            audioManager.update(Gdx.graphics.getDeltaTime());
        }

        // Ủy quyền render cho Screen hiện hành
        super.render();

        // Vẽ hiệu ứng chuyển cảnh nếu có
        if (screenTransition != null) {
            screenTransition.render(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        eventDispatcher.removeListener(EventType.MAP_TRANSITION, this);
        if (audioManager != null) {
            eventDispatcher.removeListener(EventType.PLAY_SFX, audioManager);
            eventDispatcher.removeListener(EventType.PLAY_BGM, audioManager);
            eventDispatcher.removeListener(EventType.LEVEL_UP, audioManager);
            eventDispatcher.removeListener(EventType.ITEM_PICKED_UP, audioManager);
            eventDispatcher.removeListener(EventType.ITEM_USED, audioManager);
            eventDispatcher.removeListener(EventType.TREASURE_OPENED, audioManager);
            eventDispatcher.removeListener(EventType.PUZZLE_SOLVED, audioManager);
            eventDispatcher.removeListener(EventType.PUZZLE_FAILED, audioManager);
            eventDispatcher.removeListener(EventType.ENTITY_DAMAGED, audioManager);
            eventDispatcher.removeListener(EventType.ENTITY_DIED, audioManager);
            audioManager.dispose();
        }

        if (screen != null)
            screen.dispose();
        if (spriteBatch != null)
            spriteBatch.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
        if (font != null)
            font.dispose();
        if (assetManager != null)
            assetManager.dispose();
        if (screenTransition != null)
            screenTransition.dispose();
        GameOverScreen.disposeStatic();
        HUD.disposeStatic();
        InventoryUI.disposeStatic();
        ItemDrop.disposeStaticResources();
        Projectile.disposeStaticResources();
        ShapeDrawUtils.disposeStatic();
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public GameAssetManager getAssetManager() {
        return assetManager;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public BitmapFont getFont() {
        return font;
    }

    public ScreenTransition getScreenTransition() {
        return screenTransition;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }
}
