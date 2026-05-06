package input;

/**
 * Giao diện trừu tượng hóa luồng dữ liệu đầu vào.
 * Tránh phụ thuộc cứng vào LibGDX InputProcessor.
 */
public interface IPlayerController {

    /**
     * Truy xuất trạng thái lệnh di chuyển lên.
     * Lớp PlayerMovementBehavior đọc cờ thông qua hàm này để tính toán hướng đi.
     *
     * @return true nếu phím điều hướng lên đang được giữ, ngược lại là false.
     */
    boolean isUp();

    /**
     * Truy xuất trạng thái lệnh di chuyển xuống.
     *
     * @return true nếu phím điều hướng xuống đang được giữ, ngược lại là false.
     */
    boolean isDown();

    /**
     * Truy xuất trạng thái lệnh di chuyển sang trái.
     *
     * @return true nếu phím điều hướng trái đang được giữ, ngược lại là false.
     */
    boolean isLeft();

    /**
     * Truy xuất trạng thái lệnh di chuyển sang phải.
     *
     * @return true nếu phím điều hướng phải đang được giữ, ngược lại là false.
     */
    boolean isRight();

    /**
     * Truy xuất trạng thái lệnh kích hoạt hành động.
     *
     * @return true nếu phím tương tác đang được nhấn, ngược lại là false.
     */
    boolean isAction();
}

/**
 * Lớp xử lý sự kiện đầu vào cốt lõi.
 * Triển khai giao diện IPlayerController và kết nối với hệ thống đầu vào của LibGDX thông qua InputProcessor. Gán cố định (hardcode) các phím điều hướng để chuyển đổi thành các cờ logic.
 */
public class GameInputHandler implements IPlayerController {

    /**
     * Ghi nhận sự kiện nhấn phím vật lý.
     * LibGDX engine phát hiện sự kiện nhấn phím và gọi hàm này. Hàm ghi nhận và thay đổi cờ trạng thái đầu vào tương ứng thành true.
     *
     * @param keycode Mã định danh phím (từ LibGDX Keys).
     * @return true nếu sự kiện đã được tiêu thụ.
     */
    public boolean keyDown(int keycode) {
        // ...
    }

    /**
     * Ghi nhận sự kiện nhả phím vật lý.
     * Đặt lại cờ logic nội bộ tương ứng thành false, báo hiệu kết thúc thao tác giữ phím.
     *
     * @param keycode Mã định danh phím (từ LibGDX Keys).
     * @return true nếu sự kiện đã được tiêu thụ.
     */
    public boolean keyUp(int keycode) {
        // ...
    }

    /**
     * Trả về trạng thái cờ upPressed nội bộ.
     *
     * @return Giá trị boolean của cờ.
     */
    @Override
    public boolean isUp() {
        // ...
    }

    /**
     * Trả về trạng thái cờ downPressed nội bộ.
     *
     * @return Giá trị boolean của cờ.
     */
    @Override
    public boolean isDown() {
        // ...
    }

    /**
     * Trả về trạng thái cờ leftPressed nội bộ.
     *
     * @return Giá trị boolean của cờ.
     */
    @Override
    public boolean isLeft() {
        // ...
    }

    /**
     * Trả về trạng thái cờ rightPressed nội bộ.
     *
     * @return Giá trị boolean của cờ.
     */
    @Override
    public boolean isRight() {
        // ...
    }

    /**
     * Trả về trạng thái cờ actionPressed nội bộ.
     * Khi cờ này mang giá trị true, PlayScreen sẽ đọc trạng thái và gọi EntityManager.getNearestInteractable(player) để tìm và kích hoạt thực thể gần nhất.
     *
     * @return Giá trị boolean của cờ.
     */
    @Override
    public boolean isAction() {
        // ...
    }
}