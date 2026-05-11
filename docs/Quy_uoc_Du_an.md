# Quy ước Phát triển Dự án HustGame

Tài liệu này tổng hợp các quy ước lập trình, kiến trúc và quản lý tài nguyên đặc biệt áp dụng cho dự án HustGame. Mọi thành viên và AI hỗ trợ cần tuân thủ nghiêm ngặt các quy tắc này.

---

## 1. Quy ước Lập trình (Coding Standards)

### 1.1 Nguyên tắc Chung
*   **Viết Code Đầy đủ**: Các khối code được cung cấp phải hoàn chỉnh, sẵn sàng để copy-paste. Tránh sử dụng placeholder hoặc code bị lược bỏ.
*   **Bất biến (Immutability)**: Ưu tiên sử dụng từ khóa `final` cho các biến và thuộc tính. Hạn chế sử dụng setter nếu không thực sự cần thiết.
*   **Kiểm tra Dữ liệu đầu vào**: Luôn validate dữ liệu tại các phương thức public. Ném `IllegalArgumentException` hoặc `NullPointerException` ngay khi dữ liệu không hợp lệ.
*   **Xử lý Ngoại lệ**: Không nuốt ngoại lệ (silent failure). Sử dụng `Gdx.app.error()` để log lỗi kèm context.

### 1.2 LibGDX & Java Đặc thù
*   **Quản lý Bộ nhớ**:
    *   Hạn chế sử dụng từ khóa `new` trong vòng lặp `render` hoặc `update`. Sử dụng `com.badlogic.gdx.utils.Pool` để tái sử dụng object.
    *   Mọi lớp nắm giữ tài nguyên native phải implement `Disposable` và gọi `dispose()` khi không còn sử dụng.
*   **Quản lý Tài nguyên**: Tải tài nguyên thông qua `com.badlogic.gdx.assets.AssetManager` (hoặc `GameAssetManager` của dự án) bên ngoài constructor.
*   **Độc lập Khung hình (Frame-rate Independence)**: Luôn nhân các thay đổi về di chuyển/thời gian với `delta`. Tách biệt rõ ràng logic `update(float delta)` và `draw()`.

---

## 2. Kiến trúc & Design Patterns

### 2.1 Pattern Áp dụng
*   **Singleton**: Sử dụng cho các Manager trung tâm (e.g., `EventDispatcher`).
*   **Observer/Callbacks**: Sử dụng để phá vỡ sự phụ thuộc vòng (circular dependencies) và decouple các component.
*   **Behavior-based Entities**: Logic thực thể được chia nhỏ thành các `Behavior` riêng biệt.
*   **Flyweight Pattern**: Áp dụng cho hệ thống Item để tối ưu bộ nhớ.

### 2.2 Nguyên tắc SOLID
*   **SRP**: Mỗi class chỉ giữ một trách nhiệm duy nhất.
*   **OCP**: Cho phép mở rộng (extension) nhưng hạn chế sửa đổi trực tiếp vào core đã ổn định.
*   **DIP**: Phụ thuộc vào trừu tượng (Interface/Abstract class), không phụ thuộc vào triển khai cụ thể.

---

## 3. Quy ước Đặt tên (Naming Conventions)

*   **Classes/Interfaces**: Sử dụng `PascalCase` (Ví dụ: `PlayerEntity`, `ItemManager`).
*   **Methods**: Sử dụng `camelCase` (Ví dụ: `calculateDamage()`, `updatePosition()`).
*   **Attributes/Variables**: Sử dụng `camelCase` (Ví dụ: `currentHealth`, `movementSpeed`).
*   **Constants**: Sử dụng `UPPER_SNAKE_CASE` (Ví dụ: `MAX_LEVEL_WIDTH`).

---

## 4. Quy ước Render & Phân lớp (Graphics)

Chi tiết xem tại: [Quy_uoc_Render_Layer.md](file:///c:/Users/Admin/projects/HustGame/docs/Quy_uoc_Render_Layer.md)

| Nhóm Layer | Z-index | Đặc điểm |
| :--- | :--- | :--- |
| **Background** | -100 | Nền xa, không va chạm. |
| **Ground** | -50 | Mặt đất, gạch lát. |
| **YSort_Main** | 0 | **Y-Sorting**: Player, NPC, Vật thể có chiều sâu. |
| **Foreground** | 50 | Che khuất người chơi (mái nhà, tán cây). |
| **UI** | 100 | Luôn hiển thị trên cùng. |

*   **Pivot Point**: Đối với các đối tượng trong lớp `YSort_Main`, điểm mấu chốt phải đặt ở **chân** vật thể.

---

## 5. Quy trình Phát triển

1.  **Phân tích**: Xác định Input/Output và sự kiện liên quan.
2.  **Thiết kế**: Ưu tiên sử dụng `EventDispatcher` để giao tiếp giữa các module.
3.  **Kiểm tra**: Đảm bảo không gây rò rỉ bộ nhớ (kiểm tra `dispose()`) và hiệu năng ổn định.
