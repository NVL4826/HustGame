package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import vn.hust.hustgame.screens.LabScreen;

public abstract class BaseEnemy extends Entity {
    public float hp;
    public String name;
    public Color color;
    protected TiledMap map;

    public BaseEnemy(float x, float y, float w, float h, float hp, String name, Color color, TiledMap map) {
        super(x, y, w, h);
        this.hp = hp;
        this.name = name;
        this.color = color;
        this.map = map;
    }

    @Override
    public void update(float delta) {
        // Not used by BaseEnemy, which uses custom update with Player and Screen
        // context
    }

    public abstract void update(float delta, Player player, LabScreen screen);

    @Override
    public void draw(SpriteBatch batch) {
        // Not used by BaseEnemy, which uses custom draw with ShapeRenderer
    }

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

    @Override
    public void dispose() {
        // Default implementation
    }
}
