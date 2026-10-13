package com.javacore.language.oop.classes;

/**
 * Objects, Classes, Interfaces, Packages, and Inheritance
 * <p>
 * 0. Vị trí của bài này - bài TỔNG QUAN, không dạy cú pháp
 * - Trang này chỉ dựng từ vựng cho cả nhóm OOP: object, class, inheritance,
 * interface, package. Code trên trang là snippet minh họa, cố tình không
 * đầy đủ để chạy.
 * - Cú pháp thật bắt đầu từ bài sau là Classes and Objects. Đọc bài này để
 * hiểu VÌ SAO, không phải để gõ theo.
 * <p>
 * 1. Object là gì - gói TRẠNG THÁI và HÀNH VI vào một chỗ
 * - Mọi object đều gồm hai phần: trạng thái lưu trong FIELD, hành vi thể
 * hiện bằng METHOD.
 * - Ví dụ của trang: con chó có tên và màu lông là trạng thái, sủa và chạy
 * là hành vi. Xe đạp có số và tốc độ là trạng thái, phanh và tăng tốc là
 * hành vi.
 * - Cách tìm object trong bài toán thật: nhìn quanh, vật nào có thuộc tính
 * đi kèm việc nó làm được thì vật đó là ứng viên thành object.
 * <p>
 * 2. ĐÓNG GÓI - khái niệm quan trọng nhất của bài
 * - Giấu trạng thái bên trong, bắt mọi tương tác phải đi qua method. Trang
 * gọi đây là "a fundamental principle of object-oriented programming".
 * - Lợi ích thực tế: object tự kiểm soát cách người khác dùng mình. Ví dụ
 * của trang là method đổi số có thể TỪ CHỐI giá trị ngoài khoảng 1 tới 6,
 * thay vì để bên ngoài gán bậy vào field.
 * - Bốn lợi ích trang liệt kê:
 * MODULARITY - code của từng object độc lập, mang đi nơi khác được.
 * INFORMATION HIDING - đổi phần cài đặt bên trong không ảnh hưởng bên ngoài.
 * CODE REUSE - dùng lại object có sẵn thay vì viết lại từ đầu.
 * PLUGGABILITY và DEBUGGING - object nào hỏng thì tháo ra thay, không phải
 * sửa cả hệ thống.
 * <p>
 * 3. Class là gì - BẢN THIẾT KẾ, object là sản phẩm đúc ra từ bản thiết kế
 * - Hàng nghìn chiếc xe đạp cùng kiểu được sản xuất từ một bộ bản vẽ. Class
 * là bộ bản vẽ đó, mỗi chiếc xe là một object.
 * - Ví dụ Bicycle trên trang có field cadence, speed, gear và method
 * changeCadence, changeGear, speedUp.
 * - Điểm dễ bỏ qua: class Bicycle KHÔNG có method main. Nó là bản thiết kế
 * chứ không phải chương trình chạy được. Class riêng BicycleDemo mới chứa
 * main để tạo hai object Bicycle và gọi method của chúng.
 * - Hai object cùng class nhưng trạng thái ĐỘC LẬP: gọi speedUp trên chiếc
 * này không làm chiếc kia nhanh lên.
 * <p>
 * 4. Inheritance là gì - gom phần chung lên trên, chỉ viết phần khác biệt
 * - Xe đạp địa hình, xe đua, xe đôi đều có speed, cadence, gear. Phần chung
 * đó đặt ở lớp cha Bicycle, mỗi lớp con chỉ thêm cái riêng của mình: xe
 * đôi có hai yên, xe đua có ghi đông cong.
 * - Từ khóa là EXTENDS.
 * - Quy tắc của Java: mỗi class có ĐÚNG MỘT superclass trực tiếp, nhưng một
 * superclass được phép có không giới hạn subclass.
 * - Cái lợi: code lớp con ngắn, chỉ tập trung vào phần đặc thù.
 * - Cái giá mà trang có nói rõ: code kế thừa KHÔNG xuất hiện trong file của
 * lớp con, nên người đọc không thấy nó. Vì vậy phải viết tài liệu cẩn thận.
 * <p>
 * 5. Interface là gì - HỢP ĐỒNG về hành vi, có compiler đứng ra bảo đảm
 * - Interface là một nhóm method CHỈ CÓ CHỮ KÝ, không có thân.
 * - Nó mô tả object giao tiếp với thế giới bên ngoài bằng những việc gì, mà
 * không nói việc đó làm thế nào.
 * - Từ khóa là IMPLEMENTS. Khi một class tuyên bố implements, đó là lời hứa
 * sẽ cung cấp đủ mọi method mà interface đòi.
 * - Điểm cốt lõi: lời hứa này được COMPILER KIỂM TRA. Thiếu một method là
 * không biên dịch được, chứ không phải tới lúc chạy mới phát hiện.
 * - Ví dụ trên trang là ACMEBicycle implements một interface xe đạp.
 * <p>
 * 6. Package là gì - KHÔNG GIAN TÊN để tổ chức class
 * - Package gom các class và interface liên quan lại, vai trò giống thư mục
 * trong máy tính.
 * - Lý do tồn tại: dự án thật có hàng trăm tới hàng nghìn class, không chia
 * nhóm thì không quản lý nổi và tên sẽ đụng nhau.
 * - Java Platform API là tập hợp khổng lồ các package có sẵn. Trang dẫn ba
 * ví dụ: String cho chuỗi ký tự, File cho thao tác tệp, Socket cho kết nối
 * mạng.
 * - Thông điệp của trang: việc cần làm trước tiên là TRA API xem đã có sẵn
 * chưa, thay vì tự dựng lại hạ tầng. Tài liệu tra cứu chính thức là Java
 * Platform API Specification.
 * <p>
 * 7. Ghi chú ngoài tài liệu - trang này KHÔNG nhắc tới "bốn tính chất OOP"
 * - Cách gọi bốn trụ cột là quy ước của giáo trình và câu hỏi phỏng vấn,
 * không phải thuật ngữ trong tài liệu Oracle hay trong JLS. Cả section OOP
 * của dev.java không có trang nào liệt kê chúng.
 * - Đối chiếu để khỏi hụt kiến thức:
 * ĐÓNG GÓI nằm ở mục 2 phía trên, cú pháp ở bài Classes and Objects phần
 * access modifier.
 * KẾ THỪA nằm ở mục 4, chi tiết ở bài Inheritance.
 * ĐA HÌNH không có ở bài này, nằm ở trang con Polymorphism của bài
 * Inheritance.
 * TRỪU TƯỢNG cũng không có ở bài này, rải ở trang Abstract Methods and
 * Classes và cả bài Interfaces.
 * - Hai điều bài này chưa nói nhưng sẽ gặp ngay sau: mọi class đều ngầm kế
 * thừa Object nếu không extends gì khác, và tuy chỉ được extends một class
 * nhưng được implements NHIỀU interface - đó là cách Java né bài toán đa
 * kế thừa.
 * <p>
 * Best practice: thiết kế class bắt đầu từ câu hỏi "object này chịu trách
 * nhiệm gì", không phải "nó có những dữ liệu nào". Field để private làm mặc
 * định rồi mới cân nhắc mở ra, vì mở thì dễ mà thu lại thì phá vỡ code người
 * khác. Ưu tiên interface để mô tả hành vi và chỉ dùng kế thừa khi quan hệ
 * đúng là "cái này LÀ MỘT loại của cái kia"; lạm dụng extends chỉ để dùng ké
 * code là nguồn gốc của những cây kế thừa không ai sửa nổi.
 */
public class t1_Classes {
    int tuoi;                              // instance field

//    static void main() {
//        t1_Classes nguoi = new t1_Classes();
//        System.out.println(nguoi.tuoi);          // LỖI BIÊN DỊCH
//    }

    void main() {
        System.out.println(this.tuoi);          // LỖI BIÊN DỊCH
    }
}
