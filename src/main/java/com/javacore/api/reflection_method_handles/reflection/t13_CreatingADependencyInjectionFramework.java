package com.javacore.api.reflection_method_handles.reflection;

/**
 * Creating a Dependency Injection Framework
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 13/13 - bài CUỐI của section The Reflection API.
 * - Bài dự án thứ hai, và là đích đến của cả section: dựng một DI container thu nhỏ. Sau
 * bài này, {@code @Autowired} không còn là phép màu.
 * - Một câu cho cả bài: DI chỉ là "tạo object, quét field có đánh dấu, tạo tiếp object cho
 * từng field, rồi {@code setAccessible} và {@code set}" - đúng bốn bước, không hơn.
 * - Phần TỔNG KẾT cả section nằm ở mục 9 đến 11.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/dependency-injection/">dev.java - Creating a Dependency Injection Framework</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING A DEPENDENCY INJECTION FRAMEWORK (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. DI LÀ GÌ
 * - (What is Dependency Injection?)
 * - Ý tưởng, nguyên văn: "instead of creating the objects your application needs YOURSELF,
 * you delegate this task to a FACTORY. This factory can then reflectively explore your
 * class, check if some fields need to be initialized, and if this is the case, do it for you."
 * - Vì sao cần: "Initializing a set of business objects can be complex, as your objects
 * depend on other, collaborator objects, that need to be properly initialized IN A SPECIFIC
 * ORDER."
 * + Chữ "thứ tự cụ thể" là lý do thật sự DI tồn tại. Không phải để tránh gõ {@code new},
 * mà để không ai phải nhớ thứ tự dựng của vài trăm object.
 * - Trang gốc xây dần ba tầng: factory thường (mục 2), thêm singleton (mục 3), thêm tiêm
 * phụ thuộc (mục 4).
 * <p>
 * 2. TẦNG MỘT - BEAN FACTORY
 * - (Creating a Bean Factory)
 * - Factory là một {@code enum} một hằng {@code INSTANCE} - "This class implements the
 * singleton pattern, that you can easily implement with an ENUMERATION."
 * + Đây đúng là kết luận của bài t7 mục 6: singleton bằng enum là loại reflection không phá
 * được. Trang gốc dùng nó mà không giải thích; giờ bạn biết vì sao.
 * - Ba bước của {@code getInstanceOf(Class<T> beanClass, Object... arguments)}:
 * + Dựng mảng kiểu tham số:
 * {@code Arrays.stream(arguments).map(Object::getClass).toArray(Class[]::new)}.
 * + Tìm constructor: {@code beanClass.getConstructor(argumentsClasses)} - "If it does not
 * exist, an exception is thrown."
 * + Gọi: {@code beanConstructor.newInstance(arguments)}.
 * - Bắt gọn bốn exception của bài t7 mục 5a và bọc thành {@code RuntimeException}.
 * - CÙNG MỘT CẢNH BÁO như bài t12, nguyên văn: "for the getConstructor() to find the
 * constructor you are looking for, THE TYPES NEED TO BE EXACT. If the constructor takes a
 * CharSequence instead of a String, then the call ... would throw a NoSuchMethodException."
 * - Chạy ra {@code Message[message=Hello]} và {@code Message[message=world]} - hai object
 * khác nhau, vì chưa có singleton.
 * <p>
 * 3. TẦNG HAI - SINGLETON
 * - (Creating Singletons)
 * - Vì sao cần: "This makes sense if your bean are used to access services like a database
 * or some REST server."
 * - Annotation {@code @Singleton} đặt trên {@code TYPE}, retention {@code RUNTIME}.
 * - Tái cấu trúc: phần tạo object chuyển vào method private {@code instantiateBeanClass()}
 * để khỏi lặp.
 * - Trang gốc nói thẳng phần này khó: "This part is a little SUBTLE to write, because it
 * needs a registry, that needs to be THREAD-SAFE, and USED IN A THREAD-SAFE WAY."
 * + Hai vế đó khác nhau, và đây là bài học hay nhất của trang: dùng
 * {@code ConcurrentHashMap} chưa đủ - còn phải dùng ĐÚNG CÁCH.
 * - Luồng xử lý:
 * + Không có {@code @Singleton}: làm như tầng một.
 * + Có: tra {@code registry} ({@code ConcurrentMap<Class<?>, Object>}); có rồi thì trả về.
 * + Chưa có: tạo mới, {@code putIfAbsent}, rồi {@code get}.
 * - BA GHI CHÚ VỀ ĐỒNG THỜI, đáng đọc chậm:
 * + "this part can be executed by several threads concurrently. So at this point, you may
 * build SEVERAL instances of beanClass. You could avoid that by synchronizing all this code,
 * but that would create some CONTENTION." - chấp nhận lãng phí vài object để đổi lấy không
 * khóa. Đây là một đánh đổi thiết kế có chủ đích, không phải lỗi.
 * + "the call to ConcurrentMap.putIfAbsent() is an ATOMIC call ..., so you do not need to
 * synchronize this part."
 * + Cái bẫy, nguyên văn: "the value you passed to the ConcurrentMap.putIfAbsent() MAY NOT BE
 * THE ONE that, in the end, was put in the map. ... So to overcome this, you need to call
 * ConcurrentMap.get(), to return the singleton."
 * - Đây là chi tiết quyết định đúng sai: trả về biến {@code bean} cục bộ thay vì gọi lại
 * {@code registry.get()} thì hai luồng sẽ cầm hai object khác nhau, và "singleton" không
 * còn là singleton. Lỗi này KHÔNG lộ ra khi test một luồng.
 * - Kết quả chạy: {@code Instances of DBService are the same? true}.
 * <p>
 * 4. TẦNG BA - TIÊM PHỤ THUỘC
 * - (Injecting Dependencies)
 * - Annotation {@code @Inject} đặt trên {@code FIELD}, retention {@code RUNTIME}.
 * - Bốn bước thêm vào {@code instantiateBeanClass()}:
 * + Dựng bean như cũ - "What you get at this point is a bean, but ALL ITS FIELDS ARE NULL."
 * + {@code beanClass.getDeclaredFields()} - "no matter their visibility modifiers".
 * + Lọc {@code field.isAnnotationPresent(Inject.class)}.
 * + Với từng field: lấy {@code injectableField.getType()}, gọi ĐỆ QUY
 * {@code BeanFactory.INSTANCE.getInstanceOf(fieldClass)}, rồi
 * {@code setAccessible(true)} và {@code set(bean, fieldValue)}.
 * - Hai ghi chú của trang gốc:
 * + Đệ quy qua chính {@code BeanFactory} nên "this instantiation FOLLOWS THE @Singleton
 * declaration you may have on this class" - dependency dùng chung cũng chỉ tạo một lần.
 * + "setAccessible(true) is called NO MATTER WHAT, even if the field is public. In that case,
 * it would not be needed." - gọi thừa thì không sao, thiếu thì vỡ.
 * - Kết quả: {@code App has been created with a DBService: true}.
 * - Dừng lại một giây mà nhìn: toàn bộ {@code @Autowired} của Spring nằm gọn trong bốn bước
 * này. Phần còn lại của Spring là vòng đời, phạm vi, proxy, và cấu hình - không phải
 * cơ chế tiêm.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. NĂM THỨ CÒN THIẾU SO VỚI MỘT DI CONTAINER THẬT
 * - TIÊM THEO INTERFACE: {@code getType()} của field kiểu {@code UserRepository} trả về
 * chính INTERFACE đó, và interface thì {@code newInstance} không được (bài t7 mục 5a).
 * Container thật phải có một BẢNG ÁNH XẠ interface sang implementation - đây chính là việc
 * {@code @ComponentScan} và {@code @Bean} của Spring làm. Thiếu bảng này thì chưa phải DI,
 * mới chỉ là auto-new.
 * - PHỤ THUỘC VÒNG: {@code A} tiêm {@code B}, {@code B} tiêm {@code A} thì đệ quy ở mục 4
 * chạy vô hạn tới {@code StackOverflowError}. Spring giải bằng một "early reference cache" -
 * đưa object chưa hoàn thiện vào registry TRƯỚC khi tiêm field. Và với constructor injection
 * thì Spring không giải được, nó báo lỗi thẳng lúc khởi động - đó là một điểm cộng chứ
 * không phải điểm trừ.
 * - FIELD CỦA LỚP CHA: {@code getDeclaredFields()} không leo lên lớp cha (bài t5 mục 1).
 * Bean kế thừa từ một lớp cơ sở có {@code @Inject} sẽ không được tiêm. Phải thêm vòng lặp
 * {@code getSuperclass()} như bài t5 mục 9.
 * - PHẠM VI VÀ VÒNG ĐỜI: chỉ có singleton hoặc tạo mới. Không có request scope, không có
 * {@code @PostConstruct}, không có đóng tài nguyên lúc tắt.
 * - THÔNG BÁO LỖI: bọc hết vào {@code RuntimeException} nên khi hỏng bạn chỉ biết "hỏng".
 * Spring dành rất nhiều công cho việc này - thông báo kiểu "No qualifying bean of type X
 * available, consider defining a bean" là lý do người ta chịu đựng được nó.
 * <p>
 * 6. BA LỖI KỸ THUẬT CODE MẪU SẼ DÍNH TRONG DỰ ÁN THẬT
 * - {@code Object::getClass} trên đối số {@code null} ném {@code NullPointerException}, và
 * đối số nguyên thủy bị autobox nên {@code getConstructor(Integer.class)} không khớp
 * {@code f(int)}. Giống hệt bài t12 mục 8 - cùng một đoạn code, cùng hai lỗi.
 * - Bọc {@code InvocationTargetException} mà không {@code getCause()}: lỗi thật trong
 * constructor của bean bị chôn thêm một tầng (bài t6 mục 10).
 * - {@code (T) registry.get(beanClass)} là ép kiểu không kiểm tra được. Sửa sạch bằng
 * {@code Class.cast()}: {@code beanClass.cast(registry.get(beanClass))} - vừa hết cảnh báo,
 * vừa nổ sớm và rõ ràng nếu sai.
 * <p>
 * 7. VÌ SAO KHÔNG DÙNG COMPUTEIFABSENT CHO GỌN
 * - Nhìn code mục 3, phản xạ tự nhiên là thay ba dòng bằng
 * {@code registry.computeIfAbsent(beanClass, this::instantiate)}. Trông đẹp hơn và thật sự
 * nguyên tử hơn.
 * - NHƯNG nó sẽ hỏng ở đúng bài này: hàm dựng bean gọi đệ quy
 * {@code getInstanceOf(fieldClass)}, tức là động vào CHÍNH {@code ConcurrentHashMap} đang
 * trong {@code computeIfAbsent}. {@code ConcurrentHashMap} phát hiện và ném
 * {@code IllegalStateException: Recursive update}, hoặc tệ hơn là treo.
 * - Nên mẫu {@code putIfAbsent} rồi {@code get} của trang gốc KHÔNG phải cổ lỗ - nó là lựa
 * chọn đúng cho trường hợp hàm dựng có thể đệ quy. Bài học rộng hơn: đừng gọi code tùy ý bên
 * trong {@code computeIfAbsent}.
 * <p>
 * 8. FIELD INJECTION LÀ THỨ BÀI NÀY DẠY - VÀ LÀ THỨ BẠN NÊN TRÁNH
 * - Trang gốc dạy field injection vì nó minh họa reflection rõ nhất. Nhưng trong dự án thật,
 * constructor injection tốt hơn, và lý do thì chính bài này cho thấy:
 * + Field injection buộc field KHÔNG được {@code final} - object có thể bị đổi trạng thái
 * sau khi dựng xong.
 * + Object tồn tại ở trạng thái "đã tạo nhưng chưa tiêm" giữa hai bước ở mục 4. Code chạy
 * trong constructor sẽ thấy mọi dependency là {@code null}.
 * + Viết unit test không dùng container thì không có cách nào tiêm vào, trừ khi chính test
 * cũng phải dùng reflection.
 * + Nó giấu đi việc một lớp có quá nhiều phụ thuộc - thêm một field là một dòng, trong khi
 * constructor mười tham số thì ai cũng thấy ngay là có vấn đề.
 * - Constructor injection không cần {@code setAccessible}, không cần phá đóng gói, và dùng
 * được chính cơ chế ở mục 2. Spring khuyến nghị nó từ bản 4.3.
 * <p>
 * ======================================================================
 * TỔNG KẾT CẢ SECTION THE REFLECTION API
 * ======================================================================
 * <p>
 * 9. BẢN ĐỒ 13 BÀI
 * <pre>
 *   t1  Mở đầu          ba ca dùng chính đáng, ba cái giá phải trả
 *   --- LẤY VÀ ĐỌC THÔNG TIN ---
 *   t2  Retrieving      5 cách lấy Class; superclass, interface, member, enclosing
 *   t3  Names           4 kiểu tên; chỉ getName() nạp ngược được; cú pháp [[Ljava.lang.String;
 *   t4  Modifiers       bitmask và Modifier; accessFlags() của Java 20
 *   --- TÁC ĐỘNG LÊN MEMBER ---
 *   t5  Fields          get/set, setAccessible, final, kiểu nguyên thủy
 *   t6  Methods         invoke, nạp chồng, InvocationTargetException, erasure
 *   t7  Constructors    newInstance; vì sao Class.newInstance() bị loại
 *   t8  Arrays          lớp Array, newInstance theo kiểu, widening
 *   t9  Enums           $VALUES, constructor ẩn, không tạo được hằng mới
 *   t10 Records         RecordComponent, canonical constructor, KHÔNG ghi được
 *   t11 Annotations     RetentionPolicy, lặp lại, kế thừa, TYPE_USE
 *   --- RÁP LẠI ---
 *   t12 Interceptor     annotation + Method.invoke = Spring AOP thu nhỏ
 *   t13 DI framework    annotation + Field.set = @Autowired thu nhỏ
 * </pre>
 * <p>
 * 10. BẢY QUY TẮC MANG SANG DỰ ÁN THẬT
 * - MỘT: tra cứu MỘT LẦN, dùng NHIỀU LẦN. {@code getMethod}, {@code getDeclaredField},
 * {@code getDeclaredFields} đều đắt và đều tạo mảng mới. Cache vào {@code static final}
 * hoặc {@code Map}. Giống hệt {@code Pattern.compile} ở section Regex. (t5 mục 10, t6 mục 13)
 * - HAI: {@code setAccessible(true)} KHÔNG làm member hết private - nó chỉ tắt kiểm tra trên
 * đúng object {@code Field}/{@code Method} bạn đang cầm. (t5 mục 5)
 * - BA: reflection không được hưởng tiện nghi nào của trình biên dịch. Không autoboxing
 * ({@code int.class} khác {@code Integer.class}), không đa hình khi TÌM method, không suy
 * luận generic. (t2 mục 6, t6 mục 2 và mục 8)
 * - BỐN: luôn bóc {@code InvocationTargetException} bằng {@code getCause()}. Không bóc là
 * mất sạch nguyên nhân thật. (t6 mục 10)
 * - NĂM: phân biệt {@code IllegalAccessException} (checked, lỗi của bạn, sửa bằng code) với
 * {@code InaccessibleObjectException} (unchecked, module chặn, chỉ sửa bằng
 * {@code --add-opens}). {@code catch (ReflectiveOperationException)} KHÔNG bắt được cái sau.
 * (t5 mục 11)
 * - SÁU: quét member thì luôn lọc {@code synthetic} và {@code bridge}, và nhớ
 * {@code getDeclaredX} không leo lớp cha. (t4 mục 7, t5 mục 9, t6 mục 12)
 * - BẢY: nhiều method trả {@code null} chứ không trả rỗng khi lớp không có đặc tính đó -
 * {@code getEnumConstants}, {@code getRecordComponents}, {@code getComponentType},
 * {@code getCanonicalName}. Kiểm tra {@code null}. (t3 mục 7, t9 mục 6, t10 mục 5)
 * <p>
 * 11. REFLECTION TRONG MỘT DỰ ÁN SPRING BOOT - BẢN ĐỒ CUỐI
 * - Lúc KHỞI ĐỘNG là lúc reflection chạy nhiều nhất: quét classpath, đọc annotation, dựng
 * {@code BeanDefinition}, tạo bean, tiêm phụ thuộc, dựng bảng định tuyến của controller,
 * dựng metadata của entity. Vài giây khởi động của Spring Boot phần lớn là ở đây.
 * - Lúc CHẠY thì mọi thứ đã nằm trong cache: {@code HandlerMethod} giữ sẵn {@code Method},
 * Jackson giữ sẵn metadata của từng lớp, Hibernate giữ sẵn mô tả entity. Đó là lý do
 * reflection không làm chậm production.
 * - Ba chỗ bạn SẼ gặp lại trực tiếp:
 * + {@code bean.getClass()} trả về lớp proxy chứ không phải lớp gốc - dùng
 * {@code AopUtils.getTargetClass}. (t2 mục 10, t12 mục 7)
 * + {@code @Transactional} im lặng không chạy khi gọi nội bộ hoặc trên method private -
 * hệ quả trực tiếp của mô hình proxy. (t12 mục 7)
 * + Annotation của Spring phải tìm bằng {@code AnnotatedElementUtils}, không phải
 * {@code getAnnotation()} trần, vì {@code @Inherited} không qua interface và không hiểu
 * meta-annotation. (t11 mục 8)
 * - GraalVM native image: mọi thứ gọi qua reflection phải khai báo trong
 * {@code reflect-config.json}. Spring Boot 3 sinh file này lúc build bằng AOT. Nếu bạn tự
 * viết code reflection trong dự án nhắm native image thì phải tự khai báo.
 * - Lời khuyên cuối: trong code nghiệp vụ, mỗi lần định dùng reflection hãy hỏi trước "một
 * interface hoặc một {@code Map<String, Handler>} có giải được không". Phần lớn trường hợp
 * là có. Reflection dành cho người viết framework - và bây giờ bạn hiểu framework đủ rõ để
 * không cần viết lại nó.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG CẢ SECTION
 * - Lý do thật sự DI tồn tại là gì (không phải để tránh gõ {@code new})?
 * - Vì sao {@code BeanFactory} được viết bằng {@code enum}?
 * - Vì sao phải gọi {@code registry.get()} sau {@code putIfAbsent()}? Không gọi thì sao?
 * - Vì sao trang gốc chấp nhận có thể tạo thừa vài instance thay vì đồng bộ hóa?
 * - Vì sao KHÔNG thay {@code putIfAbsent} bằng {@code computeIfAbsent} ở bài này?
 * - Bốn bước tiêm phụ thuộc vào field?
 * - Vì sao {@code setAccessible(true)} được gọi kể cả với field public?
 * - Năm thứ còn thiếu so với một DI container thật?
 * - Tiêm một field kiểu interface thì hỏng ở đâu? Container thật giải thế nào?
 * - Phụ thuộc vòng làm code mẫu hỏng thế nào? Spring xử lý ra sao?
 * - Bốn lý do nên dùng constructor injection thay vì field injection?
 * - Kể lại bảy quy tắc ở mục 10 không nhìn tài liệu.
 * - Vẽ lại bản đồ 13 bài ở mục 9 và nói một câu về mỗi bài.
 */
