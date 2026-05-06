package vn.hust.hustgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class BaseEnemy {
    public float x, y, width, height;
    public float hp;
    public String name;
    public Color color;
    public Rectangle bounds;

    public BaseEnemy(float x, float y, float w, float h, float hp, String name, Color color) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.hp = hp;
        this.name = name;
        this.color = color;
        this.bounds = new Rectangle(x, y, w, h);
    }

    public abstract void update(float delta, Player player, LabScreen screen);

    public void draw(ShapeRenderer sr, SpriteBatch batch, BitmapFont font) {
        sr.setColor(color);
        sr.rect(x, y, width, height);
        
        // HP Bar
        sr.setColor(Color.GREEN);
        sr.rect(x, y + height + 5, (hp / getMaxHp()) * width, 5);
        
        batch.begin();
        font.draw(batch, name, x, y + height + 25);
        batch.end();
    }
    
    public abstract float getMaxHp();

    public void takeDamage(float amount) {
        hp -= amount;
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
