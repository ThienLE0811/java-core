package com.javacore.language.constructs.basics;

/**
 * Branching with Switch Statements and Switch Expressions
 * <p>
 * 1. Switch Statement - dạng cổ điển, cú pháp case L:
 * - Là LỆNH: chọn nhánh để chạy, bản thân nó không trả về giá trị.
 * - Kiểu selector được phép: byte, short, char, int; các lớp bao tương ứng
 * Character, Byte, Short, Integer; enum; và String (từ Java SE 7).
 * - KHÔNG được phép: boolean, long, float, double.
 * - So sánh String trong switch hoạt động như dùng String.equals(), tức so
 * sánh NỘI DUNG và có phân biệt hoa thường.
 * - Selector là null thì ném NullPointerException, KHÔNG rơi vào default.
 * <p>
 * 2. Fall-through - đặc tính dễ gây bug nhất
 * - Khi khớp một case, Java chạy TẤT CẢ lệnh phía sau cho tới khi gặp break.
 * - Quên break là nguồn bug kinh điển; nhưng gom nhiều case cho cùng một xử lý
 * lại là cách dùng fall-through hợp lệ và phổ biến.
 * - Mọi case dùng CHUNG một phạm vi biến, nên biến khai báo ở case này vẫn
 * nhìn thấy được ở case sau.
 * <p>
 * 3. Switch Expression - dạng mới từ Java SE 14, cú pháp case L mũi tên
 * - Là BIỂU THỨC: trả về một giá trị, gán được vào biến nên kết thúc bằng dấu
 * chấm phẩy.
 * - Sinh ra để sửa ba nhược điểm của dạng cũ: fall-through dễ sai, biến bị rò
 * phạm vi giữa các case, và không dùng được như biểu thức.
 * - Chỉ nhánh khớp được chạy, KHÔNG fall-through nên không cần break.
 * - Phần bên phải mũi tên được phép là: một biểu thức, một block ngoặc nhọn,
 * hoặc một lệnh throw.
 * - Gộp nhiều nhãn cho một nhánh bằng dấu phẩy: case 0, 1 mũi tên ...
 * <p>
 * 4. Exhaustiveness - bắt buộc phủ hết
 * - Switch expression phải phủ MỌI giá trị có thể, thiếu là lỗi compile
 * "the switch expression does not cover all possible input values".
 * - Với enum đã liệt kê đủ mọi hằng số thì không cần default.
 * - Khi đó compiler tự thêm một default ngầm; nó không bao giờ chạy trong điều
 * kiện bình thường, chỉ kích hoạt nếu enum được thêm hằng số mới mà class này
 * chưa biên dịch lại, và sẽ ném IncompatibleClassChangeError.
 * <p>
 * 5. yield - trả giá trị từ nhánh nhiều dòng
 * - Nhánh là biểu thức đơn thì tự trả giá trị; nhánh là block nhiều dòng phải
 * dùng yield để chỉ ra giá trị trả về.
 * - Không dùng return vì return đã mang nghĩa thoát khỏi method, sẽ nhập nhằng.
 * - yield chỉ thoát khỏi switch expression và trả giá trị cho chính biểu thức đó.
 * <p>
 * 6. Ghi chú ngoài tài liệu - case null (Java SE 21+)
 * - dev.java viết rằng switch expression không nhận null và case null còn là
 * preview của Java SE 17. Điều này đã LỖI THỜI: trên JDK 21 trở lên case null
 * là tính năng chính thức, chạy được mà không cần bật preview.
 * - Hành vi mặc định không đổi: KHÔNG viết case null thì vẫn ném NPE như cũ.
 * <p>
 * Best practice: mặc định dùng switch expression với cú pháp mũi tên. Ba lớp
 * bug biến mất ngay từ lúc compile - quên break, bỏ sót trường hợp, và biến rò
 * phạm vi giữa các case. Chỉ dùng switch statement khi các nhánh thực sự làm
 * việc chứ không trả giá trị, và khi đó vẫn nên viết mũi tên để tránh fall-through.
 */
public class t8_switch {

    enum TrangThai { MOI, DANG_GIAO, HUY }

    /** Switch statement: là lệnh, phải tự gán biến, phải nhớ break. */
    void switchStatement() {
        int thang = 8;
        String ten;

        switch (thang) {
            case 1:  ten = "Tháng Một"; break;
            case 8:  ten = "Tháng Tám"; break;
            default: ten = "Không hợp lệ"; break;
        }

        IO.println("switch statement: " + ten);
    }

    /** Thiếu break thì chạy lan xuống các nhánh sau - fall-through. */
    void fallThrough() {
        IO.println("fall-through, diem = 2:");
        int diem = 2;

        switch (diem) {
            case 1: IO.println("  case 1");
            case 2: IO.println("  case 2");   // khớp từ đây
            case 3: IO.println("  case 3");   // vẫn chạy vì không có break
        }
    }

    /** Switch expression: là biểu thức, trả thẳng giá trị, không cần break. */
    void switchExpression() {
        int quy = 2;

        int soNgay = switch (quy) {
            case 0, 1 -> 90;      // gộp nhiều nhãn bằng dấu phẩy
            case 2 -> 91;
            default -> -1;        // không có default là lỗi compile
        };

        IO.println("switch expression: quy " + quy + " có " + soNgay + " ngày");
    }

    /** Nhánh nhiều dòng dùng block và yield; enum phủ hết thì không cần default. */
    void yieldVaEnum() {
        TrangThai trangThai = TrangThai.DANG_GIAO;

        int phi = switch (trangThai) {
            case MOI -> 30_000;
            case DANG_GIAO -> {
                int phiGoc = 30_000;
                yield phiGoc / 2;      // yield, KHÔNG phải return
            }
            case HUY -> 0;
        };

        IO.println("yield + enum: phí khi " + trangThai + " = " + phi);
    }

    void main() {
        switchStatement();
        fallThrough();
        switchExpression();
        yieldVaEnum();
    }
}