public class t13_CreatingADependencyInjectionFramework {

    void main() {
        // TODO 1 (mục 2): Viết record Message và enum BeanFactory với getInstanceOf ba bước.
        //   Chạy ví dụ của trang gốc và dán output.
        //
        // TODO 2 (mục 2): Đổi constructor của Message thành nhận CharSequence rồi chạy lại.
        //   Dán NoSuchMethodException. Sửa bằng cách duyệt getConstructors() và dùng
        //   isAssignableFrom thay vì khớp chính xác.
        //
        // TODO 3 (mục 3): Thêm @Singleton và registry ConcurrentHashMap. Xác nhận hai lần
        //   getInstanceOf(DBService.class) trả về CÙNG một object.
        //
        // TODO 4 (mục 3): Cố ý trả về biến bean cục bộ thay vì registry.get(). Rồi viết test
        //   đa luồng: 100 luồng cùng gọi getInstanceOf, thu kết quả vào một Set theo định
        //   danh object. Xác nhận Set có nhiều hơn một phần tử. Sửa lại và chạy lại.
        //   Đây là bài tập QUAN TRỌNG NHẤT của bài.
        //
        // TODO 5 (mục 7): Thay putIfAbsent bằng computeIfAbsent và chạy với một bean có
        //   @Inject. Dán IllegalStateException: Recursive update. Giải thích.
        //
        // TODO 6 (mục 4): Thêm @Inject và hoàn thiện instantiateBeanClass. Chạy ví dụ
        //   MyApplication và xác nhận dbService khác null.
        //
        // TODO 7 (mục 4): Cho hai bean cùng @Inject một DBService có @Singleton. Xác nhận cả
        //   hai nhận CÙNG một instance. Rồi bỏ @Singleton và xác nhận chúng khác nhau.
        //
        // TODO 8 (mục 5): Đổi field thành kiểu interface (ví dụ @Inject private Repository
        //   repo). Dán exception. Rồi thêm một Map<Class<?>, Class<?>> ánh xạ interface sang
        //   implementation và sửa factory để tra bảng này trước khi tạo.
        //
        // TODO 9 (mục 5): Tạo A tiêm B và B tiêm A. Chạy và dán StackOverflowError.
        //   Sửa bằng cách đưa bean chưa hoàn thiện vào registry TRƯỚC khi tiêm field.
        //
        // TODO 10 (mục 5): Cho một bean kế thừa lớp cha có @Inject. Xác nhận field của lớp
        //   cha KHÔNG được tiêm. Sửa bằng vòng lặp getSuperclass() của bài t5 mục 9.
        //
        // TODO 11 (mục 6): Sửa cả ba lỗi kỹ thuật: null trong Object::getClass, bọc
        //   InvocationTargetException không getCause, và ép kiểu (T) thay bằng Class.cast.
        //
        // TODO 12 (mục 8): Thêm hỗ trợ CONSTRUCTOR INJECTION: tìm constructor có
        //   @Inject (hoặc constructor duy nhất), tạo dependency cho từng tham số, rồi gọi.
        //   So sánh với field injection về số dòng code và về việc field có final được không.
        //
        // TODO 13 (mục 5): Thêm @PostConstruct: sau khi tiêm xong, tìm method có annotation
        //   đó và invoke. Đây là cơ chế vòng đời đơn giản nhất của một container.
        //
        // TODO 14 (tổng kết, mục 10): Rà lại 13 file của section này, với mỗi quy tắc trong
        //   bảy quy tắc ở mục 10, tìm đúng một đoạn code của bạn minh họa nó.
        //
        // TODO 15 (tổng kết, mục 11): Mở một dự án Spring Boot thật. Tìm ba chỗ ở mục 11:
        //   in bean.getClass() của một bean có @Transactional; viết một method gọi nội bộ
        //   method @Transactional và xác nhận transaction không mở; thử getAnnotation trần
        //   trên một lớp có @RestController để tìm @Controller và xác nhận null.
    }
}
