package com.javacore.practice.javacore33.streams.csv;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bài 30 - Đọc file CSV nhân viên, validate từng dòng và gom toàn bộ lỗi.
 *
 * <p>Nguyên tắc xuyên suốt: KHÔNG dừng ở lỗi đầu tiên. File 1000 dòng sai 5 dòng thì
 * nhập 995 dòng và trả về 5 lỗi kèm số dòng, chứ không bắt người dùng sửa rồi upload
 * lại 5 lần.
 *
 * <p>Hai loại lỗi được xử lý khác hẳn nhau:
 * <ul>
 *   <li>Lỗi CẤU TRÚC (không đọc được file, sai header) -> ném IOException, dừng ngay.
 *       Sai header thì mọi dòng đều vô nghĩa, parse tiếp chỉ sinh rác.</li>
 *   <li>Lỗi DỮ LIỆU (sai email, lương âm...) -> gom vào {@link LoiDong}, đi tiếp.</li>
 * </ul>
 *
 * <p>Giới hạn đã biết: dòng được tách bằng {@code split(",")}, nên KHÔNG xử lý được dấu
 * phẩy nằm trong ô bọc nháy kép hay ô có xuống dòng. Đủ cho CSV "hiền" do hệ thống nội
 * bộ xuất ra. Gặp CSV thật từ Excel hay đối tác thì dùng OpenCSV / Apache Commons CSV,
 * đừng tự viết parser.
 */
public final class CsvImporter {

    // Byte Order Mark. Excel xuất CSV UTF-8 hay chèn ký tự này vào đầu file, khiến cột đầu
    // của header thành BOM + "id" và mọi phép so sánh header đều fail một cách khó hiểu.
    private static final char BOM = (char) 0xFEFF;

    private static final List<String> HEADER_MONG_DOI =
            List.of("id", "hoTen", "email", "phongBan", "luong", "ngayVaoLam");
    private static final int SO_COT = HEADER_MONG_DOI.size();

    // yyyy-MM-dd. Luôn khai báo tường minh thay vì tin vào định dạng mặc định của máy.
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ISO_LOCAL_DATE;

