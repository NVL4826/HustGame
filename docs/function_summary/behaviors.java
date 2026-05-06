package behaviors;

/**
 * Giao diện gốc định nghĩa các khối logic đóng gói có thể tái sử dụng.
 */
public interface IBehavior {

    /**
     * Cập nhật trạng thái hoặc logic chung của thực thể trong vòng lặp game.
     *
     * @param entity Thực thể chịu sự tác động của hành vi.
     * @param delta Thời gian trôi qua giữa hai khung hình.
     */
    void update(Entity entity, float delta);
}

/**
 * Giao diện mở rộng IBehavior, quy định ranh giới và chuẩn hóa cho các logic di chuyển.
 * Đóng gói việc quản lý tọa độ và vận tốc của đối tượng Actor.
 */
public interface IMovementBehavior extends IBehavior {

    /**
     * Tính toán và điều chỉnh vận tốc di chuyển cho thực thể mục tiêu.
     *
     * @param actor Thực thể có khả năng di chuyển.
     * @param delta Thời gian trôi qua giữa hai khung hình.
     */
    void move(Actor actor, float delta);
}

/**
 * Triển khai logic di chuyển dành riêng cho người chơi, xử lý tín hiệu điều khiển đầu vào.
 */
public class PlayerMovementBehavior implements IMovementBehavior {

    /**
     * Khởi tạo hành vi di chuyển cho Player.
     * Nhận tham chiếu IPlayerController để dịch chuyển theo phím bấm.
     *
     * @param controller Đối tượng quản lý dữ liệu phím bấm đầu vào.
     */
    public PlayerMovementBehavior(IPlayerController controller) {
        // ...
    }

    /**
     * Tính toán vận tốc dựa trên các phím điều hướng đang được nhấn.
     * Đọc các cờ trạng thái thông qua IPlayerController, tính toán hướng đi, và gọi hàm Setter để cập nhật vận tốc hợp lệ cho Actor.
     *
     * @param actor Thực thể Player cần áp dụng di chuyển.
     * @param delta Thời gian trôi qua giữa hai khung hình.
     */
    @Override
    public void move(Actor actor, float delta) {
        // ...
    }
}

/**
 * Triển khai thuật toán AI tự động đi tuần tra vô định dựa trên bán kính giới hạn.
 */
public class WanderMovementBehavior implements IMovementBehavior {

    /**
     * Khởi tạo hành vi đi tuần tra AI.
     *
     * @param radius Bán kính di chuyển tối đa so với tâm điểm ngẫu nhiên.
     */
    public WanderMovementBehavior(float radius) {
        // ...
    }

    /**
     * Tính toán tọa độ đích ngẫu nhiên theo bộ định thời và điều hướng Actor.
     * Kiểm tra bộ đếm thời gian (timer); nếu hết, tính toán tọa độ đích ngẫu nhiên mới trong phạm vi wanderRadius xung quanh vị trí hiện tại và gọi các hàm Setter để cập nhật vận tốc hướng về tọa độ đó. Nếu timer chưa hết, duy trì vận tốc hiện tại.
     *
     * @param actor Thực thể NPC được áp dụng AI tuần tra.
     * @param delta Thời gian trôi qua giữa hai khung hình.
     */
    @Override
    public void move(Actor actor, float delta) {
        // ...
    }
}