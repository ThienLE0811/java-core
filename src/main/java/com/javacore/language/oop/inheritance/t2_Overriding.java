package com.javacore.language.oop.inheritance;

/**
 * Overriding and Hiding Methods
 * <p>
 * 0. Vị trí bài này
 * - Trang 2/5 của section Inheritance. t1 đã nói LUẬT NỀN của kế thừa và
 * đã chạm vào chuyện che/ghi đè ở mục 7 và mục 10 - trang này đi sâu,
 * đặc biệt là phần interface và bảng tổng kết. Chỗ nào t1 đã chứng minh
 * rồi thì ở đây chỉ trỏ ngược, không chép lại.
 * - Một câu cho cả bài: cùng một chữ ký method ở cha và con có thể dẫn
 * tới BA kết cục khác hẳn nhau - GHI ĐÈ (override), CHE (hide), hoặc
 * LỖI BIÊN DỊCH - và cái quyết định là instance hay static.
 * - Mọi kết quả dán trong ghi chú này đều chạy thật trên JDK 25 của repo.
 * <p>
 * ======================================================================
 * TRANG GỐC - OVERRIDING AND HIDING METHODS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. INSTANCE METHOD - GHI ĐÈ
 * - Định nghĩa nguyên văn: "An instance method in a subclass with the
 * SAME SIGNATURE (name, plus the number and the type of its parameters)
 * and RETURN TYPE as an instance method in the superclass OVERRIDES the
 * superclass's method".
 * - Chữ ký = tên + danh sách kiểu tham số. KHÔNG tính kiểu trả về, KHÔNG
 * tính tên tham số. Sai một kiểu tham số là thành method khác hẳn -
 * xem mục 6 và mục 7.
 * - Ghi đè xong thì bản của con LUÔN được gọi, bất kể biến đang khai báo
 * kiểu gì. Đó là dynamic dispatch, đã chứng minh ở t1 mục 7.
 * <p>
 * 2. COVARIANT RETURN TYPE VÀ ANNOTATION OVERRIDE
 * - "An overriding method can also return a SUBTYPE of the type returned
 * by the overridden method. This subtype is called a COVARIANT RETURN
 * TYPE".
 * - Tức là kiểu trả về được phép HẸP hơn, không được rộng hơn. Cha trả
 * Object thì con trả String - đã thử, biên dịch sạch. Hợp lý: ai đang
 * cầm biến kiểu cha vẫn nhận được thứ họ mong đợi.
 * - Annotation Override "instructs the compiler that you intend to
 * override a method in the superclass". Nó KHÔNG đổi hành vi, chỉ bắt
 * lỗi ý định. Đã chạy thật, gõ nhầm toString thành toSting:
 * error: method does not override or implement a method from a supertype
 * Không có annotation đó thì code vẫn biên dịch, chỉ là method mình
 * viết chẳng bao giờ được gọi. Luôn luôn viết nó.
 * <p>
 * 3. STATIC METHOD - CHE, KHÔNG PHẢI GHI ĐÈ
 * - "If a subclass defines a static method with the same signature as a
 * static method in the superclass, then the method in the subclass
 * HIDES the one in the superclass".
 * - Câu phân biệt quan trọng nhất của cả trang: "The version of the
 * OVERRIDDEN INSTANCE method that gets invoked is the one IN THE
 * SUBCLASS. The version of the HIDDEN STATIC method that gets invoked
 * depends on WHETHER IT IS INVOKED FROM THE SUPERCLASS OR THE
 * SUBCLASS".
 * - Ví dụ Animal/Cat của trang gốc, chạy thật trên repo:
 * Animal.testClassMethod()      -&gt; static method in Animal
 * myAnimal.testInstanceMethod() -&gt; instance method in Cat
 * myAnimal.testClassMethod()    -&gt; static method in Animal
 * Cat.testClassMethod()         -&gt; static method in Cat
 * với myAnimal và myCat là CÙNG MỘT object.
 * - Dòng thứ ba là chỗ đau: gọi static qua một biến, Java lờ object đi
 * và chỉ nhìn KIỂU của biến. Đừng bao giờ gọi static method qua biến
 * instance - viết thẳng TenClass.method() cho khỏi hiểu nhầm.
 * - Nhớ gọn: instance method hỏi "object này thật ra là gì", static
 * method hỏi "chỗ này đang khai báo kiểu gì".
 * - Muốn thấy tận mắt thì javap -c dòng myAnimal.testClassMethod():
 * aload_2        &lt;- nạp object lên
 * pop            &lt;- rồi VỨT ĐI ngay
 * invokestatic   Animal.testClassMethod
 * Java vứt object đi trước khi gọi. Còn instance method thì dịch ra
 * invokevirtual, nghĩa là "lúc chạy hãy nhìn object thật mà chọn".
 * - Đọc một lần cho biết rồi thôi: CHE static KHÔNG phải kỹ thuật để
 * dùng, nó chỉ là thứ cần nhận ra khi đọc code người khác. Overriding
 * thật sự chỉ là chuyện của instance method. Cứ gọi static bằng tên
 * class là cả mục này không bao giờ làm phiền mình.
 * <p>
 * 4. INTERFACE METHOD - hai nguyên tắc và cách gỡ xung đột
 * - Nguyên tắc 1: "Instance methods are preferred over interface DEFAULT
 * methods". Method thừa kế từ CLASS luôn thắng default của interface.
 * Đã chạy thật: class Base có ten() trả "Base(class)", đồng thời
 * implement hai interface có default ten() - kết quả in ra
 * Base(class).
 * - Nguyên tắc 2: "Methods that are already overridden by other
 * candidates are ignored". Hệ quả: interface CON ghi đè default của
 * interface CHA thì không còn xung đột. Đã thử
 * interface Q extends P và cùng implement P, Q - biên dịch sạch, bản
 * cụ thể nhất thắng.
 * - Khi hai nhánh ĐỘC LẬP cùng có default trùng tên thì compiler không
 * chọn hộ. Đã chạy (t1 mục 6):
 * error: class D1 inherits unrelated defaults for hi() from types I1 and I2
 * Cách gỡ: tự override rồi chỉ đích danh muốn gọi ai, bằng cú pháp
 * TenInterface.super.method(). Đã chạy thật:
 * public String hi() { return I1.super.hi() + "+" + I2.super.hi(); }
 * -&gt; in ra I1+I2
 * - Default đụng ABSTRACT cũng là lỗi, không phải "cái có thân thì
 * thắng". Đã chạy với U có default ten() và V có abstract ten():
 * error: W is not abstract and does not override abstract method ten() in V
 * - "Static methods in interfaces are NEVER inherited". Đã chạy: class K
 * implements I rồi gọi K.s() -&gt;
 * error: cannot find symbol ... location: class K
 * Phải gọi I.s(). Khác hẳn static của class (mục 3) - static của
 * interface không hề có mặt trong class cài đặt.
 * - "Inherited instance methods from classes can override abstract
 * interface methods" - method có sẵn từ cha có thể đứng ra cài đặt hộ
 * một abstract method của interface.
 * <p>
 * 5. MODIFIERS - nới được, thu thì không
 * - "The access specifier for an overriding method can allow MORE, but
 * NOT LESS, access than the overridden method". protected lên public
 * thì được, public xuống protected thì không. Đã chạy (t1 mục 10):
 * attempting to assign weaker access privileges; was public
 * - Đây là Liskov được compiler cưỡng chế: có người đang cầm biến kiểu
 * cha và gọi method đó, con không có quyền khóa cửa lại.
 * - "You will get a compile-time error if you attempt to change an
 * INSTANCE method in the superclass to a STATIC method in the subclass,
 * and vice versa". Hai chiều cho hai thông báo khác nhau, đã chạy thật:
 * cha static, con instance -&gt; error: f() in B cannot override f() in A
 * overridden method is static
 * cha instance, con static -&gt; error: g() in B2 cannot override g() in A2
 * overriding method is static
 * <p>
 * 6. BẢNG TỔNG KẾT - học thuộc bảng này là xong trang
 * - Cột là thành viên của CHA, hàng là thành viên của CON:
 * <p>
 * cha: instance      cha: static
 * con: instance     GHI ĐÈ            lỗi biên dịch
 * con: static       lỗi biên dịch     CHE
 * <p>
 * - Đọc theo đường chéo: cùng loại thì có chuyện xảy ra, khác loại thì
 * hỏng. Và hai ô có chuyện xảy ra lại hành xử trái ngược nhau (mục 3).
 * - Thêm một ô thứ ba trang gốc nhắc riêng: "In a subclass, you can
 * OVERLOAD the methods inherited from the superclass. Such overloaded
 * methods neither hide nor override the superclass instance methods -
 * they are NEW METHODS, unique to the subclass".
 * - Nghĩa là đổi danh sách tham số thì rơi ra khỏi bảng, thành method
 * hoàn toàn mới. Nghe vô hại, nhưng đó là mục 7.
 * <p>
 * ======================================================================
 * NGOÀI TRANG GỐC - NHỮNG CHỖ THỰC SỰ GÂY LỖI
 * ======================================================================
 * <p>
 * 7. BẪY KINH ĐIỂN NHẤT: equals(MyType) thay vì equals(Object)
 * - Object khai báo equals(Object). Viết equals(Sai khac) là OVERLOAD -
 * một method mới toanh - chứ không phải ghi đè. Code biên dịch sạch,
 * gọi trực tiếp vẫn đúng, nên không ai nghi ngờ gì.
 * - Nhưng mọi thư viện đều gọi qua kiểu Object. Đã chạy thật:
 * s1.equals(s2)          -&gt; true     (compiler chọn bản overload)
 * s1.equals((Object) s2) -&gt; false    (chạy bản mặc định của Object)
 * List.of(s1).contains(s2) -&gt; false
 * HashSet thêm hai object giống nhau -&gt; size = 2
 * Cùng dữ liệu, bản viết đúng equals(Object) cho contains = true và
 * size = 1.
 * - Triệu chứng ngoài đời: cache không bao giờ trúng, danh sách có bản
 * ghi trùng, distinct() không lọc được gì. Không exception, không log.
 * - Phòng bệnh: viết annotation Override lên equals. Sai chữ ký là
 * compiler chặn ngay. Và nhớ luật cặp đôi - override equals thì phải
 * override hashCode.
 * - Liên hệ bước 9: record cho sẵn cả hai, đúng chữ ký, miễn nhiễm với
 * bẫy này. Đây là một lý do rất thực tế để ưu tiên record cho DTO.
 * <p>
 * 8. CHECKED EXCEPTION CHỈ ĐƯỢC THU HẸP
 * - Trang gốc không nói, nhưng cùng một tinh thần với mục 5: method ghi
 * đè không được ném checked exception RỘNG HƠN bản của cha. Đã chạy:
 * cha throws IOException, con throws Exception -&gt;
 * error: h() in B3 cannot override h() in A3
 * overridden method does not throw Exception
 * - Được phép: ném hẹp hơn, hoặc không ném gì cả. Lý do vẫn là Liskov -
 * người gọi đang bắt IOException, con không được bắt họ xử thêm việc.
 * - RuntimeException thì không bị ràng buộc, muốn ném gì cũng được.
 * <p>
 * 9. ĐỪNG GỌI METHOD CÓ THỂ BỊ GHI ĐÈ TỪ TRONG CONSTRUCTOR
 * - Constructor của cha chạy TRƯỚC khi field của con được gán. Nếu nó
 * gọi một method mà con đã ghi đè, bản của con sẽ chạy trên một object
 * chưa dựng xong. Đã chạy thật:
 * ctor cha goi : ConXau nhan=DA-GAN so=0
 * ket qua cuoi : ConXau nhan=DA-GAN so=7
 * Cùng một method, cùng một object, hai kết quả khác nhau - vì lần đầu
 * field so còn là 0.
 * - Chi tiết làm bug này càng khó đoán: nhan ra đúng ngay lần đầu vì nó
 * là hằng chuỗi biên dịch được, javac nhúng thẳng giá trị vào. Field
 * "bình thường" thì không. Nên object trông như đã dựng xong một nửa.
 * - Luật an toàn: trong constructor chỉ gọi method private, static hoặc
 * final. Ba loại đó không ai ghi đè được.
 * <p>
 * 10. GÓC NHÌN PRODUCTION
 * - Ghi đè là KẾ THỪA HỢP ĐỒNG, không chỉ kế thừa chữ ký. Bản của con
 * phải làm đúng thứ javadoc của cha đã hứa: không siết thêm điều kiện
 * đầu vào, không trả về ít hơn lời hứa. Compiler chỉ cưỡng chế được
 * phần kiểu, phần ngữ nghĩa là trách nhiệm của người viết.
 * - Khi ghi đè, tự hỏi có cần gọi super.method() không. Bỏ quên lời gọi
 * đó là cách phổ biến nhất làm hỏng class cha - hay gặp khi ghi đè các
 * method vòng đời hoặc các hook của framework.
 * - Trong Spring: ghi đè method của một bean được proxy (Transactional,
 * Cacheable, Async) rất dễ làm mất annotation nếu con không khai lại.
 * Và gọi một method Transactional từ bên trong cùng class thì proxy
 * không chen vào được - đây là bug "transaction không chạy" kinh điển,
 * sẽ gặp lại khi học Spring.
 * - Mặc định nên đánh dấu class hoặc method là final nếu không có ý định
 * cho ghi đè. Mở ra thì dễ, đóng lại thì không bao giờ đóng được nữa.
 */
