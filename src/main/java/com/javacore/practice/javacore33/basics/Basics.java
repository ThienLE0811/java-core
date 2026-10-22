package com.javacore.practice.javacore33.basics;

/**
 * Bài 1 - Xếp loại học sinh.
 *
 * <p>Cùng một quy tắc xếp loại được viết hai cách: {@code if/else if} và {@code switch}.
 * Hai method phải cho kết quả giống hệt nhau trên mọi điểm hợp lệ - điều này được
 * kiểm chứng trong {@code BasicsTest}.
 *
 * <p>Thang điểm: dưới 40 Yếu, dưới 50 Trung bình, dưới 65 Khá, dưới 80 Giỏi,
 * từ 80 trở lên Xuất sắc.
 */
public class Basics {
    public static final String KHONG_HOP_LE = "Không hợp lệ";

    public static String xepLoaiHocSinhIfElse(Integer diem) {
        if (diem < 0 || diem > 100) {
            return KHONG_HOP_LE;
        }

        if (diem < 40) {
            return "Yếu";
        } else if (diem < 50) {
            return "Trung bình";
        } else if (diem < 65) {
            return "Khá";
        } else if (diem < 80) {
            return "Giỏi";
        } else {
            return "Xuất sắc";
        }
    }

    public static String xepLoaiHocSinhSwitch(Integer diem) {
        return switch (diem) {
            case Integer i when i < 0 || i > 100 -> KHONG_HOP_LE;
            case Integer i when i < 40 -> "Yếu";
            case Integer i when i < 50 -> "Trung bình";
            case Integer i when i < 65 -> "Khá";
            case Integer i when i < 80 -> "Giỏi";
            default -> "Xuất sắc";
        };
    }
}
