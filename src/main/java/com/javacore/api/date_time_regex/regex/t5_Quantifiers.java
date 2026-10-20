package com.javacore.api.date_time_regex.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Quantifiers
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 5/10 trong section Regular Expressions.
 * - BÀI QUAN TRỌNG NHẤT CỦA SECTION. Đây là nơi sinh ra cả hai loại lỗi đắt nhất khi dùng
 * regex trong production: khớp sai đoạn (greedy nuốt quá tay) và treo CPU (ReDoS).
 * - Một câu cho cả bài: quantifier không chỉ quyết định "bao nhiêu lần", nó quyết định
 * CÁCH bộ máy đi tìm - và cách đi tìm quyết định cả kết quả lẫn thời gian chạy.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/quantifiers/">dev.java - Quantifiers</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - QUANTIFIERS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. BẢNG BA CỘT - HỌC THUỘC
 * <pre>
 *   Greedy    Reluctant   Possessive   Ý nghĩa
 *   X?        X??         X?+          X, once or not at all
 *   X*        X*?         X*+          X, zero or more times
 *   X+        X+?         X++          X, one or more times
 *   X{n}      X{n}?       X{n}+        X, exactly n times
 *   X{n,}     X{n,}?      X{n,}+       X, at least n times
 *   X{n,m}    X{n,m}?     X{n,m}+      X, at least n but not more than m times
 * </pre>
 * - Quy luật tạo cột: greedy là dạng gốc, thêm {@code ?} thành reluctant, thêm {@code +}
 * thành possessive.
 * - Đừng nhầm {@code X?} (quantifier "không hoặc một") với dấu {@code ?} ĐỨNG SAU một
 * quantifier khác (biến nó thành reluctant). Cùng một ký tự, hai vai trò.
 * <p>
 * 2. QUANTIFIER BÁM VÀO CÁI GÌ
 * - "Quantifiers can only attach to one character at a time, so the regular expression
 * {@code abc+} would mean 'a, followed by b, followed by c one or more times'."
 * - Muốn lặp cả cụm thì bọc group: {@code (abc)+}. Muốn lặp một tập ký tự thì dùng
 * character class: {@code [abc]+}.
 * - Ba thứ này KHÁC HẲN nhau, viết nhầm là sai âm thầm:
 * + {@code abc+} → "ab" rồi một hoặc nhiều chữ "c".
 * + {@code (abc)+} → cụm "abc" lặp một hoặc nhiều lần.
 * + {@code [abc]+} → một hoặc nhiều ký tự, mỗi ký tự là a, b, hoặc c.
 * <p>
 * 3. ZERO-LENGTH MATCHES
 * - "Matches of this sort are known as zero-length matches." Xảy ra khi start index bằng
 * end index.
 * - Trang gốc nói chúng xuất hiện ở: chuỗi rỗng, đầu chuỗi, sau ký tự cuối cùng, và giữa
 * hai ký tự bất kỳ.
 * - Đây là lý do {@code a?} chạy trên "aaa" cho NHIỀU match hơn số chữ a: sau mỗi chữ a
 * còn một match rỗng, và cuối chuỗi cũng có một match rỗng. Tự chạy TODO 3 để đếm.
 * - Trong production: vòng {@code while (m.find())} với regex có thể khớp rỗng vẫn kết thúc
 * (Java tự đẩy con trỏ), nhưng {@code split()} và {@code replaceAll()} cho kết quả rất lạ.
 * Quy tắc an toàn: regex dùng cho split/replace nên bảo đảm khớp ít nhất một ký tự
 * (dùng {@code +} thay {@code *}).
 * <p>
 * 4. GREEDY, RELUCTANT, POSSESSIVE - CƠ CHẾ
 * - Greedy: "force the matcher to read in, or eat, the entire input string prior to
 * attempting the first match." Nếu trượt thì "backs off the input string by one character
 * and tries again" - lùi dần tới khi khớp.
 * - Reluctant: "take the opposite approach: They start at the beginning of the input string,
 * then reluctantly eat one character at a time looking for a match."
 * - Possessive: "Possessive quantifiers always eat the entire input string, trying once
 * (and only once) for a match. Unlike the greedy quantifiers, possessive quantifiers never
 * back off."
 * - Từ khóa phân biệt: greedy CÓ lùi, reluctant tiến dần, possessive KHÔNG BAO GIỜ lùi.
 * Chữ "backtracking" chính là động tác lùi đó.
 * <p>
 * 5. VÍ DỤ KINH ĐIỂN - INPUT "xfooxxxxxxfoo"
 * - {@code .*foo} (greedy): nuốt cả chuỗi rồi lùi dần cho tới khi trả lại được "foo" ở cuối.
 * Kết quả là MỘT match phủ toàn bộ chuỗi.
 * - {@code .*?foo} (reluctant): ăn từng ký tự một, nên dừng ngay khi gặp "foo" đầu tiên.
 * Kết quả là NHIỀU match ngắn.
 * - {@code .*+foo} (possessive): nuốt cả chuỗi, không lùi, nên không còn gì cho "foo".
 * Kết quả: KHÔNG match nào.
 * - Chạy TODO 4 để tự in start/end của từng match ba trường hợp. Ghi số liệu thật vào đây.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. CATASTROPHIC BACKTRACKING VÀ ReDoS - PHẦN PRODUCTION QUAN TRỌNG NHẤT
 * - Backtracking của greedy không miễn phí. Khi hai quantifier LỒNG NHAU và cùng khớp được
 * một tập ký tự, số đường đi mà bộ máy phải thử tăng theo HÀM MŨ của độ dài input.
 * - Mẫu nguy hiểm kinh điển: {@code (a+)+b}. Với input toàn chữ a mà không có b, Java phải
 * thử mọi cách chia chuỗi a thành các nhóm. Input 30 ký tự đã treo nhiều giây; 40 ký tự là
 * coi như vô hạn.
 * - Các mẫu cùng họ, nhìn đâu cũng thấy trong code thật:
 * + {@code (\s*,)*} dùng để tách danh sách.
 * + {@code (\w+\s?)*} dùng để validate tên.
 * + {@code ^(.*,)*.*$} dùng để parse CSV.
 * + Hai nhánh {@code |} khớp chồng nhau rồi bọc {@code *}: {@code (a|aa)+}.
 * - ReDoS là lỗ hổng BẢO MẬT thật: một request HTTP với body vài chục byte có thể ghim
 * 100% một core. Trong Spring Boot mỗi request chiếm một thread, nên vài request là đủ làm
 * cạn thread pool - toàn bộ service đứng.
 * - Ba lớp phòng thủ, theo thứ tự ưu tiên:
 * + VIẾT LẠI REGEX cho hết lồng nhau. Gần như lúc nào cũng làm được và đây là cách đúng.
 * + Dùng possessive quantifier hoặc atomic group {@code (?>X)} để CẤM lùi ở chỗ không cần
 * lùi. Hai thứ này là cùng một cơ chế: {@code X*+} tương đương {@code (?>X*)}.
 * + GIỚI HẠN ĐỘ DÀI INPUT trước khi cho chạm regex. Rẻ nhất, hiệu quả nhất, và nên làm
 * kể cả khi regex đã an toàn.
 * - Java KHÔNG có timeout cho việc so khớp. Không có cờ nào cứu được. Nếu bắt buộc phải
 * chạy regex do người dùng cung cấp, phải tự đẩy sang thread riêng có hạn giờ - và nhớ là
 * không hủy được thread đang backtracking một cách sạch sẽ.
 * <p>
 * 7. KHI NÀO POSSESSIVE THỰC SỰ HỮU ÍCH
 * - Dùng khi bạn BIẾT CHẮC phần đã khớp không bao giờ cần trả lại. Ví dụ:
 * {@code \d++} để đọc một dãy số - đã là số thì không có lý do lùi.
 * - Lợi ích kép: nhanh hơn (không lưu trạng thái để lùi) và an toàn hơn (chặn ReDoS).
 * - Rủi ro: dùng bừa sẽ làm regex không khớp được nữa, đúng như ví dụ {@code .*+foo} ở
 * mục 5. Nguyên tắc: possessive hợp với class HẸP ({@code \d++}, {@code [^,]++}), nguy
 * hiểm với class RỘNG ({@code .*+}).
 * <p>
 * 8. GREEDY NUỐT QUÁ TAY - BUG IM LẶNG HAY GẶP NHẤT
 * - Lấy nội dung giữa hai dấu ngoặc kép bằng {@code ".*"} trên chuỗi có HAI cặp ngoặc sẽ
 * nuốt luôn cả phần ở giữa. Phải dùng {@code ".*?"} hoặc tốt hơn là {@code "[^"]*"}.
 * - Quy tắc thực tế: khi có "ký tự kết thúc" rõ ràng, dùng CLASS PHỦ ĐỊNH
 * ({@code [^"]*}) thay vì reluctant ({@code .*?}). Nhanh hơn nhiều và không cần backtrack.
 * - Đây là mẹo quan trọng nhất của bài: {@code [^X]*X} luôn tốt hơn {@code .*?X}.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - Greedy và reluctant giống hệt nhau hai bên: {@code X*} và {@code X*?}.
 * - Possessive {@code X*+} và atomic group {@code (?>X)} là ĐẶC SẢN Java. JS chuẩn không
 * có. Hệ quả: regex chống ReDoS viết cho Java không chuyển thẳng sang TS được - phía TS
 * phải viết lại bằng class phủ định hoặc giới hạn độ dài.
 * - ReDoS tồn tại ở cả hai. Node còn nguy hiểm hơn vì chỉ có MỘT event loop: một regex treo
 * là chết cả process, không phải chỉ một thread như Java.
 * - {@code X{n,m}} giống nhau. Nhưng chú ý: bên Java {@code {} viết sai cú pháp là
 * {@code PatternSyntaxException} ngay lúc compile; JS có chỗ lại coi đó là ký tự thường.
 * Java nghiêm hơn, và nghiêm ở đây là tốt.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Viết lại bảng ba cột quantifier không nhìn tài liệu.
 * - {@code abc+}, {@code (abc)+} và {@code [abc]+} khác nhau thế nào?
 * - Zero-length match là gì, xảy ra ở những vị trí nào?
 * - {@code a?} chạy trên "aaa" cho mấy match? Vì sao nhiều hơn 3?
 * - Mô tả bằng lời cơ chế của greedy, reluctant, possessive. Cái nào có backtracking?
 * - Vì sao {@code .*+foo} không khớp được "xfooxxxxxxfoo"?
 * - Vì sao {@code (a+)+b} nguy hiểm? Nêu ba cách phòng thủ theo thứ tự ưu tiên.
 * - Java có timeout cho regex không?
 * - Giữa {@code ".*?"} và {@code "[^"]*"} nên chọn cái nào và vì sao?
 * - Possessive hợp với loại character class nào, nguy hiểm với loại nào?
 */
public class t5_Quantifiers {

    void main() {
        // TODO 1 (mục 4, 5): Chạy đoạn dưới. Dán kết quả thật vào mục 5 của phần note trên.
        //
        String input = "xfooxxxxxxfoo";
        String[] regexes = {".*foo", ".*?foo", ".*+foo"};

        for (String regex : regexes) {
            Matcher m = Pattern.compile(regex).matcher(input);
            System.out.print(regex + " -> ");
            boolean found = false;
            while (m.find()) {
                found = true;
                System.out.print("[" + m.start() + "," + m.end() + ")=" + m.group() + " ");
            }
            System.out.println(found ? "" : "KHONG MATCH");
        }

        // TODO 2 (mục 2): So sánh "abc+", "(abc)+" và "[abc]+" trên các input
        //   "abccc", "abcabc", "cba". Lập bảng 3x3 kết quả matches().
        //
        // TODO 3 (mục 3): Đếm số match của "a?", "a*", "a+" trên input "" và trên "aaa".
        //   In start/end từng match. Giải thích con số bằng khái niệm zero-length match.
        //
        // TODO 4 (mục 3): Chạy "a*".split trên "aaabaaac" và "[a]*" replaceAll thành "-".
        //   Quan sát dấu "-" thừa ra ở đâu, rồi đổi * thành + và chạy lại.
        //
        // TODO 5 (mục 6): Đo thời gian Pattern.matches("(a+)+b", "a".repeat(n) ) với
        //   n = 20, 24, 28, 30. In thời gian mỗi lần. DỪNG LẠI trước khi n quá lớn.
        //   Vẽ ra nhận xét: thời gian tăng theo kiểu gì?
        //
        // TODO 6 (mục 6, 7): Chạy lại TODO 5 với "(a++)+b" và với "(?>a+)+b".
        //   Xác nhận cả hai trả về ngay lập tức. Kết luận về quan hệ giữa hai cú pháp.
        //
        // TODO 7 (mục 6): Viết lại "(a+)+b" thành một regex tương đương KHÔNG lồng quantifier.
        //   Xác nhận nó cho cùng kết quả trên các input hợp lệ và chạy nhanh trên input xấu.
        //
        // TODO 8 (mục 8): Với input: ten="Thien" lop="Java"
        //   Thử ba regex: "\".*\"", "\".*?\"", "\"[^\"]*\"".
        //   In tất cả match của từng cái. Giải thích vì sao cái đầu chỉ ra một match dài.
        //
        // TODO 9 (mục 8): Đo thời gian của "\".*?\"" và "\"[^\"]*\"" trên một chuỗi dài
        //   có 1000 cặp ngoặc kép. Ghi lại chênh lệch.
    }
}
