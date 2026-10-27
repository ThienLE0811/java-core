package com.javacore.practice.javacore33.oop.hinh;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 9 - hình tròn.
 *
 * <p>Giá trị kỳ vọng trong {@link #dienTichDungVoiCacBanKinh(double, double)} là số tính sẵn
 * chứ không phải {@code Math.PI * r * r}. Cố ý: nếu test viết lại đúng công thức của code thì
 * công thức sai ở đâu, test sai y hệt ở đó, và nó sẽ pass trong khi chương trình hỏng.
 */
class HinhTronTest {

    private static final double DELTA = 1e-9;

    // --- diện tích -------------------------------------------------------

    @ParameterizedTest(name = "bán kính {0} -> diện tích {1}")
    @CsvSource({
            "1,    3.141592653589793",
            "2,    12.566370614359172",
            "0.5,  0.7853981633974483",
            "10,   314.1592653589793"
    })
    void dienTichDungVoiCacBanKinh(double banKinh, double mongDoi) {
        assertEquals(mongDoi, new HinhTron(banKinh).dienTich(), DELTA);
    }

    // --- validate --------------------------------------------------------

    @ParameterizedTest(name = "bán kính {0} bị từ chối")
    @ValueSource(doubles = {-1, 0, Double.NaN, Double.POSITIVE_INFINITY})
    void banKinhKhongHopLeThiNem(double banKinh) {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new HinhTron(banKinh));

        assertTrue(ex.getMessage().contains("Bán kính"),
                "message phải nêu rõ tham số sai, nhận được: " + ex.getMessage());
    }

    // --- toString --------------------------------------------------------

    /**
     * Assert bằng {@code contains} chứ không so khớp cả chuỗi: format của {@code toString}
     * không phải hợp đồng với ai cả, đổi dấu phẩy hay thứ tự field là chuyện bình thường.
     * Thứ cần đảm bảo là nó <em>có</em> nêu được state của object - khác hẳn bản mặc định
     * {@code HinhTron@4b44655e} vô dụng lúc đọc log.
     */
    @Test
    void toStringNeuDuocBanKinh() {
        String ketQua = new HinhTron(2).toString();

        assertTrue(ketQua.contains("HinhTron"), ketQua);
        assertTrue(ketQua.contains("2.0"), ketQua);
    }
}
