package com.javacore.practice.javacore33.oop.hinh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 9 - tam giác ba cạnh, diện tích theo công thức Heron.
 *
 * <p>Đây là hình duy nhất có ràng buộc <em>giữa các tham số</em>, không chỉ trên từng tham số
 * rời: ba số dương hợp lệ vẫn có thể không dựng được thành tam giác. Vì vậy phần test bất đẳng
 * thức tam giác chạy đủ <em>ba hoán vị</em> - mỗi hoán vị đánh vào một vế khác nhau của điều
 * kiện {@code a+b<=c || a+c<=b || b+c<=a}, bỏ sót một hoán vị là bỏ sót một nhánh chưa ai chạy.
 */
class HinhTamGiacTest {

    private static final double DELTA = 1e-9;

    // --- diện tích -------------------------------------------------------

    @ParameterizedTest(name = "tam giác {0}-{1}-{2} -> diện tích {3}")
    @CsvSource({
            "3, 4,  5,  6",                   // Pythagore, diện tích chẵn
            "6, 8,  10, 24",
            "5, 12, 13, 30",
            "2, 2,  2,  1.7320508075688772"   // tam giác đều cạnh 2 = √3
    })
    void dienTichDungTheoHeron(double a, double b, double c, double mongDoi) {
        assertEquals(mongDoi, new HinhTamGiac(a, b, c).dienTich(), DELTA);
    }

    @Test
    void doiThuTuBaCanhThiDienTichKhongDoi() {
        double chuan = new HinhTamGiac(3, 4, 5).dienTich();

        assertEquals(chuan, new HinhTamGiac(5, 3, 4).dienTich(), DELTA);
        assertEquals(chuan, new HinhTamGiac(4, 5, 3).dienTich(), DELTA);
    }

    // --- bất đẳng thức tam giác ------------------------------------------

    @ParameterizedTest(name = "{0}-{1}-{2} không khép thành tam giác")
    @CsvSource({
            "1,  2,  10",   // cạnh quá dài nằm ở vị trí c
            "10, 1,  2",    // ... ở vị trí a
            "2,  10, 1"     // ... ở vị trí b
    })
    void baCanhKhongKhepThanhTamGiacThiNem(double a, double b, double c) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhTamGiac(a, b, c));

        assertTrue(ex.getMessage().contains("tam giác"), ex.getMessage());
    }

    /**
     * Tổng hai cạnh <em>đúng bằng</em> cạnh thứ ba: ba đỉnh thẳng hàng. Heron sẽ trả
     * {@code 0.0} chứ không lỗi, nên nếu constructor dùng {@code >=} thay vì {@code >} thì
     * một "hình" có diện tích 0 lọt được vào mảng mà không ai hay.
     */
    @ParameterizedTest(name = "{0}-{1}-{2} thẳng hàng")
    @CsvSource({
            "1, 2, 3",
            "5, 5, 10"
    })
    void baDinhThangHangThiNem(double a, double b, double c) {
        assertThrows(IllegalArgumentException.class, () -> new HinhTamGiac(a, b, c));
    }

    // --- validate từng cạnh ----------------------------------------------

    @ParameterizedTest(name = "cạnh a = {0} bị từ chối")
    @ValueSource(doubles = {-1, 0, Double.NaN, Double.POSITIVE_INFINITY})
    void canhAKhongHopLeThiNem(double canhA) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhTamGiac(canhA, 4, 5));

        assertTrue(ex.getMessage().contains("Cạnh a"),
                "message phải chỉ đúng vào cạnh a, nhận được: " + ex.getMessage());
    }

    @ParameterizedTest(name = "cạnh b = {0} bị từ chối")
    @ValueSource(doubles = {-1, 0, Double.NaN})
    void canhBKhongHopLeThiNem(double canhB) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhTamGiac(3, canhB, 5));

        assertTrue(ex.getMessage().contains("Cạnh b"), ex.getMessage());
    }

    @ParameterizedTest(name = "cạnh c = {0} bị từ chối")
    @ValueSource(doubles = {-1, 0, Double.NaN})
    void canhCKhongHopLeThiNem(double canhC) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhTamGiac(3, 4, canhC));

        assertTrue(ex.getMessage().contains("Cạnh c"), ex.getMessage());
    }

    /**
     * {@code NaN} phải bị chặn ở bước kiểm từng cạnh, <em>trước</em> bất đẳng thức tam giác.
     * Lý do: mọi so sánh với {@code NaN} đều trả {@code false}, nên {@code a+b <= c} không
     * phát hiện được gì - tam giác NaN sẽ lọt và {@code dienTich()} trả {@code NaN}.
     */
    @Test
    void canhNaNBiChanTruocKhiToiBatDangThuc() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhTamGiac(Double.NaN, 4, 5));

        assertTrue(ex.getMessage().contains("Cạnh a"),
                "NaN phải bị chặn ở bước kiểm kích thước, không phải ở bất đẳng thức: "
                        + ex.getMessage());
    }

    // --- toString --------------------------------------------------------

    @Test
    void toStringNeuDuocBaCanh() {
        String ketQua = new HinhTamGiac(3, 4, 5).toString();

        assertTrue(ketQua.contains("HinhTamGiac"), ketQua);
        assertTrue(ketQua.contains("3.0"), ketQua);
        assertTrue(ketQua.contains("4.0"), ketQua);
        assertTrue(ketQua.contains("5.0"), ketQua);
    }
}
