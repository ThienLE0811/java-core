package com.javacore.api.io.java_io;

/**
 * Manipulating Files and Directories
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 5/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: đây là bài CHẠM ĐĨA thật sự đầu tiên - kiểm tra, xóa, chép, di chuyển
 * file bằng các method static của {@code Files}, và mọi thao tác đều có thể ném
 * {@code IOException} nên phải quyết định bắt ở đâu.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/move-copy-delete/">dev.java - Manipulating Files and Directories</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - MANIPULATING FILES AND DIRECTORIES (mục 1 đến 9)
 * ======================================================================
 * <p>
 * 1. KIỂM TRA FILE CÓ TỒN TẠI KHÔNG
 * - (Checking a File or Directory)
 * - {@code Files.exists(path, LinkOption...)} và {@code Files.notExists(path, LinkOption...)}.
 * - BẪY QUAN TRỌNG NHẤT CỦA MỤC NÀY: {@code !exists()} KHÔNG tương đương {@code notExists()}.
 * Có BA kết quả chứ không phải hai: xác nhận có, xác nhận không có, và KHÔNG XÁC MINH ĐƯỢC
 * (thường do thiếu quyền đọc thư mục cha).
 * - Khi cả hai cùng trả về {@code false} thì "the existence of the file cannot be verified".
 * <p>
 * 2. KIỂM TRA QUYỀN TRUY CẬP
 * - (Checking File Accessibility)
 * - {@code Files.isReadable(path)}, {@code Files.isWritable(path)}, {@code Files.isExecutable(path)}.
 * - Cảnh báo nguyên văn: "Once any of these methods completes, there is no guarantee that the
 * file can be accessed." Đây là lỗ hổng TOCTTOU (time-of-check-to-time-of-use): giữa lúc kiểm tra
 * và lúc dùng, file có thể bị tiến trình khác đổi quyền hoặc xóa.
 * <p>
 * 3. HAI PATH CÓ TRỎ CÙNG MỘT FILE KHÔNG
 * - (Checking Whether Two Paths Locate the Same File)
 * - {@code Files.isSameFile(p1, p2)} - ném {@code IOException}, phải bắt hoặc khai báo throws.
 * - Khác hẳn {@code p1.equals(p2)}: {@code equals} chỉ so cú pháp chuỗi (bài t3 mục 7),
 * còn {@code isSameFile} hỏi ĐĨA, nên giải được symbolic link và hard link cùng trỏ về một file.
 * <p>
 * 4. XÓA FILE HOẶC THƯ MỤC
 * - (Deleting a File or Directory)
 * - {@code Files.delete(path)}: thất bại thì NÉM EXCEPTION nói rõ lý do.
 * + {@code NoSuchFileException} - file không tồn tại.
 * + {@code DirectoryNotEmptyException} - thư mục còn nội dung bên trong.
 * + {@code IOException} - lý do khác, ví dụ thiếu quyền.
 * - {@code Files.deleteIfExists(path)}: trả về {@code boolean}, không ném khi file vắng mặt.
 * Dùng khi "xóa nếu có" là hành vi bình thường chứ không phải lỗi.
 * - LUẬT: xóa thư mục thì thư mục PHẢI RỖNG. Muốn xóa đệ quy phải tự duyệt cây (bài t10).
 * <p>
 * 5. CHÉP FILE HOẶC THƯ MỤC
 * - (Copying a File or Directory)
 * - Ba overload: {@code copy(Path, Path, CopyOption...)}, {@code copy(InputStream, Path, CopyOption...)},
 * {@code copy(Path, OutputStream)}. Hai cái sau là cầu nối giữa path và stream (nhóm bài t13-t17).
 * - Ba {@code StandardCopyOption} / {@code LinkOption} dùng được:
 * + {@code REPLACE_EXISTING} - ghi đè nếu đích đã có. Không có nó thì chép vào chỗ đã tồn tại là lỗi.
 * + {@code COPY_ATTRIBUTES} - chép kèm thuộc tính (ít nhất là thời gian sửa đổi cuối).
 * + {@code NOFOLLOW_LINKS} - chép chính CÁI LINK chứ không chép file mà link trỏ tới.
 * - BẪY: chép thư mục chỉ tạo ra THƯ MỤC RỖNG ở đích, nội dung bên trong KHÔNG được chép theo.
 * - Dùng {@code REPLACE_EXISTING} mà đích là thư mục không rỗng thì ném {@code DirectoryNotEmptyException}.
 * <p>
 * 6. DI CHUYỂN FILE HOẶC THƯ MỤC
 * - (Moving a File or Directory)
 * - {@code Files.move(source, target, CopyOption...)}.
 * - Hai option dùng được: {@code REPLACE_EXISTING} và {@code ATOMIC_MOVE}.
 * - Khác với copy: thư mục KHÔNG RỖNG vẫn di chuyển được, miễn là nội dung bên trong không phải
 * dời đi đâu (ngữ nghĩa {@code rename} của UNIX - cùng một file store thì chỉ đổi tên).
 * - {@code ATOMIC_MOVE} đòi file system hỗ trợ; không hỗ trợ thì ném
 * {@code AtomicMoveNotSupportedException}.
 * <p>
 * 7. VARARGS
 * - (Using Varargs)
 * - Ký hiệu ba chấm {@code ...} cho phép truyền 0, 1, nhiều giá trị cách nhau bởi dấu phẩy,
 * hoặc truyền thẳng một mảng.
 * - Vì thế {@code Files.copy(a, b)} và {@code Files.copy(a, b, REPLACE_EXISTING, COPY_ATTRIBUTES)}
 * là cùng một method.
 * <p>
 * 8. THAO TÁC NGUYÊN TỬ
 * - (Atomic Operations)
 * - Nguyên văn: "An atomic file operation is an operation that cannot be interrupted or
 * 'partially' performed." Hoặc xong hẳn, hoặc hỏng hẳn, không có trạng thái dở dang.
 * - Quan trọng khi nhiều tiến trình cùng đụng một thư mục.
 * <p>
 * 9. NHẬN BIẾT LINK
 * - (Link Awareness)
 * - Lớp {@code Files} "link aware": mặc định các method ĐI THEO symbolic link, và cho option
 * {@code NOFOLLOW_LINKS} khi muốn thao tác trên chính cái link. Chi tiết ở bài t6.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 10. GÓC NHÌN PRODUCTION
 * - ĐỪNG viết {@code if (Files.exists(p)) Files.delete(p);} - đó chính là TOCTTOU ở mục 2.
 * Viết {@code Files.deleteIfExists(p)} để kiểm tra và xóa diễn ra trong một thao tác.
 * Cùng lý do: dùng {@code Files.createFile(p)} thay cho {@code if (!exists) create}.
 * - {@code ATOMIC_MOVE} là mẹo kinh điển để GHI FILE AN TOÀN: ghi ra file tạm cùng thư mục,
 * ghi xong mới {@code move} đè lên file thật. Người đọc không bao giờ thấy file viết dở.
 * Lưu ý phải CÙNG {@code FileStore} thì mới atomic được, nên file tạm đặt cùng thư mục đích
 * chứ không đặt ở thư mục temp hệ thống.
 * - Mọi exception trong bài đều thừa kế {@code FileSystemException}, lớp này có
 * {@code getFile()}, {@code getOtherFile()}, {@code getReason()} - log ba cái này ra thay vì
 * chỉ log {@code getMessage()} (bài t12 mục 3).
 * - Chép cả cây thư mục KHÔNG có sẵn trong {@code Files}. Phải tự viết bằng
 * {@code walkFileTree} (bài t10) hoặc dùng {@code FileUtils.copyDirectory} của commons-io.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.copy} ≈ {@code fs.copyFile}; {@code Files.move} ≈ {@code fs.rename}.
 * - {@code fs.rename} của Node cũng dính đúng giới hạn "không qua được thiết bị khác"
 * (lỗi {@code EXDEV}), giống hệt chuyện {@code ATOMIC_MOVE} đòi cùng file store.
 * - {@code Files.exists} ≈ {@code fs.existsSync} - và tài liệu Node cũng khuyên ĐỪNG dùng
 * trước khi mở file, vì đúng lý do TOCTTOU. Hai hệ sinh thái gặp nhau ở cùng một bài học.
 * - Node không có khái niệm trạng thái thứ ba "không xác minh được", chỗ này Java chặt hơn.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao {@code !Files.exists(p)} không bằng {@code Files.notExists(p)}?
 * - TOCTTOU là gì, viết lại {@code if (exists) delete} cho an toàn.
 * - {@code Files.delete} và {@code Files.deleteIfExists} khác nhau thế nào, khi nào dùng cái nào?
 * - Xóa một thư mục còn file bên trong thì nhận exception gì?
 * - Chép một thư mục có nội dung bằng {@code Files.copy} thì đích nhận được gì?
 * - Ba {@code CopyOption} của {@code copy} là gì, {@code move} nhận những option nào?
 * - Vì sao {@code move} xử lý được thư mục không rỗng còn {@code copy} thì không?
 * - {@code p1.equals(p2)} khác {@code Files.isSameFile(p1, p2)} ở đâu?
 * - Mô tả mẹo ghi file an toàn bằng {@code ATOMIC_MOVE}.
 */
