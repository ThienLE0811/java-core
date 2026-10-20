package com.javacore.api.virtual_threads;

/**
 * Why Virtual Threads?
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 1/8 - mục MỞ ĐẦU của trang Virtual Threads.
 * - Bài duy nhất trong section không có API nào để học. Nó chỉ trả lời một câu hỏi: vì sao
 * Java phải thêm một loại thread thứ hai sau 27 năm. Đọc kỹ, vì 7 bài sau chỉ là hệ quả.
 * - Một câu cho cả bài: virtual thread KHÔNG làm một tác vụ chạy nhanh hơn; nó làm cho việc
 * CHẶN (blocking) rẻ đi, nhờ đó bạn được quay lại viết code đồng bộ mà vẫn chịu tải cao.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#why">dev.java - Virtual Threads, mục Why Virtual Threads?</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WHY VIRTUAL THREADS? (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. MỐC THỜI GIAN - THREAD CÓ TỪ NGÀY ĐẦU
 * - Java hỗ trợ thread ngay từ bản 1.0 năm 1995. Đây là điểm trang gốc nhấn: thread không
 * phải thứ mới thêm, nó nằm trong ngôn ngữ từ đầu.
 * - Từ Java 1.2, mỗi {@code Thread} của Java được ánh xạ 1-1 vào một thread của HỆ ĐIỀU HÀNH.
 * Loại này từ nay gọi tên riêng là PLATFORM THREAD.
 * - Ghi nhớ cách gọi tên, cả section dùng xuyên suốt:
 * + platform thread = thread cũ = một OS thread thật.
 * + virtual thread = thread mới = một object trên heap, KHÔNG phải OS thread.
 * + carrier thread = platform thread đang "chở" một virtual thread chạy.
 * <p>
 * 2. PLATFORM THREAD ĐẮT Ở CHỖ NÀO
 * - Hai con số của trang gốc, nên thuộc:
 * + Khởi tạo tốn "a few thousand CPU instructions".
 * + Mỗi thread ngốn "a few megabytes of memory" (chủ yếu là stack do OS cấp phát sẵn).
 * - Hệ quả số học: vài MB x 10.000 thread = hàng chục GB. Không máy chủ nào gánh nổi. Đây
 * chính là lý do mô hình "mỗi request một thread" bị chặn trần ở vài trăm request đồng thời.
 * - Trang gốc nói thẳng cái lãng phí nằm ở đâu: các request đó "spend much of their time
 * blocking, waiting for a result from a database or another service."
 * - Đọc lại câu trên cho kỹ. Thread đắt tiền đó phần lớn thời gian KHÔNG làm gì cả - nó
 * đứng chờ mạng. Bạn trả vài MB RAM để mua một chỗ ngồi chờ.
 * <p>
 * 3. LỜI GIẢI CŨ - NON-BLOCKING API, VÀ CÁI GIÁ CỦA NÓ
 * - Cách chữa kinh điển trước virtual thread: đừng chặn, hãy đăng ký callback rồi trả thread
 * về cho pool, khi nào có kết quả thì callback chạy.
 * - Đây là nguyên lý của Node.js, của Netty, của WebFlux, của {@code CompletableFuture}.
 * - Trang gốc chê đúng một điểm, nguyên văn: cách này "gets unpleasant quickly, as the
 * callbacks nest ever more deeply."
 * - Cái mất không chỉ là thẩm mỹ. Khi tách ra callback, bạn mất luôn ba thứ quen thuộc:
 * + {@code try/catch} không bắt được lỗi xảy ra trong callback.
 * + Stack trace đứt đoạn, không còn chỉ ra được ai gọi ai.
 * + Debugger step-through vô dụng, breakpoint rơi vào thread khác.
 * <p>
 * 4. VIRTUAL THREAD LÀM GÌ - CƠ CHẾ MOUNT VÀ UNMOUNT
 * - Mốc phát hành: xuất hiện ở Java 19 (JEP 425), preview ở Java 20 (JEP 436), CHÍNH THỨC ở
 * Java 21 (JEP 444). Trang gốc ghi: "Virtual threads became a preview feature in Java 20
 * (JEP 436) and are final in Java 21."
 * - Cơ chế, nguyên văn: "Many virtual threads run on a platform thread. Whenever a virtual
 * thread blocks, it is unmounted, and the platform thread runs another virtual thread."
 * - Dịch sang hình ảnh: carrier thread là cái ghế, virtual thread là người ngồi. Người nào
 * phải đi chờ (gọi DB, gọi HTTP) thì ĐỨNG DẬY nhường ghế; stack của người đó được cất lên
 * heap. Khi kết quả về, họ xếp hàng xin một cái ghế bất kỳ còn trống để ngồi tiếp.
 * - Chú ý chỗ tinh tế: khi ngồi lại, có thể là MỘT CÁI GHẾ KHÁC. Nên không được giả định
 * "trước và sau một lệnh blocking tôi vẫn ở trên cùng một OS thread". Đây là gốc rễ của
 * mọi vấn đề {@code ThreadLocal} ở bài t7.
 * - Câu chốt của cả section, nguyên văn: "With virtual threads, blocking is cheap. When a
 * result is not immediately available, you simply block in a virtual thread."
 * <p>
 * 5. DÙNG KHI NÀO VÀ KHÔNG DÙNG KHI NÀO
 * - Trang gốc khoanh vùng rất hẹp, chỉ đúng khi CẢ HAI điều kiện cùng đúng:
 * + số lượng tác vụ đồng thời LỚN, và
 * + các tác vụ đó "mostly block on network I/O".
 * - Và nói thẳng chỗ vô ích: "no benefit for CPU-intensive tasks". Việc nặng CPU thì dùng
 * parallel stream hoặc fork-join, vì ở đó cái khan hiếm là LÕI CPU chứ không phải thread.
 * - Lý do số học, một dòng: tác vụ nặng CPU không bao giờ unmount, nên 10.000 virtual thread
 * vẫn phải xếp hàng qua đúng số carrier = số lõi. Không nhanh hơn, chỉ thêm chi phí lập lịch.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. BẮC CẦU TỪ TYPESCRIPT - CHỖ DỄ HIỂU NHẦM NHẤT
 * - Nếu bạn quen async/await của TS thì công thức nhớ là:
 * + Node.js: 1 thread + event loop. Hàm async trả Promise. Mọi I/O BẮT BUỘC phải await,
 * nếu lỡ chặn thật thì cả tiến trình đứng.
 * + Java virtual thread: N thread ảo trên M carrier. KHÔNG có từ khóa async/await, KHÔNG có
 * Promise. Bạn gọi hàm chặn bình thường, JVM tự tháo bạn ra khỏi carrier.
 * - Điểm giống nhau thực chất: cả hai đều cất "phần còn lại của việc đang làm" đi rồi chạy
 * việc khác. Khác nhau ở chỗ CẤT CÁI GÌ: TS do compiler cắt hàm thành state machine; Java do
 * JVM copy nguyên cái stack lên heap.
 * - Hệ quả thực tế rất quan trọng: trong TS, màu hàm lây lan (một hàm async thì mọi hàm gọi
 * nó phải async - "function coloring"). Java KHÔNG có vấn đề này. Code cũ viết đồng bộ chạy
 * nguyên xi trên virtual thread mà không sửa một dòng. Đây mới là giá trị lớn nhất.
 * <p>
 * 7. VÌ SAO POOL 200 THREAD CHẶN THROUGHPUT - ĐỊNH LUẬT LITTLE
 * - Công thức: throughput = số tác vụ chạy đồng thời / thời gian mỗi tác vụ.
 * - Thế số thật của một API Spring Boot gọi 1 DB query mất 50ms:
 * + Tomcat mặc định 200 thread -> 200 / 0,05s = 4.000 request/giây. TRẦN CỨNG, không thể
 * vượt dù CPU còn rảnh 90%.
 * + Cùng dịch vụ đó trên virtual thread, giới hạn chuyển sang connection pool và downstream.
 * - Đây là con số cần nhớ để trả lời "virtual thread giúp gì cho dự án tôi": nó dỡ bỏ cái
 * trần do SỐ THREAD đặt ra, không dỡ bỏ trần do CPU hay do database đặt ra.
 * - Và nhớ luôn mặt trái, bài t5 sẽ quay lại: dỡ trần ở tầng web nghĩa là tải dồn xuống
 * tầng dưới. Nút cổ chai không biến mất, nó DI CHUYỂN.
 * <p>
 * 8. VIRTUAL THREAD RẺ TỚI MỨC NÀO - SO SÁNH THẲNG
 * - Stack của virtual thread nằm trên HEAP, cấp phát theo nhu cầu, co giãn được. Khởi điểm
 * cỡ vài trăm byte đến vài KB thay vì vài MB.
 * - Nhờ vậy hàng triệu virtual thread là chuyện bình thường; hàng chục nghìn platform thread
 * thì không.
 * - Lịch chạy do một {@code ForkJoinPool} riêng của JVM đảm nhiệm (KHÁC
 * {@code ForkJoinPool.commonPool()} dùng cho parallel stream), mặc định parallelism = số lõi.
 * Tên cờ chỉnh được ở bài t6.
 * - Lưu ý về cách đo: đừng benchmark virtual thread bằng {@code Thread.sleep} rồi kết luận
 * "nhanh gấp 100 lần". Sleep là trường hợp lý tưởng nhất của unmount. Muốn con số có nghĩa
 * thì phải gọi HTTP hoặc JDBC thật.
 * <p>
 * 9. BẬT VIRTUAL THREAD TRONG SPRING BOOT - MỘT DÒNG
 * - Spring Boot 3.2 trở lên: {@code spring.threads.virtual.enabled=true} trong
 * application.properties. Chạy trên Java 21+.
 * - Một dòng đó đổi hai thứ: Tomcat xử lý mỗi request trên một virtual thread, và các
 * {@code @Async}/{@code @Scheduled} dùng executor virtual thread.
 * - Những thứ nó KHÔNG đổi, phải tự xử lý - đây là danh sách cần nhớ:
 * + {@code server.tomcat.threads.max} trở nên vô nghĩa, nên hàng rào bảo vệ cũ biến mất (t5).
 * + Connection pool ({@code spring.datasource.hikari.maximum-pool-size}) thành nút cổ chai
 * thật sự và thành thứ duy nhất còn chặn tải (t5).
 * + Mọi {@code ThreadLocal} trong ứng dụng cần rà lại (t7).
 * + Thư viện nào còn dùng {@code synchronized} quanh I/O cần kiểm tra pinning (t6).
 * <p>
 * 10. BA HIỂU LẦM PHỔ BIẾN - TỰ KIỂM TRA MÌNH CÓ MẮC KHÔNG
 * - "Virtual thread làm code tôi chạy nhanh hơn": SAI. Một request vẫn mất đúng từng ấy
 * mili-giây. Chỉ THÔNG LƯỢNG tăng, ĐỘ TRỄ của từng request không giảm.
 * - "Virtual thread thay thế reactive/WebFlux": chỉ đúng một nửa. Nó thay được phần "tránh
 * chặn thread". Nó KHÔNG thay được backpressure và các toán tử xử lý luồng dữ liệu của
 * Reactive Streams. Dự án đang chạy WebFlux ổn định thì không có lý do gì phải đổi.
 * - "Có virtual thread rồi thì khỏi lo thread-safe": SAI NGUY HIỂM NHẤT. Hai virtual thread
 * vẫn chạy song song thật trên hai lõi. Mọi race condition, mọi {@code synchronized}, mọi
 * {@code AtomicInteger} vẫn cần y như cũ. Thực tế còn khó hơn, vì số thread đồng thời tăng
 * gấp hàng trăm lần làm những race hiếm gặp trở nên thường xuyên.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Platform thread đắt ở hai chỗ nào? Hai con số của trang gốc là gì?
 * - Phân biệt ba từ: platform thread, virtual thread, carrier thread.
 * - Unmount xảy ra lúc nào? Sau khi mount lại, có chắc vẫn là carrier cũ không? Hệ quả?
 * - Trang gốc chê non-blocking API ở điểm gì? Ngoài thẩm mỹ, còn mất ba thứ gì nữa?
 * - Hai điều kiện để virtual thread có ích? Vì sao vô ích với tác vụ nặng CPU?
 * - Theo định luật Little, pool 200 thread với request 50ms cho throughput tối đa bao nhiêu?
 * - Vì sao "function coloring" của TS không xảy ra với Java virtual thread?
 * - Ba hiểu lầm ở mục 10, bạn từng tin cái nào?
 */
