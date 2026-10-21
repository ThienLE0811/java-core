package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

/**
 * Jar - the Archive Tool
 * <p>
 * 0. Vị trí bài này
 * - Bài 8/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jar/">dev.java - Jar - the Archive Tool</a>
 * - Nguyên văn: {@code jar} là "a general-purpose archiving and compression tool, based on
 * the ZIP and ZLIB compression formats."
 * - Câu trên đã nói hết điều quan trọng nhất: MỘT FILE JAR LÀ MỘT FILE ZIP. Đổi đuôi thành
 * {@code .zip} là giải nén được bằng bất cứ công cụ nào. Mọi thứ còn lại chỉ là quy ước về
 * nội dung bên trong.
 * - Mọi kết quả ở mục 6 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JAR (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. SÁU CHẾ ĐỘ CHÍNH - CHỌN ĐÚNG MỘT
 * - {@code -c} / {@code --create}: tạo mới.
 * - {@code -t} / {@code --list}: liệt kê nội dung.
 * - {@code -x} / {@code --extract}: giải nén.
 * - {@code -u} / {@code --update}: cập nhật archive có sẵn.
 * - {@code -d} / {@code --describe-module}: in module descriptor.
 * - {@code -i} / {@code --generate-index}: sinh thông tin chỉ mục (gần như không còn dùng).
 * <p>
 * 2. CỜ ĐI KÈM HAY DÙNG
 * - {@code -f} / {@code --file}: tên file archive. KHÔNG có cờ này thì {@code jar} đọc/ghi
 * qua stdin/stdout.
 * - {@code -C <thư mục>}: ĐỔI THƯ MỤC trước khi thêm file kế tiếp. Cờ quan trọng nhất và
 * hay bị hiểu sai - xem mục 6.
 * - {@code -e} / {@code --main-class}: đặt điểm vào, ghi thành {@code Main-Class} trong
 * manifest.
 * - {@code -m} / {@code --manifest}: gộp thêm một manifest tự viết.
 * - {@code -M} / {@code --no-manifest}: không tạo manifest.
 * - {@code -v} / {@code --verbose}, {@code -0} / {@code --no-compress} (chỉ lưu, không nén).
 * - {@code --release <N>}: tạo multi-release jar (mục 4).
 * - {@code --module-version}, {@code --hash-modules}, {@code -p} / {@code --module-path}:
 * dành cho modular jar.
 * <p>
 * 3. MANIFEST - THỨ LÀM JAR KHÁC ZIP
 * - Luôn nằm ở {@code META-INF/MANIFEST.MF}, dạng {@code khoá: giá trị}.
 * - Khoá đáng nhớ: {@code Main-Class} (điểm vào), {@code Class-Path} (classpath phụ - xem
 * {@code t5_} mục 6), {@code Multi-Release}, {@code Automatic-Module-Name}.
 * - {@code Automatic-Module-Name} là cách đặt tên module ỔN ĐỊNH cho một jar chưa có
 * {@code module-info}, để code khác {@code requires} được. Không có nó, tên module được
 * suy ra từ TÊN FILE jar - đổi tên file là gãy.
 * <p>
 * 4. MODULAR JAR VÀ MULTI-RELEASE JAR
 * - Modular jar = jar thường + {@code module-info.class} ở thư mục gốc. Đặt được vào
 * module path. Trang gốc có ví dụ biến jar thường thành modular jar.
 * - Multi-release jar: cùng một jar chứa nhiều phiên bản của cùng một class, đặt ở
 * {@code META-INF/versions/<N>/}. JVM phiên bản N chọn bản phù hợp nhất, JVM cũ hơn chỉ
 * thấy bản ở gốc.
 * - Tạo bằng {@code jar --create --file app.jar -C classes8 . --release 16 -C classes16 .}
 * - Dùng khi một thư viện cần hỗ trợ cả JDK 8 lẫn JDK mới mà vẫn muốn tận dụng API mới.
 * <p>
 * 5. {@code @file}
 * - {@code jar @classes.list} đọc tham số từ file, giống {@code javac} và {@code java}.
 * Dùng khi danh sách file quá dài cho một dòng lệnh Windows.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 6. {@code -C} QUYẾT ĐỊNH CẤU TRÚC BÊN TRONG - LỖI PHỔ BIẾN NHẤT
 * - {@code jar --create --file demo.jar -C out .} cho nội dung:
 * {@code META-INF/}, {@code META-INF/MANIFEST.MF}, {@code Demo.class}
 * - Nếu viết {@code jar --create --file demo.jar out} (không có {@code -C}), đường dẫn
 * {@code out/} sẽ bị GÓI LUÔN VÀO TRONG, thành {@code out/Demo.class}. Lúc chạy sẽ ra
 * {@code ClassNotFoundException} vì tên class không khớp đường dẫn.
 * - Nhớ một câu: {@code -C <thư mục> .} nghĩa là "coi thư mục này là gốc của archive".
 * <p>
 * 7. MANIFEST SINH RA TRÔNG NHƯ THẾ NÀO
 * - Chạy {@code jar --create --file demo.jar --main-class Demo -C out .} rồi đọc
 * {@code META-INF/MANIFEST.MF}, nội dung thật:
 * {@code Manifest-Version: 1.0}
 * {@code Created-By: 25.0.4.1 (Oracle Corporation)}
 * {@code Main-Class: Demo}
 * - Sau đó {@code java -jar demo.jar} chạy được ngay. Nếu thiếu {@code --main-class}, lỗi
 * sẽ là {@code no main manifest attribute, in demo.jar}.
 * <p>
 * 8. {@code --describe-module} TRÊN JAR THƯỜNG - THÔNG BÁO ĐÁNG NHỚ
 * - Chạy trên một jar KHÔNG có {@code module-info} và class nằm ở package mặc định:
 * {@code Unable to derive module descriptor for: demo.jar}
 * {@code Demo.class found in top-level directory (unnamed package not allowed in module)}
 * - Thông báo này dạy hai điều cùng lúc: (a) với jar thường, {@code jar -d} cố SUY RA một
 * automatic module descriptor; (b) module KHÔNG BAO GIỜ chấp nhận package mặc định. Cùng
 * một lý do khiến {@code jdeps --generate-module-info} thất bại ở {@code t11_}.
 * - BẪY ĐO ĐƯỢC: in ra chữ {@code Unable to...} nhưng MÃ THOÁT VẪN LÀ 0. Script CI kiểm
 * tra bằng mã thoát sẽ coi như thành công. Muốn chặn thì phải đọc nội dung đầu ra.
 * So sánh: {@code jdeps --generate-module-info} với cùng tình huống trả về mã khác 0
 * ({@code t11_} mục 7). Hai tool, hai cách báo lỗi - không suy từ cái này ra cái kia.
 * <p>
 * 9. QUY TẮC THỨ TỰ THAM SỐ - KIỂU CŨ RẤT DỄ SAI
 * - Kiểu cũ (một gạch, gộp chữ): {@code jar cvfe app.jar Main -C out .} Các chữ cái
 * {@code c v f e} phải khớp THỨ TỰ với các giá trị đi sau ({@code app.jar} ứng với
 * {@code f}, {@code Main} ứng với {@code e}). Đảo một chữ là jar tạo sai tên file.
 * - Kiểu mới (hai gạch, có tên): {@code jar --create --file app.jar --main-class Main
 * -C out .} Dài hơn nhưng không thể sai thứ tự.
 * - Trong script tự động luôn dùng kiểu hai gạch. Kiểu cũ chỉ để ĐỌC HIỂU khi gặp trong
 * tài liệu cũ.
 * <p>
 * 10. THỰC TẾ: FAT JAR CỦA SPRING BOOT KHÔNG PHẢI JAR THƯỜNG
 * - {@code spring-boot-maven-plugin} tạo một jar có cấu trúc riêng: code của bạn nằm ở
 * {@code BOOT-INF/classes/}, thư viện ở {@code BOOT-INF/lib/}, và
 * {@code Main-Class} trỏ tới {@code JarLauncher} của Spring chứ không phải class của bạn
 * ({@code Start-Class} mới là class của bạn).
 * - Hệ quả: giải nén jar đó bằng {@code jar -x} rồi chạy class trực tiếp sẽ KHÔNG hoạt
 * động như mong đợi. Đây là lý do nhiều người tưởng jar Spring Boot "hỏng".
 * - Mở {@code META-INF/MANIFEST.MF} của một fat jar là cách nhanh nhất để hiểu nó.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Jar khác zip ở chỗ nào?
 * - {@code -C out .} làm gì? Bỏ nó đi thì archive sai ra sao?
 * - Thiếu {@code --main-class} thì {@code java -jar} báo gì?
 * - {@code Automatic-Module-Name} giải quyết vấn đề gì?
 * - Multi-release jar đặt class ở đâu?
 * - Fat jar của Spring Boot khác jar thường thế nào?
 */
