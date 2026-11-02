package com.javacore.practice.javacore33.concurrency.dashboard;

import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.CauHinh;
import com.javacore.practice.javacore33.concurrency.dashboard.DichVuGiaLap.GoiY;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Bài 36 - chạy bằng nút Run trong IDE.
 *
 * <ol>
 *   <li>Năm cách gộp 3 API, so thời gian.</li>
 *   <li>getRecommendations lỗi - dashboard vẫn dựng được.</li>
 *   <li>getRecommendations chậm quá - completeOnTimeout và orTimeout khác nhau chỗ nào.</li>
 *   <li>Chuỗi không ai join - lỗi biến mất không một dòng log.</li>
 *   <li>Executor quá nhỏ - "song song" trên giấy, tuần tự khi chạy.</li>
 * </ol>
 */
public class DashboardDemo {

    private static final Duration HET_GIO_GOI_Y = Duration.ofSeconds(1);
    private static final Duration GOI_Y_CHAM = Duration.ofSeconds(2);
    private static final Duration DONG_POOL_CHO = Duration.ofSeconds(5);
    private static final int SO_THREAD = 3;

    private record Cach(String ten, Function<DichVuGiaLap, Dashboard> chay) {
    }

    private static final List<Cach> CAC_CACH = List.of(
            new Cach("join() sớm (SAI)", TrinhGopDashboard::gopKieuSai),
            new Cach("join() muộn (ĐÚNG)", TrinhGopDashboard::gopJoinMuon),
            new Cach("thenCombine", dv -> TrinhGopDashboard.gopBangThenCombine(dv, HET_GIO_GOI_Y).join()),
            new Cach("allOf", dv -> TrinhGopDashboard.gopBangAllOf(dv, HET_GIO_GOI_Y).join()),
            new Cach("thenCompose", dv -> TrinhGopDashboard.gopKieuPhuThuoc(dv, HET_GIO_GOI_Y).join()));

