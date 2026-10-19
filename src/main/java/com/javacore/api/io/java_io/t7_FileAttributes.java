package com.javacore.api.io.java_io;

/**
 * Managing File Attributes
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 7/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: metadata của file đọc được theo hai lối - hỏi lẻ từng cái qua
 * {@code Files.isDirectory()}, {@code Files.size()}... hoặc hỏi MỘT LẦN lấy cả cụm qua
 * {@code Files.readAttributes()}; lối thứ hai nhanh hơn nhiều và là thứ cần nhớ của bài.
 * - Đây là bài vá nhược điểm số 4 và số 5 của lớp {@code File} nêu ở bài t1 (thiếu hỗ trợ
 * metadata, và đọc metadata không hiệu quả).
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/metadata/">dev.java - Managing File Attributes</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - MANAGING FILE ATTRIBUTES (mục 1 đến 9)
 * ======================================================================
 * <p>
 * 1. THUỘC TÍNH CỦA FILE VÀ CỦA FILE STORE
 * - (File and File Store Attributes)
 * - Nhóm hỏi LẺ, mỗi lần một thuộc tính, đều là method static của {@code Files}:
 * {@code size()}, {@code isDirectory()}, {@code isRegularFile()}, {@code isSymbolicLink()},
 * {@code isHidden()}, {@code getLastModifiedTime()}, {@code setLastModifiedTime()},
 * {@code getOwner()}, {@code setOwner()}, {@code getPosixFilePermissions()},
 * {@code setPosixFilePermissions()}, {@code getAttribute()}, {@code setAttribute()}.
 * - Cảnh báo nguyên văn: "Repeatedly accessing the file system to retrieve a single attribute
 * can adversely affect performance." Mỗi lời gọi là một lần đi xuống OS.
 * - Nhóm hỏi GỘP: hai overload của {@code Files.readAttributes()} - lấy cả cụm trong MỘT lần
 * chạm đĩa.
 * <p>
 * 2. THUỘC TÍNH CƠ BẢN
 * - (Basic File Attributes)
 * - {@code Files.readAttributes(path, BasicFileAttributes.class, LinkOption...)} trả về
 * {@code BasicFileAttributes} - tập thuộc tính MỌI file system đều có.
 * - Các accessor: {@code creationTime()}, {@code lastModifiedTime()}, {@code lastAccessTime()},
 * {@code size()}, {@code isDirectory()}, {@code isRegularFile()}, {@code isSymbolicLink()},
 * {@code isOther()}, {@code fileKey()}.
 * - Ba mốc thời gian trả về {@code FileTime}. Không phải implementation nào cũng hỗ trợ đủ
 * cả ba; chỗ không hỗ trợ trả về giá trị tùy implementation.
 * - {@code fileKey()} trả về định danh duy nhất của file, hoặc {@code null} nếu không có.
 * - "far more efficient than accessing the file system separately to read each individual
 * attribute" - đây là câu chốt của cả bài.
 * - {@code NOFOLLOW_LINKS} khiến method đọc thuộc tính của CHÍNH symbolic link (nối tiếp bài t6).
 * <p>
 * 3. ĐẶT MỐC THỜI GIAN
 * - (Setting Time Stamps)
 * - {@code Files.setLastModifiedTime(path, FileTime)}.
 * - {@code FileTime.fromMillis(long)} để dựng từ milli giây; cũng có {@code FileTime.from(Instant)}.
 * <p>
 * 4. THUỘC TÍNH DOS
 * - (DOS File Attributes)
 * - {@code DosFileAttributes}: bốn cờ {@code isReadOnly()}, {@code isHidden()},
 * {@code isArchive()}, {@code isSystem()}.
 * - "DOS file attributes are also supported on file systems other than DOS, such as Samba."
 * - BẪY: KHÔNG có setter riêng. Muốn đặt phải dùng
 * {@code Files.setAttribute(path, "dos:hidden", true)} - tên thuộc tính là chuỗi có tiền tố view.
 * <p>
 * 5. QUYỀN POSIX
 * - (POSIX File Permissions)
 * - "POSIX is an acronym for Portable Operating System Interface for UNIX". Chín quyền:
 * đọc/ghi/thực thi × chủ sở hữu/nhóm/người khác.
 * - {@code PosixFileAttributes} cho {@code owner()}, {@code group()}, {@code permissions()}.
 * - Lớp tiện ích {@code PosixFilePermissions}: {@code toString(Set)} ra chuỗi kiểu
 * {@code "rw-r--r--"}, {@code fromString(String)} đọc ngược lại, {@code asFileAttribute(Set)}
 * gói thành {@code FileAttribute} để truyền vào {@code Files.createFile} /
 * {@code Files.createDirectory}.
 * - BẪY: "The new file might be more secure than the permissions that were requested" - umask
 * của hệ thống có thể cắt bớt quyền so với cái mình yêu cầu.
 * <p>
 * 6. ĐẶT CHỦ SỞ HỮU VÀ NHÓM
 * - (Setting a File or Group Owner)
 * - Không truyền chuỗi tên thẳng được; phải tra qua
 * {@code FileSystem.getUserPrincipalLookupService()} để lấy {@code UserPrincipal}.
 * - Đặt chủ sở hữu: {@code Files.setOwner(path, userPrincipal)}.
 * - BẪY: {@code Files} KHÔNG có method đặt nhóm. Phải đi đường view:
 * {@code Files.getFileAttributeView(path, PosixFileAttributeView.class).setGroup(groupPrincipal)}.
 * <p>
 * 7. THUỘC TÍNH DO NGƯỜI DÙNG TỰ ĐỊNH NGHĨA
 * - (User-Defined File Attributes)
 * - {@code UserDefinedFileAttributeView} cho phép gắn cặp tên/giá trị tùy ý vào file
 * (extended attributes), khi tập thuộc tính có sẵn không đủ dùng.
 * - Giới hạn kích thước tùy hệ thống: "ext3 limits the size to 4 kilobytes".
 * - Trên Linux có thể phải mount lại file system với tùy chọn {@code user_xattr} trong
 * {@code /etc/fstab}; nếu tính năng bị tắt sẽ nhận {@code UnsupportedOperationException}.
 * - Ví dụ của trang gốc: lưu MIME type của file vào chính file đó.
 * <p>
 * 8. THUỘC TÍNH CỦA FILE STORE
 * - (File Store Attributes)
 * - {@code Files.getFileStore(path)} trả về {@code FileStore} chứa path đó.
 * - Dùng để hỏi dung lượng tổng / còn trống, nối tiếp bài t4 mục 3.
 * <p>
 * 9. XÁC ĐỊNH MIME TYPE
 * - (Determining MIME Type)
 * - {@code Files.probeContentType(path)} trả về chuỗi kiểu {@code "text/plain"}, hoặc
 * {@code null} nếu không đoán được.
 * - Cảnh báo nguyên văn: "The implementation of this method is highly platform specific and is
 * not infallible" - "it might be fooled". Cài đặt mặc định chủ yếu nhìn PHẦN MỞ RỘNG của tên file.
 * - Muốn tự quyết thì cài đặt {@code FileTypeDetector} riêng.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 10. HIỂU HỆ THỐNG VIEW - CHÌA KHÓA CỦA CẢ BÀI
 * - Metadata chia theo VIEW, mỗi view là một bộ thuộc tính. Tên view là tiền tố chuỗi:
 * <p>
 * .  view           interface đọc              có trên
 * .  "basic"        BasicFileAttributes        mọi file system
 * .  "dos"          DosFileAttributes          Windows, Samba
 * .  "posix"        PosixFileAttributes        Linux, macOS
 * .  "owner"        FileOwnerAttributeView     hầu hết
 * .  "user"         UserDefinedFileAttributeView  tùy hệ thống
 * <p>
 * - Hai cách lấy, khác nhau ở chỗ ĐỌC hay ĐỌC-GHI:
 * + {@code Files.readAttributes(p, XxxFileAttributes.class)} - CHỤP ẢNH giá trị tại một thời
 * điểm, chỉ đọc, giá trị KHÔNG tự cập nhật sau đó.
 * + {@code Files.getFileAttributeView(p, XxxFileAttributeView.class)} - cái TAY CẦM để vừa đọc
 * vừa GHI. Mọi setter đều nằm ở đây.
 * - Luật nhớ: {@code ...Attributes} = ảnh chụp chỉ đọc; {@code ...AttributeView} = cần ghi.
 * - Hỏi một thuộc tính bằng chuỗi: {@code Files.getAttribute(p, "basic:size")},
 * {@code Files.getAttribute(p, "dos:hidden")}. Không có tiền tố thì mặc định là {@code "basic:"}.
 * - Xem file system hỗ trợ view nào: {@code FileSystems.getDefault().supportedFileAttributeViews()}.
 * <p>
 * 11. GÓC NHÌN PRODUCTION
 * - Trong vòng lặp duyệt vài nghìn file, thay 5 lời gọi {@code Files.isDirectory} /
 * {@code Files.size} / {@code getLastModifiedTime} bằng MỘT {@code readAttributes} là mẹo
 * tăng tốc rõ rệt nhất của cả section. {@code walkFileTree} (bài t10) đã sẵn trao
 * {@code BasicFileAttributes} vào tay ở callback - đừng gọi lại {@code Files.size()} nữa.
 * - ĐỪNG tin {@code probeContentType} để kiểm duyệt file upload: nó nhìn đuôi tên file,
 * đổi {@code virus.exe} thành {@code anh.png} là qua mặt. Kiểm tra magic bytes đầu file
 * mới chắc.
 * - {@code getPosixFilePermissions} trên Windows ném {@code UnsupportedOperationException}.
 * Code chạy đa nền tảng phải hỏi {@code supportedFileAttributeViews().contains("posix")} trước.
 * - {@code lastAccessTime} thường KHÔNG chính xác trên hệ thống thật: Linux hay mount với
 * {@code relatime}/{@code noatime}, Windows mặc định tắt cập nhật atime vì lý do hiệu năng.
 * Đừng xây logic nghiệp vụ trên nó.
 * <p>
 * 12. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.readAttributes(p, BasicFileAttributes.class)} ≈ {@code fs.stat(p)}, và
 * {@code ...(NOFOLLOW_LINKS)} ≈ {@code fs.lstat(p)}. Node cũng gom hết vào một lời gọi -
 * nghĩa là Node đi thẳng tới lối "hỏi gộp" ngay từ đầu, còn Java phải để lại cả hai lối.
 * - {@code BasicFileAttributes.fileKey()} ≈ cặp {@code stats.dev} + {@code stats.ino} của Node.
 * - {@code setLastModifiedTime} ≈ {@code fs.utimes}.
 * - Node không có khái niệm view; quyền POSIX nằm thẳng ở {@code stats.mode} dạng số bát phân.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 13. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao gọi {@code Files.size()} rồi {@code Files.isDirectory()} lại kém hơn một
 * {@code readAttributes()}?
 * - {@code XxxFileAttributes} khác {@code XxxFileAttributeView} ở điểm nào, muốn GHI thì dùng cái nào?
 * - Kể tên năm view và interface đọc tương ứng.
 * - Đặt cờ hidden cho file trên Windows bằng cách nào?
 * - Vì sao {@code Files.setOwner} nhận {@code UserPrincipal} chứ không nhận {@code String}?
 * - Đặt NHÓM sở hữu bằng cách nào, vì sao không gọi thẳng từ {@code Files}?
 * - {@code probeContentType} dựa vào đâu để đoán, có tin được để kiểm duyệt upload không?
 * - Code chạy cả Windows lẫn Linux thì phải kiểm tra gì trước khi gọi
 * {@code getPosixFilePermissions}?
 */
