package com.javacore.practice.javacore33.oop.thietbi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 10 - router: kết nối được nhưng không sạc được.
 *
 * <p>Phần quan trọng nhất của class này không test được bằng assert, vì nó xảy ra lúc
 * <em>compile</em>: {@code Chargeable.sacTatCa(new Chargeable[]{ new Router() })} không biên
 * dịch nổi. {@link #routerKhongPhaiLaThietBiSacDuoc()} là cách gần nhất để chốt lại ý định đó
 * - nếu ai đó thêm {@code implements Chargeable} vào {@link Router}, test sẽ đỏ.
 */
class RouterTest {

    @Test
    void banDauChuaKetNoi() {
        assertFalse(new Router().dangKetNoi());
    }

    @Test
    void ketNoiRoiThiDangKetNoiTraTrue() {
        Router router = new Router();

        router.ketNoi();

        assertTrue(router.dangKetNoi());
    }

    @Test
    void ngatKetNoiRoiThiDangKetNoiTraFalse() {
        Router router = new Router();

        router.ketNoi();
        router.ngatKetNoi();

        assertFalse(router.dangKetNoi());
    }

    @Test
    void ngatKhiChuaKetNoiThiVanLaChuaKetNoi() {
        Router router = new Router();

        router.ngatKetNoi();

        assertFalse(router.dangKetNoi());
    }

    /**
     * Chốt lại ranh giới của bài 10: router không mang năng lực sạc.
     *
     * <p>Đây là test bảo vệ <em>thiết kế</em>, không phải hành vi. Compiler đã chặn việc nhét
     * router vào {@code sacTatCa} rồi, nhưng nó không chặn được việc ai đó sửa khai báo class.
     * Test này làm việc đó.
     */
    @Test
    void routerKhongPhaiLaThietBiSacDuoc() {
        assertFalse(Chargeable.class.isAssignableFrom(Router.class),
                "Router không được implements Chargeable - nó cắm điện trực tiếp, không có pin");
    }

    @Test
    void routerLaThietBiKetNoiDuoc() {
        assertTrue(Connectable.class.isAssignableFrom(Router.class));
    }

    @Test
    void toStringNeuTrangThaiKetNoi() {
        Router router = new Router();
        router.ketNoi();

        assertTrue(router.toString().contains("true"), "thiếu trạng thái kết nối: " + router);
    }
}
