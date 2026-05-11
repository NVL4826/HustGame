package hust.adventure.stats;

import com.badlogic.gdx.utils.Pool;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.StatChangedEvent;
import java.util.EnumMap;
import java.util.Objects;

/**
 * Manager for a player's dynamic stats. Uses the Modifier Pattern to calculate final values and notifies changes via
 * events.
 */
public final class PlayerStats {
    private final EnumMap<StatType, Stat> statsMap;
    private final EventDispatcher eventDispatcher;
    private final Pool<StatChangedEvent> eventPool;

    public PlayerStats(final CharacterData data) {
        Objects.requireNonNull(data, "CharacterData cannot be null");
        this.statsMap = new EnumMap<>(StatType.class);
        this.eventDispatcher = EventDispatcher.getInstance();
        this.eventPool = new Pool<StatChangedEvent>(10) {
            @Override
            protected StatChangedEvent newObject() {
                return new StatChangedEvent();
            }
        };

        initializeStats(data);
    }

    private void initializeStats(final CharacterData data) {
        for (final StatType type : StatType.values()) {
            final float baseValue = data.getBaseStat(type);
            final Stat stat;

            // Apply specific clamping based on game design requirements
            switch (type) {
            case COOLDOWN:
                // Max 90% reduction means min 10% multiplier
                stat = new Stat(baseValue, 0.1f, 10.0f);
                break;
            case MOVE_SPEED:
                // Min 10% speed to prevent standing still
                stat = new Stat(baseValue, 0.1f, 5.0f);
                break;
            case MAX_HP:
                stat = new Stat(baseValue, 1.0f, Float.POSITIVE_INFINITY);
                break;
            default:
                stat = new Stat(baseValue);
                break;
            }
            statsMap.put(type, stat);
        }
    }

    /**
     * Gets the current final value for a specific stat.
     *
     * @param type The stat type to retrieve.
     * @return The calculated and clamped final value.
     */
    public float getStatValue(final StatType type) {
        final Stat stat = statsMap.get(type);
        return stat != null ? stat.calculateFinalValue() : 0.0f;
    }

    /**
     * Adds a modifier to a stat.
     *
     * @param type     The stat to modify.
     * @param modifier The modifier to add.
     * @param notify   Whether to dispatch a StatChangedEvent.
     */
    public void addModifier(final StatType type, final StatModifier modifier, final boolean notify) {
        final Stat stat = statsMap.get(type);
        if (stat == null || modifier == null)
            return;

        final float oldValue = stat.calculateFinalValue();
        stat.addModifier(modifier);
        final float newValue = stat.calculateFinalValue();

        if (notify && oldValue != newValue) {
            dispatchChangeEvent(type, oldValue, newValue);
        }
    }

    /**
     * Removes a modifier from a stat.
     *
     * @param type     The stat to modify.
     * @param modifier The modifier to remove.
     * @param notify   Whether to dispatch a StatChangedEvent.
     */
    public void removeModifier(final StatType type, final StatModifier modifier, final boolean notify) {
        final Stat stat = statsMap.get(type);
        if (stat == null || modifier == null)
            return;

        final float oldValue = stat.calculateFinalValue();
        stat.removeModifier(modifier);
        final float newValue = stat.calculateFinalValue();

        if (notify && oldValue != newValue) {
            dispatchChangeEvent(type, oldValue, newValue);
        }
    }

    /**
     * Removes all modifiers for a stat from a specific source.
     *
     * @param type   The stat to modify.
     * @param source The source object.
     * @param notify Whether to dispatch a StatChangedEvent.
     */
    public void removeModifiersFromSource(final StatType type, final Object source, final boolean notify) {
        final Stat stat = statsMap.get(type);
        if (stat == null || source == null)
            return;

        final float oldValue = stat.calculateFinalValue();
        stat.removeModifiersFromSource(source);
        final float newValue = stat.calculateFinalValue();

        if (notify && oldValue != newValue) {
            dispatchChangeEvent(type, oldValue, newValue);
        }
    }

    /**
     * Updates the base value of a stat (e.g., during level up).
     *
     * @param type         The stat to update.
     * @param newBaseValue The new base value.
     * @param notify       Whether to dispatch a StatChangedEvent.
     */
    public void setBaseValue(final StatType type, final float newBaseValue, final boolean notify) {
        final Stat stat = statsMap.get(type);
        if (stat == null)
            return;

        final float oldValue = stat.calculateFinalValue();
        stat.setBaseValue(newBaseValue);
        final float newValue = stat.calculateFinalValue();

        if (notify && oldValue != newValue) {
            dispatchChangeEvent(type, oldValue, newValue);
        }
    }

    private void dispatchChangeEvent(final StatType type, final float oldValue, final float newValue) {
        final StatChangedEvent event = eventPool.obtain();
        event.init(type, oldValue, newValue);
        eventDispatcher.dispatch(event);
        eventPool.free(event);
    }
}
