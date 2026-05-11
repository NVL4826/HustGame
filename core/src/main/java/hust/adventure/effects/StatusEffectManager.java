package hust.adventure.effects;

import com.badlogic.gdx.utils.Array;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.status.StatusFlag;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages active status effects for a specific BaseActor.
 */
public class StatusEffectManager {
    private final BaseActor owner;
    private final Array<StatusEffect> activeEffects;
    private final Map<StatusFlag, Integer> flagCounts;

    public StatusEffectManager(final BaseActor owner) {
        if (owner == null)
            throw new IllegalArgumentException("Owner cannot be null");
        this.owner = owner;
        this.activeEffects = new Array<>();
        this.flagCounts = new HashMap<>();
    }

    /**
     * Adds a new effect to the actor.
     * 
     * @param effect The effect to add.
     */
    public void addEffect(final StatusEffect effect) {
        if (effect == null)
            return;

        activeEffects.add(effect);
        effect.onStart(owner);

        final StatusFlag flag = effect.getFlag();
        if (flag != null) {
            flagCounts.put(flag, flagCounts.getOrDefault(flag, 0) + 1);
        }
    }

    /**
     * Updates all active effects and handles expiration.
     * 
     * @param delta Time since last frame.
     */
    public void update(final float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            final StatusEffect effect = activeEffects.get(i);
            effect.update(owner, delta);

            if (effect.getDuration() <= 0) {
                removeEffect(i);
            }
        }
    }

    private void removeEffect(final int index) {
        final StatusEffect effect = activeEffects.removeIndex(index);
        effect.onEnd(owner);

        final StatusFlag flag = effect.getFlag();
        if (flag != null) {
            final int count = flagCounts.getOrDefault(flag, 0);
            if (count > 1) {
                flagCounts.put(flag, count - 1);
            } else {
                flagCounts.remove(flag);
            }
        }
    }

    /**
     * Checks if a status flag is currently active.
     * 
     * @param flag The flag to check.
     * @return True if active.
     */
    public boolean hasStatus(final StatusFlag flag) {
        return flagCounts.containsKey(flag);
    }
 
    /**
     * Clears all active effects. Used for object pooling reset.
     */
    public void clear() {
        activeEffects.clear();
        flagCounts.clear();
    }
}
