package com.javacore.api.virtual_threads;

/**
 * Creating Virtual Threads
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 2/8 - mục thứ hai của trang Virtual Threads.
 * - Bài API thuần túy: bốn cách tạo virtual thread, và quy tắc chọn cách nào.
 * - Một câu cho cả bài: đừng tạo thủ công nếu không cần - 90% trường hợp dùng
 * {@code Executors.newVirtualThreadPerTaskExecutor()} trong try-with-resources là đủ.
 * - Điều cần đổi trong đầu ngay từ bài này: bạn KHÔNG còn "xin một thread từ pool" nữa.
 * Virtual thread rẻ tới mức mô hình đúng là TẠO MỚI MỘT CÁI CHO MỖI TÁC VỤ rồi vứt đi.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#creating">dev.java - Virtual Threads, mục Creating Virtual Threads</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING VIRTUAL THREADS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. CÁCH CHÍNH - EXECUTOR SERVICE
 * - API: {@code Executors.newVirtualThreadPerTaskExecutor()} trả về một
 * {@code ExecutorService} mà MỖI tác vụ nộp vào được chạy trên MỘT virtual thread RIÊNG.
 * - Khung code của trang gốc, nhớ nguyên dạng này:
 * <pre>{@code
 * try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
 *     for (int i = 0; i < 1_000_000; i++) {
 *         executor.submit(() -> { ... });
 *     }
 * }
 * }</pre>
 * - Chú ý chữ "PerTask" trong tên: đây KHÔNG phải pool. Không có số lượng thread cố định,
 * không có hàng đợi tác vụ chờ thread rảnh. Nộp bao nhiêu tác vụ thì sinh bấy nhiêu thread.
 * - Mẹo nhỏ của trang gốc trong ví dụ: dùng {@code LockSupport.parkNanos()} thay cho
 * {@code Thread.sleep()} chỉ để KHỎI PHẢI bắt {@code InterruptedException} cho gọn ví dụ.
 * Đây là thủ thuật trình bày, không phải lời khuyên cho code thật.
 * <p>
 * 2. THREAD.BUILDER VÀ THREAD FACTORY
 * - Khi một API cấp thấp đòi bạn đưa vào một {@code ThreadFactory}, dùng interface mới
 * {@code Thread.Builder}:
 * <pre>{@code
 * ThreadFactory factory = Thread.ofVirtual().name("request-", 1).factory();
 * Thread t = factory.newThread(myRunnable);   // thread CHƯA chạy
 * t.start();
 * }</pre>
 * - {@code name("request-", 1)} đặt tên tự tăng: request-1, request-2, request-3...
 * - Bên cạnh {@code Thread.ofVirtual()} còn có {@code Thread.ofPlatform()} cùng kiểu builder,
 * nên API tạo thread giờ thống nhất cho cả hai loại.
 * <p>
 * 3. TẠO MỘT THREAD ĐƠN LẺ
 * - Hai method kết thúc builder:
 * + {@code Thread.ofVirtual().unstarted(runnable)} - trả về {@code Thread} CHƯA chạy, bạn
 * tự gọi {@code start()} sau.
 * + {@code Thread.ofVirtual().start(runnable)} - tạo và chạy luôn.
 * - Lối tắt cho demo nhanh: {@code Thread.startVirtualThread(runnable)}.
 * <p>
 * 4. MUỐN LẤY KẾT QUẢ THÌ BẮT BUỘC QUA EXECUTOR SERVICE
 * - Điểm trang gốc nói rõ: chỉ con đường {@code ExecutorService} mới nhận được
 * {@code Callable} (tức tác vụ CÓ giá trị trả về) và trả về {@code Future}.
 * - {@code Thread}/{@code ThreadFactory} chỉ nhận {@code Runnable}, không có chỗ trả kết quả.
 * - Suy ra quy tắc chọn, đây là thứ đáng nhớ nhất bài:
 * + Cần KẾT QUẢ -> {@code newVirtualThreadPerTaskExecutor()} + {@code submit(Callable)}.
 * + Chỉ cần CHẠY rồi quên -> {@code Thread.startVirtualThread()}.
 * + Phải đưa factory cho thư viện khác -> {@code Thread.ofVirtual().factory()}.
 * - Câu định nghĩa quan trọng của trang gốc: "A virtual thread is an instance of Thread.
 * Cancellation works the same way as for platform threads."
 * - Tức là KHÔNG có lớp {@code VirtualThread} công khai để bạn khai báo kiểu. Kiểu vẫn là
 * {@code Thread}. Muốn biết nó loại gì thì hỏi {@code isVirtual()} (bài t3).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. TRY-WITH-RESOURCES TRÊN EXECUTOR SERVICE - TIỆN NHƯNG CÓ BẪY
 * - Từ Java 19, {@code ExecutorService} kế thừa {@code AutoCloseable}, nên viết được
 * {@code try (var executor = ...)}. Trang gốc dùng dạng này trong mọi ví dụ.
 * - Phải biết {@code close()} làm gì: nó gọi {@code shutdown()} rồi CHẶN cho tới khi MỌI tác
 * vụ đã nộp chạy xong. Không có timeout.
 * - Hệ quả thực tế - hai cái bẫy:
 * + Dấu ngoặc nhọn đóng khối try chính là một điểm CHẶN. Nhìn code thấy vòng lặp submit chạy
 * vèo xong, nhưng chương trình đứng ở dấu ngoặc đóng. Người mới hay tưởng bị treo.
 * + ĐỪNG viết try-with-resources này trong một method service được gọi mỗi request mà bên
 * trong có tác vụ dài. Request sẽ bị giữ cho tới khi tác vụ cuối cùng xong.
 * + Nếu thread đang đợi ở {@code close()} bị interrupt, nó KHÔNG hủy các tác vụ con mà
 * chuyển sang {@code shutdownNow()} rồi chờ tiếp. Cần hủy dứt khoát thì xem Structured
 * Concurrency ở bài t4.
 * <p>
 * 6. "ĐỪNG POOL VIRTUAL THREAD" - VÌ SAO CÂU NÀY LẶP LẠI SUỐT SECTION
 * - Pool sinh ra để tái dùng một thứ ĐẮT. Virtual thread rẻ, nên pool mất lý do tồn tại.
 * - Tệ hơn: pool có số thread cố định, mà đó đúng là cái trần ta vừa bỏ công dỡ bỏ ở bài t1.
 * Pool virtual thread = dỡ trần xong dựng lại trần mới, tốn công vô ích.
 * - Sai lầm điển hình phải tránh:
 * <pre>{@code
 * // SAI - vừa vô nghĩa vừa dựng lại đúng cái trần cần phá
 * Executors.newFixedThreadPool(200, Thread.ofVirtual().factory());
 * }</pre>
 * - Muốn giới hạn tải thì giới hạn Ở TÀI NGUYÊN, không giới hạn ở thread. Đó là nội dung
 * bài t5.
 * <p>
 * 7. ĐẶT TÊN THREAD - VIỆC NHỎ NHƯNG ĐỪNG BỎ QUA
 * - Virtual thread mặc định KHÔNG CÓ TÊN ({@code getName()} trả về chuỗi rỗng), khác hẳn
 * platform thread vốn tự có "Thread-0", "Thread-1".
 * - Hệ quả trực tiếp: pattern {@code %thread} của Logback/Log4j2 in ra chỗ trống. Thread dump
 * cũng khó đọc.
 * - Nên luôn đặt tên khi tự tạo: {@code Thread.ofVirtual().name("import-", 1)}. Với tác vụ
 * nghiệp vụ, tên nên mang thông tin truy vết (mã đơn hàng, id job).
 * - Lưu ý ngược lại: {@code newVirtualThreadPerTaskExecutor()} cũng tạo thread không tên.
 * Cần tên thì truyền factory riêng:
 * {@code Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("task-", 1).factory())}.
 * <p>
 * 8. SO SÁNH VỚI TYPESCRIPT - CHỖ TƯƠNG ỨNG VÀ CHỖ KHÔNG
 * - {@code executor.submit(callable)} gần giống việc gọi một hàm async và nhận Promise:
 * cả hai đều trả ngay một tay cầm (handle) cho kết quả tương lai.
 * - Chỗ KHÔNG tương ứng, và là chỗ dễ nhầm nhất:
 * + {@code Promise.all([...])} chạy các promise đã khởi động sẵn; {@code invokeAll(...)}
 * (bài t4) nhận danh sách tác vụ CHƯA chạy rồi tự khởi động.
 * + Promise là eager - tạo xong là chạy. {@code Callable} là lazy - chỉ chạy khi được nộp.
 * + JS không có gì tương ứng với {@code close()} chặn ở cuối khối. Nhớ kỹ mục 5.
 * <p>
 * 9. TRONG SPRING BOOT THÌ CODE NÀY NẰM Ở ĐÂU
 * - Thực tế bạn hiếm khi tự gọi {@code newVirtualThreadPerTaskExecutor()} trong controller.
 * Thường là:
 * + Bật {@code spring.threads.virtual.enabled=true} rồi để Tomcat lo tầng request.
 * + Khai báo một bean {@code AsyncTaskExecutor} dựa trên virtual thread cho {@code @Async}.
 * + Chỉ tự tạo executor khi cần "chẻ một request thành N lời gọi song song rồi gộp lại" -
 * đúng ca dùng của bài t4.
 * - Nhớ nguyên tắc: tự tạo executor trong code nghiệp vụ là ngoại lệ, không phải mặc định.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn cách tạo virtual thread? Cách nào là mặc định nên dùng?
 * - Vì sao {@code newVirtualThreadPerTaskExecutor()} KHÔNG phải là một pool?
 * - Muốn lấy giá trị trả về thì bắt buộc đi đường nào? Vì sao hai đường kia không được?
 * - {@code close()} của {@code ExecutorService} làm gì? Nó có timeout không?
 * - Vì sao {@code newFixedThreadPool(200, Thread.ofVirtual().factory())} là sai?
 * - Virtual thread mặc định tên là gì? Điều đó gây phiền ở đâu?
 * - Có lớp public nào tên {@code VirtualThread} để khai báo kiểu không?
 */
