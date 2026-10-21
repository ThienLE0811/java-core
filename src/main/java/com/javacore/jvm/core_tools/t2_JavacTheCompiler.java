package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

/**
 * Javac - the Compiler
 * <p>
 * 0. Vị trí bài này
 * - Bài 2/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/javac/">dev.java - Javac - the Compiler</a>
 * - CẢNH BÁO VỀ TRANG GỐC: trang này gần như chép nguyên man page, hơn 30 option liệt kê
 * thành bảng. Chép lại bảng đó vào note là vô ích vì {@code javac --help} tra nhanh hơn.
 * Bài này vì thế gọn phần trang gốc, dày phần đo thật.
 * - 11 mục của trang gốc: Introducing javac, Description, Programmatic Interface, Implicitly
 * Loaded Source Files, Using JDK_JAVAC_OPTIONS, Overview of javac Options, Standard Options,
 * Cross-Compilation Options, Extra Options, Command-Line Argument Files, Examples,
 * Annotation Processing, Searching for Types.
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Mọi con số và thông báo lỗi ở
 * mục 7 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JAVAC (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. JAVAC LÀ GÌ
 * - Nguyên văn: {@code javac} "read[s] Java class and interface definitions and compile[s]
 * them into bytecode and class files."
 * - Vào {@code .java}, ra {@code .class}. Không link, không đóng gói - đó là việc của
 * {@code jar} ({@code t8_}).
 * - Ngoài biên dịch, {@code javac} còn chạy annotation processing (mục 5), thay cho lệnh
 * {@code apt} đã bỏ từ lâu.
 * <p>
 * 2. BA NHÓM OPTION
 * - Standard Options: tồn tại ở mọi bản JDK, ví dụ {@code -d}, {@code -cp},
 * {@code -encoding}, {@code -g}, {@code -verbose}, {@code -Werror}.
 * - Cross-Compilation Options: {@code -bootclasspath}, {@code -extdirs} - xem mục 7, phần
 * này của trang gốc đã lỗi thời.
 * - Extra Options bắt đầu bằng {@code -X}: {@code -Xlint}, {@code -Xdiags}. Không bảo đảm
 * tồn tại ở bản sau.
 * - Quy ước chung của mọi tool JDK: một gạch là option cũ, hai gạch là option thời module
 * (JDK 9+), ví dụ {@code --release}, {@code --module-path}.
 * <p>
 * 3. IMPLICITLY LOADED SOURCE FILES - MỤC QUAN TRỌNG NHẤT TRANG GỐC
 * - {@code javac} có thể tự nạp thêm file {@code .java} mà bạn KHÔNG truyền trên dòng lệnh,
 * khi code đang biên dịch tham chiếu tới chúng và tìm thấy chúng trên {@code -sourcepath}.
 * - Trang gốc nói các file nạp ngầm này "aren't subject to annotation processing", và gợi ý
 * {@code -implicit} để tắt cảnh báo.
 * - {@code -implicit:class} (mặc định) sinh luôn {@code .class}; {@code -implicit:none}
 * thì không sinh.
 * - Vì sao phải nhớ: đây là lý do build Maven đôi khi ra nhiều {@code .class} hơn số file
 * bạn nghĩ, và là lý do xoá một {@code .class} rồi build lại nó vẫn xuất hiện. Đo thật ở
 * mục 8.
 * <p>
 * 4. SEARCHING FOR TYPES VÀ {@code -sourcepath}
 * - {@code javac} tìm kiểu theo thứ tự: module hệ thống -&gt; class path người dùng -&gt;
 * source path.
 * - {@code -classpath} tìm {@code .class}; {@code -sourcepath} tìm {@code .java}. Nếu không
 * đặt {@code -sourcepath}, nó mặc định dùng luôn giá trị của {@code -classpath}.
 * - Khi cả hai cùng có, {@code -Xprefer:newer} (mặc định) chọn cái mới hơn;
 * {@code -Xprefer:source} ép đọc source.
 * <p>
 * 5. ANNOTATION PROCESSING
 * - {@code javac} tự tìm processor qua file {@code META-INF/services/} của
 * {@code javax.annotation.processing.Processor} trên classpath, hoặc nhận chỉ định bằng
 * {@code -processor}.
 * - Nếu processor sinh thêm source, {@code javac} chạy NHIỀU VÒNG cho tới khi không còn
 * file mới.
 * - {@code -proc:none} tắt hẳn, {@code -proc:only} chỉ xử lý annotation mà không biên dịch.
 * - Thực tế backend: đây chính là cơ chế của Lombok, MapStruct, và
 * {@code hibernate-jpamodelgen}. Khi IDE báo "không thấy class sinh ra", 90% là
 * {@code -proc} bị tắt hoặc processor không nằm trên classpath.
 * <p>
 * 6. PROGRAMMATIC INTERFACE VÀ {@code JDK_JAVAC_OPTIONS}
 * - Trang gốc chỉ nói một câu: {@code javac} hỗ trợ Java Compiler API trong package
 * {@code javax.tools}. Thực chất đó là cả một API bắt được từng lỗi dưới dạng object -
 * xem mục 10.
 * - {@code JDK_JAVAC_OPTIONS} (có từ JDK 9) được NỐI VÀO TRƯỚC dòng lệnh {@code javac}.
 * Nháy đơn/kép bao được tham số có khoảng trắng; {@code @file} dùng được; ký tự đại diện
 * thì KHÔNG.
 * - Dùng khi cần ép một cờ cho mọi lần build trong CI mà không sửa được lệnh build.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 7. TRANG GỐC CÒN DẠY {@code -source/-target}, JAVAC 25 THÌ KHÔNG
 * - Đây là điểm đáng giá nhất của cả bài, vì trang gốc viết từ thời trước
 * {@code --release}.
 * - Chạy {@code javac -source 17 -target 17 Hi.java}, chính compiler mắng (nguyên văn):
 * {@code warning: [options] location of system modules is not set in conjunction with
 * -source 17}
 * {@code not setting the location of system modules may lead to class files that cannot run
 * on JDK 17}
 * {@code --release 17 is recommended instead of -source 17 -target 17 because it sets the
 * location of system modules automatically}
 * - LÝ DO PHẢI NHỚ: {@code -source/-target} chỉ hạ CÚ PHÁP và PHIÊN BẢN BYTECODE, nhưng vẫn
 * link vào API của JDK 25. Biên dịch trót lọt, rồi CHẾT LÚC CHẠY trên JDK 17 với
 * {@code NoSuchMethodError} / {@code NoSuchFieldError}.
 * - {@code --release N} khoá luôn cả tập API của bản N, nên sai sẽ lộ ngay lúc biên dịch.
 * - Trong {@code pom.xml} của repo này đang là {@code maven.compiler.source/target} = 25.
 * Dạng hiện đại tương đương là {@code <maven.compiler.release>25</maven.compiler.release>}.
 * Chỉ cần nhớ khi nào build cho JDK cũ hơn JDK đang chạy thì BẮT BUỘC phải là
 * {@code release}.
 * - {@code --release 8} vẫn chạy nhưng đã kêu: {@code warning: [options] source value 8 is
 * obsolete and will be removed in a future release}.
 * <p>
 * 8. IMPLICIT LOADING - ĐO THẬT (kiểm chứng mục 3)
 * - Hai file: {@code Hi.java} (có dùng {@code Helper}) và {@code Helper.java}. Chỉ truyền
 * {@code Hi.java} cho {@code javac}.
 * - Không đặt {@code -sourcepath}: {@code error: cannot find symbol: class Helper}.
 * - Thêm {@code -sourcepath src -verbose}, log hiện đúng hành vi nạp ngầm:
 * {@code [loading src\greet\Helper.java]} rồi {@code [wrote out\greet\Helper.class]} -
 * một file chưa bao giờ được truyền vào.
 * - Thêm tiếp {@code -implicit:none}: thư mục ra CHỈ còn {@code Hi.class}.
 * <p>
 * 9. {@code -g} ĐỔI ĐƯỢC BAO NHIÊU
 * - Cùng một {@code Demo.java}: {@code -g} ra 726 byte, {@code -g:none} ra 505 byte, chênh
 * khoảng 30%.
 * - Nhưng mặc định (không cờ) KHÔNG phải là {@code -g:none}. Đo bằng {@code javap -c -l}:
 * mặc định có {@code LineNumberTable} (3 chỗ) nhưng KHÔNG có {@code LocalVariableTable}
 * (0 chỗ); thêm {@code -g} thì {@code LocalVariableTable} xuất hiện (3 chỗ).
 * - Hệ quả production: số dòng trong stack trace đến từ {@code LineNumberTable} nên mặc
 * định vẫn có. Nhưng debugger hiện tên biến cục bộ thì cần {@code -g}. Đừng bao giờ bật
 * {@code -g:none} cho artifact chạy thật - mất số dòng là mất khả năng đọc log sự cố.
 * - Chi tiết cách soi nằm ở bài sau, {@code t3_}.
 * <p>
 * 10. HAI CÁCH GỌI JAVAC TỪ JAVA - KHÁC NHAU Ở ĐÂU
 * - {@code java.util.spi.ToolProvider.findFirst("javac")} -&gt; {@code JavacToolProvider}
 * (module {@code java.base}): y hệt dòng lệnh, trả về {@code int}, lỗi in ra stream.
 * - {@code javax.tools.ToolProvider.getSystemJavaCompiler()} -&gt; {@code JavacTool}
 * (module {@code java.compiler}): trả về {@code JavaCompiler}, cắm được
 * {@code DiagnosticCollector} để nhận từng lỗi dưới dạng OBJECT, có
 * {@code getLineNumber()}, {@code getColumnNumber()}, {@code getCode()}.
 * - Cùng tên {@code ToolProvider}, khác package, khác module. Nhầm là chuyện thường.
 * - Khi nào dùng cái nào: cần biết mã thoát thì dùng cái đầu; cần PHÂN TÍCH lỗi (lint tự
 * viết, công cụ sinh code, test kiểm tra "code này phải không biên dịch được") thì dùng
 * cái sau.
 * <p>
 * 11. BA THÔNG BÁO HAY GẶP NHẤT TRONG DỰ ÁN THẬT
 * - {@code warning: [options] source value 8 is obsolete...}: đổi sang {@code --release}.
 * - {@code error: unmappable character for encoding windows-1252}: thiếu
 * {@code -encoding UTF-8}. Trên Windows đây là lỗi kinh điển vì mã hoá mặc định của hệ
 * điều hành không phải UTF-8. {@code pom.xml} của repo đã đặt
 * {@code project.build.sourceEncoding} = UTF-8 nên tránh được.
 * - {@code Note: ... uses unchecked or unsafe operations}: chỉ là NOTE, không phải warning
 * đầy đủ. Muốn thấy chi tiết phải thêm {@code -Xlint:unchecked}. Bật
 * {@code -Xlint:all -Werror} trong CI là cách rẻ nhất để chặn nợ kỹ thuật.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao {@code --release 17} an toàn hơn {@code -source 17 -target 17}? Hỏng kiểu gì?
 * - {@code -classpath} và {@code -sourcepath} khác nhau ra sao? Không đặt cái sau thì sao?
 * - Implicit loading là gì, và cờ nào tắt nó?
 * - Mặc định {@code javac} giữ lại bảng gỡ lỗi nào, bỏ bảng nào?
 * - Hai class {@code ToolProvider} - cái nào cho {@code DiagnosticCollector}?
 * - {@code JDK_JAVAC_OPTIONS} nối vào trước hay sau tham số dòng lệnh?
 */
