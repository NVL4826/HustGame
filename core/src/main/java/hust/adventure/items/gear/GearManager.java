package hust.adventure.items.gear;

import com.badlogic.gdx.utils.Array;
import hust.adventure.items.equipment.StudentEquipment;

/**
 * Adapter module maintaining backward compatibility for passive gear operations while delegating
 * to the deepened StudentEquipment module when present.
 */
public class GearManager {
    private final StudentEquipment equipment;
    private final Array<Gear> legacyGears;

    public GearManager() {
        this(null);
    }

    public GearManager(final StudentEquipment equipment) {
        this.equipment = equipment;
        this.legacyGears = (equipment == null) ? new Array<>() : null;
    }

    /**
     * Gets all currently equipped gears.
     * 
     * @return Array of Gear items.
     */
    public Array<Gear> getGears() {
        return equipment != null ? equipment.getGears() : legacyGears;
    }

    /**
     * Finds an equipped gear by its ID.
     * 
     * @param id The unique identifier of the gear.
     * @return The Gear object, or null if not equipped.
     */
    public Gear getGear(final String id) {
        if (id == null) {
            return null;
        }
        if (equipment != null) {
            return equipment.getGear(id);
        }
        for (final Gear gear : legacyGears) {
            if (gear.getId().equalsIgnoreCase(id)) {
                return gear;
            }
        }
        return null;
    }

    /**
     * Equips a new gear item.
     * 
     * @param gear The gear to equip.
     */
    public void addGear(final Gear gear) {
        if (gear == null) {
            throw new IllegalArgumentException("Gear cannot be null");
        }
        if (equipment != null) {
            equipment.addGear(gear);
        } else {
            if (getGear(gear.getId()) == null) {
                legacyGears.add(gear);
            }
        }
    }
}
