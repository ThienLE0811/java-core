package com.javacore.api.collections_and_streams.collections_framework;

import java.util.HashMap;
import java.util.Map;

/**
 * Managing the Content of a Map
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 9/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/working-with-keys-and-values/">dev.java - Managing the Content of a Map</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - MANAGING THE CONTENT OF A MAP (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. THÊM CẶP KHÓA-GIÁ TRỊ
 * - (Adding a Key Value Pair to a Map)
 * - {@code put()} ghi đè và trả về giá trị cũ; {@code putIfAbsent()} chỉ ghi khi khóa chưa có
 * HOẶC đang gắn với null - đây là cách dọn giá trị null trong map.
 * <p>
 * 2. LẤY GIÁ TRỊ TỪ KHÓA
 * - (Getting a Value From a Key)
 * - {@code get()} trả null khi không có khóa; {@code getOrDefault()} trả giá trị mặc định.
 * <p>
 * 3. XÓA KHÓA
 * - (Removing a Key from a Map)
 * - {@code remove(key)} trả về giá trị bị xóa; {@code remove(key, value)} chỉ xóa khi khớp cả hai.
 * <p>
 * 4. KIỂM TRA SỰ TỒN TẠI
 * - (Checking for the Presence of a Key or a Value)
 * - {@code containsKey()} nhanh; {@code containsValue()} phải quét toàn bộ map.
 * <p>
 * 5. KIỂM TRA NỘI DUNG MAP
 * - (Checking for the Content of a Map)
 * - {@code isEmpty()}, {@code size()}, {@code clear()}, {@code putAll()}.
 * <p>
 * 6. VIEW TRÊN KHÓA, GIÁ TRỊ VÀ ENTRY
 * - (Getting a View on the Keys, the Values or the Entries of a Map)
 * - {@code keySet()}, {@code values()}, {@code entrySet()} đều là VIEW, không phải bản sao.
 * - Xóa trên keySet thì cặp tương ứng bị xóa khỏi map.
 * - Xóa trên values chỉ xóa CẶP ĐẦU TIÊN khớp giá trị.
 * - Không thêm được phần tử vào các view này.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. get() TRẢ NULL LÀ MƠ HỒ
 * - null có thể nghĩa là "không có khóa" hoặc "khóa gắn với null". Phân biệt bằng containsKey,
 * hoặc tốt hơn là đừng lưu giá trị null trong map.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - putIfAbsent xử lý trường hợp giá trị hiện tại là null như thế nào?
 * - getOrDefault có ghi giá trị mặc định vào map không?
 * - containsKey và containsValue khác nhau thế nào về chi phí?
 * - Xóa một phần tử khỏi keySet thì map có đổi không? Thêm vào keySet thì sao?
 * - get() trả về null nghĩa là gì?
 */
public class t9_ManagingMapContent {

    void main() {
        // TODO 1 (mục 1): Đưa vào map một cặp có giá trị null, rồi gọi putIfAbsent với cùng khóa.
        //   In map trước và sau, giải thích kết quả.
        //
        // TODO 2 (mục 2): So sánh get() và getOrDefault() trên một khóa không tồn tại.
        //   Kiểm tra xem map có bị thêm khóa sau khi gọi getOrDefault không.
        //
        Map<String, String> map = new HashMap<>();
        map.put("a", "Anh");
        map.put("b", null);

        // Bẫy 1: hai khóa khác hẳn nhau nhưng get() đều trả null.
        IO.println("get(b)   : " + map.get("b"));   // có khóa, giá trị null
        IO.println("get(z)   : " + map.get("z"));   // không có khóa
        IO.println("hasKey b : " + map.containsKey("b"));
        IO.println("hasKey z : " + map.containsKey("z"));

        // getOrDefault chỉ đọc, không ghi gì vào map.
        IO.println("orDefault: " + map.getOrDefault("z", "khong co"));
        IO.println("map      : " + map);

        // Bẫy 2: keySet là VIEW, xóa trên view là xóa thật trong map.
        map.keySet().remove("a");
        IO.println("sau remove tren keySet: " + map);
        //
        // TODO 3 (mục 3): Dùng remove(key, value) với giá trị đúng và với giá trị sai.
        //   Ghi lại giá trị trả về mỗi trường hợp.
        //
        // TODO 4 (mục 6): Gọi keySet().remove(...) rồi in map. Sau đó thử keySet().add(...)
        //   và dán exception nhận được vào đây.
        //
        // TODO 5 (mục 6): Tạo map có hai khóa cùng trỏ tới một giá trị, gọi values().remove(value).
        //   Ghi lại cặp nào bị xóa.
        //
        // TODO 6 (mục 7): Viết một method tra cứu phân biệt rõ ba trường hợp: không có khóa,
        //   khóa gắn null, khóa gắn giá trị thật.
    }
}
