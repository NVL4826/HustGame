package hust.adventure.ui;

/**
 * Registry containing static lists of debug options for maps, items, and monsters.
 */
public class DebugOptionRegistry {

    public static final DebugOption[] MAP_OPTIONS = {
        new DebugOption("tang1.tmx", "Floor 1 (tang1)"),
        new DebugOption("library.tmx", "Library"),
        new DebugOption("lab.tmx", "Lab"),
        new DebugOption("boss_room.tmx", "Boss Room"),
        new DebugOption("Final Outside.tmx", "Final Outside"),
        new DebugOption("test.tmx", "Test Map"),
        new DebugOption("tsx/map_1.tmx", "Map 1")
    };

    public static final DebugOption[] ITEM_OPTIONS = {
        new DebugOption("coffee_den", "Coffee Den"),
        new DebugOption("coffee_sua", "Coffee Sua"),
        new DebugOption("coffee_da", "Coffee Da"),
        new DebugOption("coffee_chon", "Coffee Chon"),
        new DebugOption("energy_drink", "Energy Drink"),
        new DebugOption("kho_ga", "Dried Chicken"),
        new DebugOption("whip", "Whip (Roi)"),
        new DebugOption("magic_wand", "Magic Wand (Đua)"),
        new DebugOption("garlic", "Garlic (Toi)"),
        new DebugOption("bun_dau", "Bun Dau (Đậu)")
    };

    public static final DebugOption[] MONSTER_OPTIONS = {
        new DebugOption("syntax_error", "Syntax Error"),
        new DebugOption("null_pointer", "Null Pointer"),
        new DebugOption("infinite_loop", "Infinite Loop"),
        new DebugOption("stack_overflow", "Stack Overflow"),
        new DebugOption("libboss", "Library Boss"),
        new DebugOption("finalboss", "Final Boss (THT)")
    };

    /**
     * Gets the corresponding array of debug options for the given SelectionMode.
     *
     * @param mode the selection mode
     * @return an array of DebugOption, or null if the mode is NONE or invalid
     */
    public static DebugOption[] getOptions(SelectionMode mode) {
        if (mode == null) {
            return null;
        }
        switch (mode) {
            case MAP:
                return MAP_OPTIONS;
            case ITEM:
                return ITEM_OPTIONS;
            case MONSTER:
                return MONSTER_OPTIONS;
            default:
                return null;
        }
    }
}
