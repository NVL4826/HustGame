package hust.adventure;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

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
import com.badlogic.gdx.Screen;

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

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        assetManager = new GameAssetManager();
        eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.addListener(EventType.MAP_TRANSITION, this);
        screenTransition = new ScreenTransition(this);

        // Khởi tạo font hỗ trợ tiếng Việt
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/font.ttf"));
        final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 18;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "àáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴÈÉẸẺẼÊỀẾỆỂỄÌÍỊỈĨÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠÙÚỤỦŨƯỪỨỰỬỮỲÝỴỶỸĐ";
        font = generator.generateFont(parameter);
        generator.dispose();

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
                1.0f, null, nextId.getAmbientColor());
        final Screen nextScreen = LevelFactory.createLevel(this, config);

        if (nextScreen != null) {
            screenTransition.fadeOut(nextScreen, 0.5f);
        }
    }

    @Override
    public void render() {
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
}
