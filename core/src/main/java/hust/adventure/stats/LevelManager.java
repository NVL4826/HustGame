package hust.adventure.stats;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ExpGainedEvent;
import hust.adventure.events.LevelUpEvent;
import com.badlogic.gdx.utils.Disposable;

public class LevelManager implements EventListener, Disposable {
    private int currentLevel = 1;
    private float currentExp = 0;
    private float expToNextLevel = 100f;

    public LevelManager() {
        EventDispatcher.getInstance().addListener(EventType.EXP_GAINED, this);
        this.currentLevel = ProgressContext.instance.getLevel();
        this.currentExp = ProgressContext.instance.getExp();
        this.expToNextLevel = ProgressContext.instance.getExpToNextLevel();
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.EXP_GAINED) {
            ExpGainedEvent data = (ExpGainedEvent) event.getData();
            addExp(data.getAmount());
        }
    }

    public void addExp(float amount) {
        currentExp += amount;
        while (currentExp >= expToNextLevel) {
            currentExp -= expToNextLevel;
            currentLevel++;
            expToNextLevel *= 1.5f; // simple scaling

            LevelUpEvent payload = new LevelUpEvent(currentLevel);
            GameEvent<LevelUpEvent> event = new GameEvent<>(EventType.LEVEL_UP, payload);

            EventDispatcher.getInstance().dispatch(event);
        }
        syncProgressContext();
    }

    private void syncProgressContext() {
        ProgressContext.instance.setLevel(currentLevel);
        ProgressContext.instance.setExp(currentExp);
        ProgressContext.instance.setExpToNextLevel(expToNextLevel);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public float getCurrentExp() {
        return currentExp;
    }

    public float getExpToNextLevel() {
        return expToNextLevel;
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.EXP_GAINED, this);
    }
}
