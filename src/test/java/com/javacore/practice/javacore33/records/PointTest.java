package com.javacore.practice.javacore33.records;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointTest {

    private static final double SAI_SO = 1e-9;

    @ParameterizedTest
    @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void toaDoKhongHuuHanThiNem(double xau) {
        assertThrows(IllegalArgumentException.class, () -> new Point(xau, 0));
        assertThrows(IllegalArgumentException.class, () -> new Point(0, xau));
    }

    @Test
    void equalsTheoGiaTriChuKhongTheoThamChieu() {
        Point a = new Point(1, 2);
        Point b = new Point(1, 2);

        assertNotSame(a, b);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        // HashSet gộp hai phần tử bằng nhau. Set.of thì NÉM IllegalArgumentException khi
        // phát hiện trùng - nó không nhận duplicate ngay từ lúc dựng.
        assertEquals(1, new HashSet<>(List.of(a, b)).size());
    }

    @Test
    void equalsDungDoubleCompareChuKhongDungToanTuBang() {
        // 0.0 == -0.0 là true, nhưng equals sinh tự động của record thì không.
        assertNotEquals(new Point(0.0, 0.0), new Point(-0.0, 0.0));
    }

    @Test
    void toStringCoTenRecordVaTungComponent() {
        assertEquals("Point[x=3.0, y=4.0]", new Point(3, 4).toString());
    }

    @Test
    void khoangCach() {
        assertEquals(5, new Point(3, 4).khoangCachToiGoc(), SAI_SO);
        assertEquals(5, new Point(0, 1).khoangCachToi(new Point(3, 5)), SAI_SO);
        assertEquals(0, Point.GOC.khoangCachToiGoc(), SAI_SO);
    }

    @Test
    void khoangCachToiNullThiNemNpe() {
        assertThrows(NullPointerException.class, () -> new Point(1, 1).khoangCachToi(null));
    }

    @Test
    void tinhTienTraVeObjectMoiVaKhongDungToiObjectCu() {
        Point goc = new Point(3, 4);
        Point moi = goc.tinhTien(1, -1);

        assertEquals(new Point(4, 3), moi);
        assertEquals(new Point(3, 4), goc);
        assertNotSame(goc, moi);
    }

    @Test
    void tinhTienVanBiChanViDiQuaConstructor() {
        // tinhTien không tự validate, nhưng nó gọi new Point() nên compact constructor vẫn chạy.
        assertThrows(IllegalArgumentException.class,
                () -> new Point(1, 1).tinhTien(Double.NaN, 0));
    }

    @Test
    void cuaGiongHetConstructor() {
        assertEquals(new Point(1, 2), Point.cua(1, 2));
    }

    @Test
    void compareToTheoKhoangCachToiGoc() {
        Point gan = new Point(1, 0);
        Point xa = new Point(3, 4);

        assertTrue(gan.compareTo(xa) < 0);
        assertTrue(xa.compareTo(gan) > 0);
        assertEquals(0, gan.compareTo(new Point(0, 1)));
    }

    /**
     * Ghi lại một giới hạn đã biết: thứ tự tự nhiên không nhất quán với equals, nên TreeSet
     * coi hai điểm cùng khoảng cách là trùng nhau và nuốt mất cái thứ hai. HashSet thì không.
     */
    @Test
    void thuTuKhongNhatQuanVoiEqualsNenTreeSetNuotPhanTu() {
        Point a = new Point(1, 0);
        Point b = new Point(0, 1);

        assertNotEquals(a, b);
        assertEquals(0, a.compareTo(b));
        assertEquals(1, new TreeSet<>(Set.of(a, b)).size());
        assertEquals(2, Set.of(a, b).size());
    }
}
