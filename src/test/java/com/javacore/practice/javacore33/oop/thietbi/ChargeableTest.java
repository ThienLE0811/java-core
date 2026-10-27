package com.javacore.practice.javacore33.oop.thietbi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 10 - phần dùng chung của {@link Chargeable}: {@code sacTatCa}, {@code dayPin} và
 * {@code kiemTraPhanTram}.
 *
 * <p>Trọng tâm là <em>đa hình</em>: {@link Chargeable#sacTatCa(Chargeable[])} gọi đúng một
 * dòng {@code sac()} nhưng mỗi loại thiết bị nhảy một mức khác nhau. Test
 * {@link #sacDuocCaLoaiThietBiChuaTungTonTai()} chứng minh method này không hỏi "mày là
 * thiết bị gì" - thêm loại mới không phải sửa một dòng nào.
 *
 * <p>Khác bài 9: ở đó so sánh {@code double} nên cần {@code DELTA}. Ở đây phần trăm pin là
 * {@code int}, so bằng tuyệt đối được.
 */
class ChargeableTest {

    // --- sacTatCa: trường hợp hợp lệ -------------------------------------

    @Test
    void mangRongThiKhongLamGi() {
        Chargeable.sacTatCa(new Chargeable[0]);   // không ném là đạt
    }

    /**
     * Cùng một dòng {@code sac()} trong vòng lặp, hai kết quả khác nhau - điện thoại sạc
     * nhanh hơn pin dự phòng. Đây là bài học của bài 10 viết dưới dạng test.
     */
    @Test
    void moiLoaiThietBiSacTheoTocDoRieng() {
        DienThoai dienThoai = new DienThoai(50);
        PinDuPhong pin = new PinDuPhong(50);

        Chargeable.sacTatCa(new Chargeable[]{dienThoai, pin});

        assertEquals(70, dienThoai.phanTramPin());
        assertEquals(55, pin.phanTramPin());
    }

    @Test
    void sacDungMotNhipChuKhongSacDay() {
        PinDuPhong pin = new PinDuPhong(0);

        Chargeable.sacTatCa(new Chargeable[]{pin});

        assertEquals(5, pin.phanTramPin(), "sacTatCa chỉ sạc một nhịp, không sạc đến đầy");
    }

    /**
     * Một loại thiết bị chưa từng tồn tại lúc {@link Chargeable#sacTatCa(Chargeable[])} được
     * viết, tạo bằng anonymous class ngay tại đây. Nó vẫn được sạc, vì method chỉ gọi
     * {@code sac()} qua interface chứ không kiểm tra kiểu cụ thể.
     *
     * <p>Nếu {@code sacTatCa} có {@code instanceof} hay {@code switch} theo loại thiết bị thì
     * test này gãy.
     */
    @Test
    void sacDuocCaLoaiThietBiChuaTungTonTai() {
        Chargeable thietBiLa = new Chargeable() {
            private int pin = 10;

            @Override
            public int phanTramPin() {
                return pin;
            }

            @Override
            public void sac() {
                pin = Math.min(pin + 1, PIN_TOI_DA);
            }
        };

        Chargeable.sacTatCa(new Chargeable[]{thietBiLa});

        assertEquals(11, thietBiLa.phanTramPin());
    }

    /** Thiết bị đã đầy không bị sạc quá - {@code sacTatCa} tin thiết bị tự chặn trần. */
    @Test
    void thietBiDaDayThiDungYen() {
        DienThoai dienThoai = new DienThoai(Chargeable.PIN_TOI_DA);

        Chargeable.sacTatCa(new Chargeable[]{dienThoai});

        assertEquals(Chargeable.PIN_TOI_DA, dienThoai.phanTramPin());
    }

    // --- sacTatCa: trường hợp lỗi ----------------------------------------

    @Test
    void mangNullThiNem() {
        assertThrows(NullPointerException.class, () -> Chargeable.sacTatCa(null));
    }

    @Test
    void phanTuNullThiNemKemViTri() {
        Chargeable[] danhSach = {new DienThoai(10), null, new PinDuPhong(10)};

        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> Chargeable.sacTatCa(danhSach));

        assertTrue(ex.getMessage().contains("1"),
                "message phải chỉ ra vị trí phần tử null, nhận được: " + ex.getMessage());
    }

    /** Phần tử null ở giữa thì các phần tử trước đó đã kịp được sạc - ghi lại hành vi thật. */
    @Test
    void nemGiuaChungThiPhanTuTruocDoDaDuocSac() {
        DienThoai dienThoai = new DienThoai(50);

        assertThrows(NullPointerException.class,
                () -> Chargeable.sacTatCa(new Chargeable[]{dienThoai, null}));

        assertEquals(70, dienThoai.phanTramPin());
    }

    // --- dayPin (default method) -----------------------------------------

    @Test
    void chuaDayThiDayPinTraFalse() {
        assertFalse(new PinDuPhong(99).dayPin());
    }

    @Test
    void dungMucToiDaThiDayPinTraTrue() {
        assertTrue(new PinDuPhong(Chargeable.PIN_TOI_DA).dayPin());
    }

    /**
     * Pin vượt quá {@value Chargeable#PIN_TOI_DA} vẫn tính là đầy.
     *
     * <p>Không lớp nào trong package này để pin vượt trần, nhưng {@code dayPin()} dùng
     * {@code >=} chính là để phòng một lớp cài {@code sac()} sai. Nếu ai đó đổi thành
     * {@code ==}, test này đỏ.
     */
    @Test
    void vuotMucToiDaVanTinhLaDay() {
        Chargeable pinLoi = new Chargeable() {
            @Override
            public int phanTramPin() {
                return PIN_TOI_DA + 50;
            }

            @Override
            public void sac() {
                // không cần cho test này
            }
        };

        assertTrue(pinLoi.dayPin());
    }

    // --- kiemTraPhanTram --------------------------------------------------

    @ParameterizedTest(name = "mức pin {0} hợp lệ")
    @ValueSource(ints = {0, 1, 50, 99, 100})
    void mucPinHopLeThiTraVeChinhNo(int giaTri) {
        assertEquals(giaTri, Chargeable.kiemTraPhanTram(giaTri));
    }

    @ParameterizedTest(name = "mức pin {0} bị từ chối")
    @ValueSource(ints = {-1, -100, 101, 1000, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void mucPinNgoaiKhoangThiNem(int giaTri) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Chargeable.kiemTraPhanTram(giaTri));

        assertTrue(ex.getMessage().contains(String.valueOf(giaTri)),
                "message phải nêu giá trị sai, nhận được: " + ex.getMessage());
    }
}
