package com.javacore.t05_inheritance_abstraction_interface_polymorphism;

public class InterfaceDemo {

    public static void main(String[] args) {
        Duck duck = new Duck();
        duck.fly();
        duck.swim();
        duck.land(); // Duck tự override land() để giải quyết xung đột default method từ 2 interface

        System.out.println();
        // Field trong interface luôn ngầm định là public static final -> đây thực chất là hằng số dùng chung
        System.out.println("Flyable.MAX_ALTITUDE = " + Flyable.MAX_ALTITUDE);

        // static method trong interface: gọi qua tên interface, giống static method trong class
        Flyable.printInfo();

        // Một class có thể implements NHIỀU interface cùng lúc (khác với extends chỉ được 1 class cha)
        System.out.println("Duck vừa là Flyable vừa là Swimmable: "
                + (duck instanceof Flyable) + ", " + (duck instanceof Swimmable));

        System.out.println();
        // Functional interface: interface chỉ có ĐÚNG 1 abstract method -> có thể gán bằng lambda
        // (Runnable ở MethodsDemo.java của t03 cũng là 1 functional interface có sẵn trong JDK)
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;
        System.out.println("add.calculate(3, 4) = " + add.calculate(3, 4));
        System.out.println("multiply.calculate(3, 4) = " + multiply.calculate(3, 4));
    }

    // interface: hợp đồng (contract) về HÀNH VI, không quan tâm cài đặt bên trong ra sao,
    // mọi method khai báo không có thân hàm đều ngầm định "public abstract"
    interface Flyable {
        int MAX_ALTITUDE = 10_000; // ngầm định: public static final int MAX_ALTITUDE

        void fly(); // ngầm định: public abstract void fly();

        // default method (Java 8+): interface có thể cung cấp SẴN cài đặt, class implement không bắt buộc override
        default void land() {
            System.out.println("Flyable: hạ cánh xuống đất");
        }

        // static method trong interface: thuộc về interface, không thuộc về object nào implement nó
        static void printInfo() {
            System.out.println("Flyable.printInfo(): độ cao tối đa " + MAX_ALTITUDE);
        }
    }

    interface Swimmable {
        void swim();

        default void land() {
            System.out.println("Swimmable: bơi vào bờ");
        }
    }

    // Duck implements CẢ 2 interface -> đây là cách Java "mô phỏng" multiple inheritance,
    // vì class thường chỉ extends được 1 class cha duy nhất
    static class Duck implements Flyable, Swimmable {
        @Override
        public void fly() {
            System.out.println("Duck bay là là");
        }

        @Override
        public void swim() {
            System.out.println("Duck bơi trên mặt nước");
        }

        // Xung đột: cả Flyable và Swimmable đều có default method land() -> compiler BẮT BUỘC
        // Duck phải tự override, không được để trống (nếu không sẽ lỗi compile "inherits unrelated defaults")
        @Override
        public void land() {
            Flyable.super.land();   // gọi tường minh default method của Flyable
            Swimmable.super.land(); // gọi tường minh default method của Swimmable
            System.out.println("Duck: kết hợp cả 2 cách hạ cánh");
        }
    }

    // @FunctionalInterface chỉ mang tính khai báo ý định: nếu vô tình thêm abstract method thứ 2,
    // compiler sẽ báo lỗi ngay thay vì để runtime mới phát hiện
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }
}
