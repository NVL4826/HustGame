Mô tả kiến trúc hệ thống Vũ khí **HustGame** (Vampire Survivors). Tuân thủ OOP (naming, SRP, OCP, LSP, Encapsulation).
Lưu ý: Interface `Weaponable` (không dùng tiền tố "I", dùng hậu tố "-able").

---

### 1. Interface: `Weaponable`
Hợp đồng giao tiếp giữa `Player`/`WeaponManager` và vũ khí. Thiết kế nhỏ gọn tránh phương thức thừa.

**Methods (camelCase):**
* **`fire()`**: Kích hoạt tấn công. Manager gọi không cần biết loại vũ khí.
* **`upgrade()`**: Nâng cấp vũ khí (level, stats).
* **`updateTimer(deltaTime)`**: Xử lý cooldown từ Game Loop.

---

### 2. Abstract Class: `BaseWeapon` implements `Weaponable`
Khung logic chung. Trách nhiệm: quản lý chỉ số cơ bản + Cooldown Lifecycle.

**Attributes:**
* `baseDamage`, `cooldown`, `level`, `currentCooldownTimer` khai báo `private`/`protected`.
* Truy cập qua Getter/Setter (vd: `getBaseDamage()`, `setLevel()`).

**Template Method Pattern (DRY):**
* `BaseWeapon` xử lý đếm ngược trong `updateTimer(deltaTime)`.
* `fire()` check `currentCooldownTimer`. Nếu 0, gọi `executeAttackAction()` và reset timer.
* `executeAttackAction()` là abstract (hiding complex implementations). Class con định nghĩa logic tấn công.

---

### 3. Concrete Classes (LSP & OCP)
Đa hình (Polymorphism). `WeaponManager` quản lý list `Weaponable`.
Thêm vũ khí mới không sửa `Weaponable`/`BaseWeapon` (OCP). Class con thay thế lớp cha (LSP).

#### A. `WhipWeapon` (Roi)
* **Inheritance:** `BaseWeapon`.
* **Attack:** `executeAttackAction()` tương tác `CollisionManager`, không bắn đạn.
* **Logic:** Tạo Hitbox (AABB) quét ngang hướng nhìn. `Enemy` trong Hitbox nhận `baseDamage`.
* **Effects:** `EventDispatcher` phát âm thanh + animation.

#### B. `MagicWandWeapon` (Đũa phép)
* **Inheritance:** `BaseWeapon`.
* **Attack:** Bắn đạn (`Projectile`).
* **Logic:**
    1. Tìm quái gần nhất qua helper (`EntityManager`/Spatial Hashing).
    2. Mượn `Projectile` từ `EntityFactory` (Object Pool).
    3. Truyền `baseDamage`, vị trí, hướng bay cho `Projectile`.
* **Memory:** `Projectile` về Pool khi trúng đích/ra khỏi màn hình.

#### C. `GarlicAuraWeapon` (Tỏi)
* **Inheritance:** `BaseWeapon`.
* **Attack:** AoE duy trì.
* **Logic:**
    * `executeAttackAction()` tạo vòng tròn va chạm bám `Player`.
    * `cooldown` là Tick rate (khoảng cách gây dame).
    * Quét thực thể trong bán kính, áp dụng dame theo nhịp.
* **Graphics:** Rendering vẽ hiệu ứng vòng tròn.

---

### Data Flow
1. Game loop: `Player` duyệt vũ khí, gọi `updateTimer(deltaTime)`.
2. `BaseWeapon` trừ `currentCooldownTimer`.
3. Timer hết, `BaseWeapon` kích hoạt `fire()`.
4. `fire()` gọi logic `Override` trong class con.
5. Vũ khí tương tác qua DIP với `EntityFactory`/`CollisionManager` (interfaces/abstractions). Tránh tight coupling.