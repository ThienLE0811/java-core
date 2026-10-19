package com.javacore.api.io.java_io;

/**
 * Watching a Directory for Changes
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 11/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Bài cuối của nhóm File System Basics; từ bài t12 trở đi chuyển sang nhóm đọc ghi nội dung.
 * - Một câu cho cả bài: thay vì hỏi đi hỏi lại "có gì đổi chưa" (polling), đăng ký thư mục với
 * một {@code WatchService} rồi CHỜ hệ điều hành báo về - nhưng phải nhớ gọi
 * {@code key.reset()} sau mỗi lần xử lý, quên là mất hết sự kiện về sau.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/watching-dir-changes/">dev.java - Watching a Directory for Changes</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WATCHING A DIRECTORY FOR CHANGES (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. TỔNG QUAN WATCH SERVICE
 * - (Watch Service Overview)
 * - API tầng thấp, linh hoạt, phát hiện thay đổi bằng CƠ CHẾ THÔNG BÁO thay vì polling tốn kém.
 * - Sáu bước của một vòng theo dõi:
 * + Tạo một {@code WatchService}.
 * + Đăng ký các thư mục cần theo dõi (chúng là đối tượng {@code Watchable}).
 * + Vòng lặp vô hạn chờ sự kiện tới.
 * + Lấy {@code WatchKey} ra khỏi hàng đợi, xử lý các sự kiện đang chờ trong nó.
 * + {@code reset()} cái key rồi quay lại chờ.
 * + Đóng service khi xong.
 * - "WatchKeys are thread-safe" - dùng được cùng các tiện ích đa luồng.
 * <p>
 * 2. THỬ CHẠY
 * - (Try It Out)
 * - Trang gốc có chương trình mẫu {@code WatchDir} xử lý sự kiện trên một luồng.
 * - Lưu ý khi test: vòng chờ sự kiện CHẶN bàn phím, nên chạy ở cửa sổ riêng hoặc chạy nền,
 * rồi tạo/sửa/xóa file từ cửa sổ khác.
 * <p>
 * 3. TẠO WATCH SERVICE VÀ ĐĂNG KÝ
 * - (Creating a Watch Service and Registering for Events)
 * - Tạo: {@code FileSystems.getDefault().newWatchService()} - lại đi qua {@code FileSystem}
 * như bài t4.
 * - Đăng ký: {@code path.register(watchService, kinds...)}. Method này đến từ interface
 * {@code Watchable} mà {@code Path} cài đặt.
 * - Bốn loại sự kiện trong {@code StandardWatchEventKinds}:
 * + {@code ENTRY_CREATE} - có phần tử mới trong thư mục.
 * + {@code ENTRY_DELETE} - có phần tử bị xóa.
 * + {@code ENTRY_MODIFY} - có phần tử bị sửa.
 * + {@code OVERFLOW} - sự kiện có thể đã bị MẤT hoặc bị loại bỏ. Luôn có thể xảy ra dù
 * không đăng ký, nên code phải xử lý.
 * <p>
 * 4. XỬ LÝ SỰ KIỆN
 * - (Processing Events)
 * - Ba cách lấy key ra khỏi hàng đợi:
 * + {@code take()} - CHẶN cho tới khi có sự kiện.
 * + {@code poll()} - trả về ngay, {@code null} nếu chưa có gì.
 * + {@code poll(timeout, unit)} - chờ tối đa rồi trả về {@code null}.
 * - Với mỗi key: {@code key.pollEvents()} lấy danh sách sự kiện; mỗi {@code WatchEvent} có
 * {@code kind()} (loại) và {@code context()} (tên phần tử).
 * - BẪY QUAN TRỌNG NHẤT CỦA BÀI, nguyên văn: "If you fail to invoke reset(), this key will
 * not receive any further events."
 * - Ba trạng thái của một {@code WatchKey}:
 * + {@code Ready} - sẵn sàng nhận sự kiện (trạng thái ban đầu).
 * + {@code Signaled} - đang có sự kiện trong hàng đợi, KHÔNG trở lại Ready cho tới khi
 * {@code reset()}.
 * + {@code Invalid} - không còn hiệu lực, do gọi {@code cancel()}, do thư mục không truy cập
 * được nữa, hoặc do service đã đóng.
 * <p>
 * 5. LẤY TÊN FILE
 * - (Retrieving the File Name)
 * - {@code event.context()} trả về tên phần tử, nhưng kiểu khai báo là {@code WatchEvent<?>}
 * nên phải ép về {@code WatchEvent<Path>}; chỗ ép này sinh cảnh báo unchecked, dập bằng
 * {@code @SuppressWarnings("unchecked")}.
 * - Tên trả về là tên TƯƠNG ĐỐI so với thư mục đã đăng ký. Muốn đường dẫn đầy đủ phải
 * {@code thuMucDangKy.resolve(tenFile)}.
 * <p>
 * 6. KHI NÀO NÊN VÀ KHÔNG NÊN DÙNG
 * - (When to Use and Not Use This API)
 * - NÊN: ứng dụng theo dõi nhiều file đang mở - trình soạn thảo, IDE, application server nạp
 * lại cấu hình.
 * - KHÔNG NÊN: lập chỉ mục cả ổ cứng.
 * - "When a file system does not support this mechanism, the Watch Service will poll the file
 * system, waiting for events." Nghĩa là API vẫn CHẠY ở mọi nơi, nhưng hiệu năng khác nhau
 * rất xa tùy nền tảng.
 * <p>
 * 7. CHƯƠNG TRÌNH MẪU
 * - (The WatchDir Example)
 * - Bản cài đặt đầy đủ, có cả phần đăng ký đệ quy cho thư mục con - thứ mà API không làm sẵn.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. BỐN GIỚI HẠN PHẢI BIẾT TRƯỚC KHI DÙNG THẬT
 * - KHÔNG ĐỆ QUY. {@code register} chỉ theo dõi ĐÚNG MỘT cấp thư mục. Muốn theo dõi cả cây thì
 * phải tự {@code walkFileTree} (bài t10) để đăng ký từng thư mục, VÀ tự đăng ký thêm mỗi khi
 * nhận {@code ENTRY_CREATE} của một thư mục mới.
 * - CHẬM TRÊN MACOS. Windows và Linux có cơ chế native ({@code ReadDirectoryChangesW},
 * {@code inotify}); macOS thì JDK rơi về polling mặc định 10 giây. Đây là lý do nhiều dự án
 * dùng thư viện ngoài thay vì {@code WatchService}.
 * - MỘT THAO TÁC CÓ THỂ SINH NHIỀU SỰ KIỆN. Lưu file bằng trình soạn thảo thường bắn
 * {@code ENTRY_CREATE} rồi vài {@code ENTRY_MODIFY} liên tiếp, vì phần mềm hay ghi file tạm
 * rồi đổi tên. Code thật phải gộp sự kiện (debounce) theo cửa sổ thời gian.
 * - SỰ KIỆN CÓ THỂ TỚI KHI FILE CHƯA GHI XONG. Nhận {@code ENTRY_CREATE} rồi đọc ngay có thể
 * đọc phải file viết dở. Đây chính là chỗ mẹo {@code ATOMIC_MOVE} ở bài t5 mục 10 phát huy
 * tác dụng ở phía người ghi.
 * <p>
 * 9. KHUNG CODE TỐI THIỂU ĐÚNG
 * - Đặt {@code WatchService} trong try-with-resources ({@code WatchService} là
 * {@code Closeable} - bài t12).
 * - Vòng lặp chuẩn: {@code take()} lấy key, duyệt {@code pollEvents()}, bỏ qua
 * {@code OVERFLOW}, xử lý, rồi {@code boolean valid = key.reset(); if (!valid) break;}.
 * - Luôn kiểm tra giá trị trả về của {@code reset()}: {@code false} nghĩa là key đã
 * {@code Invalid}, thư mục có thể đã bị xóa - tiếp tục vòng lặp là lặp vô ích.
 * - {@code take()} ném {@code InterruptedException}, nên vòng theo dõi phải nằm ở luồng riêng
 * và phải dừng được bằng {@code interrupt()}.
 * <p>
 * 10. GÓC NHÌN PRODUCTION
 * - Trong Spring Boot, nhu cầu "có file mới thì xử lý" thường KHÔNG nên giải bằng
 * {@code WatchService} thuần: Spring Integration có sẵn
 * {@code FileReadingMessageSource} với bộ lọc chống xử lý trùng.
 * - Vòng theo dõi phải chạy trên luồng riêng ({@code @Async} hoặc một
 * {@code ExecutorService}), dừng gọn gàng trong {@code @PreDestroy}, nếu không ứng dụng
 * không tắt được.
 * - Mẫu an toàn khi nhận {@code ENTRY_CREATE} cho file cần đọc: chờ kích thước file ngừng
 * thay đổi trong vài trăm mili giây rồi mới xử lý, hoặc quy ước bên ghi phải ghi ra đuôi
 * {@code .tmp} rồi đổi tên.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - {@code WatchService} ≈ {@code fs.watch()} của Node - cả hai đều dựa trên inotify /
 * ReadDirectoryChangesW, nên dính ĐÚNG những giới hạn ở mục 8: không đệ quy đáng tin cậy,
 * bắn nhiều sự kiện cho một thao tác, hành vi khác nhau theo OS.
 * - Hệ sinh thái Node giải bằng {@code chokidar} (bọc lại, có debounce và đăng ký đệ quy).
 * Java không có thư viện phổ biến tương đương - hoặc tự viết, hoặc dùng Spring Integration.
 * - {@code fs.watchFile()} của Node là bản polling thuần, tương đương cách JDK rơi về khi
 * file system không hỗ trợ native (mục 6).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Sáu bước của một vòng theo dõi là gì?
 * - Tạo {@code WatchService} từ đâu, đăng ký thư mục bằng method nào của lớp nào?
 * - Bốn loại {@code WatchEvent.Kind} là gì, cái nào xảy ra dù không đăng ký?
 * - Quên gọi {@code key.reset()} thì chuyện gì xảy ra?
 * - Ba trạng thái của {@code WatchKey} và cách chuyển giữa chúng.
 * - {@code take()} khác {@code poll()} ở đâu?
 * - {@code event.context()} trả về đường dẫn đầy đủ hay tên tương đối?
 * - {@code WatchService} có theo dõi thư mục con không?
 * - Vì sao nhận {@code ENTRY_CREATE} xong đọc file ngay là nguy hiểm?
 */
