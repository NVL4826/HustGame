package vn.hust.hustgame.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ScreenTransition {
    private Game game;
    private ShapeRenderer shapeRenderer;
    private Screen nextScreen;
    private float duration;
    private float time;
    private boolean isFadingOut;
    private boolean isFadingIn;
    
    public ScreenTransition(Game game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
    }
    
    public void fadeOut(Screen next, float durationSec) {
        this.nextScreen = next;
        this.duration = durationSec;
        this.time = 0;
        this.isFadingOut = true;
        this.isFadingIn = false;
    }
    
    public void fadeIn(float durationSec) {
        this.duration = durationSec;
        this.time = 0;
        this.isFadingIn = true;
        this.isFadingOut = false;
    }
    
    public boolean update(float delta) {
        if (isFadingOut || isFadingIn) {
            time += delta;
            return true;
        }
        return false;
    }
    
    public void render() {
        if (!isFadingOut && !isFadingIn) return;
        
        float progress = Math.min(time / duration, 1f);
        float alpha = isFadingOut ? progress : (1f - progress);
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, alpha));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        if (progress >= 1f) {
            if (isFadingOut) {
                isFadingOut = false;
                game.setScreen(nextScreen);
                fadeIn(duration); // auto fade in after fade out
            } else if (isFadingIn) {
                isFadingIn = false;
            }
        }
    }
    
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
