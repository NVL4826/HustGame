package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.maps.tiled.TiledMap;
import vn.hust.hustgame.screens.LabScreen;

public class StackOverflowEnemy extends BaseEnemy {
    public boolean isSplit;

    public StackOverflowEnemy(float x, float y, TiledMap map) {
        this(x, y, false, map);
    }

    public StackOverflowEnemy(float x, float y, boolean isSplit, TiledMap map) {
        super(x, y, isSplit ? 40 : 80, isSplit ? 40 : 80, isSplit ? 50 : 100, "StackOver", Color.MAROON, map);
        this.isSplit = isSplit;
    }

    @Override
    public float getMaxHp() {
        return isSplit ? 50 : 100;
    }

    @Override
    public void update(float delta, Player p, LabScreen s) {
        float dx = p.getX() - getX();
        float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            setX(getX() + (dx / dist) * 35 * delta);
            setY(getY() + (dy / dist) * 35 * delta);
        }
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));
        getBounds().setPosition(getX(), getY());
    }
}

