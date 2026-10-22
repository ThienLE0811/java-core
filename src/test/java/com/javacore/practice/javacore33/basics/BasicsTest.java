package com.javacore.practice.javacore33.basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicsTest {

    @ParameterizedTest(name = "diem {0} -> {1}")
    @CsvSource({
            "0,   Yếu",
            "39,  Yếu",
            "40,  Trung bình",
            "49,  Trung bình",
            "50,  Khá",
            "64,  Khá",
            "65,  Giỏi",
            "79,  Giỏi",
            "80,  Xuất sắc",
            "100, Xuất sắc"
    })
    void xepLoaiDungTaiCacMocBien(int diem, String mongDoi) {
        assertEquals(mongDoi, Basics.xepLoaiHocSinhIfElse(diem));
        assertEquals(mongDoi, Basics.xepLoaiHocSinhSwitch(diem));
    }

    @ParameterizedTest(name = "diem {0} nam ngoai khoang 0-100")
    @ValueSource(ints = {-1, -100, 101, 1000})
    void diemNgoaiKhoangTraVeKhongHopLe(Integer diem) {
        assertEquals(Basics.KHONG_HOP_LE, Basics.xepLoaiHocSinhIfElse(diem));
        assertEquals(Basics.KHONG_HOP_LE, Basics.xepLoaiHocSinhSwitch(diem));
    }

    @Test
    void haiCachVietCungKetQua() {
        for (Integer diem = -10; diem <= 110; diem++) {
            assertEquals(
                    Basics.xepLoaiHocSinhIfElse(diem),
                    Basics.xepLoaiHocSinhSwitch(diem),
                    "Hai cach viet lech nhau tai diem = " + diem);
        }
    }
}
