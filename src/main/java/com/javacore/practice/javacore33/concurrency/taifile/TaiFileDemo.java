package com.javacore.practice.javacore33.concurrency.taifile;

import com.javacore.practice.javacore33.concurrency.taifile.TaiFile.KetQua;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Bài 35 phần 1, 2 và 4 - chạy bằng nút Run trong IDE.
 *
 * <p>Ba khối, đọc từ trên xuống:
 * <ol>
 *   <li>Cùng 20 file, ba cách chạy - xem thời gian và số thread thật sự dùng.</li>
 *   <li>Một file hỏng - xem submit() nuốt lỗi còn invokeAll + get() thì không.</li>
 *   <li>10.000 file - chỗ virtual thread bỏ xa thread pool.</li>
 * </ol>
 */
public class TaiFileDemo {

    private static final int SO_FILE = 20;
    private static final long SEED = 42;
    private static final int SO_THREAD = 4;

    private static final int SO_FILE_NHIEU = 10_000;
    private static final int VI_TRI_FILE_HONG = 5;

    // Functional interface riêng vì Supplier/Function không khai báo được
    // throws InterruptedException - lambda ném checked exception sẽ không biên dịch.
    private interface ChienLuoc {
        List<KetQua> chay(List<TaiFile> danhSach) throws InterruptedException;
    }

    private record Cach(String ten, ChienLuoc chienLuoc) {
    }

    private static final List<Cach> CAC_CACH = List.of(
            new Cach("Tuần tự", TrinhTaiFile::tuanTu),
            new Cach("FixedPool(%d)".formatted(SO_THREAD), ds -> TrinhTaiFile.fixedPool(ds, SO_THREAD)),
            new Cach("Virtual thread", TrinhTaiFile::virtualThread));

    void main() throws InterruptedException {
        List<TaiFile> danhSach = TaiFile.taoDanhSach(SO_FILE, SEED);

        inDuDoan(danhSach);
        inBangSoSanh(danhSach);
        inDemoNuotLoi(danhSach);
        inDemoNhieuFile();
    }

    /**
     * In trước con số kỳ vọng rồi mới chạy. Đoán trước - chạy - đối chiếu là
     * cách duy nhất để biết mình thật sự hiểu hay chỉ đang nhìn số rồi gật gù.
     */
    private static void inDuDoan(List<TaiFile> danhSach) {
        long tongMs = tongDoTreMs(danhSach);
        long maxMs = maxDoTreMs(danhSach);

        System.out.printf("%d file, độ trễ 200-800ms, seed = %d%n", danhSach.size(), SEED);
        System.out.printf("Tổng độ trễ = %,d ms   File chậm nhất = %,d ms%n%n", tongMs, maxMs);

        System.out.println("Dự đoán trước khi chạy:");
        System.out.printf("  Tuần tự        ~ %,d ms  (tổng - làm từng file một)%n", tongMs);
        System.out.printf("  FixedPool(%d)   > %,d ms  (tổng/%d, nhưng còn phải chờ file chậm nhất)%n",
                SO_THREAD, tongMs / SO_THREAD, SO_THREAD);
        System.out.printf("  Virtual thread ~ %,d ms  (chạy hết cùng lúc, chờ đúng file chậm nhất)%n%n", maxMs);
    }

    private static void inBangSoSanh(List<TaiFile> danhSach) {
        System.out.println("--- Khối 1: thực tế ---");
        System.out.printf("%-16s %10s %10s %10s%n", "Cách chạy", "Thời gian", "Số thread", "Nhanh gấp");

        long mocTuanTuMs = 0;

        for (Cach cach : CAC_CACH) {
            long batDau = System.nanoTime();
            List<KetQua> ketQua = chayKhongNemChecked(cach, danhSach);
            long ms = (System.nanoTime() - batDau) / 1_000_000;

            // Mỗi KetQua mang tên thread đã chạy nó, distinct() ra số thread thật.
            long soThread = ketQua.stream().map(KetQua::tenThread).distinct().count();

            if (mocTuanTuMs == 0) {
                mocTuanTuMs = ms;
            }

            System.out.printf("%-16s %7d ms %10d %9.1fx%n",
                    cach.ten(), ms, soThread, (double) mocTuanTuMs / ms);
        }
        System.out.println();
    }

