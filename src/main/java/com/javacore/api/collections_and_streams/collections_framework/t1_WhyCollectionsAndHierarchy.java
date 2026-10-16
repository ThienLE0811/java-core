package com.javacore.api.collections_and_streams.collections_framework;

import java.util.*;

/**
 * Storing Data Using the Collections Framework + Getting to Know the Collection Hierarchy
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 1/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Bài này gộp HAI trang gốc vì cả hai đều là tổng quan, gần như không có API mới để gõ tay.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/intro/">dev.java - Storing Data Using the Collections Framework</a>,
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/organization/">dev.java - Getting to Know the Collection Hierarchy</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC 1 - STORING DATA USING THE COLLECTIONS FRAMEWORK (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. GIỚI THIỆU COLLECTIONS FRAMEWORK
 * - (Introducing the Collections Framework)
 * - Framework được dùng nhiều nhất trong JDK; lịch sử tiến hóa qua các phiên bản Java.
 * <p>
 * 2. ĐỊNH VỊ TRONG FRAMEWORK
 * - (Finding Your Way in the Collections Framework)
 * - Hai cây phân cấp tách biệt: Collection (gốc Iterable) và Map. Queue, Iterator nằm ở đâu.
 * <p>
 * 3. TRÁNH CÁC INTERFACE VÀ CÀI ĐẶT CŨ
 * - (Avoiding Using Old Interfaces and Implementations)
 * - Vector, Stack, Hashtable, Enumeration - vì sao không dùng nữa và thay bằng gì.
 * <p>
 * 4. VÌ SAO CHỌN COLLECTION THAY VÌ ARRAY
 * - (Why Choose a Collection Over an Array?)
 * - Collection tự quản lý dung lượng, kiểm soát phần tử đưa vào, có sẵn thao tác bậc cao.
 * <p>
 * ======================================================================
 * TRANG GỐC 2 - GETTING TO KNOW THE COLLECTION HIERARCHY (mục 5 đến 10)
 * ======================================================================
 * <p>
 * 5. KHÔNG LẠC TRONG CÂY PHÂN CẤP
 * - (Avoiding Getting Lost in the Collection Hierarchy)
 * <p>
 * 6. INTERFACE ITERABLE
 * - (The Iterable Interface) Gốc của cây; cho phép dùng vòng for-each.
 * <p>
 * 7. INTERFACE COLLECTION
 * - (Storing Elements in a Container with the Collection Interface)
 * - Thêm, xóa, kiểm tra tồn tại, các phép toán tập hợp.
 * <p>
 * 8. LIST MỞ RỘNG COLLECTION
 * - (Extending Collection with List) Giữ thứ tự chèn, truy cập theo chỉ số.
 * <p>
 * 9. SET MỞ RỘNG COLLECTION
 * - (Extending Collection with Set) Không cho phép phần tử trùng.
 * <p>
 * 10. SORTEDSET VÀ NAVIGABLESET
 * - (Sorting the elements of a Set with SortedSet and NavigableSet)
 * - Sắp xếp dựa trên Comparable hoặc Comparator.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 11. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Array của JS làm được cả vai trò List, Stack, Queue. Java tách thành các interface
 * riêng - được gì, mất gì.
 * - Map và Set của JS so với Map và Set của Java khác nhau ở đâu (thứ tự, so sánh khóa bằng gì).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Map có phải là Collection không? Vì sao?
 * - Iterable cho ta thứ gì mà Collection không có?
 * - Kể ba nhược điểm của array so với collection.
 * - Vector, Stack, Hashtable nên thay bằng gì, và vì sao chúng vẫn còn trong JDK?
 * - Vẽ lại cây phân cấp từ Iterable xuống NavigableSet mà không nhìn tài liệu.
 */
public class t1_WhyCollectionsAndHierarchy {

    // Hàm này KHÔNG biết nó đang làm việc với cái gì
    static void nap(Collection<String> c) {
        c.add("java");
        c.add("spring");
        c.add("java");   // trùng
    }


    void main() {
        // TODO 1 (mục 4): Viết hai đoạn làm cùng một việc - lưu 10 số rồi xóa số ở giữa -
        //   một đoạn dùng int[], một đoạn dùng List<Integer>. So sánh số dòng code.
        //
        nap(new ArrayList<>());      // [java, spring, java]      giữ thứ tự, cho trùng
        nap(new HashSet<>());        // [java, spring]            khử trùng, thứ tự tùy hash
        nap(new LinkedHashSet<>());  // [java, spring]            khử trùng, giữ thứ tự chèn
        nap(new TreeSet<>());        // [java, spring]            khử trùng, sắp xếp

        // TODO 2 (mục 2): In ra cây phân cấp của ArrayList bằng cách duyệt
        //   getClass().getInterfaces() và getSuperclass(). Đối chiếu với sơ đồ trong tài liệu.
        //
        // TODO 3 (mục 6): Khai báo biến kiểu Iterable<String> trỏ tới một List, rồi thử gọi
        //   size() trên nó. Dán lỗi biên dịch vào đây và giải thích.
        //
        // TODO 4 (mục 3): Đọc Javadoc của Vector và ArrayList, ghi lại khác biệt cốt lõi
        //   (đồng bộ hóa) và vì sao điều đó lại là nhược điểm.
        //
        // TODO 5 (mục 11): Ghi lại ba khác biệt giữa Array/Map/Set của TypeScript và của Java.
    }
}
