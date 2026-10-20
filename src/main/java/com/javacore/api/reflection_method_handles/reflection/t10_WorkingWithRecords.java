package com.javacore.api.reflection_method_handles.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

/**
 * Working with Records
 * <p>
 * 0. Vị trí bài này
 * - Bước 27/41, bài 10/13 của section The Reflection API. Khép lại nhóm ba kiểu đặc biệt
 * (mảng t8, enum t9, record t10).
 * - Record là kiểu DUY NHẤT trong Java mà deep reflection KHÔNG ghi được. Đây là điều đáng
 * nhớ nhất của bài, và nó đã được nhắc từ bước 26 mục 4a.
 * - Một câu cho cả bài: record mang theo metadata đầy đủ về các thành phần của nó
 * ({@code RecordComponent}), nên nó là kiểu dữ liệu thân thiện nhất với mọi thư viện
 * ánh xạ - đồng thời là kiểu bất biến thật sự, không có cửa sau.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/reflection-method-handles/reflection/records/">dev.java - Working with Records</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Output ở mục 5 và 6 đã chạy
 * thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WORKING WITH RECORDS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. HAI ĐẶC ĐIỂM CỦA RECORD ẢNH HƯỞNG TỚI REFLECTION
 * - "Records are a particular type of class, that strongly enforce NON-MODIFIABILITY.
 * Records can have any number of RECORD COMPONENTS. Both elements have an impact on the
 * Reflection API."
 * - Hai gạch đầu dòng đó chia luôn cả bài: phần "record component" là API MỚI (mục 3), phần
 * "non-modifiability" là HẠN CHẾ mới (mục 4).
 * <p>
 * 2. NHẬN DIỆN RECORD
 * - (Identifying Record Type)
 * - "When Records were introduced in Java SE 16, several new methods and classes were added
 * to the Reflection API. Among them is a method on the class Class to check if a given class
 * is a record class: Class.isRecord()."
 * - Kết quả hiển nhiên: {@code String.class.isRecord()} là {@code false},
 * {@code Point.class.isRecord()} là {@code true}.
 * <p>
 * 3. RECORDCOMPONENT - LỚP MỚI CỦA REFLECTION API
 * - (Getting Components Information)
 * - "A record component is one of the elements declared along with the declaration of a
 * record." Tức là phần trong ngoặc đơn của {@code record Point(int x, int y)}.
 * - Lấy bằng {@code Class.getRecordComponents()}, trả mảng {@code RecordComponent[]}.
 * - Sáu method của {@code RecordComponent}, bảng của trang gốc:
 * <pre>
 *   getDeclaringRecord()    lớp record khai báo thành phần này
 *   getAccessor()           Method ứng với accessor của thành phần
 *   getName()               tên thành phần
 *   getType()               kiểu, dạng Class
 *   getGenericType()        kiểu generic, dạng Type
 *   getGenericSignature()   chữ ký generic, dạng String
 * </pre>
 * - Kết quả ví dụ trang gốc cho thành phần {@code x} của {@code Point}:
 * <pre>
 *   Declaring record: class Point
 *   name = x
 *   accessor = public int Point.x()
 *   type = int
 *   genericType = int
 *   genericSignature = null
 * </pre>
 * - {@code getAccessor()} là method đáng giá nhất của bảng: nó cho thẳng object
 * {@code Method} để {@code invoke}, khỏi phải đoán tên accessor. Với record thì accessor
 * trùng tên thành phần ({@code x()} chứ không phải {@code getX()}) - đây là chỗ các thư
 * viện viết theo chuẩn JavaBean cũ hay vấp.
 * - Vì sao {@code genericSignature} là {@code null}: chữ ký generic chỉ tồn tại khi có biến
 * kiểu. Đã kiểm chứng với {@code record Pair<A, B>(A first, B second)} trên JDK 25:
 * <pre>
 *   first   type = class java.lang.Object   generic = A   signature = TA;
 * </pre>
 * + {@code TA;} là cú pháp chữ ký của JVM cho biến kiểu {@code A} - cùng họ với cú pháp mã
 * hóa tên mảng ở bài t3 mục 6.
 * + Và chú ý {@code getType()} ra {@code Object} - type erasure vẫn áp dụng y như bài t5.
 * <p>
 * 4. ĐỌC ĐƯỢC NHƯNG KHÔNG GHI ĐƯỢC
 * - (Accessing Record Fields)
 * - Một thiếu sót của API được trang gốc nêu thẳng: "there is NO METHOD on the
 * RecordComponent class to get a reference on the Field object that models the field of this
 * component."
 * + Muốn {@code Field} thì phải quay lại {@code Class.getDeclaredFields()} và tự khớp theo
 * tên.
 * - Field của record là {@code private final}: {@code getDeclaredFields()} trên
 * {@code Point} ra {@code private final int Point.x} và {@code private final int Point.y}.
 * - ĐỌC: cần {@code setAccessible(true)} vì field private, sau đó {@code get(p)} hoặc
 * {@code getInt(p)} - "to avoid auto-unboxing". Chạy ra {@code x = 1}.
 * - GHI: KHÔNG ĐƯỢC, nguyên văn: "you cannot modify the fields of a record, EVEN BY CALLING
 * Field.setAccessible(true). This behavior is specific to records."
 * - Exception nhận được, đã kiểm chứng nguyên văn trên JDK 25:
 * <pre>
 *   java.lang.IllegalAccessException: Can not set final int field Point.x to (int)0
 * </pre>
 * - Đây là ngoại lệ thứ hai trong ba ngoại lệ của {@code setAccessible} mà bài t5 mục 7 đã
 * liệt kê (cái còn lại là field {@code static}).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. HAI GIÁ TRỊ NULL PHẢI PHÒNG
 * - Đã kiểm chứng trên JDK 25: {@code String.class.getRecordComponents()} trả {@code null},
 * KHÔNG phải mảng rỗng. Giống hệt {@code getEnumConstants()} ở bài t9 mục 6.
 * - Nên mẫu đúng luôn là kiểm tra {@code isRecord()} TRƯỚC, hoặc kiểm tra {@code null} sau.
 * Viết thẳng {@code for (var rc : c.getRecordComponents())} trên một lớp bất kỳ là một
 * {@code NullPointerException} đang chờ.
 * - Quy luật chung của Reflection API, đáng ghi nhớ cho cả section: method hỏi về một ĐẶC
 * TÍNH mà lớp không có thì trả {@code null} chứ không trả rỗng -
 * {@code getEnumConstants}, {@code getRecordComponents}, {@code getComponentType},
 * {@code getSuperclass} trên {@code Object}, {@code getDeclaringClass} trên lớp vô danh.
 * <p>
 * 6. THỨ TỰ CÁC THÀNH PHẦN ĐƯỢC ĐẢM BẢO - KHÁC HẲN GETDECLAREDFIELDS
 * - {@code getRecordComponents()} trả về đúng thứ tự khai báo. Đây là bảo đảm của đặc tả,
 * vì thứ tự đó cũng là thứ tự tham số của canonical constructor.
 * - Ngược lại, {@code getDeclaredFields()} và {@code getDeclaredMethods()} KHÔNG đảm bảo thứ
 * tự - bài t1 bước 26 mục 3c đã cảnh báo ("The order of these methods is not guaranteed").
 * - Hệ quả thực dụng rất mạnh: muốn dựng lại một record từ dữ liệu, cứ lấy
 * {@code getRecordComponents()}, map sang mảng kiểu, lấy canonical constructor và gọi:
 * <pre>
 *   RecordComponent[] rcs = c.getRecordComponents();
 *   Class&lt;?&gt;[] types = Arrays.stream(rcs).map(RecordComponent::getType).toArray(Class[]::new);
 *   Constructor&lt;?&gt; canonical = c.getDeclaredConstructor(types);
 * </pre>
 * - Đây chính xác là cách Jackson, Gson đời mới và mọi thư viện ánh xạ dựng record.
 * - Nhắc lại bẫy của bài t7 mục 5b: canonical constructor có CÙNG mức truy cập với record,
 * nên record không public thì phải dùng {@code getDeclaredConstructor}, không phải
 * {@code getConstructor}.
 * <p>
 * 7. VÌ SAO RECORD KHÔNG GHI ĐƯỢC - VÀ VÌ SAO ĐÓ LÀ TIN TỐT
 * - JVM cưỡng chế ở tầng dưới, không phải chuyện module hay quyền truy cập. Record là hợp
 * đồng bất biến THẬT, không có cửa sau nào.
 * - Ba hệ quả tích cực:
 * + Dùng record làm khóa của {@code Map} hoặc phần tử của {@code Set} là an toàn tuyệt đối -
 * không thư viện nào, không đoạn code nào đổi được {@code hashCode} sau lưng bạn.
 * + Dùng record làm DTO trả về từ API thì không ai sửa được dữ liệu sau khi đã dựng.
 * + Java serialization của record đi qua CANONICAL CONSTRUCTOR, nên mọi kiểm tra trong
 * compact constructor vẫn chạy. Đây là khác biệt lớn với lớp thường - bài t7 mục 8 nói lớp
 * thường bị deserialize mà bỏ qua constructor.
 * - Một hệ quả tiêu cực cần biết: JPA KHÔNG dùng record làm entity được. Entity cần
 * constructor không tham số và cần ghi field sau khi dựng (bài t7 mục 7) - record cấm cả
 * hai. Record dùng rất tốt cho DTO và cho kết quả projection, không dùng cho entity.
 * <p>
 * 8. RECORD TRONG CÁC THƯ VIỆN BẠN DÙNG
 * - Jackson hỗ trợ record từ bản 2.12 và không cần {@code @JsonCreator}: nó đọc
 * {@code getRecordComponents()} rồi gọi canonical constructor. Đây là lý do record
 * deserialize được dù không có setter và không có constructor rỗng.
 * - Spring: dùng record cho {@code @ConfigurationProperties} là mẫu được khuyến nghị, và cho
 * response body của controller thì hoàn toàn tự nhiên.
 * - Spring Data JPA: dùng record làm interface projection hoặc DTO projection rất tốt -
 * Spring dựng record qua canonical constructor.
 * - Validation: {@code @NotNull} đặt trên record component sẽ được sao chép sang field,
 * tham số constructor và accessor tùy cấu hình {@code @Target} - đây là chỗ hay gây bối rối,
 * và là cầu nối sang bài t11 về annotation.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - Không có tương đương trực tiếp. Thứ gần nhất về MỤC ĐÍCH là một {@code interface} hoặc
 * {@code type} làm DTO - nhưng chúng biến mất lúc chạy, nên không thể liệt kê thành phần.
 * Record thì mang metadata theo mình, và đó là khác biệt quyết định.
 * - {@code Object.freeze(obj)} gần với tính bất biến của record, nhưng nông: nó chỉ khóa một
 * tầng và không có kiểm tra lúc biên dịch.
 * - Vấn đề "làm sao biết một type có những field nào lúc chạy" là nỗi đau kinh điển của hệ
 * TS: phải khai báo lại bằng zod, io-ts, class-validator, hoặc sinh code. Java với record
 * giải xong bằng {@code getRecordComponents()} - đây là ví dụ rõ nhất trong cả section về
 * chỗ Java mạnh hơn hẳn.
 * - Điểm chung: cả hai hệ đều đang đi về phía "dữ liệu bất biến có cấu trúc rõ ràng". Record
 * của Java và {@code readonly} object của TS cùng một triết lý, khác nhau ở chỗ Java cưỡng
 * chế được lúc chạy.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Record component là gì, lấy bằng method nào, có từ Java mấy?
 * - Sáu method của {@code RecordComponent}, cái nào hữu dụng nhất và vì sao?
 * - Vì sao {@code getGenericSignature()} thường là {@code null}? Khi nào nó có giá trị?
 * - {@code RecordComponent} có cho bạn object {@code Field} không? Lấy field bằng cách nào?
 * - Đọc field của record cần làm gì? Ghi thì sao, exception gì?
 * - {@code getRecordComponents()} trên một lớp thường trả về gì?
 * - Thứ tự các thành phần có đảm bảo không? So với {@code getDeclaredFields()}?
 * - Viết ba dòng code lấy canonical constructor của một record bất kỳ?
 * - Vì sao record an toàn khi làm khóa của {@code Map}?
 * - Vì sao record không dùng làm entity JPA được?
 * - Vì sao Jackson deserialize record được dù không có setter?
 */
