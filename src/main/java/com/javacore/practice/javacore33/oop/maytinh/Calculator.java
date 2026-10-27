package com.javacore.practice.javacore33.oop.maytinh;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lớp Máy tính hỗ trợ các phép toán cơ bản, căn bậc hai và phân tích chuỗi biểu thức.
 *
 * <p>Các tính năng chính:
 * <ul>
 *   <li>Phép toán nhị phân: cộng ({@code +}), trừ ({@code -}), nhân ({@code *}), chia ({@code /}),
 *       chia lấy dư ({@code %}), lũy thừa ({@code ^}).</li>
 *   <li>Phép toán đơn phân: căn bậc hai ({@code sqrt}).</li>
 *   <li>Phân tích và thực thi biểu thức từ chuỗi văn bản (ví dụ {@code "10 / 0"}, {@code "sqrt 16"}).</li>
 * </ul>
 *
 * <p><b>Hệ thống ngoại lệ tự định nghĩa:</b>
 * <ul>
 *   <li>{@link DivisionByZeroException}: khi chia hoặc lấy dư cho 0.</li>
 *   <li>{@link NegativeSqrtException}: khi lấy căn bậc hai số âm trong tập số thực.</li>
 *   <li>{@link InvalidOperationException}: khi toán tử không hợp lệ hoặc cú pháp sai định dạng.</li>
 * </ul>
 */
public class Calculator {

    /**
     * Một số thực: bắt buộc có ít nhất một chữ số.
     *
     * <p>Dùng {@code \d+} chứ không phải {@code \d*}: với {@code \d*} thì chuỗi rỗng cũng là
     * một "số" hợp lệ, và {@code "10/4"} sẽ bị cắt thành toán hạng {@code "10"}, toán tử
     * {@code "/4"}, toán hạng {@code ""}. Nhánh {@code |\.\d+} để vẫn nhận dạng {@code ".5"}.
     */
    private static final String NUMBER = "[-+]?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][-+]?\\d+)?";

    /** Regex nhận diện biểu thức sqrt dạng: sqrt 16 hoặc sqrt(16). */
    private static final Pattern SQRT_PATTERN =
            Pattern.compile("^sqrt\\s*\\(?\\s*(" + NUMBER + ")\\s*\\)?$", Pattern.CASE_INSENSITIVE);

    /**
     * Regex nhận diện biểu thức nhị phân dạng: {@code <số> <toán tử> <số>}.
     *
     * <p>Toán tử dùng {@code \S+?} (lazy) chứ không phải {@code \S+} (greedy), nếu không nó sẽ
     * nuốt luôn toán hạng bên phải khi biểu thức viết liền không có khoảng trắng.
     */
    private static final Pattern BINARY_PATTERN =
            Pattern.compile("^(" + NUMBER + ")\\s*(\\S+?)\\s*(" + NUMBER + ")$");

    /**
     * Khởi tạo một đối tượng Calculator.
     */
    public Calculator() {
    }

    /**
     * Thực hiện phép cộng hai số thực.
     *
     * @param a số hạng thứ nhất
     * @param b số hạng thứ hai
     * @return tổng của {@code a} và {@code b}
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * Thực hiện phép trừ hai số thực.
     *
     * @param a số bị trừ
     * @param b số trừ
     * @return hiệu của {@code a} và {@code b}
     */
    public double subtract(double a, double b) {
        return a - b;
    }

    /**
     * Thực hiện phép nhân hai số thực.
     *
     * @param a thừa số thứ nhất
     * @param b thừa số thứ hai
     * @return tích của {@code a} và {@code b}
     */
    public double multiply(double a, double b) {
        return a * b;
    }

    /**
     * Thực hiện phép chia hai số thực.
     *
     * <p>Trong Java, {@code a / 0.0} sẽ trả về {@link Double#POSITIVE_INFINITY} hoặc
     * {@link Double#NEGATIVE_INFINITY}. Phương thức này chủ động kiểm tra và ném
     * {@link DivisionByZeroException} khi mẫu số bằng 0.
     *
     * @param a số bị chia
     * @param b số chia
     * @return thương của {@code a} và {@code b}
     * @throws DivisionByZeroException nếu {@code b == 0}
     */
    public double divide(double a, double b) {
        if (isZero(b)) {
            throw new DivisionByZeroException(a, "/", b);
        }
        return a / b;
    }

    /**
     * Thực hiện phép chia lấy phần dư giữa hai số thực.
     *
     * @param a số bị chia
     * @param b số chia
     * @return phần dư của phép chia
     * @throws DivisionByZeroException nếu {@code b == 0}
     */
    public double modulo(double a, double b) {
        if (isZero(b)) {
            throw new DivisionByZeroException(a, "%", b);
        }
        return a % b;
    }

