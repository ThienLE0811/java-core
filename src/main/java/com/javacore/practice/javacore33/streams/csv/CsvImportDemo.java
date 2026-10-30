package com.javacore.practice.javacore33.streams.csv;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;

/**
 * Bài 30 - Demo import CSV: validate từng dòng, gom lỗi, rồi báo cáo trên phần hợp lệ.
 *
 * <p>Phần logic nằm ở {@link CsvImporter}. File này chỉ lo chuẩn bị dữ liệu và trình bày
 * kết quả - đúng vai của một controller trong Spring Boot.
 */
public class CsvImportDemo {

    private static final String DONG = "%-34s: %s%n";

    private static final Path THU_MUC = Path.of("target", "bai30");
    private static final Path FILE_CSV = THU_MUC.resolve("nhanvien.csv");
    private static final Path FILE_SAI_HEADER = THU_MUC.resolve("sai-header.csv");

    // Mốc cố định để tính thâm niên. Dùng LocalDate.now() thì output đổi theo ngày chạy,
    // demo sẽ không đối chiếu được với bảng TỰ KIỂM ở cuối file.
    private static final LocalDate MOC_TINH_THAM_NIEN = LocalDate.of(2026, 1, 1);

    /**
     * File mẫu: mỗi dòng lỗi minh hoạ đúng MỘT loại lỗi, để output dễ đối chiếu.
     * Dòng 11 cố tình để trống - dòng trống không phải lỗi, chỉ bị bỏ qua.
     */
    private static final String NOI_DUNG_MAU = """
            id,hoTen,email,phongBan,luong,ngayVaoLam
            1,Nguyen Van An,an.nguyen@example.com,IT,25000000,2020-03-15
            2,Tran Thi Binh,binh.tran@example.com,KE_TOAN,18000000,2019-07-01
            3,,cuong.le@example.com,IT,22000000,2021-01-10
            4,Le Van Cuong,khong-phai-email,IT,22000000,2021-01-10
            5,Pham Thi Dung,dung.pham@example.com,IT,hai muoi trieu,2022-05-20
            6,Hoang Van Em,em.hoang@example.com,NHAN_SU,-5000000,2022-05-20
            7,Vu Thi Phuong,phuong.vu@example.com,IT,30000000,20-05-2022
            8,Do Van Giang,giang.do@example.com,MARKETING,28000000
            9,Bui Thi Hoa,an.nguyen@example.com,IT,26000000,2023-02-01
            
            10,Dang Thi Lan,lan.dang@example.com,KHONG_TON_TAI,20000000,2023-09-01
            11,Ngo Van Khanh,khanh.ngo@example.com,IT,27000000,2023-06-01
            """;

    private static final String NOI_DUNG_SAI_HEADER = """
            ma,ten,mail
            1,Nguyen Van An,an.nguyen@example.com
            """;

    public static void main(String[] args) throws IOException {
        chuanBiFile();

        // Đọc MỘT lần rồi truyền kết quả đi, đừng gọi CsvImporter.doc() trong từng hàm in.
        KetQuaImport ketQua = CsvImporter.doc(FILE_CSV);

        inTongQuan(ketQua);
        inChiTietLoi(ketQua);
        baoCaoTheoPhongBan(ketQua);
        baoCaoLuong(ketQua);
        demoSaiHeader();
    }

    /**
     * Bước 0: sinh file CSV mẫu để demo chạy được trên máy bất kỳ.
     */
    private static void chuanBiFile() throws IOException {
        Files.createDirectories(THU_MUC);

        Files.writeString(FILE_CSV, NOI_DUNG_MAU, StandardCharsets.UTF_8);
        Files.writeString(FILE_SAI_HEADER, NOI_DUNG_SAI_HEADER, StandardCharsets.UTF_8);
        System.out.printf(DONG, "Đường dẫn", FILE_CSV.toAbsolutePath());
        System.out.printf(DONG, "Kích thước (byte)", Files.size(FILE_CSV));
    }


