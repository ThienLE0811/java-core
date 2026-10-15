package com.javacore.language.fp.lambdas;

import java.util.function.Consumer;

/**
 * Writing Your First Lambda Expression
 * <p>
 * 0. Vị trí bài này
 * - Bước 14/41, bài 1/5 trong section Lambda Expressions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/lambdas/first-lambdas/">dev.java - Writing Your First Lambda Expression</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WRITING YOUR FIRST LAMBDA EXPRESSION (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. KIỂU CỦA MỘT LAMBDA EXPRESSION
 * - (Identifying the Type of a Lambda Expression)
 * - Functional interface là gì, vai trò của {@code @FunctionalInterface}.
 * - Ví dụ các functional interface có sẵn trong JDK.
 * <p>
 * 2. TÌM ĐÚNG METHOD CẦN CÀI ĐẶT
 * - (Finding the Right Method to Implement)
 * - Vì sao một interface có nhiều method vẫn có thể là functional interface.
 * <p>
 * 3. CÀI ĐẶT METHOD ĐÓ BẰNG LAMBDA
 * - (Implementing the Right Method with a Lambda Expression)
 * - Rút gọn cú pháp: bỏ kiểu tham số, bỏ ngoặc tròn, bỏ ngoặc nhọn và return.
 * - Ví dụ {@code Consumer<String>} và {@code Runnable}.
 * <p>
 * 4. GỌI MỘT LAMBDA EXPRESSION
 * - (Calling a Lambda Expression)
 * - Lambda không tự chạy: gọi qua method trừu tượng của interface.
 * <p>
 * 5. CAPTURING LOCAL VALUES
 * - (Capturing Local Values)
 * - Biến local bị capture phải là final hoặc effectively final. Lý do.
 * - Field của object thì khác - không chịu ràng buộc này.
 * <p>
 * 6. SERIALIZING LAMBDAS
 * - (Serializing Lambdas)
 * <p>
 * 7. THỰC HÀNH
 * - (Practicing Using Lambdas) Consumer, Supplier, Predicate, Function.
 * - Dùng lambda với collection: forEach, removeIf, replaceAll.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. LAMBDA KHÁC ANONYMOUS CLASS
 * - Ý nghĩa của {@code this} trong lambda và trong anonymous class.
 * - Lambda không tạo scope mới cho biến, anonymous class thì có (shadowing).
 * <p>
 * 9. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Arrow function của TS và lambda của Java giống nhau ở đâu, khác nhau ở đâu
 * (kiểu của biểu thức, closure capture, {@code this}).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kiểu của một lambda expression được quyết định bởi cái gì?
 * - Functional interface là gì? Interface có default method, static method, hay method
 * của Object thì còn là functional interface không?
 * - {@code @FunctionalInterface} có bắt buộc không, nó mang lại gì?
 * - Bốn mức rút gọn cú pháp lambda là gì?
 * - Effectively final nghĩa là gì, vì sao Java bắt buộc điều đó?
 * - Vì sao capture một field của object lại không bị ràng buộc như capture biến local?
 * - {@code this} bên trong lambda trỏ tới đâu, khác anonymous class ra sao?
 */
public class t1_FirstLambdas {

    void main() {
        // ==================================================================
        // VÍ DỤ MẪU (mục 3) - CÙNG MỘT HÀNH VI, BA CÁCH VIẾT
        // Consumer<String> là functional interface: một method trừu tượng duy nhất
        // là void accept(String s). Cả ba biến dưới đây đều là cài đặt của method đó.
        // ==================================================================

        // Cách 1 - anonymous class: cách duy nhất trước Java 8.
        // Phải nói lại tên interface, tên method, kiểu tham số, kiểu trả về.
        Consumer<String> inHoa1 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s.toUpperCase());
            }
        };

        // Cách 2 - lambda đầy đủ: bỏ hết phần thừa, chỉ giữ THAM SỐ và THÂN HÀM.
        // Không cần viết "Consumer", không cần viết "accept" - compiler suy ra từ
        // kiểu của biến bên trái (target type).
        Consumer<String> inHoa2 = (String s) -> {
            System.out.println(s.toUpperCase());
        };

        // Cách 3 - lambda rút gọn: bỏ kiểu tham số (suy ra được), bỏ ngoặc tròn
        // (chỉ một tham số), bỏ ngoặc nhọn (thân chỉ một lệnh).
        Consumer<String> inHoa3 = s -> System.out.println(s.toUpperCase());

        // Lambda KHÔNG tự chạy. Phải gọi qua method trừu tượng của interface.
        inHoa1.accept("alpha");
        inHoa2.accept("beta");
        inHoa3.accept("gamma");
        
        // Điểm cốt lõi của bài: biểu thức "s -> System.out.println(s.toUpperCase())"
        // tự nó KHÔNG có kiểu. Nó là Consumer<String> chỉ vì vế trái khai báo như vậy.
        // Đặt cùng biểu thức đó vào chỗ khai báo kiểu khác thì nó mang kiểu khác.

        // TODO 1 (mục 1): Tự khai báo một functional interface, đánh dấu @FunctionalInterface,
        //   rồi thử thêm method trừu tượng thứ hai để xem compiler báo gì.
        //
        // TODO 2 (mục 3): Cài đặt cùng một hành vi theo ba cách - anonymous class, lambda đầy đủ,
        //   lambda rút gọn hết mức. So sánh độ dài và độ rõ.
        //
        // TODO 3 (mục 5): Capture một biến local rồi gán lại giá trị cho nó sau đó.
        //   Dán lỗi của compiler vào đây và giải thích.
        //
        // TODO 4 (mục 7): Dùng forEach, removeIf, replaceAll trên một List để thấy lambda
        //   được nhận vào API của collection như thế nào.
        //
        // TODO 5 (mục 8): In this.getClass() bên trong lambda và bên trong anonymous class
        //   cùng một chỗ. Ghi lại khác biệt.
    }
}
