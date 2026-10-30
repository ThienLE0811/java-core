package com.javacore.practice.javacore33.concurrency;

import com.javacore.practice.javacore33.oop.TaiKhoanNganHang;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Bài 34 phần 3 - chạy để thấy deadlock, rồi chạy lại để thấy nó biến mất.
 *
 * <p>Mặc định chạy bản an toàn. Đổi {@link #TAI_HIEN_DEADLOCK} thành {@code true}
 * rồi Run lại: chương trình sẽ treo. Lúc đó mở một terminal khác và gõ:
 *
 * <pre>
 * jcmd -l                     # tìm pid của ChuyenTienDemo
 * jcmd &lt;pid&gt; Thread.print     # tìm dòng "Found one Java-level deadlock"
 * </pre>
 *
 * <p>Dùng platform thread chứ không phải virtual thread, vì bộ phát hiện
 * deadlock của JVM chỉ soi monitor do platform thread giữ.
 */
public class ChuyenTienDemo {

    private static final boolean TAI_HIEN_DEADLOCK = false;

    private static final BigDecimal SO_DU_BAN_DAU = new BigDecimal("1000.00");
    private static final BigDecimal SO_TIEN_CHUYEN = new BigDecimal("100.00");

    private static final Duration HET_GIO_CHO = Duration.ofSeconds(3);

    void main() throws InterruptedException {
        TaiKhoanNganHang a = new TaiKhoanNganHang("ACC-001", "An", SO_DU_BAN_DAU);
        TaiKhoanNganHang b = new TaiKhoanNganHang("ACC-002", "Bình", SO_DU_BAN_DAU);

        System.out.println(TAI_HIEN_DEADLOCK ? "Chạy bản DEADLOCK" : "Chạy bản AN TOÀN");
        System.out.printf("Trước:  A = %s, B = %s%n", a.getSoDu(), b.getSoDu());

        Thread aSangB = taoThread("chuyen-A-sang-B", a, b);
        Thread bSangA = taoThread("chuyen-B-sang-A", b, a);

        aSangB.start();
        bSangA.start();

        // join có timeout: nếu treo thì chương trình vẫn báo được là treo,
        // thay vì đứng im không nói gì.
        aSangB.join(HET_GIO_CHO.toMillis());
        bSangA.join(HET_GIO_CHO.toMillis());

        boolean conTreo = aSangB.isAlive() || bSangA.isAlive();

        if (conTreo) {
            System.out.printf("%nDEADLOCK: hai thread vẫn sống sau %s.%n", HET_GIO_CHO);
            System.out.println("Chạy 'jcmd -l' rồi 'jcmd <pid> Thread.print' ở terminal khác để xem.");
            System.out.println("Process này sẽ không tự thoát - dừng bằng nút Stop trong IDE.");
            return;
        }

        System.out.printf("Sau:    A = %s, B = %s%n", a.getSoDu(), b.getSoDu());
        System.out.printf("Tổng:   %s (phải bằng %s)%n",
                a.getSoDu().add(b.getSoDu()),
                SO_DU_BAN_DAU.add(SO_DU_BAN_DAU));
    }

    private static Thread taoThread(String ten, TaiKhoanNganHang tu, TaiKhoanNganHang den) {
        Runnable viec = TAI_HIEN_DEADLOCK
                ? () -> ChuyenTien.chuyenTienDeadlock(tu, den, SO_TIEN_CHUYEN)
                : () -> ChuyenTien.chuyenTienAnToan(tu, den, SO_TIEN_CHUYEN);

        return Thread.ofPlatform().name(ten).unstarted(viec);
    }
}
