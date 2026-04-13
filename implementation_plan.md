# Kế hoạch Triển khai: Thành viên 3 - Thực thể, Vật lý & AI

Kế hoạch này giải quyết **Ticket 3.1** (FSM Thực thể & Vòng đời đối tượng) và **Ticket 3.2** (Hệ thống Vật lý, AI & Thu thập Input) dựa trên tài liệu `giao_viec.md`.

## Mục tiêu

Tái cấu trúc lớp `Player` nguyên khối hiện tại thành một kiến trúc Entity-Component chuẩn. Việc này bao gồm khởi tạo các lớp cơ sở `Entity` và `Actor`, một `EntityManager` để quản lý vòng đời của chúng, và tách biệt logic di chuyển, thu thập Input qua các mẫu thiết kế Strategy (`IMovementBehavior`) và Giao diện (`IPlayerController`).

## Yêu cầu người dùng xem xét

> [!IMPORTANT]  
> Lớp `Player` hiện đang tự xử lý hầu hết mọi thứ (input, va chạm, render, hoạt ảnh). Việc tách nó ra thành `Entity`, `Actor` và các hành vi (behaviors) riêng biệt sẽ yêu cầu thay đổi cách file `HustGame.java` khởi tạo và cập nhật nhân vật.
> 
> Ngoài ra, vì Thành viên 4 phụ trách phần `Inventory`, tôi sẽ tạo một lớp `Inventory` rỗng (mock) để thỏa mãn yêu cầu khởi tạo của `Player` nhằm đảm bảo code không báo lỗi. Bạn có đồng ý với điều này không?

## Các thay đổi đề xuất

### Core Interfaces & FSM
Định nghĩa quy chuẩn cho việc xử lý input, di chuyển và trạng thái cơ bản của một thực thể.

#### [NEW] `com/duc/hustgame/EntityState.java`
- Enum đại diện cho các trạng thái: `IDLE`, `MOVING`, `DEAD`.

#### [NEW] `com/duc/hustgame/IPlayerController.java`
- Giao diện lấy trạng thái bàn phím: `isUp()`, `isDown()`, `isLeft()`, `isRight()`.

#### [NEW] `com/duc/hustgame/IMovementBehavior.java`
- Giao diện chứa phương thức xử lý di chuyển: `void update(Entity entity, float delta)`.

---

### Các lớp cơ sở của hệ thống Thực thể (Entity System)
Xây dựng nền tảng gốc cho tất cả các đối tượng trong game.

#### [NEW] `com/duc/hustgame/Entity.java`
- **Thuộc tính**: tọa độ `x`, `y`, kích thước `width`, `height`, hộp va chạm `bounds` (Rectangle), cờ `isDestroyed` (boolean), trạng thái `state` (EntityState).
- **Phương thức**: trừu tượng `update(float delta)`, trừu tượng `draw(SpriteBatch batch)`, `dispose()`.

#### [NEW] `com/duc/hustgame/Actor.java`
- **Kế thừa**: `Entity` 
- **Thuộc tính**: quản lý `IMovementBehavior`, hướng `Direction` (Enum) và hỗ trợ cập nhật Texture/Animation cơ bản.

#### [NEW] `com/duc/hustgame/EntityManager.java`
- Quản lý danh sách `Array<Entity>`.
- Vòng lặp `update(float delta)` để duyệt toàn bộ list, cập nhật logic và xóa bỏ các đối tượng nếu biến `isDestroyed` đang là true.

---

### Cài đặt Logic Vật lý, Input & AI
Đưa logic xử lý va chạm và hệ thống AI ra ngoài để dễ quản lý.

#### [NEW] `com/duc/hustgame/GameInputHandler.java`
- Chạy giao thức `IPlayerController` và `InputProcessor` của LibGDX. Lắng nghe WASD/Phím mũi tên và chuyển đổi thành boolean.

#### [NEW] `com/duc/hustgame/PlayerMovementBehavior.java`
- Cài đặt `IMovementBehavior`. Nhận logic từ `IPlayerController` và dữ liệu bản đồ `TiledMap`. 
- Logic: kiểm tra va chạm bằng độ phức tạp O(n²) kết hợp `Intersector.overlaps()`, giới hạn tọa độ để không văng ra khỏi biên màn hình.

#### [NEW] `com/duc/hustgame/WanderMovementBehavior.java`
- Cài đặt `IMovementBehavior`. Trí tuệ nhân tạo (AI) cơ bản giúp NPC di chuyển ngẫu nhiên.

#### [NEW] `com/duc/hustgame/Inventory.java`
- Lớp ảo rỗng, tạo ra để cho vào hàm tạo của Player theo đúng thiết kế của Thành viên 4.

---

### Tái cấu trúc Player (Refactoring)

#### [MODIFY] `com/duc/hustgame/Player.java`
- **Kế thừa**: `Actor`.
- **Hàm tạo (Constructor)**: Nhận `IPlayerController`, `Inventory` và tham chiếu tới di chuyển sang `PlayerMovementBehavior`.
- Xóa bỏ việc kiểm tra `Gdx.input` ở trong hàm `update()`. Trích xuất và đẩy code xử lý va chạm vào `PlayerMovementBehavior`. Giữ lại phần vẽ ảnh (draw) và hoạt ảnh (animation), tận dụng FSM state của `Entity` (`IDLE` hoặc `MOVING`).

## Câu hỏi Mở

1. Bạn có muốn tích hợp luôn `EntityManager` và `GameInputHandler` vào trong class `HustGame.java` luôn để chạy thử xem nhân vật có cử động bình thường không? (Bởi vì tính năng `InputMultiplexer` là nhiệm vụ của Thành viên 1, nhưng ta vẫn có thể kết nối tạm thời).
2. Ở phần va chạm O(n²) dùng `Intersector.overlaps()`, ngoài lớp tĩnh của bản đồ, bạn có muốn kiểm tra va chạm với các vật thể động nào khác không?

## Kế hoạch Kiểm tra

### Tự động
- Không tồn tại lỗi logic / ClassNotFound khi biên dịch Java.

### Thủ công
- Mở game lên và xem xét xem bộ nút WASD có hoạt động bình thường không.
- Camera có bám sát theo chiều đi mà không sinh ra lỗi gì hay đi xuyên qua tường không.
