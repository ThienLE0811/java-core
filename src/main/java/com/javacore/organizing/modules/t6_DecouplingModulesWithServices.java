package com.javacore.organizing.modules;

import java.nio.file.spi.FileSystemProvider;
import java.util.ServiceLoader;

/**
 * Decoupling Modules with Services
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 6/12 của section Modules.
 * - Bài duy nhất trong section nói về KIẾN TRÚC chứ không về luật truy cập. Bốn bài trước
 * đều trả lời "ai được thấy cái gì"; bài này trả lời "làm sao để KHÔNG cần thấy nhau".
 * - Một câu cho cả bài: {@code uses} + {@code provides} + {@code ServiceLoader} cho phép
 * module tiêu thụ chỉ phụ thuộc vào INTERFACE, còn module cài đặt thì tự khai báo mình với
 * hệ thống - thay cài đặt bằng cách đổi JAR trên module path, không sửa một dòng code.
 * - Nếu bạn quen Spring, đây là bản DI tối giản có sẵn trong JVM, không cần framework.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/services/">dev.java - Decoupling Modules with Services</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - SERVICES (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. VẤN ĐỀ: INTERFACE KHÔNG ĐỦ ĐỂ TÁCH RỜI
 * - Nguyên văn: "the challenge of using interfaces to decouple user and provider of an API
 * is that at some point a specific implementation has to be instantiated."
 * - Đọc kỹ chỗ tinh tế này. Bạn tách interface {@code PaymentGateway} ra một module, cài
 * đặt {@code StripeGateway} ở module khác - trông thì đã tách rời. Nhưng ở đâu đó vẫn phải
 * có dòng {@code new StripeGateway()}, và dòng đó kéo theo một {@code requires} tới module
 * cài đặt. Mũi tên phụ thuộc vẫn còn nguyên, chỉ bị đẩy đi chỗ khác.
 * - Service giải đúng chỗ đó: người tiêu thụ KHÔNG BAO GIỜ viết {@code new}, mà hỏi hệ
 * thống "ai cài đặt interface này?".
 * <p>
 * 2. HAI DIRECTIVE
 * - Phía TIÊU THỤ khai {@code uses}:
 * <pre>
 *   module com.demo.app {
 *       requires com.demo.spi;          // chỉ phụ thuộc interface
 *       uses     com.demo.spi.PaymentGateway;
 *   }
 * </pre>
 * - Phía CUNG CẤP khai {@code provides ... with}:
 * <pre>
 *   module com.demo.stripe {
 *       requires  com.demo.spi;
 *       provides  com.demo.spi.PaymentGateway
 *            with com.demo.stripe.internal.StripeGateway;
 *   }
 * </pre>
 * - CHÚ Ý điểm quan trọng nhất của cả bài: trong cả hai file trên KHÔNG có dòng nào nối
 * {@code com.demo.app} với {@code com.demo.stripe}. Hai module không biết nhau tồn tại.
 * Cả hai chỉ cùng biết module interface.
 * <p>
 * 3. CÁI ĐƯỢC KHAI SAU CHỮ WITH PHẢI LÀ GÌ
 * - Trang gốc cho hai lựa chọn:
 * + (1) Một lớp cụ thể cài đặt service, có "a public, parameterless constructor (called a
 * provider constructor)".
 * + (2) Một kiểu bất kỳ có "a public, static, parameterless method provider that returns a
 * type that extends or implements" service đó - gọi là PROVIDER METHOD.
 * - Khi nào cần dạng (2): khi việc tạo object cần logic - đọc cấu hình, trả về singleton,
 * chọn cài đặt theo môi trường. Vì constructor rỗng không làm được những việc đó.
 * <pre>
 *   public class GatewayFactory {
 *       public static PaymentGateway provider() {
 *           return CONFIG.isSandbox() ? new FakeGateway() : new StripeGateway();
 *       }
 *   }
 * </pre>
 * - Tên method bắt buộc đúng chữ {@code provider}, bắt buộc {@code public static}, bắt
 * buộc không tham số. Sai một trong ba là lỗi lúc biên dịch module-info.
 * <p>
 * 4. SERVICELOADER - PHÍA TIÊU THỤ LẤY HÀNG
 * - Lúc chạy, gọi {@code ServiceLoader.load(PaymentGateway.class)}, trả về một
 * {@code ServiceLoader} mà "you can use in various ways to get access to the service
 * providers."
 * - Ba cách dùng hay gặp:
 * <pre>
 *   // lấy hết
 *   for (PaymentGateway g : ServiceLoader.load(PaymentGateway.class)) { ... }
 *
 *   // lấy cái đầu tiên, chấp nhận không có
 *   Optional&lt;PaymentGateway&gt; first =
 *       ServiceLoader.load(PaymentGateway.class).findFirst();
 *
 *   // lọc theo điều kiện mà KHÔNG khởi tạo object (stream of Provider)
 *   ServiceLoader.load(PaymentGateway.class).stream()
 *       .filter(p -&gt; p.type().getSimpleName().startsWith("Stripe"))
 *       .map(java.util.ServiceLoader.Provider::get)
 *       .findFirst();
 * </pre>
 * - Điểm đáng nhớ ở cách thứ ba: {@code stream()} cho {@code Provider} - bạn xem được
 * {@code type()} TRƯỚC khi {@code get()} tạo instance. Hữu ích khi việc khởi tạo đắt.
 * - Và nhớ: {@code ServiceLoader} KHÔNG bảo đảm thứ tự các provider. Đừng viết code phụ
 * thuộc vào việc cái nào ra trước.
 * <p>
 * 5. SERVICE BINDING - MODULE SYSTEM TỰ KÉO PROVIDER VÀO
 * - Nguyên văn: "once it resolves a module that uses a service, it also adds all modules to
 * the graph that provide that service."
 * - Đây là ngoại lệ với luật ở t1 mục 9 (chỉ module được {@code requires} tới mới vào
 * graph). Module cung cấp service lọt vào graph dù KHÔNG ai requires nó - đúng như thiết
 * kế, vì nếu không thì cơ chế này vô dụng.
 * - Lợi ích phụ mà trang gốc nhấn: module cung cấp KHÔNG cần export package chứa lớp cài
 * đặt. Module system tự khởi tạo nó, không ai cần nhìn thấy nó. API công khai của module
 * cung cấp có thể bằng không.
 * - Gộp lại thành một câu đáng nhớ: service cho phép một module ĐÓNG KÍN HOÀN TOÀN mà vẫn
 * đóng góp được chức năng cho hệ thống.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. SERVICELOADER CÓ TRƯỚC JPMS - HAI CÁCH KHAI BÁO
 * - {@code ServiceLoader} có từ Java 6, dùng file text trong
 * {@code META-INF/services/ten.day.du.cua.Interface}, mỗi dòng một tên lớp cài đặt.
 * - Từ Java 9, có thêm cách khai bằng {@code provides ... with} trong module-info.
 * - Luật chọn cách, cần nhớ để khỏi debug mò:
 * + JAR trên MODULE PATH -> module system đọc {@code module-info}, file
 * {@code META-INF/services} bị LỜ ĐI.
 * + JAR trên CLASS PATH -> ngược lại, chỉ {@code META-INF/services} có tác dụng.
 * - Vì vậy một thư viện muốn chạy được cả hai nơi phải khai CẢ HAI. Đây là lý do bạn vẫn
 * thấy thư mục {@code META-INF/services} trong nhiều thư viện hiện đại.
 * <p>
 * 7. NHỮNG CHỖ JDK TỰ DÙNG SERVICE - ĐỂ THẤY NÓ KHÔNG PHẢI ĐỒ CHƠI
 * - JDBC: {@code java.sql.Driver} là service. Đây là lý do từ Java 6 bạn không còn phải gọi
 * {@code Class.forName("com.mysql.jdbc.Driver")} nữa - {@code DriverManager} dùng
 * {@code ServiceLoader} tự tìm.
 * - Logging: {@code System.LoggerFinder} (Java 9+) cho phép thay toàn bộ backend log của
 * JDK bằng cách thả một JAR vào.
 * - Charset, locale, file system provider ({@code java.nio.file.spi.FileSystemProvider})...
 * - Nhìn vào đây để nhớ công thức: hậu tố {@code ...Provider}, {@code ...Factory},
 * {@code ...Finder} trong JDK gần như luôn là dấu hiệu của một service.
 * <p>
 * 8. SO SÁNH VỚI SPRING DI - BIẾT ĐỂ KHỎI NHẦM VAI
 * - Giống: cả hai đều là inversion of control, người dùng không {@code new}.
 * - Khác, và đây là chỗ quyết định chọn cái nào:
 * + Phạm vi: ServiceLoader chỉ nối interface với cài đặt. Spring còn quản vòng đời, scope,
 * AOP, transaction, cấu hình.
 * + Thời điểm: ServiceLoader quét lúc gọi {@code load()}. Spring dựng context lúc khởi động.
 * + Ràng buộc: ServiceLoader không có khái niệm "bean này tiêm vào bean kia".
 * - Kết luận thực tế: dùng ServiceLoader cho ĐIỂM MỞ RỘNG của thư viện (plugin, driver,
 * adapter). Dùng Spring cho dây nối bên trong một ứng dụng. Hai thứ không cạnh tranh nhau.
 * <p>
 * 9. BẮC CẦU TỪ TYPESCRIPT
 * - Không có cơ chế tương đương sẵn trong Node. Cách người ta làm cùng việc đó:
 * + Quét {@code package.json} theo quy ước tên ({@code eslint-plugin-*}, {@code babel-preset-*}).
 * + Hoặc khai tay trong file cấu hình ({@code plugins: ["..."]}).
 * - Khác biệt đáng nhớ: Node dựa vào QUY ƯỚC TÊN và {@code require} động; Java dựa vào
 * KHAI BÁO được trình biên dịch kiểm tra. Khai sai tên lớp trong {@code provides ... with}
 * là lỗi biên dịch; viết sai tên plugin trong file config JS thì chỉ biết lúc chạy.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao chỉ tách interface ra module riêng là CHƯA đủ để tách rời?
 * - Module tiêu thụ khai gì, module cung cấp khai gì? Hai module đó có nhắc tới nhau không?
 * - Hai dạng hợp lệ của phần sau chữ {@code with}? Provider method phải thỏa ba điều gì?
 * - Service binding là gì, và nó phá luật nào ở t1 mục 9?
 * - Vì sao module cung cấp không cần export package chứa lớp cài đặt?
 * - JAR nằm trên class path thì {@code provides} trong module-info có tác dụng không?
 * - {@code ServiceLoader} có bảo đảm thứ tự provider không?
 * - Khi nào chọn ServiceLoader, khi nào chọn Spring DI?
 */
