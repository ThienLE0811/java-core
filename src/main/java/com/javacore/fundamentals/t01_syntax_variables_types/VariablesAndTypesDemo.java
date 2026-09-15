package com.javacore.fundamentals.t01_syntax_variables_types;

public class VariablesAndTypesDemo {

    public static void main(String[] args) {
        // Kiểu nguyên thủy (primitive types)
        byte smallNumber = 127;
        short mediumNumber = 32000;
        int number = 1_000_000;
        long bigNumber = 10_000_000_000L;
        float price = 19.99f;
        double distance = 384_400.5;
        char letter = 'A';
        boolean isActive = true;

        // Kiểu tham chiếu (reference type)
        String message = "Hello, Java!";

        System.out.println("byte: " + smallNumber);
        System.out.println("short: " + mediumNumber);
        System.out.println("int: " + number);
        System.out.println("long: " + bigNumber);
        System.out.println("float: " + price);
        System.out.println("double: " + distance);
        System.out.println("char: " + letter);
        System.out.println("boolean: " + isActive);
        System.out.println("String: " + message);

        // Ép kiểu ngầm định (widening) và tường minh (narrowing)
        int intValue = 100;
        double widenedValue = intValue;
        double doubleValue = 9.78;
        int narrowedValue = (int) doubleValue;

        System.out.println("Widening int -> double: " + widenedValue);
        System.out.println("Narrowing double -> int: " + narrowedValue);

        // var: suy luận kiểu (type inference), vẫn là kiểu tĩnh tại compile-time
        var inferredNumber = 42;
        var inferredText = "Java suy luận kiểu này là String";
        System.out.println(inferredNumber + " - " + inferredText);

        // Thêm ví dụ về reference type: array, class tự định nghĩa, null
        // (Wrapper class & autoboxing xem riêng ở WrapperClassesAndAutoboxingDemo)
        int[] scores = {90, 85, 100};
        System.out.println("Array (reference type): " + scores[0] + ", " + scores[1] + ", " + scores[2]);

        Point origin = new Point(0, 0);
        System.out.println("Class tự định nghĩa: " + origin);

        String nullableMessage = null; // reference type có thể null, primitive thì không
        System.out.println("Reference type có thể null: " + nullableMessage);
    }

    // Class tự định nghĩa cũng là một reference type
    static class Point {
        private final int x;
        private final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point(" + x + ", " + y + ")";
        }
    }
}
