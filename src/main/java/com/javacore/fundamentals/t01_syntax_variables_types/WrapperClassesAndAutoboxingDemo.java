package com.javacore.fundamentals.t01_syntax_variables_types;

import java.util.ArrayList;
import java.util.List;

public class WrapperClassesAndAutoboxingDemo {

    public static void main(String[] args) {
        // Mỗi kiểu nguyên thủy đều có một wrapper class (reference type) tương ứng
        // byte -> Byte, short -> Short, int -> Integer, long -> Long
        // float -> Float, double -> Double, char -> Character, boolean -> Boolean
        Integer boxedNumber = 10; // wrapper class của int
        System.out.println("Wrapper class Integer: " + boxedNumber);

        // Autoboxing: tự động chuyển primitive -> wrapper
        int primitiveValue = 42;
        Integer autoBoxed = primitiveValue; // Integer.valueOf(primitiveValue)
        System.out.println("Autoboxing int -> Integer: " + autoBoxed);

        // Unboxing: tự động chuyển wrapper -> primitive
        Integer wrapped = 100;
        int unboxed = wrapped; // wrapped.intValue()
        System.out.println("Unboxing Integer -> int: " + unboxed);

        // Autoboxing thường gặp khi dùng Collection, vì generic không nhận primitive
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);  // int 1 tự động autobox thành Integer
        numbers.add(2);
        int firstNumber = numbers.get(0); // Integer tự động unbox thành int
        System.out.println("Autoboxing trong List: " + numbers + ", unbox phần tử đầu: " + firstNumber);

        // Cẩn thận khi so sánh wrapper bằng == : Integer cache giá trị từ -128 đến 127
        Integer a = 100;
        Integer b = 100;
        System.out.println("a == b (trong cache -128..127): " + (a == b)); // true, do cache

        Integer c = 200;
        Integer d = 200;
        System.out.println("c == d (ngoài cache): " + (c == d)); // false, hai object khác nhau
        System.out.println("c.equals(d): " + c.equals(d)); // true, nên luôn dùng equals() để so sánh giá trị

        // Wrapper class còn cung cấp phương thức tiện ích để parse, chuyển đổi
        int parsedNumber = Integer.parseInt("123");
        String numberAsText = Integer.toString(456);
        System.out.println("parseInt: " + parsedNumber + ", toString: " + numberAsText);

        // Unboxing giá trị null sẽ ném NullPointerException
        Integer nullableValue = null;
        try {
            int result = nullableValue; // tự động unbox -> gọi nullableValue.intValue()
            System.out.println("Sẽ không tới đây: " + result);
        } catch (NullPointerException e) {
            System.out.println("Unbox giá trị null gây ra NullPointerException");
        }
    }
}
