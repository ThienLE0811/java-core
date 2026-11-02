package com.javacore.practice.javacore33.jvm;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

// Bài 37 phần 3 - hai loại Error của JVM: tràn stack và tràn heap.
//
// Chạy với VM option -Xmx16m để phần OutOfMemoryError xảy ra nhanh:
//   java -Xmx16m src/main/java/com/javacore/practice/javacore33/jvm/TranBoNhoDemo.java
// Trong IDE: Run Configuration -> VM options -> -Xmx16m
//
// Lưu ý: bắt StackOverflowError / OutOfMemoryError là việc KHÔNG được làm
// trong code thật. Ở đây catch chỉ để quan sát rồi in ra.
public class TranBoNhoDemo {

    private static final int KICH_THUOC_KHOI = 100_000;
    private static final int SO_KHOI_VUT_DI = 10_000;
    private static final int MB = 1024 * 1024;

    private static int doSau;

    void main() {
        inThongTinBoNho();
        tranStack();
        gcCoBan();
        tranHeap();
    }

    private static void inThongTinBoNho() {
        Runtime runtime = Runtime.getRuntime();
        long maxMb = runtime.maxMemory() / MB;

        System.out.printf("Heap tối đa (-Xmx) : %,d MB%n", maxMb);
        System.out.printf("Heap đã cấp        : %,d MB%n", runtime.totalMemory() / MB);
        if (maxMb > 64) {
            System.out.println("Heap đang khá lớn - thêm VM option -Xmx16m để phần tràn heap chạy nhanh.");
        }
    }

    // Stack: mỗi lần gọi method đẩy thêm một stack frame. Stack có kích thước
    // cố định cho mỗi thread (-Xss), không phụ thuộc -Xmx.
    private static void tranStack() {
        tieuDe("StackOverflowError - tràn stack");

        doSau = 0;
        try {
            dequyKhongDiemDung();
            System.out.println("(không tới được đây)");
        } catch (StackOverflowError e) {
            System.out.printf("Tràn stack sau %,d lần gọi lồng nhau.%n", doSau);
            System.out.printf("Error: %s%n", e.getClass().getName());
        }

        System.out.println("""

                Vì sao: mỗi lần gọi method đẩy thêm một stack frame (tham số, biến
                cục bộ, địa chỉ quay về). Stack cố định theo thread, chỉnh bằng
                -Xss chứ không phải -Xmx - thử -Xss256k rồi -Xss4m xem con số trên
                đổi thế nào.
                Ở Spring: hai entity JPA trỏ nhau hai chiều, dùng Lombok @Data thì
                toString()/hashCode() gọi vòng vô tận và nổ đúng lỗi này.""");
    }

    private static void dequyKhongDiemDung() {
        doSau++;
        dequyKhongDiemDung();
    }

    // Đối chứng cho tranHeap(): cấp phát y hệt nhưng KHÔNG giữ tham chiếu.
    // Quy tắc của GC chỉ có một: object nào không còn đường đi tới từ GC root
    // (biến cục bộ trên stack, field static, ...) thì dọn được.
    private static void gcCoBan() {
        tieuDe("GC cơ bản - cùng lượng cấp phát, khác ở chỗ có giữ hay không");

        Runtime runtime = Runtime.getRuntime();
        long tongMb = (long) SO_KHOI_VUT_DI * KICH_THUOC_KHOI / MB;

        // tong chỉ để JIT không xoá hẳn vòng lặp vì thấy kết quả không ai dùng.
        long tong = 0;
        for (int i = 0; i < SO_KHOI_VUT_DI; i++) {
            byte[] khoi = new byte[KICH_THUOC_KHOI];
            khoi[0] = (byte) i;
            tong += khoi[0];
            // Hết vòng lặp, khoi mất tham chiếu -> GC dọn ngay vòng sau.
        }
        System.out.printf("Đã cấp phát %,d MB trên heap %,d MB mà không hề tràn (tổng kiểm %d).%n",
                tongMb, runtime.maxMemory() / MB, tong);
        System.out.println("Heap chưa bao giờ giữ quá một khối cùng lúc - GC dọn kịp từng vòng.");

        // WeakReference: xem tận mắt một object biến mất khi mất tham chiếu mạnh.
        Object doiTuong = new Object();
        WeakReference<Object> yeu = new WeakReference<>(doiTuong);
        System.out.printf("%nCòn biến doiTuong trỏ tới  : weakRef.get() = %s%n", yeu.get());

        doiTuong = null;
        System.gc();
        System.out.printf("Sau doiTuong = null + gc() : weakRef.get() = %s%n", yeu.get());

        System.out.printf("%nHeap đang dùng: %,d MB / %,d MB đã cấp%n",
                (runtime.totalMemory() - runtime.freeMemory()) / MB, runtime.totalMemory() / MB);

        System.out.println("""

                Vì sao: GC không đếm xem object "cũ" hay "to", nó chỉ hỏi còn ai
                với tới được không. Vòng lặp trên tạo gần 1 GB object nhưng tại
                mỗi thời điểm chỉ một khối còn sống, nên 16 MB heap là thừa.
                System.gc() chỉ là GỢI Ý, JVM có quyền bỏ qua - đừng bao giờ gọi
                nó trong code thật để "giải phóng bộ nhớ".
                Ở Spring: leak hay gặp nhất không phải do cấp phát nhiều, mà do
                một Map/List static hoặc một bean singleton giữ mãi dữ liệu của
                từng request. Cấp phát nhiều mà vứt đi liền thì GC lo được.""");
    }

    // Heap: nơi chứa object. List giữ tham chiếu tới mọi khối đã tạo nên GC
    // không dọn được khối nào - đây chính là hình dạng của một memory leak.
    private static void tranHeap() {
        tieuDe("OutOfMemoryError - tràn heap");

        List<byte[]> danhSach = new ArrayList<>();
        try {
            while (true) {
                danhSach.add(new byte[KICH_THUOC_KHOI]);
            }
        } catch (OutOfMemoryError e) {
            int soKhoi = danhSach.size();
            // Phải dọn TRƯỚC khi in: printf cũng cần cấp phát, không dọn thì
            // OutOfMemoryError lần hai nổ ngay trong khối catch này.
            danhSach.clear();
            danhSach = null;

            System.out.printf("Tràn heap sau %,d khối x %,d byte = %,d MB.%n",
                    soKhoi, KICH_THUOC_KHOI, (long) soKhoi * KICH_THUOC_KHOI / MB);
            System.out.printf("Error: %s: %s%n", e.getClass().getName(), e.getMessage());
        }

        System.out.println("""

                Vì sao: GC chỉ dọn được object không còn ai tham chiếu tới. List ở
                trên giữ chặt mọi khối nên không khối nào dọn được - heap đầy dần
                tới khi hết. Thử bỏ dòng add() và chỉ new byte[] rồi vứt đi: vòng
                lặp chạy mãi không bao giờ tràn, vì GC dọn kịp.
                Ở Spring: findAll() trên bảng vài triệu dòng trong container đặt
                -Xmx512m. Luôn bật sẵn -XX:+HeapDumpOnOutOfMemoryError để còn có
                cái mà mở ra xem khi sự cố xảy ra lúc 2 giờ sáng.""");
    }

    private static void tieuDe(String ten) {
        System.out.printf("%n=== %s ===%n", ten);
    }
}
