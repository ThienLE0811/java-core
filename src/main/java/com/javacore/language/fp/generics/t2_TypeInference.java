package com.javacore.language.fp.generics;

/**
 * Type Inference
 * <p>
 * 0. Vị trí bài này
 * - Bước 13/41, bài 2/5 trong section Generics của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Compiler tự suy ra type argument dựa trên đối số truyền vào và
 * KIỂU ĐÍCH của ngữ cảnh, nhưng chỉ suy một lần tại chỗ và không bao giờ nhìn xuống
 * các dòng phía sau.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/generics/type-inference/">dev.java - Type Inference</a>
 * <p>
 * ======================================================================
 * TRANG GỐC - TYPE INFERENCE (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA VÀ GIỚI HẠN CỦA THUẬT TOÁN
 * - "Type inference is a Java compiler's ability to look at each method invocation and
 * corresponding declaration to determine the type argument (or arguments) that make the
 * invocation applicable."
 * - Câu quan trọng nhất của cả bài, nói về GIỚI HẠN:
 * "The inference algorithm uses only invocation arguments, target types, and possibly an
 * obvious expected return type to infer types. The inference algorithm does not use
 * results from later in the program."
 * - Nghĩa là compiler chỉ nhìn ba nguồn: đối số truyền vào, kiểu đích, kiểu trả về mong đợi.
 * Nó KHÔNG nhìn xuống dưới xem biến được dùng thế nào ở các dòng sau. Hiểu điều này là
 * giải thích được hầu hết các trường hợp "vì sao compiler không tự suy ra được".
 * <p>
 * 2. SUY LUẬN VỚI GENERIC METHOD - TYPE WITNESS
 * - Gọi generic method như method thường, compiler tự suy từ đối số. Khi cần ép tường minh
 * thì dùng cú pháp type witness, đặt type argument trước tên method:
 * <pre>{@code
 * Collections.<String>emptyList();   // type witness - chỉ định tường minh
 * Collections.emptyList();           // thường dùng - để compiler suy
 * }</pre>
 * - Cú pháp {@code obj.<Type>method()} nhìn lạ nhưng hay gặp trong code cũ.
 * <p>
 * 3. DIAMOND - VÀ BẪY BỎ LUÔN NGOẶC NHỌN
 * <pre>{@code
 * Map<String, List<String>> map = new HashMap<String, List<String>>();  // dài dòng
 * Map<String, List<String>> map = new HashMap<>();                      // diamond
 * }</pre>
 * - "This pair of angle brackets is informally called the diamond."
 * - BẪY: {@code <>} KHÁC với không viết gì. Bỏ cả ngoặc nhọn là biến constructor thành
 * raw type, mất sạch kiểm tra kiểu và sinh cảnh báo unchecked conversion:
 * <pre>{@code
 * Map<String, List<String>> map = new HashMap();   // raw type - unchecked warning
 * }</pre>
 * Thiếu đúng hai ký tự mà hậu quả hoàn toàn khác.
 * <p>
 * 4. GENERIC CONSTRUCTOR
 * - Constructor có thể khai báo type parameter của RIÊNG nó, độc lập với type parameter
 * của class, và có ở cả class generic lẫn class không generic.
 * - Với {@code new MyClass<Integer>("string")}, compiler suy hai thứ từ hai nguồn khác nhau:
 * {@code Integer} cho type parameter của class, {@code String} cho type parameter của
 * constructor (suy từ đối số truyền vào).
 * - Từ Java 7, diamond còn suy được cả type parameter của class chứ không chỉ của constructor.
 * - Thực tế hiếm gặp, biết là đủ.
 * <p>
 * 5. TARGET TYPE - LINH HỒN CỦA BÀI NÀY
 * - Target type là kiểu dữ liệu mà NGỮ CẢNH đang mong đợi. Compiler dùng nó để suy ngược.
 * <pre>{@code
 * // signature: static <T> List<T> emptyList()
 * List<String> list = Collections.emptyList();
 * }</pre>
 * - {@code T} được suy thành {@code String} KHÔNG phải từ đối số (method này không có đối số
 * nào) mà từ chỗ nhận giá trị. Suy luận chảy ngược từ trái sang phải.
 * <p>
 * 6. JAVA 7 KHÁC JAVA 8 - VÌ SAO CODE CŨ TRÔNG KỲ LẠ
 * - Java 7 chỉ coi PHÉP GÁN là target type:
 * <pre>{@code
 * List<String> list = Collections.emptyList();     // Java 7: OK
 * processStringList(Collections.emptyList());      // Java 7: LỖI - suy ra List<Object>
 * processStringList(Collections.<String>emptyList()); // Java 7: phải viết tay như này
 * }</pre>
 * - Java 8 mở rộng target typing sang cả ĐỐI SỐ METHOD, nên dòng thứ hai biên dịch được.
 * - Ý nghĩa thực tế: thấy {@code Collections.<String>emptyList()} trong code cũ thì biết đó là
 * di sản Java 7, đừng bắt chước.
 * <p>
 * 7. LAMBDA VÀ TARGET TYPING
 * - Lambda KHÔNG có kiểu của riêng nó. Ngữ cảnh gán kiểu cho nó.
 * - Các ngữ cảnh hợp lệ trang gốc liệt kê: khai báo biến, phép gán, câu return, khởi tạo mảng,
 * đối số method hoặc constructor, thân lambda, biểu thức điều kiện {@code ?:}, và cast.
 * - Khi có overload, compiler chọn dựa trên target type:
 * <pre>{@code
 * void invoke(Callable<T> c)   // có giá trị trả về
 * void invoke(Runnable r)      // không trả về gì
 *
 * invoke(() -> "done");        // chọn Callable, vì lambda trả về một giá trị
 * }</pre>
 * - Đây là cầu nối sang bài 14 - Lambda Expressions.
 * <p>
 * ======================================================================
 * GHI CHÚ THÊM - ĐỐI CHIẾU VỚI TYPESCRIPT
 * ======================================================================
 * <p>
 * 8. JAVA SUY LUẬN MỘT LẦN, TẠI CHỖ, RỒI CHỐT
 * - TS có phân tích luồng, kiểu có thể "tiến hoá" theo cách dùng ở các dòng sau:
 * <pre>{@code
 * // TypeScript
 * const list = [];       // any[]
 * list.push("hello");    // TS cập nhật lại thành string[]
 * }</pre>
 * - Java chốt kiểu ngay tại dòng khai báo và không bao giờ đổi:
 * <pre>{@code
 * var list = new ArrayList<>();   // ArrayList<Object> - chốt luôn tại đây
 * list.add("hello");              // vẫn là ArrayList<Object>
 * }</pre>
 * - Nếu thông tin tại dòng đó không đủ, compiler chọn {@code Object} hoặc báo lỗi, chứ không
 * đi tìm thêm manh mối ở nơi khác.
 * - Lưu ý: {@code var} là tính năng Java 10, KHÔNG nằm trong trang gốc, nhưng tương tác trực
 * tiếp với type inference nên đáng thử.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Thuật toán suy luận dùng những nguồn thông tin nào, và KHÔNG dùng nguồn nào?
 * - {@code new HashMap<>()} khác {@code new HashMap()} ở điểm gì?
 * - Target type là gì? Vì sao {@code Collections.emptyList()} biết {@code T} là {@code String}?
 * - Cú pháp type witness viết thế nào, khi nào cần đến?
 * - Vì sao code Java 7 hay thấy {@code Collections.<String>emptyList()} còn code nay thì không?
 * - {@code var list = new ArrayList<>();} suy ra kiểu gì, và vì sao không phải thứ ta mong đợi?
 */
public class t2_TypeInference {

    void main() {
        // TODO 1 (mục 3): Khai báo Map<String, List<String>> theo ba cách - đủ type argument,
        //   diamond, và bỏ hẳn ngoặc nhọn. Biên dịch với -Xlint:unchecked và dán cảnh báo
        //   của cách thứ ba vào đây.
        //
        // TODO 2 (mục 5): Gán Collections.emptyList() cho List<String>. Sau đó in ra
        //   getClass() để thấy runtime chẳng biết gì về String - nối sang bài t4 Type Erasure.
        //
        // TODO 3 (mục 6): Viết method processStringList(List<String>) rồi gọi
        //   processStringList(Collections.emptyList()). Trên JDK 25 dòng này biên dịch được -
        //   giải thích vì sao Java 7 lại không.
        //
        // TODO 4 (mục 8): Viết var list = new ArrayList<>(); rồi thử list.add("hello") và
        //   String s = list.get(0). Quan sát lỗi và giải thích kiểu thực sự được suy ra là gì.
    }
}
