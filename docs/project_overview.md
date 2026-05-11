# Tổng quan Kỹ thuật & Chức năng HustGame

Mô tả kỹ thuật, kiến trúc, chức năng dự án **HustGame**.

## 1. Kiến trúc Hệ thống (Architectural Overview)

Nền tảng **LibGDX** (Java). Thiết kế hiện đại, dễ mở rộng.

*   **Singleton Pattern**: Dùng cho `EventDispatcher`, `ItemManager`.
*   **Factory Pattern**: `EntityFactory` quản lý khởi tạo thực thể, `LevelFactory` tạo màn chơi.
*   **Object Pool Pattern**: Sử dụng `com.badlogic.gdx.utils.Pool` trong `EntityFactoryImpl` (cho Projectile) và `CollisionManager` (cho nội bộ grid) để tối ưu bộ nhớ.
*   **Screen-based Architecture**: Quản lý vòng đời qua `LoadingScreen` và `BaseLevelScreen` (với các lớp con như `Floor1Level`, `LibraryLevel`, `BossFightLevel`).
*   **Event-Driven Design**: Giảm phụ thuộc qua `EventDispatcher`.
*   **State Management**: `GameState` và `PlayMode` quản lý luồng game (RUNNING, PAUSED, IN_UI).

---

## 2. Các Phân hệ Cốt lõi (Core Systems)

### 2.1 Quản lý Tài nguyên (Asset Management)
*   **Lớp**: `GameAssetManager`
*   **Chức năng**: 
    *   Tải/giải phóng Textures, Atlas, TiledMaps, Sounds, Fonts.
    *   Tải không đồng bộ, tránh lag.

### 2.2 Hệ thống Thực thể (Entity System)
*   **Lớp**: `EntityManager`, `BaseEntity`, `Player`, `EntityFactory`
*   **Kỹ thuật**: 
    *   **Behavior-based Components**: Logic tách thành `PlayerMovementBehavior`, `WanderMovementBehavior`, `ChaseBehavior`, `FleeBehavior`.
    *   **Y-Sorting**: Tự động sắp xếp thứ tự vẽ dựa trên tọa độ Y để tạo chiều sâu cho góc nhìn Top-down.
    *   **Factory Pattern**: Sử dụng `EntityFactoryImpl` để tập trung logic tạo quái vật, NPC và vật thể.
    *   **Object Pooling**: Áp dụng cho các thực thể có vòng đời ngắn (đạn, hiệu ứng) để tránh giật lag do Garbage Collection.

### 2.3 Hệ thống Va chạm (Collision System)
*   **Lớp**: `CollisionManager`, `Collider`
*   **Kỹ thuật**: 
    *   Kiểm tra va chạm AABB (Axis-Aligned Bounding Box).
    *   **Spatial Hashing**: Chia thế giới thành các ô lưới (grid) để giới hạn số lượng kiểm tra va chạm, tăng hiệu năng đáng kể.
    *   Lớp va chạm (`CollisionLayer`): Tường, Vật thể, Thực thể, Vùng chuyển map.
    *   **Tiled Map Integration**: Tự động tạo vật thể va chạm từ layer "Object Layer 1" trong file .tmx.

### 2.4 Hệ thống Sự kiện (Event System)
*   **Lớp**: `EventDispatcher`, `GameEvent`, `EventType`
*   **Chức năng**: 
    *   Đăng ký/thông báo sự kiện (Singleton).
    *   Truyền tin giữa các hệ thống qua `GameEvent` (Sự kiện nhặt đồ `ItemPickedUpEvent`, dữ liệu chuyển map `MapTransitionData`).

### 2.5 Hệ thống Thế giới (World System)
*   **Lớp**: `WorldManager`, `Portal`
*   **Chức năng**: 
    *   Quản lý bản đồ, tải từ TiledMap.
    *   Khởi tạo `WallEntity`, `Portal`, vật thể môi trường.

### 2.6 Hệ thống Nhập liệu (Input Handling)
*   **Lớp**: `InputReader`, `PlayerController`
*   **Chức năng**: Tách logic phím/chuột khỏi logic game.

### 2.7 Hệ thống Vật phẩm (Item System)
*   **Lớp**: `ItemManager`, `BaseItem`, `Consumable`
*   **Chức năng**: Quản lý vòng đời/tác dụng vật phẩm (hồi máu, tăng tốc).

