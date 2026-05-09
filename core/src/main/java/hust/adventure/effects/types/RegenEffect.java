package hust.adventure.effects.types;

import hust.adventure.effects.StatusEffect;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.status.StatusFlag;

public class RegenEffect implements StatusEffect {
    private float duration;
    private final float amountPerSecond;

    public RegenEffect(float duration, float amountPerSecond) {
        this.duration = duration;
        this.amountPerSecond = amountPerSecond;
    }

    @Override
    public void onStart(BaseActor target) {
    }

    @Override
    public void update(BaseActor target, float delta) {
        if (duration > 0) {
            target.heal(amountPerSecond * delta);
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
        return StatusFlag.REGEN_HP;
    }
}
