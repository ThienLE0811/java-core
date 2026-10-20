package com.javacore.api.reflection_method_handles.reflection;

/**
 * Invoking Constructors
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 7/13 của section The Reflection API.
 * - Khép lại bộ ba {@code Field} - {@code Method} - {@code Constructor}. Ba bài cùng một
 * khuôn, nên bài này đọc nhanh; phần mới nằm ở chỗ constructor KHÔNG được kế thừa và ở
 * danh sách exception khi tạo object.
 * - Một câu cho cả bài: {@code Constructor.newInstance()} là cách tạo object động duy nhất
 * nên dùng - {@code Class.newInstance()} đã deprecate từ Java 9 vì nó lách qua cơ chế
 * checked exception của trình biên dịch.
 * - Đây là nền của việc Spring dựng bean, Jackson dựng DTO, Hibernate dựng entity.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/constructors/">dev.java - Invoking Constructors</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Output ở mục 9 đã chạy thật
 * trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INVOKING CONSTRUCTORS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. TÌM CONSTRUCTOR - CHỈ BỐN METHOD VÀ CHỈ HAI TRỤC
 * - (Locating Constructors)
 * - Khác biệt duy nhất so với bài t5 và t6, nguyên văn: "constructors are NOT INHERITED, so
 * you do not have any equivalent to the methods you saw on fields or methods, to locate
 * elements on inherited classes."
 * + Nên bảng mất hẳn cột "kể cả kế thừa". Ở đây chữ {@code Declared} chỉ còn mang đúng một
 * nghĩa: có thấy private hay không.
 * <pre>
 *                                 trả mảng?   thấy private?
 *   getDeclaredConstructor(Class...)  không      CÓ
 *   getConstructor(Class...)          không      không
 *   getDeclaredConstructors()         CÓ         CÓ
 *   getConstructors()                 CÓ         không
 * </pre>
 * - "A constructor declaration includes the name, modifiers, parameters, and list of
 * throwable exceptions."
 * - Ví dụ trang gốc: {@code String.class.getDeclaredConstructors()} ra 19 constructor trên
 * JDK 23, "Note that not all of them are public." Đã kiểm chứng trên JDK 25: vẫn 19, trong
 * đó 15 public - tức 4 cái private hoặc package-private.
 * + Danh sách đó là một bài học đọc API: {@code String} có cả
 * {@code String(char[],int,int,java.lang.Void)} private - tham số {@code Void} chỉ để phân
 * biệt chữ ký với bản public, một mẹo nội bộ của JDK.
 * - Tìm một constructor cụ thể: {@code c.getConstructor(byte[].class)}. Nhắc lại cú pháp
 * {@code byte[].class} của bài t3.
 * <p>
 * 2. THAM SỐ CỦA CONSTRUCTOR
 * - (Obtaining the Parameters of a Constructor)
 * - Hai method song song như bài t6: {@code getParameterTypes()} cho mảng {@code Class},
 * {@code getGenericParameterTypes()} cho mảng {@code Type}.
 * - Ví dụ trang gốc chạy trên {@code ArrayList.getConstructors()} và in ra ba constructor:
 * một nhận {@code Collection}, một không tham số, một nhận {@code int}.
 * - CHỖ TRANG GỐC SAI, đã kiểm chứng: trang gốc nói kết quả của bản generic "is the same"
 * với bản raw. Trên JDK 25 thì KHÔNG:
 * <pre>
 *   getParameterTypes()         java.util.Collection
 *   getGenericParameterTypes()  java.util.Collection&lt;? extends E&gt;
 * </pre>
 * + Đây đúng là điều ta mong đợi và là lý do tồn tại của method generic. Bản raw mất
 * wildcard, bản generic giữ. Khi học, hãy tin kết quả bạn chạy hơn tin ảnh chụp trong tài liệu.
 * + Với tham số {@code int} thì hai bản giống nhau - kiểu nguyên thủy không có gì để xóa.
 * <p>
 * 3. EXCEPTION CỦA CONSTRUCTOR
 * - (Obtaining Exceptions of a Constructor)
 * - {@code getExceptionTypes()} và {@code getGenericExceptionTypes()}, giống hệt bài t6 mục 4.
 * - Ví dụ: constructor {@code FileWriter(String)} khai báo {@code java.io.IOException}.
 * - Dùng để làm gì trong thực tế: khi viết framework gọi constructor động, bạn cần biết
 * trước nó ném checked exception gì để bọc lại cho tử tế thay vì nuốt.
 * <p>
 * 4. HAI KIỂU TẠO OBJECT - VÀ VÌ SAO MỘT CÁI BỊ LOẠI
 * - (Invocation Patterns)
 * - "There are two reflective methods for creating instances of classes:
 * Class.newInstance() and Constructor.newInstance(), WHICH IS THE PATTERN YOU SHOULD USE."
 * - {@code Class.newInstance()} gọi constructor không tham số; không có hoặc không nhìn thấy
 * thì ném exception.
 * - "The Class.newInstance() method has been DEPRECATED IN JAVA SE 9, and should not be used
 * anymore." Ba lý do, nguyên văn:
 * + "Class.newInstance() can only invoke the zero-argument constructor, while
 * Constructor.newInstance() may invoke any constructor, regardless of the number of
 * parameters."
 * + "Class.newInstance() throws any exception thrown by the constructor, INCLUDING A CHECKED
 * EXCEPTION. This method effectively BYPASSES THE COMPILE-TIME EXCEPTION CHECKING that would
 * otherwise be performed by the compiler."
 * + "Class.newInstance() requires that the constructor be visible; Constructor.newInstance()
 * may invoke private constructors under certain circumstances."
 * - Lý do thứ hai là lý do nghiêm trọng nhất và đáng dừng lại: nó ném thẳng checked
 * exception ra mà không khai báo, nên một method có thể ném {@code IOException} dù chữ ký
 * của nó không nói gì. Đây là lỗ hổng trong hệ thống kiểu của Java, không chỉ là bất tiện.
 * {@code Constructor.newInstance()} vá lại bằng cách bọc mọi thứ vào
 * {@code InvocationTargetException}.
 * <p>
 * 5. TẠO OBJECT - BA VÍ DỤ VÀ BỐN EXCEPTION
 * <p>
 * - 5a. TẠO MỘT STRING
 * + {@code String.class.getConstructor().newInstance()} ra chuỗi rỗng.
 * + Danh sách exception, trang gốc liệt kê rành mạch - học thuộc vì mỗi cái chỉ một lỗi khác:
 * - {@code getConstructor()} ném {@code NoSuchMethodException} "in the case the class does
 * not have a no-arg constructor".
 * - {@code newInstance()} ném {@code InvocationTargetException} "wrapping the exception the
 * invocation of the constructor may have thrown".
 * - {@code newInstance()} ném {@code InstantiationException} "in case the corresponding
 * class cannot be instantiated. This is the case for ABSTRACT CLASSES OR INTERFACES."
 * - {@code newInstance()} ném {@code IllegalAccessException} "in case the no-arg constructor
 * is not accessible from the calling code."
 * + Đã kiểm chứng trên JDK 25: gọi {@code newInstance()} trên một lớp {@code abstract} ném
 * đúng {@code java.lang.InstantiationException}. Còn với INTERFACE thì hỏng sớm hơn một
 * bước - {@code Runnable.class.getDeclaredConstructor()} đã ném
 * {@code NoSuchMethodException: java.lang.Runnable.<init>()}, vì interface không có
 * constructor nào để mà tìm.
 * <p>
 * - 5b. TẠO MỘT RECORD
 * + Record có hai đường vào. Với {@code public record Point(int x, int y)} có thêm
 * {@code public Point() { this(0,0); }}:
 * - {@code getConstructor().newInstance()} ra {@code Point[x=0, y=0]}.
 * - {@code getConstructor(int.class, int.class).newInstance(1, 1)} ra {@code Point[x=1, y=1]}.
 * + Lưu ý của trang gốc về constructor thứ hai: "this canonical constructor is not explicitly
 * written in the code, but the compiler generates it for you."
 * + CHI TIẾT TỰ PHÁT HIỆN KHI CHẠY THỬ: canonical constructor có cùng mức truy cập với chính
 * record. Khai báo {@code record Point(...)} không có {@code public} thì canonical
 * constructor là package-private, và {@code getConstructor(int.class, int.class)} ném
 * {@code NoSuchMethodException} - phải dùng {@code getDeclaredConstructor}. Đây là bẫy rất
 * dễ dính khi record là lớp lồng.
 * <p>
 * - 5c. CONSTRUCTOR PRIVATE
 * + (Invoking Private Constructors)
 * + Giống hệt field và method: {@code IllegalAccessException}, mở bằng
 * {@code constructor.setAccessible(true)}, và nhắc lại lần thứ ba trong section: "calling
 * setAccessible(true) ... does not make this method public. It just suppresses the checks
 * for access control."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. ENUM KHÔNG TẠO ĐƯỢC BẰNG REFLECTION - MỘT NGOẠI LỆ TRANG GỐC KHÔNG NÊU
 * - Đã kiểm chứng trên JDK 25: lấy constructor của enum, {@code setAccessible(true)}, rồi
 * {@code newInstance("BLUE", 2)} ném
 * {@code IllegalArgumentException: Cannot reflectively create enum objects}.
 * - JVM chặn ở tầng dưới, không phải chuyện quyền truy cập. Đây là lý do mẫu singleton bằng
 * {@code enum} được coi là an toàn nhất trong Java: reflection không phá được, và
 * deserialization cũng không tạo ra bản thứ hai.
 * - Đối chiếu: singleton bằng {@code private static final INSTANCE} với constructor private
 * thì reflection PHÁ ĐƯỢC dễ dàng - chỉ cần {@code setAccessible(true)}. Phòng thủ cổ điển
 * là ném exception trong chính constructor nếu {@code INSTANCE != null}.
 * <p>
 * 7. VÌ SAO FRAMEWORK NÀO CŨNG ĐÒI CONSTRUCTOR KHÔNG THAM SỐ
 * - Jackson deserialize JSON: nó chưa biết giá trị nào trước, nên cách đơn giản nhất là tạo
 * object rỗng bằng constructor không tham số rồi {@code Field.set} từng field (bài t5).
 * Thiếu constructor đó là {@code InvalidDefinitionException} lúc chạy.
 * - Hibernate và JPA: đặc tả BẮT BUỘC entity có constructor không tham số, ít nhất
 * {@code protected}. Provider tạo instance rỗng rồi đổ dữ liệu từ ResultSet vào.
 * - Cách thoát hiện đại cho Jackson: {@code @JsonCreator} trên constructor có tham số, hoặc
 * module {@code jackson-module-parameter-names} kèm cờ {@code -parameters} (bài t6 mục 3).
 * Lúc đó record và lớp bất biến dùng được bình thường.
 * - Spring thì khác hẳn: nó ưu tiên constructor CÓ tham số, vì đó chính là constructor
 * injection. Một constructor duy nhất thì khỏi cần {@code @Autowired}.
 * <p>
 * 8. TẠO OBJECT MÀ KHÔNG CHẠY CONSTRUCTOR - CÓ THẬT, VÀ NGUY HIỂM
 * - Cơ chế deserialization của Java tạo object KHÔNG qua constructor của lớp đó. Thư viện
 * {@code Objenesis} (Mockito dùng) làm cùng việc đó, và {@code sun.misc.Unsafe
 * .allocateInstance} là lối cũ.
 * - Hệ quả bảo mật: mọi kiểm tra tính hợp lệ bạn viết trong constructor đều bị bỏ qua. Một
 * {@code Order} với {@code amount} âm có thể tồn tại dù constructor cấm.
 * - Vì sao phải biết: đây là nền của cả lớp lỗ hổng deserialization. Không tin dữ liệu
 * serialize từ bên ngoài, và ưu tiên JSON có schema thay vì Java serialization.
 * <p>
 * 9. MẪU CODE AN TOÀN KHI TẠO OBJECT ĐỘNG
 * - Thứ tự đúng và lý do từng bước:
 * <pre>
 *   Class&lt;?&gt; c = Class.forName(name, false, loader);   // false: chưa chạy static block
 *   // kiểm tra c nằm trong danh sách trắng TRƯỚC khi đi tiếp  (bài t2 mục 9)
 *   Constructor&lt;?&gt; ctor = c.getDeclaredConstructor();   // ném NoSuchMethodException
 *   ctor.setAccessible(true);                           // có thể ném InaccessibleObjectException
 *   Object o = ctor.newInstance();                      // ném InstantiationException / ITE
 * </pre>
 * - Bốn exception bốn nguyên nhân khác nhau, đừng gộp vào một
 * {@code catch (Exception e)} rồi log một dòng chung - lúc hỏng bạn sẽ không biết hỏng ở bước nào.
 * - Luôn bóc {@code InvocationTargetException} bằng {@code getCause()} (bài t6 mục 10).
 * - Cache {@code Constructor} vào {@code Map<Class<?>, Constructor<?>>} nếu tạo nhiều lần.
 * - Trong Spring thì đã có {@code BeanUtils.instantiateClass(clazz)} làm hết các bước trên
 * và bóc exception sẵn.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Reflect.construct(Ctor, argsArray)} là bản sao gần như một-một của
 * {@code Constructor.newInstance}: nhận hàm dựng và một mảng đối số.
 * - {@code new ctor(...args)} với {@code ctor} là biến cũng làm được điều tương tự - JS
 * không cần API riêng vì class là giá trị hạng nhất. Java thì class không phải giá trị, nên
 * mới cần cả bộ Reflection API.
 * - JS không có nạp chồng constructor, nên không phải chọn chữ ký - biến mất luôn một nửa độ
 * phức tạp của bài này.
 * - Không có tương đương cho {@code InstantiationException}: JS không có lớp trừu tượng ở
 * tầng ngôn ngữ (chỉ có quy ước).
 * - Bẫy "constructor không chạy" ở mục 8 cũng có bên JS với {@code Object.create(proto)} -
 * tạo object có đúng prototype mà bỏ qua hàm dựng. Cùng một rủi ro, cùng một bài học.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao bảng tìm constructor chỉ có hai cột chứ không ba như field và method?
 * - {@code Class.newInstance()} deprecate từ phiên bản nào, ba lý do là gì?
 * - Lý do nào trong ba lý do đó là lỗ hổng trong hệ thống kiểu, không chỉ là bất tiện?
 * - Bốn exception khi tạo object động, mỗi cái ứng với nguyên nhân gì?
 * - Gọi {@code newInstance()} trên interface thì hỏng ở bước nào, exception gì?
 * - Record có mấy đường tạo object qua reflection? Canonical constructor có mức truy cập nào?
 * - Vì sao {@code getConstructor} trên canonical constructor của record lồng hay thất bại?
 * - Tạo enum bằng reflection được không? Hệ quả cho mẫu singleton?
 * - Vì sao Jackson và Hibernate đòi constructor không tham số, còn Spring thì không?
 * - Có cách nào tạo object mà bỏ qua constructor không? Rủi ro gì?
 */
