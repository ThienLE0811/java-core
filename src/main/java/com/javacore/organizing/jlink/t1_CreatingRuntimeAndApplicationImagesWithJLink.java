package com.javacore.organizing.jlink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.spi.ToolProvider;

/**
 * Creating Runtime and Application Images with JLink
 * <p>
 * 0. Vị trí bài này
 * - Bước 31/41, section JLink. Trên dev.java section này chỉ có MỘT trang, nên repo cũng chỉ
 * một file {@code t1_*}.
 * - Đây là chỗ trả lời câu hỏi thực dụng nhất của cả cụm Organizing: "học module để làm gì?".
 * Câu trả lời: để {@code jlink} cắt được JDK xuống còn đúng phần bạn dùng.
 * - Nối thẳng từ t12 mục 9 của section Modules: {@code --add-modules} đã học ở đó chính là
 * cờ bắt buộc của {@code jlink}.
 * - Một câu cho cả bài: {@code jlink} = chọn module + ghép thành một runtime tự đứng được,
 * KHÔNG phải biên dịch sang mã máy.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/jlink/">dev.java - Creating Runtime and Application Images with JLink</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * - Mọi con số và thông báo lỗi ở mục 10-13 là ĐO THẬT trên JDK 25.0.4.1 / Windows, không
 * phải lấy từ trang gốc. Máy khác sẽ ra số khác, nhưng tỉ lệ thì giống.
 * - Tám mục của trang gốc: Creating Runtime Images, The Smallest Runtime, Creating
 * Application Images, The Optional Module Path, Generating a Native Launcher, Including
 * Services, Generating Images Across Operating Systems, Optimizing the Image.
 * <p>
 * ======================================================================
 * TRANG GỐC - JLINK (mục 1 đến 9)
 * ======================================================================
 * <p>
 * 1. JLINK LÀ GÌ
 * - Nguyên văn: {@code jlink} "select[s] a number of modules, platform modules as well as
 * those making up your application, and link[s] them into a runtime image."
 * - Kết quả chạy được y như một JDK, nhưng bên trong chỉ có những module bạn chọn cùng các
 * phụ thuộc của chúng.
 * - CÂU DỄ HIỂU SAI NHẤT, trang gốc nói thẳng: nó "just links bytecode - it does not compile
 * it to machine code, so this is no ahead-of-time compilation."
 * + Tức là {@code jlink} KHÔNG phải GraalVM native-image. Ra vẫn là bytecode + một JVM.
 * + Hệ quả: image nhỏ hơn JDK nhưng không phải vài MB, và thời gian khởi động cải thiện ít.
 * - Đối chiếu cho người đến từ Node.js: {@code jlink} gần với việc đóng gói đúng những
 * {@code node_modules} thật sự dùng kèm luôn bản Node runtime, chứ không phải biên dịch ra
 * một binary như Go.
 * <p>
 * 2. TẠO RUNTIME IMAGE - HAI CỜ BẮT BUỘC
 * - {@code jlink} cần đúng hai thông tin:
 * + {@code --add-modules} - lấy module nào (chính là cờ ở t12 mục 1).
 * + {@code --output} - đổ image ra thư mục nào.
 * - BA ĐIỂM LẠ khi {@code jlink} resolve module, phải nhớ vì nó KHÁC lúc chạy {@code java}:
 * + Service KHÔNG được bind mặc định - "services are not bound by default" (xem mục 7).
 * + Phụ thuộc tùy chọn ({@code requires static}, bài t3) KHÔNG được resolve - "optional
 * dependencies are not resolved".
 * + Automatic module (bài t8) KHÔNG được phép.
 * - Cách nhớ ba điểm này: {@code jlink} chỉ nhận những gì CHẮC CHẮN. Cái gì mơ hồ - tùy
 * chọn, động, không có descriptor thật - thì nó bỏ qua hoặc từ chối.
 * <p>
 * 3. RUNTIME NHỎ NHẤT
 * - "The simplest possible runtime image contains only the base module", tức là
 * {@code --add-modules java.base}.
 * <pre>
 *   jlink --add-modules java.base --output base-image
 * </pre>
 * - Image này chạy được bất cứ chương trình nào chỉ cần {@code java.base} - nhưng không có
 * {@code jdk.compiler}, nên KHÔNG chạy được {@code java Mot.java} (source-file mode), cũng
 * không có JShell, không có jar/javac.
 * <p>
 * 4. TẠO APPLICATION IMAGE
 * - Khác runtime image ở chỗ đóng gói luôn module ứng dụng vào trong:
 * + {@code --module-path} trỏ tới chỗ chứa module ứng dụng.
 * + {@code --add-modules} nêu main module (và module nào khác cần).
 * <pre>
 *   jlink --module-path mods --add-modules com.example.app --output app-image
 * </pre>
 * - Module nền tảng và module ứng dụng sau khi vào image được gọi chung là SYSTEM MODULES.
 * Thuật ngữ này quan trọng cho mục 5.
 * - Giới hạn nhắc lại lần hai: "jlink only operates on explicit modules, so an application
 * depending on automatic modules can't be linked into an image."
 * + Đây là LÝ DO THỰC TẾ MẠNH NHẤT để modular hóa thư viện. Một JAR chưa có
 * {@code module-info} trong cây phụ thuộc là đủ để chặn cả image.
 * <p>
 * 5. MODULE PATH LÚC CHẠY TRỞ THÀNH TÙY CHỌN
 * - Vì image đã chứa cả ứng dụng, chạy không cần {@code --module-path}:
 * <pre>
 *   app-image/bin/java --module com.example.app
 * </pre>
 * - Vẫn được phép thêm {@code --module-path} khi chạy, nhưng nhớ luật: "system modules will
 * always shadow modules of the same name on the module path."
 * + Nghĩa là module trong image LUÔN THẮNG module cùng tên đặt ngoài. Không có cách nào
 * "vá đè" một module đã nằm trong image bằng cách để bản mới ra ngoài.
 * + Công dụng hợp lệ của module path lúc này: thêm NHÀ CUNG CẤP DỊCH VỤ mới (tên module
 * khác) mà không phải dựng lại image - tức là cơ chế plugin.
 * <p>
 * 6. SINH NATIVE LAUNCHER
 * - Cú pháp: {@code --launcher NAME=MODULE/MAIN_CLASS}
 * + {@code NAME} - tên file thực thi sinh ra trong {@code bin/}.
 * + {@code MODULE} - module để chạy.
 * + {@code MAIN_CLASS} - bỏ được nếu module đã nhúng main class vào descriptor (t9 mục 2).
 * <pre>
 *   jlink --module-path mods --add-modules com.example.app --output app-image --launcher app=com.example.app
 * </pre>
 * - Trên Windows {@code jlink} sinh RA HAI file: {@code bin/app} (script sh) và
 * {@code bin/app.bat}. Gọi từ cmd/PowerShell thì dùng bản {@code .bat}.
 * - HẠN CHẾ phải nhớ, nguyên văn: "All options you try to apply to the launching JVM will be
 * interpreted as if you had put them behind the {@code --module} option, making them program
 * arguments instead."
 * + Dịch sang hậu quả thật: {@code ./bin/app -Xmx512m} KHÔNG đặt heap, mà truyền chuỗi
 * {@code "-Xmx512m"} vào {@code args} của hàm main. Lỗi này rất khó nhìn ra vì không có
 * thông báo nào cả - JVM vẫn chạy, chỉ là chạy với cấu hình mặc định.
 * - Hai lối thoát trang gốc đưa ra:
 * + Sửa script launcher để nó đọc biến môi trường {@code JLINK_VM_OPTIONS}.
 * + Hoặc bỏ launcher, gọi thẳng {@code app-image/bin/java --module ...} - lúc đó mọi cờ JVM
 * hoạt động bình thường.
 * - Thực tế: dùng launcher cho tiện lúc demo; production gần như luôn gọi thẳng
 * {@code bin/java} vì cần chỉnh heap, GC, cờ container.
 * <p>
 * 7. ĐƯA SERVICE VÀO IMAGE
 * - Mặc định: "no service binding when creating an image". Nhắc lại mục 2.
 * + Lý do: binding service là quan hệ ĐỘNG (t6). Nếu bind hết, image sẽ phình ra vì mỗi
 * {@code uses} kéo theo mọi {@code provides} tương ứng.
 * - Ba cách xử lý:
 * + {@code --suggest-providers SERVICE} - "list all modules in the runtime or on the module
 * path that provide an implementation of SERVICE". Dùng để TRA trước khi quyết định.
 * + Thêm tay từng module bằng {@code --add-modules} - cách nên dùng, kiểm soát được.
 * + {@code --bind-services} - "include all modules that provide a service that is used by
 * another resolved module". Tiện nhưng nguy hiểm, xem mục 12.
 * - VÍ DỤ CHUẨN CỦA TRANG GỐC - CHARSET:
 * + {@code java.base} có sẵn các charset thông dụng (ISO-8859-1, UTF-8, UTF-16).
 * + Các charset còn lại nằm ở {@code jdk.charsets}, nối vào bằng service nên KHÔNG tự vào
 * image. Cần thì phải {@code --add-modules jdk.charsets}.
 * + Đây là loại lỗi chỉ nổ ở production khi gặp đúng file dữ liệu encoding lạ. Xem số đo
 * thật ở mục 13 và demo chạy được ở phần code dưới.
 * <p>
 * 8. TẠO IMAGE CHO HỆ ĐIỀU HÀNH KHÁC
 * - Image phụ thuộc HĐH vì chứa JVM dạng mã máy của HĐH đó.
 * - Nhưng vẫn cross-build được trên một máy, ba bước:
 * + Tải JDK của HĐH đích.
 * + Đặt thư mục {@code jmods} của JDK đó lên module path.
 * + Chạy {@code jlink} của máy mình.
 * <pre>
 *   jlink --module-path mods:/path/to/windows-jdk/jmods --add-modules com.example.app --output app-image
 * </pre>
 * - Quy tắc: {@code jmods} tham chiếu phải CÙNG PHIÊN BẢN với binary {@code jlink} đang chạy
 * (trang gốc lấy ví dụ cả hai đều 16.0.2).
 * - Kiểm chứng: nhìn vào {@code app-image/bin}, có {@code java.exe} tức là image cho Windows.
 * - Lưu ý dấu ngăn cách module path: {@code :} trên Linux/macOS, {@code ;} trên Windows
 * (giống t9 mục 1).
 * <p>
 * 9. TỐI ƯU IMAGE
 * - Trang gốc không liệt kê cờ, chỉ nói có nhiều tùy chọn "reducing image size and some
 * improve launch times", và bảo xem "the {@code jlink} reference for a full list of options".
 * - Lời khuyên nguyên văn đáng nhớ: phải "thoroughly test the resulting image and measure
 * actual improvements" trước khi đem dùng.
 * + Hiểu đúng: mỗi cờ tối ưu đều CẮT BỚT thứ gì đó. Cắt sai thì image vẫn build thành công
 * và chỉ hỏng lúc chạy. Danh sách cờ cụ thể ở mục 11.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 10. SỐ ĐO THẬT - JDK 25.0.4.1 / WINDOWS
 * <pre>
 *   JDK đầy đủ                                                 365 MB   (hơn 70 module)
 *   jlink --add-modules java.base                               49 MB   (1 module)
 *   ... thêm --strip-debug --no-header-files --no-man-pages
 *       --compress zip-9                                        31 MB   (1 module)
 *   jlink --add-modules java.base --bind-services              162 MB   (46 module!)
 *   application image (com.example.app + java.logging)          49 MB   (3 module)
 * </pre>
 * - Ba điều rút ra:
 * + Tỉ lệ thật là khoảng 7 lần (365 -&gt; 49), không phải "vài MB". Đúng với mục 1: không AOT.
 * + Nhóm cờ tối ưu cắt thêm chừng 37% nữa - đáng làm, và đây là lý do dùng nó cho Docker.
 * + {@code --bind-services} một mình làm image to hơn 3 lần. Xem mục 12.
 * <p>
 * 11. CÁC CỜ TỐI ƯU CỤ THỂ - ĐỐI CHIẾU {@code jlink --help} TRÊN JDK 25
 * <pre>
 *   --compress zip-0..zip-9   nén resource; zip-0 = không nén, zip-9 = nén mạnh nhất
 *   -G, --strip-debug         bỏ thông tin debug trong class file
 *   --no-header-files         bỏ file header C (chỉ cần khi viết JNI)
 *   --no-man-pages            bỏ trang man
 *   --bind-services           kéo vào mọi module cung cấp service đang được dùng
 *   --suggest-providers NAME  tra xem module nào cung cấp service NAME
 *   --launcher NAME=MODULE[/MAIN_CLASS]
 * </pre>
 * - CẢNH BÁO về {@code --strip-debug}: nó bỏ bảng số dòng, nên stack trace mất số dòng
 * ({@code Unknown Source}). Tiết kiệm vài MB mà đổi lấy việc gỡ lỗi production trong mù mịt
 * là đánh đổi rất tệ. Riêng cờ này phải cân nhắc kỹ, khác hẳn ba cờ kia.
 * - {@code --compress} đánh đổi dung lượng lấy thời gian giải nén lúc nạp class.
 * - Cú pháp {@code --compress} đã ĐỔI: bản cũ dùng {@code --compress=2}, từ JDK 21 là
 * {@code zip-0..zip-9}. Script cũ chép trên mạng về có thể không chạy.
 * <p>
 * 12. {@code --bind-services} LÀ CÁI BẪY LỚN NHẤT CỦA BÀI NÀY
 * - Nghe thì hợp lý: "cứ bind hết cho chắc". Nhưng đo thật ở mục 10: image từ 1 module /
 * 49 MB nhảy lên 46 module / 162 MB - chỉ vì {@code java.base} có vài khai báo {@code uses}.
 * - Nó kéo vào cả {@code jdk.compiler}, {@code jdk.javadoc}, {@code jdk.jshell},
 * {@code jdk.jpackage} - những thứ không ứng dụng production nào cần.
 * - Quy tắc nên theo: dùng {@code --suggest-providers} để TRA, rồi {@code --add-modules}
 * đúng module cần. Chỉ dùng {@code --bind-services} khi dựng image dạng "công cụ" mà không
 * biết trước sẽ cần gì.
 * <p>
 * 13. BA LỖI THẬT VÀ THÔNG BÁO CỦA CHÚNG
 * - Automatic module (mục 2, mục 4), thông báo nguyên văn khi chạy thử:
 * <pre>
 *   Error: automatic module cannot be used with jlink: plainlib from file:///.../plainlib.jar
 * </pre>
 * + Cách sửa thật: thêm {@code module-info.java} cho thư viện đó, hoặc dùng {@code jdeps}
 * sinh ra một bản nháp ({@code jdeps --generate-module-info}), hoặc đổi thư viện.
 * - {@code --output} trỏ vào thư mục ĐÃ TỒN TẠI: {@code jlink} từ chối, không ghi đè. Trong
 * script CI phải xóa thư mục cũ trước.
 * - Thiếu charset: image java.base chỉ có 55 charset, JDK đầy đủ có 173 - hụt 118 cái, trong
 * đó có {@code EUC-JP}, {@code EUC-KR}, {@code Big5}, {@code GB2312}. Image build xong,
 * chạy ổn, rồi một ngày đọc đúng file đó thì {@code UnsupportedCharsetException}. Đây là lỗi
 * muộn nhất và khó truy nhất của bài - phần code dưới tái hiện lại nó.
 * + Ranh giới KHÔNG đoán được bằng trực giác: {@code Shift_JIS} và {@code GBK} vẫn nằm
 * trong {@code java.base}, nhưng {@code EUC-JP} và {@code GB2312} thì không. Phải đo chứ
 * đừng suy luận.
 * <p>
 * 14. {@code jdeps} - CÔNG CỤ ĐI TRƯỚC {@code jlink}
 * - Câu hỏi "ứng dụng của tôi cần những module nào?" không trả lời bằng tay được. Dùng:
 * <pre>
 *   jdeps --print-module-deps --ignore-missing-deps APP.jar   -&gt; chuỗi dán thẳng vào --add-modules
 *   jdeps -s APP.jar                                          -&gt; tóm tắt quan hệ module
 *   jdeps --generate-module-info THU_MUC APP.jar              -&gt; sinh nháp module-info.java
 * </pre>
 * - Quy trình chuẩn trong thực tế: {@code jdeps} tìm ra tập module, rồi đưa tập đó cho
 * {@code jlink}.
 * - Lưu ý khi đo thật: {@code jdeps} phân tích BYTECODE, nên chỉ thấy phụ thuộc đang thực sự
 * được dùng. Module khai trong {@code requires} nhưng chưa dùng tới sẽ không hiện ra - và
 * phụ thuộc chỉ xuất hiện qua reflection thì nó không thấy. Đừng tin tuyệt đối.
 * <p>
 * 15. {@code jpackage} - BƯỚC SAU {@code jlink}
 * - {@code jlink} ra một THƯ MỤC. {@code jpackage} (chuẩn từ JDK 16) gói thư mục đó thành
 * INSTALLER của HĐH: {@code .msi}/{@code .exe}, {@code .dmg}/{@code .pkg},
 * {@code .deb}/{@code .rpm}.
 * - Quan hệ: {@code jpackage} gọi {@code jlink} bên trong. Không phải thay thế nhau.
 * - Chỉ cần khi phát hành ứng dụng desktop cho người dùng cuối. Backend/server thường dừng ở
 * {@code jlink} rồi cho vào Docker.
 * <p>
 * 16. VÌ SAO BACKEND NÊN QUAN TÂM - DOCKER
 * - Đây là chỗ {@code jlink} có giá trị rõ nhất với người làm server: Dockerfile hai tầng,
 * tầng build có JDK đầy đủ, tầng chạy chỉ copy image do {@code jlink} sinh ra.
 * <pre>
 *   # build stage: jlink --add-modules $(jdeps --print-module-deps app.jar) --output /jre
 *   # run stage:   FROM debian:slim ; COPY --from=build /jre /jre ; CMD ["/jre/bin/java", ...]
 * </pre>
 * - So sánh: image Docker dùng {@code eclipse-temurin:25} nặng vài trăm MB; dùng runtime do
 * {@code jlink} cắt thì phần Java chỉ còn vài chục MB.
 * - NHƯNG, điểm phải biết trước khi hào hứng: Spring Boot thường KHÔNG modular hóa, phụ thuộc
 * đầy automatic module, nên mục 4 chặn lại. Với Spring Boot, cách dùng {@code jlink} khả dĩ
 * là chỉ cắt RUNTIME (một tập module JDK đủ dùng) rồi chạy fat JAR trên đó bằng class path -
 * chứ không gói ứng dụng vào image.
 * - Hệ quả thực dụng: biết {@code jlink} là để cắt base image, không phải để modular hóa ứng
 * dụng Spring.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 17. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code jlink} có biên dịch sang mã máy không? Nó khác GraalVM native-image chỗ nào?
 * - Hai cờ bắt buộc của {@code jlink} là gì?
 * - Ba thứ {@code jlink} KHÔNG tự xử lý khi resolve module?
 * - Vì sao một automatic module trong cây phụ thuộc là đủ để chặn cả application image?
 * - Chạy image kèm {@code --module-path}, module nào thắng khi trùng tên?
 * - Vì sao {@code ./bin/app -Xmx512m} không đặt được heap? Hai cách sửa?
 * - {@code --suggest-providers} khác {@code --bind-services} thế nào, và vì sao nên ưu tiên
 * cái đầu?
 * - Cần charset ngoài UTF-8/ISO-8859-1 thì thêm module nào?
 * - Cross-build image cho Windows từ Linux cần gì, và ràng buộc phiên bản là gì?
 * - {@code --strip-debug} đánh đổi điều gì?
 * - {@code jdeps} / {@code jlink} / {@code jpackage} - mỗi cái nhận vào gì, ra gì?
 */
