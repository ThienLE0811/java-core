package com.javacore.practice.javacore33.streams.nhanvien;

import java.util.*;

import static java.util.stream.Collectors.*;


/**
 * Bài 26 - Stream với Object: filter nhiều điều kiện, groupingBy, Collectors
 * (averagingLong / counting), sorted với Comparator và top-N.
 */
public class StreamObjectDemo {

    private static final List<Employee> NHAN_VIEN = EmployeeData.danhSach();

    private static final String DONG = "%-34s: %s%n";

    public static void main(String[] args) {
        locLuongCaoVaKinhNghiem();
        luongTrungBinhTheoPhongBan();
        topBaLuongCaoNhat();
        nhomTheoPhongBan();
        demSoNhanVienMoiPhongBan();
    }

    /**
     * Câu 1: lương TRÊN 15 triệu và kinh nghiệm TỪ 3 năm.
     */
    private static void locLuongCaoVaKinhNghiem() {
        System.out.println("=== 1. Lương > 15tr và kinh nghiệm >= 3 năm ===");

        List<Employee> luongCao =
                NHAN_VIEN.stream()
                        .filter(n -> n.salary() > 15_000_000 && n.yearsOfExperience() >= 3)
                        .toList();

        List<Employee> luongCao2 =
                NHAN_VIEN.stream()
                        .filter(n -> n.salary() > 15_000_000)
                        .filter(n -> n.yearsOfExperience() >= 3)
                        .toList();
        // Cách 1: Viết bằng Lambda thông thường
        //        luongCao.forEach(employee -> System.out.println(employee));
        // Cách 2: Viết siêu ngắn bằng Method Reference (::)
        luongCao.forEach(System.out::println);
        luongCao2.forEach(System.out::println);
        // Tự kiểm: Vũ Đức Dũng (16,5tr / 2 năm) và Phan Tuấn Minh (đúng 15tr) đều KHÔNG được lọt.

        System.out.println();
    }

