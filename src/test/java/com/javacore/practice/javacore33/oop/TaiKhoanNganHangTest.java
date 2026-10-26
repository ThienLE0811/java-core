package com.javacore.practice.javacore33.oop;

import com.javacore.practice.javacore33.oop.TaiKhoanNganHang.GiaoDich;
import com.javacore.practice.javacore33.oop.TaiKhoanNganHang.LoaiGiaoDich;
import com.javacore.practice.javacore33.oop.TaiKhoanNganHang.SoDuKhongDuException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 8 - deposit/withdraw với validation, lịch sử giao dịch và static field.
 *
 * <p>Trọng tâm là <em>hai bất biến</em> mà object phải giữ sau mọi thao tác:
 * <ol>
 *   <li>Số dư luôn khớp với bản ghi cuối trong lịch sử ({@code soDuSau}), và luôn có đúng
 *       2 chữ số thập phân.</li>
 *   <li>Một thao tác thất bại không để lại <em>bất kỳ</em> dấu vết nào - không đổi số dư,
 *       không ghi lịch sử.</li>
 * </ol>
 *
 * <p>Vì vậy phần lớn test sau khi assert exception đều assert tiếp trạng thái của object.
 * Chỉ assert "có ném" là bỏ sót đúng nửa hợp đồng.
 *
 * <p>Dùng {@code assertEquals} trên {@link BigDecimal} là <em>cố ý</em>, không phải
 * {@code compareTo}: {@code equals} của {@code BigDecimal} so cả scale, nên nó kiểm luôn
 * được việc chuẩn hoá về 2 chữ số thập phân. Nếu chỉ cần so giá trị thì {@code compareTo}
 * đã đủ, nhưng ở đây scale chính là thứ cần kiểm.
 */
class TaiKhoanNganHangTest {

    private static final String SO_TK = "0123456789";
    private static final String CHU_TK = "Nguyễn Văn Nam";

    private static TaiKhoanNganHang taiKhoan(String soDuBanDau) {
        return new TaiKhoanNganHang(SO_TK, CHU_TK, new BigDecimal(soDuBanDau));
    }

    private static BigDecimal tien(String giaTri) {
        return new BigDecimal(giaTri);
    }

    // --- constructor: trường hợp hợp lệ ----------------------------------

    @Test
    void taoTaiKhoanHopLe() {
        TaiKhoanNganHang tk = taiKhoan("100");

        assertEquals(tien("100.00"), tk.getSoDu());
        assertTrue(tk.getLichSu().isEmpty(), "tài khoản mới chưa có giao dịch nào");
    }

    @ParameterizedTest(name = "số dư ban đầu {0} -> {1}")
    @CsvSource({
            "100,      100.00",
            "100.0,    100.00",
            "100.000,  100.00",
            "99.999,   100.00",
            "0.004,    0.00",
            "0,        0.00"
    })
    void soDuBanDauDuocChuanHoaVeHaiChuSoThapPhan(String dauVao, String mongDoi) {
        assertEquals(tien(mongDoi), taiKhoan(dauVao).getSoDu());
    }

    @Test
    void soDuBanDauBangKhongDuocChapNhan() {
        assertEquals(tien("0.00"), taiKhoan("0").getSoDu());
    }

    /**
     * Không có getter cho {@code soTaiKhoan}, nên kiểm việc {@code strip()} gián tiếp qua
     * {@code equals} - vốn chỉ so trên field đó.
     */
    @Test
    void soTaiKhoanBiCatKhoangTrangDauCuoi() {
        TaiKhoanNganHang coKhoangTrang = new TaiKhoanNganHang("  " + SO_TK + "  ", CHU_TK, tien("0"));

        assertEquals(taiKhoan("0"), coKhoangTrang);
    }

    // --- constructor: trường hợp bị từ chối ------------------------------

    @Test
    void soTaiKhoanNullNemNullPointerException() {
        NullPointerException loi = assertThrows(NullPointerException.class,
                () -> new TaiKhoanNganHang(null, CHU_TK, tien("0")));

        assertEquals("soTaiKhoan không được null", loi.getMessage());
    }

    @Test
    void chuTaiKhoanNullNemNullPointerException() {
        NullPointerException loi = assertThrows(NullPointerException.class,
                () -> new TaiKhoanNganHang(SO_TK, null, tien("0")));

        assertEquals("chuTaiKhoan không được null", loi.getMessage());
    }

