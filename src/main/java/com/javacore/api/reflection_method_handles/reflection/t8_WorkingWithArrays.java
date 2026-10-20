package com.javacore.api.reflection_method_handles.reflection;

/**
 * Working with Arrays
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 8/13 của section The Reflection API. Mở đầu nhóm ba bài về các kiểu
 * ĐẶC BIỆT: mảng (t8), enum (t9), record (t10).
 * - Mảng đặc biệt vì nó là kiểu duy nhất trong Java KHÔNG có khai báo lớp ở đâu cả - JVM
 * dựng lớp cho nó lúc chạy. Nên nó cần một lớp tiện ích riêng:
 * {@code java.lang.reflect.Array}.
 * - Một câu cho cả bài: mảng không có method nào của riêng nó, nên mọi thao tác động trên
 * mảng đều đi qua các static method của lớp {@code Array}.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/arrays/">dev.java - Working with Arrays</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WORKING WITH ARRAYS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. MẢNG LÀ THỨ GÌ TRONG JVM
 * - Ba câu mở đầu của trang gốc, mỗi câu một sự thật đáng nhớ:
 * + "Arrays are implemented in the Java virtual machine." Không có file
 * {@code Array.class} nào cho {@code int[]} - JVM sinh lớp đó khi cần.
 * + "The only methods on arrays are those inherited from Object." Mảng không có method
 * riêng. {@code length} KHÔNG phải method, và {@code clone()} là của {@code Object}.
 * + "The length of an array is NOT PART OF ITS TYPE; arrays have a length FIELD which is
 * accessible via Array.getLength(Object)."
 * - Câu thứ ba giải thích vì sao {@code int[5]} và {@code int[10]} cùng một kiểu, và vì sao
 * {@code getLength} phải nhận object chứ không nhận {@code Class}.
 * - Reflection cho bạn bốn nhóm việc với mảng: nhận diện kiểu mảng, lấy kiểu phần tử, tạo
 * mảng mới, và đọc/ghi phần tử.
 * <p>
 * 2. NHẬN DIỆN KIỂU MẢNG
 * - (Identifying Array Types)
 * - Hai method, luôn đi cặp:
 * + {@code Class.isArray()} - có phải mảng không.
 * + {@code Class.getComponentType()} - kiểu của phần tử. Trả {@code null} nếu không phải mảng.
 * - Ví dụ trang gốc rất khéo: soi field {@code value} bên trong {@code String}, ra
 * {@code isArray = true} và {@code elementType = byte}.
 * + Đây là một bài học nằm ngoài chủ đề: từ JDK 9 (JEP 254, compact strings),
 * {@code String} lưu dữ liệu trong {@code byte[]} chứ không còn {@code char[]}.
 * + Nhưng lưu ý: đoạn code này chỉ ĐỌC KIỂU của field, không
 * {@code setAccessible} - nên nó vẫn chạy trên JDK hiện đại. Thử {@code get()} giá trị thật
 * của field đó mới bị module chặn (bài t5 mục 11).
 * - Với mảng nhiều chiều, {@code getComponentType()} bóc MỘT lớp:
 * {@code int[][].class.getComponentType()} ra {@code int[]}, không phải {@code int}. Muốn
 * tới đáy thì lặp.
 * <p>
 * 3. TẠO VÀ THAO TÁC MẢNG ĐỘNG
 * - (Creating New Arrays)
 * - "reflection supports the ability to dynamically create arrays of arbitrary type and
 * dimensions via java.lang.reflect.Array.newInstance()."
 * - "The Array class contains a collection of STATIC methods to get reflective information
 * on arrays, as well as to set and read the elements on any array."
 * - Ví dụ trang gốc, giải thích từng dòng:
 * <pre>
 *   Array.newInstance(int.class, 10)   tạo mảng 10 phần tử int
 *   o.getClass().isArray()             true
 *   o.getClass().getComponentType()    int
 *   Array.getLength(o)                 10
 *   Array.set(o, i, 2*i)               ghi phần tử thứ i
 *   Array.get(o, i)                    đọc phần tử thứ i
 * </pre>
 * + Kết quả in ra: {@code [0, 2, 4, 6, 8, 10, 12, 14, 16, 18]}.
 * + Chú ý kiểu trả về của {@code newInstance} là {@code Object}, không phải {@code int[]} -
 * phải ép kiểu khi muốn dùng như mảng thật: {@code (int[]) o}.
 * - Bản chuyên dụng cho kiểu nguyên thủy: "the Array class has methods to set primitive
 * values WITHOUT BOXING OR UNBOXING. For example, you can use Array.setDouble() and
 * Array.getDouble()". Giống hệt {@code Field.getInt} ở bài t5 mục 8.
 * - HAI QUY TẮC CHUYỂN KIỂU, trang gốc nêu rõ và đây là phần dễ sai nhất:
 * + "Calling a Array.setFloat() method with a Float object instead of a float primitive type
 * will throw an IllegalArgumentException." Không tự mở hộp - lại đúng bài học của t5 mục 8.
 * + "Calling Array.setInt() and passing an array of long is OK, as there is no loss of
 * precision when converting an int to a long. The contrary is illegal. You cannot call
 * Array.setLong(), passing an array of int, as it would lead to a loss of precision."
 * + Tóm lại: reflection CHO PHÉP mở rộng kiểu (widening), CẤM thu hẹp (narrowing). Đây đúng
 * là quy tắc gán của ngôn ngữ Java, chỉ khác là vi phạm thì nổ lúc chạy chứ không lúc biên dịch.
 * - "trying to read or write elements at an index greater than the length of this array will
 * throw an ArrayIndexOutOfBoundsException." Cùng exception với mảng thường.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. MẢNG NHIỀU CHIỀU
 * - {@code Array.newInstance(Class, int...)} có bản nhận nhiều độ dài:
 * {@code Array.newInstance(int.class, 3, 4)} tạo {@code int[3][4]}.
 * - Mảng hai chiều trong Java thật ra là MẢNG CỦA MẢNG, nên
 * {@code Array.get(matrix, 0)} trả về một {@code int[]} chứ không phải một số. Phải
 * {@code Array.get} hai lần để tới phần tử.
 * - Tạo mảng lởm chởm (jagged) thì truyền một chiều rồi tự gán từng hàng.
 * <p>
 * 5. VÌ SAO BẠN SẼ CẦN LỚP ARRAY - BA TÌNH HUỐNG THẬT
 * - Viết bộ chuyển đổi tổng quát (JSON, CSV, map sang object): gặp field kiểu mảng thì bạn
 * không biết kiểu phần tử lúc biên dịch. Chỉ {@code Array.newInstance(componentType, n)} mới
 * tạo được mảng ĐÚNG KIỂU. Tạo {@code Object[]} rồi ép sang {@code String[]} sẽ ném
 * {@code ClassCastException} - vì mảng trong Java giữ kiểu thật lúc chạy.
 * - Gọi method có vararg qua {@code Method.invoke}: phải dựng mảng đúng kiểu tham số (bài t6
 * mục 11), và khi kiểu đó chỉ biết lúc chạy thì phải dùng {@code Array.newInstance}.
 * - Sao chép hoặc nối mảng tổng quát: {@code Arrays.copyOf} cần biết kiểu lúc biên dịch;
 * {@code Array.newInstance(arr.getClass().getComponentType(), n)} thì không.
 * <p>
 * 6. MẢNG LÀ COVARIANT - BẪY ARRAYSTORE
 * - {@code String[]} là kiểu con của {@code Object[]} - Java cho phép, và đó là một quyết
 * định thiết kế có từ trước generic.
 * - Hệ quả: {@code Object[] o = new String[2]; o[0] = 42;} biên dịch được nhưng ném
 * {@code ArrayStoreException} lúc chạy.
 * - Với reflection thì chuyện này gặp thường xuyên hơn, vì {@code Array.newInstance} trả
 * {@code Object} và bạn hay giữ nó trong biến {@code Object[]}. Ghi nhầm kiểu là
 * {@code ArrayStoreException}, không phải {@code IllegalArgumentException} - thêm một
 * exception nữa vào danh sách cần phân biệt.
 * - So với {@code List}: {@code List<String>} KHÔNG phải kiểu con của {@code List<Object>},
 * nên generic không có bẫy này. Đây là một trong những lý do nên ưu tiên {@code List} hơn
 * mảng trong code nghiệp vụ.
 * <p>
 * 7. HIỆU NĂNG
 * - {@code Array.get}/{@code Array.set} chậm hơn truy cập mảng trực tiếp rõ rệt, vì mỗi lời
 * gọi phải kiểm tra kiểu và chỉ số lúc chạy, cộng đóng hộp nếu dùng bản tổng quát.
 * - Mẫu tối ưu thực dụng: dùng reflection để TẠO mảng và để biết kiểu, rồi ÉP KIỂU một lần
 * sang mảng thật và dùng vòng lặp thường. Chỉ giữ {@code Array.set} khi kiểu phần tử thật sự
 * không biết trước.
 * - Dùng bản chuyên kiểu ({@code setInt}, {@code getDouble}) khi phải ở lại trong reflection -
 * tiết kiệm được phần đóng hộp.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - JS chỉ có một kiểu {@code Array} duy nhất, không có kiểu phần tử lúc chạy. Nên toàn bộ
 * bài này - {@code getComponentType}, {@code newInstance} theo kiểu, quy tắc widening -
 * không có tương đương.
 * - {@code Array.getLength(o)} tương ứng {@code arr.length}; {@code Array.get}/{@code set}
 * tương ứng {@code arr[i]}. Bên JS không cần API riêng vì không có kiểu để kiểm tra.
 * - Thứ gần nhất với "mảng có kiểu" của JS là {@code TypedArray} ({@code Int32Array},
 * {@code Float64Array}). Chúng cũng ném lỗi khi ghi sai kiểu, cũng có khái niệm chuyển kiểu
 * - tinh thần giống mục 3.
 * - {@code ArrayStoreException} ở mục 6 không có tương đương bên JS. Nó là cái giá Java trả
 * cho việc cho mảng covariant.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Mảng có những method nào của riêng nó?
 * - {@code length} là field hay method? Lấy nó qua reflection bằng gì?
 * - Vì sao độ dài không nằm trong kiểu của mảng?
 * - {@code int[][].class.getComponentType()} trả về gì?
 * - {@code Array.newInstance} trả về kiểu gì, phải làm gì tiếp?
 * - {@code Array.setFloat} với một object {@code Float} thì sao?
 * - {@code Array.setInt} vào mảng {@code long} được không? Ngược lại thì sao? Vì sao?
 * - Ba tình huống thật buộc bạn dùng {@code Array.newInstance} thay vì {@code new}?
 * - {@code ArrayStoreException} xảy ra khi nào, khác {@code IllegalArgumentException} ra sao?
 * - Vì sao {@code List} không có bẫy covariance như mảng?
 */
