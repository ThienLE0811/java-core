package com.javacore.practice.javacore33.oop.maytinh;

/**
 * Lỗi cú pháp hoặc toán tử không được hỗ trợ.
 *
 * <p><b>Vì sao class này dùng static factory method thay vì constructor công khai?</b>
 * Hai class anh em của nó ({@link DivisionByZeroException}, {@link NegativeSqrtException}) mỗi
 * cái chỉ có đúng một tình huống, nên một constructor là đủ: đưa dữ liệu vào, message suy ra
 * được. Class này thì gom năm tình huống khác hẳn nhau - biểu thức rỗng, cú pháp sai, cú pháp
 * {@code sqrt} sai, toán tử lạ, toán hạng không phải số. Nếu làm theo kiểu constructor, hoặc là
 * phải để {@link Calculator} tự viết message rồi chuyền vào (message cùng loại lỗi sẽ mỗi chỗ
 * một kiểu), hoặc là phải thêm một tham số "mã lỗi" rồi {@code switch} - cả hai đều tệ hơn.
 *
 * <p>Static factory giải quyết gọn: mỗi tình huống một method có <em>tên</em>, message nằm trọn
 * trong class này, và chỗ gọi đọc như tiếng Anh - {@code throw InvalidOperationException.unsupportedOperator(expr, "&")}.
 * Constructor để {@code private} nên không ai dựng được một thể hiện "không rõ lý do".
 */
public class InvalidOperationException extends CalculatorException {

    /** Toán tử liên quan tới lỗi; {@code null} khi lỗi không gắn với toán tử nào. */
    private final String operator;

    private InvalidOperationException(String message, String expression, String operator, Throwable cause) {
        super(message, expression, cause);
        this.operator = operator;
    }

    /**
     * Biểu thức {@code null}, rỗng hoặc chỉ gồm khoảng trắng.
     *
     * @param expression biểu thức gốc
     * @return exception đã dựng sẵn message
     */
    public static InvalidOperationException emptyExpression(String expression) {
        return new InvalidOperationException(
                "Biểu thức không được để trống.", expression, null, null);
    }

    /**
     * Biểu thức không khớp bất kỳ định dạng nào được hỗ trợ.
     *
     * @param expression biểu thức gốc
     * @return exception đã dựng sẵn message
     */
    public static InvalidOperationException invalidSyntax(String expression) {
        return new InvalidOperationException(
                "Cú pháp biểu thức không hợp lệ: '" + expression
                        + "'. Ví dụ hợp lệ: '10 / 2' hoặc 'sqrt 16'.",
                expression, null, null);
    }

    /**
     * Biểu thức bắt đầu bằng {@code sqrt} nhưng phần còn lại sai định dạng.
     *
     * @param expression biểu thức gốc
     * @return exception đã dựng sẵn message
     */
    public static InvalidOperationException invalidSqrtSyntax(String expression) {
        return new InvalidOperationException(
                "Cú pháp căn bậc hai không đúng: '" + expression
                        + "'. Ví dụ hợp lệ: 'sqrt 16' hoặc 'sqrt(16)'.",
                expression, "sqrt", null);
    }

    /**
     * Toán tử nằm ngoài tập được hỗ trợ.
     *
     * @param expression biểu thức gốc
     * @param operator   toán tử không hợp lệ
     * @return exception đã dựng sẵn message
     */
    public static InvalidOperationException unsupportedOperator(String expression, String operator) {
        return new InvalidOperationException(
                "Toán tử không được hỗ trợ: '" + operator + "'. Chỉ hỗ trợ + - * / % ^ và sqrt.",
                expression, operator, null);
    }

    /**
     * Toán hạng không phải là số hợp lệ.
     *
     * <p>Đây là chỗ duy nhất trong bài có {@code cause}: {@link Double#parseDouble(String)} ném
     * {@link NumberFormatException}, ta bọc nó lại thành lỗi nghiệp vụ nhưng <em>giữ nguyên</em>
     * exception gốc làm {@code cause}. Bắt rồi nuốt mất {@code cause} là một trong bốn
     * anti-pattern kinh điển khi xử lý ngoại lệ.
     *
     * @param expression biểu thức gốc
     * @param operator   toán tử của biểu thức, có thể {@code null}
     * @param token      chuỗi không parse được thành số
     * @param cause      exception gốc từ {@code Double.parseDouble}
     * @return exception đã dựng sẵn message và giữ nguyên nguyên nhân gốc
     */
    public static InvalidOperationException operandNotANumber(
            String expression, String operator, String token, NumberFormatException cause) {
        return new InvalidOperationException(
                "Toán hạng '" + token + "' không phải là số hợp lệ trong biểu thức '" + expression + "'.",
                expression, operator, cause);
    }

    /**
     * @return toán tử liên quan tới lỗi, hoặc {@code null} nếu lỗi không gắn với toán tử nào
     */
    public String getOperator() {
        return operator;
    }
}
