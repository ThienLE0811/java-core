package com.javacore.language.constructs.numbers_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Autoboxing and Unboxing
 * <p>
 * 1. Lý do bài này tồn tại - Java có HAI thế giới số
 * - Primitive nhanh và nhẹ, nhưng KHÔNG phải object nên không bỏ vào
 * collection được. Viết List của int là sai, phải viết List của Integer.
 * - Mỗi primitive có một lớp bao tương ứng để làm cầu nối sang thế giới
 * object. Autoboxing sinh ra để bạn không phải tự tay bắc cầu đó.
 * <p>
 * 2. Tám cặp primitive và lớp bao
 * - boolean với Boolean, byte với Byte, short với Short, int với Integer,
 * long với Long, float với Float, double với Double, char với Character.
 * - Lưu ý hai cái tên KHÔNG theo quy tắc viết hoa chữ đầu: int thành Integer
 * và char thành Character.
 * <p>
 * 3. Hai chiều chuyển đổi
 * - AUTOBOXING là chiều primitive thành lớp bao.
 * - UNBOXING là chiều ngược lại, lớp bao thành primitive.
 * - Compiler làm tự động, code nhìn như thể chỉ có một loại số.
 * <p>
 * 4. Khi nào xảy ra
 * - Khi GÁN vào một biến có kiểu thuộc thế giới còn lại.
 * - Khi TRUYỀN THAM SỐ cho method mong đợi kiểu thuộc thế giới còn lại.
 * - Thêm một trường hợp trang không nói rõ nhưng ví dụ có dùng: khi đưa lớp
 * bao vào PHÉP TOÁN số học hoặc so sánh, nó bị unbox để tính.
 * <p>
 * 5. Thực chất chỉ là đường cú pháp
 * - Không có phép màu nào. Compiler viết hộ lời gọi valueOf cho chiều box và
 * lời gọi intValue, doubleValue, charValue cho chiều unbox.
 * - Nghĩa là chi phí tạo object vẫn còn nguyên, chỉ là bị giấu đi.
 * <p>
 * 6. Ghi chú ngoài tài liệu - trang này KHÔNG cảnh báo gì, nhưng đây là nguồn
 * của ba bug kinh điển
 * - NULL LÀM NỔ CHƯƠNG TRÌNH. Primitive không có null, nên unbox một lớp bao
 * đang null là ném NullPointerException. Chỗ nổ thường cách xa chỗ gán null
 * nên rất khó lần ra. Hay gặp nhất khi lấy giá trị từ Map ra: get trả về
 * null nếu không có key, gán thẳng vào int là nổ.
 * - SO SÁNH BẰNG == CHO KẾT QUẢ SAI. Lớp bao so bằng == là so THAM CHIẾU.
 * Integer có cache cho khoảng âm 128 tới 127 nên hai giá trị 127 cho true,
 * còn hai giá trị 128 lại cho false. Luôn dùng equals.
 * - CHẬM TRONG VÒNG LẶP. Mỗi vòng phải unbox, tính, rồi box lại, sinh ra một
 * object rác. Khai báo biến tích lũy bằng long thay vì Long là nhanh hơn
 * hàng chục lần mà không đổi gì khác.
 * - Một bẫy nữa của collection: List có HAI method remove, bản nhận int xóa
 * theo CHỈ SỐ còn bản nhận Object xóa theo GIÁ TRỊ. Gọi remove với một số
 * nguyên sẽ chọn bản theo chỉ số, muốn xóa theo giá trị phải bọc tường minh
 * bằng Integer.valueOf.
 * - Toán tử ba ngôi cũng unbox: nếu hai nhánh khác kiểu, compiler quy về một
 * kiểu chung rồi unbox, nên một nhánh trả null là ném NPE dù nhánh đó không
 * hề được chọn để chạy.
 * <p>
 * Best practice: dùng PRIMITIVE làm mặc định cho mọi tính toán và biến cục bộ.
 * Chỉ dùng lớp bao khi bắt buộc, tức là khi cần bỏ vào collection, cần dùng
 * generic, hoặc cần biểu diễn trạng thái "chưa có giá trị" bằng null - điển
 * hình là field của entity ánh xạ cột cho phép null trong database. Mọi so
 * sánh lớp bao đi qua equals, và kiểm tra null trước khi để nó bị unbox.
 */
public class t5_Autoboxing_Unboxing {

    void main() {
        List<Integer> list = new ArrayList<>();
        list.add(10);                  // autoboxing: 10 thành Integer.valueOf(10)
        list.add(20);

        int tong = list.get(0) + list.get(1);   // unboxing để cộng
        System.out.println(tong);               // 30

        // Bẫy 1: == so sánh tham chiếu, kết quả đổi theo vùng cache -128..127
        Integer a = 127, b = 127;
        Integer c = 128, d = 128;
        System.out.println(a == b);             // true
        System.out.println(c == d);             // false  (!)
        System.out.println(c.equals(d));        // true   - cách đúng

        // Bẫy 2: unbox một lớp bao đang null là ném NullPointerException
        Integer chuaCo = null;
        try {
            int x = chuaCo;
            System.out.println(x);
        } catch (NullPointerException e) {
            System.out.println("NPE khi unbox null");
        }

        // Bẫy 3: remove(int) xóa theo CHỈ SỐ, remove(Object) xóa theo GIÁ TRỊ
        list.remove(0);                         // xóa phần tử ở vị trí 0
        System.out.println(list);               // [20]
        list.remove(Integer.valueOf(20));       // xóa phần tử có giá trị 20
        System.out.println(list);               // []
    }
}
