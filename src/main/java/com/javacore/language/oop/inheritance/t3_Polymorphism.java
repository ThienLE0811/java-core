package com.javacore.language.oop.inheritance;

import java.util.List;

/**
 * Polymorphism
 * <p>
 * 0. Vị trí bài này
 * - Trang 3/5 của section Inheritance, và là PHẦN THƯỞNG của hai trang
 * trước. t2 nói LUẬT chọn method; t3 nói HỆ QUẢ: vì sao người ta chịu
 * đựng kế thừa.
 * - Một câu cho cả bài: viết MỘT dòng gọi method, chạy ra NHIỀU hành vi
 * khác nhau, và code gọi không cần biết có bao nhiêu loại. Đã chứng
 * minh bằng bytecode ở mục 3 - đúng một lệnh invokevirtual cho ba hành
 * vi khác nhau.
 * - Mọi kết quả dán trong ghi chú này đều chạy thật trên JDK 25 của repo.
 * <p>
 * ======================================================================
 * TRANG GỐC - POLYMORPHISM (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. ĐA HÌNH LÀ GÌ
 * - Trang gốc mượn nghĩa sinh học: "a principle in biology in which an
 * organism or species can have many different FORMS or STAGES".
 * - Áp vào Java: "Subclasses of a class can define their OWN UNIQUE
 * BEHAVIORS and yet SHARE some of the same FUNCTIONALITY of the parent
 * class".
 * - Hai vế của câu đó là hai nửa của mọi thiết kế: phần GIỐNG NHAU nằm ở
 * cha (hoặc interface), phần KHÁC NHAU nằm ở con.
 * <p>
 * 2. VÍ DỤ BICYCLE - MOUNTAINBIKE - ROADBIKE
 * - Bicycle có printDescription() in cadence, speed, gear. MountainBike
 * thêm suspension, RoadBike thêm tireWidth; cả hai ghi đè
 * printDescription() và gọi super.printDescription() trước rồi in thêm
 * phần riêng.
 * - Đã chạy thật với một List&lt;Bicycle&gt; chứa đủ ba loại:
 * Bike: cadence 20, speed 10, gear 1.
 * Bike: cadence 20, speed 10, gear 5.
 * + suspension Dual.
 * Bike: cadence 40, speed 20, gear 8.
 * + tire width 23 mm.
 * - Chú ý cách super.printDescription() được dùng: con KHÔNG chép lại
 * phần chung, nó gọi cha làm phần chung rồi bổ sung phần riêng. Đây là
 * cách ghi đè đúng đắn nhất - mở rộng chứ không thay thế.
 * <p>
 * 3. VIRTUAL METHOD INVOCATION - câu quan trọng nhất của trang
 * - Nguyên văn: "The Java virtual machine (JVM) calls the appropriate
 * method FOR THE OBJECT that is referred to in each variable. It does
 * NOT call the method that is defined by the VARIABLE'S TYPE".
 * - Tên gọi chính thức của cơ chế này là virtual method invocation, hay
 * dynamic dispatch, hay late binding - ba tên một thứ.
 * - Đã chạy javap -c trên vòng lặp ở mục 2. Toàn bộ vòng lặp chỉ có ĐÚNG
 * MỘT lệnh gọi:
 * 72: invokevirtual  Method Bicycle.printDescription:()V
 * Một dòng bytecode, ba hành vi khác nhau. Chữ "virtual" nghĩa là đích
 * đến chưa chốt lúc biên dịch; JVM nhìn object thật rồi mới chọn.
 * - So với t2 mục 3: static dịch ra invokestatic, đích chốt cứng ngay
 * lúc biên dịch. Khác nhau đúng ở một chữ trong bytecode.
 * - Hệ quả lớn nhất, và là lý do tồn tại của cả bài: class Bicycle được
 * viết xong từ lâu, KHÔNG HỀ BIẾT RoadBike tồn tại, vậy mà code gọi qua
 * biến Bicycle vẫn chạy đúng bản của RoadBike. Thêm loại xe mới không
 * phải sửa một dòng nào ở chỗ gọi.
 * <p>
 * 4. FIELD THÌ KHÔNG CÓ ĐA HÌNH
 * - Trang gốc nhắc lại: "Within a class, a field that has the same name
 * as a field in the superclass HIDES the superclass's field, EVEN IF
 * THEIR TYPES ARE DIFFERENT".
 * - Vế "even if their types are different" nghe lạ nên đã thử thật: cha
 * có String nhan = "CHA", con có int nhan = 99. Biên dịch sạch, và:
 * asCha.nhan -&gt; CHA  (kiểu String)
 * c.nhan     -&gt; 99
 * ((Cha) c).nhan -&gt; CHA
 * Cùng một object, cùng một tên field, hai KIỂU dữ liệu khác nhau tùy
 * chỗ nhìn. Che field không quan tâm kiểu vì nó chỉ là chuyện tên.
 * - Truy cập bản của cha từ trong con thì dùng super.nhan, từ ngoài thì
 * ép kiểu. Nhưng kết luận thực dụng vẫn như t1 mục 7: ĐỪNG CHE FIELD.
 * - Nhớ gọn cho cả ba bài: METHOD đa hình, FIELD và STATIC thì không.
 * <p>
 * ======================================================================
 * NGOÀI TRANG GỐC - NHỮNG CHỖ THỰC SỰ GÂY LỖI
 * ======================================================================
 * <p>
 * 5. JAVA CHỈ CÓ SINGLE DISPATCH - bẫy tinh vi nhất
 * - Đa hình chỉ áp dụng cho OBJECT ĐỨNG TRƯỚC DẤU CHẤM, không áp dụng
 * cho ĐỐI SỐ. Chọn bản overload nào là quyết định lúc BIÊN DỊCH, dựa
 * trên kiểu khai báo của đối số.
 * - Đã chạy thật với hai overload xuLy(Object) và xuLy(String):
 * Object o = "toi la String that";
 * May.xuLy(o) -&gt; ban Object        &lt;- dù o.getClass() là String
 * May.xuLy(s) -&gt; ban String
 * Cùng một object, hai kết quả, chỉ vì kiểu khai báo của biến khác
 * nhau. Runtime không hề được hỏi ý kiến.
 * - Nhớ gọn: OVERRIDE chọn lúc CHẠY theo object nhận; OVERLOAD chọn lúc
 * BIÊN DỊCH theo kiểu đối số. Hai cơ chế trái ngược nhau mà tên gọi
 * tiếng Việt lại na ná - đây là nguồn nhầm lẫn kinh điển.
 * - Đây cũng là lý do visitor pattern tồn tại: khi cần hành vi phụ thuộc
 * CẢ HAI phía, phải tự dựng double dispatch bằng hai lần gọi. Ngày nay
 * sealed interface cộng switch pattern matching giải quyết gọn hơn.
 * <p>
 * 6. GIÁ TRỊ THẬT: THÊM LOẠI MỚI MÀ KHÔNG SỬA CODE CŨ
 * - Đa hình không phải để code "trông thông minh". Nó để chỗ GỌI không
 * phải biết danh sách các loại.
 * - Đã chạy thật với interface ThanhToan và hai cài đặt:
 * Momo  phi cho 100000 = 1000
 * The   phi cho 100000 = 4500
 * Vòng lặp in ra hoàn toàn không biết có bao nhiêu cách thanh toán.
 * Thêm ZaloPay chỉ là thêm một file, không đụng vào vòng lặp.
 * - Đối chiếu bản không có đa hình: một chuỗi if/else theo String ma, và
 * mỗi lần thêm loại là phải tìm cho hết mọi chỗ có chuỗi if đó. Quên
 * một chỗ là bug im lặng.
 * - Một chuỗi dài instanceof hoặc switch theo kiểu thường là dấu hiệu
 * thiếu đa hình. Nhưng không phải luôn luôn - xem mục 8.
 * <p>
 * 7. TEMPLATE METHOD - cha giữ khung, con điền chi tiết
 * - Dạng dùng đa hình phổ biến nhất trong code thật: cha viết TRÌNH TỰ
 * và để lại vài lỗ trống abstract cho con.
 * - Đã chạy thật:
 * mo | than PDF | dong
 * mo | than CSV | dong
 * với xuat() là method final ở cha, gọi than() là abstract.
 * - Hai chi tiết đáng học: method khung để FINAL để không ai phá trình
 * tự, và lỗ trống để protected abstract vì nó dành cho con chứ không
 * phải cho thế giới bên ngoài.
 * - Rất nhiều framework hoạt động theo kiểu này; những method mình ghi
 * đè khi dùng thư viện thường chính là các lỗ trống đó.
 * <p>
 * 8. GÓC NHÌN PRODUCTION
 * - Trong Spring, đa hình xuất hiện mỗi ngày dù mình không gọi tên nó:
 * khai báo một interface rồi inject List&lt;ThanhToan&gt; là Spring đưa
 * cho mình MỌI bean cài đặt interface đó. Thêm một cài đặt mới chỉ là
 * thêm một class có annotation Component - chỗ dùng không đổi.
 * - Service luôn nên phụ thuộc vào INTERFACE, không phụ thuộc class cụ
 * thể. Nhờ vậy test mới thay được bằng bản giả.
 * - Nhắc lại từ t2 mục 9: đừng gọi method có thể bị ghi đè từ trong
 * constructor. Đa hình chạy sớm hơn mình tưởng, trên object chưa dựng
 * xong.
 * - Khi nào ĐỪNG dùng đa hình kiểu kế thừa: khi tập các biến thể là CỐ
 * ĐỊNH và logic cần đọc tập trung một chỗ. Lúc đó sealed interface
 * cộng record cộng switch pattern matching tốt hơn - compiler bắt lỗi
 * thiếu nhánh, và toàn bộ luật nằm trong một màn hình thay vì rải ra
 * năm file. Kế thừa hợp khi tập các loại còn MỞ; sealed hợp khi ĐÓNG.
 * - Về hiệu năng: đừng lo. JIT nội tuyến được lời gọi ảo khi ở một chỗ
 * gọi chỉ thấy một hai kiểu thực tế. Viết cho dễ đọc trước đã.
 */
