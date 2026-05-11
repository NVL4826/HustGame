package hust.adventure.wave;

import com.badlogic.gdx.utils.Array;

/**
 * Data structure for the entire wave configuration.
 */
public class WaveConfig {
    public Array<WaveEntry> waves;

    public static class WaveEntry {
        public float timeStart;
        public float timeEnd;
        public String enemyType;
        public float spawnInterval;
        public int spawnCount;
        public String pattern;
    }
}
