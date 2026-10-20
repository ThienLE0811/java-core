package com.javacore.api.date_time_regex.regex;

import java.util.regex.Pattern;

/**
 * Predefined Character Classes
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 4/10 trong section Regular Expressions.
 * - Bài rất ngắn ở trang gốc, nhưng là nơi sinh ra bẫy ASCII-only lớn nhất của regex Java.
 * - Một câu cho cả bài: các lớp viết tắt tiện nhưng mặc định CHỈ hiểu ASCII, nên dùng với
 * dữ liệu tiếng Việt phải bật thêm cờ hoặc đổi sang thuộc tính Unicode.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/predefined-character-classes/">dev.java - Predefined Character Classes</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PREDEFINED CHARACTER CLASSES (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. BẢNG CẤU TRÚC - HỌC THUỘC
 * <pre>
 *   .     Any character (may or may not match line terminators)
 *   \d    A digit:                 [0-9]
 *   \D    A non-digit:             [^0-9]
 *   \s    A whitespace character:  [ \t\n\x0B\f\r]
 *   \S    A non-whitespace char:   [^\s]
 *   \w    A word character:        [a-zA-Z_0-9]
 *   \W    A non-word character:    [^\w]
 * </pre>
 * - Quy luật dễ nhớ: chữ THƯỜNG là tập đó, chữ HOA là phần bù của tập đó.
 * - Chú ý {@code \w} có cả dấu gạch dưới. Nó là "word character" theo nghĩa định danh của
 * lập trình, không phải "chữ cái".
 * - Sáu ký tự khoảng trắng của {@code \s}: dấu cách, tab, xuống dòng, vertical tab (0x0B),
 * form feed, carriage return.
 * <p>
 * 2. VÌ SAO NÊN DÙNG
 * - "Use the predefined classes whenever possible. They make your code easier to read and
 * eliminate errors introduced by malformed character classes."
 * - "Constructs beginning with a backslash are called escaped constructs."
 * <p>
 * 3. BACKSLASH TRONG STRING LITERAL
 * - Trong String Java phải nhân đôi: muốn regex {@code \d} thì code viết {@code "\\d"}.
 * - Trang gốc ghi rõ: khi đọc regex trực tiếp từ console (như test harness bài t1) thì
 * KHÔNG cần nhân đôi - tầng escape của String không tham gia.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. MẶC ĐỊNH CHỈ ASCII - BẪY LỚN NHẤT CỦA BÀI NÀY
 * - {@code \w} là ĐÚNG {@code [a-zA-Z_0-9]}. Không có ă, â, đ, ê, ô, ơ, ư, không có chữ Hán,
 * không có chữ Cyrillic. Validate "họ tên" bằng {@code \w} là loại sạch tên tiếng Việt.
 * - {@code \d} là ĐÚNG {@code [0-9]}. Không khớp chữ số Ả Rập-Ấn hay chữ số toàn hình
 * (fullwidth) mà người dùng có thể dán vào từ bàn phím khác.
 * - Hai cách mở rộng ra Unicode:
 * + Bật cờ {@code Pattern.UNICODE_CHARACTER_CLASS} (hoặc nhúng {@code (?U)}). Cờ này làm
 * {@code \w \d \s \b} hiểu theo chuẩn Unicode. Có từ Java 7. Xem bài t8.
 * + Hoặc viết thẳng thuộc tính Unicode: {@code \p{L}} cho chữ cái, {@code \p{N}} cho chữ số,
 * {@code \p{IsAlphabetic}} cho ký tự chữ.
 * - Khuyến nghị thực tế: dữ liệu người dùng nhập tự do thì dùng {@code \p{...}}; dữ liệu
 * máy sinh ra có khuôn cố định (mã giao dịch, UUID, số serial) thì {@code \w \d} là đủ và
 * còn nhanh hơn.
 * <p>
 * 5. DẤU CHẤM VÀ KÝ TỰ XUỐNG DÒNG
 * - Trang gốc viết "may or may not match line terminators" - mập mờ có chủ ý. Sự thật:
 * mặc định dấu chấm KHÔNG khớp ký tự kết thúc dòng.
 * - Muốn nó khớp cả xuống dòng thì bật cờ {@code DOTALL} (hoặc nhúng {@code (?s)}) - bài t8.
 * - Hệ quả thực tế: parse log nhiều dòng bằng {@code .*} sẽ dừng ở cuối dòng đầu tiên, rất
 * hay bị tưởng là regex sai.
 * <p>
 * 6. \s VÀ CRLF - BẪY WINDOWS
 * - File tạo trên Windows kết thúc dòng bằng {@code \r\n} (hai ký tự), Linux chỉ {@code \n}.
 * - {@code \s} khớp cả hai ký tự đó, nhưng {@code trim()} và {@code strip()} cũng vậy -
 * nên lỗi hay lộ ra ở chỗ khác: {@code "abc\r"} so sánh {@code equals("abc")} ra false.
 * - Khi đọc file cấu hình hay CSV, chuẩn hóa xuống dòng TRƯỚC, đừng vá bằng regex.
 * - {@code \s} KHÔNG khớp non-breaking space (U+00A0) - ký tự hay lọt vào khi người dùng
 * copy từ Word hay trình duyệt. Đây là nguồn bug "nhìn giống dấu cách mà không phải".
 * Bật {@code UNICODE_CHARACTER_CLASS} thì {@code \s} mới bắt được nó.
 * <p>
 * 7. CÁC LỚP POSIX - CÓ SẴN NHƯNG TRANG GỐC KHÔNG NHẮC
 * - Java còn hỗ trợ {@code \p{Alpha}}, {@code \p{Digit}}, {@code \p{Alnum}},
 * {@code \p{Punct}}, {@code \p{Upper}}, {@code \p{Lower}}, {@code \p{Space}},
 * {@code \p{XDigit}} (chữ số hex) và vài cái khác.
 * - Chúng cũng chỉ ASCII theo mặc định, cùng một bẫy như mục 4.
 * - {@code \p{XDigit}} tiện khi validate mã màu hex hay chuỗi hex của chữ ký HMAC.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - {@code \d \D \s \S \w \W} có đủ bên JS với ý nghĩa gần như y hệt.
 * - Khác biệt đáng nhớ: {@code \s} bên JS RỘNG HƠN - nó bao gồm cả non-breaking space và
 * các khoảng trắng Unicode khác ngay từ mặc định. Java thì không, trừ khi bật
 * {@code UNICODE_CHARACTER_CLASS}. Cùng một regex trim khoảng trắng chạy đúng ở frontend
 * mà sai ở backend Java chính là do chỗ này.
 * - Cờ {@code s} của JS = {@code DOTALL} của Java. Tên khác, nghĩa giống.
 * - JS không có {@code UNICODE_CHARACTER_CLASS}; muốn tương đương phải viết
 * {@code [\p{L}\p{N}_]} với cờ {@code u}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Viết lại {@code \d \s \w} dưới dạng character class tường minh, không nhìn bảng.
 * - {@code \w} có chứa dấu gạch dưới không? Có chứa dấu gạch ngang không?
 * - {@code \d} có khớp chữ số tiếng Ả Rập hay chữ số fullwidth không? Vì sao?
 * - Hai cách làm {@code \w} hiểu tiếng Việt là gì? Cách nào nên dùng khi nào?
 * - Dấu chấm mặc định có khớp ký tự xuống dòng không? Bật bằng cờ nào?
 * - {@code \s} có khớp non-breaking space không? Hệ quả thực tế là gì?
 * - {@code \s} bên Java và bên JavaScript khác nhau ở điểm nào?
 * - Kể ba lớp POSIX và nói chúng có thoát được bẫy ASCII không.
 */
