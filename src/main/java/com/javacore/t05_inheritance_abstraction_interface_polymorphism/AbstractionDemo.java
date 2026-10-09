package com.javacore.t05_inheritance_abstraction_interface_polymorphism;

public class AbstractionDemo {

    public static void main(String[] args) {
        // Không thể new trực tiếp abstract class (dòng dưới sẽ KHÔNG compile nếu bỏ comment):
        // Shape shape = new Shape();

        Shape circle = new Circle(5);
        Shape rectangle = new Rectangle(4, 6);

        // describe() là method CỤ THỂ (concrete) định nghĩa sẵn trong Shape (template method),
        // nhưng bên trong nó gọi area()/perimeter() là abstract -> mỗi class con tự quyết định cách tính
        System.out.println(circle.describe());
        System.out.println(rectangle.describe());

        // Abstract class vẫn có constructor bình thường, dù không tạo object trực tiếp được,
        // constructor này chỉ chạy khi class con gọi super(...)
    }

    // abstract class: lớp "dở dang" - có thể trộn method abstract (chưa cài đặt) và method cụ thể (đã cài đặt sẵn),
    // dùng khi các class con CHIA SẺ chung một phần logic, nhưng có phần bắt buộc mỗi con phải tự triển khai khác nhau
    abstract static class Shape {
        private final String name;

        // Abstract class vẫn có constructor: chạy khi class con gọi super(...), dùng để khởi tạo phần chung
        Shape(String name) {
            this.name = name;
        }

        // abstract method: chỉ khai báo signature, KHÔNG có thân hàm -> bắt buộc class con phải override
        abstract double area();

        abstract double perimeter();

        // Template method pattern: method cụ thể định nghĩa SẴN bộ khung xử lý,
        // các bước chi tiết (area/perimeter) được "cắắm" vào từ class con thông qua abstract method
        String describe() {
            return name + ": area=" + area() + ", perimeter=" + perimeter();
        }
    }

    static class Circle extends Shape {
        private final double radius;

        Circle(double radius) {
            super("Circle");
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }

        @Override
        double perimeter() {
            return 2 * Math.PI * radius;
        }
    }

    static class Rectangle extends Shape {
        private final double width;
        private final double height;

        Rectangle(double width, double height) {
            super("Rectangle");
            this.width = width;
            this.height = height;
        }

        @Override
        double area() {
            return width * height;
        }

        @Override
        double perimeter() {
            return 2 * (width + height);
        }
    }
}
