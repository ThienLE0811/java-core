package com.javacore.language.oop.classes_objects;

/**
 * Creating Classes
 * <p>
 * 0. Vị trí bài này - bắt đầu phần CÚ PHÁP THẬT
 * - Bài tổng quan trước chỉ dựng từ vựng. Từ đây mới là gõ code.
 * - Trang này trả lời đúng hai câu hỏi: khai báo một class viết thế nào, và
 * khai báo dữ liệu bên trong nó viết thế nào.
 * <p>
 * 1. Khai báo class - năm phần, ĐÚNG THỨ TỰ này
 * - Trang thừa nhận dòng khai báo đầy đủ trông "quite complicated", nhưng
 * thực chất chỉ là năm ô ghép lại:
 * MODIFIER (tùy chọn) - public, private... Lưu ý private CHỈ dùng được cho
 * class lồng bên trong class khác, không dùng cho class cấp cao nhất.
 * TÊN CLASS (bắt buộc) - quy ước viết hoa chữ cái đầu.
 * EXTENDS (tùy chọn) - chỉ được MỘT lớp cha duy nhất.
 * IMPLEMENTS (tùy chọn) - được NHIỀU interface, ngăn nhau bởi dấu phẩy.
 * THÂN CLASS (bắt buộc) - phần nằm trong cặp ngoặc nhọn.
 * - Thứ tự này là bắt buộc. Viết implements trước extends là lỗi biên dịch.
 * <p>
 * 2. Thân class chứa gì
 * - Trang mô tả đây là nơi chứa "all the code that provides for the life
 * cycle of the objects created from the class": constructor để sinh ra
 * object, field để giữ trạng thái, method để thực hiện hành vi.
 * <p>
 * 3. Ba loại biến - đọc kỹ chỗ này vì tên gọi hay bị dùng lẫn
 * - FIELD, còn gọi là member variable: khai báo ngay trong thân class.
 * - LOCAL VARIABLE: khai báo bên trong method hoặc trong một khối lệnh.
 * - PARAMETER: biến nằm trong dấu ngoặc của khai báo method.
 * - Ba loại này khác nhau về phạm vi sống và về quy tắc khởi tạo, xem mục 7.
 * <p>
 * 4. Khai báo field - đúng ba thành phần
 * - Không hoặc nhiều MODIFIER, rồi KIỂU DỮ LIỆU, rồi TÊN.
 * - Ví dụ của trang là class Bicycle với ba field cùng kiểu int: cadence,
 * gear và speed.
 * <p>
 * 5. Access modifier - trang này mới nói hai mức
 * - PUBLIC nghĩa là "the field is accessible from all classes".
 * - PRIVATE nghĩa là "the field is accessible only within its own class".
 * - Quy tắc thực hành mà trang nêu: để field PRIVATE, rồi mở ra bên ngoài
 * bằng method public. Đây chính là đóng gói nói ở bài tổng quan, lần này
 * ở dạng cú pháp cụ thể.
 * - Lý do: truy cập gián tiếp qua method thì object còn chặn được giá trị
 * sai. Truy cập thẳng vào field thì không chặn được gì.
 * <p>
 * 6. Kiểu và quy ước đặt tên
 * - Field dùng được cả kiểu nguyên thủy (int, float, boolean) lẫn kiểu tham
 * chiếu (String, mảng, object khác).
 * - Quy ước đặt tên giống biến thường, với hai ngoại lệ trang nhắc riêng:
 * TÊN CLASS viết hoa chữ cái đầu, TÊN METHOD nên bắt đầu bằng ĐỘNG TỪ.
 * <p>
 * 7. Ghi chú ngoài tài liệu - những điều trang không nói mà sẽ gặp ngay
 * - FIELD CÓ GIÁ TRỊ MẶC ĐỊNH, LOCAL VARIABLE THÌ KHÔNG. Khai báo
 * "int tuoi;" trong class rồi in ra sẽ được 0 chứ không lỗi, vì số về 0,
 * boolean về false, kiểu tham chiếu về null. Nhưng khai báo "int tuoi;"
 * trong method rồi dùng ngay là LỖI BIÊN DỊCH. Đây là nguồn nhầm lẫn rất
 * hay gặp khi mới học.
 * - CÓ BỐN MỨC TRUY CẬP chứ không phải hai. Ngoài public và private còn có
 * protected, và mức mặc định khi KHÔNG viết gì gọi là package-private,
 * nghĩa là chỉ các class cùng package thấy được. Bảng đầy đủ nằm ở trang
 * More on Classes phía sau.
 * - MỘT FILE CHỈ ĐƯỢC MỘT CLASS public, và tên file phải trùng tên class đó.
 * Đây là luật của trình biên dịch, không phải quy ước.
 * - FINAL dùng cho field nghĩa là gán một lần rồi không đổi được nữa. Field
 * nào không có lý do phải thay đổi thì nên để final ngay từ đầu.
 * <p>
 * Best practice: mặc định mọi field là private, và chỉ mở ra khi thực sự có
 * ai đó cần. Mở thì dễ, thu lại thì phá vỡ code của người khác. Đừng sinh
 * getter và setter theo phản xạ cho mọi field - một class mà field nào cũng
 * có đủ cặp getter setter thì thực chất vẫn là public, chỉ dài dòng hơn;
 * hãy hỏi "bên ngoài CẦN làm gì với dữ liệu này" rồi mới viết đúng method
 * phục vụ việc đó. Kiểu dữ liệu chọn sát nghĩa nhất có thể, vì kiểu sai cho
 * phép biểu diễn những trạng thái lẽ ra không được tồn tại.
 */
public class t1_Creating_Classes {
    void main() {
        var bike = new Bicycle(10, 20, 30);
        IO.println(bike.cadence + " " + bike.gear + " " + bike.speed);
        bike.speedUp(5);
    }
}


class Bicycle {

    // the Bicycle class has
    // three fields
    public int cadence;
    public int gear;
    public int speed;

    // the Bicycle class has
    // one constructor
    public Bicycle(int startCadence, int startSpeed, int startGear) {
        gear = startGear;
        cadence = startCadence;
        speed = startSpeed;
    }

    // the Bicycle class has
    // four methods
    public void setCadence(int newValue) {
        cadence = newValue;
    }

    public void setGear(int newValue) {
        gear = newValue;
    }

    public void applyBrake(int decrement) {
        speed -= decrement;
    }

    public void speedUp(int increment) {
        speed += increment;
    }
}
