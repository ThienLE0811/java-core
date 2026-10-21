package com.javacore.organizing.modules;

/**
 * Strong Encapsulation (of JDK Internals)
 * <p>
 * 0. Vị trí bài này
 * - Bước 30/41, bài 10/12 của section Modules. Mở đầu cụm ba bài cuối, cụm DÒNG LỆNH.
 * - Đây là bài mà người làm Java ở Việt Nam gặp nhiều nhất trong đời đi làm, dù không biết
 * tên nó: mọi dòng {@code --add-opens java.base/java.lang=ALL-UNNAMED} dán vào pom.xml hay
 * script khởi động đều bắt nguồn từ đây.
 * - Một câu cho cả bài: từ Java 9 JDK tự đóng gói ruột của mình, từ Java 17 thì đóng
 * CỨNG - không còn cờ nới lỏng toàn cục, chỉ còn cách mở từng package một cách tường minh.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/organizing/modules/strong-encapsulation/">dev.java - Strong Encapsulation (of JDK Internals)</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Các output lệnh trong note
 * đã chạy thật trên JDK 25.0.4.1.
 * <p>
 * ======================================================================
 * TRANG GỐC - STRONG ENCAPSULATION OF JDK INTERNALS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. BA VÙNG TÊN TRONG JDK - LUẬT NHẬN DIỆN
 * <p>
 * - 1a. {@code java.*} - API CÔNG KHAI
 * + Là API chính thức, nhưng trang gốc nhắc một giới hạn: chỉ "only public members of
 * public classes" là truy cập được. Thứ ít lộ hơn vẫn là nội bộ.
 * + Tức là ngay trong {@code java.*} cũng có phần ruột (ví dụ field private của
 * {@code String}) - và đó là chỗ các framework hay chọc vào.
 * <p>
 * - 1b. {@code sun.*} - GẦN NHƯ TOÀN BỘ LÀ NỘI BỘ
 * + "Almost all such packages are internal", với hai ngoại lệ: {@code sun.misc} và
 * {@code sun.reflect}, được export qua module {@code jdk.unsupported}.
 * + Kết luận của trang gốc: "Generally speaking, sun.* packages should be seen as internal."
 * + Kiểm chứng thật trên JDK 25 ({@code java --describe-module jdk.unsupported}):
 * <pre>
 *   jdk.unsupported@25.0.4.1
 *   exports com.sun.nio.file
 *   exports sun.misc
 *   exports sun.reflect
 *   requires java.base mandated
 *   opens sun.reflect
 *   opens sun.misc
 * </pre>
 * + Chú ý tên module: UNSUPPORTED. JDK nói thẳng "chúng tôi cho anh dùng nhưng không hứa
 * gì cả". Và nó vừa {@code exports} vừa {@code opens} - tức là mở cả cho reflection.
 * <p>
 * - 1c. {@code com.sun.*} - PHA TRỘN, PHẢI CẨN THẬN
 * + "approximately 90% of it are non-exported packages and they are internal", 10% còn lại
 * được các module {@code jdk.*} export ra như API được hỗ trợ.
 * + Không có cách nhìn tên mà biết; phải tra bằng {@code --describe-module}.
 * <p>
 * - 1d. CÂU TÓM TẮT CẦN THUỘC, nguyên văn: "use java.*, avoid sun.*, be careful with
 * com.sun.*."
 * <p>
 * 2. SUN.MISC.UNSAFE - CA NGOẠI LỆ
 * - Trang gốc giải thích vì sao lớp này được đối xử riêng: nó cung cấp "functionality that
 * is critical to a number of projects and doesn't have feasible alternatives within or
 * outside the JDK."
 * - Danh sách "một số dự án" đó không nhỏ: Netty, Hibernate, Cassandra, Spring, mọi thư
 * viện serialization hiệu năng cao. Nếu đóng cứng {@code Unsafe} thì nửa hệ sinh thái Java
 * ngừng chạy.
 * - Đó là lý do có module {@code jdk.unsupported} ở mục 1b: một vùng đệm chính thức cho
 * những thứ không chính thức.
 * <p>
 * 3. LỊCH SỬ SIẾT DẦN - BA MỐC PHẢI NHỚ
 * - Java 9 đến 15: cờ {@code --illegal-access} mặc định là {@code permit}. Code trên class
 * path vẫn chọc được vào ruột JDK, chỉ bị cảnh báo. Đây là thời kỳ "cảnh báo nhưng vẫn cho
 * qua", và là lý do rất nhiều ứng dụng cũ sống sót qua Java 11 mà không sửa gì.
 * - Java 16: mặc định đổi thành {@code deny}. Ai cần thì phải tự bật lại.
 * - Java 17 trở đi: cờ bị VÔ HIỆU HÓA hoàn toàn. Nguyên văn: "only --add-exports and
 * --add-opens give access to internal APIs."
 * - Kiểm chứng thật trên JDK 25:
 * <pre>
 *   $ java --illegal-access=permit -version
 *   Java HotSpot(TM) 64-Bit Server VM warning: Ignoring option
 *   --illegal-access=permit; support was removed in 17.0
 * </pre>
 * + Để ý chữ IGNORING: JVM không báo lỗi, chỉ lờ đi. Nên nếu script khởi động cũ của công
 * ty còn dòng này, nó đang KHÔNG có tác dụng gì - và lỗi thật nằm ở chỗ khác.
 * <p>
 * 4. VÌ SAO BẮT CẤU HÌNH LÚC KHỞI ĐỘNG - LẬP LUẬN CỦA TRANG GỐC
 * - Nguyên văn: "the exceptions need to be configured when launching the app. That gives
 * the app's owner full transparency into these issues and allows them to assess the
 * situation."
 * - Đây là triết lý thiết kế đáng ghi lại: JDK không cấm tuyệt đối, nó chỉ bắt việc phá rào
 * phải HIỆN RA ở lệnh khởi động. Người VẬN HÀNH ứng dụng nhìn thấy, chứ không phải lập
 * trình viên thư viện lén làm sau lưng.
 * - Hệ quả thực tế rất dễ chịu: muốn biết một ứng dụng đang phá bao nhiêu rào, chỉ cần nhìn
 * dòng lệnh khởi động của nó. Không cần đọc code.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. VÌ SAO JAVA.BASE KHÔNG OPENS GÌ CẢ
 * - Chạy thật trên JDK 25: {@code java --describe-module java.base} có ĐÚNG 0 dòng
 * {@code opens}.
 * - Đây chính là lý do kỹ thuật của dòng cờ quen thuộc nhất:
 * <pre>
 *   --add-opens java.base/java.lang=ALL-UNNAMED
 * </pre>
 * + Không phải "ai đó quên mở", mà là CỐ TÌNH không mở. Mọi thư viện cần soi vào
 * {@code java.lang} (một số công cụ mock, một số thư viện serialization, vài agent) đều
 * phải xin phép ở dòng lệnh.
 * - Nhớ phân biệt lần cuối với bài t2: {@code exports}/{@code opens} là CHỦ NHÀ tự mở;
 * {@code --add-exports}/{@code --add-opens} là NGƯỜI CHẠY phá khóa từ ngoài. JDK là chủ
 * nhà, nó đã quyết không mở - nên chỉ còn đường thứ hai.
 * <p>
 * 6. ĐỌC MỘT THÔNG BÁO LỖI THẬT
 * <pre>
 *   java.lang.reflect.InaccessibleObjectException: Unable to make field
 *   private final byte[] java.lang.String.value accessible: module java.base
 *   does not "opens java.lang" to unnamed module @0x1b6d3586
 * </pre>
 * - Công thức dịch sang cờ cần thêm, luôn đúng theo ba bước:
 * + lấy {@code module java.base} -&gt; phần trước dấu gạch chéo.
 * + lấy {@code opens java.lang} -&gt; phần sau dấu gạch chéo, và chọn {@code --add-opens}
 * (nếu thông báo ghi {@code exports} thì chọn {@code --add-exports}).
 * + lấy {@code to unnamed module} -&gt; vế phải là {@code ALL-UNNAMED}.
 * + Ghép lại: {@code --add-opens java.base/java.lang=ALL-UNNAMED}. Chi tiết cú pháp ở t11.
 * <p>
 * 7. XU HƯỚNG SAU NÀY - INTEGRITY BY DEFAULT
 * - Hướng đi của OpenJDK sau Java 17 là siết tiếp, không nới ra: các lối phá rào còn lại
 * (nạp agent động, truy cập bộ nhớ qua {@code Unsafe}, JNI) lần lượt chuyển từ "im lặng
 * cho phép" sang "cảnh báo" rồi sang "phải bật bằng cờ". Tên gọi chung trong các tài liệu
 * OpenJDK là "integrity by default".
 * - Điều cần làm với thông tin này: ĐỪNG coi {@code --add-opens} là giải pháp lâu dài. Nó
 * là thuốc giảm đau để nâng cấp JDK được ngay; việc thật là chờ hoặc thúc thư viện cập nhật.
 * - Cách tự kiểm chứng trên chính JDK đang dùng: chạy chương trình với cờ
 * {@code --enable-native-access} hoặc thử dùng {@code sun.misc.Unsafe} và quan sát cảnh
 * báo mà JVM in ra (TODO 5). Mỗi bản JDK mỗi khác, nên hãy tin vào output trước mặt hơn là
 * tin tài liệu cũ.
 * <p>
 * 8. GẶP TRONG DỰ ÁN THẬT - BA TÌNH HUỐNG KINH ĐIỂN
 * - Nâng Java 8 lên 17: hàng loạt thư viện cũ chết vì mất {@code --illegal-access=permit}.
 * Cách xử lý đúng: nâng thư viện trước, chỉ thêm {@code --add-opens} cho cái không nâng được.
 * - Chạy test: Mockito/ByteBuddy, các thư viện reflection trong test thường cần cờ. Thấy
 * cờ trong cấu hình Surefire là vì vậy.
 * - Công cụ giám sát / APM: agent đọc nội bộ JVM, gần như luôn cần một bộ cờ riêng do nhà
 * cung cấp công bố.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Câu tóm tắt ba vùng tên của trang gốc?
 * - Hai package {@code sun.*} nào được export, và qua module nào? Tên module đó nói lên gì?
 * - Vì sao {@code sun.misc.Unsafe} được đối xử đặc biệt?
 * - Ba mốc của {@code --illegal-access} (9-15, 16, 17+)? Trên JDK hiện tại, truyền cờ đó
 * thì JVM làm gì?
 * - Từ Java 17, còn đúng hai cách nào để truy cập API nội bộ?
 * - {@code java.base} có bao nhiêu directive {@code opens}? Vì sao con số đó quan trọng?
 * - Nhìn một {@code InaccessibleObjectException}, đọc ra dòng cờ cần thêm theo ba bước nào?
 * - Lập luận của trang gốc về việc bắt cấu hình ở lệnh khởi động?
 */
