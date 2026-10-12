package com.javacore.language.constructs.numbers_strings;

import java.util.Calendar;
import java.util.Locale;

/**
 * Numbers
 * <p>
 * 1. Lớp bao số và cây kế thừa Number
 * - Mọi lớp bao số đều là lớp con của lớp trừu tượng Number: Byte, Short,
 * Integer, Long, Float, Double.
 * - Ngoài ra còn BigDecimal và BigInteger cho tính toán độ chính xác cao,
 * AtomicInteger và AtomicLong cho ứng dụng đa luồng.
 * - Character và Boolean cũng là lớp bao nhưng KHÔNG kế thừa Number, vì chúng
 * không mang giá trị số.
 * <p>
 * 2. Khi nào cần object thay vì primitive - ba lý do
 * - Khi method yêu cầu một Object: điển hình là collection, viết được
 * List của Integer chứ không viết được List của int.
 * - Khi cần hằng số của lớp như MIN_VALUE và MAX_VALUE.
 * - Khi cần method chuyển đổi: giữa các kiểu nguyên thủy, giữa số và chuỗi,
 * và giữa các hệ cơ số.
 * <p>
 * 3. Method của Number và Integer
 * - Nhóm chuyển về primitive: byteValue, shortValue, intValue, longValue,
 * floatValue, doubleValue. Chuyển xuống kiểu hẹp hơn là có thể MẤT dữ liệu.
 * - Nhóm so sánh: compareTo và equals.
 * - parseInt trả về int (primitive), còn valueOf trả về Integer (object) -
 * đây là cặp hay bị nhầm nhất.
 * - parseInt và valueOf đều có bản nhận thêm radix để đọc theo cơ số 2, 8,
 * 10 hoặc 16.
 * - decode tự nhận diện tiền tố: 0x và # là hệ 16, số 0 đứng đầu là hệ 8,
 * còn lại là hệ 10.
 * - toString có cả bản instance và bản static nhận vào một int.
 * <p>
 * 4. Autoboxing và Unboxing
 * - Đặt primitive vào chỗ cần object thì compiler tự bọc lại (autoboxing);
 * đặt object vào chỗ cần primitive thì compiler tự mở ra (unboxing).
 * - Cơ chế này chỉ là đường cú pháp: code sinh ra vẫn gọi valueOf và
 * intValue, nên vẫn có chi phí tạo object.
 * <p>
 * 5. Format output bằng printf và format
 * - Lớp PrintStream cung cấp hai method tương đương nhau là format và
 * printf, đều nhận varargs, nên System.out.printf và System.out.format
 * dùng thay nhau được.
 * - Cấu trúc một format specifier: dấu phần trăm, rồi flag, rồi độ rộng và
 * độ chính xác, kết thúc bằng ký tự converter.
 * - Converter hay dùng: d cho số nguyên, f cho số thực, n cho xuống dòng độc
 * lập hệ điều hành, nhóm bắt đầu bằng t cho ngày giờ (tB tên tháng đầy đủ,
 * td và te ngày, ty và tY năm, tl giờ, tM phút, tp sáng chiều, tm tháng,
 * tD gộp ngày tháng năm).
 * - Flag hay dùng: 08 là độ rộng 8 đệm số 0, dấu cộng là luôn hiện dấu,
 * dấu phẩy là phân nhóm hàng nghìn theo locale, dấu trừ là canh trái,
 * chấm 3 là lấy 3 số lẻ, 10 chấm 3 là rộng 10 canh phải và 3 số lẻ.
 * - Truyền Locale làm tham số đầu tiên để đổi quy ước dấu thập phân, ví dụ
 * Locale.FRANCE in ra dấu phẩy thay cho dấu chấm.
 * - Luôn dùng %n thay cho \n để không phụ thuộc hệ điều hành.
 * <p>
 * 6. DecimalFormat - format theo pattern
 * - Nằm ở java.text.DecimalFormat, linh hoạt hơn printf vì mô tả định dạng
 * bằng một chuỗi pattern tái sử dụng được.
 * - Ký tự pattern: # là chữ số nhưng bỏ qua nếu không có, 0 là chữ số và ÉP
 * hiện số 0, dấu phẩy là vị trí phân nhóm, dấu chấm là vị trí thập phân.
 * - Ví dụ với 123456.789: pattern ###,###.### cho 123,456.789; pattern
 * ###.## cho 123456.79 (có LÀM TRÒN chứ không cắt).
 * - Pattern 000000.000 với 123.78 cho 000123.780 - đệm cả đầu lẫn đuôi.
 * - Ký tự lạ trong pattern được giữ nguyên làm tiền tố, ví dụ thêm dấu đô la
 * ở đầu để in tiền tệ.
 * <p>
 * 7. Lớp Math - hằng số và method cơ bản
 * - Toàn bộ method của Math là static, gọi thẳng từ tên lớp, không cần tạo
 * object.
 * - Hằng số: Math.E là cơ số logarit tự nhiên, Math.PI là tỉ số chu vi trên
 * đường kính.
 * - abs trả trị tuyệt đối, có bản nạp chồng cho int, long, float, double.
 * - Bốn cách làm tròn khác nhau, rất hay nhầm: ceil lấy số nguyên nhỏ nhất
 * không nhỏ hơn đối số (làm tròn LÊN), floor lấy số nguyên lớn nhất không
 * lớn hơn đối số (làm tròn XUỐNG), rint lấy số nguyên gần nhất nhưng vẫn
 * trả về double, round trả về long khi nhận double và trả về int khi nhận
 * float.
 * - min và max trả về giá trị nhỏ hơn hoặc lớn hơn trong hai đối số.
 * <p>
 * 8. Mũ, logarit và lượng giác
 * - exp trả e mũ đối số, log là logarit TỰ NHIÊN (cơ số e, không phải cơ số
 * 10), pow nhận cơ số và số mũ, sqrt là căn bậc hai.
 * - Toàn bộ method lượng giác nhận góc tính bằng RADIAN, không phải độ:
 * sin, cos, tan, asin, acos, atan.
 * - atan2 nhận y và x, dùng để đổi từ tọa độ vuông góc sang tọa độ cực.
 * - toDegrees đổi radian sang độ, toRadians đổi độ sang radian - nhớ gọi
 * toRadians trước khi đưa góc vào sin hay cos.
 * <p>
 * 9. Số ngẫu nhiên
 * - Math.random trả về một double trong khoảng từ 0.0 (có lấy) đến 1.0
 * (không lấy).
 * - Muốn số nguyên từ 0 đến 9 thì nhân với 10 rồi ép kiểu về int.
 * - Khi cần cả một DÃY số ngẫu nhiên, nên tạo một instance của
 * java.util.Random và gọi method trên object đó, thay vì gọi Math.random
 * nhiều lần.
 * <p>
 * 10. Static import cho gọn
 * - import static java.lang.Math toàn bộ cho phép viết cos thay vì Math.cos.
 * - Chỉ nên dùng trong lớp nặng về tính toán; lạm dụng sẽ làm người đọc
 * không biết method đến từ đâu.
 * <p>
 * 11. Ghi chú ngoài tài liệu - những cái bẫy thật sự hay gặp
 * - So sánh hai lớp bao bằng == là so sánh THAM CHIẾU. Integer có cache cho
 * khoảng từ âm 128 đến 127, nên hai Integer cùng bằng 127 thì == trả true,
 * nhưng cùng bằng 128 lại trả false. Luôn dùng equals hoặc so sánh sau khi
 * đã unbox.
 * - Unboxing một lớp bao đang null sẽ ném NullPointerException, và nó nổ ở
 * chỗ gán hoặc chỗ tính toán chứ không phải chỗ nhận giá trị null, nên rất
 * khó lần ra nguyên nhân.
 * - Math.abs của Integer.MIN_VALUE vẫn trả về chính Integer.MIN_VALUE (số
 * âm) vì tràn số - miền giá trị int không có số dương tương ứng.
 * - double và float là nhị phân nên không biểu diễn chính xác được số thập
 * phân: 0.1 cộng 0.2 không bằng 0.3. Dữ liệu tiền tệ phải dùng BigDecimal,
 * và phải khởi tạo từ CHUỖI chứ không từ literal double.
 * - Autoboxing trong vòng lặp lớn tạo rất nhiều object rác; phần tính toán
 * nặng nên giữ ở kiểu nguyên thủy.
 * <p>
 * Best practice: dùng primitive để tính toán và chỉ dùng lớp bao khi API bắt
 * buộc phải là object. Mọi so sánh lớp bao đi qua equals hoặc compareTo, không
 * bao giờ dùng ==. Khi format số, printf hợp cho log và output dùng một lần,
 * còn DecimalFormat hợp khi cùng một định dạng được tái sử dụng nhiều nơi.
 * Riêng với tiền và số liệu kế toán thì bỏ hẳn double, dùng BigDecimal ngay
 * từ đầu.
 */
public class t1_Numbers {

    void main() {
        long n = 461012;
        System.out.format("%d%n", n);      // "461012"
        System.out.format("%08d%n", n);    // "00461012"
        System.out.format("%+8d%n", n);    // " +461012"
        System.out.format("%,8d%n", n);    //  461,012"
        System.out.format("%+,8d%n%n", n); // "+461,012"

        double pi = Math.PI;

        System.out.format("%f%n", pi);        // "3.141593"
        System.out.format("%.3f%n", pi);      // "3.142"
        System.out.format("%10.3f%n", pi);    // "     3.142"
        System.out.format("%-10.3f%n", pi);   // "3.142"
        System.out.format(Locale.FRANCE,
                "%-10.4f%n%n", pi); // "3,1416"

        Calendar c = Calendar.getInstance();
        System.out.format("%tB %te, %tY%n", c, c, c); // "May 29, 2006"

        System.out.format("%tl:%tM %tp%n", c, c, c);  // "2:34 am"

        System.out.format("%tD%n", c);    // "05/29/06"
    }
}
