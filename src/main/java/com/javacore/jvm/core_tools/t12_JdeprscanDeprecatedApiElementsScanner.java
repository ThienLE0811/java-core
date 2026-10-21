package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Jdeprscan - Deprecated API Elements Scanner
 * <p>
 * 0. Vị trí bài này
 * - Bài 12/12, bài cuối của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jdeprscan/">dev.java - Jdeprscan</a>
 * - Nguyên văn: công cụ phân tích TĨNH quét jar và class để tìm chỗ dùng API đã bị đánh
 * dấu lỗi thời.
 * - Giới hạn ghi ngay trong trang gốc và phải nhớ: nó CHỈ biết các API bị deprecate của
 * JAVA SE. API deprecate của Spring, Hibernate hay thư viện bất kỳ thì nó không biết.
 * - {@code jdeprscan} KHÔNG có {@code ToolProvider} (xem {@code t1_} mục 5) nên
 * {@code main()} dưới đây gọi nó bằng {@code ProcessBuilder}.
 * - Mọi kết quả ở mục 4 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JDEPRSCAN (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. CÁCH GỌI
 * - {@code jdeprscan [options] {thư mục|jar|tên class|file class} ...}
 * - Các lớp phụ thuộc phải có trên {@code --class-path}, nếu không kết quả sẽ thiếu.
 * <p>
 * 2. CÁC CỜ
 * - {@code --list} / {@code -l}: in TOÀN BỘ danh sách API deprecate của một bản Java (không
 * quét gì cả).
 * - {@code --for-removal}: chỉ hiện những thứ đã đánh dấu SẼ BỊ XOÁ.
 * - {@code --release <N>}: lấy danh sách deprecate của bản Java nào.
 * - {@code --class-path}, {@code --verbose} / {@code -v}, {@code --version},
 * {@code --full-version}, {@code --help}.
 * <p>
 * 3. HAI MỨC DEPRECATE - GỐC CỦA MỌI HIỂU NHẦM
 * - {@code @Deprecated(forRemoval = false)}: "không nên dùng nữa", nhưng có thể tồn tại mãi.
 * - {@code @Deprecated(forRemoval = true)}: "sẽ bị XOÁ", code sẽ gãy ở một bản sau.
 * - {@code javac} phân biệt hai mức này: mức thứ hai cho cảnh báo nặng hơn
 * ({@code removal}) và KHÔNG tắt được bằng {@code -Xlint:-deprecation}.
 * - Chỉ nhóm thứ hai mới là việc gấp. {@code --for-removal} tồn tại để lọc ra đúng nhóm đó.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 4. TRANG GỐC ĐÃ LỖI THỜI Ở ĐÚNG CHỖ DỄ THẤY NHẤT
 * - Trang gốc ghi cờ là {@code --release 15|16}.
 * - {@code jdeprscan --help} trên JDK 25 in ra:
 * {@code --release 8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25}
 * - Bài học chung cho cả section: với tool JDK, {@code --help} của bản đang chạy luôn
 * đúng hơn tài liệu trên web.
 * <p>
 * 5. KẾT QUẢ QUÉT THẬT
 * - Class dùng {@code new Integer(5)}, {@code new Boolean(true)} và
 * {@code Thread.getId()}, quét bằng {@code jdeprscan <thư mục>}:
 * {@code class Old uses deprecated method java/lang/Integer::<init>(I)V}
 * {@code class Old uses deprecated method java/lang/Boolean::<init>(Z)V}
 * {@code class Old uses deprecated method java/lang/Thread::getId()J}
 * - Chữ ký in ra ở dạng BYTECODE ({@code (I)V}, {@code ()J}) chứ không phải dạng Java -
 * thêm một lý do để đọc được ký hiệu kiểu nội bộ đã gặp ở {@code javap -s} ({@code t3_}).
 * - So sánh: khi biên dịch, {@code javac} chỉ nói đúng một dòng
 * {@code Note: Old.java uses or overrides a deprecated API} mà KHÔNG chỉ ra chỗ nào.
 * {@code jdeprscan} liệt kê đủ ba chỗ. Đây là giá trị thật của tool này.
 * <p>
 * 6. {@code --for-removal} RA RỖNG - VÀ ĐÓ LÀ ĐÚNG
 * - Chạy {@code jdeprscan --for-removal} trên đúng thư mục đó: không có dòng nào.
 * - Kiểm chứng trực tiếp bằng reflection thay vì đoán:
 * {@code Integer.class.getDeclaredConstructor(int.class).getAnnotation(Deprecated.class)}
 * trả về {@code @java.lang.Deprecated(forRemoval=false, since="9")}
 * {@code Thread.class.getMethod("getId")} trả về
 * {@code @java.lang.Deprecated(forRemoval=false, since="19")}
 * - Tức là {@code new Integer(5)} tuy bị deprecate từ Java 9 nhưng CHƯA bao giờ được đánh
 * dấu sẽ xoá - trái với điều nhiều người (và nhiều bài blog) khẳng định.
 * - Đây đúng là kiểu câu hỏi phải hỏi chính JDK, không hỏi trí nhớ.
 * <p>
 * 7. {@code --list} - 720 DÒNG
 * - {@code jdeprscan --list} trên JDK 25 in ra 720 dòng. Đó là toàn bộ API deprecate của
 * Java SE 25, không cần quét gì.
 * - Dùng khi muốn biết "bản này bỏ thêm những gì" trước một đợt nâng cấp JDK.
 * - Kết hợp {@code --release N --list} để so sánh hai bản với nhau.
 * <p>
 * 8. VỊ TRÍ TRONG QUY TRÌNH NÂNG CẤP JDK
 * - Bộ ba dùng chung một lúc khi nâng cấp:
 * + {@code jdeprscan}: API Java SE lỗi thời mà code mình đang dùng.
 * + {@code jdeps --jdk-internals}: API nội bộ JDK mà code mình đang chạm vào.
 * + {@code javap} đọc {@code major version}: thư viện bên thứ ba build bằng JDK nào.
 * - Cả ba đều phân tích TĨNH trên bytecode, nên đều chạy được trên jar của bên thứ ba mà
 * không cần mã nguồn - ưu thế lớn so với cảnh báo của {@code javac}, vốn chỉ thấy code
 * đang biên dịch.
 * - Và cả ba đều mù với reflection. Cái gì nạp lúc chạy thì không công cụ tĩnh nào thấy.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA - HẾT SECTION
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code jdeprscan} biết API deprecate của ai? Không biết của ai?
 * - Hai mức {@code @Deprecated} khác nhau thế nào, mức nào là việc gấp?
 * - {@code jdeprscan} hơn cảnh báo của {@code javac} ở điểm gì?
 * - {@code new Integer(5)} có bị đánh dấu sẽ xoá không? Kiểm chứng bằng cách nào?
 * - Ba tool nào đi cùng nhau khi nâng cấp JDK, và điểm mù chung của chúng là gì?
 * <p>
 * 10. XONG CẢ SECTION - NHÌN LẠI 12 BÀI
 * - Dựng: {@code javac} (t2) -&gt; {@code jar} (t8) -&gt; {@code javadoc} (t4).
 * - Chạy: {@code java} (t5), {@code jshell} (t6, t7).
 * - Soi: {@code javap} (t3), {@code jdeps} (t11), {@code jdeprscan} (t12).
 * - Đóng gói: {@code jmod} (t10) -&gt; {@code jlink} (t9).
 * - Bước tiếp theo trong lộ trình là 33/41 - Monitoring and Troubleshooting, nơi dùng
 * {@code jcmd}, {@code jstack}, {@code jmap}, {@code jfr}: chuyển từ soi file tĩnh sang
 * soi tiến trình đang chạy.
 */
