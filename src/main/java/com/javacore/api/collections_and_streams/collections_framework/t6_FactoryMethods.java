package com.javacore.api.collections_and_streams.collections_framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creating and Processing Data with the Collections Factory Methods
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 6/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/factory-methods/">dev.java - Creating and Processing Data with the Collections Factory Methods</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING AND PROCESSING DATA WITH THE COLLECTIONS FACTORY METHODS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. TẠO COLLECTION KHÔNG SỬA ĐƯỢC
 * - (Creating Unmodifiable Collections)
 * - {@code List.of()}, {@code Set.of()} (Java SE 9).
 * - Ba ràng buộc: không nhận null, Set.of không nhận phần tử trùng, không sửa được sau khi tạo.
 * <p>
 * 2. TẠO BẢN SAO KHÔNG SỬA ĐƯỢC
 * - (Getting an Unmodifiable Copy of a Collection)
 * - {@code List.copyOf()}, {@code Set.copyOf()} (Java SE 10) - dùng làm defensive copy.
 * - copyOf tạo BẢN SAO, nên sửa collection nguồn sau đó không ảnh hưởng bản sao.
 * <p>
 * 3. BỌC MẢNG THÀNH LIST
 * - (Wrapping an Array in a List)
 * - {@code Arrays.asList()} - list có kích thước cố định, chỉ thay được phần tử (set),
 * không add/remove. Khác hẳn List.of.
 * <p>
 * 4. LỚP TIỆN ÍCH COLLECTIONS
 * - (Using the Collections Factory Class to Process a Collection)
 * - 4.1 Nhỏ nhất, lớn nhất: {@code min()}, {@code max()} (có bản nhận Comparator).
 * - 4.2 Tìm list con: {@code indexOfSubList()}, {@code lastIndexOfSubList()}.
 * - 4.3 Đổi thứ tự: {@code sort()}, {@code shuffle()}, {@code rotate()}, {@code reverse()},
 * {@code swap()}.
 * - 4.4 Bọc không cho sửa: các method {@code unmodifiable*()}.
 * - 4.5 Bọc đồng bộ hóa: các method {@code synchronized*()}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BA KHÁI NIỆM DỄ LẪN
 * - {@code List.of()}: bất biến thật (immutable), tự giữ dữ liệu riêng.
 * - {@code Collections.unmodifiableList(list)}: chỉ là VIEW chặn ghi. Sửa list gốc thì view
 * cũng đổi theo - KHÔNG phải bất biến.
 * - {@code Arrays.asList(array)}: view trên mảng, sửa mảng thì list đổi và ngược lại.
 * <p>
 * 6. unmodifiable KHÔNG BẢO VỆ PHẦN TỬ BÊN TRONG
 * - List bất biến chứa object mutable thì object đó vẫn sửa được. Shallow immutability.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 7. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - List.of có nhận null không? Set.of có nhận phần tử trùng không? Ném gì nếu vi phạm?
 * - copyOf khác unmodifiableList ở điểm nào?
 * - Arrays.asList cho add() không? Cho set() không? Vì sao?
 * - Collections.synchronizedList có đủ an toàn khi duyệt không?
 * - Khi cần trả một collection ra ngoài class, nên trả kiểu gì để an toàn?
 */
public class t6_FactoryMethods {

    void main() {
        // TODO 1 (mục 1): Thử List.of(null) và Set.of("a", "a"). Dán hai exception nhận được
        //   vào đây và gọi tên chúng.
        //
        List<String> strings = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4"));
        List<String> unmodifiableStrings = Collections.unmodifiableList(strings);
        IO.println(unmodifiableStrings);
        strings.add("5");
        IO.println(unmodifiableStrings);
        // TODO 2 (mục 5): Tạo ArrayList, bọc bằng Collections.unmodifiableList, rồi SỬA list gốc.
        //   In view ra và giải thích vì sao view đổi theo.
        //
        // TODO 3 (mục 2): Làm lại TODO 2 nhưng dùng List.copyOf. Ghi lại khác biệt.
        //
        // TODO 4 (mục 3): Với Arrays.asList trên một String[], thử set(), add(), và sửa mảng gốc.
        //   Ghi lại cái nào chạy, cái nào ném exception.
        //
        // TODO 5 (mục 4): Dùng Collections.min/max với Comparator, rồi shuffle, rotate, reverse,
        //   swap trên cùng một list và in sau mỗi bước.
        //
        // TODO 6 (mục 6): Tạo List.of chứa một object mutable, sửa object đó. Chứng minh
        //   bất biến của list chỉ là "nông".
    }
}
