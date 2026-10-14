package com.javacore.language.oop.classes_objects;

/**
 * Providing Constructors for your Classes
 * <p>
 * 0. Vị trí bài này
 * - Bài 1 dạy TRẠNG THÁI (class, field), bài 2 dạy HÀNH VI (method).
 * - Bài này trả lời câu hỏi còn lại: object RA ĐỜI như thế nào, và ai được
 * phép cho nó ra đời.
 * - Trang gốc rất ngắn, gần như không có code block mới - nó dùng lại class
 * Bicycle của bài Creating Classes. Phần ví dụ là chỗ để tự viết.
 * <p>
 * 1. Constructor là gì - giống method, TRỪ ĐÚNG HAI ĐIỂM
 * - Nguyên văn: "Constructor declarations look like method declarations -
 * except that they use the name of the class and have no return type".
 * - Nghĩa là: TÊN phải trùng tên class, và KHÔNG CÓ kiểu trả về. Không
 * phải void, mà là không viết gì cả.
 * - Hệ quả của bẫy này: viết "public void Bicycle()" thì compiler KHÔNG
 * báo lỗi, nó chỉ coi đó là một method bình thường tên Bicycle. Class
 * coi như vẫn chưa có constructor nào. Lỗi im lặng, rất khó nhìn ra.
 * <p>
 * 2. Gọi constructor - bằng toán tử new
 * - "A constructor is called by the new operator". Lời gọi new Bicycle(30,
 * 0, 8) làm hai việc: cấp phát bộ nhớ cho object mới, rồi khởi tạo field.
 * - Constructor không được gọi trực tiếp như method thường. Nó chỉ chạy
 * qua new, hoặc qua this(...) / super(...) ở mục 6.
 * <p>
 * 3. Overloading constructor - cùng luật với overloading method
 * - Một class được có nhiều constructor. Trang nói rõ "Although Bicycle
 * only has one constructor, it could have others, including a no-argument
 * constructor": Bicycle yourBike = new Bicycle();
 * - Nền tảng Java phân biệt chúng bằng "the number of arguments in the list
 * and their types" - SỐ LƯỢNG và KIỂU đối số.
 * - Vì vậy "you cannot write two constructors that have the same number and
 * type of arguments for the same class, because the compiler would not be
 * able to tell them apart".
 * - Giống method ở mục 2 bài trước: TÊN THAM SỐ không tính. Hai constructor
 * Bicycle(int a) và Bicycle(int b) là trùng nhau, lỗi biên dịch.
 * <p>
 * 4. DEFAULT CONSTRUCTOR - cái bẫy lớn nhất của bài
 * - "The compiler automatically provides a no-argument, default constructor
 * for any class without constructors".
 * - Đọc kỹ cụm "without constructors": compiler chỉ tặng khi class KHÔNG
 * CÓ constructor nào. Vừa tự viết một constructor có tham số là bản no-arg
 * BIẾN MẤT, và new MyClass() đang chạy tốt bỗng thành lỗi biên dịch.
 * - Default constructor đó sẽ gọi no-argument constructor của lớp cha.
 * - "If your class has no explicit superclass, then it has an implicit
 * superclass of Object, which does have a no-argument constructor" - nên
 * với class thường thì luôn an toàn.
 * - Cảnh báo của trang: nếu lớp cha KHÔNG có no-arg constructor mà lớp con
 * lại không viết constructor nào, chương trình sẽ không biên dịch được.
 * <p>
 * 5. Access modifier trên constructor
 * - "You can use access modifiers in a constructor's declaration to control
 * which other classes can call the constructor".
 * - Đây là chỗ mà modifier mang ý nghĩa khác hẳn khi đặt trên field hay
 * method: nó không kiểm soát việc ĐỌC/GHI, nó kiểm soát việc AI ĐƯỢC
 * PHÉP TẠO RA object này.
 * - Constructor private nghĩa là bên ngoài không new được - nền tảng của
 * singleton, của static factory method và của utility class.
 * <p>
 * 6. super(...) - trang chỉ nhắc tên
 * - Trang nói "you can use a superclass constructor yourself" và trỏ sang
 * ví dụ MountainBike. Chi tiết nằm ở section Inheritance phía sau, ở đây
 * chỉ cần nhớ: constructor của lớp con luôn kéo theo một lời gọi tới
 * constructor của lớp cha, viết tường minh hay không cũng vậy.
 * <p>
 * 7. Ghi chú ngoài tài liệu
 * - this(...) GỌI CONSTRUCTOR ANH EM trong cùng class. Đây là cách chuẩn
 * để các bản nạp chồng không chép lại logic của nhau: bản ít tham số điền
 * giá trị mặc định rồi ủy quyền cho bản đầy đủ nhất. Chỉ có ĐÚNG MỘT
 * constructor thật sự gán field, các bản còn lại chỉ là lối vào.
 * - THỨ TỰ CỦA this()/super() ĐÃ ĐỔI Ở JAVA MỚI. Sách và bài viết cũ đều
 * nói chúng bắt buộc là câu lệnh ĐẦU TIÊN. Từ JDK 25 (chính là bản
 * javac 25 của repo này) đã có flexible constructor bodies: được đặt lệnh
 * trước super(). Đã thử biên dịch thật đoạn "if (x < 0) throw ...;
 * super(x);" trên máy và nó PASS. Lợi ích thực tế là validate tham số
 * được TRƯỚC khi lớp cha kịp chạy.
 * Trang More on Classes (xem note bài t4, mục 10) nói rõ hơn về giới hạn:
 * phần trước lời gọi gọi là PROLOGUE, trong đó KHÔNG dùng được instance
 * đang dựng - trừ khi đang khởi tạo instance field - và tuyệt đối không
 * gọi được instance method nào.
 * - KHÔNG GỌI METHOD CÓ THỂ BỊ OVERRIDE BÊN TRONG CONSTRUCTOR. Lúc
 * constructor lớp cha chạy thì field của lớp con CHƯA được khởi tạo. Nếu
 * nó gọi một method mà lớp con đã override, bản của lớp con sẽ chạy trên
 * một object dở dang và đọc phải null hoặc 0. Bug này rất khó truy.
 * - CONSTRUCTOR LÀ CỔNG GÁC TÍNH HỢP LỆ. Đây là lý do quan trọng nhất để
 * ưu tiên constructor có tham số: object không thể tồn tại ở trạng thái
 * sai, vì muốn sinh ra nó phải đi qua cửa kiểm tra. Ném
 * IllegalArgumentException ngay trong constructor tốt hơn nhiều so với
 * để một object thiếu dữ liệu đi lang thang qua năm tầng rồi mới nổ NPE.
 * - CHUỖI CONSTRUCTOR DÀI DẦN LÀ MÙI CODE. Khi thấy mình viết tới bản thứ
 * tư thứ năm chỉ khác nhau vài tham số (telescoping constructor), và nhất
 * là khi có nhiều tham số cùng kiểu đứng cạnh nhau, đó là lúc nghĩ tới
 * static factory method có tên nói rõ ý đồ, hoặc builder.
 * - TRONG SPRING BOOT, CONSTRUCTOR INJECTION LÀ CHUẨN. Class chỉ có một
 * constructor thì không cần @Autowired, Spring tự hiểu. Dependency khai
 * báo final nên không ai đổi được sau khi bean dựng xong, viết unit test
 * chỉ cần new thẳng với mock, và vòng phụ thuộc vòng tròn bị phát hiện
 * ngay lúc khởi động chứ không âm thầm như field injection.
 * - JPA/HIBERNATE VÀ JACKSON LẠI ĐÒI NO-ARG CONSTRUCTOR để dựng object
 * bằng reflection. Đây chính là mục 4 đập vào mặt trong dự án thật: vừa
 * thêm một constructor đầy đủ vào entity là lúc chạy nổ lỗi không tìm
 * thấy constructor mặc định. Cách xử lý quen thuộc là khai báo thêm một
 * constructor no-arg để mức protected - đủ cho framework dùng, nhưng
 * chặn code nghiệp vụ tạo ra entity rỗng.
 * - SO VỚI TYPESCRIPT: TS chỉ có MỘT thân constructor duy nhất, muốn nhiều
 * cách khởi tạo thì phải khai báo overload signature rồi tự if/else bên
 * trong. Java overload thật, mỗi bản một thân riêng. Chiều ngược lại,
 * TS có parameter properties viết gọn "constructor(private ten: string)"
 * tự tạo luôn field - Java KHÔNG có, luôn phải this.ten = ten. Thứ gần
 * nhất là record, học ở section sau.
 * <p>
 * Best practice: mỗi object sinh ra phải ở trạng thái DÙNG ĐƯỢC NGAY, nên
 * cái gì bắt buộc phải có thì đưa vào tham số constructor, đừng để object
 * ra đời rỗng rồi trông chờ người gọi nhớ setter. Giữ constructor THUẦN
 * KHỞI TẠO - gán field và kiểm tra tính hợp lệ, không gọi service, không
 * đọc file, không mở kết nối, vì một constructor nặng làm class gần như
 * không test được. Khi có nhiều bản nạp chồng, chỉ một bản gán field thật,
 * các bản khác ủy quyền bằng this(...) để không có hai nguồn sự thật. Và
 * field nào không có lý do thay đổi thì để final ngay - compiler sẽ bắt
 * buộc mọi nhánh constructor đều gán nó, đó là tấm lưới an toàn miễn phí.
 */