public class t2_CreatingVirtualThreads {

    void main() {
        // TODO 1 (mục 1): Chạy đúng khung try-with-resources của trang gốc với 1_000_000 tác
        //   vụ, mỗi tác vụ LockSupport.parkNanos(1_000_000_000) rồi tăng một AtomicInteger.
        //   Đo tổng thời gian. Ghi lại cả thời gian từ lúc vòng lặp submit xong đến lúc ra
        //   khỏi khối try - đó chính là phần close() chặn ở mục 5.
        //
        // TODO 2 (mục 1, mục 6): Lặp lại TODO 1 nhưng thay bằng
        //   Executors.newFixedThreadPool(200). Chương trình chạy bao lâu? Giải thích con số
        //   bằng định luật Little ở bài t1.
        //
        // TODO 3 (mục 2, mục 7): Tạo ThreadFactory có tên "request-" rồi sinh 5 thread, in
        //   getName() và isVirtual() của từng cái. Sau đó tạo 5 thread bằng
        //   newVirtualThreadPerTaskExecutor() và in tên bên trong tác vụ. So sánh output.
        //
        // TODO 4 (mục 3): Dùng unstarted() tạo một thread, in getState() TRƯỚC khi start(),
        //   sau start(), và sau join(). Dán ba trạng thái vào note.
        //
        // TODO 5 (mục 4): Thử submit một Callable<String> qua ExecutorService và lấy kết quả;
        //   rồi thử làm điều tương tự chỉ với Thread.startVirtualThread. Viết lại xem bạn
        //   phải tự thêm những gì (biến chia sẻ, CountDownLatch...) để lấy được kết quả.
        //   Kết luận vì sao trang gốc nói chỉ executor mới nhận Callable.
        //
        // TODO 6 (mục 5): Viết một method giả làm service: bên trong có
        //   try (var ex = newVirtualThreadPerTaskExecutor()) nộp 3 tác vụ, mỗi tác vụ sleep
        //   2 giây. Đo thời gian method đó trả về. Xác nhận là ~2 giây chứ không phải ~0.
        //
        // TODO 7 (mục 6): Viết ra giấy (hoặc note) ba lý do vì sao pool virtual thread là
        //   sai, rồi tìm trong các thư viện dự án bạn đang dùng xem có chỗ nào đang nhận
        //   ThreadFactory - đó là chỗ có thể truyền virtual thread factory vào một cách
        //   chính đáng.
    }
}
