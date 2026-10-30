package com.javacore.practice.javacore33.streams.optional;

import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Bài 28 - Optional: orElse, orElseGet, orElseThrow, map, flatMap, filter.
 * Nửa sau là 4 cái bẫy hay gặp nhất khi review code: gọi .get() không kiểm tra,
 * dùng Optional làm field/tham số, orElse(taoObjectTonKem()), và isPresent + get.
 */
public class OptionalDemo {

    private static final String DONG = "%-40s: %s%n";

    private static final UserRepository USER_REPOSITORY = new UserRepository();
    private static final AddressRepository ADDRESS_REPOSITORY = new AddressRepository();
    private static final UserService USER_SERVICE = new UserService(USER_REPOSITORY, ADDRESS_REPOSITORY);

    private static final String EMAIL_CO_THAT = "an.nguyen@example.com";
    private static final String EMAIL_KHONG_CO = "khong.ton.tai@example.com";
    private static final String EMAIL_DA_KHOA = "cuc.le@example.com";
    private static final String EMAIL_KHONG_PHONE = "bao.tran@example.com";

    // Đếm số lần "tạo object tốn kém" để chứng minh orElse vs orElseGet.
    private static int soLanTaoMacDinh;

    public static void main(String[] args) {
        taoOptional();
        layGiaTriRaNgoai();
        bienDoiBangMap();
        noiChuoiBangFlatMap();
        locBangFilter();
        cacHamTienIch();

        bayGoiGetKhongKiemTra();
        bayOptionalLamFieldVaThamSo();
        bayOrElseTonKem();
        bayIsPresentRoiGet();
    }

    /**
     * Câu 1: ba cách tạo Optional và cái bẫy Optional.of(null).
     */
    private static void taoOptional() {
        System.out.println("=== 1. Tạo Optional ===");

        // of: CAM KẾT giá trị không null. Null là ném NPE ngay lập tức.
        Optional<String> coGiaTri = Optional.of("xin chào");
        // ofNullable: "có thể null" - null thì thành empty, không ném gì.
        Optional<String> tuNull = Optional.ofNullable(null);
        Optional<String> rong = Optional.empty();

        System.out.printf(DONG, "Optional.of(\"xin chào\")", coGiaTri);
        System.out.printf(DONG, "Optional.ofNullable(null)", tuNull);
        System.out.printf(DONG, "Optional.empty()", rong);
        System.out.printf(DONG, "isPresent / isEmpty", coGiaTri.isPresent() + " / " + coGiaTri.isEmpty());

        // BẪY: Optional.of(null) nổ NGAY tại dòng tạo, không đợi đến lúc lấy giá trị.
        try {
            String chuaBiet = null;
            Optional.of(chuaBiet);
        } catch (NullPointerException e) {
            System.out.printf(DONG, "Optional.of(null)", "NullPointerException ngay lập tức");
        }
        // Quy tắc chọn: biết chắc không null -> of (để lỗi lộ ra sớm).
        //               không chắc -> ofNullable.
        System.out.println();
    }

