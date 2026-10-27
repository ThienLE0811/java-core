package com.javacore.practice.javacore33.oop.thietbi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 10 - pin dự phòng: chỉ sạc được, không kết nối.
 *
 * <p>Giá trị kỳ vọng viết thẳng con số (50 sạc một nhịp ra 55) chứ không viết lại
 * {@code pin + TOC_DO_SAC}. Cùng lý do với bài 9: test chép lại công thức của code thì code
 * sai ở đâu, test sai y hệt ở đó và vẫn pass.
 */
class PinDuPhongTest {

    // --- sạc --------------------------------------------------------------

    @ParameterizedTest(name = "pin {0} sạc một nhịp -> {1}")
    @CsvSource({
            "0,   5",
            "50,  55",
            "94,  99",
            "95,  100"
    })
    void moiNhipSacTangNamPhanTram(int banDau, int mongDoi) {
        PinDuPhong pin = new PinDuPhong(banDau);

        pin.sac();

        assertEquals(mongDoi, pin.phanTramPin());
    }

    @Test
    void sacNhieuLanThiCongDon() {
        PinDuPhong pin = new PinDuPhong(0);

        pin.sac();
        pin.sac();
        pin.sac();

        assertEquals(15, pin.phanTramPin(), "trạng thái phải được giữ qua từng lần gọi");
    }

    @ParameterizedTest(name = "pin {0} sạc nhiều lần không vượt 100")
    @ValueSource(ints = {96, 97, 98, 99, 100})
    void khongBaoGioVuotMucToiDa(int banDau) {
        PinDuPhong pin = new PinDuPhong(banDau);

        for (int i = 0; i < 10; i++) {
            pin.sac();
        }

        assertEquals(Chargeable.PIN_TOI_DA, pin.phanTramPin());
    }

    @Test
    void daDayThiSacTiepKhongDoiGi() {
        PinDuPhong pin = new PinDuPhong(Chargeable.PIN_TOI_DA);

        pin.sac();

        assertEquals(Chargeable.PIN_TOI_DA, pin.phanTramPin());
        assertTrue(pin.dayPin());
    }

    // --- khởi tạo ---------------------------------------------------------

    @ParameterizedTest(name = "khởi tạo {0} thì phanTramPin() trả {0}")
    @ValueSource(ints = {0, 1, 50, 100})
    void phanTramPinTraDungMucKhoiTao(int banDau) {
        assertEquals(banDau, new PinDuPhong(banDau).phanTramPin());
    }

    @ParameterizedTest(name = "khởi tạo {0} bị từ chối")
    @ValueSource(ints = {-1, -50, 101, 200})
    void mucPinKhoiTaoNgoaiKhoangThiNem(int banDau) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new PinDuPhong(banDau));

        assertTrue(ex.getMessage().contains(String.valueOf(banDau)),
                "message phải nêu giá trị sai, nhận được: " + ex.getMessage());
    }

    // --- toString ---------------------------------------------------------

    @Test
    void toStringNeuMucPinHienTai() {
        assertTrue(new PinDuPhong(42).toString().contains("42"));
    }
}
