# TÀI LIỆU ĐẶC TẢ KIẾN TRÚC TỔNG THỂ DỰ ÁN GAME

## 1. Thông tin chung
- **Thể loại:** 2D Top-down RPG.
- **Nền tảng phát triển:** Java, thư viện LibGDX.
- **Mô hình kiến trúc:** Lập trình hướng đối tượng (OOP) truyền thống.

## 2. Công nghệ và Kỹ thuật áp dụng
- **Xử lý va chạm:** Tự xây dựng hệ thống va chạm AABB (Axis-Aligned Bounding Box) thông qua lớp `Rectangle` và `Intersector` của LibGDX. Thuật toán kiểm tra va chạm động duyệt qua các thực thể với độ phức tạp $O(n^2)$.
- **Xử lý đầu vào (Input):** Gán cố định (hardcode) các phím điều hướng (WASD/Arrow keys) để chuyển đổi thành các cờ logic (boolean).
- **Quản lý dữ liệu tĩnh:** Thông tin cấu hình vật phẩm (`ItemDefinition`) được khởi tạo và lưu trữ trực tiếp trong mã nguồn.
- **Tạm dừng luồng (UI Pause):** Sử dụng cờ trạng thái `GameState`. Khi mở giao diện, vòng lặp vật lý và logic của thực thể tạm dừng, trong khi vòng lặp kết xuất đồ họa (render) vẫn tiếp tục hoạt động.

## 3. Cấu trúc các phân hệ chức năng

Hệ thống được chia thành 5 phân hệ chính:

### 3.1. Phân hệ Core & Events
- Sử dụng lớp `HustGame` (kế thừa `Game`) làm lớp gốc để quản lý vòng đời ứng dụng và lưu trữ các thành phần dùng chung: `SpriteBatch`, `GameAssetManager`, và `EventDispatcher`.
- Sử dụng `EventDispatcher` để quản lý hệ thống phát/nhận sự kiện. Khai báo danh sách chờ (`queueActions`) và cờ `isDispatching` để đảm bảo an toàn khi các listener được thêm hoặc xóa trong quá trình hàng đợi sự kiện đang lặp.
- Sử dụng lớp `GameEvent<T>` (generic) kết hợp với enum `EventType` để định nghĩa và đóng gói dữ liệu truyền tải giữa các module.

### 3.2. Phân hệ Screen & Flow
- Sử dụng `BaseScreen` làm lớp cha abstract để chia sẻ tham chiếu đến đối tượng `HustGame` cho các màn hình con.
- Lớp `LoadingScreen` quản lý quá trình nạp tài nguyên. Khi điều kiện nạp hoàn tất, chuyển quyền điều khiển sang đối tượng `Screen` tiếp theo được chỉ định.
- Lớp `PlayScreen` là màn hình chính của trò chơi, quản lý biến `GameState` để điều tiết việc chạy hay dừng của logic game.

### 3.3. Phân hệ World & Rendering
- Sử dụng `MapManager` để phân tích dữ liệu từ `TiledMap`, trích xuất các vật thể tĩnh và lưu trữ thành một mảng `Array<Rectangle>` nhằm phục vụ việc kiểm tra ranh giới môi trường.
- Sử dụng `GameCamera` để tính toán góc nhìn. Lớp này nhận vào một tham số implement giao diện `ITargetable` (chứa hàm getX, getY) để tự động căn giữa khung hình theo đối tượng mục tiêu mỗi khung hình.
- Sử dụng `GameRenderer` để tách biệt tác vụ vẽ màn hình. Luồng vẽ áp dụng chiến thuật Y-Sorting: thu thập thực thể, sắp xếp theo tọa độ Y, và gọi hàm vẽ theo thứ tự (layer bản đồ dưới -> thực thể -> layer bản đồ trên).

