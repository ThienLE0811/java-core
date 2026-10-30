package com.javacore.practice.javacore33.concurrency;

import com.javacore.practice.javacore33.oop.TaiKhoanNganHang;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Bài 34 - test cho phần chuyển tiền.
 *
 * <p>Cố ý KHÔNG test {@code chuyenTienDeadlock}: nó treo, mà một test treo thì
 * treo luôn cả bản build. Deadlock là thứ để quan sát bằng {@code ChuyenTienDemo}
 * và {@code jcmd}, không phải thứ để assert.
 *
 * <p>Cũng cố ý không test "race condition có xảy ra không". Test như vậy không
 * tin cậy - cùng một đoạn code có lần sai có lần đúng. Thứ test được ở đây là
 * <em>bất biến</em>: dù các thread xen kẽ thế nào, tổng tiền toàn hệ thống
 * không đổi. Tiền không tự sinh ra và không tự mất đi.
 */
class ChuyenTienTest {

    private static final BigDecimal SO_DU_BAN_DAU = new BigDecimal("1000.00");
    private static final BigDecimal SO_TIEN_CHUYEN = new BigDecimal("10.00");

    @Test
    void chuyenTienAnToan_nhieuThreadChuyenCheo_tongSoDuKhongDoi() throws InterruptedException {
        List<TaiKhoanNganHang> taiKhoan = List.of(
                new TaiKhoanNganHang("ACC-001", "An", SO_DU_BAN_DAU),
                new TaiKhoanNganHang("ACC-002", "Bình", SO_DU_BAN_DAU),
                new TaiKhoanNganHang("ACC-003", "Cường", SO_DU_BAN_DAU),
                new TaiKhoanNganHang("ACC-004", "Dung", SO_DU_BAN_DAU));

        BigDecimal tongTruoc = tongSoDu(taiKhoan);

        // Mọi cặp (i, j) khác nhau chuyển cho nhau cùng lúc - trong đó có đủ các
        // cặp ngược chiều, đúng tình huống gây deadlock ở bản không sắp thứ tự.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (TaiKhoanNganHang tu : taiKhoan) {
                for (TaiKhoanNganHang den : taiKhoan) {
                    if (tu != den) {
                        executor.submit(() -> ChuyenTien.chuyenTienAnToan(tu, den, SO_TIEN_CHUYEN));
                    }
                }
            }
        }

        assertEquals(tongTruoc, tongSoDu(taiKhoan));

        // Mỗi tài khoản gửi đi 3 lần và nhận về 3 lần, nên số dư phải về đúng
        // chỗ cũ. Đây là phần bắt lỗi chặt hơn cả tổng: tổng vẫn đúng ngay cả
        // khi một giao dịch bị tính hai lần ở cả hai đầu.
        for (TaiKhoanNganHang tk : taiKhoan) {
            assertEquals(SO_DU_BAN_DAU, tk.getSoDu(), "Sai ở " + tk.getSoTaiKhoan());
        }
    }

    @Test
    void chuyenTienAnToan_cungMotObject_nemIllegalArgument() {
        TaiKhoanNganHang a = new TaiKhoanNganHang("ACC-101", "An", SO_DU_BAN_DAU);

        assertThrows(IllegalArgumentException.class,
                () -> ChuyenTien.chuyenTienAnToan(a, a, SO_TIEN_CHUYEN));
        assertEquals(SO_DU_BAN_DAU, a.getSoDu());
    }

    @Test
    void chuyenTienAnToan_haiObjectTrungSoTaiKhoan_nemIllegalArgument() {
        TaiKhoanNganHang a = new TaiKhoanNganHang("ACC-201", "An", SO_DU_BAN_DAU);
        TaiKhoanNganHang banSao = new TaiKhoanNganHang("ACC-201", "An", SO_DU_BAN_DAU);

        assertThrows(IllegalArgumentException.class,
                () -> ChuyenTien.chuyenTienAnToan(a, banSao, SO_TIEN_CHUYEN));
    }

    @Test
    void chuyenTienAnToan_soTienKhongDuong_nemIllegalArgument() {
        TaiKhoanNganHang a = new TaiKhoanNganHang("ACC-301", "An", SO_DU_BAN_DAU);
        TaiKhoanNganHang b = new TaiKhoanNganHang("ACC-302", "Bình", SO_DU_BAN_DAU);

        assertThrows(IllegalArgumentException.class,
                () -> ChuyenTien.chuyenTienAnToan(a, b, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> ChuyenTien.chuyenTienAnToan(a, b, new BigDecimal("-10.00")));
    }

    @Test
    void chuyenTienAnToan_soDuKhongDu_khongBenNaoDoiSoDu() {
        TaiKhoanNganHang a = new TaiKhoanNganHang("ACC-401", "An", new BigDecimal("50.00"));
        TaiKhoanNganHang b = new TaiKhoanNganHang("ACC-402", "Bình", SO_DU_BAN_DAU);

        assertThrows(TaiKhoanNganHang.SoDuKhongDuException.class,
                () -> ChuyenTien.chuyenTienAnToan(a, b, new BigDecimal("100.00")));

        // withdraw ném trước khi deposit chạy, nên bên nhận không được cộng thêm.
        assertEquals(new BigDecimal("50.00"), a.getSoDu());
        assertEquals(SO_DU_BAN_DAU, b.getSoDu());
    }

    private static BigDecimal tongSoDu(List<TaiKhoanNganHang> taiKhoan) {
        return taiKhoan.stream()
                .map(TaiKhoanNganHang::getSoDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
