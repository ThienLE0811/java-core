package com.javacore.api.collections_and_streams.collections_framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handling Map Values with Lambda Expressions
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 10/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Bài này nối thẳng với section Lambda Expressions (bước 14) - xem lại
 * {@code com.javacore.language.fp.lambdas} nếu quên BiConsumer, BiFunction.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/maps-and-lambdas/">dev.java - Handling Map Values with Lambda Expressions</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - HANDLING MAP VALUES WITH LAMBDA EXPRESSIONS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. DUYỆT NỘI DUNG MAP
 * - (Consuming the Content of a Map)
 * - {@code forEach(BiConsumer<K, V>)} - gọn hơn duyệt entrySet.
 * <p>
 * 2. THAY GIÁ TRỊ
 * - (Replacing Values)
 * - {@code replace(key, value)}, {@code replace(key, oldValue, newValue)},
 * {@code replaceAll(BiFunction)}.
 * - replace KHÁC put: replace không tạo cặp mới nếu khóa chưa tồn tại.
 * <p>
 * 3. TÍNH GIÁ TRỊ
 * - (Computing Values)
 * - {@code compute()}, {@code computeIfPresent()}, {@code computeIfAbsent()}.
 * - Hàm remapping trả về null thì cặp đó bị XÓA khỏi map.
 * - computeIfAbsent là cách chuẩn để xây map-of-list (gom nhóm).
 * <p>
 * 4. GỘP GIÁ TRỊ
 * - (Merging Values)
 * - {@code merge(key, value, BiFunction)} - nếu khóa chưa có thì đặt value,
 * nếu đã có thì gọi hàm gộp giá trị cũ với giá trị mới.
 * - Mẫu kinh điển: đếm tần suất từ.
 * <p>
 * 5. THỰC HÀNH
 * - (Practicing Map Operations)
 * - Năm ví dụ: BiConsumer, các method replace, compute, computeIfAbsent để gom nhóm,
 * merge để đếm tần suất.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. computeIfAbsent SO VỚI putIfAbsent
 * - putIfAbsent LUÔN tạo giá trị mới (tốn kém nếu là new ArrayList<>()), computeIfAbsent chỉ
 * gọi hàm khi thực sự thiếu khóa. Với map-of-list thì computeIfAbsent là lựa chọn đúng.
 * <p>
 * 7. KHÔNG SỬA MAP BÊN TRONG HÀM REMAPPING
 * - Javadoc cấm hàm truyền vào compute/merge tự sửa chính map đó - hành vi không xác định.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - replace khác put ở đâu?
 * - compute, computeIfPresent, computeIfAbsent khác nhau ở điều kiện nào?
 * - Hàm remapping trả về null thì điều gì xảy ra?
 * - merge làm gì khi khóa chưa tồn tại?
 * - Viết lại "đếm tần suất từ" bằng ba cách: vòng lặp thuần, computeIfAbsent, merge.
 */
public class t10_MapsAndLambdas {

    void main() {
        // TODO 1 (mục 1): Duyệt map bằng forEach với BiConsumer, so với duyệt entrySet.
        //   Ghi lại cách nào dễ đọc hơn và vì sao.
        //
        // TODO 2 (mục 2): Gọi replace trên một khóa KHÔNG tồn tại, rồi gọi put trên cùng khóa.
        //   In map sau mỗi lệnh.
        //
        // TODO 3 (mục 3): Xây map-of-list (gom chuỗi theo chữ cái đầu) bằng computeIfAbsent.
        //   Viết lại cùng việc đó bằng vòng lặp thuần để thấy khác biệt.
        //
        // TODO 4 (mục 3): Dùng compute với hàm trả về null và kiểm tra map sau đó.
        //
        // TODO 5 (mục 4): Đếm tần suất từ trong một câu bằng merge.
        //
        List<String> ten = List.of("An", "Binh", "An", "Cuong", "Binh", "An");

        // Thần chú 1 - merge: đếm số lần xuất hiện.
        Map<String, Integer> demSoLan = new HashMap<>();
        for (String t : ten) {
            demSoLan.merge(t, 1, Integer::sum);
        }
        IO.println("dem    : " + demSoLan);

        // Thần chú 2 - computeIfAbsent: gom nhóm theo chữ cái đầu.
        Map<Character, List<String>> gomNhom = new HashMap<>();
        for (String t : ten) {
            gomNhom.computeIfAbsent(t.charAt(0), c -> new ArrayList<>()).add(t);
        }
        IO.println("gom    : " + gomNhom);

        // forEach với BiConsumer - duyệt gọn hơn entrySet (TODO 1).
        demSoLan.forEach((k, v) -> IO.println("  " + k + " xuat hien " + v + " lan"));
        //
        // TODO 6 (mục 6): So sánh putIfAbsent và computeIfAbsent khi giá trị là new ArrayList<>().
        //   Đếm số lần ArrayList được tạo ra ở mỗi cách.
    }
}