public class t6_DecouplingModulesWithServices {

    void main() {
        // 1. Một service có sẵn trong JDK, chạy được ngay không cần cấu hình gì (mục 4).
        //    FileSystemProvider là interface; các cài đặt nằm ở module khác và được khai
        //    bằng directive `provides` trong module-info của chúng.
        ServiceLoader<FileSystemProvider> loader = ServiceLoader.load(FileSystemProvider.class);
        System.out.println("Các cài đặt của FileSystemProvider tìm được:");
        loader.stream().forEach(p -> System.out.println(
                "   " + p.type().getName() + "   (module: " + p.type().getModule().getName() + ")"));

        // 2. stream() cho Provider - xem được type() TRƯỚC khi get() tạo instance (mục 4)
        System.out.println("\nlấy instance đầu tiên:");
        loader.stream().findFirst().ifPresent(p -> {
            System.out.println("   type() trước khi khởi tạo : " + p.type().getSimpleName());
            FileSystemProvider fsp = p.get();
            System.out.println("   get()  rồi, scheme        : " + fsp.getScheme());
        });

        // 3. Bên tiêu thụ chỉ cần biết INTERFACE. Xem java.base khai gì (mục 2, mục 5)
        var base = String.class.getModule().getDescriptor();
        base.provides().stream()
                .filter(p -> p.service().contains("FileSystemProvider"))
                .forEach(p -> System.out.println(
                        "\njava.base provides " + p.service() + "\n     with " + p.providers()));

        // 4. Phía dùng khai `uses` - ví dụ java.sql dùng Driver (mục 2)
        ModuleLayer.boot().findModule("java.sql").orElseThrow().getDescriptor()
                .uses().forEach(u -> System.out.println("java.sql uses " + u));
        System.out.println("   -> đây là lý do không cần Class.forName(\"...Driver\") nữa (mục 7)");

        System.out.println();
        // Tiếp tục dùng sandbox đã tạo ở bài t1.
        //
        // TODO 1 (mục 2): Dựng bộ ba module:
        //     demo.spi     - exports com.demo.spi, chứa interface Greeter { String greet(); }
        //     demo.vi      - provides com.demo.spi.Greeter with ...ViGreeter  (KHÔNG exports)
        //     demo.app     - requires demo.spi; uses com.demo.spi.Greeter
        //   demo.app in ra lời chào lấy từ ServiceLoader. Xác nhận module-info của demo.app
        //   KHÔNG hề nhắc tới demo.vi.
        //
        // TODO 2 (mục 5): Viết thêm demo.en provides cùng interface. Chạy lại mà KHÔNG sửa
        //   demo.app. Xác nhận ServiceLoader giờ trả về 2 provider. Đây là toàn bộ giá trị
        //   của bài này - thêm tính năng bằng cách thêm JAR.
        //
        // TODO 3 (mục 5): Bỏ demo.vi khỏi module path rồi chạy. Ứng dụng có chết không?
        //   Rồi thử ngược lại: để demo.vi trên module path nhưng XÓA directive `uses` của
        //   demo.app. In ra ModuleLayer.boot().modules() - demo.vi có trong graph không?
        //   Giải thích bằng khái niệm service binding.
        //
        // TODO 4 (mục 3): Đổi demo.en sang dạng PROVIDER METHOD: một lớp GreeterFactory có
        //   `public static Greeter provider()` trả về cài đặt khác nhau tùy biến môi trường.
        //   Sau đó cố tình làm sai lần lượt ba điều kiện (bỏ static / thêm tham số / đổi tên
        //   method) và chép lại ba thông báo lỗi javac.
        //
        // TODO 5 (mục 4): Dùng cả ba cách lấy provider: vòng for, findFirst(), và stream()
        //   với Provider.type(). Ở cách thứ ba, in ra type() TRƯỚC khi gọi get(), và thêm
        //   một câu println trong constructor của provider để nhìn thấy nó CHƯA được tạo.
        //
        // TODO 6 (mục 6): Lấy demo.vi, bỏ module-info, thay bằng file
        //   META-INF/services/com.demo.spi.Greeter. Chạy ứng dụng ở hai chế độ: JAR trên
        //   module path và JAR trên class path. Ghi lại: chế độ nào tìm thấy provider?
        //   Sau đó khai CẢ HAI cách và xác nhận chạy được ở cả hai nơi.
        //
        // TODO 7 (mục 7): Chạy `java --describe-module java.sql` và tìm dòng `uses`. Rồi
        //   mở JAR driver PostgreSQL/MySQL trong ~/.m2 xem nó khai provides ở đâu
        //   (module-info hay META-INF/services, hay cả hai).
    }
}
