package hust.adventure.effects;

import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.status.StatusFlag;

/**
 * Interface representing a status effect (buff/debuff) on a BaseActor.
 */
public interface StatusEffect {
    /**
     * Called when the effect starts.
     * 
     * @param target The actor receiving the effect.
     */
    void onStart(BaseActor target);

    /**
     * Called every frame to update the effect logic.
     * 
     * @param target The actor receiving the effect.
     * @param delta  Time since last frame.
     */
    void update(BaseActor target, float delta);

    /**
     * Called when the effect ends.
     * 
     * @param target The actor that had the effect.
     */
    void onEnd(BaseActor target);

    /**
     * @return Current remaining duration of the effect.
     */
    float getDuration();

    /**
     * @return The status flag associated with this effect, or null if none.
     */
    StatusFlag getFlag();
}
