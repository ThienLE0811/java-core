package com.javacore.t03_string_array_methods;

public class MethodsDemo {

    public static void main(String[] args) {
        // Gọi method: instance method cần object, static method gọi qua tên class
        MethodsDemo demo = new MethodsDemo();
        demo.greet("An");

        System.out.println("sum(2, 3): " + sum(2, 3));

        // Method overloading: nhiều method cùng tên, khác số lượng/kiểu tham số
        // Java chọn đúng method dựa vào signature lúc COMPILE TIME (khác với JS không hỗ trợ overload thật)
        System.out.println("sum(2, 3): " + sum(2, 3));
        System.out.println("sum(2, 3, 4): " + sum(2, 3, 4));
        System.out.println("sum(2.5, 3.5): " + sum(2.5, 3.5));

        // Varargs: nhận số lượng tham số bất kỳ (0, 1, hoặc nhiều), thực chất là 1 mảng bên trong
        System.out.println("total(): " + total());
        System.out.println("total(1, 2, 3, 4): " + total(1, 2, 3, 4));

        // Pass by value: Java LUÔN truyền theo giá trị (kể cả với object, giá trị truyền đi là reference)
        int number = 10;
        changePrimitive(number);
        System.out.println("number sau changePrimitive (không đổi vì pass by value): " + number);

        int[] array = {1, 2, 3};
        changeArrayContent(array);
        System.out.println("array sau changeArrayContent (nội dung bị đổi vì cùng reference): "
                + java.util.Arrays.toString(array));

        reassignArray(array);
        System.out.println("array sau reassignArray (không đổi, vì chỉ đổi reference cục bộ trong method): "
                + java.util.Arrays.toString(array));

        // Method reference đến chính instance/static method (dùng nhiều với Stream/Lambda sau này)
        Runnable runnable = demo::sayHello;
        runnable.run();
    }

    // Instance method: cần tạo object mới gọi được
    private void greet(String name) {
        System.out.println("Xin chào, " + name + "!");
    }

    private void sayHello() {
        System.out.println("Hello từ method reference!");
    }

    // Static method: gọi trực tiếp qua tên class, không cần tạo object
    private static int sum(int a, int b) {
        return a + b;
    }

    // Overload: cùng tên "sum" nhưng khác số lượng tham số
    private static int sum(int a, int b, int c) {
        return a + b + c;
    }

    // Overload: cùng tên "sum" nhưng khác kiểu tham số
    private static double sum(double a, double b) {
        return a + b;
    }

    // Varargs: int... numbers thực chất là int[] numbers bên trong method
    private static int total(int... numbers) {
        int result = 0;
        for (int n : numbers) {
            result += n;
        }
        return result;
    }

    private static void changePrimitive(int value) {
        value = 999; // chỉ thay đổi bản copy cục bộ, không ảnh hưởng biến gốc bên ngoài
    }

    private static void changeArrayContent(int[] arr) {
        arr[0] = 999; // sửa nội dung thông qua reference được truyền vào -> ảnh hưởng mảng gốc
    }

    private static void reassignArray(int[] arr) {
        arr = new int[]{100, 200, 300}; // chỉ gán lại biến cục bộ arr trỏ tới mảng mới,
        // không ảnh hưởng biến gốc bên ngoài vì reference được truyền theo giá trị
    }
}
