package com.javacore.language.fp.generics;

/**
 * Introducing Generics
 * <p>
 * 0. Vị trí bài này
 * - Bước 13/41, bài 1/5 trong section Generics của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Generics cho phép truyền KIỂU vào như một tham số khi định nghĩa
 * class, interface và method, nhờ đó compiler bắt lỗi kiểu ngay lúc biên dịch thay vì
 * để chương trình ném ClassCastException lúc chạy.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/generics/intro/">dev.java - Introducing Generics</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRODUCING GENERICS (mục 1 đến 11)
 * ======================================================================
 * <p>
 * 1. VÌ SAO CẦN GENERICS
 * - Định nghĩa: "generics enable types (classes and interfaces) to be parameters when
 * defining classes, interfaces and methods."
 * - Ba lợi ích trang gốc nêu:
 * + Kiểm tra kiểu chặt hơn lúc biên dịch: "A Java compiler applies strong type checking
 * to generic code and issues errors if the code violates type safety."
 * + Loại bỏ ép kiểu (cast): code không generic phải nhận {@code Object} rồi cast tay;
 * code generic thì không cần.
 * + Cho phép viết thuật toán tổng quát: "By using generics, programmers can implement
 * generic algorithms that work on collections of different types, can be customized,
 * and are type safe and easier to read."
 * - Ý cốt lõi: generics DỜI LỖI TỪ RUNTIME VỀ COMPILE TIME. Lỗi lúc biên dịch dễ sửa hơn
 * lỗi lúc chạy rất nhiều.
 * <p>
 * 2. GENERIC TYPE - LỚP BOX
 * - Generic type là một class hoặc interface được tham số hoá theo kiểu.
 * - Phiên bản KHÔNG generic dùng {@code Object} nên không an toàn: "One part of the code may
 * place an Integer in the box and expect to get objects of type Integer out of it, while
 * another part of the code may mistakenly pass in a String, resulting in a runtime error."
 * <pre>{@code
 * // Trước generics - compiler không cản được gì
 * public class Box {
 *     private Object object;
 *     public void set(Object object) { this.object = object; }
 *     public Object get() { return object; }
 * }
 * }</pre>
 * - Phiên bản generic: thêm phần type parameter trong cặp ngoặc nhọn ngay sau tên class,
 * rồi thay mọi chỗ {@code Object} bằng {@code T}.
 * <pre>{@code
 * public class Box<T> {
 *     private T t;
 *     public void set(T t) { this.t = t; }
 *     public T get() { return t; }
 * }
 * }</pre>
 * - Phạm vi của {@code T}: dùng được ở mọi nơi trong thân class.
 * - GIỚI HẠN QUAN TRỌNG: "A type variable can be any non-primitive type you specify: any class
 * type, any interface type, any array type, or even another type variable."
 * Nghĩa là KHÔNG có {@code Box<int>}, phải viết {@code Box<Integer>}. Đây là lý do tồn tại của
 * autoboxing và của các lớp bọc Integer/Double/Boolean.
 * <p>
 * 3. QUY ƯỚC ĐẶT TÊN TYPE PARAMETER
 * - "By convention, type parameter names are single, uppercase letters."
 * - Bảng quy ước chuẩn:
 * + E - Element (Collections Framework dùng khắp nơi: {@code List<E>}, {@code Set<E>})
 * + K - Key ({@code Map<K, V>})
 * + V - Value
 * + N - Number
 * + T - Type (mặc định khi không rơi vào nhóm nào)
 * + S, U, V - kiểu thứ 2, thứ 3, thứ 4
 * - Vì sao một chữ cái in hoa: để phân biệt ngay với tên class thật trong cùng một dòng code.
 * Thấy {@code T} là biết đó là biến kiểu, không phải một class có tên T.
 * <p>
 * 4. TYPE PARAMETER KHÁC TYPE ARGUMENT
 * - "When coding, one provides type arguments in order to create a parameterized type.
 * Therefore, the T in Foo&lt;T&gt; is a type parameter and the String in Foo&lt;String&gt; f
 * is a type argument."
 * - Nhớ theo cặp: type PARAMETER là chỗ trống lúc ĐỊNH NGHĨA, type ARGUMENT là giá trị điền
 * vào lúc SỬ DỤNG. Giống hệt quan hệ parameter/argument của method thông thường, chỉ khác là
 * thứ được truyền vào là một KIỂU chứ không phải một giá trị.
 * - "You can think of a generic type invocation as being similar to an ordinary method
 * invocation, but instead of passing an argument to a method, you are passing a type
 * argument - Integer in this case - to the Box class itself."
 * <p>
 * 5. KHAI BÁO, KHỞI TẠO VÀ TOÁN TỬ DIAMOND
 * - Khai báo chỉ tạo ra một tham chiếu, chưa tạo đối tượng. Muốn có đối tượng phải dùng {@code new}.
 * <pre>{@code
 * Box<Integer> integerBox;                       // mới chỉ là tham chiếu, đang null
 * Box<Integer> integerBox = new Box<Integer>();  // Java 5/6: lặp lại type argument hai lần
 * Box<Integer> integerBox = new Box<>();         // Java 7+: diamond, compiler tự suy ra
 * }</pre>
 * - Diamond: "In Java SE 7 and later, you can replace the type arguments required to invoke the
 * constructor of a generic class with an empty set of type arguments (&lt;&gt;) as long as the
 * compiler can determine, or infer, the type arguments from the context."
 * - Thực tế luôn dùng diamond. Viết đủ hai lần chỉ khiến dòng code dài ra mà không thêm thông tin.
 * <p>
 * 6. NHIỀU TYPE PARAMETER
 * - Một generic type có bao nhiêu type parameter cũng được, ngăn cách bằng dấu phẩy.
 * <pre>{@code
 * public interface Pair<K, V> {
 *     public K getKey();
 *     public V getValue();
 * }
 *
 * public class OrderedPair<K, V> implements Pair<K, V> {
 *     private K key;
 *     private V value;
 *     public OrderedPair(K key, V value) { this.key = key; this.value = value; }
 *     public K getKey()   { return key; }
 *     public V getValue() { return value; }
 * }
 *
 * Pair<String, Integer> p1 = new OrderedPair<>("Even", 8);
 * Pair<String, String>  p2 = new OrderedPair<>("hello", "world");
 * }</pre>
 * - Type argument tự nó cũng có thể là một parameterized type, ví dụ
 * {@code OrderedPair<String, List<String>>} hoàn toàn hợp lệ.
 * - Generic interface tuân theo đúng mọi quy ước của generic class.
 * <p>
 * 7. RAW TYPE
 * - Định nghĩa: raw type là "the name of a generic class or interface without any type arguments".
 * Với {@code Box<T>} thì raw type là {@code Box} viết trần.
 * - Vì sao tồn tại: "Raw types show up in legacy code because lots of API classes (such as the
 * Collections classes) were not generic prior to JDK 5.0." Java phải giữ tương thích ngược với
 * hàng triệu dòng code viết trước khi có generics.
 * <pre>{@code
 * Box<String> stringBox = new Box<>();
 * Box rawBox = stringBox;        // OK, không cảnh báo - gán có kiểu cho raw
 *
 * Box rawBox2 = new Box();
 * Box<Integer> intBox = rawBox2; // CẢNH BÁO unchecked - gán raw cho có kiểu
 * }</pre>
 * - Hậu quả: "Raw types bypass generic type checks, deferring the catch of unsafe code to runtime."
 * Dùng raw type là tự nguyện vứt bỏ toàn bộ lợi ích của generics.
 * - Cảnh báo unchecked: "The term 'unchecked' means that the compiler does not have enough type
 * information to perform all type checks necessary to ensure type safety."
 * + Xem đầy đủ cảnh báo: biên dịch lại với {@code -Xlint:unchecked}
 * + Tắt cảnh báo: {@code -Xlint:-unchecked}, hoặc annotation {@code @SuppressWarnings("unchecked")}
 * + Nguyên tắc nghề: chỉ đặt {@code @SuppressWarnings} ở phạm vi HẸP NHẤT có thể (một biến, một
 * method ngắn) và kèm comment giải thích vì sao chỗ đó thực sự an toàn. Đặt lên cả class là
 * che mất những cảnh báo thật sự nguy hiểm.
 * <p>
 * 8. GENERIC METHOD
 * - Generic method khai báo type parameter của RIÊNG nó, phạm vi chỉ trong method đó.
 * - Cú pháp: phần type parameter đặt trong ngoặc nhọn, ĐỨNG TRƯỚC KIỂU TRẢ VỀ. Đây là chỗ
 * người mới hay viết sai thứ tự nhất.
 * <pre>{@code
 * public class Util {
 *     public static <K, V> boolean compare(Pair<K, V> p1, Pair<K, V> p2) {
 *         return p1.getKey().equals(p2.getKey()) &&
 *                p1.getValue().equals(p2.getValue());
 *     }
 * }
 *
 * Pair<Integer, String> p1 = new OrderedPair<>(1, "apple");
 * Pair<Integer, String> p2 = new OrderedPair<>(2, "pear");
 *
 * boolean same = Util.<Integer, String>compare(p1, p2); // chỉ định tường minh
 * boolean same = Util.compare(p1, p2);                  // thường dùng: để compiler suy ra
 * }</pre>
 * - "Generally, this can be left out and the compiler will infer the type that is needed."
 * Cơ chế suy luận này là nội dung của bài t2 - Type Inference.
 * - "Static and non-static generic methods are allowed, as well as generic class constructors."
 * <p>
 * 9. BOUNDED TYPE PARAMETER
 * - Mục đích: giới hạn những kiểu nào được phép điền vào type parameter.
 * - Cú pháp: "list the type parameter's name, followed by the extends keyword, followed by its
 * upper bound."
 * <pre>{@code
 * public class Box<T extends Number> { ... }   // chỉ nhận Number và lớp con
 * }</pre>
 * - LƯU Ý TỪ KHOÁ: "In this context, extends is used in a general sense to mean either 'extends'
 * (as in classes) or 'implements' (as in interfaces)." Nghĩa là với interface vẫn viết
 * {@code extends}, không viết {@code implements}.
 * - Lợi ích thứ hai, quan trọng hơn việc giới hạn: ràng buộc cho phép GỌI ĐƯỢC METHOD của kiểu
 * chặn trên. Không có bound thì compiler chỉ biết {@code T} là {@code Object} nên chỉ gọi được
 * method của {@code Object}; có {@code T extends Number} thì gọi được {@code intValue()}.
 * - Multiple bounds: {@code <T extends A & B & C>}
 * + "A type variable with multiple bounds is a subtype of all the types listed in the bound."
 * + Nếu trong các bound có một CLASS thì class đó PHẢI đứng đầu tiên, các interface xếp sau.
 * Viết sai thứ tự là lỗi biên dịch.
 * <p>
 * 10. GENERIC METHOD KẾT HỢP BOUNDED TYPE
 * - Bài toán của trang gốc: đếm số phần tử trong mảng lớn hơn một phần tử cho trước.
 * - Vì sao không viết được nếu chỉ có {@code <T>}: toán tử lớn hơn chỉ áp dụng cho kiểu nguyên
 * thuỷ (primitive), không dùng được cho object. "To fix the problem, use a type parameter bounded
 * by the Comparable&lt;T&gt; interface."
 * <pre>{@code
 * public static <T extends Comparable<T>> int countGreaterThan(T[] anArray, T elem) {
 *     int count = 0;
 *     for (T e : anArray)
 *         if (e.compareTo(elem) > 0)
 *             ++count;
 *     return count;
 * }
 * }</pre>
 * - Đọc cú pháp {@code <T extends Comparable<T>>}: "T là kiểu nào đó mà bản thân nó so sánh được
 * với chính nó". Kiểu tự tham chiếu này nhìn rối nhưng rất phổ biến trong JDK.
 * <p>
 * 11. GENERICS, INHERITANCE VÀ SUBTYPE - MỤC DỄ SAI NHẤT CỦA CẢ BÀI
 * - Với kiểu thường, quan hệ "is a" hoạt động như mong đợi: {@code Integer} là {@code Number},
 * nên truyền {@code Integer} vào tham số kiểu {@code Number} được.
 * - Với generic thì KHÔNG: "Box&lt;Integer&gt; is not a subtype of Box&lt;Number&gt; even though
 * Integer is a subtype of Number."
 * - Phát biểu tổng quát: "Given two concrete types A and B, for example, Number and Integer,
 * MyClass&lt;A&gt; has no relationship to MyClass&lt;B&gt;, regardless of whether or not A and B
 * are related. The common parent of MyClass&lt;A&gt; and MyClass&lt;B&gt; is Object."
 * - Tính chất này gọi là INVARIANT (bất biến). Vì sao Java phải cấm:
 * <pre>{@code
 * List<Integer> ints = new ArrayList<>();
 * List<Number> nums = ints;   // GIẢ SỬ cho phép...
 * nums.add(3.14);             // ...thì dòng này hợp lệ, nhét Double vào List<Integer>
 * Integer i = ints.get(0);    // và nổ ClassCastException ở đây
 * }</pre>
 * - Ngược lại, MẢNG trong Java lại COVARIANT, và đó là một lỗ hổng lịch sử:
 * <pre>{@code
 * String[] arr = new String[10];
 * Object[] objArr = arr;      // compile OK
 * objArr[0] = 42;             // nổ ArrayStoreException lúc chạy
 * }</pre>
 * Mảng covariant nhưng kiểm tra lúc runtime; generic invariant nên kiểm tra lúc compile.
 * Nhớ cặp đối lập này là hiểu được vì sao bài t3 phải sinh ra wildcard.
 * - Quan hệ subtype VẪN ĐƯỢC GIỮ nếu không đụng vào type argument:
 * "ArrayList&lt;String&gt; is a subtype of List&lt;String&gt;, which is a subtype of
 * Collection&lt;String&gt;. So long as you do not vary the type argument, the subtyping
 * relationship is preserved between the types."
 * - Có thể mở rộng một generic interface bằng cách thêm type parameter:
 * {@code interface PayloadList<E, P> extends List<E>} - khi đó
 * {@code PayloadList<String, String>}, {@code PayloadList<String, Integer>} và
 * {@code PayloadList<String, Exception>} đều là subtype của {@code List<String>}.
 * <p>
 * ======================================================================
 * GHI CHÚ THÊM - ĐỐI CHIẾU VỚI TYPESCRIPT
 * ======================================================================
 * <p>
 * 12. NỀN TS GIÚP GÌ VÀ HẠI GÌ
 * - Giống: cú pháp ngoặc nhọn gần như y hệt; cả hai đều xoá kiểu lúc runtime; khái niệm ràng
 * buộc {@code <T extends Number>} tương ứng với {@code <T extends number>} của TS.
 * - Khác, và đây là chỗ hay sai:
 * + TS: mảng là COVARIANT một cách lỏng lẻo, {@code string[]} gán được cho {@code object[]}.
 * Java: generic INVARIANT tuyệt đối, chặn ngay lúc biên dịch.
 * + TS suy ra variance tự động theo cấu trúc. Java bắt lập trình viên khai báo tay bằng
 * wildcard {@code ? extends} / {@code ? super} (bài t3).
 * + TS có kiểu nguyên thuỷ nằm chung hệ thống kiểu nên {@code Array<number>} hợp lệ.
 * Java tách riêng primitive nên {@code List<int>} là lỗi, phải dùng {@code List<Integer>}.
 * + Java có RAW TYPE do gánh nặng tương thích ngược; TS không có khái niệm tương đương.
 * + Java kiểm tra kiểu theo TÊN (nominal), TS theo HÌNH DẠNG (structural). Hai class Java có
 * cùng field và method vẫn là hai kiểu khác nhau.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 13. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Trong {@code Foo<T>} và {@code Foo<String> f}, đâu là type parameter, đâu là type argument?
 * - Vì sao {@code Box<int>} không biên dịch được?
 * - Diamond tiết kiệm cho ta điều gì, và có từ phiên bản Java nào?
 * - Raw type là gì, vì sao Java vẫn phải cho phép nó tồn tại?
 * - Trong khai báo generic method, phần {@code <T>} đặt ở đâu so với kiểu trả về?
 * - {@code <T extends Comparable<T>>} đọc thành câu tiếng Việt là gì?
 * - Vì sao {@code List<Integer>} không phải subtype của {@code List<Number>}, trong khi
 * {@code Integer[]} lại là subtype của {@code Number[]}?
 * - {@code ArrayList<String>} có phải subtype của {@code List<String>} không? Vì sao câu trả lời
 * lại khác câu hỏi ngay trên?
 */