public class t5_ManipulatingFilesAndDirectories {

    void main() {
        // TODO 1 (mục 1): Tạo một Path trỏ tới file không tồn tại. In đồng thời Files.exists(),
        //   Files.notExists() và !Files.exists(). Xác nhận cả ba trên một file bình thường,
        //   rồi thử nghĩ ra tình huống khiến exists và notExists CÙNG trả về false.
        //
        // TODO 2 (mục 4): Gọi Files.delete() trên ba trường hợp: file không tồn tại, thư mục rỗng,
        //   thư mục còn file bên trong. Dán lại tên exception của từng trường hợp.
        //
        // TODO 3 (mục 4): Lặp lại TODO 2 với deleteIfExists() và ghi lại khác biệt.
        //
        // TODO 4 (mục 5): Tạo thư mục "nguon" có 1 file bên trong, chép sang "dich" bằng
        //   Files.copy. Liệt kê nội dung "dich" và xác nhận nó RỖNG.
        //
        // TODO 5 (mục 5): Chép một file hai lần liên tiếp. Lần đầu không option, lần hai thêm
        //   REPLACE_EXISTING. Dán lại exception của lần đầu khi đích đã tồn tại.
        //
        // TODO 6 (mục 6): Di chuyển thư mục "nguon" (đang có file bên trong) sang tên khác
        //   bằng Files.move. Xác nhận lần này nội dung bên trong ĐI THEO.
        //
        // TODO 7 (mục 6, 10): Viết hàm ghi file an toàn: writeString vào file tạm cùng thư mục,
        //   rồi move sang file đích với REPLACE_EXISTING và ATOMIC_MOVE.
        //   Thử lại với file tạm nằm ở System.getProperty("java.io.tmpdir") và ghi lại kết quả.
        //
        // TODO 8 (mục 3): Tạo hai Path khác chuỗi nhưng cùng trỏ một file (ví dụ "a/../b.txt"
        //   và "b.txt"). So sánh equals() với Files.isSameFile() và giải thích.
    }
}
