package hust.adventure.ui.components;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.Player;

public class DamageIncreaseAction implements UpgradeAction {
    @Override
    public String getName() {
        return "Tăng sát thương";
    }

    @Override
    public String getDescription() {
        return "+100% sát thương cho tất cả vũ khí";
    }

    @Override
    public void execute(Player player) {
        // Nhân đôi damageMultiplier trong ProgressContext → persist qua map transition
        float current = ProgressContext.instance.getDamageMultiplier();
        ProgressContext.instance.setDamageMultiplier(current * 2f);
    }
}
