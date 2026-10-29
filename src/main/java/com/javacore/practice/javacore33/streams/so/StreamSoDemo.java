package com.javacore.practice.javacore33.streams.so;

import com.javacore.practice.javacore33.basics.SoNguyenTo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Bài 25 - Stream với số: filter / map / reduce trên List&lt;Integer&gt;, so sánh
 * IntStream với Stream&lt;Integer&gt;, tính lazy và stream chỉ dùng được một lần.
 */
public class StreamSoDemo {

    // Dữ liệu dùng chung cho mọi demo: 1..50
    private static final List<Integer> SO = IntStream.rangeClosed(1, 50).boxed().toList();

    // Căn nhãn cho thẳng cột thay vì đếm dấu cách thủ công ở từng dòng print.
    private static final String DONG = "%-34s: %s%n";

    public static void main(String[] args) {
        locSoNguyenTo();
        tongBinhPhuongSoLe();
        topNamSauKhiNhanBa();
        demoMatch();
        demoReduce();
        demoLazyVaMotLan();
    }

    private static void locSoNguyenTo() {
        System.out.println("=== 1. Lọc số nguyên tố ===");

        List<Integer> soNguyenTo = SO.stream().filter(SoNguyenTo::laSoNguyenTo).toList();

        System.out.printf(DONG, "Danh sách số nguyên tố", soNguyenTo);
        System.out.printf(DONG, "Số lượng", soNguyenTo.size());
        System.out.println();
    }

    private static void tongBinhPhuongSoLe() {
        System.out.println("=== 2. Tổng bình phương các số lẻ ===");

        // Cách 1: mapToInt -> IntStream, không boxing, có sẵn sum().
        int tongCach1 = SO.stream()
                .filter(n -> n % 2 != 0)
                .mapToInt(n -> n * n)
                .sum();

        // Cách 2: map giữ Stream<Integer> (boxing từng phần tử), tự cộng bằng reduce.
        int tongCach2 = SO.stream()
                .filter(n -> n % 2 != 0)
                .map(n -> n * n)
                .reduce(0, Integer::sum);

        System.out.printf(DONG, "Tổng bình phương (mapToInt.sum)", tongCach1);
        System.out.printf(DONG, "Tổng bình phương (map.reduce)", tongCach2);
        System.out.printf(DONG, "Hai cách bằng nhau?", tongCach1 == tongCach2);

        // average() trả OptionalDouble chứ không phải double: stream có thể rỗng,
        // mà "trung bình của không phần tử nào" thì không có giá trị nào để trả.
        OptionalDouble trungBinh = SO.stream()
                .filter(n -> n % 2 != 0)
                .mapToInt(n -> n * n)
                .average();

        System.out.printf(DONG, "Kiểu trả về của average()", trungBinh.getClass().getSimpleName());
        System.out.printf(DONG, "Trung bình", trungBinh.orElse(0));
        System.out.println();
    }

    private static void topNamSauKhiNhanBa() {
        System.out.println("=== 3. Top 5 sau khi nhân 3 ===");

        // "Top 5" ở đây hiểu là 5 giá trị LỚN NHẤT sau khi nhân 3.
        List<Integer> topNam = SO.stream()
                .map(n -> n * 3)
                .sorted(Comparator.reverseOrder())
                .limit(5)
                .toList();

        // Đảo thứ tự limit/map: lấy 5 phần tử ĐẦU rồi mới nhân 3 - kết quả khác hẳn.
        List<Integer> namPhanTuDau = SO.stream()
                .limit(5)
                .map(n -> n * 3)
                .toList();

        System.out.printf(DONG, "map -> sorted desc -> limit(5)", topNam);
        System.out.printf(DONG, "limit(5) -> map", namPhanTuDau);

        // sorted() là stateful: phải nuốt hết 50 phần tử rồi mới nhả được phần tử đầu,
        // nên limit(5) đặt sau nó không tiết kiệm được gì. Đếm để thấy rõ.
        AtomicInteger demCoSort = new AtomicInteger();
        SO.stream()
                .peek(n -> demCoSort.incrementAndGet())
                .map(n -> n * 3)
                .sorted(Comparator.reverseOrder())
                .limit(5)
                .toList();

        AtomicInteger demKhongSort = new AtomicInteger();
        SO.stream()
                .peek(n -> demKhongSort.incrementAndGet())
                .limit(5)
                .map(n -> n * 3)
                .toList();

        System.out.printf(DONG, "Số phần tử duyệt khi có sorted", demCoSort.get());
        System.out.printf(DONG, "Số phần tử duyệt khi limit trước", demKhongSort.get());
        System.out.println();
    }

