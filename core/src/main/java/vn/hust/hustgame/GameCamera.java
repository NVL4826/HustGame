package vn.hust.hustgame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class GameCamera {
    private OrthographicCamera camera;
    private ITargetable target; // Mục tiêu bám theo (Player)

    // Các thông số khung hình và bản đồ
    private float viewWidth;
    private float viewHeight;
    private float mapWidth;
    private float mapHeight;

    public GameCamera(float viewWidth, float viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        camera = new OrthographicCamera();
        // Thiết lập camera theo kích thước màn hình
        camera.setToOrtho(false, viewWidth, viewHeight);
    }

    public void setTarget(ITargetable target) {
        this.target = target;
    }

    // Cập nhật kích thước bản đồ mỗi khi qua màn mới
    public void setMapBounds(float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setZoom(float zoom) {
        camera.zoom = zoom;
    }

    // Hàm này sẽ tự động tính toán vị trí để không bị lẹm ra ngoài map
    public void update() {
        if (target != null) {
            float halfW = viewWidth * camera.zoom / 2f;
            float halfH = viewHeight * camera.zoom / 2f;

            float camX = MathUtils.clamp(target.getX(), halfW, mapWidth - halfW);
            float camY = MathUtils.clamp(target.getY(), halfH, mapHeight - halfH);

            camera.position.set(camX, camY, 0);
        }
        camera.update();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
