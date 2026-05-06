package screens;

/**
 * Lớp cơ sở trừu tượng cho tất cả các màn hình trong game.
 * Sử dụng BaseScreen làm lớp cha abstract để chia sẻ tham chiếu đến đối tượng HustGame cho các màn hình con.
 */
public abstract class BaseScreen implements Screen {

    protected HustGame game;

    /**
     * Khởi tạo màn hình cơ sở và lưu trữ tham chiếu đến hệ thống trung tâm.
     *
     * @param game Đối tượng HustGame đóng vai trò gốc quản lý vòng đời.
     */
    public BaseScreen(HustGame game) {
        // ...
    }

    /**
     * Thực thi logic khởi tạo giao diện khi đối tượng được gán làm màn hình hiện hành.
     */
    public void show() {
        // ...
    }

    /**
     * Vòng lặp liên tục cập nhật logic và kết xuất đồ họa.
     *
     * @param delta Thời gian trôi qua kể từ khung hình trước đó (giây).
     */
    public void render(float delta) {
        // ...
    }

    /**
     * Thực thi logic ẩn giao diện khi màn hình bị thay thế.
     */
    public void hide() {
        // ...
    }

    /**
     * Gỡ bỏ các đối tượng phân bổ và giải phóng bộ nhớ khi màn hình bị hủy.
     */
    public void dispose() {
        // ...
    }
}

/**
 * Quản lý quá trình nạp tài nguyên. 
 * Khi điều kiện nạp hoàn tất, chuyển quyền điều khiển sang đối tượng Screen tiếp theo được chỉ định.
 */
public class LoadingScreen extends BaseScreen {

    /**
     * Khởi tạo màn hình chờ tải tài nguyên.
     *
     * @param game Tham chiếu đến HustGame.
     */
    public LoadingScreen(HustGame game) {
        // ...
    }

    /**
     * Cập nhật tiến trình nạp tài nguyên vào RAM và điều hướng luồng.
     * Gọi GameAssetManager.update() để tiến hành nạp bất đồng bộ tài nguyên. Khi phương thức này trả về kết quả true (tài nguyên đã nạp xong), khởi tạo đối tượng PlayScreen và gọi HustGame.setScreen() để chuyển quyền điều khiển.
     *
     * @param delta Thời gian trôi qua kể từ khung hình trước đó (giây).
     */
    @Override
    public void render(float delta) {
        // ...
    }

    /**
     * Vẽ thanh tiến trình nạp.
     * Truy xuất tiến độ qua GameAssetManager.getProgress() để vẽ thanh tải.
     */
    private void drawProgressBar() {
        // ...
    }
}

/**
 * Màn hình chính của trò chơi, quản lý biến GameState để điều tiết việc chạy hay dừng của logic game.
 */
public class PlayScreen extends BaseScreen {

    private GameState state;

    /**
     * Trích xuất tài nguyên và khởi tạo toàn bộ phân hệ vận hành trò chơi.
     * Trích xuất TiledMap từ AssetManager để truyền vào MapManager và khởi tạo toàn bộ các phân hệ (EntityManager, GameRenderer, HUDManager).
     *
     * @param game Tham chiếu đến HustGame.
     */
    public PlayScreen(HustGame game) {
        // ...
    }

    /**
     * Vòng lặp cập nhật cốt lõi của trò chơi dựa trên trạng thái FSM.
     * Nếu trạng thái GameState == RUNNING, gọi EntityManager.update(delta) để cập nhật logic cho toàn bộ thực thể. Nếu trạng thái là UI_PAUSED, bỏ qua lệnh gọi cập nhật khiến toàn bộ quái vật và người chơi lập tức đứng im. Các lệnh gọi GameRenderer.render() và HUDManager.render() luôn được thực thi để duy trì kết xuất hình ảnh tĩnh.
     *
     * @param delta Thời gian trôi qua kể từ khung hình trước đó (giây).
     */
    @Override
    public void render(float delta) {
        // ...
    }
}

/**
 * Liệt kê các trạng thái quyết định luồng thực thi của vòng lặp PlayScreen.
 */
public enum GameState {
    /**
     * Vòng lặp vật lý và logic đang hoạt động.
     */
    RUNNING,

    /**
     * UI đang mở, vòng lặp logic tạm dừng nhưng vẫn vẽ đồ họa.
     */
    UI_PAUSED
}