package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Java - Your Application Launcher
 * <p>
 * 0. Vị trí bài này
 * - Bài 5/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/java/">dev.java - Java - Your Application Launcher</a>
 * - ĐÂY LÀ TRANG DÀI NHẤT SECTION: 13 mục, và 7 mục cuối là bảng option {@code -XX} (JIT,
 * GC, serviceability) dài hàng trăm dòng. Những bảng đó KHÔNG thuộc về bài này - chúng là
 * nội dung của section Monitoring and Troubleshooting và section GC. Ở đây chỉ giữ phần
 * launcher.
 * - Khác với 7 bài kia, {@code java} KHÔNG có {@code ToolProvider}. Không thể gọi in-process
 * (hiển nhiên: nó là thứ đang chạy chính mình). {@code main()} bên dưới dùng
 * {@code ProcessBuilder}.
 * - Mọi kết quả ở mục 6 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JAVA (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. BỐN DẠNG GỌI - NHỚ ĐỦ BỐN
 * - {@code java [options] mainclass [args...]} - cổ điển, tìm class trên classpath.
 * - {@code java [options] -jar jarfile [args...]} - lấy {@code Main-Class} từ manifest.
 * - {@code java [options] -m module[/mainclass] [args...]} - chạy từ module path (JDK 9+).
 * - {@code java [options] source-file [args...]} - chạy thẳng file nguồn (JDK 11+), mục 3.
 * - Bốn dạng này quyết định cách class được NẠP, nên quyết định luôn lỗi bạn sẽ gặp.
 * <p>
 * 2. NHÓM OPTION ĐÁNG NHỚ
 * - Đường dẫn: {@code -cp} / {@code --class-path}, {@code -p} / {@code --module-path},
 * {@code --add-modules}, {@code --list-modules}, {@code --describe-module}.
 * - Cấu hình: {@code -Dkhoá=giá trị}, {@code --enable-preview}, {@code --dry-run}.
 * - Assertion: {@code -ea} / {@code -da}. Mặc định assertion TẮT lúc chạy - đây là lý do
 * {@code assert} trong code production coi như không tồn tại nếu quên {@code -ea}.
 * - Chẩn đoán: {@code -verbose:class}, {@code -verbose:gc}, {@code -verbose:module},
 * {@code -XshowSettings:properties}, {@code --show-module-resolution}.
 * - Bộ nhớ ({@code -X}): {@code -Xms}, {@code -Xmx}, {@code -Xss}, {@code -Xint},
 * {@code -Xshare}.
 * - {@code @argfile} để nhét tham số vào file khi dòng lệnh quá dài -
 * {@code --disable-@files} tắt tính năng này.
 * <p>
 * 3. SOURCE-FILE MODE
 * - Từ JDK 11: {@code java HelloWorld.java} biên dịch TRONG BỘ NHỚ rồi chạy luôn, không hề
 * sinh file {@code .class}.
 * - Quy tắc trang gốc nêu rõ: chạy class CÔNG KHAI ĐẦU TIÊN tìm thấy trong file, và class
 * nạp vào unnamed module.
 * - {@code --source N} cho phép chạy file KHÔNG có đuôi {@code .java} (kiểu script shell có
 * dòng shebang).
 * - Các option biên dịch đặt TRƯỚC tên file mới có tác dụng: {@code --class-path},
 * {@code --module-path}, {@code --add-modules}, {@code --enable-preview}...
 * <p>
 * 4. {@code JDK_JAVA_OPTIONS}
 * - Nội dung biến môi trường được NỐI VÀO TRƯỚC tham số dòng lệnh. Tức là
 * {@code JDK_JAVA_OPTIONS=-Xmx1024m -Dprop=value} rồi {@code java MyApp} tương đương
 * {@code java -Xmx1024m -Dprop=value MyApp}.
 * - Không cho phép các option chỉ định main class ({@code -jar}) hay làm dừng sớm
 * ({@code -h}).
 * - Đây là cách đặt cờ JVM trong container mà không sửa được lệnh khởi động - dùng rất
 * nhiều trong Docker image của Spring Boot.
 * <p>
 * 5. NHÓM {@code -XX} - BIẾT ĐỂ KHÔNG SỢ
 * - {@code -XX:+HeapDumpOnOutOfMemoryError} và {@code -XX:HeapDumpPath=...}: hai cờ đáng
 * bật mặc định cho MỌI dịch vụ production. Không có chúng, một lần OOM là mất sạch bằng
 * chứng.
 * - {@code -XX:+PrintCommandLineFlags}: in ra các cờ JVM tự chọn theo máy. Cách nhanh nhất
 * để biết GC nào đang chạy và heap mặc định là bao nhiêu.
 * - {@code -XX:MaxRAMPercentage}: trong container nên dùng thay cho {@code -Xmx} cố định.
 * - {@code -XX:ActiveProcessorCount}, {@code -XX:-UseContainerSupport}: liên quan tới việc
 * JVM đọc giới hạn cgroup. Mặc định JVM hiện đại ĐÃ nhận biết container.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 6. {@code -jar} BỎ QUA {@code -cp} - BẪY LỚN NHẤT CỦA BÀI NÀY
 * - Dựng thí nghiệm: {@code a.jar} chứa đúng {@code A.class} và khai {@code Main-Class: A};
 * {@code B.class} để riêng một thư mục.
 * - {@code java -cp cpt/onlyB -jar cpt/a.jar} ra:
 * {@code Exception in thread "main" java.lang.NoClassDefFoundError: B}
 * {@code Caused by: java.lang.ClassNotFoundException: B}
 * - Cùng bộ class đó, đổi sang dạng gọi class: {@code java -cp "cpt/a.jar;cpt/onlyB" A} ->
 * in ra bình thường.
 * - KẾT LUẬN: khi dùng {@code -jar}, classpath người dùng được định nghĩa HOÀN TOÀN bởi
 * thuộc tính {@code Class-Path} trong manifest; mọi {@code -cp} trên dòng lệnh bị BỎ QUA
 * lặng lẽ, không một cảnh báo nào.
 * - Đây là lý do thực sự của "fat jar"/"uber jar": không phải vì tiện, mà vì {@code -jar}
 * không cho thêm classpath từ ngoài.
 * - Trên Windows dấu phân cách classpath là {@code ;}, trên Linux/macOS là {@code :}. Sai
 * dấu này là lỗi kinh điển khi đem script từ CI Linux về máy Windows.
 * <p>
 * 7. MULTI-FILE SOURCE MODE - TRANG GỐC CHƯA KỊP CẬP NHẬT
 * - Trang gốc mô tả source-file mode như chỉ chạy được MỘT file.
 * - Đo thật trên JDK 25: {@code java src/greet/Hi.java} với {@code Hi} có dùng
 * {@code Helper} nằm ở file khác -&gt; chạy được, in ra {@code hi}, mã thoát 0.
 * - Đó là JEP 458 (Launch Multi-File Source-Code Programs), chính thức từ JDK 22. Launcher
 * tự tìm và biên dịch luôn các file nguồn liên quan trong cùng cây thư mục.
 * - Ý nghĩa: từ JDK 22, một thư mục {@code .java} không cần build tool vẫn chạy được -
 * đúng kiểu chạy một thư mục {@code .js} bằng {@code node}.
 * <p>
 * 8. {@code --version} RA STDOUT, {@code -version} RA STDERR
 * - Đo thật: {@code java --version} ghi ra stdout; {@code java -version} ghi ra stderr
 * (chuyển hướng stderr đi thì màn hình trống trơn).
 * - Vì sao phải nhớ: mọi script CI kiểu {@code JAVA_VER=$(java -version)} đều trả về chuỗi
 * RỖNG. Phải dùng {@code java --version} (hai gạch), hoặc {@code 2>&1}.
 * - Cùng một quy ước ở {@code -help} (stderr) và {@code --help} (stdout).
 * <p>
 * 9. {@code JDK_JAVA_OPTIONS} CÓ BÁO, KHÁC VỚI {@code -cp} BỊ NUỐT
 * - Đo thật với {@code JDK_JAVA_OPTIONS=-Dfoo=bar}, JVM in ra dòng:
 * {@code NOTE: Picked up JDK_JAVA_OPTIONS: -Dfoo=bar}
 * và {@code -XshowSettings:properties} xác nhận {@code foo = bar}.
 * - Dòng NOTE này đi ra STDERR. Khi log production có dòng lạ này, nghĩa là môi trường
 * đang chèn cờ vào sau lưng bạn - chỗ đầu tiên cần nhìn khi "cùng một lệnh mà máy này
 * chạy khác máy kia".
 * <p>
 * 10. BA CỜ NÊN CÓ TRONG MỌI DỊCH VỤ SPRING BOOT
 * - {@code -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/app/heap.hprof}: lưu
 * bằng chứng khi OOM.
 * - {@code -XX:MaxRAMPercentage=75.0}: để JVM tự tính heap theo giới hạn container thay vì
 * {@code -Xmx} cứng.
 * - {@code -Dfile.encoding=UTF-8} nay không còn cần từ JDK 18 (JEP 400 đặt UTF-8 làm mặc
 * định), nhưng {@code -Duser.timezone=UTC} thì vẫn nên có.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn dạng gọi {@code java} là gì?
 * - Vì sao {@code java -cp lib/ -jar app.jar} không nạp được class trong {@code lib/}?
 * - Source-file mode từ JDK mấy, và JDK 22 thêm gì?
 * - {@code java -version} ghi ra stream nào? Hệ quả với script CI?
 * - {@code JDK_JAVA_OPTIONS} nối vào trước hay sau? Dấu hiệu nhận biết trong log?
 * - Assertion mặc định bật hay tắt lúc chạy?
 */
public class t5_JavaYourApplicationLauncher {

    void main() throws IOException, InterruptedException {
        // java KHÔNG có ToolProvider (xem t1_ mục 5), nên phải gọi tiến trình con. Lấy
        // đường dẫn từ java.home thay vì tin vào PATH - xem t1_ mục 4.
        Path javaExe = Path.of(System.getProperty("java.home"), "bin", "java");

        Path work = Files.createTempDirectory("java-launcher-demo");
        try {
            Path src = work.resolve("greet");
            Files.createDirectories(src);
            Files.writeString(src.resolve("Hi.java"), """
                    package greet;
                    public class Hi {
                        public static void main(String[] args) {
                            System.out.println("hi tu " + new Helper().who());
                        }
                    }
                    """);
            Files.writeString(src.resolve("Helper.java"), """
                    package greet;
                    class Helper {
                        String who() { return "file khac"; }
                    }
                    """);

            // 1. Multi-file source mode: không javac, không .class, vẫn chạy (mục 7).
            System.out.println("=== (1) java <file>.java - hai file nguồn, JEP 458 ===");
            run(work, List.of(javaExe.toString(), src.resolve("Hi.java").toString()));

            // 2. --version ra stdout, -version ra stderr (mục 8).
            System.out.println("\n=== (2) --version vs -version: stream nào ===");
            System.out.println("  java --version, chỉ giữ stdout:");
            runCapturingStdoutOnly(work, List.of(javaExe.toString(), "--version"));
            System.out.println("  java -version, chỉ giữ stdout:");
            runCapturingStdoutOnly(work, List.of(javaExe.toString(), "-version"));
            System.out.println("  (trống nghĩa là nó đã ghi ra stderr)");

            // 3. JDK_JAVA_OPTIONS được nhặt vào, và JVM nói ra điều đó (mục 9).
            System.out.println("\n=== (3) JDK_JAVA_OPTIONS ===");
            ProcessBuilder builder = new ProcessBuilder(
                    javaExe.toString(), "-XshowSettings:properties", "--version");
            builder.environment().put("JDK_JAVA_OPTIONS", "-Dfoo=bar");
            builder.directory(work.toFile());
            builder.redirectErrorStream(true);
            Process process = builder.start();
            String output = new String(process.getInputStream().readAllBytes());
            process.waitFor();
            output.lines()
                    .filter(line -> line.contains("Picked up") || line.contains("foo"))
                    .forEach(line -> System.out.println("  " + line.strip()));
        } finally {
            deleteRecursively(work);
        }
    }

    private static void run(Path dir, List<String> command) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command).directory(dir.toFile());
        builder.redirectErrorStream(true);
        Process process = builder.start();
        System.out.print(new String(process.getInputStream().readAllBytes()));
        System.out.println("mã thoát = " + process.waitFor());
    }

    /** Cố tình KHÔNG gộp stderr, để thấy stream nào thực sự mang nội dung (mục 8). */
    private static void runCapturingStdoutOnly(Path dir, List<String> command)
            throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).directory(dir.toFile()).start();
        String stdout = new String(process.getInputStream().readAllBytes()).strip();
        process.waitFor();
        System.out.println("    [" + stdout.lines().findFirst().orElse("") + "]");
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
