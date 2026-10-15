package com.javacore.language.fp.generics;

import java.util.ArrayList;
import java.util.List;

/**
 * Wildcards
 * <p>
 * 0. Vị trí bài này
 * - Bước 13/41, bài 3/5 trong section Generics của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Generic là invariant nên quá cứng, wildcard {@code ?} là cơ chế nới
 * lỏng CÓ KIỂM SOÁT, và cái giá phải trả là mất bớt quyền đọc hoặc quyền ghi.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/generics/wildcards/">dev.java - Wildcards</a>
 * - Đây là bài khó nhất của cả section. Đừng học thuộc cú pháp, hãy học câu hỏi ở mục 7.
 * <p>
 * ======================================================================
 * TRANG GỐC - WILDCARDS (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. VẤN ĐỀ CẦN GIẢI
 * - Từ bài t1: generic là INVARIANT, {@code List<Integer>} không phải {@code List<Number>}.
 * An toàn nhưng quá cứng - method nhận {@code List<Number>} sẽ từ chối cả
 * {@code List<Integer>} lẫn {@code List<Double>}.
 * - Wildcard {@code ?} sinh ra để nới lỏng ràng buộc đó mà vẫn giữ được an toàn kiểu.
 * <p>
 * 2. UPPER BOUNDED WILDCARD - {@code ? extends}
 * - "The upper bounded wildcard, &lt;? extends Foo&gt;, where Foo is any type, matches Foo and
 * any subtype of Foo."
 * <pre>{@code
 * public static double sumOfList(List<? extends Number> list) {
 *     double s = 0.0;
 *     for (Number n : list)
 *         s += n.doubleValue();
 *     return s;
 * }
 *
 * sumOfList(List.of(1, 2, 3));        // List<Integer> - OK
 * sumOfList(List.of(1.5, 2.5));       // List<Double>  - OK
 * }</pre>
 * - Khác biệt then chốt: {@code List<Number>} chỉ khớp đúng list của Number;
 * {@code List<? extends Number>} khớp Number và mọi lớp con.
 * <p>
 * 3. VÌ SAO {@code ? extends} GẦN NHƯ CHỈ ĐỌC
 * <pre>{@code
 * void f(List<? extends Number> list) {
 *     Number n = list.get(0);   // OK - chắc chắn lấy ra được Number
 *     list.add(42);             // LỖI - compiler chặn
 * }
 * }</pre>
 * - Lý do: compiler KHÔNG BIẾT list thực ra là {@code List<Integer>} hay {@code List<Double>}.
 * Nhét {@code Integer} vào trong khi nó đang là {@code List<Double>} thì hỏng. Nên cấm ghi hết.
 * - Trang gốc nói rõ {@code List<? extends Type>} coi như read-only: chỉ thêm được {@code null},
 * gọi được {@code clear()}, dùng được {@code remove()} của iterator, hoặc capture wildcard để
 * ghi lại chính phần tử vừa đọc ra.
 * <p>
 * 4. LOWER BOUNDED WILDCARD - {@code ? super}
 * - Giới hạn theo chiều ngược lại: kiểu đó hoặc các lớp CHA của nó.
 * <pre>{@code
 * public static void addNumbers(List<? super Integer> list) {
 *     for (int i = 1; i <= 10; i++)
 *         list.add(i);          // OK - ghi Integer vào luôn an toàn
 * }
 * // nhận được List<Integer>, List<Number>, List<Object>
 * }</pre>
 * - Vì sao ghi được: dù thực tế là {@code List<Integer>}, {@code List<Number>} hay
 * {@code List<Object>}, nhét một {@code Integer} vào đều hợp lệ.
 * - Nhưng đọc ra thì chỉ dám hứa {@code Object}, vì không biết kiểu thật là cái nào.
 * - "You can specify an upper bound for a wildcard, or you can specify a lower bound, but you
 * cannot specify both." Không có {@code <? extends A super B>}.
 * <p>
 * 5. UNBOUNDED WILDCARD - {@code ?}
 * - Dạng {@code List<?>} nghĩa là "list của một kiểu nào đó mà tôi không biết".
 * - Dùng khi: chỉ cần method của {@code Object}, hoặc method không phụ thuộc type parameter
 * như {@code size()}, {@code clear()}.
 * <pre>{@code
 * public static void printList(List<?> list) {
 *     for (Object elem : list)
 *         IO.println(elem + " ");
 * }
 * }</pre>
 * - CHỖ DỄ NHẦM NHẤT CỦA BÀI: {@code List<?>} KHÁC {@code List<Object>}.
 * "You can insert Object instances into List&lt;Object&gt;, but only null into List&lt;?&gt;."
 * + {@code List<Object>} là một kiểu CỤ THỂ, chỉ nhận đúng {@code List<Object>}.
 * + {@code List<?>} nhận MỌI {@code List}, nhưng đổi lại không ghi được gì ngoài {@code null}.
 * + Muốn {@code printList()} in được mọi loại list thì bắt buộc dùng {@code List<?>}.
 * <p>
 * 6. WILDCARD VÀ SUBTYPING
 * - Generic không tự sinh quan hệ subtype theo type argument: {@code Integer} là con của
 * {@code Number} nhưng {@code List<Integer>} KHÔNG phải con của {@code List<Number>}.
 * - Wildcard tạo ra quan hệ đó: {@code List<Integer>} và {@code List<Double>} đều là con của
 * {@code List<? extends Number>}.
 * - Tổ tiên chung của {@code List<Number>} và {@code List<Integer>} là {@code List<?>},
 * KHÔNG phải {@code List<Object>}.
 * <p>
 * 7. HƯỚNG DẪN DÙNG WILDCARD - PECS
 * - Trang gốc phân loại biến theo vai trò "in" (cấp dữ liệu) và "out" (nơi nhận dữ liệu):
 * + Biến "in", ta ĐỌC từ nó: dùng {@code ? extends}
 * + Biến "out", ta GHI vào nó: dùng {@code ? super}
 * + Chỉ cần method của {@code Object}: dùng {@code ?} không bound
 * + Vừa đọc vừa ghi: KHÔNG dùng wildcard, dùng thẳng {@code <T>}
 * - Cách nhớ phổ biến ngoài trang gốc: PECS - Producer Extends, Consumer Super.
 * - Lời khuyên quan trọng: "Using a wildcard as a return type should be avoided because it
 * forces programmers using the code to deal with wildcards."
 * Wildcard dành cho THAM SỐ, không dành cho KIỂU TRẢ VỀ.
 * - Ví dụ kinh điển trong JDK, đọc là thấy PECS ngay:
 * <pre>{@code
 * Collections.copy(List<? super T> dest, List<? extends T> src)
 * //               consumer -> super        producer -> extends
 * }</pre>
 * <p>
 * 8. WILDCARD CAPTURE VÀ HELPER METHOD
 * - Khi gặp {@code List<?}&gt;, compiler tự đặt tên tạm cho kiểu chưa biết. Tên này xuất hiện
 * trong thông báo lỗi dưới dạng {@code capture#1 of ?}.
 * - Đôi khi compiler báo lỗi dù thao tác thực ra an toàn. Cách gỡ là tách ra một private
 * helper method có type parameter thật, để compiler suy được kiểu capture:
 * <pre>{@code
 * void foo(List<?> list) {
 *     fooHelper(list);          // OK - nhờ helper mà compiler suy được kiểu
 * }
 *
 * private <T> void fooHelper(List<T> list) {
 *     list.set(0, list.get(0)); // đọc ra rồi ghi lại chính nó - vốn dĩ an toàn
 * }
 * }</pre>
 * - CẢNH BÁO của trang gốc: helper method chỉ cứu được thao tác VỐN ĐÃ HỢP LỆ. Thao tác bản
 * chất không an toàn, ví dụ chuyển phần tử giữa hai generic type không tương thích, thì không
 * mẹo nào cứu được.
 * <p>
 * ======================================================================
 * GHI CHÚ THÊM
 * ======================================================================
 * <p>
 * 9. CÂU HỎI DUY NHẤT CẦN NHỚ
 * - Đừng học thuộc cú pháp. Mỗi khi viết signature nhận collection, hỏi đúng một câu:
 * "Method này ĐỌC từ collection, hay GHI vào nó?"
 * + Chỉ đọc -> {@code ? extends}
 * + Chỉ ghi -> {@code ? super}
 * + Cả hai -> bỏ wildcard, dùng {@code <T>}
 * <p>
 * 10. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - TS không có cơ chế tương đương trực tiếp. TS suy variance tự động theo cấu trúc, lập trình
 * viên không khai báo gì cả. Java bắt khai báo tay TẠI TỪNG CHỖ DÙNG (use-site variance).
 * - Thứ gần nhất bên TS là {@code readonly T[]} hoặc {@code ReadonlyArray<T>} - cũng mang tinh
 * thần "chỉ đọc, không ghi" giống {@code List<? extends T>}, nhưng không có vế {@code super}.
 * - Đây là nơi người từ TS sang hay viết code không compile được mà không hiểu vì sao.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code List<? extends Number>} đọc ra kiểu gì, ghi vào được gì?
 * - {@code List<? super Integer>} đọc ra kiểu gì, ghi vào được gì?
 * - {@code List<?>} khác {@code List<Object>} ở điểm nào?
 * - Tổ tiên chung của {@code List<Number>} và {@code List<Integer>} là gì?
 * - PECS là viết tắt của gì, áp dụng thế nào vào {@code Collections.copy}?
 * - Vì sao không nên dùng wildcard làm kiểu trả về?
 * - Có viết được {@code <? extends Number super Integer>} không? Vì sao?
 */
