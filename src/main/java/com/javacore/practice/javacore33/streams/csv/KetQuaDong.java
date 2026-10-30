package com.javacore.practice.javacore33.streams.csv;

import java.util.List;
import java.util.Objects;

/**
 * Bài 30 - Kết quả parse MỘT dòng CSV: hoặc ra nhân viên hợp lệ, hoặc ra danh sách lỗi.
 *
 * <p>Java không có kiểu Either như một số ngôn ngữ khác, nên ta tự dựng bằng sealed
 * interface. "Sealed" nghĩa là chỉ hai record khai báo bên trong file này mới được
 * implement - không ai thêm nhánh thứ ba từ nơi khác. Nhờ vậy switch pattern matching
 * trên KetQuaDong là ĐỦ NHÁNH và compiler kiểm tra giúp, không cần default.
 *
 * <p>Vì sao không dùng exception cho nhánh lỗi: dữ liệu người dùng gõ sai là chuyện
 * thường ngày, không phải tình huống ngoại lệ. Dùng exception sẽ mất luôn khả năng
 * đi tiếp các dòng sau và gom đủ lỗi trong một lần chạy.
 */
public sealed interface KetQuaDong {

    /**
     * Số dòng trong file gốc. Cả hai nhánh đều có component tên {@code soDong}
     * nên record tự động hiện thực method này, không phải viết gì thêm.
     */
    long soDong();

    /**
     * Dòng parse thành công.
     */
    record HopLe(long soDong, NhanVien nhanVien) implements KetQuaDong {
        public HopLe {
            Objects.requireNonNull(nhanVien, "nhanVien không được null");
        }
    }

    /**
     * Dòng có lỗi. Là List chứ không phải một lỗi đơn: thiếu tên VÀ sai email thì
     * báo cả hai một lượt, đừng bắt người dùng sửa từng cái rồi upload lại.
     */
    record Loi(long soDong, List<LoiDong> cacLoi) implements KetQuaDong {
        public Loi {
            Objects.requireNonNull(cacLoi, "cacLoi không được null");
            if (cacLoi.isEmpty()) {
                throw new IllegalArgumentException("nhánh Loi phải có ít nhất một lỗi");
            }
            // List.copyOf chụp lại bản sao bất biến: người gọi có sửa list gốc sau đó
            // cũng không ảnh hưởng tới record. Record chỉ bất biến ở THAM CHIẾU,
            // không tự bảo vệ nội dung của collection bên trong.
            cacLoi = List.copyOf(cacLoi);
        }
    }
}