### 2.8 Hệ thống Đồ họa & Ánh sáng (Graphics & Lighting)
*   **Lớp**: `LightingManager`, `CameraManager`, `GameRenderer`, `RepeatingImageTiledMapRenderer`
*   **Kỹ thuật**: 
    *   **Lighting System**: Ánh sáng động (Ambient, point lights qua `LightProvider`).
    *   **Camera Management**: Camera mượt, zoom, follow nhân vật thông qua `CameraManager`.
    *   **Custom Map Rendering**: Vẽ Tiled với `RepeatingImageTiledMapRenderer` hỗ trợ các họa tiết lặp lại.

### 2.9 Hệ thống Giao diện (UI System)
*   **Lớp**: `UIManager`, `HUD`, `InventoryUI`, `BookPuzzle`
*   **Chức năng**: 
    *   Quản lý các thành phần UI (Nút, thanh máu, đối thoại).
    *   Tích hợp mini-games trực tiếp vào lớp UI (`BookPuzzle`).
    *   Hiển thị thông tin vật phẩm và túi đồ.

---

## 3. Chức năng & Đặc điểm Nổi bật (Key Features)

### 3.1 Quản lý Cấp độ & Bản đồ
*   **Tiled Map Integration**: Dùng Tiled (.tmx).
    *   **Lớp Đối tượng (`Object Layer 1`)**: Tạo `WallEntity` qua `WorldManager`.
    *   **Lớp Cổng (`Portals`)**: Vùng chuyển map, `target`, `spawnX`, `spawnY`.
    *   **Lớp Ánh sáng (`LightingObjects`)**: Sinh `Candle`, `FloatingBook` tích hợp ánh sáng.
*   **Map Transition**: Chuyển map mượt qua `ScreenTransition`.

### 3.2 AI Đơn giản
*   Enemies/NPCs dùng Behaviors: `Wander`, `Chase` (Vector-based), `Flee`.

### 3.3 Hệ thống Túi đồ (Inventory)
*   **Kỹ thuật**: **Flyweight Pattern** tối ưu bộ nhớ. 
*   **Đặc điểm**: Không giới hạn slot, không trọng lượng.

### 3.4 Giao diện (UI/UX)
*   **Vietnamese Support**: `FreeTypeFont` và `BitmapFont` hỗ trợ đầy đủ ký tự tiếng Việt.
*   **Dialog System**: Hệ thống hội thoại linh hoạt cho NPC.
*   **Mini-games/Puzzles**: Cơ chế giải đố đặc thù như **BookPuzzle** (Xếp sách đúng vị trí) được cài đặt như một UI Overlay.

### 3.5 Hiệu ứng trạng thái (Status Effects)
*   **Lớp**: `StatusEffectManager` quản lý thay đổi tốc độ, sức mạnh theo thời gian.

---

## 4. Hướng dẫn Phát triển & Mở rộng (Development Guide)

Bảng kiểm tra đánh giá tính năng mới:

### 5.1 Bảng Kiểm tra Tính năng (Feature Checklist)

| Loại tính năng mới | Hỗ trợ? | Cần bổ sung? |
| :--- | :--- | :--- |
| **Thực thể mới** | **Có** | Kế thừa `BaseEntity`, gán Behavior, đăng ký trong `EntityFactoryImpl`. |
| **Vật phẩm mới** | **Có** | Kế thừa `BaseItem`/`Consumable`, đăng ký trong `ItemManager`. |
| **Màn chơi mới** | **Có** | File `.tmx`, đăng ký `LevelID`, tạo class trong `screens.levels`. |
| **Hiệu ứng hình ảnh** | **Có** | Shader/Particle System hoặc `StatusEffect`. |
| **Cơ chế chiến đấu** | **Sơ khai** | Dùng `Projectile` và `StatusEffect`. |
| **Hệ thống Nhiệm vụ** | **Chưa có** | Xây dựng `QuestManager`, tích hợp `EventDispatcher`. |

### 5.2 Quy trình Đánh giá (Evaluation Workflow)

1.  **Xác định Input/Output**: Dữ liệu từ đâu, tác động lên đâu?
2.  **Kiểm tra Sự kiện**: Dùng `EventDispatcher` để tránh Tight Coupling.
3.  **Kiểm tra Thành phần**: Tái sử dụng Behaviors (`Chase`, `Wander`)?
4.  **Quyết định**: 
    *   Thay đổi đơn giản -> Dùng hệ thống hiện có.
    *   Logic mới (Save game, Multiplayer) -> Xây dựng **Manager** mới trong `core/`.

---

## 6. Công nghệ Sử dụng (Tech Stack)

*   **Ngôn ngữ**: Java 17+
*   **Framework**: LibGDX
*   **Build Tool**: Gradle
*   **Design Tools**: Tiled Map Editor, TexturePacker
*   **Phông chữ**: Font Unicode (`assets/ui/font.ttf`).

---
*Tài liệu cập nhật tự động theo cấu trúc dự án.*
