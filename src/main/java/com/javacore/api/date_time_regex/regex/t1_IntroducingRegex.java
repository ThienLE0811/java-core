package com.javacore.api.date_time_regex.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Introducing Regular Expressions
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 1/10 - bài MỞ ĐẦU của section Regular Expressions.
 * - Bài này chưa dạy cú pháp regex. Nó dựng ba thứ dùng lại suốt 9 bài còn lại:
 * ba lớp của {@code java.util.regex}, cách Unicode được hỗ trợ, và một test harness để
 * thử pattern nhanh.
 * - Một câu cho cả bài: regex trong Java KHÔNG phải cú pháp của ngôn ngữ Java - nó là một
 * ngôn ngữ con viết trong String, nên mọi dấu backslash phải nhân đôi.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/intro/">dev.java - Introducing Regular Expressions</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTRODUCING REGULAR EXPRESSIONS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. REGEX LÀ GÌ
 * - Định nghĩa nguyên văn: "Regular expressions are a way to describe a set of strings based
 * on common characteristics shared by each string in the set."
 * - Dùng để làm gì: "They can be used to search, edit, or manipulate text and data."
 * - Cảnh báo nguyên văn ngay đầu trang: "You must learn a specific syntax to create regular
 * expressions - one that goes beyond the normal syntax of the Java programming language."
 * - Phương ngữ: "The regular expression syntax in the java.util.regex API is most similar to
 * that found in Perl." Nghĩa là kinh nghiệm regex từ Perl/PHP/JS chuyển sang dùng được gần
 * hết, nhưng KHÔNG phải 100%.
 * <p>
 * 2. BA LỚP CỦA GÓI JAVA.UTIL.REGEX
 * - {@code Pattern}: "A Pattern object is a compiled representation of a regular expression."
 * + "The Pattern class provides no public constructors." Phải dùng
 * {@code Pattern.compile()} - đây là static factory, không phải {@code new}.
 * - {@code Matcher}: "A Matcher object is the engine that interprets the pattern and performs
 * match operations."
 * + "Like the Pattern class, Matcher defines no public constructors."
 * + Lấy bằng {@code pattern.matcher(input)}.
 * - {@code PatternSyntaxException}: "an unchecked exception that indicates a syntax error in
 * a regular expression pattern." Bài t10 mổ kỹ lớp này.
 * - Luồng chuẩn ba bước, thuộc lòng ngay từ bài 1:
 * {@code Pattern p = Pattern.compile(regex)} → {@code Matcher m = p.matcher(input)}
 * → {@code m.find()} hoặc {@code m.matches()}.
 * <p>
 * 3. UNICODE SUPPORT
 * - "As of the JDK 7 release, Regular Expression pattern matching has expanded functionality
 * to support Unicode 6.0."
 * - Khớp một code point cụ thể: "You can match a specific Unicode code point using an escape
 * sequence of the form uFFFF, where FFFF is the hexadecimal value." Viết trong regex là
 * {@code \\uFFFF}.
 * + Cách khác kiểu Perl: "you can specify a code point using Perl-style hex notation",
 * dạng {@code \\x} rồi mã hex trong ngoặc nhọn.
 * + Ví dụ trang gốc: {@code \\u6771} khớp chữ Hán nghĩa là "east".
 * - Khớp theo THUỘC TÍNH ký tự:
 * + "You can match a single character belonging to a particular category with the
 * expression" {@code \\p} kèm tên property trong ngoặc nhọn.
 * + Phủ định dùng {@code \\P} chữ P HOA.
 * + "The three supported property types are scripts, blocks, and a 'general' category."
 * - Script: "you can either use the script keyword, or the sc short form", ví dụ
 * {@code script=Hiragana} hoặc dạng ngắn {@code IsHiragana}.
 * - Block: "A block can be specified using the block keyword, or the blk short form",
 * ví dụ {@code block=Mongolian}. Hoặc thêm tiền tố In: {@code InMongolian}.
 * - General category: "Categories can be specified with optional prefix Is. For example,
 * IsL matches the category of Unicode letters." Cũng viết được {@code general_category=Lu}
 * hoặc dạng ngắn {@code gc=Lu} cho chữ hoa.
 * - PHÂN BIỆT script và block - chỗ trang gốc không nói rõ: script là hệ CHỮ VIẾT
 * (Latin, Han, Hiragana), block là một DẢI code point liên tục trong bảng Unicode.
 * Một script có thể nằm rải ở nhiều block. Khi phân vân, chọn script.
 * <p>
 * 4. TEST HARNESS
 * - Trang gốc đưa file {@code RegexTestHarness.java}: "The application loops repeatedly,
 * prompting the user for a regular expression and input string."
 * - "Using this test harness is optional, but you may find it convenient for exploring the
 * test cases discussed in the following pages."
 * - Lợi ích thật của nó: đọc regex từ Console nên KHÔNG phải nhân đôi backslash. Gõ
 * {@code \d} là ra {@code \d}. Đây là lý do nên dựng nó ngay bài 1 (xem TODO 5).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BACKSLASH NHÂN ĐÔI - THUẾ PHẢI TRẢ VÌ JAVA KHÔNG CÓ REGEX LITERAL
 * - Java không có cú pháp regex literal như JS. Regex luôn đi qua một String, mà String cũng
 * dùng backslash làm escape. Nên có HAI tầng escape chồng lên nhau.
 * - Muốn regex là {@code \d} thì code phải viết {@code "\\d"}. Muốn regex khớp một dấu
 * backslash thật thì regex là {@code \\} và code phải viết bốn dấu backslash.
 * - Viết thiếu thì trình biên dịch báo "illegal escape character" ngay, nhưng có vài tổ hợp
 * lọt qua được compiler rồi sai lúc chạy. Luôn kiểm bằng cách in {@code pattern.pattern()}.
 * - JDK 15+ có text block ba nháy kép, nhưng text block KHÔNG tắt escape, nên vẫn phải nhân
 * đôi backslash. Nó chỉ giúp khi regex dài nhiều dòng dùng với cờ COMMENTS (bài t8).
 * <p>
 * 6. PATTERN AN TOÀN ĐA LUỒNG, MATCHER THÌ KHÔNG
 * - {@code Pattern} là immutable và thread-safe. {@code Matcher} có trạng thái (vị trí con
 * trỏ, kết quả group gần nhất) nên KHÔNG thread-safe.
 * - Hệ quả trực tiếp cho Spring: trong một service singleton, khai báo
 * {@code private static final Pattern} ở mức class, còn {@code Matcher} phải tạo mới
 * trong từng method. Để Matcher làm field là bug đa luồng im lặng, chỉ hiện khi tải cao.
 * - Bài t8 sẽ nói tiếp vì sao {@code compile()} phải nằm ngoài vòng lặp và ngoài method.
 * <p>
 * 7. BA CÂU HỎI KHÁC NHAU - ĐỪNG DÙNG NHẦM
 * - "Cả chuỗi có đúng khuôn không?" → {@code matcher.matches()} hoặc {@code String.matches()}.
 * - "Trong chuỗi có chỗ nào khớp không?" → {@code matcher.find()}.
 * - "Chuỗi có BẮT ĐẦU bằng khuôn này không?" → {@code matcher.lookingAt()}.
 * - Nhầm {@code matches()} với {@code find()} là lỗi số một của người mới. Bài t9 mổ kỹ.
 * <p>
 * 8. KHI NÀO KHÔNG DÙNG REGEX
 * - Tìm chuỗi con cố định: {@code String.contains()} nhanh hơn và rõ hơn.
 * - Parse HTML, XML, JSON, CSV có escape: dùng parser đúng, regex sẽ sai ở ca biên.
 * - Validate email cho thật: dùng annotation {@code Email} của Jakarta Bean Validation,
 * đừng tự chế.
 * - Regex hợp lý nhất ở: validate định dạng ngắn có quy tắc rõ (mã số thuế, biển số, SĐT),
 * tách token, và tìm/thay trong log.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - TS/JS có regex literal viết giữa hai dấu gạch chéo - không qua String nên không phải
 * nhân đôi backslash. Java không có. Đây là khác biệt gây vấp nhiều nhất khi chuyển sang.
 * - {@code new RegExp("\\d+")} bên JS mới giống Java: qua String thì cũng phải nhân đôi.
 * - Cờ {@code g} của JS không có tương đương trong Java. Java thay bằng vòng lặp
 * {@code while (m.find())}. Đổi lại, Java không dính bẫy {@code lastIndex} dùng chung
 * khi tái sử dụng một regex literal có cờ g.
 * - Vai trò tách đôi: JS gộp mọi thứ vào một object {@code RegExp}; Java tách
 * {@code Pattern} (bất biến, dùng chung) và {@code Matcher} (có trạng thái, dùng một lần).
 * Tách như vậy chính là thứ làm Java an toàn đa luồng còn JS thì không cần quan tâm.
 * - {@code \p} của Java tương ứng {@code \p} của JS khi bật cờ {@code u}. Cú pháp gần giống
 * nhưng tên property không trùng hoàn toàn - phải tra lại, đừng copy thẳng.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba lớp chính của {@code java.util.regex} là gì, mỗi lớp giữ vai trò nào?
 * - Vì sao không gọi được {@code new Pattern(...)}?
 * - Luồng ba bước từ chuỗi regex tới kết quả khớp gồm những gọi nào?
 * - Muốn regex khớp đúng một dấu backslash thì viết trong String Java ra sao?
 * - Lớp nào thread-safe, lớp nào không? Hệ quả khi đặt field trong Spring bean singleton?
 * - {@code matches()}, {@code find()}, {@code lookingAt()} khác nhau ở điểm nào?
 * - {@code \p} chữ thường và {@code \P} chữ hoa khác nhau chỗ nào?
 * - Script và block khác nhau thế nào, khi phân vân chọn cái nào?
 * - Kể ba trường hợp KHÔNG nên dùng regex.
 */
