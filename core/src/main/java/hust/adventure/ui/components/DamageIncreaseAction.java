package hust.adventure.ui.components;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.player.Player;

public class DamageIncreaseAction implements UpgradeAction {
    private final ProgressContext progressContext;

    public DamageIncreaseAction(final ProgressContext progressContext) {
        this.progressContext = progressContext;
    }

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
        float current = progressContext.getDamageMultiplier();
        progressContext.setDamageMultiplier(current * 2f);
    }
}
