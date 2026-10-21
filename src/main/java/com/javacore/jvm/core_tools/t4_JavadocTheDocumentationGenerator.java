package com.javacore.jvm.core_tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.spi.ToolProvider;
import java.util.stream.Stream;

/**
 * Javadoc - the Documentation Generator
 * <p>
 * 0. Vị trí bài này
 * - Bài 4/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/javadoc/">dev.java - Javadoc - the Documentation Generator</a>
 * - Trang gốc có 8 mục, trong đó 4 mục là bảng option (hơn 60 cờ của standard doclet). Lại
 * là một trang man page - không chép bảng, chỉ giữ cái không đoán được.
 * - Bài này có ý nghĩa riêng với repo: toàn bộ note của repo đang nằm trong Javadoc. Tức là
 * {@code javadoc} có thể dựng chính kho note này thành HTML tra cứu được. Thư mục
 * {@code target/jdoc} trong repo là kết quả của một lần chạy như vậy.
 * - Mọi thông báo ở mục 6 trở đi là ĐO THẬT trên JDK 25.0.4.1 / Windows.
 * <p>
 * ======================================================================
 * TRANG GỐC - JAVADOC (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. JAVADOC LÀ GÌ
 * - Đọc KHAI BÁO và COMMENT TÀI LIỆU trong file {@code .java}, sinh ra một bộ HTML mô tả
 * API.
 * - Nhận vào: tên package, file nguồn, hoặc file cấu hình {@code @argfile}.
 * - Điểm cốt lõi hay bị quên: {@code javadoc} PHẢI PHÂN TÍCH CÚ PHÁP được code. Code không
 * biên dịch nổi thì {@code javadoc} cũng không chạy nổi. Nó dùng chung front-end với
 * {@code javac}, nên nhận gần hết option của {@code javac}: {@code -cp},
 * {@code --module-path}, {@code -encoding}, {@code --release}.
 * <p>
 * 2. DOCLET - KIẾN TRÚC CẮM THÊM
 * - {@code javadoc} chỉ phân tích code rồi giao cho một DOCLET quyết định in ra cái gì.
 * - Mặc định là "standard doclet" sinh HTML. Thay bằng {@code -doclet} +
 * {@code -docletpath} để sinh định dạng khác.
 * - Hệ quả: các cờ chia làm hai loại - cờ của chính {@code javadoc} (phân tích code) và cờ
 * của standard doclet (trình bày). Trang gốc tách riêng hai bảng vì lý do này.
 * - Cờ standard doclet hay dùng: {@code -d} (thư mục ra), {@code -doctitle},
 * {@code -windowtitle}, {@code -link}, {@code -linkoffline}, {@code -linksource},
 * {@code -author}, {@code -notimestamp}.
 * <p>
 * 3. CỜ VỀ PHẠM VI
 * - {@code -public} / {@code -protected} / {@code -package} / {@code -private}.
 * - MẶC ĐỊNH là {@code -protected}, tức là chỉ tài liệu hoá public và protected. Đây là
 * khác biệt cần nhớ với {@code javap} (mặc định {@code -package}).
 * - Hệ quả cho repo này: class note để {@code public} nhưng phương thức {@code main()} của
 * các bài là package-private, nên mặc định KHÔNG xuất hiện trong HTML. Muốn thấy phải
 * thêm {@code -private}.
 * - {@code -subpackages} để quét cả cây package thay vì liệt kê từng cái.
 * <p>
 * 4. CONFORMANCE - CÂU CẢNH BÁO CỦA TRANG GỐC
 * - Trang gốc nói rõ: standard doclet KHÔNG kiểm chứng NỘI DUNG của comment tài liệu.
 * - Nghĩa là: {@code @param} sai tên, mô tả nói một đằng code làm một nẻo - {@code javadoc}
 * không biết. Nó chỉ kiểm tra được phần cấu trúc, và đó là việc của doclint (mục 6).
 * <p>
 * 5. CỜ {@code -J}
 * - {@code -J} truyền thẳng option xuống JVM chạy {@code javadoc}, ví dụ
 * {@code javadoc -J-Xmx32m -J-Xms32m com.mypackage}.
 * - Cần khi tài liệu hoá codebase lớn bị {@code OutOfMemoryError}. Quy ước {@code -J} này
 * dùng chung cho nhiều tool JDK ({@code javap}, {@code jar} cũng có).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐO THẬT TRÊN JDK 25.0.4.1 / WINDOWS
 * ======================================================================
 * <p>
 * 6. DOCLINT BẬT SẴN - VÀ NÓ KHÓ TÍNH HƠN BẠN TƯỞNG
 * - Trang gốc xếp {@code -Xdoclint} vào mục "additional options" như một thứ phụ. Thực tế
 * nó BẬT MẶC ĐỊNH từ JDK 8 và là nguồn gây ồn lớn nhất khi chạy {@code javadoc}.
 * - Đo thật với một class có đủ {@code @param a} nhưng thiếu {@code @param b}:
 * {@code warning: no @param for b}
 * {@code warning: use of default constructor, which does not provide a comment}
 * {@code 2 warnings}
 * - Cái thứ hai đáng chú ý: KHÔNG viết constructor nào cũng bị cảnh báo, vì constructor mặc
 * định cũng là thành viên public cần tài liệu.
 * - {@code -Xdoclint:none} tắt sạch. Lọc theo nhóm được: {@code accessibility},
 * {@code html}, {@code missing}, {@code reference}, {@code syntax}. Hay dùng nhất là
 * {@code -Xdoclint:all,-missing} - vẫn bắt HTML hỏng và link gãy, nhưng không ép phải
 * viết đủ mọi {@code @param}.
 * <p>
 * 7. WARNING KHÔNG LÀM HỎNG BUILD
 * - Đo thật: dù in ra 2 warning, {@code javadoc} vẫn trả mã thoát 0 và vẫn sinh đủ HTML.
 * - Hệ quả CI: muốn chặn tài liệu hỏng thì phải tự kiểm tra, vì mã thoát không nói gì. Với
 * Maven, đó là {@code <failOnWarning>} của {@code maven-javadoc-plugin}.
 * - Chỉ LỖI cú pháp (HTML không đóng thẻ, tag không tồn tại) mới làm mã thoát khác 0.
 * <p>
 * 8. BỘ HTML SINH RA GỒM NHỮNG GÌ
 * - Đo thật trên một package một class: {@code allclasses-index.html},
 * {@code allpackages-index.html}, {@code element-list}, {@code help-doc.html}, thư mục
 * theo tên package, cùng {@code index.html}, {@code search.js}, stylesheet.
 * - {@code element-list} (trước JDK 10 tên là {@code package-list}) là file quan trọng
 * nhất mà không ai để ý: đó là thứ {@code -link} tải về để biết package nào có trong một
 * bộ tài liệu bên ngoài. Thiếu nó thì liên kết chéo sang Javadoc của JDK hay thư viện
 * khác sẽ gãy hết.
 * <p>
 * 9. BA CỜ THỰC SỰ DÙNG TRONG DỰ ÁN THẬT
 * - {@code -link https://docs.oracle.com/en/java/javase/25/docs/api/}: mọi tên class JDK
 * trong tài liệu của bạn thành liên kết tới tài liệu chính chủ. Cần mạng lúc build.
 * {@code -linkoffline} làm cùng việc đó với bản {@code element-list} tải sẵn - đây là
 * cách build offline trong CI.
 * - {@code -notimestamp}: bỏ dấu thời gian khỏi HTML. Không có cờ này, mỗi lần build ra
 * một diff khác nhau dù nội dung y hệt - hỏng build tái lập được, bẩn git.
 * - {@code -quiet}: chỉ in cảnh báo và lỗi, bỏ dòng tiến trình.
 * <p>
 * 10. CÁCH DỰNG HTML CHO CHÍNH KHO NOTE NÀY
 * - Lệnh tương đương với thư mục {@code target/jdoc} đã có trong repo:
 * {@code javadoc -d target/jdoc -private -encoding UTF-8 -quiet -Xdoclint:none
 * -sourcepath src/main/java -subpackages com.javacore}
 * - {@code -private} là bắt buộc nếu muốn thấy {@code main()} của từng bài (mục 3).
 * - {@code -encoding UTF-8} là bắt buộc trên Windows, nếu không tiếng Việt trong note sẽ
 * thành ký tự rác - cùng một lý do với mục 11 của {@code t2_}.
 * - {@code -Xdoclint:none} vì note không viết theo chuẩn API (không có {@code @param} cho
 * mọi tham số), và đó là có chủ ý.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Phạm vi mặc định của {@code javadoc} là gì? Khác {@code javap} chỗ nào?
 * - Doclet là gì, và vì sao option lại chia làm hai bảng?
 * - Doclint có bật sẵn không? Tắt một phần bằng cách nào?
 * - Warning của {@code javadoc} có làm mã thoát khác 0 không?
 * - {@code element-list} dùng để làm gì? {@code -link} khác {@code -linkoffline} ra sao?
 * - Vì sao build lặp lại nên có {@code -notimestamp}?
 */
