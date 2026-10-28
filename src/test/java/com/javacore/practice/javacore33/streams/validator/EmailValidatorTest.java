package com.javacore.practice.javacore33.streams.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {

    private final Validator<String> validator = new EmailValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "a@b.com",
            "thienlv@paraline.io",
            "x.y@sub.domain.vn",
            "nguoi-dung_01@vi-du.com.vn"
    })
    void emailHopLe(String email) {
        assertTrue(validator.validate(email), email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",             // rỗng
            "   ",          // toàn khoảng trắng
            "ab.com",       // không có '@'
            "@b.com",       // '@' đứng đầu, phần tên rỗng
            " @b.com",      // phần tên chỉ có khoảng trắng
            "a@",           // không có tên miền
            "a@   ",        // tên miền chỉ có khoảng trắng
            "a@b",          // tên miền không có dấu chấm
            "a@.com",       // dấu chấm đứng đầu tên miền
            "a@b.",         // dấu chấm đứng cuối tên miền
            "a@@b.com",     // hai '@'
            "a@b.com@c"     // '@' thứ hai ở cuối
    })
    void emailKhongHopLe(String email) {
        assertFalse(validator.validate(email), email);
    }

    @Test
    void nullThiNemNpeChuKhongTraFalse() {
        // Hợp đồng ở Validator.validate: null là lỗi lập trình, không phải dữ liệu sai.
        assertThrows(NullPointerException.class, () -> validator.validate(null));
    }

    /**
     * Ghi lại một giới hạn đã biết chứ không phải mong muốn: luật viết tay chỉ kiểm tra dấu
     * chấm đầu tiên, nên hai chấm liền nhau vẫn lọt. Validate email thật trong production là
     * gửi mail xác nhận, không phải parse chuỗi.
     */
    @Test
    void haiDauChamLienNhauVanLotDayLaGioiHanDaBiet() {
        assertTrue(validator.validate("a@b..com"));
    }
}
