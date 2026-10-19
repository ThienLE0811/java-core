package com.javacore.api.io.java_io;

/**
 * Listing the Content of a Directory
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 9/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File System Basics của section.
 * - Một câu cho cả bài: liệt kê MỘT cấp thư mục bằng {@code DirectoryStream} - đọc dần từng
 * phần tử nên không nổ bộ nhớ với thư mục khổng lồ, bắt buộc đóng bằng try-with-resources,
 * và lọc được ngay lúc liệt kê bằng glob hoặc filter tự viết.
 * - Đây là bài vá nhược điểm số 6 của lớp {@code File} nêu ở bài t1 (liệt kê thư mục lớn
 * có thể treo hoặc ngốn hết bộ nhớ).
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/file-system/listing/">dev.java - Listing the Content of a Directory</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - LISTING THE CONTENT OF A DIRECTORY (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. LIỆT KÊ NỘI DUNG THƯ MỤC
 * - (Listing the Content of a Directory)
 * - {@code Files.newDirectoryStream(path)} trả về {@code DirectoryStream<Path>}.
 * - "The DirectoryStream interface extends Iterable, so you can iterate through the directory
 * stream, reading all of the objects. This approach scales well to very large directories."
 * - BẮT BUỘC ĐÓNG: {@code DirectoryStream} chiếm tài nguyên của file system. Luôn đặt trong
 * try-with-resources; nếu không thì phải {@code close()} trong khối {@code finally}.
 * - Lỗi xảy ra GIỮA CHỪNG vòng lặp không ném {@code IOException} được (vì {@code Iterator}
 * không khai báo throws), nên được bọc lại thành {@code DirectoryIteratorException}
 * - một {@code RuntimeException}.
 * - Trả về TOÀN BỘ nội dung một cấp: kể cả file ẩn và thư mục con. KHÔNG đệ quy xuống dưới.
 * - Các {@code Path} trả về đã được resolve sẵn theo thư mục cha, ví dụ {@code /tmp/a},
 * {@code /tmp/b} - không phải tên trần.
 * <p>
 * 2. LỌC BẰNG GLOB
 * - (Filtering a Directory Listing By Using Globbing)
 * - {@code Files.newDirectoryStream(path, "*.{class,java,jar}")} - lọc ngay ở tầng file system,
 * khỏi phải tự viết {@code if} trong vòng lặp.
 * <p>
 * 3. TỰ VIẾT FILTER
 * - (Writing Your Own Directory Filter)
 * - Khi điều kiện lọc phức tạp hơn so khớp tên: cài đặt
 * {@code DirectoryStream.Filter<Path>} với method {@code accept(Path)} trả về {@code boolean},
 * rồi truyền vào {@code Files.newDirectoryStream(path, filter)}.
 * - Ví dụ trang gốc: chỉ lấy thư mục con, dùng {@code Files.isDirectory(entry)}.
 * - Lưu ý: chỉ lọc trong MỘT thư mục. Muốn tìm trên cả cây thì sang bài t10.
 * <p>
 * 4. GLOB LÀ GÌ
 * - (What is a Glob)
 * - Cú pháp so khớp đơn giản hơn regex, dùng được ở nhiều chỗ trong API.
 * - Luật:
 * <p>
 * .  *        không hoặc nhiều ký tự, KHÔNG vượt qua dấu phân cách thư mục
 * .  **       như trên nhưng VƯỢT QUA được dấu phân cách thư mục
 * .  ?        đúng một ký tự
 * .  {a,b}    một trong các lựa chọn liệt kê
 * .  [a-z]    một ký tự trong khoảng
 * .  [!a-z]   một ký tự NGOÀI khoảng
 * .  \        thoát ký tự đặc biệt
 * <p>
 * - Khi cần so khớp nâng cao hơn: {@code FileSystem.getPathMatcher(String)} nhận chuỗi dạng
 * {@code "glob:*.java"} hoặc {@code "regex:..."} (bài t10 mục 5 dùng tới).
 * - Ký tự đặc biệt cần thoát khi gõ trên shell.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BA CÁCH LIỆT KÊ MỘT THƯ MỤC - BIẾT CHỌN CÁI NÀO
 * <p>
 * .  API                          kiểu trả về          đệ quy  phải đóng
 * .  file.listFiles()             File[]               không   không (nhưng nạp hết vào RAM)
 * .  Files.newDirectoryStream(p)  DirectoryStream      không   CÓ
 * .  Files.list(p)                Stream&lt;Path&gt;   không   CÓ
 * .  Files.walk(p)                Stream&lt;Path&gt;   CÓ      CÓ
 * <p>
 * - {@code Files.list(p)} (Java SE 8) là bản bọc {@code DirectoryStream} thành Stream API.
 * Code hiện đại hay dùng nó hơn vì nối được {@code filter}/{@code map}/{@code sorted}.
 * - BẪY CHẾT NGƯỜI: {@code Files.list} trả về {@code Stream} chiếm file handle. Quên
 * try-with-resources là RÒ RỈ FILE HANDLE - chạy lâu sẽ gặp
 * {@code IOException: Too many open files}. Đây là khác biệt lớn nhất so với
 * {@code List.stream()} vốn không cần đóng.
 * - Nhắc lại bài t1: {@code Stream} ở đây là Stream API (bước 20), KHÔNG phải I/O stream.
 * Hai khái niệm trùng tên lại gặp nhau ngay trong một bài.
 * <p>
 * 6. THỨ TỰ KHÔNG ĐẢM BẢO
 * - Cả {@code DirectoryStream} lẫn {@code Files.list} đều KHÔNG hứa thứ tự nào. Muốn sắp xếp
 * phải tự {@code sorted(...)} - và lúc đó mất luôn ưu thế không nạp hết vào RAM.
 * - Nội dung thư mục là ẢNH CHỤP KHÔNG ĐẢM BẢO: file được tạo/xóa trong lúc đang duyệt
 * thì có thể xuất hiện, có thể không. Code phải chịu được chuyện một {@code Path} vừa liệt kê
 * xong đã biến mất khi đọc tới.
 * <p>
 * 7. GLOB SO VỚI REGEX - CHỖ HAY SAI
 * - Trong glob, {@code *} KHÔNG vượt qua dấu phân cách thư mục. {@code "glob:*.java"} khớp
 * {@code Main.java} nhưng KHÔNG khớp {@code src/Main.java}; phải viết {@code "glob:**.java"}
 * hoặc {@code "glob:**&#47;*.java"}.
 * (Ở dòng trên phải viết {@code &#47;} thay cho dấu gạch chéo: hai ký tự sao-gạch-chéo liền
 * nhau sẽ ĐÓNG SỚM khối javadoc này và làm cả file không biên dịch được.)
 * - Trong regex, {@code *} nghĩa là "lặp lại ký tự trước đó" - hoàn toàn khác. Viết
 * {@code "regex:*.java"} là lỗi cú pháp regex.
 * - {@code newDirectoryStream(p, "...")} luôn hiểu chuỗi là GLOB, không có cách truyền regex;
 * muốn regex phải qua {@code getPathMatcher("regex:...")}.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.list(p)} ≈ {@code fs.readdir(p)}, nhưng Node trả MẢNG TÊN TRẦN
 * ({@code "a.txt"}), còn Java trả {@code Path} đã resolve sẵn ({@code /tmp/a.txt}).
 * Đây là chỗ port code từ Node sang Java hay sai.
 * - {@code fs.readdir(p, {withFileTypes: true})} của Node ≈ lấy kèm
 * {@code BasicFileAttributes} - cùng một ý tưởng tiết kiệm lượt chạm đĩa ở bài t7.
 * - Node không có API đọc dần thư mục cho tới {@code fs.opendir()} (Node 12) - chính là
 * {@code DirectoryStream} của Java, ra đời trước cả chục năm.
 * - Glob của Java ≈ glob của shell và của thư viện {@code glob} trên npm, cùng bộ luật.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao {@code DirectoryStream} "scale tốt với thư mục rất lớn" còn {@code File.listFiles} thì không?
 * - Vì sao bắt buộc đóng {@code DirectoryStream}, quên đóng thì hỏng thế nào?
 * - Lỗi xảy ra giữa chừng vòng lặp được báo bằng exception gì, nó checked hay unchecked?
 * - {@code newDirectoryStream} có liệt kê file ẩn không, có đi xuống thư mục con không?
 * - Path trả về là tên trần hay đã resolve theo thư mục cha?
 * - Ba cách lọc nội dung thư mục là gì, cái nào lọc được theo "là thư mục hay không"?
 * - {@code *} và {@code **} trong glob khác nhau ở đâu?
 * - {@code Files.list} khác {@code List.stream()} ở điểm sống còn nào?
 */
