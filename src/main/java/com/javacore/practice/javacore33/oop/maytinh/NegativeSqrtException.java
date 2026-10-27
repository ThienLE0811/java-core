package com.javacore.practice.javacore33.oop.maytinh;

/**
 * Lỗi lấy căn bậc hai của một số âm trong tập số thực.
 *
 * <p><b>Điểm mấu chốt của bài 13:</b> {@link Math#sqrt(double)} <em>không ném gì cả</em> khi
 * nhận số âm - nó trả về {@link Double#NaN} và chương trình chạy tiếp như không có chuyện gì.
 * Tệ hơn, {@code NaN} lan ra mọi phép tính sau đó ({@code NaN + 1} vẫn là {@code NaN}), nên lỗi
 * chỉ lộ ra ở một nơi rất xa chỗ phát sinh. Exception này tồn tại để biến một thất bại im lặng
 * thành một thất bại ồn ào, ngay tại chỗ.
 *
 * <pre>
 * Math.sqrt(-1)  →  NaN      (im lặng, không ai biết)
 * calc.sqrt(-1)  →  ném NegativeSqrtException
 * </pre>
 */
public class NegativeSqrtException extends CalculatorException {

    /** Giá trị âm đã bị từ chối. */
    private final double negativeValue;

    /**
     * @param negativeValue giá trị âm được truyền cho phép căn bậc hai
     */
    public NegativeSqrtException(double negativeValue) {
        super("Không thể lấy căn bậc hai của số âm " + dinhDangSo(negativeValue)
                        + " trong tập số thực.",
                "sqrt " + dinhDangSo(negativeValue));
        this.negativeValue = negativeValue;
    }

    /**
     * @return giá trị âm đã gây ra lỗi
     */
    public double getNegativeValue() {
        return negativeValue;
    }
}
