package com.javacore.api.collections_and_streams.streams;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Creating Streams
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 3/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài này là bài tra cứu: nhiều factory method, ít khái niệm mới. Gõ hết một lượt rồi
 * đánh dấu những cái thật sự hay dùng.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/creating/">dev.java - Creating Streams</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING STREAMS (mục 1 đến 13)
 * ======================================================================
 * <p>
 * 1. VÌ SAO CẦN NHIỀU CÁCH TẠO STREAM
 * - (Creating a Stream)
 * - Ngoài {@code Collection.stream()} còn rất nhiều nguồn khác, biết đủ thì mới dùng được
 * Stream API ở khắp codebase chứ không chỉ trên collection.
 * <p>
 * 2. TỪ COLLECTION HOẶC ITERATOR
 * - (Creating a Stream from a Collection or an Iterator)
 * - {@code Collection.stream()}. Map KHÔNG có stream(), phải đi qua {@code keySet()},
 * {@code entrySet()} hoặc {@code values()}.
 * - Từ Iterator: {@code StreamSupport.stream(spliterator, parallel)}, khai báo kích thước
 * ước lượng và characteristics.
 * <p>
 * 3. STREAM RỖNG
 * - (Creating an Empty Stream)
 * - {@code Stream.empty()}. Dùng nhiều trong flatMap để loại phần tử không hợp lệ (xem bài 2 mục 4).
 * <p>
 * 4. TỪ VARARG HOẶC MẢNG
 * - (Creating a Stream from a Vararg or an Array)
 * - {@code Stream.of(a, b, c)} và {@code Arrays.stream(array)}.
 * <p>
 * 5. TỪ MỘT SUPPLIER
 * - (Creating a Stream from a Supplier)
 * - {@code Stream.generate(supplier)} cho stream VÔ HẠN, bắt buộc phải chặn bằng
 * {@code limit()} hoặc một toán tử short-circuit khác.
 * <p>
 * 6. TỪ UNARYOPERATOR VÀ HẠT GIỐNG
 * - (Creating a Stream from a UnaryOperator and a Seed)
 * - {@code Stream.iterate(seed, f)} vô hạn; bản Java SE 9 {@code Stream.iterate(seed, predicate, f)}
 * có điều kiện dừng, đọc như vòng for.
 * <p>
 * 7. TỪ KHOẢNG SỐ
 * - (Creating a Stream from a Range of Numbers)
 * - {@code IntStream.range(a, b)} và {@code LongStream.range()}: cận trên HỞ.
 * {@code rangeClosed()}: cận trên ĐÓNG.
 * - Kết hợp {@code mapToObj()} để duyệt mảng theo chỉ số.
 * <p>
 * 8. STREAM SỐ NGẪU NHIÊN
 * - (Creating a Stream of Random Numbers)
 * - {@code Random.ints()}, {@code longs()}, {@code doubles()}, có overload nhận số lượng và cận.
 * - Random có seed cố định thì dãy lặp lại được, tiện cho test.
 * <p>
 * 9. TỪ CÁC KÝ TỰ CỦA MỘT STRING
 * - (Creating a Stream from the Characters of a String)
 * - {@code String.chars()} trả về {@code IntStream} (code point), đổi lại thành ký tự bằng
 * {@code Character.toString(int)} (Java SE 11).
 * <p>
 * 10. TỪ CÁC DÒNG CỦA FILE VĂN BẢN
 * - (Creating a Stream from the Lines of a Text File)
 * - {@code BufferedReader.lines()} hoặc {@code Files.lines(path)}.
 * - Stream này là {@code AutoCloseable}, BẮT BUỘC dùng try-with-resources.
 * <p>
 * 11. TỪ BIỂU THỨC CHÍNH QUY
 * - (Creating a Stream from a Regular Expression)
 * - {@code Pattern.compile(regex).splitAsStream(text)}: không tạo mảng trung gian như
 * {@code String.split()} rồi {@code Arrays.stream()}.
 * <p>
 * 12. BUILDER PATTERN
 * - (Creating a Stream with the Builder Pattern)
 * - {@code Stream.builder()}, {@code add()} nhiều lần, rồi {@code build()}.
 * Gọi {@code add()} sau {@code build()} thì ném {@code IllegalStateException}.
 * <p>
 * 13. TỪ NGUỒN HTTP
 * - (Creating a Stream on an HTTP Source)
 * - {@code HttpResponse.BodyHandlers.ofLines()} của HTTP Client API (JDK 11), xử lý body
 * theo dòng mà không nạp hết vào bộ nhớ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 14. BẪY STREAM VÔ HẠN
 * - {@code generate()} và {@code iterate()} hai tham số cho stream vô hạn. Ghép với toán tử
 * stateful như {@code sorted()} hoặc {@code distinct()} là treo chương trình, không có lỗi biên dịch.
 * <p>
 * 15. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code Stream.iterate} gần với generator function {@code function*} của JS: sinh giá trị lười.
 * - {@code IntStream.range(0, n)} thay cho {@code Array.from({length: n}, (_, i) =&gt; i)}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 16. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao Map không có method stream()? Lấy stream từ Map bằng cách nào?
 * - range và rangeClosed khác nhau ở đâu?
 * - Stream nào bắt buộc phải đóng? Không đóng thì hậu quả gì?
 * - generate và iterate khác nhau ở chỗ nào, cái nào biết được giá trị trước đó?
 * - splitAsStream hơn String.split ở điểm gì?
 */
public class t3_CreatingStreams {

    void main() {
        // Stream vô hạn + limit: không có limit là treo.
        Stream.iterate(1, i -> i * 2)
                .limit(8)
                .forEach(i -> IO.println("luy thua 2: " + i));

        // range cận trên hở: in 0..4
        IO.println(IntStream.range(0, 5).boxed().toList());

        // TODO 1 (mục 2): Từ một Map<String, Integer>, in ra các entry có value lớn hơn 10
        //   bằng entrySet().stream(). So sánh với cách dùng keySet().
        //
        // TODO 2 (mục 6): Viết lại vòng lặp "i = 1; i < 100; i *= 3" bằng
        //   Stream.iterate(seed, predicate, next) ba tham số.
        //
        // TODO 3 (mục 7 + 9): Đếm số nguyên âm trong một chuỗi bằng String.chars().
        //   Nhớ rằng chars() cho IntStream, không phải Stream<Character>.
        //
        // TODO 4 (mục 10): Đọc chính file nguồn này bằng Files.lines() trong try-with-resources,
        //   đếm số dòng bắt đầu bằng " * ". Bỏ try-with-resources đi thì rủi ro là gì?
        //
        // TODO 5 (mục 12): Dùng Stream.builder() dựng một stream, gọi build() rồi gọi add() tiếp.
        //   Dán exception nhận được vào đây.
        //
        // TODO 6 (mục 14): Thử Stream.generate(Math::random).distinct().limit(5) và giải thích
        //   vì sao chương trình không dừng (chạy xong nhớ ghi lại rồi xóa, đừng để trong file).
    }
}
