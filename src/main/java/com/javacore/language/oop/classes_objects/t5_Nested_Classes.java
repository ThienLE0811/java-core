package com.javacore.language.oop.classes_objects;

/**
 * Nested Classes
 * <p>
 * 0. Vị trí bài này
 * - Bốn bài trước nói về class đứng một mình. Bài này nói về class LỒNG
 * TRONG class khác, và câu hỏi xuyên suốt chỉ có một: cái bên trong được
 * phép chạm vào những gì của cái bên ngoài.
 * - File gộp HAI trang cuối của section classes-objects. Trang 1 là Nested
 * Classes, bản thân nó đã dài và gồm bốn phần nối nhau: Nested Classes,
 * Inner Class Example, Local Classes, Anonymous Classes - nằm ở mục 1 tới
 * 10. Trang 2 là When to Use Nested, Local, Anonymous Classes and Lambda
 * Expressions, rất ngắn, nằm ở mục 11.
 * - Trang 1 dạy BỐN THỨ LÀ GÌ, trang 2 trả lời KHI NÀO DÙNG CÁI NÀO. Thiếu
 * trang 2 thì học xong vẫn không biết chọn.
 * - Đây cũng là nền để học Lambda ở bước 14. Anonymous class chính là thứ
 * mà lambda sinh ra để thay thế.
 * <p>
 * ======================================================================
 * TRANG 1 - NESTED CLASSES (mục 1 đến 10)
 * ======================================================================
 * <p>
 * 1. HAI LOẠI, BỐN TÊN GỌI - đọc kỹ chỗ này vì tên rất dễ lẫn
 * - Nguyên văn: "Nested classes are divided into two categories: non-static
 * and static. Non-static nested classes are called INNER CLASSES. Nested
 * classes that are declared static are called STATIC NESTED CLASSES".
 * - Nói cách khác: NESTED CLASS là tên chung của cả hai. Có chữ static thì
 * gọi là static nested class, không có chữ static thì gọi là INNER CLASS.
 * Nhiều người gọi nhầm mọi class lồng nhau là "inner class".
 * - Nested class là một THÀNH VIÊN của class bao ngoài, nên dùng được cả
 * bốn mức truy cập: private, public, protected, package-private. Trang
 * nhắc lại để đối chiếu: class cấp cao nhất CHỈ có public hoặc
 * package-private. Đây là bảng ở mục 11 bài t4 áp dụng vào đây.
 * <p>
 * 2. VÌ SAO DÙNG - ba lý do trang đưa ra
 * - GOM NHÓM HỢP LÝ: một class chỉ hữu ích cho đúng một class khác thì nên
 * nhét vào trong nó, "Nesting such helper classes makes their package more
 * streamlined" - package bớt lộn xộn.
 * - TĂNG ĐÓNG GÓI, đây là lý do mạnh nhất: có hai class A và B, B cần chạm
 * vào thành viên của A mà lẽ ra phải để private. Giấu B vào trong A thì A
 * giữ được private, B vẫn chạm được, và bản thân B cũng được giấu khỏi
 * thế giới bên ngoài.
 * - DỄ ĐỌC HƠN: đặt class nhỏ ngay cạnh chỗ nó được dùng.
 * <p>
 * 3. INNER CLASS - gắn với một OBJECT
 * - "An inner class is associated with an INSTANCE of its enclosing class
 * and has direct access to that object's methods and fields", kể cả các
 * thành viên private.
 * - Object của inner class chỉ tồn tại BÊN TRONG một object của class
 * ngoài. Không có object ngoài thì không có object trong.
 * - Hệ quả về cú pháp: muốn tạo inner class phải có object ngoài trước, rồi
 * viết "outerObject.new InnerClass()". Đã chạy thật, gọi theo kiểu thường
 * thì javac từ chối:
 * error: an enclosing instance that contains Outer.Inner is required
 * - Hai loại inner class đặc biệt là LOCAL CLASS và ANONYMOUS CLASS, ở mục
 * 7 và 9.
 * <p>
 * 4. STATIC NESTED CLASS - gắn với CLASS
 * - Giống static method và static field: nó gắn với CLASS chứ không gắn với
 * object nào.
 * - "A static nested class cannot refer directly to instance variables or
 * methods defined in its enclosing class: it can use them only through an
 * object reference". Đúng bốn luật static ở mục 12 bài t4. Đã chạy thật:
 * error: non-static variable instanceField cannot be referenced from
 * a static context
 * - Note quan trọng nhất của cả trang: "In effect, a static nested class is
 * BEHAVIORALLY A TOP-LEVEL CLASS that has been nested in another top-level
 * class for packaging convenience". Nó thực chất là một class bình thường,
 * chỉ lồng vào cho gọn chỗ. Không có ràng buộc ngầm nào với object ngoài.
 * - Tạo nó y như class thường: new OuterClass.StaticNestedClass().
 * <p>
 * 5. Bảng so sánh quyền truy cập - thứ cần nhớ sau cả bài
 * - INNER CLASS: chạm được MỌI thứ của object ngoài, kể cả private, chạm
 * trực tiếp không cần gì thêm.
 * - STATIC NESTED CLASS: chỉ chạm trực tiếp được thành viên static của
 * class ngoài. Muốn chạm instance thì phải cầm một object reference.
 * - TOP-LEVEL CLASS khác: giống hệt static nested class.
 * <p>
 * 6. SHADOWING - và cú pháp ClassName.this
 * - Khi một khai báo ở phạm vi trong trùng tên với khai báo ở phạm vi
 * ngoài, nó CHE KHUẤT cái ngoài, và "You cannot refer to a shadowed
 * declaration by its name alone".
 * - Ví dụ ShadowTest của trang có BA biến tên x: field của ShadowTest,
 * field của inner class FirstLevel, và tham số của method
 * methodInFirstLevel(). Cách gọi từng cái:
 * x                      -> tham số của method (cái gần nhất)
 * this.x                 -> field của inner class FirstLevel
 * ShadowTest.this.x      -> field của class bao ngoài
 * - Cú pháp "TenClass.this" là thứ mới của bài này. Nó cho phép chỉ đích
 * danh object ngoài khi đang đứng trong inner class.
 * <p>
 * 7. LOCAL CLASS - class khai báo trong một KHỐI
 * - Local class định nghĩa được trong bất kỳ khối lệnh nào: thân method,
 * trong vòng for, trong nhánh if. Hay gặp nhất là trong thân method.
 * - Ví dụ của trang là class PhoneNumber khai báo bên trong method
 * validatePhoneNumber() để kiểm tra số điện thoại đủ mười chữ số.
 * - Nó truy cập được thành viên của class bao ngoài, và từ Java SE 8 thì
 * truy cập được cả THAM SỐ của method chứa nó.
 * - Local class KHÔNG khai báo được interface bên trong một khối, vì
 * "interfaces are inherently static".
 * - Local class nằm trong static method thì chỉ chạm được thành viên
 * static của class ngoài.
 * <p>
 * 8. BIẾN BỊ BẮT - final hoặc EFFECTIVELY FINAL
 * - Đây là luật quan trọng nhất của mục 7 và 9, và là chỗ người từ JS sang
 * hay vấp.
 * - Khi local class dùng một biến cục bộ của khối bao ngoài, nó BẮT
 * (capture) biến đó. Ngày xưa biến bắt buộc phải khai báo final.
 * - Từ Java SE 8, biến chỉ cần final hoặc EFFECTIVELY FINAL. Định nghĩa
 * nguyên văn: "A variable or parameter whose value is never changed after
 * it is initialized is effectively final". Tức là không cần gõ chữ final,
 * chỉ cần thực tế không bao giờ gán lại.
 * - Gán lại một lần thôi là hỏng. Đã chạy thật:
 * error: local variables referenced from an inner class must be final
 * or effectively final
 * <p>
 * 9. ANONYMOUS CLASS - khai báo và khởi tạo cùng lúc
 * - "They enable you to declare and instantiate a class at the same time.
 * They are like local classes except that they do not have a name. Use
 * them if you need to use a local class ONLY ONCE".
 * - Khác biệt cốt lõi về bản chất: local class là một KHAI BÁO, còn
 * anonymous class là một BIỂU THỨC. Vì là biểu thức nên nó phải nằm trong
 * một câu lệnh, và đó là lý do có DẤU CHẤM PHẨY sau ngoặc nhọn đóng -
 * chi tiết khiến người mới bối rối nhất.
 * - Cấu trúc một biểu thức anonymous class gồm bốn phần:
 * toán tử new
 * tên INTERFACE muốn implement hoặc CLASS muốn kế thừa
 * cặp ngoặc đơn chứa đối số constructor - implement interface thì để
 * RỖNG, vì interface không có constructor
 * một thân class, trong đó khai báo method thì được nhưng viết câu lệnh
 * trần thì không
 * - Bắt biến giống hệt local class: chỉ dùng được biến final hoặc
 * effectively final của phạm vi bao ngoài.
 * - KHÔNG khai báo được constructor trong anonymous class - nó không có
 * tên thì lấy gì đặt tên cho constructor.
 * - Khai báo được: field, method phụ (kể cả method không thuộc supertype),
 * instance initializer, và cả local class.
 * <p>
 * 10. SERIALIZATION - khuyến cáo của trang
 * - "Serialization of inner classes, including local and anonymous classes,
 * is strongly discouraged".
 * - Lý do: compiler sinh ra các SYNTHETIC CONSTRUCT - class, method, field
 * không hề có trong mã nguồn - để cài đặt tính năng ngôn ngữ mà không
 * phải sửa JVM.
 * - Mà synthetic construct lại khác nhau giữa các bản cài đặt compiler, nên
 * file .class cũng khác nhau. Serialize bằng JRE này rồi deserialize bằng
 * JRE khác có thể hỏng.
 * <p>
 * ======================================================================
 * TRANG 2 - WHEN TO USE NESTED, LOCAL, ANONYMOUS CLASSES AND LAMBDAS
 * Trang cuối cùng của section classes-objects. Rất ngắn, nhưng là trang
 * DUY NHẤT trả lời câu hỏi "vậy rốt cuộc dùng cái nào".
 * ======================================================================
 * <p>
 * 11. CHỌN CÁI NÀO - danh sách quyết định của trang
 * - Mở đầu trang nhắc lại: nested class cho phép gom nhóm class chỉ dùng ở
 * một chỗ, tăng đóng gói, và code dễ đọc dễ bảo trì hơn. Local class,
 * anonymous class và lambda cũng đem lại đúng những lợi ích đó, nhưng
 * "they are intended to be used for more specific situations" - mỗi thứ
 * sinh ra cho một tình huống hẹp hơn.
 * - LOCAL CLASS: "Use it if you need to create MORE THAN ONE INSTANCE of a
 * class, access its CONSTRUCTOR, or introduce a NEW, NAMED TYPE".
 * Ba dấu hiệu: cần nhiều hơn một object, cần gọi constructor, hoặc cần
 * một cái TÊN để gọi.
 * - ANONYMOUS CLASS: "Use it if you need to declare FIELDS or ADDITIONAL
 * METHODS". Tức là khi cần giữ trạng thái riêng, hoặc cần thêm method
 * ngoài những method của supertype.
 * - LAMBDA, hai trường hợp:
 * "Use it if you are encapsulating a SINGLE UNIT OF BEHAVIOR that you
 * want to pass to other code" - đóng gói một hành vi để truyền đi.
 * "Use it if you need a simple instance of a FUNCTIONAL INTERFACE and
 * none of the preceding criteria apply" - tức là khi ba tiêu chí trên
 * đều không cần tới. Lambda là lựa chọn MẶC ĐỊNH khi mọi thứ đơn giản.
 * - NESTED CLASS: "Use it if your requirements are similar to those of a
 * local class, you want to make the type MORE WIDELY AVAILABLE, and you
 * DON'T require access to local variables or method parameters".
 * - Và câu chốt phân biệt hai loại nested, nhắc lại mục 3 và 4:
 * "Use a NON-STATIC nested class (or inner class) if you require access
 * to an enclosing instance's non-public fields and methods. Use a STATIC
 * nested class if you don't require this access".
 * - Đọc xuôi thì rối, nên đọc theo thứ tự NGƯỢC sẽ dễ nhớ hơn: mặc định
 * dùng lambda; cần field hoặc method phụ thì lên anonymous class; cần
 * nhiều object hoặc cần một cái tên thì lên local class; cần dùng lại ở
 * nhiều nơi thì lên nested class; và chỉ bỏ chữ static khi thật sự cần
 * chạm vào thành viên không public của object ngoài.
 * <p>
 * ======================================================================
 * GHI CHÚ NGOÀI TÀI LIỆU VÀ BEST PRACTICE
 * ======================================================================
 * <p>
 * 12. Ghi chú ngoài tài liệu
 * - TRANG NÀY ĐÃ LỖI THỜI MỘT CHỖ, cẩn thận. Trang khẳng định local class
 * "cannot contain most kinds of static declarations" và chỉ cho phép hằng
 * số, kèm thông báo lỗi "modifier static is only allowed in constant
 * variable declaration". Đã thử trên JDK 25 của repo này: khai báo CẢ
 * static method LẪN static field trong local class và trong inner class
 * đều BIÊN DỊCH VÀ CHẠY BÌNH THƯỜNG, in ra "1 9 ok". Luật cũ được nới ra
 * từ Java 16, cùng đợt với record. Tài liệu chưa cập nhật phần này.
 * - INNER CLASS GIỮ THAM CHIẾU NGẦM TỚI OBJECT NGOÀI, và đây là lý do thực
 * tế quan trọng nhất của cả bài. javap trên một inner class cho thấy:
 * class Outer2$Inner {
 * final Outer2 this$0;        <- tham chiếu ngầm, không có trong mã nguồn
 * Outer2$Inner(Outer2);       <- constructor nhận object ngoài
 * }
 * Hệ quả: giữ một object inner còn sống là giữ luôn cả object ngoài còn
 * sống, dù không hề dùng tới nó. Theo đúng luật GC ở mục 8 bài t4, object
 * ngoài vẫn đang được tham chiếu nên không bao giờ bị thu gom. Đây là
 * nguồn memory leak kinh điển, nhất là khi object inner được đưa vào một
 * cache hay một listener sống lâu.
 * - QUY TẮC THỰC CHIẾN RÚT RA: MẶC ĐỊNH DÙNG static nested class. Chỉ bỏ
 * chữ static khi thật sự cần chạm vào instance member của class ngoài.
 * Thêm một chữ static là cắt đứt sợi dây ngầm đó.
 * - ANONYMOUS CLASS VÀ LAMBDA KHÔNG THAY THẾ NHAU HOÀN TOÀN, và trang 2 ở
 * mục 11 không nói rõ chỗ nguy hiểm nhất: TỪ KHÓA this TRỎ ĐI HAI NƠI
 * KHÁC NHAU. Đã chạy thật trên máy này:
 * anonymous: this là -> F$1     (chính object anonymous đó)
 * lambda   : this là -> F       (object BAO NGOÀI)
 * Đổi một anonymous class sang lambda mà bên trong có dùng this là đổi
 * luôn ý nghĩa của code, compiler không hề cảnh báo. Ngoài ra lambda
 * không có field nên không tự giữ trạng thái được - đó chính là tiêu chí
 * "need to declare fields" mà trang dùng để chọn anonymous class.
 * Kết quả F$1 ở trên cũng xác nhận luôn quy tắc đặt tên $1 nói dưới đây.
 * - EFFECTIVELY FINAL CHỈ RÀNG BUỘC THAM CHIẾU, KHÔNG RÀNG BUỘC RUỘT. Một
 * biến kiểu List không được gán lại, nhưng gọi list.add(...) thì vẫn chạy
 * bình thường và inner class thấy ngay thay đổi đó. Đúng nguyên tắc ở mục
 * 4 bài t4: đổi được RUỘT, không đổi được HƯỚNG.
 * - ĐỌC STACK TRACE: tên file .class là Outer$Inner với class có tên, và
 * Outer$1, Outer$2 với anonymous class, đánh số theo thứ tự xuất hiện.
 * Thấy "$1" trong stack trace là biết lỗi nằm trong một anonymous class,
 * và con số đó là manh mối duy nhất để tìm ra nó.
 * - TRONG SPRING BOOT gặp nested class ở ba chỗ quen thuộc: static nested
 * class làm DTO hoặc response gắn liền với đúng một controller, static
 * nested class làm Builder, và anonymous class trong các callback hay
 * config kiểu cũ - phần lớn chỗ này ngày nay đã thay bằng lambda.
 * - SO VỚI TYPESCRIPT: TS không có nested class thật. Thứ gần nhất với
 * anonymous class là object literal implement một interface, còn lambda
 * thì gần với arrow function. Khác biệt lớn nhất và gây khó chịu nhất lúc
 * đầu là chuyện BẮT BIẾN: closure của JS bắt biến tự do thoải mái, đổi
 * giá trị vô tư; Java bắt buộc final hoặc effectively final. Lý do là JS
 * bắt chính Ô NHỚ của biến, còn Java chép GIÁ TRỊ của nó vào lúc tạo
 * object, nên nếu cho phép đổi thì hai bên sẽ thấy hai giá trị khác nhau.
 * <p>
 * Best practice: mặc định chọn static nested class, chỉ dùng inner class khi
 * thật sự cần tới object ngoài - mỗi inner class là một sợi dây vô hình giữ
 * object ngoài sống mãi. Nested class nên để private nếu nó là chi tiết nội
 * bộ, vì đó chính là lý do nó được lồng vào thay vì đứng riêng. Local class
 * chỉ dùng khi logic đó thật sự chỉ có nghĩa trong đúng một method; dùng tới
 * lần thứ hai là lúc tách nó ra thành class riêng. Anonymous class ngày nay
 * chỉ còn hợp lý khi interface có NHIỀU method hoặc khi cần giữ trạng thái -
 * còn lại cứ lambda cho gọn. Và nếu một nested class phình to quá vài chục
 * dòng thì đó là tín hiệu nó đã đủ lớn để sống một mình trong file riêng:
 * lồng nhau là để gom thứ nhỏ, không phải để giấu thứ lớn.
 */
