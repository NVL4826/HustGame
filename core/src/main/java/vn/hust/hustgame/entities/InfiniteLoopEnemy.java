package vn.hust.hustgame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import vn.hust.hustgame.screens.LabScreen;

public class InfiniteLoopEnemy extends BaseEnemy {
    private float vx = 70, vy = 70;

    public InfiniteLoopEnemy(float x, float y, TiledMap map) {
        super(x, y, 50, 50, 80, "InfLoop", Color.PURPLE, map);
    }

    @Override
    public float getMaxHp() {
        return 80;
    }

    @Override
    public void update(float delta, Player p, LabScreen s) {
        setX(getX() + vx * delta);
        setY(getY() + vy * delta);
        if (getX() < 0) {
            setX(0);
            vx = -vx;
        }
        if (getX() > 800 - getWidth()) {
            setX(800 - getWidth());
            vx = -vx;
        }
        if (getY() < 0) {
            setY(0);
            vy = -vy;
        }
        if (getY() > 600 - getHeight()) {
            setY(600 - getHeight());
            vy = -vy;
        }
        getBounds().setPosition(getX(), getY());
    }
}
