package com.javacore.organizing.modules;

/**
 * Circumventing Strong Encapsulation with --add-exports and --add-opens
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 11/12 của section Modules.
 * - Bài t10 nói VÌ SAO bị chặn; bài này nói LÀM SAO đi qua. Hai bài phải đọc liền nhau.
 * - Một câu cho cả bài: {@code --add-exports} và {@code --add-opens} là phiên bản dòng lệnh
 * của {@code exports ... to} và {@code opens ... to} ở bài t5 - cùng ngữ nghĩa, chỉ khác
 * người ra quyết định: không phải tác giả module, mà là người CHẠY ứng dụng.
 * - Đây là bài có giá trị tra cứu cao nhất section. Học thuộc cú pháp là tiết kiệm được
 * hàng giờ mỗi lần nâng cấp JDK.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/add-exports-opens/">dev.java - Circumventing Strong Encapsulation</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc.
 * <p>
 * ======================================================================
 * TRANG GỐC - ADD-EXPORTS VÀ ADD-OPENS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. LỜI CẢNH BÁO ĐẶT TRƯỚC - ĐỌC TRƯỚC KHI DÙNG
 * - Nguyên văn: "Since this comes with a trade-off between more features or performance
 * (presumably) versus less maintainability or undermined platform integrity, this decision
 * should not be made lightly."
 * - Chú ý chữ PRESUMABLY trong ngoặc - trang gốc hoài nghi ngay cả cái lợi. Thường thì cái
 * giá là chắc chắn, còn cái lợi chỉ là giả định.
 * - Nhớ một câu để tự kiểm: mỗi dòng {@code --add-opens} là một món NỢ KỸ THUẬT có ngày
 * đáo hạn - ngày bạn nâng JDK lần sau.
 * <p>
 * 2. CÚ PHÁP --ADD-EXPORTS
 * - Dạng chuẩn:
 * <pre>
 *   --add-exports MODULE/PACKAGE=READING_MODULE
 * </pre>
 * - Dạng thay dấu cách bằng dấu bằng cũng hợp lệ:
 * {@code --add-exports=MODULE/PACKAGE=READING_MODULE}. Dạng này cần khi truyền cờ qua
 * biến môi trường hoặc file argument, nơi dấu cách bị cắt.
 * - Ngữ nghĩa, nguyên văn: "Code in READING_MODULE can hence access all public types and
 * members in PACKAGE but other modules can not."
 * - Đọc kỹ cấu trúc ba phần, luôn theo thứ tự "mở của AI / package NÀO = cho AI":
 * + MODULE: module SỞ HỮU package (bên bị mở).
 * + PACKAGE: package cần mở.
 * + READING_MODULE: module ĐƯỢC PHÉP đọc (bên được lợi).
 * - Và nhớ: {@code --add-exports} chỉ mở phần PUBLIC - đúng như {@code exports} ở t2.
 * <p>
 * 3. ALL-UNNAMED - GIÁ TRỊ BẠN SẼ DÙNG NHIỀU NHẤT
 * - Nguyên văn: "When setting READING_MODULE to ALL-UNNAMED, all code from the class path
 * can access that package. In a project that doesn't use modules, you will always use that
 * placeholder."
 * - Câu cuối rất đáng nhớ: dự án KHÔNG dùng module (tức gần như mọi dự án Spring Boot) thì
 * LUÔN LUÔN dùng {@code ALL-UNNAMED}. Không có lựa chọn nào khác, vì code của bạn nằm
 * trong unnamed module (bài t7) và nó không có tên để mà ghi.
 * - Hai ví dụ của trang gốc đặt cạnh nhau:
 * <pre>
 *   --add-exports jdk.internal/sun.util=ALL-UNNAMED            // cho class path
 *   --add-exports jdk.internal/sun.util=com.example.internal   // cho một module cụ thể
 * </pre>
 * <p>
 * 4. DÙNG Ở HAI THỜI ĐIỂM
 * - Lúc biên dịch: {@code javac --add-exports jdk.internal/sun.util=ALL-UNNAMED MyClass.java}
 * - Lúc chạy: {@code java --add-exports jdk.internal/sun.util=ALL-UNNAMED MyClass}
 * - Đây là cái bẫy hay gặp nhất: sửa được lỗi biên dịch rồi tưởng xong, lúc chạy lại lỗi
 * đúng chỗ đó. Với {@code --add-exports}, thường phải khai ở CẢ HAI.
 * <p>
 * 5. CÚ PHÁP --ADD-OPENS VÀ MỘT KHÁC BIỆT QUAN TRỌNG
 * - Dạng chuẩn:
 * <pre>
 *   --add-opens MODULE/PACKAGE=REFLECTING_MODULE
 * </pre>
 * - Ngữ nghĩa, nguyên văn: "Code in REFLECTING_MODULE can hence reflectively access all
 * types and members, public and non-public ones, in PACKAGE but other modules can not."
 * + Chú ý "public and non-public" - mở sâu hơn hẳn {@code --add-exports}, đúng quan hệ
 * giữa {@code opens} và {@code exports} ở bài t2.
 * - KHÁC BIỆT QUAN TRỌNG, nguyên văn: "Since --add-opens is bound to reflection, a pure
 * run time concept, it only makes sense for the java command."
 * + Truyền cho {@code javac} thì nó không từ chối, nhưng in cảnh báo: "--add-opens has no
 * effect at compile time".
 * + Nhớ cặp này cho gọn: {@code --add-exports} dùng cho CẢ javac lẫn java;
 * {@code --add-opens} CHỈ dùng cho java.
 * - Ví dụ:
 * <pre>
 *   java --add-opens jdk.internal/sun.util=ALL-UNNAMED MyClass
 *   java --add-opens jdk.internal/sun.util=com.example.internal MyClass
 * </pre>
 * <p>
 * 6. TRIẾT LÝ: QUYẾT ĐỊNH THUỘC VỀ NGƯỜI CHẠY
 * - Nguyên văn: cả hai cờ "applied at launch time, so the user is aware that the trade-off
 * is being made."
 * - Đây là ý nối tiếp t10 mục 4: không cấm tuyệt đối, nhưng bắt lộ thiên. Người dùng - chứ
 * không chỉ lập trình viên - phải đồng ý một cách tường minh.
 * - Hệ quả thiết kế đáng nhớ: một THƯ VIỆN không thể tự ý phá rào giúp bạn. Nó chỉ có thể
 * ghi trong README "hãy thêm cờ này". Nếu một thư viện nào đó chạy được mà không cần cờ,
 * nghĩa là nó đang dùng đường chính thống - đó là dấu hiệu tốt khi chọn thư viện.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BẢNG TRA NHANH - DÁN VÀO ĐẦU
 * <pre>
 *   Lỗi nói gì                    Cờ cần dùng        Đặt cho javac?   Đặt cho java?
 *   "... is not exported ..."     --add-exports      CÓ               CÓ
 *   "... does not opens ..."      --add-opens        không (vô ích)   CÓ
 *
 *   Code của tôi ở đâu            Vế phải dấu bằng
 *   class path (không module)     ALL-UNNAMED
 *   module tường minh             ten.module.cua.toi
 * </pre>
 * - Thuộc bảng này là xử lý được 95% lỗi module gặp trong dự án thật.
 * <p>
 * 8. ĐẶT CỜ Ở ĐÂU TRONG DỰ ÁN THẬT
 * - Maven Surefire (cho test):
 * <pre>
 *   &lt;argLine&gt;--add-opens java.base/java.lang=ALL-UNNAMED&lt;/argLine&gt;
 * </pre>
 * - maven-compiler-plugin (cho biên dịch): đưa {@code --add-exports ...} vào
 * {@code compilerArgs}.
 * - Lúc chạy production: thêm vào dòng {@code java ...} trong Dockerfile/systemd, hoặc đặt
 * vào biến môi trường {@code JAVA_TOOL_OPTIONS} (JVM tự đọc, tiện khi không sửa được lệnh
 * khởi động - nhưng nhớ là nó áp cho MỌI tiến trình JVM trên máy đó).
 * - Dạng ít người biết nhưng rất hữu ích: file argument. Ghi các cờ vào một file rồi gọi
 * {@code java @jvm-args.txt -jar app.jar}. Đây là lúc buộc phải dùng dạng có dấu bằng ở
 * mục 2, và là cách giữ cho dòng lệnh khởi động còn đọc được khi danh sách cờ dài ra.
 * <p>
 * 9. MANIFEST ADD-EXPORTS / ADD-OPENS - CHỈ CHO EXECUTABLE JAR
 * - JAR chạy bằng {@code java -jar} có thể khai sẵn trong {@code MANIFEST.MF}:
 * <pre>
 *   Add-Exports: java.base/sun.nio.ch
 *   Add-Opens: java.base/java.lang
 * </pre>
 * - Giới hạn phải nhớ: CHỈ có tác dụng khi JAR đó được chạy bằng {@code java -jar}. Đặt
 * trong một thư viện bình thường thì vô tác dụng - và đây đúng là điều mục 6 muốn: thư
 * viện không được tự phá rào, chỉ ứng dụng mới được.
 * <p>
 * 10. QUY TRÌNH XỬ LÝ KHI GẶP LỖI - LÀM THEO THỨ TỰ
 * - Bước 1: đọc thông báo, xác định {@code exports} hay {@code opens} (bảng mục 7).
 * - Bước 2: TRƯỚC KHI thêm cờ, kiểm tra xem thư viện đã có bản mới không. Phần lớn lỗi loại
 * này đã được vá ở phiên bản sau.
 * - Bước 3: nếu buộc phải thêm cờ, thêm HẸP NHẤT có thể - đúng một package, không mở cả
 * module.
 * - Bước 4: ghi comment ngay cạnh cờ: thư viện nào cần, từ phiên bản nào, link issue. Không
 * có comment thì sáu tháng sau không ai dám xóa.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Viết cú pháp đầy đủ của cả hai cờ, chỉ rõ ba phần?
 * - Bên trái dấu bằng là module nào - bên mở hay bên được đọc?
 * - Khi nào bắt buộc dùng {@code ALL-UNNAMED}?
 * - Cờ nào vô tác dụng với javac, và javac phản ứng thế nào khi nhận nó?
 * - {@code --add-exports} mở tới mức nào, {@code --add-opens} mở tới mức nào?
 * - Vì sao JPMS bắt đặt cờ lúc khởi động thay vì cho thư viện tự khai?
 * - Khai {@code Add-Opens} trong manifest của một thư viện có tác dụng không? Vì sao?
 * - Bốn bước xử lý khi gặp lỗi, và vì sao bước 2 đứng trước bước 3?
 */
