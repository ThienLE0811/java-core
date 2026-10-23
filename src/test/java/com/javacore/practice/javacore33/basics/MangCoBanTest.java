package com.javacore.practice.javacore33.basics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Bài 3 - Đảo ngược mảng.
 *
 * <p>{@code daoNguoc} được chọn kiểu trả về {@code int[]} (bản sao) thay vì sửa tại chỗ,
 * nên test phải khẳng định cả hai điều: kết quả đúng VÀ mảng gốc không bị đụng tới.
 */
class MangCoBanTest {

    @Test
    void daoNguocMangDoDaiLe() {
        assertArrayEquals(new int[] {5, 4, 3, 2, 1}, MangCoBan.daoNguoc(new int[] {1, 2, 3, 4, 5}));
    }

    @Test
    void daoNguocMangDoDaiChan() {
        assertArrayEquals(new int[] {4, 3, 2, 1}, MangCoBan.daoNguoc(new int[] {1, 2, 3, 4}));
    }

    /**
     * Mảng chứa số 0 thật ở cuối. Nếu phần tử cuối không được gán, Java để lại giá trị
     * mặc định 0 - trùng với dữ liệu hợp lệ, nên bug sẽ trốn thoát nếu chỉ test mảng kiểu này.
     * Cặp test dưới đây cố tình dùng dữ liệu không chứa số 0 để chặn đường trốn đó.
     */
    @Test
    void daoNguocMangCoSoAmVaSoTrung() {
        assertArrayEquals(new int[] {7, -3, 7, 10}, MangCoBan.daoNguoc(new int[] {10, 7, -3, 7}));
    }

    @Test
    void mangMotPhanTuGiuNguyen() {
        assertArrayEquals(new int[] {42}, MangCoBan.daoNguoc(new int[] {42}));
    }

    @Test
    void mangRongTraVeMangRong() {
        assertArrayEquals(new int[] {}, MangCoBan.daoNguoc(new int[] {}));
    }

    @Test
    void khongSuaMangGoc() {
        int[] goc = {1, 2, 3};
        int[] ketQua = MangCoBan.daoNguoc(goc);

        assertArrayEquals(new int[] {1, 2, 3}, goc, "Mang goc bi sua - method nay phai tra ve ban sao");
        assertNotSame(goc, ketQua, "Phai la mang moi, khong duoc tra ve chinh mang goc");
    }

    /**
     * Property test: đảo hai lần phải quay về đúng mảng ban đầu. Bắt được cả lỗi lệch
     * chỉ số lẫn lỗi bỏ sót phần tử mà không cần liệt kê tay từng trường hợp.
     */
    @Test
    void daoNguocHaiLanTraVeMangBanDau() {
        for (int n = 0; n <= 10; n++) {
            int[] goc = new int[n];
            for (int i = 0; i < n; i++) {
                goc[i] = i + 1;
            }
            assertArrayEquals(goc, MangCoBan.daoNguoc(MangCoBan.daoNguoc(goc)), "Lech nhau tai do dai n = " + n);
        }
    }

    // --- demChanLe ------------------------------------------------------

    @Test
    void demDungMangTronLan() {
        var ketQua = MangCoBan.demChanLe(new int[] {1, 2, 3, 4, 5});

        assertEquals(2, ketQua.tongSoChan());
        assertEquals(3, ketQua.tongSoLe());
    }

    /**
     * Bẫy kinh điển: viết {@code list[i] % 2 == 1} để nhận số lẻ. Trong Java (và C, khác Python),
     * phép {@code %} giữ dấu của toán hạng trái, nên {@code -3 % 2} ra {@code -1} chứ không phải 1
     * - mọi số âm lẻ sẽ bị đếm nhầm sang chẵn. Test này chốt hành vi đó lại.
     */
    @Test
    void demDungKhiCoSoAm() {
        var ketQua = MangCoBan.demChanLe(new int[] {-3, -2, -1, 0, 7});

        assertEquals(2, ketQua.tongSoChan(), "-2 va 0 la so chan");
        assertEquals(3, ketQua.tongSoLe(), "-3, -1 va 7 la so le");
    }

    @Test
    void mangRongDemRaKhongKhong() {
        var ketQua = MangCoBan.demChanLe(new int[] {});

        assertEquals(0, ketQua.tongSoChan());
        assertEquals(0, ketQua.tongSoLe());
    }

    @Test
    void mangToanChanVaMangToanLe() {
        var toanChan = MangCoBan.demChanLe(new int[] {2, 4, 6, 8});
        assertEquals(4, toanChan.tongSoChan());
        assertEquals(0, toanChan.tongSoLe());

        var toanLe = MangCoBan.demChanLe(new int[] {1, 3, 5});
        assertEquals(0, toanLe.tongSoChan());
        assertEquals(3, toanLe.tongSoLe());
    }

    @Test
    void tongHaiNhomLuonBangDoDaiMang() {
        int[] list = {0, -1, 15, -20, 7, 8, Integer.MIN_VALUE, Integer.MAX_VALUE};
        var ketQua = MangCoBan.demChanLe(list);

        assertEquals(list.length, ketQua.tongSoChan() + ketQua.tongSoLe(), "Co phan tu bi bo sot hoac dem hai lan");
    }
}
