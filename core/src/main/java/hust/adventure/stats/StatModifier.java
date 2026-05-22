package hust.adventure.stats;
 
import java.util.Objects;
 
/**
 * Represents a modifier applied to a stat.
 */
public final class StatModifier {
    private final float value;
    private final ModifierType type;
    private final Object source;
 
    /**
     * Creates a new stat modifier.
     *
     * @param value The numerical value of the modifier.
     * @param type The type of calculation (FLAT or PERCENTAGE).
     * @param source The object that originated this modifier (e.g., an Item or Buff).
     * @throws NullPointerException if type or source is null.
     */
    public StatModifier(final float value, final ModifierType type, final Object source) {
        this.value = value;
        this.type = Objects.requireNonNull(type, "ModifierType cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
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
}