public class t1_CreatingRuntimeAndApplicationImagesWithJLink {

    void main() throws IOException, InterruptedException {
        // Cả bài này là chuyện dòng lệnh, nhưng jlink có mặt dưới dạng ToolProvider nên gọi
        // được ngay từ Java - giống cách t9 gọi javac. Nhờ vậy các con số ở mục 10 và 13
        // kiểm chứng lại được mà không phải mở terminal.
        ToolProvider jlink = ToolProvider.findFirst("jlink").orElseThrow(
                () -> new IllegalStateException("không thấy jlink - đang chạy trên JRE chứ không phải JDK?"));

        // 1. TRA TRƯỚC KHI THÊM: ai cung cấp CharsetProvider? (mục 7)
        System.out.println("=== jlink --suggest-providers java.nio.charset.spi.CharsetProvider ===");
        jlink.run(System.out, System.err,
                "--suggest-providers", "java.nio.charset.spi.CharsetProvider");

        // 2. RUNTIME NHỎ NHẤT (mục 3). jlink từ chối --output đã tồn tại (mục 13), nên trỏ
        //    vào một thư mục con CHƯA được tạo bên trong thư mục tạm.
        Path work = Files.createTempDirectory("jlink-demo");
        Path image = work.resolve("base-image");
        System.out.println("\n=== jlink --add-modules java.base --output " + image + " ===");
        int rc = jlink.run(System.out, System.err,
                "--add-modules", "java.base",
                "--output", image.toString());
        System.out.println("jlink trả về mã " + rc + "   (0 = thành công)");

        try {
            // 3. ĐO THẬT: image vừa cắt so với JDK đang chạy (mục 10)
            Path jdkHome = Path.of(System.getProperty("java.home"));
            System.out.printf("%nimage  : %4d MB   %s%n", sizeMb(image), image);
            System.out.printf("JDK    : %4d MB   %s%n", sizeMb(jdkHome), jdkHome);
            System.out.println("   -> chênh nhau vài lần, KHÔNG phải vài chục lần: vì không có AOT (mục 1)");

            // 4. Image chứa đúng những gì? Chạy chính java của image để hỏi nó.
            Path imageJava = javaBinOf(image);
            System.out.println("\n=== java --list-modules (bên trong image) ===");
            run(imageJava.toString(), "--list-modules");

            // 5. SERVICE KHÔNG ĐƯỢC BIND (mục 7, 13): cùng một class, chạy trên hai runtime,
            //    đếm charset. Biên dịch bằng javac của JDK hiện tại rồi ném sang image chạy
            //    bằng -cp - image chỉ có java.base nên không tự biên dịch được.
            Path src = work.resolve("DemCharset.java");
            Files.writeString(src, """
                    import java.nio.charset.Charset;
                    public class DemCharset {
                        public static void main(String[] args) {
                            System.out.println("so charset = " + Charset.availableCharsets().size()
                                    + ", Shift_JIS ? " + Charset.isSupported("Shift_JIS")
                                    + ", EUC-JP ? " + Charset.isSupported("EUC-JP"));
                        }
                    }
                    """);
            ToolProvider javac = ToolProvider.findFirst("javac").orElseThrow();
            javac.run(System.out, System.err, "-d", work.toString(), src.toString());

            System.out.println("\n=== cùng một class, hai runtime ===");
            System.out.print("JDK đầy đủ : ");
            run(javaBinOf(jdkHome).toString(), "-cp", work.toString(), "DemCharset");
            System.out.print("base-image : ");
            run(imageJava.toString(), "-cp", work.toString(), "DemCharset");
            System.out.println("   -> hụt hơn một trăm charset, vì jdk.charsets nối vào bằng SERVICE");
            System.out.println("      mà jlink không bind service (mục 7). Sửa: --add-modules jdk.charsets");
            System.out.println("   -> và để ý: Shift_JIS vẫn còn (java.base có), EUC-JP thì mất.");
            System.out.println("      Ranh giới này phải ĐO, không suy luận được (mục 13).");
        } finally {
            // Dọn thư mục tạm dù ở trên có hỏng - đừng để lại vài chục MB rác mỗi lần chạy.
            deleteRecursively(work);
        }

        System.out.println();
        // Bài này chủ yếu gõ tay ở terminal. Làm tuần tự, mỗi TODO ghi lại lệnh thật.
        //
        // TODO 1 (mục 3, 10): Chạy `jlink --add-modules java.base --output base-image` ở
        //   terminal. Đo dung lượng. Rồi chạy lại với --strip-debug --no-header-files
        //   --no-man-pages --compress zip-9. Ghi hai con số vào note và so với mục 10.
        //
        // TODO 2 (mục 11): Trên image đã --strip-debug, chạy một chương trình ném exception.
        //   Nhìn stack trace: còn số dòng không? Đây là lúc tự quyết định có đáng đổi không.
        //
        // TODO 3 (mục 4, 6): Lấy lại module demo.app đã dựng ở TODO 1 bài t9. Tạo
        //   application image có --launcher. Chạy bằng bin/app và bằng bin/java --module,
        //   xác nhận cả hai ra cùng kết quả.
        //
        // TODO 4 (mục 6): Với launcher vừa tạo, chạy `bin/app -Xmx16m`. Chương trình nhận
        //   heap 16 MB, hay nhận chuỗi "-Xmx16m" vào args? In Runtime.getRuntime().maxMemory()
        //   để chứng minh. Rồi làm lại bằng bin/java --module để thấy khác biệt.
        //
        // TODO 5 (mục 2, 4, 13): Thêm một plain JAR (không có module-info) vào module path
        //   và thử jlink. Chép nguyên thông báo lỗi vào note. Rồi dùng
        //   `jdeps --generate-module-info` sinh module-info cho nó, biên dịch lại, và xác
        //   nhận jlink chạy được.
        //
        // TODO 6 (mục 7, 12): Chạy `jlink --add-modules java.base --bind-services`. Đếm
        //   module và đo dung lượng. So với image ở TODO 1. Tìm trong danh sách những module
        //   bạn chắc chắn không cần - đó là lý do mục 12 gọi cờ này là bẫy.
        //
        // TODO 7 (mục 7, 13): Dựng image java.base rồi đọc một file encoding EUC-JP hoặc
        //   GB2312. Ghi lại exception. Thêm --add-modules jdk.charsets, chạy lại, xác nhận
        //   hết lỗi và đo xem image to thêm bao nhiêu. Rồi in cả hai danh sách
        //   Charset.availableCharsets() ra file và diff - tự xây bảng "cái nào ở java.base,
        //   cái nào ở jdk.charsets" thay vì đoán.
        //
        // TODO 8 (mục 5): Chạy application image KÈM --module-path trỏ tới một bản MỚI HƠN
        //   của chính module đã nằm trong image. Xác nhận bản trong image vẫn thắng
        //   (shadowing). Rồi thử lại với một module TÊN KHÁC đóng vai service provider -
        //   lần này nó có được nạp không?
        //
        // TODO 9 (mục 14): Chạy `jdeps --print-module-deps` lên một JAR thật trong dự án của
        //   bạn. Dán kết quả vào --add-modules của jlink. Image build được không? Nếu lỗi,
        //   lỗi đó cho thấy jdeps đã bỏ sót gì?
        //
        // TODO 10 (mục 16): Viết một Dockerfile hai tầng dùng jlink. So sánh dung lượng image
        //   cuối với bản dùng thẳng eclipse-temurin. Rồi thử với một ứng dụng Spring Boot và
        //   ghi lại chỗ nó chặn bạn - đối chiếu với lời cảnh báo ở mục 16.
    }

