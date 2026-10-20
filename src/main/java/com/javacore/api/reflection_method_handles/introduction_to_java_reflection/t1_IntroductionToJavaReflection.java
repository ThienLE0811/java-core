package com.javacore.api.reflection_method_handles.introduction_to_java_reflection;

/**
 * Introduction to Java Reflection
 * <p>
 * 0. Vị trí bài này
 * - Bước 26/41, bài 1/1 trong section Introduction to Java Reflection của lộ trình dev.java/learn.
 * - Đây là bài MỞ ĐẦU của nhóm Reflection and Method Handles. Giọng văn trang gốc rất đời
 * thường (tác giả mở bài bằng chuyện soi gương buổi sáng và làm thịt xông khói) - nhưng đừng
 * để nó đánh lừa: mọi framework Java bạn dùng hằng ngày đều đứng trên nội dung bài này.
 * - Một câu cho cả bài: reflection cho phép chương trình HỎI và TÁC ĐỘNG lên cấu trúc của
 * chính nó lúc chạy - thứ mà trình biên dịch không còn kiểm tra giúp được nữa.
 * - Trang gốc do Dr Heinz M. Kabutz đóng góp, cấp phép UPL.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/introduction_to_java_reflection/">dev.java - Introduction to Java Reflection</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * - LƯU Ý VỀ CODE MẪU: trang gốc viết bằng implicitly declared class (JEP 512, chính thức
 * từ Java 25) nên không có {@code class}, không có {@code public static}, và in bằng
 * {@code IO.println}. Repo này dùng lớp có tên + instance {@code main()}, nên khi chép ví dụ
 * về hãy đổi {@code IO.println} thành {@code System.out.println}.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRODUCTION TO JAVA REFLECTION (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. REFLECTION LÀ GÌ
 * - (Reflection)
 * - Định nghĩa nguyên văn: "Java reflection allows an object to look in the mirror and
 * discover what fields, methods, and constructors it has. We can read and write fields,
 * invoke methods, and even create new objects by calling the constructors."
 * - Bốn động từ cần thuộc, vì chúng là bốn nhóm API sẽ gặp lại suốt cả nhóm bài: ĐỌC field,
 * GHI field, GỌI method, TẠO object.
 * - Lý do nên học, nguyên văn: reflection "will allow you to sometimes save thousands of
 * fingerbreaking lines of code with a few well-positioned chunks of reflection poetry".
 * Nói cách khác: reflection là công cụ viết CODE TỔNG QUÁT - code không biết trước nó sẽ
 * làm việc với lớp nào.
 * <p>
 * 2. LỚP CLASS
 * - (The Class {@code Class})
 * - Mở đầu nguyên văn: "No, this is not a typo. There is a class called Class. And it is a
 * subclass of Object. And Object has a Class. A nice circular dependency."
 * - Một object của {@code Class} không phải là instance của lớp đó - nó là MÔ TẢ về lớp đó.
 * Phân biệt được chỗ này thì cả bài trôi.
 * <p>
 * - 2a. BA CÁCH LẤY CLASS OBJECT
 * + {@code obj.getClass()} - method kế thừa từ {@code java.lang.Object}, trả về lớp THẬT.
 * Kết luận nguyên văn: "it does not matter how the variable is declared, we always get the
 * actual implementation object's class". Khai báo {@code List<String> l = new ArrayList<>()}
 * thì {@code l.getClass()} vẫn ra {@code class java.util.ArrayList}, không phải {@code List}.
 * + Class literal: viết tên lớp rồi {@code .class}, ví dụ {@code Number.class},
 * {@code java.util.List.class}. Dùng được cả với interface (in ra có chữ
 * {@code interface java.util.List}).
 * + {@code Class.forName(String)} - "We can also load classes by name as String, without
 * even knowing whether the class will be available at runtime." Đây là cách duy nhất trong
 * ba cách nhận tên lớp từ dữ liệu - từ file cấu hình, từ input người dùng, từ DB.
 * <p>
 * - 2b. CLASSLOADER
 * + "Each class is loaded into a ClassLoader. The JDK classes all reside in the bootstrap
 * class loader, whereas our classes are in the system class loader, also called application
 * class loader."
 * + {@code String.class.getClassLoader()} trả về {@code null} - bootstrap class loader
 * không có đại diện Java object, nên JDK biểu diễn nó bằng {@code null}.
 * + Cùng một file chạy hai kiểu cho ra hai loader khác nhau: chạy thẳng
 * {@code java ClassLoaderDemo.java} thì là {@code MemoryClassLoader}, còn biên dịch trước rồi
 * {@code java ClassLoaderDemo} thì là {@code AppClassLoader}.
 * + Mục đích, nguyên văn: "The purpose of class loaders is to partition classes for security
 * reasons. Classes in the JDK cannot see our classes at all, and similarly, classes in the
 * AppClassLoader have no relation to classes in the MemoryClassLoader."
 * + Cảnh báo kèm theo: "This can cause some surprises when we compile our classes and then
 * also launch them with the single-file command java SomeClass.java".
 * <p>
 * 3. SHALLOW REFLECTIVE ACCESS
 * - (Shallow Reflective Access)
 * - Định nghĩa phạm vi, nguyên văn: "Once we have the class, we can find out a lot of
 * information about it, such as who the superclasses are, what public members it has, what
 * interfaces it has implemented. If it is a sealed type, we can even find the subtypes."
 * - "Shallow" = chỉ đụng tới member PUBLIC. Chưa phá vỡ đóng gói.
 * <p>
 * - 3a. LIỆT KÊ METHOD
 * + {@code Iterator.class.getMethods()} trả mảng {@code Method}. Trang gốc chạy ra bốn
 * method, hai trong số đó là {@code default}:
 * <pre>
 *   public default void java.util.Iterator.remove()
 *   public default void java.util.Iterator.forEachRemaining(java.util.function.Consumer)
 *   public abstract boolean java.util.Iterator.hasNext()
 *   public abstract java.lang.Object java.util.Iterator.next()
 * </pre>
 * + Chú ý tham số in ra là {@code Consumer} trần, không có tham số generic - generic đã bị
 * xóa lúc biên dịch. Đây là lần đầu bài chạm vào type erasure; xem thêm mục 8.
 * <p>
 * - 3b. GỌI MỘT METHOD ĐÃ TÌM ĐƯỢC
 * + Tìm theo tên VÀ danh sách kiểu tham số:
 * {@code Iterator.class.getMethod("forEachRemaining", Consumer.class)}.
 * + Gọi: {@code method.invoke(receiver, args...)}.
 * + Hai thứ có thể hỏng, trang gốc nêu đích danh: method không tồn tại
 * ({@code NoSuchMethodException}) và không có quyền gọi ({@code IllegalAccessException}).
 * + Nguyên văn: "Since Java 7, we have a blanket exception that covers everything that can
 * go wrong with reflection, the ReflectiveOperationException." Đây là lớp CHA của cả
 * {@code ClassNotFoundException}, {@code NoSuchMethodException},
 * {@code NoSuchFieldException}, {@code IllegalAccessException}, {@code InstantiationException}
 * - nên khai báo {@code throws ReflectiveOperationException} là gọn nhất.
 * <p>
 * - 3c. VÍ DỤ ĐẮT NHẤT CỦA BÀI - DÒ METHOD HỢP LỆ RỒI GỌI
 * + Ý tưởng: duyệt {@code Collections.class.getMethods()}, giữ lại method nào thỏa
 * {@code getReturnType() == void.class}, {@code getParameterCount() == 1} và
 * {@code getParameterTypes()[0] == List.class}, rồi gọi thử trên một {@code List}.
 * + Tìm được ba method: {@code sort()}, {@code shuffle()}, {@code reverse()} - mà code
 * KHÔNG hề nhắc tên cái nào.
 * + Method {@code static} thì truyền {@code null} làm receiver, nguyên văn: "Since the
 * methods are declared static in Collections, the first parameter of our invoke() method
 * will be null."
 * + Vì sao không dùng Stream, nguyên văn: "We could use a stream, but they don't play nice
 * with checked exceptions, thus the plain old for-in loop it will have to be." Dùng lại
 * đúng bài học checked exception trong lambda của section Streams.
 * + CẢNH BÁO quan trọng nhất ở đây: "The order of these methods is not guaranteed." Thứ tự
 * {@code getMethods()} trả về KHÔNG xác định và có thể đổi giữa các bản JDK. Trang gốc còn
 * đối chiếu cụ thể: trong file nguồn OpenJDK 21 thứ tự khai báo là sort, reverse, shuffle,
 * nhưng khi chạy lại ra reverse, sort, shuffle. Đừng bao giờ viết logic phụ thuộc thứ tự này.
 * <p>
 * 4. DEEP REFLECTIVE ACCESS
 * - (Deep Reflective Access)
 * - Ranh giới, nguyên văn: "Up to now, we did not do anything that was particularly
 * dangerous. All the methods we discovered and called were public. The only part that was a
 * bit dangerous was that we did not have a compiler check that these methods exist and are
 * accessible. However, we can also stare more deeply into the mirror."
 * <p>
 * - 4a. VÍ DỤ FOUNTAIN OF YOUTH
 * + Lớp {@code Person} có {@code private final String name} và {@code private final int age},
 * không hề có setter.
 * + Ba bước phá đóng gói:
 * {@code Field ageField = Person.class.getDeclaredField("age");} rồi
 * {@code ageField.setAccessible(true); // deep reflection engaged!} rồi
 * {@code ageField.set(person, age);}
 * + Kết quả trang gốc in ra: {@code Heinz Kabutz (51)} rồi {@code Heinz Kabutz (45)}.
 * + KẾT LUẬN nguyên văn: "Note that the age field is private and final, and yet we were able
 * to change it. If we convert Person to a record, then it will no longer allow us to change
 * the properties via deep reflection."
 * + Chi tiết dễ bỏ qua trong code mẫu: {@code int age; age *= .9;} biên dịch được dù vế phải
 * là {@code double}, vì phép gán rút gọn có ép kiểu ngầm. 51 * 0.9 = 45.9 cắt thành 45 - đó
 * là lý do ra 45 chứ không phải 46.
 * <p>
 * - 4b. BẪY HAI CLASS LOADER
 * + Khi đã có hai file (Person và demo) thì PHẢI biên dịch, nguyên văn: "It would be a
 * mistake to use a single-file call, because in that case Person and the demo would exist in
 * different class loaders. This can cause hard to understand runtime errors. I once spent a
 * day chasing this exact error. Don't be me."
 * + Đây chính là hệ quả thực tế của mục 2b. Lỗi biểu hiện rất khó hiểu vì cùng tên lớp nhưng
 * khác loader thì JVM coi là HAI lớp khác nhau.
 * <p>
 * - 4c. JAVA MODULE SYSTEM CHẶN DEEP REFLECTION
 * + (The Java Module System)
 * + "Deep reflection only works if the module in which the class resides is open to us."
 * + Xin tác giả module mở package ra thì nên chuẩn bị bị từ chối, và từ chối là có lý:
 * "By opening up their package, they allow unfettered access to their most intimate
 * implementation detail. What if, in the near or distant future, they want to change field
 * names or types? The deep reflective code would likely stop working, and they would be
 * forever having to fix other modules."
 * + Lối thoát cuối cùng, nguyên văn: "We can open up a package in another module for deep
 * reflection with a command line parameter --add-opens, but we should only use that as an
 * absolute last resort. It is so undesirable, that I merely mention it here in disgust, but
 * won't show more details of how to use it."
 * <p>
 * 5. KẾT LUẬN CỦA TRANG GỐC
 * - (Conclusion)
 * - Trang thừa nhận mình mới chạm bề mặt: "There are many other topics to explore: arrays,
 * dynamic proxies, generics, sealed classes, etc. How we can read the properties of a record,
 * how parameter names can be preserved."
 * - Những thứ đó nằm ở bước 27 ({@code reflection} - The Reflection API).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. GETX SO VỚI GETDECLAREDX - KHÁC BIỆT TRANG GỐC DÙNG MÀ KHÔNG GIẢI THÍCH
 * - Trang gốc dùng {@code getMethods()} ở mục 3 và {@code getDeclaredField()} ở mục 4, nhưng
 * không nói vì sao đổi. Quy tắc hai chiều, thuộc lòng:
 * <pre>
 *   getMethods()              public, KỂ CẢ kế thừa từ lớp cha và interface
 *   getDeclaredMethods()      MỌI mức truy cập, nhưng CHỈ khai báo ngay trong lớp này
 *   getFields()               public, kể cả kế thừa
 *   getDeclaredFields()       mọi mức truy cập, chỉ của riêng lớp này
 *   getConstructors()         chỉ constructor public
 *   getDeclaredConstructors() mọi constructor, kể cả private
 * </pre>
 * - Hai trục hoàn toàn khác nhau: chữ {@code Declared} nói về PHẠM VI KHAI BÁO (lớp này hay
 * cả cây kế thừa), và cũng chính nó quyết định MỨC TRUY CẬP được nhìn thấy.
 * - Hệ quả hay làm người mới hụt: {@code getDeclaredFields()} trên một lớp con KHÔNG trả về
 * field của lớp cha. Muốn quét hết thì phải leo {@code getSuperclass()} cho tới khi gặp
 * {@code Object}. Spring và Jackson đều làm đúng vòng lặp đó.
 * - {@code getDeclaredX} không tự động cho quyền. Vẫn phải {@code setAccessible(true)} trước
 * khi đọc hoặc ghi member private.
 * <p>
 * 7. INACCESSIBLEOBJECTEXCEPTION - THỰC TẾ TRÊN JDK 17 TRỞ LÊN
 * - Trang gốc nói về module một cách trừu tượng. Trên JDK hiện tại nó cụ thể thế này:
 * + {@code setAccessible(true)} lên lớp CỦA MÌNH trong classpath: chạy bình thường. Code
 * không khai báo module thì nằm trong unnamed module, vốn mở sẵn.
 * + {@code setAccessible(true)} lên field private của lớp JDK (ví dụ
 * {@code java.lang.String.value}): ném {@code InaccessibleObjectException} - một
 * {@code RuntimeException}, KHÔNG phải {@code ReflectiveOperationException}, nên
 * {@code catch (ReflectiveOperationException e)} không bắt được nó.
 * + Mốc lịch sử: JDK 9 cảnh báo, JDK 16 bật strong encapsulation mặc định (JEP 396),
 * JDK 17 bỏ luôn cờ {@code --illegal-access} (JEP 403). Thư viện cũ chết ở mốc này rất nhiều.
 * - Record: thử {@code setAccessible(true)} rồi {@code set()} lên field của record sẽ ném
 * {@code IllegalAccessException} - JVM cấm ở mức sâu hơn, không phải chuyện module.
 * - {@code static final} nguyên thủy còn một bẫy nữa: trình biên dịch đã nội tuyến hằng số
 * vào nơi sử dụng, nên có sửa được giá trị bằng reflection thì code đọc nó vẫn thấy giá trị cũ.
 * <p>
 * 8. GIÁ PHẢI TRẢ - VÌ SAO KHÔNG DÙNG REFLECTION Ở MỌI NƠI
 * - Mất kiểm tra lúc biên dịch. Đây là cái giá lớn nhất, chính trang gốc cũng nhận ở mục 4:
 * đổi tên method thì IDE refactor được code thường, nhưng chuỗi {@code "forEachRemaining"}
 * thì không ai sửa hộ. Lỗi chuyển từ lúc build sang lúc chạy.
 * - Mất type safety: {@code invoke()} trả {@code Object}, {@code field.get()} trả
 * {@code Object}. Mọi thứ phải ép kiểu, và sai thì {@code ClassCastException}.
 * - Chi phí hiệu năng: {@code getMethod} và {@code getDeclaredField} là tra cứu tốn kém,
 * còn {@code invoke} chậm hơn gọi trực tiếp. Quy tắc giống hệt {@code Pattern.compile} ở
 * section Regex: TÌM MỘT LẦN, cất vào {@code static final}, GỌI NHIỀU LẦN. Đừng gọi
 * {@code getMethod} trong vòng lặp.
 * - Generic bị xóa: {@code getParameterTypes()} chỉ cho {@code List}, không cho
 * {@code List} kèm tham số kiểu. Muốn lấy generic phải dùng nhánh API riêng
 * ({@code getGenericParameterTypes}, {@code ParameterizedType}) - bước 27 nói kỹ.
 * - Tên tham số cũng bị xóa trừ khi biên dịch với cờ {@code -parameters}, nếu không
 * {@code getParameters()} trả {@code arg0}, {@code arg1}. Spring Boot bật sẵn cờ này -
 * đó là lý do {@code @RequestParam} thường không cần ghi tên.
 * - Kết luận thực dụng: reflection hợp lý khi viết FRAMEWORK hoặc công cụ tổng quát. Trong
 * code nghiệp vụ, nếu thấy mình dùng reflection thì phần lớn trường hợp một interface
 * hoặc một {@code Map} tra cứu handler là lời giải đúng hơn.
 * <p>
 * 9. NƠI BẠN ĐÃ DÙNG REFLECTION MÀ KHÔNG BIẾT - SPRING BOOT
 * - Component scan: Spring đọc bytecode tìm lớp có {@code @Component}, rồi nạp lớp và gọi
 * constructor - đúng bốn động từ ở mục 1.
 * - Dependency injection vào field: {@code @Autowired} trên field private hoạt động bằng
 * {@code Field.setAccessible(true)} rồi {@code Field.set}. Chính là mục 4a. Đây cũng là lý do
 * constructor injection được khuyến nghị hơn - nó không cần phá đóng gói, và test được
 * không cần Spring.
 * - Jackson: serialize đọc field hoặc getter bằng reflection; deserialize gọi constructor
 * rỗng rồi set từng field. Lớp DTO thiếu constructor rỗng là hỏng ở đây.
 * - Hibernate và Spring Data JPA: entity BẮT BUỘC có constructor không tham số (có thể
 * {@code protected}) vì provider tạo instance bằng reflection rồi đổ dữ liệu vào field. Entity
 * cũng không được {@code final} vì Hibernate còn sinh proxy kế thừa nó để lazy loading.
 * - JUnit 5: tìm method có {@code @Test} bằng {@code getDeclaredMethods()} rồi {@code invoke}.
 * - Lombok KHÔNG phải reflection - nó sinh code lúc biên dịch. Phân biệt rõ hai thứ này.
 * - GraalVM native image: ảnh hưởng thực tế lớn nhất. Native image loại bỏ lớp không thấy
 * được tham chiếu tĩnh, nên mọi lớp chỉ được gọi qua reflection phải khai báo trong
 * {@code reflect-config.json}. Spring Boot 3 sinh file này lúc build thông qua AOT - đó là
 * phần lớn lý do Spring Boot 3 phải làm lại nhánh AOT.
 * - Thời gian khởi động: quét và reflect hàng nghìn lớp chính là phần lớn vài giây khởi động
 * của một ứng dụng Spring Boot.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - TS và JS cũng soi được object lúc chạy: {@code typeof}, {@code Object.keys(obj)},
 * {@code obj.constructor.name}, {@code Reflect.ownKeys()}. Tinh thần giống mục 3.
 * - Khác biệt gốc rễ: kiểu của TypeScript bị XÓA SẠCH khi biên dịch sang JS - không còn gì
 * để soi. Java thì ngược lại, file {@code .class} giữ nguyên tên field, kiểu field, chữ ký
 * method, nên có một object {@code Class} thật để hỏi. Type erasure của Java chỉ áp dụng cho
 * THAM SỐ GENERIC, không phải toàn bộ hệ thống kiểu.
 * - Hệ quả: decorator của TS muốn biết kiểu tham số phải cài thêm {@code reflect-metadata} và
 * bật {@code emitDecoratorMetadata} để tsc ghi metadata ra - đúng thứ Java có sẵn. NestJS
 * injection hoạt động được là nhờ vậy, còn Spring thì không cần bước này.
 * - {@code Class.forName("com.a.B")} gần với {@code await import(name)} của JS ở chỗ nhận tên
 * dạng chuỗi lúc chạy, nhưng khác: {@code forName} đồng bộ và chỉ nạp lớp, không chạy module.
 * - Không có tương đương bên JS cho {@code setAccessible}: trường {@code #private} của JS bị
 * chặn ở tầng ngôn ngữ, không có cửa sau. Java thì có cửa sau, và đó vừa là sức mạnh của
 * Spring vừa là lý do Module System ra đời.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn việc reflection làm được, kể theo đúng thứ tự trang gốc nêu?
 * - Ba cách lấy một {@code Class} object, cách nào dùng khi tên lớp nằm trong dữ liệu?
 * - Khai báo biến kiểu {@code List} nhưng gán {@code ArrayList} thì {@code getClass()} ra gì?
 * - {@code String.class.getClassLoader()} trả về gì, vì sao lại là giá trị đó?
 * - {@code getMethods()} khác {@code getDeclaredMethods()} ở hai điểm nào?
 * - Muốn lấy mọi field kể cả của lớp cha thì phải làm sao?
 * - Exception cha chung của reflection tên gì, có từ Java mấy, nó KHÔNG bao gồm exception nào
 * mà bạn vẫn hay gặp?
 * - Ba dòng code để sửa một field {@code private final}?
 * - Vì sao record chống được deep reflection?
 * - Thứ tự {@code getMethods()} trả về có đảm bảo không? Hệ quả khi viết code?
 * - Kể ba chỗ trong Spring Boot đang chạy bằng reflection.
 * - Vì sao entity JPA bắt buộc có constructor không tham số?
 * - Nếu phải dùng reflection trong vòng lặp nóng, bạn tối ưu thế nào?
 */
