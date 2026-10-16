package com.javacore.api.collections_and_streams.collections_framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Iterating over the Elements of a Collection
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 3/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/iterating/">dev.java - Iterating over the Elements of a Collection</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - ITERATING OVER THE ELEMENTS OF A COLLECTION (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. VÒNG FOR-EACH
 * - (Using the for-each Pattern)
 * - Cách đơn giản nhất để ĐỌC phần tử; không sửa được collection trong lúc duyệt.
 * <p>
 * 2. DÙNG ITERATOR
 * - (Using an Iterator on a Collection)
 * - {@code hasNext()}, {@code next()}, {@code remove()}.
 * - for-each thực chất được compiler dịch thành vòng lặp dùng Iterator.
 * <p>
 * 3. SỬA COLLECTION TRONG LÚC DUYỆT
 * - (Updating a Collection While Iterating over It)
 * - {@code ConcurrentModificationException} xảy ra khi nào.
 * - Cách an toàn: {@code Iterator.remove()} hoặc {@code removeIf()}.
 * <p>
 * 4. TỰ CÀI ĐẶT ITERABLE
 * - (Implementing the Iterable Interface)
 * - Viết class hoặc record của riêng mình trả về một Iterator để dùng được for-each.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. FAIL-FAST SO VỚI FAIL-SAFE
 * - Iterator của ArrayList, HashMap là fail-fast (dựa trên modCount).
 * - Iterator của CopyOnWriteArrayList, ConcurrentHashMap là fail-safe (duyệt trên bản sao hoặc
 * trạng thái tại lúc tạo iterator) - không ném exception nhưng có thể đọc dữ liệu cũ.
 * - ConcurrentModificationException KHÔNG chỉ xảy ra khi đa luồng; một luồng cũng gây ra được.
 * <p>
 * 6. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Iterator protocol của JS (Symbol.iterator, next() trả về {value, done}) so với
 * Iterable/Iterator của Java. Vì sao Java cần hasNext() còn JS thì không.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 7. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - for-each được compiler dịch thành gì?
 * - Có mấy cách xóa phần tử khi đang duyệt, cách nào đúng?
 * - ConcurrentModificationException là checked hay unchecked? Do ai ném ra?
 * - Cần cài đặt những method nào để dùng được for-each trên class của mình?
 * - Fail-fast khác fail-safe ở điểm nào?
 */
public class t3_Iterating {

    void main() {
        // TODO 1 (mục 1): Duyệt một List bằng for-each rồi gọi list.remove(...) ngay trong thân
        //   vòng lặp. Dán stack trace nhận được vào đây.
        //
        // List.of(...) bat bien -> remove() nem UnsupportedOperationException, khong phai CME.
        // Boc lai bang ArrayList de co mot list sua duoc.
        Collection<String> strings = new ArrayList<>(List.of("one", "two", "three"));

        for (String element : strings) {
            IO.println(element);
            strings.remove(element);   // ConcurrentModificationException o vong lap ke tiep
        }
        // TODO 2 (mục 2): Viết lại vòng lặp trên bằng Iterator và dùng iterator.remove().
        //   Giải thích vì sao lần này không ném exception.
        //
        // TODO 3 (mục 3): Thử xóa phần tử ÁP CHÓT trong vòng for-each. Ghi lại kết quả bất ngờ,
        //   rồi tra modCount và cursor trong mã nguồn ArrayList để giải thích.
        //
        // TODO 4 (mục 4): Viết một record Range(int start, int end) implements Iterable<Integer>
        //   rồi duyệt nó bằng for-each.
        //
        // TODO 5 (mục 5): Lặp lại TODO 1 với CopyOnWriteArrayList. Ghi lại khác biệt.
    }
}