public class t7_InvokingConstructors {

    void main() {
        // TODO 1 (mục 1): In getDeclaredConstructors() và getConstructors() của String trên
        //   JDK 25. Đếm cả hai và tìm ra những constructor không public. Giải thích vì sao
        //   có constructor nhận tham số kiểu Void.
        //
        // TODO 2 (mục 1): Tạo lớp có 4 constructor (public, protected, package, private).
        //   Chạy cả bốn method của bảng mục 1 và lập bảng kết quả.
        //
        // TODO 3 (mục 2): Chạy ví dụ ArrayList với cả getParameterTypes và
        //   getGenericParameterTypes. Dán output JDK 25 và xác nhận chúng KHÁC nhau -
        //   ngược với điều trang gốc viết.
        //
        // TODO 4 (mục 3): In exception của constructor FileWriter(String) và của
        //   FileInputStream(String). So sánh.
        //
        // TODO 5 (mục 4): Tìm Class.newInstance() trong Javadoc JDK 25, đọc phần deprecated
        //   và viết lại ba lý do bằng lời của bạn.
        //
        // TODO 6 (mục 5a): Gọi newInstance() trên: một lớp thường, một abstract class, một
        //   interface, một lớp chỉ có constructor private, một lớp mà constructor ném
        //   exception. Lập bảng năm kết quả với đúng tên exception.
        //
        // TODO 7 (mục 5b): Tạo "public record Point(int x, int y)". Gọi canonical constructor
        //   qua getConstructor. Rồi BỎ từ khóa public của record và chạy lại - dán exception
        //   và sửa bằng getDeclaredConstructor.
        //
        // TODO 8 (mục 6): Thử tạo một hằng enum mới bằng reflection. Dán exception.
        //   Rồi viết một singleton kiểu "private static final INSTANCE" và PHÁ nó bằng
        //   reflection - xác nhận tạo được instance thứ hai.
        //
        // TODO 9 (mục 6): Thêm phòng thủ vào constructor của singleton đó (ném exception nếu
        //   INSTANCE != null) và xác nhận reflection không phá được nữa.
        //
        // TODO 10 (mục 7): Viết một DTO không có constructor không tham số rồi thử
        //   deserialize bằng Jackson (nếu có trong classpath) hoặc mô phỏng bằng chính code
        //   reflection của bạn. Ghi lại lỗi và hai cách khắc phục.
        //
        // TODO 11 (mục 9): Viết hàm create(String className) theo đúng mẫu ở mục 9, có danh
        //   sách trắng, bắt riêng từng exception với thông điệp rõ ràng, và bóc
        //   InvocationTargetException. Test với cả năm ca của TODO 6.
        //
        // TODO 12 (mục 9): Đo thời gian tạo 1 triệu object theo ba cách: new trực tiếp,
        //   Constructor cache, và getDeclaredConstructor mỗi vòng. Lập bảng.
    }
}
