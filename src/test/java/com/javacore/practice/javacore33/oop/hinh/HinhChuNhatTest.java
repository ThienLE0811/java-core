package com.javacore.practice.javacore33.oop.hinh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 9 - hình chữ nhật.
 *
 * <p>Đáng chú ý là cặp test {@link #chieuDaiKhongHopLeThiNem(double)} và
 * {@link #chieuRongKhongHopLeThiNem(double)}: chúng kiểm message có <em>phân biệt được</em>
 * tham số nào sai. Một message chung chung cho cả hai vẫn làm code chạy đúng, nhưng để lại
 * người đọc log với một câu không giúp được gì.
 */
class HinhChuNhatTest {

    private static final double DELTA = 1e-9;

    // --- diện tích -------------------------------------------------------

    @ParameterizedTest(name = "{0} x {1} -> {2}")
    @CsvSource({
            "3,    4,    12",
            "5,    5,    25",     // hình vuông cũng là hình chữ nhật
            "0.5,  0.5,  0.25",
            "1000, 1000, 1000000"
    })
    void dienTichDungVoiCacKichThuoc(double chieuDai, double chieuRong, double mongDoi) {
        assertEquals(mongDoi, new HinhChuNhat(chieuDai, chieuRong).dienTich(), DELTA);
    }

    @Test
    void doiChoDaiVaRongThiDienTichKhongDoi() {
        assertEquals(new HinhChuNhat(3, 7).dienTich(), new HinhChuNhat(7, 3).dienTich(), DELTA);
    }

    // --- validate --------------------------------------------------------

    @ParameterizedTest(name = "chiều dài {0} bị từ chối")
    @ValueSource(doubles = {-1, 0, Double.NaN, Double.POSITIVE_INFINITY})
    void chieuDaiKhongHopLeThiNem(double chieuDai) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhChuNhat(chieuDai, 5));

        assertTrue(ex.getMessage().contains("Chiều dài"),
                "message phải chỉ đúng vào chiều dài, nhận được: " + ex.getMessage());
    }

    @ParameterizedTest(name = "chiều rộng {0} bị từ chối")
    @ValueSource(doubles = {-1, 0, Double.NaN, Double.POSITIVE_INFINITY})
    void chieuRongKhongHopLeThiNem(double chieuRong) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhChuNhat(5, chieuRong));

        assertTrue(ex.getMessage().contains("Chiều rộng"),
                "message phải chỉ đúng vào chiều rộng, nhận được: " + ex.getMessage());
    }

    /**
     * Cả hai tham số cùng sai thì ném ở tham số <em>đầu tiên</em> - validate dừng ngay lần
     * sai đầu, không gom lỗi. Đủ dùng cho một constructor; gom lỗi là chuyện của tầng
     * validate form phía trên.
     */
    @Test
    void caHaiThamSoSaiThiBaoLoiThamSoDauTien() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhChuNhat(-1, -2));

        assertTrue(ex.getMessage().contains("Chiều dài"), ex.getMessage());
    }

    // --- toString --------------------------------------------------------

    @Test
    void toStringNeuDuocHaiCanh() {
        String ketQua = new HinhChuNhat(3, 4).toString();

        assertTrue(ketQua.contains("HinhChuNhat"), ketQua);
        assertTrue(ketQua.contains("3.0"), ketQua);
        assertTrue(ketQua.contains("4.0"), ketQua);
    }
}