### 3.4. Phân hệ Entities & Input
- Sử dụng hệ thống kế thừa OOP để định nghĩa các đối tượng trong game. Lớp cơ sở `Entity` quản lý tọa độ, hộp va chạm (`Rectangle`) và hình ảnh (`TextureRegion`). 
- Áp dụng nguyên tắc Composition over Inheritance (Strategy Pattern) để giải quyết sự cứng nhắc của kế thừa. Các lớp thực thể không tự thực hiện logic di chuyển.
- Lớp `Actor` lưu trữ các tham chiếu hành vi độc lập thông qua giao diện `IMovementBehavior`. Tích hợp hàm `setMovementBehavior()` để tiêm (inject) logic tương ứng vào thực thể.
- Các lớp `Actor`, `Player`, `NPC`, và `Item` kế thừa và mở rộng tính năng của `Entity`.
- Sử dụng giao diện `IInteractable` cho các đối tượng có thể tương tác (như `NPC`, `Item`).
- Sử dụng `EntityManager` để quản lý toàn bộ thực thể. Các thực thể hết vòng đời sẽ được đánh dấu (`isDestroyed`), sau đó được `EntityManager` loại bỏ đồng loạt vào cuối khung hình thông qua hàm `cleanUpDestroyedEntities()`.
- Sử dụng giao diện `IPlayerController` để Player đọc tín hiệu điều khiển. Lớp `GameInputHandler` triển khai giao diện này và kết nối với hệ thống đầu vào của LibGDX (`InputProcessor`).

### 3.5. Phân hệ Inventory & UI
- Sử dụng `Inventory` làm lớp chứa dữ liệu cốt lõi của túi đồ, lưu trữ mảng các `ItemStack`. Mỗi `ItemStack` chứa tham chiếu tới cấu hình gốc `ItemDefinition` và số lượng.
- Phân tách logic giao diện: `Inventory` phát sự kiện thay đổi dữ liệu qua `EventDispatcher`. `HUDManager` đóng vai trò lắng nghe sự kiện để kích hoạt lớp `InventoryUITable` tiến hành vẽ lại (rebuild) cấu trúc hiển thị.
- Sử dụng các thành phần của thư viện `Scene2D.ui` (`Stage`, `Table`, `ScrollPane`) bên trong `HUDManager` để khởi tạo, hiển thị và xử lý đầu vào của giao diện túi đồ.

### 3.6. Phân hệ Behaviors (Hành vi)
- Phân hệ quản lý các khối logic đóng gói có thể tái sử dụng. Sử dụng giao diện gốc `IBehavior` có hàm `update(entity, delta)`.
- **Hành vi di chuyển (`IMovementBehavior`):** Quản lý tọa độ và vận tốc. Triển khai cụ thể bao gồm `PlayerMovementBehavior` (nhận tham chiếu `IPlayerController` để dịch chuyển theo phím bấm) và `WanderMovementBehavior` (thuật toán AI tự động đi tuần tra dựa trên bán kính `wanderRadius`).

## 4. Giao thức giao tiếp (Interface Contracts)
Nhằm đảm bảo nguyên tắc Inversion of Control (IoC) và phát triển song song, hệ thống bắt buộc sử dụng các giao thức sau:
- **`ITargetable`**: Tách biệt Camera khỏi Player. Bất kỳ đối tượng nào implement interface này đều có thể được Camera theo dõi.
- **`IInteractable`**: Đóng gói logic tương tác (nói chuyện, nhặt đồ). Player chỉ cần kiểm tra xem đối tượng có implement interface này hay không để kích hoạt.
- **`IPlayerController`**: Trừu tượng hóa luồng dữ liệu đầu vào. Tránh phụ thuộc cứng vào LibGDX InputProcessor.
- **`EventListener`**: Chuẩn hóa chữ ký hàm nhận sự kiện.
- **`IMovementBehavior`**: Quy định ranh giới cho logic di chuyển.

## 5. Quy chuẩn Namespace và Package
Hệ thống yêu cầu toàn bộ mã nguồn và test phải được đặt bên trong package gốc `vn.hust.hustgame`. 
Ví dụ: `vn.hust.hustgame.core`, `vn.hust.hustgame.screens`, `vn.hust.hustgame.events`. Việc khai báo sai package hoặc đặt ở thư mục Java mặc định sẽ phá vỡ quy chuẩn dự án phân tầng hiện tại.
