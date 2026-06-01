package hust.adventure.items.base;

/**
 * Abstract class representing items that can be equipped (Weapons, Gears). Inherits from Item.
 */
public abstract class EquipmentItem extends Item {

    public EquipmentItem() {
        super();
    }

    public EquipmentItem(final String id, final String name, final String description, final String spritePath) {
        super(id, name, description, spritePath);
    }
}
