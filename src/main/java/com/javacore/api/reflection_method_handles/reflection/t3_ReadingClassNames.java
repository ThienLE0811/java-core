package com.javacore.api.reflection_method_handles.reflection;

/**
 * Reading Class Names
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 3/13 của section The Reflection API.
 * - Nghe như bài vặt, thật ra không: Java có BỐN kiểu tên cho cùng một lớp, chúng cho kết
 * quả khác nhau, và chỉ MỘT trong bốn cái nạp ngược lại thành {@code Class} được.
 * - Một câu cho cả bài: {@code getSimpleName} và {@code getCanonicalName} là để CON NGƯỜI
 * đọc, còn {@code getName} là để MÁY dùng - chọn sai thì log đẹp nhưng
 * {@code Class.forName} chết.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/names/">dev.java - Reading Class Names</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Mọi output trong note này đã
 * chạy thật trên JDK 25 (xem mục 8), không chép từ tài liệu.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING CLASS NAMES (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. MỞ ĐẦU
 * - "All the elements that the Reflection API gives access to have names: classes, fields,
 * methods, constructors, or method and constructor parameters." Bài này chỉ lo tên LỚP.
 * - "The Reflection API defines several conventions for class names" - chữ SEVERAL là mấu
 * chốt của cả bài.
 * - Hai cái trang gốc nói bạn sẽ dùng nhiều nhất: {@code getCanonicalName()} cho tên đầy đủ
 * và {@code getSimpleName()} cho tên trần không kèm package.
 * - Với lớp có tên tường minh thì "what you get is what you expect":
 * {@code Class.forName("java.util.ArrayList").getCanonicalName()} ra
 * {@code java.util.ArrayList}.
 * - Nhưng với lớp KHÔNG xuất hiện tường minh trong code thì bất ngờ ngay. Ví dụ trang gốc,
 * ba dòng tạo {@code List} theo ba cách:
 * <pre>
 *   new ArrayList&lt;&gt;()                       java.util.ArrayList
 *   Arrays.asList("one","two","three")      java.util.Arrays.ArrayList
 *   List.of("one","two","three")            java.util.ImmutableCollections.ListN
 * </pre>
 * - Bài học: {@code Arrays.asList} KHÔNG trả về {@code java.util.ArrayList} mà trả về một
 * lớp lồng cùng tên bên trong {@code Arrays} - đây chính là lý do nó không hỗ trợ
 * {@code add()}. Tên lớp giải thích được một hành vi mà người mới thường thấy khó hiểu.
 * <p>
 * 2. SIMPLE NAME
 * - "The simple name of a class is the name as it appears in the source code."
 * - {@code String.class.getSimpleName()} ra {@code String}.
 * - Nhưng "Not all classes appear explicitly in the source code. This is the case for
 * anonymous classes and lambda expressions."
 * + Lớp vô danh: "anonymous classes do not have a simple name" - trả về CHUỖI RỖNG, không
 * phải {@code null}. Phân biệt hai cái này quan trọng khi viết log.
 * + Lambda: ra dạng {@code Main$$Lambda/0x0000025851003538}. Trang gốc lưu ý {@code Main} là
 * simple name của lớp chứa lambda, "So it may vary."
 * <p>
 * 3. CANONICAL NAME
 * - "The canonical name of a class is defined in the specification of the language. In a
 * nutshell, it corresponds to its fully qualified name, that is its simple name, prefixed by
 * its package name."
 * - Câu quan trọng nhất của bài: "Not all classes have canonical names. This is the case for
 * LOCAL classes, ANONYMOUS classes, and HIDDEN classes that were added in Java SE 15."
 * - "When a class does not have a canonical name, then the method getCanonicalName() returns
 * null."
 * - Kết quả ví dụ trang gốc: {@code String} ra {@code java.lang.String}; lớp vô danh ra
 * {@code null}; lambda ra {@code null}.
 * - Hệ quả thực tế: {@code getCanonicalName()} có thể trả {@code null}. Dùng nó thẳng trong
 * chuỗi log là một nguồn {@code NullPointerException} khi gặp lambda hoặc lớp vô danh.
 * <p>
 * 4. TYPE NAME
 * - "The type name of a class is an informative string for this class. This method is
 * defined in the Type interface, added in Java SE 8."
 * - Khác canonical name ở chỗ: lớp vô danh và lambda ĐỀU CÓ type name - "which is the name
 * of the class created by the compiler for you".
 * - Kết quả ví dụ trang gốc: {@code java.lang.String}; {@code org.devjava.Main$1};
 * {@code org.devjava.Main$$Lambda/0x00000275a0003748}.
 * - Chữ INFORMATIVE là một lời cảnh báo: đừng parse chuỗi này, đừng dùng nó làm khóa.
 * <p>
 * 5. GETNAME()
 * - "The Class.getName() is yet another way to get the name of a class. It returns the name
 * of the entity (class, interface, array class, primitive type, or void) represented by this
 * Class object."
 * - Quan hệ với {@code getTypeName()}: "For all the classes that are not representing
 * arrays, calling this method is the same as calling Class.getTypeName()."
 * - Nghĩa là bốn method nhưng thực chất chỉ ba hành vi khác nhau, và chỗ duy nhất
 * {@code getName} tách khỏi {@code getTypeName} là MẢNG.
 * <p>
 * 6. TÊN CỦA MẢNG - CÚ PHÁP MÃ HÓA
 * - (Array Name)
 * - Ba method "người đọc" cho kết quả tự nhiên, cả ba đều ra {@code int[]}:
 * {@code getSimpleName()}, {@code getTypeName()}, {@code getCanonicalName()}.
 * - Riêng {@code getName()} theo quy ước riêng: {@code new int[0].getClass().getName()} ra
 * {@code [I}.
 * - Quy ước, "precisely described in the Javadoc of the method Class.getName()":
 * + "The name of an array consists of as many opening square bracket character ([) as there
 * are nested arrays." Hai chiều thì hai dấu ngoặc.
 * + Kiểu nguyên thủy mã hóa theo bảng sau:
 * <pre>
 *   boolean  Z          long     J   (chú ý: J chứ không phải L)
 *   byte     B          char     C
 *   short    S          float    F
 *   int      I          double   D
 * </pre>
 * + Trang gốc nhấn mạnh đúng một dòng: "Note that long is encoded with a J, not an L." Lý do
 * là {@code L} đã dành cho kiểu tham chiếu.
 * + "If the type of the array is a reference type of name N, then the encoding is a L,
 * followed by the name of this type, followed by the character ;".
 * - Hai ví dụ: {@code long[][]} ra {@code [[J}; {@code String[][]} ra
 * {@code [[Ljava.lang.String;}.
 * - Điểm có giá trị nhất của cả bài: "in the case of arrays, you can use the string returned
 * by this method to get the class of this array" - tức là
 * {@code Class.forName("[[Ljava.lang.String;")} CHẠY ĐƯỢC. Chuỗi của {@code getName()} nạp
 * ngược lại được; chuỗi {@code int[]} của ba method kia thì không.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BẢNG BỐN TÊN - ĐÃ CHẠY THẬT TRÊN JDK 25
 * - Trang gốc rải kết quả khắp bài. Gom lại thành một bảng thì mới thấy quy luật:
 * <pre>
 *                      getName()          getTypeName()   getCanonicalName()  getSimpleName()
 *   String             java.lang.String   = getName       java.lang.String    String
 *   static nested      Probe$Inner        = getName       Probe.Inner         Inner
 *   local class        Probe$1Local       = getName       null                Local
 *   anonymous          Probe$1            = getName       null                "" (rỗng)
 *   lambda             Probe$$Lambda/0x.. = getName       null                Probe$$Lambda/0x..
 *   int[]              [I                 int[]           int[]               int[]
 *   long[][]           [[J                long[][]        long[][]            long[][]
 *   String[][]         [[Ljava.lang.String;  ...           ...                 ...
 * </pre>
 * - Ba quy luật rút ra:
 * + {@code getName} và {@code getTypeName} chỉ khác nhau ở MẢNG.
 * + {@code getCanonicalName} là cái DUY NHẤT trả {@code null} - và nó trả null đúng ba ca
 * mà trang gốc liệt kê ở mục 3.
 * + {@code getSimpleName} là cái duy nhất trả CHUỖI RỖNG (cho lớp vô danh).
 * - Chi tiết nhỏ chạy ra khác tài liệu: {@code List.of("a")} với MỘT phần tử ra
 * {@code ImmutableCollections.List12} chứ không phải {@code ListN} - {@code List.of} có lớp
 * riêng tối ưu cho 1 và 2 phần tử. Đúng tinh thần mục 4: đừng bám vào chuỗi này.
 * <p>
 * 8. TÊN NÀO NẠP NGƯỢC THÀNH CLASS ĐƯỢC - ĐÂY LÀ LÝ DO BÀI NÀY QUAN TRỌNG
 * - Chỉ {@code getName()}. Trang gốc nói điều này cho mảng, nhưng nó đúng cho cả lớp lồng -
 * và chỗ lớp lồng mới là chỗ người ta ngã.
 * - Đã kiểm chứng trên JDK 25:
 * <pre>
 *   Class.forName("Probe$Inner")   -> class Probe$Inner        OK
 *   Class.forName("Probe.Inner")   -> ClassNotFoundException   HỎNG
 * </pre>
 * - {@code getCanonicalName()} dùng dấu CHẤM giữa lớp ngoài và lớp trong, còn
 * {@code Class.forName} đòi dấu ĐÔ LA. Đây là bug kinh điển khi tên lớp đi qua file cấu
 * hình: ai đó ghi {@code com.app.Outer.Inner} trong YAML, ứng dụng chạy
 * {@code Class.forName} rồi chết lúc khởi động.
 * - Quy tắc: tên lớp đem đi LƯU TRỮ hoặc TRUYỀN ĐI thì luôn lấy từ {@code getName()}. Ba
 * method kia chỉ dùng để hiển thị.
 * <p>
 * 9. DÙNG TÊN NÀO CHO VIỆC NÀO
 * - Log và thông báo lỗi cho người đọc: {@code getSimpleName()}. Ngắn, đủ hiểu. Nhưng nhớ nó
 * ra chuỗi rỗng với lớp vô danh - log sẽ có một khoảng trắng khó hiểu, nên fallback sang
 * {@code getName()} khi rỗng.
 * - Tài liệu, message cho người dùng cuối: {@code getCanonicalName()}, kèm kiểm tra
 * {@code null}.
 * - Lưu vào DB, ghi vào JSON, truyền qua message queue, tra cứu ngược: {@code getName()}.
 * Không có lựa chọn khác.
 * - Khóa của {@code Map} cache: {@code Class} chính nó, đừng dùng chuỗi tên. Mỗi lớp có đúng
 * một object {@code Class} trong một class loader (bài t2 mục 1) nên
 * {@code Map<Class<?>, X>} vừa nhanh hơn vừa không dính bẫy tên.
 * <p>
 * 10. VÌ SAO PHẢI BIẾT CÚ PHÁP {@code [[Ljava.lang.String;}
 * - Không phải để gõ tay. Bạn sẽ GẶP nó trong ba chỗ và cần đọc được:
 * + Stack trace và thông báo lỗi: {@code ClassCastException: class [Ljava.lang.String;
 * cannot be cast to ...} - đọc được là biết ngay đang nói về mảng String một chiều.
 * + Chữ ký JNI và descriptor trong bytecode - cùng hệ mã hóa.
 * + File cấu hình GraalVM {@code reflect-config.json} khi khai báo mảng.
 * - Cách đọc nhanh: đếm số dấu ngoặc ra số chiều, rồi tra chữ cái cuối. {@code [[J} là mảng
 * hai chiều của {@code long}.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - TS/JS chỉ có một thứ gần giống: {@code obj.constructor.name}, tương đương
 * {@code getSimpleName()}. Không có package nên không cần khái niệm canonical name.
 * - Lớp vô danh trong JS ({@code class {}}) cũng cho {@code name} là chuỗi rỗng - giống hệt
 * Java ở mục 2. Hai ngôn ngữ gặp cùng vấn đề và chọn cùng cách giải.
 * - Không có tương đương cho {@code getName()} nạp ngược được: JS không có cơ chế lấy
 * constructor từ chuỗi tên (trừ khi tự giữ một registry). Đây là lý do thư viện JS hay bắt
 * bạn đăng ký thủ công, còn Java thì {@code Class.forName} là đủ.
 * - Bẫy tên mảng không tồn tại bên JS vì mảng chỉ là {@code Array}, không có kiểu phần tử.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn method lấy tên lớp, cái nào trả về {@code null} và trong ba ca nào?
 * - Cái nào trả về chuỗi rỗng và khi nào?
 * - {@code getName()} và {@code getTypeName()} khác nhau ở trường hợp duy nhất nào?
 * - Tên lớp lưu vào file cấu hình thì lấy bằng method nào, vì sao không dùng canonical?
 * - {@code Arrays.asList(...)} có canonical name là gì? Điều đó giải thích hành vi nào?
 * - Đọc {@code [[J}, {@code [I}, {@code [[Ljava.lang.String;} ra kiểu gì?
 * - Vì sao {@code long} mã hóa thành {@code J}?
 * - Chuỗi nào trong bốn chuỗi tên có thể truyền lại cho {@code Class.forName}?
 */
