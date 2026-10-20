package com.javacore.api.reflection_method_handles.reflection;

import java.lang.reflect.Field;

/**
 * Reading and Writing Fields
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 5/13 của section The Reflection API. Mở đầu nhóm TÁC ĐỘNG.
 * - Bốn bài đầu chỉ đọc thông tin. Từ bài này trở đi bạn thay đổi được trạng thái chương
 * trình, nên mọi cảnh báo ở bài t1 bắt đầu có răng.
 * - Một câu cho cả bài: {@code setAccessible(true)} KHÔNG làm field hết private - nó chỉ tắt
 * khâu kiểm tra quyền. Hiểu sai câu này là nguồn của phần lớn hiểu lầm về reflection.
 * - Đây là bài nền của {@code @Autowired} trên field, của Jackson, của Hibernate. Bài t13
 * sẽ dựng lại chúng, nhưng cơ chế nằm hết ở đây.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/fields/">dev.java - Reading and Writing Fields</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING AND WRITING FIELDS (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. TÌM FIELD - BỐN METHOD
 * - (Locating Fields)
 * - Hai trục phân loại, trang gốc nói rõ: tìm MỘT field theo tên hay lấy TẤT CẢ; và lấy của
 * riêng lớp này hay lấy cả cây kế thừa lên tới {@code Object}.
 * - Bảng tóm tắt của trang gốc, đáng học thuộc:
 * <pre>
 *                            trả mảng?   kể cả kế thừa?   thấy private?
 *   getDeclaredField(String)   không        không            CÓ
 *   getField(String)           không        CÓ               không
 *   getDeclaredFields()        CÓ           không            CÓ
 *   getFields()                CÓ           CÓ               không
 * </pre>
 * - Quy tắc nhớ một dòng: chữ {@code Declared} đổi lấy "thấy được private" bằng cái giá
 * "không leo lên lớp cha". Không có method nào cho bạn cả hai - muốn cả hai thì phải tự
 * lặp, xem mục 9.
 * - Ví dụ trang gốc trên {@code ArrayList}, chạy {@code getDeclaredFields()} kèm
 * {@code accessFlags()}:
 * <pre>
 *   serialVersionUID                  [PRIVATE, STATIC, FINAL]
 *   DEFAULT_CAPACITY                  [PRIVATE, STATIC, FINAL]
 *   EMPTY_ELEMENTDATA                 [PRIVATE, STATIC, FINAL]
 *   DEFAULTCAPACITY_EMPTY_ELEMENTDATA [PRIVATE, STATIC, FINAL]
 *   elementData                       [TRANSIENT]
 *   size                              [PRIVATE]
 * </pre>
 * + "only the fields from the ArrayList class itself have been found, whatever their
 * modifier is."
 * + Chi tiết đáng chú ý: {@code elementData} chỉ có cờ {@code TRANSIENT} - nó là
 * package-private, và package-private KHÔNG có bit riêng trong bitmask. Vắng mặt cả
 * {@code PUBLIC}, {@code PRIVATE}, {@code PROTECTED} chính là cách biểu diễn nó.
 * - Còn {@code getFields()} trên {@code ArrayList} ra {@code 0}: "there are no public
 * fields in ArrayList, nor in any of its super classes."
 * + Bài học thực dụng: {@code getFields()} gần như vô dụng với code viết tử tế, vì code viết
 * tử tế không có field public. Trong thực tế bạn sẽ luôn dùng {@code getDeclaredFields()}.
 * <p>
 * 2. KIỂU CỦA FIELD
 * - (Obtaining Field Types)
 * - Hai cách, và trang gốc nói thẳng cách dễ có giới hạn: {@code getType()} "works well as
 * long as the type of the field is not generic. If it is, then the type is just Object,
 * which may give you ambiguous information."
 * <p>
 * - 2a. KIỂU KHÔNG GENERIC: {@code Field.getType()} trả về {@code Class}.
 * + Với {@code private String name;} thì ra {@code java.lang.String}.
 * + Lưu ý trang gốc nhắc: "the getDeclaredField() can throw a NoSuchFieldException."
 * <p>
 * - 2b. KIỂU GENERIC: {@code Field.getGenericType()} trả về {@code Type} (từ Java SE 5).
 * + Với {@code class Box<T> { private T t; }}:
 * {@code getType()} ra {@code java.lang.Object} - kiểu đã bị xóa;
 * {@code getGenericType().getTypeName()} ra {@code T} - đúng như khai báo.
 * + GIỚI HẠN quan trọng nhất, nguyên văn: "this method cannot give you access to the real
 * type at runtime, it can only give you the type you declared in the source code."
 * + Trang gốc chứng minh bằng ví dụ: tạo {@code new Box<String>("Hello")} rồi hỏi
 * {@code getGenericType()} vẫn ra {@code T}, KHÔNG ra {@code String}.
 * + Đây là ranh giới cần khắc sâu: reflection đọc được KHAI BÁO, không đọc được thứ xảy ra
 * lúc chạy. Nối tiếp bài t2 mục 11.
 * <p>
 * 3. MODIFIER CỦA FIELD
 * - (Retrieving Field Modifiers)
 * - Hoạt động giống hệt modifier của lớp ở bài t4: {@code Field.getModifiers()} trả
 * {@code int}, giải mã bằng các static method của {@code Modifier}.
 * - {@code Field.accessFlags()} cũng dùng được, và trang gốc gợi ý đúng cách kiểm tra:
 * {@code nameField.accessFlags().contains(AccessFlag.STATIC)}. Đọc dễ hơn hẳn so với bitmask.
 * - Nhắc lại cảnh báo của bài t4: "there is not a one to one correspondence between
 * modifiers and access flags."
 * <p>
 * 4. ĐỌC VÀ GHI GIÁ TRỊ
 * - (Getting and Setting Field Values)
 * - Hai method: {@code Field.get(Object)} và {@code Field.set(Object, Object)}.
 * - VÌ SAO phải truyền instance vào - đây là đoạn giải thích hay nhất của cả bài, nguyên văn:
 * "you need to pass the instance you need to read or write, because the Field object is
 * obtained from the Class, which is unique for a given class, and thus shared for all the
 * instances of this class."
 * + Nói lại cho rõ: một lớp có MỘT object {@code Class}, nên một field có MỘT object
 * {@code Field}, dùng chung cho mọi instance. {@code Field} là MÔ TẢ về field, không phải ô
 * nhớ chứa giá trị. Muốn biết giá trị thì phải chỉ rõ "của object nào".
 * + Hệ quả trực tiếp cho field {@code static}: không thuộc instance nào, nên truyền
 * {@code null}.
 * - Ví dụ: {@code nameField.get(maria)} ra {@code Maria}; {@code nameField.set(maria,
 * "Mary")} đổi giá trị. "Note that this method can throw an IllegalAccessException."
 * <p>
 * 5. FIELD KHÔNG NHÌN THẤY ĐƯỢC
 * - (Dealing With Non-Visible Fields)
 * - Trang gốc chỉ ra một điều dễ bỏ sót trong ví dụ của chính nó: field {@code name} trong
 * ví dụ KHÔNG private, nên code chạy được là nhờ nằm cùng package.
 * - Làm nó private hoặc chuyển code gọi sang package khác thì ra:
 * <pre>
 *   java.lang.IllegalAccessException: class org.devjava.Main cannot access
 *   a member of class org.devjava.User with modifiers "private"
 * </pre>
 * - Kết luận nguyên văn: "This exception tells you that the visibility rules are STILL
 * HOLDING for the Reflection API." Reflection không tự động cho bạn quyền.
 * - Cách mở: {@code nameField.setAccessible(true);} - "telling the API that you want to
 * suppress the checks for access control."
 * - CÂU QUAN TRỌNG NHẤT CỦA CẢ BÀI, nguyên văn: "A common mistake is to think that this call
 * to setAccessible(true) makes a field non-private, which is not the case. It just
 * suppresses the checks for access control."
 * + Hệ quả thực tế: cờ đó đặt trên OBJECT {@code Field} bạn đang cầm, không đặt lên lớp
 * {@code User}. Code khác lấy một object {@code Field} mới cho cùng field đó vẫn bị chặn như
 * thường. Field vẫn private với toàn bộ phần còn lại của chương trình.
 * <p>
 * 6. ACCESSIBLEOBJECT VÀ MODULE SYSTEM
 * - (Making a Field, a Method or a Constructor Accessible)
 * - "The Field, Method, and Constructor classes all extend a common superclass:
 * AccessibleObject. This class defined the setAccessible() method that can suppress the
 * checks for visibility control, AND THE FINAL CONTROL FOR FIELDS."
 * + Chú ý vế sau: {@code setAccessible} tắt HAI thứ - kiểm tra tầm nhìn và kiểm tra
 * {@code final}. Mục 7 khai thác đúng vế này.
 * - Khi nào thất bại, nguyên văn: "This call will fail if the element you want to make
 * accessible belongs to another, named module, and this named module does not declare itself
 * or the package that contains the element you want to access as being opened to reflection."
 * - Mặc định: "By default a named module is CLOSED for reflective access, meaning that other
 * named modules, and the unnamed module cannot access its non-public classes, and the
 * non-public members of its public classes."
 * - Tin tốt cho hầu hết dự án Spring Boot, nguyên văn: "If you are working on an application
 * that does not use the Java Module System, then you can safely use the Reflection API to
 * access all the elements of your application, as everything is part of the unnamed module."
 * + Dịch ra: không có {@code module-info.java} thì code của bạn nằm trong unnamed module,
 * và reflection vào code của chính bạn luôn chạy. Chỉ reflection vào lớp JDK mới bị chặn.
 * <p>
 * 7. FIELD FINAL
 * - (Dealing With Final Fields)
 * - "making a field accessible suppresses the checks for access control. That includes
 * visibility AND FINAL MODIFIERS."
 * - Ví dụ trang gốc: {@code private final String name} vẫn đổi được từ {@code Maria} thành
 * {@code Mary}.
 * - BA NGOẠI LỆ, nguyên văn: "Setting the accessible flag to true on a field object gives
 * you read and write access to this field. There are some exceptions though: it does not
 * give you write access if the field is a STATIC field, and it does not give you write
 * access on the INSTANCE FIELDS OF A RECORD."
 * - CẢNH BÁO TƯƠNG LAI - phần mới nhất của trang, đáng chú ý nhất:
 * + "starting with JDK 26, you will get a warning when you mutate a final field by making it
 * accessible."
 * + Nội dung cảnh báo của JDK 26:
 * <pre>
 *   WARNING: Final field name in class User has been mutated reflectively by class User
 *            in unnamed module
 *   WARNING: Use --enable-final-field-mutation=ALL-UNNAMED to avoid a warning
 *   WARNING: Mutating final fields will be BLOCKED IN A FUTURE RELEASE unless final field
 *            mutation is enabled
 * </pre>
 * + Lý do, nguyên văn: "Because a final field can be updated, you cannot assume that it will
 * not be, making it harder for you to reason about the correctness of your code. It may
 * prevent also the JVM from conducting some optimizations like CONSTANT FOLDING."
 * + Trang gốc dẫn JEP 500: Prepare to Make Final Mean Final - "There is currently an effort
 * to make final mean final (sic)."
 * + Kết luận cho bạn: code ghi đè field {@code final} bằng reflection là code có HẠN SỬ
 * DỤNG. Đang chạy trên JDK 25 thì chưa sao, nhưng đừng xây kiến trúc lên nó.
 * <p>
 * 8. KIỂU NGUYÊN THỦY
 * - (Dealing with Primitive Types)
 * - {@code get} trả {@code Object} và {@code set} nhận {@code Object}, nên kiểu nguyên thủy
 * bị đóng hộp. Phải ép kiểu khi đọc.
 * - Tránh chi phí đóng hộp bằng nhóm method chuyên dụng: {@code Field.getInt(Object)},
 * {@code Field.setInt(Object, int)} - "There are methods for all the primitive types on the
 * Field class, for both getting and setting the value of the field."
 * - BẪY, trang gốc cảnh báo rõ: "using such a method on a field that is not of the right
 * type will throw an exception." Đổi field từ {@code int} sang {@code Integer} rồi vẫn gọi
 * {@code getInt} thì ra:
 * <pre>
 *   java.lang.IllegalArgumentException: Attempt to get java.lang.Integer field
 *   "org.devjava.User.age" with illegal data type conversion to int
 * </pre>
 * + Tức là {@code getInt} KHÔNG tự mở hộp {@code Integer}. Autoboxing là chuyện của trình
 * biên dịch, mà ở đây không có trình biên dịch nào tham gia. Đây là ví dụ sạch nhất cho
 * nguyên tắc chung: reflection không được hưởng bất kỳ tiện nghi nào của compiler.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 9. QUÉT HẾT FIELD CỦA CẢ CÂY KẾ THỪA - ĐOẠN CODE MỌI FRAMEWORK ĐỀU CÓ
 * - Bảng ở mục 1 cho thấy không method nào vừa thấy private vừa leo lớp cha. Nên ai cũng
 * phải tự viết:
 * <pre>
 *   for (Class&lt;?&gt; c = type; c != null &amp;&amp; c != Object.class; c = c.getSuperclass()) {
 *       for (Field f : c.getDeclaredFields()) { ... }
 *   }
 * </pre>
 * - Bốn chi tiết phải nhớ khi viết vòng lặp này:
 * + Dừng ở {@code Object.class} chứ không dừng ở {@code null} - {@code Object} không có
 * field nào đáng quan tâm.
 * + Lọc {@code static} nếu bạn đang xử lý trạng thái của instance.
 * + Lọc {@code synthetic}: inner class có field {@code this$0}, công cụ đo coverage chèn
 * {@code $jacocoData}. Không lọc là hỏng theo cách rất khó truy (bài t4 mục 7).
 * + Field TRÙNG TÊN giữa lớp con và lớp cha là hợp lệ trong Java (field shadowing). Vòng lặp
 * sẽ gặp cả hai - quyết định trước bạn lấy cái nào.
 * - Spring có sẵn {@code ReflectionUtils.doWithFields(clazz, callback)} và
 * {@code ReflectionUtils.findField}. Trong dự án Spring thì dùng chúng, đừng viết lại.
 * <p>
 * 10. HIỆU NĂNG - CHỖ TỐN THẬT SỰ NẰM Ở ĐÂU
 * - {@code getDeclaredFields()} tạo một MẢNG MỚI mỗi lần gọi. Đây là bản sao phòng vệ -
 * {@code Field} là đối tượng có trạng thái (cờ accessible), nên JDK không thể trả về mảng
 * dùng chung. Gọi nó trong vòng lặp là vừa tốn CPU vừa tạo rác.
 * - Mỗi lần gọi {@code getDeclaredField(name)} cũng quét lại toàn bộ danh sách field.
 * - Quy tắc, giống hệt {@code Pattern.compile} ở section Regex: TRA CỨU MỘT LẦN, cất vào
 * {@code static final Field} (hoặc {@code Map<String, Field>}), gọi {@code setAccessible}
 * một lần luôn, rồi {@code get}/{@code set} bao nhiêu lần tùy ý.
 * - Đây chính xác là thứ Jackson và Hibernate làm: lần đầu gặp một lớp thì quét và dựng
 * metadata, các lần sau tra cache. Đó là lý do request đầu tiên sau khi khởi động luôn chậm
 * hơn hẳn.
 * - {@code Field} có an toàn luồng không: đọc/ghi giá trị thì có, nhưng
 * {@code setAccessible} là thay đổi trạng thái của chính object {@code Field}. Cache rồi
 * {@code setAccessible} một lần lúc khởi tạo là an toàn; gọi {@code setAccessible} lặp đi
 * lặp lại từ nhiều luồng trên một {@code Field} dùng chung thì nên tránh.
 * <p>
 * 11. HAI EXCEPTION DỄ NHẦM
 * - {@code IllegalAccessException} (checked, con của {@code ReflectiveOperationException}):
 * bạn chưa gọi {@code setAccessible(true)}, hoặc đang cố GHI vào {@code static final} /
 * field của record. Sửa được bằng code.
 * - {@code InaccessibleObjectException} (UNCHECKED, con của {@code RuntimeException}):
 * {@code setAccessible(true)} bị module từ chối. KHÔNG sửa được bằng code - phải thêm
 * {@code --add-opens} lúc chạy, hoặc bỏ ý định.
 * - Vì cái thứ hai là unchecked nên {@code catch (ReflectiveOperationException e)} KHÔNG bắt
 * được nó. Đây là lỗi hay gặp khi nâng JDK: code cũ bắt đủ exception mà vẫn chết.
 * - Cách phân biệt nhanh khi đọc log: có chữ "cannot access a member ... with modifiers" là
 * cái thứ nhất; có chữ "module ... does not open ..." là cái thứ hai.
 * <p>
 * 12. ĐÂY CHÍNH LÀ CƠ CHẾ CỦA FIELD INJECTION - VÀ LÝ DO NÊN TRÁNH NÓ
 * - {@code @Autowired} trên field private chạy đúng ba bước của mục 5 và 7:
 * {@code getDeclaredField} rồi {@code setAccessible(true)} rồi {@code set(bean, dependency)}.
 * Không có phép màu nào.
 * - Hai hệ quả giải thích vì sao constructor injection được khuyến nghị:
 * + Field injection không cho phép khai báo {@code final}, nên bean có thể bị đổi trạng thái
 * sau khi dựng xong.
 * + Viết unit test không dùng Spring thì không có cách nào tiêm dependency vào, trừ khi
 * chính bạn cũng phải dùng reflection trong test.
 * - Jackson deserialize cũng đi lối này: gọi constructor rỗng rồi {@code set} từng field.
 * Đây là lý do DTO thiếu constructor không tham số là hỏng.
 * - Hibernate dùng field access khi {@code @Id} đặt trên field, và đó là lý do entity phải
 * có constructor {@code protected} không tham số.
 * <p>
 * 13. ĐỐI CHIẾU TYPESCRIPT
 * - Đọc field động: {@code obj[key]} của JS làm đúng việc của {@code Field.get}, nhưng gọn
 * hơn hẳn vì JS không có khái niệm quyền truy cập ở runtime.
 * - Liệt kê field: {@code Object.keys(obj)} gần với {@code getDeclaredFields()} nhưng khác
 * một điểm then chốt - nó liệt kê field CỦA OBJECT CỤ THỂ, nghĩa là field nào chưa gán thì
 * không có. Java liệt kê theo LỚP nên luôn đủ, kể cả field đang {@code null}.
 * - Field {@code #private} của JS: {@code Object.keys} không thấy, {@code obj["#x"]} không
 * đọc được, và KHÔNG có {@code setAccessible}. Đóng gói của JS chặt hơn Java ở điểm này.
 * - {@code final} của Java gần với {@code Object.freeze} hơn là với {@code readonly} của TS:
 * {@code readonly} chỉ compiler biết và biến mất lúc chạy, còn {@code final} có thật trong
 * bytecode - đó là lý do phải có cả một JEP để siết nó lại (mục 7).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 14. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn method tìm field, vẽ lại bảng ba cột ở mục 1 không nhìn tài liệu?
 * - Vì sao {@code getFields()} trên {@code ArrayList} trả về mảng rỗng?
 * - Vì sao {@code elementData} chỉ hiện cờ {@code TRANSIENT}?
 * - {@code getType()} và {@code getGenericType()} khác nhau ra sao? Cái sau có cho biết
 * {@code Box} đang chứa {@code String} không?
 * - Vì sao {@code Field.get} bắt buộc truyền instance? Field {@code static} thì truyền gì?
 * - {@code setAccessible(true)} làm gì và KHÔNG làm gì?
 * - Hai thứ nào bị {@code setAccessible} vô hiệu hóa?
 * - Ba trường hợp {@code setAccessible} không cho bạn quyền GHI?
 * - JDK 26 thay đổi gì với field {@code final}? Hệ quả cho code của bạn?
 * - Field kiểu {@code Integer} gọi {@code getInt()} thì sao, vì sao không tự mở hộp?
 * - Viết vòng lặp quét field cả cây kế thừa và nêu ba thứ phải lọc?
 * - {@code IllegalAccessException} khác {@code InaccessibleObjectException} ở ba điểm nào?
 * - Mô tả ba bước {@code @Autowired} trên field thực hiện?
 */
