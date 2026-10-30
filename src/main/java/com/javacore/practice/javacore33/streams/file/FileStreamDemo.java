package com.javacore.practice.javacore33.streams.file;

import com.javacore.practice.javacore33.streams.file.TextFileAnalyzer.DongKhop;
import com.javacore.practice.javacore33.streams.file.TextFileAnalyzer.ThongKe;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Bài 29 - Stream đọc file: Files.lines() trong try-with-resources.
 *
 * <p>Nửa đầu là 6 câu bài tập, nửa sau là 4 cái bẫy hay gặp khi đọc file bằng Stream:
 * quên đóng stream, dùng lại stream đã tiêu thụ, sai charset, và lines() vs readAllLines().
 */
public class FileStreamDemo {

    private static final String DONG = "%-40s: %s%n";

    private static final Path THU_MUC = Path.of("target", "bai29");
    private static final Path FILE_VAN_BAN = THU_MUC.resolve("vanban.txt");

    private static final String NOI_DUNG_MAU = """
            Java Stream API ra doi tu Java 8 nam 2014.
            Stream khong phai la cau truc du lieu, no chi mo ta phep bien doi.
            Files.lines tra ve mot Stream luoi, doc den dau xu ly den do.
            
            Stream co ba phan: nguon, cac phep trung gian, va mot phep ket thuc.
            Phep trung gian nhu filter, map, flatMap deu luoi.
            Phep ket thuc nhu count, collect, forEach moi thuc su chay pipeline.
            
            Doc file lon bang readAllLines se nap toan bo noi dung vao bo nho.
            Doc file lon bang Files.lines thi chi giu mot dong tai mot thoi diem.
            Day la ly do Files.lines phu hop voi file log hang trieu dong.
            
            Try-with-resources dam bao stream duoc dong ca khi co ngoai le.
            Quen dong stream se ro ri file descriptor, tren Windows con khoa luon file.
            Stream chi dung duoc mot lan, dung lai se nem IllegalStateException.
            """;

    public static void main(String[] args) throws IOException {
        chuanBiFile();

        demCoBan();
        thongKeMotLanDoc();
        timTuDaiNhat();
        locTheoTuKhoa();
        demTanSuat();

        bayQuenDongStream();
        bayDungLaiStream();
        baySaiCharset();
        bayLinesVsReadAllLines();
    }

    /**
     * Tạo file mẫu trong target/ để demo chạy được ở bất kỳ máy nào mà không cần chuẩn bị gì.
     */
    private static void chuanBiFile() throws IOException {
        Files.createDirectories(THU_MUC);
        // writeString mặc định UTF-8, mặc định CREATE + TRUNCATE_EXISTING.
        Files.writeString(FILE_VAN_BAN, NOI_DUNG_MAU, StandardCharsets.UTF_8);

        System.out.println("=== 0. File mẫu ===");
        System.out.printf(DONG, "Đường dẫn", FILE_VAN_BAN.toAbsolutePath());
        System.out.printf(DONG, "Kích thước (byte)", Files.size(FILE_VAN_BAN));
        System.out.println();
    }

    /**
     * Câu 1: đếm dòng, từ, ký tự - mỗi phép đếm mở file một lần.
     */
    private static void demCoBan() throws IOException {
        System.out.println("=== 1. Đếm dòng / từ / ký tự ===");

        System.out.printf(DONG, "Số dòng", TextFileAnalyzer.demDong(FILE_VAN_BAN));
        System.out.printf(DONG, "Số từ", TextFileAnalyzer.demTu(FILE_VAN_BAN));
        System.out.printf(DONG, "Số ký tự (không tính xuống dòng)", TextFileAnalyzer.demKyTu(FILE_VAN_BAN));

        // Files.size đếm BYTE trên đĩa: có cả \r\n và ký tự UTF-8 nhiều byte.
        // Đừng bao giờ dùng Files.size() để kết luận "file có bao nhiêu ký tự".
        System.out.printf(DONG, "Files.size (byte, để so sánh)", Files.size(FILE_VAN_BAN));
        System.out.println();
    }

    /**
     * Câu 2: gộp ba phép đếm vào một lần đọc bằng map + reduce.
     */
    private static void thongKeMotLanDoc() throws IOException {
        System.out.println("=== 2. Thống kê trong một lần đọc ===");

        ThongKe tk = TextFileAnalyzer.thongKe(FILE_VAN_BAN);
        System.out.printf(DONG, "ThongKe", tk);
        System.out.printf(DONG, "Số ký tự không phải khoảng trắng", tk.soKyTuKhongTrang());
        // Cùng kết quả với câu 1 nhưng chỉ mở file 1 lần thay vì 3 lần.
        System.out.println();
    }

    /**
     * Câu 3: 10 từ dài nhất.
     */
    private static void timTuDaiNhat() throws IOException {
        System.out.println("=== 3. 10 từ dài nhất ===");

        List<String> top = TextFileAnalyzer.tuDaiNhat(FILE_VAN_BAN, 10);
        top.forEach(tu -> System.out.printf("  %2d ký tự  %s%n", tu.length(), tu));
        // distinct() đặt TRƯỚC sorted() cho rẻ: sắp xếp trên tập đã bớt phần tử.
        System.out.println();
    }

