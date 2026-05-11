package hust.adventure.events;

import com.badlogic.gdx.utils.Pool;

public class LevelUpEvent implements Pool.Poolable {
    private int newLevel;

    public LevelUpEvent() {
    }

    public void init(int newLevel) {
        this.newLevel = newLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    @Override
    public void reset() {
        this.newLevel = 0;
    }
}
