package hust.adventure.events;

public class MapTransitionData {
    public final String targetMap;
    public final float spawnX;
    public final float spawnY;

    public MapTransitionData(String targetMap, float spawnX, float spawnY) {
        this.targetMap = targetMap;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }
}
