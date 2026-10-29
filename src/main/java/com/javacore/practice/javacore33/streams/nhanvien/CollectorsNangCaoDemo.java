package com.javacore.practice.javacore33.streams.nhanvien;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

/**
 * Bài 27 - Collectors nâng cao: partitioningBy, joining, toMap (xử lý key trùng),
 * summarizingDouble. Kèm 2 cái bẫy kinh điển: toMap ném IllegalStateException khi
 * key trùng, và dùng double cho tiền.
 */
public class CollectorsNangCaoDemo {

    private static final List<Employee> NHAN_VIEN = EmployeeData.danhSach();

    private static final long MUC_LUONG_CAO = 20_000_000;

    private static final String DONG = "%-34s: %s%n";

    public static void main(String[] args) {
        chiaHaiNhomTheoLuong();
        ghepTenTheoPhongBan();
        lapChiMucTheoTen();
        bayToMapKeyTrung();
        thongKeLuong();
        bayDungDoubleChoTien();
    }

    /**
     * Câu 1: partitioningBy - chia đôi theo lương trên / dưới 20 triệu.
     */
    private static void chiaHaiNhomTheoLuong() {
        System.out.println("=== 1. partitioningBy: lương > 20tr ===");

        Predicate<Employee> luongCao = e -> e.salary() > MUC_LUONG_CAO;

        // 1a: chia đôi danh sách. Map trả về luôn có đúng 2 key: false rồi true.
        Map<Boolean, List<Employee>> haiNhom = NHAN_VIEN.stream()
                .collect(partitioningBy(luongCao));

        haiNhom.forEach((vuotMuc, danhSach) -> {
            System.out.printf("%s (%d người):%n",
                    vuotMuc ? "Lương > 20tr" : "Lương <= 20tr", danhSach.size());
            danhSach.forEach(nv -> System.out.println("    " + nv));
        });
        System.out.println();

        // 1b: downstream counting() - chỉ cần con số thì không việc gì phải dựng cả List rồi .size().
        Map<Boolean, Long> soNguoi = NHAN_VIEN.stream()
                .collect(partitioningBy(luongCao, counting()));
        System.out.printf(DONG, "Số người lương > 20tr", soNguoi.get(true));
        System.out.printf(DONG, "Số người lương <= 20tr", soNguoi.get(false));

        // 1c: downstream averagingLong - lương trung bình của từng nửa.
        Map<Boolean, Double> luongTrungBinh = NHAN_VIEN.stream()
                .collect(partitioningBy(luongCao, averagingLong(Employee::salary)));
        System.out.printf(DONG, "Lương TB nhóm > 20tr",
                String.format("%,.0f đ", luongTrungBinh.get(true)));
        System.out.printf(DONG, "Lương TB nhóm <= 20tr",
                String.format("%,.0f đ", luongTrungBinh.get(false)));
        System.out.println();

        // 1d: lồng partitioningBy vào trong groupingBy -> map hai tầng.
        Map<Department, Map<Boolean, List<String>>> theoPhongRoiTheoMuc = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        partitioningBy(luongCao, mapping(Employee::name, toList()))));

        theoPhongRoiTheoMuc.forEach((phong, theoMuc) -> {
            System.out.println(phong.tenHienThi() + ":");
            System.out.printf("    %-10s %s%n", "> 20tr :", theoMuc.get(true));
            System.out.printf("    %-10s %s%n", "<= 20tr:", theoMuc.get(false));
        });
        // Marketing không có ai > 20tr nhưng key true vẫn tồn tại với list RỖNG - đây chính là
        // lý do partitioningBy an toàn hơn groupingBy khi bạn định gọi thẳng .get(true).size().

        System.out.println();
    }

    /**
     * Câu 2: joining - ghép tên nhân viên theo phòng ban.
     */
    private static void ghepTenTheoPhongBan() {
        System.out.println("=== 2. joining: ghép tên theo phòng ban ===");

        String tatCaTenDinhLien = NHAN_VIEN.stream().map(Employee::name).collect(joining());
        System.out.printf(DONG, "joining()", tatCaTenDinhLien);
        String tatCaTenDauPhay = NHAN_VIEN.stream().map(Employee::name).collect(joining(", "));
        System.out.printf(DONG, "joining(\", \")", tatCaTenDauPhay);
        String tatCaTenPrefix = NHAN_VIEN.stream().map(Employee::name).collect(joining(", ", "[", "]"));
        System.out.printf(DONG, "joining(\", \", \"[\", \"]\")", tatCaTenPrefix);

        // Stream rỗng + joining 3 tham số -> vẫn ra "[]", không phải chuỗi rỗng.
        String khongAiDat = NHAN_VIEN.stream()
                .filter(e -> e.salary() > 999_000_000)
                .map(Employee::name)
                .collect(joining(", ", "[", "]"));
        System.out.printf(DONG, "joining trên stream rỗng", khongAiDat);
        System.out.println();

        // 2d: gộp với groupingBy -> Map<Department, String>. Khác bài 26 câu 4
        // (mapping + toList rồi String.join): joining gom thẳng vào StringBuilder,
        // không dựng List trung gian.
        Map<Department, String> tenTheoPhongBan = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        mapping(Employee::name, joining(", "))));

        tenTheoPhongBan.forEach((phong, ten) -> System.out.printf(DONG, phong.tenHienThi(), ten));

        System.out.println();
    }

    /**
     * Câu 3: toMap - lập chỉ mục nhân viên theo tên (key duy nhất).
     */
    private static void lapChiMucTheoTen() {
        System.out.println("=== 3. toMap: tra cứu nhân viên theo tên ===");

        // 3a: lập chỉ mục tên -> object. Đây là mẫu chống N+1 query hay dùng nhất:
        // load một phát toàn bộ danh sách rồi tra cứu trong bộ nhớ, thay vì gọi
        // repository bên trong vòng lặp.
        Map<String, Employee> theoTen = NHAN_VIEN.stream()
                .collect(toMap(Employee::name, e -> e));

        System.out.printf(DONG, "Số key", theoTen.size());
        System.out.printf(DONG, "Tra cứu 'Ngô Thị Lan'", theoTen.get("Ngô Thị Lan"));
        // Tên không tồn tại -> get() trả null, KHÔNG ném exception. Luôn nhớ kiểm tra.
        System.out.printf(DONG, "Tra cứu tên không có", theoTen.get("Không Tồn Tại"));

        // 3b: chỉ cần một trường thì map thẳng sang trường đó, nhẹ hơn giữ cả object.
        Map<String, Long> luongTheoTen = NHAN_VIEN.stream()
                .collect(toMap(Employee::name, Employee::salary));
        System.out.printf(DONG, "Lương của Ngô Thị Lan",
                String.format("%,d đ", luongTheoTen.get("Ngô Thị Lan")));
        System.out.println();

        // 3c: toMap 4 tham số - tham số thứ 4 là map factory.
        // HashMap không hứa thứ tự; TreeMap sắp key theo alphabet, in ra ổn định.
        // Lưu ý: đã dùng bản 4 tham số thì BẮT BUỘC truyền merge function ở tham số 3,
        // không có overload (keyMapper, valueMapper, mapFactory).
        Map<String, Long> luongTheoTenSapXep = NHAN_VIEN.stream()
                .collect(toMap(Employee::name, Employee::salary, (cu, moi) -> moi, TreeMap::new));

        luongTheoTenSapXep.forEach((ten, luong) ->
                System.out.printf("%-18s: %,13d đ%n", ten, luong));

        System.out.println();
    }

    /**
     * BẪY 1: toMap với key trùng -> IllegalStateException.
     */
    private static void bayToMapKeyTrung() {
        System.out.println("=== 4. BẪY: toMap khi key trùng ===");

        // Trần Minh Bảo (Kỹ thuật) và Nguyễn Thị Uyên (Kế toán) cùng lương 28.500.000.
        // Lấy lương làm key -> hai phần tử đụng nhau.
        try {
            Map<Long, String> theoLuong = NHAN_VIEN.stream()
                    .collect(toMap(Employee::salary, Employee::name));
            System.out.println(theoLuong);
        } catch (IllegalStateException e) {
            System.out.printf(DONG, "Nổ như dự đoán", e.getClass().getSimpleName());
            System.out.printf(DONG, "Thông điệp", e.getMessage());
        }

        // Cách sửa 1: merge function - quyết định giữ cái nào khi đụng key.
        Map<Long, String> giuNguoiDauTien = NHAN_VIEN.stream()
                .collect(toMap(Employee::salary, Employee::name, (cu, moi) -> cu));
        Map<Long, String> gopCaHai = NHAN_VIEN.stream()
                .collect(toMap(Employee::salary, Employee::name, (cu, moi) -> cu + " & " + moi));

        System.out.printf(DONG, "Giữ người đầu tiên (28,5tr)", giuNguoiDauTien.get(28_500_000L));
        System.out.printf(DONG, "Gộp cả hai (28,5tr)", gopCaHai.get(28_500_000L));

        // Cách sửa 2: nếu key trùng là CHUYỆN BÌNH THƯỜNG của bài toán thì đừng dùng toMap,
        // dùng groupingBy -> giá trị là List, không mất dữ liệu.
        Map<Long, List<String>> nhomTheoLuong = NHAN_VIEN.stream()
                .collect(groupingBy(Employee::salary, mapping(Employee::name, toList())));
        System.out.printf(DONG, "groupingBy (28,5tr)", nhomTheoLuong.get(28_500_000L));

        // Bẫy phụ: toMap ném NullPointerException khi VALUE là null, kể cả lúc key không
        // trùng - khác hẳn HashMap.put(key, null) vốn chấp nhận bình thường.
        try {
            Map<String, Department> coValueNull = NHAN_VIEN.stream()
                    .limit(3)
                    .collect(toMap(Employee::name, e -> (Department) null));
            System.out.println(coValueNull);
        } catch (NullPointerException e) {
            System.out.printf(DONG, "toMap với value null", "NullPointerException");
        }

        System.out.println();
    }

    /**
     * Câu 5: summarizingDouble - thống kê lương một lượt duyệt.
     */
    private static void thongKeLuong() {
        System.out.println("=== 5. summarizingDouble: thống kê lương ===");

        // 5a: một lượt duyệt lấy luôn 5 con số, thay vì 5 lần stream.
        DoubleSummaryStatistics toanCongTy = NHAN_VIEN.stream()
                .collect(summarizingDouble(Employee::salary));

        System.out.printf(DONG, "Số nhân viên", toanCongTy.getCount());
        System.out.printf(DONG, "Tổng quỹ lương", String.format("%,.0f đ", toanCongTy.getSum()));
        System.out.printf(DONG, "Thấp nhất", String.format("%,.0f đ", toanCongTy.getMin()));
        System.out.printf(DONG, "Cao nhất", String.format("%,.0f đ", toanCongTy.getMax()));
        System.out.printf(DONG, "Trung bình", String.format("%,.0f đ", toanCongTy.getAverage()));
        System.out.println();

        // 5b: gộp với groupingBy -> thống kê từng phòng ban.
        Map<Department, DoubleSummaryStatistics> theoPhongBan = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        summarizingDouble(Employee::salary)));

        theoPhongBan.forEach((phong, tk) -> System.out.printf(
                "%-12s | số NV: %2d | thấp nhất: %,13.0f | cao nhất: %,13.0f | tổng: %,13.0f | TB: %,13.0f%n",
                phong.tenHienThi(), tk.getCount(), tk.getMin(), tk.getMax(), tk.getSum(), tk.getAverage()));
        System.out.println();

        // 5c: so sánh cách HIỂN THỊ của Long và Double statistics - cùng một con số.
        LongSummaryStatistics banLong = NHAN_VIEN.stream()
                .collect(summarizingLong(Employee::salary));

        System.out.printf(DONG, "toString() của LongSummary", banLong);
        System.out.printf(DONG, "toString() của DoubleSummary", toanCongTy);
        System.out.printf(DONG, "sum kiểu long", banLong.getSum());
        System.out.printf(DONG, "sum kiểu double", toanCongTy.getSum());
        // Với 24 bản ghi thì double chưa sai (số nguyên dưới 2^53 biểu diễn chính xác),
        // nhưng nếu đem con số đó đi cộng/chia tiếp thì sai số bắt đầu tích lũy - xem BẪY 2.

        System.out.println();
    }

    /**
     * BẪY 2: dùng double cho tiền.
     */
    private static void bayDungDoubleChoTien() {
        System.out.println("=== 6. BẪY: double không dùng cho tiền ===");

        // Kinh điển: 0.1 + 0.2 trong hệ nhị phân không ra đúng 0.3.
        System.out.printf(DONG, "0.1 + 0.2 == 0.3 ?", (0.1 + 0.2) == 0.3);
        System.out.printf(DONG, "0.1 + 0.2", 0.1 + 0.2);

        // Với tiền: cộng 0.1 đồng 10 lần phải ra đúng 1 đồng.
        double congBangDouble = 0;
        for (int i = 0; i < 10; i++) {
            congBangDouble += 0.1;
        }
        System.out.printf(DONG, "Cộng 0.1 mười lần (double)", congBangDouble);

        BigDecimal congBangBigDecimal = BigDecimal.ZERO;
        for (int i = 0; i < 10; i++) {
            congBangBigDecimal = congBangBigDecimal.add(new BigDecimal("0.1"));
        }
        System.out.printf(DONG, "Cộng 0.1 mười lần (BigDecimal)", congBangBigDecimal);

        // Lương trung bình: average() của mọi SummaryStatistics đều trả double.
        // Dùng để HIỂN THỊ thì được, dùng để TÍNH TIẾP rồi ghi vào DB thì không.
        double tbDouble = NHAN_VIEN.stream().mapToLong(Employee::salary).average().orElse(0);

        BigDecimal tongTien = NHAN_VIEN.stream()
                .map(e -> BigDecimal.valueOf(e.salary()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tbBigDecimal = tongTien.divide(
                BigDecimal.valueOf(NHAN_VIEN.size()), 2, RoundingMode.HALF_UP);

        System.out.printf(DONG, "Lương TB (double)", tbDouble);
        System.out.printf(DONG, "Lương TB (BigDecimal)", tbBigDecimal);

        // Quy tắc production: tiền dùng BigDecimal (hoặc long theo đơn vị nhỏ nhất),
        // trong JPA map sang NUMERIC/DECIMAL, KHÔNG bao giờ map sang DOUBLE PRECISION.
        // Khởi tạo BigDecimal bằng String hoặc BigDecimal.valueOf(),
        // không bao giờ new BigDecimal(0.1) - nó bê nguyên sai số của double vào.
        System.out.printf(DONG, "new BigDecimal(0.1)", new BigDecimal(0.1));
        System.out.printf(DONG, "new BigDecimal(\"0.1\")", new BigDecimal("0.1"));

        System.out.println();
    }
}
