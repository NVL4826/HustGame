package hust.adventure.ui.components;

import hust.adventure.entities.Player;
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.weapons.Weaponable;

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
        // Tăng gấp đôi baseDamage của tất cả vũ khí (= +100%)
        for (Weaponable w : player.getWeaponManager().getWeapons()) {
            if (w instanceof BaseWeapon) {
                BaseWeapon bw = (BaseWeapon) w;
                bw.addBaseDamage(bw.getBaseDamage()); // add 100% current damage
            }
        }
    }
}
