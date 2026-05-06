package ui;

/**
 * Quản lý toàn bộ hệ thống giao diện người dùng (Heads-Up Display) trên màn hình.
 * Sử dụng các thành phần của thư viện Scene2D.ui (Stage, Table, ScrollPane) để khởi tạo, hiển thị và xử lý đầu vào của giao diện.
 */
public class HUDManager {

    /**
     * Khởi tạo trình quản lý giao diện và thiết lập liên kết dữ liệu.
     *
     * @param batch Đối tượng vẽ đồ họa được tái sử dụng.
     * @param player Tham chiếu đến người chơi để trích xuất dữ liệu túi đồ (Inventory).
     * @param dispatcher Hệ thống quản lý sự kiện để đăng ký lắng nghe và phát tín hiệu.
     */
    public HUDManager(SpriteBatch batch, Player player, EventDispatcher dispatcher) {
        // ...
    }

    /**
     * Cập nhật logic của hệ thống giao diện.
     * Vòng lặp chính gọi hàm này để cập nhật logic của giao diện thông qua Stage.act.
     *
     * @param delta Thời gian trôi qua giữa hai khung hình.
     */
    public void update(float delta) {
        // ...
    }

    /**
     * Kết xuất đồ họa giao diện lên màn hình.
     * Gọi Stage.draw() để vẽ giao diện UI đè lên lớp đồ họa của game. Hàm này vẫn được gọi ngay cả khi GameState là UI_PAUSED.
     */
    public void render() {
        // ...
    }

    /**
     * Đảo ngược trạng thái hiển thị của giao diện túi đồ.
     * Đảo ngược trạng thái của cờ isUIOpen. Nếu mở UI, tạo đối tượng sự kiện và gọi eventDispatcher.dispatch() với EventType.INVENTORY_OPENED. Khi đóng UI, phát ra sự kiện EventType.INVENTORY_CLOSED.
     */
    public void toggleInventory() {
        // ...
    }

    /**
     * Cung cấp tham chiếu gốc tới sân khấu giao diện (Stage).
     * PlayScreen gọi hàm này để đăng ký Stage vào InputMultiplexer, giúp Scene2D.ui nhận cờ ưu tiên hệ thống đầu vào.
     *
     * @return Đối tượng Stage quản lý các UI Actor.
     */
    public Stage getStage() {
        // ...
    }

    /**
     * Đăng ký các hàm callback để lắng nghe sự kiện từ hệ thống trung tâm.
     * Lắng nghe sự kiện thay đổi dữ liệu từ túi đồ để kích hoạt lớp InventoryUITable tiến hành vẽ lại (rebuild) cấu trúc hiển thị.
     */
    private void registerEvents() {
        // ...
    }
}

/**
 * Thành phần giao diện chịu trách nhiệm định dạng và hiển thị trực quan các vật phẩm trong túi đồ.
 */
public class InventoryUITable {

    /**
     * Khởi tạo bảng giao diện và gán tham chiếu dữ liệu.
     *
     * @param inv Đối tượng dữ liệu túi đồ cần hiển thị.
     */
    public InventoryUITable(Inventory inv) {
        // ...
    }

    /**
     * Cập nhật lại toàn bộ cấu trúc nút bấm và icon trên bảng UI.
     * Quét lại mảng cấu trúc dữ liệu bên trong Inventory và tạo mới các thành phần đồ họa để phản ánh chính xác trạng thái vật phẩm hiện tại.
     */
    public void rebuildUI() {
        // ...
    }
}