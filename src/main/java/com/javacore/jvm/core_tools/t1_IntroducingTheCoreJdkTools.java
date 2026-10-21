package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Introducing the Core JDK Tools
 * <p>
 * 0. Vị trí bài này
 * - Bước 32/41 trong lộ trình dev.java/learn, bài 1/12 của section The Core JDK Tools.
 * - Trang gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/intro/">dev.java - Introducing the Core JDK Tools</a>
 * - Trang gốc RẤT ngắn: một đoạn giới thiệu, một disclaimer, rồi danh sách 11 tool còn lại.
 * Nguyên văn: các tool này "enable developers to handle development tasks such as compiling
 * and running a program, packaging source files into a Java Archive (JAR) file, applying
 * security policies to a JAR file, and more."
 * - Disclaimer của trang gốc cũng đáng nhớ: đây CHỈ là giới thiệu, không thay thế tài liệu
 * chính thức của OpenJDK. Nên mọi bảng option trong section này không cần học thuộc - học
 * cách tra là đủ.
 * - Khác biệt lớn so với các section trước: ở đây đối tượng học KHÔNG phải API Java mà là
 * DÒNG LỆNH. Một bài có thể không có code Java nào để chạy.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRO (mục 1 đến 2)
 * ======================================================================
 * <p>
 * 1. SECTION NÀY GỒM NHỮNG GÌ
 * - 12 trang: intro (bài này) + 11 tool. Repo đặt tương ứng {@code t1_} đến {@code t12_}.
 * - Thứ tự trang gốc: javac, javap, javadoc, java, jshell, jshell nâng cao, jar, jlink,
 * jmod, jdeps, jdeprscan.
 * - Thứ tự này KHÔNG phải thứ tự dùng trong thực tế. Thứ tự thực tế của một vòng đời build:
 * {@code javac} -&gt; {@code jar} -&gt; {@code java}, rồi mới tới {@code jdeps} /
 * {@code jlink} / {@code jpackage} khi cần đóng gói, và {@code javap} / {@code jdeprscan}
 * khi cần soi.
 * <p>
 * 2. VÌ SAO BACKEND VẪN PHẢI BIẾT, DÙ ĐÃ CÓ MAVEN
 * - Maven/Gradle chỉ là lớp vỏ gọi lại đúng những tool này. {@code maven-compiler-plugin}
 * gọi {@code javac}; {@code maven-jar-plugin} gọi {@code jar}; {@code maven-javadoc-plugin}
 * gọi {@code javadoc}.
 * - Khi build đỏ với thông báo lạ, cái báo lỗi là tool chứ không phải Maven. Đọc được thông
 * báo của {@code javac} thì mới sửa được cấu hình plugin.
 * - Đối chiếu cho người đến từ Node.js: {@code javac}/{@code jar} ở vai trò của
 * {@code tsc} + {@code npm pack}, còn Maven ở vai trò của {@code npm scripts} - gọi hộ,
 * không làm thay.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 3. JDK 25 CÓ 37 TOOL, KHÔNG PHẢI 11
 * - Đếm thật trong thư mục {@code bin} của JDK: 37 file {@code .exe}.
 * - Trang gốc chỉ dạy 11 cái. Phần lớn số còn lại thuộc nhóm vận hành, sẽ gặp ở section
 * Monitoring and Troubleshooting (bước 33) - package {@code jvm.monitoring_troubleshooting}.
 * - Gom nhóm cho dễ nhớ:
 * + Dựng: {@code javac}, {@code jar}, {@code javadoc}
 * + Chạy: {@code java}, {@code jshell}, {@code jrunscript}, {@code jwebserver}
 * + Soi tĩnh: {@code javap}, {@code jdeps}, {@code jdeprscan}, {@code jnativescan},
 * {@code jimage}, {@code serialver}
 * + Đóng gói: {@code jlink}, {@code jmod}, {@code jpackage}
 * + Vận hành lúc chạy: {@code jcmd}, {@code jps}, {@code jstack}, {@code jmap},
 * {@code jstat}, {@code jstatd}, {@code jinfo}, {@code jfr}, {@code jconsole},
 * {@code jhsdb}, {@code jdb}
 * + Bảo mật: {@code keytool}, {@code jarsigner}, {@code kinit}, {@code klist}, {@code ktab}
 * - Nhớ 5 nhóm này đáng giá hơn nhớ 37 cái tên.
 * <p>
 * 4. BẪY THỰC TẾ: PATH CHỈ CÓ 3 TOOL
 * - Trên máy này PATH đi qua thư mục shim của Oracle
 * ({@code Common Files\Oracle\Java\javapath}), và shim đó CHỈ có {@code java},
 * {@code javac}, {@code javaw}, {@code jshell}.
 * - Hệ quả đo thật trong Git Bash: gõ {@code javap} hay {@code jar} trần ra
 * {@code command not found}, dù JDK 25 cài đầy đủ.
 * - Ba cách thoát, theo thứ tự nên dùng:
 * + Gọi bằng đường dẫn đầy đủ tới {@code JAVA_HOME/bin/javap}.
 * + Thêm {@code JAVA_HOME/bin} vào PATH, và đặt nó TRƯỚC javapath.
 * + Gọi in-process bằng {@code ToolProvider} - xem mục 5, và là cách các file {@code t2_}
 * đến {@code t12_} dùng để {@code main()} chạy được ở mọi máy.
 * <p>
 * 5. {@code ToolProvider}: 9 TOOL GỌI ĐƯỢC TỪ JAVA, 3 TOOL THÌ KHÔNG
 * - {@code java.util.spi.ToolProvider} là SPI cho phép gọi tool ngay trong tiến trình,
 * không cần {@code ProcessBuilder}, không cần PATH.
 * - Liệt kê thật trên JDK 25 (chính {@code main()} bên dưới in lại):
 * {@code jar}, {@code javac}, {@code javadoc}, {@code javap}, {@code jdeps}, {@code jlink},
 * {@code jmod}, {@code jnativescan}, {@code jpackage}.
 * - KHÔNG có trong danh sách: {@code java} (launcher), {@code jshell}, và
 * {@code jdeprscan}. Ba bài đó phải dùng {@code ProcessBuilder} hoặc gõ tay.
 * - Đừng suy ra "mọi tool tên j* đều có provider". {@code jdeprscan} là phản ví dụ.
 * <p>
 * 6. HAI CLASS TÊN {@code ToolProvider} - BẪY TÊN
 * - {@code java.util.spi.ToolProvider} (module {@code java.base}): cổng DÒNG LỆNH, trả về
 * {@code int} như shell. Dùng ở tất cả các bài trong section này.
 * - {@code javax.tools.ToolProvider} (module {@code java.compiler}): cổng COMPILER API, trả
 * về {@code JavaCompiler} kèm {@code DiagnosticListener}, {@code JavaFileManager}.
 * - Hai thứ khác hẳn nhau, chi tiết ở {@code t2_} mục 6. Import nhầm thì không compile.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 7. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kể 5 nhóm tool và mỗi nhóm 2 cái tên.
 * - Maven gọi tool nào khi chạy {@code mvn package}?
 * - Vì sao gõ {@code jar} trong terminal lại {@code command not found} dù máy có JDK?
 * - Ba tool nào KHÔNG gọi được qua {@code ToolProvider}?
 * - Hai class {@code ToolProvider} khác nhau ở đâu?
 */
