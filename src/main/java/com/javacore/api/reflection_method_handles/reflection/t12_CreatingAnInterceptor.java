package com.javacore.api.reflection_method_handles.reflection;

/**
 * Creating an Interceptor with Annotations
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 12/13 của section The Reflection API. Bài DỰ ÁN đầu tiên.
 * - Mười một bài trước dạy từng mảnh rời. Bài này ráp chúng lại thành một thứ chạy được:
 * đọc annotation (t11) + tìm method (t6) + tạo instance (t7) + gọi method (t6).
 * - Một câu cho cả bài: đây chính là nguyên lý của {@code @Transactional}, Spring AOP, và
 * interceptor của Jakarta EE - không có gì khác ngoài bốn mảnh trên.
 * - Trang gốc cố ý làm bản ĐƠN GIẢN, không dùng {@code Proxy}. Mục 7 bên dưới nói rõ bản
 * thật khác chỗ nào, vì chính chỗ khác đó sinh ra cái bẫy Spring nổi tiếng nhất.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/interceptor/">dev.java - Creating an Interceptor with Annotations</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING AN INTERCEPTOR WITH ANNOTATIONS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. INTERCEPTOR LÀ GÌ
 * - (What is an Interceptor?)
 * - Khái niệm, nguyên văn: "you add an annotation on a method, this annotation takes a class
 * as an attribute, and a specific method of this class is called INSTEAD OF your service
 * method."
 * - Hai thành phần: một SERVICE có method nghiệp vụ, và một INTERCEPTOR cũng có một method.
 * Gọi service thì interceptor chạy thay.
 * - Sáu việc interceptor làm được, trang gốc liệt kê - đọc kỹ vì đây đúng là danh sách những
 * gì Spring AOP làm:
 * + kiểm tra đối số truyền vào;
 * + THAY ĐỔI đối số;
 * + áp luật bảo mật;
 * + quyết định CÓ GỌI service hay không;
 * + nhận kết quả rồi trả nguyên hoặc sửa lại;
 * + bắt exception của service và xử lý.
 * - Ghi chú về phạm vi, nguyên văn: "This kind of interceptor can be implemented using Proxy
 * objects, that are beyond the scope of this chapter. So we are going to implement this
 * feature in a SIMPLER WAY."
 * <p>
 * 2. GIAO DIỆN VÀ ANNOTATION
 * - (Designing the Intercept Interface and Annotation)
 * - Giao diện có hai tham số kiểu: kiểu object bị chặn và kiểu trả về.
 * <pre>
 *   public interface Interceptor&lt;T, R&gt; {
 *       R intercept(T interceptedObject, Method method, Object... arguments);
 *   }
 * </pre>
 * + Ba tham số của {@code intercept} chính là ba thứ cần để gọi lại method bằng reflection
 * (bài t6 mục 6): object nhận, {@code Method}, và mảng đối số.
 * - Annotation chỉ cần một thuộc tính - lớp của interceptor:
 * <pre>
 *   &#64;Target(ElementType.METHOD)
 *   &#64;Retention(RetentionPolicy.RUNTIME)
 *   public &#64;interface Intercept {
 *       Class&lt;? extends Interceptor&lt;?, ?&gt;&gt; value();
 *   }
 * </pre>
 * + {@code RUNTIME} là bắt buộc - bài t11 mục 6. Thiếu nó thì toàn bộ bài này im lặng
 * không chạy.
 * <p>
 * 3. SERVICE BỊ CHẶN
 * - (Designing a Service and an Intercepted Method)
 * <pre>
 *   public class SomeInterceptedService {
 *       &#64;Intercept(MessageInterceptor.class)
 *       public String message(String input) { return input.toUpperCase(); }
 *   }
 * </pre>
 * - Cách gọi mà trang gốc nhắm tới:
 * {@code ServiceFactory.invoke(SomeInterceptedService.class, "message", "Hello")}.
 * - Trang gốc tự nhận ngay điểm yếu, nguyên văn: "In a Proxy based system, this call would
 * be MUCH SIMPLER, as a dynamic proxy can build implementations for you. So your call would
 * look like a regular method call on a regular class."
 * + Đây là khác biệt cốt lõi giữa bản bài tập và bản production. Giữ câu này trong đầu tới
 * mục 7.
 * <p>
 * 4. LỚP MESSAGEINTERCEPTOR - PHẦN NGHIỆP VỤ
 * - (Writing the MessageInterceptor Class)
 * - Ba thứ cần để gọi lại method bị chặn, trang gốc nhắc lại rành mạch: "The method itself,
 * in the form of a Method object"; "The object on which this method is invoked. This is
 * actually your service"; "And the arguments you need to pass to this method."
 * - Thân method làm đúng ba bước, theo thứ tự:
 * + Kiểm tra đối số: đúng một phần tử kiểu {@code String}, không {@code null}, không rỗng.
 * Sai thì NÉM exception và service KHÔNG được gọi - đây là quyền lực thật sự của interceptor.
 * + Gọi service: {@code (String) interceptedMethod.invoke(service, arguments)}. Trang gốc
 * nhận xét thêm: "we could have MODIFIED the arguments passed to this method, if needed."
 * + Sửa kết quả: nối thêm {@code " [was intercepted]"}.
 * - Xử lý exception: bắt {@code IllegalAccessException} và
 * {@code InvocationTargetException} rồi bọc thành {@code RuntimeException}. Đúng hai nhóm
 * exception của bài t6 mục 10 - nhưng xem mục 8 bên dưới, cách bọc này còn thiếu một bước.
 * <p>
 * 5. LỚP SERVICEFACTORY - PHẦN KỸ THUẬT
 * - (Writing the ServiceFactory Class)
 * - Năm bước, mỗi bước là một bài đã học:
 * + TẠO SERVICE: {@code serviceClass.getConstructor().newInstance()} - bài t7. "This
 * constructor needs to be there, if not an exception is thrown."
 * + DỰNG MẢNG KIỂU THAM SỐ từ chính các đối số:
 * {@code Arrays.stream(arguments).map(Object::getClass).toArray(Class[]::new)} - bài t2.
 * + TÌM METHOD: {@code serviceClass.getDeclaredMethod(methodName, parameterClasses)} - bài t6.
 * + ĐỌC ANNOTATION: {@code isAnnotationPresent} rồi {@code getDeclaredAnnotation} rồi
 * {@code value()} - bài t11.
 * - Lưu ý về ép kiểu, nguyên văn: "you need to cast it to the right type, as ANNOTATIONS DO
 * NOT SUPPORT GENERICS. This cast will issue a compiler warning, as the compiler cannot
 * check if it is correct or not."
 * + GỌI: tạo interceptor bằng {@code getConstructor().newInstance()} rồi gọi
 * {@code intercept(...)}. Trang gốc nhấn mạnh: "This is a DIRECT call, not a reflective
 * call" - vì interceptor cài interface đã biết.
 * - Nhánh còn lại: "if the annotation is not found, then it reflectively calls the service
 * method, without any interception."
 * - Kết quả chạy:
 * <pre>
 *   Intercepted output     = HELLO [was intercepted]
 *   Non intercepted output = HELLO
 * </pre>
 * - LỜI THÚ NHẬN THẲNG THẮN CỦA TRANG GỐC, đáng trích nguyên và nên nhớ cả section:
 * "this code is rather COMPLEX, somehow OBSCURE, and NOT TYPE SAFE at compile time. This is
 * MOST OF THE TIME THE CASE when you are using the Reflection API on real application use
 * cases."
 * <p>
 * 6. GIỚI HẠN TRANG GỐC TỰ CHỈ RA
 * - Mảng kiểu tham số được suy ra từ ĐỐI SỐ THỰC, nên nó chỉ khớp khi kiểu khai báo trùng
 * đúng kiểu của đối số.
 * - Nguyên văn: "If the message() method was defined on the CharSequence type instead of the
 * String type ..., then the getDeclaredMethod() call would not have returned this message()
 * method."
 * - Nói cách khác: cách tra method này KHÔNG hiểu đa hình. Bài t6 mục 2 đã dọn đường cho kết
 * luận này - {@code getMethod} khớp chữ ký CHÍNH XÁC, không khớp theo "có tương thích không".
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BẢN THẬT DÙNG DYNAMIC PROXY - VÀ ĐÂY LÀ CHỖ SINH RA BẪY SPRING NỔI TIẾNG NHẤT
 * - Trang gốc né {@code Proxy}, nhưng không biết nó thì không hiểu nổi Spring. Bản tóm tắt:
 * <pre>
 *   Object proxy = Proxy.newProxyInstance(
 *       loader,
 *       new Class&lt;?&gt;[]{ MyService.class },     // PHẢI là interface
 *       (p, method, args) -&gt; {                   // InvocationHandler
 *           // ... logic chặn ở đây ...
 *           return method.invoke(target, args);
 *       });
 * </pre>
 * - Khác biệt quyết định: proxy CÀI ĐÚNG interface của service, nên code gọi viết như bình
 * thường ({@code service.message("Hello")}), không phải gọi qua
 * {@code ServiceFactory.invoke(...)} như bài tập.
 * - {@code java.lang.reflect.Proxy} chỉ proxy được INTERFACE. Muốn proxy lớp thường thì phải
 * sinh lớp con lúc chạy - đó là việc của CGLIB hay ByteBuddy. Spring dùng JDK proxy khi bean
 * có interface, và CGLIB khi không có.
 * - HỆ QUẢ SỐ MỘT, giải thích được bằng chính sơ đồ trên: proxy chỉ chặn được lời gọi ĐI QUA
 * NÓ. Một method trong bean gọi thẳng method khác của chính bean đó ({@code this.foo()}) thì
 * KHÔNG qua proxy. Đây đúng là lý do {@code @Transactional} "không chạy" khi gọi nội bộ -
 * câu hỏi phổ biến nhất về Spring, và bây giờ bạn hiểu vì sao.
 * - HỆ QUẢ SỐ HAI: {@code @Transactional} trên method {@code private} hoặc {@code final}
 * cũng vô tác dụng - proxy không ghi đè được chúng.
 * - HỆ QUẢ SỐ BA: bean được proxy thì {@code bean.getClass()} trả về lớp proxy, không phải
 * lớp gốc. Đúng cái bẫy ở bài t2 mục 10, và cách gỡ là
 * {@code AopUtils.getTargetClass(bean)}.
 * <p>
 * 8. BỐN CHỖ CODE MẪU SẼ VỠ TRONG DỰ ÁN THẬT
 * - ĐỐI SỐ NULL: {@code Arrays.stream(arguments).map(Object::getClass)} ném
 * {@code NullPointerException} ngay khi có một đối số {@code null}. Mà truyền {@code null}
 * là chuyện hằng ngày.
 * - KIỂU NGUYÊN THỦY: truyền số {@code 5} thì autoboxing cho {@code Integer}, nên
 * {@code getClass()} ra {@code Integer.class}. Method khai báo {@code f(int)} sẽ KHÔNG tìm
 * thấy - đúng bẫy {@code int.class} khác {@code Integer.class} của bài t2 mục 6 và t6 mục 2.
 * - BỌC EXCEPTION KHÔNG BÓC: code mẫu làm
 * {@code throw new RuntimeException(e)} với {@code e} là
 * {@code InvocationTargetException}. Lỗi nghiệp vụ thật bị chôn sâu thêm một tầng. Đúng phải
 * là {@code throw new RuntimeException(e.getCause())} - bài t6 mục 10.
 * - TẠO MỚI MỌI THỨ MỖI LẦN GỌI: mỗi lần {@code invoke} lại {@code newInstance} service,
 * {@code newInstance} interceptor, và tra lại {@code Method}. Trong production thì service
 * là singleton, interceptor là singleton, {@code Method} nằm trong cache - đó chính là việc
 * Spring làm lúc khởi động (bài t6 mục 13).
 * <p>
 * 9. NÂNG CẤP BÀI TẬP THÀNH THỨ DÙNG ĐƯỢC - LỘ TRÌNH
 * - Bước 1: sửa bốn lỗi ở mục 8. Vẫn giữ kiến trúc {@code ServiceFactory}.
 * - Bước 2: thay {@code getDeclaredMethod(name, types)} bằng quét
 * {@code getDeclaredMethods()} rồi lọc theo tên và theo
 * {@code parameterType.isAssignableFrom(argType)}. Lúc đó đa hình hoạt động và giới hạn ở
 * mục 6 biến mất.
 * - Bước 3: đổi sang {@code Proxy.newProxyInstance} để code gọi trở về bình thường.
 * - Bước 4: cho phép NHIỀU interceptor xếp chồng trên một method (annotation lặp lại, bài
 * t11 mục 3) và chạy chúng theo thứ tự - đây chính là khái niệm "chuỗi interceptor" của
 * Spring và của Jakarta EE.
 * - Làm hết bốn bước là bạn đã viết lại Spring AOP ở quy mô nhỏ, và sẽ không bao giờ còn
 * thấy nó huyền bí.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - NestJS có đúng khái niệm này và đúng tên: {@code @UseInterceptors},
 * {@code @UseGuards}, {@code @UsePipes}. Cùng danh sách sáu việc ở mục 1.
 * - Khác biệt về cơ chế: TS decorator là hàm chạy ngay lúc định nghĩa lớp, nên nó BỌC method
 * thật bằng cách thay {@code descriptor.value} - không cần proxy, không cần
 * {@code ServiceFactory}. Gọn hơn Java rõ rệt.
 * - Đổi lại: vì nó thay thẳng method trên prototype, TS KHÔNG có bẫy gọi nội bộ ở mục 7 -
 * {@code this.foo()} vẫn đi qua phiên bản đã bọc. Đây là một chỗ hiếm hoi mà mô hình của JS
 * tốt hơn hẳn.
 * - {@code Proxy} của JS (chuẩn ES6) gần với {@code java.lang.reflect.Proxy} về tên nhưng
 * mạnh hơn nhiều: nó bẫy được cả việc đọc thuộc tính, không chỉ lời gọi method, và không
 * đòi interface.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Sáu việc một interceptor làm được?
 * - Ba thứ cần có để gọi lại method bị chặn?
 * - Vì sao annotation {@code @Intercept} bắt buộc {@code RUNTIME}?
 * - Năm bước của {@code ServiceFactory.invoke}, mỗi bước thuộc bài nào?
 * - Vì sao phải ép kiểu giá trị trả về của {@code intercept.value()}?
 * - Giới hạn trang gốc tự nêu về {@code getDeclaredMethod} là gì?
 * - Bốn chỗ code mẫu sẽ vỡ trong dự án thật?
 * - {@code Proxy.newProxyInstance} đòi hỏi gì, và Spring làm gì khi bean không có interface?
 * - Vì sao {@code @Transactional} không chạy khi method gọi nội bộ? Giải thích bằng proxy.
 * - Vì sao {@code @Transactional} trên method private vô tác dụng?
 * - Câu thú nhận của trang gốc về code reflection trong ứng dụng thật là gì?
 */
