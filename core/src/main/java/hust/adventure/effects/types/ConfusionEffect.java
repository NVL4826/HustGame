package hust.adventure.effects.types;

import hust.adventure.effects.StatusEffect;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.status.StatusFlag;

public class ConfusionEffect implements StatusEffect {
    private float duration;

    public ConfusionEffect(float duration) {
        this.duration = duration;
    }

    @Override
    public void onStart(BaseActor target) {
    }

    @Override
    public void update(BaseActor target, float delta) {
        if (duration > 0) {
            duration -= delta;
        }
    }

    @Override
    public void onEnd(BaseActor target) {
    }

    @Override
    public float getDuration() {
        return duration;
    }

    @Override
    public StatusFlag getFlag() {
        return StatusFlag.CONFUSED;
    }
}
