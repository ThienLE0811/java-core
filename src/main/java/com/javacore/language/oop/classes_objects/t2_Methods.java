package com.javacore.language.oop.classes_objects;

/**
 * Defining Methods + Calling Methods and Constructors
 * <p>
 * 0. Vị trí bài này
 * - Bài trước dạy khai báo class và field, tức là phần TRẠNG THÁI.
 * - Bài này là phần HÀNH VI. Sau bài này mới viết được getter và toString.
 * - File gộp hai trang của lộ trình: Defining Methods ở mục 1 tới 5, và
 * Calling Methods and Constructors ở mục 6 tới 10.
 * <p>
 * 1. Khai báo method - sáu thành phần, đúng thứ tự này
 * - MODIFIER (tùy chọn) - public, private, static...
 * - KIỂU TRẢ VỀ (bắt buộc) - kiểu của giá trị trả ra, hoặc void nếu không
 * trả về gì.
 * - TÊN METHOD (bắt buộc) - theo quy tắc đặt tên định danh.
 * - DANH SÁCH THAM SỐ (bắt buộc cặp ngoặc) - từng tham số ghi kiểu rồi tên,
 * ngăn nhau bởi dấu phẩy. Không có tham số thì để ngoặc rỗng.
 * - DANH SÁCH EXCEPTION (tùy chọn) - phần throws, học ở section Exceptions.
 * - THÂN METHOD (bắt buộc) - nằm trong ngoặc nhọn, chứa cả biến cục bộ.
 * - Chỉ BỐN thứ là bắt buộc: kiểu trả về, tên, cặp ngoặc đơn, thân method.
 * <p>
 * 2. SIGNATURE - định nghĩa quan trọng nhất của bài
 * - Signature của một method chỉ gồm "the method's name and the parameter
 * types". Nghĩa là TÊN cộng với KIỂU CỦA CÁC THAM SỐ.
 * - KHÔNG bao gồm kiểu trả về. KHÔNG bao gồm modifier. KHÔNG bao gồm tên
 * của tham số.
 * - Vì tên tham số không tính, hai method sau có CÙNG signature:
 * tinh(int a) và tinh(int b).
 * <p>
 * 3. Đặt tên method - động từ đứng đầu
 * - Một từ thì viết thường toàn bộ, nhiều từ thì camelCase với từ đầu viết
 * thường. Từ đầu tiên nên là ĐỘNG TỪ vì method mô tả một hành động.
 * - Trong một class, tên method thường là duy nhất, trừ trường hợp
 * overloading ở mục dưới.
 * <p>
 * 4. Overloading - nhiều method trùng tên, khác danh sách tham số
 * - Java phân biệt các bản nạp chồng bằng SỐ LƯỢNG và KIỂU của tham số.
 * - Lợi ích trang nêu: gọi draw cho mọi thứ vẽ được, thay vì phải bịa ra
 * drawString, drawInteger, drawFloat.
 * - Kiểu trả về KHÔNG dùng để phân biệt. Vì vậy không thể viết hai method
 * cùng signature mà khác kiểu trả về, đó là lỗi biên dịch.
 * <p>
 * 5. Cảnh báo của chính tài liệu
 * - Trang khuyên dùng overloading DÈ DẶT, vì "overloaded methods should be
 * used sparingly, as they can make code much less readable".
 * <p>
 * 6. PARAMETER và ARGUMENT là hai thứ khác nhau
 * - PARAMETER là biến ghi trong phần khai báo method.
 * - ARGUMENT là giá trị thật được truyền vào lúc GỌI method.
 * - Argument phải khớp parameter cả về KIỂU lẫn THỨ TỰ.
 * - Tiếng Việt hay gọi gộp cả hai là "tham số" nên dễ lẫn, nhưng tài liệu
 * và thông báo lỗi của javac phân biệt rõ hai từ này.
 * <p>
 * 7. Kiểu của tham số và varargs
 * - Tham số nhận mọi kiểu: nguyên thủy (int, double...) hoặc tham chiếu
 * (object, mảng). Ví dụ của trang là method nhận một mảng Point để dựng
 * một Polygon.
 * - VARARGS cho phép truyền số lượng đối số tùy ý mà không phải tự tạo
 * mảng. Cú pháp là ba dấu chấm, một dấu cách, rồi tên tham số.
 * - Bên trong method, tham số varargs hoạt động y như một MẢNG.
 * - Ví dụ quen thuộc nhất chính là printf, nhận số lượng object tùy ý.
 * <p>
 * 8. Tên tham số và che khuất field
 * - Tên tham số phải là duy nhất trong phạm vi của nó, không trùng với
 * tham số khác và không trùng với biến cục bộ.
 * - Nhưng tham số ĐƯỢC PHÉP trùng tên với field của class. Khi đó nó CHE
 * KHUẤT field, viết tên trần là chạm vào tham số chứ không phải field.
 * - Trang cảnh báo "shadowing fields can make your code difficult to read"
 * và nói rõ chỉ nên dùng trong CONSTRUCTOR và SETTER.
 * <p>
 * 9. Truyền kiểu NGUYÊN THỦY - truyền theo giá trị
 * - Method nhận một BẢN SAO của giá trị. Sửa tham số bên trong method chỉ
 * có tác dụng trong phạm vi method đó.
 * - Method kết thúc là mọi thay đổi trên tham số biến mất, biến bên ngoài
 * không hề hấn gì.
 * <p>
 * 10. Truyền kiểu THAM CHIẾU - vẫn là truyền theo giá trị
 * - Đây là mục khó nhất của cả bước 8, đọc kỹ.
 * - Object cũng được truyền theo giá trị. Thứ được SAO CHÉP là cái THAM
 * CHIẾU, không phải object. Sau lời gọi, "the passed-in reference still
 * references the same object as before".
 * - Hệ quả có hai vế ngược nhau:
 * SỬA RUỘT OBJECT thì bên ngoài THẤY, vì cả hai tham chiếu cùng trỏ tới
 * một object duy nhất.
 * GÁN LẠI tham chiếu bên trong method thì bên ngoài KHÔNG THẤY, vì chỉ
 * bản sao tham chiếu bị đổi hướng.
 * <p>
 * 11. Ghi chú ngoài tài liệu
 * - JAVA LUÔN LUÔN TRUYỀN THEO GIÁ TRỊ, không có pass by reference. Đây là
 * câu phỏng vấn kinh điển và rất nhiều người trả lời sai. Object không
 * phải ngoại lệ, chỉ là cái được sao chép là tham chiếu chứ không phải
 * nội dung object. Cách nhớ: method đổi được RUỘT, không đổi được HƯỚNG.
 * - JS VÀ TYPESCRIPT HOẠT ĐỘNG Y HỆT. Truyền object vào hàm rồi sửa thuộc
 * tính thì bên ngoài thấy, còn gán lại cả biến thì không. Trực giác đã có
 * sẵn, chỉ là Java có tên gọi chính xác hơn cho nó.
 * - VARARGS CÓ BA LUẬT phải nhớ: phải là tham số CUỐI CÙNG, mỗi method chỉ
 * được MỘT, và gọi với KHÔNG đối số nào cũng hợp lệ nên bên trong luôn
 * phải chuẩn bị cho mảng rỗng. Truyền thẳng một mảng vào cũng được.
 * - VARARGS CỘNG OVERLOAD DỄ GÂY MƠ HỒ, vì varargs chỉ được compiler xét ở
 * lượt cuối cùng. Thêm một bản varargs vào class đã có sẵn nhiều bản nạp
 * chồng là cách nhanh nhất để tạo ra lời gọi khó đoán.
 * - CHE KHUẤT FIELD CÓ MỘT CHỖ DÙNG ĐÚNG duy nhất là this.x = x trong
 * constructor và setter. Đó là idiom chuẩn, ai đọc cũng hiểu. Ngoài hai
 * chỗ đó mà trùng tên thì là lỗi, vì người đọc không biết đang nói tới
 * field hay tham số.
 * - TRUYỀN OBJECT CÓ THỂ THAY ĐỔI VÀO METHOD LÀ NGUỒN BUG THỰC TẾ. Method
 * nhận object rồi lặng lẽ sửa ruột nó, nơi gọi không hề biết. Trong ứng
 * dụng Spring Boot điều này hay xảy ra khi một entity hoặc DTO đi qua
 * nhiều tầng service. Hai cách phòng: làm object BẤT BIẾN, hoặc sao chép
 * phòng vệ trước khi giữ lại tham chiếu của bên ngoài.
 * - COMPILER KHÔNG CẢNH BÁO GÌ về việc method sửa ruột tham số. Tên method
 * là manh mối duy nhất người đọc có, nên tên phải trung thực: method tên
 * kiểm tra hay lấy dữ liệu mà lại sửa trạng thái là cái bẫy.
 * <p>
 * Best practice: mỗi method làm ĐÚNG MỘT việc và tên method phải nói đúng
 * việc đó - method tên getX mà lại sửa dữ liệu là cái bẫy cho người đọc sau.
 * Tham số càng ít càng tốt; danh sách dài toàn cùng một kiểu là nguồn bug
 * âm thầm vì gọi sai thứ tự mà compiler không phát hiện được, lúc đó nên
 * nghĩ tới việc gom chúng vào một kiểu riêng. Thân method dài quá một màn
 * hình thường là dấu hiệu nó đang làm nhiều việc, tách ra sẽ dễ đọc và dễ
 * test hơn. Mặc định coi tham số là CHỈ ĐỌC - nhận vào thì đọc rồi trả ra
 * kết quả mới, đừng sửa ruột object của người gọi; nếu buộc phải sửa thì
 * nói rõ ngay trong tên method. Varargs chỉ dùng khi số lượng đối số thật
 * sự không đoán trước được, còn lại cứ nhận thẳng List cho rõ ràng.
 */
