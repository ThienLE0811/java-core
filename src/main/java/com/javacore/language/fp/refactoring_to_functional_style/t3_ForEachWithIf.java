package com.javacore.language.fp.refactoring_to_functional_style;

import java.util.List;

/**
 * Converting foreach with if
 * <p>
 * 0. Vị trí bài này
 * - Bước 16/41, bài 3/5 trong section Refactoring to Functional Style của lộ trình dev.java/learn.
 * - Một câu cho cả bài: foreach chuyển thành forEach() nội bộ, và if lọc phần tử chuyển thành filter() trên Stream.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/refactoring-to-functional-style/for-each-with-if/">dev.java - Converting foreach with if</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CONVERTING FOREACH WITH IF (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. LẶP VỚI FOREACH
 * - (Iterating with foreach)
 * - Java 5 giới thiệu cú pháp {@code for (String name : names)}.
 * - Bản chất bytecode: foreach được trình biên dịch dịch ra thành mã dùng {@code Iterator}
 * (vòng lặp {@code while (iterator.hasNext()) { String name = iterator.next(); ... }}).
 * - "In other words, the foreach is a nice concise syntax sugar for iteration with a while loop
 * over the elements provided by an Iterator."
 * <p>
 * 2. TỪ IMPERATIVE SANG FUNCTIONAL VỚI FOREACH
 * - (From Imperative to Functional Style)
 * - Vòng lặp foreach imperative chuyển thẳng sang method {@code forEach()} của functional style.
 * - Đây là INTERNAL ITERATOR: việc tiến tới phần tử tiếp theo được xử lý tự động và nội bộ
 * bởi thư viện, thay vì lập trình viên phải tự bước từng bước qua external iterator.
 * - {@code names.forEach(IO::println)}: trực tiếp gọi trên {@code Iterable}/{@code Collection}.
 * - Biến thể qua stream: {@code names.stream().forEach(IO::println)}.
 * - Điểm khác biệt quan trọng: {@code forEach()} có mặt ở cả {@code Collection<T>} lẫn {@code Stream<T>}.
 * Tuy nhiên, các thao tác trung gian như {@code filter()}, {@code map()} CHỈ có trên {@code Stream<T>}.
 * "This is by design in order to provide efficiency when multiple intermediate operations
 * may precede the terminal operation like forEach(), findFirst(), etc."
 * <p>
 * 3. CHỌN LỌC PHẦN TỬ BẰNG IF
 * - (Picking select elements with if)
 * - Trong imperative style, khi cần chọn lọc (ví dụ chỉ in các tên có độ dài 4):
 * {@code for (String name : names) { if (name.length() == 4) IO.println(name); }}
 * - Trong functional style, method {@code filter()} của Stream là sự thay thế trực tiếp cho {@code if}:
 * "The filter() method acts like a gate, it opens to let some elements pass through and
 * closes to reject or discard some elements, as the iteration moves forward."
 * - Nhận một lambda dạng {@code Predicate<T>}: trả về {@code true} thì cho qua, {@code false} thì loại bỏ.
 * <p>
 * 4. BẢNG ÁNH XẠ
 * - (Mappings)
 * - {@code foreach} duyệt đơn thuần             → {@code collection.forEach(...)}
 * - {@code foreach} có {@code if} lọc phần tử  → {@code collection.stream().filter(...).forEach(...)}
 * - "Anywhere you see a foreach loop, use the forEach() method directly on the collection.
 * If the body of the foreach has a if statement to selectively pick some value,
 * then use the stream() API with the call to the filter() method."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. Collection.forEach VS Stream.forEach
 * - Không có thao tác trung gian (filter/map) thì DÙNG {@code list.forEach(...)} trực tiếp:
 * nhanh hơn, không tốn chi phí khởi tạo pipeline của Stream.
 * - Có trung gian (hoặc cần parallel) mới mở {@code list.stream()}.
 * - Trong parallel stream: {@code forEach} không đảm bảo thứ tự gặp (encounter order);
 * nếu cần bảo toàn thứ tự, phải dùng {@code forEachOrdered}.
 * <p>
 * 6. FILTER KHÔNG PHẢI LÀ SHORT-CIRCUIT
 * - {@code filter()} luôn duyệt qua tất cả phần tử được đẩy đến nó (trừ khi có terminal op ngắt sớm).
 * - Muốn hành vi giống {@code if (...) { ...; break; }} (tìm thấy là dừng), phải kết hợp
 * {@code filter()} với các short-circuit operation: {@code findFirst()}, {@code findAny()},
 * {@code anyMatch()}.
 * <p>
 * 7. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - JS Array có {@code array.forEach()} và {@code array.filter()}.
 * - Khác biệt sống còn: {@code array.filter()} trong JS tạo ra một mảng mới ngay lập tức (eager evaluation).
 * Trong Java, {@code stream.filter()} là LAZY (lười biếng), các phần tử chỉ được kiểm tra khi
 * terminal operation (như {@code forEach}, {@code toList}) yêu cầu kéo dữ liệu, không sinh mảng trung gian.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vòng foreach ở mức bytecode dùng cơ chế gì?
 * - Khi nào nên dùng Collection.forEach, khi nào bắt buộc phải dùng stream().filter().forEach()?
 * - filter() nhận functional interface nào? Nguyên lý hoạt động ví như cánh cổng ra sao?
 * - Trong parallel stream, forEach và forEachOrdered khác nhau thế nào?
 * - Muốn biến vòng lặp if có break thành functional thì dùng filter kết hợp với gì?
 * - filter của Java Stream khác filter của mảng JavaScript ở điểm then chốt nào?
 */
