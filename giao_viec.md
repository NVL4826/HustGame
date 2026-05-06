# Bản giao việc

## Thành viên 1: Hạ tầng Core & Quản lý Luồng (Core, State, Asset & Global Input)

### **Ticket 1.1: Hệ thống Quản lý Sự kiện & Vòng đời gốc**

* **Thành phần cấu trúc:** `HustGame`, `EventDispatcher`, `GameEvent`, `EventListener`.
* **Giao thức:** Chuẩn hóa `EventListener` (mục 4 - `mo_ta.md`).
* **Luồng thực thi:** Vòng lặp chính và luồng kết xuất (mục 1 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Cài đặt luồng `render` gốc của engine, phân bổ logic xuống các màn hình.
  * Triển khai Event-Driven, xử lý hàng đợi sự kiện an toàn qua cờ `isDispatching`.
  * Quản lý vòng đời `dispose()` tại lớp gốc `HustGame` để giải phóng SpriteBatch và các tài nguyên toàn cục.

### **Ticket 1.2: FSM Màn hình, Nạp tài nguyên & Cấu hình Đầu vào**

* **Thành phần cấu trúc:** `BaseScreen`, `LoadingScreen`, `PlayScreen`, `GameAssetManager`, `InputMultiplexer`.
* **Máy trạng thái:** Quản lý luồng Game (`trang_thai.md`) - tập trung `LOADING_SCREEN`.
* **Luồng thực thi:** Nạp tài nguyên (mục 5 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Tải tài nguyên bất đồng bộ (Textures, Maps). Chuyển quyền điều khiển sang `PlayScreen` khi hoàn tất.
  * Khởi tạo `InputMultiplexer` tại `PlayScreen` và đăng ký làm `InputProcessor` chính của LibGDX.
  * Gọi `dispose()` tại `GameAssetManager` khi kết thúc game.

---

## Thành viên 2: Kết xuất Đồ họa & Môi trường (Rendering & Camera)

### **Ticket 2.1: Dữ liệu Bản đồ & Logic Camera**

* **Thành phần cấu trúc:** `MapManager`, `GameCamera`.
* **Giao thức:** Triển khai `ITargetable` (mục 4 - `mo_ta.md`).
* **Luồng thực thi:** Theo dõi mục tiêu và giới hạn biên (mục 7 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Trích xuất mảng va chạm tĩnh (Rectangle) từ `TiledMap`.
  * Tính toán `OrthographicCamera.position`, khóa cứng tại biên màn hình.
  * Triển khai `dispose()` trong `MapManager` để hủy `TiledMap`.

### **Ticket 2.2: Hệ thống Vẽ đồ họa (Rendering Engine)**

* **Thành phần cấu trúc:** `GameRenderer`.
* **Luồng thực thi:** Vẽ bản đồ phân lớp và thực thể (mục 6 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Quản lý `OrthogonalTiledMapRenderer`. Cài đặt thuật toán Y-Sorting phân loại độ sâu các `Entity` theo tọa độ Y.
  * Triển khai `dispose()` trong `GameRenderer` để hủy map renderer và các batch cục bộ.

---

## Thành viên 3: Thực thể, Vật lý & AI (Entities, Physics & Input Handler)

### **Ticket 3.1: FSM Thực thể & Vòng đời đối tượng**

* **Thành phần cấu trúc:** `Entity`, `Actor`, `EntityManager`, `Player`.
* **Máy trạng thái:** Trạng thái Thực thể (`trang_thai.md`) - `IDLE`, `MOVING`, `DEAD`.
* **Luồng thực thi:** Dọn dẹp bộ nhớ entity (mục 4 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Xây dựng `EntityManager` duyệt danh sách, cập nhật logic, và xóa đối tượng `isDestroyed`.
  * Lập trình lớp `Player` thực tế kế thừa `Actor`, khởi tạo kèm đối tượng `Inventory`.

### **Ticket 3.2: Hệ thống Vật lý, AI & Thu thập Input**

* **Thành phần cấu trúc:** `PlayerMovementBehavior`, `WanderMovementBehavior`, `GameInputHandler`.
* **Giao thức:** `IPlayerController`, `IMovementBehavior` (mục 4 - `mo_ta.md`).
* **Luồng thực thi:** Logic di chuyển (mục 2), tiêm hành vi (mục 9), AI (mục 10 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Đóng gói logic di chuyển. Lập trình kiểm tra va chạm O(n^2) kết hợp `Intersector.overlaps()`.
  * Lập trình `GameInputHandler` đọc phím WASD/Arrow chuyển thành boolean cho `IPlayerController` và đăng ký bộ lắng nghe này vào `InputMultiplexer`.

---

## Thành viên 4: Tương tác, Giao diện & Dữ liệu (Interaction, Inventory & Static Data)

### **Ticket 4.1: Hệ thống Tương tác & Lớp Thực thể phụ**

* **Thành phần cấu trúc:** `NPC`, `Item`, `ItemDefinition`.
* **Giao thức:** `IInteractable` (mục 4 - `mo_ta.md`).
* **Luồng thực thi:** Người chơi tương tác thực thể (mục 3 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Viết thuật toán quét khoảng cách tìm `IInteractable` gần nhất cho `EntityManager`.
  * Khởi tạo lớp `NPC` và `Item`, ghi đè hàm `interact()`.
  * Xây dựng kho dữ liệu giả lập (Mock Data) khởi tạo danh sách `ItemDefinition` tĩnh để tránh NullPointerException khi nhặt đồ.

### **Ticket 4.2: FSM UI & Lõi Inventory**

* **Thành phần cấu trúc:** `HUDManager`, `InventoryUITable`, `Inventory`, `ItemStack`, Scene2D Stage.
* **Máy trạng thái:** Luồng Game (`trang_thai.md`) - `PLAY_RUNNING` <-> `PLAY_UI_PAUSED`.
* **Luồng thực thi:** Bật/tắt UI và tạm dừng vật lý (mục 8 - `tuan_tu.md`).
* **Nhiệm vụ:**
  * Xây dựng cấu trúc lưu trữ mảng `ItemStack` trong `Inventory` và hàm cập nhật số lượng.
  * Lắng nghe sự kiện `INVENTORY_OPENED`/`CLOSED`. Đổi trạng thái `GameState` ngắt update vật lý.
  * **Can thiệp Input:** Đăng ký/Gỡ bỏ `Stage` của `HUDManager` khỏi `InputMultiplexer` khi mở/đóng túi đồ.
