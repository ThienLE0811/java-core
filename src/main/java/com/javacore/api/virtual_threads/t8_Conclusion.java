package com.javacore.api.virtual_threads;

/**
 * Conclusion
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 8/8 - mục cuối của trang Virtual Threads và là bài khép lại cả section.
 * - Trang gốc chốt bằng 5 gạch đầu dòng. File này chép lại 5 gạch đó, rồi thêm phần TỔNG KẾT
 * cả section và một checklist đưa virtual thread vào dịch vụ Spring Boot thật.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#conclusion">dev.java - Virtual Threads, mục Conclusion</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CONCLUSION (5 gạch đầu dòng)
 * ======================================================================
 * <p>
 * 1. NĂM Ý CHỐT
 * - 1a. Dùng virtual thread để TĂNG THÔNG LƯỢNG khi có nhiều tác vụ mà phần lớn thời gian
 * chúng chặn ở I/O mạng. Nguyên văn: "Use virtual threads to increase throughput when you
 * have many tasks that mostly block on network I/O".
 * - 1b. Lợi ích chính là được viết theo lối ĐỒNG BỘ quen thuộc, không phải chuỗi callback.
 * - 1c. ĐỪNG pool virtual thread. Cần hạn chế tải thì dùng cơ chế khác (bài t5).
 * - 1d. Theo dõi và xử lý pinning nếu có (bài t6).
 * - 1e. Giảm tối đa việc dùng {@code ThreadLocal} (bài t7).
 * - Để ý thứ tự: hai ý đầu là LỢI ÍCH, ba ý sau là THỨ PHẢI CANH CHỪNG. Tỉ lệ 2/3 đó phản
 * ánh đúng thực tế một bản migrate.
 * <p>
 * ======================================================================
 * TỔNG KẾT CẢ SECTION - NGOÀI TRANG GỐC
 * ======================================================================
 * <p>
 * 2. TÁM BÀI GOM LẠI THÀNH MỘT MẠCH
 * - t1: platform thread đắt (vài MB, vài nghìn lệnh CPU) nên không thể có nhiều. Virtual
 * thread rẻ nhờ unmount khi chặn. Chỉ có ích khi NHIỀU tác vụ và CHẶN Ở I/O MẠNG.
 * - t2: tạo bằng {@code newVirtualThreadPerTaskExecutor()} là chính; nhớ
 * {@code close()} chặn ở cuối khối try.
 * - t3: vẫn là {@code Thread}, nhưng daemon/priority/thread-group không còn tác dụng, và
 * các setter đó thất bại TRONG IM LẶNG.
 * - t4: blocking {@code get()} nay là cách viết đúng. Submit hết rồi mới get thì mới song song.
 * - t5: bỏ pool là bỏ luôn cái phanh. Lắp phanh lại ở TỪNG TÀI NGUYÊN.
 * - t6: pinning; nhưng JEP 491 (JDK 24) đã gỡ nguyên nhân lớn nhất.
 * - t7: {@code ThreadLocal} mất lý do tồn tại ở ca dùng "cache", còn đúng ở ca dùng "ngữ
 * cảnh request". {@code ScopedValue} là đường đi tiếp.
 * - t8: gói lại thành 5 câu.
 * <p>
 * 3. MỘT CÂU DUY NHẤT NẾU CHỈ ĐƯỢC NHỚ MỘT CÂU
 * - Virtual thread không làm gì nhanh hơn. Nó làm cho việc CHẶN hết tốn kém, nhờ đó mô hình
 * "mỗi tác vụ một thread" - vốn là cách viết dễ đọc nhất - lại dùng được ở quy mô lớn.
 * - Mọi rắc rối còn lại đều sinh ra từ một nguồn duy nhất: những thứ được thiết kế với giả
 * định "thread thì ít và đắt" nay sai. Pool sai, {@code ThreadLocal} cache sai,
 * {@code synchronized} quanh I/O sai, mọi giới hạn tải dựa vào số thread sai.
 * <p>
 * 4. NHỮNG GÌ SECTION NÀY KHÔNG DẠY - ĐỂ BIẾT MÌNH CÒN THIẾU GÌ
 * - Structured Concurrency ({@code StructuredTaskScope}, JEP 505): vẫn preview ở JDK 25.
 * Đây là bước tiếp theo tự nhiên sau bài t4.
 * - Cách bộ lập lịch hoạt động bên trong (ForkJoinPool, work stealing, continuation).
 * - Virtual thread với file I/O: {@code java.nio.file} trên nhiều hệ điều hành KHÔNG unmount
 * được thật sự, JDK phải đẩy sang thread riêng. Nên đừng kỳ vọng virtual thread giúp cho
 * khối lượng đọc ghi đĩa lớn - nó sinh ra cho I/O MẠNG, đúng như câu 1a.
 * - Đo lường và giám sát: JFR, Micrometer, cách đọc thread dump JSON (nhắc ở t3).
 * <p>
 * 5. CHECKLIST ĐƯA VIRTUAL THREAD VÀO MỘT DỊCH VỤ SPRING BOOT THẬT
 * - TRƯỚC KHI BẬT:
 * + Java 21+ và Spring Boot 3.2+. Ưu tiên JDK 24/25 để khỏi lo pinning do
 * {@code synchronized} (t6).
 * + Grep {@code ThreadLocal}, phân loại A/B/C theo mục 9 bài t7.
 * + Grep {@code setPriority}, {@code setDaemon}, {@code ThreadGroup},
 * {@code getAllStackTraces} theo mục 8 bài t3.
 * + Ghi lại các con số hiện tại để sau còn so: throughput, p99, kích thước pool Hikari,
 * {@code server.tomcat.threads.max}.
 * - KHI BẬT:
 * + {@code spring.threads.virtual.enabled=true}.
 * + Rà lại {@code spring.datasource.hikari.maximum-pool-size} và
 * {@code connection-timeout} - nay chúng là tuyến phòng thủ chính (t5).
 * + Thêm {@code Semaphore} hoặc rate limiter cho từng dịch vụ bên ngoài (t5).
 * + Bật JFR, canh sự kiện {@code VirtualThreadPinned} (t6).
 * - SAU KHI BẬT:
 * + Chạy tải thật, so sánh với bộ số đã ghi. Kỳ vọng: throughput tăng, p99 KHÔNG tăng.
 * + Giám sát hàng đợi connection pool ({@code hikaricp.connections.pending}) và bộ nhớ heap.
 * + Kiểm tra log còn in được tên thread và traceId không (t3 mục 7, t7 mục 5).
 * - DẤU HIỆU PHẢI TẮT NGAY: p99 tăng vọt trong khi throughput không tăng (thường là pinning
 * hoặc nút cổ chai đã dời xuống DB), hoặc heap tăng tuyến tính theo tải (thường là
 * {@code ThreadLocal} nhóm C).
 * <p>
 * 6. KHI NÀO KHÔNG NÊN BẬT - ĐỂ CÔNG BẰNG
 * - Dịch vụ nặng CPU (mã hóa, xử lý ảnh, tính toán): không được gì (t1 mục 5).
 * - Dịch vụ tải thấp và ổn định: không được gì đáng kể, mà vẫn gánh đủ rủi ro migrate.
 * - Đang chạy WebFlux ổn định: không có lý do đổi.
 * - Phụ thuộc thư viện cũ chưa kiểm chứng trên virtual thread, mà bạn không có môi trường
 * chạy tải thật để thử: hoãn lại.
 * - Nhớ nguyên tắc chung của nghề: một thay đổi hạ tầng không có SỐ ĐO trước-sau thì không
 * phải cải tiến, chỉ là thay đổi.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA CẢ SECTION
 * ======================================================================
 * <p>
 * 7. TRẢ LỜI ĐƯỢC HẾT THÌ COI NHƯ XONG BƯỚC 29/41
 * - Năm ý chốt của trang gốc, kể đủ không cần tra?
 * - Giải thích cho một đồng nghiệp trong 2 phút: virtual thread là gì, giúp gì, không giúp gì.
 * - Ba thứ phải canh chừng khi migrate, và mỗi thứ sửa bằng cách nào?
 * - Vì sao "đừng pool virtual thread" lại được nhắc tới ba lần trong section?
 * - Trên JDK 25, trong ba cái bẫy (pinning, rate limiting, thread local), cái nào đã nhẹ đi
 * và cái nào vẫn nguyên?
 * - Nếu sếp hỏi "bật cái này lên có tăng tốc hệ thống không", bạn trả lời thế nào cho đúng?
 * - Bạn sẽ đo những chỉ số nào trước và sau khi bật?
 * - Kể ba tình huống KHÔNG nên bật virtual thread.
 */