public class t4_PredefinedCharacterClasses {

    void main() {
        // TODO 1 (mục 1, 4): Chạy đoạn dưới. Ghi lại ký tự nào trượt và đối chiếu với định
        //   nghĩa [a-zA-Z_0-9] của \w.
        //
        String[] samples = {"a", "_", "9", "-", "ê", "東", " "};

        for (String s : samples) {
            System.out.println("[" + s + "] \\w=" + Pattern.matches("\\w", s)
                    + "  \\d=" + Pattern.matches("\\d", s)
                    + "  \\s=" + Pattern.matches("\\s", s));
        }

        // TODO 2 (mục 4): Chạy lại đúng mảng trên với Pattern.compile("\\w", Pattern.UNICODE_CHARACTER_CLASS).
        //   Lập bảng trước/sau và chỉ ra ký tự nào đổi kết quả.
        //
        // TODO 3 (mục 4): Làm lại lần nữa bằng "\\p{L}" không cần cờ.
        //   So sánh ba cách và chọn cách bạn sẽ dùng cho field hoTen, giải thích lý do.
        //
        // TODO 4 (mục 5): Với input "dong 1\ndong 2", đếm số match của ".*" rồi của "(?s).*".
        //   Giải thích vì sao hai con số khác nhau.
        //
        // TODO 5 (mục 6): Tạo chuỗi "abc def" (có non-breaking space). Thử tách bằng
        //   "\\s+" ở cả hai chế độ: không cờ và có UNICODE_CHARACTER_CLASS. In length mảng.
        //
        // TODO 6 (mục 6): Tạo hai chuỗi "abc\r\n" và "abc\n". Dùng "\\s+$" để cắt đuôi,
        //   rồi so sánh kết quả với strip(). Kết luận nên dùng cái nào.
        //
        // TODO 7 (mục 1): Viết regex validate mã giao dịch dạng 3 chữ cái hoa + 6 chữ số,
        //   ví dụ "TXN123456". Dùng predefined class. Test cả ca hợp lệ và không hợp lệ.
        //
        // TODO 8 (mục 7): Viết regex validate mã màu hex "#A3F2C1" bằng \\p{XDigit}.
        //   So sánh với cách viết bằng [0-9a-fA-F] về độ dài và độ rõ.
        //
        // TODO 9 (mục 3): Đọc một regex từ bàn phím (dùng harness bài t1) và gõ đúng \d
        //   một backslash. Xác nhận nó chạy, rồi giải thích vì sao trong code phải viết hai.
    }
}
