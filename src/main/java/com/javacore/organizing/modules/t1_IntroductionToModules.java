package com.javacore.organizing.modules;

import java.lang.module.ModuleDescriptor;

/**
 * Introduction to Modules in Java
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 1/12 của section Modules.
 * - Bài nền của cả section. 11 bài sau chỉ làm hai việc: tinh chỉnh từng directive trong
 * {@code module-info.java} (t2 đến t6), hoặc dạy cách sống chung / gỡ rào khi thế giới
 * thật chưa modular hóa (t7 đến t12). Hiểu sai bài này thì 11 bài sau chỉ là cú pháp.
 * - Một câu cho cả bài: module là một tầng đóng gói NẰM TRÊN package - nó quyết định
 * package nào được nhìn thấy từ bên ngoài, và JVM kiểm tra toàn bộ cấu hình đó NGAY LÚC
 * KHỞI ĐỘNG thay vì để {@code NoClassDefFoundError} nổ giữa chừng lúc 3 giờ sáng.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/intro/">dev.java - Introduction to Modules in Java</a>
 * (đăng 14/09/2021).
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * - LƯU Ý THỰC HÀNH: repo này là một project Maven thường, toàn bộ code nằm trong UNNAMED
 * MODULE (bài t7). Không được thả {@code module-info.java} vào {@code src/main/java} - sẽ
 * phá build hiện tại. Mọi thực hành của section này làm trong thư mục sandbox riêng, gõ
 * {@code javac} / {@code java} tay (xem phần TODO ở {@code main()}).
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRODUCTION TO MODULES (mục 1 đến 10)
 * ======================================================================
 * <p>
 * 1. MODULE GỒM BỐN THỨ
 * - "At the core of each module is the module declaration, a file with the name
 * module-info.java that defines all properties of a module."
 * - Bốn thành phần của một module, theo đúng trang gốc:
 * + một cái TÊN,
 * + danh sách PHỤ THUỘC vào các module khác,
 * + một API CÔNG KHAI - "with everything else being module internal and inaccessible",
 * + danh sách SERVICE mà nó dùng và cung cấp.
 * - Đọc kỹ vế thứ ba. Đây là chỗ đảo ngược mặc định của Java suốt 20 năm: trước JPMS, cứ
 * {@code public} là cả thế giới gọi được. Từ JPMS, {@code public} chỉ còn nghĩa "công khai
 * TRONG module", muốn ra ngoài phải được {@code exports} cho phép.
 * <p>
 * 2. KHUNG CỦA MODULE-INFO.JAVA
 * - Trang gốc đưa đúng khung này (thay các tên viết hoa bằng tên thật):
 * <pre>
 *   module TEN_MODULE {
 *       requires  TEN_MODULE_KHAC;
 *       exports   TEN_PACKAGE;
 *       opens     TEN_PACKAGE;
 *       uses      TEN_KIEU_DAY_DU;
 *       provides  KIEU_SERVICE with KIEU_CAI_DAT;
 *   }
 * </pre>
 * - Vị trí file: "placed in the project's source root", tức ngang hàng với thư mục package
 * cao nhất, KHÔNG nằm trong package nào cả.
 * - Nhớ ba điều về file này:
 * + Nó là file {@code .java} thật, do {@code javac} biên dịch, không phải file cấu hình.
 * + {@code module}, {@code requires}, {@code exports}... là RESTRICTED KEYWORD - chỉ có
 * nghĩa đặc biệt bên trong file này. Đặt tên biến là {@code module} ở nơi khác vẫn hợp lệ.
 * + Thứ tự các directive không quan trọng; quy ước là nhóm theo loại cho dễ đọc.
 * <p>
 * 3. TÊN MODULE
 * - Ký tự hợp lệ, nguyên văn: "legal letters include A-Z, a-z, 0-9, _, and $, separated
 * by ." - tức cùng bộ ký tự với tên package.
 * - Quy ước: "module names are all lower case and $ is only used for mechanically generated
 * code", và "names should be globally unique."
 * - Cách đặt tên an toàn trong thực tế: lấy REVERSE-DNS giống package gốc của dự án, ví dụ
 * {@code com.javacore.organizing}. Lý do phải toàn cục duy nhất nằm ở mục 12: hai artifact
 * cùng khai một tên module thì JVM từ chối khởi động.
 * <p>
 * 4. REQUIRES - KHAI BÁO PHỤ THUỘC
 * - {@code requires} liệt kê các phụ thuộc TRỰC TIẾP. Phụ thuộc không được khai thì không
 * dùng được, kể cả khi JAR đó đang nằm sẵn trên module path.
 * - Trang gốc báo trước hai biến thể, học riêng ở hai bài sau:
 * "It is also possible to handle optional dependencies (with requires static) and to
 * 'forward' dependencies that are part of a module's API (with requires transitive)."
 * + {@code requires static} -&gt; bài t3.
 * + {@code requires transitive} -&gt; bài t4.
 * <p>
 * 5. EXPORTS VÀ OPENS - HAI CÁNH CỬA KHÁC NHAU
 * - Đây là cặp khái niệm dễ nhầm nhất bài. Trang gốc phân biệt bằng đúng hai câu:
 * + {@code exports}: "public types and members in exported packages are available at
 * compile and run time".
 * + {@code opens}: "all types and members in open packages can be accessed at run time via
 * reflection".
 * - Đặt cạnh nhau cho rõ ba điểm khác:
 * + exports mở lúc BIÊN DỊCH và lúc CHẠY; opens chỉ mở lúc CHẠY.
 * + exports chỉ mở phần {@code public}; opens mở TẤT CẢ, kể cả {@code private}.
 * + exports phục vụ code gọi thẳng; opens phục vụ REFLECTION.
 * - Hệ quả thực tế: Spring, Jackson, Hibernate đều đọc field {@code private} bằng
 * reflection. Với chúng, {@code exports} là vô dụng - phải {@code opens}. Đây chính là toàn
 * bộ nội dung bài t2.
 * - Lời khuyên của trang gốc, đáng chép nguyên văn: "try to export as few packages as
 * possible - just like keeping fields private, only making methods package-visible or
 * public if needed."
 * - Mẹo đặt package cho dễ sống: tách sẵn {@code ...api} (sẽ export) và {@code ...internal}
 * (không bao giờ export). Quyết định này nên có từ ngày đầu, sửa sau rất đau.
 * <p>
 * 6. USES VÀ PROVIDES - SERVICE
 * - {@code uses} khai báo "tôi sẽ đi tìm các cài đặt của interface này lúc chạy";
 * {@code provides ... with} khai báo "tôi cung cấp một cài đặt". Cơ chế tìm là
 * {@code ServiceLoader}.
 * - Giá trị: module dùng chỉ phụ thuộc vào INTERFACE, không phụ thuộc vào module cài đặt.
 * Thay cài đặt = đổi JAR trên module path, không sửa một dòng code.
 * - Chi tiết đầy đủ ở bài t6.
 * <p>
 * 7. MODULAR JAR VÀ MODULE DESCRIPTOR
 * - {@code module-info.java} biên dịch ra {@code module-info.class}, và file class này
 * chính là MODULE DESCRIPTOR.
 * - JAR có module descriptor = MODULAR JAR. JAR không có = PLAIN JAR.
 * - Nhớ cặp từ này, vì mục 8 ngay dưới sẽ lật ngược trực giác về nó.
 * <p>
 * 8. MODULE PATH - VÀ CÂU QUAN TRỌNG NHẤT CỦA BÀI
 * - Định nghĩa, nguyên văn: "The module path is a new concept that parallels the class
 * path: It is a list of artifacts (JARs or bytecode folders) and directories that contain
 * artifacts."
 * - Và đây là câu phải thuộc, nguyên văn: "Whether a JAR is modular or not does not
 * determine whether it is treated as a module! All JARs on the class path are treated as a
 * single barely-a-module, all JARs on the module path get turned into modules."
 * - Dịch cho thật rõ, vì sai chỗ này là sai cả section: thứ quyết định một JAR có phải
 * module hay không KHÔNG phải nội dung của nó, mà là BẠN ĐẶT NÓ Ở ĐÂU.
 * - Bốn tổ hợp, nên thuộc bảng này:
 * + modular JAR + module path -&gt; module tường minh, đúng tên trong descriptor.
 * + plain JAR + module path -&gt; AUTOMATIC MODULE, tên suy ra từ tên file (bài t8).
 * + modular JAR + class path -&gt; descriptor bị LỜ ĐI, chui vào unnamed module (bài t7).
 * + plain JAR + class path -&gt; unnamed module, y như Java 8 (bài t7).
 * - Cờ dòng lệnh: {@code --module-path} (viết tắt {@code -p}) so với {@code --class-path}
 * ({@code -cp}). Bài t9 thực hành gõ tay.
 * <p>
 * 9. MODULE RESOLUTION, MODULE GRAPH, READABILITY
 * - Lệnh chạy một ứng dụng modular:
 * <pre>
 *   java --module-path DUONG_DAN --module TEN_MODULE/TEN_LOP_MAIN
 * </pre>
 * - Thuật toán, nguyên văn: "Beginning with the initial module's name, the module system
 * will search the module path for it. If it finds it, it will check its requires directives
 * to see what modules it needs and then repeats the process for them."
 * - Tức là duyệt đồ thị từ module khởi đầu. Hệ quả rất thực tế: module nằm trên module path
 * nhưng KHÔNG ai {@code requires} tới thì không được nạp. Đây là nguồn gốc của cờ
 * {@code --add-modules} ở bài t12.
 * - Kết quả là MODULE GRAPH: node là module, cạnh là READABILITY EDGE đi từ module
 * {@code requires} sang module được {@code requires}.
 * - Từ "đọc được" (reads) sẽ xuất hiện liên tục ở các bài sau. "A reads B" nghĩa là A được
 * phép dùng những gì B đã export.
 * <p>
 * 10. JAVA.BASE - MODULE ĐẶC BIỆT
 * - "the module system is aware of it specifically", và "no need to put requires java.base
 * in a module declaration - a dependency on the base module comes for free."
 * - {@code java.base} chứa {@code java.lang}, {@code java.util}, {@code java.io}... Mọi
 * module đều tự động đọc nó, giống như mọi file Java tự động thấy {@code java.lang}.
 * - Bản thân JDK đã được tách thành nhiều chục module từ Java 9. Đừng học thuộc con số,
 * hãy chạy {@code java --list-modules} để tự xem (TODO 1).
 * <p>
 * ======================================================================
 * TRANG GỐC - BA LỢI ÍCH (mục 11 đến 13)
 * ======================================================================
 * <p>
 * 11. STRONG ENCAPSULATION - BA ĐIỀU KIỆN
 * - Nguyên văn, điều kiện để dùng được một kiểu từ module khác: "A type from a module can
 * only be used if: the type is public (as before), the package is exported, the module
 * using the type reads the module containing the type."
 * - BA điều kiện, phải đủ CẢ BA. Trước Java 9 chỉ có điều kiện đầu.
 * - Câu chốt của mục, nguyên văn: "the creator of a module has much more control over which
 * types make up the public API. No longer is it all public types, now it's all public types
 * in exported packages."
 * - Nhớ công thức một dòng: API công khai = public + nằm trong package được exports + bên
 * kia có requires.
 * - Vi phạm điều kiện 2 hoặc 3 thì {@code javac} báo lỗi ngay; nếu lách qua reflection thì
 * lúc chạy nhận lỗi truy cập - gốc của bài t10 và t11.
 * <p>
 * 12. RELIABLE CONFIGURATION - BA KIỂM TRA LÚC KHỞI ĐỘNG
 * - Module resolution kiểm tra ba điều, nguyên văn:
 * + "There must be no ambiguity, i.e. no two artifacts can claim they're the same module."
 * + "There must be no static dependency cycles between modules."
 * + "Packages should have a unique origin, so no two modules can contain types in the same
 * package."
 * - Ý nghĩa thật của cả ba: chuyển lỗi từ LÚC CHẠY về LÚC KHỞI ĐỘNG. Thời classpath, thiếu
 * JAR hay trùng version thì chương trình vẫn lên, rồi chết bằng
 * {@code NoClassDefFoundError} đúng vào lúc người dùng bấm nút. Với module, JVM từ chối
 * khởi động kèm thông báo rõ ràng.
 * - Kiểm tra thứ ba có tên riêng rất hay gặp trong log: SPLIT PACKAGE. Hai module cùng chứa
 * một package là lỗi. Đây là lý do nhiều thư viện cũ không lên module path được.
 * - Lưu ý chữ STATIC ở kiểm tra thứ hai: cấm chu trình phụ thuộc KHAI BÁO. Lúc chạy, hai
 * module vẫn gọi qua lại nhau được (qua service hoặc callback).
 * <p>
 * 13. SCALABLE PLATFORM - DẪN SANG JLINK
 * - Modular hóa "makes it a self-contained application image that comes with everything it
 * needs, from your code to dependencies to JDK APIs and the JVM."
 * - Tức là đóng gói được một runtime chỉ gồm những module thực sự dùng, không phải cả JDK.
 * Công cụ làm việc đó là {@code jlink} - chính là bước 31 ngay sau section này.
 * - Đây là câu trả lời thực tế cho câu hỏi "modular hóa để được gì": image nhỏ hơn -&gt;
 * Docker image nhẹ hơn, khởi động nhanh hơn, bề mặt tấn công hẹp hơn.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 14. BẮC CẦU TỪ TYPESCRIPT - GIỐNG VÀ KHÁC Ở ĐÂU
 * - Trực giác gần nhất từ TS: trường {@code "exports"} trong {@code package.json}, KHÔNG
 * phải từ khóa {@code export} của ES module.
 * + {@code export} trong TS mở ở mức FILE. {@code exports} của Java mở ở mức PACKAGE.
 * + {@code "exports"} của package.json quyết định đường dẫn nào bên ngoài import được vào
 * gói - đúng vai trò của {@code exports} trong {@code module-info.java}.
 * - Ba điểm Java làm chặt hơn hẳn Node:
 * + Node kiểm tra khi thực sự import tới. Java kiểm tra TOÀN BỘ đồ thị ngay lúc khởi động
 * (mục 12).
 * + Node cho phép hai version của cùng một package cùng tồn tại trong {@code node_modules}.
 * Java CẤM tuyệt đối - hai artifact cùng tên module là lỗi khởi động.
 * + Node không có khái niệm tương đương {@code opens}, vì JS không có access modifier để mà
 * phá.
 * - Một điểm giống thú vị: cả hai đều gặp bài toán "thư viện cũ chưa khai báo gì cả". Node
 * xử lý bằng CommonJS interop, Java xử lý bằng automatic module (bài t8).
 * <p>
 * 15. VÌ SAO DỰ ÁN SPRING BOOT THỰC TẾ HẦU NHƯ KHÔNG DÙNG JPMS
 * - Câu hỏi sẽ tự nảy ra: học xong 12 bài này rồi có dùng ở công ty không? Trả lời thẳng:
 * phần lớn là KHÔNG, và cần biết lý do để khỏi hiểu nhầm là mình học thừa.
 * + Spring Boot đóng gói fat JAR với bộ nạp class riêng, chạy trên CLASS PATH. Không có hỗ
 * trợ chính thức cho việc chạy một ứng dụng Boot trên module path.
 * + Hệ sinh thái Spring dùng reflection dày đặc, nên nếu modular hóa thì phải
 * {@code opens} gần như mọi package - lợi ích đóng gói gần như bằng không.
 * + Nhiều thư viện phổ biến vẫn còn split package hoặc chưa khai báo tên module.
 * - Vậy học để làm gì? Ba lý do rất thực tế:
 * + Bản thân JDK đã modular từ Java 9. Mọi lỗi truy cập kiểu "module java.base does not
 * opens java.lang to unnamed module" mà bạn gặp đều bắt nguồn từ đây (bài t10, t11). Không
 * hiểu module thì chỉ biết copy cờ từ StackOverflow.
 * + Thư viện và framework (thứ bạn phụ thuộc) thì CÓ modular hóa.
 * + {@code jlink} và ảnh runtime gọn (bước 31) chỉ hoạt động trên nền module.
 * <p>
 * 16. BA LỖI KINH ĐIỂN - NHẬN MẶT TRƯỚC KHI GẶP
 * - "module not found": JAR đó đang ở class path chứ không phải module path, hoặc không ai
 * {@code requires} tới nó (mục 9).
 * - "package P is declared in module A and module B": split package, vi phạm kiểm tra thứ
 * ba ở mục 12. Hướng xử lý duy nhất là gộp hoặc đổi tên package.
 * - "Unable to make field private ... accessible: module A does not opens P to module B":
 * thiếu {@code opens}, KHÔNG phải thiếu {@code exports}. Xem lại mục 5 rồi sang bài t2.
 * <p>
 * 17. BA LỆNH TRA CỨU NÊN THUỘC
 * - {@code java --list-modules} - liệt kê mọi module có trong JDK đang dùng.
 * - {@code java --describe-module TEN_MODULE} - in ra nội dung descriptor: nó requires gì,
 * exports gì, opens gì. Cách nhanh nhất để đọc một module lạ.
 * - {@code jdeps} - phân tích phụ thuộc thật của code; {@code jdeps --generate-module-info}
 * còn sinh sẵn bản nháp {@code module-info.java}. Rất hữu ích khi modular hóa dự án cũ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 18. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn thành phần của một module là gì?
 * - Ba điều kiện để dùng được một kiểu từ module khác (mục 11)?
 * - {@code exports} và {@code opens} khác nhau ở ba điểm nào? Jackson cần cái nào?
 * - Modular JAR đặt trên class path thì chuyện gì xảy ra? Plain JAR đặt trên module path?
 * - Ba kiểm tra của reliable configuration? Cái nào sinh ra lỗi split package?
 * - Vì sao không cần viết {@code requires java.base}?
 * - Module nằm trên module path mà không ai requires tới thì có được nạp không? Vì sao?
 * - "A reads B" nghĩa là gì, và nó khác "A requires B" ở chỗ nào?
 */
