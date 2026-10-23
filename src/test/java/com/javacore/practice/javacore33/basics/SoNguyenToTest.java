package com.javacore.practice.javacore33.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 2 - Số nguyên tố.
 *
 * <p>Các giá trị mong đợi trong file này đều được kiểm chứng bằng sàng Eratosthenes
 * chạy độc lập, không lấy theo trí nhớ: tổng các số nguyên tố đến 100 là 1060,
 * có đúng 25 số nguyên tố không vượt quá 100, tổng đến 10 là 17.
 */
class SoNguyenToTest {

    // --- laSoNguyenTo ---------------------------------------------------

    @ParameterizedTest(name = "{0} la so nguyen to")
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 97, 7919})
    void nhanDienDungSoNguyenTo(int num) {
        assertTrue(SoNguyenTo.laSoNguyenTo(num), num + " la so nguyen to");
    }

    /**
     * 9, 25, 49, 121 là bình phương của số nguyên tố - bẫy của vòng lặp viết
     * {@code uoc < Math.sqrt(num)} thay vì {@code <=}: ước duy nhất nằm đúng tại
     * {@code √num} sẽ bị bỏ qua. 91 = 7 x 13 bẫy kiểu chỉ chia thử vài ước nhỏ.
     */
    @ParameterizedTest(name = "{0} khong phai so nguyen to")
    @ValueSource(ints = {4, 6, 8, 9, 15, 21, 25, 27, 49, 91, 121, 7917, 1_000_000})
    void nhanDienDungHopSo(int num) {
        assertFalse(SoNguyenTo.laSoNguyenTo(num), num + " la hop so");
    }

    @ParameterizedTest(name = "{0} nho hon 2 nen khong phai so nguyen to")
    @ValueSource(ints = {Integer.MIN_VALUE, -7, -1, 0, 1})
    void soNhoHonHaiKhongPhaiSoNguyenTo(int num) {
        assertFalse(SoNguyenTo.laSoNguyenTo(num));
    }

    // --- lietKeSoNguyenTo -----------------------------------------------

    @Test
    void lietKeDenHaiMuoi() {
        assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19), SoNguyenTo.lietKeSoNguyenTo(20));
    }

    @Test
    void lietKeBaoGomCaNumNeuNumLaSoNguyenTo() {
        assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19), SoNguyenTo.lietKeSoNguyenTo(19));
        assertEquals(List.of(2, 3, 5, 7, 11, 13, 17), SoNguyenTo.lietKeSoNguyenTo(18));
    }

    @Test
    void lietKeDenHaiChiCoMotPhanTu() {
        assertEquals(List.of(2), SoNguyenTo.lietKeSoNguyenTo(2));
    }

    @ParameterizedTest(name = "num = {0} tra ve list rong")
    @ValueSource(ints = {Integer.MIN_VALUE, -5, 0, 1})
    void numNhoHonHaiTraVeListRong(int num) {
        assertEquals(List.of(), SoNguyenTo.lietKeSoNguyenTo(num));
    }

    @Test
    void coDungHaiMuoiLamSoNguyenToKhongVuotQuaMotTram() {
        assertEquals(25, SoNguyenTo.lietKeSoNguyenTo(100).size());
    }

    @Test
    void ketQuaLietKeTheoThuTuTangDan() {
        List<Integer> ketQua = SoNguyenTo.lietKeSoNguyenTo(500);
        for (int i = 1; i < ketQua.size(); i++) {
            assertTrue(
                    ketQua.get(i - 1) < ketQua.get(i),
                    "Khong tang dan tai vi tri " + i + ": " + ketQua.get(i - 1) + " >= " + ketQua.get(i));
        }
    }

    // --- tongSoNguyenTo -------------------------------------------------

    @ParameterizedTest(name = "tong cac so nguyen to den {0} = {1}")
    @CsvSource({
            "-5,  0",
            "0,   0",
            "1,   0",
            "2,   2",
            "10,  17",
            "100, 1060"
    })
    void tinhDungTongSoNguyenTo(int num, long mongDoi) {
        assertEquals(mongDoi, SoNguyenTo.tongSoNguyenTo(num));
    }

    /**
     * Tổng đến 300000 là 3709507114, lớn hơn {@link Integer#MAX_VALUE} (2147483647).
     * Nếu kiểu trả về bị đổi về {@code int}, phép cộng tràn im lặng và test này bắt được ngay.
     */
    @Test
    void tongKhongBiTranKhiVuotNguongInt() {
        assertEquals(3_709_507_114L, SoNguyenTo.tongSoNguyenTo(300_000));
    }

    // --- Tính nhất quán giữa ba method ----------------------------------

    /**
     * Ba method phải kể cùng một câu chuyện: mọi số mà {@code laSoNguyenTo} nói "đúng"
     * đều phải có mặt trong danh sách liệt kê, và ngược lại.
     */
    @Test
    void lietKeKhopVoiLaSoNguyenTo() {
        int num = 200;
        List<Integer> daLietKe = SoNguyenTo.lietKeSoNguyenTo(num);
        for (int i = 2; i <= num; i++) {
            assertEquals(
                    SoNguyenTo.laSoNguyenTo(i),
                    daLietKe.contains(i),
                    "Hai method lech nhau tai i = " + i);
        }
    }

    @Test
    void tongBangTongCacPhanTuDaLietKe() {
        for (int num = -1; num <= 100; num++) {
            long mongDoi = SoNguyenTo.lietKeSoNguyenTo(num).stream().mapToLong(Integer::longValue).sum();
            assertEquals(mongDoi, SoNguyenTo.tongSoNguyenTo(num), "Lech nhau tai num = " + num);
        }
    }

    @Test
    void tongBangTongCacPhanTuDaLietKe2() {
        for (int num = -1; num <= 100; num++) {
            long mongDoi = SoNguyenTo.lietKeSoNguyenTo(num).stream().mapToLong(Integer::longValue).sum();
            assertEquals(mongDoi, SoNguyenTo.tongSoNguyenTo(num), "Lech nhau tai num = " + num);
        }
    }
}
