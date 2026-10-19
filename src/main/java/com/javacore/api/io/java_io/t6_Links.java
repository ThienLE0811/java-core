package com.javacore.api.io.java_io;

/**
 * Links, Symbolic and Otherwise
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 6/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: toàn bộ {@code java.nio.file} "link aware" - mặc định mọi method
 * ĐI THEO symbolic link, muốn thao tác trên chính cái link thì phải nói rõ bằng
 * {@code NOFOLLOW_LINKS}; đây là nhược điểm số 3 của lớp {@code File} mà {@code Path} vá được.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/links/">dev.java - Links, Symbolic and Otherwise</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - LINKS, SYMBOLIC AND OTHERWISE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. HAI LOẠI LINK VÀ TÍNH "LINK AWARE"
 * - (Links, Symbolic and Otherwise)
 * - Package {@code java.nio.file} và lớp {@code Path} đều "link aware": mỗi method hoặc tự
 * phát hiện symbolic link, hoặc cho option để quyết định xử lý thế nào khi gặp.
 * - SYMBOLIC LINK (soft link): một file đặc biệt chứa ĐƯỜNG DẪN tới file khác. Giống một
 * tấm biển chỉ đường.
 * - HARD LINK: một TÊN khác trỏ thẳng vào cùng dữ liệu trên đĩa. Ràng buộc của hard link:
 * + Đích PHẢI tồn tại sẵn.
 * + KHÔNG được tạo trên thư mục.
 * + KHÔNG vượt được qua partition/volume hay file system khác.
 * + Nhìn từ bên ngoài giống hệt file thường.
 * + Giống hệt file gốc về quyền, mốc thời gian và mọi thuộc tính khác.
 * - Hard link ít dùng hơn symbolic link, nhưng các method của {@code Path} làm việc trơn tru
 * với cả hai.
 * <p>
 * 2. TẠO SYMBOLIC LINK
 * - (Creating a Symbolic Link)
 * - {@code Files.createSymbolicLink(link, target, FileAttribute...)}.
 * - Tham số thứ nhất là CÁI LINK sắp tạo, tham số thứ hai là ĐÍCH mà link trỏ tới.
 * - Đích "might or might not exist" - KHÔNG bắt buộc tồn tại. Link trỏ vào hư không gọi là
 * broken link / dangling link.
 * - Chỉ chạy được nếu file system có hỗ trợ, không thì ném {@code UnsupportedOperationException}.
 * <p>
 * 3. TẠO HARD LINK
 * - (Creating a Hard Link)
 * - {@code Files.createLink(link, existing)}.
 * - Ngược với mục 2: file đích BẮT BUỘC phải tồn tại, không thì ném {@code NoSuchFileException}.
 * <p>
 * 4. PHÁT HIỆN SYMBOLIC LINK
 * - (Detecting a Symbolic Link)
 * - {@code Files.isSymbolicLink(path)}.
 * - Không có method tương ứng cho hard link, vì hard link KHÔNG phân biệt được với file thường -
 * đúng theo định nghĩa ở mục 1.
 * <p>
 * 5. TÌM ĐÍCH CỦA LINK
 * - (Finding the Target of a Link)
 * - {@code Files.readSymbolicLink(path)} trả về {@code Path} mà link đang trỏ tới.
 * - Ném {@code NotLinkException} nếu path không phải symbolic link.
 * - Giá trị trả về có thể là đường dẫn TƯƠNG ĐỐI (so với thư mục chứa link), nên thường phải
 * {@code link.getParent().resolve(target).normalize()} mới ra đường dẫn dùng được.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. BA METHOD DỄ LẪN - ĐÂY LÀ PHẦN CẦN THUỘC
 * - {@code readSymbolicLink(p)}: đọc nguyên văn nội dung link, MỘT bước, không kiểm tra đích
 * có thật không.
 * - {@code toRealPath()} (bài t3 mục 4): giải HẾT mọi link trên đường đi, khử {@code ..} và
 * {@code .}, trả về đường dẫn tuyệt đối THẬT. Đích phải tồn tại, không thì {@code NoSuchFileException}.
 * - {@code toRealPath(LinkOption.NOFOLLOW_LINKS)}: tuyệt đối hóa nhưng KHÔNG giải link cuối.
 * - Nhớ: {@code readSymbolicLink} = "biển này ghi gì", {@code toRealPath} = "đi tới cùng thì
 * tới đâu".
 * <p>
 * 7. NOFOLLOW_LINKS XUẤT HIỆN Ở ĐÂU
 * - {@code LinkOption.NOFOLLOW_LINKS} là tham số của rất nhiều method đã học và sắp học:
 * {@code Files.exists}, {@code Files.copy}, {@code Files.readAttributes} (t7),
 * {@code Files.isDirectory}, {@code toRealPath}.
 * - Luật nhớ: KHÔNG truyền gì = đi theo link; truyền {@code NOFOLLOW_LINKS} = dừng ở cái link.
 * - Hệ quả hay gặp: {@code Files.delete} trên một symbolic link XÓA CÁI LINK, không xóa đích.
 * Đây là hành vi đúng và là ngoại lệ so với luật "mặc định đi theo link".
 * <p>
 * 8. BẪY TRÊN WINDOWS
 * - Tạo symbolic link trên Windows cần quyền đặc biệt: hoặc bật Developer Mode, hoặc chạy
 * terminal bằng quyền Administrator. Không có quyền thì ném {@code FileSystemException}
 * với thông báo "A required privilege is not held by the client".
 * - Windows phân biệt symlink trỏ file và symlink trỏ thư mục ở tầng OS, nhưng
 * {@code createSymbolicLink} của Java tự xử lý dựa vào đích.
 * - Hard link ({@code createLink}) thì KHÔNG cần quyền đặc biệt trên NTFS - test cái này dễ hơn.
 * <p>
 * 9. VÌ SAO LINK LÀ CHUYỆN NGHIÊM TÚC
 * - Symbolic link vòng (A trỏ B, B trỏ A) làm code duyệt cây thư mục treo vô hạn. Đây là
 * nhược điểm số 7 của {@code File} ở bài t1; {@code walkFileTree} (bài t10) giải bằng
 * {@code FileSystemLoopException}.
 * - Lỗ hổng bảo mật kinh điển: người dùng upload tên file là symlink trỏ vào
 * {@code /etc/passwd}, server đọc file đó mà không kiểm tra. Cách chặn: so sánh
 * {@code path.toRealPath()} có {@code startsWith} thư mục gốc cho phép hay không -
 * kiểm tra sau khi đã giải link, không phải trước.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.createSymbolicLink} ≈ {@code fs.symlink} (Node cũng đảo thứ tự tham số là
 * {@code target, path} - NGƯỢC với Java, dễ nhầm).
 * - {@code Files.createLink} ≈ {@code fs.link}; {@code readSymbolicLink} ≈ {@code fs.readlink};
 * {@code toRealPath} ≈ {@code fs.realpath}.
 * - {@code Files.isSymbolicLink} ≈ {@code fs.lstat(p).isSymbolicLink()}. Cặp
 * {@code stat}/{@code lstat} của Node chính là cặp "theo link / không theo link", tức là
 * {@code NOFOLLOW_LINKS} của Java.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kể bốn ràng buộc của hard link mà symbolic link không có.
 * - Trong {@code createSymbolicLink(a, b)} thì cái nào là link, cái nào là đích?
 * - Tạo symbolic link tới file chưa tồn tại được không? Còn hard link?
 * - Vì sao không có method {@code Files.isHardLink}?
 * - {@code readSymbolicLink} khác {@code toRealPath} ở điểm nào?
 * - Mặc định {@code Files.exists} có đi theo symbolic link không, tắt bằng cách nào?
 * - {@code Files.delete} trên một symlink thì xóa gì?
 * - Dùng gì để chặn lỗ hổng upload symlink trỏ ra ngoài thư mục cho phép?
 */
