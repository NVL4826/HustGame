package vn.hust.hustgame;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
    public ScreenTransition screenTransition;

    @Override
    public void create() {
        screenTransition = new ScreenTransition(this);
        // Start with FinalOutsideScreen
        setScreen(new FinalOutsideScreen(this));
    }
    
    @Override
    public void render() {
        super.render(); // This renders the current screen
        // Render transition effect on top of the screen if any
        if (screenTransition != null && screenTransition.update(com.badlogic.gdx.Gdx.graphics.getDeltaTime())) {
            screenTransition.render();
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (screenTransition != null) {
            screenTransition.dispose();
        }
    }
}
