package com.javacore.organizing.modules;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Exports;

/**
 * Qualified exports and opens
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 5/12 của section Modules. Bài cuối của nhóm "tinh chỉnh directive".
 * - Một câu cho cả bài: {@code exports P to M} mở package P cho ĐÚNG module M và đóng kín
 * với tất cả phần còn lại - dùng khi một bộ module muốn chia sẻ code nội bộ với nhau mà
 * không để lọt ra ngoài.
 * - Bài này có một điểm đặc biệt: trang gốc khuyên dùng qualified EXPORTS nhưng khuyên
 * ĐỪNG dùng qualified OPENS. Lý do khác nhau, và đó là phần đáng nhớ nhất.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/qualified-exports-opens/">dev.java - Qualified exports and opens</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - QUALIFIED EXPORTS AND OPENS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. CÚ PHÁP
 * - Hai dạng, cùng một ngữ pháp:
 * <pre>
 *   exports com.demo.internal to com.demo.app, com.demo.cli;
 *   opens   com.demo.entity   to org.hibernate.orm.core;
 * </pre>
 * - Danh sách module ngăn bằng dấu phẩy. Dạng {@code opens ... to} có ngữ nghĩa y hệt, chỉ
 * khác ở chỗ nó mở cho reflection thay vì cho biên dịch (bài t2).
 * - Hiệu lực, nguyên văn: "To the modules named in an exports to directive, the package
 * will be exactly as accessible as with a regular exports directive. To all other modules
 * the package will be as strongly encapsulated as if there were no exports at all."
 * - Tức là chỉ có hai trạng thái: trong danh sách thì mở hoàn toàn, ngoài danh sách thì
 * đóng hoàn toàn. Không có mức trung gian.
 * <p>
 * 2. JDK TỰ DÙNG NÓ THẾ NÀO - VÍ DỤ JAVA.XML
 * - Nguyên văn: "Six of its internal packages, prefixed with com.sun.org.apache.xml.internal
 * and com.sun.org.apache.xpath.internal are used by java.xml.crypto (the API for XML
 * cryptography) and are thus exported to it (and only it)."
 * - Đây là ví dụ mẫu mực cho đúng vấn đề mà qualified exports sinh ra để giải: hai module
 * của cùng một nhà sản xuất cần dùng chung code nội bộ, nhưng không ai muốn
 * {@code com.sun.org.apache.xml.internal} trở thành API công khai của Java.
 * - Kiểm chứng được ngay: {@code java --describe-module java.xml} rồi tìm các dòng có chữ
 * {@code to java.xml.crypto} (TODO 1).
 * <p>
 * 3. HAI LUẬT BIÊN DỊCH PHẢI NHỚ
 * - Luật 1, nguyên văn: "If a module that declares a qualified export/open is compiled and
 * the target module can't be found, the compiler will issue a warning."
 * + Chỉ WARNING, không phải error. Vì module đích có thể chưa tồn tại lúc biên dịch module
 * này - chuyện bình thường khi build từng module riêng.
 * - Luật 2, nguyên văn: "It is not allowed to use a package in an exports and in an
 * exports to" - vi phạm là LỖI biên dịch.
 * + Lý do hợp lý: đã mở cho tất cả rồi thì mở thêm cho một module cụ thể là vô nghĩa và
 * mâu thuẫn. Luật này áp dụng tương tự cho {@code opens}.
 * <p>
 * 4. KHI NÀO DÙNG QUALIFIED EXPORTS - CÓ LÝ DO CHÍNH ĐÁNG
 * - Tình huống của nó, nguyên văn: "a set of modules wants to share functionality between
 * them without exposing it."
 * - Trang gốc gắn nó vào lịch sử: trước Java 9 không có cách nào để một lớp vừa
 * {@code public} (cho module anh em gọi) vừa không truy cập được từ bên ngoài. "strong
 * encapsulation solved that by allowing us to make public classes inaccessible outside a
 * module" - và qualified exports là mức tinh chỉnh tiếp theo của chính ý tưởng đó.
 * - Đối tượng hưởng lợi, nguyên văn: "libraries and frameworks that consist of several
 * modules and want to share code without clients being able to use it."
 * - Nhớ một câu: qualified exports là dành cho NHÀ SẢN XUẤT NHIỀU MODULE, không dành cho
 * ứng dụng đơn lẻ.
 * <p>
 * 5. KHI NÀO DÙNG QUALIFIED OPENS - TRANG GỐC KHUYÊN DÈ DẶT
 * - Lập luận gốc, nguyên văn: "The target modules for qualified opens, on the other hand,
 * are typically frameworks and whether you open a package for reflection to every module or
 * just to Hibernate, either way Spring won't start depending on it. As such, the use case
 * for qualified opens is much smaller."
 * - Dịch cho rõ lập luận, vì nó tinh tế: nguy cơ của một {@code exports} thừa là có người
 * SẼ phụ thuộc vào nó rồi bạn không gỡ được nữa. Nhưng {@code opens} chỉ mở cho reflection
 * - không ai "vô tình phụ thuộc" vào một package chỉ mở cho reflection cả. Nên siết lại
 * bằng {@code to} thu được rất ít lợi ích.
 * - Và có một bất lợi thật, nguyên văn: "packages must always be opened to the exact module
 * that does the actual reflection. So in cases where specifications and implementations are
 * separated (for example, JPA and Hibernate) you might find yourself having to open entity
 * packages to the implementation instead of the API."
 * + Hệ quả rất khó chịu trong thực tế: bạn phải viết
 * {@code opens com.demo.entity to org.hibernate.orm.core;} - tức là hard-code tên module
 * của một CÀI ĐẶT cụ thể vào khai báo module của mình. Đổi sang EclipseLink là phải sửa
 * module-info. Đúng thứ mà JPA sinh ra để tránh.
 * - Khuyến nghị mặc định, nguyên văn: "A good default approach for opening packages for
 * reflection is to not qualify the access unless your project uses a lot of reflection over
 * its own code... Opening just to frameworks seems not worth the hassle."
 * <p>
 * 6. TÓM TẮT LỜI KHUYÊN CỦA TRANG GỐC
 * - {@code exports ... to}: DÙNG, khi bạn có nhiều module cùng nhà cần chia sẻ code nội bộ.
 * - {@code opens ... to}: THƯỜNG KHÔNG CẦN. Mở không điều kiện ({@code opens} trơn) là mặc
 * định tốt, trừ khi chính dự án bạn dùng nhiều reflection lên code của mình.
 * - Đây là một trong ít chỗ trang gốc đưa khuyến nghị rõ ràng theo hai hướng ngược nhau cho
 * hai directive song sinh - đáng nhớ vì nó cho thấy phải nghĩ theo RỦI RO chứ không theo
 * đối xứng cú pháp.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. NHƯỢC ĐIỂM LỚN NHẤT - PHỤ THUỘC NGƯỢC
 * - Qualified exports tạo ra một thứ trái khoáy: module CUNG CẤP phải biết TÊN của module
 * TIÊU THỤ. Bình thường mũi tên phụ thuộc đi một chiều, giờ nó có một mũi tên ngược ở mức
 * khai báo.
 * - Hậu quả thực tế: thêm một module anh em mới là phải sửa {@code module-info.java} của
 * tất cả module đang chia sẻ code. Danh sách {@code to} phình dần theo thời gian.
 * - Giải pháp thay thế nên cân nhắc trước: nếu nhiều module cần chung một thứ, có lẽ thứ đó
 * xứng đáng thành MỘT MODULE RIÊNG với API đàng hoàng. Qualified exports là cách vá khi
 * không tách được.
 * <p>
 * 8. BẮC CẦU TỪ TYPESCRIPT
 * - Không có tương đương trực tiếp trong npm/TS. Thứ gần nhất về tinh thần là quy ước đặt
 * tên {@code @scope/internal-*} cộng với lint rule cấm import chéo - tức là một hàng rào
 * MỀM, dựa vào kỷ luật.
 * - Java cho hàng rào CỨNG ở mức JVM: ngoài danh sách {@code to} thì không cách nào gọi,
 * kể cả cố tình. Đây là khác biệt đáng nhớ về triết lý: TS tin vào quy ước, Java cưỡng chế.
 * <p>
 * 9. LIÊN HỆ SANG CÁC BÀI SAU
 * - Khi bạn là người CHẠY ứng dụng và cần mở một package mà tác giả không mở: cờ
 * {@code --add-exports} và {@code --add-opens} ở bài t11 chính là phiên bản dòng lệnh của
 * đúng hai directive trong bài này, kể cả cú pháp cũng tương tự dạng "mở P cho M".
 * - Lưu ý quan trọng sẽ gặp lại ở t7: nếu module đích nằm trên class path thì tên module
 * của nó là {@code ALL-UNNAMED}, không phải một cái tên cụ thể.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Module KHÔNG nằm trong danh sách {@code to} thì thấy package đó ở mức nào?
 * - Khai {@code exports to} một module không tìm thấy lúc biên dịch: lỗi hay cảnh báo? Vì
 * sao chọn mức đó?
 * - Vừa {@code exports P} vừa {@code exports P to M} thì sao?
 * - {@code java.xml} dùng qualified exports cho module nào và vì mục đích gì?
 * - Vì sao trang gốc nói use case của qualified opens "much smaller"? Lập luận cốt lõi là gì?
 * - Bất lợi của qualified opens trong tình huống JPA/Hibernate là gì?
 * - Khuyến nghị mặc định cho việc mở package cho reflection?
 * - Nhược điểm "phụ thuộc ngược" của qualified exports, và cách tránh?
 */