public class t1_WhyVirtualThreads {

    void main() {
        // TODO 1 (mục 2): Đo giới hạn thật của máy mình. Viết vòng lặp tạo platform thread
        //   (Thread.ofPlatform().start(...)), mỗi thread chỉ sleep 10 giây, đếm xem tạo được
        //   bao nhiêu cái trước khi OutOfMemoryError hoặc máy treo. Chạy với -Xmx512m.
        //   Ghi lại con số. Đây là cái trần mà cả section đang tìm cách phá.
        //
        // TODO 2 (mục 2, mục 8): Lặp lại TODO 1 với Thread.ofVirtual().start(...), cùng
        //   -Xmx512m. Đặt hai con số cạnh nhau trong note. Tỉ lệ là bao nhiêu?
        //
        // TODO 3 (mục 4): In Thread.currentThread() bên trong một virtual thread, TRƯỚC và
        //   SAU một lệnh blocking (ví dụ Thread.sleep(50)). Phần sau dấu @ là carrier thread.
        //   LƯU Ý khi làm: với MỘT thread chạy trên máy đang rảnh, carrier gần như luôn giữ
        //   nguyên - bộ lập lịch trả bạn về đúng worker cũ vì nó đang trống. Muốn thấy nó
        //   ĐỔI thì phải chạy đồng thời vài trăm tác vụ để các worker bận. Làm cả hai cách
        //   và ghi lại tỉ lệ số tác vụ bị đổi carrier.
        //
        // TODO 4 (mục 5): Viết hai tác vụ - một tác vụ sleep 1 giây (giả lập I/O), một tác vụ
        //   tính tổng 1 tỉ số nguyên (nặng CPU). Chạy 1.000 tác vụ mỗi loại, mỗi loại hai lần:
        //   một lần trên newFixedThreadPool(số lõi), một lần trên
        //   newVirtualThreadPerTaskExecutor(). Lập bảng 4 ô thời gian.
        //   Tự trả lời: ô nào virtual thread thắng đậm, ô nào gần như hòa? Vì sao?
        //
        // TODO 5 (mục 7): Với một API thật trong dự án bạn đang làm, đo thời gian trung bình
        //   một request (p50) rồi áp định luật Little để tính trần throughput với pool 200
        //   thread. So với số liệu thật nếu có. Khớp không?
        //
        // TODO 6 (mục 6): Viết cùng một việc "gọi 3 dịch vụ rồi gộp kết quả" theo hai cách:
        //   CompletableFuture chuỗi thenCombine, và virtual thread + blocking get. Đếm số
        //   dòng, rồi cố tình ném exception ở dịch vụ thứ 2 và so sánh stack trace hai bản.
        //
        // TODO 7 (mục 10): Viết một bộ đếm int thường, tăng nó trong 10.000 virtual thread,
        //   in kết quả cuối. Chạy 5 lần. Xác nhận bằng mắt rằng virtual thread KHÔNG hề giúp
        //   gì cho thread safety. Sau đó sửa bằng AtomicInteger.
    }
}
