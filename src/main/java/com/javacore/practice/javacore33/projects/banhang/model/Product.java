package com.javacore.practice.javacore33.projects.banhang.model;

import java.util.Objects;

public class Product {

    private final String id;
    private String ten;
    private long gia;
    private int tonKho;

    public Product(String id, String ten, long gia, int tonKho) {
        this.id = kiemTraChuoi(id, "Mã sản phẩm");
        this.ten = kiemTraChuoi(ten, "Tên sản phẩm");
        this.gia = kiemTraGia(gia);
        if (tonKho < 0) {
            throw new IllegalArgumentException("Tồn kho ban đầu không được âm: " + tonKho);
        }
        this.tonKho = tonKho;
    }

    public String getId() {
        return this.id;
    }

    public String getTen() {
        return this.ten;
    }

    public long getGia() {
        return this.gia;
    }

    public int getTonKho() {
        return this.tonKho;
    }

    // R18: đổi tên/giá không đụng tới tồn kho, và không ảnh hưởng đơn đã đặt (R11).
    public void doiThongTin(String tenMoi, long giaMoi) {
        this.ten = kiemTraChuoi(tenMoi, "Tên sản phẩm");
        this.gia = kiemTraGia(giaMoi);
    }

    public void nhapThem(int soLuong) {
        kiemTraSoLuong(soLuong);
        this.tonKho += soLuong;
    }

    // R1 + R2: chỗ duy nhất tồn kho giảm, và nó tự từ chối thay vì kẹp về 0.
    public void giamTon(int soLuong) {
        kiemTraSoLuong(soLuong);
        if (soLuong > this.tonKho) {
            throw new OutOfStockException(this.id, this.ten, this.tonKho, soLuong);
        }
        this.tonKho -= soLuong;
    }

    public void hoanTon(int soLuong) {
        kiemTraSoLuong(soLuong);
        this.tonKho += soLuong;
    }

    public boolean duTon(int soLuong) {
        kiemTraSoLuong(soLuong);
        return soLuong <= this.tonKho;
    }

    private static String kiemTraChuoi(String giaTri, String ten) {
        Objects.requireNonNull(giaTri, ten + " không được null");
        if (giaTri.isBlank()) {
            throw new IllegalArgumentException(ten + " không được trống");
        }
        return giaTri;
    }

    private static long kiemTraGia(long gia) {
        if (gia < 0) {
            throw new IllegalArgumentException("Giá không được âm: " + gia);
        }
        return gia;
    }

    private static void kiemTraSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0: " + soLuong);
        }
    }

    // Danh tính là id, không phải tên hay giá - để dùng làm khoá Map an toàn kể cả sau khi
    // doiThongTin(). Khác hẳn Product của bài 11 (so sánh theo tham chiếu, sắp xếp theo giá).
    @Override
    public boolean equals(Object o) {
        return o instanceof Product khac && this.id.equals(khac.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Product{id='%s', ten='%s', gia=%d, tonKho=%d}", id, ten, gia, tonKho);
    }
}