public class t1_IntroductionToJavaReflection {

    void main() {
        // TODO 1 (mục 2a): Khai báo "List<String> list1 = new ArrayList<>();" và
        //   "var list2 = new ArrayList<String>();" rồi in getClass() của cả hai.
        //   Dán output thật vào mục 2a và tự trả lời: vì sao hai dòng giống nhau?
        //
        // TODO 2 (mục 2a): In Number.class, java.util.List.class, int.class, int[].class,
        //   String[].class. Lập bảng tên in ra và ghi chú chỗ nào lạ (chú ý mảng và kiểu
        //   nguyên thủy - đây là phần trang gốc bỏ qua).
        //
        // TODO 3 (mục 2a): Viết hàm nhận tên lớp dạng String và in Class.forName(name).
        //   Thử với "java.util.Iterator", với "khong.ton.tai.Lop" và dán lại exception.
        //   Trả lời: exception đó là checked hay unchecked?
        //
        // TODO 4 (mục 2b): In String.class.getClassLoader() và this.getClass().getClassLoader().
        //   Dán output. Giải thích vì sao cái đầu là null.
        //
        // TODO 5 (mục 3a): In toàn bộ Iterator.class.getMethods(). Đối chiếu với bốn dòng ở
        //   mục 3a - JDK 25 có cho ra đúng bốn method không? Chú ý phần "Consumer" không có
        //   tham số generic và ghi lại nhận xét.
        //
        // TODO 6 (mục 3b): Lấy Method forEachRemaining qua getMethod("forEachRemaining",
        //   Consumer.class), tạo iterator từ List.of("Hello", "Dev", "Java") rồi invoke.
        //   Sau đó cố tình gõ sai tên method thành "forEachRemaning" và dán lại exception.
        //
        // TODO 7 (mục 3c): Chép ví dụ Collections: tạo List<Integer> từ các chữ số của pi,
        //   duyệt Collections.class.getMethods(), lọc theo returnType == void.class,
        //   parameterCount == 1, parameterTypes()[0] == List.class, rồi invoke(null, list).
        //   Dán output và đối chiếu thứ tự method với thứ tự trang gốc ghi nhận.
        //
        // TODO 8 (mục 3c): Chạy lại TODO 7 ba lần. Thứ tự method có đổi giữa các lần chạy
        //   trên cùng một JDK không? Viết kết luận của bạn về việc dựa vào thứ tự này.
        //
        // TODO 9 (mục 4a): Tạo file Person.java riêng với private final String name và
        //   private final int age, không setter. Viết lại ví dụ FountainOfYouth bằng
        //   getDeclaredField + setAccessible(true) + set(). Dán output trước và sau.
        //
        // TODO 10 (mục 4a): Giải thích vì sao 51 thành 45 chứ không phải 46. Thử đổi
        //   "age *= .9" thành "age = age * .9" và dán lại lỗi biên dịch.
        //
        // TODO 11 (mục 4a, mục 7): Đổi Person thành record rồi chạy lại TODO 9.
        //   Dán lại exception và ghi rõ tên lớp exception đó.
        //
        // TODO 12 (mục 6): Tạo lớp Employee kế thừa Person. Gọi Employee.class.getDeclaredFields()
        //   và Employee.class.getFields(). Giải thích kết quả. Sau đó viết vòng lặp leo
        //   getSuperclass() để gom đủ field của cả cây kế thừa.
        //
        // TODO 13 (mục 7): Thử setAccessible(true) lên một field private của lớp JDK, ví dụ
        //   String.class.getDeclaredField("value"). Dán exception và kiểm chứng:
        //   catch (ReflectiveOperationException e) có bắt được nó không?
        //
        // TODO 14 (mục 8): Đo thời gian gọi một method 1.000.000 lần theo hai cách: gọi trực
        //   tiếp, và gọi qua Method.invoke. Sau đó thử cách thứ ba: lấy Method MỘT LẦN ra
        //   biến rồi mới invoke trong vòng lặp. Lập bảng ba con số và rút ra quy tắc.
        //
        // TODO 15 (mục 8): In tên các tham số của một method bạn tự viết bằng getParameters().
        //   Biên dịch lại với cờ -parameters rồi in lại. Dán hai kết quả.
        //
        // TODO 16 (mục 9): Viết một "mini DI container": quét các field có annotation tự định
        //   nghĩa @Inject của một object, tạo instance cho từng field bằng constructor rỗng và
        //   set vào. Chạy được rồi thì bạn đã hiểu @Autowired làm gì.
    }
}