public class t3_ReadingClassNames {

    void main() {
        // TODO 1 (mục 1): In getCanonicalName() của new ArrayList<>(), Arrays.asList(...),
        //   List.of(...) với 1, 2 và 4 phần tử. Dán output. Giải thích vì sao List.of ra tên
        //   khác nhau theo số phần tử.
        //
        // TODO 2 (mục 1): Dựa vào canonical name của Arrays.asList, giải thích vì sao gọi
        //   add() trên nó ném UnsupportedOperationException. Kiểm chứng bằng code.
        //
        // TODO 3 (mục 2, 3, 4, 5): Viết một method dump(Class<?> c) in cả bốn tên trên một
        //   dòng. Gọi nó cho: String, một static nested class, một local class, một lớp vô
        //   danh, một lambda, int[], long[][], String[][], int.class, void.class.
        //   Dán toàn bộ output và đối chiếu với bảng ở mục 7.
        //
        // TODO 4 (mục 2, 3): Với lớp vô danh, phân biệt rõ trong output đâu là chuỗi rỗng và
        //   đâu là null - in kèm dấu ngoặc vuông quanh giá trị để thấy được.
        //
        // TODO 5 (mục 6): Không chạy code, tự viết ra getName() của: boolean[], char[][],
        //   double[][][], Integer[], Object[][]. Sau đó chạy để kiểm tra.
        //
        // TODO 6 (mục 6, mục 8): Lấy getName() của String[][] rồi truyền lại cho
        //   Class.forName. Xác nhận kết quả == String[][].class.
        //
        // TODO 7 (mục 8): Tạo static nested class Inner. Thử Class.forName với canonical name
        //   (dấu chấm) và với getName() (dấu đô la). Dán cả hai kết quả.
        //   Đây là bài tập QUAN TRỌNG NHẤT của bài.
        //
        // TODO 8 (mục 9): Viết hàm tiện ích displayName(Class<?> c) trả về tên hiển thị tốt
        //   nhất: ưu tiên getSimpleName(), nếu rỗng thì dùng getName(). Test với lớp vô danh
        //   và lambda.
        //
        // TODO 9 (mục 10): Cố ý gây ClassCastException với một mảng, ví dụ ép Object[] chứa
        //   String sang Integer[]. Đọc thông báo lỗi và chỉ ra phần tên mảng mã hóa trong đó.
    }
}
