package com.javacore.api.io.java_io;

/**
 * Walking the File Tree
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 10/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: duyệt ĐỆ QUY cả cây thư mục bằng {@code Files.walkFileTree} - bạn đưa
 * vào một {@code FileVisitor} có bốn callback, mỗi callback trả về một {@code FileVisitResult}
 * để điều khiển việc đi tiếp, dừng, hay bỏ qua cả nhánh.
 * - Đây là bài vá nhược điểm số 7 của lớp {@code File} nêu ở bài t1 (không viết nổi code duyệt
 * đệ quy xử lý đúng symbolic link vòng).
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/walking-tree/">dev.java - Walking the File Tree</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WALKING THE FILE TREE (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. INTERFACE FILEVISITOR
 * - (The FileVisitor Interface)
 * - Bốn callback, được gọi tại bốn thời điểm khác nhau của quá trình duyệt:
 * + {@code preVisitDirectory(dir, attrs)} - TRƯỚC khi đi vào một thư mục.
 * + {@code visitFile(file, attrs)} - với mỗi file thường.
 * + {@code visitFileFailed(file, exc)} - khi không truy cập được một phần tử (thiếu quyền,
 * bị xóa giữa chừng...).
 * + {@code postVisitDirectory(dir, exc)} - SAU khi đã duyệt xong mọi thứ bên trong thư mục.
 * - {@code SimpleFileVisitor} là lớp adapter: thừa kế nó rồi override đúng callback mình cần,
 * khỏi phải cài đặt cả bốn.
 * - Chú ý: hai callback {@code preVisitDirectory} và {@code visitFile} nhận SẴN
 * {@code BasicFileAttributes} - không cần gọi lại {@code Files.size()} nữa (bài t7 mục 11).
 * - Cài đặt mặc định của {@code SimpleFileVisitor} NÉM LẠI exception nhận được, nên lỗi không
 * bị nuốt im lặng.
 * <p>
 * 2. KHỞI ĐỘNG QUÁ TRÌNH DUYỆT
 * - (Kickstarting the Process)
 * - {@code Files.walkFileTree(start, visitor)} - bản 2 tham số, KHÔNG đi theo symbolic link,
 * độ sâu không giới hạn.
 * - {@code Files.walkFileTree(start, Set<FileVisitOption>, int maxDepth, visitor)} - bản
 * 4 tham số, cho đặt độ sâu tối đa và tùy chọn duyệt.
 * - {@code FileVisitOption} hiện chỉ có một giá trị: {@code FOLLOW_LINKS}.
 * - Muốn duyệt hết mọi cấp thì truyền {@code Integer.MAX_VALUE} làm {@code maxDepth}.
 * <p>
 * 3. LƯU Ý KHI VIẾT FILEVISITOR
 * - (Considerations When Creating a FileVisitor)
 * - "A file tree is walked depth first, but you cannot make any assumptions about the iteration
 * order that subdirectories are visited." Duyệt theo chiều sâu, nhưng KHÔNG được giả định
 * thứ tự các thư mục con.
 * - Đặt thao tác vào ĐÚNG callback mới chạy đúng:
 * + XÓA đệ quy: xóa file ở {@code visitFile}, xóa thư mục ở {@code postVisitDirectory}
 * (vì thư mục phải rỗng mới xóa được - bài t5 mục 4).
 * + CHÉP đệ quy: tạo thư mục đích ở {@code preVisitDirectory}, rồi chép thuộc tính của
 * thư mục ở {@code postVisitDirectory} (phải chép sau, vì ghi nội dung vào trong sẽ làm
 * thay đổi mốc thời gian của thư mục).
 * - Symbolic link vòng chỉ gây lỗi khi BẬT {@code FOLLOW_LINKS}; lúc đó JDK phát hiện được và
 * ném {@code FileSystemLoopException} chứ không treo vô hạn.
 * <p>
 * 4. ĐIỀU KHIỂN LUỒNG DUYỆT
 * - (Controlling the Flow)
 * - Mỗi callback trả về một giá trị enum {@code FileVisitResult}:
 * + {@code CONTINUE} - đi tiếp bình thường.
 * + {@code TERMINATE} - dừng toàn bộ quá trình duyệt ngay lập tức.
 * + {@code SKIP_SUBTREE} - bỏ qua cả nhánh thư mục này. CHỈ có ý nghĩa khi trả về từ
 * {@code preVisitDirectory}; trả từ chỗ khác thì bị coi như {@code CONTINUE}.
 * + {@code SKIP_SIBLINGS} - bỏ qua các phần tử còn lại CÙNG CẤP. Trả từ
 * {@code preVisitDirectory} thì bỏ luôn cả thư mục đó; trong cả hai trường hợp
 * {@code postVisitDirectory} của thư mục cha vẫn được gọi.
 * - Ví dụ trang gốc: bỏ qua thư mục tên SCCS, và dừng hẳn khi tìm thấy file cần tìm.
 * <p>
 * 5. TÌM FILE
 * - (Finding Files)
 * - {@code FileSystem.getPathMatcher(String)} trả về {@code PathMatcher} - interface chỉ có
 * một method {@code matches(Path)} trả {@code boolean}.
 * - Chuỗi truyền vào có dạng {@code "cu-phap:mau"}, hai cú pháp bắt buộc có là
 * {@code glob:} và {@code regex:}. "Glob syntax is easy to use and flexible but, if you prefer,
 * you can also use regular expressions, or regex, syntax."
 * - Luật glob đã học ở bài t9 mục 4.
 * <p>
 * 6. SO KHỚP ĐỆ QUY
 * - (Recursive Pattern Matching)
 * - Ghép {@code PathMatcher} vào trong {@code visitFile} của một {@code FileVisitor} là có
 * ngay lệnh {@code find} của UNIX viết bằng Java.
 * - Nếu bật {@code FOLLOW_LINKS} thì phải tự phòng chuyện link vòng trong {@code visitFile}.
 * <p>
 * 7. BA VÍ DỤ ĐẦY ĐỦ CỦA TRANG GỐC
 * - (The Find Example / The Copy Example / The Chmod Example)
 * - Ba chương trình mẫu hoàn chỉnh: tìm file theo mẫu, chép đệ quy cả cây, đổi quyền hàng loạt.
 * Nên đọc mã nguồn, nhất là bản Copy - nó là lời giải chuẩn cho chuyện "{@code Files.copy}
 * chỉ chép thư mục rỗng" ở bài t5 mục 5.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. BA API DUYỆT CÂY - PHẢI BIẾT CHỌN
 * <p>
 * .  API                            kiểu           điều khiển luồng  báo lỗi
 * .  Files.walkFileTree(p, visitor) void           đủ 4 FileVisitResult  visitFileFailed
 * .  Files.walk(p)                  Stream&lt;Path&gt;  không có       ném UncheckedIOException
 * .  Files.find(p, depth, matcher)  Stream&lt;Path&gt;  không có       ném UncheckedIOException
 * <p>
 * - {@code walkFileTree} là API duy nhất CHỊU ĐƯỢC LỖI: một thư mục thiếu quyền chỉ kích hoạt
 * {@code visitFileFailed} rồi duyệt tiếp. {@code Files.walk} thì nổ {@code UncheckedIOException}
 * giữa chừng stream và mất hết kết quả - rất hay gặp khi duyệt {@code C:\} hoặc {@code /}.
 * - Dùng {@code Files.walk} cho script ngắn trên thư mục mình kiểm soát; dùng
 * {@code walkFileTree} cho code production duyệt thư mục của người dùng.
 * - Cả {@code walk} và {@code find} trả {@code Stream} chiếm file handle - PHẢI
 * try-with-resources (nhắc lại bài t9 mục 5).
 * <p>
 * 9. XÓA ĐỆ QUY - ĐOẠN CODE CẦN THUỘC
 * - JDK KHÔNG có {@code Files.deleteRecursively}. Mẫu chuẩn là
 * {@code SimpleFileVisitor} với {@code visitFile} gọi {@code Files.delete(file)} và
 * {@code postVisitDirectory} gọi {@code Files.delete(dir)}, cả hai trả {@code CONTINUE}.
 * - Nhớ kiểm tra tham số {@code exc} trong {@code postVisitDirectory}: khác {@code null}
 * nghĩa là việc duyệt bên trong đã hỏng, lúc đó đừng xóa thư mục mà hãy ném lại.
 * - Mặc định KHÔNG theo link là điều tốt ở đây: xóa đệ quy mà đi theo symlink thì xóa nhầm
 * dữ liệu ngoài cây. Đây là lý do {@code walkFileTree} chọn mặc định an toàn.
 * <p>
 * 10. GÓC NHÌN PRODUCTION
 * - Luôn ĐẶT GIỚI HẠN ĐỘ SÂU khi duyệt thư mục do người dùng cung cấp. Cây sâu bất thường
 * là một vector tấn công làm cạn tài nguyên.
 * - Duyệt cây là thao tác I/O nặng và chậm. Trong Spring Boot đừng gọi nó trong luồng xử lý
 * request; đẩy sang {@code @Async} hoặc một job theo lịch.
 * - Tận dụng {@code BasicFileAttributes} mà callback trao sẵn thay vì gọi lại {@code Files.xxx} -
 * trên cây vài chục nghìn file, khác biệt này đo được bằng giây.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - Node không có tương đương {@code walkFileTree}. {@code fs.readdir(p, {recursive: true})}
 * (Node 18.17+) gần với {@code Files.walk} - trả danh sách, không điều khiển luồng được.
 * - Mô hình bốn callback giống {@code fast-glob}/{@code chokidar} ở chỗ cho hook, nhưng
 * {@code FileVisitResult} với {@code SKIP_SUBTREE} thì không hệ sinh thái nào của Node có
 * sẵn - thường phải tự viết đệ quy.
 * - {@code fs.rm(p, {recursive: true, force: true})} của Node làm sẵn chuyện ở mục 9; Java
 * bắt tự viết, đổi lại cho kiểm soát từng bước.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kể bốn callback của {@code FileVisitor} và thời điểm mỗi cái được gọi.
 * - Vì sao nên thừa kế {@code SimpleFileVisitor} thay vì cài đặt thẳng {@code FileVisitor}?
 * - Bốn giá trị {@code FileVisitResult} là gì, cái nào chỉ dùng được ở {@code preVisitDirectory}?
 * - Xóa đệ quy thì xóa file ở callback nào, xóa thư mục ở callback nào, vì sao?
 * - Chép đệ quy thì tạo thư mục đích ở callback nào, vì sao chép thuộc tính lại để sau cùng?
 * - Mặc định {@code walkFileTree} có đi theo symbolic link không? Bật lên thì rủi ro gì?
 * - {@code walkFileTree} khác {@code Files.walk} ở chỗ nào khi gặp thư mục thiếu quyền?
 * - {@code PathMatcher} nhận chuỗi theo định dạng nào, hai cú pháp bắt buộc có là gì?
 */