public class t8_JarTheArchiveTool {

    void main() throws IOException {
        Path work = Files.createTempDirectory("jar-demo");
        try {
            Path source = work.resolve("Demo.java");
            Files.writeString(source, """
                    public class Demo {
                        public static void main(String[] args) {
                            System.out.println("chạy từ bên trong jar");
                        }
                    }
                    """);
            Path classes = work.resolve("out");
            ToolProvider.findFirst("javac").orElseThrow()
                    .run(System.out, System.err, "-d", classes.toString(), source.toString());

            ToolProvider jar = ToolProvider.findFirst("jar").orElseThrow();
            Path archive = work.resolve("demo.jar");

            // 1. Tạo jar có điểm vào (mục 7).
            System.out.println("=== (1) jar --create --file demo.jar --main-class Demo -C out . ===");
            int rc = jar.run(System.out, System.err,
                    "--create", "--file", archive.toString(),
                    "--main-class", "Demo",
                    "-C", classes.toString(), ".");
            System.out.println("mã thoát = " + rc);

            // 2. Liệt kê nội dung: thấy rõ -C đã đặt Demo.class ở GỐC archive (mục 6).
            System.out.println("\n=== (2) jar --list ===");
            jar.run(System.out, System.err, "--list", "--file", archive.toString());

            // 3. Đọc manifest - jar chỉ là zip nên dùng ZipFile đọc thẳng (mục 3, mục 7).
            System.out.println("\n=== (3) META-INF/MANIFEST.MF bên trong ===");
            try (ZipFile zip = new ZipFile(archive.toFile())) {
                var entry = zip.getEntry("META-INF/MANIFEST.MF");
                try (var in = zip.getInputStream(entry)) {
                    new String(in.readAllBytes()).lines()
                            .filter(line -> !line.isBlank())
                            .forEach(line -> System.out.println("  " + line));
                }
            }

            // 4. --describe-module trên jar thường: thông báo dạy được 2 điều (mục 8).
            System.out.println("\n=== (4) jar --describe-module trên jar KHÔNG có module-info ===");
            int rcDescribe = jar.run(System.out, System.err,
                    "--describe-module", "--file", archive.toString());
            System.out.println("mã thoát = " + rcDescribe
                    + "  <- in ra lỗi nhưng vẫn 0, xem mục 8");

            // 5. Chạy thử bằng java -jar để xác nhận Main-Class có tác dụng.
            System.out.println("\n=== (5) java -jar demo.jar ===");
            Process process = new ProcessBuilder(
                    Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                    "-jar", archive.toString())
                    .redirectErrorStream(true)
                    .start();
            System.out.print(new String(process.getInputStream().readAllBytes()));
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
