package com.javacore.practice.javacore33.concurrency.taifile;

import com.javacore.practice.javacore33.concurrency.taifile.TaiFile.KetQua;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class TrinhTaiFile {

    private static final Duration HET_GIO_CHO = Duration.ofSeconds(30);

    private TrinhTaiFile() {
    }

    public static List<KetQua> tuanTu(List<TaiFile> danhSach) throws InterruptedException {
        Objects.requireNonNull(danhSach, "danhSach không được null");

        List<KetQua> ketQua = new ArrayList<>(danhSach.size());
        for (TaiFile file : danhSach) {
            ketQua.add(file.tai());
        }
        return ketQua;
    }

    public static List<KetQua> fixedPool(List<TaiFile> danhSach, int soThread) throws InterruptedException {
        Objects.requireNonNull(danhSach, "danhSach không được null");
        if (soThread <= 0) {
            throw new IllegalArgumentException("soThread phải dương: " + soThread);
        }

        return chayTrenPool(Executors.newFixedThreadPool(soThread), danhSach);
    }

    public static List<KetQua> virtualThread(List<TaiFile> danhSach) throws InterruptedException {
        Objects.requireNonNull(danhSach, "danhSach không được null");

        // Không phải pool: mỗi task một virtual thread mới. Đừng tìm cách giới
        // hạn số thread ở đây - muốn giới hạn thì dùng Semaphore ở tầng nghiệp vụ.
        return chayTrenPool(Executors.newVirtualThreadPerTaskExecutor(), danhSach);
    }

    // Bản minh hoạ CÁCH SAI - chỉ dùng cho Demo, đừng bắt chước.
    public static List<KetQua> fixedPoolDungSubmit(List<TaiFile> danhSach, int soThread) {
        Objects.requireNonNull(danhSach, "danhSach không được null");

        ExecutorService pool = Executors.newFixedThreadPool(soThread);
        List<KetQua> ketQua = new CopyOnWriteArrayList<>();

        try {
            for (TaiFile file : danhSach) {
                // submit() gói mọi exception của task vào Future. Vứt Future đi
                // nghĩa là vứt luôn lỗi: không stack trace, không log, chỉ thiếu
                // dữ liệu một cách âm thầm. Đây là bug production kinh điển.
                pool.submit(() -> ketQua.add(file.tai()));
            }
        } finally {
            dongPoolDungCach(pool, HET_GIO_CHO);
        }

        return List.copyOf(ketQua);
    }

    private static List<KetQua> chayTrenPool(ExecutorService pool, List<TaiFile> danhSach)
            throws InterruptedException {
        try {
            List<Callable<KetQua>> congViec = danhSach.stream()
                    .map(file -> (Callable<KetQua>) file::tai)
                    .toList();

            // invokeAll chặn cho tới khi MỌI task kết thúc. Thứ tự Future trả về
            // khớp thứ tự Callable đưa vào, không phải thứ tự chạy xong.
            return thuKetQua(pool.invokeAll(congViec));
        } finally {
            // finally: lỗi ở giữa thì pool vẫn phải đóng, nếu không thread
            // non-daemon của pool giữ cho JVM không bao giờ thoát.
            dongPoolDungCach(pool, HET_GIO_CHO);
        }
    }

    private static List<KetQua> thuKetQua(List<Future<KetQua>> futures) throws InterruptedException {
        List<KetQua> ketQua = new ArrayList<>(futures.size());

        for (Future<KetQua> future : futures) {
            try {
                // invokeAll đã bảo đảm mọi future xong, nên get() không chờ nữa.
                ketQua.add(future.get());
            } catch (ExecutionException e) {
                // Lỗi thật nằm ở getCause(); ExecutionException chỉ là lớp vỏ.
                // Quên getCause() là log ra một stack trace vô nghĩa.
                throw new IllegalStateException("Tải file thất bại: " + e.getCause(), e.getCause());
            } catch (CancellationException e) {
                // Gặp khi dùng invokeAll(tasks, timeout, unit): task chưa xong bị
                // huỷ, get() ném chứ không trả null.
                throw new IllegalStateException("Task bị huỷ trước khi chạy xong", e);
            }
        }

        return ketQua;
    }

    static void dongPoolDungCach(ExecutorService pool, Duration hetGio) {
        // shutdown() KHÔNG chờ, nó chỉ chặn nhận task mới và trả về ngay.
        // Phải awaitTermination mới biết task đang chạy đã xong hay chưa.
        pool.shutdown();

        try {
            if (!pool.awaitTermination(hetGio.toMillis(), TimeUnit.MILLISECONDS)) {
                // Hết giờ: ngắt các task đang chạy rồi chờ thêm một nhịp.
                // shutdownNow() chỉ gửi interrupt - task nào không đáp lại
                // interrupt thì vẫn chạy tiếp, không ai giết được nó.
                pool.shutdownNow();

                if (!pool.awaitTermination(hetGio.toMillis(), TimeUnit.MILLISECONDS)) {
                    System.err.println("Pool không chịu dừng sau " + hetGio);
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            // Nuốt interrupt ở đây là bug: tầng trên mất tín hiệu "hãy dừng lại".
            Thread.currentThread().interrupt();
        }
    }
}
