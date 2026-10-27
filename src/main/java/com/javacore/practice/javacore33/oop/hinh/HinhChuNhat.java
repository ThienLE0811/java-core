package com.javacore.practice.javacore33.oop.hinh;

public class HinhChuNhat extends Hinh {
    private final double chieuDai;
    private final double chieuRong; // final: hình đã tạo thì không méo đi được

    public HinhChuNhat(double chieuDai, double chieuRong) {
        this.chieuDai = kiemTraKichThuoc(chieuDai, "Chiều dài");
        this.chieuRong = kiemTraKichThuoc(chieuRong, "Chiều rộng");
    }

    @Override
    public double dienTich() {
        return chieuDai * chieuRong;
    }

    @Override
    public String toString() {
        return String.format("HinhChuNhat{chieuDai=%s,chieuRong=%s, dienTich=%s}",
                chieuDai, chieuRong, dienTich());
    }
}
