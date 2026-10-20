package com.javacore.api.date_time_regex.regex;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Methods of the PatternSyntaxException Class
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 10/10 - bài CUỐI của section Regular Expressions.
 * - Phần API của bài rất ngắn (bốn method). Phần giá trị nằm ở chỗ: đây là nơi gom lại cả
 * section và chốt những thứ mang sang production.
 * - Một câu cho cả bài: regex sai cú pháp là lỗi LÚC CHẠY chứ không phải lúc biên dịch, nên
 * mọi regex phải được compile ở chỗ chương trình khởi động, không phải giữa luồng xử lý.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/pattern-syntax-exception/">dev.java - Methods of the PatternSyntaxException Class</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PATTERNSYNTAXEXCEPTION (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA
 * - "A PatternSyntaxException is an unchecked exception that indicates a syntax error in a
 * regular expression pattern."
 * - UNCHECKED nghĩa là compiler KHÔNG bắt bạn try-catch. Nên nó lọt tới production dễ dàng.
 * <p>
 * 2. BỐN METHOD
 * - {@code getDescription()} - mô tả lỗi cú pháp gặp phải.
 * - {@code getIndex()} - vị trí trong chuỗi regex nơi xảy ra lỗi.
 * - {@code getPattern()} - chính chuỗi regex bị lỗi.
 * - {@code getMessage()} - chuỗi NHIỀU DÒNG gộp đủ: mô tả lỗi, vị trí, "the erroneous
 * regular-expression pattern, and a visual indication of the error index within the pattern."
 * Tức là có cả một dòng dấu mũ chỉ thẳng vào chỗ sai.
 * <p>
 * 3. VÍ DỤ TRANG GỐC
 * - Regex hỏng: {@code ?i)foo}. Trang gốc giải thích đây là lỗi "the programmer has
 * forgotten the opening parenthesis in the embedded flag expression {@code (?i)}" - quên
 * dấu mở ngoặc của embedded flag đã học ở bài t8 mục 4.
 * - Kết quả in ra cho thấy "the syntax error is a dangling metacharacter (the question mark)
 * at index 0".
 * - Chạy TODO 1 rồi dán output THẬT của JDK 25 vào đây - thông điệp lỗi có thay đổi nhẹ
 * giữa các phiên bản JDK, đừng chép từ tài liệu cũ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. NÉM LÚC NÀO - CHI TIẾT QUYẾT ĐỊNH CÁCH VIẾT CODE
 * - Ném tại {@code Pattern.compile()}, tức là lúc BIÊN DỊCH REGEX, không phải lúc so khớp.
 * - Hệ quả then chốt: nếu mọi regex là {@code private static final Pattern} thì regex hỏng
 * làm lớp đó không khởi tạo được - ứng dụng chết NGAY lúc khởi động, lỗi lộ ra ở môi
 * trường dev. Đây là lý do thứ hai (sau hiệu năng, bài t8 mục 7) để làm {@code static final}.
 * - Ngược lại, nếu regex nằm trong {@code String.matches()} giữa một method service thì lỗi
 * chỉ nổ khi có request chạm vào nhánh đó - có thể là 3 giờ sáng Chủ nhật.
 * - Lưu ý về static initializer: regex hỏng trong {@code static final} sẽ biểu hiện thành
 * {@code ExceptionInInitializerError}, và ở lần truy cập sau là
 * {@code NoClassDefFoundError}. Nguyên nhân thật nằm ở "Caused by" tận dưới cùng - phải
 * đọc hết stack trace.
 * <p>
 * 5. KHI NÀO CẦN BẮT NÓ
 * - Chỉ một trường hợp: regex đến từ BÊN NGOÀI - người dùng nhập, file cấu hình, bảng trong
 * DB. Lúc đó bắt và trả về lỗi 400 kèm {@code getDescription()} và {@code getIndex()} để
 * người dùng sửa được.
 * - Tuyệt đối không nuốt im lặng. Và nhớ: regex do người dùng cung cấp còn mở ra ReDoS
 * (bài t5 mục 6) - cú pháp đúng không có nghĩa là an toàn.
 * - Regex do chính bạn viết trong code thì KHÔNG bắt. Bắt nó là che lỗi của chính mình.
 * <p>
 * ======================================================================
 * TỔNG KẾT CẢ SECTION REGULAR EXPRESSIONS
 * ======================================================================
 * <p>
 * 6. BẢN ĐỒ 10 BÀI
 * <pre>
 *   t1  Giới thiệu      Pattern / Matcher / PatternSyntaxException, Unicode, harness
 *   --- CÚ PHÁP ---
 *   t2  String literal  chỉ số start/end, metacharacter, \Q \E, Pattern.quote
 *   t3  Character class [abc] [^abc] [a-z], union, giao, trừ
 *   t4  Lớp dựng sẵn    . \d \s \w và phần bù, bẫy ASCII-only
 *   t5  Quantifier      greedy / reluctant / possessive, zero-length, ReDoS
 *   t6  Group           đánh số, backreference, named group, lookaround
 *   t7  Boundary        ^ $ \b \B \A \G \Z \z, MULTILINE
 *   --- API ---
 *   t8  Pattern         compile một lần, các cờ, split, API stream
 *   t9  Matcher         matches/lookingAt/find, replace, trạng thái, results()
 *   t10 Exception       lỗi lúc chạy, và tổng kết này
 * </pre>
 * <p>
 * 7. NĂM QUY TẮC MANG SANG DỰ ÁN THẬT
 * - MỘT: mọi regex dùng lại là {@code private static final Pattern}. Vừa nhanh hơn, vừa
 * biến lỗi cú pháp thành lỗi lúc khởi động. (t8 mục 7, t10 mục 4)
 * - HAI: {@code Pattern} dùng chung được, {@code Matcher} thì không. Tạo Matcher mới trong
 * từng lần gọi. (t1 mục 6, t9 mục 7)
 * - BA: giới hạn độ dài input trước khi cho chạm regex, và tránh quantifier lồng nhau.
 * Java không có timeout cho regex. (t5 mục 6)
 * - BỐN: dữ liệu từ bên ngoài phải đi qua {@code Pattern.quote} ở phía pattern và
 * {@code Matcher.quoteReplacement} ở phía replacement. (t2 mục 4, t9 mục 6)
 * - NĂM: dữ liệu tiếng Việt thì {@code \w \d \b} mặc định là SAI. Dùng
 * {@code UNICODE_CHARACTER_CLASS} hoặc {@code \p{L}}, và chuẩn hóa NFC trước khi so khớp.
 * (t3 mục 9, t4 mục 4, t7 mục 8)
 * <p>
 * 8. REGEX TRONG SPRING BOOT - NƠI BẠN SẼ GẶP LẠI CẢ SECTION NÀY
 * - Bean Validation: annotation {@code Pattern} của Jakarta nhận thuộc tính {@code regexp}
 * và {@code flags}. Regex ở đó được compile một lần khi dựng validator - đúng quy tắc MỘT.
 * Nhưng nó vẫn ném {@code PatternSyntaxException} lúc khởi động nếu viết sai.
 * - Spring Security: {@code RegexRequestMatcher} khớp URL bằng regex. Sai một dấu neo là
 * hở cả một nhánh API - nhớ bài t7 mục 7 về {@code $} và xuống dòng.
 * - Spring MVC: {@code @PathVariable} có cho đặt regex ngay trong mapping, dạng
 * {@code /user/{id:[0-9]+}}. Đây KHÔNG phải regex của {@code java.util.regex} hoàn toàn -
 * nó đi qua bộ so khớp đường dẫn riêng, nên đừng dùng cú pháp phức tạp ở đó.
 * - Phía DB thì regex là của DB, không phải của Java: Postgres có toán tử {@code ~} và
 * {@code SIMILAR TO}, Oracle có {@code REGEXP_LIKE}. Cú pháp theo chuẩn POSIX, KHÔNG có
 * possessive quantifier, và hành vi {@code \d} cũng khác. Đừng copy regex Java xuống SQL.
 * - Khuyến nghị cuối: lọc dữ liệu bằng SQL có index thì luôn tốt hơn kéo cả bảng lên rồi
 * lọc bằng regex trong Java. Regex trong Java hợp lý ở tầng validate đầu vào và xử lý văn
 * bản, không phải tầng truy vấn.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG CẢ SECTION
 * - {@code PatternSyntaxException} là checked hay unchecked? Hệ quả là gì?
 * - Bốn method của nó và method nào cho ra dòng dấu mũ chỉ vị trí lỗi?
 * - Exception này ném ở bước nào: compile hay match?
 * - Vì sao {@code static final Pattern} biến lỗi regex thành lỗi lúc khởi động?
 * - Trường hợp duy nhất nên try-catch exception này là gì?
 * - Kể lại năm quy tắc production ở mục 7 không nhìn tài liệu.
 * - Vì sao không copy thẳng regex Java xuống câu SQL của Postgres?
 * - Nhìn lại cả 10 bài: ba thứ bạn thấy dễ sai nhất là gì?
 */
