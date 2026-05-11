package hust.adventure.inventory;

import hust.adventure.items.Item;
import hust.adventure.items.ItemManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp quản lý túi đồ của người chơi.
 * Lưu trữ danh sách các vật phẩm và số lượng tương ứng.
 * Tuân thủ Flyweight pattern: chỉ lưu trữ tham chiếu đến Item và số lượng.
 */
public class Inventory {

    // Lưu trữ đối tượng Item (Logical) làm key
    private final Map<Item, Integer> items;

    public Inventory() {
        items = new HashMap<>();
    }

    /**
     * Thêm vật phẩm vào túi đồ.
     *
     * @param item Đối tượng vật phẩm
     * @param amount Số lượng muốn thêm
     */
    public void addItem(Item item, int amount) {
        if (item == null || amount <= 0)
            return;
        items.put(item, items.getOrDefault(item, 0) + amount);
    }

    /**
     * Thêm vật phẩm vào túi đồ qua ID (Hỗ trợ từ ItemManager).
     *
     * @param itemId Mã định danh vật phẩm
     * @param amount Số lượng muốn thêm
     */
    public void addItem(String itemId, int amount) {
        Item item = ItemManager.instance.getItem(itemId);
        if (item != null) {
            addItem(item, amount);
        }
    }

    /**
     * Xóa vật phẩm khỏi túi đồ.
     *
     * @param item Đối tượng vật phẩm
     * @param amount Số lượng muốn xóa
     * @return true nếu xóa thành công, false nếu không đủ.
     */
    public boolean removeItem(Item item, int amount) {
        if (item == null || amount <= 0)
            return false;

        int currentAmount = items.getOrDefault(item, 0);
        if (currentAmount >= amount) {
            int newAmount = currentAmount - amount;
            if (newAmount == 0) {
                items.remove(item);
            } else {
                items.put(item, newAmount);
            }
            return true;
        }
        return false;
    }

    /**
     * Xóa vật phẩm khỏi túi đồ qua ID.
     */
    public boolean removeItem(String itemId, int amount) {
        Item item = ItemManager.instance.getItem(itemId);
        return item != null && removeItem(item, amount);
    }

    /**
     * Kiểm tra xem túi đồ có chứa một lượng vật phẩm nhất định hay không.
     */
    public boolean hasItem(Item item, int amount) {
        return items.getOrDefault(item, 0) >= amount;
    }

    /**
     * Lấy số lượng của một vật phẩm cụ thể.
     */
    public int getItemCount(Item item) {
        return items.getOrDefault(item, 0);
    }

    /**
     * Lấy toàn bộ danh sách vật phẩm dưới dạng Read-only để đảm bảo tính đóng gói.
     * Thích hợp dùng cho UI.
     */
    public Map<Item, Integer> getReadOnlyItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Làm trống túi đồ.
     */
    public void clear() {
        items.clear();
    }
}
