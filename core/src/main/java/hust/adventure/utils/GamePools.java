package hust.adventure.utils;

import com.badlogic.gdx.utils.PoolManager;
import com.badlogic.gdx.math.Vector2;
import hust.adventure.events.GameEvent;
import hust.adventure.events.ItemPickedUpEvent;
import hust.adventure.events.MapTransitionData;
import hust.adventure.events.ExpGainedEvent;
import hust.adventure.events.LevelUpEvent;

import hust.adventure.entities.combat.Projectile;
import hust.adventure.entities.interactables.ItemDrop;
import hust.adventure.entities.interactables.ExpGem;
import hust.adventure.entities.interactables.TreasureChest;
import hust.adventure.entities.enemies.*;
import hust.adventure.entities.components.SimpleSwarmBehavior;
import hust.adventure.entities.components.FleeBehavior;
import hust.adventure.entities.components.BouncingBehavior;
import hust.adventure.events.EntityDamagedEvent;
import hust.adventure.events.RewardSelectedEvent;
import hust.adventure.events.TimeLimitReachedEvent;
import hust.adventure.events.TreasureOpenedEvent;
import hust.adventure.ui.DamageText;

/**
 * Central registry for object pools using PoolManager.
 */
public class GamePools {
    private static final PoolManager manager = new PoolManager();

    static {
        // Register events
        manager.addPool(GameEvent::new);
        manager.addPool(ItemPickedUpEvent::new);
        manager.addPool(MapTransitionData::new);
        manager.addPool(ExpGainedEvent::new);
        manager.addPool(LevelUpEvent::new);
        manager.addPool(EntityDamagedEvent::new);
        manager.addPool(DamageText::new);
        manager.addPool(TreasureOpenedEvent::new);
        manager.addPool(RewardSelectedEvent::new);
        manager.addPool(TimeLimitReachedEvent::new);
        manager.addPool(TreasureChest::new);

        // Register combat entities
        manager.addPool(Projectile::new);
        manager.addPool(ItemDrop::new);
        manager.addPool(ExpGem::new);

        // Register enemies
        manager.addPool(SyntaxErrorEnemy::new);
        manager.addPool(NullPointerEnemy::new);
        manager.addPool(InfiniteLoopEnemy::new);
        manager.addPool(StackOverflowEnemy::new);
        manager.addPool(LibraryBoss::new);
        manager.addPool(FinalBoss::new);
        manager.addPool(Vector2::new);
        manager.addPool(SimpleSwarmBehavior::new);
        manager.addPool(FleeBehavior::new);
        manager.addPool(BouncingBehavior::new);
    }

    /**
     * Obtains an object from the pool.
     */
    public static <T> T obtain(Class<T> type) {
        return manager.obtain(type);
    }

    /**
     * Specialized method to obtain a GameEvent with correct generic type. Centralizes the unchecked cast to avoid
     * warnings at call sites.
     */
    @SuppressWarnings("unchecked")
    public static <T> GameEvent<T> obtainEvent() {
        return (GameEvent<T>) manager.obtain(GameEvent.class);
    }

    /**
     * Frees an object back to the pool.
     */
    public static void free(Object object) {
        if (object != null) {
            manager.free(object);
        }
    }

    private GamePools() {
        // Utility class
    }
}
