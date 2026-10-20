package com.javacore.api.virtual_threads;

/**
 * Thread API Changes
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 3/8 - mục thứ ba của trang Virtual Threads.
 * - Bài liệt kê: lớp {@code Thread} có gì mới, có gì bị gỡ, và năm chỗ virtual thread hành
 * xử KHÁC platform thread dù cùng kiểu {@code Thread}.
 * - Một câu cho cả bài: virtual thread dùng chung lớp {@code Thread} nhưng BỎ QUA một loạt
 * tính năng cũ của nó - những method đó không ném lỗi, chúng im lặng không làm gì. Đó mới
 * là chỗ nguy hiểm.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#api-changes">dev.java - Virtual Threads, mục Thread API Changes</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THREAD API CHANGES (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. NHỮNG GÌ GIỮ NGUYÊN
 * - Câu nền của cả mục: "A virtual thread is an instance of Thread." Không có lớp con công
 * khai, không có kiểu riêng để khai báo.
 * - Hủy tác vụ vẫn y như cũ: "Cancellation works the same way as for platform threads",
 * tức là {@code interrupt()} đặt cờ, còn code của bạn phải tự kiểm tra cờ đó hoặc đang nằm
 * trong một lệnh blocking có khả năng ném {@code InterruptedException}.
 * - Nhắc lại cho chắc: {@code interrupt()} KHÔNG giết thread. Nó chỉ xin thread tự dừng.
 * <p>
 * 2. METHOD MỚI THÊM
 * - {@code boolean Thread.isVirtual()} - hỏi một thread là loại gì. Có cả bản instance và
 * cách dùng quen thuộc {@code Thread.currentThread().isVirtual()}.
 * - {@code Thread.join(Duration)} - bản nạp chồng nhận {@code Duration} thay vì cặp
 * (long millis, int nanos) khó đọc.
 * - {@code Thread.sleep(Duration)} - tương tự.
 * - {@code Thread.ofVirtual()} / {@code Thread.ofPlatform()} / {@code Thread.Builder} - đã
 * học ở bài t2.
 * <p>
 * 3. METHOD BỊ DEPRECATE HOẶC BỊ GỠ
 * - {@code getId()} bị deprecate vì nó KHÔNG final (lớp con ghi đè được, nên không tin
 * được). Thay bằng {@code threadId()} - method final, luôn trả về id thật.
 * - Nguyên văn của trang gốc về nhóm method cổ: "Thread.suspend() and Thread.resume() was
 * removed from the JDK 23, and Thread.stop() was removed from the JDK 26. These methods
 * have been deprecated since Java 1.2 and deprecated for removal since Java 18."
 * - Mốc cần nhớ gọn: suspend/resume gỡ ở JDK 23, stop gỡ ở JDK 26, cả ba deprecate từ Java
 * 1.2 (tức là gần 30 năm) và deprecated-for-removal từ Java 18.
 * - Vì sao chúng sai từ đầu: {@code stop()} ném {@code ThreadDeath} vào GIỮA bất kỳ lệnh
 * nào, nên có thể cắt ngang một khối {@code synchronized} và để lại dữ liệu nửa chừng,
 * nhưng vẫn nhả khóa. {@code suspend()} giữ nguyên khóa khi đóng băng thread, gây deadlock.
 * <p>
 * 4. NĂM CHỖ VIRTUAL THREAD HÀNH XỬ KHÁC - PHẢI THUỘC
 * - 4a. THREAD GROUP: mọi virtual thread nằm trong CÙNG MỘT thread group, và không có API
 * nào cho bạn đổi sang group khác.
 * - 4b. ĐỘ ƯU TIÊN: mọi virtual thread đều là {@code NORM_PRIORITY}. Gọi
 * {@code setPriority()} KHÔNG có tác dụng, và cũng KHÔNG ném lỗi.
 * - 4c. DAEMON: mọi virtual thread đều là daemon thread. Gọi {@code setDaemon(false)} KHÔNG
 * có tác dụng. Xem mục 5, đây là cái bẫy lớn nhất bài.
 * - 4d. KHÔNG BIẾT ĐƯỢC CARRIER: không có cơ chế nào hỏi "virtual thread này đang chạy trên
 * platform thread nào".
 * - 4e. STACK TRACE TOÀN CỤC: {@code Thread.getAllStackTraces()} chỉ trả về các PLATFORM
 * thread, virtual thread KHÔNG có trong đó.
 * - Điểm chung của 4b và 4c đáng gạch chân: các setter đó THẤT BẠI TRONG IM LẶNG. Code cũ
 * gọi {@code setPriority(MAX_PRIORITY)} vẫn biên dịch, vẫn chạy, chỉ là không còn tác dụng
 * gì. Không có cảnh báo nào cho bạn biết.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. "LUÔN LÀ DAEMON" - HẬU QUẢ CỤ THỂ, GẶP NGAY BÀI ĐẦU TIÊN
 * - Luật của JVM: JVM thoát khi KHÔNG CÒN non-daemon thread nào. Virtual thread luôn là
 * daemon, nên JVM KHÔNG BAO GIỜ chờ virtual thread.
 * - Hệ quả trực tiếp, lỗi mà gần như ai cũng mắc một lần:
 * <pre>{@code
 * void main() {
 *     Thread.startVirtualThread(() -> System.out.println("xin chao"));
 * }   // main kết thúc -> JVM thoát -> rất có thể KHÔNG in ra gì cả
 * }</pre>
 * - Ba cách xử lý đúng:
 * + Giữ tham chiếu {@code Thread} rồi {@code join()}.
 * + Dùng {@code ExecutorService} trong try-with-resources - {@code close()} tự chờ (bài t2).
 * + Dùng {@code StructuredTaskScope} (bài t4).
 * - Nhớ mẹo chẩn đoán: chương trình chạy "không ra gì cả", không lỗi, không exception -
 * nghĩ ngay tới daemon thread chưa kịp chạy.
 * <p>
 * 6. ĐỌC CHUỖI toString() CỦA VIRTUAL THREAD
 * - Dạng điển hình: {@code VirtualThread[#21,request-3]/runnable@ForkJoinPool-1-worker-2}
 * - Giải mã từng phần, hữu ích khi đọc log:
 * + {@code #21} - giá trị {@code threadId()}.
 * + {@code request-3} - tên bạn đặt; để trống nếu không đặt (xem mục 7 bài t2).
 * + {@code runnable} - trạng thái.
 * + Phần sau dấu @ - CARRIER THREAD hiện tại. Đây là cách duy nhất "nhìn trộm" được carrier,
 * dù mục 4d nói không có API chính thức. Chỉ dùng để học, đừng parse chuỗi này trong code.
 * - Chạy cùng một virtual thread qua vài lệnh blocking rồi in lại: phần sau @ sẽ đổi. Đó là
 * bằng chứng mắt thấy của unmount/mount ở bài t1.
 * <p>
 * 7. THREAD DUMP VỚI HÀNG TRIỆU THREAD
 * - Mục 4e nói {@code getAllStackTraces()} bỏ qua virtual thread. Công cụ cũ dựa trên đó
 * (một số agent giám sát) sẽ mù.
 * - Cách đúng trên JDK 21+: {@code jcmd <pid> Thread.dump_to_file -format=json <file>}.
 * Lệnh này liệt kê CẢ virtual thread, xuất JSON để xử lý bằng máy - vì vài triệu thread thì
 * không ai đọc bằng mắt được.
 * - {@code jstack} kiểu cũ vẫn chỉ cho platform thread. Ghi nhớ để khỏi mất thời gian.
 * <p>
 * 8. MỘT BIẾN THỂ CỦA "THẤT BẠI TRONG IM LẶNG" - DANH SÁCH CẦN RÀ KHI MIGRATE
 * - Khi bật virtual thread cho một dịch vụ cũ, rà đúng các dấu hiệu sau trong code và trong
 * thư viện bên thứ ba:
 * + {@code setPriority(...)} - từ nay vô tác dụng.
 * + {@code setDaemon(...)} - từ nay vô tác dụng.
 * + {@code new ThreadGroup(...)} hoặc logic gom nhóm theo group - không còn chia tách được.
 * + {@code Thread.getAllStackTraces()} - không còn nhìn thấy phần lớn thread của hệ thống.
 * + {@code ThreadLocal} - xem bài t7.
 * + {@code synchronized} quanh I/O - xem bài t6.
 * - Bốn dấu hiệu đầu KHÔNG làm chương trình lỗi, chúng chỉ làm giả định của bạn sai. Loại
 * lỗi này tốn thời gian nhất để tìm.
 * <p>
 * 9. SO VỚI TYPESCRIPT
 * - TS/Node không có khái niệm tương ứng với thread priority, thread group hay daemon, nên
 * cả mục 4 không có chỗ bắc cầu.
 * - Chỗ bắc cầu được duy nhất: ý niệm "tiến trình thoát khi không còn việc gì giữ nó lại".
 * Node giữ tiến trình sống nhờ handle đang mở (timer, socket); Java giữ JVM sống nhờ
 * non-daemon thread. Virtual thread giống một timer đã bị {@code unref()} - có chạy hay
 * không cũng không giữ tiến trình lại.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba method mới trên {@code Thread} mà bài này giới thiệu?
 * - Vì sao {@code getId()} bị deprecate còn {@code threadId()} thì không?
 * - suspend/resume gỡ ở JDK nào, stop gỡ ở JDK nào? Vì sao chúng sai về bản chất?
 * - Năm hành vi khác biệt ở mục 4, kể đủ không cần tra?
 * - {@code setPriority()} trên virtual thread xảy ra chuyện gì? Có ném exception không?
 * - Chương trình chỉ gọi {@code Thread.startVirtualThread(...)} rồi kết thúc main thì in ra
 * gì? Vì sao? Ba cách sửa?
 * - Muốn xem stack trace của toàn bộ virtual thread thì dùng lệnh gì?
 * - Đọc chuỗi {@code VirtualThread[#21,x]/runnable@ForkJoinPool-1-worker-2}, phần nào là
 * carrier thread?
 */
