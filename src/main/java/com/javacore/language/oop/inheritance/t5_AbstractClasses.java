package com.javacore.language.oop.inheritance;

import java.util.List;

/**
 * Abstract Methods and Classes
 * <p>
 * 0. Vị trí bài này
 * - Trang 5/5, đóng section Inheritance. Đây là nơi gom lại: kế thừa
 * (t1), ghi đè (t2), đa hình (t3), hợp đồng của Object (t4) - abstract
 * class là công cụ dùng cả bốn thứ đó cùng lúc.
 * - Một câu cho cả bài: abstract class là class CHƯA HOÀN CHỈNH - nó mô
 * tả phần chung và CỐ Ý để trống vài chỗ, bắt class con phải điền.
 * - Câu hỏi thật sự của trang này không phải "abstract class là gì" mà
 * là "khi nào dùng abstract class, khi nào dùng interface" - mục 3.
 * - Mọi kết quả dán trong ghi chú này đều chạy thật trên JDK 25 của repo.
 * <p>
 * ======================================================================
 * TRANG GỐC - ABSTRACT METHODS AND CLASSES (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA VÀ LUẬT CƠ BẢN
 * - "Abstract classes CANNOT BE INSTANTIATED, but they CAN BE
 * SUBCLASSED". Đã chạy thật:
 * error: G is abstract; cannot be instantiated
 * - Abstract method là "a method that is declared WITHOUT an
 * implementation (without braces, and followed by a semicolon)":
 * abstract void draw();
 * - Có abstract method thì CLASS cũng phải abstract. Đã chạy, javac báo
 * bằng một thông báo thoạt nhìn khó hiểu vì nó nói class không ghi đè
 * chính method của mình:
 * error: G2 is not abstract and does not override abstract method ve() in G2
 * - Chiều ngược lại thì được: class abstract KHÔNG BẮT BUỘC phải có
 * abstract method nào. Khai abstract chỉ để cấm tạo object trực tiếp
 * cũng là cách dùng hợp lệ.
 * <p>
 * 2. LUẬT CHO CLASS CON
 * - "When an abstract class is subclassed, the subclass USUALLY PROVIDES
 * IMPLEMENTATIONS FOR ALL of the abstract methods in its parent class.
 * However, if it does not, then the subclass MUST ALSO BE DECLARED
 * ABSTRACT".
 * - Nghĩa là nợ có thể truyền xuống đời sau, nhưng đến class cụ thể đầu
 * tiên thì phải trả hết. Đã chạy, con cài một method và bỏ sót một:
 * error: Tron is not abstract and does not override abstract method doiCo() in G3
 * - Đây là điểm mạnh nhất của abstract class so với việc tự nhớ: compiler
 * KHÔNG CHO QUÊN. Thêm một abstract method vào cha là mọi class con
 * đồng loạt hỏng biên dịch cho tới khi được cập nhật.
 * <p>
 * 3. ABSTRACT CLASS SO VỚI INTERFACE - phần quan trọng nhất
 * - Khác biệt theo trang gốc:
 * Abstract class: khai báo được field KHÔNG static và KHÔNG final;
 * method có đủ public/protected/private; chỉ extends được MỘT class.
 * Interface: mọi field mặc định là public static final; mọi method
 * khai báo đều là public; implement được bao nhiêu cũng được.
 * - Đã chạy thử field của interface:
 * error: cannot assign a value to static final variable x
 * Field trong interface là HẰNG SỐ dùng chung, không phải trạng thái
 * của từng object. Đây là khác biệt cốt lõi.
 * - DÙNG ABSTRACT CLASS KHI (nguyên văn):
 * "You want to SHARE CODE among several CLOSELY RELATED classes"
 * "You expect that classes that extend your abstract class have MANY
 * COMMON METHODS OR FIELDS"
 * "You want to declare NON-STATIC or NON-FINAL FIELDS"
 * - DÙNG INTERFACE KHI (nguyên văn):
 * "You expect that UNRELATED classes would implement your interface"
 * "You want to specify the BEHAVIOR of a particular data type, but not
 * concerned about WHO IMPLEMENTS its behavior"
 * "You want to take advantage of MULTIPLE INHERITANCE OF TYPE"
 * - Rút lại thành hai câu hỏi: các class này có HỌ HÀNG GẦN không, và có
 * cần CHIA SẺ TRẠNG THÁI không? Hai lần có thì abstract class. Còn chỉ
 * cần thống nhất một bộ hành vi cho những class chẳng liên quan gì tới
 * nhau thì interface.
 * <p>
 * 4. ABSTRACT CLASS CÀI ĐẶT INTERFACE MỘT PHẦN
 * - Một class được phép implements interface mà KHÔNG cài hết method,
 * miễn là nó tự khai abstract. Phần còn nợ để lại cho class con.
 * - Đã chạy, biên dịch sạch:
 * interface I { void a(); void b(); }
 * abstract class Nua implements I { public void a() {} }
 * class Du extends Nua { public void b() {} }
 * - Đây là khuôn mẫu rất phổ biến trong thư viện: interface định nghĩa
 * hợp đồng đầy đủ, một abstract class làm sẵn phần nhàm chán, người
 * dùng chỉ cần kế thừa và điền phần riêng. Tên thường thấy là
 * AbstractXxx - trong thư viện chuẩn có AbstractList, AbstractMap.
 * <p>
 * 5. THÀNH VIÊN CỦA ABSTRACT CLASS
 * - "An abstract class MAY HAVE STATIC FIELDS AND STATIC METHODS. You can
 * use these static members with a class reference (for example,
 * AbstractClass.staticMethod()) as you would with any other class".
 * - Và nó cũng có CONSTRUCTOR, dù không ai new nó trực tiếp được -
 * constructor đó chạy khi class con gọi super(...). Đúng bài t1 mục 8.
 * - Ví dụ GraphicObject của trang gốc, chạy thật trên repo:
 * ve hinh tron tai 0,0
 * Circle(0,0) S=12.6
 * ve hinh chu nhat 3x4
 * Rectangle(1,1) S=12.0
 * GraphicObject.demDaTao() = 2
 * Abstract class này có: field x, y dùng chung; constructor; một static
 * field đếm số vật thể; method moveTo có thân; toString ghi đè; và hai
 * method abstract để trống cho con.
 * - Nhìn danh sách đó là thấy ngay vì sao interface không thay thế được:
 * x, y là TRẠNG THÁI của từng object, interface không giữ được.
 * <p>
 * ======================================================================
 * NGOÀI TRANG GỐC - NHỮNG CHỖ THỰC SỰ GÂY LỖI
 * ======================================================================
 * <p>
 * 6. BA TỔ HỢP MODIFIER BỊ CẤM
 * - abstract đi với private, static, hoặc final đều vô nghĩa, và javac
 * nói thẳng. Đã chạy cả bốn:
 * abstract private -&gt; error: illegal combination of modifiers: abstract and private
 * abstract static  -&gt; error: illegal combination of modifiers: abstract and static
 * abstract final   -&gt; error: illegal combination of modifiers: abstract and final
 * class abstract final -&gt; cùng thông báo đó
 * - Lý do chung: abstract nghĩa là "sẽ có người khác viết tiếp", còn
 * private nghĩa là "không ai thấy", static nghĩa là "không đa hình",
 * final nghĩa là "cấm viết tiếp". Mâu thuẫn trực tiếp.
 * <p>
 * 7. ANONYMOUS CLASS - class con vô danh tạo ngay tại chỗ
 * - Không new được abstract class, nhưng được phép vừa khai báo một class
 * con vô danh vừa tạo object của nó:
 * GraphicObject tam = new GraphicObject(9, 9) {
 * void draw() { ... }
 * double dienTich() { return 0; }
 * };
 * - Đã chạy thật. Chú ý getClass() của nó:
 * getClass = D$1
 * và getSimpleName() trả về chuỗi RỖNG - đúng nghĩa vô danh. Nối với
 * bài nested classes ở bước 8: compiler sinh ra một class thật tên
 * Ngoai$1.
 * - Đây là cách hiện thực nhanh một hợp đồng chỉ dùng một lần. Ngày nay
 * nếu interface chỉ có đúng một method thì dùng lambda gọn hơn, nhưng
 * với abstract class thì vẫn phải viết kiểu này.
 * <p>
 * 8. TỪ JAVA 8, RANH GIỚI ĐÃ MỜ ĐI
 * - Trước Java 8, interface không có thân method nào, nên chọn lựa rất
 * rõ. Từ khi có default method (t2 mục 4), interface cũng chia sẻ được
 * CODE.
 * - Vậy còn lại gì chỉ abstract class làm được?
 * (1) TRẠNG THÁI - field riêng của từng object, đây là khác biệt lớn
 * nhất và không thể thay thế;
 * (2) CONSTRUCTOR - bắt buộc dữ liệu đầu vào đúng ngay lúc tạo;
 * (3) method protected hoặc private - interface không có phần "chỉ
 * dành cho họ hàng";
 * (4) ĐẢM BẢO chỉ một cha, nên không có chuyện xung đột default.
 * - Ngược lại, chỉ interface làm được: một class mang NHIỀU VAI. Và đây
 * là lý do lời khuyên mặc định vẫn là ưu tiên interface - nó không
 * tiêu mất suất kế thừa duy nhất của class con.
 * - Mẹo quyết định nhanh: cần giữ DỮ LIỆU thì abstract class; chỉ cần mô
 * tả HÀNH VI thì interface. Nếu phân vân, dùng interface - về sau thêm
 * một abstract class cài đặt interface đó vẫn kịp, còn gỡ kế thừa ra
 * thì khó hơn nhiều.
 * <p>
 * 9. TEMPLATE METHOD - lý do tồn tại phổ biến nhất của abstract class
 * - Đã làm ở t3 mục 7: cha viết TRÌNH TỰ bằng một method final, để lại
 * vài lỗ trống protected abstract cho con điền.
 * - Abstract class là chỗ tự nhiên nhất cho khuôn mẫu này vì nó có đủ ba
 * thứ: method final để khóa trình tự, method protected để chỉ mở cho
 * con, và field để giữ trạng thái chung.
 * - Rất nhiều framework hoạt động như vậy. Những method mình ghi đè khi
 * dùng thư viện thường chính là các lỗ trống đó.
 * <p>
 * 10. GÓC NHÌN PRODUCTION
 * - Trong Spring, abstract class xuất hiện ít hơn ta tưởng. Service nên
 * phụ thuộc interface rồi tiêm phụ thuộc, không nên dựng cây kế thừa.
 * Dùng chung code thì tách ra một class riêng rồi tiêm vào - đó là
 * composition, dễ test hơn nhiều.
 * - Chỗ abstract class thật sự hợp lý: lớp cơ sở cho entity (thường đi
 * với annotation MappedSuperclass, mang id, createdAt, updatedAt), cây
 * exception của ứng dụng, và các lớp AbstractXxx làm template method.
 * - Nhớ từ t2 mục 9: constructor của abstract class ĐỪNG gọi abstract
 * method. Nó sẽ chạy bản của con trên object chưa dựng xong.
 * - Khi tập các loại là CỐ ĐỊNH và biết trước, cân nhắc sealed thay cho
 * abstract class mở: sealed interface cộng record cho phép switch
 * pattern matching và compiler bắt lỗi thiếu nhánh. Abstract class hợp
 * khi tập còn MỞ cho người khác mở rộng; sealed hợp khi mình muốn ĐÓNG
 * danh sách lại.
 * <p>
 * 11. TỔNG KẾT CẢ SECTION INHERITANCE
 * - t1: kế thừa cho dùng lại code, nhưng field và static KHÔNG đa hình.
 * - t2: cùng chữ ký thì instance là GHI ĐÈ, static là CHE, lệch loại là
 * lỗi biên dịch.
 * - t3: một lệnh invokevirtual, nhiều hành vi - chỗ gọi không cần biết
 * có bao nhiêu loại.
 * - t4: Object cho sẵn sáu method, nhưng ghi đè equals là ký một hợp
 * đồng mà compiler không kiểm tra được.
 * - t5: abstract class để chia sẻ TRẠNG THÁI và ép con điền chỗ trống;
 * interface để mô tả HÀNH VI cho những class không họ hàng.
 * - Và câu xuyên suốt cả năm bài: kế thừa là công cụ mạnh nhưng ràng
 * buộc chặt. Mặc định hãy dùng composition và interface; chỉ dùng
 * extends khi quan hệ đúng là "cái này LÀ MỘT loại của cái kia".
 */