public class t10_StrongEncapsulationOfJdkInternals {

    void main() {
        Module here = getClass().getModule();
        Module base = String.class.getModule();

        // 1. Ba vùng tên, kiểm chứng bằng API thay vì tin tài liệu (mục 1)
        System.out.println("java.* công khai  : java.base exports java.util ? "
                + base.isExported("java.util"));
        System.out.println("sun.*  nội bộ     : java.base exports sun.nio.ch ? "
                + base.isExported("sun.nio.ch"));

        // 2. Ngoại lệ sun.misc / sun.reflect nằm ở module jdk.unsupported (mục 1b, mục 2)
        Module unsup = ModuleLayer.boot().findModule("jdk.unsupported").orElseThrow();
        System.out.println("\njdk.unsupported exports sun.misc ? " + unsup.isExported("sun.misc"));
        System.out.println("jdk.unsupported opens   sun.misc ? " + unsup.isOpen("sun.misc", here));
        System.out.println("   -> tên module nói thẳng: UNSUPPORTED. Dùng được nhưng không hứa gì.");

        // 3. Con số quan trọng nhất bài: java.base KHÔNG opens package nào (mục 5)
        System.out.println("\njava.base có " + base.getDescriptor().opens().size()
                + " directive opens   <- đây là lý do phải dùng --add-opens");

        // 4. Hệ quả nhìn thấy được, và công thức dịch lỗi thành cờ (mục 6)
        try {
            java.lang.reflect.Field f = String.class.getDeclaredField("value");
            f.setAccessible(true);
            System.out.println("\nsoi String.value: ĐƯỢC (bạn đang chạy có --add-opens)");
        } catch (ReflectiveOperationException | RuntimeException e) {
            System.out.println("\n" + e.getMessage());
            System.out.println("   dịch theo 3 bước mục 6 -> cần thêm cờ:");
            System.out.println("   --add-opens java.base/java.lang=ALL-UNNAMED");
        }

        System.out.println();
        // TODO 1 (mục 1b): Chạy `java --describe-module jdk.unsupported` trên máy mình, so
        //   với output chép ở mục 1b. Có khác gì không? Rồi thử
        //   `java --describe-module jdk.internal.misc` - module này có tồn tại không, và
        //   nó export gì ra ngoài?
        //
        // TODO 2 (mục 3): Chạy `java --illegal-access=permit -version`. Chép nguyên văn
        //   cảnh báo. Ghi nhớ: nếu script khởi động ở công ty còn dòng này thì nó đang vô
        //   tác dụng.
        //
        // TODO 3 (mục 5, 6): Viết một lớp dùng reflection đọc field private
        //   `java.lang.String.value`. Chạy KHÔNG cờ -> chép nguyên văn
        //   InaccessibleObjectException. Áp công thức ba bước ở mục 6 để tự suy ra dòng cờ,
        //   rồi chạy lại với cờ đó. Đây là bài tập giá trị nhất của cả bài.
        //
        // TODO 4 (mục 1a, 1c): Chọn 3 package com.sun.* bất kỳ (gợi ý: com.sun.net.httpserver,
        //   com.sun.management, com.sun.org.apache.xerces.internal). Với mỗi cái, dùng
        //   --describe-module để xác định nó thuộc module nào và có được exports không.
        //   Phân loại chúng vào nhóm 10% hay 90% ở mục 1c.
        //
        // TODO 5 (mục 2, 7): Viết code lấy sun.misc.Unsafe qua reflection
        //   (Unsafe.class.getDeclaredField("theUnsafe")) rồi gọi một method truy cập bộ nhớ.
        //   Chạy trên JDK 25 và chép lại MỌI cảnh báo JVM in ra. Tự trả lời: JDK đang ở
        //   giai đoạn nào của tiến trình siết ở mục 7?
        //
        // TODO 6 (mục 8): Mở pom.xml/script khởi động của một dự án thật bạn từng làm, tìm
        //   mọi dòng --add-opens / --add-exports. Với từng dòng: thư viện nào cần nó, và
        //   phiên bản mới của thư viện đó đã bỏ được chưa? Đây là bài tập sát việc nhất.
    }
}