public class t3_ThreadApiChanges {

    void main() {
        // TODO 1 (mục 5): Viết đúng ví dụ ở mục 5 - startVirtualThread in một dòng rồi kết
        //   thúc main. Chạy 10 lần, đếm bao nhiêu lần in ra. Sau đó thêm join() và chạy lại.
        //   Dán cả hai kết quả vào note. Đây là bài học đắt nhất của cả bài.
        //
        // TODO 2 (mục 4b, 4c): Tạo một virtual thread, gọi setPriority(Thread.MAX_PRIORITY)
        //   và setDaemon(false), rồi in lại getPriority() và isDaemon(). Xác nhận không có
        //   exception nào và giá trị KHÔNG đổi. Thử lại setDaemon(false) với platform thread
        //   đang chạy để thấy nó ném IllegalThreadStateException - so sánh hai hành vi.
        //
        // TODO 3 (mục 4a, 4d): In getThreadGroup() của 5 virtual thread khác nhau và của 2
        //   platform thread. Xác nhận 5 cái đầu cùng một group.
        //
        // TODO 4 (mục 4e, mục 7): Tạo 1.000 virtual thread đang sleep, rồi in
        //   Thread.getAllStackTraces().size(). Con số có phản ánh 1.000 thread đó không?
        //   Sau đó chạy jcmd <pid> Thread.dump_to_file -format=json dump.json trong khi
        //   chương trình còn sống và đếm số entry trong file. So sánh hai con số.
        //
        // TODO 5 (mục 6): In Thread.currentThread() bên trong một virtual thread sau mỗi
        //   lần sleep(50), lặp 10 lần. Đánh dấu những lần phần sau dấu @ thay đổi.
        //   Nếu chạy một thread đơn lẻ mà carrier không bao giờ đổi thì KHÔNG phải bạn làm
        //   sai: máy rảnh thì bộ lập lịch trả bạn về đúng worker cũ. Chạy 200 tác vụ đồng
        //   thời rồi đếm xem bao nhiêu tác vụ bị đổi carrier.
        //
        // TODO 6 (mục 1): Viết một virtual thread chạy vòng lặp vô hạn CHỈ tính toán (không
        //   sleep), rồi gọi interrupt() từ main. Xác nhận nó KHÔNG dừng. Sau đó thêm
        //   if (Thread.currentThread().isInterrupted()) break; và xác nhận nó dừng.
        //   Kết luận: interrupt là lời đề nghị, không phải mệnh lệnh.
        //
        // TODO 7 (mục 2): Viết hàm nhỏ in ra "virtual" hay "platform" cho thread hiện tại,
        //   rồi gọi nó từ main, từ một virtual thread, và từ bên trong một parallel stream.
        //   Kết quả ở parallel stream có làm bạn bất ngờ không?
        //
        // TODO 8 (mục 8): Grep toàn bộ dự án thật của bạn cho setPriority, setDaemon,
        //   ThreadGroup, getAllStackTraces. Lập danh sách các chỗ sẽ âm thầm mất tác dụng
        //   nếu bật virtual thread.
    }
}