    /**
     * Câu 4: lọc các dòng chứa từ khoá, kèm số dòng.
     */
    private static void locTheoTuKhoa() throws IOException {
        System.out.println("=== 4. Lọc dòng chứa từ khoá ===");

        for (String tuKhoa : List.of("stream", "Files.lines", "khong-co-trong-file")) {
            List<DongKhop> khop = TextFileAnalyzer.locDongChua(FILE_VAN_BAN, tuKhoa);
            System.out.printf(DONG, "Từ khoá \"" + tuKhoa + "\"", khop.size() + " dòng khớp");
            khop.forEach(d -> System.out.println("    " + d));
        }
        System.out.println();
    }

    /**
     * Câu 5: đếm tần suất - bài 18 viết lại bằng Stream.
     */
    private static void demTanSuat() throws IOException {
        System.out.println("=== 5. Tần suất từ (bài 18 viết lại bằng Stream) ===");

        Map<String, Long> tanSuat = TextFileAnalyzer.tanSuatTu(FILE_VAN_BAN);
        System.out.printf(DONG, "Số từ khác nhau", tanSuat.size());

        System.out.println("  Top 10:");
        TextFileAnalyzer.topTu(tanSuat, 10)
                .forEach(e -> System.out.printf("    %-16s %d%n", e.getKey(), e.getValue()));

        long chiMotLan = tanSuat.values().stream().filter(v -> v == 1).count();
        System.out.printf(DONG, "Số từ chỉ xuất hiện 1 lần", chiMotLan);
        System.out.println();
    }

    // ============================================================
    // BỐN CÁI BẪY KHI ĐỌC FILE BẰNG STREAM
    // ============================================================

    /**
     * BẪY 1: quên đóng stream. Files.lines KHÔNG tự đóng sau phép kết thúc.
     */
    private static void bayQuenDongStream() throws IOException {
        System.out.println("=== 6. BẪY: quên đóng stream ===");

        // Dùng file tạm riêng để không đụng vào file chính.
        Path tam = THU_MUC.resolve("tam-khong-dong.txt");
        Files.copy(FILE_VAN_BAN, tam, StandardCopyOption.REPLACE_EXISTING);

        // SAI: mở stream ngoài try-with-resources. count() là phép kết thúc, nhưng phép kết
        // thúc KHÔNG đóng stream - file handle vẫn còn mở sau dòng này.
        Stream<String> quenDong = Files.lines(tam, StandardCharsets.UTF_8);
        System.out.printf(DONG, "Đã count xong", quenDong.count() + " dòng");

        // Rò rỉ fd là hậu quả CHẮC CHẮN. Còn xoá được hay không thì tuỳ hệ điều hành và
        // cách mở file, nên đừng lấy "xoá được" làm bằng chứng là đã đóng stream.
        try {
            Files.delete(tam);
            System.out.printf(DONG, "Xoá khi chưa đóng", "xoá được - nhưng file handle VẪN đang mở");
        } catch (IOException e) {
            System.out.printf(DONG, "Xoá khi chưa đóng", e.getClass().getSimpleName() + " - file đang bị khoá vì chưa đóng");
        }

        quenDong.close();
        if (Files.exists(tam)) {
            Files.delete(tam);
            System.out.printf(DONG, "Xoá sau khi close()", "thành công");
        }

        // ĐÚNG: try-with-resources đóng stream kể cả khi giữa chừng ném ngoại lệ.
        Path tam2 = THU_MUC.resolve("tam-co-dong.txt");
        Files.copy(FILE_VAN_BAN, tam2, StandardCopyOption.REPLACE_EXISTING);
        try (Stream<String> dong = Files.lines(tam2, StandardCharsets.UTF_8)) {
            System.out.printf(DONG, "try-with-resources", dong.count() + " dòng");
        }
        Files.delete(tam2);
        System.out.printf(DONG, "Xoá sau try-with-resources", "thành công");

        // Ghi chú: Stream thường (list.stream()) không cần đóng. CHỈ các stream gắn với tài
        // nguyên hệ thống mới cần: Files.lines, Files.list, Files.walk, Files.find.
        System.out.println();
    }

