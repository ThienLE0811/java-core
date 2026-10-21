package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Jdeps - Analyze your Java Classes Dependencies
 * <p>
 * 0. Vị trí bài này
 * - Bài 11/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jdeps/">dev.java - Jdeps</a>
 * - Trang gốc là man page với hơn 40 cờ chia làm 4 bảng. Ở đây chỉ giữ 6 cờ thực sự dùng.
 * - Một câu cho cả bài: {@code jdeps} trả lời "code này THỰC SỰ phụ thuộc vào cái gì" bằng
 * cách đọc bytecode - không phải đọc {@code pom.xml}, không phải đọc {@code import}.
 * - Khác biệt quan trọng với Maven: {@code mvn dependency:tree} cho biết bạn KHAI BÁO
 * những gì; {@code jdeps} cho biết bạn DÙNG những gì. Hai con số này gần như không bao
 * giờ bằng nhau.
 * - Mọi kết quả ở mục 5 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JDEPS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. JDEPS LÀ GÌ
 * - Nguyên văn: hiển thị "package-level or class-level dependencies of Java class files".
 * - Nhận vào: file {@code .class}, thư mục, hoặc {@code .jar}.
 * - Xuất ra văn bản, hoặc ngôn ngữ DOT ({@code --dot-output}) để vẽ thành đồ thị.
 * <p>
 * 2. SÁU CỜ ĐÁNG NHỚ
 * - {@code -s} / {@code -summary}: chỉ in mức archive -&gt; module. Dùng 90% số lần.
 * - {@code -v} / {@code -verbose}: chi tiết tới từng class -&gt; class.
 * - {@code --print-module-deps}: in ra danh sách module ngăn cách bằng dấu phẩy, định dạng
 * ĐÚNG để đưa thẳng cho {@code jlink --add-modules}.
 * - {@code --jdk-internals}: tìm chỗ dùng API nội bộ của JDK ({@code sun.misc.Unsafe},
 * {@code sun.security.*}...). Đây là cờ quan trọng nhất khi nâng cấp từ JDK 8.
 * - {@code --generate-module-info <thư mục>}: sinh sẵn file {@code module-info.java} cho
 * một jar chưa có.
 * - {@code --check <module>}: kiểm tra module descriptor có thừa {@code requires} không.
 * - Các cờ lọc: {@code -p} (theo package), {@code -e} (theo biểu thức chính quy),
 * {@code --require}, {@code -filter:none}.
 * <p>
 * 3. BA NHÓM CỜ CÒN LẠI
 * - Phân tích module: {@code --module-path}, {@code --add-modules}, {@code --multi-release},
 * {@code --list-deps}, {@code --list-reduced-deps}, {@code --generate-open-module}.
 * - Lọc phụ thuộc: {@code -filter:package} (mặc định - bỏ qua phụ thuộc trong cùng
 * package), {@code -filter:archive}, {@code -filter:module}.
 * - Lọc class được phân tích: {@code -include}, {@code -R} (đệ quy), {@code -I} (đảo
 * ngược), {@code --compile-time}.
 * - {@code --multi-release N} là BẮT BUỘC khi phân tích multi-release jar, nếu không
 * {@code jdeps} báo lỗi và dừng.
 * <p>
 * 4. VÒNG ĐỜI PHỐI HỢP VỚI {@code jlink}
 * - Bước 1: {@code jdeps --print-module-deps --ignore-missing-deps app.jar} -&gt; ra danh
 * sách module.
 * - Bước 2: {@code jlink --add-modules <danh sách đó> --output runtime}.
 * - Đây là lý do {@code jdeps} đứng ngay trước {@code jlink} trong dây chuyền ở
 * {@code t9_} mục 1.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 5. {@code -s} VÀ {@code --print-module-deps} KHÁC NHAU Ở ĐỊNH DẠNG
 * - {@code jdeps -s demo.jar} -&gt; {@code demo.jar -> java.base}
 * - {@code jdeps --print-module-deps demo.jar} -&gt; {@code java.base}
 * - Khác biệt nhỏ mà quan trọng: cái sau in ĐÚNG thứ {@code jlink} cần nhận, không có tên
 * archive, không có mũi tên. Đó là lý do tồn tại của cờ này - để nối ống lệnh.
 * <p>
 * 6. {@code -v} PHƠI RA THỨ KHÔNG CÓ TRONG MÃ NGUỒN
 * - Class {@code Demo} chỉ in một chuỗi, nhưng {@code jdeps -v} liệt kê cả:
 * {@code Demo -> java.lang.invoke.CallSite   java.base}
 * - Trong mã nguồn không hề có {@code CallSite}. Nó đến từ {@code invokedynamic} mà
 * compiler sinh ra cho phép nối chuỗi - đúng thứ đã soi bằng {@code javap -c} ở
 * {@code t3_} mục 8.
 * - BÀI HỌC: phụ thuộc ở mức bytecode LUÔN nhiều hơn phụ thuộc nhìn thấy trong source.
 * Phân tích {@code import} bằng mắt là không đủ.
 * <p>
 * 7. {@code --generate-module-info} THẤT BẠI VỚI PACKAGE MẶC ĐỊNH
 * - Chạy trên jar có class ở package mặc định:
 * {@code Error: demo.jar contains an unnamed package that is not allowed in a module}
 * - Cùng một luật với thông báo của {@code jar --describe-module} ở {@code t8_} mục 8.
 * Nhớ một lần cho cả hai tool: HỆ THỐNG MODULE KHÔNG CHẤP NHẬN PACKAGE MẶC ĐỊNH.
 * - Nhưng mã thoát thì KHÁC NHAU: {@code jdeps} trả về 1, còn {@code jar} trả về 0 dù in
 * ra lỗi. Cùng một lỗi, hai cách báo - đo thật cả hai, đừng suy từ cái này ra cái kia.
 * <p>
 * 8. KHI NÀO BACKEND THỰC SỰ MỞ {@code jdeps}
 * - Nâng cấp JDK 8 lên 17/21: {@code jdeps --jdk-internals *.jar} liệt kê trước mọi chỗ sẽ
 * gãy, thay vì chờ {@code NoClassDefFoundError} lúc chạy.
 * - Trước khi {@code jlink}: lấy danh sách module tối thiểu (mục 4).
 * - Rà dependency thừa: so {@code jdeps -s} với {@code mvn dependency:tree}. Thư viện có
 * trong cây Maven nhưng không xuất hiện trong {@code jdeps} là ứng viên để xoá - nhưng
 * CẨN THẬN: {@code jdeps} chỉ thấy phụ thuộc TĨNH. Thư viện nạp bằng reflection (JDBC
 * driver, Spring component quét lúc chạy) sẽ không hiện ra, xoá là chết lúc chạy.
 * - Giới hạn này cũng đúng với {@code jlink}: đó là lý do {@code --bind-services} tồn tại,
 * như đã ghi ở bài jlink.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code jdeps} đọc gì để biết phụ thuộc? Khác {@code mvn dependency:tree} chỗ nào?
 * - {@code -s} và {@code --print-module-deps} khác nhau ra sao, vì sao cần cả hai?
 * - Vì sao {@code -v} in ra class không có trong mã nguồn?
 * - Cờ nào dùng khi nâng cấp từ JDK 8?
 * - Giới hạn lớn nhất của {@code jdeps} là gì, và nó gây hậu quả gì nếu tin tuyệt đối?
 */
