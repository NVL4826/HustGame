package hust.adventure.items;

import com.badlogic.gdx.graphics.Color;
import hust.adventure.effects.types.ConfusionEffect;
import hust.adventure.effects.types.RegenEffect;
import hust.adventure.effects.types.SpeedBoostEffect;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry and manager for all item definitions.
 */
public final class ItemManager {
    public static final ItemManager instance = new ItemManager();
    private final Map<String, Item> items;

    private ItemManager() {
        items = new HashMap<>();
        registerItems();
    }

    private void registerItems() {
        register(new CoffeeItem("coffee_den", "Cà Phê Đen", "Hồi 10 HP, 30 Thể lực, tăng tốc trong 30s.", consumer -> {
            consumer.heal(10f);
            consumer.restoreStamina(30f);
            consumer.getStatusEffectManager().addEffect(new SpeedBoostEffect(30f, 1.3f));
        }, Arrays.asList(
            new FloatingTextInfo("+10 HP", Color.GREEN, 1.2f, -15f, 90f),
            new FloatingTextInfo("+30 SP", Color.GOLD, 1.2f, 15f, 90f),
            new FloatingTextInfo("Speed Up!", Color.CYAN, 1.2f, 0f, 120f)
        )));

        register(new CoffeeItem("coffee_sua", "Cà Phê Sữa", "Hồi 20 HP, 25 Thể lực, tăng tốc và hồi máu theo thời gian.", consumer -> {
            consumer.heal(20f);
            consumer.restoreStamina(25f);
            consumer.getStatusEffectManager().addEffect(new SpeedBoostEffect(20f, 1.3f));
            consumer.getStatusEffectManager().addEffect(new RegenEffect(20f, 1f));
        }, Arrays.asList(
            new FloatingTextInfo("+20 HP", Color.GREEN, 1.2f, -15f, 90f),
            new FloatingTextInfo("+25 SP", Color.GOLD, 1.2f, 15f, 90f),
            new FloatingTextInfo("Regen & Speed Up!", Color.CYAN, 1.2f, 0f, 120f)
        )));

        register(new CoffeeItem("coffee_da", "Cà Phê Đá", "Hồi 10 Thể lực.", consumer -> {
            consumer.restoreStamina(10f);
        }, Arrays.asList(
            new FloatingTextInfo("+10 SP", Color.GOLD, 1.2f, 0f, 90f)
        )));

        register(new CoffeeItem("coffee_chon", "Cà Phê Chồn", "Hồi 60 Thể lực, siêu tăng tốc nhưng gây ảo giác.", consumer -> {
            consumer.heal(5f);
            consumer.restoreStamina(60f);
            consumer.getStatusEffectManager().addEffect(new SpeedBoostEffect(45f, 1.5f));
            consumer.getStatusEffectManager().addEffect(new ConfusionEffect(45f));
        }, Arrays.asList(
            new FloatingTextInfo("+5 HP", Color.GREEN, 1.2f, -20f, 90f),
            new FloatingTextInfo("+60 SP", Color.GOLD, 1.2f, 20f, 90f),
            new FloatingTextInfo("Hyper Speed!", Color.CYAN, 1.2f, 0f, 120f),
            new FloatingTextInfo("Confused!", Color.MAGENTA, 1.2f, 0f, 150f)
        )));

        register(new ConsumableItem("energy_drink", "Nước tăng lực", "Hồi 40 Thể lực.", consumer -> {
            consumer.restoreStamina(40f);
        }, Arrays.asList(
            new FloatingTextInfo("+40 SP", Color.GOLD, 1.2f, 0f, 90f)
        )));

        register(new ConsumableItem("kho_ga", "Khô gà lá chanh", "Hồi 25 HP.", consumer -> {
            consumer.heal(25f);
        }, Arrays.asList(
            new FloatingTextInfo("+25 HP", Color.GREEN, 1.2f, 0f, 90f)
        )));

        register(new BaseItem("note", "Ghi chú", "Một mảnh giấy nhỏ có chữ."));
        register(new BaseItem("usb", "USB", "USB chứa mã nguồn sửa lỗi hệ thống."));
        register(new BaseItem("whip", "Roi da", "Tấn công kẻ địch trước mặt theo hình chữ nhật."));
        register(new BaseItem("magic_wand", "Gậy phép", "Bắn tia phép vào kẻ địch gần nhất."));
        register(new BaseItem("garlic", "Tỏi bảo hộ", "Tạo vòng hào quang gây sát thương xung quanh."));
        register(new BaseItem("bun_dau", "Bun Dau", "Bắn một viên đậu theo hướng nhìn của người chơi."));
    }

    public void register(final Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.put(item.getId(), item);
    }

    public Item getItem(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return items.get(id);
    }
}
