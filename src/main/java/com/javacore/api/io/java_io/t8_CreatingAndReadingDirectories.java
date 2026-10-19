package com.javacore.api.io.java_io;

/**
 * Creating and Reading Directories
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 8/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: bài ngắn, chỉ ba việc - liệt kê ổ gốc, tạo thư mục
 * ({@code createDirectory} một cấp / {@code createDirectories} nhiều cấp), và tạo thư mục tạm.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/creating-reading-directories/">dev.java - Creating and Reading Directories</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING AND READING DIRECTORIES (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. LIỆT KÊ CÁC THƯ MỤC GỐC
 * - (Listing a File System's Root Directories)
 * - {@code FileSystem.getRootDirectories()} trả về {@code Iterable<Path>}, duyệt bằng for-each.
 * - Windows cho {@code [C:\, D:\, ...]} - mỗi ổ một phần tử. Hệ POSIX chỉ cho đúng {@code [/]}.
 * - Nối tiếp bài t4: {@code getRootDirectories()} là cấu trúc CÂY THƯ MỤC,
 * {@code getFileStores()} là THIẾT BỊ LƯU TRỮ. Hai danh sách thường trùng nhau trên Windows
 * nhưng khác nhau trên Linux (nhiều store cùng gắn vào một gốc {@code /}).
 * <p>
 * 2. TẠO THƯ MỤC
 * - (Creating a Directory)
 * - {@code Files.createDirectory(path, FileAttribute...)}: tạo ĐÚNG MỘT cấp. Thư mục cha
 * phải tồn tại sẵn, không thì ném {@code NoSuchFileException}.
 * Thư mục đã tồn tại thì ném {@code FileAlreadyExistsException}.
 * - {@code Files.createDirectories(path, FileAttribute...)}: method tiện lợi, tạo luôn mọi cấp
 * cha còn thiếu từ trên xuống. Thư mục đã tồn tại thì KHÔNG ném exception - chạy im lặng.
 * - BẪY nguyên văn: "It is possible for this method to fail after creating some, but not all,
 * of the parent directories." Hỏng giữa chừng thì các cấp đã tạo VẪN NẰM LẠI trên đĩa -
 * {@code createDirectories} KHÔNG nguyên tử.
 * - Tham số {@code FileAttribute} cho phép đặt quyền ngay lúc tạo, thường dựng bằng
 * {@code PosixFilePermissions.asFileAttribute(...)} (bài t7 mục 5).
 * <p>
 * 3. TẠO THƯ MỤC TẠM
 * - (Creating a Temporary Directory)
 * - Hai overload:
 * + {@code Files.createTempDirectory(Path dir, String prefix, FileAttribute...)} - chỉ định
 * nơi đặt.
 * + {@code Files.createTempDirectory(String prefix, FileAttribute...)} - dùng thư mục tạm
 * mặc định của hệ thống.
 * - Tên sinh ra gồm prefix cộng một chuỗi ngẫu nhiên, nên không bao giờ đụng tên.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. CHỌN GIỮA createDirectory VÀ createDirectories
 * - Luật đơn giản: gần như luôn dùng {@code createDirectories}. Nó idempotent (gọi nhiều lần
 * vẫn an toàn) nên thay được cả đoạn {@code if (!Files.exists(dir)) create...} - tránh luôn
 * TOCTTOU ở bài t5 mục 2.
 * - Chỉ dùng {@code createDirectory} khi việc "thư mục cha chưa tồn tại" ĐÁNG LÀ MỘT LỖI
 * mà bạn muốn biết, ví dụ tạo thư mục con trong một thư mục upload bắt buộc phải có sẵn.
 * - Mẫu hay gặp khi ghi file: {@code Files.createDirectories(file.getParent())} rồi mới ghi.
 * Nhớ {@code getParent()} có thể trả {@code null} nếu path chỉ có mỗi tên file (bài t3 mục 2) -
 * phải kiểm tra null trước khi truyền vào.
 * <p>
 * 5. THƯ MỤC TẠM KHÔNG TỰ XÓA
 * - {@code createTempDirectory} và {@code createTempFile} KHÔNG tự dọn khi JVM thoát. Tự dọn
 * bằng một trong ba cách:
 * + try-with-resources quanh một {@code Stream} rồi xóa đệ quy ở {@code finally}.
 * + {@code Runtime.getRuntime().addShutdownHook(...)} - không chạy khi JVM bị kill -9.
 * + {@code StandardOpenOption.DELETE_ON_CLOSE} (bài t13) - chỉ áp dụng cho FILE, không
 * cho thư mục.
 * - Thư mục tạm phải RỖNG mới xóa được bằng {@code Files.delete} (bài t5 mục 4), nên dọn
 * thư mục tạm có nội dung là phải duyệt cây (bài t10).
 * - Vị trí thư mục tạm mặc định: {@code System.getProperty("java.io.tmpdir")}.
 * <p>
 * 6. GÓC NHÌN PRODUCTION
 * - Thư mục tạm là chỗ an toàn hơn thư mục làm việc để ghi file trung gian: quyền truy cập
 * đã được JDK đặt chặt sẵn (chỉ chủ sở hữu), tránh được kiểu tấn công đoán tên file.
 * - Nhưng nhớ bài t5 mục 10: file tạm dùng cho mẹo "ghi rồi {@code ATOMIC_MOVE}" phải nằm
 * CÙNG thư mục đích chứ không nằm ở {@code java.io.tmpdir}, vì khác file store thì không
 * atomic được.
 * - Trong Spring Boot, phần lớn nhu cầu "thư mục upload" nên lấy từ cấu hình
 * ({@code @Value("${app.upload-dir}")}) rồi {@code createDirectories} một lần lúc khởi động
 * trong {@code @PostConstruct}, thay vì tạo lại ở mỗi request.
 * <p>
 * 7. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.createDirectory} ≈ {@code fs.mkdir(p)};
 * {@code Files.createDirectories} ≈ {@code fs.mkdir(p, {recursive: true})}.
 * - Cờ {@code recursive} của Node đổi luôn cả hành vi khi thư mục đã tồn tại (không còn ném
 * {@code EEXIST}) - giống hệt khác biệt giữa hai method Java.
 * - {@code createTempDirectory} ≈ {@code fs.mkdtemp(prefix)}. Node cũng không tự dọn.
 * - {@code getRootDirectories()} không có tương đương trong Node.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code getRootDirectories()} trả về gì trên Windows, trên Linux?
 * - {@code createDirectory} và {@code createDirectories} khác nhau ở hai điểm nào?
 * - Gọi {@code createDirectories} trên thư mục đã tồn tại thì chuyện gì xảy ra?
 * - Vì sao nói {@code createDirectories} không nguyên tử?
 * - Vì sao nên dùng {@code createDirectories} thay cho {@code if (!exists) createDirectory}?
 * - Thư mục tạm có tự xóa khi JVM tắt không?
 * - Lấy đường dẫn thư mục tạm của hệ thống bằng gì?
 * - Đặt quyền ngay lúc tạo thư mục bằng tham số nào?
 */
