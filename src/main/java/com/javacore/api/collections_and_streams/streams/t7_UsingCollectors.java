package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Using a Collector as a Terminal Operation
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 7/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài nặng ký nhất phần giữa. {@code groupingBy} + downstream collector là thứ dùng
 * hằng ngày trong code production.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/using-collectors/">dev.java - Using a Collector as a Terminal Operation</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - USING A COLLECTOR AS A TERMINAL OPERATION (mục 1 đến 10)
 * ======================================================================
 * <p>
 * 1. GOM PHẦN TỬ BẰNG COLLECTOR
 * - (Collecting Stream Elements with a Collector)
 * - {@code collect()} là terminal operation nhận một {@code Collector}.
 * - Lớp factory {@code Collectors} chứa sẵn phần lớn collector cần dùng.
 * <p>
 * 2. GOM VÀO COLLECTION
 * - (Collecting in a Collection)
 * - {@code toList()}, {@code toSet()}, {@code toCollection(supplier)}.
 * - Bản bất biến: {@code toUnmodifiableList()}, {@code toUnmodifiableSet()}.
 * <p>
 * 3. ĐẾM BẰNG COLLECTOR
 * - (Counting with a Collector)
 * - {@code counting()} cho kết quả y hệt {@code count()}, nhưng dùng được làm DOWNSTREAM
 * bên trong {@code groupingBy()} - đó mới là lý do nó tồn tại.
 * <p>
 * 4. GOM THÀNH CHUỖI
 * - (Collecting in a String of Characters)
 * - {@code joining()}, {@code joining(sep)}, {@code joining(sep, prefix, suffix)}.
 * <p>
 * 5. CHIA ĐÔI THEO PREDICATE
 * - (Partitioning Elements with a Predicate)
 * - {@code partitioningBy(predicate)} cho {@code Map<Boolean, List<T>>}, có overload nhận downstream.
 * - Map luôn có ĐỦ hai khóa true và false, kể cả khi một bên rỗng.
 * <p>
 * 6. NHÓM BẰNG GROUPINGBY
 * - (Collecting in a Map with Grouping By)
 * - {@code groupingBy(classifier)} cho {@code Map<K, List<T>>} - dựng histogram.
 * - Overload {@code groupingBy(classifier, downstream)} đổi phần giá trị thành thứ khác List.
 * <p>
 * 7. DỰNG MAP BẰNG TOMAP
 * - (Collecting in a Map with To Map)
 * - {@code toMap(keyMapper, valueMapper)}; TRÙNG KHÓA thì ném {@code IllegalStateException}.
 * - Overload thêm {@code BinaryOperator} để xử lý trùng khóa. Có bản {@code toConcurrentMap()}.
 * <p>
 * 8. LẤY GIÁ TRỊ LỚN NHẤT TỪ HISTOGRAM
 * - (Extracting Maxes from a Histogram)
 * - {@code Stream.max()} kết hợp {@code Map.Entry.comparingByValue()}.
 * - Muốn biết có NHIỀU giá trị cùng lớn nhất hay không thì đảo map bằng collector mapping.
 * <p>
 * 9. COLLECTOR TRUNG GIAN
 * - (Using Intermediate Collectors)
 * - {@code mapping()}, {@code filtering()}, {@code flatMapping()} mô phỏng các intermediate
 * operation ngay bên trong collector; bắt buộc phải có downstream đi kèm.
 * <p>
 * 10. COLLECTOR ĐẦU CUỐI
 * - (Using Terminal Collectors)
 * - {@code maxBy()}, {@code minBy()}, {@code summingInt()}, {@code averagingInt()}...
 * tương ứng các terminal operation của Stream nhưng dùng được làm downstream.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 11. BẪY NULL CỦA TOMAP VÀ GROUPINGBY
 * - {@code toMap()} ném {@code NullPointerException} nếu valueMapper trả về null
 * (vì nó dùng {@code Map.merge()} bên trong).
 * - {@code groupingBy()} ném NPE nếu classifier trả về null khóa.
 * - Gặp tình huống này thì dùng {@code Collectors.toMap} với HashMap tự cấp qua overload bốn tham số,
 * hoặc lọc null trước.
 * <p>
 * 12. ĐỌC NGƯỢC TỪ TRONG RA NGOÀI
 * - {@code groupingBy(f, mapping(g, toList()))} đọc là: nhóm theo f, trong mỗi nhóm map qua g
 * rồi gom thành List. Mẹo đọc: downstream luôn là tham số CUỐI.
 * <p>
 * 13. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code groupingBy} tương đương {@code Object.groupBy} / {@code Map.groupBy} (ES2024),
 * nhưng JS không có khái niệm downstream nên muốn đếm theo nhóm vẫn phải tự lặp lần nữa.
 * - {@code joining} tương đương {@code Array.prototype.join}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 14. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao cần counting() khi đã có count()?
 * - partitioningBy khác groupingBy(predicate) ở điểm nào về khóa của map kết quả?
 * - toMap gặp khóa trùng thì sao? Sửa bằng cách nào?
 * - Downstream collector là gì, viết một ví dụ groupingBy hai tầng.
 * - mapping() khác map() của Stream ở chỗ nào về vị trí áp dụng?
 */
public class t7_UsingCollectors {

    void main() {
        List<String> words = List.of("apple", "avocado", "banana", "blueberry", "cherry");

        // groupingBy đơn giản: Map<Character, List<String>>
        Map<Character, List<String>> byFirstLetter =
                words.stream().collect(Collectors.groupingBy(w -> w.charAt(0)));
        IO.println(byFirstLetter);

        // groupingBy + downstream: đếm theo nhóm thay vì giữ cả list.
        Map<Character, Long> countByFirstLetter =
                words.stream().collect(Collectors.groupingBy(w -> w.charAt(0), Collectors.counting()));
        IO.println(countByFirstLetter);

        // TODO 1 (mục 4): Dùng joining(", ", "[", "]") để in danh sách thành một chuỗi.
        //
        // TODO 2 (mục 5): partitioningBy theo độ dài lớn hơn 6. In map kết quả và kiểm tra
        //   nó có đủ cả khóa true lẫn false khi một bên rỗng không.
        //
        // TODO 3 (mục 6 + 9): Nhóm theo chữ cái đầu nhưng giá trị là List ĐỘ DÀI của từ,
        //   dùng groupingBy kết hợp mapping.
        //
        // TODO 4 (mục 7): Dựng Map<String, Integer> từ tên sang độ dài bằng toMap.
        //   Thêm một phần tử trùng tên rồi dán exception nhận được vào đây, sau đó
        //   sửa bằng overload có BinaryOperator.
        //
        // TODO 5 (mục 8): Từ countByFirstLetter, tìm chữ cái có nhiều từ nhất bằng
        //   Map.Entry.comparingByValue().
        //
        // TODO 6 (mục 11): Gọi toMap với valueMapper trả về null. Dán NPE nhận được
        //   và ghi lại vì sao Map.merge gây ra nó.
    }
}
