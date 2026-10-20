package com.javacore.api.reflection_method_handles.reflection;

/**
 * Reading Annotations
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 11/13 của section The Reflection API.
 * - Bài này là CẦU NỐI: nó trả lời câu hỏi "làm sao một dòng {@code @Transactional} lại
 * khiến code chạy khác đi". Hai bài cuối (t12, t13) chỉ là ráp bài này với bài t5 và t6.
 * - Câu mở đầu của trang gốc đã nói hết: annotation là cách các framework ĐÁNH DẤU, còn
 * reflection là cách chúng ĐỌC DẤU và HÀNH ĐỘNG.
 * - Một câu cho cả bài: annotation không làm gì cả - nó chỉ là dữ liệu gắn vào code, và
 * phải có ai đó đọc nó bằng reflection thì mới có chuyện gì xảy ra.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/annotations/">dev.java - Reading Annotations</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Output ở mục 5, 6, 7 đã chạy
 * thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING ANNOTATIONS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. ANNOTATION DÙNG Ở ĐÂU
 * - "Many frameworks use them extensively and with great success. This is the case for the
 * object relational mapping frameworks, the dependency injection frameworks, some validation
 * frameworks, and some security frameworks."
 * - Mục tiêu của trang, nguyên văn: "This section shows you how all this is working UNDER
 * THE HOOD, and how you can use annotations at runtime."
 * <p>
 * 2. ANNOTATEDELEMENT - GIAO DIỆN CHUNG
 * - (Discovering Annotations on Elements)
 * - "The following classes from the Reflection API implement the interface AnnotatedElement:
 * Field, Method, Constructor, and Class."
 * - Nhắc lại phạm vi của {@code Class}: "Remember that the class Class models classes,
 * abstract classes, interfaces, enumerations, records, and arrays."
 * - Năm nhóm method, học một lần dùng cho cả bốn chỗ:
 * + {@code isAnnotationPresent(Class)} - có annotation này không. Trang gốc lưu ý: "The
 * class you need to pass as an argument MUST be an annotation class. If you don't, you will
 * get a compiler error." Đây là một trong rất ít chỗ của Reflection API còn được trình biên
 * dịch kiểm tra giúp.
 * + {@code getAnnotations()} và {@code getDeclaredAnnotations()} - trả mảng, "can be empty
 * if there is no such annotation". Chú ý: RỖNG chứ không {@code null} - khác với
 * {@code getEnumConstants} và {@code getRecordComponents} ở hai bài trước.
 * + {@code getAnnotation(Class)} và {@code getDeclaredAnnotation(Class)} - trả đúng một
 * annotation theo kiểu, hoặc {@code null}.
 * + {@code getAnnotationsByType(Class)} - thêm ở Java SE 8 cho annotation lặp lại. "If it
 * finds a repeating annotation, it OPENS IT to return the repeating element."
 * - Cặp {@code Declared} / không-{@code Declared} ở đây mang nghĩa KHÁC hẳn các bài trước:
 * nó không nói về private, mà nói về KẾ THỪA (mục 3).
 * - Điểm quan trọng dễ lướt qua, nguyên văn: "what you get in return are INSTANCES of the
 * annotation classes you define, ON WHICH YOU CAN CALL THE METHODS THEY DECLARE." Ví dụ:
 * {@code c.getAnnotation(Serialized.class).format()} ra {@code JSON}. Annotation có thuộc
 * tính, và thuộc tính truy cập bằng cách gọi method cùng tên.
 * <p>
 * 3. ANNOTATION LẶP LẠI
 * - Khai báo cần hai annotation: một CONTAINER chứa mảng, và annotation thật mang
 * {@code @Repeatable(Container.class)}.
 * - SỰ THẬT Ở TẦNG BYTECODE, nguyên văn: "From the class file perspective, there is actually
 * ONLY ONE annotation on the name field: {@code @Validators}."
 * + Nên {@code getAnnotations()} trả về MỘT phần tử - cái container. Phải gọi
 * {@code value()} của nó để lấy mảng bên trong. Trang gốc nhận xét đúng: "it's a little
 * tedious".
 * + {@code getAnnotationsByType(Validator.class)} làm hộ việc bóc đó, trả thẳng HAI phần tử.
 * - Đã kiểm chứng trên JDK 25, khớp từng chữ:
 * <pre>
 *   getAnnotations()              [&#64;Validators({&#64;Validator(NON_NULL), &#64;Validator(NON_EMPTY)})]
 *   getAnnotationsByType(...)     [&#64;Validator(NON_NULL), &#64;Validator(NON_EMPTY)]
 * </pre>
 * - Quy tắc: gặp annotation có thể lặp thì LUÔN dùng {@code getAnnotationsByType}. Nó chạy
 * đúng cho cả trường hợp chỉ có một cái, nên không cần phân nhánh.
 * <p>
 * 4. ANNOTATION KẾ THỪA
 * - (Getting Inherited Annotations)
 * - "Annotations declared on types (classes, abstract classes, and interfaces) can be
 * inherited by their subtypes." Muốn vậy phải đánh dấu chính annotation đó bằng
 * {@code @Inherited}.
 * - "It does not make sense to do that for enumerations and records, as these two are FINAL
 * classes, and thus cannot be extended."
 * - Khác biệt hai nhóm method:
 * + {@code getDeclaredAnnotations()} và {@code getDeclaredAnnotation(Class)} có "a STRICT
 * behavior. They return only the annotations declared on the type you are calling these
 * methods on."
 * + {@code getAnnotations()} và {@code getAnnotation(Class)} "MAY return inherited
 * annotations."
 * - Ví dụ trang gốc: {@code @Bean} có {@code @Inherited}, đặt trên {@code Person}, lớp
 * {@code User extends Person} không đặt gì. Kết quả trên {@code User}:
 * {@code declared annotations = 0}, nhưng {@code annotations = 1}. Đã kiểm chứng trên
 * JDK 25, đúng y vậy.
 * <p>
 * 5. ANNOTATION TRÊN KIỂU (TYPE_USE)
 * - (Getting Annotations on Types)
 * - Với {@code @Target(ElementType.TYPE_USE)}, annotation đặt được ở MỌI CHỖ kiểu xuất hiện,
 * không chỉ ở chỗ khai báo. Reflection đọc chúng qua giao diện {@code AnnotatedType}.
 * - Ba cửa chính:
 * + {@code Class.getAnnotatedSuperclass()} - annotation trên mệnh đề {@code extends}.
 * + {@code Class.getAnnotatedInterfaces()} - annotation trên mệnh đề {@code implements}.
 * + {@code Executable.getAnnotatedExceptionTypes()} - annotation trên mệnh đề
 * {@code throws}.
 * - Ví dụ cuối của trang rất đáng đọc vì nó là một thiết kế hoàn chỉnh chứ không chỉ demo
 * API: định nghĩa {@code @ReThrowAsRuntimeException} đặt trên mệnh đề {@code throws} của
 * constructor, rồi code gọi đọc annotation đó để quyết định có bọc checked exception thành
 * {@code RuntimeException} hay không.
 * + Đây là minh họa sạch nhất cho tư tưởng của cả bài: annotation là DỮ LIỆU, và người đọc
 * dữ liệu mới là người quyết định hành vi.
 * + Lưu ý nhỏ của trang gốc: {@code getConstructor()} ném {@code NoSuchMethodException} nên
 * code mẫu vẫn phải xử lý nó.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. RETENTIONPOLICY - ĐIỀU QUAN TRỌNG NHẤT MÀ TRANG GỐC KHÔNG GIẢI THÍCH
 * - Trang gốc dùng {@code @Retention(RetentionPolicy.RUNTIME)} ở mọi ví dụ nhưng không nói
 * vì sao. Đây là thứ phải hiểu trước tất cả:
 * <pre>
 *   SOURCE   chỉ tồn tại trong mã nguồn, javac vứt đi    -> reflection KHÔNG thấy
 *   CLASS    có trong file .class, JVM không nạp         -> reflection KHÔNG thấy  (MẶC ĐỊNH)
 *   RUNTIME  nạp vào JVM                                 -> reflection thấy
 * </pre>
 * - MẶC ĐỊNH LÀ {@code CLASS}. Quên {@code @Retention(RUNTIME)} là annotation của bạn vô
 * hình với reflection, và KHÔNG có lỗi nào báo cho bạn biết - code chỉ im lặng không chạy.
 * Đây là lỗi số một khi tự viết annotation.
 * - Ví dụ thực tế của từng mức: {@code @Override} là {@code SOURCE} (chỉ compiler cần);
 * {@code @NotNull} của một số thư viện là {@code CLASS} (công cụ phân tích bytecode đọc);
 * {@code @Entity}, {@code @Autowired}, {@code @Test} đều là {@code RUNTIME}.
 * - NGOẠI LỆ THÚ VỊ ĐÃ KIỂM CHỨNG: trong ví dụ annotation lặp của trang gốc,
 * {@code @Validator} KHÔNG hề khai báo {@code @Retention}, chỉ container
 * {@code @Validators} có {@code RUNTIME}. Vậy mà {@code getAnnotationsByType} vẫn đọc được
 * cả hai. Lý do: các annotation con được lưu BÊN TRONG giá trị của container, nên retention
 * của container mới là thứ quyết định. Đừng dựa vào mẹo này - cứ khai báo
 * {@code RUNTIME} cho cả hai.
 * <p>
 * 7. ANNOTATION BẠN NHẬN VỀ LÀ MỘT PROXY
 * - Đã kiểm chứng trên JDK 25:
 * <pre>
 *   bean.getClass()        class $Proxy8
 *   bean.annotationType()  interface Bean
 *   bean instanceof Proxy  true
 * </pre>
 * - Annotation chỉ là một {@code interface}; JVM sinh một dynamic proxy cài interface đó,
 * với các giá trị lấy từ bytecode. Chính là cơ chế {@code Proxy} mà bài t12 sẽ dùng.
 * - Ba hệ quả thực tế:
 * + {@code getClass()} trên một annotation VÔ DỤNG. Luôn dùng {@code annotationType()}.
 * + So sánh {@code anno.getClass() == Bean.class} luôn sai. Dùng
 * {@code anno.annotationType() == Bean.class}.
 * + {@code equals()} và {@code hashCode()} của annotation đã được đặc tả định nghĩa theo
 * GIÁ TRỊ các thuộc tính, nên hai annotation cùng kiểu cùng giá trị thì bằng nhau - dùng làm
 * khóa {@code Map} được.
 * <p>
 * 8. BA GIỚI HẠN CỦA {@code @Inherited} - LÝ DO SPRING PHẢI TỰ VIẾT LẠI
 * - Chỉ áp dụng cho annotation trên LỚP. Đặt {@code @Inherited} rồi mong method ghi đè kế
 * thừa annotation của method cha: KHÔNG xảy ra.
 * - KHÔNG kế thừa qua INTERFACE. Đã kiểm chứng: {@code @Bean} có {@code @Inherited} đặt trên
 * một interface, lớp {@code implements} interface đó thì {@code getAnnotations()} trả về
 * MẢNG RỖNG. Chỉ {@code extends} lớp mới kế thừa.
 * - Không có khái niệm META-ANNOTATION: {@code @MyAnno} được đánh dấu bằng
 * {@code @Transactional} thì {@code getAnnotation(Transactional.class)} vẫn trả {@code null}.
 * - Cả ba giới hạn này là lý do Spring có nguyên một bộ API riêng:
 * {@code AnnotatedElementUtils.findMergedAnnotation}, {@code AnnotationUtils.findAnnotation},
 * và {@code MergedAnnotations}. Chúng leo cả lớp cha, interface, method bị ghi đè, và cả
 * cây meta-annotation. {@code @RestController} tìm được vì nó mang
 * {@code @Controller} chính là nhờ bộ này.
 * - Quy tắc trong dự án Spring: ĐỪNG dùng {@code getAnnotation()} trần để tìm annotation của
 * Spring. Dùng {@code AnnotatedElementUtils}. Tự viết thì bạn sẽ thiếu đúng ba ca trên.
 * <p>
 * 9. MẪU CHUẨN - QUÉT VÀ XỬ LÝ ANNOTATION
 * - Khung code mà bài t12 và t13 sẽ dùng lại:
 * <pre>
 *   for (Method m : type.getDeclaredMethods()) {
 *       if (m.isSynthetic() || m.isBridge()) continue;       // bài t4 mục 7, t6 mục 12
 *       MyAnno anno = m.getAnnotation(MyAnno.class);
 *       if (anno == null) continue;
 *       m.setAccessible(true);                               // bài t6 mục 7
 *       handlers.put(anno.value(), m);                       // CACHE, không quét lại
 *   }
 * </pre>
 * - Ba nguyên tắc rút ra từ cả section:
 * + Quét MỘT LẦN lúc khởi động, cất vào {@code Map}. Không quét trong luồng xử lý request.
 * + Lọc {@code synthetic} và {@code bridge} trước khi đọc annotation.
 * + Kiểm tra {@code null} sau {@code getAnnotation}, đừng dựa vào
 * {@code isAnnotationPresent} rồi gọi lại {@code getAnnotation} - hai lần tra cứu.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - Decorator của TS là tương đương gần nhất, nhưng khác nhau ở bản chất:
 * + Annotation Java là DỮ LIỆU THỤ ĐỘNG. Nó không chạy gì; phải có ai đó đọc.
 * + Decorator TS là HÀM ĐƯỢC GỌI ngay lúc định nghĩa lớp. Nó chủ động sửa lớp.
 * - Hệ quả: TS không cần {@code RetentionPolicy} - decorator chạy rồi thì xong. Nhưng nếu
 * muốn ĐỌC LẠI metadata sau đó (đúng việc của bài này) thì phải tự lưu bằng
 * {@code reflect-metadata}: {@code Reflect.defineMetadata} khi decorate và
 * {@code Reflect.getMetadata} khi đọc. Java có sẵn cả hai chiều trong bytecode.
 * - NestJS {@code @Injectable}, {@code @Get}, {@code @Body} làm đúng việc Spring làm, và
 * cùng một kiến trúc: đánh dấu, lưu metadata, quét lúc khởi động, dựng bảng định tuyến.
 * Hiểu bài này là hiểu luôn NestJS.
 * - Annotation lặp lại không có bên TS - decorator chồng nhau là chuỗi hàm, không phải mảng
 * dữ liệu.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn lớp nào cài {@code AnnotatedElement}?
 * - Năm nhóm method đọc annotation, mỗi cái dùng khi nào?
 * - Ở bài này, chữ {@code Declared} mang nghĩa gì (khác các bài trước thế nào)?
 * - Đọc thuộc tính của một annotation bằng cách nào?
 * - Ba giá trị của {@code RetentionPolicy}, cái nào là mặc định, cái nào reflection thấy?
 * - Quên {@code @Retention(RUNTIME)} thì triệu chứng là gì?
 * - Annotation lặp lại được lưu thế nào trong file {@code .class}?
 * - {@code getAnnotations()} trên field có hai annotation lặp trả về mấy phần tử?
 * - {@code @Inherited} hoạt động trong trường hợp nào và KHÔNG hoạt động trong ba trường hợp nào?
 * - {@code anno.getClass()} trả về gì? Dùng gì thay thế?
 * - Hai annotation cùng kiểu cùng giá trị có bằng nhau không?
 * - {@code TYPE_USE} khác {@code TYPE} ở chỗ nào? Ba method đọc annotation trên kiểu?
 * - Vì sao trong dự án Spring không nên dùng {@code getAnnotation()} trần?
 */