public class t11_WatchingDirectoryChanges {

    void main() {
        // Lưu ý: các TODO dưới đây chạy vòng lặp CHẶN. Chạy ở một cửa sổ terminal riêng,
        // rồi dùng cửa sổ khác (hoặc File Explorer) để tạo/sửa/xóa file.
        //
        // TODO 1 (mục 3, 4, 9): Tạo WatchService trong try-with-resources, đăng ký một thư mục
        //   tạm với cả ba loại sự kiện. Vòng lặp take() -> pollEvents() -> in kind và context
        //   -> reset(). Nhớ break khi reset() trả về false.
        //
        // TODO 2 (mục 4): Cố tình BỎ lời gọi key.reset(). Tạo file lần 1 (thấy sự kiện),
        //   tạo file lần 2 (không thấy gì nữa). Dán lại quan sát.
        //
        // TODO 3 (mục 5): Từ event.context() dựng đường dẫn đầy đủ bằng thuMuc.resolve(...).
        //   In cả hai để thấy khác biệt.
        //
        // TODO 4 (mục 8): Mở một file text trong thư mục đang theo dõi bằng Notepad hoặc IDE,
        //   sửa một chữ rồi lưu. Đếm xem MỘT lần lưu sinh ra mấy sự kiện và loại gì.
        //
        // TODO 5 (mục 8): Tạo một thư mục con bên trong thư mục đang theo dõi, rồi tạo file
        //   bên trong thư mục con đó. Xác nhận KHÔNG nhận được sự kiện của file con.
        //
        // TODO 6 (mục 8): Sửa lại TODO 5: khi nhận ENTRY_CREATE mà Files.isDirectory() đúng
        //   thì đăng ký luôn thư mục mới đó. Chạy lại và xác nhận đã bắt được sự kiện.
        //
        // TODO 7 (mục 4): Thay take() bằng poll(2, TimeUnit.SECONDS) và in một dòng "cho..."
        //   mỗi lần hết giờ mà không có sự kiện. Nêu khi nào nên dùng bản nào.
        //
        // TODO 8 (mục 9, 10): Đưa vòng theo dõi vào một Thread riêng, cho chạy 10 giây rồi
        //   gọi interrupt() để dừng. Xác nhận chương trình thoát sạch.
    }
}