public class t3_Polymorphism {

    /**
     * Ví dụ của trang gốc, rút gọn phần constructor cho dễ đọc.
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

        public void printDescription() {
            IO.println("Bike: cadence " + cadence + ", speed " + speed + ", gear " + gear + ".");
        }
    }

    public static class MountainBike extends Bicycle {
        private final String suspension;

        public MountainBike(int cadence, int speed, int gear, String suspension) {
            super(cadence, speed, gear);
            this.suspension = suspension;
        }

        @Override
        public void printDescription() {
            super.printDescription();          // phan chung: de cha lam
            IO.println("   + suspension " + suspension + ".");
        }
    }

    public static class RoadBike extends Bicycle {
        private final int tireWidth;

        public RoadBike(int cadence, int speed, int gear, int tireWidth) {
            super(cadence, speed, gear);
            this.tireWidth = tireWidth;
        }

        @Override
        public void printDescription() {
            super.printDescription();
            IO.println("   + tire width " + tireWidth + " mm.");
        }
    }

    void main() {
        List<Bicycle> danhSach = List.of(
                new Bicycle(20, 10, 1),
                new MountainBike(20, 10, 5, "Dual"),
                new RoadBike(40, 20, 8, 23));

        for (Bicycle xe : danhSach) {
            xe.printDescription();     // MOT dong goi duy nhat -> BA hanh vi
        }

        // Vong lap tren khong he biet co bao nhieu loai xe. Them loai moi chi
        // la them mot class; dong code nay khong doi. Do la toan bo gia tri
        // cua da hinh.

        // TODO 1 (muc 2, 3): them class ElectricBike extends Bicycle, ghi de
        //   printDescription() de in them dung luong pin. Them no vao
        //   danhSach. Xac nhan KHONG phai sua dong goi nao o tren.

        // TODO 2 (muc 3): build xong, chay javap -c tren class nay va tim
        //   lenh invokevirtual trong main. Dem xem co bao nhieu lenh goi
        //   printDescription trong bytecode, doi chieu voi so hanh vi in ra.

        // TODO 3 (muc 4): cho Bicycle mot field String nhan = "CHA" va cho
        //   MountainBike mot field int nhan = 99 (CO Y de khac kieu). In
        //   nhan qua bien kieu Bicycle va qua bien kieu MountainBike. Giai
        //   thich vi sao bien dich duoc du hai kieu khac nhau.

        // TODO 4 (muc 5 - bai quan trong nhat ngoai trang goc): viet hai
        //   method overload xuLy(Object) va xuLy(String). Goi voi mot bien
        //   khai bao kieu Object nhung chua String. Doan ket qua truoc khi
        //   chay. In them o.getClass() de thay runtime biet ro no la String
        //   ma van khong duoc hoi y kien.

        // TODO 5 (muc 6): viet interface ThanhToan { double phi(double tien); }
        //   voi hai record cai dat. Duyet List<ThanhToan> va in phi. Sau do
        //   viet lai cung logic bang mot chuoi if/else theo String ma, roi tu
        //   so sanh: them loai thu ba thi ban nao phai sua nhieu hon?

        // TODO 6 (muc 7): viet abstract class BaoCao co method final xuat()
        //   goi mot method protected abstract than(). Tao hai class con. Thu
        //   bo chu final o xuat() va nghi xem dieu gi co the hong.

        // TODO 7 (muc 8): doi ba class xe o tren thanh mot sealed interface
        //   voi ba record, roi viet switch pattern matching tinh phi thue xe.
        //   Bo sot mot nhanh de xem compiler bao gi. Tu rut ra khi nao nen
        //   dung ke thua, khi nao nen dung sealed.
    }
}