public class t1_IntroducingGenerics {

    static class Box<T> {
        // T stands for "Type"
        private T t;

        public void set(T t) {
            this.t = t;
        }

        public T get() {
            return t;
        }
    }

    void main() {
        // TODO 1 (mục 2): Viết class lồng Box<T> với set/get. Tạo Box<Integer> và Box<String>,
        //   thử gọi set() với kiểu sai để xem compiler chặn ngay tại dòng nào.
        Box<Integer> integerBox = new Box<Integer>();

        // TODO 2 (mục 2): Thử khai báo Box<int>. Đọc kỹ thông báo lỗi của compiler và giải thích
        //   vì sao type variable không nhận kiểu nguyên thuỷ.
        //
        // TODO 3 (mục 6): Viết interface Pair<K, V> và class OrderedPair<K, V> implements Pair.
        //   Tạo một OrderedPair<String, List<String>> để thấy type argument lồng nhau.
        //
        // TODO 4 (mục 7): Gán OrderedPair có kiểu cho một biến raw type, rồi gán ngược lại.
        //   Biên dịch với -Xlint:unchecked và dán cảnh báo nhận được vào đây.
        //
        // TODO 5 (mục 8): Viết generic method static <K, V> boolean compare(Pair, Pair).
        //   Gọi nó hai cách: tường minh Util.<Integer, String>compare(...) và để compiler suy ra.
        //
        // TODO 6 (mục 9): Viết Box<T extends Number> và gọi intValue() trên T. Sau đó bỏ bound
        //   đi, quan sát method nào còn gọi được - đó chính là tập method của Object.
        //
        // TODO 7 (mục 10): Cài countGreaterThan với <T extends Comparable<T>>, chạy thử trên
        //   Integer[] và String[].
        //
        // TODO 8 (mục 11): Thử gán List<Integer> cho List<Number> (lỗi compile), rồi thử gán
        //   String[] cho Object[] và ghi 42 vào (lỗi runtime ArrayStoreException).
        //   Dán cả hai thông báo lỗi vào đây để so sánh THỜI ĐIỂM phát hiện lỗi.
    }
}