    /**
     * Bước 1: con số tổng quan - thứ đầu tiên người dùng nhìn sau khi upload.
     */
    private static void inTongQuan(KetQuaImport ketQua) {
        System.out.println("=== 1. Tổng quan ===");
        System.out.printf(DONG, "Số dòng hợp lệ", ketQua.soDongHopLe());
        System.out.printf(DONG, "Số lỗi", ketQua.soLoi());
        System.out.printf(DONG, "Số dòng lỗi", ketQua.soDongLoi());
        System.out.printf(DONG, "Thành công hoàn toàn", ketQua.thanhCongHoanToan());
        System.out.println();
    }

    /**
     * Bước 2: chi tiết lỗi - phải đủ để người dùng mở Excel lên sửa được ngay.
     */
    private static void inChiTietLoi(KetQuaImport ketQua) {
        System.out.println("=== 2. Chi tiết lỗi ===");
        // println(Object) tự gọi toString(). Đừng dùng printf ở đây: tham số đầu của printf là
        // FORMAT STRING, nhét dữ liệu người dùng vào đó là format string injection.
        ketQua.cacLoi().forEach(System.out::println);
        System.out.println();

        Map<String, Long> loiTheoCot = ketQua.cacLoi().stream()
                .collect(Collectors.groupingBy(LoiDong::cot, TreeMap::new, Collectors.counting()));

        System.out.println("Số lỗi theo cột:");
        loiTheoCot.forEach((cot, soLuong) -> System.out.printf("    %-12s %d%n", cot, soLuong));
        System.out.println();
    }

    /**
     * Bước 3: báo cáo theo phòng ban - nối lại bài 26 (groupingBy).
     */
    private static void baoCaoTheoPhongBan(KetQuaImport ketQua) {
        System.out.println("=== 3. Theo phòng ban ===");
        Map<PhongBan, Long> soNhanVienMoiPhong = ketQua.hopLe().stream()
                .collect(Collectors.groupingBy(NhanVien::phongBan,
                        () -> new EnumMap<>(PhongBan.class), Collectors.counting()));

        System.out.println("Số nhân viên mỗi phòng:");
        // tenHienThi() có ngoặc: ở đây cần GIÁ TRỊ, không phải method reference.
        // %-22s vì "Công nghệ thông tin" dài 19 ký tự, %-12s sẽ tràn cột.
        soNhanVienMoiPhong.forEach((phongBan, soLuong) ->
                System.out.printf("    %-22s %d%n", phongBan.tenHienThi(), soLuong));

        // EnumMap ở cả hai bảng để thứ tự phòng ban in ra luôn khớp nhau.
        Map<PhongBan, String> hoTenMoiPhong = ketQua.hopLe().stream()
                .collect(Collectors.groupingBy(NhanVien::phongBan,
                        () -> new EnumMap<>(PhongBan.class), mapping(NhanVien::hoTen, joining(", "))));

        System.out.println("Danh sách nhân viên mỗi phòng:");
        // %s chứ không %d: hoTen là String. printf chỉ kiểm tra format lúc CHẠY.
        hoTenMoiPhong.forEach((phongBan, hoTen) ->
                System.out.printf("    %-22s %s%n", phongBan.tenHienThi(), hoTen));
        System.out.println();
    }