public class t12_CreatingAnInterceptor {

    void main() {
        // TODO 1 (mục 2, 3): Viết interface Interceptor<T, R>, annotation @Intercept, và lớp
        //   SomeInterceptedService đúng như trang gốc.
        //
        // TODO 2 (mục 4): Viết MessageInterceptor với đủ ba bước: validate, gọi, sửa kết quả.
        //
        // TODO 3 (mục 5): Viết ServiceFactory.invoke theo năm bước. Chạy với cả
        //   SomeInterceptedService và SomeNonInterceptedService. Dán output và xác nhận
        //   khớp với trang gốc.
        //
        // TODO 4 (mục 2, mục 6 bài t11): Bỏ @Retention(RUNTIME) khỏi @Intercept rồi chạy lại.
        //   Quan sát: interceptor bị bỏ qua hoàn toàn mà không có lỗi nào.
        //
        // TODO 5 (mục 4): Sửa MessageInterceptor để nó TỪ CHỐI gọi service khi input rỗng.
        //   Rồi sửa để nó ĐỔI đối số (ví dụ trim) trước khi gọi. Rồi sửa để nó nuốt exception
        //   của service và trả về giá trị mặc định. Ba biến thể, ba lần chạy.
        //
        // TODO 6 (mục 6): Đổi chữ ký service thành "message(CharSequence input)" rồi chạy lại.
        //   Dán NoSuchMethodException và giải thích.
        //
        // TODO 7 (mục 8): Gọi invoke với đối số null. Dán NullPointerException. Sửa code để
        //   xử lý được null (gợi ý: cho phép truyền mảng kiểu tham số tường minh).
        //
        // TODO 8 (mục 8): Thêm một service method "int twice(int x)" và gọi nó qua
        //   ServiceFactory với đối số 5. Dán exception và giải thích bằng int.class so với
        //   Integer.class. Rồi sửa bằng một bảng ánh xạ wrapper sang primitive.
        //
        // TODO 9 (mục 8): Cho service method ném một exception nghiệp vụ. Chạy và xem stack
        //   trace bị chôn mấy tầng. Sửa lại bằng getCause() và so sánh hai stack trace.
        //
        // TODO 10 (mục 7): Viết lại toàn bộ bằng Proxy.newProxyInstance: tách interface
        //   MessageService, cho SomeInterceptedService implements nó, dựng proxy với
        //   InvocationHandler. Xác nhận code gọi trở thành "service.message("Hello")".
        //
        // TODO 11 (mục 7): Trong bản proxy ở TODO 10, thêm một method gọi nội bộ
        //   this.message(...) từ một method khác. Xác nhận interceptor KHÔNG chạy cho lời gọi
        //   nội bộ đó. Đây là bài tập QUAN TRỌNG NHẤT của bài.
        //
        // TODO 12 (mục 9): Cho phép nhiều @Intercept trên một method (repeatable) và chạy
        //   chuỗi interceptor theo thứ tự khai báo. In log vào/ra của từng tầng.
        //
        // TODO 13 (mục 8): Thêm cache: service singleton, interceptor singleton, Map<String,
        //   Method>. Đo thời gian 100.000 lời gọi trước và sau khi cache.
    }
}
