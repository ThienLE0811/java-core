package com.javacore.organizing.modules;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Incremental Modularization with Automatic Modules
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 8/12 của section Modules. Nửa còn lại của cặp đôi với t7.
 * - Bài này trả lời câu hỏi sống còn của mọi dự án thật: tôi muốn modular hóa code của
 * mình, nhưng 40 thư viện tôi đang dùng chưa có {@code module-info}. Làm sao bắt đầu?
 * - Một câu cho cả bài: thả một plain JAR lên MODULE PATH thì module system tự bịa ra một
 * module cho nó - có tên, đọc được tất cả, export tất cả. Nhờ đó bạn modular hóa được code
 * của mình TRƯỚC KHI thư viện kịp modular hóa.
 * - Đối chiếu với t7 cho dễ nhớ: cùng một plain JAR, đặt class path thì vào unnamed module,
 * đặt module path thì thành automatic module.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/automatic-module/">dev.java - Incremental Modularization with Automatic Modules</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - AUTOMATIC MODULES (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. NÓ LÀ GÌ
 * - Với MỌI JAR trên module path mà thiếu module descriptor, module system tạo một
 * AUTOMATIC MODULE.
 * - Trang gốc nhấn: chúng là "full-fledged named modules" - module tường minh đầy đủ tư
 * cách, CÓ TÊN, được {@code requires} tới bình thường. Không phải hạng hai.
 * - Đây là điểm khác cốt tử so với unnamed module ở t7: unnamed KHÔNG có tên nên không ai
 * requires được; automatic module CÓ tên nên module của bạn requires được.
 * <p>
 * 2. TÊN - HAI BƯỚC SUY RA
 * - Bước 1: tìm header {@code Automatic-Module-Name} trong {@code META-INF/MANIFEST.MF} của
 * JAR. Có thì lấy luôn làm tên.
 * - Bước 2: không có thì SUY RA TỪ TÊN FILE - bỏ phần version, rồi đổi mọi ký tự không phải
 * chữ-số thành dấu chấm.
 * <pre>
 *   guava-32.1.3-jre.jar            -&gt;  guava
 *   commons-lang3-3.12.0.jar        -&gt;  commons.lang3
 *   my-cool_lib-1.0-SNAPSHOT.jar    -&gt;  my.cool.lib
 * </pre>
 * - Thư viện muốn đi đường tử tế thì chỉ cần thêm một dòng vào manifest - không cần viết
 * {@code module-info.java}, không cần nâng Java version. Đây là bước di cư rẻ nhất mà một
 * thư viện có thể làm, và là lý do rất nhiều thư viện đã làm nó từ lâu.
 * <p>
 * 3. PHỤ THUỘC VÀ EXPORT - DỄ DÃI NHƯ UNNAMED MODULE
 * - PHỤ THUỘC, nguyên văn: "An automatic module reads all other modules that make it into
 * the graph, including the unnamed module."
 * + Vế cuối là chìa khóa của cả bài. Automatic module là module CÓ TÊN DUY NHẤT đọc được
 * unnamed module. Nhờ vậy một thư viện cũ đặt trên module path vẫn gọi được các thư viện
 * khác còn nằm trên class path - di cư từng phần mới khả thi.
 * - EXPORT, nguyên văn: "An automatic module exports all its packages and also opens them
 * for reflection."
 * + Mở toàn bộ, cả cho reflection. Không có đóng gói. Hợp lý: JAR đó có bao giờ khai báo
 * cái gì là API đâu mà biết đóng cái nào.
 * <p>
 * 4. HAI HÀNH VI ĐẶC BIỆT LÚC RESOLVE
 * - Hành vi 1, nguyên văn: "Once you place a plain JAR on the module path, the module system
 * pulls in all automatic modules once it encounters the first one that is explicitly
 * required."
 * + Tức là kéo cả cụm. Chỉ cần requires MỘT automatic module, TẤT CẢ automatic module trên
 * module path đều được nạp. Ngược hẳn với luật thông thường ở t1 mục 9.
 * - Hành vi 2, nguyên văn: automatic module có implied readability - "any module that reads
 * one, reads all of them."
 * + Tức là mọi automatic module đều ngầm {@code requires transitive} lẫn nhau (bài t4).
 * - Vì sao phải thế: module system không biết JAR cũ phụ thuộc vào cái gì, nên không dám
 * thiếu. Nó chọn giải pháp an toàn nhất là nối tất cả với tất cả.
 * - Cái giá phải trả: bạn mất gần hết lợi ích của reliable configuration đối với cụm
 * automatic module. Đây là trạng thái TẠM THỜI, không phải đích đến.
 * <p>
 * 5. CẢNH BÁO VỀ TÊN - VÀ MỘT LỆNH CẤM
 * - Vấn đề, nguyên văn: "If the JAR's manifest does not contain the Automatic-Module-Name
 * entry, the automatic module name is inferred from the file name."
 * + Mà tên file thì KHÔNG ổn định: Maven, Gradle, shaded JAR, bản repackage của công ty...
 * mỗi nơi một tên. Module của bạn lại khai {@code requires} đúng cái tên đó.
 * + Hậu quả: {@code module-info.java} của bạn gắn chặt vào tên file của người khác. Đổi
 * cách đóng gói là vỡ.
 * - Và đây là câu lệnh cấm của trang gốc, rất dứt khoát: "You should never publish (to an
 * openly accessible repository) modules that require a plain JAR without an
 * Automatic-Module-Name entry in its manifest."
 * + Dịch: dùng trong nội bộ thì được; đẩy lên Maven Central thì KHÔNG. Vì bạn sẽ ép mọi
 * người dùng vào một cái tên có thể thay đổi bất cứ lúc nào.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. BA LOẠI MODULE - BẢNG TỔNG HỢP CẦN THUỘC
 * <pre>
 *                      EXPLICIT              AUTOMATIC            UNNAMED
 *   Nguồn gốc          có module-info        plain JAR trên       mọi thứ trên
 *                      trên module path      module path          class path
 *   Có tên             có, tự đặt            có, suy ra           KHÔNG
 *   Ai requires được   mọi module            mọi module           không ai
 *   Đọc được gì        đúng cái đã khai      TẤT CẢ, kể cả        TẤT CẢ
 *                                            unnamed
 *   Mở ra ngoài        đúng cái đã khai      tất cả + opens       tất cả + opens
 * </pre>
 * - Nhìn bảng sẽ thấy automatic module đứng đúng giữa: có tên như explicit, dễ dãi như
 * unnamed. Nó là bậc thang trung gian, thiết kế để đứng tạm chứ không để ở lâu.
 * <p>
 * 7. CHIẾN LƯỢC DI CƯ BOTTOM-UP - THỨ TỰ ĐÚNG
 * - Nguyên tắc rút ra từ t7 mục 4 ("muốn requires thì artifact phải ở module path") cộng
 * với bài này: modular hóa từ DƯỚI LÊN.
 * + Bước 1: đẩy toàn bộ phụ thuộc lên module path, chấp nhận chúng thành automatic module.
 * + Bước 2: viết {@code module-info.java} cho module nền của mình, {@code requires} các
 * automatic module đó.
 * + Bước 3: dần dần, mỗi khi một thư viện phát hành bản có {@code module-info} thật, nó tự
 * động chuyển từ automatic sang explicit mà bạn không phải sửa gì (nếu tên không đổi).
 * - Sai lầm hay gặp: cố modular hóa module trên cùng (ứng dụng) trước. Không làm được, vì
 * mọi thứ nó phụ thuộc còn ở class path.
 * <p>
 * 8. CÁCH TRA TÊN CỦA MỘT JAR CỤ THỂ
 * - Lệnh nhanh nhất: {@code jar --file guava-32.1.3-jre.jar --describe-module}
 * + In ra tên module, và quan trọng là nói rõ nó là automatic hay explicit.
 * - Hoặc xem thẳng manifest: giải nén {@code META-INF/MANIFEST.MF} tìm dòng
 * {@code Automatic-Module-Name}.
 * - Mẹo khi đọc kết quả: nếu output ghi {@code automatic} thì tên đó CHƯA chắc ổn định -
 * phải kiểm tra tiếp xem nó đến từ manifest hay từ tên file (mục 5).
 * <p>
 * 9. BẮC CẦU TỪ TYPESCRIPT
 * - Tình huống y hệt: thư viện JS cũ không có file {@code .d.ts}, TypeScript không biết
 * kiểu. Cộng đồng vá bằng {@code @types/*}, còn TS thì cho phép
 * {@code declare module "x": any} để đi tiếp.
 * - Automatic module chính là {@code any} của thế giới module: nó cho bạn tiếp tục công
 * việc bằng cách giả định mọi thứ đều ổn, với đúng cái giá là mất kiểm tra.
 * - Và cũng như {@code any}, dấu hiệu dự án khỏe mạnh là số lượng automatic module GIẢM
 * dần theo thời gian.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Automatic module khác unnamed module ở điểm quyết định nào?
 * - Hai bước suy ra tên? {@code commons-lang3-3.12.0.jar} cho tên gì?
 * - Automatic module đọc được những gì? Vì sao vế "including the unnamed module" lại quan
 * trọng với việc di cư?
 * - Hai hành vi đặc biệt lúc resolve, và vì sao module system phải làm vậy?
 * - Vì sao KHÔNG được publish module requires một plain JAR thiếu
 * {@code Automatic-Module-Name}?
 * - Modular hóa nên đi từ trên xuống hay từ dưới lên? Vì sao chiều kia không khả thi?
 * - Lệnh nào tra nhanh tên module của một JAR?
 */
public class t8_AutomaticModules {

    /** Tạo một plain JAR (không có module-info) trong thư mục riêng, trả về thư mục đó. */
    private static Path plainJar(String tenFile, String automaticModuleName) throws IOException {
        Path dir = Files.createTempDirectory("auto");
        Manifest man = new Manifest();
        man.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (automaticModuleName != null) {
            man.getMainAttributes().putValue("Automatic-Module-Name", automaticModuleName);
        }
        try (OutputStream os = Files.newOutputStream(dir.resolve(tenFile));
             JarOutputStream jar = new JarOutputStream(os, man)) {
            jar.putNextEntry(new JarEntry("com/demo/Dummy.class"));  // để JAR có 1 package
            jar.closeEntry();
        }
        return dir;
    }

    /** Hỏi module system: JAR này nếu đặt lên module path thì mang tên gì? */
    private static void inTenModule(String tenFile, String automaticModuleName)
            throws IOException {
        Path dir = plainJar(tenFile, automaticModuleName);
        ModuleFinder.of(dir).findAll().forEach(ref -> System.out.println(
                "   " + tenFile + "\n        ->  tên module: " + ref.descriptor().name()
                        + "   (automatic? " + ref.descriptor().isAutomatic() + ")"));
    }

    void main() throws IOException {
        // 1. Không có Automatic-Module-Name -> tên SUY RA TỪ TÊN FILE (mục 2, bước 2)
        System.out.println("Tên suy ra từ tên file:");
        inTenModule("guava-32.1.3-jre.jar", null);
        inTenModule("commons-lang3-3.12.0.jar", null);
        inTenModule("my-cool_lib-1.0-SNAPSHOT.jar", null);

        // 2. Có Automatic-Module-Name trong manifest -> lấy luôn (mục 2, bước 1)
        System.out.println("\nCùng tên file, nhưng manifest có Automatic-Module-Name:");
        inTenModule("commons-lang3-3.12.0.jar", "org.apache.commons.lang3");
        System.out.println("   -> ĐÂY là lý do mục 5 cấm publish module requires một plain JAR"
                + " thiếu header này: tên kia phụ thuộc vào tên file, có thể đổi bất cứ lúc nào.");

        System.out.println();
        // TODO 1 (mục 2, 8): Chọn 5 JAR bất kỳ trong ~/.m2 (gợi ý: guava, commons-lang3,
        //   jackson-databind, slf4j-api, lombok). Chạy
        //   `jar --file <ten>.jar --describe-module` cho từng cái. Lập bảng 3 cột:
        //   tên module / explicit hay automatic / tên lấy từ manifest hay từ tên file.
        //
        // TODO 2 (mục 2): Với JAR không có Automatic-Module-Name, tự suy tên theo luật ở
        //   mục 2 TRƯỚC khi chạy lệnh, rồi so với kết quả thật. Làm 3 lần cho quen luật.
        //
        // TODO 3 (mục 1, 3): Lấy demo.greet ở bài t1, XÓA module-info.class khỏi JAR để nó
        //   thành plain JAR. Đặt lên module path. Viết demo.app requires đúng cái tên suy
        //   ra từ tên file. Xác nhận: giờ truy cập được cả package trước đây KHÔNG exports.
        //   Giải thích bằng mục 3.
        //
        // TODO 4 (mục 2): Thêm `Automatic-Module-Name: com.demo.greet` vào manifest của
        //   plain JAR đó (dùng `jar --create --manifest=...`). Xác nhận tên module đổi, và
        //   demo.app phải sửa requires theo. Đây là trải nghiệm trực tiếp của vấn đề ở mục 5.
        //
        // TODO 5 (mục 4): Đặt BA plain JAR lên module path, demo.app chỉ requires MỘT cái.
        //   In ModuleLayer.boot().modules() - có bao nhiêu cái được nạp? Rồi thử dùng lớp
        //   của JAR thứ ba mà không khai requires nó -> có biên dịch được không? Giải thích
        //   bằng hai hành vi ở mục 4.
        //
        // TODO 6 (mục 3, 7): Dựng đúng tình huống di cư: demo.app (explicit, module path)
        //   -> demo.oldlib (plain JAR, module path, tức automatic) -> demo.ancient (plain
        //   JAR, CLASS path, tức unnamed). Xác nhận chuỗi này chạy được, rồi thử cho
        //   demo.app gọi thẳng demo.ancient -> lỗi. Vẽ lại sơ đồ mũi tên vào note.
        //
        // TODO 7 (mục 5, 9): Đếm trong một dự án thật của bạn: bao nhiêu phụ thuộc đã có
        //   module-info, bao nhiêu chỉ có Automatic-Module-Name, bao nhiêu không có gì cả.
        //   Con số thứ ba là thước đo bạn còn cách việc modular hóa bao xa.
    }
}
