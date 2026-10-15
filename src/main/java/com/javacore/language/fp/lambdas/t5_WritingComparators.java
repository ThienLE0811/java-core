package com.javacore.language.fp.lambdas;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.*;

/**
 * Writing and Combining Comparators
 * <p>
 * 0. Vị trí bài này
 * - Bước 14/41, bài 5/5 trong section Lambda Expressions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/lambdas/writing-comparators/">dev.java - Writing and Combining Comparators</a>
 * - Đây là bài áp dụng: gom lambda (t1), functional interface (t2), method reference (t3)
 * và phép ghép (t4) vào một API dùng hằng ngày.
 * <p>
 * ======================================================================
 * TRANG GỐC - COMPARATOR (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. CÀI ĐẶT COMPARATOR BẰNG LAMBDA
 * - {@code Comparator<T>} là functional interface với {@code compare(T, T)}.
 * - Hợp đồng của giá trị trả về: âm, không, dương nghĩa là gì.
 * <p>
 * 2. TẠO COMPARATOR BẰNG FACTORY METHOD
 * - {@code Comparator.comparing(Function)} - truyền key extractor, thường là method reference.
 * <p>
 * 3. NỐI CHUỖI COMPARATOR
 * - {@code thenComparing()} - sắp theo tiêu chí phụ khi tiêu chí chính bằng nhau.
 * <p>
 * 4. COMPARATOR CHUYÊN CHO PRIMITIVE
 * - {@code comparingInt()}, {@code comparingLong()}, {@code comparingDouble()} và bản
 * {@code thenComparingInt()}... - tránh boxing.
 * <p>
 * 5. SO SÁNH THEO THỨ TỰ TỰ NHIÊN
 * - {@code Comparator.naturalOrder()}, quan hệ với {@code Comparable.compareTo()}.
 * <p>
 * 6. ĐẢO NGƯỢC COMPARATOR
 * - {@code reversed()} - và cái bẫy: đặt {@code reversed()} ở đâu trong chuỗi
 * {@code comparing().thenComparing()} thì đảo cái nào.
 * <p>
 * 7. XỬ LÝ GIÁ TRỊ NULL
 * - {@code nullsFirst()}, {@code nullsLast()} - bọc comparator sẵn có.
 * - Phân biệt: null ở chính PHẦN TỬ với null ở KEY rút ra từ phần tử.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. HỢP ĐỒNG CỦA COMPARATOR
 * - Phải nhất quán (phản đối xứng, bắc cầu); nếu vi phạm,
 * {@code List.sort()} có thể ném {@code IllegalArgumentException: Comparison method violates
 * its general contract!}
 * - Mẹo so sánh số: vì sao {@code (a, b) -> a - b} là sai, dùng {@code Integer.compare} thay thế.
 * <p>
 * 9. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code arr.sort((a, b) => ...)} của JS trả về number giống hệt hợp đồng của Java.
 * - Khác: JS sort mặc định so sánh theo chuỗi; Java không có mặc định kiểu đó.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Hợp đồng giá trị trả về của {@code compare()} là gì?
 * - {@code Comparator.comparing(Person::getName)} rút gọn được đoạn lambda nào?
 * - Sắp theo họ rồi tới tên thì viết thế nào?
 * - {@code comparing(A).thenComparing(B).reversed()} đảo ngược những gì?
 * - Vì sao cần {@code comparingInt} khi đã có {@code comparing}?
 * - Danh sách có phần tử null, hoặc key null, thì xử lý ra sao?
 * - Vì sao {@code (a, b) -> a - b} nguy hiểm?
 */
public class t5_WritingComparators {

    void main() {
        // TODO 1 (mục 1): Tự viết Comparator<String> theo độ dài bằng lambda thuần.
        //

        List<String> names = new ArrayList<>(List.of("beta", "an", "al", "gamma"));

        names.sort(comparing(String::length));
        // [an, al, beta, gamma]   ← "an" trước "al" vì sort ổn định, giữ nguyên thứ tự cũ

        names.sort(comparing(String::length).thenComparing(naturalOrder()));
        // [al, an, beta, gamma]   ← cùng độ dài thì xét tiếp A-Z

        // TODO 2 (mục 2 và 3): Tạo một record Person(String lastName, String firstName, int age),
        //   rồi sắp xếp theo lastName, sau đó tới firstName, bằng comparing + thenComparing.
        //
        // TODO 3 (mục 6): Thêm .reversed() vào cuối chuỗi ở TODO 2 và quan sát kết quả.
        //   So sánh với việc chỉ đảo riêng tiêu chí đầu.
        //
        // TODO 4 (mục 7): Cho một phần tử null vào list rồi sort - dán exception vào đây.
        //   Sửa bằng nullsFirst/nullsLast và ghi lại khác biệt.
        //
        // TODO 5 (mục 8): Viết comparator (a, b) -> a - b cho hai số rất lớn/rất nhỏ
        //   (Integer.MAX_VALUE, Integer.MIN_VALUE) để thấy tràn số, rồi sửa bằng Integer.compare.
    }
}
