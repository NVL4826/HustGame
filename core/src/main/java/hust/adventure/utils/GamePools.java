package hust.adventure.utils;
 
import com.badlogic.gdx.utils.PoolManager;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.entities.interactables.ExpGem;
import hust.adventure.ui.DamageText;
 
/**
 * Central registry for object pools using PoolManager.
 * Only high-frequency objects are pooled to balance performance and complexity.
 */
public class GamePools {
    private static final PoolManager manager = new PoolManager();
 
    static {
        // Register essential high-frequency objects
        manager.addPool(DamageText::new);
        manager.addPool(Projectile::new);
        manager.addPool(ExpGem::new);
    }
 
    /**
     * Obtains an object from the pool.
     */
    public static <T> T obtain(Class<T> type) {
        return manager.obtain(type);
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
