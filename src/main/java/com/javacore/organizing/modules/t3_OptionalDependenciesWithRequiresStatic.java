package com.javacore.organizing.modules;

import java.util.Optional;

/**
 * Optional Dependencies with requires static
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 3/12 của section Modules.
 * - Bài đầu tiên trong ba bài tinh chỉnh {@code requires} (t3: static, t4: transitive, t5:
 * qualified exports/opens).
 * - Một câu cho cả bài: {@code requires static} nghĩa là "lúc biên dịch tôi CẦN module này,
 * lúc chạy thì có cũng được không có cũng xong" - và cái giá phải trả là bạn tự chịu trách
 * nhiệm đừng để {@code NoClassDefFoundError} nổ.
 * - Đây là directive của người viết THƯ VIỆN, không phải của người viết ứng dụng. Nếu bạn
 * chưa từng viết thư viện, hãy đọc nó như cách để HIỂU thư viện mình đang dùng.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/optional-dependencies/">dev.java - Optional Dependencies with requires static</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - OPTIONAL DEPENDENCIES (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. HAI THỜI ĐIỂM, HAI LUẬT KHÁC NHAU
 * - Cú pháp:
 * <pre>
 *   module com.demo.lib {
 *       requires static java.sql;
 *   }
 * </pre>
 * - Lúc BIÊN DỊCH: module được nhắc tới BẮT BUỘC phải có mặt, và module khai báo đọc được
 * nó bình thường. Không khác gì {@code requires} thường.
 * - Lúc CHẠY: module đó "might be absent and that will cause neither error nor warning."
 * + Đọc kỹ: KHÔNG lỗi, KHÔNG cả cảnh báo. JVM im lặng khởi động như không có chuyện gì.
 * - Đây là chỗ ngược với trực giác: thường ta nghĩ thiếu phụ thuộc thì phải báo. Ở đây JPMS
 * cố tình im, vì bạn đã tuyên bố "không bắt buộc".
 * <p>
 * 2. MODULE RESOLUTION BỎ QUA HOÀN TOÀN
 * - Nguyên văn, đây là câu quan trọng nhất bài: khi gặp {@code requires static}, hệ thống
 * "does not try to fulfill it, meaning it does not even check whether the referenced module
 * can be found."
 * - Chữ NOT EVEN CHECK rất mạnh: không phải "tìm không thấy thì thôi", mà là KHÔNG TÌM.
 * - Hệ quả: module tùy chọn chỉ lọt vào module graph nếu một trong hai điều sau xảy ra:
 * + nó là phụ thuộc THƯỜNG của một module khác đang được resolve, hoặc
 * + nó được thêm tay bằng cờ {@code --add-modules} (bài t12).
 * - Và một khi đã vào được graph bằng hai đường trên, nó hoạt động y hệt phụ thuộc thường.
 * - Nhớ cặp này: {@code requires static} quyết định LUẬT BIÊN DỊCH; việc module có mặt lúc
 * chạy hay không do NGƯỜI KHÁC quyết định, không phải do bạn.
 * <p>
 * 3. CÁI GIÁ - NOCLASSDEFFOUNDERROR
 * - Nếu code chạm tới một kiểu thuộc module vắng mặt, JVM vẫn cố nạp class đó và thất bại,
 * ném {@code NoClassDefFoundError}.
 * - Trang gốc mô tả hậu quả, nguyên văn: lỗi này có thể "crash the application or at least
 * fail out of the chunk of logic that was being executed."
 * - Chú ý nó là ERROR chứ không phải Exception - không ai bắt, và nó nổ ở ĐÚNG lúc dòng
 * code đó chạy, có thể là nhiều giờ sau khi khởi động. Đây chính là loại lỗi muộn mà t1
 * mục 12 nói module system sinh ra để TRÁNH. Dùng {@code requires static} là tự nguyện từ
 * bỏ lớp bảo vệ đó cho riêng phụ thuộc này.
 * <p>
 * 4. HAI CÁCH VIẾT CODE CHO AN TOÀN
 * <p>
 * - 4a. KIỂM TRA MODULE CÓ MẶT HAY KHÔNG
 * + Hỏi thẳng module system xem module kia có trong graph không, rồi mới dùng.
 * + Dùng {@code ModuleLayer.boot().findModule("ten.module").isPresent()}. Code trong nhánh
 * đó mới được chạm tới kiểu của module kia.
 * + Lưu ý kỹ thuật quan trọng: phải TÁCH phần code dùng kiểu đó ra lớp riêng. Nếu để chung
 * một lớp, việc nạp chính lớp đó đã có thể kéo theo việc phân giải kiểu vắng mặt.
 * <p>
 * - 4b. DỰA VÀO QUAN HỆ PHỤ THUỘC ĐÃ CÓ SẴN
 * + Giả định module có mặt, NẾU phần code đó chỉ bao giờ được gọi từ code vốn đã phụ thuộc
 * vào module kia.
 * + Ví dụ của trang gốc: phần code liên quan JDBC trong một thư viện có thể yên tâm là
 * {@code java.sql} có mặt - vì chỉ người đang dùng JDBC mới gọi tới phần đó, mà họ thì
 * chắc chắn đã có {@code java.sql}.
 * + Đây là cách phổ biến hơn trong thực tế, vì không phải viết code phòng thủ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. KHI NÀO MỘT THƯ VIỆN THẬT SỰ CẦN REQUIRES STATIC
 * - Ba tình huống kinh điển:
 * + Thư viện có tính năng PHỤ chỉ bật khi có thư viện khác: ví dụ một thư viện log tự động
 * đẩy sang JMX nếu {@code java.management} có mặt.
 * + Annotation chỉ dùng lúc biên dịch: các annotation có
 * {@code RetentionPolicy.SOURCE}/{@code CLASS} (null-checker, annotation processor) không
 * tồn tại lúc chạy, nên module chứa chúng là ứng viên hoàn hảo.
 * + Tích hợp tùy chọn với nhiều framework: thư viện hỗ trợ cả Jackson lẫn Gson, người dùng
 * chỉ có một trong hai.
 * - Dấu hiệu bạn ĐANG DÙNG SAI: ứng dụng (không phải thư viện) mà khai
 * {@code requires static}. Ứng dụng biết chính xác môi trường chạy của mình, không có lý
 * do gì để phụ thuộc tùy chọn.
 * <p>
 * 6. BẮC CẦU TỪ TYPESCRIPT/NPM
 * - Tương đương gần như một-một: {@code peerDependenciesMeta.optional} và
 * {@code optionalDependencies} trong {@code package.json}.
 * - Mẫu code quen thuộc bên Node:
 * <pre>
 *   let chalk; try { chalk = require("chalk"); } catch { chalk = null; }
 * </pre>
 * - Java không có mẫu try/catch quanh import, nên thay bằng hai cách ở mục 4. Khác biệt
 * cốt lõi: Node phát hiện lúc {@code require}, Java phát hiện lúc NẠP CLASS - mà thời điểm
 * nạp class thì khó đoán hơn nhiều, đó là lý do phải tách lớp ở mục 4a.
 * <p>
 * 7. SO SÁNH VỚI MAVEN SCOPE - ĐỪNG NHẦM
 * - {@code requires static} trông giống Maven {@code provided} nhưng KHÔNG phải một thứ:
 * + Maven scope quyết định JAR có nằm trong classpath/modulepath hay không - đó là chuyện
 * của BUILD TOOL.
 * + {@code requires static} quyết định module system xử lý ra sao lúc resolve - đó là
 * chuyện của JVM.
 * - Trong thực tế hai thứ này đi cùng nhau: khai {@code provided} trong pom để JAR không bị
 * đóng gói đi kèm, và {@code requires static} trong module-info để JVM không đòi nó. Thiếu
 * một trong hai là sai.
 * <p>
 * 8. LIÊN HỆ SANG CÁC BÀI SAU
 * - Cách ĐƯA một module tùy chọn vào graph lúc chạy: {@code --add-modules}, bài t12.
 * - Phụ thuộc kiểu ngược lại (bắt buộc và còn lan sang người dùng): {@code requires
 * transitive}, bài t4 ngay sau đây. Hai directive này nằm ở hai cực: {@code static} yếu đi,
 * {@code transitive} mạnh thêm.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Lúc biên dịch, {@code requires static} khác {@code requires} thường ở điểm nào?
 * - Lúc resolve, module system làm gì với một {@code requires static}? Câu "not even check"
 * nghĩa là gì?
 * - Hai con đường để một module tùy chọn vẫn lọt vào module graph?
 * - Lỗi gì sẽ xảy ra nếu code chạm tới kiểu của module vắng mặt? Error hay Exception, và
 * tại sao phân biệt đó quan trọng?
 * - Hai cách viết code an toàn? Vì sao cách 4a phải tách ra lớp riêng?
 * - Vì sao {@code requires static} là chuyện của thư viện chứ không phải của ứng dụng?
 */
