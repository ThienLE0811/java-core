package com.javacore.api.collections_and_streams.collections_framework;

import java.util.TreeMap;

/**
 * Keeping Keys Sorted with SortedMap and NavigableMap
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 11/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/sorted-maps/">dev.java - Keeping Keys Sorted with SortedMap and NavigableMap</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - KEEPING KEYS SORTED WITH SORTEDMAP AND NAVIGABLEMAP (mục 1 đến 2)
 * ======================================================================
 * <p>
 * 1. METHOD DO SORTEDMAP THÊM VÀO
 * - (Methods Added by SortedMap)
 * - Cài đặt: {@code TreeMap}. Khóa phải Comparable, hoặc truyền Comparator lúc tạo.
 * - {@code firstKey()}, {@code lastKey()}, {@code headMap()}, {@code tailMap()},
 * {@code subMap()} - các *Map() trả về VIEW.
 * <p>
 * 2. METHOD DO NAVIGABLEMAP THÊM VÀO
 * - (Methods Added by NavigableMap)
 * - 2.1 Truy cập khóa hoặc entry cụ thể: {@code ceilingKey()}, {@code floorKey()},
 * {@code higherKey()}, {@code lowerKey()} (và các bản *Entry tương ứng).
 * - 2.2 Dùng map như hàng đợi: {@code pollFirstEntry()}, {@code pollLastEntry()}.
 * - 2.3 Duyệt ngược: {@code descendingKeySet()}, {@code descendingMap()},
 * {@code navigableKeySet()}.
 * - 2.4 View map con: {@code subMap()}, {@code headMap()}, {@code tailMap()} có thêm tham số
 * boolean để chọn bao gồm biên hay không.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 3. TreeMap SO VỚI HashMap
 * - TreeMap là cây đỏ-đen: get/put là O(log n), nhưng khóa luôn có thứ tự.
 * - TreeMap so khóa bằng compareTo/compare, KHÔNG dùng equals. Comparator không nhất quán với
 * equals sẽ làm map "nuốt" khóa mà mình tưởng là khác nhau.
 * - TreeMap không nhận khóa null (vì phải so sánh), HashMap thì nhận một khóa null.
 * <p>
 * 4. ỨNG DỤNG THỰC TẾ
 * - Tra bậc thang: thuế lũy tiến, khung giá theo số lượng, tra mã vùng - dùng floorEntry.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 5. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - SortedMap thêm gì, NavigableMap thêm tiếp gì?
 * - ceilingKey và higherKey khác nhau ở điểm nào?
 * - headMap trả về view hay bản sao? Thêm khóa ngoài khoảng vào view thì sao?
 * - descendingMap có tốn chi phí sao chép không?
 * - TreeMap có nhận khóa null không? Vì sao?
 */
public class t11_SortedAndNavigableMaps {

    void main() {
        // TODO 1 (mục 1): Tạo TreeMap<Integer, String> và in firstKey, lastKey, headMap, tailMap,
        //   subMap. Ghi lại biên nào được bao gồm theo mặc định.
        //
        // TODO 2 (mục 2.1): So sánh ceilingKey/floorKey/higherKey/lowerKey trên một khóa CÓ trong
        //   map và một khóa KHÔNG có trong map.
        //
        // TODO 3 (mục 2.2): Dùng pollFirstEntry trong vòng while để lấy hết map theo thứ tự tăng.
        //
        // TODO 4 (mục 2.3): In descendingMap và descendingKeySet, rồi SỬA map gốc và in lại
        //   để chứng minh đó là view.
        //
        // TODO 5 (mục 4): Dựng bảng thuế lũy tiến bằng TreeMap và dùng floorEntry để tra bậc
        //   cho một mức thu nhập bất kỳ.
        //
        // Bảng bậc thang: khóa là mốc bắt đầu, giá trị là thuế suất.
        TreeMap<Long, Integer> bacThue = new TreeMap<>();
        bacThue.put(0L, 5);
        bacThue.put(5_000_000L, 10);
        bacThue.put(10_000_000L, 15);
        IO.println("bang thue: " + bacThue);   // TreeMap luon in theo thu tu khoa

        // floorEntry: tìm mốc lớn nhất mà <= thu nhập.
        for (long thuNhap : new long[]{3_000_000L, 7_500_000L, 20_000_000L}) {
            var bac = bacThue.floorEntry(thuNhap);
            IO.println(thuNhap + " -> moc " + bac.getKey() + ", thue " + bac.getValue() + "%");
        }

        // Bốn hàm điều hướng, so trên cùng một khóa CÓ trong map (TODO 2).
        IO.println("floor (5tr)  : " + bacThue.floorKey(5_000_000L));    // lay ca chinh no
        IO.println("lower (5tr)  : " + bacThue.lowerKey(5_000_000L));    // bo chinh no
        IO.println("ceiling(5tr) : " + bacThue.ceilingKey(5_000_000L));
        IO.println("higher(5tr)  : " + bacThue.higherKey(5_000_000L));
        //
        // TODO 6 (mục 3): Thử put(null, "x") vào TreeMap và vào HashMap. Ghi lại khác biệt.
    }
}
