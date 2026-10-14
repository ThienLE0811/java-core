package com.javacore.language.oop.inheritance;

/**
 * What is Inheritance
 * <p>
 * 0. Vị trí bài này
 * - Bước 10/41, trang đầu của section Inheritance. Mục 4 bài
 * classes/t1_Classes đã nói ý tưởng chung; trang này đi vào LUẬT.
 * - Một câu cho cả bài: kế thừa là nói "cái này LÀ MỘT loại của cái kia",
 * và đổi lại Java cho subclass dùng ké toàn bộ thành viên của superclass.
 * Chỗ khó không nằm ở extends mà ở chuyện CÁI GÌ ĐƯỢC CHỌN LÚC NÀO -
 * field chọn theo kiểu biến, method chọn theo object thật (mục 7).
 * - Mọi kết quả dán trong ghi chú này đều chạy thật trên JDK 25 của repo.
 * <p>
 * ======================================================================
 * TRANG GỐC - WHAT IS INHERITANCE (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. TỪ VỰNG VÀ MÔ HÌNH ĐƠN KẾ THỪA
 * - "A class that is derived from another class is called a SUBCLASS
 * (also a derived class, extended class, or child class). The class
 * from which the subclass is derived is called a SUPERCLASS (also a
 * base class or a parent class)".
 * - "Excepting Object, which has no superclass, every class has ONE AND
 * ONLY ONE direct superclass (single inheritance)".
 * - Lý do dùng: đã có sẵn class chứa phần code mình cần thì tạo class mới
 * dẫn xuất từ nó, chỉ viết phần KHÁC BIỆT.
 * - Object nằm ở đỉnh mọi cây kế thừa, "defines and implements behavior
 * common to all classes". Không viết extends thì vẫn ngầm extends
 * Object - đó là lý do mọi object đều có toString, equals, hashCode,
 * getClass. Đã chạy: toString mặc định in ra Cha2@6d06d69c, tức là
 * tên class + hashCode dạng hex.
 * - Subclass kế thừa "all the members (fields, methods, and nested
 * classes)". CONSTRUCTOR KHÔNG phải là member nên KHÔNG được kế thừa -
 * xem mục 8.
 * <p>
 * 2. VÍ DỤ CỦA TRANG GỐC
 * - Bicycle có cadence, gear, speed cùng các method điều khiển.
 * MountainBike extends Bicycle, "inherits all the fields and methods of
 * Bicycle and adds the field seatHeight and a method to set it".
 * - Đọc một câu là thấy toàn bộ lợi ích: file MountainBike chỉ chứa phần
 * RIÊNG. Và cũng thấy luôn cái giá - nhìn file con không biết nó có
 * những gì, phải mở file cha ra đọc.
 * <p>
 * 3. TRONG SUBCLASS LÀM ĐƯỢC GÌ - danh sách luật của trang gốc
 * - Tầm nhìn: "A subclass inherits all of the PUBLIC and PROTECTED
 * members of its parent, NO MATTER WHAT PACKAGE the subclass is in. If
 * the subclass is in the SAME PACKAGE as its parent, it also inherits
 * the PACKAGE-PRIVATE members".
 * - Đã chạy thật với Bicycle ở package a, MountainBike ở package b:
 * public    : 1      &lt;- thấy
 * protected : 2      &lt;- thấy
 * private qua accessor: 4
 * còn field package-private và private thì không biên dịch nổi.
 * - Tám việc trang gốc liệt kê:
 * (1) dùng thẳng field kế thừa;
 * (2) khai báo field TRÙNG TÊN với field của cha, tức CHE nó đi -
 * trang gốc ghi rõ "(not recommended)";
 * (3) khai báo field mới;
 * (4) dùng thẳng method kế thừa;
 * (5) viết instance method cùng chữ ký để GHI ĐÈ (override);
 * (6) viết static method cùng chữ ký để CHE (hide) - khác hẳn (5);
 * (7) khai báo method mới;
 * (8) viết constructor gọi constructor của cha, ngầm hoặc bằng super.
 * - Hai chữ cần phân biệt từ đầu: CHE (hiding) và GHI ĐÈ (overriding)
 * nghe giống nhau nhưng chạy khác nhau hoàn toàn - mục 7.
 * <p>
 * 4. PRIVATE TRONG SUPERCLASS
 * - "A subclass does NOT inherit the private members of its parent
 * class". Nhưng nếu cha có method public/protected đọc field private đó
 * thì con dùng được - đã chạy: layRieng() trả về 4.
 * - Chứng minh private không được kế thừa, bằng javac: viết lại method
 * private của cha trong con rồi đánh dấu override:
 * error: method does not override or implement a method from a supertype
 * Con không hề "thấy" method đó, nên cái nó viết chỉ là một method mới
 * trùng tên. Đây là lý do method private không bao giờ đa hình.
 * - Ngoại lệ duy nhất: "A nested class has access to ALL the private
 * members of its enclosing class" - đúng bài nested class ở t5.
 * <p>
 * 5. ÉP KIỂU OBJECT
 * - "MountainBike is descended from Bicycle and Object. Therefore, a
 * MountainBike IS A Bicycle and is also an Object". Chiều ngược lại
 * không chắc: "a Bicycle MAY BE a MountainBike, but it is not
 * necessarily".
 * - Lên trên (upcast) là IMPLICIT CASTING, không cần cú pháp gì:
 * Bicycle b = mb;   // mb kiểu MountainBike
 * Luôn an toàn vì con chắc chắn là một loại của cha.
 * - Xuống dưới (downcast) phải viết tường minh, và "this cast inserts a
 * RUNTIME CHECK". Sai thì nổ lúc chạy. Đã chạy thật:
 * class a.Bicycle cannot be cast to class b.MountainBike
 * - "You can make a logical test as to the type of a particular object
 * using the instanceof operator. This can save you from a runtime error
 * owing to an improper cast".
 * - Bản hiện đại gộp kiểm tra và ép kiểu làm một (pattern matching for
 * instanceof, JDK 16). Đã chạy:
 * if (b instanceof MountainBike m) ... -&gt; seatHeight=50
 * Viết kiểu này thì không còn chỗ nào để ép nhầm nữa. Trong code mới,
 * gần như không bao giờ phải viết dấu ngoặc ép kiểu nữa.
 * <p>
 * 6. ĐA KẾ THỪA: STATE, IMPLEMENTATION, TYPE
 * - Trang gốc tách làm ba, và Java trả lời khác nhau cho từng loại.
 * - STATE (kế thừa FIELD từ nhiều class): KHÔNG cho. Lý do nguyên văn:
 * "What if methods or constructors from different superclasses
 * instantiate the same field? Which method or constructor will take
 * precedence?". Đã thử extends hai class, javac hỏng ngay ở cú pháp:
 * error: '{' expected
 * - IMPLEMENTATION (kế thừa THÂN method từ nhiều nguồn): có một phần qua
 * default method của interface, và xung đột thì phải tự xử. Đã chạy
 * thật với hai interface cùng có default hi():
 * error: types I1 and I2 are incompatible;
 * class D1 inherits unrelated defaults for hi() from types I1 and I2
 * Java không chọn hộ - bắt mình override và nói rõ muốn gì.
 * - TYPE (implement nhiều interface): ĐƯỢC, thoải mái. "The Java
 * programming language supports multiple inheritance of type".
 * - Rút gọn thành một câu dễ nhớ: nhiều VAI THÌ ĐƯỢC, nhiều CHA THÌ
 * KHÔNG. Đây chính là nền cho lời khuyên "ưu tiên interface".
 * <p>
 * ======================================================================
 * NGOÀI TRANG GỐC - NHỮNG CHỖ THỰC SỰ GÂY LỖI
 * ======================================================================
 * <p>
 * 7. QUAN TRỌNG NHẤT: FIELD THEO KIỂU BIẾN, METHOD THEO OBJECT THẬT
 * - Cho Cha có field nhan="CHA" và method ten(); Con che field thành
 * nhan="CON" và ghi đè ten(). Chạy với "Cha c = new Con();":
 * c.nhan        -&gt; CHA        &lt;- theo KIỂU BIẾN (static binding)
 * c.ten()       -&gt; Con.ten    &lt;- theo OBJECT THẬT (dynamic dispatch)
 * ((Con)c).nhan -&gt; CON        &lt;- đổi kiểu biến là đổi field luôn
 * - Cùng một object mà trả lời hai kiểu khác nhau. Field được chọn lúc
 * BIÊN DỊCH, method được chọn lúc CHẠY.
 * - Tàn nhẫn hơn, trong method gioiThieu() viết ở class Cha:
 * field=CHA | method=Con.ten
 * Cùng một dòng code, field lấy của cha còn method nhảy xuống con.
 * - Kết luận thực dụng: ĐỪNG BAO GIỜ che field. Trang gốc nói "not
 * recommended"; thực tế thì đây là bug không ai đọc ra khi review. Để
 * field private và cho con dùng qua method là xong chuyện.
 * - Static method cũng là CHE chứ không ghi đè: Cha.loai() và Con.loai()
 * là hai method độc lập, gọi cái nào là do kiểu lúc biên dịch. Đã chạy:
 * Cha.loai() -&gt; Cha.loai | Con.loai() -&gt; Con.loai
 * - super.nhan và super.ten() luôn trỏ thẳng lên bản của cha. Đã chạy:
 * this.nhan=CON, super.nhan=CHA, super.ten()=Cha.ten
 * <p>
 * 8. CONSTRUCTOR KHÔNG ĐƯỢC KẾ THỪA - và super() ngầm
 * - Constructor không phải member nên không kế thừa. Hệ quả: cha có
 * Bicycle(int gear) thì con KHÔNG tự nhiên có constructor đó.
 * - Constructor của con LUÔN gọi một constructor của cha. Không viết gì
 * thì compiler chèn super() không tham số. Nếu cha không có constructor
 * rỗng là hỏng ngay. Đã chạy thật:
 * error: constructor A1 in class A1 cannot be applied to given types;
 * required: int
 * found:    no arguments
 * Thông báo này gây hoang mang vì chỗ lỗi trỏ vào constructor của CON
 * mà không thấy dòng gọi nào - vì dòng gọi là do compiler chèn.
 * - Cách sửa: tự viết super(...) với đúng đối số.
 * - MỚI TỪ JDK 25 (JEP 513 Flexible Constructor Bodies): được viết lệnh
 * TRƯỚC super(), miễn là không đụng tới this. Đã chạy thật trên repo:
 * Con2(String s) {
 * int t = Integer.parseInt(s);
 * if (t &gt; 150) throw new IllegalArgumentException("qua lon: " + t);
 * super(t);
 * }
 * -&gt; in ra "validate truoc super(): qua lon: 999"
 * Trước JDK 22 phải lách bằng static method. Giờ validate đối số trước
 * khi dựng cha là chuyện bình thường.
 * <p>
 * 9. PROTECTED KHÁC PACKAGE - cái bẫy ít ai biết
 * - "Con thấy được protected của cha" là đúng nhưng chưa đủ. Khi khác
 * package, con chỉ truy cập được protected QUA THAM CHIẾU KIỂU CON
 * (hoặc this), chứ không phải qua một object cha bất kỳ. Đã chạy:
 * this.x       -&gt; OK
 * cungLoai.x   -&gt; OK   (biến kiểu Q)
 * nguoiKhac.x  -&gt; error: x has protected access in P
 * - Lý do: protected là để một dòng họ tự chăm sóc trạng thái của mình,
 * không phải để mở cửa cho mọi họ hàng xa cùng package khác.
 * <p>
 * 10. LUẬT KHI GHI ĐÈ - bốn thứ javac sẽ chặn
 * - Không được THU HẸP quyền truy cập. Đã chạy:
 * error: ten() in B9 cannot override ten() in A9
 * attempting to assign weaker access privileges; was public
 * Đây chính là LSP ở dạng compiler cưỡng chế: con không được khó tính
 * hơn cha, vì ai đó đang cầm biến kiểu cha.
 * - final trên method là chặn ghi đè:
 * error: khoa() in B5 cannot override khoa() in A5
 * overridden method is final
 * - final trên class là chặn kế thừa: error: cannot inherit from final A6
 * (đúng cái record đã gặp ở bước 9).
 * - Kiểu trả về được PHÉP hẹp hơn (covariant return type): cha trả Object
 * thì con trả String - biên dịch sạch, đã thử.
 * - Luôn viết annotation Override. Nó không đổi hành vi, nhưng gõ sai
 * một chữ trong tên method thì compiler báo ngay thay vì để mình ngồi
 * tự hỏi sao method không chạy - đúng tình huống mục 4 ở trên.
 * <p>
 * 11. GÓC NHÌN PRODUCTION - dùng kế thừa ở đâu cho đúng
 * - Mặc định nên là COMPOSITION, không phải inheritance. Câu hỏi để tự
 * kiểm: "X thật sự LÀ MỘT loại của Y" hay chỉ là "X CẦN DÙNG code của
 * Y"? Vế sau thì tiêm Y vào làm field, đừng extends.
 * - Lý do không chỉ là triết lý: extends khóa chặt con vào chi tiết cài
 * đặt của cha. Cha đổi một dòng, mọi class con có thể hỏng mà không ai
 * báo trước - tên gọi quen thuộc là fragile base class.
 * - Trong Spring: tầng service gần như không bao giờ nên extends nhau,
 * hãy tiêm dependency. Chỗ kế thừa thật sự có lý là cây exception
 * (RuntimeException -&gt; ApiException -&gt; NotFoundException) và
 * BaseEntity mang id/createdAt dùng chung, thường đi với
 * annotation MappedSuperclass của JPA.
 * - Và nhớ bài học đã học ở t5/t6: khi chỉ cần một tập cố định các biến
 * thể, sealed interface cộng record cho kết quả an toàn hơn hẳn một cây
 * kế thừa mở - compiler bắt lỗi thiếu nhánh thay vì mình tự nhớ.
 */
