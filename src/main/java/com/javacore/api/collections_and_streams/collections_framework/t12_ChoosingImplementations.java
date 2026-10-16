package com.javacore.api.collections_and_streams.collections_framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Choosing Immutable Types for Your Key + Choosing the Right Implementation Between ArrayList and LinkedList
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 12/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Bài này gộp HAI trang gốc vì cả hai đều trả lời cùng một câu hỏi: chọn kiểu nào cho đúng.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/choosing-keys/">dev.java - Choosing Immutable Types for Your Key</a>,
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/arraylist-vs-linkedlist/">dev.java - Choosing the Right Implementation Between ArrayList and LinkedList</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC 1 - CHOOSING IMMUTABLE TYPES FOR YOUR KEY (mục 1 đến 2)
 * ======================================================================
 * <p>
 * 1. TRÁNH KHÓA MUTABLE
 * - (Avoiding the Use of Mutable Keys)
 * - Sửa một object sau khi đã dùng nó làm khóa thì hashCode đổi, map tìm sai bucket:
 * giá trị vẫn nằm trong map nhưng KHÔNG LẤY RA ĐƯỢC nữa.
 * - Kết luận: khóa nên là kiểu bất biến (String, Integer, record với field bất biến).
 * <p>
 * 2. CẤU TRÚC BÊN TRONG CỦA HASHSET
 * - (Diving into the Structure of HashSet)
 * - HashSet thực chất bọc một HashMap bên trong.
 * - Sửa object sau khi đã thêm vào Set có thể tạo ra phần tử TRÙNG trong một Set.
 * <p>
 * ======================================================================
 * TRANG GỐC 2 - ARRAYLIST VERSUS LINKEDLIST (mục 3 đến 10)
 * ======================================================================
 * <p>
 * 3. ĐẶT VẤN ĐỀ
 * - (Introduction) Vì sao cần so sánh, và vì sao lý thuyết độ phức tạp là chưa đủ.
 * <p>
 * 4. ĐỘ PHỨC TẠP THUẬT TOÁN
 * - (Algorithm Complexity)
 * - Trên lý thuyết: LinkedList chậm khi truy cập giữa list, hai bên ngang nhau ở hai đầu.
 * <p>
 * 5. ĐỌC PHẦN TỬ
 * - (Reading Elements From a List)
 * - Benchmark: ArrayList luôn nhanh hơn. LinkedList chịu chi phí pointer chasing - mỗi node
 * nằm rải rác trong bộ nhớ nên CPU cache không giúp được gì.
 * <p>
 * 6. DUYỆT PHẦN TỬ
 * - (Iterating Over the Elements of a List)
 * - ArrayList nhanh hơn ở cả hai kiểu duyệt. Duyệt LinkedList tốn khoảng gấp đôi.
 * - Dùng chỉ số để duyệt LinkedList là O(n²) - tuyệt đối tránh.
 * <p>
 * 7. CHÈN PHẦN TỬ
 * - (Inserting Elements in a List)
 * - LinkedList chỉ nhanh hơn khi chèn ở đầu hoặc cuối. Chi phí cấp phát lại mảng của
 * ArrayList được khấu hao tốt.
 * <p>
 * 8. SO SÁNH TRỰC TIẾP
 * - (Comparing the Insertion for LinkedList and ArrayList)
 * - "ArrayList outperforms LinkedList for all the operations but one."
 * <p>
 * 9. TIÊU THỤ BỘ NHỚ
 * - (Analyzing the Memory Consumption of LinkedList and ArrayList)
 * - Mỗi node LinkedList tốn thêm khoảng 24 byte cho hai con trỏ và phần đầu object.
 * - Với cùng số phần tử, ArrayList tiết kiệm bộ nhớ hơn nhiều lần.
 * <p>
 * 10. NÊN CHỌN CÁI NÀO
 * - (Which Implementation Should You Choose?)
 * - List thông thường: ArrayList.
 * - Cần stack hoặc queue: ArrayDeque, không phải LinkedList.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 11. HỢP ĐỒNG equals/hashCode
 * - Hai object equals nhau thì BẮT BUỘC hashCode bằng nhau. Chiều ngược lại không bắt buộc.
 * - hashCode phải ổn định suốt thời gian object nằm trong map/set - đây chính là lý do
 * khóa phải bất biến.
 * - record tự sinh equals/hashCode theo các component, nhưng vẫn mutable nếu component là
 * object mutable (ví dụ record chứa một List).
 * <p>
 * 12. ĐO ĐẠC TRƯỚC KHI KẾT LUẬN
 * - Microbenchmark trong main() rất dễ sai do JIT warm-up và dead code elimination.
 * Con số chỉ mang tính tham khảo; công cụ nghiêm túc là JMH.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 13. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Sửa một object đang làm khóa trong HashMap thì chuyện gì xảy ra với cặp đó?
 * - HashSet lưu trữ bằng cấu trúc gì bên trong?
 * - Vì sao LinkedList chậm hơn ArrayList dù độ phức tạp lý thuyết ngang nhau?
 * - Trường hợp DUY NHẤT nào LinkedList thắng?
 * - Cần một stack thì nên dùng class nào?
 * - Hợp đồng giữa equals và hashCode phát biểu thế nào?
 */
