package com.javacore.api.date_time_regex.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Boundaries
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 7/10 trong section Regular Expressions.
 * - Bài cuối của khối CÚ PHÁP. Từ bài t8 trở đi là API.
 * - Boundary matcher không khớp ký tự nào cả - chúng khớp VỊ TRÍ. Đây là khái niệm khó nhất
 * của khối cú pháp, nhưng hiểu rồi thì regex gọn đi rất nhiều.
 * - Một câu cho cả bài: khi regex của bạn khớp đúng nội dung nhưng SAI CHỖ, câu trả lời
 * nằm ở bài này.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/boundary-matchers/">dev.java - Boundaries</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - BOUNDARIES (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. BẢNG BOUNDARY MATCHER - HỌC THUỘC
 * <pre>
 *   ^     The beginning of a line
 *   $     The end of a line
 *   \b    A word boundary
 *   \B    A non-word boundary
 *   \A    The beginning of the input
 *   \G    The end of the previous match
 *   \Z    The end of the input but for the final terminator, if any
 *   \z    The end of the input
 * </pre>
 * - Trang gốc mở đầu: "you can make your pattern matches more precise by specifying such
 * information with boundary matchers." Ý là chúng trả lời câu hỏi match nằm Ở ĐÂU, chứ
 * không chỉ có match hay không.
 * - Chú ý {@code \Z} và {@code \z} khác nhau ở chữ hoa/thường - đây là cặp dễ nhầm nhất bảng.
 * <p>
 * 2. ^ VÀ $ - BỐN TÌNH HUỐNG TRANG GỐC DẪN RA
 * - Một pattern khớp trọn cả chuỗi thì thành công.
 * - Cùng pattern đó thất bại khi phía trước mục tiêu có thêm khoảng trắng.
 * - Cho phép khoảng trắng tùy ý trước một từ đứng cuối dòng thì khớp lại được.
 * - Yêu cầu từ đứng đầu dòng rồi theo sau là các ký tự word cũng khớp.
 * - Thông điệp rút ra: {@code ^} và {@code $} là ràng buộc, thêm chúng vào là THU HẸP
 * tập chuỗi được chấp nhận.
 * <p>
 * 3. \b - WORD BOUNDARY
 * - Trang gốc dùng {@code \bdog\b} để bảo đảm "dog" là một TỪ trọn vẹn, không phải phần
 * nằm trong một từ dài hơn.
 * - Định nghĩa chính xác của {@code \b}: vị trí mà một bên là ký tự word ({@code \w}) và
 * bên kia không phải (hoặc là biên của chuỗi). Nó là một VỊ TRÍ, độ dài bằng 0.
 * <p>
 * 4. \B - NON-WORD BOUNDARY
 * - Khớp những vị trí KHÔNG phải word boundary. Tức là hai bên cùng là ký tự word, hoặc
 * hai bên cùng không phải.
 * - Dùng để tìm chuỗi con NẰM TRONG một từ: {@code \bdog\B} khớp "dog" khi phía sau nó
 * vẫn còn chữ, ví dụ trong "doggie".
 * <p>
 * 5. \G - END OF PREVIOUS MATCH
 * - Trang gốc đưa hai ví dụ cạnh nhau và nhận xét: "the second example finds only one match,
 * because the second occurrence of 'dog' does not start at the end of the previous match."
 * - Nói cách khác: {@code \G} ép các match phải NỐI LIỀN nhau. Hễ có khoảng hở là dừng.
 * - Lần tìm đầu tiên, {@code \G} khớp ở vị trí bắt đầu vùng tìm kiếm.
 * - Ứng dụng thật: viết tokenizer. Mỗi token phải bắt đầu đúng ngay sau token trước, nếu
 * không thì đầu vào có ký tự lạ và cần báo lỗi - {@code \G} làm chuyện đó tự động.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. ^ $ ĐỔI NGHĨA THEO CỜ MULTILINE - CHỖ GÂY HIỂU NHẦM NHIỀU NHẤT
 * - MẶC ĐỊNH, Java KHÔNG bật multiline. Khi đó {@code ^} là đầu TOÀN BỘ input và {@code $}
 * là cuối toàn bộ input (bỏ qua ký tự xuống dòng cuối cùng nếu có).
 * - Bảng của trang gốc ghi "beginning of a line" là mô tả khi ĐÃ bật {@code MULTILINE}.
 * Đọc bảng mà không biết điều này là hiểu sai ngay từ đầu.
 * - Bật {@code Pattern.MULTILINE} (hoặc nhúng {@code (?m)}) thì {@code ^} và {@code $} mới
 * khớp ở mỗi đầu/cuối dòng. Xem bài t8.
 * - Cặp {@code \A} và {@code \z} thì KHÔNG bao giờ đổi nghĩa theo cờ. Khi muốn "chắc chắn
 * là đầu/cuối cả input" thì dùng chúng - rõ ràng hơn {@code ^} {@code $} rất nhiều.
 * <p>
 * 7. $ VÀ \Z BỎ QUA XUỐNG DÒNG CUỐI - BẪY VALIDATE BẢO MẬT
 * - Với {@code find()}, chuỗi {@code "abc\n"} KHỚP regex {@code ^abc$}. Nhiều người không
 * ngờ chuyện này.
 * - Lý do: {@code $} và {@code \Z} đều bỏ qua ký tự kết thúc dòng ở cuối cùng. Chỉ
 * {@code \z} là tuyệt đối cuối.
 * - PHẢI PHÂN BIỆT HAI CÁCH GỌI, vì kết luận ngược nhau:
 * + {@code matcher.find()} với {@code ^[a-z]+$} CHO LỌT {@code "admin\n"}.
 * + {@code matcher.matches()} và {@code String.matches()} thì KHÔNG, vì chúng đòi tiêu thụ
 * trọn vùng input - ký tự {@code \n} còn thừa nên trượt.
 * - Hệ quả bảo mật thật: code validate hay viết kiểu
 * {@code if (!p.matcher(input).find()) reject()}. Lúc đó {@code "admin\n"} lọt qua, và nếu
 * giá trị đó được ghép vào header HTTP, lệnh shell, hay file cấu hình thì xuống dòng là
 * ký tự tiêm.
 * - Hai cách phòng, nên làm cả hai: dùng {@code matches()} thay {@code find()} khi ý định
 * là "cả chuỗi phải đúng khuôn", và dùng {@code \A...\z} thay {@code ^...$} để ý định đó
 * nằm luôn trong regex, không phụ thuộc người gọi chọn method nào.
 * <p>
 * 8. \b CŨNG CHỈ ASCII THEO MẶC ĐỊNH
 * - {@code \b} được định nghĩa qua {@code \w}, mà {@code \w} chỉ ASCII (bài t4 mục 4).
 * - Hệ quả: trong chuỗi "Thiện", Java thấy một word boundary NGAY TRƯỚC chữ "ệ" vì "ệ"
 * không phải ký tự word. Tìm từ trọn vẹn trong văn bản tiếng Việt bằng {@code \b} sẽ ra
 * kết quả sai.
 * - Sửa bằng cờ {@code UNICODE_CHARACTER_CLASS} - cờ này sửa cả {@code \w} lẫn {@code \b}.
 * Đây là lý do mục 4 của bài t4 quan trọng hơn vẻ ngoài của nó.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - {@code ^ $ \b \B} có đủ bên JS, nghĩa giống nhau.
 * - Cờ {@code m} của JS = {@code MULTILINE} của Java. Mặc định cả hai đều TẮT.
 * - {@code \A \Z \z \G} là ĐẶC SẢN Java, JS KHÔNG có. Muốn neo tuyệt đối bên JS phải dựa
 * vào việc không bật cờ {@code m} - kém rõ ràng hơn hẳn.
 * - Bẫy "abc\n" khớp {@code ^abc$}: JS KHÔNG có bẫy này. Khi không bật cờ {@code m},
 * {@code $} của JS là cuối tuyệt đối. Đây là khác biệt dễ gây lỗi khi port validate từ
 * frontend sang backend - cùng regex, Java lỏng hơn.
 * - {@code \b} bên JS cũng chỉ ASCII, cùng bẫy tiếng Việt, và JS cũng không có cờ sửa.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Viết lại cả 8 boundary matcher không nhìn bảng.
 * - Boundary matcher khớp bao nhiêu ký tự?
 * - Định nghĩa chính xác {@code \b} bằng lời.
 * - {@code \bdog\b} và {@code \bdog\B} khác nhau ra sao? Cho một input phân biệt được.
 * - Mặc định {@code ^} nghĩa là gì? Sau khi bật MULTILINE thì nghĩa là gì?
 * - {@code \Z} và {@code \z} khác nhau chỗ nào?
 * - Chuỗi "abc\n" có khớp {@code ^abc$} không? Trả lời khác nhau thế nào giữa
 * {@code find()} và {@code matches()}? Hệ quả bảo mật là gì? Sửa thế nào?
 * - {@code \G} ép điều gì lên các match liên tiếp?
 * - {@code \b} có hoạt động đúng với "Thiện" không? Vì sao? Sửa bằng gì?
 */