public class t3_ForEachWithIf {

    void main() {
        List<String> names = List.of("Jack", "Paula", "Kate", "Peter");

        // ==================================================================
        // MỤC 2 - FOREACH ĐƠN THUẦN: DUYỆT TẤT CẢ PHẦN TỬ
        // ==================================================================

        // Imperative: dùng foreach của Java 5 (ẩn sau là Iterator).
        for (String name : names) {
            IO.println(name);
        }

        // Functional 1: trực tiếp dùng forEach trên Collection (gọn, không tốn chi phí dựng stream).
        names.forEach(IO::println);

        // Functional 2: thông qua stream (dùng khi sau này cần mở rộng pipeline).
        names.stream()
                .forEach(IO::println);

        // ==================================================================
        // MỤC 3 - FOREACH CÓ IF: LỌC PHẦN TỬ
        // ==================================================================

        // Imperative: if lồng bên trong thân vòng lặp.
        for (String name : names) {
            if (name.length() == 4) {
                IO.println(name);
            }
        }

        // Functional: if chuyển thành filter() trong pipeline của Stream.
        // filter đóng vai trò như cánh cổng: chỉ những ai thỏa mãn (true) mới được đi tiếp.
        names.stream()
                .filter(name -> name.length() == 4)
                .forEach(IO::println);
    }

    // TODO 1 (mục 2): Viết một danh sách chuỗi, dùng cả list.forEach() và list.stream().forEach().
    //   Khi nào thì nên chọn cách 1, khi nào bắt buộc phải chọn cách 2?
    //
    // TODO 2 (mục 3): Đổi một vòng for có 2 if lồng nhau:
    //   if (name.length() == 4) { if (name.startsWith("J")) { ... } }
    //   thành Stream pipeline: thử 2 cách (1 filter gộp điều kiện &&, hoặc 2 filter liên tiếp)
    //   và nhận xét cách nào dễ đọc hơn.
    //
    // TODO 3 (mục 5): Tạo một danh sách số, chạy song song với .parallelStream().forEach() và
    //   .parallelStream().forEachOrdered(). Quan sát thứ tự in ra console.
    //
    // TODO 4 (mục 6): Viết vòng for lặp tìm người đầu tiên có độ dài 4 rồi break; sau đó
    //   chuyển thành Stream dùng .filter(...).findFirst(). Nhận xét sự an toàn khi kết quả là Optional.
}
