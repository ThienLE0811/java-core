package com.javacore.practice.javacore33.concurrency;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

/**
 * Bài 34 phần 1 và 2 - quan sát race condition rồi so sánh ba cách sửa.
 *
 * <p>Chạy bằng nút Run trong IDE. {@code mvn exec:java} không gọi được
 * {@code void main()} dạng instance (plugin vẫn tìm {@code main(String[])}).
 */
public class BoDemDemo {

    private static final int SO_TASK = 1_000;
    private static final int SO_LAN_MOI_TASK = 1_000;
    private static final long KY_VONG = (long) SO_TASK * SO_LAN_MOI_TASK;

    private static final int SO_LUOT_DO = 5;

    private record Cach(String ten, Consumer<BoDem> tang, ToLongFunction<BoDem> doc) {
    }

    private static final List<Cach> CAC_CACH = List.of(
            new Cach("giaTri++ (không bảo vệ)", BoDem::tang, b -> b.getGiaTri()),
            new Cach("volatile", BoDem::tangVolatile, b -> b.getGiaTriVolatile()),
            new Cach("synchronized", BoDem::tangDongBo, b -> b.getGiaTriDongBo()),
            new Cach("AtomicInteger", BoDem::tangAtomic, b -> b.getGiaTriAtomic()),
            new Cach("ReentrantLock", BoDem::tangLock, b -> b.getGiaTriLock()),
            new Cach("LongAdder", BoDem::tangAdder, BoDem::getGiaTriAdder));

    void main() {
        System.out.printf("%d task x %d lần, kỳ vọng = %,d%n%n", SO_TASK, SO_LAN_MOI_TASK, KY_VONG);

        inKetQua();
        System.out.println();
        inThoiGian();
    }

    /**
     * Chạy {@code SO_TASK} task song song, mỗi task gọi {@code tang} đúng
     * {@code SO_LAN_MOI_TASK} lần. Trả về thời gian chạy tính bằng nano giây.
     */
    private static long chayDongThoi(BoDem boDem, Consumer<BoDem> tang) {
        long batDau = System.nanoTime();

        // ExecutorService là AutoCloseable từ Java 19. Dấu } đóng khối try chính
        // là chỗ chương trình CHỜ: close() chặn cho tới khi mọi task chạy xong.
        // Nhờ vậy không cần tự giữ danh sách Thread rồi join() từng cái.
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < SO_TASK; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < SO_LAN_MOI_TASK; j++) {
                        tang.accept(boDem);
                    }
                });
            }
        }

        return System.nanoTime() - batDau;
    }

    private static void inKetQua() {
        System.out.println("--- Kết quả ---");
        for (Cach cach : CAC_CACH) {
            BoDem boDem = new BoDem();
            chayDongThoi(boDem, cach.tang());

            long thucTe = cach.doc().applyAsLong(boDem);
            long thieu = KY_VONG - thucTe;

            System.out.printf("%-24s %,12d   %s%n",
                    cach.ten(),
                    thucTe,
                    thieu == 0 ? "đúng" : "MẤT %,d lần tăng".formatted(thieu));
        }
    }

    /**
     * Đo thời gian: chạy {@link #SO_LUOT_DO} lượt, bỏ lượt đầu vì JIT chưa kịp
     * biên dịch sang mã máy nên lượt đó luôn chậm bất thường.
     *
     * <p>Đây không phải benchmark nghiêm túc (muốn nghiêm túc thì dùng JMH).
     * Chỉ đọc được thứ tự nhanh/chậm, đừng đọc tỉ lệ.
     */
    private static void inThoiGian() {
        System.out.printf("--- Thời gian (bỏ lượt đầu, trung bình %d lượt sau) ---%n", SO_LUOT_DO - 1);

        for (Cach cach : CAC_CACH) {
            long tongNano = 0;

            for (int luot = 0; luot < SO_LUOT_DO; luot++) {
                long nano = chayDongThoi(new BoDem(), cach.tang());
                if (luot > 0) {
                    tongNano += nano;
                }
            }

            double trungBinhMs = tongNano / (SO_LUOT_DO - 1) / 1_000_000.0;
            System.out.printf("%-24s %8.1f ms%n", cach.ten(), trungBinhMs);
        }
    }
}
