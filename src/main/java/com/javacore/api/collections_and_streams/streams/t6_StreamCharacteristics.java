package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.Spliterator;

/**
 * Finding the Characteristics of a Stream
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 6/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài này giải thích VÌ SAO cùng một pipeline lại nhanh chậm khác nhau tùy nguồn dữ liệu.
 * Nền tảng để đọc bài 11 (parallel stream).
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/characteristics/">dev.java - Finding the Characteristics of a Stream</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - FINDING THE CHARACTERISTICS OF A STREAM (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. ĐẶC TÍNH CỦA MỘT STREAM
 * - (Characteristics of a Stream)
 * - "The Stream API relies on a special object, an instance of the {@code Spliterator} interface."
 * - Sáu đặc tính: ORDERED, DISTINCT, NONNULL, SORTED, SIZED, SUBSIZED.
 * - Đọc bằng {@code Spliterator.characteristics()} và {@code Spliterator.hasCharacteristics()}.
 * - Mỗi toán tử trung gian GIỮ hoặc MẤT từng đặc tính; stream còn giữ càng nhiều thì càng tối ưu được.
 * <p>
 * 2. ORDERED
 * - (Ordered Streams)
 * - Có từ {@code List}, {@code Files.lines(path)}, {@code Pattern.splitAsStream()}.
 * - {@code unordered()} cố tình BỎ đặc tính này để giảm chi phí khi chạy song song.
 * <p>
 * 3. SORTED
 * - (Sorted Streams)
 * - Có từ {@code TreeSet} hoặc sau khi gọi {@code sorted()}.
 * - {@code map()} và {@code flatMap()} LÀM MẤT (vì hàm map có thể phá thứ tự);
 * {@code filter()} thì GIỮ.
 * <p>
 * 4. DISTINCT
 * - (Distinct Streams)
 * - "A DISTINCT stream is a stream with no duplicates among the elements it is processing."
 * - Có từ {@code HashSet} hoặc sau {@code distinct()}. {@code filter()} giữ, {@code map()} mất.
 * <p>
 * 5. NONNULL
 * - (Non-Null Streams)
 * - Có từ {@code ArrayDeque}, các cấu trúc concurrent như {@code ArrayBlockingQueue},
 * và {@code Files.lines()}.
 * - {@code filter()}, {@code sorted()}, {@code distinct()} giữ; {@code map()}, {@code flatMap()} mất.
 * <p>
 * 6. SIZED VÀ SUBSIZED
 * - (Sized and Subsized Streams)
 * - SIZED: biết trước số phần tử (mọi {@code Collection}).
 * - SUBSIZED: chẻ đôi ra vẫn được hai stream SIZED, ví dụ {@code ArrayList}.
 * {@code HashSet} là SIZED nhưng KHÔNG SUBSIZED.
 * - {@code map()} và {@code sorted()} giữ cả hai; {@code flatMap()} và {@code filter()} làm mất
 * (vì không biết trước còn lại bao nhiêu phần tử).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. ĐẶC TÍNH LÀ NGUỒN GỐC CỦA CÁC TỐI ƯU NGẦM
 * - {@code list.stream().count()}: nếu stream còn SIZED, JDK trả thẳng kích thước, KHÔNG duyệt phần tử.
 * - Hệ quả thực tế: {@code list.stream().peek(print).count()} có thể không in gì cả.
 * Thêm một {@code filter()} vào là mất SIZED, lúc đó peek mới chạy.
 * - {@code distinct()} trên stream đã DISTINCT sẵn là no-op; {@code sorted()} trên stream SORTED cũng vậy.
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - JS không có khái niệm tương đương. Mảng JS luôn "ordered + sized", mọi thao tác đều eager
 * nên không có chỗ cho loại tối ưu này. Đây là phần tư duy phải xây mới, không bắc cầu được.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Spliterator đóng vai trò gì trong Stream API?
 * - Kể sáu đặc tính và một nguồn điển hình cho mỗi cái.
 * - Vì sao map() làm mất SORTED, DISTINCT, NONNULL nhưng filter() thì không?
 * - SIZED khác SUBSIZED thế nào? Cho ví dụ cấu trúc có cái này mà không có cái kia.
 * - Vì sao peek() đứng trước count() có khi không in gì?
 */
public class t6_StreamCharacteristics {

    void main() {
        List<String> list = List.of("b", "a", "c", "a");

        // Đọc đặc tính trực tiếp từ spliterator của nguồn.
        Spliterator<String> fromList = list.spliterator();
        IO.println("ORDERED: " + fromList.hasCharacteristics(Spliterator.ORDERED));
        IO.println("SIZED  : " + fromList.hasCharacteristics(Spliterator.SIZED));
        IO.println("SORTED : " + fromList.hasCharacteristics(Spliterator.SORTED));

        // TODO 1 (mục 1): In đặc tính của spliterator lấy từ List, HashSet, TreeSet, ArrayDeque.
        //   Lập bảng đối chiếu sáu đặc tính cho bốn nguồn này.
        //
        // TODO 2 (mục 6): So sánh hasCharacteristics(SUBSIZED) giữa ArrayList và HashSet.
        //
        // TODO 3 (mục 7): Chạy list.stream().peek(IO::println).count() rồi chạy
        //   list.stream().filter(s -> true).peek(IO::println).count().
        //   Ghi lại cái nào in, cái nào không, và giải thích bằng đặc tính SIZED.
        //
        // TODO 4 (mục 3): Gọi sorted() hai lần liên tiếp và đo thời gian trên list lớn.
        //   Lần thứ hai có tốn thêm gì không?
        //
        // TODO 5 (mục 2): Dùng unordered() trước một parallel stream rồi so sánh thứ tự
        //   kết quả của forEach với bản không unordered.
    }
}
