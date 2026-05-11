package hust.adventure.stats;

import com.badlogic.gdx.utils.Pool.Poolable;
import java.util.Objects;

/**
 * Represents a modifier applied to a stat.
 * Implements Poolable to support efficient memory management.
 */
public final class StatModifier implements Poolable {
    private float value;
    private ModifierType type;
    private Object source;

    /**
     * Default constructor for pooling.
     */
    public StatModifier() {
        this.value = 0;
        this.type = null;
        this.source = null;
    }

    /**
     * Initializes the modifier.
     *
     * @param value The numerical value of the modifier.
     * @param type The type of calculation (FLAT or PERCENTAGE).
     * @param source The object that originated this modifier (e.g., an Item or Buff).
     * @return This modifier for chaining.
     * @throws NullPointerException if type or source is null.
     */
    public StatModifier init(final float value, final ModifierType type, final Object source) {
        Objects.requireNonNull(type, "ModifierType cannot be null");
        Objects.requireNonNull(source, "Source cannot be null");
        this.value = value;
        this.type = type;
        this.source = source;
        return this;
    }

    public float getValue() {
        return value;
    }

    public ModifierType getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    @Override
    public void reset() {
        this.value = 0;
        this.type = null;
        this.source = null;
    }
}
