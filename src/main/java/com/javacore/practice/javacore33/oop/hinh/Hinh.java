package com.javacore.practice.javacore33.oop.hinh;

import java.util.Objects;

/**
 * Bài 9 - lớp trừu tượng cho mọi hình phẳng.
 *
 * <p>Điểm của bài này nằm ở {@link #tongDienTich(Hinh[])}: nó cộng diện tích mà không cần
 * biết từng phần tử là hình gì. Thêm một loại hình mới chỉ cần {@code extends Hinh} - method
 * tính tổng không phải sửa một dòng nào. Nếu trong đó xuất hiện {@code instanceof} hay
 * {@code switch} theo loại hình thì đa hình đã hỏng.
 *
 * <p>Dùng {@code double} chứ không {@link java.math.BigDecimal} như
 * {@link com.javacore.practice.javacore33.oop.TaiKhoanNganHang}: đây là đại lượng đo lường,
 * bản thân {@link Math#PI} đã là xấp xỉ nên độ chính xác tuyệt đối vừa không đạt được vừa
 * không cần thiết. BigDecimal để dành cho tiền.
 */
public abstract class Hinh {

    /**
     * Diện tích của hình, luôn là số dương hữu hạn.
     *
     * <p>Khai báo {@code abstract} chứ không phải method thường trả {@code 0}: lớp con quên
     * cài công thức thì hỏng ngay lúc compile, chứ không âm thầm đóng góp {@code 0} vào tổng.
     */
    public abstract double dienTich();

    /**
     * Tổng diện tích của một mảng hình.
     *
     * <p>Mảng rỗng trả {@code 0.0} - không có hình nào thì tổng bằng 0, đó là kết quả đúng
     * chứ không phải lỗi.
     *
     * @param danhSach mảng hình, không được null và không được chứa phần tử null
     * @return tổng diện tích, {@code 0.0} nếu mảng rỗng
     * @throws NullPointerException nếu {@code danhSach} là null, hoặc chứa phần tử null
     *                              => tính trừu tượng
     */
    public static double tongDienTich(Hinh[] danhSach) {
        Objects.requireNonNull(danhSach, "danhSach không được null");

        double tong = 0;
        // Duyệt theo chỉ số thay vì for-each để báo được vị trí phần tử null:
        // "phần tử thứ 3 là null" giúp tìm ra chỗ sai nhanh hơn hẳn một NPE trống trơn.
        for (int i = 0; i < danhSach.length; i++) {
            if (danhSach[i] == null) {
                throw new NullPointerException("Phần tử tại vị trí " + i + " là null");
            }
            tong += danhSach[i].dienTich();
        }
        return tong;
    }

    protected static double kiemTraKichThuoc(double giaTri, String ten) {
        // > 0 và hữu hạn → trả về giaTri; ngược lại ném IllegalArgumentException có tên + giá trị
        if (!Double.isFinite(giaTri) || giaTri <= 0) {
            throw new IllegalArgumentException(String.format("%s phải là số dương hữu hạn"
                    , ten));
        }
        return giaTri;
    }
}