public class t5_AbstractClasses {

    /**
     * Ví dụ GraphicObject của trang gốc - có đủ field, constructor,
     * static member, method có thân và method abstract.
     */
    public abstract static class GraphicObject {
        protected int x;
        protected int y;
        private static int soVatTheDaTao = 0;

        protected GraphicObject(int x, int y) {   // con goi qua super(...)
            this.x = x;
            this.y = y;
            soVatTheDaTao++;
        }

        public void moveTo(int newX, int newY) {  // method CO than, dung chung
            this.x = newX;
            this.y = newY;
        }

        public abstract void draw();              // de trong cho con

        public abstract double dienTich();

        public static int demDaTao() {            // static method
            return soVatTheDaTao;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + x + "," + y + ")"
                    + " S=" + String.format("%.1f", dienTich());
        }
    }

    public static class Circle extends GraphicObject {
        private final double r;

        public Circle(int x, int y, double r) {
            super(x, y);
            this.r = r;
        }

        @Override
        public void draw() {
            IO.println("ve hinh tron tai " + x + "," + y);
        }

        @Override
        public double dienTich() {
            return Math.PI * r * r;
        }
    }

    public static class Rectangle extends GraphicObject {
        private final int w;
        private final int h;

        public Rectangle(int x, int y, int w, int h) {
            super(x, y);
            this.w = w;
            this.h = h;
        }

