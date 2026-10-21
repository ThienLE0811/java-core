package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Advanced JShell Usage
 * <p>
 * 0. Vị trí bài này
 * - Bài 7/12 của section The Core JDK Tools, nối thẳng từ {@code t6_}.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jshell-advanced/">dev.java - Advanced JShell Usage</a>
 * - Trang gốc có 4 mục: Overview, Load External Libraries to JShell, Create and Load Custom
 * JShell Scripts, How to Use JDK Tools Inside JShell.
 * - Đây là trang HIẾM trong section này: không phải man page, mà là hướng dẫn thật với một
 * ví dụ xuyên suốt (dựng một công cụ đánh giá biểu thức bằng JavaFX).
 * - Một câu cho cả bài: {@code jshell} không chỉ là chỗ gõ thử - nó nạp được thư viện, lưu
 * được phiên thành script, và gọi được các tool JDK khác.
 * - Mọi kết quả ở mục 5 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JSHELL NÂNG CAO (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. NẠP THƯ VIỆN NGOÀI
 * - Lúc khởi động: {@code jshell --class-path <đường dẫn>} cho jar thường,
 * {@code jshell --module-path <đường dẫn> --add-modules <tên module>} cho module.
 * - Đang chạy giữa chừng: {@code /env --class-path ...} hoặc {@code /env --module-path ...}.
 * - LƯU Ý QUAN TRỌNG: {@code /env} KHỞI ĐỘNG LẠI trạng thái phiên rồi phát lại các snippet
 * cũ. Nên nó an toàn với khai báo, nhưng tác dụng phụ (ghi file, gọi mạng) sẽ chạy LẠI.
 * - Ví dụ của trang gốc dùng JavaFX, vốn không còn nằm trong JDK từ JDK 11 - đó chính là
 * lý do cần {@code --module-path}.
 * <p>
 * 2. LƯU VÀ NẠP SCRIPT
 * - {@code /save <file>}: ghi các snippet ĐANG HOẠT ĐỘNG ra file.
 * - {@code /save -history <file>}: ghi TOÀN BỘ lịch sử, gồm cả lệnh {@code /} và các
 * snippet đã bị thay thế.
 * - {@code /open <file>}: nạp script vào phiên đang chạy.
 * - {@code jshell <file.jsh>}: nạp ngay lúc khởi động.
 * - {@code /set start <file>}: đặt script khởi động mặc định cho MỌI phiên sau - chỗ để
 * đặt sẵn import hay hàm tiện ích hay dùng.
 * - Cách làm việc trang gốc gợi ý: thử nghiệm trong jshell, {@code /save} lại thành
 * {@code setup-....jsh}, lần sau {@code jshell setup-....jsh} là có nguyên môi trường.
 * <p>
 * 3. GỌI TOOL JDK NGAY TRONG JSHELL
 * - {@code /open TOOLING} nạp một script DỰNG SẴN trong JDK (không phải file trên máy
 * bạn), cung cấp ba hàm: {@code tools()} liệt kê tool, {@code run(tên, tham số...)} chạy
 * tool bất kỳ, và các hàm riêng như {@code javac(...)}, {@code jar(...)}.
 * - Bên dưới nó chính là {@code java.util.spi.ToolProvider} đã học ở {@code t1_} mục 5 -
 * nên danh sách tool y hệt, và cũng thiếu đúng {@code java}/{@code jshell}/
 * {@code jdeprscan}.
 * - Trang gốc dùng nó để biên dịch rồi đóng gói một jar chạy được mà không rời jshell.
 * <p>
 * 4. {@code /edit} VÀ EDITOR NGOÀI
 * - {@code /edit} mở trình soạn thảo cho snippet dài. Mặc định là một cửa sổ Swing tối
 * giản; {@code /set editor <lệnh>} để đổi sang editor quen thuộc.
 * - {@code /save src/example/VisualJShell.java} là cách trang gốc chuyển từ snippet sang
 * file nguồn thật khi ý tưởng đã chín.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 5. {@code tools()} TRẢ VỀ ĐÚNG 9 TOOL
 * - Chạy thật {@code /open TOOLING} rồi {@code tools()}:
 * {@code jar javac javadoc javap jdeps jlink jmod jnativescan jpackage}
 * - Khớp từng chữ với danh sách {@code ToolProvider} đo được ở {@code t1_}. Đây là bằng
 * chứng TOOLING chỉ là lớp vỏ mỏng của SPI đó.
 * <p>
 * 6. HAI LỖI KHI NẠP JAR - PHÂN BIỆT ĐƯỢC THÌ KHỎI ĐOÁN MÒ
 * - Nạp {@code demo.jar} có class {@code Demo} ở PACKAGE MẶC ĐỊNH, gõ {@code Demo}:
 * {@code cannot find symbol}. Nghĩa là jshell không bao giờ thấy được package mặc định,
 * kể cả khi jar đã nằm trên classpath.
 * - Nạp {@code greet.jar} có class {@code greet.Helper} KHÔNG public, gõ
 * {@code new greet.Helper()}:
 * {@code greet.Helper is not public in greet; cannot be accessed from outside package}
 * - Cùng là lỗi, nhưng thông báo thứ hai CHỨNG MINH classpath đã đúng - chỉ là thiếu
 * {@code public}. Thông báo thứ nhất mới là "không nạp được".
 * - Quy tắc rút ra: {@code cannot find symbol} -&gt; xem lại classpath hoặc package;
 * {@code is not public} -&gt; classpath đúng rồi, vấn đề là phạm vi truy cập.
 * <p>
 * 7. JSHELL TRONG SCRIPT TỰ ĐỘNG
 * - Tổ hợp đáng nhớ: {@code jshell --execution local -q <file.jsh>} với dòng cuối là
 * {@code /exit}.
 * + {@code --execution local}: chạy trong cùng JVM, nhanh hơn, không sinh tiến trình phụ.
 * + {@code -q} (hoặc {@code --feedback concise}): bớt ồn.
 * + {@code /exit}: nếu thiếu, tiến trình treo chờ stdin (xem {@code t6_} mục 7).
 * - Muốn điều khiển jshell bằng CODE chứ không bằng tiến trình: dùng API
 * {@code jdk.jshell.JShell} trong module {@code jdk.jshell}. Đó là thứ các IDE dùng để
 * làm scratch file.
 * <p>
 * 8. SO SÁNH VỚI NODE.JS ĐỂ NHỚ NHANH
 * - {@code jshell} ~ {@code node} (REPL).
 * - {@code /env --class-path x.jar} ~ cài thêm package rồi {@code require} - nhưng jshell
 * KHỞI ĐỘNG LẠI phiên, {@code require} thì không.
 * - {@code /save} ~ chép lịch sử REPL ra file {@code .js}.
 * - {@code /open TOOLING} thì Node không có thứ tương đương: nó là chuyện gọi được cả
 * {@code tsc} lẫn {@code npm pack} ngay trong REPL.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Hai cách nạp jar vào jshell? {@code /env} có tác dụng phụ gì?
 * - {@code /save} khác {@code /save -history} ra sao?
 * - {@code TOOLING} lấy danh sách tool từ đâu?
 * - Phân biệt {@code cannot find symbol} và {@code is not public} khi nạp jar?
 * - Ba cờ để chạy jshell trong CI mà không bị treo?
 */
public class t7_AdvancedJShellUsage {

    void main() throws IOException, InterruptedException {
        Path jshellExe = Path.of(System.getProperty("java.home"), "bin", "jshell");
        Path work = Files.createTempDirectory("jshell-adv-demo");
        try {
            // Dựng sẵn một jar để nạp: class có package và PUBLIC thì dùng được, class
            // package-private thì không (mục 6).
            Path src = work.resolve("src/greet");
            Files.createDirectories(src);
            Files.writeString(src.resolve("Greeter.java"), """
                    package greet;
                    public class Greeter {
                        public static String hi(String name) { return "xin chào " + name; }
                    }
                    """);
            Files.writeString(src.resolve("Helper.java"), """
                    package greet;
                    class Helper {
                        String msg() { return "không public"; }
                    }
                    """);

            Path classes = work.resolve("classes");
            ToolProvider.findFirst("javac").orElseThrow().run(System.out, System.err,
                    "-d", classes.toString(),
                    src.resolve("Greeter.java").toString(), src.resolve("Helper.java").toString());

            Path jar = work.resolve("greet.jar");
            ToolProvider.findFirst("jar").orElseThrow().run(System.out, System.err,
                    "--create", "--file", jar.toString(), "-C", classes.toString(), ".");

            // Script: nạp jar bằng /env, gọi class public, rồi cố gọi class package-private.
            Path script = work.resolve("adv.jsh");
            Files.writeString(script, """
                    /env --class-path %s
                    System.out.println("gọi class public: " + greet.Greeter.hi("jshell"))
                    new greet.Helper().msg()
                    /open TOOLING
                    tools()
                    /exit
                    """.formatted(jar.toString().replace("\\", "/")));

            System.out.println("=== jshell: /env nạp jar, rồi /open TOOLING ===");
            ProcessBuilder builder = new ProcessBuilder(
                    jshellExe.toString(), "--execution", "local", script.toString());
            builder.directory(work.toFile());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            System.out.print(new String(process.getInputStream().readAllBytes()));
            System.out.println("mã thoát = " + process.waitFor());
            System.out.println("""

                    Đọc kết quả trên:
                      - greet.Greeter.hi(...) chạy được  -> jar đã nằm trên classpath.
                      - new greet.Helper()   báo "is not public" -> vẫn là classpath đúng,
                        chỉ thiếu từ khoá public (mục 6).
                      - tools() in ra đúng 9 tool của ToolProvider, không hơn (mục 5).""");
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
