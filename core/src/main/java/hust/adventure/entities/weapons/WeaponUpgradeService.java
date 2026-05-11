package hust.adventure.entities.weapons;

import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.RewardSelectedEvent;
import hust.adventure.entities.Player;

public class WeaponUpgradeService implements EventListener, com.badlogic.gdx.utils.Disposable {
    private final Player player;

    public WeaponUpgradeService(Player player) {
        this.player = player;
        EventDispatcher.getInstance().addListener(EventType.REWARD_SELECTED, this);
    }

    @Override
    public void onEvent(GameEvent<?> event) {
        if (event.getType() == EventType.REWARD_SELECTED) {
            RewardSelectedEvent data = (RewardSelectedEvent) event.getData();
            String rewardId = data.getRewardId();
            
            // Dummy logic
            for (Weaponable w : player.getWeaponManager().getWeapons()) {
                if (w instanceof WhipWeapon && "whip_upgrade".equals(rewardId)) {
                    ((BaseWeapon) w).addBaseDamage(5f);
                }
            }
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.REWARD_SELECTED, this);
    }
}
