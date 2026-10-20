package com.javacore.api.reflection_method_handles.reflection;

/**
 * Invoking Methods
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 6/13 của section The Reflection API.
 * - Bài QUAN TRỌNG NHẤT của cả section. Đọc field là xem; gọi method là CHẠY code mà trình
 * biên dịch chưa từng nhìn thấy lời gọi đó.
 * - Một câu cho cả bài: {@code Method.invoke} làm được mọi thứ một lời gọi thường làm, chỉ
 * mất đúng hai thứ - kiểm tra lúc biên dịch, và exception nguyên vẹn (nó bị bọc trong
 * {@code InvocationTargetException}).
 * - Đây là nền của {@code @Test} trong JUnit, của handler method trong Spring MVC, của
 * {@code @EventListener}, {@code @Scheduled}. Bài t12 sẽ dựng lại cơ chế đó.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/methods/">dev.java - Invoking Methods</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Output trong mục 9 đã chạy
 * thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INVOKING METHODS (mục 1 đến 8)
 * ======================================================================
 * <p>
 * 1. TÌM METHOD - BỐN METHOD, SONG SONG VỚI FIELD
 * - (Locating Methods)
 * - Bảng giống hệt bài t5, chỉ khác chỗ phải nêu thêm KIỂU THAM SỐ khi tìm theo tên:
 * <pre>
 *                                      trả mảng?  kể cả kế thừa?  thấy private?
 *   getDeclaredMethod(String, Class...)  không      không           CÓ
 *   getMethod(String, Class...)          không      CÓ              không
 *   getDeclaredMethods()                 CÓ         không           CÓ
 *   getMethods()                         CÓ         CÓ              không
 * </pre>
 * - Vì sao phải truyền kiểu tham số: Java cho NẠP CHỒNG, nên tên method không định danh duy
 * nhất. {@code substring} có hai bản, phải nói rõ bản nào.
 * - Ví dụ trang gốc dùng lớp {@code User} có {@code getName()} public,
 * {@code validate(String)} private, và {@code toString()} ghi đè.
 * + {@code getMethods()} ra 10 dòng: {@code getName}, {@code toString} của {@code User},
 * cộng toàn bộ method public của {@code Object} TRỪ {@code toString} - vì bản của
 * {@code User} đã che nó. Trang gốc nói rõ: "If you remove the toString() method from the
 * User class, then you will see the toString() from the Object class in this list."
 * + Bài học: {@code getMethods()} trả về method ĐÃ GIẢI QUYẾT GHI ĐÈ, mỗi chữ ký một lần,
 * lấy bản cụ thể nhất.
 * + {@code getDeclaredMethods()} chỉ ra 3 dòng, nhưng CÓ {@code validate} private.
 * <p>
 * 2. KIỂU TRẢ VỀ
 * - (Obtaining the Return Type of a Method)
 * - "The return type of a method is modeled in the same way as the type of a Field":
 * {@code getReturnType()} cho {@code Class}, {@code getGenericReturnType()} cho {@code Type}.
 * - Ví dụ {@code List.get(int)}:
 * <pre>
 *   getReturnType()        class java.lang.Object      (kiểu đã xóa)
 *   getGenericReturnType() E                           (như khai báo)
 * </pre>
 * - CẢNH BÁO quan trọng chen ngang ở đây, nguyên văn: "we used the class int.class to denote
 * the int type of the parameter that the List.get(index) method takes. Using the
 * Integer.class type would have thrown a NoSuchMethodException."
 * + Đã kiểm chứng trên JDK 25: {@code List.class.getMethod("get", Integer.class)} ném
 * {@code NoSuchMethodException: java.util.List.get(java.lang.Integer)}.
 * + Đây là hệ quả trực tiếp của bài t2 mục 6: {@code int.class} khác {@code Integer.class}.
 * Autoboxing là tiện nghi của trình biên dịch, reflection không có.
 * - Ví dụ thứ hai {@code List.of(Object)}: {@code getReturnType()} ra
 * {@code interface java.util.List}, {@code getGenericReturnType()} ra {@code java.util.List}
 * - ở đây hai cái gần như trùng nhau vì kiểu trả về không phụ thuộc biến kiểu.
 * <p>
 * 3. THAM SỐ CỦA METHOD
 * - (Obtaining the Parameters of a Method)
 * - {@code getParameters()} cho cả tên tham số. "The classes Method and Constructor extend
 * the class Executable and therefore inherit this method."
 * + Ghi nhớ cây lớp: {@code AccessibleObject} là cha của {@code Field}, {@code Method},
 * {@code Constructor}; {@code Executable} nằm giữa, là cha chung của {@code Method} và
 * {@code Constructor}. Mọi thứ liên quan tham số nằm ở {@code Executable}.
 * - NHƯNG tên tham số mặc định KHÔNG có, nguyên văn: ".class files do not store formal
 * parameter names by default."
 * - Ba lý do trang gốc nêu, đáng đọc vì nó giải thích một quyết định thiết kế:
 * + "many tools that produce and consume class files may not expect the larger static and
 * dynamic footprint of .class files that contain parameter names" - file to hơn.
 * + "the Java Virtual Machine (JVM) would use more memory".
 * + Lý do bảo mật: "some parameter names, such as secret or password, may expose information
 * about security-sensitive methods."
 * - Cách bật: "compile the source file with the -parameters option to the javac compiler."
 * - Không bật thì {@code getParameters()} ra {@code arg0}, {@code arg1}. Đã kiểm chứng trên
 * JDK 25: in ra {@code [int arg0]}.
 * <p>
 * 4. EXCEPTION MÀ METHOD KHAI BÁO
 * - (Obtaining the Exceptions of a Method)
 * - Hai method song song: {@code getExceptionTypes()} trả mảng {@code Class},
 * {@code getGenericExceptionTypes()} trả mảng {@code Type}.
 * - Ví dụ của trang gốc rất khéo - nó soi chính {@code Constructor.newInstance(Object[])},
 * tức là dùng reflection để đọc API reflection. Kết quả bốn exception:
 * {@code InstantiationException}, {@code IllegalAccessException},
 * {@code IllegalArgumentException}, {@code InvocationTargetException}.
 * + Ghi nhớ luôn danh sách này - bài t7 sẽ gặp lại đủ cả bốn.
 * - Chi tiết cú pháp đáng nhớ: khai báo tham số kiểu mảng thì viết {@code Object[].class}.
 * <p>
 * 5. MODIFIER CỦA METHOD
 * - (Retrieving Method Modifiers)
 * - Giống hệt lớp và field. Ví dụ
 * {@code String.getMethod("join", CharSequence.class, Iterable.class)} ra
 * {@code isStatic = true}, {@code isPublic = true}, và {@code accessFlags()} ra
 * {@code [PUBLIC, STATIC]}.
 * <p>
 * 6. GỌI METHOD
 * - (Invoking a Method)
 * - Vì sao tính năng này quan trọng, nguyên văn: "This feature of the Method class is widely
 * used in many frameworks, because it allows you to execute a method from elements that are
 * DISCOVERABLE."
 * - Chữ ký: {@code invoke(Object receiver, Object... args)}.
 * + Tham số đầu: "the object on which you want to invoke the method".
 * + Tham số sau là vararg: "you can pass any number of arguments with this declaration, even
 * no argument."
 * + Đếm cho khỏi nhầm: method không tham số thì {@code invoke} nhận ĐÚNG MỘT đối số; method
 * hai tham số thì {@code invoke} nhận BA đối số.
 * <p>
 * - 6a. LẤY KẾT QUẢ
 * + {@code String.length()}: {@code (int) lengthMethod.invoke(hello)} ra {@code 5}.
 * + "the invoke() method returns a type Object, meaning that you need to cast the return
 * object to the correct type, if you know it." Ép kiểu sai thì
 * {@code ClassCastException} - và không ai cảnh báo trước.
 * <p>
 * - 6b. TRUYỀN THAM SỐ
 * + {@code getMethod("substring", int.class, int.class)} rồi {@code invoke(hello, 6, 11)}
 * ra {@code world}. Hai {@code int.class} là cách chọn đúng bản nạp chồng thứ hai.
 * <p>
 * - 6c. EXCEPTION BỊ BỌC - ĐIỂM PHẢI NHỚ KỸ NHẤT CỦA BÀI
 * + "The Reflection API catches these exceptions for you, and wraps them in a
 * InvocationTargetException that you can catch and analyze."
 * + Lấy exception thật ra bằng {@code ite.getTargetException()} (hoặc
 * {@code getCause()}, cùng kết quả).
 * + Ví dụ trang gốc mở file không tồn tại qua {@code Files.newBufferedWriter}, bắt được
 * {@code java.nio.file.NoSuchFileException} bên trong.
 * + Ví dụ đó còn dạy thêm hai thứ:
 * - Method {@code static} thì "the first argument passed to the invoke() method is null".
 * - Method có vararg thì "you need to consider this variable argument as an array, and pass
 * an empty array of the correct type" - tức khai báo {@code OpenOption[].class} và truyền
 * {@code new OpenOption[0]}.
 * <p>
 * 7. METHOD PRIVATE
 * - (Accessing Private Methods)
 * - "Trying to invoke a private method from outside the class, or any other case of a
 * non-visible method will throw an IllegalAccessException."
 * - Mở bằng {@code method.setAccessible(true)}, và nhắc lại nguyên văn cảnh báo của bài t5:
 * "calling setAccessible(true) on a method does not make this method public. It just
 * suppresses the checks for access control."
 * <p>
 * 8. GENERIC VÀ TYPE ERASURE
 * - (Generics and Type Erasure)
 * - "the discovery of a method depends on how this method has been compiled. The type
 * parameters of a method are erased at compile time, meaning that you cannot use them with
 * the Reflection API."
 * - Ví dụ kinh điển: với {@code List<String>}, gọi {@code getMethod("add", String.class)}
 * ném {@code NoSuchMethodException: java.util.ArrayList.add(java.lang.String)}. Đã kiểm
 * chứng trên JDK 25, đúng từng chữ.
 * - Lý do: tham số khai báo là {@code E}, bị xóa thành {@code Object}. Phải viết
 * {@code getMethod("add", Object.class)}.
 * - Câu kết của trang gốc, cũng là câu kết của cả phần: "by doing so you lose the type safety
 * of calling the method directly."
 * - Quy tắc rút ra: khi đi tìm method của lớp generic, LUÔN nghĩ bằng kiểu đã bị xóa -
 * {@code Object} nếu biến kiểu không có bound, hoặc chính cái bound nếu có
 * ({@code <T extends Number>} xóa thành {@code Number}).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 9. INVOKE VẪN TÔN TRỌNG ĐA HÌNH - ĐIỀU TRANG GỐC KHÔNG NÓI
 * - Câu hỏi tự nhiên: lấy {@code Method} từ lớp CHA rồi {@code invoke} trên instance lớp
 * CON thì chạy bản nào? Đã kiểm chứng trên JDK 25:
 * <pre>
 *   Method m = Base.class.getMethod("who");
 *   m.invoke(new Child());     ->  "Child"
 * </pre>
 * - Tức là {@code invoke} đi qua bảng điều phối ảo đúng như lời gọi thường. Object
 * {@code Method} chỉ xác định CHỮ KÝ, không xác định bản cài đặt.
 * - Hệ quả: bằng Reflection API bạn KHÔNG gọi được bản của lớp cha khi object là lớp con -
 * không có {@code super.who()} qua reflection. Muốn làm việc đó phải dùng
 * {@code MethodHandles.Lookup.findSpecial}, tức là sang bước 28.
 * - Ngoại lệ một chiều: method {@code private}, {@code static}, hoặc {@code final} không có
 * điều phối ảo, nên {@code invoke} gọi đúng bản bạn chỉ định.
 * <p>
 * 10. INVOCATIONTARGETEXCEPTION - XỬ LÝ SAI LÀ MẤT SẠCH THÔNG TIN LỖI
 * - Đã kiểm chứng: gọi một method ném {@code ArithmeticException} qua {@code invoke} thì
 * {@code ite.getMessage()} là {@code null}, còn {@code ite.getCause()} mới ra
 * {@code java.lang.ArithmeticException: / by zero}.
 * - Nghĩa là {@code log.error("loi: " + e.getMessage())} trên {@code InvocationTargetException}
 * ghi ra đúng chữ {@code null}. Đây là một trong những cách làm mất dấu lỗi hiệu quả nhất
 * trong Java.
 * - Mẫu đúng: luôn bóc một lớp.
 * <pre>
 *   try { m.invoke(target, args); }
 *   catch (InvocationTargetException e) { throw new MyException(e.getCause()); }
 * </pre>
 * - Phân biệt ba nhóm exception khi gọi {@code invoke}, vì chúng chỉ ra ba lỗi hoàn toàn
 * khác nhau:
 * + {@code IllegalAccessException} - LỖI CỦA BẠN, chưa {@code setAccessible}.
 * + {@code IllegalArgumentException} - LỖI CỦA BẠN, truyền sai số lượng hoặc sai kiểu đối số,
 * hoặc truyền {@code null} cho tham số nguyên thủy, hoặc quên receiver.
 * + {@code InvocationTargetException} - KHÔNG phải lỗi của bạn, method đã chạy và ném
 * exception của chính nó. Chỉ có nhóm này mới cần bóc.
 * - Spring gói sẵn chuyện này trong {@code ReflectionUtils.invokeMethod} - nó bóc và ném lại
 * dạng unchecked. Trong dự án Spring thì dùng nó.
 * <p>
 * 11. BA BẪY KHI TRUYỀN ĐỐI SỐ
 * - VARARG: method khai báo {@code f(String... xs)} thì đối với reflection nó là
 * {@code f(String[])}. Tìm bằng {@code getMethod("f", String[].class)}, gọi bằng
 * {@code invoke(obj, (Object) new String[]{"a","b"})}. Thiếu ép kiểu {@code (Object)} thì
 * trình biên dịch trải mảng ra thành nhiều đối số và bạn nhận
 * {@code IllegalArgumentException}.
 * - NULL CHO THAM SỐ NGUYÊN THỦY: truyền {@code null} vào chỗ {@code int} ném
 * {@code IllegalArgumentException}, không phải {@code NullPointerException}. Dễ nhầm khi
 * dựng mảng đối số động.
 * - METHOD KHÔNG THAM SỐ TRÊN STATIC: {@code invoke(null)} - đúng một đối số là
 * {@code null}. Viết {@code invoke()} thì không biên dịch được.
 * <p>
 * 12. BRIDGE METHOD - THỨ BẠN SẼ GẶP MÀ KHÔNG HIỂU VÌ SAO
 * - Khi một lớp cài {@code Comparable<User>}, trình biên dịch sinh thêm
 * {@code compareTo(Object)} gọi sang {@code compareTo(User)}. Tương tự khi ghi đè có thu hẹp
 * kiểu trả về.
 * - Hệ quả: {@code getDeclaredMethods()} trả về HAI method cùng tên. Vòng lặp "tìm method
 * tên X" sẽ bắt nhầm bản cầu nối, và bản đó không có annotation của bạn.
 * - Lọc bằng {@code method.isBridge()} hoặc {@code method.isSynthetic()}. Mọi framework đều
 * lọc - đây là lý do nên dùng {@code ReflectionUtils} của Spring thay vì tự viết.
 * <p>
 * 13. HIỆU NĂNG VÀ CACHE
 * - Chi phí nằm ở TRA CỨU, không ở {@code invoke}. {@code getMethods()} và
 * {@code getDeclaredMethods()} tạo mảng mới mỗi lần gọi (bản sao phòng vệ, vì {@code Method}
 * có trạng thái accessible).
 * - Mẫu chuẩn: tra cứu một lần lúc khởi động, {@code setAccessible(true)} một lần, cất vào
 * {@code static final Method} hoặc {@code Map<String, Method>}.
 * - Từ JDK 18 phần lõi của {@code Method.invoke} đã được viết lại trên method handle, nên
 * khoảng cách với lời gọi trực tiếp hẹp hơn nhiều so với con số bạn đọc trên các bài blog
 * cũ. Nhưng chỉ đúng khi {@code Method} đã được cache.
 * - Nếu thật sự cần nhanh trong vòng lặp nóng thì đừng tối ưu {@code invoke} - hãy sang
 * {@code MethodHandle} hoặc {@code LambdaMetafactory} (bước 28). Jackson dùng cách này để
 * sinh accessor nhanh.
 * <p>
 * 14. ĐÂY LÀ CƠ CHẾ CỦA MỌI ANNOTATION CHẠY ĐƯỢC
 * - JUnit 5: quét {@code getDeclaredMethods()}, giữ method có {@code @Test}, gọi
 * {@code setAccessible(true)} rồi {@code invoke}. Test thất bại chính là
 * {@code InvocationTargetException} được bóc ra.
 * - Spring MVC: mỗi handler là một {@code Method} đã cache trong
 * {@code HandlerMethod}. Khi có request, Spring dựng mảng đối số từ
 * {@code @PathVariable}, {@code @RequestParam}, {@code @RequestBody} rồi {@code invoke}.
 * Đây là lý do tên tham số quan trọng - và là lý do Spring Boot bật sẵn {@code -parameters}
 * (mục 3).
 * - {@code @Scheduled}, {@code @EventListener}, {@code @PostConstruct}: cùng một mẫu.
 * - Nhìn ra được mẫu này rồi thì ba phần tư sự "huyền bí" của Spring biến mất.
 * <p>
 * 15. ĐỐI CHIẾU TYPESCRIPT
 * - {@code obj[name](...args)} của JS làm đúng việc của {@code invoke}, và cũng có điều phối
 * động như mục 9.
 * - {@code Function.prototype.apply(thisArg, argsArray)} là bản gần nhất về hình dạng: đối số
 * đầu là receiver, đối số sau là mảng tham số - giống hệt {@code invoke}.
 * - Khác biệt lớn: JS KHÔNG bọc exception. Lỗi ném ra từ lời gọi động giống hệt lỗi ném ra
 * từ lời gọi thường. Cái bẫy {@code InvocationTargetException} ở mục 10 là đặc sản của Java.
 * - JS không có nạp chồng nên không cần truyền kiểu tham số khi tìm method - đây là lý do
 * reflection bên JS nhẹ hơn hẳn, và cũng là lý do nó kém an toàn hơn hẳn.
 * - Tên tham số: JS cũng không có cách lấy đàng hoàng (parse {@code fn.toString()} là trò
 * các thư viện DI cũ từng dùng và đã bỏ). Vấn đề của mục 3 là chung cho cả hai hệ, và cả hai
 * đều giải bằng cách thêm metadata lúc build.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 16. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn method tìm method, và vì sao phải truyền kiểu tham số?
 * - {@code getMethods()} trên lớp có ghi đè {@code toString} trả về mấy bản {@code toString}?
 * - {@code getMethod("get", Integer.class)} trên {@code List} ra gì, vì sao?
 * - {@code getReturnType()} và {@code getGenericReturnType()} của {@code List.get} khác nhau
 * thế nào?
 * - Vì sao tên tham số mặc định không có trong file {@code .class}? Ba lý do?
 * - Method hai tham số thì {@code invoke} nhận mấy đối số?
 * - Method {@code static} thì đối số đầu của {@code invoke} là gì?
 * - Method có vararg thì tìm và gọi thế nào?
 * - Ba nhóm exception của {@code invoke}, nhóm nào KHÔNG phải lỗi của bạn?
 * - {@code ite.getMessage()} trả về gì, phải dùng gì thay thế?
 * - Lấy {@code Method} từ lớp cha rồi {@code invoke} trên instance lớp con chạy bản nào?
 * - Gọi bản của lớp cha qua reflection được không, nếu không thì dùng gì?
 * - Bridge method là gì, lọc bằng cách nào?
 * - {@code getMethod("add", String.class)} trên {@code ArrayList} ra gì, sửa thế nào?
 */