public class t10_PatternSyntaxException {

    void main() {
        // TODO 1 (mục 2, 3): Chạy đoạn dưới và DÁN output thật vào mục 3 của phần note.
        //   Đối chiếu getIndex() với vị trí dấu mũ trong getMessage().
        //
        try {
            Pattern.compile("?i)foo");
        } catch (PatternSyntaxException e) {
            System.out.println("getDescription() : " + e.getDescription());
            System.out.println("getIndex()       : " + e.getIndex());
            System.out.println("getPattern()     : " + e.getPattern());
            System.out.println("getMessage()     :\n" + e.getMessage());
        }

        // TODO 2 (mục 2): Làm tương tự với ba regex hỏng khác: "[a-", "(abc", "a{2,1}".
        //   Lập bảng: regex, getDescription(), getIndex(). Đoán trước rồi mới chạy.
        //
        // TODO 3 (mục 4): Tạo một lớp có "private static final Pattern P = Pattern.compile(\"[a-\");"
        //   rồi truy cập nó. Dán lại toàn bộ stack trace và chỉ ra dòng "Caused by".
        //   Ghi lại tên lớp Error ở lần truy cập thứ hai.
        //
        // TODO 4 (mục 4): So sánh thời điểm nổ lỗi của hai cách: static final Pattern
        //   và "input".matches("[a-") nằm trong một method chỉ gọi khi có điều kiện.
        //   Viết ra kịch bản lỗi lọt lên production của cách thứ hai.
        //
        // TODO 5 (mục 5): Viết hàm nhận regex do người dùng nhập, trả về một kết quả có
        //   thông báo lỗi thân thiện khi regex sai (dùng getDescription và getIndex).
        //   Thêm cả giới hạn độ dài input để phòng ReDoS theo quy tắc BA ở mục 7.
        //
        // TODO 6 (tổng kết): Rà lại code cũ của chính mình trong repo này, tìm mọi chỗ gọi
        //   String.matches, String.split, String.replaceAll. Với mỗi chỗ, quyết định có cần
        //   đổi sang static final Pattern không và ghi lý do.
        //
        // TODO 7 (tổng kết, mục 7): Viết một lớp ValidationPatterns gom các regex hay dùng
        //   của một dự án thật: số điện thoại VN, mã số thuế, email, biển số xe.
        //   Tất cả là static final, có javadoc nêu rõ ca hợp lệ và ca không hợp lệ.
        //
        // TODO 8 (tổng kết, mục 8): Viết regex validate họ tên tiếng Việt cho field hoTen.
        //   Phải đúng với "Lê Văn Thiện", "Nguyễn Thị Hồng Nhung", và từ chối "admin<script>".
        //   Dùng lại kiến thức bài t3 mục 9, t4 mục 4, t7 mục 7.
    }
}
