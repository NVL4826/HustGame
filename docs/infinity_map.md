Thuật toán triển khai "Bản đồ vô tận" (Infinite/Treadmill Map) cho Vampire Survivors dựa trên **Spatial Repositioning** hoặc **Chunk-based Rendering**.

Tránh tạo bản đồ vô hạn gây tràn RAM. Chỉ vẽ Chunks quanh Camera. Chunks khuất tầm nhìn được tái sử dụng vẽ vị trí mới.

Chi tiết thuật toán, thiết kế OOP.

---

### 1. Phân tích Thuật toán (Core Algorithm)

Tính toán dựa trên World Coordinates và Chunk Size.

**Đầu vào:**
*   `cameraX`, `cameraY`: Tâm Camera.
*   `viewportWidth`, `viewportHeight`: Kích thước Viewport.
*   `chunkWidth`, `chunkHeight`: Kích thước background chunk (Ví dụ: 512x512).

**Các bước thực thi (mỗi frame):**
1.  **Tính ô trung tâm (Center Chunk):** Camera ở cột/hàng nào.
    *   `currentCol = floor(cameraX / chunkWidth)`
    *   `currentRow = floor(cameraY / chunkHeight)`

2.  **Xác định vùng vẽ (Visible Range):** Số khối lấp đầy màn hình + 1 khối đệm.
    *   `chunksX = ceil(viewportWidth / chunkWidth) + 1`
    *   `chunksY = ceil(viewportHeight / chunkHeight) + 1`

3.  **Vòng lặp Render (Treadmill Loop):** Lặp cột/hàng vùng nhìn thấy.
    *   `startX = currentCol - (chunksX / 2)`
    *   `endX = currentCol + (chunksX / 2)`
    *   `startY = currentRow - (chunksY / 2)`
    *   `endY = currentRow + (chunksY / 2)`

    Mỗi vòng lặp `(x, y)`:
    *   `worldPosX = x * chunkWidth`
    *   `worldPosY = y * chunkHeight`
    *   Vẽ texture tại `(worldPosX, worldPosY)`.

---

### 2. Thiết kế Cấu trúc OOP

Linh hoạt, tái sử dụng.

#### 2.1. Giao diện (Interfaces)
*   **`Renderable`**: `void render(SpriteBatch batch, float delta);`
*   **`Updateable`**: `void update(float delta);`

#### 2.2. Các lớp thực thi (Classes)

*   **`MapChunk`** (Data Class)
    *   Lưu `TextureRegion`, `isPassable`.

*   **`InfiniteMapRenderer`** (`Renderable`, `Updateable`)
    *   Quản lý tọa độ, vòng lặp vẽ. Tách biệt logic Player/Entity.
    *   Attributes: `CameraManager`, `MapChunk`, `chunkWidth`, `chunkHeight`. Inject dependencies qua Constructor.

---

### 3. Triển khai Mã nguồn (Java / LibGDX)

Mã giả lập cấu trúc LibGDX.

```java
public class InfiniteMapRenderer implements Renderable, Updateable {

    private final CameraManager cameraManager;
    private final MapChunk baseChunk;
    
    private final float chunkWidth;
    private final float chunkHeight;
    
    private int visibleChunksX;
    private int visibleChunksY;

    public InfiniteMapRenderer(CameraManager cameraManager, MapChunk baseChunk) {
        this.cameraManager = cameraManager;
        this.baseChunk = baseChunk;
        
        // Get actual dimensions of the texture
        this.chunkWidth = baseChunk.getTexture().getRegionWidth();
        this.chunkHeight = baseChunk.getTexture().getRegionHeight();
        
        calculateVisibleChunks();
    }

    private void calculateVisibleChunks() {
        OrthographicCamera camera = cameraManager.getCamera();
        // Calculate the number of chunks required to fill the Viewport.
        // Add 2 as a buffer on both sides (left/right, top/bottom) to prevent visual tearing.
        this.visibleChunksX = (int) Math.ceil(camera.viewportWidth / chunkWidth) + 2;
        this.visibleChunksY = (int) Math.ceil(camera.viewportHeight / chunkHeight) + 2;
    }

    @Override
    public void update(float delta) {
        // If the game has a camera Zoom mechanism (which changes the viewport),
        // calculateVisibleChunks() should be called here.
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        OrthographicCamera camera = cameraManager.getCamera();
        
        float camX = camera.position.x;
        float camY = camera.position.y;

        // 1. Calculate the index of the center cell (the cell where the camera is located)
        int centerCol = MathUtils.floor(camX / chunkWidth);
        int centerRow = MathUtils.floor(camY / chunkHeight);

        // 2. Determine the iteration range
        int startCol = centerCol - (visibleChunksX / 2);
        int endCol = centerCol + (visibleChunksX / 2);
        
        int startRow = centerRow - (visibleChunksY / 2);
        int endRow = centerRow + (visibleChunksY / 2);

        // 3. Rendering loop (Treadmill approach)
        for (int col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {
                
                // Convert from Grid coordinates to World coordinates
                float drawX = col * chunkWidth;
                float drawY = row * chunkHeight;
                
                // Execute draw command
                batch.draw(baseChunk.getTexture(), drawX, drawY, chunkWidth, chunkHeight);
            }
        }
    }
}
```

### 4. Tích hợp vào Hệ thống hiện tại (WorldManager)

Sửa `WorldManager` thay thế/bổ sung `RepeatingImageTiledMapRenderer`.

```java
public class WorldManager {
    private InfiniteMapRenderer mapRenderer;

    public void initWorld(GameAssetManager assetManager, CameraManager cameraManager) {
        // Load texture from AssetManager
        TextureRegion grassTexture = assetManager.getTextureRegion("grass_tile");
        MapChunk grassChunk = new MapChunk(grassTexture);
        
        // Initialize the infinite map system
        this.mapRenderer = new InfiniteMapRenderer(cameraManager, grassChunk);
    }

    public void renderBackground(SpriteBatch batch, float delta) {
        mapRenderer.render(batch, delta);
    }
}
```