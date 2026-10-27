package com.javacore.practice.javacore33.oop.maytinh;

import java.util.List;

/**
 * Demo bài 13 - Máy tính với các custom exception: {@link DivisionByZeroException},
 * {@link NegativeSqrtException}, {@link InvalidOperationException}.
 *
 * <p>Demo này minh họa:
 * <ul>
 *   <li>Xử lý biểu thức chuỗi: từ các phép tính đúng đến từng kịch bản ngoại lệ cụ thể.</li>
 *   <li>Cơ chế bắt ngoại lệ phân cấp (multi-catch hoặc catch từ cụ thể đến tổng quát):
 *       bắt {@link DivisionByZeroException}, {@link NegativeSqrtException},
 *       {@link InvalidOperationException} và lớp cơ sở {@link CalculatorException}.</li>
 *   <li>In thông báo lỗi rõ ràng, mang tính ngữ cảnh (contextual failure-capture information).</li>
 * </ul>
 */
public final class MayTinhDemo {

    private MayTinhDemo() {
        // Utility class
    }

    /**
     * Điểm chạy của chương trình demo.
     *
     * @param args tham số dòng lệnh (nếu có biểu thức truyền vào sẽ ưu tiên tính biểu thức đó)
     */
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        System.out.println("===============================================================");
        System.out.println("  BÀI 13: MÁY TÍNH & HỆ THỐNG CUSTOM EXCEPTION TRONG JAVA");
        System.out.println("===============================================================\n");

        if (args != null && args.length > 0) {
            String expr = String.join(" ", args);
            System.out.println("Tính biểu thức từ tham số dòng lệnh:");
            xuLyBieuThuc(calculator, expr);
            return;
        }

        // Danh sách các ca kiểm thử mẫu bao gồm thành công và tất cả các loại ngoại lệ
        List<String> bieuThucMau = List.of(
                // 1. Các biểu thức hợp lệ
                "10 + 5.5",
                "20 - 8",
                "4.5 * 2",
                "10 / 4",
                "10 % 3",
                "2 ^ 8",
                "sqrt 25",
                "sqrt(144)",

                // 2. Ngoại lệ chia cho 0: DivisionByZeroException
                "10 / 0",
                "15 % 0",
                "0 ^ -2",

                // 3. Ngoại lệ căn bậc hai số âm: NegativeSqrtException
                "sqrt -4",
                "sqrt(-25)",

                // 4. Ngoại lệ toán tử không hỗ trợ: InvalidOperationException
                "10 & 2",
                "8 @ 3",

                // 5. Ngoại lệ cú pháp hoặc định dạng số: InvalidOperationException
                "abc + 5",
                "10 +",
                ""
        );

        int stt = 1;
        for (String bieuThuc : bieuThucMau) {
            System.out.printf("[%02d] Biểu thức: \"%s\"%n", stt++, bieuThuc);
            xuLyBieuThuc(calculator, bieuThuc);
            System.out.println("---------------------------------------------------------------");
        }
    }

    /**
     * Xử lý tính toán một biểu thức và minh họa bắt các exception cụ thể.
     *
     * <p>Thứ tự các khối {@code catch} đi từ ngoại lệ con cụ thể đến ngoại lệ cha tổng quát:
     * <ol>
     *   <li>{@link DivisionByZeroException}: xử lý lỗi chia cho 0.</li>
     *   <li>{@link NegativeSqrtException}: xử lý lỗi căn bậc hai số âm.</li>
     *   <li>{@link InvalidOperationException}: xử lý lỗi toán tử không hỗ trợ hoặc sai cú pháp.</li>
     *   <li>{@link CalculatorException}: bắt mọi ngoại lệ tính toán còn lại nếu có.</li>
     * </ol>
     *
     * @param calculator đối tượng máy tính
     * @param expression chuỗi biểu thức cần tính
     */
    public static void xuLyBieuThuc(Calculator calculator, String expression) {
        try {
            double ketQua = calculator.calculate(expression);
            System.out.printf("     => Kết quả: %.4f%n", ketQua);
        } catch (DivisionByZeroException e) {
            System.err.printf("     [LỖI CHIA CHO 0]: %s (Toán hạng trái: %.2f)%n",
                    e.getMessage(), e.getLeftOperand());
        } catch (NegativeSqrtException e) {
            System.err.printf("     [LỖI CĂN BẬC HAI SỐ ÂM]: %s (Giá trị âm: %.2f)%n",
                    e.getMessage(), e.getNegativeValue());
        } catch (InvalidOperationException e) {
            System.err.printf("     [LỖI THAO TÁC / CÚ PHÁP]: %s%s%n",
                    e.getMessage(),
                    e.getOperator() != null ? " [Toán tử: " + e.getOperator() + "]" : "");
        } catch (CalculatorException e) {
            System.err.printf("     [LỖI TÍNH TOÁN CHUNG]: %s%n", e.getMessage());
        }
    }
}
