package hust.adventure.items;

/**
 * Catalog for weapon names, upgrade descriptions, and gear properties.
 * Provides unified access to upgrade descriptions for level up screens.
 */
public final class UpgradeCatalog {

    private UpgradeCatalog() {
        // Prevent instantiation
    }

    /**
     * Gets the display name of a weapon by its unique identifier.
     *
     * @param id The unique weapon identifier (e.g. "whip", "magic_wand").
     * @return The localized display name of the weapon.
     */
    public static String getWeaponName(final String id) {
        if (id == null) {
            return "";
        }
        switch (id.toLowerCase()) {
            case "whip": return "Roi Da (Whip)";
            case "magic_wand": return "Gậy Phép (Magic Wand)";
            case "garlic": return "Tỏi Bảo Hộ (Garlic)";
            case "bun_dau": return "Bún Đậu (Knife)";
            default: return id;
        }
    }

    /**
     * Gets the description of a weapon at a specific level.
     *
     * @param id    The unique weapon identifier.
     * @param level The level of the weapon (1 to 5).
     * @return The description of the upgrade/unlock effect for that level.
     */
    public static String getWeaponLevelDescription(final String id, final int level) {
        if (id == null) {
            return "";
        }
        switch (id.toLowerCase()) {
            case "whip":
                switch (level) {
                    case 1: return "Tấn công theo chiều ngang, xuyên qua mọi kẻ địch.";
                    case 2: return "Tấn công thêm 1 lần (ngược hướng).";
                    case 3: return "Sát thương gốc +5.";
                    case 4: return "Kích thước vùng đánh +10%, Sát thương gốc +5.";
                    case 5: return "Sát thương gốc +5.";
                    default: return "";
                }
            case "magic_wand":
                switch (level) {
                    case 1: return "Bắn tự động vào kẻ địch gần nhất.";
                    case 2: return "Bắn thêm 1 tia phép.";
                    case 3: return "Giảm hồi chiêu đi 0.2 giây.";
                    case 4: return "Bắn thêm 1 tia phép.";
                    case 5: return "Sát thương gốc +10.";
                    default: return "";
                }
            case "garlic":
                switch (level) {
                    case 1: return "Tạo vòng bảo hộ gây sát thương xung quanh.";
                    case 2: return "Phạm vi +40%, Sát thương gốc +2.";
                    case 3: return "Giảm hồi chiêu đi 0.1s, Sát thương gốc +1.";
                    case 4: return "Phạm vi +20%, Sát thương gốc +1.";
                    case 5: return "Giảm hồi chiêu đi 0.1s, Sát thương gốc +2.";
                    default: return "";
                }
            case "bun_dau":
                switch (level) {
                    case 1: return "Bắn theo hướng di chuyển cuối cùng khi bấm Space.";
                    case 2: return "Bắn thêm 1 viên đậu.";
                    case 3: return "Bắn thêm 1 viên đậu, Sát thương gốc +5.";
                    case 4: return "Bắn thêm 1 viên đậu.";
                    case 5: return "Viên đậu xuyên qua thêm 1 mục tiêu.";
                    default: return "";
                }
            default:
                return "";
        }
    }

    /**
     * Gets the display name of a gear item by its unique identifier.
     *
     * @param id The unique gear identifier.
     * @return The localized display name of the gear.
     */
    public static String getGearName(final String id) {
        return Gear.getDefaultName(id);
    }

    /**
     * Gets the description of a gear item at a specific level.
     *
     * @param id    The unique gear identifier.
     * @param level The level of the gear (1 to 5).
     * @return The description of the upgrade/unlock effect for that level.
     */
    public static String getGearLevelDescription(final String id, final int level) {
        return Gear.getDefaultDescription(id, level);
    }
}
