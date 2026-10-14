package com.javacore.language.oop.classes_objects;

/**
 * Enums
 * <p>
 * 0. Vị trí bài này
 * - Trang cuối cùng của bước 8. Sau bài này là đóng trọn section
 * classes-objects.
 * - Enum là chỗ gom lại gần như mọi thứ đã học: nó là class (t1), có
 * constructor (t3), có field và method (t2), constructor của nó private
 * nên bên ngoài không new được (t4 mục 11), và mỗi hằng có thân riêng
 * thực chất là một anonymous subclass (t5 mục 9).
 * - Trang gốc ngắn và ít code. Phần lớn ghi chú dưới đây được xác nhận
 * bằng cách chạy thật trên JDK 25 của repo, có dán kết quả kèm theo.
 * <p>
 * ======================================================================
 * TRANG GỐC - ENUMS (mục 1 đến 8)
 * ======================================================================
 * <p>
 * 1. ENUM LÀ GÌ - định nghĩa gọn mà chính xác
 * - Nguyên văn: "Enums are classes where ALL INSTANCES ARE KNOWN TO THE
 * COMPILER. They are used for creating types that can only have few
 * possible values".
 * - Nhấn mạnh chữ CLASSES: enum không phải một danh sách số như nhiều ngôn
 * ngữ khác, nó là class thật.
 * - Dùng từ khóa enum thay cho class. Thân enum chứa các HẰNG ngăn nhau
 * bằng dấu phẩy. Không tạo được instance nào ngoài các hằng đó.
 * - "All enums implicitly extend java.lang.Enum and cannot have any named
 * subclasses". Đã xác nhận bằng javap:
 * abstract class Phep extends java.lang.Enum<Phep> {
 * public static final Phep CONG;      <- hằng là static final field
 * private Phep();                     <- constructor TỰ ĐỘNG private
 * }
 * - Dòng "private Phep()" chính là cơ chế chặn: không ai new được từ bên
 * ngoài. Đúng mục 5 bài t3 - access modifier trên constructor kiểm soát
 * AI ĐƯỢC PHÉP tạo ra object.
 * - Vì đã trót extends java.lang.Enum nên enum KHÔNG kế thừa được class
 * nào khác, Java chỉ cho một lớp cha. Nhưng implement interface thì
 * thoải mái.
 * <p>
 * 2. TRUY CẬP, SO SÁNH VÀ SWITCH
 * - Hằng enum hoạt động như hằng số, gọi bằng TenEnum.TEN_HANG.
 * - So sánh bằng toán tử == . Đây là một trong rất ít chỗ dùng == với kiểu
 * tham chiếu mà vẫn ĐÚNG, vì mỗi hằng chỉ tồn tại đúng một object duy
 * nhất. Đã chạy thật: Mau.DO == Mau.valueOf("DO") -> true.
 * - switch dùng được trực tiếp trên enum, và viết tên hằng trần không cần
 * tiền tố tên enum.
 * - Điểm mạnh nhất: "With SWITCH EXPRESSIONS, the compiler can check
 * whether ALL VALUES of the enum are handled". Đã chạy thật, bỏ sót một
 * nhánh là biên dịch hỏng ngay:
 * error: the switch expression does not cover all possible input values
 * <p>
 * 3. THÊM THÀNH VIÊN - constructor, field, method
 * - Enum có đủ constructor, method và field y như class thường.
 * - LUẬT CÚ PHÁP phải nhớ: sau danh sách hằng BẮT BUỘC có DẤU CHẤM PHẨY
 * trước khi khai báo thêm bất cứ thứ gì. Thiếu dấu này là lỗi khó hiểu.
 * - Đối số truyền cho constructor đặt trong ngoặc ngay sau tên hằng.
 * - Ví dụ đã chạy thật:
 * enum TrangThai {
 * MOI("Moi tao", 0), DANG_XU_LY("Dang xu ly", 1), XONG("Hoan tat", 2);
 * private final String nhan;
 * TrangThai(String nhan, int buoc) { ... }
 * }
 * Gọi DANG_XU_LY.nhan() ra "Dang xu ly".
 * - Field của enum nên để final: hằng mà thay đổi được trạng thái thì
 * không còn là hằng nữa.
 * <p>
 * 4. BỐN METHOD CÓ SẴN - không phải gõ dòng nào
 * - name() trả về TÊN của hằng. Đã chạy: "DANG_XU_LY".
 * - ordinal() trả về VỊ TRÍ của hằng, đếm từ 0. Đã chạy: 1.
 * - values() là method STATIC, trả về mảng mọi instance. Đã chạy:
 * [MOI, DANG_XU_LY, XONG].
 * - valueOf(String) là method STATIC, lấy instance theo tên. Nó PHÂN BIỆT
 * HOA THƯỜNG, sai một chữ là ném IllegalArgumentException. Đã chạy:
 * valueOf("xong") -> No enum constant TrangThai.xong
 * - toString() mặc định trả về đúng như name().
 * <p>
 * 5. ENUM LÀ COMPARABLE
 * - "Enums implement the interface Comparable... ordered according to their
 * ORDINAL NUMBER". Tức là thứ tự so sánh chính là thứ tự KHAI BÁO, không
 * phải thứ tự chữ cái.
 * - Đã chạy: MOI.compareTo(XONG) = -2, đúng bằng hiệu hai ordinal.
 * - Hệ quả: sắp xếp một danh sách enum là ra đúng thứ tự khai báo, và
 * TreeMap với khóa enum cũng vậy.
 * <p>
 * 6. SINGLETON BẰNG ENUM
 * - Chỉ cần khai báo một enum có ĐÚNG MỘT hằng là có ngay singleton.
 * - Đơn giản đến mức khó tin, nhưng đây là cách làm singleton được khuyên
 * dùng nhất trong Java - lý do ở mục 9.
 * <p>
 * 7. ABSTRACT METHOD TRONG ENUM
 * - Enum khai báo được method abstract, và khi đó MỖI HẰNG bắt buộc phải
 * tự cài đặt method đó trong thân riêng của mình.
 * - Ví dụ đã chạy thật:
 * enum Phep {
 * CONG { public int ap(int a, int b){ return a+b; } },
 * NHAN { public int ap(int a, int b){ return a*b; } };
 * public abstract int ap(int a, int b);
 * }
 * Phep.CONG.ap(3,4) = 7, Phep.NHAN.ap(3,4) = 12.
 * - CƠ CHẾ BÊN DƯỚI, đây mới là phần thú vị: mỗi hằng có thân riêng thực
 * chất là một ANONYMOUS SUBCLASS. Biên dịch đoạn trên sinh ra ba file:
 * Phep.class, Phep$1.class, Phep$2.class
 * Đúng quy tắc đặt tên anonymous class đã học ở t5. Nói cách khác, enum
 * có abstract method chính là nơi anonymous class của bài trước xuất
 * hiện ngay trong cú pháp mà mình không hề nhận ra.
 * - Đây là cách thay thế switch rất sạch: thay vì hỏi "nó là hằng nào rồi
 * làm gì", để mỗi hằng tự biết việc của nó.
 * <p>
 * 8. CẢNH BÁO CỦA TRANG
 * - "Care should be taken when using enums where the NUMBER (or NAMES) of
 * instances is SUBJECT TO CHANGE".
 * - Sửa một enum có thể gây lỗi biên dịch, lỗi lúc chạy, hoặc tệ hơn là
 * dữ liệu không nhất quán ở phía code phụ thuộc vào nó.
 * - Lời khuyên: rà soát MỌI chỗ dùng trước khi sửa enum.
 * - Nếu số lượng instance nhiều và hay thay đổi thì cân nhắc dùng file cấu
 * hình thay vì enum.
 * <p>
 * ======================================================================
 * GHI CHÚ NGOÀI TÀI LIỆU VÀ BEST PRACTICE
 * ======================================================================
 * <p>
 * 9. Ghi chú ngoài tài liệu
 * - ordinal() LÀ CÁI BẪY LỚN NHẤT, và nó chính là mục 8 nói cụ thể ra.
 * ordinal phụ thuộc hoàn toàn vào THỨ TỰ KHAI BÁO. Chèn một hằng vào
 * giữa là mọi ordinal phía sau dịch đi một, trong khi mã nguồn nhìn vẫn
 * y nguyên. Quy tắc: ĐỪNG BAO GIỜ lưu ordinal xuống database, ghi ra
 * file, hay gửi qua API. ordinal() chỉ nên dùng cho EnumMap/EnumSet nội
 * bộ, nơi JDK tự quản lý.
 * - TRONG JPA ĐÂY LÀ BUG KINH ĐIỂN: annotation @Enumerated nếu không ghi
 * gì thì MẶC ĐỊNH là EnumType.ORDINAL, tức là lưu con số xuống cột. Sau
 * này chèn thêm một hằng vào giữa danh sách là toàn bộ dữ liệu cũ bị
 * hiểu sai - IM LẶNG, không exception, không log, chỉ là đơn hàng "đã
 * hủy" bỗng thành "đang giao". Luôn luôn viết rõ
 * @Enumerated(EnumType.STRING).
 * - JACKSON thì ngược lại, mặc định serialize enum bằng name() nên an toàn
 * hơn. Nhưng đổi lại, ĐỔI TÊN một hằng là phá vỡ hợp đồng API với mọi
 * client đang chạy. Enum lộ ra ngoài API thì tên của nó là một phần của
 * hợp đồng, không phải chi tiết nội bộ.
 * - values() TRẢ VỀ MẢNG MỚI MỖI LẦN GỌI, không phải mảng dùng chung. Đã
 * chạy thật:
 * Mau.values() == Mau.values()  ->  false
 * sửa mảng trả về rồi gọi lại   ->  [DO, XANH, VANG] (không đổi)
 * Đó là bản sao phòng vệ, vì mảng trong Java luôn sửa được nên JDK không
 * dám đưa mảng gốc ra. Hệ quả thực tế: gọi values() trong vòng lặp nóng
 * là sinh rác vô ích, nên cache lại một lần nếu dùng nhiều.
 * - ENUM LÀ SINGLETON AN TOÀN NHẤT TRONG JAVA. Nó chống được cả hai đường
 * mà singleton thường bị phá: deserialization không tạo ra bản thứ hai,
 * và reflection cũng không gọi được constructor của enum. So với mẫu
 * double-checked locking vừa dài vừa dễ sai, enum một hằng thắng tuyệt
 * đối. Đây là lý do đằng sau mục 6.
 * - EnumSet VÀ EnumMap LÀ HAI COLLECTION CHUYÊN DỤNG mà rất nhiều người
 * không biết. EnumSet cài bằng bit vector, EnumMap cài bằng mảng thường -
 * cả hai nhanh hơn hẳn HashSet/HashMap và không cần băm gì cả. Hễ khóa
 * là enum thì dùng EnumMap, đừng dùng HashMap.
 * - SWITCH ĐẦY ĐỦ LÀ TÍNH NĂNG, KHÔNG PHẢI PHIỀN TOÁI. Dùng switch
 * EXPRESSION kiểu mũi tên và CỐ TÌNH KHÔNG viết nhánh default: sau này
 * thêm một hằng mới vào enum, compiler sẽ chỉ ra chính xác mọi chỗ cần
 * sửa. Viết default là tự tay tắt mất tấm lưới an toàn đó, và chỗ thiếu
 * sót sẽ chỉ lộ ra lúc chạy.
 * - ENUM IMPLEMENT ĐƯỢC INTERFACE. Đây là cách làm plugin rất gọn: định
 * nghĩa một interface, cho enum implement nó, mỗi hằng là một cách cài
 * đặt. Kết hợp với mục 7 thì enum thay được cả một chùm if-else dài.
 * - SO VỚI TYPESCRIPT, khác biệt rất lớn chứ không chỉ là cú pháp. enum
 * của TS yếu hơn nhiều: numeric enum thực chất chỉ là số và còn nhận
 * được cả giá trị ngoài danh sách; string enum thì khá hơn. Idiom phổ
 * biến bên TS là union type kiểu 'MOI' | 'XONG', nhưng union chỉ là
 * KIỂU - nó không mang được field, không mang được method. Enum Java là
 * OBJECT THẬT: có constructor, có field, có method, implement interface
 * được, và mỗi hằng có thể có hành vi riêng. Đây là một trong số ít chỗ
 * Java mạnh hơn hẳn TypeScript.
 * <p>
 * Best practice: dùng enum ngay khi một giá trị chỉ có vài khả năng cố
 * định - nó mạnh hơn String hay int hằng số ở chỗ compiler chặn được giá
 * trị sai ngay lúc biên dịch, thay vì để một chuỗi gõ nhầm chạy thẳng tới
 * production. Field trong enum luôn để final. Tuyệt đối không lưu ordinal
 * ra bên ngoài chương trình, và trong JPA thì luôn ghi rõ
 * @Enumerated(EnumType.STRING). Ưu tiên switch expression không có default
 * để compiler nhắc mình mỗi khi enum có thêm hằng mới. Khi mỗi hằng cần
 * hành vi khác nhau, cho enum một abstract method thay vì viết switch rải
 * rác nhiều nơi - logic nằm ngay cạnh hằng thì không thể quên cập nhật.
 * Và nhớ rằng tên hằng enum là một phần hợp đồng với bên ngoài một khi nó
 * đã đi vào database hay API: đổi tên không phải là refactor vô hại.
 */