public class t1_Inheritance {

    /**
     * Ví dụ của trang gốc, rút gọn còn phần cốt lõi.
     */
    public static class Bicycle {
        protected int cadence;
        protected int gear;
        protected int speed;

        public Bicycle(int cadence, int speed, int gear) {
            this.cadence = cadence;
            this.speed = speed;
            this.gear = gear;
        }

        public void applyBrake(int decrement) {
            speed -= decrement;
        }

        public String moTa() {
            return "Bicycle speed=" + speed;
        }
    }

    public static class MountainBike extends Bicycle {
        private int seatHeight;

        public MountainBike(int cadence, int speed, int gear, int seatHeight) {
            super(cadence, speed, gear);   // BAT BUOC: cha khong co constructor rong
            this.seatHeight = seatHeight;
        }

        public void setHeight(int newValue) {
            seatHeight = newValue;
        }

        @Override
        public String moTa() {
            return "MountainBike speed=" + speed + " seat=" + seatHeight;
        }
    }

    void main() {
        MountainBike mb = new MountainBike(20, 10, 1, 50);
        Bicycle b = mb;                     // upcast ngam, khong can cu phap gi

        IO.println(mb.moTa());
        IO.println(b.moTa());               // van goi ban cua MountainBike
        IO.println(b instanceof MountainBike);

        // TODO 1 (muc 3, 4): them vao Bicycle mot field private, mot field
        //   protected va mot field package-private. Trong MountainBike thu
        //   doc ca ba. Sau do COPY Bicycle sang mot package khac va thu lai -
        //   doi chieu voi luat o muc 3.

        // TODO 2 (muc 7 - lam ky bai nay nhat): cho Bicycle mot field
        //   String nhan = "CHA" va method ten(). Trong MountainBike khai bao
        //   LAI field nhan = "CON" va ghi de ten(). Voi bien kieu Bicycle tro
        //   toi object MountainBike, in ca field lan method. Giai thich tai
        //   sao hai ket qua di theo hai huong khac nhau.

        // TODO 3 (muc 7): viet trong Bicycle mot method gioiThieu() in ra ca
        //   nhan va ten(). Goi no tren object MountainBike. Doc ket qua roi
        //   tu tra loi: dong code nam o class cha, vay no lay field cua ai va
        //   goi method cua ai?

        // TODO 4 (muc 8): xoa dong super(...) trong constructor cua
        //   MountainBike de doc loi cua javac. Doc ky phan "required / found"
        //   va giai thich vi sao loi lai tro vao constructor cua CON.

        // TODO 5 (muc 8, JDK 25): viet constructor cho MountainBike co validate
        //   doi so TRUOC khi goi super(...), nem IllegalArgumentException neu
        //   seatHeight am. Day la tinh nang moi cua JDK 25.

        // TODO 6 (muc 5): tao mot Bicycle thuan tuy roi ep kieu xuong
        //   MountainBike de bat ClassCastException va in message. Sau do viet
        //   lai bang "instanceof MountainBike m" cho an toan.

        // TODO 7 (muc 6): thu "extends" hai class cung luc de doc loi. Roi
        //   tao hai interface cung co mot default method trung ten va cho mot
        //   class implement ca hai - doc loi "inherits unrelated defaults".

        // TODO 8 (muc 10): thu bon thu javac chan: thu hep access khi ghi de,
        //   ghi de method final, ke thua class final, va ghi de method private
        //   cua cha. Luu lai bon thong bao loi - sau nay gap la nhan ra ngay.
    }
}
