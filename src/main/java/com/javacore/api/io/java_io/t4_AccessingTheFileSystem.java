package com.javacore.api.io.java_io;

/**
 * Accessing the File System
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 4/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: {@code Path} không trôi nổi một mình - nó luôn thuộc về một
 * {@code FileSystem}, và {@code FileSystem} đó mới biết dấu phân cách, danh sách ổ gốc
 * và các thiết bị lưu trữ ({@code FileStore}) nằm bên dưới.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/file-system/">dev.java - Accessing the File System</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - ACCESSING THE FILE SYSTEM (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. FILE SYSTEM MẶC ĐỊNH
 * - (Default File System)
 * - Lấy file system mặc định của máy: {@code FileSystems.getDefault()}.
 * - BẪY TÊN GỌI: {@code FileSystems} (SỐ NHIỀU, có "s") là lớp factory toàn method static;
 * {@code FileSystem} (SỐ ÍT) mới là đối tượng thật trả về. Viết nhầm một chữ "s" là không biên dịch.
 * - Mọi {@code Path} đều gắn với một file system. Nếu không chỉ định gì, {@code Path.of(...)}
 * tự động gắn vào file system mặc định. Hỏi ngược lại bằng {@code path.getFileSystem()}.
 * <p>
 * 2. DẤU PHÂN CÁCH ĐƯỜNG DẪN
 * - (Path String Separator)
 * - Dấu phân cách khác nhau theo hệ điều hành: {@code /} trên hệ POSIX (Linux, macOS),
 * {@code \} trên Windows.
 * - Lấy ra bằng {@code fileSystem.getSeparator()} thay vì hardcode trong code.
 * - Dùng được cho MỌI file system đang có, không riêng cái mặc định.
 * <p>
 * 3. FILE STORE
 * - (File Stores)
 * - Một file system chứa một hoặc nhiều {@code FileStore} - đại diện cho thiết bị lưu trữ
 * nằm bên dưới.
 * - Trên UNIX: mỗi file system được mount là một store. Trên Windows: mỗi volume (ổ C:, D:...)
 * là một store.
 * - Duyệt toàn bộ bằng {@code fileSystem.getFileStores()}, trả về {@code Iterable} nên dùng
 * for-each được ngay.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. FILESTORE CÓ GÌ DÙNG ĐƯỢC
 * - {@code name()}, {@code type()} (NTFS, ext4, APFS...), {@code isReadOnly()}.
 * - Ba method về dung lượng, đơn vị BYTE: {@code getTotalSpace()}, {@code getUsableSpace()}
 * (còn trống và tiến trình hiện tại ghi được), {@code getUnallocatedSpace()} (còn trống về mặt
 * vật lý, có thể lớn hơn usable vì quota).
 * - Muốn lấy store chứa MỘT path cụ thể thay vì duyệt hết: {@code Files.getFileStore(path)}
 * (bài t7 sẽ gặp lại).
 * - Ví dụ output thật trên máy Windows:
 * <p>
 * .  separator -> [\]
 * .  roots     -> [C:\, D:\]
 * .  store: Windows-SSD (C:) | type=NTFS | total=402GB | free=195GB | readOnly=false
 * <p>
 * 5. FILESYSTEM KHÔNG NHẤT THIẾT LÀ Ổ ĐĨA - ĐÂY MỚI LÀ GIÁ TRỊ THẬT CỦA BÀI
 * - {@code FileSystems.newFileSystem(pathToZip)} mở một file {@code .zip}/{@code .jar} NHƯ MỘT
 * file system. Sau đó dùng {@code Path} và {@code Files} y hệt để đọc ghi BÊN TRONG file nén,
 * không cần thư viện zip riêng.
 * - Đây chính là lý do bài t1 viết "định vị TÀI NGUYÊN" chứ không viết "định vị FILE", và vì sao
 * trang gốc nhấn mạnh file "may reside in memory".
 * - Lấy path bên trong zip: {@code zipFs.getPath("/thu-muc/file.txt")}. Chú ý dùng
 * {@code fileSystem.getPath(...)} chứ KHÔNG dùng {@code Path.of(...)} - {@code Path.of}
 * luôn gắn vào file system mặc định.
 * - Không trộn path của hai file system khác nhau: {@code resolve} hay {@code relativize}
 * giữa chúng sẽ ném {@code ProviderMismatchException}.
 * <p>
 * 6. FILESYSTEM LÀ CLOSEABLE
 * - File system mặc định KHÔNG đóng được: gọi {@code close()} trên nó ném
 * {@code UnsupportedOperationException}.
 * - File system do mình mở (zip) thì PHẢI đóng, nên luôn đặt trong try-with-resources
 * (bài t12 nói kỹ).
 * <p>
 * 7. ĐỐI CHIẾU TYPESCRIPT
 * - Node KHÔNG có tầng trừu tượng này: {@code fs} cắm cứng vào file system của OS, muốn đọc zip
 * phải cài thư viện riêng ({@code adm-zip}, {@code yauzl}) với API hoàn toàn khác.
 * - Java tách {@code FileSystemProvider} ra thành điểm cắm, nên cùng một đoạn code
 * {@code Files.readString(path)} chạy được cả trên đĩa lẫn trong zip. Đây là ưu thế hiếm hoi
 * của Java I/O so với Node.
 * - {@code getSeparator()} ≈ {@code path.sep} của Node.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code FileSystems} và {@code FileSystem} khác nhau chỗ nào, cái nào có method static?
 * - {@code Path.of("a.txt")} gắn vào file system nào?
 * - Muốn biết dấu phân cách của hệ điều hành đang chạy thì gọi method nào?
 * - {@code FileStore} đại diện cho cái gì trên Windows, trên UNIX?
 * - {@code getUsableSpace()} khác {@code getUnallocatedSpace()} ở điểm nào?
 * - Vì sao mở được file {@code .zip} như một file system lại là chuyện quan trọng?
 * - Trong một zip file system, tạo {@code Path} bằng method nào, vì sao không dùng {@code Path.of}?
 */
public class t4_AccessingTheFileSystem {

    void main() {
        // TODO 1 (mục 1): In ra FileSystems.getDefault().provider(). Sau đó so sánh
        //   Path.of("x.txt").getFileSystem() == FileSystems.getDefault() và giải thích kết quả.
        //
        // TODO 2 (mục 2): In ra getSeparator() của file system mặc định. Ghi lại kết quả trên
        //   máy bạn và đoán xem trên Linux nó sẽ in ra gì.
        //
        // TODO 3 (mục 3): Duyệt for-each qua getFileStores(), với mỗi store in name, type,
        //   getTotalSpace và getUsableSpace quy ra GB.
        //
        // TODO 4 (mục 4): Gọi Files.getFileStore(Path.of(".")) và so sánh kết quả với danh sách
        //   ở TODO 3 - thư mục hiện tại đang nằm trên store nào?
        //
        // TODO 5 (mục 5): Dùng FileSystems.newFileSystem(Path.of("thu.zip"), Map.of("create","true"))
        //   để tạo một zip rỗng, ghi vào trong nó một file text bằng Files.writeString, đóng lại,
        //   rồi mở ra đọc lại nội dung. Xác nhận đã dùng ĐÚNG API Files như với file thường.
        //
        // TODO 6 (mục 5): Thử gọi resolve giữa một Path của zip và một Path của ổ đĩa.
        //   Dán nguyên văn exception nhận được vào đây.
        //
        // TODO 7 (mục 6): Gọi FileSystems.getDefault().close(). Dán lại exception và giải thích.
    }
}