    /**
     * Thực hiện phép nâng lên lũy thừa.
     *
     * @param base     cơ số
     * @param exponent số mũ
     * @return {@code base ^ exponent}
     * @throws DivisionByZeroException nếu cơ số bằng 0 và số mũ âm (tương đương chia cho 0)
     */
    public double power(double base, double exponent) {
        if (isZero(base) && exponent < 0) {
            throw new DivisionByZeroException(base, "^", exponent);
        }
        return Math.pow(base, exponent);
    }

    /**
     * Thực hiện phép tính căn bậc hai trên tập số thực.
     *
     * <p>Trong Java chuẩn, {@link Math#sqrt(double)} trả về {@link Double#NaN} khi số âm.
     * Phương thức này chủ động kiểm tra và ném {@link NegativeSqrtException}.
     *
     * @param value số cần lấy căn
     * @return căn bậc hai của {@code value}
     * @throws NegativeSqrtException nếu {@code value < 0}
     */
    public double sqrt(double value) {
        if (value < 0) {
            throw new NegativeSqrtException(value);
        }
        return Math.sqrt(value);
    }

    /**
     * Phân tích và thực hiện tính toán giá trị của một chuỗi biểu thức.
     *
     * <p>Các dạng biểu thức được hỗ trợ:
     * <ul>
     *   <li>Phép toán nhị phân: {@code "<số> <toán tử> <số>"} (ví dụ: {@code "10 + 5"}, {@code "10 / 0"}).
     *       Toán tử gồm {@code +}, {@code -}, {@code *}, {@code /}, {@code %}, {@code ^}.</li>
     *   <li>Phép căn bậc hai: {@code "sqrt <số>"} hoặc {@code "sqrt(<số>)"} (ví dụ: {@code "sqrt 16"}, {@code "sqrt -4"}).</li>
     * </ul>
     *
     * @param expression chuỗi biểu thức cần tính
     * @return kết quả dạng {@code double}
     * @throws CalculatorException nếu biểu thức rỗng, sai cú pháp, toán tử không hỗ trợ,
     *                             chia cho 0 hoặc căn bậc hai số âm
     */
    public double calculate(String expression) {
        if (expression == null || expression.isBlank()) {
            throw InvalidOperationException.emptyExpression(expression);
        }

        String trimmed = expression.trim();

        // 1. Kiểm tra phép căn bậc hai (unary sqrt)
        if (trimmed.toLowerCase().startsWith("sqrt")) {
            return evaluateSqrt(trimmed, expression);
        }

        // 2. Kiểm tra biểu thức nhị phân có khoảng trắng (tokenized)
        String[] tokens = trimmed.split("\\s+");
        if (tokens.length == 3) {
            return evaluateBinary(tokens[0], tokens[1], tokens[2], expression);
        }

        // 3. Dự phòng cho biểu thức nhị phân viết liền (ví dụ "10/2" hoặc "10+5")
        Matcher binaryMatcher = BINARY_PATTERN.matcher(trimmed);
        if (binaryMatcher.matches()) {
            return evaluateBinary(binaryMatcher.group(1), binaryMatcher.group(2), binaryMatcher.group(3), expression);
        }

        // 4. Nếu không khớp bất kỳ mẫu chuẩn nào
        throw InvalidOperationException.invalidSyntax(expression);
    }

    /**
     * Đánh giá biểu thức căn bậc hai.
     */
    private double evaluateSqrt(String trimmed, String rawExpression) {
        Matcher matcher = SQRT_PATTERN.matcher(trimmed);
        if (!matcher.matches()) {
            throw InvalidOperationException.invalidSqrtSyntax(rawExpression);
        }

        String operandStr = matcher.group(1);
        double value = parseOperand(operandStr, rawExpression, "sqrt");
        return sqrt(value);
    }

    /**
     * Đánh giá biểu thức nhị phân với hai toán hạng và một toán tử.
     */
    private double evaluateBinary(String leftStr, String operator, String rightStr, String rawExpression) {
        double left = parseOperand(leftStr, rawExpression, operator);
        double right = parseOperand(rightStr, rawExpression, operator);

        return switch (operator) {
            case "+" -> add(left, right);
            case "-" -> subtract(left, right);
            case "*" -> multiply(left, right);
            case "/" -> divide(left, right);
            case "%" -> modulo(left, right);
            case "^" -> power(left, right);
            default -> throw InvalidOperationException.unsupportedOperator(rawExpression, operator);
        };
    }

    /**
     * Chuyển đổi chuỗi toán hạng sang kiểu {@code double}.
     */
    private double parseOperand(String token, String expression, String operation) {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw InvalidOperationException.operandNotANumber(expression, operation, token, e);
        }
    }

    /**
     * Kiểm tra một số thực có bằng 0 hay không (tính cả 0.0 và -0.0).
     */
    private boolean isZero(double value) {
        return Double.compare(value, 0.0) == 0 || Double.compare(value, -0.0) == 0;
    }
}
