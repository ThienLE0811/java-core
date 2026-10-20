package com.javacore.api.reflection_method_handles.introduction_to_method_handles;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;

/**
 * Introduction to Method Handles
 * <p>
 * 0. Vị trí bài này
 * - Bước 28/41, bài 1/1 trong section Introduction to Method Handles của lộ trình dev.java/learn.
 * - Đây là bài TỔNG KẾT và NÂNG CẤP của nhóm Reflection and Method Handles.
 *   + Bước 26 (introduction_to_java_reflection): Thấy được sức mạnh và sự kỳ diệu của Reflection.
 *   + Bước 27 (reflection): Cẩm nang tham khảo 13 bài đầy đủ mọi ngóc ngách của Reflection API.
 *   + Bước 28 (introduction_to_method_handles): Cách tiếp cận hiện đại hơn, cấp thấp hơn, hiệu năng
 *     tiệm cận bytecode trực tiếp, và là chìa khóa mở ra kiến trúc JVM hiện đại (invokedynamic, lambda, VarHandle).
 * - Một câu cho cả bài: Method Handles là con trỏ hàm có kiểu mạnh ở tầng máy ảo (JVM bytecode-level
 *   typed function pointer) - không thay thế khả năng duyệt tìm của Reflection, nhưng vượt trội ở
 *   tốc độ gọi và khả năng ghép nối pipeline biến đổi tham số.
 * - Trang gốc do Nataliia Dziubenko đóng góp, cấp phép UPL.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/introduction_to_method_handles/">dev.java - Introduction to Method Handles</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRODUCTION TO METHOD HANDLES (mục 1 đến 12)
 * ======================================================================
 * <p>
 * 1. METHOD HANDLES LÀ GÌ
 * - (What are method handles)
 * - Định nghĩa nguyên văn: "Method handles are a low-level mechanism used for method lookup and invocation.
 *   They are often compared to reflection, because both the Reflection API and method handles provide a means
 *   to invoke methods, constructors, and access fields."
 * - Bản chất: "It's a directly invocable reference to an underlying method, constructor, or field.
 *   The Method Handle API allows manipulations on top of a simple pointer to the method that allows us
 *   to insert or reorder the arguments, transform the return values, etc."
 * - Hai từ khóa cốt lõi:
 *   + "Directly invocable reference": Một con trỏ hàm trực tiếp ở cấp bytecode.
 *   + "Manipulations / Combinators": Khả năng gói, chèn, đổi thứ tự, biến đổi kiểu tham số và giá trị trả về
 *     thành một pipeline thực thi mới mà không cần sinh class wrapper thủ công.
 * <p>
 * 2. KIỂM TRA QUYỀN TRUY CẬP (ACCESS CHECKING)
 * - (Access checking)
 * - Khác biệt mang tính nền tảng so với Reflection, nguyên văn:
 *   "With reflection, each call results in access checks for the caller.
 *   For method handles, the access is only checked when the method handle is created."
 * - Cơ chế bảo mật dựa trên năng lực (Capability-based security):
 *   + Reflection: Mỗi lần gọi {@code method.invoke(...)} là một lần JVM phải kiểm tra xem caller có quyền không.
 *   + MethodHandle: Chỉ kiểm tra MỘT LẦN DUY NHẤT lúc tạo ra qua {@code Lookup}. Sau khi đã tạo ra,
 *     MethodHandle trở thành một "tấm vé vào cửa" (token/capability).
 * - CẢNH BÁO NGUY HIỂM VỀ RÒ RỈ QUYỀN (Security Leak):
 *   "It is important to keep in mind that if the method handle is created within a context where it can
 *   access non-public members, when passed outside, it can still access those non-public members.
 *   As a result, non-public members can potentially be accessed from code where they shouldn't be accessible."
 * - Trách nhiệm thuộc về lập trình viên: Phải giữ MethodHandle đó private, hoặc khởi tạo Lookup với
 *   phạm vi giới hạn thích hợp (như {@code publicLookup()}).
 * <p>
 * 3. METHOD HANDLE LOOKUP
 * - (Method handle lookup)
 * - Lớp {@code MethodHandles.Lookup} đóng vai trò là factory tạo ra mọi MethodHandle.
 * - Ngữ cảnh tra cứu (Lookup Context):
 *   + {@code MethodHandles.lookup()}: Tạo Lookup mang quyền hạn của chính class đang gọi (caller class).
 *     Nó có quyền truy cập mọi member của class đó (kể cả private, protected, package-private, public).
 *   + {@code MethodHandles.publicLookup()}: Chỉ có quyền tìm kiếm các member {@code public} trong các class {@code public}
 *     thuộc các package được export vô điều kiện.
 * <p>
 * 4. METHOD TYPE - CHỮ KÝ CỦA PHƯƠNG THỨC
 * - (Method type)
 * - Biểu diễn chữ ký kiểu của method (kiểu trả về và danh sách kiểu tham số), đại diện bởi {@code MethodType}.
 * - Quy tắc khởi tạo qua factory method:
 *   {@code MethodType.methodType(Class<?> returnType, Class<?>... parameterTypes)}
 *   Ví dụ:
 *   + {@code MethodType.methodType(String.class, char.class, char.class)}: nhận 2 char, trả về String.
 *   + {@code MethodType.methodType(void.class)}: method không nhận tham số, trả về void.
 * - Đặc tính quan trọng: {@code MethodType} là bất biến (immutable) và được JVM chia sẻ/cache nội bộ (interned).
 * <p>
 * 5. TRA CỨU PHƯƠNG THỨC VÀ CONSTRUCTOR
 * - Cần kết hợp cả 3 thành phần: {@code Lookup} + tên phương thức (nếu có) + {@code MethodType}:
 *   + Instance method (virtual dispatch): {@code lookup.findVirtual(Class<?> refc, String name, MethodType type)}
 *     Ví dụ: {@code lookup.findVirtual(String.class, "replace", MethodType.methodType(String.class, char.class, char.class))}
 *     LƯU Ý: MethodHandle tạo ra từ {@code findVirtual} sẽ nhận thêm tham số ĐẦU TIÊN là đối tượng nhận (receiver / this).
 *   + Static method: {@code lookup.findStatic(Class<?> refc, String name, MethodType type)}
 *     Ví dụ: {@code lookup.findStatic(String.class, "valueOf", MethodType.methodType(String.class, Object.class))}
 *   + Constructor: {@code lookup.findConstructor(Class<?> refc, MethodType type)}
 *     LƯU Ý: {@code type} của constructor LUÔN có kiểu trả về là {@code void.class}, nhưng MethodHandle trả về
 *     khi gọi lại sinh ra instance của chính class đó!
 *   + Special method (gọi super hoặc private method): {@code lookup.findSpecial(Class<?> refc, String name, MethodType type, Class<?> specialCaller)}
 * <p>
 * 6. GỌI METHOD HANDLE (METHOD HANDLE INVOCATION)
 * - (Method handle invocation)
 * - Ba phương thức gọi chính:
 *   + 6a. {@code invokeExact(Object... args)}: Yêu cầu khớp kiểu TUYỆT ĐỐI 100% tại call-site (cả kiểu tham số lẫn kiểu trả về).
 *     Không tự động boxing/unboxing, không tự ép kiểu con -> cha. Nếu sai lệch dù chỉ là gán kiểu trả về cho Object,
 *     JVM ném ngay {@code WrongMethodTypeException}.
 *   + 6b. {@code invoke(Object... args)}: Linh hoạt hơn. Tự động chuyển đổi kiểu (boxing/unboxing, widening, casting)
 *     thông qua bước gọi ngầm {@code asType()} trước khi chuyển tiếp sang {@code invokeExact}.
 *   + 6c. {@code invokeWithArguments(Object... args)} hoặc {@code invokeWithArguments(List<?> args)}:
 *     Cho phép truyền danh sách tham số dưới dạng mảng/List (giống Reflection). Nếu số lượng đối số vượt quá số lượng
 *     mong đợi, các đối số dư sẽ được gộp vào mảng cuối cùng (nếu là varargs).
 * - NGOẠI LỆ: Cả {@code invokeExact} và {@code invoke} đều khai báo {@code throws Throwable}.
 *   Bất kỳ method đích ném ngoại lệ gì (kể cả checked exception), MethodHandle ném thẳng ra ngoại lệ đó nguyên bản
 *   mà KHÔNG bọc vào {@code InvocationTargetException} như Reflection API. Caller bắt buộc phải bắt {@code Throwable}
 *   hoặc khai báo {@code throws Throwable}.
 * <p>
 * 7. THAO TÁC VỚI FIELD
 * - (Accessing fields)
 * - MethodHandle có thể dùng để đọc và ghi trực tiếp vào field:
 *   + Instance field getter: {@code lookup.findGetter(Class<?> refc, String name, Class<?> type)}
 *   + Instance field setter: {@code lookup.findSetter(Class<?> refc, String name, Class<?> type)}
 *   + Static field getter: {@code lookup.findStaticGetter(Class<?> refc, String name, Class<?> type)}
 *   + Static field setter: {@code lookup.findStaticSetter(Class<?> refc, String name, Class<?> type)}
 * - Lưu ý từ trang gốc: Mặc dù MethodHandle đọc ghi field được, thực tế hiện đại người ta ưu tiên
 *   sử dụng {@code VarHandle} (từ Java 9) để truy cập field an toàn và có các cơ chế memory fence/atomic operations.
 * <p>
 * 8. LÀM VIỆC VỚI MẢNG
 * - (Working with arrays)
 * - Lớp {@code MethodHandles} cung cấp sẵn các MethodHandle dựng sẵn để thao tác mảng (không cần access check qua Lookup):
 *   + {@code MethodHandles.arrayConstructor(Class<?> arrayClass)}: tạo mảng mới theo kích thước truyền vào.
 *   + {@code MethodHandles.arrayElementGetter(Class<?> arrayClass)}: đọc phần tử tại index.
 *   + {@code MethodHandles.arrayElementSetter(Class<?> arrayClass)}: ghi giá trị vào phần tử tại index.
 *   + {@code MethodHandles.arrayLength(Class<?> arrayClass)}: lấy độ dài mảng.
 * <p>
 * 9. XỬ LÝ NGOẠI LỆ (EXCEPTION HANDLING)
 * - (Exception handling)
 * - 9a. {@code MethodHandles.catchException(MethodHandle target, Class<? extends Throwable> exType, MethodHandle handler)}:
 *   Bọc method handle đích bên trong một khối catch. Nếu method đích ném {@code exType}, quyền điều khiển được chuyển
 *   sang {@code handler}. Handler phải nhận tham số đầu tiên là ngoại lệ đó, theo sau là các đối số gốc.
 * - 9b. {@code MethodHandles.tryFinally(MethodHandle target, MethodHandle cleanup)}:
 *   Bọc method handle đích bên trong cấu trúc try-finally. {@code cleanup} luôn được chạy dù target có ném ngoại lệ
 *   hay thực thi thành công.
 * <p>
 * 10. BIẾN ĐỔI METHOD HANDLE (METHOD HANDLE TRANSFORMATIONS / COMBINATORS)
 * - (Method handle transformations)
 * - Điểm độc nhất vô nhị mà Reflection API hoàn toàn không có:
 *   + 10a. {@code asType(MethodType newType)}: Biến đổi kiểu chữ ký của MethodHandle (ép kiểu ngầm, unboxing/boxing).
 *   + 10b. {@code MethodHandles.permuteArguments(MethodHandle target, MethodType newType, int... reorder)}:
 *     Đổi thứ tự, nhân bản, hoặc hoán vị các tham số đầu vào.
 *   + 10c. {@code MethodHandles.insertArguments(MethodHandle target, int pos, Object... values)}:
 *     Gán cứng trước (pre-bind / curry) một hoặc nhiều tham số tại vị trí chỉ định.
 *     Tương đương việc tạo ra closure/partial application.
 *   + 10d. {@code MethodHandles.filterArguments(MethodHandle target, int pos, MethodHandle... filters)}:
 *     Tiền xử lý biến đổi một hoặc nhiều tham số trước khi chuyển vào target method handle.
 *   + 10e. {@code MethodHandles.foldArguments(MethodHandle target, int pos, MethodHandle combiner)}:
 *     Gom một tập tham số, tính toán ra kết quả phụ, rồi chèn kết quả đó vào vị trí đối số mong muốn của target.
 *   + 10f. {@code MethodHandles.filterReturnValue(MethodHandle target, MethodHandle filter)}:
 *     Lấy giá trị trả về của target truyền làm đầu vào cho hàm biến đổi filter.
 * <p>
 * 11. METHOD HANDLES SO VỚI REFLECTION API
 * - (Method Handles vs Reflection API)
 * - Bảng so sánh 4 tiêu chí cốt lõi:
 *   + Duyệt tìm và soi cấu trúc (Inspection / Member listing):
 *     Reflection CÓ (getMethods, getDeclaredFields,...). Method Handles KHÔNG CÓ (phải biết rõ tên và kiểu mới lookup được).
 *   + Thời điểm kiểm tra quyền truy cập (Access checking):
 *     Reflection kiểm tra MỖI LẦN GỌI. Method Handles kiểm tra MỘT LẦN DUY NHẤT LÚC LOOKUP.
 *   + Tốc độ thực thi (Performance):
 *     Reflection có chi phí boxing, kiểm tra quyền liên tục, khó JIT inline.
 *     Method Handles nếu là {@code static final} sẽ được JVM inlining hoàn toàn thành direct call bytecode.
 *   + Khả năng biến đổi (Transformation / Adapters):
 *     Reflection chỉ gọi trực tiếp. Method Handles có cả một bộ công cụ combinator phong phú (permute, insert, filter, fold).
 * <p>
 * 12. CHUYỂN ĐỔI QUA LẠI GIỮA REFLECTION VÀ METHOD HANDLES
 * - (Conversion between Reflection API and method handles)
 * - Từ Reflection sang MethodHandle (cần Lookup để kiểm tra quyền):
 *   + {@code lookup.unreflect(Method)}
 *   + {@code lookup.unreflectSpecial(Method, Class<?>)}
 *   + {@code lookup.unreflectConstructor(Constructor<?>)}
 *   + {@code lookup.unreflectGetter(Field)}, {@code lookup.unreflectSetter(Field)}
 * - Từ MethodHandle ngược về Reflection:
 *   + {@code MethodHandles.reflectAs(Class<T> expectedType, MethodHandle target)}
 *   Lưu ý: Chỉ chuyển đổi ngược được nếu MethodHandle là handle gốc trực tiếp, chưa qua bất kỳ biến đổi (combinator) nào!
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 13. MA THUẬT @PolymorphicSignature CỦA JAVAC
 * - Khi nhìn vào mã nguồn JDK của {@code invokeExact} và {@code invoke}, bạn sẽ thấy khai báo:
 *   {@code public final native @PolymorphicSignature Object invokeExact(Object... args) throws Throwable;}
 * - Tại sao ta truyền tham số nguyên thủy (ví dụ int, char) mà không bị tự động bọc thành mảng Object[]?
 * - Trình biên dịch Java (javac) đối xử đặc biệt với annotation {@code @PolymorphicSignature}:
 *   Nó KHÔNG sinh mã tạo mảng varargs, mà sinh thẳng mã bytecode {@code invokevirtual} với signature
 *   chính xác tại dòng lệnh gọi của bạn!
 *   Ví dụ: {@code (String) mh.invokeExact("a", 'b')} -> bytecode sinh ra là:
 *   {@code invokevirtual MethodHandle.invokeExact:(Ljava/lang/String;C)Ljava/lang/String;}
 * - Đó là lý do vì sao {@code invokeExact} cực kỳ khắt khe: kiểu ép trả về cũng tham gia vào bytecode signature!
 * <p>
 * 14. JIT COMPILER INLINING VÀ QUY TẮC "STATIC FINAL METHODHANDLE"
 * - Muốn MethodHandle đạt tốc độ tối đa ngang ngửa gọi trực tiếp (direct invocation), MethodHandle PHẢI
 *   được lưu trong biến {@code static final}.
 * - Khi là hằng số biên dịch, JIT C2 Compiler của JVM có thể nhận dạng được target handle và tiến hành
 *   "inlining" mã máy của phương thức đích thẳng vào call-site, loại bỏ hoàn toàn chi phí gọi gián tiếp.
 * <p>
 * 15. NỀN TẢNG CỦA JAVA HIỆN ĐẠI: INVOKEDYNAMIC VÀ LAMBDA
 * - Method Handles ra đời trong Java 7 (JSR 292) để phục vụ opcode {@code invokedynamic}.
 * - Từ Java 8: Biểu thức Lambda không sinh ra anonymous inner class như nhiều người tưởng.
 *   Nó sinh ra chỉ lệnh {@code invokedynamic} trỏ đến {@code LambdaMetafactory.metafactory(...)},
 *   bên dưới sử dụng MethodHandle để bind method thực thi với functional interface lúc chạy.
 * - Từ Java 9: Phép cộng chuỗi {@code "a" + b + "c"} (JEP 280) dùng {@code StringConcatFactory} sinh MethodHandle
 *   thay cho {@code StringBuilder}, tối ưu bộ nhớ vượt trội.
 * <p>
 * 16. JAVA MODULE SYSTEM VÀ MethodHandles.privateLookupIn
 * - Trong Java 9+, Java Module System (JPMS) khóa chặt deep reflection ({@code setAccessible(true)}).
 * - Giải pháp chuẩn mực cho các framework (như Hibernate, Spring, Jackson) truy cập private members
 *   là sử dụng {@code MethodHandles.privateLookupIn(targetClass, callerLookup)}, với điều kiện module
 *   chứa targetClass đã mở (opens) gói tương ứng cho caller.
 * <p>
 * 17. TỔNG QUAN VỀ VARHANDLE (JAVA 9+)
 * - {@code VarHandle} là anh em của {@code MethodHandle}, chuyên dùng cho truy cập biến và phần tử mảng.
 * - Thay thế an toàn và tiêu chuẩn cho {@code sun.misc.Unsafe}.
 * - Cung cấp các chế độ truy cập bộ nhớ của Java Memory Model: Plain, Opaque, Release/Acquire, Volatile,
 *   cùng các thao tác nguyên tử (Atomic CAS, getAndAdd, bitwise operations).
 * <p>
 * ======================================================================
 * ĐỐI CHIẾU TYPESCRIPT / JAVASCRIPT
 * ======================================================================
 * <p>
 * 18. METHOD HANDLES SO VỚI JS/TS FUNCTION POINTERS & CURRYING
 * - {@code MethodHandles.insertArguments} hay {@code mh.bindTo(receiver)} tương đương hoàn toàn
 *   với {@code fn.bind(this, arg1, arg2)} trong JS/TS.
 * - {@code MethodHandles.permuteArguments} và {@code filterArguments} tương tự các kỹ thuật Higher-Order
 *   Functions (HOC), Ramda/Lodash {@code curry()}, {@code compose()}, và {@code pipe()}.
 * - {@code invokeWithArguments} tương đương với {@code fn.apply(null, args)}.
 * - Khác biệt lớn nhất: Trong JS, con trỏ hàm là công dân hạng nhất (first-class citizen) tự nhiên.
 *   Trong Java, máy ảo JVM vốn được thiết kế chặt chẽ xoay quanh Class và VTable, nên {@code MethodHandle}
 *   chính là cây cầu cấp thấp giúp JVM đạt được tính linh hoạt của functional language với hiệu năng máy ảo C++.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 19. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - MethodHandle khác Reflection API ở 3 điểm cơ bản nào?
 * - Tại sao nói kiểm tra quyền của MethodHandle là "capability-based"? Nếu vô tình để lộ một MethodHandle private thì sao?
 * - {@code MethodHandles.lookup()} khác {@code MethodHandles.publicLookup()} ở đâu?
 * - Phân biệt {@code invokeExact}, {@code invoke}, và {@code invokeWithArguments}. Khi nào bị {@code WrongMethodTypeException}?
 * - Cả {@code invokeExact} và {@code invoke} ném ngoại lệ gì? Vì sao không phải là {@code InvocationTargetException}?
 * - Khi tra cứu method instance bằng {@code findVirtual}, danh sách kiểu tham số trong {@code MethodType} có gì đặc biệt so với chữ ký lúc gọi?
 * - Muốn JIT compiler tối ưu hóa inlining một MethodHandle thì cần khai báo nó như thế nào?
 * - Liệt kê ít nhất 3 bộ biến đổi (combinators) của MethodHandles và nêu tình huống áp dụng.
 * - Làm cách nào để chuyển một đối tượng {@code java.lang.reflect.Method} thành {@code MethodHandle} và ngược lại?
 * - Lambda trong Java 8 và phép cộng chuỗi từ Java 9 liên quan mật thiết thế nào đến Method Handles?
 */
