package com.javacore.organizing.modules;

/**
 * Code on the Class Path - the Unnamed Module
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 7/12 của section Modules. Mở đầu nhóm "sống chung với thế giới chưa
 * modular hóa" (t7, t8).
 * - Bài này quan trọng với bạn hơn vẻ ngoài của nó: TOÀN BỘ code trong repo java-core này,
 * và gần như mọi ứng dụng Spring Boot bạn từng viết, đều đang chạy trong unnamed module.
 * Đây là bài mô tả chính xác cái thế giới bạn đang sống.
 * - Một câu cho cả bài: unnamed module là một cái túi gom tất cả code trên class path, nó
 * ĐỌC ĐƯỢC mọi module khác và MỞ TOÀN BỘ chính nó - nhưng không module tường minh nào
 * {@code requires} tới nó được, vì nó không có tên.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/unnamed-module/">dev.java - Code on the Class Path - the Unnamed Module</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - THE UNNAMED MODULE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. NÓ LÀ GÌ
 * - Unnamed module chứa mọi "non-modular classes": class biên dịch mà không có module
 * descriptor, và mọi class nạp từ CLASS PATH.
 * - Vai trò: cây cầu tương thích ngược. Nhờ nó, code viết từ thời Java 8 chạy nguyên xi
 * trên JVM hiện đại mà không cần biết module là gì.
 * - Nhớ: nó là một MODULE THẬT trong mắt JVM, có đủ tư cách trong module graph. Không phải
 * "vùng ngoài module".
 * <p>
 * 2. BA TÍNH CHẤT - PHẢI THUỘC
 * - TÊN: "the unnamed module has none (makes sense, right?), which means no other module
 * can mention it in its declaration."
 * - PHỤ THUỘC: "the unnamed module reads all other modules that make it into the graph."
 * + Tức là code trên class path dùng được MỌI thứ đã được export, không cần khai gì. Đây
 * chính là lý do bạn chưa bao giờ phải viết {@code requires} trong đời đi làm.
 * - EXPORT: "the unnamed module exports all its packages and also opens them for reflection."
 * + Mở hết, cả {@code exports} lẫn {@code opens}. Không có đóng gói nào ở đây cả.
 * - Gộp ba tính chất: unnamed module là một module CỰC KỲ DỄ DÃI theo cả hai chiều.
 * <p>
 * 3. NÓ CHỨA CẢ MODULAR JAR - ĐIỂM DỄ SAI NHẤT
 * - Nguyên văn: "the unnamed module is in charge of all JARs on the class path, modular or
 * not. Consequently, modular JARs are not bound to be loaded as modules!"
 * - Đây là câu t1 mục 8 đã nói, nay nhìn từ phía class path: một JAR có
 * {@code module-info.class} đàng hoàng, nếu đặt trên class path thì descriptor bị LỜ ĐI
 * hoàn toàn. Mọi {@code exports}, {@code opens}, {@code requires} trong đó vô hiệu.
 * - Hệ quả tốt mà trang gốc nhấn: thư viện phát hành modular JAR KHÔNG ép người dùng phải
 * modular hóa theo. Ai muốn ở lại class path thì cứ ở.
 * - Hệ quả cần cảnh giác: nếu bạn thử nghiệm module mà đặt nhầm {@code -cp} thay vì
 * {@code -p}, mọi luật đóng gói biến mất và bạn sẽ tưởng code mình đúng.
 * <p>
 * 4. VÌ SAO KHÔNG CÓ "REQUIRES CLASS-PATH"
 * - Trang gốc lập luận bằng một thí nghiệm tưởng tượng: giả sử có directive đó, thì "As
 * long as there is at least one class on the class path, the module system would have to
 * assume that the dependency is fulfilled. That would not be very helpful."
 * - Nghĩa là: class path không có danh tính, không có ranh giới, không kiểm chứng được. Cho
 * phép {@code requires} nó thì toàn bộ RELIABLE CONFIGURATION (t1 mục 12) sụp đổ.
 * - Giải pháp của JPMS: để nó KHÔNG CÓ TÊN, vì "no module is supposed to ever reference it
 * in a requires directive."
 * - Rút ra quy tắc một chiều phải nhớ:
 * <pre>
 *   unnamed module  ---đọc được--&gt;  module tường minh      (được)
 *   module tường minh  ---đọc--&gt;  unnamed module           (KHÔNG BAO GIỜ)
 * </pre>
 * - Và hệ quả thực tiễn, nguyên văn: "For an explicit module to depend on an artifact, that
 * artifact has to be on the module path." Đây là câu quyết định toàn bộ chiến lược di cư:
 * muốn modular hóa một module của mình, mọi phụ thuộc của nó phải lên module path trước.
 * <p>
 * 5. MODULE RESOLUTION KHI KHỞI ĐỘNG TỪ CLASS PATH
 * - Khi chạy ứng dụng từ class path, module khởi đầu là unnamed module. Lúc đó "module
 * resolution starts in a predefined set of root modules", gồm {@code java.se} (nếu có) và
 * các module {@code java.*} khác.
 * - Ràng buộc quan trọng, nguyên văn: "Modules present on the module path will never be
 * resolved unless added explicitly with --add-modules."
 * - Dịch ra tình huống hay gặp: bạn để JAR trên module path nhưng ứng dụng chạy từ class
 * path, rồi ngạc nhiên vì "không thấy module đâu". Vì không ai {@code requires} nó (unnamed
 * module không khai gì được), nó không bao giờ được nạp. Lối thoát duy nhất là
 * {@code --add-modules} (bài t12).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. ALL-UNNAMED - TÊN GỌI KHI CẦN TRỎ TỚI NÓ
 * - Nó không có tên trong {@code module-info.java}, nhưng trên DÒNG LỆNH thì có một tên
 * đặc biệt: {@code ALL-UNNAMED}.
 * <pre>
 *   --add-opens java.base/java.lang=ALL-UNNAMED
 * </pre>
 * - Dòng trên là cờ bạn sẽ gặp nhiều nhất trong đời đi làm Java (Spring, Lombok, Mockito,
 * các công cụ build đều có lúc cần). Giờ đọc được nó: "mở package {@code java.lang} của
 * module {@code java.base} cho toàn bộ code trên class path".
 * - Chi tiết đầy đủ ở bài t11. Nhớ ở đây một điều: {@code ALL-UNNAMED} chỉ dùng được trên
 * dòng lệnh, KHÔNG viết được trong module-info.
 * <p>
 * 7. TRONG THỰC TẾ: REPO NÀY VÀ MỌI DỰ ÁN SPRING BOOT
 * - Repo java-core này không có {@code module-info.java} -> mọi lớp bạn viết nằm trong
 * unnamed module. Đó là lý do bài t1 dặn phải thực hành trong sandbox riêng.
 * - Fat JAR của Spring Boot chạy bằng {@code java -jar}, tức CLASS PATH, tức unnamed
 * module. Mọi thư viện trong đó, modular hay không, đều bị gom vào một túi.
 * - Vì thế trong dự án thật bạn gần như chỉ gặp module system ở đúng MỘT chỗ: khi code của
 * bạn (unnamed) đụng vào hàng rào của các module JDK (java.base, java.desktop...). Đó là
 * bài t10 và t11.
 * <p>
 * 8. CÁCH TỰ KIỂM TRA MÌNH ĐANG Ở ĐÂU
 * - Trong code bất kỳ:
 * <pre>
 *   Module m = getClass().getModule();
 *   System.out.println(m.getName());   // null nghĩa là unnamed module
 *   System.out.println(m.isNamed());   // false
 * </pre>
 * - {@code getName()} trả về {@code null} chứ không phải chuỗi rỗng - một chỗ dễ gây NPE
 * nếu bạn log mà không kiểm tra.
 * - Thêm cờ {@code -Xlog:module=debug} khi chạy JVM để xem toàn bộ quá trình resolve, rất
 * hữu ích khi gỡ lỗi "module không được nạp" ở mục 5.
 * <p>
 * 9. LIÊN HỆ SANG CÁC BÀI SAU
 * - Nửa còn lại của bức tranh di cư: plain JAR đặt trên MODULE path thì thành AUTOMATIC
 * MODULE (bài t8). Hai bài này là hai mặt của cùng một đồng xu - cùng là JAR không có
 * descriptor, khác nhau ở chỗ bạn đặt nó.
 * - Một tính chất của automatic module chỉ hiểu được sau bài này: nó là module tường minh
 * DUY NHẤT đọc được unnamed module. Đó là cầu nối khiến việc di cư từng bước khả thi.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba tính chất của unnamed module (tên, phụ thuộc, export)?
 * - Modular JAR đặt trên class path thì module descriptor của nó có tác dụng gì không?
 * - Vì sao JPMS cố tình không cho unnamed module một cái tên?
 * - Module tường minh có cách nào đọc được unnamed module không?
 * - Chạy ứng dụng từ class path, JAR trên module path có được nạp không? Vì sao? Cách sửa?
 * - {@code ALL-UNNAMED} dùng ở đâu, và KHÔNG dùng được ở đâu?
 * - Làm sao biết một lớp đang nằm trong unnamed module hay không?
 */
