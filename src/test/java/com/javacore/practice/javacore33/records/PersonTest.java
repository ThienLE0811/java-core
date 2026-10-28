package com.javacore.practice.javacore33.records;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonTest {

    @Test
    void taoDuocNguoiHopLe() {
        Person p = new Person("Thiên", 30, "thienlv@paraline.io");

        assertEquals("Thiên", p.name());
        assertEquals(30, p.age());
        assertEquals("thienlv@paraline.io", p.email());
    }

    @Test
    void compactConstructorChuanHoaNameVaEmail() {
        Person p = new Person("  Thiên  ", 30, "  ThienLV@Paraline.IO ");

        assertEquals("Thiên", p.name());
        assertEquals("thienlv@paraline.io", p.email());
    }

    @Test
    void chuanHoaLamHaiNguoiKhacInputTroThanhTrungNhau() {
        // Đây là lý do DTO chuẩn hoá ở constructor: hai request khác hoa/thường
        // không tạo ra hai user trùng.
        Person a = new Person("Thiên", 30, "A@B.COM");
        Person b = new Person("  Thiên  ", 30, "a@b.com");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals(1, new HashSet<>(List.of(a, b)).size());
    }

    @Test
    void nullLaLoiLapTrinhNenNemNpe() {
        assertThrows(NullPointerException.class, () -> new Person(null, 30, "a@b.com"));
        assertThrows(NullPointerException.class, () -> new Person("Thiên", 30, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    void nameRongSauKhiStripThiNem(String xau) {
        assertThrows(IllegalArgumentException.class, () -> new Person(xau, 30, "a@b.com"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 18, 150})
    void tuoiTrongBienLaHopLe(int tuoi) {
        assertEquals(tuoi, new Person("Thiên", tuoi, "a@b.com").age());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 151, Integer.MIN_VALUE, Integer.MAX_VALUE})
    void tuoiNgoaiBienThiNem(int tuoi) {
        assertThrows(IllegalArgumentException.class, () -> new Person("Thiên", tuoi, "a@b.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@b", "ab.com", "@b.com", "a@", "a@.com", "   "})
    void emailKhongHopLeThiNem(String xau) {
        assertThrows(IllegalArgumentException.class, () -> new Person("Thiên", 30, xau));
    }

    @Test
    void thongDiepLoiKemGiaTriThucDeDocLogDuocNgay() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Person("Thiên", 200, "a@b.com"));

        assertTrue(e.getMessage().contains("200"), e.getMessage());
    }

    @Test
    void cuaDungEmailMacDinhHopLe() {
        Person p = Person.cua("Thiên", 30);

        assertEquals("Thiên", p.name());
        assertFalse(p.email().isBlank());
    }

    @Test
    void laNguoiLonTaiBien() {
        assertFalse(Person.cua("Thiên", 17).laNguoiLon());
        assertTrue(Person.cua("Thiên", 18).laNguoiLon());
    }

    @Test
    void doiEmailTraVeObjectMoiVaKhongDungToiObjectCu() {
        Person goc = new Person("Thiên", 30, "a@b.com");
        Person moi = goc.doiEmail("c@d.com");

        assertEquals("c@d.com", moi.email());
        assertEquals("a@b.com", goc.email());
        assertNotSame(goc, moi);
    }

    @Test
    void doiEmailVanBiValidateViDiQuaConstructor() {
        Person goc = new Person("Thiên", 30, "a@b.com");

        assertThrows(IllegalArgumentException.class, () -> goc.doiEmail("hỏng"));
        assertThrows(NullPointerException.class, () -> goc.doiEmail(null));
    }

    @Test
    void recordDaKeThuaJavaLangRecordNenKhongExtendsDuocGiNua() {
        assertEquals(Record.class, Person.class.getSuperclass());
    }
}