public class t9_ListingDirectoryContent {

    void main() {
        // TODO 1 (mục 1): Dùng try-with-resources + Files.newDirectoryStream để liệt kê thư mục
        //   hiện tại. In từng Path, xác nhận nó là đường dẫn đã resolve chứ không phải tên trần.
        //
        // TODO 2 (mục 1): Cố tình BỎ try-with-resources ở TODO 1. Code vẫn chạy đúng - giải thích
        //   vì sao cái sai này nguy hiểm hơn một lỗi làm chương trình chết ngay.
        //
        // TODO 3 (mục 2): Liệt kê các file .java trong thư mục này bằng glob "*.java".
        //   Rồi đổi sang "t1*.java" và "t?_*.java", ghi lại khác biệt.
        //
        // TODO 4 (mục 3): Viết một DirectoryStream.Filter<Path> chỉ lấy file có kích thước
        //   lớn hơn 4000 byte (dùng Files.size). Áp dụng lên thư mục này.
        //
        // TODO 5 (mục 5): Làm lại TODO 3 bằng Files.list(...).filter(...). So sánh số dòng code
        //   của hai cách và nêu ưu nhược.
        //
        // TODO 6 (mục 4, 7): Dùng FileSystems.getDefault().getPathMatcher("glob:*.java") rồi
        //   matcher.matches() trên hai Path: Path.of("Main.java") và Path.of("src/Main.java").
        //   Dán hai kết quả, sau đó đổi pattern sang "glob:**.java" và chạy lại.
        //
        // TODO 7 (mục 6): Liệt kê một thư mục có nhiều file rồi in theo thứ tự nhận được.
        //   Chạy lại vài lần xem thứ tự có đổi không, sau đó thêm sorted() để ép thứ tự.
    }
}