    @Test
    void soDuBanDauNullNemNullPointerException() {
        NullPointerException loi = assertThrows(NullPointerException.class,
                () -> new TaiKhoanNganHang(SO_TK, CHU_TK, null));

        assertEquals("soDuBanDau không được null", loi.getMessage());
    }

    @ParameterizedTest(name = "soTaiKhoan [{0}] bị từ chối")
    @ValueSource(strings = {"", " ", "   ", "\t", "\n", " \t \n "})
    void soTaiKhoanRongBiTuChoi(String soTaiKhoan) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new TaiKhoanNganHang(soTaiKhoan, CHU_TK, tien("0")));

        assertEquals("soTaiKhoan không được rỗng", loi.getMessage());
    }

    @ParameterizedTest(name = "chuTaiKhoan [{0}] bị từ chối")
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void chuTaiKhoanRongBiTuChoi(String chuTaiKhoan) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new TaiKhoanNganHang(SO_TK, chuTaiKhoan, tien("0")));

        assertEquals("chuTaiKhoan không được rỗng", loi.getMessage());
    }

    @ParameterizedTest(name = "số dư ban đầu {0} bị từ chối")
    @ValueSource(strings = {"-0.01", "-1", "-100.00", "-99999999999999999999"})
    void soDuBanDauAmBiTuChoi(String soDuBanDau) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new TaiKhoanNganHang(SO_TK, CHU_TK, tien(soDuBanDau)));

        assertEquals(IllegalArgumentException.class, loi.getClass(),
                "phải là IllegalArgumentException thuần, không phải lớp con nào khác");
        assertEquals("Số dư ban đầu không được âm, nhận được: " + soDuBanDau, loi.getMessage());
    }

    // --- deposit: đường thành công ---------------------------------------

    @Test
    void depositTangSoDuVaGhiMotBanGhiLichSu() {
        TaiKhoanNganHang tk = taiKhoan("100");

        tk.deposit(tien("50"));

        assertEquals(tien("150.00"), tk.getSoDu());

        List<GiaoDich> lichSu = tk.getLichSu();
        assertEquals(1, lichSu.size());
        assertEquals(LoaiGiaoDich.NAP, lichSu.getFirst().loai());
        assertEquals(tien("50.00"), lichSu.getFirst().soTien());
        assertEquals(tien("150.00"), lichSu.getFirst().soDuSau());
    }

    /**
     * Bất biến quan trọng nhất của bài: số tiền ghi vào sổ phải đúng bằng số tiền thực sự
     * cộng vào số dư. Nếu {@code deposit} cộng giá trị thô nhưng đưa giá trị đã làm tròn cho
     * {@code GiaoDich} (hoặc ngược lại), sổ và số dư sẽ lệch nhau - loại sai lệch chỉ lộ ra
     * sau hàng nghìn giao dịch.
     */
    @Test
    void soTienGhiTrongSoKhopVoiSoTienThucSuCongVaoSoDu() {
        TaiKhoanNganHang tk = taiKhoan("100");

        tk.deposit(tien("50.555"));

        GiaoDich gd = tk.getLichSu().getFirst();
        assertEquals(tien("50.56"), gd.soTien(), "50.555 làm tròn HALF_UP thành 50.56");
        assertEquals(tien("150.56"), tk.getSoDu());
        assertEquals(tk.getSoDu(), gd.soDuSau(), "soDuSau trong sổ phải bằng số dư thật");
    }

    @Test
    void nhieuLanDepositCongDonVaGhiDuLichSu() {
        TaiKhoanNganHang tk = taiKhoan("0");

        tk.deposit(tien("10"));
        tk.deposit(tien("20"));
        tk.deposit(tien("0.01"));

        assertEquals(tien("30.01"), tk.getSoDu());
        assertEquals(3, tk.getLichSu().size());
        assertEquals(tien("30.01"), tk.getLichSu().getLast().soDuSau());
    }

    @Test
    void thoiDiemGiaoDichDuocGhiLai() {
        TaiKhoanNganHang tk = taiKhoan("0");
        Instant truoc = Instant.now();

        tk.deposit(tien("10"));

        Instant thoiDiem = tk.getLichSu().getFirst().thoiDiem();
        assertTrue(!thoiDiem.isBefore(truoc) && !thoiDiem.isAfter(Instant.now()),
                "thoiDiem phải nằm trong khoảng thời gian thực hiện test");
    }

    /**
     * {@code 0.005} làm tròn HALF_UP thành {@code 0.01} - vẫn là số tiền hợp lệ, không bị
     * chặn. Đây là biên dưới của những giá trị được chấp nhận.
     */
    @Test
    void soTienNhoNhatVanDuocChapNhanLaMotXu() {
        TaiKhoanNganHang tk = taiKhoan("0");

        tk.deposit(tien("0.005"));

        assertEquals(tien("0.01"), tk.getSoDu());
    }

    // --- deposit: trường hợp bị từ chối ----------------------------------

    @Test
    void depositNullNemNullPointerException() {
        TaiKhoanNganHang tk = taiKhoan("100");

        NullPointerException loi = assertThrows(NullPointerException.class, () -> tk.deposit(null));

        assertEquals("soTien không được null", loi.getMessage());
    }

    @ParameterizedTest(name = "deposit({0}) bị chặn")
    @ValueSource(strings = {"0", "0.00", "-0.01", "-1", "-999999"})
    void depositSoTienKhongDuongBiChan(String soTien) {
        TaiKhoanNganHang tk = taiKhoan("100");

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> tk.deposit(tien(soTien)));

        assertEquals("Số tiền nạp phải dương, nhận được: " + soTien, loi.getMessage());
    }

    @ParameterizedTest(name = "deposit({0}) quá nhỏ")
    @ValueSource(strings = {"0.001", "0.004", "0.0049"})
    void depositSoTienQuaNhoBiChan(String soTien) {
        TaiKhoanNganHang tk = taiKhoan("100");

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> tk.deposit(tien(soTien)));

        assertEquals("Số tiền nạp quá nhỏ: " + soTien + " bị làm tròn thành 0.00", loi.getMessage());
    }

    /**
     * Message phải nói đúng thao tác người dùng đang làm. Đây là lỗi copy-paste rất dễ xảy ra
     * khi {@code deposit} và {@code withdraw} dùng chung một hàm validate.
     */
    @Test
    void messageLoiCuaDepositNoiVeNapChuKhongPhaiRut() {
        TaiKhoanNganHang tk = taiKhoan("100");

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> tk.deposit(tien("-1")));

        assertTrue(loi.getMessage().contains("nạp"), "message phải nói về nạp tiền: " + loi.getMessage());
        assertFalse(loi.getMessage().contains("rút"), "message không được nói về rút tiền: " + loi.getMessage());
    }

    @Test
    void depositThatBaiKhongDeLaiDauVet() {
        TaiKhoanNganHang tk = taiKhoan("100");

        assertThrows(RuntimeException.class, () -> tk.deposit(null));
        assertThrows(RuntimeException.class, () -> tk.deposit(tien("-50")));
        assertThrows(RuntimeException.class, () -> tk.deposit(tien("0.001")));

        assertEquals(tien("100.00"), tk.getSoDu());
        assertTrue(tk.getLichSu().isEmpty());
    }

    // --- withdraw: đường thành công --------------------------------------

    @Test
    void withdrawGiamSoDuVaGhiMotBanGhiLichSu() {
        TaiKhoanNganHang tk = taiKhoan("100");

        tk.withdraw(tien("30"));

        assertEquals(tien("70.00"), tk.getSoDu());

        GiaoDich gd = tk.getLichSu().getFirst();
        assertEquals(LoaiGiaoDich.RUT, gd.loai());
        assertEquals(tien("30.00"), gd.soTien());
        assertEquals(tien("70.00"), gd.soDuSau());
    }

    @Test
    void rutDungBangSoDuDuocPhepVaDuaSoDuVeKhong() {
        TaiKhoanNganHang tk = taiKhoan("100");

        tk.withdraw(tien("100"));

        assertEquals(tien("0.00"), tk.getSoDu());
        assertEquals(tien("0.00"), tk.getLichSu().getFirst().soDuSau());
    }

    /**
     * Luật đủ số dư phải kiểm trên con số <em>sẽ thực sự bị trừ</em>, tức là sau khi làm tròn.
     * Số dư {@code 100.00}, rút {@code 100.004}: giá trị thô lớn hơn số dư nhưng số tiền thực
     * trừ chỉ là {@code 100.00} - vừa đủ, phải cho rút.
     */
    @Test
    void kiemTraDuSoDuDuaTrenSoTienDaLamTron() {
        TaiKhoanNganHang tk = taiKhoan("100");

        tk.withdraw(tien("100.004"));

        assertEquals(tien("0.00"), tk.getSoDu());
    }

    @Test
    void lichSuGiuDungThuTuThoiGian() {
        TaiKhoanNganHang tk = taiKhoan("100");

        tk.deposit(tien("50"));
        tk.withdraw(tien("30"));
        tk.deposit(tien("5"));

        List<GiaoDich> lichSu = tk.getLichSu();
        assertEquals(3, lichSu.size());
        assertEquals(LoaiGiaoDich.NAP, lichSu.get(0).loai());
        assertEquals(LoaiGiaoDich.RUT, lichSu.get(1).loai());
        assertEquals(LoaiGiaoDich.NAP, lichSu.get(2).loai());
        assertEquals(tien("125.00"), lichSu.getLast().soDuSau());
        assertEquals(tk.getSoDu(), lichSu.getLast().soDuSau());
    }

    // --- withdraw: số dư không đủ ----------------------------------------

    @Test
    void rutQuaSoDuNemSoDuKhongDuException() {
        TaiKhoanNganHang tk = taiKhoan("100");

        SoDuKhongDuException loi = assertThrows(SoDuKhongDuException.class,
                () -> tk.withdraw(tien("100.01")));

        assertEquals("Số dư không đủ: cần rút 100.01 nhưng chỉ có 100.00", loi.getMessage());
        assertEquals(tien("100.00"), loi.getSoDuHienTai());
        assertEquals(tien("100.01"), loi.getSoTienYeuCau());
    }

    /**
     * Số dư không đủ là lỗi <em>nghiệp vụ</em>, không phải lỗi gọi sai tham số. Tầng trên
     * phải phân biệt được hai loại để trả mã lỗi HTTP khác nhau, nên nó không được là
     * {@link IllegalArgumentException}.
     *
     * <p>Kiểm bằng {@link Class#isAssignableFrom(Class)} chứ không bằng {@code instanceof}:
     * compiler biết chắc hai nhánh này không giao nhau nên {@code instanceof} là lỗi compile,
     * không phải assert chạy được.
     */
    @Test
    void soDuKhongDuKhongPhaiLaIllegalArgumentException() {
        TaiKhoanNganHang tk = taiKhoan("100");

        assertThrows(SoDuKhongDuException.class, () -> tk.withdraw(tien("200")));

        assertFalse(IllegalArgumentException.class.isAssignableFrom(SoDuKhongDuException.class));
        assertTrue(RuntimeException.class.isAssignableFrom(SoDuKhongDuException.class),
                "phải là unchecked exception");
    }

    @Test
    void rutThieuMotXuCungBiChan() {
        TaiKhoanNganHang tk = taiKhoan("100");

        assertThrows(SoDuKhongDuException.class, () -> tk.withdraw(tien("100.005")),
                "100.005 làm tròn thành 100.01, vượt số dư 100.00");
    }

    @Test
    void withdrawThatBaiKhongDeLaiDauVet() {
        TaiKhoanNganHang tk = taiKhoan("100");
        tk.deposit(tien("50"));

        assertThrows(SoDuKhongDuException.class, () -> tk.withdraw(tien("9999")));
        assertThrows(IllegalArgumentException.class, () -> tk.withdraw(tien("-5")));
        assertThrows(NullPointerException.class, () -> tk.withdraw(null));

        assertEquals(tien("150.00"), tk.getSoDu());
        assertEquals(1, tk.getLichSu().size(), "chỉ còn đúng giao dịch nạp thành công");
    }

    @Test
    void messageLoiCuaWithdrawNoiVeRutChuKhongPhaiNap() {
        TaiKhoanNganHang tk = taiKhoan("100");

        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> tk.withdraw(tien("-1")));

        assertEquals("Số tiền rút phải dương, nhận được: -1", loi.getMessage());
    }

    // --- getLichSu: đóng gói ---------------------------------------------

    @Test
    void khongSuaDuocLichSuTuBenNgoai() {
        TaiKhoanNganHang tk = taiKhoan("100");
        tk.deposit(tien("50"));

        List<GiaoDich> lichSu = tk.getLichSu();

        assertThrows(UnsupportedOperationException.class, lichSu::clear);
        assertThrows(UnsupportedOperationException.class,
                () -> lichSu.add(new GiaoDich(LoaiGiaoDich.NAP, tien("1"), tien("1"), Instant.now())));
        assertEquals(1, tk.getLichSu().size());
    }

    /**
     * {@code List.copyOf} trả về bản chụp tại thời điểm gọi, không phải khung nhìn sống.
     * Người gọi cầm list đi xử lý sẽ không bị dữ liệu đổi dưới chân.
     */
    @Test
    void lichSuTraVeLaBanChupKhongDoiTheoTaiKhoan() {
        TaiKhoanNganHang tk = taiKhoan("100");
        tk.deposit(tien("50"));

        List<GiaoDich> banChup = tk.getLichSu();
        tk.deposit(tien("10"));

        assertEquals(1, banChup.size(), "bản chụp cũ không được đổi theo");
        assertEquals(2, tk.getLichSu().size());
    }

    // --- totalAccounts ----------------------------------------------------

    /**
     * Cố ý đo <em>chênh lệch</em> chứ không assert giá trị tuyệt đối: {@code totalAccounts} là
     * static, cộng dồn qua mọi test trong cả lớp và không reset giữa các lần chạy. Assert
     * {@code == 2} sẽ xanh khi chạy riêng method này và đỏ khi chạy cả bộ - đúng kiểu test
     * phụ thuộc thứ tự mà static mutable state gây ra.
     */
    @Test
    void totalAccountsTangTheoSoTaiKhoanTaoThanhCong() {
        int truoc = TaiKhoanNganHang.getTotalAccounts();

        taiKhoan("0");
        taiKhoan("100");

        assertEquals(truoc + 2, TaiKhoanNganHang.getTotalAccounts());
    }

    @Test
    void totalAccountsKhongTangKhiConstructorNem() {
        int truoc = TaiKhoanNganHang.getTotalAccounts();

        assertThrows(NullPointerException.class, () -> new TaiKhoanNganHang(null, CHU_TK, tien("0")));
        assertThrows(IllegalArgumentException.class, () -> new TaiKhoanNganHang(" ", CHU_TK, tien("0")));
        assertThrows(IllegalArgumentException.class, () -> new TaiKhoanNganHang(SO_TK, CHU_TK, tien("-1")));

        assertEquals(truoc, TaiKhoanNganHang.getTotalAccounts(),
                "tài khoản không được tạo ra thì không được đếm");
    }

    // --- equals / hashCode: danh tính theo số tài khoản -------------------

    @Test
    void haiTaiKhoanCungSoThiBangNhauDuKhacSoDu() {
        TaiKhoanNganHang a = new TaiKhoanNganHang(SO_TK, CHU_TK, tien("100"));
        TaiKhoanNganHang b = new TaiKhoanNganHang(SO_TK, "Người khác", tien("999"));

        assertEquals(a, b, "cùng số tài khoản là cùng một tài khoản, chụp ở hai thời điểm");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void khacSoTaiKhoanThiKhacNhau() {
        TaiKhoanNganHang a = new TaiKhoanNganHang("0001", CHU_TK, tien("100"));
        TaiKhoanNganHang b = new TaiKhoanNganHang("0002", CHU_TK, tien("100"));

        assertNotEquals(a, b);
    }

    @Test
    void khongBangNullVaKhongBangKieuKhac() {
        TaiKhoanNganHang tk = taiKhoan("100");

        assertNotEquals(null, tk);
        assertNotEquals("0123456789", tk);
    }

    /**
     * Hậu quả cụ thể nếu {@code soDu} tham gia {@code equals}/{@code hashCode}: object sẽ biến
     * mất khỏi chính {@code HashSet} đang chứa nó ngay sau một lần {@code deposit}.
     */
    @Test
    void timLaiDuocTrongHashSetSauKhiSoDuThayDoi() {
        TaiKhoanNganHang tk = taiKhoan("100");
        Set<TaiKhoanNganHang> set = new HashSet<>();
        set.add(tk);
        int hashTruoc = tk.hashCode();

        tk.deposit(tien("50"));
        tk.withdraw(tien("20"));

        assertEquals(hashTruoc, tk.hashCode(), "hashCode không được đổi theo số dư");
        assertTrue(set.contains(tk), "object phải vẫn tìm lại được trong set");
    }

    @Test
    void hashSetCoiHaiTaiKhoanCungSoLaMot() {
        Set<TaiKhoanNganHang> set = new HashSet<>();

        set.add(new TaiKhoanNganHang(SO_TK, CHU_TK, tien("100")));
        set.add(new TaiKhoanNganHang(SO_TK, CHU_TK, tien("999")));

        assertEquals(1, set.size());
    }

    // --- toString ---------------------------------------------------------

    @Test
    void toStringChuaCacTruongCanThiet() {
        TaiKhoanNganHang tk = taiKhoan("100");
        tk.deposit(tien("50"));

        assertEquals("TaiKhoanNganHang{soTaiKhoan=" + SO_TK + ", chuTaiKhoan=" + CHU_TK
                + ", soDu=150.00, soGiaoDich=1}", tk.toString());
    }

    /**
     * {@code toString()} bị gọi nhiều nhất trong log. In cả lịch sử sẽ biến một dòng log thành
     * vài trăm KB với tài khoản dùng lâu năm, nên chỉ in số lượng.
     */
    @Test
    void toStringKhongInNoiDungLichSu() {
        TaiKhoanNganHang tk = taiKhoan("100");
        tk.deposit(tien("50"));
        tk.withdraw(tien("20"));

        String ketQua = tk.toString();

        assertFalse(ketQua.contains("GiaoDich["), "không được in từng giao dịch: " + ketQua);
        assertTrue(ketQua.contains("soGiaoDich=2"));
    }

    // --- GiaoDich: bất biến của value object ------------------------------

    @Test
    void giaoDichChuanHoaScaleVeHaiChuSo() {
        GiaoDich gd = new GiaoDich(LoaiGiaoDich.NAP, tien("50"), tien("100"), Instant.now());

        assertEquals(tien("50.00"), gd.soTien());
        assertEquals(tien("100.00"), gd.soDuSau());
    }

    /**
     * {@code BigDecimal.equals} so cả scale, nên không chuẩn hoá thì {@code 50} và
     * {@code 50.00} tạo ra hai {@code GiaoDich} khác nhau dù cùng giá trị.
     */
    @Test
    void haiGiaoDichCungGiaTriKhacScaleThiBangNhau() {
        Instant thoiDiem = Instant.now();

        GiaoDich a = new GiaoDich(LoaiGiaoDich.NAP, tien("50"), tien("100"), thoiDiem);
        GiaoDich b = new GiaoDich(LoaiGiaoDich.NAP, tien("50.00"), tien("100.000"), thoiDiem);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest(name = "GiaoDich với soTien = {0} bị từ chối")
    @ValueSource(strings = {"0", "-0.01", "-100"})
    void giaoDichSoTienKhongDuongBiTuChoi(String soTien) {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new GiaoDich(LoaiGiaoDich.NAP, tien(soTien), tien("100"), Instant.now()));

        assertEquals("Số tiền giao dịch phải dương, nhận được: " + soTien, loi.getMessage());
    }

    @Test
    void giaoDichSoDuSauAmBiTuChoi() {
        IllegalArgumentException loi = assertThrows(IllegalArgumentException.class,
                () -> new GiaoDich(LoaiGiaoDich.RUT, tien("10"), tien("-0.01"), Instant.now()));

        assertEquals("Số dư sau giao dịch không được âm, nhận được: -0.01", loi.getMessage());
    }

    @Test
    void giaoDichKhongChapNhanComponentNull() {
        Instant now = Instant.now();

        assertEquals("loai không được null", assertThrows(NullPointerException.class,
                () -> new GiaoDich(null, tien("10"), tien("100"), now)).getMessage());
        assertEquals("soTien không được null", assertThrows(NullPointerException.class,
                () -> new GiaoDich(LoaiGiaoDich.NAP, null, tien("100"), now)).getMessage());
        assertEquals("soDuSau không được null", assertThrows(NullPointerException.class,
                () -> new GiaoDich(LoaiGiaoDich.NAP, tien("10"), null, now)).getMessage());
        assertEquals("thoiDiem không được null", assertThrows(NullPointerException.class,
                () -> new GiaoDich(LoaiGiaoDich.NAP, tien("10"), tien("100"), null)).getMessage());
    }
}
