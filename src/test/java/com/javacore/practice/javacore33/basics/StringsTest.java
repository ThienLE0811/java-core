package com.javacore.practice.javacore33.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 5 - Xử lý chuỗi (Strings).
 *
 * <p>Kiểm chứng 3 bài toán xử lý chuỗi cơ bản:
 * <ul>
 *   <li>{@link Strings#demKyTu(String)}: đếm ký tự không phải khoảng trắng.</li>
 *   <li>{@link Strings#demTu(String)}: đếm số từ cách nhau bởi khoảng trắng.</li>
 *   <li>{@link Strings#laPalindrome(String)}: kiểm tra chuỗi đối xứng (bỏ qua hoa/thường và ký tự đặc biệt).</li>
 * </ul>
 */
class StringsTest {

    // --- demKyTu --------------------------------------------------------

    @Test
    void demKyTuChuoiBinhThuong() {
        assertEquals(5, Strings.demKyTu("hello"));
        assertEquals(10, Strings.demKyTu("hello world"));
    }

    @Test
    void demKyTuBoQuaTabVaXuongDong() {
        assertEquals(3, Strings.demKyTu("a\tb\nc"));
        assertEquals(4, Strings.demKyTu(" \t a b c d \n "));
    }

    @Test
    void demKyTuCacCaBien() {
        assertEquals(0, Strings.demKyTu(null));
        assertEquals(0, Strings.demKyTu(""));
        assertEquals(0, Strings.demKyTu("   \t  \n  "));
    }

    // --- demTu ----------------------------------------------------------

    @ParameterizedTest(name = "\"{0}\" -> {1} tu")
    @CsvSource({
            "'Hello world', 2",
            "'Java Core 33 bai', 4",
            "'One', 1",
            "'  Java   core   ', 2",
            "'One\tTwo\nThree', 3"
    })
    void demTuDung(String chuoi, int soTuMongDoi) {
        assertEquals(soTuMongDoi, Strings.demTu(chuoi));
    }

    @Test
    void demTuCacCaBien() {
        assertEquals(0, Strings.demTu(null));
        assertEquals(0, Strings.demTu(""));
        assertEquals(0, Strings.demTu("   \t  \n  "));
    }

    // --- laPalindrome ---------------------------------------------------

    @ParameterizedTest(name = "\"{0}\" la palindrome")
    @ValueSource(strings = {
            "racecar",
            "RaceCar",
            "A man a plan a canal Panama",
            "Was it a car or a cat I saw?",
            "No 'x' in Nixon",
            "Madam, I'm Adam",
            "12321",
            "0P0",
            "a",
            "",
            "   .,?! "
    })
    void chuoiDoiXungTraVeTrue(String chuoi) {
        assertTrue(Strings.laPalindrome(chuoi));
    }

    @ParameterizedTest(name = "\"{0}\" khong phai palindrome")
    @ValueSource(strings = {
            "hello",
            "race a car",
            "123456",
            "0P"
    })
    void chuoiKhongDoiXungTraVeFalse(String chuoi) {
        assertFalse(Strings.laPalindrome(chuoi));
    }

    @Test
    void palindromeVoiNullTraVeFalse() {
        assertFalse(Strings.laPalindrome(null));
    }
}
