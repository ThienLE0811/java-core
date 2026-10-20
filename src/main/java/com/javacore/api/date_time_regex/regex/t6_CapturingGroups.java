package com.javacore.api.date_time_regex.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Capturing Groups
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 6/10 trong section Regular Expressions.
 * - Đây là bài biến regex từ "kiểm tra đúng/sai" thành "trích xuất dữ liệu". Mọi lần bạn
 * cần lấy RA một phần của chuỗi chứ không chỉ hỏi có khớp không, bạn cần group.
 * - Một câu cho cả bài: mở ngoặc tròn là đánh số một chỗ để nhớ, và thứ tự số đếm theo
 * dấu mở ngoặc từ trái sang phải.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/groups/">dev.java - Capturing Groups</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CAPTURING GROUPS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. GROUP LÀ GÌ
 * - Bọc nhiều ký tự trong ngoặc tròn để coi chúng là một đơn vị. {@code (dog)} gom ba chữ
 * d, o, g thành một.
 * - "The portion of the input string that matches the capturing group will be saved in
 * memory for later recall via backreferences."
 * - Hai lợi ích tách biệt, đừng gộp làm một:
 * + Gom cụm để gắn quantifier: {@code (abc)+} (đã gặp ở bài t5).
 * + GHI NHỚ đoạn đã khớp để lấy ra sau. Đây mới là "capturing".
 * <p>
 * 2. CÁCH ĐÁNH SỐ
 * - Đếm theo dấu MỞ ngoặc, từ trái sang phải. Với {@code ((A)(B(C)))} có bốn group:
 * <pre>
 *   1  ((A)(B(C)))   toàn bộ nhóm ngoài
 *   2  (A)
 *   3  (B(C))
 *   4  (C)
 * </pre>
 * - Group 0 luôn là TOÀN BỘ biểu thức, và không được tính vào {@code groupCount()}.
 * - {@code groupCount()} trả về số capturing group, không kể group 0. Với ví dụ trên là 4.
 * - Nhóm bắt đầu bằng {@code (?} là NON-CAPTURING: không lưu nội dung và không được đánh số.
 * <p>
 * 3. BA METHOD NHẬN SỐ GROUP
 * - {@code start(int group)} - chỉ số bắt đầu của đoạn mà group đó bắt được.
 * - {@code end(int group)} - chỉ số ngay sau ký tự cuối của đoạn đó.
 * - {@code group(int group)} - chính đoạn chuỗi mà group đó bắt được.
 * - Gọi không tham số ({@code group()}, {@code start()}, {@code end()}) là tương đương
 * group 0, tức toàn bộ đoạn khớp.
 * <p>
 * 4. BACKREFERENCES
 * - "A backreference is specified in the regular expression as a backslash followed by a
 * digit indicating the number of the group to be recalled."
 * - Ví dụ trang gốc: {@code (\d\d)\1} khớp hai chữ số rồi ĐÚNG hai chữ số đó lần nữa.
 * + Input "1212" → khớp.
 * + Input "1213" → không khớp.
 * - Với group lồng nhau cũng làm y hệt: backslash cộng số thứ tự của group.
 * - Phân biệt quan trọng: trong PATTERN backreference viết {@code \1}; trong chuỗi THAY THẾ
 * của {@code replaceAll} lại viết {@code $1}. Hai cú pháp khác nhau cho cùng một ý tưởng -
 * nhầm là lỗi hay gặp. Bài t9 nói tiếp.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. NAMED GROUP - CÓ TỪ JAVA 7, TRANG GỐC KHÔNG NHẮC
 * - Cú pháp khai báo: {@code (?<ten>X)}. Lấy ra: {@code matcher.group("ten")}.
 * Backreference trong pattern: {@code \k<ten>}. Trong chuỗi thay thế: {@code ${ten}}.
 * - Vì sao nên dùng trong code production: group đánh số là vị trí, mà vị trí thay đổi mỗi
 * khi ai đó thêm một cặp ngoặc vào giữa regex. Lúc đó {@code group(3)} vẫn chạy nhưng trả
 * về nội dung sai - bug im lặng, không exception, test không bắt được nếu dữ liệu giống nhau.
 * - Tên group chỉ được dùng chữ cái và chữ số, phải bắt đầu bằng chữ cái. Không gạch dưới.
 * - Nguyên tắc: regex dùng một lần trong một dòng thì đánh số cũng được; regex là hằng số
 * dùng nhiều chỗ thì BẮT BUỘC đặt tên.
 * <p>
 * 6. NON-CAPTURING GROUP - DÙNG NHIỀU HƠN BẠN NGHĨ
 * - {@code (?:X)} gom cụm mà không lưu, không đánh số.
 * - Dùng khi chỉ cần gom để gắn quantifier hoặc để giới hạn phạm vi của {@code |}.
 * Ví dụ {@code (?:https?|ftp)://} - cần gom hai nhánh lại nhưng không cần lấy ra.
 * - Lợi ích: regex đỡ tốn bộ nhớ, và quan trọng hơn là SỐ THỨ TỰ của các group còn lại
 * không bị xê dịch khi bạn thêm bớt nhóm gom.
 * - Thói quen tốt: mặc định viết {@code (?:...)}, chỉ chuyển sang {@code (...)} khi thực sự
 * cần lấy nội dung ra.
 * <p>
 * 7. LOOKAHEAD VÀ LOOKBEHIND - CŨNG LÀ NGOẶC, NHƯNG KHÔNG TIÊU THỤ KÝ TỰ
 * - Bốn dạng:
 * + {@code (?=X)} lookahead khẳng định - phía sau phải là X.
 * + {@code (?!X)} lookahead phủ định - phía sau không được là X.
 * + {@code (?<=X)} lookbehind khẳng định - phía trước phải là X.
 * + {@code (?<!X)} lookbehind phủ định - phía trước không được là X.
 * - "Không tiêu thụ" nghĩa là sau khi kiểm tra xong, con trỏ KHÔNG di chuyển. Nên chúng
 * không nằm trong {@code group()} và dùng được để đặt nhiều điều kiện lên cùng một vị trí.
 * - Ứng dụng kinh điển: kiểm tra mật khẩu nhiều điều kiện cùng lúc.
 * {@code ^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$} - mỗi lookahead là một điều kiện độc lập.
 * - GIỚI HẠN CỦA JAVA: lookbehind phải có ĐỘ DÀI HỮU HẠN. {@code (?<=abc)} được,
 * {@code (?<=a{1,5})} được, nhưng {@code (?<=a+)} thì ném {@code PatternSyntaxException}.
 * JS hiện đại không có giới hạn này - đây là chỗ regex JS không chuyển sang Java được.
 * - Cảnh báo hiệu năng: nhiều lookahead chồng nhau trên input dài là một nguồn ReDoS khác.
 * Với validate mật khẩu thì vô hại vì input ngắn; với parse log thì cân nhắc lại.
 * <p>
 * 8. GROUP KHÔNG THAM GIA MATCH TRẢ VỀ NULL
 * - Nếu một group nằm trong nhánh {@code |} không được chọn, hoặc nằm dưới {@code ?} mà
 * phần đó không khớp, thì {@code group(n)} trả về {@code null} chứ không phải chuỗi rỗng.
 * - Đây là nguồn {@code NullPointerException} rất hay gặp khi trích dữ liệu. Luôn kiểm tra
 * null trước khi {@code .trim()} hay {@code .length()} trên kết quả group tùy chọn.
 * - {@code start(n)} của group không tham gia trả về -1.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - Cách đánh số và group 0 giống hệt nhau. Bên JS kết quả match là một mảng:
 * {@code match[0]} là toàn bộ, {@code match[1]} là group 1 - trùng khái niệm với
 * {@code group(0)}, {@code group(1)} của Java.
 * - Named group cùng cú pháp {@code (?<ten>X)} ở cả hai. Nhưng lấy ra khác nhau:
 * JS dùng {@code match.groups.ten}, Java dùng {@code matcher.group("ten")}.
 * - Non-capturing {@code (?:X)} giống nhau.
 * - Backreference: pattern cả hai đều {@code \1}; chuỗi thay thế cả hai đều {@code $1}.
 * Chỗ này hiếm hoi mà hai ngôn ngữ trùng nhau hoàn toàn.
 * - Lookbehind: JS cho độ dài biến thiên, Java BẮT BUỘC hữu hạn. Copy regex có
 * {@code (?<=\s+)} từ frontend sang Java sẽ ném exception lúc chạy, không phải lúc compile.
 * - Group không tham gia: JS trả về {@code undefined}, Java trả về {@code null}. Cùng bẫy,
 * khác tên.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code ((A)(B(C)))} có mấy group? Group 3 là cái nào?
 * - {@code groupCount()} có tính group 0 không?
 * - Viết regex khớp bốn chữ số mà hai số đầu lặp lại ở hai số cuối.
 * - Trong pattern thì backreference viết thế nào, trong chuỗi thay thế thì thế nào?
 * - Vì sao group đánh số nguy hiểm khi regex được sửa về sau?
 * - Khi nào dùng {@code (?:X)} thay vì {@code (X)}?
 * - "Không tiêu thụ ký tự" của lookahead nghĩa là gì?
 * - Viết một regex kiểm tra mật khẩu có ít nhất một chữ số và một chữ hoa, dài từ 8.
 * - Java có cho lookbehind độ dài biến thiên không? Chuyện gì xảy ra nếu viết vậy?
 * - {@code group(2)} trả về gì khi group 2 nằm trong nhánh không được chọn?
 */
public class t6_CapturingGroups {

    void main() {
        // TODO 1 (mục 2, 3): Chạy đoạn dưới. Đối chiếu từng group với bảng đánh số ở mục 2.
        //
        Matcher m = Pattern.compile("((A)(B(C)))").matcher("ABC");

        if (m.matches()) {
            System.out.println("groupCount() = " + m.groupCount());
            for (int i = 0; i <= m.groupCount(); i++) {
                System.out.println("group(" + i + ") = " + m.group(i)
                        + "  [" + m.start(i) + "," + m.end(i) + ")");
            }
        }

        // TODO 2 (mục 4): Kiểm chứng "(\\d\\d)\\1" trên "1212" và "1213".
        //   Rồi viết regex khớp chuỗi có một từ bị lặp liền, ví dụ "the the".
        //
        // TODO 3 (mục 2, 6): Thêm một cặp ngoặc gom vào giữa regex ở TODO 1 và chạy lại.
        //   Quan sát số thứ tự group bị xê dịch. Sau đó đổi cặp ngoặc mới thành (?:...)
        //   và xác nhận số thứ tự trở lại như cũ.
        //
        // TODO 4 (mục 5): Viết regex tách ngày dạng "2026-10-20" thành ba phần bằng NAMED
        //   group: nam, thang, ngay. In ra bằng group("nam") thay vì group(1).
        //
        // TODO 5 (mục 5): Dùng regex ở TODO 4 với replaceAll để đổi "2026-10-20" thành
        //   "20/10/2026". Làm hai bản: dùng $1 $2 $3 và dùng ${ngay} ${thang} ${nam}.
        //
        // TODO 6 (mục 7): Viết regex kiểm tra mật khẩu: ít nhất 8 ký tự, có chữ thường,
        //   chữ hoa, chữ số và một ký tự đặc biệt. Dùng lookahead.
        //   Test với ít nhất 5 chuỗi, trong đó 2 chuỗi hợp lệ.
        //
        // TODO 7 (mục 7): Thử compile "(?<=a+)b". Dán lại PatternSyntaxException.
        //   Rồi sửa thành "(?<=a{1,5})b" và xác nhận chạy được.
        //
        // TODO 8 (mục 8): Viết regex "(\\d+)(?:-(\\d+))?" chạy trên "10" và "10-20".
        //   In group(2) cả hai lần. Thêm .length() vào và dán lại NullPointerException.
        //
        // TODO 9 (mục 7): Dùng lookahead để chèn dấu phẩy phân cách hàng nghìn vào số
        //   "1234567" thành "1,234,567" bằng replaceAll. Gợi ý: (?=(\\d{3})+$)
    }
}
