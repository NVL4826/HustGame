package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;

import vn.hust.hustgame.screens.LabScreen;

public class NullPointerEnemy extends BaseEnemy {
    public NullPointerEnemy(float x, float y, TiledMap map) {
        super(x, y, 30, 30, 30, "NPE", Color.RED, map);
    }

    @Override
    public float getMaxHp() {
        return 30;
    }

    @Override
    public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - getX();
        float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            setX(getX() + (dx / dist) * 50 * delta);
            setY(getY() + (dy / dist) * 50 * delta);
        }
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));
        getBounds().setPosition(getX(), getY());
    }
}
