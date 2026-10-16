package com.javacore.api.collections_and_streams.collections_framework;

import java.util.Map;

/**
 * Using Maps to Store Key Value Pairs
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 8/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/maps/">dev.java - Using Maps to Store Key Value Pairs</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - USING MAPS TO STORE KEY VALUE PAIRS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. CÂY PHÂN CẤP MAP
 * - (Introducing the Map Hierarchy)
 * - Nguyên lý hashmap: băm khóa để tìm ô chứa.
 * - Interface: {@code Map}, {@code SortedMap}, {@code NavigableMap}.
 * - Cài đặt: {@code HashMap}, {@code LinkedHashMap}, {@code IdentityHashMap}.
 * - Map KHÔNG mở rộng Collection - đây là lý do nó là cây phân cấp riêng.
 * <p>
 * 2. FACTORY METHOD CHO MAP
 * - (Using the Convenience Factory Methods for Collections to Create Maps)
 * - {@code Map.of()}, {@code Map.ofEntries()} (Java SE 9), {@code Map.entry()}.
 * - Không nhận null, không nhận khóa trùng; Map.of chỉ tới 10 cặp, nhiều hơn thì dùng ofEntries.
 * <p>
 * 3. LƯU CẶP KHÓA-GIÁ TRỊ
 * - (Storing Key/Value Pairs in a Map)
 * - Một khóa gắn với ĐÚNG MỘT giá trị; giá trị thì được phép trùng nhau.
 * - Các thao tác nền: put, get, remove.
 * <p>
 * 4. KHÁM PHÁ INTERFACE MAP
 * - (Exploring the Map interface)
 * - {@code Map.Entry} với {@code getKey()}, {@code getValue()}, {@code setValue(value)}.
 * - Cảnh báo không dùng khóa mutable (bài 12 sẽ đào sâu).
 * <p>
 * 5. THỰC HÀNH
 * - (Practicing Map Operations)
 * - Tạo map, {@code get()}, {@code containsKey()}, {@code containsValue()}, {@code put()},
 * {@code putIfAbsent()}, duyệt bằng {@code keySet()}, {@code values()}, {@code entrySet()}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. HashMap BÊN TRONG
 * - Mảng bucket, hashCode dùng để chọn bucket, equals dùng để phân biệt khóa trong cùng bucket.
 * - Từ Java 8, bucket quá dài chuyển từ danh sách liên kết sang cây đỏ-đen.
 * - LinkedHashMap giữ thứ tự chèn (hoặc thứ tự truy cập) nhờ danh sách liên kết đôi phụ thêm.
 * - IdentityHashMap so khóa bằng {@code ==} chứ không bằng equals.
 * <p>
 * 7. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Object của JS chỉ nhận khóa string/symbol; Map của JS nhận mọi giá trị nhưng so bằng tham
 * chiếu. HashMap của Java so bằng equals/hashCode nên hai object khác nhau vẫn có thể là một khóa.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao Map không mở rộng Collection?
 * - put() trả về gì khi khóa đã tồn tại? Khi chưa tồn tại?
 * - put khác putIfAbsent ra sao khi giá trị hiện tại là null?
 * - HashMap dùng method nào để chọn bucket, method nào để so khóa?
 * - entrySet trả về view hay bản sao? setValue trên entry có ảnh hưởng map không?
 */
public class t8_Maps {

    void main() {
        // TODO 1 (mục 2): Tạo map bằng Map.of và bằng Map.ofEntries. Thử đưa vào một giá trị null
        //   và một khóa trùng. Dán exception nhận được vào đây.
        //
        Map<Integer, String> map =
                Map.of(
                        1, "one",
                        2, "two",
                        3, "three"
                );
        IO.println("Map: " + map);
        // TODO 2 (mục 3): Gọi put() hai lần với cùng khóa, in giá trị trả về mỗi lần.
        //   Giải thích ý nghĩa của giá trị trả về.
        //
        // TODO 3 (mục 5): Duyệt một map bằng cả ba cách keySet, values, entrySet.
        //   Ghi lại cách nào phù hợp khi cần cả khóa lẫn giá trị và vì sao.
        //
        // TODO 4 (mục 4): Dùng entry.setValue() khi đang duyệt entrySet, rồi in map gốc.
        //   Giải thích vì sao map bị thay đổi.
        //
        // TODO 5 (mục 6): Đưa cùng một cặp khóa-giá trị vào HashMap, LinkedHashMap,
        //   IdentityHashMap với khóa là hai String bằng nhau nhưng khác tham chiếu
        //   (dùng new String("a")). Ghi lại khác biệt số phần tử.
    }
}
