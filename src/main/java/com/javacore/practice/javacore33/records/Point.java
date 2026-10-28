package com.javacore.practice.javacore33.records;

import java.util.Objects;

public record Point(double x, double y) implements Comparable<Point> {

    // Chạy được vì compact constructor không đụng tới static nào.
    public static final Point GOC = new Point(0, 0);

    public Point {
        // isFinite trả true khi số HỢP LỆ -> phải phủ định mới là điều kiện lỗi.
        if (!Double.isFinite(x)) {
            throw new IllegalArgumentException("x phải là số hữu hạn, nhận: " + x);
        }
        if (!Double.isFinite(y)) {
            throw new IllegalArgumentException("y phải là số hữu hạn, nhận: " + y);
        }
        // Không có this.x = x; compiler tự gán sau dòng này.
    }

    public static Point cua(double x, double y) {
        return new Point(x, y);
    }

    public double khoangCachToi(Point khac) {
        // Message là chuỗi literal: đừng format với chính biến đang nghi là null.
        Objects.requireNonNull(khac, "khac không được null");
        // hypot thay cho sqrt(a*a + b*b): không tràn số khi toạ độ lớn.
        return Math.hypot(this.x - khac.x, this.y - khac.y);
    }

    public double khoangCachToiGoc() {
        return khoangCachToi(GOC);
    }

    // Không sửa được x/y của điểm cũ -> buộc phải trả instance mới. Đó là immutability.
    public Point tinhTien(double dx, double dy) {
        return new Point(this.x + dx, this.y + dy);
    }

    // Thứ tự tự nhiên: theo khoảng cách tới gốc. KHÔNG nhất quán với equals.
    @Override
    public int compareTo(Point khac) {
        return Double.compare(this.khoangCachToiGoc(), khac.khoangCachToiGoc());
    }
}
