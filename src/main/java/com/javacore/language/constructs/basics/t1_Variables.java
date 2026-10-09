package com.javacore.language.constructs.basics;

public class t1_Variables {

    public static int test = 1;

    int test2;

    void runTest(String firstName, String lastName) {
        String fullName = firstName + lastName;

        IO.println(fullName);
    }
}

/**
 * Nội dung thật của bài này, theo trang gốc:
 * <p>
 * 1. Các loại biến trong Java (4 loại)
 * <p>
 * - Instance variable — field khai báo không có static, mỗi object có bản sao riêng.
 * - Class variable — field khai báo có static, dùng chung cho cả class, không phụ thuộc object nào.
 * - Local variable — khai báo bên trong method, chỉ tồn tại/thấy được trong method đó.
 * - Parameter — biến nằm trong method signature (tham số truyền vào).
 * 2. Quy tắc đặt tên identifier
 * <p>
 * - Phân biệt hoa/thường (case-sensitive) --- name và Name là 2 biến khác nhau..
 * - Ký tự đầu phải là chữ cái, $, hoặc _ — không được bắt đầu bằng số.
 * - Không chứa khoảng trắng; ký tự sau có thể là chữ, số, $, _.
 * - Nên dùng từ đầy đủ, tránh viết tắt khó hiểu.
 * - Convention camelCase cho biến/method thường: gearRatio, currentGear.
 * - Convention UPPER_SNAKE_CASE cho hằng số (static final): NUM_GEARS.
 *
 *
 */