    /**
     * Phần 2 của đề: cùng một danh sách có đúng một file hỏng, chạy hai kiểu.
     */
    private static void inDemoNuotLoi(List<TaiFile> danhSach) {
        List<TaiFile> coFileHong = new ArrayList<>(danhSach);
        coFileHong.set(VI_TRI_FILE_HONG, new TaiFile("file-hong", Duration.ofMillis(100), true));

        System.out.println("--- Khối 2: một file hỏng trong " + coFileHong.size() + " file ---");

        List<KetQua> ketQua = TrinhTaiFile.fixedPoolDungSubmit(coFileHong, SO_THREAD);
        System.out.printf("submit() rồi bỏ Future : chương trình chạy tiếp bình thường, thu được %d/%d kết quả.%n",
                ketQua.size(), coFileHong.size());
        System.out.println("                         Không exception, không log. Chỉ thiếu dữ liệu âm thầm.");

        try {
            TrinhTaiFile.fixedPool(coFileHong, SO_THREAD);
            System.out.println("invokeAll + get()      : (không tới được đây)");
        } catch (IllegalStateException e) {
            System.out.println("invokeAll + get()      : " + e.getMessage());
            System.out.println("                         getCause() -> " + e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi đang chạy demo", e);
        }
        System.out.println();
    }

    /**
     * Phần 4 của đề: chỉ chạy bản virtual thread. Bản FixedPool(4) chỉ ước tính
     * chứ không chạy - với 10.000 file nó mất hàng chục phút, và chính con số
     * ước tính đó mới là điều đáng nhớ.
     */
    private static void inDemoNhieuFile() throws InterruptedException {
        List<TaiFile> nhieuFile = TaiFile.taoDanhSach(SO_FILE_NHIEU, SEED);
        long tongMs = tongDoTreMs(nhieuFile);

        System.out.printf("--- Khối 3: %,d file ---%n", SO_FILE_NHIEU);
        System.out.printf("FixedPool(%d)   ~ %,d phút - không chạy ở đây, chờ lâu quá.%n",
                SO_THREAD, tongMs / SO_THREAD / 60_000);

        long batDau = System.nanoTime();
        List<KetQua> ketQua = TrinhTaiFile.virtualThread(nhieuFile);
        long ms = (System.nanoTime() - batDau) / 1_000_000;

        long soThread = ketQua.stream().map(KetQua::tenThread).distinct().count();
        System.out.printf("Virtual thread   %,d ms, %,d thread cho %,d file.%n", ms, soThread, ketQua.size());
        System.out.println("Chỉ đúng vì task ở đây là chờ I/O. Task ngốn CPU thì dùng FixedPool(số core).");
    }

    /**
     * Lambda trong {@link #CAC_CACH} có {@code throws InterruptedException},
     * gom chỗ xử lý về một nơi cho vòng lặp in bảng đỡ rối.
     */
    private static List<KetQua> chayKhongNemChecked(Cach cach, List<TaiFile> danhSach) {
        try {
            return cach.chienLuoc().chay(danhSach);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Bị ngắt khi đang chạy " + cach.ten(), e);
        }
    }

    private static long tongDoTreMs(List<TaiFile> danhSach) {
        return danhSach.stream().mapToLong(file -> file.doTre().toMillis()).sum();
    }

    private static long maxDoTreMs(List<TaiFile> danhSach) {
        return danhSach.stream().mapToLong(file -> file.doTre().toMillis()).max().orElse(0);
    }
}
