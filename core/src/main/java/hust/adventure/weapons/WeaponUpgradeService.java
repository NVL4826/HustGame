package hust.adventure.weapons;

import hust.adventure.events.EventDispatcher;
import hust.adventure.events.EventListener;
import hust.adventure.events.EventType;
import hust.adventure.events.GameEvent;
import hust.adventure.events.RewardSelectedEvent;
import hust.adventure.entities.Player;
import com.badlogic.gdx.utils.Disposable;

/**
 * Handles applying weapon level-up rewards when triggered by gameplay events (like the roulette wheel).
 */
public final class WeaponUpgradeService implements EventListener, Disposable {
    private final Player player;

    public WeaponUpgradeService(final Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = player;
        EventDispatcher.getInstance().addListener(EventType.REWARD_SELECTED, this);
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
            final String weaponId = data.getRewardId();
            if (weaponId == null) {
                return;
            }

            for (final Weaponable w : player.getWeaponManager().getWeapons()) {
                if (weaponId.equalsIgnoreCase(w.getId())) {
                    w.upgrade(0, 0); // Upgrade weapon level by 1 using config specifications
                }
            }
        }
    }

    @Override
    public void dispose() {
        EventDispatcher.getInstance().removeListener(EventType.REWARD_SELECTED, this);
    }
}
