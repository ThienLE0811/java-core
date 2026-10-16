package com.javacore.api.collections_and_streams.collections_framework;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Storing Elements in a Collection
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 2/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/collection-interface/">dev.java - Storing Elements in a Collection</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - STORING ELEMENTS IN A COLLECTION (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. KHÁM PHÁ INTERFACE COLLECTION
 * - (Exploring the Collection Interface)
 * - Vai trò: nơi chứa phần tử, định nghĩa những gì MỌI collection đều làm được.
 * <p>
 * 2. METHOD XỬ LÝ TỪNG PHẦN TỬ
 * - (Methods That Handle Individual Elements)
 * - {@code add(element)}, {@code remove(element)}, {@code contains(element)}.
 * - Giá trị boolean mà add và remove trả về nghĩa là gì, khác nhau ra sao giữa List và Set.
 * <p>
 * 3. METHOD XỬ LÝ COLLECTION KHÁC
 * - (Methods That Handle Other Collections)
 * - {@code containsAll()}, {@code addAll()}, {@code removeAll()}, {@code retainAll()}.
 * - Tương ứng với phép toán tập hợp nào: bao hàm, hợp, hiệu, giao.
 * <p>
 * 4. METHOD XỬ LÝ CHÍNH COLLECTION ĐÓ
 * - (Methods That Handle The Collection Itself)
 * - {@code size()}, {@code isEmpty()}, {@code clear()}.
 * <p>
 * 5. LẤY MẢNG CÁC PHẦN TỬ
 * - (Getting an Array of the Elements of a Collection)
 * - Ba dạng {@code toArray()}: không tham số, nhận mảng mẫu, nhận IntFunction (Java SE 11).
 * <p>
 * 6. LỌC PHẦN TỬ BẰNG PREDICATE
 * - (Filtering out Elements of a Collection with a Predicate)
 * - {@code removeIf(Predicate)} - cách xóa an toàn thay cho vòng lặp tự xóa.
 * <p>
 * 7. CHỌN CÀI ĐẶT CHO COLLECTION
 * - (Choosing an Implementation for the Collection Interface)
 * - Mặc định nên là ArrayList.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. UnsupportedOperationException
 * - Collection bất biến vẫn CÓ method add/remove nhưng ném exception lúc chạy. Đây là hệ quả
 * của việc interface không tách riêng phần đọc và phần ghi.
 * <p>
 * 9. KHAI BÁO THEO INTERFACE
 * - Thói quen khai báo biến, tham số, kiểu trả về theo Collection/List/Set thay vì ArrayList.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code add()} trả về false khi nào?
 * - {@code removeAll()} và {@code retainAll()} khác nhau ra sao?
 * - Ba dạng toArray khác nhau thế nào, nên dùng dạng nào?
 * - Vì sao removeIf an toàn hơn vòng lặp tự xóa?
 * - {@code contains()} dựa vào method nào của phần tử để so sánh?
 */
public class t2_CollectionInterface {

    void main() {
        // TODO 1 (mục 2): Dùng cùng một biến kiểu Collection<String> trỏ lần lượt tới ArrayList
        //   rồi HashSet. Thêm cùng một phần tử hai lần, in giá trị trả về của add().
        //
        Collection<String> names = new ArrayList<>();
        names.add("An");
        names.add("Bình");
        names.add("An");              // thêm được, ArrayList cho phép trùng

        System.out.println(names);          // [An, Bình, An]
        System.out.println(names.size());   // 3
        System.out.println(names.contains("Bình")); // true

        names.remove("An");                  // xoá phần tử khớp ĐẦU TIÊN
        System.out.println(names);
        // TODO 2 (mục 3): Với hai collection số nguyên, chạy addAll, removeAll, retainAll,
        //   containsAll. Ghi lại kết quả và đặt tên phép toán tập hợp tương ứng.
        //
        // TODO 3 (mục 5): Thử cả ba dạng toArray(). Giải thích vì sao dạng không tham số
        //   trả về Object[] chứ không phải String[].
        //
        // TODO 4 (mục 6): Dùng removeIf để xóa mọi chuỗi dài hơn 5 ký tự.
        //
        // TODO 5 (mục 8): Gọi add() trên List.of(...) và bắt UnsupportedOperationException.
    }
}
