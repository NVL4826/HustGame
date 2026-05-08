package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMap;

import vn.hust.hustgame.screens.LabScreen;

public class SyntaxErrorEnemy extends BaseEnemy {
    private float fireTimer = 0;

    public SyntaxErrorEnemy(float x, float y, TiledMap map) {
        super(x, y, 30, 30, 25, "SyntaxErr", Color.ORANGE, map);
    }

    @Override
    public float getMaxHp() {
        return 25;
    }

    @Override
    public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - getX();
        float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < 200) {
            setX(getX() - (dx / dist) * 30 * delta);
            setY(getY() - (dy / dist) * 30 * delta);
        }
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));
        getBounds().setPosition(getX(), getY());
        fireTimer += delta;
        if (fireTimer >= 1.5f) {
            fireTimer = 0;
            s.addEnemyProjectile(new Rectangle(getX() + 10, getY(), 10, 10));
        }
    }
}
