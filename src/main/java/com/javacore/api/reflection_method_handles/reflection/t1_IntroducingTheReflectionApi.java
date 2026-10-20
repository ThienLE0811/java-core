package com.javacore.api.reflection_method_handles.reflection;

/**
 * Introducing the Reflection API
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 1/13 - bài MỞ ĐẦU của section The Reflection API.
 * - Bài ngắn nhất section và không có một dòng code nào. Nó chỉ làm một việc: liệt kê ba ca
 * dùng chính đáng và ba cái giá phải trả. Đọc kỹ ngay từ đầu, vì 12 bài sau toàn là "làm
 * được gì" - không bài nào quay lại hỏi "có nên làm không".
 * - Một câu cho cả bài: reflection mở ra những việc không thể làm bằng cách khác, nhưng mọi
 * thứ nó mở ra đều đi kèm mất mát - mất tối ưu của JVM, mất bảo vệ của module, mất đóng gói.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/intro/">dev.java - Introducing the Reflection API</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRODUCING THE REFLECTION API (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. REFLECTION DÙNG ĐỂ LÀM GÌ
 * - Câu định vị nguyên văn, cũng là câu mở đầu cả section: "Reflection is commonly used by
 * programs which require the ability to examine or modify the runtime behavior of
 * applications running in the Java virtual machine."
 * - Cảnh báo đi kèm ngay, nguyên văn: "This is a relatively advanced feature and should be
 * used only by developers who have a strong grasp of the fundamentals of the language."
 * - Nhưng vẫn khẳng định giá trị: "reflection is a powerful technique and can enable
 * applications to perform operations which would otherwise be impossible."
 * <p>
 * 2. BA CA DÙNG CHÍNH ĐÁNG
 * - (Uses of Reflection)
 * <p>
 * - 2a. EXTENSIBILITY FEATURES - tính mở rộng
 * + "An application may make use of external, user-defined classes by creating instances of
 * extensibility objects using their fully-qualified names."
 * + Dịch sang đời thực: hệ thống plugin. Ứng dụng nhận TÊN LỚP dạng chuỗi từ file cấu hình
 * rồi tự tạo instance - lớp đó chưa tồn tại lúc bạn biên dịch ứng dụng.
 * + Đây là ca dùng của Spring ({@code @Component} trong file cấu hình), của JDBC driver,
 * của {@code ServiceLoader}.
 * <p>
 * - 2b. CLASS BROWSERS VÀ IDE
 * + "A class browser needs to be able to enumerate the members of classes. Visual
 * development environments can benefit from making use of type information available in
 * reflection to aid the developer in writing correct code."
 * + Chính là autocomplete của IntelliJ khi bạn gõ dấu chấm.
 * <p>
 * - 2c. DEBUGGER VÀ CÔNG CỤ TEST
 * + "Debuggers need to be able to examine private members on classes. Test harnesses can
 * make use of reflection to systematically call a discoverable set APIs defined on a class,
 * to insure a high level of code coverage in a test suite."
 * + Chính là JUnit: tìm mọi method có {@code @Test} rồi gọi, dù method đó không public.
 * <p>
 * - Nhận xét gom lại: cả ba ca đều là CÔNG CỤ hoặc FRAMEWORK - code không biết trước nó sẽ
 * làm việc với lớp nào. Không ca nào là code nghiệp vụ thường ngày. Đây là bộ lọc tốt nhất:
 * nếu việc bạn đang làm không rơi vào ba nhóm này, nhiều khả năng bạn đang dùng sai công cụ.
 * <p>
 * 3. BA CÁI GIÁ PHẢI TRẢ
 * - (Drawbacks of Reflection)
 * - Nguyên tắc chung, nguyên văn: "Reflection is powerful, but should not be used
 * indiscriminately. If it is possible to perform an operation without using reflection, then
 * it is preferable to avoid using it."
 * <p>
 * - 3a. PERFORMANCE OVERHEAD - chi phí hiệu năng
 * + Lý do kỹ thuật, nguyên văn: "Because reflection involves types that are dynamically
 * resolved, certain Java virtual machine optimizations can not be performed."
 * + Hệ quả: "reflective operations have slower performance than their non-reflective
 * counterparts, and should be avoided in sections of code which are called frequently in
 * performance-sensitive applications."
 * + Chú ý câu chữ: trang gốc nói JVM KHÔNG TỐI ƯU ĐƯỢC chứ không nói bản thân lời gọi chậm.
 * Khác biệt này quan trọng - xem mục 5.
 * <p>
 * - 3b. SECURITY RESTRICTIONS - giới hạn bảo mật
 * + "Reflection can be controlled at runtime, and modules can prohibit reflective access to
 * their own internals. This is in an important consideration when designing applications
 * that use reflection."
 * + Câu nặng nhất của cả bài, nguyên văn: "You also need to be aware that reflection has
 * lost its ability to introspect the classes of the standard Java API."
 * + Tức là: mọi bài blog trước JDK 16 bày cách chọc vào ruột {@code String},
 * {@code ArrayList}, {@code HashMap} giờ đều KHÔNG CHẠY. Chi tiết ở bài t5.
 * <p>
 * - 3c. EXPOSURE OF INTERNALS - phơi bày nội bộ
 * + "Since reflection allows code to perform operations that would be illegal in
 * non-reflective code, such as accessing private fields and methods, the use of reflection
 * can result in unexpected side effects, which may render code dysfunctional and may destroy
 * portability."
 * + Câu chốt đáng nhớ nhất: "Reflective code breaks abstractions and therefore may change
 * behavior with upgrades of the platform."
 * + Nói thẳng: bạn phụ thuộc vào thứ tác giả thư viện chưa từng hứa sẽ giữ nguyên. Họ đổi
 * tên một field private trong bản vá nhỏ là code bạn chết, và họ không hề sai.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. CÁI GIÁ THỨ TƯ TRANG GỐC KHÔNG NÊU - MẤT KIỂM TRA LÚC BIÊN DỊCH
 * - Trang gốc nêu hiệu năng, bảo mật, đóng gói. Thiếu cái quan trọng nhất trong thực tế:
 * reflection biến lỗi BIÊN DỊCH thành lỗi LÚC CHẠY.
 * - {@code getMethod("getUserName")} là một chuỗi. Đổi tên method trong IDE thì chuỗi đó
 * không ai sửa hộ, không ai cảnh báo. Lỗi nổ khi có request chạm vào nhánh đó.
 * - Cùng một tinh thần với bài t10 section Regex: thứ gì không được compiler kiểm tra thì
 * phải được test bao phủ, không có lựa chọn thứ ba.
 * <p>
 * 5. "CHẬM" CHẬM TỚI MỨC NÀO - ĐỪNG SỢ SAI CHỖ
 * - Hai chi phí khác hẳn nhau, trang gốc gộp chung làm người đọc sợ sai chỗ:
 * + TRA CỨU ({@code getMethod}, {@code getDeclaredField}, {@code getMethods}): đắt thật, vì
 * phải quét bảng member và tạo mảng mới mỗi lần gọi. Đây mới là chỗ tốn.
 * + GỌI ({@code Method.invoke}) sau khi đã có object {@code Method}: chậm hơn gọi trực tiếp
 * nhưng JIT xử lý được khá tốt, nhất là từ JDK 18 khi phần lõi được viết lại trên
 * method handle.
 * - Quy tắc rút ra, giống hệt {@code Pattern.compile} ở section Regex: TRA CỨU MỘT LẦN cất
 * vào {@code static final}, GỌI NHIỀU LẦN. Spring và Jackson đều làm vậy, đó là lý do chúng
 * đủ nhanh cho production dù dùng reflection ở khắp nơi.
 * - Sai lầm hay gặp: gọi {@code getMethod} bên trong vòng lặp hoặc bên trong method service
 * chạy mỗi request.
 * <p>
 * 6. BA CA DÙNG CỦA TRANG GỐC, DỊCH SANG STACK CỦA BẠN
 * - Extensibility: Spring component scan, {@code ServiceLoader},
 * {@code DriverManager} nạp driver JDBC, plugin của IDE.
 * - Class browser: autocomplete, Spring Boot Actuator phơi bean, Swagger sinh tài liệu API
 * từ chữ ký controller.
 * - Debugger và test: JUnit 5, Mockito, {@code @SpringBootTest} tiêm bean vào field private
 * của test class.
 * - Đáng chú ý: hầu như mọi ca đều là THƯ VIỆN viết sẵn. Trong code nghiệp vụ, câu hỏi nên
 * là "thư viện nào đã làm việc này rồi" trước khi hỏi "viết reflection thế nào".
 * <p>
 * 7. KHI NÀO KHÔNG DÙNG - BA DẤU HIỆU
 * - Thấy mình viết {@code if (type.equals("A")) ... else if (type.equals("B"))} rồi định
 * thay bằng {@code Class.forName(type)}: dừng lại, cái bạn cần là
 * {@code Map<String, Handler>} hoặc một interface với vài implementation.
 * - Thấy mình {@code setAccessible(true)} để đọc field private của lớp DO CHÍNH MÌNH VIẾT:
 * dừng lại, thêm getter là xong. Reflection ở đây chỉ che một thiết kế sai.
 * - Thấy mình reflect vào lớp của thư viện bên thứ ba: đây là dấu hiệu thư viện thiếu API
 * bạn cần. Mở issue trước, reflect sau - và ghi comment nêu rõ phiên bản thư viện đang bám vào.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba ca dùng chính đáng của reflection theo trang gốc? Điểm chung của cả ba là gì?
 * - Ba cái giá phải trả theo trang gốc, cộng cái giá thứ tư ở mục 4?
 * - Vì sao reflection làm JVM mất khả năng tối ưu?
 * - Câu "reflection has lost its ability to introspect the classes of the standard Java API"
 * nghĩa là gì trong thực tế?
 * - Trong hai thao tác tra cứu và gọi, thao tác nào đắt hơn? Quy tắc tối ưu rút ra là gì?
 * - Ba dấu hiệu cho thấy bạn đang dùng reflection sai chỗ?
 */
