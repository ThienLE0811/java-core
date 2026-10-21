package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Jmod - Create JMOD archives
 * <p>
 * 0. Vị trí bài này
 * - Bài 10/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jmod/">dev.java - Jmod - Create JMOD archives</a>
 * - Đây là tool ÍT DÙNG NHẤT trong 11 tool, nhưng cần hiểu vì chính JDK được đóng gói bằng
 * nó: thư mục {@code jmods} trong JDK là nguyên liệu của {@code jlink}.
 * - Một câu cho cả bài: JMOD là định dạng cho những gì JAR KHÔNG CHỨA ĐƯỢC - thư viện
 * native, lệnh thực thi, file cấu hình, header C - và nó chỉ dùng được LÚC LINK, không
 * dùng được lúc chạy.
 * - Mọi kết quả ở mục 4 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JMOD (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. NĂM LỆNH CON
 * - {@code jmod create}: tạo file {@code .jmod} mới.
 * - {@code jmod extract}: giải nén toàn bộ nội dung.
 * - {@code jmod list}: liệt kê tên các entry.
 * - {@code jmod describe}: in module descriptor (tên, exports, requires, main-class).
 * - {@code jmod hash}: tính và ghi lại mã băm của các module phụ thuộc, để ràng buộc một
 * module chỉ chạy với đúng phiên bản module khác.
 * <p>
 * 2. CÁC CỜ THEO LOẠI NỘI DUNG
 * - {@code --class-path}: class và resource (phần duy nhất giống jar).
 * - {@code --cmds}: lệnh thực thi.
 * - {@code --libs}: thư viện native ({@code .dll}, {@code .so}).
 * - {@code --config}: file cấu hình.
 * - {@code --header-files}: header C cho JNI.
 * - {@code --man-pages}, {@code --legal-notices}: tài liệu và giấy phép.
 * - {@code --main-class}, {@code --module-version}, {@code --target-platform}.
 * - {@code --dry-run} để thử {@code jmod hash} mà không ghi gì.
 * - Sáu loại nội dung đầu chính là lý do JMOD tồn tại: JAR chỉ có một khoang, JMOD có nhiều
 * khoang và {@code jlink} biết đặt mỗi khoang vào đúng thư mục của runtime image
 * ({@code bin}, {@code lib}, {@code conf}, {@code include}).
 * <p>
 * 3. HAI GIỚI HẠN PHẢI NHỚ
 * - JMOD KHÔNG dùng được lúc chạy. Không đặt lên module path của {@code java} được, cũng
 * không có chuyện {@code java -jar x.jmod}.
 * - JMOD không nén theo kiểu jar executable; nó là định dạng riêng của JDK, không phải
 * chuẩn ZIP như jar.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 4. BẰNG CHỨNG CHO GIỚI HẠN "KHÔNG DÙNG LÚC CHẠY"
 * - Dựng module {@code m} đơn giản, đóng thành {@code mods/m.jmod} bằng
 * {@code jmod create --class-path classes/m --main-class app.App mods/m.jmod}.
 * - Chạy {@code java -p mods -m m}:
 * {@code Error occurred during initialization of boot layer}
 * {@code java.lang.module.FindException: Module m not found}
 * Launcher KHÔNG hề nhìn thấy file {@code .jmod} trên module path - không phải lỗi cú
 * pháp, nó coi như module đó không tồn tại.
 * - Cùng bộ class đó đóng thành {@code m.jar} rồi {@code java -p mods2 -m m} -&gt; chạy
 * bình thường.
 * - Nhưng {@code jlink --module-path "mods;<JDK>/jmods" --add-modules m --output img} thì
 * NHẬN, và {@code img/bin/java -m m} chạy được.
 * - Kết luận gọn: JAR dùng được cả lúc chạy lẫn lúc link; JMOD chỉ dùng được LÚC LINK.
 * <p>
 * 5. {@code jmod describe} - CÁCH ĐỌC NHANH MỘT MODULE
 * - Trên module tự tạo:
 * {@code m}, {@code exports app}, {@code requires java.base mandated},
 * {@code main-class app.App}
 * - Chữ {@code mandated} đáng chú ý: {@code requires java.base} không hề được viết trong
 * {@code module-info.java}, compiler TỰ THÊM vào mọi module. Đây là bằng chứng cho quy
 * tắc đã học ở section Modules.
 * - Trên module của JDK: {@code jmod describe <JDK>/jmods/java.base.jmod} ra
 * {@code java.base@25.0.4.1} rồi hàng loạt {@code exports java.io}, {@code exports
 * java.lang}, {@code exports java.lang.classfile}...
 * <p>
 * 6. KÍCH THƯỚC THẬT - VÌ SAO {@code jlink} CẮT ĐƯỢC NHIỀU
 * - {@code jmod list} trên {@code java.base.jmod} của JDK 25 đếm được 7449 entry. Chỉ
 * riêng module nền đã bằng đó, nên runtime image cắt gọn mới có ý nghĩa.
 * - {@code jmod list} trên module tự tạo chỉ có 2 dòng:
 * {@code classes/module-info.class} và {@code classes/app/App.class}. Tiền tố
 * {@code classes/} chính là tên khoang ở mục 2 - thấy rõ cấu trúc nhiều khoang.
 * <p>
 * 7. THƯ MỤC {@code jmods} CÓ THỂ KHÔNG TỒN TẠI
 * - JDK 25 cài từ Oracle trên máy này CÓ thư mục {@code jmods}.
 * - Nhưng nhiều bản phân phối (và mọi runtime image do chính {@code jlink} tạo ra) KHÔNG
 * kèm {@code jmods} để tiết kiệm dung lượng.
 * - Hệ quả thực tế: image Docker dùng JRE hoặc JDK đã cắt sẽ không {@code jlink} tiếp
 * được, vì thiếu nguyên liệu. Lỗi sẽ là {@code Module java.base not found}. Đây là lý do
 * Dockerfile nhiều tầng thường {@code jlink} ở tầng build (có JDK đầy đủ) rồi mới chép
 * image sang tầng chạy.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - JMOD chứa được gì mà JAR không chứa được?
 * - Đặt {@code .jmod} lên module path của {@code java} thì báo lỗi gì?
 * - JAR và JMOD, cái nào dùng được lúc link, cái nào lúc chạy?
 * - {@code requires java.base mandated} - chữ mandated nghĩa là gì?
 * - Vì sao một runtime image do {@code jlink} tạo ra lại không {@code jlink} tiếp được?
 */
public class t10_JmodCreateJmodArchives {

    void main() throws IOException {
        Path work = Files.createTempDirectory("jmod-demo");
        try {
            Path src = work.resolve("src/m/app");
            Files.createDirectories(src);
            Files.writeString(work.resolve("src/m/module-info.java"), """
                    module m { exports app; }
                    """);
            Files.writeString(src.resolve("App.java"), """
                    package app;
                    public class App {
                        public static void main(String[] args) {
                            System.out.println("chạy từ module m");
                        }
                    }
                    """);

            Path classes = work.resolve("classes/m");
            ToolProvider.findFirst("javac").orElseThrow().run(System.out, System.err,
                    "-d", classes.toString(),
                    work.resolve("src/m/module-info.java").toString(),
                    src.resolve("App.java").toString());

            // jmod KHÔNG tự tạo thư mục cha, và cũng từ chối ghi đè file đã có.
            Path mods = work.resolve("mods");
            Files.createDirectories(mods);
            Path jmodFile = mods.resolve("m.jmod");

            ToolProvider jmod = ToolProvider.findFirst("jmod").orElseThrow();

            System.out.println("=== (1) jmod create ===");
            int rc = jmod.run(System.out, System.err, "create",
                    "--class-path", classes.toString(),
                    "--main-class", "app.App",
                    jmodFile.toString());
            System.out.println("mã thoát = " + rc);

            // 2. describe: thấy cả "requires java.base mandated" (mục 5).
            System.out.println("\n=== (2) jmod describe ===");
            jmod.run(System.out, System.err, "describe", jmodFile.toString());

            // 3. list: tiền tố classes/ cho thấy JMOD có nhiều khoang (mục 6).
            System.out.println("=== (3) jmod list ===");
            jmod.run(System.out, System.err, "list", jmodFile.toString());

            // 4. Bằng chứng JMOD không dùng được lúc chạy (mục 4).
            System.out.println("\n=== (4) java -p mods -m m  <- đặt .jmod lên module path ===");
            runJava(work, List.of("-p", mods.toString(), "-m", "m"));

            // 5. Cùng nội dung đóng thành jar thì chạy được - để thấy khác biệt nằm ở ĐỊNH
            //    DẠNG, không phải ở nội dung.
            Path jars = work.resolve("jars");
            Files.createDirectories(jars);
            ToolProvider.findFirst("jar").orElseThrow().run(System.out, System.err,
                    "--create", "--file", jars.resolve("m.jar").toString(),
                    "--main-class", "app.App", "-C", classes.toString(), ".");
            System.out.println("=== (5) java -p jars -m m  <- cùng class, đóng bằng jar ===");
            runJava(work, List.of("-p", jars.toString(), "-m", "m"));

            // 6. Và JDK tự đóng gói bằng chính định dạng này (mục 5, mục 7).
            Path jdkJmods = Path.of(System.getProperty("java.home"), "jmods");
            System.out.println("=== (6) thư mục jmods của JDK đang chạy ===");
            if (Files.isDirectory(jdkJmods)) {
                System.out.println("  có: " + jdkJmods);
                jmod.run(System.out, System.err, "describe",
                        jdkJmods.resolve("java.base.jmod").toString());
            } else {
                System.out.println("  KHÔNG có - runtime này đã bị cắt, không jlink tiếp được (mục 7)");
            }
        } finally {
            deleteRecursively(work);
        }
    }

    private static void runJava(Path dir, List<String> args) throws IOException {
        var command = new java.util.ArrayList<String>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.addAll(args);
        try {
            Process process = new ProcessBuilder(command)
                    .directory(dir.toFile())
                    .redirectErrorStream(true)
                    .start();
            new String(process.getInputStream().readAllBytes()).lines()
                    .limit(3)
                    .forEach(line -> System.out.println("  " + line));
            System.out.println("  mã thoát = " + process.waitFor());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
