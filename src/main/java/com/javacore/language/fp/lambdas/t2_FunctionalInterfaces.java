package com.javacore.language.fp.lambdas;

/**
 * Using Lambda Expressions in Your Application
 * <p>
 * 0. Vị trí bài này
 * - Bước 14/41, bài 2/5 trong section Lambda Expressions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/lambdas/functional-interfaces/">dev.java - Using Lambda Expressions in Your Application</a>
 * - Bài t1 dạy CÚ PHÁP, bài này dạy BỘ INTERFACE CÓ SẴN để không phải tự định nghĩa.
 * <p>
 * ======================================================================
 * TRANG GỐC - BỐN NHÓM FUNCTIONAL INTERFACE (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. GÓI java.util.function
 * - (Discovering the java.util.function package) Có bao nhiêu interface, chia theo nguyên tắc nào.
 * <p>
 * 2. SUPPLIER - TẠO RA HOẶC CUNG CẤP ĐỐI TƯỢNG
 * - Method {@code get()}. Khi nào dùng Supplier thay vì truyền thẳng giá trị (lazy).
 * - Bản chuyên cho primitive: {@code IntSupplier}, {@code BooleanSupplier},
 * {@code LongSupplier}, {@code DoubleSupplier} - method đổi tên thành {@code getAsInt()}...
 * <p>
 * 3. CONSUMER - TIÊU THỤ ĐỐI TƯỢNG
 * - Method {@code accept()}, không trả về gì.
 * - Bản chuyên: {@code IntConsumer}... Bản hai tham số: {@code BiConsumer<T, U>}.
 * - Chỗ JDK nhận Consumer: {@code Iterable.forEach()}.
 * <p>
 * 4. PREDICATE - KIỂM TRA ĐỐI TƯỢNG
 * - Method {@code test()}, trả về boolean.
 * - Bản chuyên: {@code IntPredicate}... Bản hai tham số: {@code BiPredicate<T, U>}.
 * - Chỗ JDK nhận Predicate: {@code Collection.removeIf()}.
 * <p>
 * 5. FUNCTION - ÁNH XẠ ĐỐI TƯỢNG SANG ĐỐI TƯỢNG KHÁC
 * - Method {@code apply()}.
 * - Bản chuyên: {@code IntFunction}, {@code ToIntFunction}, {@code IntToLongFunction}...
 * - {@code UnaryOperator<T>} là Function có cùng kiểu vào và ra; chỗ JDK nhận nó:
 * {@code List.replaceAll()}.
 * - {@code BiFunction<T, U, R>} và {@code BinaryOperator<T>}.
 * <p>
 * 6. TỔNG KẾT BỐN NHÓM
 * - (Wrapping up the Four Categories of Functional Interfaces)
 * - Bảng tự lập: tên interface - method trừu tượng - vào gì - ra gì - dùng khi nào.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. VÌ SAO CÓ BẢN CHUYÊN CHO PRIMITIVE
 * - Boxing/unboxing và chi phí của nó; khi nào khác biệt này thực sự đáng kể.
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - TS chỉ cần kiểu hàm {@code (t: T) => R} là đủ, không cần cả một gói interface.
 * Vì sao Java phải đi đường vòng này?
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn nhóm chính là gì, mỗi nhóm có method trừu tượng tên gì?
 * - Nhìn một chữ ký hàm bất kỳ, chọn đúng interface trong java.util.function.
 * - {@code UnaryOperator<T>} khác {@code Function<T, T>} ở điểm nào, khi nào phải dùng cái nào?
 * - {@code BinaryOperator<T>} quan hệ thế nào với {@code BiFunction}?
 * - {@code IntFunction<R>}, {@code ToIntFunction<T>}, {@code IntToDoubleFunction} khác nhau ra sao?
 * - Supplier mang lại lợi ích gì so với truyền thẳng một giá trị đã tính sẵn?
 */
public class t2_FunctionalInterfaces {

    @FunctionalInterface
    public interface Supplier<T> {

        T get();
    }


    void main() {

        Supplier<String> supplier = () -> "Hello Duke!";
        String message = supplier.get();   // <- lúc này lambda mới thực sự chạy
        System.out.println(message);
        // TODO 1 (mục 2): Viết một method log(String level, Supplier<String> message) chỉ gọi
        //   get() khi level đủ điều kiện. Chứng minh lazy bằng cách in ra bên trong supplier.
        //
        // TODO 2 (mục 3): Dùng BiConsumer với Map.forEach để in từng cặp key-value.
        //
        // TODO 3 (mục 4): Viết Predicate<String> rồi truyền vào Collection.removeIf.
        //
        // TODO 4 (mục 5): Dùng UnaryOperator<String> với List.replaceAll để chuẩn hoá dữ liệu.
        //   Sau đó thử thay bằng Function<String, String> và xem compiler nói gì.
        //
        // TODO 5 (mục 6): Tự lập bảng bốn nhóm vào phần Javadoc mục 6 ở trên.
    }
}
