package com.javacore.language.fp.generics;

/**
 * Type Erasure
 * <p>
 * 0. Vị trí bài này
 * - Bước 13/41, bài 4/5 trong section Generics của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Compiler kiểm tra kiểu xong thì XOÁ sạch type parameter, thay bằng
 * bound hoặc {@code Object} rồi chèn cast; nhờ vậy generics không tốn gì lúc chạy, nhưng
 * cũng vì vậy mà kiểu không còn tồn tại trong JVM.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/generics/type-erasure/">dev.java - Type Erasure</a>
 * - Bài này là NGUYÊN NHÂN, bài t5 Restrictions là HỆ QUẢ.
 * <p>
 * ======================================================================
 * TRANG GỐC - TYPE ERASURE (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA
 * - Compiler làm hai việc khi erasure:
 * + "replace all type parameters in generic types with their bounds or Object if the type
 * parameters are unbounded"
 * + "insert type casts if necessary to preserve type safety"
 * - Mục tiêu: "no new classes are created for parameterized types; consequently, generics
 * incur no runtime overhead."
 * <p>
 * 2. ERASURE CỦA GENERIC TYPE
 * - Không bound thì thay bằng {@code Object}:
 * <pre>{@code
 * class Node<T> {                 // trước erasure
 *     private T data;
 *     public T getData() { return data; }
 * }
 *
 * class Node {                    // sau erasure
 *     private Object data;
 *     public Object getData() { return data; }
 * }
 * }</pre>
 * - Có bound thì thay bằng bound ĐẦU TIÊN:
 * <pre>{@code
 * class Node<T extends Comparable> { private T data; }   // trước
 * class Node { private Comparable data; }                // sau
 * }</pre>
 * - Hệ quả trực quan: {@code Box<Integer>} và {@code Box<String>} dùng chung MỘT file
 * {@code Box.class}. Kiểm chứng bằng {@code a.getClass() == b.getClass()} trả về true.
 * <p>
 * 3. ERASURE CỦA GENERIC METHOD
 * - Cùng quy tắc: {@code <T>} không bound thành {@code Object}, có bound thành bound đó.
 * <pre>{@code
 * static <T> int count(T[] array, T elem)                      // trước
 * static int count(Object[] array, Object elem)                // sau
 *
 * static <T extends Shape> void draw(T shape)                  // trước
 * static void draw(Shape shape)                                // sau
 * }</pre>
 * <p>
 * 4. BRIDGE METHOD
 * - Vấn đề: khi lớp con thu hẹp type parameter, sau erasure hai chữ ký không còn khớp nhau
 * nên override bị gãy.
 * <pre>{@code
 * class Node<T>         { public void setData(T data) { ... } }
 * class MyNode extends Node<Integer> { public void setData(Integer data) { ... } }
 *
 * // sau erasure:
 * // Node.setData(Object)      <- lớp cha
 * // MyNode.setData(Integer)   <- lớp con, KHÔNG override được vì khác tham số
 * }</pre>
 * - Giải pháp của compiler: tự sinh thêm một BRIDGE METHOD (synthetic) trong lớp con:
 * <pre>{@code
 * // compiler sinh ngầm, không có trong mã nguồn
 * public void setData(Object data) {
 *     setData((Integer) data);   // cast rồi uỷ quyền
 * }
 * }</pre>
 * - Nhờ bridge method mà đa hình vẫn hoạt động sau erasure. Và chính dòng cast bên trong nó
 * là nơi {@code ClassCastException} được ném ra khi truyền sai kiểu.
 * - Thực tế: bridge method chỉ hiện ra khi đọc bytecode hoặc khi đọc stack trace lạ.
 * <p>
 * 5. REIFIABLE VÀ NON-REIFIABLE TYPE
 * - "A reifiable type is a type whose type information is fully available at runtime. This
 * includes primitives, non-generic types, raw types, and invocations of unbound wildcards."
 * - Reifiable: {@code int}, {@code String}, {@code List} (raw), {@code List<?>}
 * - Non-reifiable: {@code List<String>}, {@code Map<K, V>}, {@code T}
 * - Lúc chạy, {@code List<String>} và {@code List<Number>} KHÔNG phân biệt được với nhau.
 * - Hệ quả: non-reifiable type không dùng được trong {@code instanceof}, không làm kiểu phần
 * tử của mảng được. Chi tiết nằm ở bài t5.
 * <p>
 * 6. HEAP POLLUTION
 * - Định nghĩa: heap pollution xảy ra "when a variable of a parameterized type refers to an
 * object that is not of that parameterized type".
 * - Nguyên nhân thường gặp: trộn raw type với parameterized type, hoặc unchecked cast.
 * <pre>{@code
 * List<String> strings = new ArrayList<>();
 * List raw = strings;          // raw type
 * raw.add(42);                 // heap pollution - nhét Integer vào List<String>
 * String s = strings.get(0);   // ClassCastException lúc chạy, không phải lúc add
 * }</pre>
 * - Điểm đáng sợ: lỗi KHÔNG nổ tại chỗ gây ra, mà nổ ở chỗ đọc ra - thường cách xa hàng trăm dòng.
 * <p>
 * 7. VARARGS VỚI THAM SỐ NON-REIFIABLE
 * - Compiler biến {@code T... elements} thành {@code T[]}, rồi erasure biến tiếp thành
 * {@code Object[]}. Mảng {@code Object[]} thì nhận được bất cứ thứ gì, nên mở đường cho
 * heap pollution và {@code ClassCastException}.
 * <pre>{@code
 * static <T> void unsafe(List<T>... lists) {   // cảnh báo: possible heap pollution
 *     Object[] array = lists;                  // hợp lệ vì mảng là covariant
 *     array[0] = List.of(42);                  // nhét List<Integer> vào
 * }
 * }</pre>
 * - Cách tắt cảnh báo:
 * + {@code @SafeVarargs} - khẳng định "the implementation of the method will not improperly
 * handle the varargs formal parameter". Đặt được trên static method, final method,
 * private method và constructor.
 * + {@code @SuppressWarnings({"unchecked", "varargs"})} - trang gốc nói cách này
 * "less desirable" vì không tắt được cảnh báo ở PHÍA GỌI, chỉ tắt tại chỗ khai báo.
 * - Nguyên tắc: chỉ gắn {@code @SafeVarargs} khi method thực sự chỉ ĐỌC từ mảng varargs và
 * không để mảng đó lọt ra ngoài.
 * <p>
 * ======================================================================
 * GHI CHÚ THÊM
 * ======================================================================
 * <p>
 * 8. ERASURE XOÁ GÌ VÀ GIỮ LẠI GÌ
 * - XOÁ: type argument của một ĐỐI TƯỢNG. {@code new ArrayList<String>()} lúc chạy chỉ là
 * {@code ArrayList}, không cách nào hỏi lại nó từng là {@code String}.
 * - GIỮ: generic trong KHAI BÁO (field, chữ ký method, superclass, interface) vẫn được ghi
 * vào class file ở thuộc tính Signature, đọc lại được qua reflection
 * ({@code getGenericType()}, {@code getGenericSuperclass()}).
 * - Đây là lý do thủ thuật sau hoạt động được, và là thứ gặp hằng ngày với Jackson/Spring:
 * <pre>{@code
 * objectMapper.readValue(json, new TypeReference<List<User>>() {});
 * restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<User>>() {});
 * }</pre>
 * Cặp {@code {}} tạo một anonymous subclass, và generic superclass của nó được lưu lại
 * trong bytecode nên reflection đọc ra được.
 * <p>
 * 9. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - Giống: cả hai đều xoá kiểu, kiểu chỉ phục vụ compiler.
 * - Khác: TS xoá 100%, không còn dấu vết nào lúc chạy. Java giữ lại Signature của khai báo
 * nên vẫn moi lại được một phần qua reflection.
 * - Khác: TS không có bridge method vì không có override theo chữ ký kiểu như Java.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Erasure thay {@code T} bằng gì khi không bound, khi có bound?
 * - {@code Box<Integer>} và {@code Box<String>} sinh ra mấy file class?
 * - Bridge method là gì, compiler sinh nó ra để giải quyết vấn đề gì?
 * - Reifiable khác non-reifiable ở điểm nào? Kể ba ví dụ mỗi loại.
 * - Heap pollution là gì, vì sao nguy hiểm hơn một lỗi biên dịch thông thường?
 * - Vì sao {@code T... args} lại dễ gây heap pollution?
 * - {@code @SafeVarargs} khác {@code @SuppressWarnings} ở điểm nào?
 * - Erasure xoá generic của đối tượng, nhưng giữ lại generic của cái gì?
 */