public class t5_Nested_Classes {

    void main() {
        // TODO 1 (muc 3, 4): tao mot class ngoai co mot instance field va mot
        //   static field. Ben trong no viet MOT inner class va MOT static
        //   nested class, ca hai cung thu doc hai field do. Doc loi cua javac
        //   o cho static nested cham instance field.

        // TODO 2 (muc 3): tao object cua inner class bang dung cu phap
        //   "outerObject.new InnerClass()". Truoc do thu viet
        //   "new Outer.Inner()" de doc loi "an enclosing instance ... is
        //   required".

        // TODO 3 (muc 6): dung ba bien cung ten x nhu vi du ShadowTest cua
        //   trang - mot o class ngoai, mot o inner class, mot la tham so
        //   method. In ca ba bang x, this.x va TenClassNgoai.this.x.

        // TODO 4 (muc 7, 8): viet mot local class ben trong mot method, cho
        //   no doc mot bien cuc bo cua method do. Chay duoc roi thi them mot
        //   dong GAN LAI bien do -> doc loi "must be final or effectively
        //   final".

        // TODO 5 (muc 9): viet mot interface nho mot method, roi tao object
        //   cua no bang anonymous class. Chu y dau cham phay sau ngoac nhon
        //   dong. Sau do viet lai bang lambda de so sanh do dai.

        // TODO 6 (muc 12): build xong, chay javap -p len file
        //   Outer$Inner.class de tu nhin thay field "this$0". Doi sang static
        //   nested roi javap lai - field do bien mat.

        // ==============================================================
        // TRANG 2 - CHON DUNG LOAI
        // ==============================================================

        // TODO 7 (muc 11, 12): lay lai interface o TODO 5, viet HAI ban: mot
        //   anonymous class va mot lambda, moi ban in ra
        //   this.getClass().getName(). Doan truoc ket qua roi chay doi chieu.
        //   Day la ly do khong phai luc nao cung thay duoc anonymous bang
        //   lambda.

        // TODO 8 (muc 11): voi moi tinh huong duoi day, tu tra loi nen dung
        //   loai nao va vi sao - khong can viet code, chi can ghi comment:
        //   a) can tao 10 object cua cung mot logic chi dung trong 1 method
        //   b) can mot handler chi co 1 method, truyen vao ham khac
        //   c) can mot kieu du lieu phu chi phuc vu dung 1 controller
        //   d) can doc field private cua object ngoai tu ben trong
    }
}
