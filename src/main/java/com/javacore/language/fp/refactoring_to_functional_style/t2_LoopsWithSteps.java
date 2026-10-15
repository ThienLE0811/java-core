package com.javacore.language.fp.refactoring_to_functional_style;

import java.util.stream.IntStream;

/**
 * Converting Loops with Steps
 * <p>
 * 0. Vị trí bài này
 * - Bước 16/41, bài 2/5 trong section Refactoring to Functional Style của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Vòng for có bước nhảy ánh xạ sang IntStream.iterate; break giữa vòng lặp ánh xạ sang takeWhile.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/refactoring-to-functional-style/loops-with-steps/">dev.java - Converting Loops with Steps</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CONVERTING LOOPS WITH STEPS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. LẶP CÓ BƯỚC NHẢY
 * - (Iterating with Steps)
 * - Bài trước dùng {@code range()} cho vòng lặp qua TỪNG giá trị. Khi vòng lặp nhảy cóc
 * ({@code i = i + 3}) thì phản xạ đầu tiên hay là dùng {@code filter()} - nhưng có cách
 * đơn giản hơn: {@code IntStream.iterate()}.
 * <p>
 * 2. TỪ IMPERATIVE SANG FUNCTIONAL
 * - (From Imperative to Functional Style)
 * - Ba phần của {@code for (int i = 0; i < 15; i = i + 3)} ánh xạ 1-1 sang ba tham số
 * của {@code iterate(int seed, IntPredicate hasNext, IntUnaryOperator next)}:
 * + {@code int i = 0}      →  seed (giá trị khởi đầu)
 * + {@code i < 15}         →  IntPredicate (điều kiện tiếp tục)
 * + {@code i = i + 3}      →  IntUnaryOperator (bước nhảy)
 * - "the ;s became ,s": đổi dấu chấm phẩy thành dấu phẩy, hai phần sau thành lambda.
 * <p>
 * 3. VÒNG LẶP VÔ HẠN CÓ BREAK
 * - (Unbounded Iteration with a break)
 * - Vòng {@code for (int i = 0;; i = i + 3)} với {@code if (i > 20) break;} bên trong.
 * - Bỏ IntPredicate khỏi {@code iterate()} thì được stream VÔ HẠN (bản iterate hai tham số).
 * - "The functional programming equivalent of the imperative style break is the
 * takeWhile() method." takeWhile dừng internal iterator ngay khi predicate trả về false.
 * - Chú ý dịch điều kiện cho đúng chiều: break khi {@code i > 20} nghĩa là tiếp tục
 * khi {@code i <= 20}, nên viết {@code .takeWhile(i -> i <= 20)}.
 * <p>
 * 4. BẢNG ÁNH XẠ
 * - (Mappings)
 * - for có bước nhảy                  →  {@code iterate(seed, hasNext, next)} (3 tham số)
 * - for vô hạn + {@code break}        →  {@code iterate(seed, next)} (2 tham số) + {@code takeWhile(...)}
 * - Nói gọn: {@code takeWhile} chính là {@code break} của thế giới functional.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. takeWhile KHÁC filter
 * - {@code filter} xét HẾT mọi phần tử, phần tử nào không hợp thì bỏ qua rồi đi tiếp.
 * - {@code takeWhile} DỪNG HẲN ở phần tử đầu tiên không hợp, các phần tử sau không được xét.
 * - Với dãy 0, 3, 6, 21, 9: filter(i <= 20) cho 0, 3, 6, 9; takeWhile(i <= 20) cho 0, 3, 6.
 * - Cặp đôi của nó là {@code dropWhile}: bỏ phần đầu cho tới khi predicate sai, rồi lấy hết phần còn lại.
 * - {@code takeWhile}, {@code dropWhile} và {@code iterate} ba tham số đều có từ Java 9.
 * <p>
 * 6. STREAM VÔ HẠN THÌ PHẢI CÓ CHỖ DỪNG
 * - {@code iterate(seed, next)} hai tham số và {@code Stream.generate()} sinh stream vô hạn.
 * Thiếu một short-circuit operation ({@code takeWhile}, {@code limit}, {@code findFirst},
 * {@code anyMatch}) thì chương trình treo chứ không báo lỗi.
 * - {@code limit(n)} dừng theo SỐ LƯỢNG, {@code takeWhile} dừng theo ĐIỀU KIỆN.
 * - Trên parallel stream, {@code takeWhile} tốn kém vì phải giữ thứ tự; vòng lặp có bước
 * nhảy vốn tuần tự, đừng vội parallel.
 * <p>
 * 7. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - JS không có takeWhile trên Array (có trong đề xuất iterator helpers); tương đương
 * thường phải tự viết generator function với {@code yield} rồi {@code break}.
 * - Generator của JS cũng lazy như stream của Java - đó là chỗ giống nhau gần nhất.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba phần của vòng for ánh xạ sang ba tham số nào của iterate?
 * - iterate có mấy phiên bản, khác nhau chỗ nào, cái nào sinh stream vô hạn?
 * - break tương đương với operation nào?
 * - takeWhile khác filter ra sao? Cho một dãy ví dụ phân biệt được hai cái.
 * - Điều kiện của break và điều kiện của takeWhile ngược chiều nhau thế nào?
 * - Quên short-circuit trên stream vô hạn thì chuyện gì xảy ra?
 */
