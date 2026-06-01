package hust.adventure.items.base;

import com.badlogic.gdx.utils.Array;

/**
 * Catalog helper wrapping the list of item configurations.
 */
public class ItemConfigCatalog {
    private Array<ItemConfig> items;

    /**
     * Gets the array of item configurations.
     *
     * @return array of ItemConfig
     */
    public Array<ItemConfig> getItems() {
        return items;
    }

    /**
     * Sets the array of item configurations.
     *
     * @param items array of ItemConfig
     */
    public void setItems(final Array<ItemConfig> items) {
        this.items = items;
    }
}
