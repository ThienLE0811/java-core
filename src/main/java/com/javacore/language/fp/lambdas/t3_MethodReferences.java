package com.javacore.language.fp.lambdas;

import java.util.List;

/**
 * Writing Lambda Expressions as Method References
 * <p>
 * 0. Vị trí bài này
 * - Bước 14/41, bài 3/5 trong section Lambda Expressions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/lambdas/method-references/">dev.java - Writing Lambda Expressions as Method References</a>
 * - Method reference KHÔNG phải tính năng mới, chỉ là cách viết gọn của lambda khi thân
 * lambda chỉ gọi đúng một method có sẵn.
 * <p>
 * ======================================================================
 * TRANG GỐC - BỐN DẠNG METHOD REFERENCE (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. METHOD REFERENCE ĐẦU TIÊN
 * - (Your First Method Reference) Từ lambda sang {@code ::} - phép biến đổi diễn ra thế nào.
 * <p>
 * 2. STATIC METHOD REFERENCE
 * - Dạng {@code RefType::staticMethod}, ví dụ {@code Math::sqrt}.
 * <p>
 * 3. UNBOUND METHOD REFERENCE
 * - Dạng {@code RefType::instanceMethod}, ví dụ {@code String::length}, {@code String::indexOf}.
 * - Tham số đầu tiên của lambda trở thành RECEIVER của lời gọi.
 * - Phân biệt: method không nhận tham số và method có nhận tham số.
 * <p>
 * 4. BOUND METHOD REFERENCE
 * - Dạng {@code expr::instanceMethod}, ví dụ {@code System.out::println}.
 * - Receiver đã được chốt sẵn ngay trong method reference.
 * <p>
 * 5. CONSTRUCTOR METHOD REFERENCE
 * - Dạng {@code ClassName::new}, ví dụ {@code ArrayList::new}.
 * <p>
 * 6. TỔNG KẾT
 * - (Wrapping Up Method References) Bảng tự lập: dạng - cú pháp - lambda tương đương - ví dụ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BOUND VÀ UNBOUND DỄ NHẦM
 * - {@code String::toUpperCase} với {@code "abc"::toUpperCase} khác nhau ở chỗ nào?
 * - Khi nào compiler không phân biệt được và báo ambiguous?
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Vì sao trong JS/TS viết {@code arr.map(this.format)} hay mất {@code this}, còn method
 * reference của Java thì không gặp vấn đề đó?
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kể đủ bốn dạng và cú pháp của từng dạng.
 * - Nhìn một method reference bất kỳ, viết lại thành lambda đầy đủ.
 * - Unbound reference nhận tham số thứ nhất làm gì?
 * - Khi nào KHÔNG thể rút một lambda thành method reference?
 * - {@code ArrayList::new} dùng được ở những chỗ nhận interface nào?
 */
public class t3_MethodReferences {

    void main() {
        // TODO 1 (mục 1): Viết một lambda gọi đúng một method, rồi rút gọn thành method reference.
        //   Giữ cả hai phiên bản cạnh nhau để so sánh.
        //
        List<String> names = List.of("alpha", "beta", "gamma");

        names.forEach(s -> System.out.println(s));   // lambda
        names.forEach(System.out::println);          // method reference — y hệt

        // TODO 2 (mục 3): Dùng String::length với Function<String, Integer> và String::compareTo
        //   với Comparator<String>. Viết lại cả hai thành lambda để thấy receiver nằm ở đâu.
        //
        // TODO 3 (mục 4): Dùng System.out::println làm Consumer. Gán nó vào một biến rồi gọi
        //   nhiều lần.
        //
        // TODO 4 (mục 5): Dùng ArrayList::new làm Supplier<List<String>>, rồi thử String[]::new -
        //   ghi lại interface nào khớp được với dạng này.
        //
        // TODO 5 (mục 7): Viết cả String::toUpperCase và "abc"::toUpperCase, gán vào biến với kiểu
        //   phù hợp cho từng cái. Ghi lại kiểu của hai biến đó.
    }
}