public class t10_WorkingWithRecords {

    public record Point(int x, int y) {
    }

    /** Lớp THƯỜNG có private final - để đối chứng với record ở ví dụ 4. */
    static class Box {

        private final int x;

        Box(int x) {
            this.x = x;
        }

        @Override
        public String toString() {
            return "Box[x=" + x + "]";
        }
    }

    void main() throws ReflectiveOperationException {
        Point p = new Point(1, 2);

        // 1. Nhận diện record (từ Java 16). getRecordComponents() trả NULL - không phải
        //    mảng rỗng - với lớp thường, y hệt getEnumConstants() ở t9 (mục 2, mục 5).
        System.out.println("1. Point.isRecord()         -> " + Point.class.isRecord());
        System.out.println("   String.isRecord()        -> " + String.class.isRecord());
        System.out.println("   String.getRecordComp()   -> " + Arrays.toString(String.class.getRecordComponents()));

        // 2. RecordComponent - lớp mới của Reflection API. getAccessor() cho thẳng object
        //    Method, khỏi đoán tên: accessor của record là x(), KHÔNG phải getX() (mục 3).
        for (RecordComponent rc : Point.class.getRecordComponents()) {
            System.out.printf("2. %s | type = %s | accessor = %s | invoke -> %s%n",
                    rc.getName(), rc.getType(), rc.getAccessor(), rc.getAccessor().invoke(p));
        }

        // 3. Field của record là private final -> ĐỌC được, nhưng phải setAccessible (mục 4).
        Field fx = Point.class.getDeclaredField("x");
        fx.setAccessible(true);
        System.out.println("3. đọc field x              -> " + fx.getInt(p));

        // 4. GHI thì KHÔNG, dù đã setAccessible. Đây là điểm đáng nhớ nhất của bài (mục 4).
        try {
            fx.setInt(p, 99);
        } catch (IllegalAccessException e) {
            System.out.println("4. ghi field của record     -> IllegalAccessException: " + e.getMessage());
        }
        //    Đối chứng: lớp thường có private final thì reflection GHI ĐƯỢC bình thường.
        //    Khác biệt này là của riêng record, JVM cưỡng chế ở tầng dưới (mục 7).
        Box box = new Box(1);
        Field bx = Box.class.getDeclaredField("x");
        bx.setAccessible(true);
        bx.setInt(box, 99);
        System.out.println("   ghi field của lớp thường -> " + box);

        // 5. Thứ tự thành phần được ĐẢM BẢO (khác getDeclaredFields), và đó cũng là thứ tự
        //    tham số của canonical constructor -> dựng lại record từ dữ liệu động.
        //    Đây đúng là cách Jackson và Gson dựng record (mục 6, mục 8).
        Class<?>[] types = Arrays.stream(Point.class.getRecordComponents())
                .map(RecordComponent::getType)
                .toArray(Class[]::new);
        Constructor<?> canonical = Point.class.getDeclaredConstructor(types);
        System.out.println("5. canonical ctor           -> " + canonical);
        System.out.println("   newInstance(7, 8)        -> " + canonical.newInstance(7, 8));

        System.out.println();
        // TODO 1 (mục 2): Tạo "public record Point(int x, int y)". In isRecord() của Point,
        //   String, một enum, và một mảng. Lập bảng.
        //
        // TODO 2 (mục 3): Chạy đủ sáu method của RecordComponent cho thành phần x của Point.
        //   Dán output và đối chiếu với bảng trang gốc.
        //
        // TODO 3 (mục 3): Tạo "record Pair<A, B>(A first, B second)". In getType(),
        //   getGenericType(), getGenericSignature() của cả hai thành phần.
        //   Giải thích vì sao getType() ra Object.
        //
        // TODO 4 (mục 3): Dùng getAccessor() để lấy Method rồi invoke nó trên một instance.
        //   So sánh với việc tự đoán tên "getX" - thử getMethod("getX") và dán exception.
        //
        // TODO 5 (mục 4): Lấy getDeclaredFields() của Point, in modifier của từng field.
        //   Rồi setAccessible(true) và đọc giá trị bằng cả get() lẫn getInt().
        //
        // TODO 6 (mục 4): Thử setInt() lên field của record. Dán nguyên IllegalAccessException.
        //   Rồi làm điều tương tự với một lớp thường có private final - xác nhận lớp thường
        //   thì GHI ĐƯỢC. Đây là bài tập QUAN TRỌNG NHẤT của bài.
        //
        // TODO 7 (mục 5): Gọi getRecordComponents() trên String.class và in kết quả.
        //   Xác nhận là null chứ không phải mảng rỗng. Viết hàm an toàn trả mảng rỗng.
        //
        // TODO 8 (mục 6): Viết hàm tổng quát "Object buildRecord(Class<?> c, Object... values)"
        //   dùng getRecordComponents để lấy mảng kiểu, lấy canonical constructor, rồi
        //   newInstance. Test với Point và với một record ba thành phần.
        //
        // TODO 9 (mục 6): Bỏ từ khóa public của record rồi chạy lại TODO 8 với
        //   getConstructor thay vì getDeclaredConstructor. Dán exception.
        //
        // TODO 10 (mục 6): Viết hàm "Map<String, Object> toMap(Object record)" dùng
        //   getRecordComponents + getAccessor + invoke. Đây là một mini-serializer.
        //
        // TODO 11 (mục 8): Viết hàm ngược "T fromMap(Class<T> c, Map<String,Object> map)"
        //   khớp theo tên thành phần. Ghép với TODO 10 thành một vòng tròn và kiểm chứng
        //   object đi ra bằng object đi vào.
        //
        // TODO 12 (mục 7): Dùng record làm khóa của HashMap. Thử mọi cách bạn nghĩ ra để sửa
        //   nó sau khi đã put vào map. Ghi lại vì sao không cách nào thành công.
    }
}
