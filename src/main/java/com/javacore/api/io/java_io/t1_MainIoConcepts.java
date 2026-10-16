package com.javacore.api.io.java_io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Understanding the Main Java I/O Concepts
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 1/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Bài mở màn: dựng từ vựng chung (I/O, NIO, NIO2, File, Path, bốn lớp stream gốc).
 * Chưa đi vào API chi tiết, các bài sau mổ xẻ từng phần.
 * - Một câu cho cả bài: Java I/O có hai việc - ĐỊNH VỊ tài nguyên (Path) và MỞ LUỒNG tới nó (stream).
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/intro/">dev.java - Understanding the Main Java I/O Concepts</a>
 * <p>
 * ======================================================================
 * TRANG GỐC - UNDERSTANDING THE MAIN JAVA I/O CONCEPTS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. JAVA I/O API LÀ GÌ
 * - (Introducing the Java I/O API)
 * - I/O = Input / Output. API cho ứng dụng truy cập "bên ngoài", gồm hai thứ: file system
 * (không nhất thiết là đĩa, "they may reside in memory") và network.
 * - Thứ ba truy cập được qua phần mở rộng của Java I/O: off-heap memory (chính là section ffm, bước 23).
 * - JDBC chạy TRÊN Java I/O: nó dùng Java I/O để nói chuyện TCP/IP với database.
 * <p>
 * 2. BA ĐỜI API - I/O, NIO, NIO2
 * - (Understanding Java I/O, Java NIO and Java NIO2)
 * - Java I/O: giữa thập niên 90, cùng các bản JDK đầu tiên.
 * - Java NIO: 2002, Java SE 1.4. Ban đầu NIO nghĩa là "New I/O", nhưng cái mới rồi cũng cũ
 * nên nay hiểu là "Non-blocking I/O". NIO KHÔNG đổi cách dùng Java I/O, pattern vẫn như cũ.
 * - Java NIO2: 2011, Java SE 7. Đây mới là bản mang PATTERN MỚI. Java SE 8 bổ sung thêm.
 * - Tutorial dạy cả ba phần, nên sẽ thấy hai lối viết song song trong cùng một section.
 * <p>
 * 3. HAI CÁCH TRUY CẬP FILE - FILE VÀ PATH
 * - (Accessing a File)
 * - "There are two main concepts in Java I/O": ĐỊNH VỊ tài nguyên, và MỞ STREAM tới tài nguyên đó.
 * - {@code File} (Java SE 1.0): coi như wrapper quanh một chuỗi ký tự biểu diễn đường dẫn
 * trên file system mặc định.
 * - {@code Path} (Java SE 7, thuộc NIO2) sinh ra để vá bảy nhược điểm của {@code File}:
 * <ol>
 *   <li>Nhiều method THẤT BẠI MÀ KHÔNG NÉM EXCEPTION. {@code file.delete()} trả về
 *       {@code false} nhưng không biết vì file không tồn tại, vì thiếu quyền, hay vì lý do khác.</li>
 *   <li>{@code rename} chạy không nhất quán giữa các nền tảng.</li>
 *   <li>Không thực sự hỗ trợ symbolic link.</li>
 *   <li>Thiếu hỗ trợ metadata: quyền, chủ sở hữu, thuộc tính bảo mật.</li>
 *   <li>Đọc metadata không hiệu quả.</li>
 *   <li>Không scale: liệt kê thư mục lớn qua server có thể treo, hoặc ngốn bộ nhớ
 *       dẫn tới từ chối dịch vụ (denial of service).</li>
 *   <li>Không viết nổi code duyệt đệ quy cây thư mục mà xử lý đúng khi gặp symbolic link vòng.</li>
 * </ol>
 * - Kết luận nguyên văn của trang gốc: "So using the File class is not recommended anymore."
 * - Có một bài sau trong section này dạy refactor code {@code File} cũ sang {@code Path}.
 * <p>
 * 4. I/O STREAM VÀ BỐN LỚP GỐC
 * - (Understanding I/O Streams)
 * - "An I/O Stream represents an input source or an output destination." Nguồn hoặc đích có thể là
 * file trên đĩa, thiết bị, chương trình khác, socket mạng, hoặc mảng trong bộ nhớ.
 * - "a stream is a sequence of data": đọc/ghi MỘT PHẦN TỬ MỘT LẦN, bất kể bên trong cài đặt ra sao.
 * - BẪY TÊN GỌI: I/O stream KHÁC HẲN Stream API của Java SE 8 (bước 20). Trùng tên, khác khái niệm.
 * - API chia nội dung làm hai loại và thao tác làm hai chiều, giao nhau thành bốn lớp TRỪU TƯỢNG gốc:
 * <table border="1">
 *   <caption>Bốn lớp gốc của Java I/O</caption>
 *   <tr><th>&nbsp;</th><th>Reading</th><th>Writing</th></tr>
 *   <tr><td>Ký tự (text, XML, JSON)</td><td>{@code Reader}</td><td>{@code Writer}</td></tr>
 *   <tr><td>Byte (ảnh, video)</td><td>{@code InputStream}</td><td>{@code OutputStream}</td></tr>
 * </table>
 * - Mọi lớp byte stream đều thừa kế {@code InputStream}/{@code OutputStream}; mọi lớp character
 * stream đều thừa kế {@code Reader}/{@code Writer}. Nhìn hậu tố tên lớp là biết nó thuộc ô nào.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BA CÁI TÊN DỄ LOẠN
 * - {@code java.io.File} - lớp cũ, nên tránh.
 * - {@code java.nio.file.Path} - interface, cách hiện tại. Tạo bằng {@code Path.of(...)}
 * (Java SE 11+), không dùng {@code Paths.get(...)} nữa.
 * - {@code java.nio.file.Files} - lớp tiện ích toàn method static, làm mọi thao tác TRÊN một {@code Path}.
 * - Luật nhớ: {@code Path} chỉ là ĐỊA CHỈ, {@code Files} mới là HÀNH ĐỘNG. Khác hẳn {@code File} cũ
 * vốn gộp cả hai vai vào một lớp.
 * <p>
 * 6. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code Path} ≈ module {@code path} của Node (chỉ thao tác chuỗi đường dẫn, không chạm đĩa).
 * - {@code Files} ≈ module {@code fs} (chạm đĩa thật).
 * - Node tách sẵn hai module này từ đầu; Java mất 15 năm mới tách được chúng ra khỏi {@code File}.
 * - I/O stream ≈ {@code Readable}/{@code Writable} của Node, KHÔNG phải method của mảng như Stream API.
 * - Phân biệt Reader với InputStream ≈ đọc file kèm encoding {@code "utf8"} so với đọc ra {@code Buffer}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 7. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Hai việc chính của Java I/O là gì?
 * - NIO và NIO2 ra đời năm nào, bản nào mới thực sự đổi pattern lập trình?
 * - Kể ba nhược điểm của {@code File} mà {@code Path} vá được.
 * - {@code file.delete()} trả về {@code false} thì biết được gì về nguyên nhân?
 * - Bốn lớp trừu tượng gốc là gì, điền đúng vào bảng 2x2 (ký tự/byte × đọc/ghi).
 * - I/O stream và Stream API khác nhau ở đâu?
 * - Đọc một file JSON thì dùng nhánh nào, đọc một file PNG thì dùng nhánh nào?
 */
