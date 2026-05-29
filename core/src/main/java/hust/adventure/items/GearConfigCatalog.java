package hust.adventure.items;

import com.badlogic.gdx.utils.Array;

/**
 * Catalog helper wrapping the list of gear configurations.
 */
public class GearConfigCatalog {
    private Array<GearConfig> gears;

    /**
     * Gets the array of gear configurations.
     *
     * @return array of GearConfig
     */
    public Array<GearConfig> getGears() {
        return gears;
    }

    /**
     * Sets the array of gear configurations.
     *
     * @param gears array of GearConfig
     */
    public void setGears(final Array<GearConfig> gears) {
        this.gears = gears;
    }
}
