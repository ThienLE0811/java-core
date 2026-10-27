package com.javacore.practice.javacore33.oop.maytinh;

/**
 * Lỗi chia cho 0, dùng chung cho ba phép toán {@code /}, {@code %} và {@code ^}.
 *
 * <p><b>Điểm mấu chốt của bài 13:</b> với kiểu {@code double}, chia cho 0 <em>không</em> ném
 * {@link ArithmeticException}. Java chỉ ném khi cả hai toán hạng là số nguyên:
 * <pre>
 * 10 / 0      →  ném ArithmeticException: / by zero   (phép chia nguyên)
 * 10.0 / 0    →  Infinity                             (im lặng!)
 * -10.0 / 0   →  -Infinity                            (im lặng!)
 * 0.0 / 0     →  NaN                                  (im lặng!)
 * </pre>
 * Vì {@link Calculator} dùng {@code double} (để {@code 10 / 4} ra {@code 2.5} chứ không phải
 * {@code 2}), nó phải <em>tự kiểm tra mẫu số rồi tự ném</em>. Đây không phải trường hợp bắt lại
 * lỗi của JVM rồi bọc lại - JVM không hề báo lỗi nào.
 *
 * <p>Trường hợp {@code ^}: {@code 0 ^ -2} nghĩa là {@code 1 / (0 * 0)}, nên cũng là chia cho 0.
 * {@link Math#pow(double, double)} trả về {@code Infinity} trong tình huống này.
 */
public class DivisionByZeroException extends CalculatorException {

    /** Toán hạng bên trái, ví dụ số bị chia trong {@code 10 / 0}. */
    private final double leftOperand;

    /** Toán hạng bên phải: {@code 0} với {@code /} và {@code %}, số mũ âm với {@code ^}. */
    private final double rightOperand;

    /** Toán tử đã gây ra lỗi: {@code "/"}, {@code "%"} hoặc {@code "^"}. */
    private final String operator;

    /**
     * @param leftOperand  toán hạng bên trái
     * @param operator     toán tử gây lỗi
     * @param rightOperand toán hạng bên phải
     */
    public DivisionByZeroException(double leftOperand, String operator, double rightOperand) {
        super(taoMessage(leftOperand, operator, rightOperand),
                dinhDangSo(leftOperand) + " " + operator + " " + dinhDangSo(rightOperand));
        this.leftOperand = leftOperand;
        this.operator = operator;
        this.rightOperand = rightOperand;
    }

    /**
     * @return toán hạng bên trái, ví dụ {@code 10} trong {@code "10 / 0"}
     */
    public double getLeftOperand() {
        return leftOperand;
    }

    /**
     * @return toán hạng bên phải
     */
    public double getRightOperand() {
        return rightOperand;
    }

    /**
     * @return toán tử gây ra lỗi
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Dựng message theo từng toán tử.
     *
     * <p>{@code static} vì nó được gọi bên trong lời gọi {@code super(...)}, lúc đó object còn
     * chưa tồn tại nên không thể dùng method của instance. Đây là một ràng buộc của Java mà
     * TypeScript không có.
     */
    private static String taoMessage(double leftOperand, String operator, double rightOperand) {
        String trai = dinhDangSo(leftOperand);
        return switch (operator) {
            case "/" -> "Không thể chia " + trai + " cho 0.";
            case "%" -> "Không thể lấy phần dư của " + trai + " khi chia cho 0.";
            case "^" -> "Không thể tính " + trai + " ^ " + dinhDangSo(rightOperand)
                    + ": lũy thừa âm của 0 tương đương chia cho 0.";
            default -> "Phép toán '" + operator + "' trên " + trai + " dẫn tới chia cho 0.";
        };
    }
}
