package hust.adventure.ui.components;

import hust.adventure.entities.Player;
import hust.adventure.items.Gear;

/**
 * Action representing a choice to unlock a new gear or upgrade an existing one.
 */
public class GearUpgradeAction implements UpgradeAction {
    private final String gearId;
    private final String name;
    private final String description;
    private final boolean isUnlock;

    public GearUpgradeAction(final String gearId, final String name, final String description, final boolean isUnlock) {
        if (gearId == null) {
            throw new IllegalArgumentException("Gear ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.gearId = gearId;
        this.name = name;
        this.description = description;
        this.isUnlock = isUnlock;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void execute(final Player player) {
        if (player == null) {
            return;
        }
        if (isUnlock) {
            player.getGearManager().addGear(new Gear(gearId, name, description));
        } else {
            final Gear gear = player.getGearManager().getGear(gearId);
            if (gear != null) {
                gear.upgrade();
            }
        }

        // Trigger immediate side-effects
        if (gearId.equalsIgnoreCase("hollow_heart")) {
            player.increaseMaxHp(20f);
        }
    }
}