public class t2_LoopsWithSteps {

    void main() {
        // ==================================================================
        // MỤC 2 - FOR CÓ BƯỚC NHẢY
        // ==================================================================

        // Imperative.
        for (int i = 0; i < 15; i = i + 3) {
            IO.println(i);
        }

        // Functional: ba phần của for thành ba tham số, dấu ; thành dấu ,
        IntStream.iterate(0, i -> i < 15, i -> i + 3)
                .forEach(IO::println);

        // ==================================================================
        // MỤC 3 - FOR VÔ HẠN CÓ BREAK
        // ==================================================================

        // Imperative: không có điều kiện dừng ở đầu, thoát bằng break ở giữa thân.
        for (int i = 0; ; i = i + 3) {
            if (i > 20) {
                break;
            }
            IO.println(i);
        }

        // Functional: iterate hai tham số sinh stream VÔ HẠN (0, 3, 6, ...),
        // takeWhile đóng vai trò của break - dừng ngay khi điều kiện sai.
        // Để ý chiều điều kiện bị đảo: break khi i > 20  <=>  take while i <= 20.
        IntStream.iterate(0, i -> i + 3)
                .takeWhile(i -> i <= 20)
                .forEach(IO::println);

        // Phân biệt takeWhile với filter trên cùng một dãy không tăng đều:
        // filter xét hết rồi lọc, takeWhile dừng ở phần tử sai đầu tiên.
        IO.println("filter   : " + IntStream.of(0, 3, 6, 21, 9).filter(i -> i <= 20).boxed().toList());
        IO.println("takeWhile: " + IntStream.of(0, 3, 6, 21, 9).takeWhile(i -> i <= 20).boxed().toList());
    }

    // TODO 1 (mục 2): Viết vòng for đếm ngược có bước nhảy (i = i - 2) rồi chuyển sang iterate.
    //   Chú ý điều kiện dừng lúc này là i >= ... chứ không phải i <= ...
    //
    // TODO 2 (mục 3): Bỏ takeWhile khỏi ví dụ stream vô hạn, chạy thử rồi dừng chương trình.
    //   Ghi lại: compiler KHÔNG báo gì, lỗi chỉ lộ ra lúc chạy.
    //
    // TODO 3 (mục 5): Viết lại ví dụ filter/takeWhile ở trên với dropWhile, dự đoán kết quả
    //   trước khi chạy rồi đối chiếu.
    //
    // TODO 4 (mục 6): Dùng Stream.iterate sinh dãy Fibonacci rồi limit(10). So với cách
    //   viết bằng vòng while, cái nào dễ đọc hơn với mình?
}