public class t5_QualifiedExportsAndOpens {

    void main() {
        Module here = getClass().getModule();
        Module xml = ModuleLayer.boot().findModule("java.xml").orElseThrow();
        ModuleDescriptor d = xml.getDescriptor();

        // 1. Ví dụ của trang gốc: java.xml mở package nội bộ CHỈ cho java.xml.crypto (mục 2)
        System.out.println("java.xml - các qualified exports:");
        d.exports().stream().filter(Exports::isQualified).forEach(e ->
                System.out.println("   exports " + e.source() + "\n        to " + e.targets()));

        // 2. Đếm để thấy tỉ lệ
        long qualified = d.exports().stream().filter(Exports::isQualified).count();
        System.out.println("   -> " + qualified + " qualified / "
                + d.exports().size() + " tổng số exports");
        System.out.println("   LƯU Ý: trang gốc viết \"six of its internal packages\","
                + " nhưng JDK 25 đang là " + qualified + ". Tài liệu viết cho bản JDK cũ hơn"
                + " - luôn tin output trước mặt hơn con số trong tài liệu.");

        // 3. Hiệu lực: trong danh sách thì mở hoàn toàn, ngoài danh sách thì đóng
        //    hoàn toàn, không có mức trung gian (mục 1)
        String pkg = d.exports().stream().filter(Exports::isQualified)
                .map(Exports::source).findFirst().orElseThrow();
        System.out.println("\npackage " + pkg);
        System.out.println("   mở cho tất cả?        " + xml.isExported(pkg));
        System.out.println("   mở cho module này?    " + xml.isExported(pkg, here));
        System.out.println("   -> cả hai đều false vì ta không nằm trong danh sách 'to'");

        System.out.println();
        // Tiếp tục dùng sandbox đã tạo ở bài t1.
        //
        // TODO 1 (mục 2): Chạy `java --describe-module java.xml`, tìm các dòng chứa "to
        //   java.xml.crypto". Đếm xem có đúng 6 package như trang gốc nói không, và ghi lại
        //   tên chúng. Làm tương tự với `java --describe-module java.base` - nó qualified
        //   exports cho những module nào? (Danh sách này dài một cách đáng ngạc nhiên.)
        //
        // TODO 2 (mục 1): Ba module demo.core, demo.app, demo.outsider. Trong demo.core:
        //     exports com.demo.core.internal to demo.app;
        //   Cho cả demo.app và demo.outsider cùng import một lớp trong package đó.
        //   Xác nhận: demo.app biên dịch OK, demo.outsider lỗi. Chép nguyên văn lỗi.
        //
        // TODO 3 (mục 3): Thử hai tình huống lỗi/cảnh báo:
        //   (a) đổi thành `exports ... to demo.khong_ton_tai;` -> javac ra WARNING gì?
        //   (b) viết cả `exports com.demo.core.internal;` lẫn `exports ... to demo.app;`
        //       -> javac ra ERROR gì? Chép cả hai vào note.
        //
        // TODO 4 (mục 5): Mô phỏng bất lợi JPA/Hibernate. Tạo demo.spec (interface) và
        //   demo.impl (cài đặt, chính nó mới làm reflection). Trong demo.entity viết
        //   `opens com.demo.entity to demo.spec;` rồi chạy -> vẫn lỗi, vì kẻ reflection
        //   thật là demo.impl. Sửa lại thành `to demo.impl`. Ghi lại cảm nhận: nếu mai đổi
        //   sang demo.impl2 thì phải sửa ở đâu?
        //
        // TODO 5 (mục 7): Thêm module thứ tư demo.cli cũng cần code nội bộ của demo.core.
        //   Đếm số file module-info.java phải sửa. Rồi thử phương án thay thế ở mục 7: tách
        //   phần dùng chung thành module demo.shared có API riêng. So sánh hai cách: cách
        //   nào dễ mở rộng hơn khi có module thứ năm?
        //
        // TODO 6 (mục 6): Viết vào note quyết định của riêng mình cho một dự án giả định
        //   gồm 4 module + dùng Hibernate: chỗ nào dùng exports to, chỗ nào opens trơn,
        //   chỗ nào opens to. Giải thích từng lựa chọn bằng lập luận RỦI RO ở mục 5.
    }
}
