package com.javacore.language.fp.lambdas;

import java.util.function.Predicate;

/**
 * Combining Lambda Expressions
 * <p>
 * 0. Vị trí bài này
 * - Bước 14/41, bài 4/5 trong section Lambda Expressions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/lambdas/combining-chaining-composing/">dev.java - Combining Lambda Expressions</a>
 * - Ý chính: functional interface chỉ có MỘT abstract method, nhưng vẫn được phép có
 * default method và static method - đó là chỗ JDK cài sẵn các phép ghép lambda.
 * <p>
 * ======================================================================
 * TRANG GỐC - GHÉP LAMBDA (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. GHÉP LAMBDA EXPRESSION
 * - (Combining Lambda Expressions) Vì sao ghép được mà không phải sửa interface.
 * <p>
 * 2. GHÉP PREDICATE BẰNG DEFAULT METHOD
 * - {@code and()}, {@code or()}, {@code negate()}.
 * - Thứ tự ưu tiên khi nối chuỗi, và short-circuit có được giữ không.
 * <p>
 * 3. TẠO PREDICATE BẰNG FACTORY METHOD
 * - {@code Predicate.isEqual()} (và {@code Predicate.not()} - ghi chú phiên bản JDK nào có).
 * <p>
 * 4. GHÉP CONSUMER BẰNG DEFAULT METHOD
 * - {@code andThen()} - chạy tuần tự, chuyện gì xảy ra nếu consumer đầu ném exception.
 * <p>
 * 5. GHÉP VÀ KẾT HỢP FUNCTION BẰNG DEFAULT METHOD
 * - {@code andThen()} và {@code compose()} - cùng một phép ghép, khác nhau ở THỨ TỰ.
 * - {@code f.andThen(g)} so với {@code f.compose(g)}: cái nào chạy trước?
 * <p>
 * 6. TẠO IDENTITY FUNCTION
 * - {@code Function.identity()} - dùng ở đâu trong thực tế.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. NULL VÀ EXCEPTION KHI GHÉP
 * - Các default method này ném {@code NullPointerException} nếu truyền null - kiểm chứng
 * bằng Javadoc của JDK.
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Trong TS muốn ghép hàm phải tự viết {@code compose}/{@code pipe} hoặc mượn thư viện.
 * Java đặt sẵn phép ghép ngay trên interface - được và mất gì?
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao thêm default method vào functional interface mà nó vẫn là functional interface?
 * - {@code a.and(b).or(c)} được nhóm như thế nào?
 * - {@code f.andThen(g)} và {@code f.compose(g)} khác nhau ra sao, viết lại bằng lambda thường.
 * - {@code Function.identity()} khác {@code t -> t} ở điểm gì (nếu có)?
 * - Consumer ghép bằng andThen: cả hai cùng nhận giá trị gốc hay nhận kết quả của nhau?
 * - Những method ghép này trả về đối tượng mới hay sửa đối tượng cũ?
 */
public class t4_CombiningLambdas {

    void main() {
        // TODO 1 (mục 2): Viết ba Predicate<String> rồi ghép bằng and/or/negate. Kiểm tra
        //   short-circuit bằng cách in ra bên trong từng predicate.
        //
        Predicate<String> notEmpty = s -> !s.isEmpty();
        Predicate<String> isShort = s -> s.length() < 10;

        Predicate<String> valid = notEmpty.and(isShort);   // ghép hai cái nhỏ

        System.out.println(valid.test("hello"));   // true
        System.out.println(valid.test(""));        // false
        System.out.println(valid.test("how are you?"));
        // TODO 2 (mục 4): Ghép hai Consumer bằng andThen. Cho consumer đầu ném exception và
        //   ghi lại consumer thứ hai có chạy không.
        //
        // TODO 3 (mục 5): Với cùng hai function f và g, in kết quả của f.andThen(g) và
        //   f.compose(g) trên cùng một input. Ghi lại thứ tự thực thi.
        //
        // TODO 4 (mục 6): Dùng Function.identity() ở một chỗ thực tế (ví dụ dựng Map từ List).
        //
        // TODO 5 (mục 7): Truyền null vào andThen và xem exception ném ra lúc GHÉP hay lúc GỌI.
    }
}
