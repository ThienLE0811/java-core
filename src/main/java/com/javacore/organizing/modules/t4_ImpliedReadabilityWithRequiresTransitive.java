package com.javacore.organizing.modules;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Requires;
import java.lang.module.ModuleFinder;

/**
 * Implied Readability with requires transitive
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 4/12 của section Modules.
 * - Cặp đôi với bài t3: {@code requires static} làm phụ thuộc YẾU đi, {@code requires
 * transitive} làm nó MẠNH thêm - lan cả sang người dùng module của bạn.
 * - Một câu cho cả bài: nếu kiểu của module B XUẤT HIỆN TRONG API của module A, thì A phải
 * {@code requires transitive B} - nếu không, mọi người dùng A đều phải tự khai thêm B, và
 * họ sẽ chửi bạn.
 * - Một câu khác để nhớ ngược lại: {@code transitive} dành cho kiểu nằm "trên biên giới"
 * giữa các module, không dành cho kiểu bạn dùng trong ruột.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/implied-readability/">dev.java - Implied Readability with requires transitive</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - IMPLIED READABILITY (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. VẤN ĐỀ CẦN GIẢI
 * - Tình huống: module A có method trả về một kiểu thuộc module B. Người dùng A gọi method
 * đó, nhận về một object kiểu của B - nhưng theo luật READABILITY ở t1 mục 11, họ không
 * đọc được B nên không dùng được object đó.
 * - Nếu không có {@code transitive}, mỗi người dùng A phải tự tìm ra "à hóa ra còn phải
 * requires B nữa" - trang gốc gọi việc này là tẻ nhạt và dễ sai.
 * - Câu gốc của bài, nguyên văn: "If a module uses types from another module in its own API,
 * every outsider using the first module would be forced to also require the second. Unless
 * the first module uses requires transitive for the second one, which implies readability."
 * <p>
 * 2. TRANSITIVE LÀM GÌ
 * - Cú pháp:
 * <pre>
 *   module com.demo.lib {
 *       requires transitive com.demo.model;
 *       requires            com.demo.util;    // chỉ dùng trong ruột
 *   }
 * </pre>
 * - Ý nghĩa: ai đọc được {@code com.demo.lib} thì tự động đọc được luôn
 * {@code com.demo.model}, không cần khai gì thêm. Đó là IMPLIED READABILITY - cạnh
 * readability được SUY RA chứ không được khai.
 * - Chú ý điều này KHÔNG phải "export hộ". Module trung gian không mở thêm package nào của
 * module kia; nó chỉ chuyển tiếp QUYỀN ĐỌC. Package nào được đọc vẫn do chính module kia
 * quyết định bằng {@code exports} của nó.
 * <p>
 * 3. VÍ DỤ THẬT TRONG JDK - JAVA.SQL
 * - Trang gốc lấy {@code java.sql} làm ví dụ, và nó là ví dụ tốt nhất để nhớ:
 * + {@code java.sql.SQLXML} dùng kiểu của module {@code java.xml} trong chữ ký của nó.
 * + {@code Driver.getParentLogger()} trả về một kiểu thuộc module {@code java.logging}.
 * - Vì vậy khai báo của {@code java.sql} có {@code requires transitive} cho CẢ HAI module
 * đó. Nhờ thế bạn chỉ cần {@code requires java.sql} là code JDBC chạy được, không phải tự
 * khai thêm {@code java.xml} và {@code java.logging}.
 * - Đây là bài tập tra cứu đáng làm ngay: {@code java --describe-module java.sql} rồi tìm
 * hai dòng {@code requires transitive} đó bằng mắt (TODO 1).
 * <p>
 * 4. LUẬT DÙNG - VÀ LỜI CẢNH BÁO ĐI KÈM
 * - Luật chính thức, nguyên văn: "If one module exports a package containing a type whose
 * signature refers to a package in a second module then the declaration of the first module
 * should include a requires transitive dependence upon the second."
 * - Phân tích câu này thành checklist ba bước, vì nó chặt hơn trực giác:
 * + (1) package có được EXPORTS không? Nếu không export thì không ai thấy, khỏi cần bàn.
 * + (2) trong package đó, có kiểu nào mà CHỮ KÝ nhắc tới package của module thứ hai không?
 * + (3) đủ cả hai -> phải {@code requires transitive}.
 * - Chữ SIGNATURE gồm: kiểu trả về, kiểu tham số, kiểu ngoại lệ ném ra, lớp cha và
 * interface được cài đặt, kiểu của field public. Không gồm biến cục bộ hay chi tiết cài đặt.
 * - CẢNH BÁO của trang gốc - đừng lạm dụng: chỉ dùng implied readability khi các kiểu đó
 * nằm "on the boundary" giữa các module. Nếu code của bạn dùng chúng khắp nơi một cách độc
 * lập thì hãy khai {@code requires} thường cho tường minh - việc đó "clarifies the system's
 * structure and also future-proofs the module declaration."
 * - Dịch vế cuối cho rõ: nếu sau này module trung gian bỏ dùng kiểu đó trong API và gỡ
 * {@code transitive}, code của bạn sẽ vỡ nếu bạn đang ăn nhờ quyền đọc của nó. Khai tường
 * minh thì không sợ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. AGGREGATOR MODULE - ỨNG DỤNG LỚN NHẤT CỦA TRANSITIVE
 * - Một module RỖNG, không chứa code, chỉ gồm toàn {@code requires transitive}, tồn tại để
 * gom nhiều module thành một cái tên duy nhất:
 * <pre>
 *   module com.demo.all {
 *       requires transitive com.demo.core;
 *       requires transitive com.demo.json;
 *       requires transitive com.demo.http;
 *   }
 * </pre>
 * - Người dùng chỉ cần {@code requires com.demo.all} là có hết. Chính JDK làm đúng vậy với
 * {@code java.se}: nó không chứa class nào, chỉ gom toàn bộ Java SE lại.
 * - Trực giác từ npm: đây chính là gói "meta-package" hoặc file {@code index.ts} barrel chỉ
 * re-export mọi thứ. Cùng một ý tưởng.
 * <p>
 * 6. BA LỖI THƯỜNG GẶP
 * - QUÊN transitive: bạn viết thư viện, test nội bộ chạy ngon (vì module test có khai đủ),
 * nhưng người dùng thật báo "không biên dịch được, phải tự thêm requires". Đây là lỗi hay
 * gặp nhất, và chỉ lộ ra khi có người dùng thứ hai.
 * - LẠM DỤNG transitive: khai transitive cho mọi phụ thuộc cho "chắc ăn". Hậu quả: người
 * dùng module của bạn vô tình đọc được cả những module họ không nên biết, rồi phụ thuộc
 * vào chúng. Sau này bạn muốn đổi thư viện nội bộ thì không đổi được nữa - API thật của bạn
 * đã phình ra ngoài tầm kiểm soát.
 * - Nhầm transitive với exports: transitive KHÔNG mở thêm package nào (mục 2). Nếu module
 * kia không exports package đó thì transitive cũng vô ích.
 * <p>
 * 7. MẸO TỰ KIỂM TRA KHI VIẾT THƯ VIỆN
 * - Quy tắc rút gọn để quyết định nhanh: nhìn vào các method PUBLIC trong package được
 * EXPORTS. Mọi kiểu xuất hiện ở chữ ký của chúng mà không thuộc module mình -> module chứa
 * nó phải là {@code requires transitive}.
 * - Cách kiểm tra bằng máy: tạo một module thử nghiệm chỉ {@code requires} thư viện của
 * bạn, rồi viết code gọi hết các API chính. Nếu phải thêm bất kỳ {@code requires} nào khác
 * để biên dịch được, đó chính là chỗ bạn quên {@code transitive}.
 * <p>
 * 8. LIÊN HỆ SANG CÁC BÀI SAU
 * - Khi người chạy cần thêm quan hệ đọc mà không sửa được module-info (vì đó là thư viện
 * của người khác): {@code --add-reads}, bài t12.
 * - Automatic module (bài t8) có hành vi đặc biệt liên quan trực tiếp tới bài này: nó
 * {@code requires transitive} TẤT CẢ các module khác - sẽ giải thích ở đó.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Phát biểu luật chính thức ở mục 4 bằng lời của mình, đủ cả ba bước kiểm tra?
 * - "Chữ ký" gồm những gì, và không gồm những gì?
 * - {@code requires transitive} có mở thêm package nào không?
 * - {@code java.sql} khai transitive cho hai module nào, và vì những kiểu cụ thể nào?
 * - Vì sao trang gốc khuyên vẫn nên khai {@code requires} tường minh dù đã được hưởng
 * implied readability?
 * - Aggregator module là gì? JDK có ví dụ nào?
 * - Lạm dụng transitive gây hại gì về lâu dài?
 */
