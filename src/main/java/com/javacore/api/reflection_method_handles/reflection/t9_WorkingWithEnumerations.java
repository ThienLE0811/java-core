package com.javacore.api.reflection_method_handles.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Working with Enumerations
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 9/13 của section The Reflection API.
 * - Giá trị lớn nhất của bài không nằm ở ba method API, mà ở chỗ nó MỔ BỤNG enum: cho bạn
 * thấy trình biên dịch đã sinh ra những gì phía sau từ khóa {@code enum}.
 * - Một câu cho cả bài: enum chỉ là một lớp thường với vài field {@code public static final}
 * và một mảng {@code $VALUES} - trừ một điều, JVM cấm tuyệt đối việc tạo thêm hằng lúc chạy.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/enums/">dev.java - Working with Enumerations</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Output ở mục 6 và 7 đã chạy
 * thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WORKING WITH ENUMERATIONS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. KHÔNG CÓ LỚP REFLECT.ENUM
 * - Câu mở đầu đặt đúng trọng tâm: "Since enums are classes, reflection has no need to
 * define an explicit java.lang.reflect.Enum class, and indeed, there is none."
 * - Chỉ có ba API riêng cho enum. Mọi thứ còn lại dùng {@code Class}, {@code Field},
 * {@code Method}, {@code Constructor} y như lớp thường:
 * + {@code Class.isEnum()} - lớp này có phải enum không.
 * + {@code Class.getEnumConstants()} - "retrieves the list of enum constants defined by the
 * enum IN THE ORDER THEY ARE DECLARED."
 * + {@code Field.isEnumConstant()} - field này có phải một hằng của enum không.
 * - Câu then chốt giải thích tất cả: "enum constants are implemented as PUBLIC STATIC FINAL
 * FIELDS on the enum."
 * - Khi nào cần {@code getEnumConstants()} thay vì {@code values()}, nguyên văn: "If an
 * instance of an enum type is not available the only way to get a list of the possible
 * values is to invoke Class.getEnumConstants() since it is impossible to instantiate an enum
 * type." Nghĩa là: khi bạn chỉ cầm một {@code Class} đến từ dữ liệu, không gõ được
 * {@code Days.values()} trong code.
 * - Ví dụ trang gốc xác nhận hai cái cho kết quả giống hệt nhau:
 * {@code Days.values()} và {@code Days.class.getEnumConstants()} đều ra
 * {@code [SATURDAY, SUNDAY]}.
 * <p>
 * 2. CONSTRUCTOR CỦA ENUM - CÓ NHƯNG KHÔNG GỌI ĐƯỢC
 * - (Locating Enum Constants)
 * - Với {@code enum Days { SATURDAY, SUNDAY }} không khai báo constructor nào,
 * {@code getDeclaredConstructors()} vẫn ra MỘT constructor:
 * {@code private Days(java.lang.String, int)}.
 * - Hai tham số đó do trình biên dịch thêm, trang gốc giải thích rõ:
 * + "a String, which is the name of a constant"
 * + "an int, which is the index of this constant in the array of the constants this enum
 * defines" - chính là {@code ordinal}.
 * - Thử gọi nó để tạo hằng mới, kể cả sau {@code setAccessible(true)}:
 * <pre>
 *   java.lang.IllegalArgumentException: Cannot reflectively create enum objects
 *       at java.base/java.lang.reflect.Constructor.acquireConstructorAccessor
 * </pre>
 * - Kết luận của trang gốc: "it is not possible to add constants to an enum at runtime, even
 * using the Reflection API."
 * - Chú ý chỗ nó chặn: {@code acquireConstructorAccessor}, tức là trước cả khi chạy
 * constructor. JVM chặn cứng, không phải chuyện quyền truy cập. Đây là lý do bài t7 mục 6
 * nói singleton bằng enum là an toàn nhất.
 * <p>
 * 3. ENUM CÓ FIELD VÀ METHOD RIÊNG
 * - (Getting Access to Members)
 * - Ví dụ trang gốc nâng cấp {@code Days} thành enum có field {@code planet}, constructor
 * nhận một tham số, một accessor {@code planet()} và một factory {@code of(String)}.
 * - Kết quả đáng chú ý: enum đó vẫn chỉ có MỘT constructor, nhưng chữ ký thành
 * {@code private Days(java.lang.String, int, java.lang.String)}.
 * + Hai tham số đầu vẫn là {@code name} và {@code ordinal} do trình biên dịch chèn vào
 * TRƯỚC tham số bạn viết. Đã kiểm chứng trên JDK 25, đúng thứ tự đó.
 * + Hệ quả thực tế: muốn tìm constructor của enum bằng {@code getDeclaredConstructor} thì
 * phải nhớ thêm hai tham số đầu - lỗi hay gặp khi viết công cụ quét enum.
 * - "All the other members of your enum can be accessed reflectively, as any member of any
 * regular class."
 * <p>
 * 4. FIELD DO TRÌNH BIÊN DỊCH SINH RA
 * - (Discovering Compiler Generated Fields)
 * - Hai điều trang gốc rút ra, và đây là phần đáng giá nhất của bài:
 * + "The compiler creates one PUBLIC STATIC field for each constant that you define in your
 * enum, with the name of this constant."
 * + "It also creates a PRIVATE STATIC ARRAY CALLED $VALUES, with all the constants you
 * defined in it. This array is returned by the Days.values() call."
 * - Đã chạy lại trên JDK 25 với enum có thêm field {@code planet}, ra đủ bốn field:
 * <pre>
 *   public static final Days SATURDAY     isEnumConstant = true
 *   public static final Days SUNDAY       isEnumConstant = true
 *   private final String planet           isEnumConstant = false
 *   private static final Days[] $VALUES   isEnumConstant = false
 * </pre>
 * - Đây chính là lý do {@code isEnumConstant()} tồn tại: {@code getDeclaredFields()} trộn
 * lẫn hằng enum, field dữ liệu, và field nội bộ. Không lọc thì sai.
 * - Mẹo kỹ thuật trong code mẫu, đáng học: field {@code static} thì đọc bằng
 * {@code field.get(null)} (bài t5 mục 4), và field kiểu mảng thì phải ép sang
 * {@code Object[]} trước khi {@code Arrays.toString}.
 * - Chi tiết quan trọng: {@code values()} trả về một BẢN SAO của {@code $VALUES} mỗi lần
 * gọi, chính vì {@code $VALUES} là mảng và mảng thì sửa được. Nên gọi {@code values()}
 * trong vòng lặp nóng là tạo rác - dùng {@code EnumSet} hoặc cache lại.
 * <p>
 * 5. METHOD DO TRÌNH BIÊN DỊCH SINH RA
 * - (Discovering Compiler Generated Methods)
 * - {@code getDeclaredMethods()} trên enum có {@code of()} và {@code planet()} ra sáu method:
 * <pre>
 *   public static Days[] values()              do compiler sinh
 *   public static Days valueOf(String)         do compiler sinh
 *   public static Days of(String)              bạn viết
 *   private static Days[] $values()            do compiler sinh
 *   private static boolean lambda$of$0(...)    lambda trong of() bị nâng thành method
 *   public String planet()                     bạn viết
 * </pre>
 * - Hai điều đáng rút ra ngoài chuyện enum:
 * + {@code values()} và {@code valueOf(String)} KHÔNG có trong {@code java.lang.Enum} - trình
 * biên dịch sinh riêng cho từng enum. Đó là lý do không gọi được chúng qua kiểu
 * {@code Enum} chung, và là lý do {@code Enum.valueOf(Class, String)} tồn tại.
 * + Mỗi lambda bạn viết trở thành một method {@code private static} tên
 * {@code lambda$ten$so}. Gặp những dòng này khi quét method thì đừng ngạc nhiên - lọc bằng
 * {@code isSynthetic()}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. HẰNG ENUM CÓ THÂN RIÊNG LÀ MỘT LỚP KHÁC - BẪY LỚN NHẤT CỦA BÀI
 * - Trang gốc không nhắc. Khi một hằng có thân riêng (constant-specific class body), trình
 * biên dịch sinh ra một LỚP CON VÔ DANH cho hằng đó. Đã kiểm chứng trên JDK 25 với
 * {@code enum Op { PLUS { ... }, MINUS { ... } }}:
 * <pre>
 *   Op.PLUS.getClass()                     class Probe5$Op$1     (không phải Op)
 *   Op.PLUS.getDeclaringClass()            class Probe5$Op
 *   Op.PLUS.getClass().isEnum()            false
 *   Op.class.isEnum()                      true
 *   Op.PLUS.getClass().getEnumConstants()  null
 * </pre>
 * - Ba hệ quả phải nhớ:
 * + {@code someEnum.getClass() == Op.class} có thể {@code false}. Luôn dùng
 * {@code getDeclaringClass()} khi muốn lớp enum thật.
 * + {@code isEnum()} trả {@code false} cho lớp con vô danh đó. Code kiểm tra "đây có phải
 * enum không" sẽ trả lời sai.
 * + {@code getEnumConstants()} trả {@code null} chứ không phải mảng rỗng - thêm một nguồn
 * {@code NullPointerException}. Nó cũng trả {@code null} với mọi lớp không phải enum,
 * ví dụ {@code String.class.getEnumConstants()}.
 * - Mẫu an toàn: dùng {@code Enum.getDeclaringClass()} nếu cầm instance, và luôn kiểm tra
 * {@code null} sau {@code getEnumConstants()}.
 * <p>
 * 7. BA CÁCH LẤY HẰNG ENUM TỪ MỘT CHUỖI - CHỌN CÁI NÀO
 * - {@code Days.valueOf("SATURDAY")}: gọn nhất, nhưng cần biết kiểu lúc biên dịch. Ném
 * {@code IllegalArgumentException} nếu không khớp - và PHÂN BIỆT HOA THƯỜNG.
 * - {@code Enum.valueOf(Days.class, "SATURDAY")}: dùng khi chỉ có {@code Class} trong tay.
 * Đây là bản reflection-friendly.
 * - Duyệt {@code getEnumConstants()} rồi so khớp theo tiêu chí của bạn: dùng khi khóa không
 * phải tên hằng (ví dụ mã trong DB, nhãn hiển thị). Đây chính là mẫu
 * {@code of(String label)} trong ví dụ của trang gốc.
 * - Lưu ý thực dụng: dữ liệu từ ngoài vào thì KHÔNG dùng {@code valueOf} trần - bọc trong
 * {@code try/catch} hoặc viết một {@code Optional<Days> parse(String)}. Một chuỗi lạ từ
 * client không nên làm vỡ cả request.
 * <p>
 * 8. ENUM TRONG SPRING VÀ JPA - NƠI BÀI NÀY CÓ GIÁ
 * - Spring MVC tự chuyển {@code String} trong query param sang enum bằng đúng cơ chế
 * {@code valueOf} + reflection. Gửi sai chữ hoa thường là 400, và thông báo lỗi mặc định
 * khá khó hiểu - viết {@code Converter} riêng nếu cần chấp nhận nhiều dạng.
 * - JPA: {@code @Enumerated(EnumType.ORDINAL)} là MẶC ĐỊNH và là một quả mìn. Nó lưu
 * {@code ordinal} - đúng cái {@code int} ở mục 2 - nên chỉ cần chèn một hằng vào giữa
 * danh sách là toàn bộ dữ liệu cũ sai nghĩa. LUÔN dùng
 * {@code @Enumerated(EnumType.STRING)}.
 * - Jackson serialize enum theo {@code name()} mặc định; đổi được bằng
 * {@code @JsonValue} trên một accessor - và {@code @JsonValue} được tìm bằng reflection,
 * đúng cơ chế bài t11.
 * - {@code EnumMap} và {@code EnumSet} dùng {@code ordinal} làm chỉ số mảng bên trong, đó là
 * lý do chúng nhanh hơn {@code HashMap}. Đây là chỗ {@code ordinal} dùng ĐÚNG - trong bộ nhớ,
 * không phải trong DB.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - {@code enum} của TS biên dịch thành một object thường với ánh xạ hai chiều số-sang-tên.
 * Nên "reflection" trên nó chỉ là {@code Object.keys(MyEnum)} - và bạn phải tự lọc bỏ các
 * khóa số do ánh xạ ngược sinh ra. Java không có vấn đề này vì hằng là field có kiểu.
 * - TS không có enum có method hay field riêng. Enum của Java là LỚP thật, nên mới có cả bài
 * này. Tương đương gần nhất bên TS là một object hằng với các instance của một class.
 * - Union type của TS ({@code type Day = "SAT" | "SUN"}) biến mất hoàn toàn lúc chạy - không
 * có cách nào liệt kê các giá trị. Đây là lý do thư viện TS hay bắt khai báo trùng lặp một
 * mảng hằng, còn Java chỉ cần {@code getEnumConstants()}.
 * - Điểm chung: cả hai đều có bẫy lưu giá trị số của enum xuống DB. Bài học
 * {@code EnumType.STRING} ở mục 8 áp dụng nguyên si cho TS.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao không có lớp {@code java.lang.reflect.Enum}?
 * - Ba API riêng cho enum là gì?
 * - Khi nào bắt buộc dùng {@code getEnumConstants()} thay vì {@code values()}?
 * - Hằng enum được cài đặt bằng gì ở tầng bytecode?
 * - Constructor của một enum không khai báo gì có chữ ký thế nào, hai tham số đó là gì?
 * - Enum khai báo thêm một tham số thì tham số đó nằm ở vị trí thứ mấy?
 * - Tạo hằng enum mới bằng reflection được không, chặn ở đâu?
 * - {@code $VALUES} là gì, vì sao {@code values()} trả bản sao?
 * - Ba method nào do trình biên dịch sinh ra cho mọi enum?
 * - Hằng enum có thân riêng thì {@code getClass()} trả về gì? Dùng gì thay thế?
 * - {@code getEnumConstants()} trên một lớp không phải enum trả về gì?
 * - Vì sao {@code @Enumerated(EnumType.ORDINAL)} nguy hiểm?
 */
