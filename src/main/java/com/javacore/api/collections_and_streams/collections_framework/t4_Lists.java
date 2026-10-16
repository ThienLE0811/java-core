package com.javacore.api.collections_and_streams.collections_framework;

import java.util.ArrayList;
import java.util.List;

/**
 * Extending Collection with List
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 4/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/lists/">dev.java - Extending Collection with List</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - EXTENDING COLLECTION WITH LIST (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. KHÁM PHÁ INTERFACE LIST
 * - (Exploring the List Interface)
 * - Hai điểm List thêm vào so với Collection: thứ tự duyệt cố định và truy cập theo chỉ số.
 * <p>
 * 2. CHỌN CÀI ĐẶT CỦA LIST
 * - (Choosing your Implementation of the List Interface)
 * - ArrayList so với LinkedList; mặc định chọn ArrayList vì duyệt nhanh hơn.
 * - (Bài 12 sẽ đi sâu vào phần benchmark.)
 * <p>
 * 3. TRUY CẬP PHẦN TỬ THEO CHỈ SỐ
 * - (Accessing the Elements Using an Index)
 * - {@code add(index, element)}, {@code get(index)}, {@code set(index, element)},
 * {@code remove(index)}.
 * - Tìm vị trí: {@code indexOf()}, {@code lastIndexOf()}.
 * - Lấy một đoạn: {@code subList(start, end)} - là VIEW, sửa view là sửa list gốc.
 * <p>
 * 4. SẮP XẾP PHẦN TỬ CỦA LIST
 * - (Sorting the Elements of a List)
 * - {@code sort()} có từ Java SE 8, nhận Comparator; truyền null thì phần tử phải Comparable.
 * <p>
 * 5. DUYỆT PHẦN TỬ CỦA LIST
 * - (Iterating over the Elements of a List)
 * - {@code ListIterator}: {@code hasPrevious()}, {@code previous()}, {@code nextIndex()},
 * {@code previousIndex()}, {@code add()}, {@code set()} - duyệt được hai chiều.
 * <p>
 * 6. THỰC HÀNH
 * - (Practicing List Operations)
 * - Tạo list, lấy phần tử đầu/cuối, tìm theo chỉ số, dùng sublist, sắp xếp,
 * đảo ngược bằng {@code List.reversed()} (SequencedCollection, Java SE 21).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BẪY remove(int) SO VỚI remove(Object)
 * - Với {@code List<Integer>}, {@code list.remove(1)} xóa theo CHỈ SỐ,
 * {@code list.remove(Integer.valueOf(1))} xóa theo GIÁ TRỊ. Overload chứ không phải override.
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Array.prototype.splice/slice so với subList; slice của JS là bản sao, subList là view.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - List thêm gì so với Collection?
 * - subList trả về bản sao hay view? Hệ quả khi sửa?
 * - ListIterator làm được gì mà Iterator không làm được?
 * - {@code list.sort(null)} chạy được khi nào, ném gì khi không chạy được?
 * - Với List<Integer>, remove(1) xóa cái gì?
 */
public class t4_Lists {

    void main() {
        // TODO 1 (mục 3): Dùng đủ add(index), get, set, remove(index), indexOf, lastIndexOf
        //   trên một List<String> có phần tử trùng nhau. Ghi lại kết quả từng lệnh.
        //
        List<String> strings = new ArrayList<>(List.of("0", "1", "2", "3", "4", "5"));
        IO.println(strings);
        strings.subList(2, 5).clear();
        IO.println(strings);
        // TODO 2 (mục 3): Lấy subList(1, 4), gọi clear() trên nó, rồi in list gốc.
        //   Giải thích vì sao list gốc bị thay đổi.
        //
        // TODO 3 (mục 4): Sắp xếp một List<String> theo độ dài rồi theo thứ tự bảng chữ cái,
        //   dùng Comparator.comparing(...).thenComparing(...).
        //
        // TODO 4 (mục 5): Dùng ListIterator duyệt ngược từ cuối về đầu, và dùng set() để
        //   thay mọi phần tử thành chữ hoa ngay trong lúc duyệt.
        //
        // TODO 5 (mục 7): Chứng minh bẫy remove(int) với List<Integer> bằng một ví dụ chạy được.
    }
}