public class t2_Methods {

    void main() {
        // Muc 6: "diem" ben duoi la ARGUMENT, con "soDiem" trong khai bao
        // method tangDiem la PARAMETER.
        int diem = 10;
        tangDiem(diem);
        IO.println("Muc 9  - sau tangDiem : " + diem);

        HocSinh hs = new HocSinh("An", 10);

        // Muc 10 - ve thu nhat: sua RUOT object -> ben ngoai THAY
        suaRuot(hs);
        IO.println("Muc 10a- sau suaRuot  : " + hs.diem);

        // Muc 10 - ve thu hai: gan lai HUONG -> ben ngoai KHONG thay
        ganLai(hs);
        IO.println("Muc 10b- sau ganLai   : " + hs.ten);

        // Muc 7: varargs - truyen bao nhieu doi so cung duoc, ke ca khong cai nao
        IO.println("Muc 7  - tong(1,2,3)  : " + tong(1, 2, 3));
        IO.println("Muc 7  - tong()       : " + tong());
    }

    // Muc 9: nguyen thuy truyen theo gia tri -> sua trong method roi mat
    void tangDiem(int soDiem) {
        soDiem += 1;
    }

    // Muc 10: doi duoc RUOT vi ca hai tham chieu cung tro toi mot object
    void suaRuot(HocSinh h) {
        h.diem = 99;
    }

    // Muc 10: KHONG doi duoc HUONG vi chi ban sao tham chieu bi doi huong
    void ganLai(HocSinh h) {
        h = new HocSinh("Binh", 0);
    }

    // Muc 7: varargs phai la tham so CUOI, ben trong hoat dong nhu mang
    int tong(int... so) {
        int ketQua = 0;
        for (int s : so) {
            ketQua += s;
        }
        return ketQua;
    }
}

class HocSinh {
    String ten;
    int diem;

    // Muc 8: tham so trung ten field -> che khuat. Day la mot trong hai cho
    // duy nhat nen dung che khuat: constructor va setter.
    HocSinh(String ten, int diem) {
        this.ten = ten;
        this.diem = diem;
    }
}
