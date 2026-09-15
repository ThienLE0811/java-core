package com.javacore.fundamentals.t01_syntax_variables_types;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalForMoneyDemo {

    public static void main(String[] args) {
        // Vấn đề của double: sai số do biểu diễn nhị phân
        double doubleSum = 0.1 + 0.2;
        System.out.println("double 0.1 + 0.2 = " + doubleSum); // 0.30000000000000004

        // Luôn khởi tạo BigDecimal từ String, không từ double
        BigDecimal fromDoubleConstructor = new BigDecimal(0.1); // lộ sai số của double
        BigDecimal fromString = new BigDecimal("0.1");           // chính xác
        BigDecimal fromValueOf = BigDecimal.valueOf(0.1);         // an toàn, dùng Double.toString() nội bộ
        System.out.println("new BigDecimal(0.1): " + fromDoubleConstructor);
        System.out.println("new BigDecimal(\"0.1\"): " + fromString);
        System.out.println("BigDecimal.valueOf(0.1): " + fromValueOf);

        // BigDecimal là immutable: mọi phép toán trả về object mới
        BigDecimal price = new BigDecimal("19.99");
        BigDecimal quantity = new BigDecimal("3");
        BigDecimal total = price.multiply(quantity);
        System.out.println("Tổng tiền (19.99 x 3): " + total);

        // Cộng, trừ tiền
        BigDecimal balance = new BigDecimal("100.00");
        BigDecimal deposit = new BigDecimal("50.50");
        BigDecimal withdraw = new BigDecimal("30.25");
        BigDecimal newBalance = balance.add(deposit).subtract(withdraw);
        System.out.println("Số dư sau giao dịch: " + newBalance);

        // divide() có thể ném ArithmeticException nếu kết quả là số vô hạn tuần hoàn
        // ví dụ: 10 / 3 = 3.3333... không thể divide() mặc định
        BigDecimal ten = new BigDecimal("10");
        BigDecimal three = new BigDecimal("3");
        try {
            ten.divide(three); // ném ArithmeticException: Non-terminating decimal expansion
        } catch (ArithmeticException e) {
            System.out.println("Lỗi khi chia không chỉ định scale: " + e.getMessage());
        }

        // Cách đúng: luôn chỉ định scale (số chữ số thập phân) và RoundingMode khi chia
        BigDecimal safeDivide = ten.divide(three, 2, RoundingMode.HALF_UP);
        System.out.println("10 / 3 làm tròn 2 chữ số: " + safeDivide);

        // setScale để làm tròn về số chữ số thập phân mong muốn (ví dụ tiền tệ luôn 2 số lẻ)
        BigDecimal rawAmount = new BigDecimal("19.995");
        BigDecimal roundedAmount = rawAmount.setScale(2, RoundingMode.HALF_UP);
        System.out.println("19.995 làm tròn 2 chữ số (HALF_UP): " + roundedAmount);

        // So sánh: equals() so cả scale, compareTo() chỉ so giá trị
        BigDecimal oneDotZero = new BigDecimal("1.0");
        BigDecimal oneDotZeroZero = new BigDecimal("1.00");
        System.out.println("1.0 equals 1.00: " + oneDotZero.equals(oneDotZeroZero));       // false, khác scale
        System.out.println("1.0 compareTo 1.00 == 0: " + (oneDotZero.compareTo(oneDotZeroZero) == 0)); // true, bằng giá trị
    }
}
