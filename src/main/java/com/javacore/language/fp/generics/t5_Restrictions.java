package com.javacore.language.fp.generics;

/**
 * Restrictions on Generics
 * <p>
 * 0. Vị trí bài này
 * - Bước 13/41, bài 5/5 trong section Generics của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Bảy điều generics KHÔNG làm được, và gần như tất cả đều bắt nguồn
 * từ cùng một nguyên nhân là type erasure ở bài t4.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/generics/restrictions/">dev.java - Restrictions on Generics</a>
 * - Bài t4 là NGUYÊN NHÂN, bài này là HỆ QUẢ. Đọc kèm nhau sẽ không phải học thuộc.
 * <p>
 * ======================================================================
 * TRANG GỐC - BẢY HẠN CHẾ
 * ======================================================================
 * <p>
 * 1. KHÔNG DÙNG KIỂU NGUYÊN THUỶ LÀM TYPE ARGUMENT
 * <pre>{@code
 * Pair<int, char> p = new Pair<>(8, 'a');          // LỖI
 * Pair<Integer, Character> p = new Pair<>(8, 'a'); // OK
 * }</pre>
 * - Dòng hợp lệ chạy được nhờ autoboxing: "The Java compiler autoboxes 8 to Integer.valueOf(8)
 * and 'a' to Character('a')."
 * - Nguyên nhân sâu xa: sau erasure {@code T} thành {@code Object}, mà primitive không phải
 * {@code Object}.
 * <p>
 * 2. KHÔNG TẠO ĐỐI TƯỢNG CỦA TYPE PARAMETER
 * <pre>{@code
 * public static <E> void append(List<E> list) {
 *     E elem = new E();   // LỖI - compile-time error
 *     list.add(elem);
 * }
 * }</pre>
 * - Nguyên nhân: lúc chạy {@code E} đã bị xoá, JVM không biết phải gọi constructor nào.
 * - Cách đi vòng: "you can create an object of a type parameter through reflection" - truyền
 * {@code Class<E>} vào rồi gọi {@code cls.getDeclaredConstructor().newInstance()}.
 * <pre>{@code
 * public static <E> void append(List<E> list, Class<E> cls) throws Exception {
 *     E elem = cls.getDeclaredConstructor().newInstance();
 *     list.add(elem);
 * }
 * }</pre>
 * - Thực tế hiện đại hay dùng {@code Supplier<E>} thay cho {@code Class<E>}, gọn và an toàn hơn.
 * <p>
 * 3. KHÔNG KHAI BÁO STATIC FIELD KIỂU TYPE PARAMETER
 * <pre>{@code
 * public class MobileDevice<T> {
 *     private static T os;   // LỖI
 * }
 * }</pre>
 * - Lý do: "A class's static field is a class-level variable shared by all non-static objects
 * of the class. Hence, static fields of type parameters are not allowed."
 * - Diễn giải: {@code MobileDevice<Smartphone>}, {@code MobileDevice<Pager>},
 * {@code MobileDevice<TabletPC>} dùng chung MỘT biến static - không thể quyết định nó mang
 * kiểu nào.
 * - Lưu ý: static METHOD vẫn khai báo được type parameter riêng ({@code static <T> void f(T t)}),
 * chỉ static FIELD là bị cấm.
 * <p>
 * 4. KHÔNG CAST HAY instanceof VỚI PARAMETERIZED TYPE
 * - "Because the Java compiler erases all type parameters in generic code, you cannot verify
 * which parameterized type for a generic type is being used at runtime."
 * <pre>{@code
 * if (list instanceof ArrayList<Integer>) { ... }   // LỖI
 * if (list instanceof ArrayList<?>)       { ... }   // OK - unbounded wildcard
 * }</pre>
 * - Cast sang parameterized type cũng bị cấm, trừ khi tham số hoá bằng unbounded wildcard.
 * - Trường hợp cast được chấp nhận nhưng chỉ sinh cảnh báo unchecked là khi compiler biết
 * chắc phép cast an toàn, ví dụ cast từ {@code List<String>} sang {@code ArrayList<String>}.
 * <p>
 * 5. KHÔNG TẠO MẢNG CỦA PARAMETERIZED TYPE
 * <pre>{@code
 * List<Integer>[] arrayOfLists = new List<Integer>[2];   // LỖI
 * List<?>[] arrayOfLists = new List<?>[2];               // OK
 * }</pre>
 * - Lý do trang gốc: "If arrays of parameterized lists were allowed, the previous code would
 * fail to throw the desired ArrayStoreException."
 * - Mảng là COVARIANT và kiểm tra kiểu lúc runtime; generic đã bị xoá nên runtime không còn
 * gì để kiểm tra. Hai cơ chế xung khắc nhau, nên Java cấm kết hợp.
 * - Nối với bài t1: {@code Object[] objArr = new String[10]; objArr[0] = 42;} ném
 * {@code ArrayStoreException}. Với mảng generic thì cơ chế bảo vệ đó mất tác dụng.
 * <p>
 * 6. KHÔNG TẠO, BẮT HAY NÉM ĐỐI TƯỢNG CỦA PARAMETERIZED TYPE
 * - "A generic class cannot extend the Throwable class directly or indirectly."
 * <pre>{@code
 * class MathException<T> extends Exception { ... }   // LỖI
 * class QueueFullException<T> extends Throwable { }  // LỖI
 * }</pre>
 * - Không bắt được type parameter:
 * <pre>{@code
 * try { ... }
 * catch (T e) { ... }   // LỖI
 * }</pre>
 * - Nhưng type parameter ĐƯỢC PHÉP xuất hiện trong mệnh đề {@code throws}:
 * <pre>{@code
 * class Parser<T extends Exception> {
 *     public void parse(File file) throws T { ... }   // OK
 * }
 * }</pre>
 * - Nguyên nhân: cơ chế catch của JVM so khớp kiểu lúc runtime, mà generic thì đã bị xoá.
 * <p>
 * 7. KHÔNG OVERLOAD KHI HAI CHỮ KÝ TRÙNG NHAU SAU ERASURE
 * <pre>{@code
 * public class Example {
 *     public void print(Set<String> strSet) { }
 *     public void print(Set<Integer> intSet) { }   // LỖI
 * }
 * // sau erasure cả hai đều thành print(Set)
 * }</pre>
 * - "A class cannot have two overloaded methods that will have the same signature after type
 * erasure. The overloads would all share the same classfile representation and will generate
 * a compile-time error."
 * - Cách gỡ: đặt tên khác nhau ({@code printStrings}, {@code printIntegers}).
 * <p>
 * ======================================================================
 * GHI CHÚ THÊM
 * ======================================================================
 * <p>
 * 8. BẢNG TÓM TẮT - GẦN NHƯ TẤT CẢ ĐỀU DO ERASURE
 * <pre>
 * Hạn chế                          Nguyên nhân gốc
 * ------------------------------   --------------------------------------------
 * 1. primitive làm type argument   T thành Object, primitive không phải Object
 * 2. new T()                       runtime không biết constructor nào
 * 3. static field kiểu T           một biến chung, không quyết được kiểu nào
 * 4. cast / instanceof             runtime không còn thông tin kiểu để so khớp
 * 5. mảng generic                  mảng kiểm tra runtime, generic đã bị xoá
 * 6. throw / catch                 catch so khớp kiểu lúc runtime
 * 7. overload trùng sau erasure    hai method thành một chữ ký trong class file
 * </pre>
 * - Chỉ cần nhớ "lúc chạy kiểu không còn" là suy ra được cả bảy, không cần học thuộc.
 * <p>
 * 9. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - TS cũng xoá kiểu nên cũng không có {@code new T()}, không {@code instanceof} theo generic.
 * - Khác: TS không có khái niệm mảng generic bị cấm, không có vấn đề overload trùng chữ ký
 * (TS overload chỉ là khai báo, một implementation duy nhất), và không có ràng buộc
 * {@code Throwable}.
 * - Giống: TS cũng cấm static member tham chiếu type parameter của class, cùng lý do ngữ nghĩa
 * class-level ("Static members cannot reference class type parameters").
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Kể đủ bảy hạn chế, và nêu nguyên nhân chung của chúng.
 * - Vì sao {@code new T()} không được, và hai cách đi vòng là gì?
 * - Static FIELD kiểu T bị cấm, nhưng static METHOD có type parameter thì sao?
 * - {@code instanceof ArrayList<Integer>} sai, vậy viết thế nào mới đúng?
 * - Vì sao mảng và generic xung khắc nhau?
 * - Type parameter được phép xuất hiện ở đâu liên quan tới exception, và không được ở đâu?
 * - Hai method {@code print(Set<String>)} và {@code print(Set<Integer>)} hỏng vì lý do gì?
 */