        @Override
        public void draw() {
            IO.println("ve hinh chu nhat " + w + "x" + h);
        }

        @Override
        public double dienTich() {
            return w * h;
        }
    }

    void main() {
        List<GraphicObject> danhSach = List.of(
                new Circle(0, 0, 2),
                new Rectangle(1, 1, 3, 4));

        for (GraphicObject g : danhSach) {
            g.draw();                 // da hinh: moi loai ve mot kieu
            IO.println("   " + g);    // toString o CHA dung dienTich() cua CON
        }
        IO.println("GraphicObject.demDaTao() = " + GraphicObject.demDaTao());

        // TODO 1 (muc 1): thu "new GraphicObject(0, 0)" de doc loi. Sau do bo
        //   chu abstract o class GraphicObject va doc loi thu hai. Hai thong
        //   bao nay se gap lai rat nhieu lan.

        // TODO 2 (muc 2): them mot abstract method chuVi() vao GraphicObject.
        //   Build ngay khi CHUA cai dat o Circle va Rectangle de thay compiler
        //   bat ca hai class con cung luc. Day la gia tri lon nhat cua abstract
        //   class - khong cho quen.

        // TODO 3 (muc 2): tao class Square extends Rectangle nhung CHI cai mot
        //   phan, roi khai no abstract. Xac nhan bien dich duoc, va chi hong
        //   khi tao them mot class CU THE ke thua no ma van thieu.

        // TODO 4 (muc 5): them mot field dem rieng cho tung object va mot
        //   static field dem chung. Tao ba object roi in ca hai. Giai thich vi
        //   sao interface khong thay the duoc field thu nhat.

        // TODO 5 (muc 7): tao mot GraphicObject bang anonymous class ngay trong
        //   main. In getClass().getName() va getSimpleName(). Giai thich chuoi
        //   rong va con so sau dau $.

        // TODO 6 (muc 3, 4): viet interface CoTheVe { void draw(); } va cho
        //   GraphicObject implements no ma KHONG cai draw(). Xac nhan van bien
        //   dich duoc nho abstract. Sau do liet ke ra giay: cai gi chi abstract
        //   class lam duoc, cai gi chi interface lam duoc.

        // TODO 7 (muc 9): viet abstract class XuatFile co method final xuat()
        //   goi hai method protected abstract. Tao hai class con. Thu goi mot
        //   abstract method tu trong CONSTRUCTOR cua cha va xem dieu gi xay ra
        //   (doi chieu t2 muc 9).

        // TODO 8 (muc 10): viet lai ba class tren bang sealed interface cong
        //   record cong switch pattern matching. Dat canh nhau va tra loi:
        //   them mot hinh moi thi ban nao de hon, va doc logic tinh dien tich
        //   thi ban nao de hon?
    }
}