public class t4_TypeErasure {

    void main() {
        // TODO 1 (mục 2): Tạo Box<Integer> và Box<String>, in ra
        //   a.getClass() == b.getClass() và a.getClass().getName(). Xác nhận chỉ có một class.
        //
        // TODO 2 (mục 4): Viết Node<T> và MyNode extends Node<Integer>. Biên dịch rồi chạy
        //   javap -p MyNode.class để nhìn thấy bridge method setData(Object) mà mình không hề viết.
        //
        // TODO 3 (mục 6): Tái hiện heap pollution - gán List<String> cho một biến raw, add một
        //   Integer vào, rồi đọc ra biến String. Dán stack trace ClassCastException vào đây và
        //   ghi rõ lỗi nổ ở DÒNG NÀO so với dòng gây ra nó.
        //
        // TODO 4 (mục 7): Viết static <T> void unsafe(List<T>... lists) rồi thử làm hỏng mảng
        //   qua tham chiếu Object[]. Dán cảnh báo possible heap pollution, sau đó thêm
        //   @SafeVarargs và quan sát cảnh báo biến mất.
        //
        // TODO 5 (mục 8): Dùng reflection đọc generic của một field kiểu List<String>:
        //   getDeclaredField(...).getGenericType(). Xác nhận vẫn lấy lại được String, dù
        //   đối tượng lúc chạy không còn biết gì về String.
    }
}