    /**
     * BẪY 2: dùng lại một Stream đã tiêu thụ.
     */
    private static void bayDungLaiStream() throws IOException {
        System.out.println("=== 7. BẪY: dùng lại Stream đã tiêu thụ ===");

        try (Stream<String> dong = Files.lines(FILE_VAN_BAN, StandardCharsets.UTF_8)) {
            System.out.printf(DONG, "Lần đếm thứ nhất", dong.count());
            // SAI: Stream là luồng dùng MỘT LẦN, không phải Collection đọc được nhiều lần.
            System.out.println(dong.count());
        } catch (IllegalStateException e) {
            System.out.printf(DONG, "Lần đếm thứ hai", e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // ĐÚNG - cách 1: cần nhiều phép đếm thì mở lại file mỗi lần (xem câu 1).
        // ĐÚNG - cách 2: đọc một lần ra List rồi dùng lại thoải mái (chỉ hợp với file nhỏ).
        List<String> cacDong;
        try (Stream<String> dong = Files.lines(FILE_VAN_BAN, StandardCharsets.UTF_8)) {
            cacDong = dong.toList();
        }
        System.out.printf(DONG, "List - đếm lần 1", cacDong.size());
        System.out.printf(DONG, "List - đếm lần 2", cacDong.stream().filter(d -> !d.isBlank()).count());

        // ĐÚNG - cách 3: gộp nhiều phép tính vào một lần duyệt (xem câu 2, map + reduce).
        System.out.println();
    }

    /**
     * BẪY 3: sai charset - lỗi nổ LÚC DUYỆT chứ không phải lúc gọi Files.lines.
     */
    private static void baySaiCharset() throws IOException {
        System.out.println("=== 8. BẪY: sai charset ===");

        // Tạo file có byte 0xFF - đây là byte KHÔNG hợp lệ trong UTF-8.
        Path fileLoi = THU_MUC.resolve("khong-phai-utf8.txt");
        byte[] duLieu = {'d', 'o', 'n', 'g', ' ', 'o', 'k', '\n', (byte) 0xFF, (byte) 0xFE, '\n'};
        Files.write(fileLoi, duLieu);

        // Dòng này KHÔNG nổ: Files.lines chỉ mở file và dựng pipeline, chưa đọc nội dung.
        Stream<String> dong = Files.lines(fileLoi, StandardCharsets.UTF_8);
        System.out.printf(DONG, "Files.lines(UTF_8)", "mở thành công, chưa đọc byte nào");

        try (dong) {
            // Nổ ở ĐÂY - phép kết thúc mới thực sự giải mã byte.
            System.out.println(dong.count());
        } catch (UncheckedIOException e) {
            // IOException là checked, nhưng lambda trong Stream không được ném checked exception,
            // nên JDK bọc nó lại thành UncheckedIOException (runtime).
            System.out.printf(DONG, "Lúc count()",
                    e.getClass().getSimpleName() + " bọc " + e.getCause().getClass().getSimpleName());
        }

        // Hệ quả thực tế: try/catch IOException quanh Files.lines KHÔNG bắt được lỗi giải mã.
        // Phải bắt UncheckedIOException, hoặc đọc đúng charset ngay từ đầu.
        try (Stream<String> latin = Files.lines(fileLoi, StandardCharsets.ISO_8859_1)) {
            // ISO-8859-1 ánh xạ mọi byte sang một ký tự nên không bao giờ lỗi giải mã,
            // nhưng tiếng Việt sẽ ra chữ rác. "Không nổ" không có nghĩa là "đọc đúng".
            System.out.printf(DONG, "Đọc lại bằng ISO_8859_1", latin.count() + " dòng, nội dung là chữ rác");
        }

        // Quy tắc: LUÔN truyền charset tường minh. Files.lines(path) một tham số mặc định
        // UTF-8, nhưng viết rõ StandardCharsets.UTF_8 để người đọc không phải đoán.
        Files.delete(fileLoi);
        System.out.println();
    }

    /**
     * BẪY 4: lines() lười vs readAllLines() nạp hết - chọn sai là ăn OutOfMemoryError.
     */
    private static void bayLinesVsReadAllLines() throws IOException {
        System.out.println("=== 9. lines() lười vs readAllLines() ===");

        // readAllLines: nạp TOÀN BỘ file vào List trong bộ nhớ, rồi mới xử lý.
        // File log 2GB = cần hơn 2GB heap. Tiện cho file cấu hình nhỏ, chết với file lớn.
        List<String> tatCa = Files.readAllLines(FILE_VAN_BAN, StandardCharsets.UTF_8);
        System.out.printf(DONG, "readAllLines", tatCa.size() + " dòng đã nằm hết trong RAM");

        // Files.lines: chỉ giữ một dòng tại một thời điểm. Có limit thì dừng đọc luôn.
        long[] soDongDaCham = {0};
        List<String> baDong;
        try (Stream<String> dong = Files.lines(FILE_VAN_BAN, StandardCharsets.UTF_8)) {
            baDong = dong.peek(d -> soDongDaCham[0]++)
                    .limit(3)
                    .toList();
        }
        System.out.printf(DONG, "limit(3) - số dòng thực sự đọc", soDongDaCham[0] + " (không phải " + tatCa.size() + ")");
        baDong.forEach(d -> System.out.println("    " + d));

        // Cùng kết quả, không cần nạp cả file:
        System.out.printf(DONG, "nDongDau(3)", TextFileAnalyzer.nDongDau(FILE_VAN_BAN, 3).size() + " dòng");

        // Quy tắc chọn: file nhỏ và cần duyệt nhiều lần -> readAllLines.
        //               file lớn, hoặc chỉ duyệt một lần -> Files.lines + try-with-resources.
        System.out.println();
    }
}
