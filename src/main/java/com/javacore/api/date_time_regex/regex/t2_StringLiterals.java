package com.javacore.api.date_time_regex.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String Literals
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 2/10 trong section Regular Expressions.
 * - Bài ngắn nhưng dựng hai thứ nền: quy ước CHỈ SỐ start/end của match, và khái niệm
 * METACHARACTER - ranh giới giữa "ký tự thường" và "ký tự có phép thuật".
 * - Một câu cho cả bài: regex đơn giản nhất là chuỗi nguyên văn, và mọi thứ phức tạp sau này
 * chỉ là thêm metacharacter vào chuỗi đó.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/string-literals/">dev.java - String Literals</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - STRING LITERALS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. KHỚP NGUYÊN VĂN
 * - "If the regular expression is foo and the input string is foo, the match will succeed
 * because the strings are identical."
 * - Đây là loại regex đơn giản nhất: không có ký tự đặc biệt nào, khớp đúng từng ký tự.
 * <p>
 * 2. QUY ƯỚC CHỈ SỐ - START ĐÓNG, END MỞ
 * - Với chuỗi "foo": start index là 0, end index là 3. Ba ký tự nằm ở ô 0, 1, 2.
 * - Trang gốc vẽ chỉ số nằm GIỮA các ô ký tự, không phải trong ô. Hiểu theo kiểu "con trỏ
 * đứng giữa hai ký tự" thì mọi chuyện sau này (nhất là zero-length match ở bài t5) đều dễ.
 * - Khoảng là nửa mở: bao gồm start, KHÔNG bao gồm end. Giống hệt
 * {@code String.substring(start, end)} - và đó là lý do
 * {@code input.substring(m.start(), m.end())} luôn ra đúng đoạn khớp.
 * - Khi tìm nhiều match liên tiếp: "With subsequent matches, you will notice some overlap;
 * the start index for the next match is the same as the end index of the previous match."
 * <p>
 * 3. METACHARACTERS
 * - Định nghĩa: ký tự được matcher hiểu theo nghĩa ĐẶC BIỆT chứ không phải nghĩa đen.
 * - Ví dụ trang gốc: regex {@code cat.} với input {@code cats} thì KHỚP, vì dấu chấm là
 * metacharacter mang nghĩa "một ký tự bất kỳ".
 * - Danh sách metacharacter trang gốc liệt kê: {@code <([{\^-=$!|]})?*+.>}
 * - Hai lưu ý nguyên văn:
 * + "In certain situations the special characters listed above will not be treated as
 * metacharacters." Ví dụ dấu {@code -} chỉ đặc biệt khi nằm GIỮA hai ký tự trong ngoặc
 * vuông; dấu {@code ^} chỉ phủ định khi đứng ngay sau {@code [}.
 * + Những ký tự như {@code @} và {@code #} thì không bao giờ mang nghĩa đặc biệt.
 * - Hai cách ép metacharacter thành ký tự thường:
 * + Thêm backslash đứng trước nó.
 * + Bọc giữa {@code \Q} (bắt đầu trích) và {@code \E} (kết thúc trích). Trang gốc nói:
 * "the \Q and \E can be placed at any location within the expression, provided that the
 * \Q comes first."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. PATTERN.QUOTE - CÁCH ĐÚNG ĐỂ GHÉP DỮ LIỆU NGƯỜI DÙNG VÀO REGEX
 * - Tự nối chuỗi người dùng vào regex là lỗ hổng: họ nhập {@code (} là
 * {@code PatternSyntaxException}, nhập {@code (a+)+} là treo CPU (xem bài t5, ReDoS).
 * - {@code Pattern.quote(s)} bọc sẵn {@code \Q...\E} quanh chuỗi, trả về một regex khớp
 * ĐÚNG NGUYÊN VĂN chuỗi đó. Đây là cách duy nhất nên dùng khi phần nào của regex đến từ
 * input bên ngoài.
 * - Nhầm lẫn hay gặp: {@code Pattern.quote} dùng cho phần PATTERN. Phía chuỗi THAY THẾ có
 * hàm riêng là {@code Matcher.quoteReplacement} - bài t9.
 * - Trong Spring, chỗ hay dính nhất là tìm kiếm theo từ khóa gõ tự do: nếu dựng regex từ
 * keyword, phải quote. Tốt hơn nữa là đừng dùng regex, đẩy xuống SQL với tham số ràng buộc.
 * <p>
 * 5. BẪY \Q ... \E
 * - Bên trong {@code \Q...\E} mọi ký tự đều là nghĩa đen, KỂ CẢ backslash.
 * - Nhưng nếu chính chuỗi dữ liệu chứa sẵn {@code \E} thì vùng trích bị cắt sớm và phần còn
 * lại quay về nghĩa regex. Đây chính là lý do phải dùng {@code Pattern.quote} thay vì tự
 * nối {@code "\\Q" + s + "\\E"} - hàm thư viện có xử lý ca này.
 * <p>
 * 6. BA CHỖ DỄ QUÊN ESCAPE TRONG CODE THẬT
 * - Dấu chấm trong IP hay tên file: {@code "192.168.1.1"} làm regex sẽ khớp cả
 * "192a168b1c1". Phải viết {@code "192\\.168\\.1\\.1"}.
 * - Dấu {@code +} trong số điện thoại quốc tế: {@code "+84..."} là lỗi cú pháp vì
 * {@code +} là quantifier không có gì đứng trước. Phải escape.
 * - Dấu {@code $} trong chuỗi tiền tệ, và dấu {@code |} trong log phân tách bằng pipe -
 * {@code split("|")} tách ra TỪNG KÝ TỰ chứ không tách theo pipe. Phải là
 * {@code split("\\|")} hoặc {@code split(Pattern.quote("|"))}.
 * <p>
 * 7. ĐỐI CHIẾU TYPESCRIPT
 * - Danh sách metacharacter hai bên gần như trùng nhau vì cùng gốc Perl.
 * - {@code \Q...\E} là của Java/Perl, JS KHÔNG có. Bên JS phải tự escape bằng replace với
 * một regex liệt kê ký tự đặc biệt, hoặc dùng {@code RegExp.escape} nếu runtime đã hỗ trợ.
 * Java cho sẵn {@code Pattern.quote} từ Java 5 - đây là điểm Java tiện hơn.
 * - {@code m.start()}/{@code m.end()} ≈ {@code match.index} và
 * {@code match.index + match[0].length} bên JS. Java cho sẵn cả hai đầu nên không phải cộng tay.
 * - Bẫy {@code split("|")} có y hệt bên JS: {@code "a|b".split("|")} cũng ra từng ký tự nếu
 * truyền regex. Khác là JS cho truyền STRING thường và khi đó tách đúng theo nghĩa đen;
 * Java thì {@code String.split} LUÔN hiểu đối số là regex, không có bản nghĩa đen.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Với match đầu tiên của regex "foo" trong "foofoo", start và end là bao nhiêu?
 * Match thứ hai thì sao?
 * - Vì sao {@code input.substring(m.start(), m.end())} luôn ra đúng đoạn khớp?
 * - Kể ít nhất 8 metacharacter mà không nhìn bảng.
 * - Nêu một ký tự trong bảng metacharacter nhưng KHÔNG phải lúc nào cũng đặc biệt, và nói
 * rõ khi nào nó đặc biệt.
 * - Hai cách ép một metacharacter về nghĩa đen là gì?
 * - Vì sao nên dùng {@code Pattern.quote} thay vì tự nối {@code \Q} và {@code \E}?
 * - {@code "a|b|c".split("|")} ra kết quả gì? Sửa thế nào cho đúng?
 * - {@code Pattern.quote} và {@code Matcher.quoteReplacement} khác nhau ở chỗ nào?
 */
public class t2_StringLiterals {

    void main() {
        // TODO 1 (mục 1, 2): Chạy đoạn dưới. Ghi lại start/end của từng match và kiểm chứng
        //   câu "start của match sau bằng end của match trước".
        //
        Matcher matcher = Pattern.compile("foo").matcher("foofoo");

        while (matcher.find()) {
            System.out.println("match tai [" + matcher.start() + ", " + matcher.end() + ") = "
                    + matcher.group());
        }

        // TODO 2 (mục 3): So sánh regex "cat." với "cat\\." trên hai input "cats" và "cat.".
        //   Lập bảng 2x2 kết quả matches() và giải thích từng ô.
        //
        // TODO 3 (mục 3): Với input "a.b.c", đếm số match của regex "." rồi của regex "\\.".
        //   Giải thích vì sao hai con số khác nhau.
        //
        // TODO 4 (mục 3, 4): Viết hàm nhận keyword do người dùng nhập và trả về true nếu
        //   văn bản chứa keyword đó. Làm hai bản: nối chuỗi trực tiếp, và dùng Pattern.quote.
        //   Truyền keyword "(" vào cả hai, dán lại exception của bản sai.
        //
        // TODO 5 (mục 3): Dùng \\Q và \\E để khớp nguyên văn chuỗi "1+1=2". Sau đó thử bỏ
        //   \\Q\\E và escape tay từng ký tự. So sánh độ dài và độ dễ đọc của hai cách.
        //
        // TODO 6 (mục 6): Viết regex khớp đúng địa chỉ IP "192.168.1.1". Thử nó trên chuỗi
        //   "192a168b1c1" ở cả hai bản có escape và không escape dấu chấm.
        //
        // TODO 7 (mục 6): Tách chuỗi log "INFO|2026-10-20|user login" theo dấu pipe.
        //   Làm ba cách: split("|"), split("\\|"), split(Pattern.quote("|")).
        //   In length của mảng kết quả từng cách.
        //
        // TODO 8 (mục 5): Tạo chuỗi dữ liệu có chứa sẵn "\\E" bên trong, rồi so sánh kết quả
        //   giữa tự nối "\\Q" + s + "\\E" và Pattern.quote(s).
        //   In cả hai pattern ra để thấy Pattern.quote xử lý thêm chuyện gì.
    }
}
