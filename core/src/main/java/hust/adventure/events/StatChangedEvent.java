package hust.adventure.events;

import hust.adventure.stats.StatType;
import java.util.Objects;

/**
 * Event triggered when a player stat changes. Implements Poolable to minimize garbage collection during frequent stat
 * updates.
 */
public final class StatChangedEvent extends GameEvent<StatChangedEvent.StatChangeData> {

    /**
     * Mutable data container for stat changes.
     */
    public static final class StatChangeData {
        private StatType type;
        private float oldValue;
        private float newValue;

        private StatChangeData() {
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

        private void update(final StatType type, final float oldValue, final float newValue) {
            this.type = type;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        private void reset() {
            this.type = null;
            this.oldValue = 0.0f;
            this.newValue = 0.0f;
        }
    }

    public StatChangedEvent() {
        super(EventType.STAT_CHANGED, new StatChangeData());
    }

    /**
     * Initializes the event with new data.
     * 
     * @param type     The type of stat that changed.
     * @param oldValue The value before the change.
     * @param newValue The value after the change.
     * @return This event instance for chaining.
     * @throws NullPointerException if type is null.
     */
    public StatChangedEvent init(final StatType type, final float oldValue, final float newValue) {
        Objects.requireNonNull(type, "StatType cannot be null");
        getData().update(type, oldValue, newValue);
        return this;
    }

    @Override
    public void reset() {
        getData().reset();
    }
}
