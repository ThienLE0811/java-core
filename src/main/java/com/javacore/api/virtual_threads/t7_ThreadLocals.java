package com.javacore.api.virtual_threads;

/**
 * Thread Locals
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 7/8 - mục thứ bảy của trang Virtual Threads.
 * - Cái bẫy thứ ba và là cái khó gỡ nhất, vì {@code ThreadLocal} không nằm trong code bạn
 * viết mà nằm sâu trong Spring, Hibernate, Logback, Micrometer.
 * - Một câu cho cả bài: {@code ThreadLocal} được thiết kế cho thế giới có ÍT thread và
 * thread được TÁI DÙNG. Virtual thread phá vỡ cả hai giả định đó cùng lúc.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#thread-locals">dev.java - Virtual Threads, mục Thread Locals</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THREAD LOCALS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. THREADLOCAL LÀ GÌ, ÔN LẠI
 * - Một biến mà MỖI thread có bản sao riêng. API chỉ gồm {@code get()} và {@code set(T)},
 * nhưng giá trị lấy ra phụ thuộc thread đang gọi.
 * - Hai ca dùng kinh điển mà trang gốc nêu:
 * + Bọc một dịch vụ KHÔNG thread-safe để mỗi thread có một bản riêng. Ví dụ kinh điển:
 * {@code SimpleDateFormat}.
 * + Truyền NGỮ CẢNH NGẦM qua nhiều tầng gọi mà không phải thêm tham số vào mọi method -
 * ví dụ kết nối cơ sở dữ liệu, thông tin người dùng đang đăng nhập.
 * - Ca dùng thứ hai mới là chỗ {@code ThreadLocal} khó thay thế: nó là cách duy nhất để
 * tầng repository biết "transaction nào đang mở" mà không phải truyền tham số xuống 5 tầng.
 * <p>
 * 2. VÌ SAO THÀNH VẤN ĐỀ VỚI VIRTUAL THREAD
 * - Câu chốt của trang gốc: "Thread locals can be a problem when migrating to virtual
 * threads... you should rethink your sharing strategy."
 * - Lý do số học: pool có 200 thread thì tối đa 200 bản sao, dùng đi dùng lại. Virtual
 * thread sinh 100.000 cái mỗi phút, mỗi cái một bản sao, và không cái nào được tái dùng.
 * - Hệ quả, theo hai hướng:
 * + TỐN BỘ NHỚ: 100.000 bản {@code SimpleDateFormat} thay vì 200.
 * + MẤT Ý NGHĨA TỐI ƯU: cả lý do tồn tại của ca dùng thứ nhất là TRÁNH tạo lại object đắt
 * tiền. Với virtual thread, mỗi thread chỉ dùng bản sao của mình đúng một lần rồi vứt -
 * nghĩa là bạn vẫn tạo mới mỗi lần, chỉ là qua một lớp trung gian phức tạp hơn.
 * - Suy ra: với ca dùng thứ nhất, {@code ThreadLocal} trên virtual thread là THUẦN TÚY
 * THIỆT. Tạo thẳng object cục bộ trong method còn đơn giản và rẻ hơn.
 * <p>
 * 3. CÔNG CỤ TÌM CHỖ DÙNG
 * - Cờ JVM: {@code jdk.traceVirtualThreadLocals}. Khi bật, JVM in stack trace mỗi khi một
 * virtual thread GHI vào một thread-local variable.
 * - Dùng để trả lời câu hỏi thật sự khó: "cái gì trong đống thư viện của tôi đang dùng
 * ThreadLocal?" - vì bạn không grep được code của người khác.
 * <p>
 * 4. LỜI KHUYÊN
 * - Trang gốc chốt ngắn: rà lại chiến lược chia sẻ dữ liệu, và giảm tối đa việc dùng
 * {@code ThreadLocal} khi chạy trên virtual thread.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. TIN TỐT - PHẦN LỚN THREADLOCAL CỦA SPRING VẪN CHẠY ĐÚNG
 * - Đừng hoảng. Mô hình "mỗi request một thread" KHÔNG đổi khi bật virtual thread - chỉ là
 * thread đó nay là thread ảo. Nên các {@code ThreadLocal} theo-request vẫn đúng ngữ nghĩa:
 * + {@code RequestContextHolder} (Spring MVC)
 * + {@code SecurityContextHolder} (Spring Security)
 * + {@code TransactionSynchronizationManager} (Spring Data JPA - nơi giữ
 * {@code EntityManager}/kết nối của transaction hiện tại)
 * + MDC của SLF4J/Logback (traceId, userId trong log)
 * - Chúng vẫn chạy vì mỗi request vẫn có đúng một thread riêng từ đầu đến cuối. Cái giá phải
 * trả là BỘ NHỚ, không phải tính đúng đắn.
 * - Chỗ VỠ thật sự là khi bạn tự tạo thread con bên trong request (bài t4): giá trị
 * ThreadLocal KHÔNG tự sang thread con, nên transaction, security context và MDC biến mất
 * ở đó. Điều này đã đúng từ trước virtual thread, nhưng virtual thread khuyến khích bạn tạo
 * thread con nhiều hơn hẳn, nên lỗi này nay gặp thường xuyên hơn.
 * <p>
 * 6. INHERITABLETHREADLOCAL - MỘT DÒNG CẦN BIẾT
 * - Virtual thread CÓ kế thừa {@code InheritableThreadLocal} từ thread tạo ra nó, giống
 * platform thread.
 * - Tắt được bằng builder: {@code Thread.ofVirtual().inheritInheritableThreadLocals(false)}.
 * - Vì sao có tùy chọn tắt: kế thừa nghĩa là COPY bảng giá trị sang thread con. Với hàng
 * triệu thread con, bản copy đó là chi phí bộ nhớ đáng kể, và thường là thừa.
 * <p>
 * 7. SCOPEDVALUE - NGƯỜI THAY THẾ, VÀ LÀ THỨ CHÍNH THỨC Ở JDK 25
 * - {@code ScopedValue} (JEP 506) là tính năng CHÍNH THỨC trong JDK 25 - khác với Structured
 * Concurrency ở bài t4 vẫn còn preview. Tức là dùng được ngay trên repo này, không cần
 * {@code --enable-preview}.
 * - Khác biệt cốt lõi so với {@code ThreadLocal}:
 * + BẤT BIẾN: không có {@code set()}. Giá trị được gắn khi mở phạm vi và không đổi được.
 * + CÓ PHẠM VI RÕ RÀNG: giá trị chỉ tồn tại bên trong khối {@code run()}/{@code call()},
 * ra khỏi khối là tự biến mất. Không thể rò rỉ, không cần {@code remove()}.
 * + RẺ KHI KẾ THỪA: thread con nhìn thấy giá trị của cha mà không phải copy bảng.
 * - Hình dạng code:
 * <pre>{@code
 * private static final ScopedValue<String> NGUOI_DUNG = ScopedValue.newInstance();
 *
 * ScopedValue.where(NGUOI_DUNG, "user-42").run(() -> {
 *     xuLy();          // bên trong đây NGUOI_DUNG.get() trả "user-42"
 * });                  // ra khỏi khối là hết, không cần dọn
 * }</pre>
 * - Bản có trả về giá trị: {@code var kq = ScopedValue.where(KEY, v).call(() -> tinh());}.
 * Gắn nhiều giá trị thì nối tiếp: {@code where(X, a).where(Y, b).run(...)}.
 * - So sánh để nhớ: {@code ThreadLocal} là biến toàn cục gắn theo thread, phải tự dọn;
 * {@code ScopedValue} là tham số ngầm gắn theo PHẠM VI, tự dọn. Cái thứ hai an toàn hơn
 * hẳn, nhưng đổi lại bạn không "ghi đè giữa chừng" được.
 * - Thực tế 2026: Spring chưa chuyển các holder của mình sang {@code ScopedValue}, nên
 * trong dự án Spring bạn vẫn sống chung với {@code ThreadLocal}. Dùng {@code ScopedValue}
 * cho CODE MỚI của mình thì được.
 * <p>
 * 8. RÒ RỈ BỘ NHỚ - CÂU CHUYỆN ĐỔI CHIỀU
 * - Thời thread pool, lỗi kinh điển là QUÊN {@code remove()}: thread được tái dùng cho
 * request sau và mang theo dữ liệu của request trước. Đây vừa là rò rỉ bộ nhớ vừa là lỗ
 * hổng bảo mật (user A thấy ngữ cảnh của user B).
 * - Với virtual thread, lỗi đó TỰ BIẾN MẤT: thread chết sau một tác vụ, mang theo mọi
 * thread-local của nó. Đây là một điểm CỘNG hiếm hoi mà ít người nhắc tới.
 * - Nhưng xuất hiện lỗi mới thay thế: nếu một {@code ThreadLocal} giữ object nặng (buffer
 * vài MB) thì nay bạn nhân con số đó với SỐ THREAD ĐỒNG THỜI thay vì với kích thước pool.
 * Từ 200 x 2MB = 400MB thành 50.000 x 2MB = 100GB.
 * - Quy tắc rút ra: {@code ThreadLocal} giữ object NHỎ và rẻ thì không sao; giữ buffer,
 * cache hay connection thì phải bỏ.
 * <p>
 * 9. CHECKLIST RÀ THREADLOCAL TRƯỚC KHI BẬT VIRTUAL THREAD
 * - Grep dự án tìm {@code ThreadLocal} và {@code InheritableThreadLocal}. Với mỗi chỗ, phân
 * loại vào một trong ba nhóm:
 * + NHÓM A - ngữ cảnh theo request (user, traceId, transaction): GIỮ NGUYÊN. Vẫn đúng.
 * + NHÓM B - cache object đắt tiền để tái dùng ({@code SimpleDateFormat}, buffer,
 * {@code Random}): BỎ. Lý do tồn tại của nó đã mất (mục 2). Tạo cục bộ, hoặc dùng bản
 * thread-safe ({@code DateTimeFormatter} thay {@code SimpleDateFormat} -
 * {@code DateTimeFormatter} vốn bất biến và thread-safe, xem lại section The Date Time API).
 * + NHÓM C - object nặng vài MB: BỎ NGAY, xem mục 8.
 * - Chạy với {@code -Djdk.traceVirtualThreadLocals} dưới tải thật để tìm nhóm bạn không
 * grep được - tức là thư viện bên thứ ba.
 * - Kiểm tra riêng mọi chỗ tạo thread con bên trong request: ngữ cảnh có bị mất không
 * (mục 5)?
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Hai ca dùng kinh điển của {@code ThreadLocal} theo trang gốc?
 * - Virtual thread phá vỡ hai giả định nào của {@code ThreadLocal}?
 * - Vì sao ca dùng "cache object đắt tiền" mất sạch ý nghĩa trên virtual thread?
 * - Cờ JVM nào in stack trace khi virtual thread ghi thread-local? Tên chính xác?
 * - {@code SecurityContextHolder} có còn chạy đúng khi bật virtual thread không? Vì sao?
 * - Chỗ nào ngữ cảnh THẬT SỰ bị mất?
 * - Lỗi "quên remove()" thời thread pool xảy ra thế nào, và vì sao virtual thread xóa bỏ nó?
 * - {@code ScopedValue} ở JDK 25 là chính thức hay preview? Ba khác biệt với
 * {@code ThreadLocal}?
 * - Ba nhóm phân loại ở mục 9, mỗi nhóm xử lý thế nào?
 */
