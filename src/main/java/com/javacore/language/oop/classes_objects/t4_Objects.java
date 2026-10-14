package com.javacore.language.oop.classes_objects;

/**
 * Creating and Using Objects + More on Classes
 * <p>
 * 0. Vị trí bài này
 * - Ba bài trước nhìn từ phía NGƯỜI VIẾT class: khai báo field, viết method,
 * viết constructor.
 * - Bài này nhìn từ phía NGƯỜI DÙNG class: tạo object ra sao, chạm vào nó
 * bằng cách nào, và khi nào nó chết.
 * - File gộp hai trang của lộ trình: Creating and Using Objects ở mục 1 tới
 * 8, và More on Classes ở mục 9 tới 14. Trang thứ hai là trang TỔNG KẾT
 * cả section, nó trả nốt những món còn nợ ở ba bài trước: bảng truy cập
 * đầy đủ đã hứa ở t1, định nghĩa chính thức của this vẫn dùng từ t3, và
 * toàn bộ phần static.
 * - Ví dụ của trang là Point và Rectangle, trong đó Rectangle có BỐN
 * constructor - đúng phần overloading vừa học ở bài trước.
 * <p>
 * ======================================================================
 * TRANG 1 - CREATING AND USING OBJECTS (mục 1 đến 8)
 * ======================================================================
 * <p>
 * 1. Một câu lệnh tạo object có ĐÚNG BA PHẦN
 * - Trang tách rõ "Point origin = new Point(23, 94)" thành ba việc:
 * DECLARATION - phần "Point origin", gắn một TÊN BIẾN với một KIỂU object.
 * INSTANTIATION - từ khóa new, "a Java operator that creates the object".
 * INITIALIZATION - lời gọi constructor đứng sau new, khởi tạo object đó.
 * - Ba việc này thường viết gộp một dòng nên dễ tưởng là một. Tách được ba
 * phần ra thì mới hiểu mục 2 và mục 7.
 * <p>
 * 2. KHAI BÁO BIẾN KHÔNG TẠO RA OBJECT
 * - Nguyên văn: "Simply declaring a reference variable does not create an
 * object". Viết "Point origin;" là mới chỉ có cái TÊN, chưa có gì để trỏ.
 * - Trang nói rõ giá trị của nó lúc đó là "undetermined", và "You must
 * assign an object to origin before you use it in your code. Otherwise,
 * you will get a compiler error".
 * - Khác biệt với kiểu nguyên thủy: khai báo "int x;" là đã có chỗ chứa giá
 * trị thật. Khai báo kiểu tham chiếu thì chỉ có chỗ chứa một ĐỊA CHỈ.
 * <p>
 * 3. Toán tử new làm những gì
 * - "The new operator instantiates a class by allocating memory for a new
 * object and returning a reference to that memory. The new operator also
 * invokes the object constructor".
 * - Ba việc trong một: CẤP BỘ NHỚ, GỌI CONSTRUCTOR, TRẢ VỀ THAM CHIẾU.
 * - new đòi đúng một đối số hậu tố là lời gọi constructor, và chính tên
 * constructor cho biết class nào được dựng.
 * - Note của trang về từ vựng: "instantiating a class" và "creating an
 * object" là MỘT. Object tạo ra gọi là một INSTANCE của class.
 * - Tham chiếu mà new trả về KHÔNG bắt buộc phải gán vào biến, dùng thẳng
 * trong một biểu thức cũng được - xem mục 6 và 7.
 * <p>
 * 4. NHIỀU THAM CHIẾU CÙNG TRỎ MỘT OBJECT
 * - Khi truyền một Point có sẵn vào constructor của Rectangle, trang nhận
 * xét: "Now there are two references to the same Point object - an object
 * can have multiple references to it".
 * - Đây chính là mục 10 của bài Methods nói lại bằng hình ảnh khác: object
 * thì một, tham chiếu thì nhiều. Sửa object qua tham chiếu này thì tham
 * chiếu kia thấy ngay, vì chúng là cùng một thứ.
 * <p>
 * 5. Truy cập FIELD của object
 * - BÊN TRONG class của nó: dùng tên đơn, viết thẳng "width", "height".
 * - BÊN NGOÀI class: bắt buộc "objectReference.fieldName", tức là tham
 * chiếu rồi dấu chấm rồi tên field - rectangle.width, rectangle.height.
 * - Dùng tên đơn từ bên ngoài là LỖI BIÊN DỊCH, vì như trang giải thích
 * "those fields exist only within an object".
 * - MỖI OBJECT CÓ BẢN SAO RIÊNG của instance field: rectangle1 và
 * rectangle2 có origin, width, height riêng, không dính gì nhau.
 * - Chỗ đứng trước dấu chấm không nhất thiết là tên biến, mà là BẤT KỲ
 * BIỂU THỨC NÀO trả về tham chiếu. Ví dụ của trang:
 * new Rectangle().height
 * <p>
 * 6. Gọi METHOD của object
 * - Cùng một cú pháp: "objectReference.methodName(argumentList)", không
 * tham số thì để ngoặc rỗng.
 * - Cũng vậy, đứng trước dấu chấm có thể là biểu thức. Trang dùng
 * new Rectangle(100, 50).area() - tạo object rồi gọi method của nó ngay
 * trong một câu lệnh, không cần biến trung gian.
 * - Method có trả về giá trị thì dùng được trong biểu thức: gán cho biến,
 * làm điều kiện, điều khiển vòng lặp.
 * <p>
 * 7. OBJECT KHÔNG CÒN AI THAM CHIẾU
 * - Với câu lệnh "new Rectangle().height", trang chỉ ra điều quan trọng:
 * chạy xong thì "the program no longer has a reference to the created
 * Rectangle, because the program never stored the reference anywhere".
 * - Object đó thành "unreferenced", và tài nguyên của nó "free to be
 * recycled by the Java Virtual Machine".
 * - Nó vẫn được tạo ra đầy đủ, constructor vẫn chạy - chỉ là không ai giữ
 * nữa nên nó thành rác ngay lập tức.
 * <p>
 * 8. GARBAGE COLLECTOR
 * - Khác với "some object-oriented languages require that you keep track of
 * all the objects you create and that you explicitly destroy them", Java
 * không bắt lập trình viên hủy object.
 * - "The Java runtime environment deletes objects when it determines that
 * they are no longer being used. This process is called garbage
 * collection".
 * - ĐIỀU KIỆN để một object bị thu gom: KHÔNG CÒN THAM CHIẾU NÀO tới nó.
 * - Tham chiếu mất đi theo hai cách: biến RA KHỎI PHẠM VI, hoặc gán cho
 * biến giá trị đặc biệt null.
 * - Nhấn mạnh của trang: "all references to an object must be dropped
 * before the object is eligible for garbage collection". Còn sót MỘT
 * tham chiếu là object còn sống.
 * - GC chạy định kỳ và tự quyết thời điểm, "when it determines that the
 * time is right". Không ai hứa nó chạy lúc nào.
 * <p>
 * ======================================================================
 * TRANG 2 - MORE ON CLASSES (mục 9 đến 14)
 * Trang tổng kết cả section classes-objects, trả nốt các món nợ của ba
 * bài trước: bảng truy cập đầy đủ, định nghĩa this, và toàn bộ static.
 * ======================================================================
 * <p>
 * 9. Trả về giá trị từ method
 * - Method trả về cho nơi gọi khi một trong ba điều xảy ra TRƯỚC: chạy hết
 * mọi câu lệnh, gặp lệnh return, hoặc ném exception.
 * - Method khai báo void thì không trả giá trị, nhưng VẪN DÙNG ĐƯỢC
 * "return;" trần để thoát sớm ra khỏi method. Thứ này hay bị bỏ quên.
 * - Trả về giá trị từ method void là LỖI BIÊN DỊCH. Ngược lại, method không
 * phải void BẮT BUỘC có return kèm giá trị, kiểu phải khớp đúng kiểu đã
 * khai báo - không thể trả int từ method khai báo boolean.
 * - Trả về kiểu tham chiếu cũng được, ví dụ của trang là
 * "public Bicycle seeWhosFastest(Bicycle a, Bicycle b)".
 * - COVARIANT RETURN TYPE: khi override, được phép thu hẹp kiểu trả về
 * xuống một lớp con, "the return type is allowed to vary in the same
 * direction as the subclass". Trang khuyên chưa học Inheritance thì bỏ
 * qua mục này, quay lại sau.
 * - Dùng tên INTERFACE làm kiểu trả về cũng được, khi đó object trả về bắt
 * buộc phải implement interface đó.
 * <p>
 * 10. Từ khóa this - định nghĩa chính thức
 * - "Within an instance method or a constructor, this is a reference to the
 * CURRENT OBJECT - the object whose method or constructor is being
 * called". Từ t3 tới giờ đã dùng this.x = x theo idiom, đây mới là định
 * nghĩa của nó.
 * - Lý do dùng this nhiều nhất: field bị tham số CHE KHUẤT. Trang đưa cả
 * hai cách viết class Point - hoặc "this.x = x", hoặc đổi tên tham số
 * thành a, b rồi viết "x = a". Cách đầu là chuẩn mực.
 * - this(...) là EXPLICIT CONSTRUCTOR INVOCATION, gọi một constructor khác
 * trong cùng class. Ví dụ Rectangle của trang:
 * Rectangle()           -> this(0, 0, 1, 1)
 * Rectangle(w, h)       -> this(0, 0, w, h)
 * Rectangle(x, y, w, h) -> chỉ bản này mới gán field thật
 * Đây chính là cách tránh chép lại logic giữa các bản nạp chồng.
 * - JDK 25 VÀ PROLOGUE: "Until JDK 25, the invocation of another
 * constructor must be the first line in the constructor. Starting with
 * JDK 25 you can add some code before the invocation of another
 * constructor, but this code has restrictions". Phần đứng trước lời gọi
 * đó gọi là PROLOGUE. Giới hạn: không dùng được instance đang dựng, TRỪ
 * khi đang khởi tạo instance field; và tuyệt đối KHÔNG gọi được instance
 * method nào.
 * <p>
 * 11. Bốn mức truy cập - bảng đầy đủ đã hứa ở bài t1
 * - Có HAI TẦNG kiểm soát:
 * TẦNG CLASS - chỉ hai lựa chọn: public, hoặc không viết gì
 * (package-private, chỉ nhìn thấy trong package của chính nó).
 * TẦNG THÀNH VIÊN - đủ bốn: public, protected, private, package-private.
 * - private: chỉ truy cập được TRONG class của chính nó.
 * - protected: trong cùng package, CỘNG THÊM lớp con nằm ở package khác.
 * - Bảng của trang:
 * Modifier      Class  Package  Subclass  World
 * public          Y       Y        Y        Y
 * protected       Y       Y        Y        N
 * (không viết)    Y       Y        N        N
 * private         Y       N        N        N
 * - Lời khuyên nguyên văn: "Use the most restrictive access level that
 * makes sense for a particular member. Use private unless you have a good
 * reason not to".
 * - Và một lời tự thú đáng chú ý của chính tài liệu: "Avoid public fields
 * except for constants. Many of the examples in the tutorial use public
 * fields. This may help to illustrate some points concisely, but is not
 * recommended for production code". Lý do: field public trói mình vào
 * một cách cài đặt cụ thể, sau muốn đổi cũng không đổi được.
 * - Hai thứ nằm ngoài bảng này: Java Module System có thể siết thêm tầm
 * nhìn của class public từ ngoài module, còn Reflection API thì ngược
 * lại, có thể chọc vào thành viên lẽ ra không nhìn thấy được.
 * <p>
 * 12. static - thành viên thuộc về CLASS, không thuộc object
 * - Mỗi object có bản sao riêng của instance variable. Còn field có
 * modifier static thì gọi là CLASS VARIABLE, nó "associated with the
 * class, rather than with any object".
 * - "Every instance of the class shares a class variable, which is in one
 * fixed location in memory". Một ô nhớ duy nhất, mọi object dùng chung.
 * - Truy cập bằng TÊN CLASS: Bicycle.numberOfBicycles. Trang nói dùng object
 * reference cũng chạy nhưng "discouraged", vì nhìn vào không biết đó là
 * class variable.
 * - Ví dụ kinh điển của trang: numberOfBicycles đếm tổng số xe đã tạo, còn
 * id là của riêng từng xe. Constructor viết "id = ++numberOfBicycles".
 * Đúng một dòng đó nói rõ khác nhau giữa hai loại field.
 * - Method static gọi bằng "ClassName.methodName(args)", không cần tạo
 * object nào cả.
 * - BỐN LUẬT TRUY CẬP, nên thuộc:
 * instance method -> chạm được instance variable và instance method.
 * instance method -> chạm được class variable và class method.
 * class method    -> chạm được class variable và class method.
 * class method    -> KHÔNG chạm trực tiếp được instance variable hay
 * instance method, bắt buộc phải đi qua một object reference.
 * - Hệ quả quan trọng nhất: "class methods cannot use the this keyword as
 * there is no instance for this to refer to". Trong static KHÔNG CÓ this,
 * vì không có object nào đang đứng đó cả.
 * <p>
 * 13. Hằng số - static cộng final
 * - final nghĩa là "the value of this field cannot change". Ghép với static
 * thành hằng số dùng chung: static final double PI = 3.141592653589793;
 * - Gán lại hằng số là lỗi biên dịch.
 * - QUY ƯỚC ĐẶT TÊN: viết HOA toàn bộ, nhiều từ thì ngăn bằng dấu gạch
 * dưới - MAX_RETRY, DEFAULT_TIMEOUT.
 * - COMPILE-TIME CONSTANT, đọc kỹ vì đây là bẫy triển khai thật: nếu hằng
 * là kiểu nguyên thủy hoặc String và biết giá trị ngay lúc biên dịch,
 * "the compiler replaces the constant name everywhere in the code with
 * its value". Giá trị bị nhét thẳng vào bytecode của NƠI DÙNG. Đổi hằng
 * thì phải biên dịch lại MỌI class dùng nó, không thì chúng vẫn chạy giá
 * trị cũ. Bằng chứng đã chạy thật nằm ở mục 15.
 * <p>
 * 14. Khởi tạo field - ba cách và thứ tự giữa chúng
 * - CÁCH 1, gán ngay lúc khai báo: "public int lineCount = 0;". Gọn, nhưng
 * trang chỉ rõ giới hạn: không làm được logic nhiều bước, không bắt được
 * lỗi, không chạy được vòng lặp.
 * - CÁCH 2, cho INSTANCE variable: khởi tạo trong constructor, chỗ này viết
 * logic thoải mái.
 * - CÁCH 3, cho CLASS variable: STATIC INITIALIZATION BLOCK, là khối lệnh
 * trong ngoặc nhọn có chữ static đứng trước.
 * - Một class có bao nhiêu static block cũng được, đặt ở đâu trong thân
 * class cũng được, và hệ thống BẢO ĐẢM chúng chạy theo đúng thứ tự xuất
 * hiện trong mã nguồn.
 * - Thay thế cho static block: viết một private static method rồi gán
 * "public static varType myVar = initializeClassVariable();". Lợi thế
 * trang nêu là method thì dùng lại được khi cần khởi tạo lại.
 * - CẢNH BÁO của trang về static block: "you cannot prevent this block to be
 * executed". Nếu nó hỏng thì "you will not be able to instantiate any
 * object for this class". Nguy hiểm nhất khi static block đụng vào file
 * hoặc mạng.
 * - INSTANCE INITIALIZER BLOCK: giống static block nhưng KHÔNG có chữ
 * static. Điểm mấu chốt: "The Java compiler copies initializer blocks
 * into every constructor" - nó được chép vào MỌI constructor, nên dùng
 * được để chia sẻ code khởi tạo giữa nhiều constructor.
 * - Cách cuối: dùng một FINAL METHOD để khởi tạo instance variable. Trang
 * giải thích luôn vì sao phải final - "calling non-final methods during
 * instance initialization can cause problems", đúng cái bẫy đã ghi ở mục
 * 7 bài t3 về việc gọi method có thể bị override khi object chưa dựng
 * xong.
 * - Note phụ: không bắt buộc khai báo field ở đầu class, chỉ cần khai báo
 * và khởi tạo trước khi dùng. Nhưng để đầu class vẫn là thông lệ.
 * <p>
 * ======================================================================
 * GHI CHÚ NGOÀI TÀI LIỆU VÀ BEST PRACTICE (cho cả hai trang)
 * ======================================================================
 * <p>
 * 15. Ghi chú ngoài tài liệu
 * - TRANG NHẮC null NHƯNG KHÔNG NÓI NullPointerException. Biến tham chiếu
 * đang null mà chạm vào field hay method của nó là NPE - exception gặp
 * nhiều nhất trong đời lập trình Java. Tin tốt: từ JDK 14 thông báo đã
 * chỉ đích danh. Chạy thật trên máy này được:
 * Cannot read field "x" because "n" is null
 * Tên biến "n" chỉ hiện ra khi biên dịch có thông tin debug - Maven và
 * IntelliJ bật sẵn. Biên dịch tay không cờ -g thì chỉ thấy "<local6>".
 * - DẤU == SO SÁNH THAM CHIẾU, KHÔNG SO SÁNH NỘI DUNG. Đây là hệ quả trực
 * tiếp của mục 4 và là bẫy số một với người mới. Đã chạy thật:
 * p1 == p2 : false   (hai object khác nhau, cùng nội dung)
 * p1 == p3 : true    (p3 = p1, cùng một object)
 * new String("abc") == new String("abc") : false, còn .equals() : true
 * Quy tắc: so sánh NỘI DUNG thì luôn dùng equals, == chỉ để hỏi "có phải
 * đúng cùng một object không".
 * - BẪY == NGUY HIỂM Ở CHỖ NÓ THỈNH THOẢNG ĐÚNG. Đã chạy thật:
 * "abc" == "abc"      : true   (chuỗi hằng được gom chung một chỗ)
 * Integer 127 == 127  : true   (JVM cache sẵn từ -128 tới 127)
 * Integer 128 == 128  : false  (cùng giá trị mà vẫn false)
 * int 128 == 128      : true   (nguyên thủy thì luôn đúng)
 * Giá trị nhỏ chạy đúng, giá trị lớn sai. Chuỗi hằng chạy đúng, chuỗi
 * ghép lúc chạy thì sai. Đây là loại bug qua lọt mọi test gõ tay.
 * - GÁN BIẾN KHÔNG SAO CHÉP OBJECT. "a = b" chỉ chép cái tham chiếu, sau đó
 * hai biến cùng trỏ một object. Muốn có bản sao thật thì phải tự tạo.
 * Java không có spread hay structuredClone như JS.
 * - STATIC CÓ THỂ THAY ĐỔI CHÍNH LÀ BIẾN TOÀN CỤC, và nó phá ba thứ cùng
 * lúc: an toàn luồng (mọi request của web server dùng chung một ô nhớ),
 * khả năng test (test này làm bẩn trạng thái của test kia vì class chỉ
 * nạp một lần cho cả tiến trình), và khả năng suy luận về code. Trong
 * Spring Boot, trạng thái nằm ở BEAN SINGLETON có vòng đời được quản lý
 * chứ không nằm ở static. static chỉ nên dùng cho hằng số và cho hàm
 * thuần tuý tính toán.
 * - STATIC METHOD RẤT KHÓ TEST. Không mock được bằng cách thông thường, nên
 * một service gọi thẳng SomeUtil.doThing() là phụ thuộc cứng không gỡ ra
 * được lúc viết test. Phụ thuộc thì nên đi vào qua constructor.
 * - BẰNG CHỨNG CHO MỤC 13, đã chạy thật trên máy này: class Lib khai báo
 * "public static final int GIOI_HAN = 100", class App in Lib.GIOI_HAN ra
 * 100. Sửa hằng thành 999, biên dịch lại MỖI Lib rồi chạy App -> vẫn in
 * ra 100. Giá trị cũ nằm cứng trong file .class của App. Trong dự án
 * nhiều module, đây là nguồn của những lỗi kiểu "sửa rồi mà sao không
 * thấy đổi".
 * - STATIC BLOCK NÉM EXCEPTION THÌ CLASS ĐÓ CHẾT HẲN. JVM ném
 * ExceptionInInitializerError, và mọi lần chạm vào class sau đó đều
 * hỏng, không có cách nào thử lại. Đó là lý do đừng bao giờ đọc file,
 * gọi mạng hay mở kết nối trong static block.
 * - ACCESS MODIFIER KHÔNG CHỈ LÀ CHUYỆN PHONG CÁCH TRONG SPRING. Annotation
 * như @Transactional hay @Cacheable hoạt động bằng proxy, nên đặt trên
 * method không phải public thì nó IM LẶNG không có tác dụng gì - không
 * lỗi, không cảnh báo, chỉ là transaction không hề mở. Cùng lý do đó,
 * gọi nội bộ bằng this.method() cũng đi tắt qua proxy và mất tác dụng.
 * - PACKAGE-PRIVATE LÀ VŨ KHÍ BỊ BỎ QUÊN. Mức mặc định khi không viết gì
 * cho phép dựng một nhóm class hợp tác chặt với nhau mà không phơi thứ
 * gì ra ngoài package. Nhiều người chỉ biết public với private nên lỡ
 * tay phơi ra cả những class lẽ ra là chi tiết nội bộ.
 * - ĐỪNG GỌI System.gc(). Nó chỉ là lời đề nghị, JVM có quyền phớt lờ.
 * Thấy dòng này trong code production thường là dấu hiệu ai đó đang chữa
 * triệu chứng mà không hiểu nguyên nhân.
 * - MEMORY LEAK TRONG JAVA VẪN CÓ THẬT, chỉ là nguyên nhân khác C++: không
 * phải quên giải phóng, mà là VÔ TÌNH GIỮ tham chiếu. Thủ phạm quen mặt
 * là static Map dùng làm cache không bao giờ xóa, listener đăng ký mà
 * không gỡ, và trong Spring Boot là bean singleton lỡ giữ dữ liệu của
 * từng request. GC không cứu được, vì theo đúng luật ở mục 8 thì những
 * object đó VẪN ĐANG ĐƯỢC THAM CHIẾU.
 * - GC CHỈ LO BỘ NHỚ, KHÔNG LO TÀI NGUYÊN KHÁC. File đang mở, socket,
 * connection database thì phải đóng tường minh, và cách đúng là
 * try-with-resources. Method finalize() của thời xưa nay đã hỏng hẳn -
 * biên dịch thử trên JDK 25 này cho đúng cảnh báo:
 * warning: [removal] finalize() in Object has been deprecated and
 * marked for removal
 * - SO VỚI TYPESCRIPT: JS cũng có GC và cũng gán tham chiếu y hệt, nên
 * trực giác mục 4 và mục 8 đã có sẵn. Bốn khác biệt phải nhớ: JS có cả
 * null lẫn undefined còn Java chỉ có null; toán tử === của JS so sánh
 * CHUỖI theo giá trị, còn == của Java so sánh theo tham chiếu - đây đúng
 * là chỗ cây cầu trực giác bị gãy; Java KHÔNG có optional chaining "?."
 * nên phải tự kiểm tra null hoặc dùng Optional; và private của Java do
 * compiler thi hành thật, khác private của TS vốn chỉ tồn tại lúc biên
 * dịch.
 * <p>
 * Best practice: null là thứ nên tránh để lọt ra ngoài chứ không phải thứ
 * để kiểm tra khắp nơi - chặn nó ngay tại constructor như bài trước, và ở
 * ranh giới API thì dùng Optional để nói rõ "giá trị này có thể vắng mặt"
 * thay vì trả null rồi bắt người gọi tự đoán. Luôn dùng equals để so sánh
 * nội dung, chỉ dùng == khi thật sự muốn hỏi về danh tính object. Biến
 * tham chiếu nên khai báo càng gần chỗ dùng càng tốt và để phạm vi hẹp
 * nhất có thể, vừa dễ đọc vừa cho GC dọn sớm. Đừng tự tối ưu bộ nhớ bằng
 * cách tái dùng object - JVM xử lý object đời ngắn cực rẻ, còn một object
 * dùng chung bị sửa lung tung mới là bug thật. Và đừng bao giờ dựa vào GC
 * để đóng tài nguyên ngoài bộ nhớ: file, socket, connection thì
 * try-with-resources, không có ngoại lệ. Về phía class: mặc định mọi thành
 * viên để private rồi mở dần khi có nhu cầu thật, vì mở thì dễ mà thu lại
 * thì phá code của người khác. Dùng static cho HẰNG SỐ và cho hàm thuần tuý
 * tính toán, tuyệt đối không dùng cho trạng thái thay đổi được. Giữ static
 * block càng đơn giản càng tốt và không bao giờ cho nó đụng vào tài nguyên
 * bên ngoài. Cuối cùng, khi có nhiều constructor thì để this(...) nối chúng
 * về một bản duy nhất gán field, thay vì dùng initializer block - cả hai
 * đều chạy đúng, nhưng this(...) thì người đọc nhìn là thấy, còn initializer
 * block thì ẩn, người đọc phải tự nhớ rằng nó được chép vào mọi constructor.
 */
