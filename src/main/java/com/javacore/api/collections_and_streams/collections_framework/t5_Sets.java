package com.javacore.api.collections_and_streams.collections_framework;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Extending Collection with Set, SortedSet and NavigableSet
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 5/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/sets/">dev.java - Extending Collection with Set, SortedSet and NavigableSet</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - EXTENDING COLLECTION WITH SET, SORTEDSET AND NAVIGABLESET (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. KHÁM PHÁ INTERFACE SET
 * - (Exploring the Set Interface)
 * - Set cấm phần tử trùng và KHÔNG hứa hẹn thứ tự duyệt.
 * - {@code HashSet} (không thứ tự) và {@code LinkedHashSet} (giữ thứ tự chèn).
 * - Set không thêm method nào so với Collection, chỉ thêm RÀNG BUỘC.
 * <p>
 * 2. SORTEDSET
 * - (Extending Set with SortedSet)
 * - Cài đặt: {@code TreeSet}. Thứ tự do Comparable của phần tử hoặc Comparator truyền vào.
 * - {@code first()}, {@code last()}, {@code headSet()}, {@code tailSet()}, {@code subSet()}.
 * - Các *Set() trả về VIEW chứ không phải bản sao.
 * <p>
 * 3. NAVIGABLESET
 * - (Extending SortedSet with NavigableSet)
 * - Nạp chồng headSet/tailSet/subSet thêm tham số boolean (bao gồm biên hay không).
 * - {@code ceiling()}, {@code floor()}, {@code lower()}, {@code higher()},
 * {@code pollFirst()}, {@code pollLast()}, {@code descendingIterator()}, {@code descendingSet()}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. HỢP ĐỒNG equals/hashCode
 * - HashSet xác định trùng bằng hashCode() rồi equals(). Phần tử không override đúng hai
 * method này thì Set mất tác dụng khử trùng.
 * - TreeSet thì KHÔNG dùng equals mà dùng compareTo/compare. Hai phần tử compareTo trả về 0
 * bị coi là một, kể cả khi equals nói khác. Đây là lý do SortedSet "không nhất quán với equals"
 * được cảnh báo trong Javadoc.
 * <p>
 * 5. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Set của JS so sánh bằng SameValueZero (gần như ===), nên hai object khác tham chiếu luôn
 * là hai phần tử. Java thì tùy equals/hashCode do mình viết.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 6. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Set thêm method nào so với Collection?
 * - HashSet, LinkedHashSet, TreeSet khác nhau ở thứ tự và ở độ phức tạp thế nào?
 * - headSet trả về view hay bản sao? Thêm phần tử vào view thì sao?
 * - ceiling, floor, higher, lower khác nhau ở chỗ nào?
 * - TreeSet dùng gì để xác định hai phần tử là trùng?
 */
public class t5_Sets {

    void main() {
        // TODO 1 (mục 1): Thêm cùng một dãy phần tử vào HashSet, LinkedHashSet, TreeSet.
        //   In ra cả ba và ghi lại khác biệt về thứ tự.
        //
        List<String> strings = List.of("one", "two", "three", "four", "five", "six");
        Set<String> set = new HashSet<>();
        Set<String> hashSet = new LinkedHashSet<>();
        set.addAll(strings);
        hashSet.addAll(strings);
        set.forEach(IO::println);
        IO.println("==============");
        hashSet.forEach(IO::println);
        // TODO 2 (mục 2): Dùng TreeSet<String> với Comparator theo độ dài. Gọi first, last,
        //   headSet, tailSet, subSet và ghi lại kết quả.
        //
        // TODO 3 (mục 2): Gọi add() trên kết quả của headSet() với một phần tử NGOÀI khoảng.
        //   Dán exception nhận được vào đây.
        //
        // TODO 4 (mục 3): So sánh ceiling/floor/higher/lower trên cùng một giá trị có trong set
        //   và một giá trị không có trong set.
        //
        // TODO 5 (mục 4): Viết class Point KHÔNG override equals/hashCode, thêm hai điểm giống
        //   nhau vào HashSet. Rồi override lại và chạy lại. Ghi lại khác biệt.
    }
}
