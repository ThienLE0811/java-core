package com.javacore.api.reflection_method_handles.reflection;

/**
 * Retrieving Classes
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 2/13 của section The Reflection API.
 * - Bài dài nhất trong nhóm "đọc thông tin". Nó trả lời đúng một câu hỏi: làm sao có được
 * object {@code Class} trong tay. Mọi bài sau đều bắt đầu từ chỗ này.
 * - Một câu cho cả bài: {@code java.lang.Class} là CỬA VÀO duy nhất của Reflection API -
 * không có nó thì không tạo được {@code Field}, {@code Method}, {@code Constructor} nào.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/classes/">dev.java - Retrieving Classes</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - RETRIEVING CLASSES (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. VÌ SAO MỌI THỨ BẮT ĐẦU TỪ CLASS
 * - "The entry point for all reflection operations is java.lang.Class."
 * - Lý do kỹ thuật, nguyên văn: "None of the classes in java.lang.reflect that you need to
 * use to get reflective access to any element of a class have public constructors." Ngoại lệ
 * duy nhất là {@code ReflectPermission}, và lớp đó nay đã deprecate.
 * - Giống hệt {@code Pattern} và {@code Matcher} ở section Regex: không có {@code new}, phải
 * đi qua factory. Ở đây factory chính là các method của {@code Class}.
 * - Hai nhóm kiểu, cần phân biệt vì chúng lấy {@code Class} theo cách khác nhau:
 * + REFERENCE TYPE: class, enum, record, lambda, mảng (tất cả đều kế thừa {@code Object}),
 * và interface.
 * + PRIMITIVE TYPE: tập cố định tám cái - {@code boolean}, {@code byte}, {@code short},
 * {@code int}, {@code long}, {@code char}, {@code float}, {@code double}.
 * - "For every type of object, the Java virtual machine instantiates an immutable instance
 * of java.lang.Class". Chú ý chữ IMMUTABLE: mỗi kiểu có đúng một object {@code Class} duy
 * nhất trong một class loader, nên so sánh bằng {@code ==} là hợp lệ (bài t1 bước 26 mục 3c
 * đã dùng {@code getReturnType() == void.class}).
 * <p>
 * 2. CÁCH 1 - GETCLASS() TRÊN MỘT INSTANCE
 * - (Getting the Class of an Object)
 * - "If an instance of an object is available, then the simplest way to get its Class is to
 * invoke Object.getClass(). Of course, this only works for reference types which all inherit
 * from Object."
 * - Trang gốc đi qua sáu ca, mỗi ca dạy một điều:
 * <pre>
 *   "foo".getClass()               class java.lang.String
 *   System.console().getClass()    class java.io.ProxyingConsole
 *   Days.SATURDAY.getClass()       class org.devjava.Days
 *   new byte[1024].getClass()      class [B
 *   new HashSet&lt;&gt;() qua Set       class java.util.HashSet
 *   (Supplier) () -> "Hello"       class org.devjava.Main$$Lambda/0x00000290d4003538
 *   new Object() {}                class org.devjava.Main$1
 * </pre>
 * - Ghi chú theo từng ca:
 * + Chuỗi in ra có tiền tố {@code class} là do {@code Class.toString()} tự thêm, không phải
 * một phần của tên lớp.
 * + {@code System.console()} KHÔNG trả về {@code java.io.Console}. Trang gốc giải thích:
 * {@code Console} là lớp {@code sealed} chỉ cho phép một lớp con là {@code ProxyingConsole},
 * và lớp con đó không public nên bạn không gõ tên nó trong code được - JDK cấp lúc chạy.
 * Đây là ví dụ đầu tiên cho thấy KIỂU KHAI BÁO và KIỂU THẬT khác nhau.
 * + Enum: in ra gì còn phụ thuộc bạn khai báo enum ở package nào, là lớp riêng hay lớp lồng.
 * + Mảng {@code byte[]} ra {@code [B} - cú pháp mã hóa tên mảng, bài t3 mổ kỹ.
 * + {@code Set s = new HashSet()} ra {@code HashSet}: "it does not matter how the variable
 * is declared". Và lưu ý nguyên văn: "the generic type of this set is not there, due to type
 * erasure."
 * + Lambda: có class thật, nhưng tên sinh ra lúc chạy và ĐỔI GIỮA CÁC LẦN CHẠY. Trang gốc
 * cảnh báo nhẹ: "even if it is probably not advisable to do so."
 * + Lớp vô danh: tên dạng {@code Main$1} do trình biên dịch đặt theo thứ tự xuất hiện - thêm
 * một lớp vô danh phía trên là số thứ tự đổi hết.
 * <p>
 * 3. CÁCH 2 - CÚ PHÁP .CLASS
 * - (The .class Syntax)
 * - "If the type is available but there is no instance then it is possible to obtain a Class
 * by appending .class to the name of the type. This is also the easiest way to obtain the
 * Class for a primitive type."
 * - Kiểu nguyên thủy KHÔNG gọi {@code getClass()} được: {@code b.getClass()} với
 * {@code boolean b} là LỖI BIÊN DỊCH, vì "a boolean is a primitive type and cannot be
 * dereferenced". Phải viết {@code boolean.class}, in ra {@code boolean} - không có tiền tố
 * {@code class}.
 * - Dùng được với kiểu tham chiếu: {@code java.io.PrintStream.class}.
 * - Dùng được với mảng nhiều chiều: {@code int[][][].class} in ra {@code class [[[I}.
 * - Ưu điểm không được trang gốc nêu nhưng quan trọng: đây là cách DUY NHẤT được trình biên
 * dịch kiểm tra. Gõ sai tên lớp là lỗi lúc build, không phải lúc chạy.
 * <p>
 * 4. CÁCH 3 - CLASS.FORNAME()
 * - (The Method Class.forName())
 * - "If the fully-qualified name of a class is available, it is possible to get the
 * corresponding Class using the static method Class.forName()."
 * - Ba ràng buộc trang gốc nêu:
 * + KHÔNG dùng được cho kiểu nguyên thủy.
 * + Dùng được cho MỌI mảng, kể cả mảng kiểu nguyên thủy, theo cú pháp tên của
 * {@code Class.getName()}.
 * + "If this class does not exist, this method throws a ClassNotFoundException."
 * - Hai ví dụ mảng của trang gốc:
 * {@code Class.forName("[D")} tương đương {@code double[].class};
 * {@code Class.forName("[[Ljava.lang.String;")} tương đương {@code String[][].class}.
 * Chú ý dấu chấm phẩy ở cuối tên mảng kiểu tham chiếu - thiếu nó là
 * {@code ClassNotFoundException}.
 * <p>
 * 5. CÁCH 4 - CLASS.FORPRIMITIVENAME() (JAVA 22)
 * - (The Method Class.forPrimitiveName())
 * - "A method Class.forPrimitiveName() was added to the class Class in Java SE 22. This
 * method returns the corresponding class for any primitive type, including void."
 * - {@code Class.forPrimitiveName("int")} in ra {@code int}.
 * - Hai hành vi biên cần nhớ: trả về {@code null} nếu chuỗi không phải kiểu nguyên thủy, và
 * ném {@code NullPointerException} nếu truyền {@code null}. Trả {@code null} chứ KHÔNG ném
 * exception - đây là chỗ dễ sinh {@code NullPointerException} ở dòng sau nếu không kiểm tra.
 * - Nó lấp đúng lỗ hổng của mục 4: trước Java 22, nhận chuỗi {@code "int"} từ cấu hình thì
 * phải tự viết một bảng tra.
 * <p>
 * 6. CÁCH 5 - TRƯỜNG TYPE CỦA LỚP BAO
 * - (Retrieving a Primitive Type From a Wrapper Type)
 * - "Each wrapper class contains a static field named TYPE which is equal to the Class for
 * the primitive type being wrapped. This field is public and static."
 * - {@code Double.TYPE} in ra {@code double}, tức là {@code Double.TYPE == double.class}.
 * - Áp dụng được cả cho {@code void}: "Void.TYPE is identical to void.class."
 * - BẪY cần khắc sâu, trang gốc không nói thẳng nhưng suy ra được:
 * {@code Double.TYPE} KHÁC {@code Double.class}. Cái đầu là {@code double} (nguyên thủy),
 * cái sau là {@code java.lang.Double} (lớp bao). So sánh nhầm hai cái này làm
 * {@code getMethod} không tìm thấy method - lỗi kinh điển khi reflect vào method có tham số
 * nguyên thủy.
 * <p>
 * 7. ĐI TỪ MỘT CLASS SANG CLASS KHÁC
 * - (Getting a Class From Another Class)
 * <p>
 * - 7a. LỚP CHA: {@code getSuperclass()}
 * + "returns the only super class of a given class. It returns null for the Object class."
 * + Chữ ONLY nhắc lại: Java chỉ đơn kế thừa lớp. Lặp cho tới khi gặp {@code null} là leo hết
 * cây - ví dụ trang gốc leo từ {@code NullPointerException} lên {@code RuntimeException},
 * {@code Exception}, {@code Throwable}, {@code Object}.
 * + Đây chính là vòng lặp Spring và Jackson dùng để gom field của cả cây kế thừa.
 * <p>
 * - 7b. INTERFACE: {@code getInterfaces()}
 * + "returns an array of the interfaces DIRECTLY implemented by a given class. It does not
 * return any interface implemented by the super types of the given class."
 * + Trên interface thì trả về các interface mà nó trực tiếp mở rộng.
 * + {@code String.class.getInterfaces()} ra năm cái: {@code Serializable},
 * {@code Comparable}, {@code CharSequence}, {@code Constable}, {@code ConstantDesc}.
 * + Gọi trên MẢNG cho kết quả đáng nhớ: lớp cha là {@code Object}, interface là
 * {@code Cloneable} và {@code Serializable}. Trang gốc rút ra hai điều: "At runtime, arrays
 * are instances of Object" và mảng cài sẵn hai interface đó.
 * <p>
 * - 7c. LỚP THÀNH VIÊN: {@code getClasses()} so với {@code getDeclaredClasses()}
 * + {@code getClasses()} - "returns all the public classes, interfaces, records, and enums
 * that are members of the class, AND OF ALL ITS SUPER CLASSES."
 * + {@code getDeclaredClasses()} - "returns an array of all the member classes of this
 * class, whether they are public, protected, package protected, or private. INHERITED member
 * classes are not returned."
 * + Ví dụ trang gốc trên {@code Character}: {@code getClasses()} ra ba lớp
 * ({@code UnicodeBlock}, {@code UnicodeScript}, {@code Subset}), còn
 * {@code getDeclaredClasses()} ra thêm {@code Character$CharacterCache} - lớp thành viên
 * private.
 * + Đây là minh họa cụ thể của quy tắc {@code getX} / {@code getDeclaredX} ở bước 26 mục 6.
 * Hai trục: public-kèm-kế-thừa so với mọi-mức-truy-cập-nhưng-chỉ-lớp-này.
 * <p>
 * - 7d. DECLARING SO VỚI ENCLOSING
 * + {@code getDeclaringClass()} - "returns the class in which this class is declared. If
 * this class is not a member class of any class, then this method returns null."
 * + {@code getEnclosingClass()} - "returns the immediately enclosing class of a given class.
 * It returns null for a class that is not a member of any class."
 * + Hai cái này GIỐNG NHAU với inner class, KHÁC NHAU với lớp vô danh. Bảng kết quả của
 * trang gốc là chỗ đáng chép lại nguyên:
 * <pre>
 *                            getEnclosingClass()   getDeclaringClass()
 *   class Main                null                  -
 *   inner class User          class Main            class Main
 *   anonymous Main$1          class Main            null
 *   lambda                    null                  null
 * </pre>
 * + Cách nhớ: DECLARED nói về KHAI BÁO trong mã nguồn - lớp vô danh không được "khai báo"
 * như một thành viên nên là {@code null}. ENCLOSING nói về chỗ nó NẰM TRONG lúc chạy.
 * + Lambda cho {@code null} cả hai: nó không phải lớp thành viên của ai cả, bytecode sinh
 * động lúc chạy.
 * <p>
 * - 7e. LỚP KHAI BÁO CỦA MỘT MEMBER
 * + "Fields, methods, and constructors are modeled by their own class in the Reflection API:
 * Field, Method, and Constructor. They all implement a common interface: Member."
 * + {@code Member.getDeclaringClass()} trả về lớp khai báo member đó. Quan trọng khi duyệt
 * {@code getMethods()}: kết quả gồm cả method kế thừa, và đây là cách duy nhất biết method
 * đó thật ra của lớp nào.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. BA CÁCH LẤY CLASS KHÁC NHAU Ở ĐIỂM NÀO - BẢNG QUYẾT ĐỊNH
 * <pre>
 *                      kiểm tra lúc biên dịch   ném exception   chạy static initializer
 *   X.class            CÓ                       không           KHÔNG
 *   obj.getClass()     CÓ (cần có instance)     không           đã chạy rồi
 *   Class.forName(s)   KHÔNG                    ClassNotFound   CÓ
 * </pre>
 * - Cột thứ ba là chỗ trang gốc im lặng hoàn toàn mà hậu quả lại lớn nhất:
 * {@code Class.forName("com.a.B")} một tham số sẽ KHỞI TẠO lớp, tức là chạy mọi khối
 * {@code static} của lớp đó.
 * - Đây đúng là cơ chế {@code Class.forName("com.mysql.jdbc.Driver")} ngày xưa dùng để đăng
 * ký JDBC driver: lớp Driver tự gọi {@code DriverManager.registerDriver} trong static block.
 * - Bản ba tham số {@code Class.forName(name, false, loader)} thì KHÔNG khởi tạo. Dùng bản
 * này khi chỉ muốn kiểm tra lớp có tồn tại không - ví dụ dò xem thư viện tùy chọn có trong
 * classpath hay không, kiểu {@code ClassUtils.isPresent} của Spring.
 * - Quy tắc chọn: biết tên lớp lúc viết code thì LUÔN dùng {@code .class}. Chỉ dùng
 * {@code forName} khi tên lớp thật sự đến từ dữ liệu.
 * <p>
 * 9. CẢNH BÁO BẢO MẬT VỀ CLASS.FORNAME
 * - {@code Class.forName(userInput)} là lỗ hổng, không phải chuyện lý thuyết: nó nạp và KHỞI
 * TẠO một lớp bất kỳ có trong classpath, nghĩa là chạy static block của lớp đó. Đây là mắt
 * xích trong nhiều chuỗi khai thác deserialization.
 * - Nếu buộc phải nhận tên lớp từ ngoài: kiểm tra theo DANH SÁCH TRẮNG các tên được phép,
 * đừng chỉ kiểm tra tiền tố package - và dùng bản ba tham số với {@code initialize = false}.
 * - Cùng một bài học với {@code Pattern.quote} ở section Regex: dữ liệu ngoài vào thì phải
 * đi qua cổng kiểm soát, không bao giờ vào thẳng.
 * <p>
 * 10. GETCLASS() NÓI DỐI KHI CÓ PROXY - BẪY LỚN NHẤT TRONG SPRING
 * - Trang gốc đã hé lộ ở mục 2 với {@code ProxyingConsole}: kiểu thật khác kiểu khai báo.
 * Trong Spring chuyện này xảy ra hằng ngày.
 * - Bean có {@code @Transactional}, {@code @Async}, hay {@code @Cacheable} được Spring bọc
 * trong proxy. {@code bean.getClass()} khi đó trả về lớp sinh động dạng
 * {@code UserService$$SpringCGLIB$$0}, KHÔNG phải {@code UserService}.
 * - Hệ quả thật hay gặp: {@code bean.getClass().getAnnotation(MyAnno.class)} trả {@code null}
 * vì annotation nằm trên lớp gốc, còn {@code bean.getClass().getDeclaredFields()} ra một
 * đống field nội bộ của CGLIB.
 * - Cách đúng trong Spring: {@code AopUtils.getTargetClass(bean)} hoặc
 * {@code AopProxyUtils.ultimateTargetClass(bean)}.
 * - Hibernate có bẫy song song: entity lazy là một proxy, nên
 * {@code entity.getClass() == Order.class} là {@code false}. Đây là lý do {@code equals()}
 * của entity phải viết bằng {@code instanceof} chứ không bằng so sánh {@code getClass()}.
 * Hibernate cung cấp {@code Hibernate.getClass(entity)} để lấy lớp thật.
 * <p>
 * 11. TYPE ERASURE VÀ ĐƯỜNG VÒNG ĐỂ LẤY GENERIC
 * - Trang gốc nêu vấn đề ("the generic type of this set is not there") rồi hẹn sau. Tóm tắt
 * trước để không bị hụt:
 * - {@code getClass()} KHÔNG BAO GIỜ cho biết tham số generic của một instance. Thông tin đó
 * không tồn tại lúc chạy, không có mẹo nào lấy được.
 * - Nhưng generic trong KHAI BÁO thì còn giữ trong file {@code .class}, và lấy được qua
 * nhánh API riêng: {@code Field.getGenericType()}, {@code Method.getGenericReturnType()},
 * {@code Class.getGenericSuperclass()}, rồi ép sang {@code ParameterizedType} và gọi
 * {@code getActualTypeArguments()}.
 * - Phân biệt then chốt: khai báo {@code List<String> names;} trong một lớp thì ĐỌC ĐƯỢC
 * {@code String}; còn cầm một object {@code ArrayList} trong tay thì KHÔNG.
 * - Mẹo kinh điển dựa trên khác biệt này là "super type token": tạo lớp con vô danh
 * {@code new TypeReference<List<String>>() {}} để nhét generic vào phần khai báo lớp cha.
 * Jackson {@code TypeReference} và Spring {@code ParameterizedTypeReference} đều là cái này.
 * <p>
 * 12. ĐỐI CHIẾU TYPESCRIPT
 * - {@code obj.getClass()} gần nhất với {@code obj.constructor} - và {@code getName()} gần
 * với {@code obj.constructor.name}. Nhưng {@code constructor.name} chỉ là một chuỗi, còn
 * {@code Class} là một object có đầy đủ API để hỏi tiếp.
 * - {@code X.class} không có tương đương: trong TS, một {@code interface} hay {@code type}
 * biến mất hoàn toàn sau khi biên dịch. Chỉ {@code class} của TS mới còn lại ở runtime, và
 * khi đó chính tên class là giá trị dùng được.
 * - {@code Class.forName("a.b.C")} gần với {@code await import(path)} - nhận tên lúc chạy -
 * nhưng {@code forName} là đồng bộ, và cảnh báo bảo mật ở mục 9 thì giống hệt nhau.
 * - Type erasure: Java xóa THAM SỐ generic nhưng giữ lớp; TypeScript xóa TOÀN BỘ hệ thống
 * kiểu. Nên bẫy ở mục 11 của Java vẫn nhẹ hơn: Java còn đọc được generic trong khai báo,
 * TS thì không còn gì cả nếu không có {@code reflect-metadata}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 13. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Năm cách lấy một {@code Class}, cách nào dùng được cho kiểu nguyên thủy?
 * - Vì sao {@code java.lang.reflect} không có constructor public?
 * - {@code System.console().getClass()} in ra gì và vì sao không phải {@code Console}?
 * - {@code Double.TYPE} khác {@code Double.class} thế nào? Nhầm thì hỏng ở đâu?
 * - {@code Class.forName} làm gì ngoài việc trả về {@code Class}? Làm sao chặn việc đó?
 * - Vì sao {@code Class.forName(userInput)} là lỗ hổng bảo mật?
 * - {@code getClasses()} khác {@code getDeclaredClasses()} ở hai điểm nào?
 * - {@code getDeclaringClass()} và {@code getEnclosingClass()} khác nhau ở trường hợp nào?
 * - Lambda trả về gì cho cả hai method trên, vì sao?
 * - {@code getInterfaces()} trên một mảng trả về gì?
 * - Trong Spring, vì sao {@code bean.getClass()} có thể không phải lớp bạn mong đợi, và dùng
 * gì thay thế?
 * - Lấy được {@code String} từ {@code List<String>} trong trường hợp nào, và không được
 * trong trường hợp nào?
 */
