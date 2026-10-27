package com.javacore.practice.javacore33.oop.hinh;

/**
 * Tam giác xác định bởi độ dài ba cạnh.
 *
 * <p>Chọn ba cạnh thay vì đáy và chiều cao vì ở đây có một ràng buộc thật: ba số dương bất kỳ
 * không chắc dựng được thành tam giác. {@code (1, 2, 10)} là ba kích thước hoàn toàn hợp lệ
 * nhưng không khép lại thành hình nào cả - phải chặn ngay ở constructor, nếu không công thức
 * Heron sẽ trả {@code NaN} và con số đó lặng lẽ trôi vào {@link Hinh#tongDienTich(Hinh[])}
 * làm hỏng cả tổng mà không báo lỗi gì.
 */
public class HinhTamGiac extends Hinh {

    private final double canhA;
    private final double canhB;
    private final double canhC;

    /**
     * @param canhA độ dài cạnh a
     * @param canhB độ dài cạnh b
     * @param canhC độ dài cạnh c
     * @throws IllegalArgumentException nếu một cạnh không phải số dương hữu hạn, hoặc ba cạnh
     *                                  không thoả bất đẳng thức tam giác
     */
    public HinhTamGiac(double canhA, double canhB, double canhC) {
        this.canhA = kiemTraKichThuoc(canhA, "Cạnh a");
        this.canhB = kiemTraKichThuoc(canhB, "Cạnh b");
        this.canhC = kiemTraKichThuoc(canhC, "Cạnh c");

        // Chạy sau khi cả ba cạnh đã qua kiemTraKichThuoc: tới đây chắc chắn không còn
        // NaN hay Infinity, nên các phép cộng so sánh dưới đây hành xử bình thường.
        kiemTraBatDangThucTamGiac(this.canhA, this.canhB, this.canhC);
    }

    /**
     * Bất đẳng thức tam giác: tổng hai cạnh bất kỳ phải lớn hơn cạnh còn lại.
     *
     * <p>Dùng {@code >} chứ không {@code >=}: khi tổng hai cạnh đúng bằng cạnh thứ ba thì ba
     * đỉnh thẳng hàng, diện tích bằng 0 - đó là một đoạn thẳng, không phải tam giác.
     *
     * @throws IllegalArgumentException nếu ba cạnh không dựng được thành tam giác
     */
    private static void kiemTraBatDangThucTamGiac(double a, double b, double c) {
        if (a + b <= c || a + c <= b || b + c <= a) {
            throw new IllegalArgumentException(String.format(
                    "Ba cạnh không tạo thành tam giác: a=%s, b=%s, c=%s", a, b, c));
        }
    }

    /**
     * Diện tích theo công thức Heron: {@code √(p(p-a)(p-b)(p-c))} với {@code p} là nửa chu vi.
     *
     * <p>Chọn Heron vì nó chỉ cần ba cạnh - đúng bằng dữ liệu mà hình này có, không phải suy ra
     * thêm chiều cao hay góc nào.
     *
     * <p>Lưu ý: với tam giác rất "mảnh" (một cạnh gần bằng tổng hai cạnh kia, ví dụ
     * {@code 100000, 99999.99999, 0.00001}), công thức này mất chính xác đáng kể do trừ hai số
     * gần bằng nhau. Trường hợp đó cần công thức Kahan - ngoài phạm vi bài này.
     */
    @Override
    public double dienTich() {
        double nuaChuVi = (canhA + canhB + canhC) / 2;
        return Math.sqrt(nuaChuVi * (nuaChuVi - canhA) * (nuaChuVi - canhB) * (nuaChuVi - canhC));
    }

    @Override
    public String toString() {
        return String.format("HinhTamGiac{canhA=%s,canhB=%s,canhC=%s, dienTich=%s}",
                canhA, canhB, canhC, dienTich());
    }
}
