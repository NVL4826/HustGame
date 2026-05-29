package hust.adventure.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import hust.adventure.items.ConsumableItemConfig;
import hust.adventure.items.ItemConfig;

/**
 * Data manager for loading and retrieving item configurations.
 * Handles polymorphic JSON deserialization of items.
 */
public class ItemDataManager {
    private final ObjectMap<String, ItemConfig> itemConfigs = new ObjectMap<>();

    /**
     * Initializes the manager and loads the configurations from the specified path.
     *
     * @param configsJsonPath the relative path to the configuration file
     */
    public ItemDataManager(final String configsJsonPath) {
        loadConfigs(configsJsonPath);
    }

    private void loadConfigs(final String path) {
        try {
            final Json json = new Json();
            final JsonReader reader = new JsonReader();
            final JsonValue root = reader.parse(Gdx.files.internal(path));
            final JsonValue itemsArray = root.get("items");

            if (itemsArray != null) {
                for (JsonValue entry = itemsArray.child; entry != null; entry = entry.next) {
                    final String type = entry.getString("type", "base");
                    final ItemConfig config;

                    if ("consumable".equalsIgnoreCase(type)) {
                        config = json.readValue(ConsumableItemConfig.class, entry);
                    } else {
                        config = json.readValue(ItemConfig.class, entry);
                    }

                    if (config != null && config.getId() != null) {
                        itemConfigs.put(config.getId().toLowerCase(), config);
                    }
                }
            }
        } catch (final Exception e) {
            Gdx.app.error("ItemDataManager", "Failed to load item configurations from: " + path, e);
        }
    }

    /**
     * Retrieves the configuration for a specific item ID.
     *
     * @param id the unique identifier of the item
     * @return the corresponding ItemConfig, or null if not found
     */
    public ItemConfig getConfig(final String id) {
        if (id == null) {
            return null;
        }
        return itemConfigs.get(id.toLowerCase());
    }

    /**
     * Retrieves all loaded item configurations.
     *
     * @return an iterable collection of all ItemConfig
     */
    public Iterable<ItemConfig> getAllConfigs() {
        return itemConfigs.values();
    }
}