    void main() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(SO_THREAD);
        try {
            inDuDoan();
            khoi1SoSanhCachGop(pool);
            khoi2GoiYLoi(pool);
            khoi3GoiYChamQua(pool);
            khoi4NuotLoi(pool);
            khoi5PoolQuaNho();
        } finally {
            dongPoolDungCach(pool);
        }
    }

    /** Đoán trước - chạy - đối chiếu. Nhìn số rồi gật gù thì không học được gì. */
    private static void inDuDoan() {
        System.out.println("3 API, mỗi cái 500 ms, executor " + SO_THREAD + " thread.\n");
        System.out.println("Dự đoán trước khi chạy:");
        System.out.println("  join() sớm   ~ 1500 ms  (3 lần 500 nối đuôi)");
        System.out.println("  join() muộn  ~  500 ms  (cũng 3 join, nhưng tạo future trước)");
        System.out.println("  thenCombine  ~  500 ms  (3 task cùng lúc, chờ cái chậm nhất)");
        System.out.println("  allOf        ~  500 ms  (y hệt, chỉ khác cách viết)");
        System.out.println("  thenCompose  ~ 1000 ms  (orders phải chờ userId - phụ thuộc thật)\n");
    }

    private static void khoi1SoSanhCachGop(ExecutorService pool) {
        System.out.println("--- Khối 1: thực tế ---");
        System.out.printf("%-18s %10s   %s%n", "Cách gộp", "Thời gian", "Kết quả");

        for (Cach cach : CAC_CACH) {
            DichVuGiaLap dichVu = new DichVuGiaLap(CauHinh.macDinh(), pool);

            long batDau = System.nanoTime();
            Dashboard dashboard = cach.chay().apply(dichVu);
            long ms = (System.nanoTime() - batDau) / 1_000_000;

            System.out.printf("%-18s %7d ms   %s%n", cach.ten(), ms, dashboard.moTaNgan());
        }
        System.out.println();
    }

    private static void khoi2GoiYLoi(ExecutorService pool) {
        System.out.println("--- Khối 2: getRecommendations ném lỗi ---");

        CauHinh cauHinh = CauHinh.macDinh().voiGoiY(Duration.ofMillis(200), true);
        DichVuGiaLap dichVu = new DichVuGiaLap(cauHinh, pool);

        Dashboard dashboard = TrinhGopDashboard.gopBangAllOf(dichVu, HET_GIO_GOI_Y).join();
        System.out.println("  Dashboard vẫn dựng được: " + dashboard.moTaNgan());
        System.out.println("  exceptionally nằm TRÊN NHÁNH gợi ý nên chỉ gợi ý mất,");
        System.out.println("  user và đơn hàng không việc gì.\n");

        // Đặt sai chỗ: bọc exceptionally ra NGOÀI chuỗi đã gộp.
        CompletableFuture<Dashboard> datSaiCho = dichVu.getUser()
                .thenCombine(dichVu.getRecommendations(), (nd, ds) -> new Dashboard(nd, List.of(), ds))
                .exceptionally(loi -> null);

        System.out.println("  Nếu exceptionally đặt SAU thenCombine: " + datSaiCho.join());
        System.out.println("  Cả dashboard thành null. Một nhánh hỏng kéo sập tất cả.\n");
    }

    private static void khoi3GoiYChamQua(ExecutorService pool) throws InterruptedException {
        System.out.println("--- Khối 3: getRecommendations chậm 2 s, hạn 1 s ---");

        CauHinh cauHinh = CauHinh.macDinh().voiGoiY(GOI_Y_CHAM, false);
        DichVuGiaLap dichVu = new DichVuGiaLap(cauHinh, pool);

        // Bản im lặng: quá hạn hoàn thành BÌNH THƯỜNG nên exceptionally không chạy.
        List<GoiY> imLang = TrinhGopDashboard
                .goiYImLangKhiHetGio(dichVu.getRecommendations(), HET_GIO_GOI_Y)
                .join();
        System.out.printf("  completeOnTimeout: %d gợi ý, KHÔNG một dòng log nào ở trên.%n", imLang.size());
        System.out.println("  Số task gợi ý đã chạy hết lúc này: " + dichVu.soLanGoiYChayHet());

        // Bản có log: orTimeout biến quá hạn thành TimeoutException, đi chung
        // một cửa exceptionally với lỗi thật nên không nhánh nào trôi lọt.
        long batDau = System.nanoTime();
        Dashboard dashboard = TrinhGopDashboard.gopBangAllOf(dichVu, HET_GIO_GOI_Y).join();
        long ms = (System.nanoTime() - batDau) / 1_000_000;
        System.out.printf("  orTimeout        : %d ms, %s (dòng [gợi ý] phía trên là của nó)%n",
                ms, dashboard.moTaNgan());

        // orTimeout trần, không exceptionally: lỗi nổ ra tận chỗ join().
        try {
            TrinhGopDashboard.goiYOrTimeout(dichVu, HET_GIO_GOI_Y).join();
            System.out.println("  orTimeout trần   : (không tới được đây)");
        } catch (CompletionException e) {
            System.out.println("  orTimeout trần   : join() ném " + TrinhGopDashboard.nguyenNhanGoc(e));
        }

        Thread.sleep(GOI_Y_CHAM.plusMillis(500));
        System.out.println("  Số task gợi ý chạy hết sau khi chờ thêm: " + dichVu.soLanGoiYChayHet());
        System.out.println("  Hết giờ KHÔNG huỷ task. Future xong, thread vẫn ngủ nốt rồi mới nhả.");
        System.out.println("  Thật sự muốn dừng thì phải tự truyền tín hiệu huỷ vào trong task.\n");
    }

    private static void khoi4NuotLoi(ExecutorService pool) throws InterruptedException {
        System.out.println("--- Khối 4: chuỗi không ai join ---");

        CauHinh cauHinh = CauHinh.macDinh().voiGoiY(Duration.ofMillis(100), true);
        DichVuGiaLap dichVu = new DichVuGiaLap(cauHinh, pool);

        dichVu.getRecommendations()
                .thenAccept(ds -> System.out.println("  (không bao giờ in) " + ds.size()));

        Thread.sleep(400);
        System.out.println("  Task đã hỏng xong từ lâu mà không một dòng log nào.");
        System.out.println("  JS ít ra còn cảnh báo UnhandledPromiseRejection, Java im lặng tuyệt đối.");

        try {
            dichVu.getRecommendations()
                    .thenAccept(ds -> System.out.println("  (không tới đây)"))
                    .join();
        } catch (CompletionException e) {
            System.out.println("  Chỉ cần join() là lỗi hiện ra: " + TrinhGopDashboard.nguyenNhanGoc(e));
        }

        // whenComplete: nhìn thấy lỗi nhưng KHÔNG nuốt, lỗi vẫn chảy tiếp xuống dưới.
        dichVu.getRecommendations()
                .whenComplete((ds, loi) -> {
                    if (loi != null) {
                        System.out.println("  whenComplete cũng thấy: " + TrinhGopDashboard.nguyenNhanGoc(loi));
                    }
                })
                .exceptionally(loi -> List.<GoiY>of())
                .join();
        System.out.println();
    }

    private static void khoi5PoolQuaNho() {
        System.out.println("--- Khối 5: cùng code allOf, executor 1 thread ---");

        ExecutorService poolMotThread = Executors.newSingleThreadExecutor();
        try {
            DichVuGiaLap dichVu = new DichVuGiaLap(CauHinh.macDinh(), poolMotThread);

            long batDau = System.nanoTime();
            TrinhGopDashboard.gopBangAllOf(dichVu, Duration.ofSeconds(5)).join();
            long ms = (System.nanoTime() - batDau) / 1_000_000;

            System.out.printf("  allOf trên pool 1 thread: %d ms%n", ms);
            System.out.println("  Code y hệt khối 1, chỉ đổi executor. Song song là thuộc tính của");
            System.out.println("  executor, không phải của CompletableFuture.");
            System.out.printf("  Bỏ trống tham số executor -> commonPool, máy này có %d thread.%n",
                    ForkJoinPool.commonPool().getParallelism());
        } finally {
            dongPoolDungCach(poolMotThread);
        }
    }

    private static void dongPoolDungCach(ExecutorService pool) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(DONG_POOL_CHO.toMillis(), TimeUnit.MILLISECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
