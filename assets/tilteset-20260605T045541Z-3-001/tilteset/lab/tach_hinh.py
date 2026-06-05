import cv2
import numpy as np
import os

def extract_sprites_from_sheet(image_path, output_folder, min_area=10):
    # Tạo thư mục đầu ra
    os.makedirs(output_folder, exist_ok=True)

    # Đọc ảnh với flag IMREAD_UNCHANGED để giữ nguyên kênh Alpha (trong suốt)
    image = cv2.imread(image_path, cv2.IMREAD_UNCHANGED)

    if image is None:
        print("Lỗi: Không thể tải ảnh. Hãy kiểm tra lại đường dẫn.")
        return

    # Kiểm tra ảnh có kênh Alpha hay không (định dạng BGRA)
    if image.shape[2] != 4:
        print("Lỗi: Ảnh không có kênh Alpha (không có nền trong suốt).")
        return

    # Tách riêng kênh Alpha
    alpha_channel = image[:, :, 3]

    # Nhị phân hóa kênh Alpha: pixel nào không trong suốt hoàn toàn (>0) sẽ thành 255 (trắng)
    _, mask = cv2.threshold(alpha_channel, 0, 255, cv2.THRESH_BINARY)

    # Tìm các đường viền (contours) của các vùng màu trên mặt nạ
    # RETR_EXTERNAL chỉ lấy các đường viền bên ngoài cùng, không lấy lỗ hổng bên trong hình
    contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    print(f"Phát hiện {len(contours)} vùng độc lập.")

    saved_count = 0
    # Duyệt qua từng contour để cắt ảnh
    for contour in contours:
        # Lấy tọa độ hình chữ nhật nhỏ nhất bao quanh contour
        x, y, w, h = cv2.boundingRect(contour)

        # Lọc bỏ các điểm ảnh quá nhỏ (có thể là nhiễu/pixel vụn)
        if w * h >= min_area:
            # Cắt mảng màu từ ảnh gốc
            cropped_sprite = image[y:y+h, x:x+w]

            # Tạo tên file và lưu
            output_path = os.path.join(output_folder, f"sprite_{saved_count:04d}.png")
            cv2.imwrite(output_path, cropped_sprite)
            saved_count += 1

    print(f"Đã lưu {saved_count} hình vào thư mục: {output_folder}")

# --- CÁCH SỬ DỤNG ---
# Thay đổi đường dẫn file ảnh của bạn và tên thư mục muốn lưu
input_image_path = r"C:\Users\Admin\Downloads\Fix\tilteset\lab\IMG_0031.PNG" 
output_directory = "output_sprites"

extract_sprites_from_sheet(input_image_path, output_directory, min_area=20)