    /** Tính dung lượng một thư mục, đơn vị MB - để đối chiếu với bảng ở mục 10. */
    private static long sizeMb(Path dir) throws IOException {
        try (var paths = Files.walk(dir)) {
            long bytes = paths.filter(Files::isRegularFile).mapToLong(p -> {
                try {
                    return Files.size(p);
                } catch (IOException e) {
                    return 0L;   // file biến mất giữa chừng thì bỏ qua, không làm hỏng phép đo
                }
            }).sum();
            return bytes / (1024 * 1024);
        }
    }

    /** bin/java của một image hoặc JDK, có tính tới đuôi .exe trên Windows (mục 8). */
    private static Path javaBinOf(Path home) {
        Path exe = home.resolve("bin").resolve("java.exe");
        return Files.exists(exe) ? exe : home.resolve("bin").resolve("java");
    }

    /** Chạy một tiến trình con và đổ output thẳng ra console hiện tại. */
    private static void run(String... command) throws IOException, InterruptedException {
        new ProcessBuilder(command).inheritIO().start().waitFor();
    }

    /** Dọn thư mục tạm: phải xóa file trước rồi mới tới thư mục, nên duyệt theo thứ tự ngược. */
    private static void deleteRecursively(Path root) throws IOException {
        try (var paths = Files.walk(root)) {
            for (Path p : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(p);
            }
        }
    }
}