    private static void demoMatch() {
        System.out.println("=== 4. anyMatch / allMatch / noneMatch ===");

        System.out.printf(DONG, "Có số nào chia hết cho 17?", SO.stream().anyMatch(n -> n % 17 == 0));
        System.out.printf(DONG, "Mọi số đều > 0?", SO.stream().allMatch(n -> n > 0));
        System.out.printf(DONG, "Mọi số đều là số nguyên tố?", SO.stream().allMatch(SoNguyenTo::laSoNguyenTo));
        System.out.printf(DONG, "Không có số nào > 100?", SO.stream().noneMatch(n -> n > 100));

        // Bẫy: trên stream RỖNG, allMatch luôn trả true (vacuous truth) còn anyMatch
        // luôn trả false. Ở production đây là nguồn bug thật kiểu
        // donHang.stream().allMatch(DonHang::daThanhToan) trả true khi khách chưa có đơn nào.
        List<Integer> rong = List.of();
        System.out.printf(DONG, "[rỗng] allMatch(n -> n < 0)", rong.stream().allMatch(n -> n < 0));
        System.out.printf(DONG, "[rỗng] anyMatch(n -> n < 0)", rong.stream().anyMatch(n -> n < 0));
        System.out.println();
    }

    private static void demoReduce() {
        System.out.println("=== 5. reduce ===");

        // Dạng 1: không có giá trị khởi tạo -> trả Optional, vì stream có thể rỗng.
        Optional<Integer> tongCoThe = SO.stream().reduce(Integer::sum);
        System.out.printf(DONG, "reduce(Integer::sum)", tongCoThe);
        System.out.printf(DONG, "Lấy giá trị ra", tongCoThe.orElse(0));

        // Dạng 2: có identity (0) -> luôn có kết quả, trả thẳng Integer.
        int tong = SO.stream().reduce(0, Integer::sum);
        System.out.printf(DONG, "reduce(0, Integer::sum)", tong);

        // Không phải phép cộng: reduce chỉ cần một phép kết hợp hai giá trị thành một.
        System.out.printf(DONG, "Tìm max bằng reduce", SO.stream().reduce(Integer::max).orElse(0));

        // Khác biệt lộ ra trên stream rỗng: một bên Optional.empty, một bên ra identity.
        List<Integer> rong = List.of();
        System.out.printf(DONG, "[rỗng] reduce(Integer::sum)", rong.stream().reduce(Integer::sum));
        System.out.printf(DONG, "[rỗng] reduce(0, Integer::sum)", rong.stream().reduce(0, Integer::sum));
        System.out.println();
    }

    private static void demoLazyVaMotLan() {
        System.out.println("=== 6. Lazy và stream dùng một lần ===");

        // Pipeline KHÔNG có terminal operation: peek không chạy lần nào.
        AtomicInteger demKhongTerminal = new AtomicInteger();
        SO.stream()
                .peek(n -> demKhongTerminal.incrementAndGet())
                .filter(n -> n % 2 == 0);

        System.out.printf(DONG, "Số phần tử duyệt (không terminal)", demKhongTerminal.get());

        // Cùng pipeline nhưng có findFirst: dừng ngay khi tìm được, không duyệt hết 50.
        AtomicInteger demCoTerminal = new AtomicInteger();
        int soDauTien = SO.stream()
                .peek(n -> demCoTerminal.incrementAndGet())
                .filter(n -> n % 7 == 0)
                .findFirst()
                .orElseThrow();

        System.out.printf(DONG, "Số chia hết cho 7 đầu tiên", soDauTien);
        System.out.printf(DONG, "Số phần tử duyệt (có findFirst)", demCoTerminal.get() + " / " + SO.size());

        // Stream là cái vòi, không phải cái thùng: xài xong một terminal op là hết.
        Stream<Integer> stream = SO.stream();
        System.out.printf(DONG, "Dùng lần 1 (count)", stream.count());
        try {
            stream.filter(n -> n > 0).count();
        } catch (IllegalStateException e) {
            System.out.printf(DONG, "Dùng lần 2", e.getMessage());
        }
    }
}