public class t11_CircumventingStrongEncapsulation {

    void main() throws Exception {
        Module here = getClass().getModule();
        Module base = String.class.getModule();

        // 1. Trạng thái HIỆN TẠI của bức tường - hỏi trước khi phá (mục 2, mục 5)
        System.out.println("java.base exports java.lang cho ta ? "
                + base.isExported("java.lang", here));
        System.out.println("java.base opens   java.lang cho ta ? "
                + base.isOpen("java.lang", here));

        // 2. Thử thao tác bị chặn
        boolean soiDuoc;
        try {
            java.lang.reflect.Field f = String.class.getDeclaredField("value");
            f.setAccessible(true);
            soiDuoc = true;
        } catch (RuntimeException e) {
            soiDuoc = false;
        }
        System.out.println("soi được field private của String ? " + soiDuoc);

        // 3. Tự in ra dòng cờ cần thêm, đúng cú pháp MODULE/PACKAGE=READING_MODULE (mục 2)
        if (!soiDuoc) {
            System.out.println("\nCần chạy lại với cờ (chép nguyên dòng dưới):");
            System.out.println("   java --add-opens java.base/java.lang=ALL-UNNAMED"
                    + " -cp <classpath> " + getClass().getName());
            System.out.println("   ALL-UNNAMED vì code này nằm trong unnamed module (mục 3)");
            System.out.println("Chạy lại với cờ đó rồi so ba dòng đầu: isOpen đổi false -> true.");
        } else {
            System.out.println("\nĐang chạy CÓ --add-opens: isOpen đã thành true,"
                    + " và setAccessible qua được. Đây là toàn bộ tác dụng của cờ (mục 5).");
        }

        // 4. Cùng package đó, --add-exports và --add-opens mở tới hai mức khác nhau (mục 5)
        System.out.println("\nmức mở của hai cờ:");
        System.out.println("   --add-exports -> chỉ public       (isExported)");
        System.out.println("   --add-opens   -> public + private  (isOpen)");

        System.out.println();
        // TODO 1 (mục 2, 5): Lấy lại chương trình ở TODO 3 bài t10 (đọc field private của
        //   String). Chạy lần lượt với --add-exports rồi với --add-opens. Cái nào sửa được
        //   lỗi? Giải thích bằng mục 5 ("public and non-public").
        //
        // TODO 2 (mục 5): Truyền --add-opens cho JAVAC. Chép nguyên văn cảnh báo
        //   "--add-opens has no effect at compile time". Ghi nhớ cảm giác này - nhiều người
        //   mất cả buổi vì dán cờ vào compilerArgs thay vì argLine.
        //
        // TODO 3 (mục 3): Dựng hai tình huống cho cùng một việc:
        //   (a) code gọi nằm trên class path -> buộc dùng ALL-UNNAMED
        //   (b) code gọi nằm trong module tường minh -> dùng tên module
        //   Thử tráo hai vế xem lỗi ra sao (ví dụ dùng ALL-UNNAMED khi code ở trong module).
        //
        // TODO 4 (mục 4): Lấy một package nội bộ của JDK (gợi ý: java.base/sun.nio.ch),
        //   viết code import thẳng một lớp trong đó. Xác nhận phải thêm --add-exports cho
        //   CẢ javac lẫn java. Bỏ cờ ở một bên để thấy lỗi tương ứng.
        //
        // TODO 5 (mục 8): Viết cùng bộ cờ đó theo ba cách: trực tiếp trên dòng lệnh, qua
        //   file argument (java @args.txt), và qua JAVA_TOOL_OPTIONS. Lưu ý cách thứ hai
        //   bắt buộc dùng dạng --add-opens=... (dấu bằng).
        //
        // TODO 6 (mục 9): Đóng một executable JAR có Add-Opens trong manifest, chạy bằng
        //   `java -jar`. Xác nhận chạy được không cần cờ. Sau đó đặt chính JAR đó lên class
        //   path của một ứng dụng khác -> manifest còn tác dụng không? Giải thích bằng mục 6.
        //
        // TODO 7 (mục 7, 10): Tự tạo cho mình một file tra cứu riêng: 3 lỗi module gặp thật
        //   (từ dự án hoặc từ các TODO trên), mỗi lỗi ghi đủ 4 dòng theo quy trình mục 10.
    }
}
