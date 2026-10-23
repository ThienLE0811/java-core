package com.javacore.practice.javacore33.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 4 - Tìm kiếm và sắp xếp mảng bằng {@link Arrays#sort(int[])} và
 * {@link Arrays#binarySearch(int[], int)}.
 *
 * <p>Hai điều khoản trong hợp đồng của lớp này quyết định cách viết test:
 * {@code sapXep} trả bản sao và không đụng mảng gốc; {@code timKiem} trả index tính trên
 * mảng ĐÃ SẮP XẾP, hoặc {@code -1} khi không tìm thấy.
 */
class TimKiemVaSapXepMangTest {

    // --- sapXep ---------------------------------------------------------

    @Test
    void sapXepTangDan() {
        assertArrayEquals(new int[] {1, 2, 5, 7, 9}, TimKiemVaSapXepMang.sapXep(new int[] {5, 2, 9, 1, 7}));
    }

    @Test
    void khongSuaMangGoc() {
        int[] goc = {5, 2, 9, 1, 7};
        int[] ketQua = TimKiemVaSapXepMang.sapXep(goc);

        assertArrayEquals(new int[] {5, 2, 9, 1, 7}, goc, "Arrays.sort sua tai cho - phai copy truoc khi sort");
        assertNotSame(goc, ketQua, "Phai la mang moi, khong duoc tra ve chinh mang goc");
    }

    @Test
    void mangDaSapXepSanThiGiuNguyen() {
        assertArrayEquals(new int[] {1, 2, 3}, TimKiemVaSapXepMang.sapXep(new int[] {1, 2, 3}));
    }

    @Test
    void sapXepMangRongVaMangMotPhanTu() {
        assertArrayEquals(new int[] {}, TimKiemVaSapXepMang.sapXep(new int[] {}));
        assertArrayEquals(new int[] {42}, TimKiemVaSapXepMang.sapXep(new int[] {42}));
    }

    /**
     * Số âm, số 0, phần tử trùng và hai biên của {@code int} cùng lúc. Hai biên bắt được lỗi
     * so sánh kiểu {@code a - b} (tràn số); {@code Arrays.sort(int[])} không mắc lỗi này,
     * nhưng test giữ nguyên hành vi đó nếu sau này ai đó thay bằng comparator tự viết.
     */
    @Test
    void sapXepSoAmSoTrungVaBienCuaInt() {
        int[] ketQua = TimKiemVaSapXepMang.sapXep(
                new int[] {3, -1, 3, 0, Integer.MAX_VALUE, -10, Integer.MIN_VALUE});

        assertArrayEquals(
                new int[] {Integer.MIN_VALUE, -10, -1, 0, 3, 3, Integer.MAX_VALUE},
                ketQua);
    }

    // --- timKiem --------------------------------------------------------

    /**
     * Test quan trọng nhất của hợp đồng: trong mảng gốc, số 5 nằm ở index 0; sau khi sắp xếp
     * thành {@code {1, 2, 5, 7, 9}} thì nó ở index 2. Giá trị mong đợi là 2 - nếu ai đó
     * sửa {@code timKiem} thành trả index của mảng gốc, test này sẽ bắt được.
     */
    @Test
    void traVeIndexTinhTrenMangDaSapXep() {
        assertEquals(2, TimKiemVaSapXepMang.timKiem(new int[] {5, 2, 9, 1, 7}, 5));
        assertEquals(0, TimKiemVaSapXepMang.timKiem(new int[] {5, 2, 9, 1, 7}, 1));
        assertEquals(4, TimKiemVaSapXepMang.timKiem(new int[] {5, 2, 9, 1, 7}, 9));
    }

    /**
     * {@code Arrays.binarySearch} không tìm thấy thì trả {@code -(vị trí chèn) - 1}, tức là
     * -1, -3, -4... tùy chỗ chèn chứ không phải luôn luôn -1. Cách viết
     * {@code if (viTri == -1)} sẽ lọt các giá trị còn lại; ba target dưới đây rơi vào ba vị trí
     * chèn khác nhau nên khóa được lỗi đó.
     */
    @ParameterizedTest(name = "khong tim thay {0} -> -1")
    @ValueSource(ints = {0, 5, 100, -8, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void khongTimThayTraVeAmMot(int target) {
        assertEquals(-1, TimKiemVaSapXepMang.timKiem(new int[] {1, 3, 7, 9}, target));
    }

    @Test
    void timTrongMangRongLuonTraVeAmMot() {
        assertEquals(-1, TimKiemVaSapXepMang.timKiem(new int[] {}, 1));
    }

    @Test
    void timKiemKhongSuaMangGoc() {
        int[] goc = {5, 2, 9, 1, 7};
        TimKiemVaSapXepMang.timKiem(goc, 9);

        assertArrayEquals(new int[] {5, 2, 9, 1, 7}, goc, "timKiem khong duoc lam xao tron mang cua caller");
    }

    /**
     * Mảng có phần tử trùng: {@code binarySearch} không cam kết trả về index nào trong nhóm
     * bằng nhau, nên test chỉ khẳng định điều thực sự được bảo đảm - index trả về trỏ đúng
     * vào một ô mang giá trị cần tìm. Ép một con số cụ thể ở đây là test quá chặt.
     */
    @Test
    void mangCoPhanTuTrungTraVeMotTrongCacViTriHopLe() {
        int[] goc = {4, 1, 4, 4, 2};
        int viTri = TimKiemVaSapXepMang.timKiem(goc, 4);

        assertEquals(4, TimKiemVaSapXepMang.sapXep(goc)[viTri], "Index tra ve khong tro vao phan tu bang 4");
    }

    /**
     * Property test: mọi phần tử có trong mảng đều phải tìm thấy, và index trả về phải trỏ
     * đúng giá trị đó trong mảng đã sắp xếp.
     */
    @Test
    void timThayMoiPhanTuCoTrongMang() {
        int[] goc = {15, -3, 0, 42, 7, -100, 8};
        int[] daSapXep = TimKiemVaSapXepMang.sapXep(goc);

        for (int phanTu : goc) {
            int viTri = TimKiemVaSapXepMang.timKiem(goc, phanTu);
            assertTrue(viTri >= 0, "Khong tim thay " + phanTu + " du no nam trong mang");
            assertEquals(phanTu, daSapXep[viTri], "Index " + viTri + " khong tro vao " + phanTu);
        }
    }
}