public class t1_IntroductionToMethodHandles {

    void main() throws Throwable {
        // Lookup là "giấy phép". Quyền được kiểm tra DUY NHẤT ở đây, lúc gọi không kiểm tra nữa (mục 2, 3)
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // 1. MethodType chỉ mô tả CHỮ KÝ: trả về String, nhận 2 char. Chưa gắn với class nào (mục 4)
        MethodType replaceType = MethodType.methodType(String.class, char.class, char.class);
        System.out.println("1. MethodType          -> " + replaceType);

        // 2. findVirtual: handle lấy ra có THÊM receiver làm tham số đầu tiên (mục 5)
        MethodHandle replace = lookup.findVirtual(String.class, "replace", replaceType);
        System.out.println("2. replace.type()      -> " + replace.type());
        String replaced = (String) replace.invokeExact("hello world", 'o', '0');
        System.out.println("   invokeExact         -> " + replaced);

        // 3. invokeExact khớp kiểu TUYỆT ĐỐI: chỉ cần đổi chỗ ép kiểu trả về sang Object là
        //    chữ ký tại call-site đã khác -> WrongMethodTypeException (mục 6a, mục 13)
        try {
            Object wrong = replace.invokeExact("hello world", 'o', '0');
            System.out.println(wrong);
        } catch (WrongMethodTypeException e) {
            System.out.println("3. ép về Object        -> WrongMethodTypeException: " + e.getMessage());
        }

        // 4. Vẫn lời gọi đó, đổi sang invoke(): nó tự asType() nên chạy bình thường (mục 6b)
        Object loose = replace.invoke("hello world", 'o', '0');
        System.out.println("4. invoke()            -> " + loose);

        // 5. findStatic: không có receiver, chữ ký đúng bằng chữ ký thật của method (mục 5)
        MethodHandle valueOf = lookup.findStatic(String.class, "valueOf",
                MethodType.methodType(String.class, int.class));
        System.out.println("5. valueOf(123)        -> " + (String) valueOf.invokeExact(123));

        System.out.println();
        // TODO 1 (mục 3 & 4): Tạo MethodHandles.Lookup qua MethodHandles.lookup().
        //   Tạo một MethodType mô tả hàm nhận vào String và trả về int (ví dụ String.length).
        //   In đối tượng Lookup và MethodType ra console, quan sát định dạng chuỗi của chúng.
        //
        // TODO 2 (mục 5 & 6a): Dùng lookup.findVirtual() để lấy MethodHandle của String.replace(char, char).
        //   Gọi bằng invokeExact() với receiver và 2 ký tự.
        //   Thử đổi kiểu trả về khi ép kiểu (hoặc gán vào Object) để chứng minh invokeExact đòi hỏi khớp tuyệt đối.
        //
        // TODO 3 (mục 5 & 6a): Dùng lookup.findStatic() để lấy MethodHandle của String.valueOf(int).
        //   Gọi bằng invokeExact(123) và in kết quả.
        //
        // TODO 4 (mục 5 & 6a): Dùng lookup.findConstructor() để lấy MethodHandle tạo mới StringBuilder(String).
        //   Lưu ý: MethodType của constructor phải có return type là void.class.
        //   Gọi invokeExact và in đối tượng nhận được.
        //
        // TODO 5 (mục 6b): Thực hành sự linh hoạt của invoke().
        //   Dùng MethodHandle của String.valueOf(Object). Gọi invoke với một chuỗi String hoặc primitive int,
        //   quan sát khả năng tự động ép kiểu và boxing của invoke() so với invokeExact().
        //
        // TODO 6 (mục 6c): Dùng invokeWithArguments() để truyền tham số dưới dạng List.of(...)
        //   hoặc mảng Object[] vào một MethodHandle.
        //
        // TODO 7 (mục 7): Tạo một class mẫu Person(name, age) với field private và public.
        //   Dùng findGetter, findSetter để đọc và ghi giá trị của field qua MethodHandle.
        //
        // TODO 8 (mục 8): Sử dụng MethodHandles.arrayConstructor, arrayElementGetter, arrayElementSetter
        //   và arrayLength để tạo mảng String[5], gán giá trị, đọc lại và in kích thước mảng mà không dùng cú pháp Java thông thường.
        //
        // TODO 9 (mục 9a): Tạo một method có khả năng ném IllegalArgumentException và một method handler cứu hộ.
        //   Dùng MethodHandles.catchException để gói method đích lại. Gọi thử nghiệm với tham số gây lỗi và không gây lỗi.
        //
        // TODO 10 (mục 9b): Dùng MethodHandles.tryFinally để tạo một wrapper đảm bảo hàm dọn dẹp (cleanup)
        //   luôn được thực thi khi kết thúc target method.
        //
        // TODO 11 (mục 10a): Dùng asType() để chuyển đổi MethodType của một handle chấp nhận Object sang nhận String.
        //
        // TODO 12 (mục 10b): Tạo method test(int a, String b). Dùng MethodHandles.permuteArguments
        //   để đảo thứ tự thành test(String b, int a) và thực hiện gọi invoke.
        //
        // TODO 13 (mục 10c): Dùng MethodHandles.insertArguments để bind trước tham số đầu tiên (hoặc dùng bindTo cho receiver).
        //   Kiểm chứng chữ ký mới chỉ còn lại các tham số chưa bind.
        //
        // TODO 14 (mục 10d): Tạo một hàm cộng 2 số int. Dùng MethodHandles.filterArguments để lọc (ví dụ: nhân đôi)
        //   cả 2 đối số trước khi đưa vào hàm cộng.
        //
        // TODO 15 (mục 10e): Thực hành MethodHandles.foldArguments để tính toán trước tổng 2 đối số cuối
        //   rồi truyền kết quả đó vào đối số thứ nhất của target method.
        //
        // TODO 16 (mục 10f): Dùng MethodHandles.filterReturnValue để kết hợp một hàm trả về chuỗi thường
        //   với một hàm biến chuỗi thành chữ hoa (uppercase).
        //
        // TODO 17 (mục 12): Lấy một java.lang.reflect.Method bằng Reflection truyền thống, sau đó dùng lookup.unreflect()
        //   để chuyển nó thành MethodHandle. Dùng MethodHandles.reflectAs() để chuyển ngược lại.
        //
        // TODO 18 (mục 14): Viết một benchmark nhỏ (so sánh nanoTime) giữa 3 hình thức khi gọi 1.000.000 lần:
        //   1) Direct call: obj.method()
        //   2) Method.invoke(obj) qua Reflection
        //   3) static final MethodHandle qua invokeExact.
    }
}