public class t12_JdeprscanDeprecatedApiElementsScanner {

    void main() throws IOException, InterruptedException {
        Path jdeprscan = Path.of(System.getProperty("java.home"), "bin", "jdeprscan");
        Path work = Files.createTempDirectory("jdeprscan-demo");
        try {
            Path source = work.resolve("Old.java");
            Files.writeString(source, """
                    public class Old {
                        public static void main(String[] args) {
                            Integer i = new Integer(5);
                            Boolean b = new Boolean(true);
                            System.out.println(i + " " + b + " " + Thread.currentThread().getId());
                        }
                    }
                    """);

            // 1. javac chỉ nói MỘT dòng chung chung, không chỉ ra chỗ nào (mục 5).
            System.out.println("=== (1) javac thấy gì ===");
            Path classes = work.resolve("out");
            ToolProvider.findFirst("javac").orElseThrow()
                    .run(System.out, System.err, "-d", classes.toString(), source.toString());

            // 2. jdeprscan liệt kê đủ từng chỗ, kèm chữ ký bytecode (mục 5).
            System.out.println("\n=== (2) jdeprscan <thư mục> ===");
            run(work, List.of(jdeprscan.toString(), classes.toString()));

            // 3. --for-removal ra rỗng, và mục 6 giải thích vì sao đó là đúng.
            System.out.println("\n=== (3) jdeprscan --for-removal ===");
            run(work, List.of(jdeprscan.toString(), "--for-removal", classes.toString()));

            // 4. Hỏi thẳng JDK thay vì tin trí nhớ (mục 6).
            System.out.println("\n=== (4) hỏi chính JDK về cờ forRemoval ===");
            System.out.println("  Integer(int)   : "
                    + Integer.class.getDeclaredConstructor(int.class).getAnnotation(Deprecated.class));
            System.out.println("  Thread.getId() : "
                    + Thread.class.getMethod("getId").getAnnotation(Deprecated.class));

            // 5. --help để lấy danh sách --release thật của bản đang chạy (mục 4).
            System.out.println("\n=== (5) jdeprscan --help, dòng --release ===");
            run(work, List.of(jdeprscan.toString(), "--help"), line -> line.contains("--release"));

            // 6. Đếm số API deprecate của bản này (mục 7).
            System.out.println("\n=== (6) jdeprscan --list đếm được bao nhiêu dòng ===");
            long lines = capture(work, List.of(jdeprscan.toString(), "--list"))
                    .lines().filter(line -> !line.isBlank()).count();
            System.out.println("  " + lines + " dòng");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("API đã đổi trên bản JDK này", e);
        } finally {
            deleteRecursively(work);
        }
    }

    private static void run(Path dir, List<String> command) throws IOException, InterruptedException {
        run(dir, command, line -> true);
    }

    private static void run(Path dir, List<String> command, java.util.function.Predicate<String> keep)
            throws IOException, InterruptedException {
        capture(dir, command).lines().filter(keep).forEach(line -> System.out.println("  " + line));
    }

    private static String capture(Path dir, List<String> command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(new ArrayList<>(command))
                .directory(dir.toFile())
                .redirectErrorStream(true)
                .start();
        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor();
        return output;
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
