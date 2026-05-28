package hust.adventure.items;

/**
 * Represents a passive Gear item in the player's inventory that provides stat boosts.
 * Supports levels 1 through 5.
 */
public class Gear {
    private final String id;
    private final String name;
    private final String description;
    private int level;

    public Gear(final String id, final String name, final String description) {
        if (id == null) {
            throw new IllegalArgumentException("Gear ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Gear Name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Gear Description cannot be null");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Gear level must be between 1 and 5");
        }
        this.level = level;
    }

    public void upgrade() {
        if (level < 5) {
            level++;
        }
    }

    public static String getDefaultName(final String id) {
        if (id == null) {
            return "";
        }
        switch (id.toLowerCase()) {
            case "spinach": return "Hành Tây (Spinach)";
            case "empty_tome": return "Sách Rỗng (Empty Tome)";
            case "wings": return "Đôi Cánh (Wings)";
            case "hollow_heart": return "Trái Tim Rỗng (Hollow Heart)";
            case "candelabrador": return "Chân Nến (Candelabrador)";
            case "attractorb": return "Nam Châm (Attractorb)";
            default: return id;
        }
    }

    public static String getDefaultDescription(final String id, final int level) {
        if (id == null) {
            return "";
        }
        switch (id.toLowerCase()) {
            case "spinach": return "Tăng 10% sát thương cho tất cả vũ khí (Cấp " + level + ").";
            case "empty_tome": return "Giảm 8% thời gian hồi chiêu của vũ khí (Cấp " + level + ").";
            case "wings": return "Tăng 10% tốc độ di chuyển của nhân vật (Cấp " + level + ").";
            case "hollow_heart": return "Tăng 20% lượng HP tối đa (+20 HP) (Cấp " + level + ").";
            case "candelabrador": return "Tăng 20% phạm vi tấn công của vũ khí (Cấp " + level + ").";
            case "attractorb": return "Tăng 20% phạm vi hút ngọc kinh nghiệm (Cấp " + level + ").";
            default: return "";
        }
    }
}
