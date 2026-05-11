Phân tích Stats System. Gồm data tĩnh + động tác động logic game.
Phân loại chỉ số theo nhóm để `WeaponManager`, `WaveManager`, `UIManager` xử lý.

### 1. Nhóm Thông tin Định danh & Tài nguyên (Metadata & Assets)
Data read-only cấu hình ban đầu cho UI + load tài nguyên.
* **`charName` (Antonio):** Tên nhân vật.
* **`textureName` (character_antonio):** Path/atlas cho `GameAssetManager`.
* **`spriteName` (newAntonio_01.png):** Frame mặc định.
* **`description`:** Mô tả kỹ năng nội tại.
* **`startingWeapon` (WHIP):** ID vũ khí mặc định. `WeaponManager` khởi tạo khi bắt đầu.

### 2. Nhóm Chỉ số Sinh tồn (Survival Stats)
Gắn với `BaseEntity` của Player. Quyết định sống sót.
* **`level` (1):** Cấp hiện tại. Xác định ngưỡng `PlayerLevelUpEvent`.
* **`maxHp` (120):** HP tối đa.
* **`armor` (1):** Giáp. `Sát thương = Sát thương quái - Giáp`.
* **`regen` (0):** Tốc độ hồi phục HP mỗi giây/chu kỳ.
* **`moveSpeed` (1):** Hệ số speed. Quản lý bởi `PlayerMovementBehavior`. 1 = 100% base speed.

### 3. Nhóm Chỉ số Tấn công (Offensive Stats)
Multipliers cho `WeaponManager` thay đổi thuộc tính vũ khí.
* **`power` (1):** Might. Sát thương = Gốc * `power`.
* **`cooldown` (1):** % hồi chiêu. 1 = 100%. Giảm `cooldown` = giảm thời gian chờ.
* **`area` (1):** Kích thước đòn đánh. Phóng to `Collider` + sprite.
* **`speed` (1):** Projectile Speed. Áp dụng đạn bay (dao, đũa).
* **`duration` (1):** Thời gian tồn tại đòn đánh (vùng sát thương).
* **`amount` (0):** Số lượng đạn/tia cộng thêm. 0 = mặc định. 1 = mặc định + 1.

### 4. Nhóm Chỉ số Đa dụng & Kinh tế (Utility & Economy Stats)
Tốc độ thăng tiến sức mạnh.
* **`luck` (1):** Luck rate. Tăng RNG tốt: chí mạng, rớt item, rương x3-x5.
* **`growth` (1):** EXP multiplier. 1.2 = +20% EXP.
* **`greed` (1):** Gold multiplier cho Meta-progression.
* **`curse` (1):** Độ khó. `WaveManager` tăng HP/speed/spawn quái. Quái đông = nhiều EXP.
* **`magnet` (0):** Bán kính hút `ExpGem`. 0 = chạm mới nhặt.

### 5. Nhóm Chỉ số Quản lý RNG / Giao diện (Meta / UI Stats)
Dùng tại `UpgradeScreen` hoặc khi chết.
* **`revivals` (0):** Số lần hồi sinh 50% HP.
* **`rerolls` (0):** Reset danh sách item khi lên cấp.
* **`skips` (0):** Bỏ qua chọn item, nhận ít EXP bù.
* **`banish` (0):** Xóa item khỏi pool trận đó.

### Gợi ý Triển khai OOP
Tách data khỏi lớp `Player`:
1. **Lớp `CharacterData`**: Meta/Assets (nhóm 1).
2. **Lớp `PlayerStats`**: Chỉ số động (`float`). Dùng **Observer Pattern** (hoặc `EventDispatcher`) để update Player khi chỉ số đổi (VD: `moveSpeed`). Tránh check trong `update()`.