package vn.hust.hustgame.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp quản lý túi đồ của người chơi.
 * Lưu trữ danh sách các vật phẩm và số lượng tương ứng.
 */
public class Inventory {

    // Sử dụng HashMap để lưu trữ id vật phẩm (String) và số lượng (Integer)
    private Map<String, Integer> items;

    public Inventory() {
        items = new HashMap<>();
    }

    /**
     * Thêm vật phẩm vào túi đồ.
     *
     * @param itemId Mã định danh vật phẩm (ví dụ: "coffee", "usb")
     * @param amount Số lượng muốn thêm
     */
    public void addItem(String itemId, int amount) {
        if (amount <= 0)
            return;
        items.put(itemId, items.getOrDefault(itemId, 0) + amount);
    }

    /**
     * Xóa vật phẩm khỏi túi đồ.
     *
     * @param itemId Mã định danh vật phẩm
     * @param amount Số lượng muốn xóa
     * @return true nếu xóa thành công (đủ số lượng), false nếu không đủ.
     */
    public boolean removeItem(String itemId, int amount) {
        if (amount <= 0)
            return false;

        int currentAmount = items.getOrDefault(itemId, 0);
        if (currentAmount >= amount) {
            int newAmount = currentAmount - amount;
            if (newAmount == 0) {
                items.remove(itemId);
            } else {
                items.put(itemId, newAmount);
            }
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra xem túi đồ có chứa một lượng vật phẩm nhất định hay không.
     *
     * @param itemId Mã định danh vật phẩm
     * @param amount Số lượng cần kiểm tra
     * @return true nếu đủ số lượng.
     */
    public boolean hasItem(String itemId, int amount) {
        return items.getOrDefault(itemId, 0) >= amount;
    }

    /**
     * Lấy số lượng của một vật phẩm cụ thể.
     */
    public int getItemCount(String itemId) {
        return items.getOrDefault(itemId, 0);
    }

    /**
     * Lấy toàn bộ danh sách vật phẩm.
     */
    public Map<String, Integer> getAllItems() {
        return items;
    }

    /**
     * Làm trống túi đồ.
     */
    public void clear() {
        items.clear();
    }
}