public class t7_FileAttributes {

    void main() {
        // TODO 1 (mục 10): In FileSystems.getDefault().supportedFileAttributeViews().
        //   Ghi lại danh sách trên máy bạn, so với bảng ở mục 10 xem thiếu view nào.
        //
        // TODO 2 (mục 2): Tạo một file có nội dung, gọi Files.readAttributes với
        //   BasicFileAttributes.class rồi in đủ 9 accessor. Ghi lại fileKey() ra giá trị gì.
        //
        // TODO 3 (mục 1, 11): Đo thời gian 10000 vòng lặp gọi riêng Files.size +
        //   Files.isDirectory + Files.getLastModifiedTime, so với 10000 vòng gọi một
        //   readAttributes rồi đọc 3 accessor. Dán lại hai con số.
        //
        // TODO 4 (mục 3): Đặt lastModifiedTime của file về mốc 01/01/2020 bằng
        //   FileTime.from(Instant.parse(...)). Đọc lại để xác nhận.
        //
        // TODO 5 (mục 4): Đặt cờ ẩn cho file bằng Files.setAttribute(p, "dos:hidden", true),
        //   rồi đọc lại bằng cả Files.isHidden(p) lẫn DosFileAttributes. Kiểm tra bằng
        //   File Explorer xem file có biến mất không.
        //
        // TODO 6 (mục 10): Lấy ba giá trị size của cùng một file theo ba cách: Files.size(p),
        //   Files.getAttribute(p, "basic:size"), readAttributes(...).size(). Xác nhận bằng nhau.
        //
        // TODO 7 (mục 5, 11): Gọi Files.getPosixFilePermissions trên máy Windows.
        //   Dán lại exception, rồi viết lại đoạn code có kiểm tra supportedFileAttributeViews trước.
        //
        // TODO 8 (mục 9): Tạo file "anh.png" nhưng nội dung là text thường. Gọi probeContentType
        //   và dán lại kết quả - đây là minh chứng cho câu "it might be fooled".
        //
        // TODO 9 (mục 8): Gọi Files.getFileStore trên thư mục hiện tại, in type và getUsableSpace.
        //   Đối chiếu với kết quả bài t4.
    }
}
