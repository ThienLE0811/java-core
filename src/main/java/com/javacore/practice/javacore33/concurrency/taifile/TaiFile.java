package com.javacore.practice.javacore33.concurrency.taifile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public record TaiFile(String ten, Duration doTre, boolean gayLoi) {

    private static final int DO_TRE_MIN_MS = 200;
    private static final int DO_TRE_MAX_MS = 800;

    // Compact constructor: không khai báo lại tham số, không tự gán this.x = x.
    // Compiler chèn phần gán vào cuối khối này.
    public TaiFile {
        Objects.requireNonNull(ten, "ten không được null");
        Objects.requireNonNull(doTre, "doTre không được null");

        if (ten.isBlank()) {
            throw new IllegalArgumentException("ten không được rỗng");
        }
        if (doTre.isNegative()) {
            throw new IllegalArgumentException("Độ trễ không được âm: " + doTre);
        }
    }

    public KetQua tai() throws InterruptedException {
        // Không bắt InterruptedException ở đây: method này chạy trong pool,
        // khi pool bị shutdownNow() thì tín hiệu ngắt phải đi tiếp lên trên
        // để task dừng thật, chứ không nuốt rồi chạy tiếp.
        Thread.sleep(doTre);

        if (gayLoi) {
            throw new IllegalStateException("Tải thất bại: " + ten);
        }

        // Ghi lại tên thread để Demo đếm được thực sự có bao nhiêu thread chạy.
        // Virtual thread do newVirtualThreadPerTaskExecutor tạo ra KHÔNG có tên
        // (getName() trả chuỗi rỗng), nên phải lấy threadId làm định danh.
        Thread hienTai = Thread.currentThread();
        String tenThread = hienTai.getName().isEmpty()
                ? "virtual-#" + hienTai.threadId()
                : hienTai.getName();

        return new KetQua(ten, doTre, tenThread);
    }

    public record KetQua(String tenFile, Duration doTre, String tenThread) {
        public KetQua {
            Objects.requireNonNull(tenFile, "tenFile không được null");
            Objects.requireNonNull(doTre, "doTre không được null");
            Objects.requireNonNull(tenThread, "tenThread không được null");
        }
    }

    // seed cố định để ba chiến lược ở TrinhTaiFile chạy trên cùng một bộ độ trễ,
    // nếu random lại mỗi lần thì phép so sánh thời gian vô nghĩa.
    public static List<TaiFile> taoDanhSach(int soFile, long seed) {
        if (soFile < 0) {
            throw new IllegalArgumentException("soFile không được âm: " + soFile);
        }

        Random random = new Random(seed);
        List<TaiFile> danhSach = new ArrayList<>(soFile);

        for (int i = 1; i <= soFile; i++) {
            // nextInt loại trừ cận trên, nên +1 để 800ms vẫn có thể xuất hiện.
            int milli = DO_TRE_MIN_MS + random.nextInt(DO_TRE_MAX_MS - DO_TRE_MIN_MS + 1);
            danhSach.add(new TaiFile("file-%02d".formatted(i), Duration.ofMillis(milli), false));
        }

        return List.copyOf(danhSach);
    }
}
