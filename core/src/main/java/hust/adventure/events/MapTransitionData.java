package hust.adventure.events;
 
import com.badlogic.gdx.utils.Pool;
 
/**
 * Data for MAP_TRANSITION event, supports object pooling.
 */
public class MapTransitionData implements Pool.Poolable {
    private String targetMap;
    private float spawnX;
    private float spawnY;
 
    /**
     * Default constructor for pooling.
     */
    public MapTransitionData() {
    }
 
    public MapTransitionData(String targetMap, float spawnX, float spawnY) {
        init(targetMap, spawnX, spawnY);
    }
 
    public void init(String targetMap, float spawnX, float spawnY) {
        this.targetMap = targetMap;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }
 
    @Override
    public void reset() {
        this.targetMap = null;
        this.spawnX = 0;
        this.spawnY = 0;
    }
 
    public String getTargetMap() {
        return targetMap;
    }
 
    public float getSpawnX() {
        return spawnX;
    }
 
    public float getSpawnY() {
        return spawnY;
    }
}