public class t2_Overriding {

    /**
     * Ví dụ Animal/Cat của trang gốc - giữ nguyên để thấy đủ bốn dòng output.
     */
    public static class Animal {
        public static void testClassMethod() {
            IO.println("static method in Animal");
        }

        public void testInstanceMethod() {
            IO.println("instance method in Animal");
        }
    }

    public static class Cat extends Animal {
        public static void testClassMethod() {
            IO.println("static method in Cat");
        }

        @Override
        public void testInstanceMethod() {
            IO.println("instance method in Cat");
        }
    }

    void main() {
        // ============ PHAN CHINH: GHI DE INSTANCE METHOD ============
        // Day la toan bo y nghia cua overriding. Nam hai dong duoi la du.
        Cat myCat = new Cat();
        Animal myAnimal = myCat;            // CUNG MOT object, chi khac kieu bien

        myCat.testInstanceMethod();         // instance method in Cat
        myAnimal.testInstanceMethod();      // instance method in Cat  <- VAN la ban cua Cat

        // Bien khai bao kieu CHA, nhung ban cua CON chay. Nguoi viet class
        // Animal khong he biet Cat ton tai, vay ma code cua ho goi dung vao
        // Cat. Do la da hinh - noi dung chinh cua trang sau (t3).

        // TODO 1 (muc 1, 2): cho Animal mot method moTa() tra ve Object, con
        //   Cat ghi de tra ve String. Xac nhan bien dich duoc (covariant
        //   return). Sau do thu nguoc lai: cha tra String, con tra Object -
        //   doc loi.

        // TODO 2 (muc 2, 7): viet mot class co equals(TenClass khac) thay vi
        //   equals(Object). Goi truc tiep s1.equals(s2), roi goi lai sau khi
        //   ep ve Object, roi thu List.contains va HashSet. Them annotation
        //   Override vao de xem compiler bat loi nhu the nao.

        // TODO 3 (muc 4): viet hai interface co cung mot default method trung
        //   ten, cho mot class implement ca hai de doc loi "inherits unrelated
        //   defaults". Sau do gia bang cach override va goi
        //   TenInterface.super.method() cho CA HAI ban.

        // TODO 4 (muc 4): them mot class cha co instance method trung ten voi
        //   default method do, roi xac nhan ban cua CLASS thang. Doi chieu voi
        //   nguyen tac 1.

        // TODO 5 (muc 5, 6): thu ca hai chieu instance <-> static giua cha va
        //   con de lay hai thong bao loi khac nhau. Ve lai bang tong ket o muc
        //   6 ra giay ma khong nhin note.

        // TODO 6 (muc 8): cha co method throws IOException, con ghi de va
        //   throws Exception -> doc loi. Sau do doi con thanh khong throws gi
        //   ca va xac nhan van bien dich duoc.

        // TODO 7 (muc 9): viet constructor cua cha goi mot method ma con se
        //   ghi de. Trong con de mot field int gan trong constructor. In gia
        //   tri field do ca luc constructor cha chay lan sau khi tao xong.
        //   Giai thich con so 0.

        // ============ GHI CHU BEN LE: STATIC KHONG PHAI OVERRIDING ============
        // Phan nay KHONG phai ky thuat de dung, chi de khong bi bat ngo khi doc
        // code nguoi khac. Lam sau cung, sau khi da xong 7 TODO tren.

        // TODO 8 (muc 3): goi Animal.testClassMethod() va Cat.testClassMethod()
        //   - ro rang, de hieu. Sau do them dong myAnimal.testClassMethod() va
        //   DOAN ket qua truoc khi chay.
        //   Goi y: static thuoc ve CLASS chu khong thuoc ve object, nen Java
        //   chi nhin KIEU CUA BIEN (Animal) va bo qua object that (Cat).
        //   Muon thay tan mat: javap -c tren class nay, tim dong invokestatic -
        //   se thay bytecode nap object len roi "pop" vut di ngay truoc khi goi.
        //   Bai hoc rut ra: luon goi static bang TEN CLASS, dung goi qua bien.
    }
}
