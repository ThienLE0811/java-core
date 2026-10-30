package com.javacore.practice.javacore33.streams.csv;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Bài 30 - Phòng ban. Tên hằng chính là MÃ trong file CSV, còn {@code tenHienThi}
 * chỉ dùng khi in báo cáo.
 */
public enum PhongBan {

    IT("Công nghệ thông tin"),
    KE_TOAN("Kế toán"),
    NHAN_SU("Nhân sự"),
    MARKETING("Marketing");

    // Static field của enum được khởi tạo SAU khi 4 hằng ở trên đã tạo xong,
    // nên gọi values() ở đây là an toàn. Ngược lại, đụng vào MA_HOP_LE từ bên trong
    // constructor của enum sẽ thấy null - đó là bẫy thứ tự khởi tạo kinh điển.
    private static final String MA_HOP_LE = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.joining(", "));

    private final String tenHienThi;

    PhongBan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String tenHienThi() {
        return tenHienThi;
    }

    /**
     * Tra cứu an toàn thay cho {@link #valueOf(String)}.
     *
     * <p>valueOf ném IllegalArgumentException khi không khớp và NullPointerException khi
     * truyền null. Dữ liệu người dùng gõ sai là chuyện bình thường, không phải ngoại lệ,
     * nên ở đây trả Optional rỗng để phía gọi tự quyết định xử lý thế nào.
     */
    public static Optional<PhongBan> tuChuoi(String ma) {
        if (ma == null || ma.isBlank()) {
            return Optional.empty();
        }
        // CSV hay dính khoảng trắng thừa và viết hoa thường lẫn lộn -> chuẩn hoá trước khi so.
        // Locale.ROOT là bắt buộc: toUpperCase() không tham số dùng locale máy, trên máy
        // locale Thổ Nhĩ Kỳ "it" sẽ thành "İT" và không khớp hằng IT.
        String chuan = ma.trim().toUpperCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(pb -> pb.name().equals(chuan))
                .findFirst();
    }

    /**
     * Danh sách mã hợp lệ, dùng ghép vào thông điệp lỗi cho người dùng biết đường sửa.
     */
    public static String cacMaHopLe() {
        return MA_HOP_LE;
    }
}