public class t9_WorkingWithEnumerations {

    /** Enum có field và constructor riêng - để thấy tham số của mình nằm ở đâu (mục 3). */
    enum Days {
        SATURDAY("Saturn"),
        SUNDAY("Sun");

        private final String planet;

        Days(String planet) {
            this.planet = planet;
        }

        public String planet() {
            return planet;
        }
    }

    /** Hằng có THÂN RIÊNG - mỗi hằng là một lớp con vô danh (mục 6). */
    enum Op {
        PLUS {
            @Override
            public int apply(int a, int b) {
                return a + b;
            }
        },
        MINUS {
            @Override
            public int apply(int a, int b) {
                return a - b;
            }
        };

        public abstract int apply(int a, int b);
    }

    void main() throws ReflectiveOperationException {
        // 1. Ba API riêng cho enum. getEnumConstants() trả về null (KHÔNG phải mảng rỗng)
        //    với lớp không phải enum - thêm một nguồn NPE (mục 1, mục 6).
        System.out.println("1. Days.class.isEnum()      -> " + Days.class.isEnum());
        System.out.println("   getEnumConstants()       -> " + Arrays.toString(Days.class.getEnumConstants()));
        System.out.println("   String.getEnumConstants  -> " + Arrays.toString(String.class.getEnumConstants()));
        //    values() trả BẢN SAO mới mỗi lần gọi, vì $VALUES là mảng và mảng sửa được (mục 4).
        System.out.println("   values() == values()     -> " + (Days.values() == Days.values()));

        // 2. Trình biên dịch chèn (name, ordinal) vào TRƯỚC tham số bạn viết (mục 2, mục 3).
        Constructor<?> ctor = Days.class.getDeclaredConstructors()[0];
        System.out.println("2. ctor của Days            -> " + ctor);

        // 3. Nhưng không gọi được. JVM chặn ở acquireConstructorAccessor, tức TRƯỚC khi chạy
        //    constructor - không phải chuyện quyền truy cập (mục 2, nối t7 mục 6).
        ctor.setAccessible(true);
        try {
            ctor.newInstance("MONDAY", 2, "Moon");
        } catch (IllegalArgumentException e) {
            System.out.println("3. tạo hằng mới             -> IllegalArgumentException: " + e.getMessage());
        }

        // 4. getDeclaredFields() TRỘN LẪN hằng enum, field dữ liệu và field nội bộ $VALUES.
        //    Không lọc bằng isEnumConstant() là sai (mục 4).
        for (Field f : Days.class.getDeclaredFields()) {
            System.out.printf("4. %-10s isEnumConstant = %s%n", f.getName(), f.isEnumConstant());
        }

        // 5. BẪY LỚN NHẤT CỦA BÀI: hằng có thân riêng là một LỚP CON VÔ DANH, nên
        //    getClass() KHÔNG phải Op.class và isEnum() của nó là false (mục 6).
        System.out.println("5. Op.PLUS.getClass()       -> " + Op.PLUS.getClass());
        System.out.println("   getDeclaringClass()      -> " + Op.PLUS.getDeclaringClass());
        System.out.println("   getClass().isEnum()      -> " + Op.PLUS.getClass().isEnum());
        System.out.println("   Op.class.isEnum()        -> " + Op.class.isEnum());

        System.out.println();
        // TODO 1 (mục 1): Tạo "enum Days { SATURDAY, SUNDAY }". In isEnum(), values(), và
        //   getEnumConstants(). Xác nhận hai mảng sau giống nhau nhưng KHÔNG cùng một object.
        //
        // TODO 2 (mục 2): In getDeclaredConstructors() của Days. Dán chữ ký. Rồi thử
        //   getDeclaredConstructor(String.class, int.class), setAccessible(true),
        //   newInstance("MONDAY", 2) và dán nguyên exception.
        //
        // TODO 3 (mục 3): Thêm field planet và constructor Days(String) vào enum. In lại
        //   chữ ký constructor. Xác nhận tham số của bạn nằm SAU name và ordinal.
        //
        // TODO 4 (mục 4): Chạy đoạn quét getDeclaredFields() của trang gốc, có xử lý riêng
        //   cho field static và field mảng. Dán output. Đánh dấu field nào isEnumConstant().
        //
        // TODO 5 (mục 4): Đọc giá trị $VALUES bằng field.get(null) sau setAccessible(true).
        //   Thử SỬA một phần tử của mảng đó rồi gọi Days.values() - quan sát xem có ảnh
        //   hưởng không, và giải thích bằng chuyện values() trả bản sao.
        //
        // TODO 6 (mục 5): In getDeclaredMethods() của enum có thêm một method dùng lambda.
        //   Tìm $values() và lambda$...$0. Kiểm tra isSynthetic() của từng cái.
        //
        // TODO 7 (mục 6): Tạo "enum Op { PLUS { ... }, MINUS { ... } }" với method abstract.
        //   In getClass(), getDeclaringClass(), isEnum() của Op.PLUS và của Op.class.
        //   Dán output - đây là bài tập QUAN TRỌNG NHẤT của bài.
        //
        // TODO 8 (mục 6): Viết hàm "boolean isEnumType(Object o)" chạy đúng cho CẢ hằng
        //   thường lẫn hằng có thân riêng. Test với Days.SATURDAY và Op.PLUS.
        //
        // TODO 9 (mục 7): Viết "Optional<Days> parse(String)" không ném exception, chấp nhận
        //   cả chữ thường. So sánh ba cách ở mục 7 về số dòng code và về an toàn.
        //
        // TODO 10 (mục 7): Viết hàm tổng quát
        //   "<E extends Enum<E>> Optional<E> parse(Class<E> type, String name)"
        //   dùng getEnumConstants(). Test với hai enum khác nhau.
        //
        // TODO 11 (mục 8): Tạo enum ba hằng, giả lập lưu ordinal xuống "DB" (một List),
        //   rồi CHÈN một hằng mới vào giữa và đọc lại. Quan sát dữ liệu sai nghĩa.
        //   Làm lại bằng name() và xác nhận không sao.
    }
}
