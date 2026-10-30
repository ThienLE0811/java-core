package com.javacore.practice.javacore33.streams.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Bài 30 - Một nhân viên ĐÃ qua validate. Dữ liệu trong đây luôn sạch và đủ.
 *
 * <p>Lương dùng {@link BigDecimal} chứ không phải double: double là số nhị phân dấu phẩy
 * động, 0.1 + 0.2 không bằng 0.3, cộng dồn bảng lương sẽ lệch dần. Tiền bạc thì luôn
 * BigDecimal, và luôn tạo từ String - {@code new BigDecimal(0.1)} cho ra một số rác dài
 * 50 chữ số.
 */
public record NhanVien(int id,
                       String hoTen,
                       String email,
                       PhongBan phongBan,
                       BigDecimal luong,
                       LocalDate ngayVaoLam) {

    // Record này chỉ được tạo SAU khi CsvImporter đã validate xong, nên ở đây không kiểm tra
    // dữ liệu nghiệp vụ (email đúng định dạng chưa, lương có âm không). Việc đó thuộc về
    // CsvImporter và kết quả là LoiDong, không phải exception.
    // requireNonNull ở đây bắt LỖI LẬP TRÌNH (quên truyền tham số), khác hẳn lỗi dữ liệu.
    public NhanVien {
        Objects.requireNonNull(hoTen, "hoTen không được null");
        Objects.requireNonNull(email, "email không được null");
        Objects.requireNonNull(phongBan, "phongBan không được null");
        Objects.requireNonNull(luong, "luong không được null");
        Objects.requireNonNull(ngayVaoLam, "ngayVaoLam không được null");
    }
}