public class t4_Objects {

    void main() {
        // ==============================================================
        // TRANG 1 - CREATING AND USING OBJECTS
        // ==============================================================

        // TODO 1 (muc 1, 2): khai bao "SanPham sp;" ma KHONG gan gi, roi thu
        //   doc sp.ten -> doc thong bao loi that cua javac. Sau do moi gan
        //   bang new va chay lai.
        SanPham1 sp = new SanPham1();
        SanPham1 sp3 = new SanPham1();
        SanPham1 sp1 = sp;
        SanPham1 sp2 = sp;
        sp1.ten = "Thien";
        sp2.ten = "Dat";
        sp3.ten = "Lien";
        IO.println(sp.ten);
        IO.println(sp1.ten);
        IO.println(sp2.ten);
        IO.println(sp3.ten);
        // TODO 2 (muc 4): tao mot object, cho HAI bien cung tro vao no, sua
        //   field qua bien thu nhat roi in ra qua bien thu hai. Giai thich
        //   vi sao thay doi.

        // TODO 3 (muc 15): voi hai object KHAC NHAU nhung cung noi dung, in ra
        //   ket qua cua == . Doan truoc khi chay, roi doi chieu.

        // TODO 4 (muc 5, 6): goi field va method truc tiep tren ket qua cua
        //   new, khong qua bien trung gian - vi du new SanPham().gia

        // TODO 5 (muc 8): gan null cho mot bien dang tro toi object, roi thu
        //   dung lai bien do -> bat NullPointerException va in e.getMessage()
        //   de xem no co chi dung ten bien khong.

        // ==============================================================
        // TRANG 2 - MORE ON CLASSES
        // ==============================================================

        // TODO 6 (muc 12): them vao SanPham1 mot static field dem so object da
        //   tao, va mot instance field id rieng tung object. Trong constructor
        //   viet "id = ++soSanPham" theo dung idiom cua trang. Tao ba object
        //   roi in ca id lan SanPham1.soSanPham, de thay mot ben moi object
        //   mot gia tri, con ben kia dung chung mot o nho.

        // TODO 7 (muc 12): trong SanPham1 viet mot static method roi thu doc
        //   thang field "ten" trong do, va thu dung "this". Doc hai thong bao
        //   loi cua javac - chung noi rat ro ly do.

        // TODO 8 (muc 13): them mot hang so "static final double THUE_VAT" dat
        //   ten theo dung quy uoc VIET_HOA, roi thu gan lai gia tri cho no de
        //   xem compiler noi gi.

        // TODO 9 (muc 14): them vao SanPham1 ca static block lan instance
        //   initializer block, moi khoi in mot dong. DOAN TRUOC thu tu in ra
        //   the nao roi moi chay doi chieu. Chu y static block chay may lan
        //   khi tao hai object.

        // TODO 10 (muc 11): doi field cua SanPham1 thanh private, build lai va
        //   doc loi. Sau do them getter de truy cap lai - do chinh la ly do
        //   ton tai cua getter.
    }
}

class SanPham1 {
    String ma;
    String ten;
    double gia;

    SanPham1(String ma, String ten, double gia) {
        this.ma = ma;
        this.ten = ten;
        this.gia = gia;
    }

    SanPham1() {
    }
}
