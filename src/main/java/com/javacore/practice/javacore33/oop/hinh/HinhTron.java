package com.javacore.practice.javacore33.oop.hinh;

public class HinhTron extends Hinh {
    // tính đóng gói
    private final double banKinh;        // final: hình đã tạo thì không méo đi được

    public HinhTron(double banKinh) {
        this.banKinh = kiemTraKichThuoc(banKinh, "Bán kính");
    }

    @Override
    public double dienTich() {
        return Math.PI * banKinh * banKinh;
    }

    @Override
    public String toString() {
        return String.format("HinhTron{banKinh=%s, dienTich=%s}",
                banKinh, dienTich());
    }
}