public class t7_Boundaries {

    void main() {
        // TODO 1 (mục 3, 4): Chạy đoạn dưới. Giải thích từng dòng kết quả bằng định nghĩa
        //   word boundary ở mục 3.
        //
        String text = "The dog plays, the doggie sleeps, hotdog.";

        for (String regex : new String[]{"\\bdog\\b", "\\bdog\\B", "\\Bdog\\b"}) {
            Matcher m = Pattern.compile(regex).matcher(text);
            System.out.print(regex + " -> ");
            while (m.find()) {
                System.out.print("[" + m.start() + "] ");
            }
            System.out.println();
        }

        // TODO 2 (mục 2): Chạy "^dog$" trên "dog" và trên "   dog". Rồi chạy "\\s*dog$"
        //   trên "   dog" và "^dog\\w*" trên "dogblahblah". Giải thích cả bốn kết quả.
        //
        // TODO 3 (mục 6): Với input "dong1\ndong2", đếm số match của "^dong\\d" ở hai chế độ:
        //   không cờ và có Pattern.MULTILINE. Dán số liệu vào mục 6.
        //
        // TODO 4 (mục 6, 7): Chạy "^abc$" và "\\Aabc\\z" trên "abc" và trên "abc\n",
        //   dùng find() (KHÔNG phải matches()). Lập bảng 2x2 và chỉ ra ô khác nhau.
        //
        // TODO 5 (mục 7): Viết hàm validate username chỉ cho chữ thường và số, cài bằng
        //   find(). Làm hai bản regex: "^[a-z0-9]+$" và "\\A[a-z0-9]+\\z".
        //   Truyền vào "admin\n" và ghi lại bản nào cho lọt.
        //
        // TODO 6 (mục 7): Lập bảng 2x2 cho input "admin\n": hai regex ở TODO 5 nhân với
        //   hai method find() và matches(). Kết luận: đổi method hay đổi regex thì đủ?
        //   Vì sao vẫn nên làm cả hai?
        //
        // TODO 7 (mục 5): Chạy "dog" trên "dogdog" và đếm match. Rồi chạy "\\Gdog" trên
        //   "dogdog" và trên "dog dog". So sánh ba con số.
        //
        // TODO 8 (mục 5): Dùng \\G viết một tokenizer nhỏ đọc chuỗi "12+34-5" thành các
        //   token số và toán tử. Khi gặp ký tự lạ thì dừng và báo vị trí.
        //
        // TODO 9 (mục 8): Chạy "\\bThien\\b" trên "Le Van Thien" rồi "\\bThiện\\b" trên
        //   "Lê Văn Thiện". Bật UNICODE_CHARACTER_CLASS và chạy lại. Ghi lại khác biệt.
    }
}
