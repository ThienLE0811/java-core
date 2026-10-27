package com.javacore.practice.javacore33.oop.thietbi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 10 - điện thoại: class duy nhất mang cả hai năng lực.
 *
 * <p>Phần đáng giá nhất là {@link #sacKhongAnhHuongKetNoi()} và
 * {@link #ketNoiKhongAnhHuongPin()}: hai interface được cài trên cùng một object nhưng trạng
 * thái của chúng độc lập. Nếu sau này ai đó cho {@code sac()} đụng vào cờ kết nối thì hai test
 * này đỏ.
 */
class DienThoaiTest {

    // --- sạc --------------------------------------------------------------

    @ParameterizedTest(name = "pin {0} sạc một nhịp -> {1}")
    @CsvSource({
            "0,   20",
            "50,  70",
            "79,  99",
            "80,  100"
    })
    void moiNhipSacTangHaiMuoiPhanTram(int banDau, int mongDoi) {
        DienThoai dienThoai = new DienThoai(banDau);

        dienThoai.sac();

        assertEquals(mongDoi, dienThoai.phanTramPin());
    }

    /** Điện thoại sạc nhanh gấp bốn lần pin dự phòng - cùng một lệnh, hai kết quả. */
    @Test
    void sacNhanhHonPinDuPhong() {
        DienThoai dienThoai = new DienThoai(0);
        PinDuPhong pin = new PinDuPhong(0);

        dienThoai.sac();
        pin.sac();

        assertEquals(20, dienThoai.phanTramPin());
        assertEquals(5, pin.phanTramPin());
    }

    @ParameterizedTest(name = "pin {0} sạc nhiều lần không vượt 100")
    @ValueSource(ints = {81, 90, 99, 100})
    void khongBaoGioVuotMucToiDa(int banDau) {
        DienThoai dienThoai = new DienThoai(banDau);

        for (int i = 0; i < 10; i++) {
            dienThoai.sac();
        }

        assertEquals(Chargeable.PIN_TOI_DA, dienThoai.phanTramPin());
    }

    // --- kết nối ----------------------------------------------------------

    @Test
    void banDauChuaKetNoi() {
        assertFalse(new DienThoai(50).dangKetNoi());
    }

    @Test
    void ketNoiRoiThiDangKetNoiTraTrue() {
        DienThoai dienThoai = new DienThoai(50);

        dienThoai.ketNoi();

        assertTrue(dienThoai.dangKetNoi());
    }

    @Test
    void ngatKetNoiRoiThiDangKetNoiTraFalse() {
        DienThoai dienThoai = new DienThoai(50);

        dienThoai.ketNoi();
        dienThoai.ngatKetNoi();

        assertFalse(dienThoai.dangKetNoi());
    }

    @Test
    void goiKetNoiNhieuLanVanChiLaDangKetNoi() {
        DienThoai dienThoai = new DienThoai(50);

        dienThoai.ketNoi();
        dienThoai.ketNoi();

        assertTrue(dienThoai.dangKetNoi());
    }

    // --- hai năng lực độc lập ---------------------------------------------

    @Test
    void sacKhongAnhHuongKetNoi() {
        DienThoai dienThoai = new DienThoai(10);
        dienThoai.ketNoi();

        dienThoai.sac();

        assertTrue(dienThoai.dangKetNoi(), "sạc không được đụng vào trạng thái kết nối");
        assertEquals(30, dienThoai.phanTramPin());
    }

    @Test
    void ketNoiKhongAnhHuongPin() {
        DienThoai dienThoai = new DienThoai(10);

        dienThoai.ketNoi();
        dienThoai.ngatKetNoi();

        assertEquals(10, dienThoai.phanTramPin(), "kết nối không được đụng vào mức pin");
    }

    /**
     * Cùng một object nhìn qua hai interface khác nhau - thứ {@code extends} không làm được
     * vì một class chỉ có đúng một cha.
     */
    @Test
    void motObjectDungDuocOCaHaiVaiTro() {
        DienThoai dienThoai = new DienThoai(50);

        Chargeable nhuThietBiSacDuoc = dienThoai;
        Connectable nhuThietBiKetNoiDuoc = dienThoai;

        nhuThietBiSacDuoc.sac();
        nhuThietBiKetNoiDuoc.ketNoi();

        assertEquals(70, dienThoai.phanTramPin());
        assertTrue(dienThoai.dangKetNoi());
    }

    // --- khởi tạo ---------------------------------------------------------

    @ParameterizedTest(name = "khởi tạo {0} bị từ chối")
    @ValueSource(ints = {-1, -50, 101, 200})
    void mucPinKhoiTaoNgoaiKhoangThiNem(int banDau) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new DienThoai(banDau));

        assertTrue(ex.getMessage().contains(String.valueOf(banDau)),
                "message phải nêu giá trị sai, nhận được: " + ex.getMessage());
    }

    // --- toString ---------------------------------------------------------

    @Test
    void toStringNeuCaPinVaTrangThaiKetNoi() {
        DienThoai dienThoai = new DienThoai(42);
        dienThoai.ketNoi();

        String mota = dienThoai.toString();

        assertTrue(mota.contains("42"), "thiếu mức pin: " + mota);
        assertTrue(mota.contains("true"), "thiếu trạng thái kết nối: " + mota);
    }
}
