package inventory;

/**
 * Lớp chứa dữ liệu cốt lõi của túi đồ, lưu trữ mảng các ItemStack.
 * Đóng vai trò là trung tâm dữ liệu độc lập; khi có thay đổi nội bộ, đối tượng này phát sự kiện qua EventDispatcher để hệ thống giao diện tự động cập nhật.
 */
public class Inventory {

    /**
     * Khởi tạo túi đồ và liên kết với hệ thống quản lý sự kiện.
     *
     * @param dispatcher Hệ thống EventDispatcher dùng để phát tín hiệu khi túi đồ thay đổi.
     */
    public Inventory(EventDispatcher dispatcher) {
        // ...
    }

    /**
     * Thêm một số lượng vật phẩm nhất định vào túi đồ dựa trên mã định danh.
     * Lớp Item lấy tham chiếu túi đồ và gọi hàm này khi người chơi nhặt vật phẩm. Lớp Inventory chịu trách nhiệm cập nhật dữ liệu mảng và phát sự kiện cập nhật UI ngay trong hàm này.
     *
     * @param id Mã định danh của vật phẩm (định nghĩa trong ItemDefinition).
     * @param amount Số lượng vật phẩm cần thêm.
     */
    public void addItem(String id, int amount) {
        // ...
    }

    /**
     * Xóa một số lượng vật phẩm nhất định khỏi túi đồ.
     * Thực thi kiểm tra số lượng hiện có, giảm trừ và phát sự kiện cập nhật UI. Nếu số lượng bằng 0, gỡ bỏ hoàn toàn ItemStack khỏi mảng.
     *
     * @param id Mã định danh của vật phẩm cần xóa.
     * @param amount Số lượng muốn xóa.
     * @return true nếu xóa thành công (đủ số lượng), false nếu không đủ vật phẩm.
     */
    public boolean removeItem(String id, int amount) {
        // ...
    }

    /**
     * Lấy danh sách các ngăn chứa vật phẩm hiện có trong túi.
     * Lớp InventoryUITable lấy dữ liệu thông qua hàm này để tiến hành vẽ lại (rebuild) cấu trúc hiển thị giao diện.
     *
     * @return Mảng các đối tượng ItemStack.
     */
    public Array<ItemStack> getItems() {
        // ...
    }
}

/**
 * Lớp đại diện cho một khe/ngăn chứa vật phẩm cụ thể trong túi đồ.
 * Mỗi ItemStack chứa tham chiếu tới cấu hình gốc ItemDefinition và số lượng hiện tại.
 */
public class ItemStack {

    /**
     * Khởi tạo một khe chứa vật phẩm với cấu hình và số lượng ban đầu.
     *
     * @param def Tham chiếu đến đối tượng cấu hình tĩnh ItemDefinition.
     * @param amount Số lượng khởi tạo.
     */
    public ItemStack(ItemDefinition def, int amount) {
        // ...
    }

    /**
     * Cộng dồn thêm số lượng vào khe chứa hiện tại.
     *
     * @param val Số lượng cần cộng thêm.
     */
    public void addAmount(int val) {
        // ...
    }

    /**
     * Trừ bớt số lượng khỏi khe chứa hiện tại.
     *
     * @param val Số lượng cần trừ đi.
     */
    public void removeAmount(int val) {
        // ...
    }

    /**
     * Lấy số lượng vật phẩm đang có trong khe chứa.
     *
     * @return Giá trị số lượng.
     */
    public int getAmount() {
        // ...
    }

    /**
     * Lấy thông tin cấu hình gốc của vật phẩm trong khe chứa.
     *
     * @return Đối tượng ItemDefinition.
     */
    public ItemDefinition getItemDef() {
        // ...
    }
}

/**
 * Liệt kê các danh mục phân loại vật phẩm trong game.
 */
public enum ItemCategory {
    RESOURCE,
    CONSUMABLE,
    GEAR,
    QUEST
}

/**
 * Lớp dữ liệu (Data Class) chứa thông tin cấu hình tĩnh của một loại vật phẩm.
 * Thông tin cấu hình vật phẩm được khởi tạo và lưu trữ trực tiếp trong mã nguồn. Không lưu trữ trạng thái động tại đây.
 */
public class ItemDefinition {
    
    public String id;
    public String name;
    public String iconPath;
    public ItemCategory category;
    public boolean stackable;

    // Các hàm khởi tạo hoặc getter/setter (nếu cần) tùy thuộc vào triển khai thực tế.
}