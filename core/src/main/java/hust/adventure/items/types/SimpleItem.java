package hust.adventure.items.types;

import hust.adventure.items.BaseItem;

public class SimpleItem extends BaseItem {
    private final String id;
    private final String name;
    private final String description;

    public SimpleItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String getName() { return name; }

    @Override
    public String getDescription() { return description; }
}
