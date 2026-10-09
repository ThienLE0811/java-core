package com.javacore.t06_final_enum_record_nested_class_object_lifecycle;

public class RecordDemo {

    public static void main(String[] args) {
        System.out.println("=== Record cơ bản: compiler tự sinh constructor, getter, equals/hashCode/toString ===");
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        System.out.println("p1 = " + p1); // toString tự sinh, dạng Point[x=1, y=2]
        System.out.println("p1.x() = " + p1.x() + ", p1.y() = " + p1.y()); // getter tự sinh: tên method = tên field, không phải getX()
        System.out.println("p1.equals(p2) = " + p1.equals(p2)); // true: equals so sánh theo GIÁ TRỊ field, không phải địa chỉ
        System.out.println("p1 == p2 -> " + (p1 == p2)); // false: 2 object khác nhau trong heap

        System.out.println("\n=== Compact constructor: validate dữ liệu ngay khi tạo record ===");
        try {
            new Range(10, 1); // from > to -> vi phạm invariant, bị chặn ngay tại constructor
        } catch (IllegalArgumentException e) {
            System.out.println("Từ chối tạo Range: " + e.getMessage());
        }
        Range r = new Range(1, 10);
        System.out.println("Range hợp lệ: " + r + ", length() = " + r.length());

        System.out.println("\n=== Record có thể có thêm method như class thường ===");
        Employee emp = new Employee("An", 15_000_000);
        System.out.println(emp.describe());

        System.out.println("\n=== Record implement interface: vẫn dùng được polymorphism bình thường ===");
        Shape circle = new Circle(5);
        Shape square = new Square(4);
        for (Shape s : new Shape[]{circle, square}) {
            System.out.println(s + " -> area = " + s.area());
        }
    }

    // Record: chỉ cần khai báo các field trong "header", compiler tự sinh field private final,
    // constructor, getter (x(), y()), equals/hashCode/toString -> phù hợp cho object bất biến, mang dữ liệu (data carrier)
    record Point(int x, int y) {
    }

    // Compact constructor: viết constructor KHÔNG có tham số lặp lại và không có this.field = field,
    // chỉ để validate/chuẩn hóa dữ liệu đầu vào trước khi record gán field như bình thường
    record Range(int from, int to) {
        Range {
            if (from > to) {
                throw new IllegalArgumentException("from (" + from + ") phải <= to (" + to + ")");
            }
        }

        int length() {
            return to - from;
        }
    }

    // Record vẫn cho phép thêm method tự viết, ngoài các thứ compiler tự sinh
    record Employee(String name, long salaryVnd) {
        String describe() {
            return name + " - lương " + salaryVnd + " VND";
        }
    }

    interface Shape {
        double area();
    }

    record Circle(double radius) implements Shape {
        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    record Square(double side) implements Shape {
        @Override
        public double area() {
            return side * side;
        }
    }
}