public class t6_Links {

    void main() {
        // Lưu ý trước khi làm: trên Windows, createSymbolicLink đòi Developer Mode hoặc
        // quyền Administrator. Nếu bị chặn, làm TODO 2 (hard link) trước rồi quay lại.
        //
        // TODO 1 (mục 2): Tạo file "goc.txt" có nội dung, rồi tạo symbolic link "bien-chi-duong.txt"
        //   trỏ vào nó. Đọc nội dung qua CÁI LINK bằng Files.readString và xác nhận ra nội dung gốc.
        //
        // TODO 2 (mục 3): Tạo hard link "ten-khac.txt" trỏ vào "goc.txt". Sửa nội dung qua một tên
        //   rồi đọc qua tên kia. Giải thích vì sao thấy được thay đổi.
        //
        // TODO 3 (mục 3): Gọi Files.createLink tới một file KHÔNG tồn tại. Dán lại exception.
        //
        // TODO 4 (mục 4): Gọi Files.isSymbolicLink trên cả ba thứ: file gốc, symbolic link,
        //   hard link. Dán lại ba kết quả và giải thích kết quả của hard link.
        //
        // TODO 5 (mục 5, 6): Trên cùng cái symbolic link, gọi readSymbolicLink(), toRealPath()
        //   và toRealPath(NOFOLLOW_LINKS). Dán lại ba output, chỉ ra khác biệt.
        //
        // TODO 6 (mục 2): Tạo symbolic link trỏ tới file KHÔNG tồn tại (broken link).
        //   In Files.exists(link), Files.exists(link, NOFOLLOW_LINKS) và Files.isSymbolicLink(link).
        //   Giải thích vì sao exists trả về false mà isSymbolicLink lại true.
        //
        // TODO 7 (mục 7): Xóa cái symbolic link ở TODO 1 bằng Files.delete, rồi kiểm tra
        //   "goc.txt" còn không. Kết luận delete xóa cái gì.
    }
}
