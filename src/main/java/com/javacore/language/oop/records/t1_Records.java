package com.javacore.language.oop.records;

import java.util.HashMap;
import java.util.Map;

/**
 * Records - Using Records to Model Immutable Data
 * <p>
 * 0. Vị trí bài này
 * - Bước 9/41. Mở section mới nhưng vẫn đứng trên nền section cũ: record
 * LÀ một class (t1), có constructor (t3), có field và method (t2), và
 * giống enum ở chỗ compiler viết hộ gần hết phần nhàm chán.
 * - Một câu để nhớ cả bài: record là NGƯỜI MANG DỮ LIỆU BẤT BIẾN NÔNG
 * (shallowly immutable data carrier). Chữ NÔNG là chỗ hay bị hiểu sai
 * nhất - xem mục 7 và mục 11.
 * - Mọi kết quả dán trong ghi chú này đều chạy thật trên JDK 25 của repo.
 * <p>
 * ======================================================================
 * TRANG GỐC - RECORDS (mục 1 đến 10)
 * ======================================================================
 * <p>
 * 1. VẤN ĐỀ RECORD GIẢI QUYẾT
 * - Một class chỉ để chở dữ liệu (Point, DTO, kết quả trung gian) bắt
 * buộc phải viết: field private final, constructor, accessor, toString,
 * equals, hashCode. Sáu nhóm code cho MỘT ý tưởng duy nhất là "cặp số
 * x và y".
 * - Nguyên văn trang gốc: "Records give you all this with a single line
 * of code".
 * - Đây không phải chuyện gõ ít phím. Boilerplate nhiều thì equals và
 * hashCode do IDE sinh ra rất dễ LỖI THỜI khi thêm field mới mà quên
 * sinh lại - một bug im lặng kinh điển. Record thì không bao giờ lệch,
 * vì compiler sinh lại mỗi lần biên dịch.
 * <p>
 * 2. KHAI BÁO VÀ COMPILER SINH RA GÌ
 * - Cú pháp (có từ Java SE 14):
 * public record Point(int x, int y) {}
 * - Dùng từ khóa record thay cho class. Phần trong ngoặc là RECORD
 * COMPONENTS, không phải tham số constructor thông thường - nó khai báo
 * toàn bộ trạng thái của kiểu.
 * - javap chạy thật trên record Demo(int x, String name):
 * public final class Demo extends java.lang.Record {
 * private final int x;                  <- field tự sinh, final
 * private final java.lang.String name;
 * public Demo(int, java.lang.String);   <- canonical constructor
 * public final java.lang.String toString();
 * public final int hashCode();
 * public final boolean equals(java.lang.Object);
 * public int x();                       <- accessor, KHÔNG có getX
 * public java.lang.String name();
 * }
 * - Ba điều đọc được từ đây: class là FINAL (không ai kế thừa được), cha
 * là java.lang.Record nên record KHÔNG extends class nào khác, và
 * toString/equals/hashCode được sinh ra ở dạng final.
 * - Nhưng implement interface thì thoải mái, bao nhiêu cũng được.
 * - Tên accessor là x() chứ không phải getX(). Khác biệt này làm nhiều
 * thư viện cũ theo chuẩn JavaBeans hụt chân - xem mục 15.
 * <p>
 * 3. BỐN THÀNH VIÊN TỰ SINH - chạy thật với record Point(int x, int y)
 * - toString   : Point[x=1, y=2]
 * - equals     : new Point(1,2).equals(new Point(1,2)) -> true
 * - == thì vẫn false, vì là hai object khác nhau. Record cho equals đúng
 * theo GIÁ TRỊ, chứ không gộp hai object thành một như enum.
 * - hashCode   : hai object cùng giá trị cho cùng hashCode -> true
 * - Hệ quả thực dụng: record dùng làm KHÓA HashMap ngay được, không phải
 * viết thêm dòng nào. Đã chạy: map.put(new Point(1,2), "A") rồi
 * map.get(new Point(1,2)) trả về "A".
 * - Trang gốc nói các method này có "default behavior that corresponds to
 * what an IDE would have generated".
 * <p>
 * 4. CANONICAL CONSTRUCTOR
 * - Là constructor nhận ĐÚNG danh sách component, theo ĐÚNG thứ tự.
 * Compiler luôn sinh sẵn một cái:
 * public Point(int x, int y) { this.x = x; this.y = y; }
 * - Viết lại tường minh được, nhưng khi đó phải TỰ gán this.x = x.
 * - Access modifier của canonical constructor không được hẹp hơn chính
 * record. Đã thử để private trong một record package-private, javac báo:
 * error: invalid canonical constructor in record N
 * (attempting to assign stronger access privileges; was package)
 * <p>
 * 5. COMPACT CONSTRUCTOR - nơi đặt validate và chuẩn hóa
 * - Cú pháp rút gọn, không có ngoặc tham số:
 * public record Range(int start, int end) {
 * public Range {
 * if (start > end) throw new IllegalArgumentException(...);
 * }
 * }
 * - Luật quan trọng nhất: trong compact constructor KHÔNG được gán
 * this.start = start. Thử thật, javac báo:
 * error: cannot assign a value to final variable x
 * - Lý do: compiler tự chèn phần gán field vào CUỐI thân compact. Cái
 * mình đang sửa là THAM SỐ, và sửa tham số chính là cách chuẩn hóa dữ
 * liệu. Đã chạy: compact có "if (start < 0) start = 0;" rồi gọi
 * new Range(-5, 10) cho ra Range[start=0, end=10].
 * - Đây là điểm record mạnh hơn hẳn DTO viết tay: KHÔNG CÓ ĐƯỜNG NÀO tạo
 * được một Range không hợp lệ. Mọi lối vào đều phải qua canonical
 * constructor, kể cả deserialize (mục 9).
 * <p>
 * 6. CUSTOM CONSTRUCTOR - phải gọi về canonical
 * - Mọi constructor khác bắt buộc gọi this(...) để cuối cùng vẫn rơi vào
 * canonical constructor. Thử thật, javac báo:
 * error: constructor is not canonical, so it must invoke another
 * constructor of class E5
 * - Ví dụ trang gốc: State(String name, String capital) gọi
 * this(name, capital, List.of()) để cho giá trị mặc định.
 * - Lợi ích kiến trúc: validate viết MỘT lần ở canonical constructor, mọi
 * constructor tiện lợi khác đều không thể lách qua.
 * <p>
 * 7. BẤT BIẾN "NÔNG" VÀ DEFENSIVE COPY
 * - Field là final nên THAM CHIẾU không đổi được. Nhưng OBJECT mà tham
 * chiếu đó trỏ tới thì vẫn đổi được như thường.
 * - Đã chạy thật với record Bag(List&lt;String&gt; items):
 * List src = new ArrayList(List.of("a"));
 * Bag bag = new Bag(src);
 * src.add("b");
 * -> in ra: Bag[items=[a, b]]
 * Nội dung record đã đổi dù không ai chạm vào record. Bất biến chỉ là
 * cái vỏ ngoài.
 * - Cách chữa, ngay trong compact constructor:
 * SafeBag { items = List.copyOf(items); }
 * Chạy lại: sửa list gốc -> SafeBag[items=[a]], và gọi
 * safe.items().add("c") -> UnsupportedOperationException.
 * - Trang gốc gợi ý thêm cách phòng thủ ở accessor:
 * public List&lt;String&gt; cities() { return Collections.unmodifiableList(cities); }
 * - Chọn cái nào: copy trong constructor mạnh hơn, vì nó chặn được cả
 * đường rò rỉ qua tham chiếu bên ngoài. Bọc ở accessor chỉ chặn người
 * NHẬN sửa, không chặn người ĐƯA sửa. Production nên làm ở constructor,
 * và làm cả hai khi dữ liệu nhạy cảm.
 * <p>
 * 8. NHỮNG THỨ KHÔNG ĐƯỢC THÊM - kèm lỗi javac thật
 * - Instance field ngoài component:
 * error: field declaration must be static
 * (consider replacing field with record component)
 * - Instance initializer block { ... }:
 * error: instance initializers not allowed in records
 * - extends một class khác: hỏng ngay ở cú pháp, error: '{' expected
 * - Kế thừa TỪ record cũng không được:
 * error: cannot inherit from final F
 * - Được phép thoải mái: static field, static initializer, static method
 * (factory), instance method, implement interface, generic
 * (record Box&lt;T&gt;(T value) chạy bình thường), và override accessor.
 * - Nói gọn: record khóa chặt TRẠNG THÁI nhưng mở hoàn toàn với HÀNH VI.
 * Đó là triết lý data carrier, không phải "class bị cắt bớt".
 * <p>
 * 9. SERIALIZATION - lý do record hợp làm DTO
 * - Với record implements Serializable: writeObject/readObject tự viết
 * KHÔNG có tác dụng, còn deserialize thì LUÔN đi qua canonical
 * constructor.
 * - Nghĩa là dữ liệu bẩn từ ngoài vào vẫn phải chịu đúng những phép
 * validate ở mục 5. Với class thường thì ngược lại: deserialize dựng
 * object mà không gọi constructor, mọi bất biến đều có thể bị phá.
 * - Nguyên văn: "This makes records a very good choice for creating data
 * transport objects in your application".
 * <p>
 * 10. LOCAL RECORD
 * - Khai báo record NGAY TRONG một method, thường để đặt tên cho dữ liệu
 * trung gian của stream:
 * record NumberOfCitiesPerState(State state, long count) {}
 * - Đã chạy thật: record Pair(String k, long v) {} khai báo trong main
 * dùng bình thường, in ra [Pair[k=a, v=3], Pair[k=b, v=7]].
 * - Giá trị thật nằm ở chỗ đọc: Map.Entry&lt;State, Long&gt; với
 * getKey()/getValue() luôn mơ hồ, còn state()/count() thì tự nó giải
 * thích. Có tên có nghĩa mà không phải đẻ thêm file.
 * <p>
 * ======================================================================
 * NGOÀI TRANG GỐC - NHỮNG CHỖ THỰC SỰ GÂY LỖI
 * ======================================================================
 * <p>
 * 11. BẪY LỚN NHẤT: COMPONENT KIỂU MẢNG
 * - equals/hashCode tự sinh so sánh mảng bằng THAM CHIẾU chứ không phải
 * nội dung. Đã chạy thật với record Arr(int[] data):
 * new Arr(new int[]{1,2,3}).equals(new Arr(new int[]{1,2,3})) -> false
 * toString -> Arr[data=[I@2e1d27ba]
 * - Record trông như "so sánh theo giá trị" nên bẫy này rất im lặng và
 * khó thấy lúc review. Đừng để mảng làm component; dùng List kèm
 * List.copyOf. Nếu buộc phải dùng mảng thì tự override cả ba method.
 * <p>
 * 12. toString VÀ equals ĐỌC FIELD, KHÔNG GỌI ACCESSOR
 * - Override accessor được, nhưng nó không ảnh hưởng các method khác. Đã
 * chạy với record Liar(int x) có "public int x() { return -x; }":
 * l.x() -> -5   nhưng   toString -> Liar[x=5]
 * - Một kiểu mà accessor nói một đằng, toString nói một nẻo là nguồn gốc
 * của những buổi debug rất dài. Override accessor chỉ nên dùng để BỌC
 * phòng thủ (mục 7), tuyệt đối không để BIẾN ĐỔI giá trị.
 * <p>
 * 13. ĐỪNG LƯU hashCode RA NGOÀI CHƯƠNG TRÌNH
 * - Record.hashCode chỉ được đặc tả là dẫn xuất từ các component, giá trị
 * cụ thể không được cam kết giữa các bản JVM. Point(1,2) ra 33 hôm nay
 * không bảo đảm mai vẫn là 33.
 * - Đúng bài học ordinal của enum: tiện tay đem làm khóa lưu trữ là tự
 * đặt một quả bom hẹn giờ.
 * <p>
 * 14. RECORD + PATTERN MATCHING - phần thưởng thật sự
 * - Trang gốc không nhắc, nhưng record sinh ra là để đi cùng record
 * pattern (chính thức từ JDK 21). Đã chạy:
 * if (o instanceof Point(int x, int y)) ...  -> x=1 y=2
 * - Vì compiler biết chắc danh sách component nên nó tháo tung object
 * ngay trong điều kiện. Ghép thêm sealed interface là có kiểu tổng
 * (sum type) đầy đủ - thứ tương đương discriminated union bên
 * TypeScript, và switch sẽ bắt lỗi thiếu nhánh y như với enum.
 * - Record cũng có reflection riêng: Point.class.isRecord() -> true,
 * getRecordComponents() -> [int x, int y]. Jackson và các framework
 * dựa vào đúng hai API này để biết cách dựng lại object.
 * <p>
 * 15. RECORD TRONG SPRING BOOT - dùng đúng chỗ
 * - KHÔNG dùng record làm @Entity. JPA cần class không final, cần
 * constructor rỗng, cần sửa field để làm dirty checking và lazy proxy -
 * record phản đối toàn bộ những điều đó. Entity là object có danh tính
 * và vòng đời; record là giá trị.
 * - Dùng record cho: request/response DTO, projection của Spring Data
 * (constructor expression trong JPQL), kết quả trung gian trong
 * service, value object như Money hay KhoangThoiGian.
 * - Jackson hỗ trợ record từ bản 2.12 nên Spring Boot 3 bind @RequestBody
 * vào record chạy thẳng: không setter, không Lombok.
 * - Annotation validate đặt ngay trên component, ví dụ
 * record TaoDonHang(@NotBlank String ma, @Positive int soLuong) {}
 * vẫn chạy với @Valid, vì annotation lan xuống cả field lẫn tham số
 * constructor.
 * - @ConfigurationProperties bind vào record cũng được (constructor
 * binding) - hợp lý, vì cấu hình vốn dĩ phải bất biến.
 * <p>
 * 16. KHI NÀO KHÔNG DÙNG RECORD
 * - Khi object cần thay đổi trạng thái theo thời gian.
 * - Khi hai object cùng dữ liệu vẫn phải là hai thực thể khác nhau
 * (entity có id) - lúc đó equals theo giá trị là SAI về nghiệp vụ.
 * - Khi cần kế thừa.
 * - Khi dữ liệu có quá nhiều field tùy chọn: constructor 12 tham số khó
 * đọc hơn builder.
 * - Ngoài bốn trường hợp đó, với một kiểu chỉ để chở dữ liệu thì mặc
 * định nên là record, và chỉ hạ xuống class thường khi có lý do cụ thể.
 */
