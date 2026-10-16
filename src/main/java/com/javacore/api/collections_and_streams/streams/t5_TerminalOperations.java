package com.javacore.api.collections_and_streams.streams;

import java.util.Comparator;
import java.util.List;

/**
 * Adding a Terminal Operation on a Stream
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 5/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài 4 nói về reduce thủ công; bài này là danh sách các terminal operation làm sẵn,
 * an toàn hơn và nên ưu tiên dùng.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/terminal-operations/">dev.java - Adding a Terminal Operation on a Stream</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - ADDING A TERMINAL OPERATION ON A STREAM (mục 1 đến 8)
 * ======================================================================
 * <p>
 * 1. TRÁNH DÙNG REDUCE
 * - (Avoiding the Use of the Reduce Method)
 * - {@code reduce()} buộc phải tự lo tính kết hợp và phần tử đơn vị nên dễ sai.
 * - Có sẵn {@code sum()}, {@code min()}, {@code max()}... thì dùng, vừa ngắn vừa an toàn.
 * <p>
 * 2. ĐẾM PHẦN TỬ
 * - (Counting the Elements Processed by a Stream)
 * - {@code count()} trả về {@code long} chứ không phải {@code int}, vì {@code flatMap()}
 * có thể nhân số phần tử lên vượt {@code Integer.MAX_VALUE}.
 * <p>
 * 3. TIÊU THỤ TỪNG PHẦN TỬ
 * - (Consuming Each Element One by One)
 * - {@code forEach(Consumer)}. Dùng nó để cộng dồn vào biến ngoài là ý tưởng tồi:
 * chậm và không song song hóa được. Collector mới là đường đúng.
 * <p>
 * 4. GOM VÀO COLLECTION HOẶC MẢNG
 * - (Collecting Stream Elements in a Collection, or an Array)
 * - {@code Collectors.toList()} cho {@code ArrayList} SỬA ĐƯỢC.
 * - {@code Stream.toList()} (Java SE 16) cho list KHÔNG SỬA ĐƯỢC - khác nhau ở điểm này.
 * - Còn có {@code Collectors.toSet()}, {@code Collectors.toCollection(supplier)},
 * và các overload {@code toArray()}.
 * <p>
 * 5. LẤY MIN VÀ MAX
 * - (Extracting the Maximum and the Minimum of a Stream)
 * - Stream chuyên dụng ({@code IntStream}...) có {@code max()}, {@code min()} không cần comparator,
 * trả về {@code OptionalInt} và tương tự.
 * - {@code Stream} thường bắt buộc truyền {@code Comparator}.
 * <p>
 * 6. TÌM MỘT PHẦN TỬ
 * - (Finding an Element in a Stream)
 * - {@code findFirst()} lấy phần tử đầu theo thứ tự; {@code findAny()} lấy phần tử bất kỳ
 * và nhanh hơn trên stream song song. Cả hai trả về {@code Optional}.
 * <p>
 * 7. KIỂM TRA KHỚP PREDICATE
 * - (Checking if the Elements of a Stream Match a Predicate)
 * - {@code anyMatch()}, {@code allMatch()}, {@code noneMatch()}.
 * - Trên stream RỖNG lần lượt trả về false, true, true. Nhớ kỹ chỗ này.
 * <p>
 * 8. SHORT-CIRCUIT
 * - (Short-Circuiting the Processing of a Stream)
 * - {@code findFirst()}, {@code findAny()}, {@code anyMatch()}, {@code allMatch()},
 * {@code noneMatch()} dừng ngay khi đủ kết luận, không cần duyệt hết nguồn.
 * - Nhờ vậy mới dùng được trên stream vô hạn.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 9. BA LOẠI LIST TRẢ VỀ, BA HÀNH VI KHÁC NHAU
 * - {@code collect(Collectors.toList())}: ArrayList, sửa được, cho phép null.
 * - {@code collect(Collectors.toUnmodifiableList())}: không sửa được, CẤM null.
 * - {@code stream.toList()}: không sửa được, CHO phép null.
 * - Chọn nhầm thì lỗi chỉ lộ ra lúc chạy dưới dạng {@code UnsupportedOperationException}
 * hoặc {@code NullPointerException}.
 * <p>
 * 10. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code anyMatch/allMatch/noneMatch} ứng với {@code some/every/!some} của JS;
 * {@code findFirst} ứng với {@code find} nhưng trả Optional thay vì undefined.
 * - {@code forEach} của Java không đảm bảo thứ tự khi chạy song song, {@code forEachOrdered} mới đảm bảo.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao count() trả về long?
 * - Ba cách tạo List từ stream khác nhau ở tính sửa được và null thế nào?
 * - allMatch trên stream rỗng trả về gì, vì sao hợp lý?
 * - findFirst và findAny nên dùng cái nào khi chạy song song?
 * - Toán tử nào short-circuit, điều đó cho phép làm được gì với stream vô hạn?
 */
public class t5_TerminalOperations {

    void main() {
        List<String> names = List.of("an", "binh", "chi", "dung", "an");

        IO.println("so phan tu : " + names.stream().distinct().count());
        IO.println("co ten dai 4: " + names.stream().anyMatch(n -> n.length() == 4));
        IO.println("dai nhat   : " + names.stream().max(Comparator.comparing(String::length)).orElseThrow());

        // TODO 1 (mục 4): Tạo list bằng cả ba cách (Collectors.toList, toUnmodifiableList,
        //   stream.toList) rồi thử add() vào từng cái. Ghi lại cái nào ném exception gì.
        //
        // TODO 2 (mục 4): Dùng Collectors.toCollection(TreeSet::new) để gom vào tập đã sắp xếp.
        //
        // TODO 3 (mục 7): Gọi anyMatch, allMatch, noneMatch trên Stream.empty() và
        //   đối chiếu với ba giá trị false/true/true trong mục 7.
        //
        // TODO 4 (mục 8): Dùng findFirst() trên Stream.iterate (vô hạn) kèm filter.
        //   Chương trình có dừng không? Vì sao?
        //
        // TODO 5 (mục 3): Cộng dồn vào một biến ngoài bằng forEach trên parallelStream
        //   với list vài chục nghìn phần tử. Chạy vài lần, ghi lại kết quả có ổn định không.
    }
}