    /**
     * Bước 4: thống kê lương - chỗ BigDecimal bắt đầu phiền, và đó là chủ ý.
     */
    private static void baoCaoLuong(KetQuaImport ketQua) {
        System.out.println("=== 4. Thống kê lương ===");

        List<NhanVien> nhanVien = ketQua.hopLe();
        // Chặn rỗng TRƯỚC khi chia. File toàn dòng hỏng là chuyện có thật, và chia cho 0
        // trên BigDecimal ném ArithmeticException chứ không ra Infinity như double.
        if (nhanVien.isEmpty()) {
            System.out.println("Không có dòng hợp lệ nào để thống kê.");
            System.out.println();
            return;
        }

        BigDecimal tongQuyLuong = nhanVien.stream()
                .map(NhanVien::luong)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.printf(DONG, "Tổng quỹ lương", tongQuyLuong);

        // KHÔNG dùng averagingDouble: nó ép BigDecimal về double, vứt bỏ đúng cái lý do ta
        // chọn BigDecimal cho tiền. Tự chia, và BẮT BUỘC truyền scale + RoundingMode - thiếu
        // chúng thì divide ném ArithmeticException khi kết quả là thập phân vô hạn tuần hoàn.
        BigDecimal luongTrungBinh = tongQuyLuong
                .divide(BigDecimal.valueOf(nhanVien.size()), 0, RoundingMode.HALF_UP);
        System.out.printf(DONG, "Lương trung bình", luongTrungBinh);

        // max/min trả Optional vì stream có thể rỗng - đúng chỗ dùng Optional (bài 28).
        nhanVien.stream()
                .max(Comparator.comparing(NhanVien::luong))
                .ifPresent(nv -> System.out.printf(DONG, "Lương cao nhất",
                        nv.hoTen() + " - " + nv.luong()));

        nhanVien.stream()
                .min(Comparator.comparing(NhanVien::luong))
                .ifPresent(nv -> System.out.printf(DONG, "Lương thấp nhất",
                        nv.hoTen() + " - " + nv.luong()));

        // Thâm niên: mốc cố định thay vì LocalDate.now() để output không đổi theo ngày chạy.
        // summarizingLong gom min/max/avg/sum trong MỘT lần duyệt (bài 27).
        LongSummaryStatistics thamNien = nhanVien.stream()
                .mapToLong(nv -> ChronoUnit.YEARS.between(nv.ngayVaoLam(), MOC_TINH_THAM_NIEN))
                .summaryStatistics();
        System.out.printf(DONG, "Thâm niên (năm) min/trung bình/max",
                "%d / %.1f / %d".formatted(thamNien.getMin(), thamNien.getAverage(), thamNien.getMax()));

        System.out.println();
    }

    /**
     * Bước 5: nhánh lỗi cấu trúc - khác hẳn lỗi dữ liệu.
     */
    private static void demoSaiHeader() {
        System.out.println("=== 5. Lỗi cấu trúc file ===");

        try {
            CsvImporter.doc(FILE_SAI_HEADER);
            System.out.println("Không bao giờ tới đây - header sai phải ném IOException.");
        } catch (IOException e) {
            System.out.printf(DONG, e.getClass().getSimpleName(), e.getMessage());
        }

        // Vì sao sai header thì NÉM, còn sai email thì gom vào LoiDong?
        //
        // Sai email là lỗi của MỘT DÒNG: 999 dòng còn lại vẫn nhập được, và người dùng cần
        // biết hết các dòng hỏng trong một lần. Gom lại rồi đi tiếp là phục vụ họ.
        //
        // Sai header là lỗi của CẢ FILE: không biết cột nào là cột nào thì mọi dòng đều vô
        // nghĩa. Cố parse tiếp chỉ sinh ra một danh sách lỗi dài vô ích, che mất nguyên nhân
        // thật. Dừng ngay và nói rõ "bạn upload nhầm file" mới là câu trả lời hữu ích.
        //
        // Quy tắc rút ra: tiếp tục được thì gom lỗi, tiếp tục vô nghĩa thì dừng sớm.
        System.out.println();
    }

    // ============================================================
    // TỰ KIỂM - chạy xong đối chiếu với bảng này
    // ============================================================
    //
    //   hợp lệ      : 3   (dòng 2, 3, 13 - dòng 10 hợp lệ nhưng bị loại vì trùng email)
    //   số lỗi      : 8
    //   số dòng lỗi : 8
    //
    //   dòng 4   hoTen        không được để trống
    //   dòng 5   email        sai định dạng email
    //   dòng 6   luong        không phải số
    //   dòng 7   luong        không được âm
    //   dòng 8   ngayVaoLam   sai định dạng, cần yyyy-MM-dd
    //   dòng 9   (cả dòng)    cần 6 cột nhưng có 5
    //   dòng 10  email        trùng với email ở dòng 2
    //   dòng 12  phongBan     không hợp lệ, phải là một trong: ...
    //
    //   IT = 2 người (An, Khanh), KE_TOAN = 1 người (Binh)
    //   tổng quỹ lương = 70000000
}