public class t10_WalkingTheFileTree {

    void main() {
        // TODO 1 (mục 1, 2): Thừa kế SimpleFileVisitor, override visitFile để in đường dẫn và
        //   kích thước (lấy từ attrs, KHÔNG gọi Files.size). Chạy walkFileTree trên
        //   thư mục src của dự án này.
        //
        // TODO 2 (mục 1): Thêm override cho cả bốn callback, mỗi cái in một dòng đánh dấu.
        //   Chạy trên một cây nhỏ 2 cấp và dán lại output để thấy rõ thứ tự gọi.
        //
        // TODO 3 (mục 4): Sửa visitor ở TODO 1 để trả về SKIP_SUBTREE khi gặp thư mục tên
        //   "target" hoặc ".git". Xác nhận chúng không bị duyệt vào.
        //
        // TODO 4 (mục 4): Sửa visitor để TERMINATE ngay khi tìm thấy file đầu tiên tên
        //   "t1_MainIoConcepts.java". In ra đường dẫn rồi dừng.
        //
        // TODO 5 (mục 5, 6): Tạo PathMatcher "glob:**.java", dùng nó trong visitFile để đếm
        //   số file .java trong dự án. Đổi sang "regex:.*t\\d+_.*\\.java" và so sánh kết quả.
        //
        // TODO 6 (mục 9): Viết hàm xoaDeQuy(Path). Test bằng cách tạo cây a/b/c có file ở mỗi
        //   cấp rồi xóa sạch. Nhớ kiểm tra tham số exc trong postVisitDirectory.
        //
        // TODO 7 (mục 3, 7): Viết hàm chepDeQuy(Path nguon, Path dich): tạo thư mục ở
        //   preVisitDirectory, chép file ở visitFile với COPY_ATTRIBUTES. Đối chiếu kết quả với
        //   bài t5 TODO 4 (nơi Files.copy chỉ tạo ra thư mục rỗng).
        //
        // TODO 8 (mục 2, 8, 10): Chạy walkFileTree trên ổ C:\ với maxDepth = 2 và một visitor
        //   có override visitFileFailed để đếm số chỗ bị từ chối. Sau đó thử lại bằng
        //   Files.walk(Path.of("C:/"), 2) và dán lại exception nhận được.
    }
}