public class t7_ThreadLocals {

    void main() {
        // TODO 1 (mục 2): Tạo ThreadLocal<SimpleDateFormat> với initialValue() có in ra một
        //   dòng mỗi lần khởi tạo. Chạy 1.000 tác vụ hai lần: một lần trên
        //   newFixedThreadPool(8), một lần trên newVirtualThreadPerTaskExecutor().
        //   Đếm số dòng in ở mỗi bản. Con số đó chính là lập luận của mục 2.
        //
        // TODO 2 (mục 2, mục 9 nhóm B): Viết lại TODO 1 bằng DateTimeFormatter dùng chung
        //   (static final, thread-safe, không ThreadLocal). So sánh thời gian và số object
        //   tạo ra. Đây là cách sửa đúng cho nhóm B.
        //
        // TODO 3 (mục 3): Chạy TODO 1 với -Djdk.traceVirtualThreadLocals. Dán một stack
        //   trace nhận được vào note và chỉ ra dòng nào là chỗ ghi thread-local.
        //
        // TODO 4 (mục 5): Đặt một giá trị vào ThreadLocal ở "request thread", rồi bên trong
        //   submit một tác vụ con qua newVirtualThreadPerTaskExecutor() và đọc lại
        //   ThreadLocal đó trong tác vụ con. Xác nhận là null. Đây là mô phỏng chính xác
        //   việc mất SecurityContext/transaction khi chẻ việc ra chạy song song.
        //
        // TODO 5 (mục 6): Lặp lại TODO 4 với InheritableThreadLocal. Xác nhận giá trị ĐI
        //   THEO sang thread con. Rồi tắt bằng
        //   Thread.ofVirtual().inheritInheritableThreadLocals(false) và xác nhận nó mất lại.
        //
        // TODO 6 (mục 7): Viết lại TODO 4 bằng ScopedValue (JDK 25, không cần --enable-preview).
        //   Quan sát: giá trị có sang được thread con không? Thử gọi get() NGOÀI khối run()
        //   và dán exception nhận được.
        //
        // TODO 7 (mục 8): Tạo ThreadLocal giữ một byte[1024*1024]. Chạy 2.000 virtual thread
        //   cùng lúc với -Xmx512m và quan sát điều xảy ra. Sau đó chạy lại trên
        //   newFixedThreadPool(8). Ghi lại khác biệt.
        //
        // TODO 8 (mục 9): Grep một dự án Spring thật tìm ThreadLocal (cả trong dependency
        //   nếu IDE cho phép). Phân loại từng chỗ vào nhóm A/B/C và ghi quyết định xử lý.
    }
}
