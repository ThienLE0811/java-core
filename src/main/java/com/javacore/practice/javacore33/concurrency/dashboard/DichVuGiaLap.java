package com.javacore.practice.javacore33.concurrency.dashboard;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public final class DichVuGiaLap {

    public static final Duration DO_TRE_MAC_DINH = Duration.ofMillis(500);
    private static final long MA_NGUOI_DUNG_MAC_DINH = 1L;

    private final CauHinh cauHinh;
    private final Executor executor;

    // Đếm số lần thân hàm getRecommendations chạy tới cuối. Dùng để chứng minh
    // một điều phản trực giác: orTimeout/completeOnTimeout KHÔNG dừng được task,
    // nó chỉ hoàn thành cái Future. Thread vẫn ngủ nốt rồi mới nhả ra.
    private final AtomicInteger soLanGoiYChayHet = new AtomicInteger();

    public DichVuGiaLap(CauHinh cauHinh, Executor executor) {
        this.cauHinh = Objects.requireNonNull(cauHinh, "cauHinh không được null");
        this.executor = Objects.requireNonNull(executor, "executor không được null");
    }

    public CompletableFuture<NguoiDung> getUser() {
        return CompletableFuture.supplyAsync(
                () -> {
                    cho(cauHinh.doTreUser());
                    return new NguoiDung(MA_NGUOI_DUNG_MAC_DINH, "Lê Văn Thiện", "thien@paraline.io");
                },
                // Bỏ trống tham số executor là rơi vào ForkJoinPool.commonPool(),
                // pool chỉ có (số core - 1) thread và dùng chung với cả JVM.
                // Task ngủ/chờ I/O nằm trên đó thì ba lời gọi "song song" xếp
                // hàng chờ nhau. Xem khối 5 của Demo.
                executor);
    }

    public CompletableFuture<List<DonHang>> getOrders() {
        return getOrdersTheoNguoiDung(MA_NGUOI_DUNG_MAC_DINH);
    }

    public CompletableFuture<List<DonHang>> getOrdersTheoNguoiDung(long maNguoiDung) {
        return CompletableFuture.supplyAsync(
                () -> {
                    cho(cauHinh.doTreDonHang());
                    return List.of(
                            new DonHang(maNguoiDung * 100 + 1, "DH-001", 1_250_000L),
                            new DonHang(maNguoiDung * 100 + 2, "DH-002", 480_000L));
                },
                executor);
    }

    public CompletableFuture<List<GoiY>> getRecommendations() {
        return CompletableFuture.supplyAsync(
                () -> {
                    cho(cauHinh.doTreGoiY());
                    soLanGoiYChayHet.incrementAndGet();

                    if (cauHinh.goiYGayLoi()) {
                        // Ném thẳng unchecked exception trong supplier. Nó không
                        // bay lên stack của thread gọi - CompletableFuture bắt lại
                        // và chuyển Future sang trạng thái hỏng. Ai không hỏi tới
                        // Future thì không bao giờ biết có lỗi.
                        throw new IllegalStateException("Dịch vụ gợi ý quá tải");
                    }

                    return List.of(
                            new GoiY("Bàn phím cơ", 0.91),
                            new GoiY("Tai nghe chống ồn", 0.78),
                            new GoiY("Giá đỡ màn hình", 0.64));
                },
                executor);
    }

    public int soLanGoiYChayHet() {
        return soLanGoiYChayHet.get();
    }

    private static void cho(Duration doTre) {
        try {
            Thread.sleep(doTre);
        } catch (InterruptedException e) {
            // Supplier không khai báo được checked exception. Khôi phục cờ ngắt
            // rồi bọc lại - nuốt ở đây là tầng trên mất tín hiệu "hãy dừng lại",
            // đúng lỗi đã gặp ở bài 35.
            Thread.currentThread().interrupt();
            throw new CompletionException(e);
        }
    }

    public record NguoiDung(long id, String ten, String email) {
        public NguoiDung {
            Objects.requireNonNull(ten, "ten không được null");
            Objects.requireNonNull(email, "email không được null");
        }
    }

    public record DonHang(long id, String maDon, long tongTienVnd) {
        public DonHang {
            Objects.requireNonNull(maDon, "maDon không được null");
            if (tongTienVnd < 0) {
                throw new IllegalArgumentException("tongTienVnd không được âm: " + tongTienVnd);
            }
        }
    }

    public record GoiY(String tenSanPham, double diem) {
        public GoiY {
            Objects.requireNonNull(tenSanPham, "tenSanPham không được null");
        }
    }

    public record CauHinh(Duration doTreUser, Duration doTreDonHang, Duration doTreGoiY, boolean goiYGayLoi) {

        public CauHinh {
            kiemTra(doTreUser, "doTreUser");
            kiemTra(doTreDonHang, "doTreDonHang");
            kiemTra(doTreGoiY, "doTreGoiY");
        }

        public static CauHinh macDinh() {
            return new CauHinh(DO_TRE_MAC_DINH, DO_TRE_MAC_DINH, DO_TRE_MAC_DINH, false);
        }

        public CauHinh voiGoiY(Duration doTre, boolean gayLoi) {
            return new CauHinh(doTreUser, doTreDonHang, doTre, gayLoi);
        }

        private static void kiemTra(Duration doTre, String ten) {
            Objects.requireNonNull(doTre, ten + " không được null");
            if (doTre.isNegative()) {
                throw new IllegalArgumentException(ten + " không được âm: " + doTre);
            }
        }
    }
}