public class t6_InvokingMethods {

    void main() {
        // TODO 1 (mục 1): Dựng lại lớp User của trang gốc (getName public, validate private,
        //   toString ghi đè). Chạy getMethods() và getDeclaredMethods(), dán cả hai output.
        //   Xóa toString rồi chạy lại getMethods() - xác nhận toString của Object xuất hiện.
        //
        // TODO 2 (mục 2): Lấy List.get(int) và in cả getReturnType() lẫn getGenericReturnType().
        //   Rồi thử getMethod("get", Integer.class) và dán exception.
        //
        // TODO 3 (mục 2): Viết method "<T extends Number> T pick(T a, T b)". Tìm nó bằng
        //   reflection - thử với Number.class, với Object.class, với Integer.class.
        //   Ghi lại cái nào tìm được và giải thích bằng quy tắc erasure ở mục 8.
        //
        // TODO 4 (mục 3): In getParameters() của một method tự viết. Rồi biên dịch lại với
        //   javac -parameters và in lại. Dán hai kết quả cạnh nhau.
        //
        // TODO 5 (mục 4): Lặp lại ví dụ soi Constructor.newInstance(Object[].class) và in bốn
        //   exception. Ghi nhớ danh sách này cho bài t7.
        //
        // TODO 6 (mục 6a, 6b): Gọi String.length() và String.substring(int,int) qua reflection.
        //   Sau đó cố ý ép kiểu kết quả sai (ví dụ (String) cho length) và dán exception.
        //
        // TODO 7 (mục 6c): Viết method "int div(int x) { return 10 / x; }" rồi invoke với 0.
        //   In ra cả e.getMessage() và e.getCause(). Xác nhận cái đầu là null.
        //   Đây là bài tập QUAN TRỌNG NHẤT của bài.
        //
        // TODO 8 (mục 6c, mục 11): Gọi Files.newBufferedWriter qua reflection với một đường
        //   dẫn không tồn tại. Chú ý ba thứ: receiver null vì static, OpenOption[].class khi
        //   tìm, new OpenOption[0] khi gọi.
        //
        // TODO 9 (mục 7): Gọi một method private từ lớp khác. Dán IllegalAccessException,
        //   rồi thêm setAccessible(true) và chạy lại.
        //
        // TODO 10 (mục 8): Thử getMethod("add", String.class) trên ArrayList, dán exception,
        //   rồi sửa thành Object.class và gọi add("hello"). Sau đó thử invoke với một Integer
        //   - xác nhận nó CHẠY, và giải thích câu "you lose the type safety".
        //
        // TODO 11 (mục 9): Tạo Base/Child có ghi đè. Lấy Method từ Base, invoke trên instance
        //   Child. Dán kết quả. Rồi thử nghĩ cách gọi bản của Base - ghi lại vì sao không được.
        //
        // TODO 12 (mục 11): Viết method "void f(String... xs)". Tìm và gọi nó qua reflection.
        //   Thử cả hai cách: có và không có ép kiểu (Object) khi truyền mảng. Dán cả hai kết quả.
        //
        // TODO 13 (mục 11): Gọi một method nhận int và truyền null làm đối số. Dán exception
        //   và xác nhận nó là IllegalArgumentException chứ không phải NullPointerException.
        //
        // TODO 14 (mục 12): Viết "class User implements Comparable<User>" với
        //   compareTo(User). In getDeclaredMethods() và tìm bridge method. In isBridge() và
        //   isSynthetic() của nó.
        //
        // TODO 15 (mục 13): Đo 1 triệu lần gọi getter theo bốn cách: trực tiếp, Method cache,
        //   getMethod mỗi vòng, và getMethods() mỗi vòng. Lập bảng bốn con số.
        //
        // TODO 16 (mục 14): Viết "mini JUnit": định nghĩa annotation @MyTest, quét
        //   getDeclaredMethods() của một lớp test, invoke từng method có annotation đó,
        //   bắt InvocationTargetException và in PASS/FAIL kèm nguyên nhân thật.
        //   Giữ lại code này cho bài t12.
    }
}
