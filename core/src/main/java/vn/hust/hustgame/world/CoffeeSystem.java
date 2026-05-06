package vn.hust.hustgame.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import vn.hust.hustgame.GameState;

public class CoffeeSystem {
    public enum CoffeeType {
        DEN("Cà Phê Đen", 10f, 30f, 30f, 1.3f, false),
        SUA("Cà Phê Sữa", 20f, 25f, 20f, 1.3f, true),
        CHON("Cà Phê Chồn", 5f, 60f, 45f, 1.5f, false),
        NGUOI("Cà Phê Nguội", 0f, 10f, 0f, 1f, false);
        
        public String name;
        public float hpRestore;
        public float staminaRestore;
        public float effectDuration;
        public float speedMultiplier;
        public boolean healOverTime;
        
        CoffeeType(String name, float hp, float stamina, float duration, float speed, boolean heal) {
            this.name = name;
            this.hpRestore = hp;
            this.staminaRestore = stamina;
            this.effectDuration = duration;
            this.speedMultiplier = speed;
            this.healOverTime = heal;
        }
    }
    
    private float effectTimer = 0f;
    private CoffeeType activeEffect = null;
    
    public void consume(CoffeeType type) {
        GameState.instance.coffeeCount++;
        GameState.instance.hp = Math.min(GameState.instance.maxHp, GameState.instance.hp + type.hpRestore);
        GameState.instance.stamina = Math.min(GameState.instance.maxStamina, GameState.instance.stamina + type.staminaRestore);
        
        if (type.effectDuration > 0) {
            activeEffect = type;
            effectTimer = type.effectDuration;
        }
    }
    
    public void update(float delta) {
        if (activeEffect != null) {
            effectTimer -= delta;
            
            if (activeEffect.healOverTime) {
                GameState.instance.hp = Math.min(GameState.instance.maxHp, GameState.instance.hp + delta * 1f); // 1 HP per sec
            }
            
            // "Cà Phê Chồn" effect: chance to move wrong direction is handled in input or movement logic
            
            if (effectTimer <= 0) {
                activeEffect = null;
            }
        }
    }
    
    public float getSpeedMultiplier() {
        return activeEffect != null ? activeEffect.speedMultiplier : 1f;
    }
    
    public boolean hasVignette() {
        return activeEffect == CoffeeType.CHON;
    }
    
    public boolean hasReversedInput(float randomValue) {
        return activeEffect == CoffeeType.CHON && randomValue < 0.15f;
    }
    
    public void drawVignette(ShapeRenderer shapeRenderer, float screenWidth, float screenHeight) {
        if (hasVignette()) {
            // Draw a transparent brown overlay
            com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
            com.badlogic.gdx.Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0.4f, 0.2f, 0f, 0.2f)); // Brown tint
            shapeRenderer.rect(0, 0, screenWidth, screenHeight);
            shapeRenderer.end();
            
            com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        }
    }
}
