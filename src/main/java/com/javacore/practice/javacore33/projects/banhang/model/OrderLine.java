package com.javacore.practice.javacore33.projects.banhang.model;

import java.util.Objects;

// R11: tenSanPham và donGia là bản chụp tại thời điểm đặt, KHÔNG giữ tham chiếu tới Product.
// Nhờ vậy sửa giá hay xoá sản phẩm về sau không làm sai báo cáo cũ (R15).
public record OrderLine(String productId, String tenSanPham, long donGia, int soLuong) {

    public OrderLine {
        Objects.requireNonNull(productId, "Mã sản phẩm không được null");
        Objects.requireNonNull(tenSanPham, "Tên sản phẩm không được null");
        if (donGia < 0) {
            throw new IllegalArgumentException("Đơn giá không được âm: " + donGia);
        }
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0: " + soLuong);
        }
    }

    public static OrderLine chupTu(Product sanPham, int soLuong) {
        Objects.requireNonNull(sanPham, "Sản phẩm không được null");
        return new OrderLine(sanPham.getId(), sanPham.getTen(), sanPham.getGia(), soLuong);
    }

    public long thanhTien() {
        return this.donGia * this.soLuong;
    }
}