public class t2_JavacTheCompiler {

    void main() throws IOException {
        Path work = Files.createTempDirectory("javac-demo");
        try {
            Path src = work.resolve("src/greet");
            Files.createDirectories(src);
            // Hi dùng Helper, nhưng ta sẽ chỉ truyền Hi.java cho javac.
            Files.writeString(src.resolve("Hi.java"), """
                    package greet;
                    public class Hi {
                        public static void main(String[] args) {
                            System.out.println(new Helper().msg());
                        }
                    }
                    """);
            Files.writeString(src.resolve("Helper.java"), """
                    package greet;
                    class Helper {
                        String msg() { return "hi"; }
                    }
                    """);

            ToolProvider javac = ToolProvider.findFirst("javac").orElseThrow(
                    () -> new IllegalStateException("không thấy javac - đang chạy trên JRE?"));

            // 1. KHÔNG -sourcepath: javac không tìm ra Helper (mục 8).
            System.out.println("=== (1) javac Hi.java, KHÔNG -sourcepath ===");
            Path out1 = work.resolve("out1");
            int rc1 = javac.run(System.out, System.err,
                    "-d", out1.toString(), src.resolve("Hi.java").toString());
            System.out.println("mã thoát = " + rc1 + "   (khác 0 = biên dịch hỏng)");

            // 2. CÓ -sourcepath: javac tự nạp Helper.java và sinh luôn Helper.class.
            System.out.println("\n=== (2) thêm -sourcepath -> nạp ngầm Helper ===");
            Path out2 = work.resolve("out2");
            javac.run(System.out, System.err,
                    "-sourcepath", work.resolve("src").toString(),
                    "-d", out2.toString(), src.resolve("Hi.java").toString());
            System.out.println("file sinh ra: " + listClassFiles(out2));

            // 3. -implicit:none: chặn việc sinh .class cho file nạp ngầm.
            System.out.println("\n=== (3) thêm -implicit:none ===");
            Path out3 = work.resolve("out3");
            javac.run(System.out, System.err,
                    "-sourcepath", work.resolve("src").toString(),
                    "-implicit:none",
                    "-d", out3.toString(), src.resolve("Hi.java").toString());
            System.out.println("file sinh ra: " + listClassFiles(out3));

            // 4. -source/-target kiểu cũ: in nguyên văn lời khuyên của chính compiler (mục 7).
            System.out.println("\n=== (4) -source 17 -target 17 -> javac tự khuyên dùng --release ===");
            StringWriter captured = new StringWriter();
            try (PrintWriter writer = new PrintWriter(captured)) {
                javac.run(writer, writer,
                        "-source", "17", "-target", "17",
                        "-sourcepath", work.resolve("src").toString(),
                        "-d", work.resolve("out4").toString(),
                        src.resolve("Hi.java").toString());
            }
            System.out.println(captured.toString().strip());

            // 5. Compiler API: cùng một lỗi, nhưng nhận về OBJECT thay vì chữ (mục 10).
            System.out.println("\n=== (5) javax.tools: bắt lỗi dưới dạng object ===");
            Path bad = work.resolve("Bad.java");
            Files.writeString(bad, """
                    public class Bad {
                        int x = "chuỗi gán vào int";
                    }
                    """);
            JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, null, null)) {
                Iterable<? extends JavaFileObject> units =
                        files.getJavaFileObjectsFromPaths(List.of(bad));
                compiler.getTask(null, files, diagnostics,
                        List.of("-d", work.resolve("out5").toString()), null, units).call();
            }
            diagnostics.getDiagnostics().forEach(d -> System.out.println(
                    "  dòng " + d.getLineNumber() + ", cột " + d.getColumnNumber()
                            + ", mã lỗi " + d.getCode()));
        } finally {
            deleteRecursively(work);
        }
    }

    private static List<String> listClassFiles(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(p -> p.toString().endsWith(".class"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
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