public class t1_IntroductionToModules {

    void main() {
        Module here = getClass().getModule();
        Module base = String.class.getModule();

        // 1. Mọi lớp đều thuộc về MỘT module - kể cả lớp không khai báo gì (mục 1)
        System.out.println("String.class thuộc module : " + base.getName());
        System.out.println("lớp này      thuộc module : " + here.getName()
                + "   <- null nghĩa là UNNAMED MODULE, xem bài t7");

        // 2. java.base là module đặc biệt: ai cũng đọc được mà không cần requires (mục 10)
        System.out.println("\nở đây đọc được java.base? " + here.canRead(base));

        // 3. BA ĐIỀU KIỆN truy cập - đây là mục 11 nhìn bằng số liệu thật
        ModuleDescriptor d = base.getDescriptor();
        System.out.println("\njava.base có tổng cộng " + d.packages().size() + " package,"
                + " nhưng chỉ exports " + d.exports().size() + " cái");
        System.out.println("  exports java.util?          " + base.isExported("java.util"));
        System.out.println("  exports jdk.internal.misc?  " + base.isExported("jdk.internal.misc")
                + "   <- public nhưng KHÔNG exports = không dùng được (bài t10)");

        // 4. Đồ thị module dựng lúc khởi động, đếm được (mục 9)
        System.out.println("\nboot layer đang có " + ModuleLayer.boot().modules().size()
                + " module được resolve");

        System.out.println();
        // CHUẨN BỊ SANDBOX (làm một lần, dùng cho cả section):
        //   Tạo thư mục NGOÀI src/main/java, ví dụ D:\works\java-core\sandbox\modules\
        //   Mọi TODO dưới đây làm trong đó, biên dịch bằng javac tay. Lý do: repo này là
        //   project Maven unnamed module, thả module-info.java vào src sẽ phá build (mục 0).
        //
        // TODO 1 (mục 10): Chạy `java --list-modules`, đếm xem JDK đang dùng có bao nhiêu
        //   module. Rồi chạy `java --describe-module java.base` và
        //   `java --describe-module java.sql`. Ghi lại: java.sql requires những module nào?
        //   Nó exports bao nhiêu package?
        //
        // TODO 2 (mục 1, 2, 11): Tạo module đầu tiên, cấu trúc:
        //     demo.greet/module-info.java                     -> module demo.greet { }
        //     demo.greet/com/demo/greet/api/Greeter.java       (public)
        //     demo.greet/com/demo/greet/internal/Helper.java   (public)
        //   Rồi tạo module thứ hai demo.app requires demo.greet và gọi cả hai lớp.
        //   Mục tiêu: chưa exports gì cả thì demo.app KHÔNG biên dịch được, dù cả hai lớp
        //   đều public. Chép nguyên văn thông báo lỗi của javac vào note.
        //
        // TODO 3 (mục 5, 11): Thêm `exports com.demo.greet.api;` vào demo.greet, biên dịch
        //   lại. Xác nhận: Greeter dùng được, Helper vẫn KHÔNG. Đây là mục 11 nhìn bằng mắt
        //   - public không còn đủ.
        //
        // TODO 4 (mục 5): Trong demo.app, thử đọc một field private của Greeter bằng
        //   reflection (setAccessible(true)). Biên dịch OK nhưng chạy sẽ ném
        //   InaccessibleObjectException. Đọc kỹ thông báo: nó đòi `opens`, không phải
        //   `exports`. Chưa cần sửa - để dành cho bài t2.
        //
        // TODO 5 (mục 8): Đóng demo.greet thành JAR, chạy ứng dụng hai lần: một lần đặt JAR
        //   trên --module-path, một lần trên --class-path. Lần thứ hai thử truy cập Helper
        //   (lớp không được exports). Giải thích vì sao lần này lại chạy được. Đây là cách
        //   tự chứng minh câu quan trọng nhất ở mục 8.
        //
        // TODO 6 (mục 12): Cố tình tạo split package - cho module thứ ba demo.extra cũng
        //   chứa package com.demo.greet.api. Đặt cả ba lên module path và chạy. Ghi lại
        //   nguyên văn lỗi, và CHÚ Ý nó xảy ra lúc KHỞI ĐỘNG chứ không phải lúc gọi hàm.
        //
        // TODO 7 (mục 12): Cố tình tạo chu trình: demo.greet requires demo.app và ngược lại.
        //   Thử biên dịch. Lỗi xảy ra ở giai đoạn nào - biên dịch hay chạy? Vì sao trang gốc
        //   nhấn chữ "static" trong "no static dependency cycles"?
        //
        // TODO 8 (mục 15, 17): Chạy `jdeps --generate-module-info .` lên một JAR bất kỳ
        //   trong ~/.m2 (ví dụ jackson-databind). Đọc bản module-info.java nó sinh ra: thư
        //   viện đó requires gì, và nếu phải modular hóa thì sẽ vướng vấn đề gì ở mục 15?
    }
}