public class t5_ReadingAndWritingFields {

    void main() throws ReflectiveOperationException {
        User maria = new User("Maria", 30);

        // 1. Quy tắc tầm nhìn VẪN CÒN HIỆU LỰC với Reflection API (mục 5)
        Field nameField = User.class.getDeclaredField("name");
        try {
            nameField.get(maria);
        } catch (IllegalAccessException e) {
            System.out.println("1. get() chưa setAccessible -> " + e.getMessage());
        }

        // 2. setAccessible(true) tắt khâu kiểm tra -> đọc được (mục 5)
        nameField.setAccessible(true);
        System.out.println("2. sau setAccessible(true)    -> " + nameField.get(maria));

        // 3. Nó tắt LUÔN kiểm tra final: name là private FINAL mà vẫn ghi được (mục 7)
        //    Lưu ý: JDK 26 sẽ cảnh báo, tương lai sẽ chặn (JEP 500).
        nameField.set(maria, "Mary");
        System.out.println("3. sau set() trên field final -> " + nameField.get(maria));

        // 4. CÂU QUAN TRỌNG NHẤT CỦA BÀI: setAccessible KHÔNG làm field hết private.
        //    Cờ đó đặt trên OBJECT Field đang cầm, không đặt lên lớp User.
        //    Lấy một object Field MỚI cho cùng field đó -> vẫn bị chặn y như cũ.
        Field nameFieldMoi = User.class.getDeclaredField("name");
        try {
            nameFieldMoi.get(maria);
        } catch (IllegalAccessException e) {
            System.out.println("4. Field MỚI cùng field đó   -> vẫn IllegalAccessException");
        }

        // 5. Field là MÔ TẢ dùng chung cho mọi instance, nên phải chỉ rõ "của object nào".
        //    Field static không thuộc instance nào -> truyền null (mục 4).
        Field countField = User.class.getDeclaredField("count");
        countField.setAccessible(true);
        System.out.println("5. field static, get(null)    -> " + countField.get(null));

        System.out.println();
        // TODO 1 (mục 1): Chạy getDeclaredFields() trên ArrayList kèm accessFlags() và dán
        //   output JDK 25 vào note. So sánh với bảng của trang gốc - có khác gì không?
        //
        // TODO 2 (mục 1): Tạo lớp Base có 4 field (public, protected, package, private) và
        //   lớp Child kế thừa có thêm 2 field. Chạy cả bốn method của bảng mục 1 trên Child.
        //   Lập bảng kết quả và đối chiếu với bảng lý thuyết.
        //
        // TODO 3 (mục 2): Tạo "class Box<T> { private T t; }". In getType().getName() và
        //   getGenericType().getTypeName(). Sau đó tạo new Box<String>() và hỏi lại -
        //   xác nhận vẫn ra T.
        //
        // TODO 4 (mục 2): Khai báo "private Map<String, List<Integer>> data;". Dùng
        //   getGenericType(), ép sang ParameterizedType, rồi in đệ quy toàn bộ cây kiểu.
        //   Đây là thứ Jackson làm để dựng lại kiểu lúc deserialize.
        //
        // TODO 5 (mục 4, 5): Tạo "class User { String name; }" CÙNG package. Đọc và ghi field
        //   name không gọi setAccessible - xác nhận chạy được. Rồi đổi thành private và chạy
        //   lại, dán nguyên stack trace IllegalAccessException.
        //
        // TODO 6 (mục 5): Sau khi gọi setAccessible(true) trên một object Field, lấy một
        //   object Field MỚI cho cùng field đó bằng getDeclaredField và thử get() ngay.
        //   Xác nhận nó vẫn bị chặn. Đây là bằng chứng cho câu "it just suppresses the checks".
        //
        // TODO 7 (mục 4): Tạo field static trong một lớp. Đọc nó bằng Field.get(null) và bằng
        //   Field.get(someInstance). Ghi lại cả hai có chạy không.
        //
        // TODO 8 (mục 7): Lặp lại ví dụ Maria/Mary với private final String. Xác nhận đổi
        //   được trên JDK 25. Ghi lại dự đoán của bạn về hành vi trên JDK 26.
        //
        // TODO 9 (mục 7): Thử set() lên bốn loại field: instance final, static final int,
        //   static final String, và field của một record. Lập bảng bốn kết quả.
        //
        // TODO 10 (mục 7): Tạo "static final int MAX = 10;", đổi nó bằng reflection (nếu
        //   được), rồi in MAX ở một chỗ khác trong code. Giải thích constant folding bằng
        //   kết quả quan sát được.
        //
        // TODO 11 (mục 8): Với field "private int age", dùng getInt/setInt. Rồi đổi kiểu field
        //   sang Integer và chạy lại code y nguyên. Dán IllegalArgumentException và giải thích.
        //
        // TODO 12 (mục 8): Đo chênh lệch giữa get()+ép kiểu và getInt() khi lặp 10 triệu lần.
        //   Kết luận: tiết kiệm boxing có đáng để code xấu hơn không?
        //
        // TODO 13 (mục 9): Viết hàm allFields(Class<?> c) quét cả cây kế thừa, dừng ở Object,
        //   lọc static và synthetic. Test trên một lớp có inner class để thấy this$0 bị lọc.
        //
        // TODO 14 (mục 10): Đo ba cách đọc cùng một field 1 triệu lần: getDeclaredField mỗi
        //   vòng, Field cache ngoài vòng lặp, và gọi getter trực tiếp. Lập bảng ba con số.
        //
        // TODO 15 (mục 12): Viết "mini field injector": với mỗi field có annotation @Inject
        //   của bạn, tạo instance bằng constructor rỗng rồi set vào. Đây là bản nháp cho bài
        //   t13 - giữ lại code.
    }
}

/**
 * Lớp làm đối tượng soi cho bài t5.
 * <p>
 * CỐ Ý để ở mức top-level (không phải lớp lồng): lớp lồng là NESTMATE của lớp bao
 * (JEP 181, Java 11) nên reflection đọc được field private của nó mà không cần
 * {@code setAccessible} - như vậy sẽ không minh họa được mục 5.
 */
class User {

    private final String name;
    private int age;
    private static int count = 0;

    User(String name, int age) {
        this.name = name;
        this.age = age;
        count++;
    }
}
