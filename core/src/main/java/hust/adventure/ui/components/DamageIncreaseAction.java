package hust.adventure.ui.components;

import hust.adventure.entities.Player;

public class DamageIncreaseAction implements UpgradeAction {
    @Override
    public String getName() {
        return "Increase Damage";
    }

    @Override
    public String getDescription() {
        return "+10% to all damage";
    }

    @Override
    public void execute(Player player) {
        // Assume player has a stats manager to increase damage, or apply stat change event
    }
}
