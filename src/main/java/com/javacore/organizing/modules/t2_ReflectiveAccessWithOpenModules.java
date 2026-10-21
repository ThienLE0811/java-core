package com.javacore.organizing.modules;

import java.lang.reflect.Field;

/**
 * Reflective Access with Open Modules and Open Packages
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 2/12 của section Modules.
 * - Bài này trả lời câu hỏi còn treo ở t1 mục 5: vì sao {@code exports} không đủ cho
 * framework, và {@code opens} khác nó ở chỗ nào.
 * - Một câu cho cả bài: {@code exports} mở API cho NGƯỜI VIẾT CODE gọi, {@code opens} mở
 * ruột gan cho FRAMEWORK soi bằng reflection - hai nhu cầu khác nhau nên JPMS cho hai từ
 * khóa khác nhau, đừng dùng lẫn.
 * - Đây là bài có giá trị thực tế cao nhất section với người làm Spring/Hibernate/Jackson:
 * gần như mọi lỗi module trong dự án thật đều rơi vào đúng bài này.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/opening-for-reflection/">dev.java - Reflective Access with Open Modules and Open Packages</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - OPENING FOR REFLECTION (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. VÌ SAO EXPORTS KHÔNG ĐỦ - HAI LÝ DO
 * <p>
 * - 1a. LÝ DO VỀ THIẾT KẾ: EXPORT LÀ MỘT LỜI HỨA
 * + Nguyên văn: "Exporting a package makes it part of a module's public API. This invites
 * other modules to use the types it contains and conveys a degree of stability."
 * + Đọc kỹ chữ INVITES và STABILITY. Export không chỉ là mở cửa kỹ thuật, nó là một TUYÊN
 * BỐ: "package này là API, tôi cam kết giữ ổn định".
 * + Nhưng các lớp entity, lớp xử lý HTTP request, lớp DTO... là chi tiết NỘI BỘ. Bạn muốn
 * Hibernate đọc được chúng, nhưng tuyệt đối không muốn module khác import và phụ thuộc
 * vào chúng. {@code exports} không diễn đạt được ý đó.
 * <p>
 * - 1b. LÝ DO VỀ KỸ THUẬT: EXPORT MỞ KHÔNG ĐỦ SÂU
 * + Nguyên văn: "A more technical problem is that even in exported packages only public
 * members of public types are accessible."
 * + Mà framework cần đúng những thứ KHÔNG public: constructor không tham số đang để
 * {@code private}, field {@code private} để gán giá trị, lớp package-private.
 * + Jackson tạo object rồi set thẳng vào field private. Hibernate cũng vậy. Spring gọi
 * constructor private. Với tất cả các việc đó, {@code exports} vô dụng.
 * <p>
 * 2. OPENS - MỘT CÁNH CỬA CÓ TÍNH CHẤT NGƯỢC LẠI
 * - Lúc BIÊN DỊCH, nguyên văn: "At compile time, the package is fully encapsulated as if
 * the directive weren't there."
 * + Tức là {@code opens} KHÔNG cho ai biên dịch dựa trên code bên trong. Người khác vẫn
 * không import được. Đóng gói lúc biên dịch vẫn nguyên vẹn.
 * - Lúc CHẠY, nguyên văn: "At run time, on the other hand, the package's types are
 * available for reflection."
 * + Và mở TẤT CẢ: {@code opens} "allows access to all members", kể cả {@code private}, qua
 * {@code AccessibleObject.setAccessible()}.
 * - Tóm tắt một dòng của trang gốc: {@code opens} "prevents compilation against code in
 * opened packages and only allows access at run time".
 * - BẢNG SO SÁNH PHẢI THUỘC:
 * <pre>
 *                          exports                 opens
 *   Lúc biên dịch          MỞ                      ĐÓNG hoàn toàn
 *   Lúc chạy               MỞ                      MỞ (qua reflection)
 *   Mức độ mở              chỉ public              TẤT CẢ, kể cả private
 *   Phục vụ ai             code gọi trực tiếp      framework dùng reflection
 *   Ngụ ý                  "đây là API ổn định"    "cứ soi, nhưng đừng gọi thẳng"
 * </pre>
 * - Nhìn bảng sẽ thấy hai directive này KHÔNG phải quan hệ mạnh-yếu. Chúng vuông góc nhau.
 * Một package có thể vừa {@code exports} vừa {@code opens} nếu vừa là API vừa cần soi.
 * <p>
 * 3. OPEN MODULE - MỞ CẢ MODULE
 * - Đặt từ khóa {@code open} trước {@code module}:
 * <pre>
 *   open module com.demo.app {
 *       requires com.fasterxml.jackson.databind;
 *   }
 * </pre>
 * - Nguyên văn: "An open module opens all packages it contains as if each of them was used
 * individually in an opens directive."
 * - Hệ quả cú pháp cần nhớ: trong một {@code open module}, bạn KHÔNG được viết thêm
 * directive {@code opens} nào nữa - thừa, và trình biên dịch báo lỗi.
 * - Khi nào dùng: module có quá nhiều package cần reflection, liệt kê từng cái thành vô
 * nghĩa. Thực tế hay gặp ở chính ứng dụng (module trên cùng), nơi Spring quét toàn bộ.
 * <p>
 * 4. CẶP TỪ CẦN PHÂN BIỆT TRONG TÀI LIỆU
 * - OPEN PACKAGE: một package được mở bằng directive {@code opens}.
 * - OPEN MODULE: cả module được mở bằng từ khóa {@code open} ở đầu khai báo.
 * - Hai cái cùng kết quả cho package đó, khác ở phạm vi và ở mức độ "có chủ đích". Dùng
 * {@code opens} từng package là tuyên bố rõ chỗ nào cần soi; {@code open module} là buông
 * toàn bộ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. ĐỌC LỖI CHO ĐÚNG - DẤU HIỆU NHẬN BIẾT
 * - Thông báo điển hình khi thiếu {@code opens}:
 * <pre>
 *   InaccessibleObjectException: Unable to make field private java.lang.String
 *   com.demo.User.name accessible: module com.demo does not "opens com.demo"
 *   to module com.fasterxml.jackson.databind
 * </pre>
 * - Ba thông tin cần đọc ra khỏi thông báo này, lúc nào cũng theo đúng thứ tự đó:
 * + AI bị chặn: {@code module com.demo} - module CHỨA cái bị soi.
 * + THIẾU GÌ: chữ {@code opens}, không phải {@code exports}. JVM nói thẳng tên directive.
 * + MỞ CHO AI: {@code to module com.fasterxml.jackson.databind} - và đây là module thực
 * sự làm reflection, không phải module API.
 * - Nếu thông báo nói {@code is not exported} thì vấn đề KHÁC hẳn: đó là gọi code trực
 * tiếp, sửa bằng {@code exports}. Đọc nhầm hai thông báo này là nguồn gốc của hàng giờ
 * loay hoay.
 * <p>
 * 6. BẮC CẦU TỪ TYPESCRIPT - VÌ SAO TS KHÔNG CÓ KHÁI NIỆM NÀY
 * - Trong JS/TS, {@code private} chỉ tồn tại lúc biên dịch (trừ field {@code #x} của ES).
 * Lúc chạy mọi property đều sờ được bằng {@code obj["name"]}. Nên không có gì để mà "mở".
 * - Vì vậy các thư viện kiểu class-transformer, TypeORM đọc field thoải mái, không cần xin
 * phép ai. Java thì có hàng rào thật ở mức JVM, nên phải có {@code opens} để hạ rào.
 * - Cái tương đương gần nhất về TINH THẦN trong TS: decorator metadata - bạn phải khai báo
 * tường minh (ví dụ bật {@code emitDecoratorMetadata}) thì framework mới đọc được kiểu.
 * Cũng là "phải cho phép thì mới soi được", nhưng nhẹ hơn nhiều.
 * <p>
 * 7. TRONG DỰ ÁN SPRING BOOT THẬT - CÁI GIÁ CỦA REFLECTION
 * - Nếu modular hóa một ứng dụng Spring, bạn sẽ phải mở gần hết: package entity cho
 * Hibernate, package DTO cho Jackson, package config cho Spring, package test cho JUnit.
 * - Đây chính là lý do thực tế đã nói ở t1 mục 15: mở nhiều đến mức đó thì strong
 * encapsulation gần như không còn ý nghĩa, nên phần lớn ứng dụng Boot ở lại class path.
 * - Ngược lại, THƯ VIỆN thì rất đáng modular hóa, vì thư viện ít dùng reflection lên chính
 * mình và có API rõ ràng để export.
 * - Một hệ quả ít người để ý: {@code opens} mở cho MỌI module lúc chạy, nên nếu đã phải
 * {@code opens} toàn bộ thì đừng tự an ủi là code vẫn được bảo vệ. Chỉ còn hàng rào lúc
 * biên dịch.
 * <p>
 * 8. LIÊN HỆ SANG CÁC BÀI SAU
 * - Mở cho ĐÚNG MỘT module thay vì mở cho tất cả: {@code opens ... to}, bài t5.
 * - Khi code nằm trên class path (unnamed module) thì luật này áp dụng thế nào: bài t7.
 * - Khi cần soi vào chính JDK mà JDK không mở: {@code --add-opens}, bài t11. Lưu ý quan
 * hệ: {@code opens} là chủ nhà tự mở; {@code --add-opens} là người chạy phá khóa từ ngoài.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Hai lý do trang gốc đưa ra để nói {@code exports} không đủ cho reflection?
 * - Lúc biên dịch, một package được {@code opens} có dùng được từ module khác không?
 * - {@code opens} mở tới mức nào - public, hay tất cả?
 * - {@code open module} khác gì liệt kê {@code opens} từng package? Trong open module có
 * được viết thêm {@code opens} không?
 * - Nhìn thông báo lỗi, làm sao biết nên sửa bằng {@code exports} hay bằng {@code opens}?
 * - Package vừa là API vừa cần Jackson soi thì khai báo thế nào?
 */