public class t1_MainIoConcepts {

    void main() {
        // Chứng minh nhược điểm số 1 của File: thất bại im lặng.
        // File dưới đây chắc chắn không tồn tại nên cả hai lời gọi đều hỏng - khác nhau ở cách BÁO LỖI.
        Path khongTonTai = Path.of("khong-ton-tai-" + System.nanoTime() + ".txt");

        boolean xoaDuoc = new File(khongTonTai.toString()).delete();
        IO.println("File.delete()  -> " + xoaDuoc + "   (chỉ biết là hỏng, không biết vì sao)");

        try {
            Files.delete(khongTonTai);
        } catch (IOException e) {
            IO.println("Files.delete() -> ném " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Path chỉ là ĐỊA CHỈ: hai phép dưới đây không chạm đĩa, file không cần tồn tại.
        Path thuMuc = Path.of("data", "2026");
        IO.println("resolve     -> " + thuMuc.resolve("sales.csv"));
        IO.println("tuyệt đối   -> " + thuMuc.toAbsolutePath());

        // TODO 1 (mục 3): Gọi new File("/khong/co/thu/muc/a.txt").createNewFile() và
        //   Files.createFile(Path.of("/khong/co/thu/muc/a.txt")). Dán lại hai kết quả,
        //   chỉ ra cái nào nói được NGUYÊN NHÂN.
        //
        // TODO 2 (mục 3): Tìm trong javadoc của java.io.File ba method trả về boolean thay vì
        //   ném exception khi thất bại. Ghi tên chúng vào đây.
        //
        // TODO 3 (mục 4): Với mỗi tình huống, viết tên lớp gốc đúng (Reader/Writer/InputStream/
        //   OutputStream): đọc file .properties | ghi ảnh JPEG | đọc response JSON từ HTTP | ghi log text.
        //
        // TODO 4 (mục 4): Mở javadoc InputStream, liệt kê 5 lớp con trực tiếp của nó. Đoán xem
        //   lớp nào đọc từ mảng byte trong bộ nhớ (bài t17 sẽ xác nhận).
        //
        // TODO 5 (mục 2): Tra xem Path, Files, InputStream, BufferedReader nằm ở package nào.
        //   Cái nào thuộc java.io (API đời đầu), cái nào thuộc java.nio.file (NIO2)?
        //
        // TODO 6 (mục 5): Chạy thử Path.of("a", "b").resolve("/c") và Path.of("a/b/../c").normalize().
        //   Ghi lại output, giải thích vì sao resolve một đường dẫn tuyệt đối lại ra kết quả đó.
    }
}