public class t8_Conclusion {

    void main() {
        // TODO 1 (mục 1): Viết lại 5 gạch đầu dòng của trang gốc bằng lời của mình, mỗi ý
        //   kèm số hiệu bài đã học nó (t1..t7). Không nhìn lại file.
        //
        // TODO 2 (mục 2): Gom tất cả số liệu bạn đo được ở 7 bài trước vào một bảng duy nhất
        //   trong note: số thread tối đa (t1), thời gian 1 triệu tác vụ (t2), đỉnh lời gọi
        //   đồng thời có và không có Semaphore (t5), thời gian bản synchronized và
        //   ReentrantLock trên JDK 25 (t6), số object SimpleDateFormat tạo ra (t7).
        //   Bảng này là thứ bạn mang ra khi thuyết phục người khác.
        //
        // TODO 3 (mục 5): Dựng một dịch vụ Spring Boot nhỏ có đúng một endpoint gọi DB và
        //   một endpoint gọi HTTP bên ngoài. Chạy tải (k6/JMeter/hey) hai lần - tắt và bật
        //   spring.threads.virtual.enabled - rồi lập bảng throughput, p50, p99, heap.
        //   Đây là bài tập tổng kết của cả bước 29.
        //
        // TODO 4 (mục 5): Trên dịch vụ ở TODO 3, cố tình đặt hikari maximum-pool-size=2 rồi
        //   chạy lại tải. Chứng minh bằng số liệu rằng nút cổ chai đã DI CHUYỂN chứ không
        //   biến mất (ý ở t1 mục 7).
        //
        // TODO 5 (mục 6): Viết một đoạn ngắn (5-10 dòng) trả lời câu hỏi "dự án hiện tại của
        //   tôi có nên bật virtual thread không", có lập luận dựa trên đặc điểm tải thật của
        //   dự án. Nếu câu trả lời là KHÔNG thì cũng ghi rõ vì sao - đó cũng là một kết luận
        //   đúng.
        //
        // TODO 6 (mục 4): Đọc JEP 505 và viết lại bài tập t4 TODO 8 bằng StructuredTaskScope.
        //   Ghi lại ba thứ nó làm được mà ExecutorService không làm được.
    }
}