public class t8_WorkingWithArrays {

    void main() {
        // TODO 1 (mục 2): Lấy Class của int[], String[], int[][], String[][][]. Với mỗi cái
        //   in isArray(), getComponentType(), getName(), getSimpleName().
        //   Đối chiếu getName() với bảng mã hóa ở bài t3 mục 6.
        //
        // TODO 2 (mục 2): Viết hàm đệ quy baseComponentType(Class<?> c) bóc hết các tầng mảng
        //   để lấy kiểu đáy. Test với int[][][] và String[][].
        //
        // TODO 3 (mục 2): Chạy lại ví dụ soi field "value" của String. Xác nhận elementType
        //   là byte. Rồi thử setAccessible(true) và get() giá trị thật - dán exception và
        //   giải thích khác biệt giữa ĐỌC KIỂU và ĐỌC GIÁ TRỊ.
        //
        // TODO 4 (mục 3): Chạy nguyên ví dụ Array.newInstance(int.class, 10) của trang gốc.
        //   Dán output. Rồi thử ép (String[]) o và dán ClassCastException.
        //
        // TODO 5 (mục 3): Thử Array.setFloat(arr, 0, Float.valueOf(1.0f)) và dán exception.
        //   Rồi thử Array.setInt vào mảng long (hợp lệ) và Array.setLong vào mảng int
        //   (không hợp lệ). Lập bảng ba kết quả.
        //
        // TODO 6 (mục 3): Thử Array.get với chỉ số vượt quá độ dài. Xác nhận exception giống
        //   hệt khi dùng mảng thường.
        //
        // TODO 7 (mục 4): Tạo int[3][4] bằng Array.newInstance(int.class, 3, 4). Dùng
        //   Array.get hai lần để đọc một phần tử. In getComponentType() của từng tầng.
        //
        // TODO 8 (mục 5): Viết hàm tổng quát "Object copyArray(Object src)" dùng
        //   getComponentType + Array.newInstance + Array.get/set, chạy đúng cho mọi kiểu
        //   mảng. Test với int[], String[], và int[][].
        //
        // TODO 9 (mục 5): Viết hàm "Object toArray(List<?> list, Class<?> type)" tạo mảng
        //   đúng kiểu từ một List. So sánh với list.toArray(new String[0]) - vì sao cách
        //   thường không dùng được khi kiểu chỉ biết lúc chạy?
        //
        // TODO 10 (mục 6): Viết "Object[] o = new String[2]; o[0] = 42;" và dán
        //   ArrayStoreException. Rồi thử làm điều tương tự với List<String> - xác nhận
        //   trình biên dịch chặn từ đầu.
        //
        // TODO 11 (mục 7): Đo thời gian ghi 10 triệu phần tử theo ba cách: arr[i] = v trực
        //   tiếp, Array.set, và Array.setInt. Lập bảng ba con số và rút ra quy tắc dùng.
    }
}
