package com.javacore.organizing.modules;

/**
 * Extending the Module Graph with --add-modules and --add-reads
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 12/12 - bài CUỐI của section Modules.
 * - Hai bài cuối là một cặp đối xứng rất đẹp, nên nhớ theo cặp:
 * + t11 ({@code --add-exports} / {@code --add-opens}) sửa vấn đề TRUY CẬP: module có trong
 * graph rồi, nhưng nó không mở package ra.
 * + t12 ({@code --add-modules} / {@code --add-reads}) sửa vấn đề KẾT NỐI: module chưa vào
 * graph, hoặc đã vào nhưng chưa có cạnh đọc tới nó.
 * - Một câu cho cả bài: {@code --add-modules} thêm NODE vào đồ thị, {@code --add-reads}
 * thêm CẠNH. Nhớ đúng một câu này là nhớ cả bài.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/add-modules-reads/">dev.java - Extending the Module Graph</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - ADD-MODULES VÀ ADD-READS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. --ADD-MODULES: THÊM VÀO TẬP ROOT MODULE
 * - Cú pháp: {@code --add-modules MODULES} - "accepts a comma-separated list of modules" và
 * "adds them to the set of root modules". Dạng dấu bằng {@code --add-modules=...} cũng được.
 * - Có trên BA công cụ: {@code javac}, {@code java}, và {@code jlink}.
 * - Nhắc lại nền tảng từ t1 mục 9: module resolution bắt đầu từ tập ROOT MODULE rồi đi theo
 * các {@code requires}. Module nào không ai trỏ tới thì không được nạp. Cờ này chính là cách
 * tiêm thêm điểm xuất phát.
 * - Hai tình huống cần nó, theo trang gốc:
 * + Đưa một phụ thuộc TÙY CHỌN vào graph - đúng chỗ đã treo ở bài t3 mục 2.
 * + Chỉ định root module khi tạo runtime image bằng {@code jlink} - dẫn sang bước 31.
 * <p>
 * 2. BA GIÁ TRỊ ĐẶC BIỆT CỦA --ADD-MODULES
 * <p>
 * - 2a. {@code ALL-DEFAULT}
 * + Nguyên văn: "the set of modules that is picked as root modules when launching code from
 * the class path."
 * + Tức là lấy đúng tập mặc định mà JVM dùng khi chạy từ class path (t7 mục 5).
 * + Trường hợp dùng, nguyên văn: "useful when the application is a container that hosts
 * other applications which can, in turn, depend upon modules not required by the container
 * itself." Hình dung: application server chạy các ứng dụng của người khác, không biết
 * trước chúng cần module nào.
 * + CHỈ dùng được lúc chạy.
 * <p>
 * - 2b. {@code ALL-SYSTEM}
 * + "Adds all system modules to the root set, which is sometimes needed by test harnesses."
 * + Trang gốc kèm luôn lời can: cách này "will cause many modules to be resolved; in
 * general, ALL-DEFAULT should be preferred."
 * + CHỈ dùng được lúc chạy.
 * <p>
 * - 2c. {@code ALL-MODULE-PATH}
 * + "Adds all modules found on the module path to the root set."
 * + "provided for use by build tools such as Maven" và là "a convenient means to add
 * automatic modules to the root set."
 * + Đây là giá trị bạn sẽ gặp trong log build nhiều nhất - vì build tool không muốn suy
 * nghĩ xem module nào cần, nó nạp hết.
 * <p>
 * - Cách nhớ ba giá trị: DEFAULT = như mặc định, SYSTEM = mọi module của JDK,
 * MODULE-PATH = mọi module bạn đưa vào.
 * <p>
 * 3. --ADD-READS: THÊM CẠNH ĐỌC
 * - Cú pháp: {@code --add-reads MODULE=TARGETS} - dùng được cho cả trình biên dịch lẫn lúc
 * chạy. Dạng dấu bằng cũng hợp lệ.
 * - Ngữ nghĩa: "adds readability edges from MODULE to all modules in the comma-separated
 * list TARGETS."
 * - Tác dụng: MODULE có thể "access all public types in packages exported by those modules
 * even though MODULE has no requires clauses mentioning them."
 * + Đọc kỹ vế cuối: nó bù đúng cái thiếu của một {@code requires}, mà không cần sửa
 * {@code module-info.java} - tiện khi file đó thuộc về thư viện của người khác.
 * - Giá trị đặc biệt: nếu TARGETS là {@code ALL-UNNAMED} thì module đó "can even read the
 * unnamed module."
 * + Đây là NGOẠI LỆ DUY NHẤT cho luật một chiều ở t7 mục 4. Bình thường module tường minh
 * không bao giờ đọc được unnamed module; cờ này mở được đúng chiều đó.
 * <p>
 * 4. BỐN CỜ CỦA HAI BÀI CUỐI - BẢNG TỔNG HỢP
 * <pre>
 *   Cờ               Thêm cái gì            Sửa triệu chứng gì
 *   --add-modules    node vào graph         "module not found" / module không được nạp
 *   --add-reads      cạnh đọc               "package P is declared in module M,
 *                                            but module X does not read it"
 *   --add-exports    quyền truy cập public  "package P is not exported"
 *   --add-opens      quyền reflection       "module M does not opens P"
 * </pre>
 * - Cách dùng bảng khi gặp lỗi thật: đọc thông báo, tìm đúng dòng, dùng đúng cờ. Dùng sai
 * cờ là trường hợp phổ biến nhất của "đã thêm cờ rồi mà vẫn lỗi".
 * <p>
 * 5. THỨ TỰ CHẨN ĐOÁN - TỪ DƯỚI LÊN
 * - Bốn cờ trên có thứ bậc, phải sửa theo đúng thứ tự, vì cái sau vô nghĩa nếu cái trước
 * chưa xong:
 * + (1) Module có trong graph chưa? -&gt; {@code --add-modules}
 * + (2) Bên gọi có đọc được nó không? -&gt; {@code --add-reads}
 * + (3) Package có được mở cho biên dịch/gọi thẳng không? -&gt; {@code --add-exports}
 * + (4) Package có mở cho reflection không? -&gt; {@code --add-opens}
 * - Thêm {@code --add-opens} khi module còn chưa vào graph thì không bao giờ có tác dụng -
 * và đó chính là lúc người ta bắt đầu dán bừa cờ từ StackOverflow.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. TÌNH HUỐNG THẬT HAY GẶP NHẤT VỚI --ADD-MODULES
 * - Java 11 gỡ các module Java EE ra khỏi JDK ({@code java.xml.bind}, {@code java.activation},
 * {@code java.transaction}...). Trước đó, ở Java 9-10, chúng vẫn còn nhưng KHÔNG nằm trong
 * tập root mặc định - nên code cũ chạy trên Java 9 phải thêm
 * {@code --add-modules java.xml.bind} mới chạy.
 * - Bài học rút ra vẫn còn giá trị: {@code --add-modules} là thuốc cho giai đoạn CHUYỂN
 * TIẾP. Khi module bị gỡ hẳn khỏi JDK thì cờ hết tác dụng, phải thêm thư viện thay thế.
 * - Thực hành được ngay: so sánh {@code java --list-modules} trên JDK 25 với danh sách
 * module của JDK 8/11 để thấy những cái đã biến mất (TODO 1).
 * <p>
 * 7. KHI NÀO THỰC SỰ CẦN --ADD-READS
 * - Hiếm hơn ba cờ kia nhiều. Hai tình huống chính:
 * + Test: module test cần đọc một module mà module chính không khai. Build tool thường tự
 * sinh cờ này.
 * + Vá tạm một thư viện có {@code module-info} khai thiếu {@code requires transitive}
 * (đúng lỗi ở bài t4 mục 6). Bạn không sửa được file của họ, nên thêm cạnh từ ngoài.
 * - Nhớ: nó thêm cạnh ĐỌC chứ không mở package. Nếu module đích không {@code exports} thì
 * vẫn phải kèm {@code --add-exports}. Đây là hệ quả trực tiếp của ba điều kiện ở t1 mục 11.
 * <p>
 * 8. TỔNG KẾT CẢ SECTION MODULES - 12 BÀI TRONG MỘT TRANG
 * - NỀN TẢNG (t1): module = tên + requires + exports + services. Ba điều kiện truy cập:
 * public + package được exports + bên gọi reads. Reliable configuration kiểm tra lúc KHỞI
 * ĐỘNG. Và câu quan trọng nhất: JAR là module hay không phụ thuộc vào CHỖ BẠN ĐẶT NÓ.
 * - TINH CHỈNH KHAI BÁO (t2-t5): {@code opens} cho reflection (mở sâu, chỉ lúc chạy);
 * {@code requires static} cho phụ thuộc tùy chọn; {@code requires transitive} khi kiểu lộ
 * ra trong API; {@code exports to} khi chia sẻ nội bộ giữa các module cùng nhà.
 * - TÁCH RỜI (t6): service + {@code ServiceLoader} = DI có sẵn trong JVM, module cung cấp
 * không cần export gì cả.
 * - DI CƯ (t7-t8): class path -&gt; unnamed module (không tên, đọc hết, mở hết);
 * plain JAR trên module path -&gt; automatic module (có tên, đọc được cả unnamed).
 * Chiến lược: modular hóa từ dưới lên.
 * - DÒNG LỆNH (t9-t12): ba lệnh javac/jar/java; JDK đóng cứng ruột từ Java 17; bốn cờ phá
 * rào theo đúng thứ tự chẩn đoán ở mục 5.
 * - CÂU CUỐI ĐỂ MANG ĐI: trong đời đi làm, bạn sẽ hiếm khi VIẾT {@code module-info.java},
 * nhưng sẽ thường xuyên phải ĐỌC thông báo lỗi do module system sinh ra. Section này đáng
 * học vì vế thứ hai.
 * <p>
 * 9. BƯỚC TIẾP THEO - JLINK (BƯỚC 31)
 * - {@code jlink} là lý do thực dụng nhất để modular hóa: đóng gói một runtime chỉ gồm
 * module thật sự dùng. Nó nhận {@code --add-modules} - chính cờ ở mục 1 - để chỉ định root
 * module cho image.
 * - Vì vậy bài t12 này nối thẳng sang bước 31, không đứt mạch.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Một câu phân biệt {@code --add-modules} với {@code --add-reads}?
 * - Ba giá trị đặc biệt của {@code --add-modules}, giá trị nào dành cho build tool?
 * - Vì sao trang gốc khuyên ưu tiên {@code ALL-DEFAULT} hơn {@code ALL-SYSTEM}?
 * - {@code --add-reads M=ALL-UNNAMED} phá luật nào đã học ở bài t7?
 * - Bốn cờ của hai bài cuối, mỗi cờ sửa triệu chứng gì?
 * - Thứ tự chẩn đoán bốn bước, và vì sao không được đảo?
 * - {@code --add-reads} có mở package không? Nếu không thì cần kèm cờ nào?
 * - Nhắc lại ba điều kiện truy cập ở t1 mục 11 - và chỉ ra cờ nào ứng với điều kiện nào.
 */
