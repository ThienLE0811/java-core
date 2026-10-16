package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.Optional;

/**
 * Using Optionals
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 10/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Optional đã xuất hiện rải rác từ bài 4 (reduce) và bài 5 (findFirst). Bài này gom lại
 * thành luật dùng cho đàng hoàng.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/optionals/">dev.java - Using Optionals</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - USING OPTIONALS (mục 1 đến 9)
 * ======================================================================
 * <p>
 * 1. KHI METHOD KHÔNG PHẢI LÚC NÀO CŨNG CÓ KẾT QUẢ
 * - (Supporting Methods That Cannot Always Produce a Result)
 * - Optional báo hiệu "có thể không có giá trị" ngay trên CHỮ KÝ method, thay vì trả null
 * rồi để người gọi tự đoán.
 * - Bản nguyên thủy: {@code OptionalInt}, {@code OptionalLong}, {@code OptionalDouble}.
 * <p>
 * 2. TẠO OPTIONAL
 * - (Creating Optional Objects)
 * - {@code Optional.empty()}, {@code Optional.of(value)} (null là NPE ngay),
 * {@code Optional.ofNullable(value)} (null thành empty).
 * <p>
 * 3. MỞ MỘT OPTIONAL
 * - (Opening an Optional Object)
 * - Kiểm tra: {@code isPresent()}, {@code isEmpty()}.
 * - Lấy giá trị: {@code get()} (đã bị khuyến cáo không dùng), {@code orElseThrow()} (nên dùng),
 * {@code orElse(T)}, {@code orElseGet(Supplier)}, {@code or(Supplier)}.
 * - {@code orElse} tính giá trị mặc định NGAY cả khi không cần; {@code orElseGet} chỉ tính khi rỗng.
 * <p>
 * 4. XỬ LÝ NỘI DUNG OPTIONAL
 * - (Processing an Optional Object)
 * - Optional có {@code map()}, {@code filter()}, {@code flatMap()} giống Stream,
 * nối chuỗi được mà không phải mở ra giữa chừng.
 * <p>
 * 5. VÍ DỤ - HAI TÁC GIẢ XUẤT BẢN CHUNG NHIỀU NHẤT
 * - (Getting the Two Authors that Published the Most Together)
 * - Bài toán lớn ghép lại kiến thức bài 7: record {@code PairOfAuthors}, tích Descartes,
 * {@code groupingBy()} và {@code counting()}.
 * <p>
 * 6. VÍ DỤ - TÍNH THEO TỪNG NĂM
 * - (Getting the Two Authors that Published the Most Together Per Year)
 * - Mở rộng mục 5 bằng {@code collectingAndThen()} và {@code flatMapping()}.
 * <p>
 * 7. TRÁNH PHẢI MỞ OPTIONAL
 * - (Avoiding the Opening of Optionals)
 * - {@code Optional.stream()} kết hợp {@code flatMap()}: optional rỗng tự biến mất khỏi stream,
 * khỏi cần {@code orElseThrow()} hay kiểm tra isPresent.
 * <p>
 * 8. TIÊU THỤ NỘI DUNG OPTIONAL
 * - (Consuming the Content of an Optional)
 * - {@code ifPresent(Consumer)} và {@code ifPresentOrElse(Consumer, Runnable)}.
 * <p>
 * 9. LUẬT DÙNG OPTIONAL CHO ĐÚNG
 * - (Stating Some Rules to Use Optionals Properly)
 * - Không gán null cho một biến Optional.
 * - Không gọi {@code get()} / {@code orElseThrow()} nếu chưa chắc có giá trị.
 * - Không dùng Optional làm field, tham số method, hay phần tử của collection.
 * - Optional sinh ra để làm KIỂU TRẢ VỀ, chủ yếu ở cuối một pipeline.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 10. ORELSE SO VỚI ORELSEGET
 * - {@code opt.orElse(expensiveCall())} luôn gọi expensiveCall, kể cả khi opt có giá trị.
 * - {@code opt.orElseGet(() -> expensiveCall())} chỉ gọi khi rỗng. Mặc định nên dùng orElseGet
 * khi giá trị mặc định tốn chi phí hoặc có side effect.
 * <p>
 * 11. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - TS xử lý "có thể không có" ở mức KIỂU: {@code string | undefined} cộng với strictNullChecks,
 * trình biên dịch ép kiểm tra. Java không có union type nên phải bọc trong một object.
 * - {@code opt.orElse(x)} gần với {@code value ?? x}; {@code opt.map(f)} gần với {@code value?.f()}.
 * - Khác biệt lớn: trong TS, {@code string | undefined} dùng làm field hay tham số là bình thường;
 * trong Java, Optional làm field là chống chỉ định (xem mục 9).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Optional.of và ofNullable khác nhau thế nào khi truyền null?
 * - Vì sao get() bị khuyến cáo không dùng, thay bằng gì?
 * - orElse và orElseGet khác nhau ở thời điểm tính giá trị ra sao?
 * - Optional.stream() giải quyết bài toán gì trong một pipeline?
 * - Kể ba chỗ KHÔNG nên đặt Optional và lý do.
 */
public class t10_Optionals {

    void main() {
        List<String> names = List.of("an", "binh", "chi");

        Optional<String> found = names.stream().filter(n -> n.startsWith("b")).findFirst();
        IO.println("tim thay: " + found.orElse("(khong co)"));

        // Nối chuỗi mà không mở optional ra giữa chừng.
        IO.println(found.map(String::toUpperCase).filter(n -> n.length() > 3).orElse("(khong dat)"));

        // TODO 1 (mục 2): Gọi Optional.of(null) và Optional.ofNullable(null).
        //   Dán exception của cái thứ nhất vào đây.
        //
        // TODO 2 (mục 3 + 10): Viết method expensiveDefault() có in log, rồi gọi nó trong
        //   orElse và orElseGet trên một Optional CÓ giá trị. Ghi lại cái nào in log.
        //
        // TODO 3 (mục 4): Dùng map + filter + orElseThrow để lấy độ dài tên dài hơn 3 ký tự,
        //   không dùng isPresent ở bất kỳ đâu.
        //
        // TODO 4 (mục 7): Có List<Optional<String>>, dùng flatMap(Optional::stream)
        //   để lấy List<String> chỉ gồm giá trị có mặt.
        //
        // TODO 5 (mục 8): Dùng ifPresentOrElse để in giá trị hoặc in thông báo không tìm thấy.
        //
        // TODO 6 (mục 5): Dựng record Book(String title, int year, List<String> authors),
        //   tìm cặp tác giả xuất bản chung nhiều nhất bằng groupingBy + counting.
    }
}