public class t1_IntroducingRegex {

    void main() {
        // TODO 1 (mục 2): Chạy đoạn dưới, rồi đổi input của matcher thành "a cat" và chạy lại.
        //   Ghi lại vì sao matches() đổi kết quả còn find() thì không.
        //
        Pattern pattern = Pattern.compile("cat");
        Matcher matcher = pattern.matcher("cat");

        System.out.println("regex       : " + pattern.pattern());
        System.out.println("matches()   : " + matcher.matches());
        System.out.println("find()      : " + pattern.matcher("a cat here").find());

        // TODO 2 (mục 5): In pattern.pattern() của Pattern.compile("\\d") và của
        //   Pattern.compile("\\\\"). Đếm số backslash in ra, đối chiếu với số viết trong code.
        //   Rồi thử viết Pattern.compile("\d") xem compiler báo lỗi gì.
        //
        // TODO 3 (mục 3): Khớp ký tự Hán "đông" bằng ba cách: "\\u6771", "\\x{6771}",
        //   và "\\p{IsHan}". Xác nhận cả ba cùng cho true trên chuỗi "東".
        //
        // TODO 4 (mục 3): Với chuỗi "abc123ĐÔNG東", đếm số ký tự khớp "\\p{IsL}",
        //   "\\p{IsLu}" và "\\p{script=Han}". Dùng vòng lặp while (m.find()).
        //
        // TODO 5 (mục 4): Viết lại RegexTestHarness của trang gốc: vòng lặp đọc regex và
        //   input từ bàn phím, in ra số match kèm start/end của từng match.
        //   Giữ lại, 9 bài sau sẽ dùng để thử nhanh.
        //   Gợi ý: System.console() có thể null khi chạy trong IDE - dùng Scanner(System.in).
        //
        // TODO 6 (mục 6): Tạo một Pattern static final, rồi cho hai luồng cùng gọi
        //   pattern.matcher(...) riêng - xác nhận chạy đúng. Sau đó thử DÙNG CHUNG một Matcher
        //   cho hai luồng và ghi lại kết quả sai hoặc exception quan sát được.
        //
        // TODO 7 (mục 7): Với regex "foo" và input "foobar", in cả ba:
        //   matches(), lookingAt(), find(). Lập bảng ba cột và giải thích từng ô.
        //
        // TODO 8 (mục 8): Viết hai cách kiểm tra chuỗi có chứa "error": dùng contains()
        //   và dùng regex. Đo thời gian 1 triệu lần mỗi cách và ghi lại kết luận.
    }
}