public class t5_Restrictions {

    void main() {
        // TODO 1 (mục 1): Thử Pair<int, char>. Dán lỗi vào đây, rồi sửa bằng kiểu bọc.
        //
        // TODO 2 (mục 2): Viết <E> void append(List<E> list) có new E() để thấy lỗi.
        //   Sau đó cài lại hai cách đi vòng: nhận Class<E> và nhận Supplier<E>. So sánh cách nào
        //   dễ dùng hơn ở phía gọi.
        //
        // TODO 3 (mục 3): Viết class MobileDevice<T> có private static T os. Dán lỗi vào đây.
        //   Rồi thêm một static <U> void f(U u) để xác nhận static method thì không sao.
        //
        // TODO 4 (mục 4): Thử instanceof ArrayList<Integer> rồi đổi sang ArrayList<?>.
        //   Ghi lại khác biệt trong thông báo của compiler.
        //
        // TODO 5 (mục 5): Thử new List<Integer>[2], rồi đổi sang new List<?>[2].
        //   Riêng với mảng thường, chạy Object[] o = new String[1]; o[0] = 42; để thấy
        //   ArrayStoreException - cơ chế mà mảng generic sẽ làm mất.
        //
        // TODO 6 (mục 6): Thử class MyException<T> extends Exception. Dán lỗi vào đây.
        //   Sau đó viết class Parser<T extends Exception> có method throws T để thấy chỗ nào
        //   vẫn hợp lệ.
        //
        // TODO 7 (mục 7): Viết hai method print(Set<String>) và print(Set<Integer>) trong cùng
        //   một class. Dán lỗi "have the same erasure" vào đây.
    }
}