    /**
     * Câu 2: lương trung bình theo phòng ban.
     */
    private static void luongTrungBinhTheoPhongBan() {
        System.out.println("=== 2. Lương trung bình theo phòng ban ===");

        // Cách 1: groupingBy 2 tham số -> HashMap mặc định, thứ tự key theo hash.
        Map<Department, Double> luongTrungBinh = NHAN_VIEN.stream().collect(groupingBy(Employee::department, averagingLong(Employee::salary)));
        luongTrungBinh.forEach((phong, luong) -> System.out.printf("Lương trung bình Phòng %s: %,.0f ", phong, luong));
        System.out.println();

        // Cách 2: thêm map factory EnumMap -> thứ tự key luôn theo thứ tự khai báo trong
        // enum, không phụ thuộc hash, chạy lại bao nhiêu lần cũng ra cùng một thứ tự.
        Map<Department, Double> luongTrungBinhTheoThuTu = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        averagingLong(Employee::salary)));

        luongTrungBinhTheoThuTu.forEach((phong, luong) ->
                System.out.printf(DONG, phong.tenHienThi(), String.format("%,.0f đ", luong)));

        // Cách 3: summarizingLong - một lượt duyệt lấy luôn count/sum/min/max/average,
        // thay vì stream 5 lần cho 5 con số.
        System.out.println();
        Map<Department, LongSummaryStatistics> thongKe = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        summarizingLong(Employee::salary)));

        thongKe.forEach((phong, tk) -> System.out.printf(
                "%-12s | số NV: %2d | thấp nhất: %,11d | cao nhất: %,11d | tổng: %,11d | TB: %,11.0f%n",
                phong.tenHienThi(), tk.getCount(), tk.getMin(), tk.getMax(), tk.getSum(), tk.getAverage()));

        System.out.println();
    }

    /**
     * Câu 3: top 3 nhân viên lương cao nhất.
     */
    private static void topBaLuongCaoNhat() {
        System.out.println("=== 3. Top 3 lương cao nhất ===");

        // TODO: sorted(Employee.LUONG_GIAM_DAN).limit(3).toList()
        List<Employee> topLuongCaoNhat = NHAN_VIEN.stream().sorted(Employee.LUONG_GIAM_DAN).limit(3).toList();
        topLuongCaoNhat.forEach(System.out::println);
        System.out.println();

        // TODO: làm thêm bản sorted(Comparator.comparingLong(Employee::salary).reversed()) - không tie-breaker.
        //       So hai kết quả: Trần Minh Bảo và Nguyễn Thị Uyên cùng 28,5tr, ai đứng trước?
        List<Employee> topLuongCaoNhat2 = NHAN_VIEN.stream().sorted(Comparator.comparingLong(Employee::salary).reversed()).limit(3).toList();
        topLuongCaoNhat2.forEach(System.out::println);
        System.out.println();
    }

    /**
     * Câu 4: nhóm nhân viên theo phòng ban.
     */
    private static void nhomTheoPhongBan() {
        System.out.println("=== 4. Nhóm theo phòng ban ===");

        Map<Department, List<Employee>> nhomTheoPhongBan = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        toList()));

        nhomTheoPhongBan.forEach((phong, danhSach) -> {
            System.out.printf("%s (%d người):%n", phong.tenHienThi(), danhSach.size());
            danhSach.forEach(nv -> System.out.println("    " + nv));
            System.out.println();
        });

        // mapping: biến đổi từng phần tử TRƯỚC khi gom vào list của nhóm.
        // Ở đây chỉ giữ lại tên thay vì cả object Employee - đúng kiểu DTO mỏng trả về cho API.
        Map<Department, List<String>> tenTheoPhongBan = NHAN_VIEN.stream()
                .collect(groupingBy(
                        Employee::department,
                        () -> new EnumMap<>(Department.class),
                        mapping(Employee::name, toList())));

        tenTheoPhongBan.forEach((phong, tenNhanVien) ->
                System.out.printf(DONG, phong.tenHienThi(), String.join(", ", tenNhanVien)));
        System.out.println();

        // partitioningBy: chia ĐÔI theo một điều kiện boolean, key chỉ có true/false.
        Map<Boolean, List<Employee>> theoMucLuong = NHAN_VIEN.stream()
                .collect(partitioningBy(e -> e.salary() > 20_000_000));

        System.out.printf(DONG, "Lương > 20tr", theoMucLuong.get(true).size() + " người");
        System.out.printf(DONG, "Lương <= 20tr", theoMucLuong.get(false).size() + " người");

        // Khác biệt với groupingBy: partitioningBy LUÔN có đủ cả 2 key, kể cả khi một bên rỗng.
        Map<Boolean, List<Employee>> khongAiDat = NHAN_VIEN.stream()
                .collect(partitioningBy(e -> e.salary() > 999_000_000));
        Map<Boolean, List<Employee>> cungDieuKienNhungGroupingBy = NHAN_VIEN.stream()
                .collect(groupingBy(e -> e.salary() > 999_000_000));

        System.out.printf(DONG, "[không ai đạt] partitioningBy", khongAiDat.keySet());
        System.out.printf(DONG, "[không ai đạt] groupingBy", cungDieuKienNhungGroupingBy.keySet());

        System.out.println();
    }

    /**
     * Câu 5: đếm số nhân viên mỗi phòng ban.
     */
    private static void demSoNhanVienMoiPhongBan() {
        System.out.println("=== 5. Số nhân viên mỗi phòng ban ===");
        Map<Department, Long> soNhanVienMoiPhongBan = NHAN_VIEN.stream()
                .collect(groupingBy(Employee::department, () -> new EnumMap<>(Department.class), counting()));

        Long tong = soNhanVienMoiPhongBan.values().stream().mapToLong(Long::longValue).sum();
        soNhanVienMoiPhongBan.forEach((phong, soLuong) -> {
            System.out.printf("%s: %d người", phong.tenHienThi(), soLuong);
            System.out.println();
        });
        System.out.printf("Tổng các giá trị: %d%n", tong);
    }
}