public class t1_IntroducingTheReflectionApi {

    void main() {
        // TODO 1 (mục 2): Với mỗi thư viện bạn đang dùng trong dự án thật (Spring, Jackson,
        //   Hibernate, JUnit, Mockito), xếp nó vào MỘT trong ba ca dùng ở mục 2 và viết một
        //   câu giải thích. Cái nào không xếp được thì ghi lại để kiểm tra sau.
        //
        // TODO 2 (mục 3a, mục 5): Viết benchmark thô: gọi một getter 10.000.000 lần theo ba
        //   cách - gọi trực tiếp, Method.invoke với Method lấy sẵn ngoài vòng lặp, và
        //   getMethod + invoke ngay trong vòng lặp. Lập bảng ba con số.
        //   Tự trả lời: khoảng cách giữa cách 1 và 2 có lớn như bạn tưởng không?
        //
        // TODO 3 (mục 3b): Thử String.class.getDeclaredField("value") rồi setAccessible(true)
        //   trên JDK 25. Dán lại exception. Đây là minh chứng cho câu "has lost its ability
        //   to introspect the classes of the standard Java API".
        //
        // TODO 4 (mục 3c): Tìm trong code hoặc thư viện bạn dùng một chỗ phụ thuộc vào chi
        //   tiết nội bộ qua reflection. Nếu không tìm được, tra cứu xem thư viện nào đã vỡ
        //   khi JDK 16 bật strong encapsulation và ghi lại một ví dụ.
        //
        // TODO 5 (mục 4): Viết một lớp nhỏ gọi method qua getMethod("someName"). Đổi tên
        //   method bằng chức năng Rename của IDE. Xác nhận IDE KHÔNG sửa chuỗi, và chương
        //   trình vẫn biên dịch được. Ghi lại exception lúc chạy.
        //
        // TODO 6 (mục 7): Lấy một đoạn if-else dài theo "type" trong code cũ của bạn. Viết
        //   hai bản thay thế: một bằng Map<String, Handler>, một bằng Class.forName.
        //   So sánh về an toàn kiểu, khả năng test, và tốc độ. Kết luận chọn bản nào.
    }
}