public class t3_OptionalDependenciesWithRequiresStatic {

    void main() {
        // 1. Cách hỏi module system "module kia có mặt không" - chính là cách 4a (mục 4)
        Optional<Module> coThat = ModuleLayer.boot().findModule("java.sql");
        Optional<Module> khongCo = ModuleLayer.boot().findModule("com.khong.he.ton.tai");
        System.out.println("findModule(java.sql)            -> " + coThat.isPresent());
        System.out.println("findModule(com.khong.he.ton.tai)-> " + khongCo.isPresent());

        // 2. Mẫu code an toàn: chỉ chạm vào kiểu của module kia khi nó CÓ MẶT (mục 4a)
        if (coThat.isPresent()) {
            // Trong dự án thật, dòng dưới phải nằm ở một LỚP RIÊNG - xem cảnh báo mục 4a:
            // nếu để chung lớp, việc nạp chính lớp này đã có thể kéo theo kiểu vắng mặt.
            System.out.println("\njava.sql có mặt -> bật nhánh tính năng JDBC");
        } else {
            System.out.println("\njava.sql vắng mặt -> bỏ qua nhánh JDBC, KHÔNG ném lỗi");
        }

        // 3. Vì sao phải kiểm tra: chạm vào kiểu vắng mặt là Error, không ai bắt (mục 3)
        try {
            Class.forName("com.mot.thu.vien.khong.co.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("\nnạp lớp vắng mặt -> " + e.getClass().getSimpleName());
            System.out.println("   -> ở đây là Exception vì ta chủ động nạp;"
                    + " còn khi JVM tự nạp lúc chạy code thì là NoClassDefFoundError");
        }

        System.out.println();
        // Tiếp tục dùng sandbox đã tạo ở bài t1.
        //
        // TODO 1 (mục 1, 2): Tạo demo.lib với `requires static demo.extra;` và một lớp dùng
        //   kiểu từ demo.extra. Biên dịch với demo.extra trên module path -> phải OK.
        //   Sau đó CHẠY demo.app (requires demo.lib) mà BỎ demo.extra khỏi module path.
        //   Xác nhận JVM khởi động bình thường, không một lời cảnh báo (mục 1).
        //
        // TODO 2 (mục 3): Vẫn thiếu demo.extra, giờ gọi đúng đoạn code chạm tới kiểu của
        //   nó. Ghi lại nguyên văn NoClassDefFoundError. Chú ý: nó nổ lúc nào - ngay khi
        //   khởi động, hay đúng lúc dòng code chạy?
        //
        // TODO 3 (mục 2): Vẫn thiếu demo.extra trên module path, nhưng cho demo.app khai
        //   `requires demo.extra;` (bắt buộc). Chạy lại. Lần này lỗi xảy ra ở giai đoạn
        //   nào, và thông báo khác gì TODO 2? Đây là minh họa trực tiếp cho t1 mục 12.
        //
        // TODO 4 (mục 2): Đặt lại demo.extra lên module path nhưng không ai requires thường
        //   tới nó. Xác nhận nó KHÔNG được nạp (dùng ModuleLayer.boot().modules() để in ra
        //   danh sách). Sau đó thêm cờ `--add-modules demo.extra` và xác nhận nó xuất hiện.
        //
        // TODO 5 (mục 4a): Viết phiên bản an toàn: một lớp Feature kiểm tra
        //   ModuleLayer.boot().findModule("demo.extra").isPresent(), chỉ khi true mới gọi
        //   sang lớp ExtraSupport (lớp RIÊNG, chính nó mới import kiểu của demo.extra).
        //   Chạy cả hai trường hợp có/không có module. Thử gộp hai lớp làm một xem có vỡ
        //   không - và giải thích kết quả quan sát được.
        //
        // TODO 6 (mục 5, 7): Mở module-info của một thư viện thật có requires static (gợi ý:
        //   chạy `java --describe-module` lên vài JAR trong ~/.m2). Ghi lại nó khai tùy chọn
        //   module nào và đoán xem tính năng phụ nào phụ thuộc vào module đó.
    }
}
