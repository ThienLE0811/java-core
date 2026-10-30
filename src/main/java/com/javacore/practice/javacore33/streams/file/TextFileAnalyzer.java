package com.javacore.practice.javacore33.streams.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bài 29 - Đọc file text bằng {@link Files#lines} trong try-with-resources.
 *
 * <p>Mọi phương thức ở đây đều theo cùng một khuôn: mở stream trong try-with-resources,
 * xử lý, và trả về KẾT QUẢ ĐÃ VẬT CHẤT HOÁ (List/Map/số) chứ không trả về Stream.
 * Lý do xem phần bẫy trong {@link FileStreamDemo}.
 */
public final class TextFileAnalyzer {

    // Tách từ: mọi ký tự KHÔNG phải chữ cái (\p{L}) hoặc chữ số (\p{Nd}) đều là dấu phân cách.
    // Giống bài 18 - hoạt động đúng với tiếng Việt có dấu.
    private static final Pattern DAU_PHAN_CACH = Pattern.compile("[^\\p{L}\\p{Nd}]+");

    private TextFileAnalyzer() {
        // Utility class, không cho khởi tạo
    }

    /**
     * Câu 1a: đếm dòng. Mỗi phép đếm là một lần mở file vì Stream chỉ dùng được một lần.
     */
    public static long demDong(Path file) throws IOException {
        // Files.lines trả về Stream LƯỜI và đang GIỮ file handle đang mở.
        // Không đóng = rò rỉ file descriptor. Vì vậy luôn bọc try-with-resources.
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.count();
        }
    }

    /**
     * Câu 1b: đếm từ - flatMap biến mỗi dòng thành nhiều từ rồi gộp lại một luồng phẳng.
     */
    public static long demTu(Path file) throws IOException {
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.flatMap(TextFileAnalyzer::tachTu).count();
        }
    }

    /**
     * Câu 1c: đếm ký tự. Lưu ý: {@code Files.lines} đã cắt bỏ ký tự xuống dòng,
     * nên con số này KHÁC {@link Files#size} (size tính cả \n, \r và byte đa byte của UTF-8).
     */
    public static long demKyTu(Path file) throws IOException {
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.mapToLong(String::length).sum();
        }
    }

    /**
     * Câu 2: đếm cả ba trong MỘT lần đọc file - map mỗi dòng thành một {@link ThongKe} rồi reduce.
     *
     * <p>Ba hàm ở trên mở file ba lần. Với file lớn hãy dùng bản này.
     */
    public static ThongKe thongKe(Path file) throws IOException {
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.map(TextFileAnalyzer::thongKeMotDong)
                    .reduce(ThongKe.RONG, ThongKe::cong);
        }
    }

    /**
     * Câu 3: tìm {@code n} từ dài nhất, không trùng lặp.
     */
    public static List<String> tuDaiNhat(Path file, int n) throws IOException {
        if (n <= 0) {
            return List.of();
        }
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.flatMap(TextFileAnalyzer::tachTu)
                    .distinct()
                    // Dài nhất lên đầu; dài bằng nhau thì xếp A-Z để kết quả luôn xác định.
                    .sorted(Comparator.comparingInt(String::length).reversed()
                            .thenComparing(Comparator.naturalOrder()))
                    .limit(n)
                    .toList();
        }
    }

    /**
     * Câu 4: lọc các dòng chứa từ khoá, kèm số thứ tự dòng (bắt đầu từ 1).
     *
     * <p>Số dòng phải gắn TRƯỚC filter, nếu không sẽ mất thông tin vị trí gốc.
     */
    public static List<DongKhop> locDongChua(Path file, String tuKhoa) throws IOException {
        if (tuKhoa == null || tuKhoa.isBlank()) {
            return List.of();
        }
        String khoa = tuKhoa.toLowerCase(Locale.ROOT);

        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            // Stream không có chỉ số sẵn. Mảng 1 phần tử làm bộ đếm: chỉ an toàn vì đây là
            // stream TUẦN TỰ và có thứ tự. Nếu .parallel() thì con số sẽ sai.
            long[] soThuTu = {0};
            return dong.map(noiDung -> new DongKhop(++soThuTu[0], noiDung))
                    .filter(d -> d.noiDung().toLowerCase(Locale.ROOT).contains(khoa))
                    .toList();
        }
    }

    /**
     * Câu 5: đếm tần suất từ - bài 18 viết lại bằng Stream.
     *
     * <p>Bài 18: {@code for (String w : words) map.merge(w, 1, Integer::sum);}
     * <br>Bài 29: {@code groupingBy(identity(), counting())} - cùng kết quả, không có biến tạm.
     */
    public static Map<String, Long> tanSuatTu(Path file) throws IOException {
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.flatMap(TextFileAnalyzer::tachTu)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }
    }

    /**
     * Câu 5b: top {@code n} từ xuất hiện nhiều nhất, lấy từ map tần suất.
     */
    public static List<Map.Entry<String, Long>> topTu(Map<String, Long> tanSuat, int n) {
        if (tanSuat == null || tanSuat.isEmpty() || n <= 0) {
            return List.of();
        }
        return tanSuat.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .toList();
    }

    /**
     * Câu 6: lấy {@code n} dòng đầu - minh hoạ tính LƯỜI, file 1 triệu dòng cũng chỉ đọc n dòng.
     */
    public static List<String> nDongDau(Path file, int n) throws IOException {
        if (n <= 0) {
            return List.of();
        }
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            return dong.limit(n).toList();
        }
    }

    // Tách một dòng thành luồng các từ đã chuẩn hoá chữ thường, bỏ phần tử rỗng.
    // Lưu ý: split("...") trên chuỗi bắt đầu bằng dấu phân cách sẽ sinh ra phần tử đầu rỗng.
    private static Stream<String> tachTu(String dong) {
        return DAU_PHAN_CACH.splitAsStream(dong)
                .filter(tu -> !tu.isBlank())
                .map(tu -> tu.toLowerCase(Locale.ROOT));
    }

    private static ThongKe thongKeMotDong(String dong) {
        long soTu = tachTu(dong).count();
        long soKyTuKhongTrang = dong.chars().filter(c -> !Character.isWhitespace(c)).count();
        return new ThongKe(1, soTu, dong.length(), soKyTuKhongTrang);
    }

    /**
     * Kết quả đếm gộp. Record bất biến nên {@link #cong} tạo đối tượng mới thay vì sửa tại chỗ.
     */
    public record ThongKe(long soDong, long soTu, long soKyTu, long soKyTuKhongTrang) {

        public static final ThongKe RONG = new ThongKe(0, 0, 0, 0);

        public static ThongKe cong(ThongKe a, ThongKe b) {
            return new ThongKe(
                    a.soDong + b.soDong,
                    a.soTu + b.soTu,
                    a.soKyTu + b.soKyTu,
                    a.soKyTuKhongTrang + b.soKyTuKhongTrang);
        }
    }

    /**
     * Một dòng khớp từ khoá, kèm vị trí gốc trong file.
     */
    public record DongKhop(long soThuTu, String noiDung) {
        @Override
        public String toString() {
            return "dòng %d: %s".formatted(soThuTu, noiDung);
        }
    }
}
