package hust.adventure.stats;

import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a single statistic, including its base value and active modifiers.
 */
public final class Stat {
    private float baseValue;
    private final float minValue;
    private final float maxValue;
    private final List<StatModifier> modifiers;

    /**
     * Creates a new Stat with specified bounds.
     *
     * @param baseValue The initial base value.
     * @param minValue The minimum allowed value after calculations.
     * @param maxValue The maximum allowed value after calculations.
     */
    public Stat(final float baseValue, final float minValue, final float maxValue) {
        this.baseValue = baseValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.modifiers = new ArrayList<>();
    }

    /**
     * Simplified constructor with default bounds (float min/max).
     *
     * @param baseValue The initial base value.
     */
    public Stat(final float baseValue) {
        this(baseValue, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public void addModifier(final StatModifier modifier) {
        if (modifier != null && !modifiers.contains(modifier)) {
            modifiers.add(modifier);
        }
    }

    public void removeModifier(final StatModifier modifier) {
        modifiers.remove(modifier);
    }

    /**
     * Removes all modifiers that originated from a specific source.
     *
     * @param source The source object to match.
     */
    public void removeModifiersFromSource(final Object source) {
        if (source == null) return;
        modifiers.removeIf(mod -> mod.getSource() == source);
    }

    public void setBaseValue(final float baseValue) {
        this.baseValue = baseValue;
    }

    public float getBaseValue() {
        return baseValue;
    }

    /**
     * Calculates the final value applying all modifiers and clamping to bounds.
     * Formula: (baseValue + flatSum) * (1 + percentageSum)
     *
     * @return The calculated and clamped value.
     */
    public float calculateFinalValue() {
        float flatSum = 0;
        float percentSum = 0;

        for (final StatModifier mod : modifiers) {
            if (mod.getType() == ModifierType.FLAT) {
                flatSum += mod.getValue();
            } else if (mod.getType() == ModifierType.PERCENTAGE) {
                percentSum += mod.getValue();
            }
        }

        final float result = (baseValue + flatSum) * (1.0f + percentSum);
        return MathUtils.clamp(result, minValue, maxValue);
    }
}