    // Kiểm tra email "đủ dùng". Validate email đúng chuẩn RFC 5322 bằng regex là cái bẫy:
    // regex đó dài hàng nghìn ký tự và vẫn sai. Cách duy nhất biết email có thật là gửi
    // thư xác thực. Ở đây chỉ chặn những giá trị rõ ràng không phải email.
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9_.+-]+@[A-Za-z0-9-]+([.][A-Za-z0-9-]+)+$");

    // Hai collector tách sẵn thành hằng cho dễ đọc, dùng trong teeing ở phương thức doc().
    private static final Collector<KetQuaDong, ?, List<KetQuaDong.HopLe>> GOM_HOP_LE =
            Collectors.flatMapping(
                    kq -> kq instanceof KetQuaDong.HopLe h ? Stream.of(h) : Stream.empty(),
                    Collectors.toList());

    private static final Collector<KetQuaDong, ?, List<LoiDong>> GOM_LOI =
            Collectors.flatMapping(
                    kq -> kq instanceof KetQuaDong.Loi l ? l.cacLoi().stream() : Stream.empty(),
                    Collectors.toList());

    private CsvImporter() {
        // Utility class, không cho khởi tạo
    }

    /**
     * Đọc và validate toàn bộ file.
     *
     * @throws IOException khi không đọc được file hoặc header sai cấu trúc
     */
    public static KetQuaImport doc(Path file) throws IOException {
        // Cổng chặn: sai header thì dừng luôn, không parse dòng nào.
        kiemTraHeader(file);

        List<KetQuaDong> ketQuaTungDong;
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            // Bộ đếm phải gắn TRƯỚC mọi phép lọc, nếu không số dòng báo cho người dùng sẽ
            // lệch so với file họ đang mở. Mảng 1 phần tử chỉ an toàn vì đây là stream tuần
            // tự - thêm .parallel() là số dòng sai ngay.
            long[] soDong = {0};
            ketQuaTungDong = dong.map(noiDung -> new DongTho(++soDong[0], noiDung))
                    .filter(d -> d.so() > 1)                 // bỏ header
                    .filter(d -> !d.noiDung().isBlank())     // bỏ dòng trống, không tính là lỗi
                    .map(d -> parseDong(d.so(), d.noiDung()))
                    .toList();
        }

        // teeing (Java 12): chạy HAI collector trên CÙNG một lần duyệt rồi gộp kết quả lại.
        // Viết filter/map hai lượt cũng ra kết quả y hệt và dễ đọc hơn một chút; chọn teeing
        // ở đây vì nó nói rõ ý "tách làm hai nhóm" gọn trong một biểu thức.
        TachNhom tach = ketQuaTungDong.stream()
                .collect(Collectors.teeing(GOM_HOP_LE, GOM_LOI, TachNhom::new));

        // Validate tầng hai: trùng email. Việc này KHÔNG làm được trong parseDong vì nó cần
        // biết các dòng khác. Validate theo-dòng và validate toàn-file là hai tầng khác nhau,
        // lẫn lộn chúng là lỗi thiết kế hay gặp.
        List<LoiDong> loiTrung = timEmailTrung(tach.hopLe());

        List<LoiDong> tatCaLoi = new ArrayList<>(tach.loi());
        tatCaLoi.addAll(loiTrung);
        // Sắp theo số dòng để người dùng dò từ trên xuống đúng thứ tự trong file.
        tatCaLoi.sort(Comparator.comparingLong(LoiDong::soDong));

        // Dòng bị trùng email phải bị LOẠI khỏi danh sách hợp lệ, nếu không thì vừa báo lỗi
        // vừa nhập vào DB - tự mâu thuẫn.
        Set<Long> dongBiLoai = loiTrung.stream()
                .map(LoiDong::soDong)
                .collect(Collectors.toSet());

        List<NhanVien> nhanVien = tach.hopLe().stream()
                .filter(h -> !dongBiLoai.contains(h.soDong()))
                .map(KetQuaDong.HopLe::nhanVien)
                .toList();

        return new KetQuaImport(nhanVien, tatCaLoi);
    }

    /**
     * Parse một dòng dữ liệu. Để package-private để unit test gọi thẳng vào được.
     *
     * <p>Gom HẾT lỗi của dòng rồi mới trả về: thiếu tên và sai email thì báo cả hai.
     */
    static KetQuaDong parseDong(long soDong, String dong) {
        // Tham số -1 BẮT BUỘC: "a,b,," mà split(",") chỉ ra 2 phần tử vì các ô rỗng ở cuối bị
        // nuốt mất. Thiếu tham số này thì dòng thiếu cột cuối bị báo sai lý do.
        String[] cot = dong.split(",", -1);

        if (cot.length != SO_COT) {
            return new KetQuaDong.Loi(soDong, List.of(new LoiDong(soDong, "(cả dòng)", dong,
                    "cần %d cột nhưng có %d".formatted(SO_COT, cot.length))));
        }

        List<LoiDong> loi = new ArrayList<>();
        // Các hàm dưới trả về null khi hỏng VÀ đã ghi lỗi vào list. Dùng null ở đây chấp nhận
        // được vì phạm vi gói gọn trong method này và luôn có list loi đi kèm làm chứng;
        // trả Optional rồi .orElse(null) sáu lần chỉ dài hơn chứ không an toàn hơn.
        Integer id = docId(soDong, cot[0], loi);
        String hoTen = docHoTen(soDong, cot[1], loi);
        String email = docEmail(soDong, cot[2], loi);
        PhongBan phongBan = docPhongBan(soDong, cot[3], loi);
        BigDecimal luong = docLuong(soDong, cot[4], loi);
        LocalDate ngayVaoLam = docNgay(soDong, cot[5], loi);

        if (!loi.isEmpty()) {
            return new KetQuaDong.Loi(soDong, loi);
        }
        return new KetQuaDong.HopLe(soDong,
                new NhanVien(id, hoTen, email, phongBan, luong, ngayVaoLam));
    }

    private static void kiemTraHeader(Path file) throws IOException {
        try (Stream<String> dong = Files.lines(file, StandardCharsets.UTF_8)) {
            // findFirst là phép kết thúc ngắn mạch: file một triệu dòng cũng chỉ đọc dòng đầu.
            String header = dong.findFirst()
                    .orElseThrow(() -> new IOException("File rỗng: " + file));

            List<String> cot = Arrays.stream(boBom(header).split(",", -1))
                    .map(String::trim)
                    .toList();

            if (!cot.equals(HEADER_MONG_DOI)) {
                throw new IOException("Header không đúng. Mong đợi %s nhưng nhận %s"
                        .formatted(HEADER_MONG_DOI, cot));
            }
        }
    }

    private static String boBom(String dong) {
        return !dong.isEmpty() && dong.charAt(0) == BOM ? dong.substring(1) : dong;
    }

    private static Integer docId(long soDong, String goc, List<LoiDong> loi) {
        String gia = goc.trim();
        if (gia.isEmpty()) {
            loi.add(new LoiDong(soDong, "id", goc, "không được để trống"));
            return null;
        }
        try {
            int id = Integer.parseInt(gia);
            if (id <= 0) {
                loi.add(new LoiDong(soDong, "id", goc, "phải là số dương"));
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            // NumberFormatException là RuntimeException nên compiler KHÔNG nhắc phải bắt.
            // Quên bắt là cả lần import đổ vì một ô gõ sai.
            loi.add(new LoiDong(soDong, "id", goc, "không phải số nguyên"));
            return null;
        }
    }

    private static String docHoTen(long soDong, String goc, List<LoiDong> loi) {
        String gia = goc.trim();
        if (gia.isEmpty()) {
            loi.add(new LoiDong(soDong, "hoTen", goc, "không được để trống"));
            return null;
        }
        return gia;
    }

    private static String docEmail(long soDong, String goc, List<LoiDong> loi) {
        String gia = goc.trim();
        if (gia.isEmpty()) {
            loi.add(new LoiDong(soDong, "email", goc, "không được để trống"));
            return null;
        }
        if (!EMAIL.matcher(gia).matches()) {
            loi.add(new LoiDong(soDong, "email", goc, "sai định dạng email"));
            return null;
        }
        // Chuẩn hoá chữ thường để so trùng về sau không phụ thuộc hoa thường.
        return gia.toLowerCase(Locale.ROOT);
    }

    private static PhongBan docPhongBan(long soDong, String goc, List<LoiDong> loi) {
        // orElse(null) rồi kiểm tra, chứ không orElseGet(() -> { loi.add(...); return null; }):
        // nhét side-effect vào trong Supplier là lạm dụng Optional, đọc rất khó.
        PhongBan phongBan = PhongBan.tuChuoi(goc).orElse(null);
        if (phongBan == null) {
            loi.add(new LoiDong(soDong, "phongBan", goc,
                    "không hợp lệ, phải là một trong: " + PhongBan.cacMaHopLe()));
        }
        return phongBan;
    }

    private static BigDecimal docLuong(long soDong, String goc, List<LoiDong> loi) {
        String gia = goc.trim();
        if (gia.isEmpty()) {
            loi.add(new LoiDong(soDong, "luong", goc, "không được để trống"));
            return null;
        }
        try {
            // Tạo BigDecimal từ String, không bao giờ từ double.
            BigDecimal luong = new BigDecimal(gia);
            // compareTo chứ không equals: equals so cả scale nên coi 1000 và 1000.00 là KHÁC
            // nhau. Đây là bẫy BigDecimal kinh điển.
            if (luong.compareTo(BigDecimal.ZERO) < 0) {
                loi.add(new LoiDong(soDong, "luong", goc, "không được âm"));
                return null;
            }
            return luong;
        } catch (NumberFormatException e) {
            loi.add(new LoiDong(soDong, "luong", goc, "không phải số"));
            return null;
        }
    }

    private static LocalDate docNgay(long soDong, String goc, List<LoiDong> loi) {
        String gia = goc.trim();
        if (gia.isEmpty()) {
            loi.add(new LoiDong(soDong, "ngayVaoLam", goc, "không được để trống"));
            return null;
        }
        try {
            return LocalDate.parse(gia, DINH_DANG_NGAY);
        } catch (DateTimeParseException e) {
            loi.add(new LoiDong(soDong, "ngayVaoLam", goc, "sai định dạng, cần yyyy-MM-dd"));
            return null;
        }
    }

    /**
     * Tìm email trùng nhau trong cùng một file. Dòng xuất hiện đầu tiên được giữ,
     * các dòng sau bị báo lỗi và loại ra.
     */
    private static List<LoiDong> timEmailTrung(List<KetQuaDong.HopLe> hopLe) {
        Map<String, List<KetQuaDong.HopLe>> theoEmail = hopLe.stream()
                .collect(Collectors.groupingBy(h -> h.nhanVien().email()));

        return theoEmail.values().stream()
                .filter(nhom -> nhom.size() > 1)
                .flatMap(nhom -> {
                    // groupingBy giữ nguyên thứ tự gặp trong mỗi nhóm nên phần tử đầu chính
                    // là dòng xuất hiện sớm nhất trong file.
                    long dongDauTien = nhom.getFirst().soDong();
                    return nhom.stream()
                            .skip(1)
                            .map(h -> new LoiDong(h.soDong(), "email", h.nhanVien().email(),
                                    "trùng với email ở dòng " + dongDauTien));
                })
                .toList();
    }

    /**
     * Một dòng thô kèm số dòng gốc, chỉ dùng nội bộ trong lúc duyệt stream.
     */
    private record DongTho(long so, String noiDung) {
    }

    /**
     * Kết quả tách hai nhóm của teeing.
     */
    private record TachNhom(List<KetQuaDong.HopLe> hopLe, List<LoiDong> loi) {
    }
}
