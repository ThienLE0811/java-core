package com.javacore.api.virtual_threads;

/**
 * Rate Limiting
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 5/8 - mục thứ năm của trang Virtual Threads.
 * - Bài SÁT PRODUCTION NHẤT của cả section. Bốn bài trước dạy cách tháo trần; bài này dạy
 * chuyện xảy ra ngay sau khi tháo trần: tải dồn xuống hệ thống phía dưới.
 * - Một câu cho cả bài: thread pool ngày xưa vô tình kiêm luôn vai bộ hạn chế tải; bỏ pool
 * là mất cái phanh đó, nên phải lắp phanh mới - ĐÚNG CHỖ, tức là ở từng tài nguyên.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#rate-limiting">dev.java - Virtual Threads, mục Rate Limiting</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - RATE LIMITING (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. VẤN ĐỀ
 * - Virtual thread cho phép hàng trăm nghìn tác vụ đồng thời. Nếu mỗi tác vụ gọi vào một
 * dịch vụ bên ngoài, bạn vừa biến ứng dụng của mình thành nguồn tấn công dịch vụ đó.
 * - Trước đây {@code newFixedThreadPool(20)} ngầm bảo đảm "không bao giờ quá 20 lời gọi
 * đồng thời". Không ai viết dòng comment nào về việc đó, nhưng nó là lá chắn thật.
 * - Bỏ pool đi thì lá chắn biến mất cùng. Đây là chỗ mà một bản migrate "chỉ đổi executor"
 * làm sập hệ thống phía sau.
 * <p>
 * 2. NGUYÊN TẮC
 * - Nguyên văn: "With virtual threads, you should use alternative mechanisms for controlling
 * access to limited resources."
 * - Chuyển trọng tâm: đừng giới hạn TỔNG SỐ TÁC VỤ, hãy bảo vệ TỪNG TÀI NGUYÊN khan hiếm.
 * - Vì sao khác biệt này quan trọng: một request có thể chạm nhiều tài nguyên với giới hạn
 * khác nhau (DB cho 50 kết nối, đối tác A cho 10 lời gọi/giây, đối tác B không giới hạn).
 * Một con số chung ở tầng thread không thể diễn tả được ba ràng buộc đó, còn ba bộ giới hạn
 * riêng thì diễn tả chính xác.
 * - Cách làm theo loại tài nguyên:
 * + Kết nối cơ sở dữ liệu: đã có CONNECTION POOL, dùng đúng nó, không chế thêm.
 * + Dịch vụ web bên ngoài: giới hạn riêng cho từng dịch vụ.
 * + Trường hợp tổng quát: {@code Semaphore}.
 * <p>
 * 3. SEMAPHORE
 * - Ví dụ trang gốc: một semaphore cho phép vài lời gọi đồng thời; vượt quá thì
 * {@code acquire()} CHẶN - và điều đó chấp nhận được, vì "blocking is cheap".
 * - Khung code chuẩn, {@code release()} BẮT BUỘC nằm trong {@code finally}:
 * <pre>{@code
 * private static final Semaphore GIOI_HAN = new Semaphore(10);
 *
 * String goiDichVu(String id) throws InterruptedException {
 *     GIOI_HAN.acquire();
 *     try {
 *         return httpClient.send(...);
 *     } finally {
 *         GIOI_HAN.release();
 *     }
 * }
 * }</pre>
 * - Đọc lại logic cho thấm: 100.000 virtual thread vẫn được tạo, vẫn chạy, nhưng 99.990
 * cái đang đứng chờ ở {@code acquire()}. Chúng không tốn OS thread nào. Đúng 10 lời gọi
 * thật sự ra khỏi máy. Đây là cách virtual thread và rate limiting ăn khớp nhau.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. TRONG SPRING BOOT - CÁI PHANH NÀO BIẾN MẤT, CÁI NÀO CÒN
 * - Khi bật {@code spring.threads.virtual.enabled=true}:
 * + {@code server.tomcat.threads.max} (mặc định 200) MẤT TÁC DỤNG. Cái trần cũ bảo vệ bạn
 * suốt nhiều năm đã biến mất, và không có cảnh báo nào.
 * + {@code spring.datasource.hikari.maximum-pool-size} (mặc định 10) TRỞ THÀNH giới hạn
 * thật sự duy nhất trên đường vào database.
 * - Hệ quả phải tính trước: 10.000 request đồng thời sẽ cùng gọi {@code getConnection()},
 * 10 cái được phục vụ, 9.990 cái xếp hàng. Hàng đợi đó có timeout
 * ({@code spring.datasource.hikari.connection-timeout}, mặc định 30 giây), nên quá tải sẽ
 * hiện ra dưới dạng một trận mưa {@code SQLTransientConnectionException}.
 * - Nên: trước khi bật virtual thread, rà lại connection-timeout và tăng giám sát hàng đợi
 * Hikari ({@code hikaricp.connections.pending} qua Micrometer).
 * - Lưu ý tinh tế về HikariCP: nó vốn được thiết kế cho platform thread. Bản thân
 * {@code getConnection()} chặn, và điều đó vẫn ổn trên virtual thread - nhưng nếu phiên bản
 * driver JDBC bạn dùng còn {@code synchronized} quanh I/O thì xem bài t6.
 * <p>
 * 5. SEMAPHORE - BA LỖI HAY GẶP
 * - 5a. Quên {@code release()} khi có exception. Không có {@code finally} thì semaphore rò
 * rỉ dần cho tới khi về 0 và mọi thứ đứng im VĨNH VIỄN. Loại sự cố này rất khó chẩn đoán vì
 * không có exception, không có log, chỉ có "hệ thống treo sau vài giờ".
 * - 5b. {@code acquire()} không timeout. Dùng
 * {@code tryAcquire(2, TimeUnit.SECONDS)} rồi trả lỗi 429/503 cho người gọi thì tốt hơn là
 * chờ vô hạn - chờ vô hạn chỉ dời chỗ tắc nghẽn chứ không giải quyết.
 * - 5c. Tạo {@code Semaphore} mới mỗi lần gọi. Nó phải là {@code static final} hoặc một bean
 * singleton, vì ý nghĩa của nó là "tổng số lượt đồng thời trong CẢ TIẾN TRÌNH".
 * - Mẹo nhớ: {@code Semaphore} với virtual thread giống cái cửa quay ở lối vào tòa nhà -
 * ai cũng vào được sảnh (tạo thread rẻ), nhưng chỉ N người qua được cửa một lúc.
 * <p>
 * 6. SEMAPHORE KHÔNG PHẢI RATE LIMIT - PHÂN BIỆT HAI KHÁI NIỆM
 * - {@code Semaphore} giới hạn SỐ LƯỢT ĐỒNG THỜI (concurrency limit): tối đa 10 lời gọi
 * đang bay cùng lúc.
 * - Rate limit đúng nghĩa giới hạn SỐ LƯỢT TRÊN ĐƠN VỊ THỜI GIAN: tối đa 100 lời gọi mỗi
 * giây, bất kể mỗi lời gọi nhanh hay chậm.
 * - Hai thứ này KHÔNG thay thế nhau. Đối tác ghi trong hợp đồng "100 req/s" thì
 * {@code Semaphore(10)} không bảo đảm được điều đó: nếu mỗi lời gọi chỉ mất 1ms, bạn bắn ra
 * tới 10.000 req/s mà vẫn không vượt semaphore.
 * - Cần rate limit thật thì dùng thuật toán token bucket - Resilience4j
 * ({@code RateLimiter}) hoặc Guava {@code RateLimiter}. Trang gốc không nhắc, nhưng trong
 * production đây là khác biệt làm bạn mất tài khoản API.
 * <p>
 * 7. BULKHEAD VÀ BACKPRESSURE - HAI TỪ NÊN BIẾT
 * - BULKHEAD (vách ngăn tàu thủy): mỗi dịch vụ phía dưới có một semaphore RIÊNG, để dịch vụ
 * B chậm không làm cạn tài nguyên của dịch vụ A. Đây chính xác là điều mục 2 của trang gốc
 * đang mô tả, chỉ là không gọi tên.
 * - BACKPRESSURE: khi hàng đợi đầy, phải TỪ CHỐI SỚM thay vì nhận bừa rồi chờ. Trả 503 sau
 * 50ms tốt hơn trả 504 sau 30 giây - cho cả người dùng lẫn hệ thống.
 * - Đây là chỗ WebFlux/Reactive Streams vẫn còn giá trị riêng: backpressure có sẵn trong mô
 * hình của nó, còn với virtual thread bạn phải TỰ lắp vào. Nhắc lại ý ở bài t1: virtual
 * thread không thay thế hoàn toàn reactive.
 * <p>
 * 8. QUY TẮC ĐẶT GIỚI HẠN Ở ĐÂU - BA CÂU HỎI
 * - Trước khi viết một {@code Semaphore}, hỏi theo thứ tự:
 * + Tài nguyên này đã có bộ giới hạn sẵn chưa? (connection pool, HTTP client có
 * {@code maxConnectionsPerRoute}, message broker có prefetch). Có rồi thì chỉnh cái đó.
 * + Giới hạn thật là đồng thời hay theo thời gian? (mục 6)
 * + Khi chạm giới hạn thì nên CHỜ hay nên TỪ CHỐI? Tác vụ nền thì chờ; request của người
 * dùng đang ngồi trước màn hình thì nên từ chối nhanh.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Thread pool ngày xưa vô tình đóng vai trò gì ngoài việc tái dùng thread?
 * - Nguyên tắc của trang gốc: giới hạn ở đâu, và vì sao không giới hạn ở tầng thread?
 * - Vì sao {@code acquire()} chặn lại chấp nhận được với virtual thread?
 * - {@code release()} phải đặt ở đâu? Quên thì hậu quả là gì, và vì sao khó chẩn đoán?
 * - Bật virtual thread trong Spring Boot thì cài đặt nào mất tác dụng, cài đặt nào trở
 * thành nút cổ chai?
 * - Phân biệt concurrency limit và rate limit. {@code Semaphore(10)} bảo đảm được "100
 * req/s" không?
 * - Bulkhead là gì? Backpressure là gì? Virtual thread có sẵn cái nào trong hai cái đó?
 */