public class t3_Constructors {

    void main() {
        // TODO 1 (muc 1, 2): tu viet class SanPham co field ma, ten, gia.
        //   Viet constructor day du ba tham so roi tao object bang new.
        //   Thu doi "SanPham(...)" thanh "void SanPham(...)" de thay compiler
        //   KHONG bao loi - va giai thich duoc vi sao.
        SanPham banPhim = new SanPham("SP01", "Ban phim S2001", 250000);
        SanPham noName = new SanPham();
        // TODO 2 (muc 3): them mot constructor no-arg dat gia mac dinh,
        //   roi thu them mot constructor nua trung so luong + kieu tham so
        //   de doc thong bao loi that cua javac.

        // TODO 3 (muc 4): tao mot class khong viet constructor nao, goi
        //   new ... () thay chay duoc. Sau do them constructor co tham so
        //   vao chinh class do -> lenh new cu phai hong. Ghi lai loi.

        // BUOC 1: class KhachHang o cuoi file KHONG co constructor nao, vay
        // ma van new () duoc. Do la ban DEFAULT compiler tang.
        KhachHang kh = new KhachHang();
        IO.println("TODO 3 buoc 1: new KhachHang() chay duoc, ten = " + kh.ten);

        // BUOC 2: bo comment dong constructor trong class KhachHang roi build
        // lai. Dong "new KhachHang()" NGAY TREN se gay, du khong ai dung vao
        // no. Loi that cua javac:
        //
        //   error: constructor KhachHang in class KhachHang cannot be applied
        //   to given types;
        //     required: String
        //     found:    no arguments
        //     reason: actual and formal argument lists differ in length
        //
        // Chi THEM mot constructor, ma mot constructor khac BIEN MAT. Day la
        // ly do them constructor vao class dang duoc noi khac dung la mot
        // thay doi PHA VO TUONG THICH, du truc giac thay "them thi sao ma vo".

        // TODO 4 (muc 7): dung this(...) de constructor no-arg uy quyen cho
        //   constructor day du, thay vi chep lai phan gan field.

        // TODO 5 (muc 5, 7): them kiem tra gia < 0 thi nem
        //   IllegalArgumentException ngay trong constructor.
    }
}

class SanPham {
    String ma;
    String ten;
    double gia;

    SanPham(String ma, String ten, double gia) {
        this.ma = ma;
        this.ten = ten;
        this.gia = gia;
    }

    SanPham() {
    }

}

// TODO 3: class TRANG - khong co constructor nao, nen compiler tang cho mot
// ban no-arg. Khong phai noi suong: chay javap -p len file .class se thay
// dong "KhachHang();" nam trong do du khong ai go ra no.
//
//   javap -p target/classes/com/javacore/.../KhachHang.class
//   class KhachHang {
//     java.lang.String ten;
//     KhachHang();          <- ctor VO HINH, co that trong bytecode
//   }
class KhachHang {
    String ten;

    // BUOC 2: bo comment dong duoi roi build lai -> new KhachHang() o main()
    // gay ngay. Build lai javap se thay "KhachHang();" da bien mat.
    // KhachHang(String ten) { this.ten = ten; }
}