public class t2_RetrievingClasses {

    void main() throws ClassNotFoundException {
        // TODO 1 (mục 2): Chạy đủ bảy dòng ở bảng mục 2 trên JDK 25 và dán output thật vào
        //   note. Chú ý tên lambda và tên lớp vô danh - chạy lại lần hai xem có đổi không.
        // Cách 1: có instance trong tay -> getClass()
        Object s = "foo";
        System.out.println("1. getClass()         -> " + s.getClass());

        // Cách 2: biết tên kiểu lúc viết code -> .class  (compiler kiểm tra giúp)
        System.out.println("2. .class             -> " + String.class);
        System.out.println("2. .class nguyên thủy -> " + int.class);

        // Cách 3: tên lớp nằm trong DỮ LIỆU (chuỗi) -> forName
        System.out.println("3. forName()          -> " + Class.forName("java.lang.String"));

        // Cách 4 (Java 22): tên kiểu nguyên thủy dạng chuỗi
        System.out.println("4. forPrimitiveName() -> " + Class.forPrimitiveName("int"));

        // Cách 5: trường TYPE của lớp bao
        System.out.println("5. Integer.TYPE       -> " + Integer.TYPE);

        // Mỗi kiểu chỉ có ĐÚNG MỘT object Class -> so sánh bằng ==
        System.out.println();
        System.out.println("s.getClass() == String.class  : " + (s.getClass() == String.class));
        System.out.println("Integer.TYPE == int.class     : " + (Integer.TYPE == int.class));
        System.out.println("Integer.TYPE == Integer.class : " + (Integer.TYPE == Integer.class));
        // TODO 2 (mục 2): Khai báo "CharSequence cs = new StringBuilder();" rồi in
        //   cs.getClass(). Giải thích vì sao kiểu khai báo không quyết định kết quả.
        //
        // TODO 3 (mục 3): Viết "boolean b = true; Class<?> c = b.getClass();" và dán lại lỗi
        //   biên dịch. Sửa thành boolean.class rồi in ra, so sánh chuỗi in ra với
        //   Boolean.class - khác nhau chỗ nào?
        //
        // TODO 4 (mục 3, mục 4): In int[][][].class và Class.forName("[[[I"). Xác nhận hai
        //   cái bằng nhau bằng toán tử ==. Vì sao dùng == được mà không cần equals?
        //
        // TODO 5 (mục 4): Gọi Class.forName("java.lang.String"), Class.forName("int"),
        //   Class.forName("[D"), Class.forName("[Ljava.lang.String"). Lập bảng kết quả hoặc
        //   exception. Chú ý trường hợp cuối thiếu dấu chấm phẩy.
        //
        // TODO 6 (mục 5): Gọi Class.forPrimitiveName("int"), ("void"), ("java.lang.String"),
        //   và (null). Ghi lại ba hành vi khác nhau: trả class, trả null, ném exception.
        //
        // TODO 7 (mục 6): Khẳng định Double.TYPE == double.class và Double.TYPE !=
        //   Double.class bằng assert. Sau đó viết một method "void f(double d)" và thử lấy nó
        //   bằng getMethod("f", Double.class) - dán lại exception và giải thích.
        //
        // TODO 8 (mục 7a): Viết vòng lặp leo getSuperclass() từ một lớp bất kỳ tới Object,
        //   in từng bậc. Thử với NullPointerException, với String, và với int[].
        //
        // TODO 9 (mục 7b): In getInterfaces() của String, của ArrayList, và của int[].
        //   Với ArrayList, kiểm tra xem Iterable có trong danh sách không - nếu không thì
        //   giải thích bằng chữ DIRECTLY trong định nghĩa.
        //
        // TODO 10 (mục 7c): Chạy getClasses() và getDeclaredClasses() trên Character và lập
        //   bảng khác biệt. Sau đó tự tạo một lớp có cả lớp thành viên public và private rồi
        //   lặp lại thí nghiệm.
        //
        // TODO 11 (mục 7d): Dựng lại đúng ví dụ bảng ở mục 7d: một inner class, một lớp vô
        //   danh, một lambda. In cả getEnclosingClass() và getDeclaringClass() cho từng cái.
        //   Dán output và đối chiếu với bảng.
        //
        // TODO 12 (mục 8): Tạo lớp có static block in ra một dòng. Lấy Class của nó theo ba
        //   cách: .class, Class.forName(name), Class.forName(name, false, loader).
        //   Ghi lại cách nào làm static block chạy.
        //
        // TODO 13 (mục 11): Khai báo field "private List<String> names;" trong một lớp. Dùng
        //   getDeclaredField("names").getGenericType(), ép sang ParameterizedType rồi in
        //   getActualTypeArguments()[0]. Sau đó thử lấy cùng thông tin đó từ một instance
        //   ArrayList và xác nhận KHÔNG lấy được.
        //
        // TODO 14 (mục 10): Nếu có sẵn dự án Spring, in bean.getClass() của một service có
        //   @Transactional và một service không có. So sánh hai tên lớp. Nếu chưa có dự án,
        //   ghi lại dự đoán của bạn rồi kiểm chứng sau.
    }
}
