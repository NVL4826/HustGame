package vn.hust.hustgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import vn.hust.hustgame.events.EventDispatcher;
import vn.hust.hustgame.screens.LoadingScreen;
import vn.hust.hustgame.screens.ScreenTransition;

/**
 * Lớp gốc quản lý vòng đời ứng dụng và lưu trữ các phân hệ trung tâm.
 */
public class HustGame extends Game {
    private SpriteBatch spriteBatch;
    private GameAssetManager assetManager;
    private EventDispatcher eventDispatcher;
    public ScreenTransition screenTransition;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        assetManager = new GameAssetManager();
        eventDispatcher = EventDispatcher.getInstance();
        screenTransition = new ScreenTransition(this);

        // Khởi đầu bằng màn hình tải tài nguyên
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        // Ủy quyền render cho Screen hiện hành
        super.render();

        // Vẽ hiệu ứng chuyển cảnh nếu có
        if (screenTransition != null) {
            screenTransition.render(com.badlogic.gdx.Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        if (screen != null)
            screen.dispose();
        if (spriteBatch != null)
            spriteBatch.dispose();
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

    public ScreenTransition getScreenTransition() {
        return screenTransition;
    }
}