public class t4_ImpliedReadabilityWithRequiresTransitive {

    void main() {
        Module sql = ModuleLayer.boot().findModule("java.sql").orElseThrow();
        ModuleDescriptor d = sql.getDescriptor();

        // 1. Ví dụ của trang gốc, đọc thẳng từ JDK đang chạy (mục 3)
        System.out.println("java.sql khai báo:");
        for (Requires r : d.requires()) {
            boolean transitive = r.modifiers().contains(Requires.Modifier.TRANSITIVE);
            System.out.println("   requires " + (transitive ? "transitive " : "           ")
                    + r.name());
        }
        System.out.println("   -> java.xml vì SQLXML, java.logging vì Driver.getParentLogger()");

        // 2. Hệ quả: ai đọc java.sql thì đọc luôn được java.xml, không cần khai gì (mục 2)
        Module xml = ModuleLayer.boot().findModule("java.xml").orElseThrow();
        System.out.println("\njava.sql có đọc được java.xml? " + sql.canRead(xml));

        // 3. Nhưng transitive KHÔNG phải "export hộ" - package nào mở vẫn do java.xml
        //    tự quyết bằng exports của chính nó (mục 2)
        System.out.println("java.xml exports org.w3c.dom?                    "
                + xml.isExported("org.w3c.dom"));
        System.out.println("java.xml exports com.sun.org.apache.xpath.internal? "
                + xml.isExported("com.sun.org.apache.xpath.internal"));

        // 4. Aggregator module: java.se không chứa package nào, chỉ gom (mục 5).
        //    Dùng ModuleFinder.ofSystem() chứ không dùng boot layer, vì java.se KHÔNG được
        //    resolve khi chạy từ class path - nó không exports package nào nên không vào
        //    tập root mặc định (xem bài t12 mục 2a). Đọc descriptor thì vẫn đọc được.
        ModuleDescriptor se = ModuleFinder.ofSystem().find("java.se").orElseThrow().descriptor();
        long gom = se.requires().stream()
                .filter(r -> r.modifiers().contains(Requires.Modifier.TRANSITIVE)).count();
        System.out.println("\njava.se chứa " + se.packages().size() + " package"
                + " nhưng requires transitive " + gom + " module");
        System.out.println("java.se có trong boot layer không? "
                + ModuleLayer.boot().findModule("java.se").isPresent());

        System.out.println();
        // Tiếp tục dùng sandbox đã tạo ở bài t1.
        //
        // TODO 1 (mục 3): Chạy `java --describe-module java.sql`. Tìm bằng mắt hai dòng
        //   requires transitive. Rồi chạy tiếp `java --describe-module java.se` - module
        //   này có bao nhiêu dòng requires transitive và bao nhiêu package tự chứa?
        //
        // TODO 2 (mục 1): Dựng tình huống thiếu transitive. Ba module:
        //     demo.model  - exports com.demo.model, có lớp User
        //     demo.lib    - requires demo.model, exports com.demo.lib,
        //                   có method `public User findUser(long id)`
        //     demo.app    - requires demo.lib, gọi findUser rồi dùng kết quả
        //   Biên dịch demo.app -> phải lỗi. Chép nguyên văn thông báo javac vào note.
        //
        // TODO 3 (mục 2): Sửa bằng cách đổi demo.lib thành `requires transitive demo.model`.
        //   Biên dịch lại, xác nhận demo.app chạy được mà KHÔNG cần khai requires demo.model.
        //
        // TODO 4 (mục 2): Chứng minh transitive không phải "export hộ". Thêm vào demo.model
        //   một package KHÔNG được exports, rồi thử dùng nó từ demo.app. Vẫn lỗi dù đã có
        //   transitive. Ghi lại thông báo và giải thích bằng lời.
        //
        // TODO 5 (mục 4): Luyện nhận diện "trên biên giới". Trong demo.lib thêm hai method:
        //   một method dùng kiểu của demo.util ở KIỂU TRẢ VỀ, một method chỉ dùng demo.util
        //   trong BIẾN CỤC BỘ. Áp checklist ba bước ở mục 4: method nào buộc phải transitive?
        //   Tự kiểm bằng cách thử biên dịch demo.app gọi lần lượt từng method.
        //
        // TODO 6 (mục 5): Viết một aggregator module demo.all gồm toàn requires transitive,
        //   không chứa một lớp nào. Cho demo.app chỉ requires demo.all và xác nhận dùng được
        //   hết. Đây là mô hình của java.se.
        //
        // TODO 7 (mục 6, 7): Áp mẹo ở mục 7 lên demo.lib: viết module thử nghiệm chỉ
        //   `requires demo.lib`, gọi hết API public. Mỗi lần phải thêm một requires mới là
        //   một chỗ quên transitive - liệt kê chúng vào note.
    }
}
