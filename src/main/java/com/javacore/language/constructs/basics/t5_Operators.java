package com.javacore.language.constructs.basics;

public class t5_Operators {

    public static void arithmetic() {

        int result = 1 + 2;
        // result is now 3
        IO.println("1 + 2 = " + result);
        int original_result = result;

        result = result - 1;
        // result is now 2
        IO.println(original_result + " - 1 = " + result);
        original_result = result;

        result = result * 2;
        // result is now 4
        IO.println(original_result + " * 2 = " + result);
        original_result = result;

        result = result / 2;
        // result is now 2
        IO.println(original_result + " / 2 = " + result);
        original_result = result;

        result = result + 8;
        // result is now 10
        IO.println(original_result + " + 8 = " + result);
        original_result = result;

        result = result % 7;
        // result is now 3
        IO.println(original_result + " % 7 = " + result);
    }

    void main() {
        arithmetic();
        concatString();
    }

    public void concatString() {
        String firstString = "This is";
        String secondString = " a concatenated string....";
        String thirdString = firstString + secondString;
        IO.println(thirdString);
    }
}

/**
 * Using Operators in Your Programs
 * <p>
 * 1. Operators - khái niệm
 * - Operator là ký hiệu thực hiện phép toán trên 1, 2 hoặc 3 toán hạng và
 * TRẢ VỀ một kết quả - nên nó là biểu thức, dùng gán tiếp được.
 * - Độ ưu tiên từ cao xuống thấp: unary > nhân chia > cộng trừ > dịch bit >
 * so sánh > bằng > và/hoặc bit > và/hoặc logic > ternary > gán.
 * - Toán tử hai ngôi đánh giá trái sang phải, RIÊNG toán tử gán phải sang trái.
 * <p>
 * 2. Simple Assignment Operator =
 * - Gán giá trị vế phải cho biến vế trái.
 * - Với object, nó gán THAM CHIẾU chứ không copy object.
 * <p>
 * 3. Arithmetic Operators + - * / %
 * - Chia hai số nguyên là chia nguyên, cắt phần thập phân về phía 0: -5 / 2 = -2.
 * - % trả phần dư, giữ dấu của toán hạng trái: -5 % 2 = -1.
 * - Dạng rút gọn: x += 1 tương đương x = x + 1 (tương tự -= *= /= %=).
 * - + còn dùng để nối chuỗi: "Hello" + " world".
 * <p>
 * 4. Unary Operators + - ++ -- !
 * - +x giữ nguyên dấu, -x đảo dấu, !x đảo giá trị boolean.
 * - ++ và -- tăng/giảm 1, đặt được cả trước và sau biến.
 * - Prefix ++i: tăng trước, trả giá trị MỚI. Postfix i++: trả giá trị CŨ rồi mới tăng.
 * - Đứng riêng một dòng thì i++ và ++i như nhau; khác nhau khi nằm trong biểu thức.
 * <p>
 * 5. Equality and Relational Operators == != và lớn hơn, nhỏ hơn
 * - So sánh hai giá trị, luôn trả về boolean.
 * - == trên primitive so sánh giá trị; trên object so sánh THAM CHIẾU, không so
 * sánh nội dung - muốn so sánh nội dung phải dùng equals().
 * <p>
 * 6. Conditional Operators và Ternary
 * - and và or dạng đôi có short-circuit: vế phải chỉ được tính khi cần.
 * - Dạng đơn (một ký tự) luôn tính cả hai vế, nên mất tác dụng bảo vệ null.
 * - Ternary: dieu_kien ? gia_tri_neu_dung : gia_tri_neu_sai - rút gọn của if-else
 * khi cả hai nhánh chỉ để trả về một giá trị.
 * <p>
 * 7. Type Comparison Operator instanceof
 * - Kiểm tra object có thuộc một class hay interface không, trả về boolean.
 * - null KHÔNG phải instance của bất kỳ kiểu nào: luôn trả false, không ném NPE.
 * - Dùng được cả với class cha và interface mà object đó cài đặt.
 * <p>
 * 8. Bitwise and Bit Shift Operators
 * - Làm việc trên từng bit của kiểu nguyên: đảo bit, và, hoặc, xor.
 * - Dịch trái nhân đôi, dịch phải chia đôi mỗi bước.
 * - Dịch phải có hai loại: loại giữ dấu (điền bit dấu) và loại điền 0 - với số
 * âm cho kết quả khác hẳn nhau.
 * <p>
 * Best practice: không cố nhớ hết bảng độ ưu tiên - nghi ngờ thì đóng ngoặc cho
 * rõ ý. Ưu tiên dạng short-circuit khi viết điều kiện, và tránh nhét ++ hay --
 * vào giữa một biểu thức phức tạp vì rất dễ đọc sai.
 */