    /**
     * Câu 2: orElse, orElseGet, orElseThrow - ba cách bóc giá trị ra khỏi Optional.
     */
    private static void layGiaTriRaNgoai() {
        System.out.println("=== 2. orElse / orElseGet / orElseThrow ===");

        Optional<User> timThay = USER_REPOSITORY.findByEmail(EMAIL_CO_THAT);
        Optional<User> khongThay = USER_REPOSITORY.findByEmail(EMAIL_KHONG_CO);

        // orElse: giá trị thay thế là một GIÁ TRỊ đã có sẵn.
        System.out.printf(DONG, "orElse khi có", timThay.map(User::name).orElse("Khách"));
        System.out.printf(DONG, "orElse khi rỗng", khongThay.map(User::name).orElse("Khách"));

        // orElseGet: giá trị thay thế là một HÀM, chỉ chạy khi Optional rỗng.
        System.out.printf(DONG, "orElseGet khi rỗng",
                khongThay.map(User::name).orElseGet(() -> "Khách vãng lai"));

        // orElseThrow không tham số -> NoSuchElementException, thông điệp cộc lốc.
        try {
            khongThay.orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.printf(DONG, "orElseThrow()", e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // orElseThrow(Supplier) -> exception của mình, có ngữ cảnh. Ở service hãy dùng bản này.
        try {
            USER_SERVICE.layBatBuoc(EMAIL_KHONG_CO);
        } catch (UserNotFoundException e) {
            System.out.printf(DONG, "orElseThrow(Supplier)", e.getMessage());
        }

        System.out.printf(DONG, "layBatBuoc khi có", USER_SERVICE.layBatBuoc(EMAIL_CO_THAT).name());
        System.out.println();
    }

    /**
     * Câu 3: map - biến đổi giá trị BÊN TRONG mà không cần bóc ra.
     */
    private static void bienDoiBangMap() {
        System.out.println("=== 3. map ===");

        Optional<User> user = USER_REPOSITORY.findByEmail(EMAIL_CO_THAT);

        // map đổi Optional<User> -> Optional<String>, vẫn còn trong hộp.
        System.out.printf(DONG, "map(User::name)", user.map(User::name));
        // Nối nhiều map được, mỗi bước là một phép biến đổi nhỏ.
        System.out.printf(DONG, "map(name).map(toUpperCase)", user.map(User::name).map(String::toUpperCase));

        // map trên Optional RỖNG: hàm bên trong KHÔNG hề chạy, kết quả vẫn rỗng.
        // Đây chính là thứ thay thế cho chuỗi if (x != null) lồng nhau.
        Optional<String> tuRong = USER_REPOSITORY.findByEmail(EMAIL_KHONG_CO)
                .map(u -> {
                    System.out.println("    (dòng này không bao giờ in ra)");
                    return u.name();
                });
        System.out.printf(DONG, "map trên Optional rỗng", tuRong);

        // ĐIỂM QUAN TRỌNG: map mà hàm trả về null thì kết quả là Optional.empty(),
        // không phải Optional chứa null. User #2 có phone = null.
        System.out.printf(DONG, "map(User::phone) khi phone null",
                USER_SERVICE.soDienThoai(EMAIL_KHONG_PHONE));
        System.out.printf(DONG, "map(User::phone) khi có phone",
                USER_SERVICE.soDienThoai(EMAIL_CO_THAT));
        System.out.println();
    }

    /**
     * Câu 4: flatMap - khi hàm bên trong ĐÃ trả về Optional.
     */
    private static void noiChuoiBangFlatMap() {
        System.out.println("=== 4. flatMap ===");

        Optional<User> user = USER_REPOSITORY.findByEmail(EMAIL_CO_THAT);

        // Dùng map với hàm trả Optional -> lồng hai tầng. Compile qua, nhưng vô dụng:
        // muốn lấy city phải .get().get() hoặc .map(o -> o.map(...)) rất rối.
        Optional<Optional<Address>> longNhau = user.map(u -> ADDRESS_REPOSITORY.findByUserId(u.id()));
        System.out.printf(DONG, "map -> Optional<Optional<Address>>", longNhau);

        // flatMap làm phẳng một tầng: Optional<User> -> Optional<Address>.
        Optional<Address> phang = user.flatMap(u -> ADDRESS_REPOSITORY.findByUserId(u.id()));
        System.out.printf(DONG, "flatMap -> Optional<Address>", phang);

        // Quy tắc: hàm trả về GIÁ TRỊ -> map. Hàm trả về OPTIONAL -> flatMap.
        System.out.printf(DONG, "thanhPho(user có địa chỉ)", USER_SERVICE.thanhPho(EMAIL_CO_THAT));
        // User #2 tồn tại nhưng không có địa chỉ -> rỗng, và không hề nổ ở giữa chuỗi.
        System.out.printf(DONG, "thanhPho(user không địa chỉ)", USER_SERVICE.thanhPho(EMAIL_KHONG_PHONE));
        System.out.printf(DONG, "thanhPho(email không tồn tại)", USER_SERVICE.thanhPho(EMAIL_KHONG_CO));
        System.out.println();
    }

    /**
     * Câu 5: filter - thêm điều kiện, không thoả thì Optional thành rỗng.
     */
    private static void locBangFilter() {
        System.out.println("=== 5. filter ===");

        System.out.printf(DONG, "filter(active) - user đang hoạt động",
                USER_SERVICE.timNguoiDungDangHoatDong(EMAIL_CO_THAT).map(User::name));
        // User #3 CÓ trong DB nhưng active = false -> filter đẩy về rỗng.
        System.out.printf(DONG, "filter(active) - user đã khoá",
                USER_SERVICE.timNguoiDungDangHoatDong(EMAIL_DA_KHOA).map(User::name));

        // Chuỗi đầy đủ find -> filter -> flatMap -> map -> orElse, đọc như một câu văn.
        System.out.printf(DONG, "chuỗi đầy đủ (user hoạt động)",
                USER_SERVICE.thanhPhoCuaNguoiDungDangHoatDong(EMAIL_CO_THAT));
        // User #3 có địa chỉ Hà Nội, nhưng bị filter chặn từ trước -> "Chưa rõ".
        System.out.printf(DONG, "chuỗi đầy đủ (user đã khoá)",
                USER_SERVICE.thanhPhoCuaNguoiDungDangHoatDong(EMAIL_DA_KHOA));
        System.out.println();
    }

    /**
     * Câu 6: ifPresent, ifPresentOrElse, or, stream.
     */
    private static void cacHamTienIch() {
        System.out.println("=== 6. ifPresent / ifPresentOrElse / or / stream ===");

        // ifPresent: làm gì đó khi có, không trả về gì. Dùng cho side-effect (log, gửi mail).
        USER_REPOSITORY.findByEmail(EMAIL_CO_THAT)
                .ifPresent(u -> System.out.printf(DONG, "ifPresent", u.name()));

        // ifPresentOrElse (Java 9+): xử lý cả hai nhánh, thay cho if/else trên isPresent.
        USER_REPOSITORY.findByEmail(EMAIL_KHONG_CO).ifPresentOrElse(
                u -> System.out.printf(DONG, "ifPresentOrElse - có", u.name()),
                () -> System.out.printf(DONG, "ifPresentOrElse - rỗng", "chạy nhánh else"));

        // or (Java 9+): rỗng thì thử nguồn khác, VẪN trả về Optional (khác orElse trả giá trị).
        Optional<User> duPhong = USER_REPOSITORY.findByEmail(EMAIL_KHONG_CO)
                .or(() -> USER_REPOSITORY.findById(1));
        System.out.printf(DONG, "or(() -> findById(1))", duPhong.map(User::name));

        // stream (Java 9+): Optional -> Stream 0 hoặc 1 phần tử, ghép thẳng vào pipeline.
        System.out.printf(DONG, "flatMap + Optional::stream", USER_SERVICE.thanhPhoCuaTatCa());
        System.out.println();
    }

    // ============================================================
    // BỐN CÁI BẪY KHI REVIEW CODE
    // ============================================================

    /**
     * BẪY 1: gọi .get() mà không kiểm tra.
     */
    private static void bayGoiGetKhongKiemTra() {
        System.out.println("=== 7. BẪY: .get() không kiểm tra ===");

        // SAI: Optional sinh ra để BẮT người đọc nghĩ tới trường hợp rỗng. Gọi thẳng .get()
        // là vứt hết tác dụng đó đi, chỉ đổi NullPointerException lấy NoSuchElementException.
        try {
            User u = USER_REPOSITORY.findByEmail(EMAIL_KHONG_CO).get();
            System.out.println(u);
        } catch (NoSuchElementException e) {
            System.out.printf(DONG, "Nổ như dự đoán", e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // ĐÚNG: nói rõ ý định - thiếu dữ liệu thì ném lỗi có ngữ cảnh.
        try {
            USER_SERVICE.layBatBuoc(EMAIL_KHONG_CO);
        } catch (UserNotFoundException e) {
            System.out.printf(DONG, "orElseThrow thay cho get()", e.getMessage());
        }

        // Ghi chú: Java 10 thêm orElseThrow() không tham số, hành vi ĐÚNG BẰNG get(), nhưng
        // tên gọi nói rõ "có thể ném". Thật sự cần hành vi đó thì viết orElseThrow() cho
        // người đọc thấy ngay, đừng viết get().
        System.out.println();
    }

    /**
     * BẪY 2: Optional làm field hoặc tham số.
     */
    private static void bayOptionalLamFieldVaThamSo() {
        System.out.println("=== 8. BẪY: Optional làm field / tham số ===");

        // Optional được thiết kế cho KIỂU TRẢ VỀ. Làm field thì dính 3 vấn đề thật:
        HoSoSai sai = new HoSoSai("Trần Minh Bảo", null);
        System.out.printf(DONG, "field Optional vẫn null được", sai.ten + " -> " + sai.phone);
        // (1) Chính cái field Optional ấy vẫn có thể null -> giờ có HAI cách biểu diễn
        //     "không có": field null, và field là Optional.empty(). Tệ hơn lúc đầu.
        try {
            System.out.println(sai.phone.isPresent());
        } catch (NullPointerException e) {
            System.out.printf(DONG, "sai.phone.isPresent()", "NullPointerException - đúng thứ định tránh");
        }
        // (2) Optional KHÔNG implement Serializable -> entity/DTO đem serialize sẽ vỡ.
        System.out.printf(DONG, "Optional implements Serializable?",
                Serializable.class.isAssignableFrom(Optional.class));
        // (3) JPA và Jackson không map field Optional ra cột / ra JSON một cách tự nhiên.

        // ĐÚNG: field để kiểu thật và cho phép null (xem record User), rồi bọc Optional
        // ngay tại chỗ đọc khi cần.
        HoSoDung dung = new HoSoDung("Trần Minh Bảo", null);
        System.out.printf(DONG, "bọc tại chỗ đọc",
                dung.ten + " -> " + Optional.ofNullable(dung.phone).orElse("(chưa có)"));

        // Tham số kiểu Optional cũng vậy: ép mọi người gọi phải viết Optional.of(x) rườm rà,
        // mà họ vẫn truyền null vào được. Thay bằng nạp chồng hàm hoặc nhận thẳng null.
        System.out.printf(DONG, "locTheoTrangThai(true)", USER_SERVICE.locTheoTrangThai(true).size() + " người");
        System.out.printf(DONG, "locTheoTrangThai(null) = không lọc",
                USER_SERVICE.locTheoTrangThai(null).size() + " người");
        System.out.println();
    }

    /**
     * BẪY 3: orElse(taoObjectTonKem()) thay vì orElseGet.
     */
    private static void bayOrElseTonKem() {
        System.out.println("=== 9. BẪY: orElse(taoObjectTonKem()) ===");

        Optional<User> timThay = USER_REPOSITORY.findByEmail(EMAIL_CO_THAT);

        // SAI: orElse nhận GIÁ TRỊ, nên taoUserMacDinh() phải chạy xong TRƯỚC khi orElse
        // được gọi - kể cả khi Optional có giá trị và kết quả bị vứt đi ngay sau đó.
        soLanTaoMacDinh = 0;
        User a = timThay.orElse(taoUserMacDinh());
        System.out.printf(DONG, "orElse khi ĐÃ có giá trị", a.name());
        System.out.printf(DONG, "  -> số lần tạo default", soLanTaoMacDinh + " (lãng phí)");

        // ĐÚNG: orElseGet nhận HÀM, chỉ gọi khi thật sự rỗng.
        soLanTaoMacDinh = 0;
        User b = timThay.orElseGet(OptionalDemo::taoUserMacDinh);
        System.out.printf(DONG, "orElseGet khi ĐÃ có giá trị", b.name());
        System.out.printf(DONG, "  -> số lần tạo default", soLanTaoMacDinh + " (không chạy)");

        soLanTaoMacDinh = 0;
        User c = USER_REPOSITORY.findByEmail(EMAIL_KHONG_CO).orElseGet(OptionalDemo::taoUserMacDinh);
        System.out.printf(DONG, "orElseGet khi RỖNG", c.name());
        System.out.printf(DONG, "  -> số lần tạo default", soLanTaoMacDinh + " (chạy đúng 1 lần)");

        // Lãng phí hiệu năng mới là vấn đề NHẸ. Nặng hơn: nếu hàm đó query DB, gọi API hay
        // ghi audit log thì orElse gây tác dụng phụ ngay cả khi không ai cần kết quả.
        // Nguy hiểm nhất là dạng này - nổ dù Optional có giá trị:
        try {
            String ten = Optional.of("có giá trị").orElse(chuoiGayLoi());
            System.out.println(ten);
        } catch (IllegalStateException e) {
            System.out.printf(DONG, "orElse(hàm ném lỗi)", "vẫn nổ: " + e.getMessage());
        }
        System.out.printf(DONG, "orElseGet(hàm ném lỗi)",
                Optional.of("có giá trị").orElseGet(OptionalDemo::chuoiGayLoi));

        // Quy tắc: vế phải là HẰNG hoặc biến có sẵn -> orElse. Là LỜI GỌI HÀM -> orElseGet.
        System.out.println();
    }

    /**
     * BẪY 4: isPresent() rồi get() - viết lại if-null bằng từ vựng mới.
     */
    private static void bayIsPresentRoiGet() {
        System.out.println("=== 10. BẪY: isPresent() rồi get() ===");

        Optional<User> user = USER_REPOSITORY.findByEmail(EMAIL_CO_THAT);

        // SAI: dài hơn if (user != null) mà chẳng an toàn hơn chút nào.
        String cachDai;
        if (user.isPresent()) {
            cachDai = user.get().name().toUpperCase();
        } else {
            cachDai = "KHÁCH";
        }
        System.out.printf(DONG, "isPresent + get", cachDai);

        // ĐÚNG: một chuỗi, không nhánh, không biến tạm.
        String cachGon = user.map(User::name).map(String::toUpperCase).orElse("KHÁCH");
        System.out.printf(DONG, "map + orElse", cachGon);

        // Một dạng dài dòng khác hay gặp trong stream:
        List<String> daiDong = USER_REPOSITORY.findAll().stream()
                .map(u -> ADDRESS_REPOSITORY.findByUserId(u.id()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Address::city)
                .distinct()
                .toList();
        System.out.printf(DONG, "filter(isPresent).map(get)", daiDong);
        System.out.printf(DONG, "flatMap(Optional::stream)", USER_SERVICE.thanhPhoCuaTatCa());

        // Ngoại lệ hợp lệ: isPresent/isEmpty dùng MỘT MÌNH để rẽ luồng thì không sao, ví dụ
        // if (repo.findByEmail(email).isPresent()) throw new EmailDaTonTaiException(email);
        System.out.printf(DONG, "isPresent dùng một mình (OK)",
                USER_REPOSITORY.findByEmail(EMAIL_CO_THAT).isPresent() ? "email đã tồn tại" : "email dùng được");
        System.out.println();
    }

    private static User taoUserMacDinh() {
        soLanTaoMacDinh++;
        // Giả lập việc tốn kém: trong code thật đây có thể là query DB hoặc gọi API ngoài.
        return new User(999, "Khách vãng lai", "guest@example.com", null, false);
    }

    private static String chuoiGayLoi() {
        throw new IllegalStateException("hàm này lẽ ra không được chạy");
    }

    /**
     * Minh hoạ cách dùng SAI: Optional làm field. Đừng viết thế này trong code thật.
     */
    private static final class HoSoSai {
        private final String ten;
        private final Optional<String> phone;

        private HoSoSai(String ten, Optional<String> phone) {
            this.ten = ten;
            this.phone = phone;
        }
    }

    /**
     * Cách dùng ĐÚNG: field kiểu thật, cho phép null, bọc Optional tại chỗ đọc.
     */
    private static final class HoSoDung {
        private final String ten;
        private final String phone;

        private HoSoDung(String ten, String phone) {
            this.ten = ten;
            this.phone = phone;
        }
    }
}
