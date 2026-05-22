package hust.adventure.items.weapons;

import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.RewardSelectedEvent;
import hust.adventure.entities.Player;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public final class WeaponUpgradeService implements EventListener, Disposable {
    private final Player player;
    private final Map<String, WeaponUpgradeDefinition> upgradeRegistry;

    public WeaponUpgradeService(final Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = player;
        this.upgradeRegistry = new HashMap<>();
        initDefaultUpgrades();
        EventDispatcher.getInstance().addListener(EventType.REWARD_SELECTED, this);
    }

    private void initDefaultUpgrades() {
        registerUpgrade(new WeaponUpgradeDefinition("whip_upgrade", "whip", 5f, 0f));
        registerUpgrade(new WeaponUpgradeDefinition("wand_power", "magic_wand", 3f, 0.1f));
        registerUpgrade(new WeaponUpgradeDefinition("garlic_range", "garlic", 1f, 0.05f));
        registerUpgrade(new WeaponUpgradeDefinition("bundau_upgrade", "bun_dau", 4f, 0.05f));
    }

    public void registerUpgrade(final WeaponUpgradeDefinition def) {
        if (def == null) {
            throw new IllegalArgumentException("Upgrade definition cannot be null");
        }
        upgradeRegistry.put(def.rewardId, def);
    }

    @Override
    public void onEvent(final GameEvent<?> event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.getType() == EventType.REWARD_SELECTED) {
            final RewardSelectedEvent data = (RewardSelectedEvent) event.getData();
            if (data == null) {
                return;
            }
            final String rewardId = data.getRewardId();

            final WeaponUpgradeDefinition upgrade = upgradeRegistry.get(rewardId);
            if (upgrade == null) {
                return;
            }

            for (final Weaponable w : player.getWeaponManager().getWeapons()) {
                if (upgrade.targetWeaponId.equals(w.getId())) {
                    w.upgrade(upgrade.damageBonus, upgrade.cooldownReduction);
                }
            }
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.REWARD_SELECTED, this);
    }

    /**
     * Data class for weapon upgrade definitions.
     */
    public static class WeaponUpgradeDefinition {
        public final String rewardId;
        public final String targetWeaponId;
        public final float damageBonus;
        public final float cooldownReduction;

        public WeaponUpgradeDefinition(final String rewardId, final String targetWeaponId, final float damageBonus,
                final float cooldownReduction) {
            if (rewardId == null) {
                throw new IllegalArgumentException("Reward ID cannot be null");
            }
            if (targetWeaponId == null) {
                throw new IllegalArgumentException("Target Weapon ID cannot be null");
            }
            this.rewardId = rewardId;
            this.targetWeaponId = targetWeaponId;
            this.damageBonus = damageBonus;
            this.cooldownReduction = cooldownReduction;
        }
    }
}
