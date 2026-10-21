package com.javacore.organizing.modules;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.spi.ToolProvider;

/**
 * Building Modules on the Command Line
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 9/12 của section Modules.
 * - Bài THỰC HÀNH thuần túy, không có khái niệm mới. Giá trị của nó: tám bài trước nói về
 * luật, bài này là chỗ duy nhất bạn thấy luật đó được áp vào bằng lệnh cụ thể.
 * - Một câu cho cả bài: Maven và Gradle che hết những lệnh này đi, nên gõ tay một lần là
 * cách duy nhất để hiểu build tool đang làm gì sau lưng mình.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/building/">dev.java - Building Modules on the Command Line</a>
 * - Trang gốc viết lệnh bằng biến thay thế ({@code $DEPS}, {@code $CLASS_FOLDER},
 * {@code $SOURCES}, {@code $JAR}, {@code $CLASSES}, {@code $MODULE_NAME/$MAIN_CLASS}).
 * Note này giữ nguyên cách đó, viết hoa thành TEN_BIEN cho hợp cú pháp Javadoc.
 * - Mọi cờ nêu dưới đây đã được đối chiếu với {@code javac --help} / {@code jar --help} /
 * {@code java --help} trên JDK 25 để chắc là còn tồn tại và đúng chính tả.
 * - Bốn mục của trang gốc: A Basic Build, Defining a Main Class, Circumventing Strong
 * Encapsulation, Extending the Module Graph.
 * <p>
 * ======================================================================
 * TRANG GỐC - BUILDING MODULES (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. BA BƯỚC CỦA MỘT BUILD CƠ BẢN
 * - Trang gốc mô tả: cho một dự án có vài file nguồn, một module declaration và vài phụ
 * thuộc, đây là cách biên dịch - đóng gói - chạy ở dạng đơn giản nhất.
 * <pre>
 *   // 1. BIÊN DỊCH
 *   javac --module-path DEPS -d CLASS_FOLDER SOURCES
 *
 *   // 2. ĐÓNG GÓI
 *   jar --create --file JAR -C CLASS_FOLDER .
 *
 *   // 3. CHẠY
 *   java --module-path JAR:DEPS --module MODULE_NAME/MAIN_CLASS
 * </pre>
 * - Ba điều cần chú ý, vì đây là chỗ khác với thời class path:
 * + SOURCES phải bao gồm cả {@code module-info.java}. Quên nó thì javac vẫn chạy nhưng ra
 * một bộ class THƯỜNG, không phải module - và bạn sẽ ngồi gỡ lỗi nhầm chỗ.
 * + CLASSES đóng gói phải bao gồm {@code module-info.class} - chính nó biến JAR thành
 * modular JAR (t1 mục 7).
 * + Dấu ngăn cách trong path là {@code :} trên Linux/macOS và {@code ;} trên Windows. Trên
 * Windows nhớ bọc path trong ngoặc kép.
 * - {@code --module-path} viết tắt là {@code -p}; {@code --module} viết tắt là {@code -m}.
 * <p>
 * 2. ĐẶT MAIN CLASS VÀO DESCRIPTOR
 * - Nguyên văn: "The jar option --main-class MAIN_CLASS embeds MAIN_CLASS as the class
 * containing the main method in the module descriptor."
 * <pre>
 *   jar --create --file JAR --main-class MAIN_CLASS -C CLASS_FOLDER .
 *   java --module-path JAR:DEPS --module MODULE_NAME
 * </pre>
 * - Khi đã nhúng main class, lúc chạy chỉ cần tên module, không cần phần
 * {@code /MAIN_CLASS}. Nếu vẫn ghi thì phần ghi sau dấu gạch chéo sẽ GHI ĐÈ cái đã nhúng -
 * tiện khi một module có nhiều entry point.
 * - Phân biệt với thời class path: trước đây main class nằm ở {@code Main-Class} trong
 * MANIFEST; giờ nó nằm trong MODULE DESCRIPTOR. Hai chỗ khác nhau, dùng cho hai cách chạy
 * khác nhau ({@code java -jar} so với {@code java --module}).
 * <p>
 * 3. BIÊN DỊCH NHIỀU MODULE CÙNG LÚC
 * - Khi có nhiều module trong một dự án, dùng {@code --module-source-path} thay vì liệt kê
 * từng thư mục:
 * <pre>
 *   javac --module-source-path ".\*\src\main\java" ^
 *         --module-path DEPS ^
 *         -d CLASS_FOLDER ^
 *         --module MODULE_A,MODULE_B
 * </pre>
 * - Ký tự {@code *} trong module-source-path là CHỖ TRỐNG cho tên module, không phải
 * wildcard của shell. Ví dụ trên viết theo kiểu Windows (dấu gạch ngược, nối dòng bằng
 * {@code ^}); trên Linux đổi sang dấu gạch chéo và nối dòng bằng dấu gạch ngược.
 * - Điều kiện: cấu trúc thư mục phải đặt theo TÊN MODULE, mỗi module một thư mục con, bên
 * trong là source root chứa {@code module-info.java}.
 * - Lợi ích: javac tự hiểu quan hệ giữa các module và biên dịch đúng thứ tự. Đây chính là
 * việc Maven đang làm hộ bạn trong một multi-module project.
 * <p>
 * 4. BA NHÓM CỜ NÂNG CAO MÀ TRANG GỐC NHẮC ĐẾN
 * - Hai mục cuối của trang gốc chỉ liệt kê và trỏ sang bài riêng:
 * + {@code --add-exports} - phá đóng gói lúc BIÊN DỊCH (chi tiết ở t11).
 * + {@code --add-opens} - phá đóng gói lúc CHẠY, cho reflection (chi tiết ở t11).
 * + {@code --add-modules} - thêm module vào graph (chi tiết ở t12).
 * + {@code --add-reads} - thêm cạnh readability (chi tiết ở t12).
 * - Điểm cần nhớ ngay ở đây: cả bốn cờ dùng được cho CẢ {@code javac} LẪN {@code java}.
 * Cùng một bức tường, có thể phải phá ở cả hai thời điểm. Rất nhiều người sửa được lỗi
 * biên dịch rồi ngạc nhiên vì lúc chạy vẫn lỗi.
 * <p>
 * 5. CÁC LỆNH TRA CỨU ĐI KÈM
 * <pre>
 *   java --list-modules                      // mọi module của JDK
 *   java --describe-module TEN_MODULE        // nội dung descriptor
 *   jar  --file X.jar --describe-module      // module của một JAR cụ thể
 *   javac --module-version 1.0 ...           // nhúng version vào descriptor
 *   jar  --module-version 1.0 ...            // hoặc nhúng lúc đóng gói
 * </pre>
 * - {@code --module-version} là thứ duy nhất trong JPMS liên quan tới version, và cần biết
 * giới hạn của nó: module system CHỈ GHI version vào descriptor để công cụ đọc, nó KHÔNG
 * dùng version để chọn module hay giải quyết xung đột. JPMS không thay thế Maven.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. MAVEN LÀM GÌ VỚI MODULE-INFO.JAVA
 * - Chỉ cần đặt {@code module-info.java} vào {@code src/main/java}, maven-compiler-plugin
 * tự phát hiện và chuyển sang chế độ module: nó đẩy các phụ thuộc lên MODULE PATH thay vì
 * class path.
 * - Hệ quả hay làm người ta bất ngờ: build đang chạy ngon bỗng vỡ hàng loạt sau khi thêm
 * một file {@code module-info.java}, vì mọi thư viện đột nhiên phải có tên module và mọi
 * truy cập đột nhiên bị kiểm tra. Đây chính xác là lý do bài t1 dặn ĐỪNG thêm file đó vào
 * repo java-core này.
 * - Với test: Surefire/Failsafe có cách xử lý riêng, thường cần thêm {@code --add-opens}
 * cho module test. Đây là nguồn gốc phổ biến nhất của các dòng cờ dài ngoằng trong pom.xml
 * của các dự án đã modular hóa.
 * <p>
 * 7. CẤU TRÚC THƯ MỤC CHUẨN CHO SANDBOX CỦA SECTION NÀY
 * <pre>
 *   sandbox/modules/
 *     demo.greet/
 *       module-info.java
 *       com/demo/greet/api/Greeter.java
 *     demo.app/
 *       module-info.java
 *       com/demo/app/Main.java
 *     out/            (class files)
 *     jars/           (modular JARs)
 * </pre>
 * - Hai lệnh dùng đi dùng lại cho cả section (Windows PowerShell, một module):
 * <pre>
 *   javac -d out/demo.greet (Get-ChildItem -Recurse -Filter *.java demo.greet).FullName
 *   java  -p out --module demo.app/com.demo.app.Main
 * </pre>
 * - Mẹo: để {@code -p out} trỏ vào THƯ MỤC chứa các thư mục module đã biên dịch - module
 * path nhận cả thư mục chứa artifact, không chỉ file JAR (t1 mục 8).
 * <p>
 * 8. LIÊN HỆ SANG CÁC BÀI SAU
 * - Ba bài cuối (t10, t11, t12) đều là về CỜ DÒNG LỆNH. Bài này là nền để đọc chúng: biết
 * cờ đặt ở đâu trong lệnh, và biết phải đặt cho cả javac lẫn java.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba lệnh của một build cơ bản, kể cả viết tắt {@code -p} và {@code -m}?
 * - Quên {@code module-info.java} trong danh sách SOURCES thì hậu quả là gì?
 * - {@code --main-class} của jar nhúng thông tin vào đâu? Khác gì {@code Main-Class} trong
 * manifest?
 * - Chạy {@code java --module m/Chay} khi module đã nhúng main class khác thì cái nào thắng?
 * - {@code --module-source-path} dùng khi nào và đòi hỏi cấu trúc thư mục ra sao?
 * - Bốn cờ nâng cao, và điều cần nhớ chung về chúng?
 * - {@code --module-version} có ảnh hưởng tới việc chọn module lúc resolve không?
 */