public class t7_TheUnnamedModule {

    void main() {
        Module here = getClass().getModule();
        Module base = String.class.getModule();

        // 1. Ba tính chất của unnamed module, kiểm chứng từng cái (mục 2)
        System.out.println("TÊN      : " + here.getName() + "   <- null, đúng nghĩa 'unnamed'");
        System.out.println("isNamed(): " + here.isNamed());
        System.out.println("descriptor: " + here.getDescriptor() + "   <- cũng null, không có");

        // 2. Nó ĐỌC ĐƯỢC mọi module trong graph - nên bạn chưa bao giờ phải viết requires
        System.out.println("\nđọc được java.base? " + here.canRead(base));
        System.out.println("đọc được java.sql?  "
                + here.canRead(ModuleLayer.boot().findModule("java.sql").orElseThrow()));

        // 3. Nhưng CHIỀU NGƯỢC LẠI thì không bao giờ - đây là luật một chiều ở mục 4
        System.out.println("\njava.base đọc được unnamed module? " + base.canRead(here));
        System.out.println("   -> false. Muốn ngược lại phải dùng --add-reads (bài t12)");

        // 4. Nó mở TOÀN BỘ package của mình, cả exports lẫn opens (mục 2)
        String pkg = getClass().getPackageName();
        System.out.println("\npackage " + pkg);
        System.out.println("   exports ? " + here.isExported(pkg));
        System.out.println("   opens   ? " + here.isOpen(pkg, base));
        System.out.println("   -> cả hai true: không có đóng gói nào trong unnamed module");

        System.out.println();
        // TODO 1 (mục 8): Chạy ngay trong repo này: in ra
        //   getClass().getModule().getName() và .isNamed(). Xác nhận null/false - bạn đang
        //   ở trong unnamed module. So sánh với String.class.getModule().getName().
        //
        // TODO 2 (mục 3): Lấy modular JAR demo.greet đã đóng ở bài t1 (có exports hạn chế).
        //   Chạy một lớp dùng lớp KHÔNG được exports của nó, hai lần:
        //   (a) JAR trên --module-path  -> phải lỗi
        //   (b) JAR trên --class-path   -> phải chạy được
        //   Ghi lại cả hai. Đây là bằng chứng trực tiếp cho câu "modular JARs are not bound
        //   to be loaded as modules".
        //
        // TODO 3 (mục 4): Thử cho một module tường minh requires tới code đang nằm trên
        //   class path. Ghi lại lỗi javac. Rồi chuyển code đó sang module path và thử lại.
        //   Đây là minh họa cho câu "For an explicit module to depend on an artifact, that
        //   artifact has to be on the module path."
        //
        // TODO 4 (mục 5): Chạy một lớp main nằm trên CLASS path, trong khi demo.greet nằm
        //   trên module path. In ModuleLayer.boot().modules() - demo.greet có mặt không?
        //   Thêm --add-modules demo.greet rồi in lại. Giải thích chênh lệch.
        //
        // TODO 5 (mục 2): Chứng minh "unnamed module reads all other modules". Từ một lớp
        //   trên class path, dùng java.sql hoặc java.xml mà KHÔNG khai requires gì. Chạy
        //   được. Rồi thử đúng việc đó từ một module tường minh chưa khai requires -> lỗi.
        //
        // TODO 6 (mục 8): Chạy lại TODO 4 với cờ -Xlog:module=debug. Đọc log, tìm dòng nói
        //   về việc module nào được resolve. Ghi vào note 2-3 dòng log hữu ích nhất để sau
        //   này gỡ lỗi module trong dự án thật.
    }
}