public class t3_Wildcards {

    static double sum(List<? extends Number> src) {
        double s = 0;
        for (Number n : src) s += n.doubleValue();   // đọc ra Number: OK
        return s;
    }

    static void fill(List<? super Integer> dest) {
        dest.add(1);      // ghi Integer: luôn an toàn
        dest.add(2);
    }

    static int size(List<?> any) {
        return any.size();      // method không phụ thuộc type parameter
    }


    void main() {
        // TODO 1 (mục 2): Viết sumOfList(List<? extends Number>) rồi gọi với List<Integer>,
        //   List<Double>. Sau đó đổi signature thành List<Number> và xem cả hai lời gọi đều hỏng.
        //
        List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
        List<Double> dbls = new ArrayList<>(List.of(1.5, 2.5));
        List<Object> objs = new ArrayList<>();

        sum(ints);   // 6.0
        sum(dbls);   // 4.0

        fill(ints);   // List<Integer> → [1, 2, 3, 1, 2]
        fill(objs); // List<Object>  → OK

        size(ints);   // 5
        size(dbls);   // 2
        size(objs);   // 2
        // TODO 2 (mục 3): Trong method nhận List<? extends Number>, thử list.add(42).
        //   Dán thông báo lỗi vào đây và giải thích bằng lời của mình.
        //
        // TODO 3 (mục 4): Viết addNumbers(List<? super Integer>) ghi 1..10 vào list.
        //   Gọi với List<Integer>, List<Number>, List<Object>. Thử đọc một phần tử ra biến
        //   Integer để thấy vì sao chỉ nhận được Object.
        //
        // TODO 4 (mục 5): Viết printList(List<?>) in mọi loại list. Sau đó đổi thành
        //   List<Object> và quan sát những lời gọi nào không còn biên dịch được.
        //
        // TODO 5 (mục 7): Tự viết lại copy(List<? super T> dest, List<? extends T> src).
        //   Thử hoán đổi extends/super cho nhau để thấy mỗi vế hỏng ở đâu.
        //
        // TODO 6 (mục 8): Tái hiện lỗi capture - viết void swapFirst(List<?> list) thử
        //   list.set(0, list.get(0)). Dán lỗi capture#1 vào đây, rồi sửa bằng helper method.
    }
}
