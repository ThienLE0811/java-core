package com.javacore.language.fp.refactoring_to_functional_style;

import java.util.stream.IntStream;

/**
 * Converting Simple Loops
 * <p>
 * 0. Vị trí bài này
 * - Bước 16/41, bài 1/5 trong section Refactoring to Functional Style của lộ trình dev.java/learn.
 * - Cả section do Venkat Subramaniam đóng góp, mỗi bài đều kết bằng một mục "Mappings"
 * - đó là phần cần thuộc: một dạng vòng lặp imperative ánh xạ sang một API functional nào.
 * - Một câu cho cả bài: Vòng for đếm cơ bản thay bằng IntStream.range (không gồm biên trên) hoặc rangeClosed (gồm biên trên).
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/refactoring-to-functional-style/simple-loops/">dev.java - Converting Simple Loops</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CONVERTING SIMPLE LOOPS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. IMPERATIVE VS FUNCTIONAL
 * - (Imperative vs. Functional Styles)
 * - "Imperative style is where we tell what to do and also how to do it."
 * - "Functional style is declarative in nature, where we tell what to do and delegate
 * the how or the details to the underlying libraries."
 * - Imperative dễ viết hơn vì quen tay, nhưng code dài dòng, rắc rối, khó đọc.
 * Functional khó lúc đầu nhưng nhìn chung dễ đọc, dễ hiểu, dễ sửa hơn.
 * - Cách dùng section này: khi sắp sửa bug hoặc thêm tính năng cho một đoạn code cũ,
 * tra bảng ánh xạ rồi refactor chỗ đó - không đi đổi hàng loạt.
 * <p>
 * 2. VÒNG FOR ĐƠN GIẢN
 * - (Simple for Loops)
 * - Trong {@code for (int i = 0; i < 5; i++)}, phần CỐT LÕI (essence) là cái khoảng
 * từ 0 đến dưới 5; phần RƯỜM (ceremony/noise) là cú pháp và việc tăng biến i.
 * Functional style giữ essence, bỏ ceremony.
 * - Lặp trên một khoảng số nguyên thì {@code IntStream.range(start, end)} là bản
 * tương đương trực tiếp. Rút gọn thêm bằng method reference: {@code .forEach(IO::println)}.
 * - Vòng lặp lấy cả giá trị cuối ({@code i <= 5}) thì dùng {@code IntStream.rangeClosed}.
 * - Dù range hay rangeClosed, kết quả là một stream các giá trị int, duyệt bằng
 * INTERNAL ITERATOR: stream tự bước qua từng giá trị, mình chỉ lo phần "làm gì với
 * mỗi phần tử" trong {@code forEach}.
 * - So với EXTERNAL ITERATOR của vòng for: code ngắn hơn, ít nhiễu hơn, không phải
 * tự tay thay đổi biến đếm, dễ đọc và dễ sửa hơn.
 * - Trang gốc dặn: đổi xong phải kiểm chứng code vẫn chạy đúng, tốt nhất là bằng test tự động.
 * <p>
 * 3. BẢNG ÁNH XẠ
 * - (Mappings)
 * - {@code for(...)} đếm đến TRƯỚC giá trị cuối  →  {@code IntStream.range(start, end)}
 * - {@code for(...)} đếm đến HẾT giá trị cuối    →  {@code IntStream.rangeClosed(start, end)}
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. VÌ SAO LÀ IntStream CHỨ KHÔNG PHẢI Stream&lt;Integer&gt;
 * - IntStream là primitive specialization: giữ int thật, không boxing từng phần tử.
 * Cùng họ có LongStream, DoubleStream. Không có Stream kiểu char/short/byte.
 * - {@code boxed()} để đổi sang {@code Stream<Integer>} khi cần collect.
 * - {@code range()/rangeClosed()} có từ Java 8.
 * <p>
 * 5. KHI NÀO KHÔNG NÊN ĐỔI
 * - Thân vòng lặp có {@code break}, {@code continue}, hoặc {@code return} ra khỏi
 * method: stream không có các lệnh này (bài 2 nói về break).
 * - Thân vòng lặp gán lại biến local bên ngoài: lambda chỉ capture được biến
 * effectively final, nên sẽ không biên dịch được. Dấu hiệu nên dùng reduce/collect
 * thay vì cố nhét vào forEach.
 * - Vòng lặp cực nóng, rất ngắn: stream có chi phí dựng pipeline, không phải lúc nào
 * cũng nhanh bằng for. Đo trước khi khẳng định.
 * - Nguyên tắc chung của section: refactor để ĐỌC DỄ HƠN, không phải để trông "thời thượng".
 * <p>
 * 6. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - TS/JS không có kiểu range sẵn; tương đương gần nhất là
 * {@code Array.from({length: n}, (_, i) => i).forEach(...)}.
 * - Khác biệt lớn: map/filter của mảng JS chạy ngay (eager) và tạo mảng mới mỗi bước;
 * stream của Java là LAZY, chỉ chạy khi gặp terminal operation.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 7. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Imperative khác declarative ở chỗ nào, nói bằng một câu?
 * - Trong một vòng for, đâu là essence, đâu là ceremony?
 * - range và rangeClosed khác nhau ra sao?
 * - Internal iterator khác external iterator thế nào, lợi gì?
 * - Vì sao dùng IntStream chứ không phải Stream&lt;Integer&gt;?
 * - Kể ba tình huống KHÔNG nên đổi vòng for sang stream.
 */
public class t1_SimpleLoops {

    void main() {
        // ==================================================================
        // MỤC 2 - FOR ĐẾM ĐẾN TRƯỚC GIÁ TRỊ CUỐI
        // ==================================================================

        // Imperative: nói cả "làm gì" lẫn "làm thế nào" (khởi tạo, so sánh, tăng biến).
        for (int i = 0; i < 5; i++) {
            IO.println(i);
        }

        // Functional: chỉ còn essence - khoảng [0, 5) và việc cần làm với mỗi giá trị.
        IntStream.range(0, 5)
                .forEach(i -> IO.println(i));

        // Gọn hơn nữa bằng method reference.
        IntStream.range(0, 5)
                .forEach(IO::println);

        // ==================================================================
        // MỤC 2 - FOR LẤY CẢ GIÁ TRỊ CUỐI
        // ==================================================================

        for (int i = 0; i <= 5; i++) {
            IO.println(i);
        }

        IntStream.rangeClosed(0, 5)
                .forEach(IO::println);

        // Điểm cốt lõi của bài: vòng for tự bước (external iterator), còn stream bước hộ
        // (internal iterator) - mình chỉ cung cấp hành động cho từng phần tử.
    }

    // TODO 1 (mục 2): Tìm trong code cũ của chính mình một vòng for đếm số, đổi sang
    //   range/rangeClosed. Ghi lại số dòng trước và sau.
    //
    // TODO 2 (mục 2): Viết vòng for đếm ngược (i-- ) rồi thử diễn đạt bằng IntStream.
    //   Ghi lại cách làm được (gợi ý: map hoặc iterate ở bài 2) và nhận xét độ dễ đọc.
    //
    // TODO 3 (mục 5): Viết vòng for cộng dồn vào một biến local rồi thử chuyển sang
    //   forEach. Dán lỗi compiler về effectively final vào đây, rồi viết lại bằng sum().
    //
    // TODO 4 (mục 4): So sánh IntStream.range(0, 5).sum() với
    //   Stream.of(0,1,2,3,4).mapToInt(Integer::intValue).sum() - chỗ nào bị boxing?
}
