package events;

/**
 * Quản lý hệ thống phát và nhận sự kiện trung tâm.
 */
public class EventDispatcher {

    /**
     * Lấy thể hiện duy nhất của EventDispatcher theo mẫu Singleton[cite: 11].
     * * @return Thể hiện tĩnh của EventDispatcher[cite: 11].
     */
    public static EventDispatcher getInstance() {
        // ...
    }

    /**
     * Đăng ký một đối tượng lắng nghe (listener) cho một loại sự kiện cụ thể[cite: 11].
     * Hệ thống sử dụng danh sách chờ (queueActions) và cờ isDispatching để đảm bảo an toàn, tránh lỗi thay đổi danh sách khi quá trình phát sự kiện đang diễn ra.
     *
     * @param type Loại sự kiện cần lắng nghe, định nghĩa trong EventType[cite: 11].
     * @param listener Đối tượng thực thi interface EventListener[cite: 11].
     */
    public void addListener(EventType type, EventListener listener) {
        // ...
    }

    /**
     * Hủy đăng ký lắng nghe sự kiện của một đối tượng[cite: 11].
     * Nếu isDispatching là true, thao tác xóa được đưa vào queueActions để trì hoãn đến khi vòng lặp phát sự kiện kết thúc.
     *
     * @param type Loại sự kiện muốn ngừng lắng nghe[cite: 11].
     * @param listener Đối tượng EventListener cần gỡ bỏ[cite: 11].
     */
    public void removeListener(EventType type, EventListener listener) {
        // ...
    }

    /**
     * Phát một sự kiện tới tất cả các listener đã đăng ký tương ứng với loại sự kiện đó[cite: 11].
     *
     * @param event Đối tượng GameEvent chứa phân loại và dữ liệu truyền tải[cite: 11].
     */
    public void dispatch(GameEvent<?> event) {
        // ...
    }
}

/**
 * Giao diện chuẩn hóa chữ ký hàm cho các đối tượng nhận sự kiện.
 */
public interface EventListener {

    /**
     * Xử lý logic nội bộ khi bắt được sự kiện từ EventDispatcher[cite: 12].
     *
     * @param event Đối tượng GameEvent được truyền tới[cite: 12].
     */
    void onEvent(GameEvent event);
}

/**
 * Đóng gói định nghĩa và dữ liệu truyền tải giữa các module thông qua hệ thống sự kiện.
 *
 * @param <T> Kiểu dữ liệu linh hoạt mang theo sự kiện[cite: 12].
 */
public class GameEvent<T> {

    /**
     * Khởi tạo một đối tượng GameEvent[cite: 12].
     *
     * @param type Loại sự kiện[cite: 12].
     * @param data Dữ liệu đính kèm, có thể là tham chiếu Entity, dữ liệu UI, hoặc null[cite: 12].
     */
    public GameEvent(EventType type, T data) {
        // ...
    }

    /**
     * Truy xuất loại sự kiện[cite: 12].
     *
     * @return Giá trị EventType[cite: 12].
     */
    public EventType getType() {
        // ...
    }

    /**
     * Truy xuất dữ liệu mang theo[cite: 12].
     *
     * @return Dữ liệu kiểu T[cite: 12].
     */
    public T getData() {
        // ...
    }
}