public class t4_JavadocTheDocumentationGenerator {

    void main() throws IOException {
        Path work = Files.createTempDirectory("javadoc-demo");
        try {
            Path pkg = work.resolve("src/app");
            Files.createDirectories(pkg);
            // Cố ý thiếu @param b và không có constructor -> đủ để doclint kêu 2 lần (mục 6).
            Files.writeString(pkg.resolve("Svc.java"), """
                    package app;
                    /** Dịch vụ mẫu. */
                    public class Svc {
                        /**
                         * Cộng hai số.
                         * @param a số thứ nhất
                         * @return tổng
                         */
                        public int add(int a, int b) { return a + b; }
                    }
                    """);

            ToolProvider javadoc = ToolProvider.findFirst("javadoc").orElseThrow();
            Path source = pkg.resolve("Svc.java");

            // 1. Chạy mặc định: doclint lên tiếng, nhưng mã thoát vẫn 0 (mục 6, mục 7).
            System.out.println("=== (1) javadoc mặc định - doclint bật sẵn ===");
            int rc1 = javadoc.run(System.out, System.err,
                    "-d", work.resolve("out1").toString(),
                    "-encoding", "UTF-8", "-quiet", source.toString());
            System.out.println("mã thoát = " + rc1 + "  <- có warning nhưng vẫn 0");

            // 2. Tắt doclint: im lặng hoàn toàn.
            System.out.println("\n=== (2) thêm -Xdoclint:none ===");
            Path out2 = work.resolve("out2");
            int rc2 = javadoc.run(System.out, System.err,
                    "-d", out2.toString(), "-encoding", "UTF-8", "-quiet",
                    "-Xdoclint:none", source.toString());
            System.out.println("mã thoát = " + rc2 + "  (không còn dòng cảnh báo nào ở trên)");

            // 3. Xem bộ HTML sinh ra gồm những gì (mục 8).
            System.out.println("\n=== (3) file sinh ra ở thư mục gốc ===");
            try (Stream<Path> entries = Files.list(out2)) {
                entries.map(p -> p.getFileName().toString()).sorted()
                        .forEach(name -> System.out.println("  " + name));
            }
            System.out.println("  -> element-list là file mà -link của dự án khác sẽ đọc (mục 8)");
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
