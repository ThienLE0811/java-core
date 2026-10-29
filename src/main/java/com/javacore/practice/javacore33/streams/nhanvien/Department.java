package com.javacore.practice.javacore33.streams.nhanvien;

/**
 * Phòng ban của nhân viên. Dùng enum thay String để groupingBy không bao giờ
 * sinh ra key kiểu "Ky thuat" / "KỸ THUẬT" do gõ sai.
 */
public enum Department {

    KY_THUAT("Kỹ thuật"),
    KINH_DOANH("Kinh doanh"),
    MARKETING("Marketing"),
    NHAN_SU("Nhân sự"),
    KE_TOAN("Kế toán");

    private final String tenHienThi;

    Department(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String tenHienThi() {
        return tenHienThi;
    }
}
