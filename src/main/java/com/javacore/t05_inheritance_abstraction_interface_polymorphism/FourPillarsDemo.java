package com.javacore.t05_inheritance_abstraction_interface_polymorphism;

import java.util.Arrays;
import java.util.List;

// Demo tổng hợp: 1 ví dụ nhỏ thể hiện đủ 4 tính chất OOP (Encapsulation, Inheritance,
// Abstraction, Polymorphism) cùng lúc, thay vì tách riêng từng file như các Demo trước
public class FourPillarsDemo {

    public static void main(String[] args) {
        List<Shape> shapes = Arrays.asList(
                new Circle(5),
                new Rectangle(4, 6),
                new Square(3)
        );

        System.out.println("=== Polymorphism: cùng gọi shape.describe(), nhưng chạy khác nhau tùy kiểu thực sự ===");
        for (Shape shape : shapes) {
            // Cùng 1 dòng code "shape.describe()", nhưng JVM chọn đúng bản area()/perimeter()
            // của Circle/Rectangle/Square dựa vào kiểu THỰC SỰ của object lúc runtime
            System.out.println(shape.describe());
        }

        System.out.println("\n=== Encapsulation: không sửa trực tiếp field, phải qua method có kiểm soát ===");
        Circle circle = new Circle(2);
        // circle.radius = -10; // ❌ không compile được vì radius là private -> field được bảo vệ
        try {
            circle.resize(-10); // resize() tự validate trước khi cho phép thay đổi
        } catch (IllegalArgumentException e) {
            System.out.println("Từ chối resize: " + e.getMessage());
        }
        circle.resize(10);
        System.out.println("Circle sau khi resize(10): " + circle.describe());
    }

    // ABSTRACTION: Shape định nghĩa "hợp đồng" mọi hình học phải có (area, perimeter),
    // ẩn đi cách tính cụ thể; describe() là method cụ thể dùng chung, không cần biết
    // implementation area()/perimeter() bên dưới ra sao
    abstract static class Shape {
        // ENCAPSULATION: field private, chỉ class này và class con (qua getter) mới truy cập được
        private final String name;

        Shape(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        abstract double area();

        abstract double perimeter();

        String describe() {
            return getName() + ": area=" + round(area()) + ", perimeter=" + round(perimeter());
        }

        private double round(double value) {
            return Math.round(value * 100) / 100.0;
        }
    }

    // INHERITANCE: Circle kế thừa field/method chung (name, describe()) từ Shape,
    // chỉ cần tự triển khai phần riêng (area, perimeter, resize)
    static class Circle extends Shape {
        // ENCAPSULATION: radius là private, muốn đổi phải qua resize() có validate,
        // không cho gán trực tiếp giá trị âm vô lý
        private double radius;

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

        void resize(double newRadius) {
            if (newRadius <= 0) {
                throw new IllegalArgumentException("radius phải dương");
            }
            this.radius = newRadius;
        }
    }

    static class Rectangle extends Shape {
        protected final double width;
        protected final double height;

        Rectangle(double width, double height) {
            this("Rectangle", width, height);
        }

        // constructor protected: cho phép class con (như Square) đặt tên riêng thay vì luôn là "Rectangle"
        protected Rectangle(String name, double width, double height) {
            super(name);
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

    // INHERITANCE (kế thừa nhiều tầng): Square kế thừa từ Rectangle chứ không phải từ Shape trực tiếp,
    // tái sử dụng luôn area()/perimeter() đã có của Rectangle thông qua super(name, side, side)
    static class Square extends Rectangle {
        Square(double side) {
            super("Square", side, side);
        }
    }
}
