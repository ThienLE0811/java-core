package com.javacore.practice.javacore33.streams.validator;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    private static final Validator<String> KHONG_RONG = value -> !value.isBlank();
    private static final Validator<String> NGAN = value -> value.length() <= 5;

    // --- and ------------------------------------------------------------

    @Test
    void andDungKhiCaHaiCungDung() {
        assertTrue(KHONG_RONG.and(NGAN).validate("abc"));
    }

    @Test
    void andSaiKhiMotTrongHaiSai() {
        assertFalse(KHONG_RONG.and(NGAN).validate("   "));
        assertFalse(KHONG_RONG.and(NGAN).validate("chuoi-rat-dai"));
    }

    @Test
    void andKhongChayVePhaiKhiVeTraiDaSai() {
        AtomicInteger soLanGoi = new AtomicInteger();
        Validator<String> demSoLanGoi = value -> {
            soLanGoi.incrementAndGet();
            return true;
        };

        Validator<String> luonSai = value -> false;
        luonSai.and(demSoLanGoi).validate("abc");

        assertEquals(0, soLanGoi.get(), "&& phải short-circuit, không gọi validator thứ hai");
    }

    @Test
    void andNemNpeNgayLucGhepChuKhongDoiToiLucValidate() {
        // Nổ tại dòng ghép thì stack trace chỉ thẳng chỗ sai; để trong lambda thì lỗi
        // lùi tới tận lúc gọi validate, có khi ở file khác.
        assertThrows(NullPointerException.class, () -> KHONG_RONG.and(null));
    }

    // --- or -------------------------------------------------------------

    @Test
    void orDungKhiItNhatMotVeDung() {
        assertTrue(KHONG_RONG.or(NGAN).validate("   "));
        assertTrue(KHONG_RONG.or(NGAN).validate("chuoi-rat-dai"));
    }

    @Test
    void orSaiKhiCaHaiCungSai() {
        Validator<String> luonSai = value -> false;
        assertFalse(luonSai.or(luonSai).validate("abc"));
    }

    @Test
    void orKhongChayVePhaiKhiVeTraiDaDung() {
        AtomicInteger soLanGoi = new AtomicInteger();
        Validator<String> demSoLanGoi = value -> {
            soLanGoi.incrementAndGet();
            return true;
        };

        Validator<String> luonDung = value -> true;
        luonDung.or(demSoLanGoi).validate("abc");

        assertEquals(0, soLanGoi.get(), "|| phải short-circuit, không gọi validator thứ hai");
    }

    @Test
    void orNemNpeNgayLucGhep() {
        assertThrows(NullPointerException.class, () -> KHONG_RONG.or(null));
    }

    // --- negate ---------------------------------------------------------

    @Test
    void negateDaoKetQua() {
        assertFalse(KHONG_RONG.negate().validate("abc"));
        assertTrue(KHONG_RONG.negate().validate("   "));
    }

    @Test
    void negateHaiLanTroVeNhuCu() {
        assertTrue(KHONG_RONG.negate().negate().validate("abc"));
    }

    @Test
    void negateKhongBienExceptionThanhTrue() {
        Validator<String> luonNem = value -> {
            throw new IllegalStateException("cố tình");
        };
        assertThrows(IllegalStateException.class, () -> luonNem.negate().validate("abc"));
    }

    // --- ghép nhiều tầng -------------------------------------------------

    @Test
    void ghepNhieuTangVanTraVeValidatorDungNghia() {
        Validator<String> ketHop = KHONG_RONG.and(NGAN).or(value -> value.startsWith("vip-"));

        assertTrue(ketHop.validate("abc"));
        assertTrue(ketHop.validate("vip-khach-hang-rat-dai"));
        assertFalse(ketHop.validate("chuoi-rat-dai"));
    }

    @Test
    void ghepVoiEmailValidatorThatSu() {
        Validator<String> emailNoiBo = new EmailValidator().and(value -> value.endsWith(".io"));

        assertTrue(emailNoiBo.validate("thienlv@paraline.io"));
        assertFalse(emailNoiBo.validate("a@b.com"));
    }
}