public class t9_BuildingModulesOnTheCommandLine {

    void main() throws IOException {
        // Ví dụ này dựng MỘT MODULE THẬT trong thư mục tạm rồi gọi javac qua ToolProvider -
        // đúng ba bước ở mục 1, chỉ khác là gọi bằng API thay vì gõ ở terminal. Nhờ vậy
        // chạy được ngay trong repo mà không cần thêm module-info.java vào src (mục 0).
        Path src = Files.createTempDirectory("mod-src");
        Path out = Files.createTempDirectory("mod-out");

        // 1. Hai file nguồn: module declaration + một lớp trong package được exports
        Files.writeString(src.resolve("module-info.java"), """
                module com.demo.hello {
                    exports com.demo.hello.api;
                }
                """);
        Path pkg = Files.createDirectories(src.resolve("com/demo/hello/api"));
        Files.writeString(pkg.resolve("Hello.java"), """
                package com.demo.hello.api;
                public class Hello {
                    public static String chao() { return "xin chao tu module"; }
                }
                """);

        // 2. Biên dịch - tương đương: javac -d CLASS_FOLDER SOURCES (mục 1)
        ToolProvider javac = ToolProvider.findFirst("javac").orElseThrow();
        int rc = javac.run(System.out, System.err,
                "-d", out.toString(),
                src.resolve("module-info.java").toString(),
                pkg.resolve("Hello.java").toString());
        System.out.println("javac trả về mã " + rc + "   (0 = thành công)");
        System.out.println("có sinh ra module-info.class? "
                + Files.exists(out.resolve("module-info.class"))
                + "   <- chính nó biến thư mục này thành một module (mục 1)");

        // 3. Đọc lại descriptor vừa sinh ra - tương đương: java --describe-module (mục 5)
        ModuleDescriptor d = ModuleFinder.of(out).findAll().iterator().next().descriptor();
        System.out.println("\nmodule   : " + d.name());
        System.out.println("exports  : " + d.exports());
        System.out.println("requires : " + d.requires() + "   <- java.base tự có, không phải khai");
        System.out.println("automatic? " + d.isAutomatic() + "   <- false vì có module-info thật");

        System.out.println();
        // Bài này KHÔNG có TODO đọc hiểu - toàn bộ là gõ tay. Làm tuần tự.
        //
        // TODO 1 (mục 1): Dựng lại demo.greet + demo.app theo cấu trúc ở mục 7. Biên dịch,
        //   đóng gói, chạy bằng đúng ba lệnh ở mục 1. Lưu ba lệnh đã chạy thật (có đường dẫn
        //   thật của máy mình) vào note - sau này copy lại cho các bài t10-t12.
        //
        // TODO 2 (mục 1): Cố tình BỎ module-info.java khỏi danh sách file truyền cho javac.
        //   Quan sát: javac vẫn chạy, nhưng chạy java --module thì lỗi gì? Dùng
        //   `jar --describe-module` lên kết quả để thấy nó không phải modular JAR.
        //
        // TODO 3 (mục 2): Đóng gói lại với --main-class. Chạy hai cách: chỉ `--module
        //   demo.app`, và `--module demo.app/mot.lop.Main.Khac`. Xác nhận cái sau ghi đè.
        //   Rồi chạy `jar --file ... --describe-module` để thấy main class nằm trong
        //   descriptor.
        //
        // TODO 4 (mục 3): Chuyển sandbox sang cấu trúc --module-source-path và biên dịch cả
        //   hai module bằng MỘT lệnh javac duy nhất. So sánh với việc chạy hai lệnh riêng:
        //   lệnh nào ngắn hơn, và javac tự lo được việc gì?
        //
        // TODO 5 (mục 5): Nhúng --module-version 1.0 vào demo.greet. Dùng --describe-module
        //   xác nhận version xuất hiện. Sau đó tạo bản 2.0, đặt CẢ HAI JAR lên module path
        //   cùng lúc và chạy. JVM có dùng version để chọn bản mới hơn không? Ghi lại lỗi và
        //   đối chiếu với t1 mục 12 (no ambiguity).
        //
        // TODO 6 (mục 6): Tạo một project Maven nhỏ RIÊNG (ngoài repo này), thêm
        //   module-info.java và một phụ thuộc bất kỳ. Chạy `mvn -X compile`, tìm trong log
        //   dòng javac thật mà Maven sinh ra. Đối chiếu từng cờ với mục 1 - đây là lúc thấy
        //   build tool đang làm gì sau lưng mình.
    }
}