public class t1_IntroducingTheCoreJdkTools {

    void main() throws IOException {
        // 1. JDK đang chạy nằm ở đâu (mục 4). Luôn in cái này trước khi đổ lỗi cho tool.
        Path javaHome = Path.of(System.getProperty("java.home"));
        System.out.println("java.home    = " + javaHome);
        System.out.println("java.version = " + System.getProperty("java.version"));

        // 2. Đếm thật số tool trong bin (mục 3). Lọc .exe cho Windows và file không có phần
        //    mở rộng cho Linux/macOS, để file này chạy được ở cả hai nơi.
        Path bin = javaHome.resolve("bin");
        if (Files.isDirectory(bin)) {
            try (Stream<Path> entries = Files.list(bin)) {
                List<String> tools = entries
                        .filter(Files::isRegularFile)   // bin còn có thư mục con (server), loại ra
                        .map(p -> p.getFileName().toString())
                        .filter(name -> name.endsWith(".exe") || !name.contains("."))
                        .sorted()
                        .toList();
                System.out.println("\nSố tool trong " + bin + ": " + tools.size());
                System.out.println(String.join(" ", tools));
            }
        }

        // 3. Tool nào gọi được ngay trong tiến trình này (mục 5). Danh sách này quyết định
        //    main() của 11 bài sau viết được theo kiểu nào.
        System.out.println("\nToolProvider có sẵn (gọi in-process được):");
        ServiceLoader.load(ToolProvider.class).stream()
                .map(provider -> provider.get().name())
                .sorted()
                .forEach(name -> System.out.println("  - " + name));

        // 4. Ba cái tên dưới đây KHÔNG xuất hiện ở trên - kiểm chứng mục 5 thay vì tin lời.
        System.out.println();
        for (String name : List.of("java", "jshell", "jdeprscan")) {
            System.out.println("findFirst(" + name + ") -> "
                    + ToolProvider.findFirst(name).map(ToolProvider::name).orElse("KHÔNG CÓ"));
        }
    }
}