public class t1_Records {

    /**
     * Ví dụ cơ bản nhất của trang gốc. Một dòng này thay cho cả một class
     * có 2 field final, 1 constructor, 2 accessor, equals, hashCode và
     * toString - xem javap để thấy compiler đã viết hộ những gì.
     */
    public record Point(int x, int y) {
    }

    /**
     * Ví dụ cơ bản: Record mô tả thông tin Người dùng (User).
     * <p>
     * Record là lớp mang dữ liệu bất biến (shallowly immutable data carrier).
     * Compiler tự động sinh ra:
     * <ul>
     *   <li>Các trường {@code private final} tương ứng: {@code username}, {@code email}, {@code age}.</li>
     *   <li>Canonical constructor nhận đủ các components: {@code User(String, String, int)}.</li>
     *   <li>Các accessor method có cùng tên với component: {@code username()}, {@code email()}, {@code age()}.</li>
     *   <li>Các phương thức {@code toString()}, {@code equals()}, {@code hashCode()} chuẩn theo giá trị.</li>
     * </ul>
     *
     * @param username tên tài khoản người dùng
     * @param email địa chỉ email
     * @param age tuổi của người dùng
     */
    public record User(String username, String email, int age) {
        /**
         * Compact constructor: dùng để validate hoặc chuẩn hoá dữ liệu.
         * Lưu ý: không tự gán {@code this.field = field}, compiler sẽ tự động chèn ở cuối.
         *
         * @throws IllegalArgumentException nếu dữ liệu đầu vào không hợp lệ
         */
        public User {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username không được để trống");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Email không hợp lệ: " + email);
            }
            if (age < 0) {
                throw new IllegalArgumentException("Tuổi không thể âm: " + age);
            }
        }

        /**
         * Phương thức nghiệp vụ (instance method) bổ sung cho record.
         *
         * @return {@code true} nếu người dùng từ 18 tuổi trở lên
         */
        public boolean isAdult() {
            return age >= 18;
        }
    }

    void main() {
        // --- 1. Vi du co ban Point ---
        Point p = new Point(1, 2);

        IO.println(p);                          // Point[x=1, y=2] - toString tu sinh
        IO.println(p.x() + " " + p.y());        // accessor ten x() / y(), khong phai getX()
        IO.println(p.equals(new Point(1, 2)));  // true  - equals so theo GIA TRI
        IO.println(p == new Point(1, 2));       // false - nhung van la hai object khac nhau

        // --- 2. Vi du co ban User (co validation & instance method) ---
        User user1 = new User("nguyenvana", "vana@example.com", 20);
        User user2 = new User("nguyenvana", "vana@example.com", 20);

        IO.println("\n--- Demo Record User ---");
        IO.println("toString(): " + user1);                           // User[username=nguyenvana, email=vana@example.com, age=20]
        IO.println("Accessor username(): " + user1.username());       // nguyenvana (khong phai getUsername())
        IO.println("Accessor email(): " + user1.email());             // vana@example.com
        IO.println("Accessor age(): " + user1.age());                 // 20
        IO.println("Instance method isAdult(): " + user1.isAdult());  // true
        IO.println("user1.equals(user2): " + user1.equals(user2));    // true (so theo noi dung/gia tri)
        IO.println("user1 == user2: " + (user1 == user2));            // false (hai instance khac nhau trong bo nho)

        // TODO 1 (muc 3): dua Point lam KEY cua mot HashMap, put bang object
        //   nay va get bang mot object MOI TAO cung gia tri. Giai thich vi sao
        //   lay ra duoc, trong khi == o tren tra ve false.
        Map<Point, String> pointMap = new HashMap<>();
        pointMap.put(new Point(1, 2), "Diem A");
        IO.println("\nTODO 1 - HashMap get: " + pointMap.get(new Point(1, 2))); // "Diem A" (tim duoc vi equals & hashCode so sanh theo gia tri)

        // TODO 2 (muc 2): build xong, chay javap -p tren file .class cua Point
        //   trong target/classes. Dem xem compiler da viet ho bao nhieu thanh
        //   vien, va xac nhan class la final, cha la java.lang.Record.

        // TODO 3 (muc 5): viet record KhoangThoiGian(LocalDate tu, LocalDate den)
        //   voi compact constructor nem IllegalArgumentException khi tu sau den,
        //   va chuan hoa null thanh mot gia tri mac dinh. Sau do THU viet
        //   this.tu = tu trong compact constructor de doc loi cua javac.

        // TODO 4 (muc 7, 11): viet record GioHang(List<String> matHang) KHONG
        //   copy. Truyen vao mot ArrayList, sua list goc, in record ra xem no
        //   doi chua. Sua lai bang List.copyOf trong compact constructor roi
        //   chay lai. Lam tiep mot lan nua voi component kieu String[] de thay
        //   equals tra ve false du noi dung giong het.

        // TODO 5 (muc 6, 8): them vao record o TODO 3 mot static factory
        //   homNay(), mot instance method soNgay(), va THU them mot field
        //   instance thuong de doc loi "field declaration must be static".

        // TODO 6 (muc 10): tao mot list bat ky, dung stream groupingBy +
        //   counting, roi khai bao mot LOCAL record de dat ten cho cap ket qua
        //   thay vi xai Map.Entry. So sanh hai ban xem ban nao de doc hon.

        // TODO 7 (muc 14): viet mot sealed interface voi hai record implement
        //   no, roi dung switch pattern matching tren interface do. Bo sot mot
        //   nhanh va KHONG viet default de xem compiler co bat loi khong -
        //   doi chieu voi bai hoc switch cua enum.

        // TODO 8 (muc 12): override accessor cua mot record cho no tra ve gia
        //   tri khac (vi du cong them 1). In accessor va in toString cua cung
        //   mot object. Giai thich tai sao hai ket qua lech nhau.
    }
}
