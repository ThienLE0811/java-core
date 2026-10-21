package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * JShell - The Java Shell Tool
 * <p>
 * 0. Vị trí bài này
 * - Bài 6/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jshell/">dev.java - JShell - The Java Shell Tool</a>
 * - Nguyên văn định nghĩa: {@code jshell} "interactively evaluates declarations, statements,
 * and expressions of the Java programming language in a read-eval-print loop (REPL)".
 * - Có từ JDK 9. Đây là tool làm Java bớt nặng nề nhất đối với người đến từ
 * JavaScript/TypeScript: cuối cùng cũng có một REPL như {@code node}.
 * - {@code jshell} KHÔNG có {@code ToolProvider} (xem {@code t1_} mục 5), nên
 * {@code main()} bên dưới chạy nó bằng {@code ProcessBuilder} với một file script.
 * - Mọi kết quả ở mục 6 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JSHELL (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. SNIPPET - ĐƠN VỊ LÀM VIỆC
 * - Gõ vào được: câu lệnh, biểu thức, khai báo biến/phương thức/class, import.
 * - Mỗi thứ như vậy là một SNIPPET và có một ID (1, 2, 3...). ID là thứ dùng để sửa, xoá,
 * chạy lại.
 * - Khác biệt lớn nhất so với Java bình thường:
 * + Không cần class bao ngoài, không cần {@code main}.
 * + Dấu chấm phẩy cuối câu là TUỲ CHỌN với một lệnh đơn lẻ.
 * + Khai báo PHƯƠNG THỨC ở cấp cao nhất - thứ Java không cho phép ở đâu khác.
 * + Khai báo lại cùng tên biến với KIỂU KHÁC là hợp lệ - nó thay thế snippet cũ.
 * + Exception không làm sập phiên.
 * <p>
 * 2. SCRATCH VARIABLE - BIẾN TỰ ĐẶT TÊN
 * - Gõ một biểu thức không gán cho ai, {@code jshell} tự tạo biến tên {@code $N} giữ kết
 * quả, với {@code N} là ID của snippet.
 * - Dùng lại được ngay ở snippet sau. Đây là cách thử nhanh mà không phải đặt tên biến.
 * <p>
 * 3. CÁC LỆNH {@code /} PHẢI THUỘC
 * - Xem: {@code /list} (snippet kèm ID), {@code /vars}, {@code /methods}, {@code /types},
 * {@code /imports}, {@code /history}.
 * - Sửa: {@code /edit <id>} (mở editor ngoài), {@code /drop <id>} (bỏ một snippet).
 * - Chạy lại: {@code /!} (snippet vừa rồi), {@code /<id>} (theo ID), {@code /-n} (lùi n
 * bước).
 * - Phiên làm việc: {@code /save <file>}, {@code /open <file>}, {@code /reset},
 * {@code /reload}, {@code /env}, {@code /exit}.
 * - Trợ giúp: {@code /help}, {@code /?}. {@code /help} có cả chủ đề, không chỉ danh sách
 * lệnh.
 * - MẸO: mọi lệnh bắt đầu bằng {@code /}, mọi thứ khác là code Java. Không nhầm được.
 * <p>
 * 4. PHÍM TẮT
 * - Tab: tự hoàn thành tên, và gợi ý luôn tham số của phương thức.
 * - Shift+Tab rồi {@code v}: biến biểu thức vừa gõ thành khai báo biến (tự điền kiểu).
 * - Mũi tên lên/xuống: lịch sử. Ctrl+A / Ctrl+E: đầu/cuối dòng (kiểu readline).
 * <p>
 * 5. FEEDBACK MODE
 * - {@code /set feedback <mode>} với các mode sẵn có: {@code verbose}, {@code normal}
 * (mặc định), {@code concise}, {@code silent}.
 * - Tự tạo mode mới bằng {@code /set mode <tên> <mode gốc> -command}, rồi chỉnh
 * {@code /set format}, {@code /set truncation}, {@code /set prompt}.
 * - {@code silent} là mode dùng khi chạy script - xem {@code t7_}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 6. SCRATCH VARIABLE - ĐO THẬT
 * - Nhập liên tiếp {@code int x = 2}, {@code x + 40}, {@code "abc".length()} rồi
 * {@code /list}, kết quả thật:
 * {@code int x = 2}
 * {@code int $2 = 42}
 * {@code int $3 = 3}
 * {@code 1 : int x = 2;}
 * {@code 2 : x + 40}
 * {@code 3 : "abc".length()}
 * - Thấy rõ hai điều: số của biến {@code $N} chính là ID snippet, và {@code /list} in lại
 * NGUYÊN VĂN snippet (có thêm dấu chấm phẩy mà jshell tự bù vào).
 * <p>
 * 7. CHẠY FILE SCRIPT THÌ KHÔNG VÀO CHẾ ĐỘ TƯƠNG TÁC
 * - {@code jshell file.jsh} chạy hết file rồi ĐỨNG LẠI ở dấu nhắc. Muốn thoát luôn thì
 * dòng cuối file phải là {@code /exit}.
 * - Đây là chi tiết khiến script treo trong CI: quên {@code /exit}, tiến trình chờ stdin
 * mãi mãi.
 * - {@code --execution local} chạy snippet NGAY TRONG JVM của jshell thay vì một JVM con.
 * Nhanh hơn hẳn và tránh rắc rối khởi động tiến trình phụ, phù hợp cho script tự động.
 * <p>
 * 8. BA GIỚI HẠN KHÔNG ĐƯỢC GHI TRONG TRANG GỐC
 * - KHÔNG dùng được class ở DEFAULT PACKAGE. Đo thật: nạp {@code demo.jar} chứa
 * {@code Demo} ở package mặc định rồi gõ {@code Demo.class.getName()} ->
 * {@code cannot find symbol}. Snippet của jshell được biên dịch trong package riêng của
 * nó nên không nhìn thấy package mặc định. Thư viện muốn thử trong jshell phải có
 * package.
 * - Chỉ thấy được thành viên PUBLIC. Đo thật với class package-private:
 * {@code greet.Helper is not public in greet; cannot be accessed from outside package}.
 * Lỗi này đồng thời chứng minh jar ĐÃ nằm trên classpath - phân biệt với lỗi
 * {@code cannot find symbol} ở trên.
 * - KHÔNG có {@code ToolProvider} cho jshell, nên không gọi được in-process. Muốn tự động
 * hoá phải qua {@code ProcessBuilder}, hoặc dùng API {@code jdk.jshell.JShell} (module
 * {@code jdk.jshell}) - đó mới là cổng lập trình chính thức.
 * <p>
 * 9. KHI NÀO BACKEND THỰC SỰ MỞ JSHELL
 * - Thử một phương thức của thư viện mà không muốn tạo file test: lấy regex, thử
 * {@code DateTimeFormatter}, kiểm tra hành vi của {@code String.split}.
 * - Kiểm chứng một câu khẳng định về JDK ngay lập tức, như cách bài này đo
 * {@code ToolProvider} ở {@code t1_}.
 * - Debug cấu hình: {@code System.getProperty(...)}, {@code Charset.defaultCharset()}.
 * - KHÔNG dùng để viết logic thật. Snippet không có kiểm soát phiên bản, không có test.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Snippet là gì, ID dùng để làm gì?
 * - Biến {@code $3} từ đâu ra?
 * - Ba lệnh để xem lại những gì đã gõ?
 * - Vì sao script jshell trong CI hay bị treo?
 * - Vì sao class ở default package không dùng được trong jshell?
 * - Muốn gọi jshell từ code Java thì dùng gì, vì {@code ToolProvider} không có?
 */
public class t6_JShellTheJavaShellTool {

    void main() throws IOException, InterruptedException {
        Path jshellExe = Path.of(System.getProperty("java.home"), "bin", "jshell");
        Path work = Files.createTempDirectory("jshell-demo");
        try {
            // Script tái hiện đúng mục 6. Dòng /exit là bắt buộc, nếu không tiến trình con
            // sẽ chờ stdin mãi mãi (mục 7).
            Path script = work.resolve("demo.jsh");
            Files.writeString(script, """
                    int x = 2
                    x + 40
                    "abc".length()
                    $2 + $3
                    /vars
                    /list
                    /exit
                    """);

            System.out.println("=== jshell chạy script, feedback mặc định ===");
            ProcessBuilder builder = new ProcessBuilder(
                    jshellExe.toString(), "--execution", "local", script.toString());
            builder.directory(work.toFile());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            System.out.print(new String(process.getInputStream().readAllBytes()));
            System.out.println("mã thoát = " + process.waitFor());
            System.out.println("""

                    Đọc kết quả trên:
                      - $2, $3 là scratch variable, số trùng với ID snippet (mục 2, mục 6).
                      - /vars liệt kê cả biến tự đặt tên lẫn biến mình khai.
                      - /list in lại nguyên văn snippet kèm ID - thứ dùng cho /edit, /drop.""");
        } finally {
            deleteRecursively(work);
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(root)) {
            for (Path path : walk.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