public class t5_RateLimiting {

    void main() {
        // TODO 1 (mục 1): Viết một "dịch vụ bên ngoài" giả có đếm số lời gọi ĐANG chạy đồng
        //   thời (AtomicInteger tăng ở đầu, giảm ở cuối) và ghi lại đỉnh cao nhất. Gọi nó từ
        //   10.000 virtual thread KHÔNG có giới hạn nào. In đỉnh. Con số đó chính là thứ bạn
        //   vừa bắn vào hệ thống của người khác.
        //
        // TODO 2 (mục 3): Thêm Semaphore(10) vào TODO 1 theo đúng khung có finally. Chạy lại,
        //   xác nhận đỉnh đúng bằng 10. Đo thêm tổng thời gian và so với TODO 1.
        //
        // TODO 3 (mục 5a): Cố tình bỏ finally, cho 1% tác vụ ném exception. Chạy 10.000 tác
        //   vụ và in availablePermits() ở cuối. Quan sát nó tụt dần. Đây là mô phỏng một sự
        //   cố production thật - ghi lại cảm giác "không có log nào báo gì cả".
        //
        // TODO 4 (mục 5b): Đổi acquire() thành tryAcquire(200, MILLISECONDS). Đếm bao nhiêu
        //   tác vụ bị từ chối. Thiết kế thông điệp lỗi bạn sẽ trả cho client trong trường hợp
        //   này (mã HTTP nào?).
        //
        // TODO 5 (mục 6): Chứng minh bằng code rằng Semaphore KHÔNG phải rate limit: đặt
        //   Semaphore(10) nhưng mỗi lời gọi chỉ mất 1ms, rồi đếm số lời gọi thực hiện trong
        //   1 giây. So với con số 100/giây mà "hợp đồng" yêu cầu.
        //
        // TODO 6 (mục 4): Trong một dự án Spring Boot thật (hoặc dự án nháp), bật
        //   spring.threads.virtual.enabled=true, đặt hikari maximum-pool-size=5, rồi bắn
        //   500 request đồng thời vào một endpoint có truy vấn DB. Ghi lại exception nhận
        //   được và thời điểm nó xuất hiện. Sau đó thêm Semaphore hoặc chỉnh pool và so sánh.
        //
        // TODO 7 (mục 7, mục 8): Liệt kê mọi tài nguyên bên ngoài mà một API thật của bạn
        //   chạm tới. Với từng cái, trả lời ba câu hỏi ở mục 8 và ghi ra giới hạn dự định
        //   cùng hành vi khi chạm trần (chờ hay từ chối).
    }
}
