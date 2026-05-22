package hust.adventure.events;

import hust.adventure.stats.StatType;
import java.util.Objects;

/**
 * Event triggered when a player stat changes.
 */
public final class StatChangedEvent extends GameEvent<StatChangedEvent.StatChangeData> {

    /**
     * Data container for stat changes.
     */
    public static final class StatChangeData {
        private final StatType type;
        private final float oldValue;
        private final float newValue;

        public StatChangeData(final StatType type, final float oldValue, final float newValue) {
            this.type = type;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public StatType getType() {
            return type;
        }

        public float getOldValue() {
            return oldValue;
        }

        public float getNewValue() {
            return newValue;
        }
    }

    public StatChangedEvent(final StatType type, final float oldValue, final float newValue) {
        super(EventType.STAT_CHANGED,
                new StatChangeData(Objects.requireNonNull(type, "StatType cannot be null"), oldValue, newValue));
    }
}
