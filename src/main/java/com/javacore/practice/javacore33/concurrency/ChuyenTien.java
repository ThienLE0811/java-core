package com.javacore.practice.javacore33.concurrency;

import com.javacore.practice.javacore33.oop.TaiKhoanNganHang;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Bài 34 phần 3 - cùng một nghiệp vụ chuyển tiền, hai cách khoá.
 *
 * <p>{@link #chuyenTienDeadlock} khoá theo thứ tự tham số, nên hai lời gọi ngược
 * chiều nhau sẽ ôm chéo khoá của nhau và treo vĩnh viễn.
 *
 * <p>{@link #chuyenTienAnToan} khoá theo thứ tự số tài khoản, nên mọi thread
 * trong hệ thống đều xin khoá theo cùng một trình tự - không còn vòng chờ.
 *
 * <p>Lưu ý quan trọng khi mang sang Spring: cách này chỉ đúng trong MỘT JVM.
 * Chạy nhiều pod thì khoá ở đây vô nghĩa, phải dùng khoá của database
 * ({@code @Version} hoặc {@code SELECT ... FOR UPDATE}). Nhưng nguyên tắc
 * "luôn khoá theo cùng một thứ tự" thì giữ nguyên, vì Postgres cũng sẽ ném
 * {@code deadlock detected} nếu hai transaction khoá row ngược chiều nhau.
 */
public final class ChuyenTien {

    /**
     * Nghỉ giữa hai lần khoá để deadlock tái hiện ổn định. Không có nó, thread
     * thứ nhất thường chạy xong trước khi thread thứ hai kịp bắt đầu.
     */
    private static final long DO_TRE_MS = 100;

    private ChuyenTien() {
    }

    public static void chuyenTienDeadlock(TaiKhoanNganHang tu, TaiKhoanNganHang den, BigDecimal soTien) {
        kiemTraThamSo(tu, den, soTien);

        synchronized (tu) {
            nghiNgan();
            synchronized (den) {
                chuyen(tu, den, soTien);
            }
        }
    }

    public static void chuyenTienAnToan(TaiKhoanNganHang tu, TaiKhoanNganHang den, BigDecimal soTien) {
        kiemTraThamSo(tu, den, soTien);

        int thuTu = tu.getSoTaiKhoan().compareTo(den.getSoTaiKhoan());

        // Hai object khác nhau nhưng cùng số tài khoản: không có thứ tự để sắp,
        // và khoá cả hai cũng vô nghĩa vì chúng là cùng một tài khoản về mặt
        // nghiệp vụ. Chặn từ đầu thay vì khoá nhầm.
        if (thuTu == 0) {
            throw new IllegalArgumentException("Không thể chuyển tiền cho chính tài khoản " + tu.getSoTaiKhoan());
        }

        TaiKhoanNganHang khoaTruoc = thuTu < 0 ? tu : den;
        TaiKhoanNganHang khoaSau = thuTu < 0 ? den : tu;

        synchronized (khoaTruoc) {
            nghiNgan();
            synchronized (khoaSau) {
                chuyen(tu, den, soTien);
            }
        }
    }

    /**
     * Rút trước, nạp sau. Nếu {@code withdraw} ném {@code SoDuKhongDuException}
     * thì chưa có gì xảy ra - không mất tiền. Thứ tự ngược lại (nạp trước) sẽ
     * tạo ra tiền từ hư không khi bước rút thất bại.
     */
    private static void chuyen(TaiKhoanNganHang tu, TaiKhoanNganHang den, BigDecimal soTien) {
        tu.withdraw(soTien);
        den.deposit(soTien);
    }

    private static void kiemTraThamSo(TaiKhoanNganHang tu, TaiKhoanNganHang den, BigDecimal soTien) {
        Objects.requireNonNull(tu, "tài khoản nguồn không được null");
        Objects.requireNonNull(den, "tài khoản đích không được null");
        Objects.requireNonNull(soTien, "soTien không được null");

        if (tu == den) {
            throw new IllegalArgumentException("Không thể chuyển tiền cho chính tài khoản " + tu.getSoTaiKhoan());
        }
        if (soTien.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền chuyển phải dương, nhận được: " + soTien);
        }
    }

    private static void nghiNgan() {
        try {
            Thread.sleep(DO_TRE_MS);
        } catch (InterruptedException e) {
            // Bắt InterruptedException thì phải đặt lại cờ interrupt, nếu không
            // tầng trên mất tín hiệu "hãy dừng lại" và thread không bao giờ thoát.
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt giữa chừng khi đang chuyển tiền", e);
        }
    }
}
