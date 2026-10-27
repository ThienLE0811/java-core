package com.javacore.practice.javacore33.oop.maytinh;

/**
 * Bài 13 - lớp cơ sở cho mọi lỗi nghiệp vụ của {@link Calculator}.
 *
 * <p><b>Vì sao {@code extends RuntimeException} (unchecked) chứ không phải {@code Exception}?</b>
 * Nguyên tắc vàng là "caller có phục hồi được không". Ở đây caller <em>có</em> phản ứng được
 * (in thông báo, hỏi lại người dùng), nên trên lý thuyết checked cũng hợp lệ. Ba lý do thực tế
 * nghiêng về unchecked:
 * <ol>
 *   <li>Đây là <em>lỗi dữ liệu đầu vào</em>. Trong một ứng dụng Spring, đúng loại lỗi này sẽ
 *       được {@code @RestControllerAdvice} bắt tập trung và map sang HTTP 400. Không tầng nào
 *       ở giữa cần biết, nên không tầng nào nên bị ép khai báo {@code throws}.</li>
 *   <li>Checked exception không đi qua được lambda: {@code list.forEach(e -> calc.calculate(e))}
 *       sẽ không biên dịch nếu {@code calculate} ném checked, vì {@code Consumer} không khai báo
 *       {@code throws}.</li>
 *   <li>Hệ sinh thái Java hiện đại (Spring, Hibernate) đã chuyển gần như toàn bộ sang unchecked.</li>
 * </ol>
 *
 * <p>Hệ quả của lựa chọn này: chữ ký method trong {@link Calculator} <b>không</b> có mệnh đề
 * {@code throws}, thông tin đó nằm ở thẻ {@code @throws} của Javadoc. Đây là quy ước chuẩn cho
 * unchecked exception - mệnh đề {@code throws} dành cho thứ compiler cưỡng chế.
 *
 * <p><b>Vì sao {@code abstract}?</b> Class này không bao giờ được {@code new} trực tiếp; nó tồn
 * tại để gom nhóm, cho phép người gọi viết {@code catch (CalculatorException e)} bắt cả ba loại
 * lỗi máy tính mà <em>không</em> phải dùng {@code catch (Exception e)} - thứ sẽ nuốt luôn cả
 * {@link NullPointerException} do lập trình viên viết sai. Để {@code abstract} là tuyên bố ý đồ
 * đó bằng compiler thay vì bằng lời bình luận.
 *
 * <p>Cây kế thừa:
 * <pre>
 * RuntimeException
 *   └── CalculatorException          (abstract, giữ biểu thức gốc)
 *         ├── DivisionByZeroException
 *         ├── NegativeSqrtException
 *         └── InvalidOperationException
 * </pre>
 *
 * @see <a href="https://dev.java/learn/language/annotations-exceptions/exceptions/unchecked-exception-controversy/">dev.java - The Unchecked Exceptions Controversy</a>
 */
public abstract class CalculatorException extends RuntimeException {

    /**
     * Biểu thức gốc sinh ra lỗi, ví dụ {@code "10 / 0"}.
     *
     * <p>{@code final} vì exception là object bất biến: đã ném ra rồi thì không ai được sửa
     * dữ liệu hiện trường.
     */
    private final String expression;

    /**
     * @param message   thông báo lỗi cho người đọc log
     * @param expression biểu thức gốc sinh ra lỗi
     */
    protected CalculatorException(String message, String expression) {
        this(message, expression, null);
    }

    /**
     * Constructor có {@code cause} - dùng khi lỗi này <em>bọc</em> một exception khác.
     *
     * <p>Luôn chuyền {@code cause} lên {@code super}, đừng nuốt nó. Mất {@code cause} là mất
     * stack trace của nguyên nhân gốc, và đó là thứ đắt giá nhất lúc 2 giờ sáng đọc log.
     *
     * @param message   thông báo lỗi cho người đọc log
     * @param expression biểu thức gốc sinh ra lỗi
     * @param cause     exception gốc bị bọc lại, có thể {@code null}
     */
    protected CalculatorException(String message, String expression, Throwable cause) {
        super(message, cause);
        this.expression = expression;
    }

    /**
     * Biểu thức gốc sinh ra lỗi.
     *
     * <p>Message là để <em>người</em> đọc; getter này là để <em>code</em> xử lý - ví dụ tầng
     * controller muốn trả về cho client biết chính xác input nào bị từ chối.
     *
     * @return biểu thức gốc, không bao giờ {@code null} với các lỗi hiện có
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Định dạng số cho message: bỏ phần {@code .0} thừa để {@code 10.0} hiện ra là {@code 10}.
     *
     * <p>Đặt ở class cha vì cả ba class con đều cần - đúng vai trò của kế thừa: cái gì chung
     * thì kéo lên.
     *
     * @param value số cần định dạng
     * @return chuỗi biểu diễn gọn của {@code value}
     */
    protected static String dinhDangSo(double value) {
        if (Double.isFinite(value) && value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