public class t6_Enums {

    void main() {
        // TODO 1 (muc 1, 3): viet enum TrangThaiDonHang co it nhat ba hang,
        //   moi hang mang mot nhan tieng Viet va mot ma so. Nho DAU CHAM PHAY
        //   sau danh sach hang. Thu bo dau cham phay de xem loi.

        // TODO 2 (muc 4): in ra name(), ordinal(), toString() va values().
        //   Sau do goi valueOf voi ten viet THUONG de bat
        //   IllegalArgumentException va in e.getMessage().

        // TODO 3 (muc 2): viet mot switch EXPRESSION tren enum do, CO Y bo
        //   sot mot hang va KHONG viet default -> doc loi cua javac. Day la
        //   tinh nang quan trong nhat cua enum, khong phai phien toai.

        // TODO 4 (muc 7): viet enum PhepToan voi mot abstract method, moi
        //   hang tu cai dat. Build xong vao thu muc target/classes xem co bao
        //   nhieu file PhepToan$N.class duoc sinh ra - doi chieu voi so hang.

        // TODO 5 (muc 9): chen THEM mot hang vao GIUA danh sach o TODO 1, roi
        //   in lai ordinal cua cac hang phia sau. Tu rut ra vi sao khong duoc
        //   luu ordinal xuong database.

        // TODO 6 (muc 2, 9): so sanh hai bien cung tro toi mot hang bang == va
        //   bang equals(). Giai thich vi sao day la mot trong rat it truong
        //   hop dung == voi kieu tham chieu ma van dung.
    }
}
