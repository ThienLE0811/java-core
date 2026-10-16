package com.javacore.api.collections_and_streams.streams;

import java.util.List;

/**
 * Adding Intermediate Operations on a Stream
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 2/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/intermediate-operation/">dev.java - Adding Intermediate Operations on a Stream</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - ADDING INTERMEDIATE OPERATIONS ON A STREAM (mục 1 đến 8)
 * ======================================================================
 * <p>
 * 1. MAP - ÁNH XẠ SANG STREAM KHÁC
 * - (Mapping a Stream to Another Stream)
 * - {@code map(Function)} đổi kiểu phần tử, giữ nguyên số lượng.
 * - Bản chuyên dụng: {@code mapToInt()}, {@code mapToLong()}, {@code mapToDouble()} cho ra
 * stream nguyên thủy, mở khóa các thao tác như {@code summaryStatistics()}.
 * <p>
 * 2. FILTER - LỌC
 * - (Filtering a Stream)
 * - "discards some elements processed by a stream with a predicate".
 * - {@code count()} là terminal operation trả về {@code long} số phần tử còn lại.
 * <p>
 * 3. FLATMAP - XỬ LÝ QUAN HỆ 1:P
 * - (Flatmapping a Stream to Handle 1:p Relations)
 * - "opens one-to-many relations between objects and creates streams on these relations".
 * - Function truyền vào trả về một STREAM: {@code Stream<T>} thành {@code Stream<Stream<U>>}
 * rồi được làm phẳng thành {@code Stream<U>}.
 * <p>
 * 4. FLATMAP VÀ MAPMULTI ĐỂ KIỂM SOÁT PHÉP BIẾN ĐỔI
 * - (Using Flatmap and MapMulti to Validate Elements Transformation)
 * - Mẹo flatMap: trả về {@code Stream.of(x)} nếu hợp lệ, {@code Stream.empty()} nếu không,
 * tức là vừa map vừa filter trong một bước.
 * - {@code mapMulti()} (Java SE 16) nhận một {@code BiConsumer}, hiệu quả hơn vì không phải
 * tạo stream trung gian cho từng phần tử.
 * <p>
 * 5. DISTINCT VÀ SORTED
 * - (Removing Duplicates and Sorting a Stream)
 * - {@code distinct()} dựa trên {@code hashCode()} và {@code equals()};
 * {@code sorted()} dựa trên {@code Comparator} (hoặc Comparable nếu không truyền gì).
 * - Cả hai là STATEFUL intermediate operation: phải đệm dữ liệu trong bộ nhớ.
 * <p>
 * 6. LIMIT, SKIP, TAKEWHILE, DROPWHILE
 * - (Limiting and Skipping the Elements of a Stream)
 * - {@code skip()} và {@code limit()} chọn theo CHỈ SỐ.
 * - Java SE 9 thêm {@code takeWhile()} và {@code dropWhile()} chọn theo ĐIỀU KIỆN; trang gốc
 * ví chúng "like doors": cánh cửa đóng lại / mở ra một lần duy nhất, không bật lại.
 * <p>
 * 7. NỐI HAI STREAM
 * - (Concatenating Streams)
 * - {@code Stream.concat(a, b)} giữ được đặc tính SIZED.
 * - Nối nhiều stream thì nên dùng {@code flatMap()}, đổi lại mất thông tin kích thước.
 * <p>
 * 8. GỠ LỖI STREAM
 * - (Debugging Streams)
 * - {@code peek(Consumer)} nhìn trộm phần tử đang chạy qua pipeline, dành cho gỡ lỗi,
 * không dùng trong logic production.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 9. STATELESS SO VỚI STATEFUL
 * - Stateless ({@code map}, {@code filter}, {@code flatMap}): xử lý xong phần tử nào là quên ngay.
 * - Stateful ({@code sorted}, {@code distinct}, {@code limit}, {@code skip}): phải nhớ, tốn bộ nhớ
 * và là chỗ parallel stream bị chậm (xem bài 11).
 * - {@code sorted()} trên stream vô hạn thì không bao giờ kết thúc.
 * <p>
 * 10. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code flatMap} có ở cả hai, nhưng JS nhận mảng trả về, Java nhận STREAM trả về.
 * - JS không có {@code distinct()}, phải đi đường {@code [...new Set(arr)]}.
 * - {@code peek} gần giống chèn {@code console.log} giữa chuỗi, khác ở chỗ peek có thể không chạy
 * nếu pipeline bị short-circuit hoặc thiếu terminal operation.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - map và flatMap khác nhau ở kiểu trả về của Function thế nào?
 * - Khi nào dùng mapMulti thay vì flatMap?
 * - distinct() dựa vào method nào của object? Quên override thì hỏng ra sao?
 * - takeWhile khác filter chỗ nào trên cùng một danh sách?
 * - Toán tử nào là stateful, vì sao điều đó quan trọng?
 */
public class t2_IntermediateOperations {

    void main() {
        List<String> words = List.of("apple", "banana", "avocado", "cherry", "apricot");

        // Pipeline nhiều bước: mỗi bước trả về một Stream mới, chưa chạy gì cho tới toList().
        List<String> result = words.stream()
                .filter(w -> w.startsWith("a"))
                .map(String::toUpperCase)
                .sorted()
                .toList();
        IO.println(result);

        // TODO 1 (mục 3): Có List<List<Integer>>, dùng flatMap để ra một Stream<Integer> phẳng.
        //   Thử làm lại bằng map và ghi lại kiểu mà trình biên dịch báo.
        //
        // TODO 2 (mục 4): Dùng flatMap trả về Stream.of(x) / Stream.empty() để vừa lọc vừa biến đổi
        //   một List<String> sang List<Integer> (bỏ qua chuỗi không parse được số).
        //
        // TODO 3 (mục 4): Viết lại TODO 2 bằng mapMulti. So sánh độ dài và độ dễ đọc.
        //
        // TODO 4 (mục 6): Trên List.of(1,2,3,4,1,2), so sánh kết quả của
        //   takeWhile(i -> i < 3), dropWhile(i -> i < 3) và filter(i -> i < 3). Giải thích chênh lệch.
        //
        // TODO 5 (mục 8): Chèn peek vào giữa pipeline nhưng BỎ terminal operation.
        //   Chạy thử và ghi lại peek có in gì không, vì sao.
    }
}