public class t2_ReflectiveAccessWithOpenModules {

    // Field private của CHÍNH module này, dùng để đối chứng ở bước 3
    private final String biMat = "xin chao";

    void main() throws NoSuchFieldException, IllegalAccessException {
        Module here = getClass().getModule();
        Module base = String.class.getModule();

        // 1. EXPORTS và OPENS là hai cánh cửa khác nhau - hỏi thẳng module system (mục 2)
        System.out.println("java.base exports java.lang ? " + base.isExported("java.lang"));
        System.out.println("java.base opens   java.lang ? " + base.isOpen("java.lang", here));
        System.out.println("   -> exports=true nên gọi String.length() được");
        System.out.println("   -> opens=false nên KHÔNG soi được field private của String");

        // 2. Hệ quả: cùng một thao tác setAccessible, hai kết quả khác nhau (mục 1b)
        try {
            Field f = String.class.getDeclaredField("value");
            f.setAccessible(true);
            System.out.println("\nsoi String.value: thành công (bạn đang chạy có --add-opens?)");
        } catch (RuntimeException e) {
            System.out.println("\nsoi String.value -> " + e.getClass().getSimpleName());
            System.out.println("   " + e.getMessage());
            System.out.println("   -> thông báo đòi OPENS chứ không đòi exports (mục 5)");
        }

        // 3. Đối chứng: package của chính mình nằm trong unnamed module, mở sẵn tất cả (t7)
        Field own = getClass().getDeclaredField("biMat");
        own.setAccessible(true);
        System.out.println("\nsoi field private của chính lớp này -> " + own.get(this));
        System.out.println("   -> được, vì unnamed module tự opens mọi package của nó");

        System.out.println();
        // Tiếp tục dùng sandbox đã tạo ở bài t1.
        //
        // TODO 1 (mục 1b, 2): Quay lại TODO 4 của bài t1 - chỗ demo.app đọc field private
        //   của Greeter và nhận InaccessibleObjectException. Giờ thêm
        //   `opens com.demo.greet.api;` vào demo.greet. Chạy lại, xác nhận đã qua.
        //   Chép thông báo lỗi TRƯỚC khi sửa vào note, đối chiếu với mục 5.
        //
        // TODO 2 (mục 2): Chứng minh "opens không cho biên dịch". Tạo package
        //   com.demo.greet.secret CHỈ có `opens`, không có `exports`. Thử import một lớp
        //   trong đó từ demo.app -> javac phải báo lỗi. Nhưng Class.forName(...) cùng
        //   setAccessible(true) lúc chạy thì được. Ghi lại cả hai kết quả cạnh nhau.
        //
        // TODO 3 (mục 2): Lập bảng thực nghiệm 2x2 của riêng mình. Với một package, thử đủ
        //   4 cấu hình: không khai gì / chỉ exports / chỉ opens / cả hai. Mỗi cấu hình kiểm
        //   tra 2 việc: (a) import rồi gọi method public, (b) reflection đọc field private.
        //   Điền 8 ô kết quả, rồi so với bảng ở mục 2.
        //
        // TODO 4 (mục 3): Đổi demo.greet thành `open module demo.greet { ... }`. Xác nhận
        //   mọi package đều soi được mà không cần opens. Sau đó thử thêm một directive
        //   `opens` vào trong open module -> đọc kỹ lỗi javac trả về.
        //
        // TODO 5 (mục 5, 7): Tạo một lớp "entity" có field private và constructor private,
        //   rồi dùng chính Reflection API đã học ở bước 27 để mô phỏng việc Jackson làm:
        //   tạo instance bằng constructor private + gán từng field. Chạy khi chưa opens và
        //   sau khi opens. Đây là bản thu nhỏ của mọi lỗi Hibernate/Jackson trong dự án thật.
        //
        // TODO 6 (mục 7): Liệt kê trong note: nếu phải modular hóa một dịch vụ Spring Boot
        //   bạn từng làm, những package nào buộc phải opens và mở cho module nào. Tự đánh
        //   giá: sau khi mở hết chừng đó, strong encapsulation còn lại bao nhiêu phần trăm?
    }
}
