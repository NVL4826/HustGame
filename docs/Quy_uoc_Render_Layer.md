# Quy ước Phân lớp và Sắp xếp Render (2D Orthographic)

Cấu trúc đặt tên + quản lý layer Tiled Map Editor. Tự động render, Y-sorting, mở rộng linh hoạt.

---

## 1. Cấu trúc Phân lớp (Layer Structure)

Dùng Z-index bước nhảy lớn (10/100) để dễ chèn lớp trung gian.

| Nhóm Layer | Z-index Gợi ý | Mô tả | Y-Sorting |
| :--- | :--- | :--- | :--- |
| **Background** | -100 | Cảnh nền xa, bầu trời, núi xa. | Không |
| **Ground** | -50 | Gạch lát, đất nền, mặt đường. | Không |
| **Ground_Decor** | -40 | Hoa, cỏ, vết nứt (sát đất). | Không |
| **YSort_Main** | 0 | Người chơi, NPC, cây, tường, vật thể tương tác. | **Có** |
| **Foreground** | 50 | Mái nhà, ngọn cây, mây (che khuất người chơi). | Không |
| **UI** | 100 | Thanh máu, menu, thông báo. | Không |

---

## 2. Phương pháp Triển khai trong Tiled Map Editor

2 cách tích hợp vào GameRenderer.

### Cách 1: Sử dụng Custom Properties (Khuyến nghị)
Thêm thuộc tính tùy chỉnh vào mỗi Layer:

* **`z_index` (int):** Thứ tự vẽ.
* **`y_sort` (bool):** Kích hoạt Y-sorting cho object.

**Xử lý trong GameRenderer:**
1. Đọc dữ liệu từ `.tmx` hoặc `.json`.
2. Lấy `z_index` làm ưu tiên render.
3. Nếu `y_sort == true`, sắp xếp đối tượng theo Y trước khi vẽ.

### Cách 2: Quy ước đặt tên (Naming Convention)
Định dạng: `[Z-Index]_[Tên_Mô_Tả]_[Loại_Sort]`.

**Ví dụ:**
* `-050_Ground_Static`
* `000_Main_YSort`
* `050_Roofs_Static`

**Quy tắc parsing:**
* Phần tử 1: Integer làm thứ tự render.
* Phần tử 3: Chuỗi "YSort" kích hoạt Y-sorting.

---

## 3. Lưu ý về Y-Sorting trên Layer Main

Hiệu ứng chiều sâu Orthographic:
* Vật thể chân chạm đất (Người chơi, Tường, Cây) cùng lớp `z_index = 0`.
* Pivot/Anchor Sprite đặt tại chân (đáy).
* Thuật toán: `RenderQueue.sort((a, b) => a.y - b.y)`.

---

## 4. Lợi ích của hệ thống
* **Linh hoạt:** Thêm layer (vd: `z_index = -45`) không lỗi layer cũ.
* **Tự động:** Chỉnh Tiled, GameRenderer cập nhật thứ tự vẽ, không sửa code.
* **Dễ quản lý:** Tên layer rõ nghĩa.