public class t12_ChoosingImplementations {

    void main() {
        // TODO 1 (mục 1): Viết class Key có field mutable và override equals/hashCode theo field đó.
        //   Đưa vào HashMap, sửa field, rồi gọi get(). Ghi lại kết quả và giải thích.
        //
        Map<KeyMutable, String> map = new HashMap<>();
        KeyMutable k = new KeyMutable("a");
        map.put(k, "gia tri cua a");

        IO.println("truoc khi sua: " + map.get(k));
        IO.println("hashCode     : " + k.hashCode());

        k.name = "b";   // sửa field đang được dùng làm khóa

        IO.println("sau khi sua  : " + map.get(k));          // mat dau
        IO.println("hashCode     : " + k.hashCode());        // da doi -> tim sai o
        IO.println("map van chua : " + map);                 // gia tri VAN O DAY
        IO.println("size         : " + map.size());

        // TODO 3: record bất biến - không có setter nên không dính bẫy trên.
        Map<KeyRecord, String> mapOk = new HashMap<>();
        KeyRecord r = new KeyRecord("a");
        mapOk.put(r, "gia tri cua a");
        IO.println("record       : " + mapOk.get(r) + " / " + mapOk.get(new KeyRecord("a")));
        //
        // TODO 2 (mục 2): Lặp lại TODO 1 với HashSet: thêm hai object, sửa một object để nó bằng
        //   object kia, rồi in size() và nội dung set.
        //
        // TODO 3 (mục 1): Viết lại Key thành record bất biến và chứng minh vấn đề biến mất.
        //
        // TODO 4 (mục 5, 6): Đo thời gian get(i) ngẫu nhiên 100.000 lần trên ArrayList và
        //   LinkedList cùng 100.000 phần tử. Ghi lại con số.
        //
        // TODO 5 (mục 6): Duyệt LinkedList bằng vòng for có chỉ số và bằng for-each.
        //   So sánh thời gian, giải thích vì sao cách đầu là O(n²).
        //
        // TODO 6 (mục 7): Đo thời gian chèn vào ĐẦU list của ArrayList so với LinkedList.
        //   Đây là trường hợp duy nhất LinkedList thắng - kiểm chứng xem có đúng không.
        //
        // TODO 7 (mục 12): Ghi lại ba lý do khiến con số đo được ở TODO 4-6 không đáng tin tuyệt đối.
    }

    /** Khóa TỒI: field public sửa được nên hashCode thay đổi theo. */
    static class KeyMutable {
        String name;

        KeyMutable(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof KeyMutable other && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "KeyMutable[" + name + "]";
        }
    }

    /** Khóa TỐT: record bất biến, equals/hashCode do Java tự sinh. */
    record KeyRecord(String name) {
    }
}
