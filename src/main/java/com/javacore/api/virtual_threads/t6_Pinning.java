package com.javacore.api.virtual_threads;

/**
 * Pinning
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 6/8 - mục thứ sáu của trang Virtual Threads.
 * - Pinning = virtual thread bị GHIM vào carrier thread, không unmount được. Khi nó chặn,
 * cả carrier thread chặn theo. Đây là cách duy nhất làm virtual thread mất hết ưu thế.
 * - CẢNH BÁO PHIÊN BẢN - đọc trước khi làm bài tập: trang gốc mô tả hành vi của JDK 21-23.
 * JEP 491 (JDK 24) đã ĐỔI LUẬT và XÓA cờ chẩn đoán chính. Repo này biên dịch trên JDK 25,
 * nên nhiều ví dụ của trang gốc sẽ KHÔNG tái hiện được. Mục 6 và 7 ghi rõ cả hai thời kỳ.
 * Không biết điều này thì sẽ ngồi gỡ nhầm trong nhiều giờ.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#pinning">dev.java - Virtual Threads, mục Pinning</a>
 * - Tài liệu bổ sung: <a href="https://openjdk.org/jeps/491">JEP 491 - Synchronize Virtual
 * Threads without Pinning</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn. Code thực hành bổ sung dần bên dưới theo
 * các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PINNING (mục 1 đến 5), ĐÚNG VỚI JDK 21-23
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA
 * - Virtual thread bị pinned khi bộ lập lịch KHÔNG THỂ tháo nó ra khỏi carrier thread.
 * - Hậu quả, nguyên văn: "When a pinned thread blocks, it cannot be unmounted. The carrier
 * thread is blocked" - và carrier đó không phục vụ được virtual thread nào khác.
 * - Nhìn bằng hình ảnh ghế ngồi ở bài t1: người bị ghim thì không đứng dậy nhường ghế được,
 * dù họ chỉ đang ngồi chờ.
 * <p>
 * 2. HAI NGUYÊN NHÂN THEO TRANG GỐC
 * - 2a. Đang ở trong một method hoặc khối {@code synchronized}.
 * - 2b. Đang gọi native method hoặc foreign function (FFM API).
 * <p>
 * 3. VÌ SAO NGUY HIỂM - LÀM MỘT PHÉP TÍNH
 * - Số carrier mặc định = số lõi CPU. Máy 8 lõi thì có 8 carrier.
 * - Nếu 8 virtual thread cùng bị ghim trong {@code synchronized} và cùng đang chờ mạng, thì
 * TOÀN BỘ ứng dụng đứng im, dù còn 99.992 virtual thread khác sẵn sàng chạy.
 * - Đây không phải chậm đi một chút. Đây là chết đứng. Và nó chỉ xảy ra lúc tải cao, tức là
 * lúc tệ nhất.
 * <p>
 * 4. CÁCH SỬA THEO TRANG GỐC
 * - Thay {@code synchronized} bằng {@code ReentrantLock} - {@code lock()} của
 * {@code ReentrantLock} là blocking "thân thiện" với virtual thread nên vẫn unmount được.
 * - Hoặc nâng lên JDK 24+, nơi {@code synchronized} không còn gây pinning.
 * - Khung chuyển đổi, nhớ {@code unlock()} phải nằm trong {@code finally}:
 * <pre>{@code
 * // Trước - ghim virtual thread trên JDK 21-23
 * synchronized (khoa) {
 *     goiMang();
 * }
 *
 * // Sau - không ghim ở mọi phiên bản
 * private final ReentrantLock khoa = new ReentrantLock();
 * khoa.lock();
 * try {
 *     goiMang();
 * } finally {
 *     khoa.unlock();
 * }
 * }</pre>
 * <p>
 * 5. CỜ CHỈNH VÀ CÔNG CỤ CHẨN ĐOÁN
 * - Hai cờ chỉnh bộ lập lịch (vẫn còn hiệu lực ở JDK 25):
 * + {@code jdk.virtualThreadScheduler.parallelism} - số carrier thread. Mặc định bằng số
 * lõi CPU.
 * + {@code jdk.virtualThreadScheduler.maxPoolSize} - số carrier tối đa.
 * - Chẩn đoán theo trang gốc: khởi động JVM với tùy chọn cảnh báo để phát hiện pinned thread
 * đang chặn. Lưu ý trang gốc nhấn mạnh: "You get only one warning per pinning location!"
 * Tức là chỉ cảnh báo MỘT lần cho mỗi vị trí trong code - thấy một dòng cảnh báo không có
 * nghĩa là chỉ xảy ra một lần.
 * - Cách còn lại, và là cách còn dùng được lâu dài: Java Flight Recorder, tìm hai sự kiện
 * {@code VirtualThreadPinned} và {@code VirtualThreadSubmitFailed}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. JEP 491 (JDK 24) ĐÃ ĐỔI LUẬT - PHẦN QUAN TRỌNG NHẤT CỦA BÀI
 * - Tên JEP: "Synchronize Virtual Threads without Pinning", vào JDK 24.
 * - Mục tiêu, nguyên văn: "improve the scalability of Java code that uses synchronized
 * methods and statements by arranging for virtual threads that block in such constructs to
 * release their underlying platform threads for use by other virtual threads."
 * - Phạm vi: bao gồm cả {@code Object.wait()} và các bản có timeout - "will similarly
 * unmount a virtual thread when waiting and blocking to re-acquire a monitor."
 * - Nghĩa là từ JDK 24 trở đi, NGUYÊN NHÂN 2a Ở MỤC 2 KHÔNG CÒN ĐÚNG NỮA.
 * - Bốn nguyên nhân pinning CÒN LẠI trên JDK 24+ (JEP ghi rõ chúng "should rarely cause
 * issues"):
 * + Đang nạp một lớp (phân giải tham chiếu tượng trưng tới class/interface) và bị chặn.
 * + Đang chặn bên trong một class initializer (khối {@code static}).
 * + Đang chờ một lớp được khởi tạo bởi thread khác.
 * + Gọi vào native code, và native code đó GỌI NGƯỢC về Java rồi thực hiện thao tác chặn
 * hoặc chặn trên một monitor.
 * - Sự kiện JFR {@code VirtualThreadPinned} được GIỮ LẠI và còn được làm giàu thêm: nó báo
 * cả LÝ DO bị ghim lẫn DANH TÍNH của carrier thread.
 * <p>
 * 7. CỜ {@code jdk.tracePinnedThreads} - ĐÃ BỊ XÓA, ĐỪNG MẤT CÔNG
 * - Cờ chẩn đoán kinh điển thời JDK 21-23 là
 * {@code -Djdk.tracePinnedThreads=short} (hoặc {@code =full}). Hầu hết bài blog về virtual
 * thread đều chỉ dùng cờ này.
 * - JEP 491 đã GỠ nó. Lý do nêu trong JEP: cờ này "problematic since the stack traces are
 * printed while executing critical code", và không còn cần thiết "once the synchronized
 * keyword no longer pins virtual threads".
 * - Trên JDK 24+ (gồm JDK 25 của repo này), đặt cờ đó trên dòng lệnh KHÔNG CÓ TÁC DỤNG GÌ -
 * không lỗi, không cảnh báo, chỉ đơn giản là im lặng. Lại một ca "thất bại trong im lặng"
 * như bài t3.
 * - Cách chẩn đoán đúng trên JDK 24+: dùng JFR và xem sự kiện
 * {@code jdk.VirtualThreadPinned}. Ví dụ:
 * {@code java -XX:StartFlightRecording=duration=60s,filename=app.jfr MyApp} rồi mở bằng
 * JDK Mission Control, hoặc {@code jfr print --events VirtualThreadPinned app.jfr}.
 * <p>
 * 8. BẢNG TRA NHANH THEO PHIÊN BẢN - NÊN DÁN VÀO ĐẦU
 * - JDK 21, 22, 23:
 * + {@code synchronized} quanh I/O -> GHIM. Phải đổi sang {@code ReentrantLock}.
 * + Chẩn đoán: {@code -Djdk.tracePinnedThreads=full} hoặc JFR.
 * - JDK 24, 25+:
 * + {@code synchronized} quanh I/O -> KHÔNG còn ghim. Không cần đổi sang
 * {@code ReentrantLock} chỉ vì lý do pinning.
 * + Vẫn ghim: native/FFM gọi ngược về Java, và ba ca liên quan nạp/khởi tạo lớp (mục 6).
 * + Chẩn đoán: CHỈ còn JFR. Cờ cũ vô tác dụng.
 * - Kết luận thực dụng cho dự án: nếu bạn đang ở JDK 21 LTS thì mục 2-4 của trang gốc là
 * việc phải làm thật. Nếu đã lên JDK 25 thì phần lớn nỗi lo pinning tự biến mất, và công
 * sức nên dồn sang bài t5 (rate limiting) và t7 (thread local) - đó mới là chỗ còn đau.
 * <p>
 * 9. REENTRANTLOCK CÓ PHẢI LÚC NÀO CŨNG TỐT HƠN SYNCHRONIZED KHÔNG
 * - Không. Sau JDK 24, lý do "tránh pinning" đã mất, nên hãy chọn theo tiêu chí bình thường:
 * + {@code synchronized} ngắn gọn hơn, không thể quên nhả khóa, JIT tối ưu tốt (biased/
 * lightweight locking). Mặc định nên dùng cái này.
 * + {@code ReentrantLock} hơn ở các tính năng mà {@code synchronized} không có:
 * {@code tryLock()} có timeout, khóa có thể interrupt, khóa công bằng (fair), nhiều
 * {@code Condition} trên cùng một khóa, và lock/unlock ở hai method khác nhau.
 * - Đừng refactor hàng loạt {@code synchronized} thành {@code ReentrantLock} trên một dự án
 * chạy JDK 24+ chỉ vì đọc được bài blog viết thời JDK 21. Đó là thay đổi rủi ro mà không
 * đổi lại được gì.
 * <p>
 * 10. CHỖ PINNING THẬT SỰ HAY ẨN - TRONG THƯ VIỆN, KHÔNG PHẢI TRONG CODE BẠN
 * - Code nghiệp vụ hiếm khi {@code synchronized} quanh I/O. Nơi hay gặp là thư viện cũ:
 * driver JDBC đời cũ, HTTP client tự viết, thư viện logging ghi file đồng bộ, connection
 * pool tự chế.
 * - Nên quy trình đúng khi migrate ở JDK 21-23 là: bật chẩn đoán, chạy TẢI THẬT, rồi đọc
 * stack trace xem nó chỉ vào package nào - chứ không phải ngồi đọc code của chính mình.
 * - Và nhớ "one warning per pinning location" ở mục 5: đừng dựa vào SỐ LƯỢNG dòng cảnh báo
 * để đánh giá mức độ nghiêm trọng.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Pinning là gì? Vì sao một virtual thread bị ghim lại làm hỏng cả carrier?
 * - Hai nguyên nhân pinning theo trang gốc? Phiên bản JDK nào thì điều đó còn đúng?
 * - Máy 8 lõi, bao nhiêu virtual thread bị ghim và chặn là đủ làm ứng dụng đứng im?
 * - JEP 491 vào JDK mấy, đổi điều gì? Nó có bao gồm {@code Object.wait()} không?
 * - Bốn nguyên nhân pinning còn lại trên JDK 24+?
 * - Cờ {@code jdk.tracePinnedThreads} còn dùng được trên JDK 25 không? Chuyện gì xảy ra nếu
 * bạn vẫn đặt nó?
 * - Hai sự kiện JFR cần tìm tên là gì?
 * - Hai cờ chỉnh bộ lập lịch tên là gì, mặc định bao nhiêu?
 * - Trên JDK 25, còn lý do nào để đổi {@code synchronized} sang {@code ReentrantLock} không?
 */
