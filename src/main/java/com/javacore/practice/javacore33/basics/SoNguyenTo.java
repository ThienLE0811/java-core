package com.javacore.practice.javacore33.basics;

import java.util.ArrayList;
import java.util.List;

/**
 * Bài 2 - Số nguyên tố.
 *
 * <p>Số nguyên tố là số tự nhiên lớn hơn 1, có đúng hai ước dương: 1 và chính nó.
 *
 * <p>Chỉ cần chia thử tới căn bậc hai của n: nếu {@code n = a * b} thì không thể cả
 * {@code a} và {@code b} đều lớn hơn {@code √n}, vì khi đó tích đã vượt quá n. Vậy n
 * có ước thì chắc chắn có một ước không vượt quá {@code √n}. Điều kiện được viết là
 * {@code uoc * uoc <= num} thay vì {@code uoc <= Math.sqrt(num)} để chỉ làm việc trên
 * số nguyên, tránh sai số dấu phẩy động và tránh gọi lại {@code Math.sqrt()} mỗi vòng lặp.
 *
 * <p>Class tiện ích: chỉ chứa hàm thuần, không giữ trạng thái nên khai báo {@code final}
 * và chặn khởi tạo - cùng kiểu thiết kế với {@link java.lang.Math}.
 */
public final class SoNguyenTo {

    private SoNguyenTo() {
        // Class chỉ chứa static method, không có lý do gì để tạo đối tượng.
    }

    /**
     * Kiểm tra {@code num} có phải số nguyên tố không.
     *
     * <p>Số nhỏ hơn 2 (gồm 0, 1 và mọi số âm) đều không phải số nguyên tố.
     * Riêng {@code num = 2} tự đúng mà không cần xử lý riêng: {@code 2 * 2 > 2}
     * nên vòng lặp không chạy lần nào và hàm trả về {@code true}.
     */
    public static boolean laSoNguyenTo(int num) {
        if (num < 2) return false;

        for (int uoc = 2; uoc * uoc <= num; uoc++) {
            if (num % uoc == 0) return false;
        }
        return true;
    }

    /**
     * Liệt kê các số nguyên tố trong khoảng đóng {@code [2, num]}, theo thứ tự tăng dần.
     *
     * @return danh sách số nguyên tố; danh sách rỗng nếu {@code num < 2} (không bao giờ null)
     */
    public static List<Integer> lietKeSoNguyenTo(int num) {
        List<Integer> danhSach = new ArrayList<>();
        for (int i = 2; i <= num; i++) {
            if (laSoNguyenTo(i)) danhSach.add(i);
        }
        return danhSach;
    }

    /**
     * Tính tổng các số nguyên tố trong khoảng đóng {@code [2, num]}.
     *
     * <p>Trả về {@code long} chứ không phải {@code int}: tổng vượt
     * {@link Integer#MAX_VALUE} ngay tại {@code num = 225287}, mà tràn số nguyên
     * trong Java diễn ra im lặng - kết quả thành số âm, không có exception nào báo.
     *
     * @return tổng các số nguyên tố; {@code 0} nếu {@code num < 2}
     */
    public static long tongSoNguyenTo(int num) {
        long tong = 0;
        for (int soNguyenTo : lietKeSoNguyenTo(num)) {
            tong += soNguyenTo;
        }
        return tong;
    }
}
