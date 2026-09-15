package com.javacore.fundamentals.t02_scope_operators_control_flow;

public class OperatorsDemo {

    public static void main(String[] args) {
        // Toán tử số học (arithmetic)
        int a = 17;
        int b = 5;
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (a / b)); // chia nguyên: 3, không phải 3.4
        System.out.println("a % b = " + (a % b)); // phần dư: 2

        // Toán tử tăng/giảm: hậu tố (postfix) vs tiền tố (prefix)
        int x = 5;
        System.out.println("x++ (dùng giá trị cũ trước, sau đó tăng): " + (x++)); // in 5, x thành 6
        System.out.println("x sau x++: " + x);
        System.out.println("++x (tăng trước, dùng giá trị mới): " + (++x)); // x thành 7, in 7

        // Toán tử quan hệ (relational)
        System.out.println("a > b: " + (a > b));
        System.out.println("a == b: " + (a == b));
        System.out.println("a != b: " + (a != b));

        // Toán tử logic: && và || có short-circuit (đoản mạch), & và | thì không
        boolean result = isPositive(5) && isEven(divideByZeroRisky(5)); // short-circuit tránh lỗi nếu vế trái false
        System.out.println("Short-circuit && : " + result);

        // & và | luôn đánh giá cả hai vế, kể cả khi vế trái đã đủ quyết định kết quả
        boolean sideEffectDemo = isPositive(-1) & isEven(4); // vẫn gọi isEven dù isPositive đã false
        System.out.println("Non-short-circuit & : " + sideEffectDemo);

        // Toán tử bitwise (thao tác trên từng bit)
        int m = 6;  // 0110
        int n = 3;  // 0011
        System.out.println("m & n (AND): " + (m & n));   // 0010 = 2
        System.out.println("m | n (OR): " + (m | n));    // 0111 = 7
        System.out.println("m ^ n (XOR): " + (m ^ n));   // 0101 = 5
        System.out.println("~m (NOT): " + (~m));         // -7
        System.out.println("m << 1 (shift left): " + (m << 1));  // 12
        System.out.println("m >> 1 (shift right): " + (m >> 1)); // 3

        // Toán tử gán (assignment) và gán kết hợp (compound assignment)
        int counter = 10;
        counter += 5; // counter = counter + 5
        counter -= 2;
        counter *= 3;
        counter /= 2;
        System.out.println("counter sau các phép gán kết hợp: " + counter);

        // Toán tử ba ngôi (ternary): rút gọn if-else trả về giá trị
        int age = 20;
        String category = (age >= 18) ? "Người lớn" : "Trẻ em";
        System.out.println("category: " + category);

        // Toán tử instanceof: kiểm tra kiểu của object tại runtime
        Object value = "Hello";
        if (value instanceof String text) { // pattern matching for instanceof (Java 16+)
            System.out.println("value là String với độ dài: " + text.length());
        }
    }

    private static boolean isPositive(int number) {
        return number > 0;
    }

    private static boolean isEven(int number) {
        return number % 2 == 0;
    }

    private static int divideByZeroRisky(int number) {
        return number; // giả lập một phép tính có thể tốn kém hoặc rủi ro, minh họa short-circuit tránh gọi nếu không cần
    }
}