public class t12_ExtendingTheModuleGraph {

    void main() {
        Module here = getClass().getModule();

        // 1. Đồ thị hiện tại: bao nhiêu NODE (mục 1)
        System.out.println("boot layer có " + ModuleLayer.boot().modules().size() + " module");
        System.out.println("JDK này có tổng cộng "
                + java.lang.module.ModuleFinder.ofSystem().findAll().size()
                + " module  <- phần chênh lệch là những module KHÔNG được resolve");

        // 2. Một module có trong JDK nhưng KHÔNG vào tập root mặc định (mục 2a)
        boolean coJavaSe = ModuleLayer.boot().findModule("java.se").isPresent();
        System.out.println("\njava.se có trong boot layer ? " + coJavaSe);
        if (!coJavaSe) {
            System.out.println("   java.se là aggregator, không exports package nào nên không"
                    + " vào tập root mặc định. Thêm NODE bằng:");
            System.out.println("   java --add-modules java.se -cp <classpath> "
                    + getClass().getName());
        } else {
            System.out.println("   -> đang chạy có --add-modules java.se, node đã được thêm.");
        }

        // 3. Có node rồi vẫn cần CẠNH: đọc được hay không là chuyện khác (mục 3, mục 5)
        Module sql = ModuleLayer.boot().findModule("java.sql").orElseThrow();
        System.out.println("\njava.sql có trong graph ? true");
        System.out.println("ta đọc được java.sql     ? " + here.canRead(sql)
                + "   <- unnamed module đọc được tất cả (t7)");
        System.out.println("java.sql đọc được ta     ? " + sql.canRead(here)
                + "   <- cần --add-reads java.sql=ALL-UNNAMED mới thành true (mục 3)");

        // 4. Và có cạnh rồi vẫn cần QUYỀN: ba tầng tách bạch (mục 5)
        System.out.println("\nta đọc được java.base, nhưng:");
        System.out.println("   exports jdk.internal.misc ? "
                + String.class.getModule().isExported("jdk.internal.misc")
                + "   <- tầng 3, cần --add-exports");

        System.out.println();
        // TODO 1 (mục 1, 6): Chạy `java --list-modules` trên JDK 25, tìm xem các module
        //   java.xml.bind / java.activation / java.transaction còn không. Tra xem thư viện
        //   nào thay thế chúng ngày nay (gợi ý: jakarta.xml.bind-api). Ghi vào note.
        //
        // TODO 2 (mục 1): Quay lại TODO 4 bài t3 (requires static). Dùng --add-modules để
        //   kéo module tùy chọn vào graph, in ModuleLayer.boot().modules() trước và sau.
        //   Đây là chỗ bài t3 đã hẹn sẽ quay lại.
        //
        // TODO 3 (mục 2c): Đặt 3 plain JAR lên module path. Chạy với và không có
        //   --add-modules ALL-MODULE-PATH, in danh sách module được nạp mỗi lần. Đối chiếu
        //   với hành vi "kéo cả cụm" của automatic module ở t8 mục 4 - hai cơ chế này khác
        //   nhau ở điểm nào?
        //
        // TODO 4 (mục 3): Dựng tình huống thiếu cạnh đọc: demo.app dùng kiểu của demo.model
        //   nhưng KHÔNG khai requires. Biên dịch -> lỗi. Sửa bằng
        //   --add-reads demo.app=demo.model (KHÔNG sửa module-info). Xác nhận qua được.
        //
        // TODO 5 (mục 3, 7): Cho một module tường minh đọc code đang nằm trên class path
        //   bằng --add-reads ten.module=ALL-UNNAMED. Xác nhận chạy được - đây là ngoại lệ
        //   duy nhất cho luật một chiều ở t7. Rồi thử bỏ cờ để thấy lỗi quay lại.
        //
        // TODO 6 (mục 7): Dựng tiếp từ TODO 4: cho demo.model KHÔNG exports package đang
        //   dùng. Xác nhận --add-reads một mình không đủ, phải kèm --add-exports. Ghi lại
        //   cả hai thông báo lỗi để thấy chúng khác nhau thế nào.
        //
        // TODO 7 (mục 5): Bài tập chẩn đoán tổng hợp. Cố tình dựng một tình huống sai CẢ
        //   BỐN tầng cùng lúc, rồi sửa lần lượt theo đúng thứ tự mục 5. Sau mỗi bước ghi
        //   lại thông báo lỗi MỚI - bạn sẽ thấy lỗi "tiến" dần lên từng tầng. Đây là bài
        //   tập giá trị nhất của cả section.
        //
        // TODO 8 (mục 8): Gấp tài liệu lại, viết bằng trí nhớ phần tổng kết ở mục 8 vào một
        //   trang giấy. Chỗ nào viết không ra thì mở đúng bài đó đọc lại.
    }
}