public class t6_Pinning {

    void main() {
        // TODO 1 (mục 3, mục 8): Viết ví dụ pinning kinh điển - một method synchronized có
        //   Thread.sleep(1000) bên trong, gọi từ 100 virtual thread cùng khóa một object.
        //   Chạy trên JDK 25 và đo tổng thời gian. Rồi chạy LẠI với JDK 21 nếu máy có
        //   (dùng -Djdk.tracePinnedThreads=full). So sánh hai lần chạy và dán output.
        //   Đây là bài tập quan trọng nhất của cả bài: nó cho bạn thấy tận mắt JEP 491.
        //
        // TODO 2 (mục 7): Trên JDK 25, chạy TODO 1 kèm -Djdk.tracePinnedThreads=full. Xác
        //   nhận KHÔNG có output nào và KHÔNG có cảnh báo nào về cờ không hợp lệ.
        //   Ghi lại: đây là lý do đừng tin bài blog cũ.
        //
        // TODO 3 (mục 5, mục 7): Chạy lại TODO 1 với
        //   -XX:StartFlightRecording=duration=30s,filename=pin.jfr rồi
        //   jfr print --events VirtualThreadPinned pin.jfr. Có sự kiện nào không? Giải thích
        //   kết quả bằng mục 6.
        //
        // TODO 4 (mục 5): Đặt -Djdk.virtualThreadScheduler.parallelism=1 rồi chạy 10 virtual
        //   thread, mỗi cái sleep 1 giây. Tổng thời gian là bao nhiêu - 1 giây hay 10 giây?
        //   Giải thích vì sao (gợi ý: sleep có unmount không?).
        //
        // TODO 5 (mục 4, mục 9): Viết lại TODO 1 bằng ReentrantLock. So sánh thời gian với
        //   bản synchronized TRÊN JDK 25. Hai con số có khác nhau đáng kể không? Kết luận của
        //   bạn về việc có nên refactor hàng loạt.
        //
        // TODO 6 (mục 9): Viết một ví dụ mà ReentrantLock làm được còn synchronized thì
        //   không - ví dụ tryLock(500, MILLISECONDS) rồi bỏ cuộc và trả lỗi. Đây mới là lý
        //   do chính đáng để chọn ReentrantLock.
        //
        // TODO 7 (mục 10): Tra xem phiên bản driver JDBC (PostgreSQL/Oracle) mà dự án bạn
        //   dùng đã bỏ synchronized quanh I/O chưa, và từ phiên bản nào. Ghi lại nguồn.
    }
}