public class t11_ReadingAnnotations {

    void main() {
        // TODO 1 (mục 2): Định nghĩa @Bean và @Serialized(format) đúng như trang gốc, đặt lên
        //   một lớp Person. In isAnnotationPresent, getAnnotations, và gọi annotation.format().
        //   Dán output.
        //
        // TODO 2 (mục 2, mục 6): BỎ dòng @Retention(RUNTIME) của @Bean rồi chạy lại TODO 1.
        //   Xác nhận annotation biến mất mà KHÔNG có lỗi nào. Đây là bài tập QUAN TRỌNG
        //   NHẤT của bài - nhớ triệu chứng này.
        //
        // TODO 3 (mục 2): Thử isAnnotationPresent(String.class) và dán lỗi BIÊN DỊCH.
        //   Ghi lại: đây là một trong số ít chỗ reflection còn được compiler bảo vệ.
        //
        // TODO 4 (mục 3): Định nghĩa @Validator repeatable với container @Validators, đặt hai
        //   cái lên một field. In getAnnotations() và getAnnotationsByType(). Dán cả hai.
        //
        // TODO 5 (mục 3): Đặt CHỈ MỘT @Validator lên một field khác. Chạy lại
        //   getAnnotations() và getAnnotationsByType() - quan sát container có xuất hiện
        //   không. Rút ra lý do nên luôn dùng getAnnotationsByType.
        //
        // TODO 6 (mục 4): Tạo @Bean có @Inherited, đặt lên Person, cho User extends Person.
        //   In getDeclaredAnnotations() và getAnnotations() của User.
        //
        // TODO 7 (mục 8): Lặp lại TODO 6 nhưng đặt @Bean lên một INTERFACE và cho lớp
        //   implements nó. Xác nhận KHÔNG kế thừa. Rồi thử với method ghi đè - cũng không.
        //
        // TODO 8 (mục 8): Tạo @MyTx được đánh dấu bằng chính @Bean (meta-annotation).
        //   Đặt @MyTx lên một lớp rồi getAnnotation(Bean.class) - xác nhận null.
        //   Viết hàm đệ quy tự tìm meta-annotation và giải quyết vấn đề.
        //
        // TODO 9 (mục 5): Định nghĩa @NonNull với TYPE_USE, dùng nó trên mệnh đề extends và
        //   implements. Đọc bằng getAnnotatedSuperclass và getAnnotatedInterfaces.
        //
        // TODO 10 (mục 5): Dựng lại nguyên ví dụ @ReThrowAsRuntimeException của trang gốc:
        //   annotation trên mệnh đề throws của constructor, client code đọc nó và quyết định
        //   bọc exception. Chạy và dán stack trace.
        //
        // TODO 11 (mục 7): In getClass(), annotationType(), và kiểm tra instanceof Proxy của
        //   một annotation instance. Rồi tạo hai instance của cùng annotation trên hai lớp
        //   khác nhau có cùng giá trị thuộc tính - so sánh bằng equals().
        //
        // TODO 12 (mục 9): Viết khung quét theo mẫu mục 9: định nghĩa @Handler("ten"), quét
        //   một lớp, dựng Map<String, Method>, rồi gọi handler theo tên.
        //   Giữ lại code này - bài t12 và t13 dùng tiếp.
        //
        // TODO 13 (mục 9): Đo thời gian: quét annotation của 1000 method mỗi lần gọi, so với
        //   quét một lần rồi tra Map. Con số này giải thích vì sao Spring khởi động chậm
        //   nhưng chạy nhanh.
    }
}