public class t11_JdepsAnalyzeYourJavaClassesDependencies {

    void main() throws IOException {
        Path work = Files.createTempDirectory("jdeps-demo");
        try {
            Path source = work.resolve("Demo.java");
            Files.writeString(source, """
                    public class Demo {
                        public static void main(String[] args) {
                            String name = "jdeps";
                            System.out.println("chào " + name + args.length);
                        }
                    }
                    """);
            Path classes = work.resolve("out");
            ToolProvider.findFirst("javac").orElseThrow()
                    .run(System.out, System.err, "-d", classes.toString(), source.toString());

            Path archive = work.resolve("demo.jar");
            ToolProvider.findFirst("jar").orElseThrow().run(System.out, System.err,
                    "--create", "--file", archive.toString(), "-C", classes.toString(), ".");

            ToolProvider jdeps = ToolProvider.findFirst("jdeps").orElseThrow();

            // 1. Mức tóm tắt: archive -> module (mục 5).
            System.out.println("=== (1) jdeps -s demo.jar ===");
            jdeps.run(System.out, System.err, "-s", archive.toString());

            // 2. Định dạng dành cho jlink (mục 5).
            System.out.println("\n=== (2) jdeps --print-module-deps demo.jar ===");
            jdeps.run(System.out, System.err, "--print-module-deps", archive.toString());
            System.out.println("   ^ chuỗi này đưa thẳng cho jlink --add-modules được");

            // 3. Chi tiết: lộ ra CallSite từ invokedynamic, thứ không có trong source (mục 6).
            System.out.println("\n=== (3) jdeps -v demo.jar ===");
            jdeps.run(System.out, System.err, "-v", archive.toString());
            System.out.println("   ^ java.lang.invoke.CallSite đến từ nối chuỗi, xem t3_ mục 8");

            // 4. Thất bại có ích: module không nhận package mặc định (mục 7).
            System.out.println("\n=== (4) jdeps --generate-module-info ===");
            int rc = jdeps.run(System.out, System.err,
                    "--generate-module-info", work.resolve("gen").toString(), archive.toString());
            System.out.println("mã thoát = " + rc + "  (cùng luật với jar --describe-module, t8_ mục 8)");
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
