package com.javacore.api.collections_and_streams.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

/**
 * Implementing the Collector Interface
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 9/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Mức sâu nhất của chuỗi collector: tự cài {@code Collector} để hiểu cái mình đã dùng
 * ở bài 7 và 8 thật ra chạy thế nào. Thực tế ít khi phải viết tay như vậy.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/collector-interface/">dev.java - Implementing the Collector Interface</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - IMPLEMENTING THE COLLECTOR INTERFACE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. VÌ SAO PHẢI CÀI ĐẶT COLLECTOR
 * - (Why Would You Implement the Collector Interface?)
 * - Ba mức tạo collector riêng, từ dễ đến khó:
 * ghép các collector sẵn có của {@code Collectors} (bài 7);
 * dùng {@code collect(supplier, accumulator, combiner)} hoặc {@code Collector.of()} (bài 8);
 * cài thẳng interface {@code Collector} - linh hoạt nhất.
 * <p>
 * 2. BA THAM SỐ KIỂU CỦA COLLECTOR
 * - (Understanding the Parameter Types of Collector)
 * - {@code Collector<T, A, R>}: T là kiểu phần tử của stream, A là kiểu CONTAINER trung gian
 * khả biến, R là kiểu kết quả cuối.
 * - Ví dụ {@code toList()} có A là {@code ArrayList}, {@code toSet()} có A là {@code HashSet}.
 * - A thường không lộ ra ngoài, nên hay thấy wildcard trong chữ ký method.
 * <p>
 * 3. BA ĐẶC TÍNH CỦA COLLECTOR
 * - (Understanding the Characteristics of a Collector)
 * - {@code IDENTITY_FINISH}: finisher là hàm đồng nhất, bỏ qua được (A chính là R).
 * - {@code UNORDERED}: kết quả không phụ thuộc thứ tự gặp phần tử.
 * - {@code CONCURRENT}: container chịu được nhiều thread ghi cùng lúc.
 * - Báo đúng đặc tính thì JDK mới tối ưu được; báo sai là sai kết quả khi chạy song song.
 * <p>
 * 4. TỰ CÀI TOLIST VÀ TOSET
 * - (Implementing the toList() and toSet() Collector)
 * - Cài bốn method: {@code supplier()}, {@code accumulator()}, {@code combiner()},
 * {@code characteristics()} (finisher bỏ qua được nhờ IDENTITY_FINISH).
 * - Đổi từ toList sang toSet: supplier thành {@code HashSet::new} và thêm đặc tính UNORDERED.
 * <p>
 * 5. TỰ CÀI JOINING
 * - (Implementing the joining() Collector)
 * - Container là {@code StringBuffer}, finisher là {@code toString()}.
 * - Collector này KHÔNG có đặc tính nào: giữ thứ tự (không UNORDERED), không concurrent,
 * và finisher không phải hàm đồng nhất (không IDENTITY_FINISH).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. KHAI SAI ĐẶC TÍNH LÀ BUG ÂM THẦM
 * - Khai IDENTITY_FINISH trong khi vẫn cần finisher thì bị {@code ClassCastException} lúc chạy.
 * - Khai CONCURRENT với container không thread-safe thì mất dữ liệu ngẫu nhiên, không exception.
 * - Cả hai chỉ lộ ra khi chạy song song, nên phải chủ động test parallel.
 * <p>
 * 7. KHI NÀO THẬT SỰ CẦN TỚI MỨC NÀY
 * - 95% trường hợp {@code Collectors} + {@code collectingAndThen} + {@code teeing} là đủ.
 * - Cài tay chủ yếu để học, hoặc khi viết thư viện cần collector tái dùng có tên rõ ràng.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba tham số kiểu T, A, R lần lượt là gì? Lấy ví dụ với toSet().
 * - IDENTITY_FINISH nghĩa là gì, khai sai thì lỗi gì?
 * - Vì sao toSet() có UNORDERED còn joining() thì không?
 * - Khi nào JDK gọi combiner()?
 * - Ba mức tạo collector riêng, chọn mức nào cho tình huống nào?
 */
public class t9_CollectorInterface {

    void main() {
        List<String> words = List.of("alpha", "beta", "gamma");

        // Nhắc lại bài 8: Collector.of là lối tắt của việc cài tay interface bên dưới.
        Collector<String, ?, List<String>> myToList = Collector.of(
                ArrayList::new,
                List::add,
                (a, b) -> { a.addAll(b); return a; },
                Collector.Characteristics.IDENTITY_FINISH);
        IO.println(words.stream().collect(myToList));

        // TODO 1 (mục 4): Viết class MyToListCollector implements Collector<T, List<T>, List<T>>,
        //   cài đủ bốn method và chạy thử. So sánh với Collector.of ở trên.
        //
        // TODO 2 (mục 4): Sửa collector ở TODO 1 thành MyToSetCollector (HashSet + UNORDERED).
        //
        // TODO 3 (mục 5): Cài MyJoiningCollector dùng StringBuffer, finisher là toString().
        //   Kiểm tra characteristics() trả về tập rỗng.
        //
        // TODO 4 (mục 6): Ở MyJoiningCollector, cố tình khai thêm IDENTITY_FINISH rồi chạy.
        //   Dán exception nhận được vào đây và giải thích vì sao là ClassCastException.
        //
        // TODO 5 (mục 3): Chạy collector tự viết trên parallelStream với list lớn,
        //   kiểm tra kết quả có đúng và ổn định không.
    }
}