public class t8_CreatingAndReadingDirectories {

    void main() {
        // TODO 1 (mục 1): Duyệt FileSystems.getDefault().getRootDirectories() và in từng phần tử.
        //   So sánh với getFileStores() của bài t4, hai danh sách có trùng nhau không?
        //
        // TODO 2 (mục 2): Gọi Files.createDirectory(Path.of("a/b/c")) khi a và b chưa tồn tại.
        //   Dán lại exception.
        //
        // TODO 3 (mục 2): Làm lại TODO 2 bằng createDirectories. Xác nhận cả ba cấp được tạo.
        //
        // TODO 4 (mục 2): Gọi createDirectories lần thứ hai trên cùng đường dẫn vừa tạo.
        //   Ghi lại kết quả. Rồi gọi createDirectory trên đường dẫn đó và dán lại exception.
        //
        // TODO 5 (mục 3): Tạo thư mục tạm bằng createTempDirectory("javacore-"). In đường dẫn
        //   đầy đủ, đối chiếu với System.getProperty("java.io.tmpdir").
        //
        // TODO 6 (mục 3, 5): Gọi createTempDirectory hai lần với cùng prefix. In hai đường dẫn
        //   và chỉ ra phần nào do JDK sinh ngẫu nhiên.
        //
        // TODO 7 (mục 5): Tạo một file bên trong thư mục tạm vừa tạo, rồi thử Files.delete
        //   thư mục đó. Dán lại exception và giải thích (nhắc lại bài t5 mục 4).
        //
        // TODO 8 (mục 4): Viết hàm ghiFileAnToan(Path file, String noiDung): tạo thư mục cha
        //   nếu thiếu rồi mới ghi. Nhớ xử lý trường hợp getParent() trả về null.
    }
}
