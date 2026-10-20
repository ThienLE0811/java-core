package com.javacore.api.date_time_regex.regex;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * The Pattern Class
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 8/10 trong section Regular Expressions.
 * - Hết khối cú pháp, sang khối API. Bài này là về cái OBJECT giữ regex đã biên dịch:
 * tạo nó thế nào, bật cờ gì, và nó cho sẵn những tiện ích nào.
 * - Một câu cho cả bài: {@code Pattern.compile()} TỐN KÉM nên phải làm một lần duy nhất,
 * và các cờ compile quyết định nghĩa của nửa số ký tự bạn đã học ở bảy bài trước.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/patterns/">dev.java - The Pattern Class</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THE PATTERN CLASS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. PATTERN.MATCHES - LỐI TẮT
 * - "The Pattern class defines a convenient matches method that allows you to quickly check
 * if a pattern is present in a given input string."
 * - Dùng {@code Pattern.matches("\\d", "1")} trả về {@code true}.
 * - Tên method gây hiểu nhầm: nó đòi khớp TOÀN BỘ chuỗi chứ không phải "có chứa". Xem bài
 * t9 mục 2.
 * - ĐÁNH ĐỔI: tiện nhưng biên dịch lại regex MỖI LẦN GỌI. Chỉ dùng cho code chạy một lần
 * hoặc trong test. Xem mục 7.
 * <p>
 * 2. SPLIT
 * - Tách chuỗi tại những chỗ khớp pattern.
 * - Ví dụ trang gốc: tách "one:two:three:four:five" theo dấu hai chấm ra mảng
 * {@code [one, two, three, four, five]}.
 * - Dùng được với regex phức tạp: tách cùng chuỗi đó theo {@code \d} cho kết quả y hệt khi
 * dấu phân cách là chữ số. Điểm cần nhớ là dấu phân cách KHÔNG nhất thiết phải cố định.
 * <p>
 * 3. CÁC CỜ COMPILE - ĐỌC KỸ, ĐÂY LÀ PHẦN HAY DÙNG NHẤT
 * - {@code CASE_INSENSITIVE} - bỏ qua hoa thường. Mặc định chỉ đúng với ASCII, phải ghép
 * thêm {@code UNICODE_CASE} mới đúng với tiếng có dấu.
 * - {@code UNICODE_CASE} - bật so khớp hoa thường theo chuẩn Unicode.
 * - {@code MULTILINE} - "In multiline mode the expressions ^ and $ match just after or just
 * before a line terminator." Không bật thì {@code ^ $} là đầu/cuối toàn bộ input (bài t7).
 * - {@code DOTALL} - "In dotall mode, the expression . matches any character, including a
 * line terminator." Không bật thì dấu chấm dừng ở cuối dòng (bài t4 mục 5).
 * - {@code UNIX_LINES} - "Only the \n line terminator is recognized in the behavior of
 * ., ^, and $." Bỏ qua {@code \r\n} kiểu Windows.
 * - {@code COMMENTS} - "Permits whitespace and comments in the pattern. whitespace is
 * ignored, and embedded comments starting with # are ignored." Dùng để viết regex dài có
 * chú thích.
 * - {@code LITERAL} - coi toàn bộ chuỗi pattern là nghĩa đen, mọi metacharacter mất phép.
 * - {@code CANON_EQ} - "Enables canonical equivalence. two characters will be considered to
 * match if their full canonical decompositions match." Đây là cờ xử lý chuyện NFC/NFD đã
 * nói ở bài t3 mục 9.
 * - Ghép nhiều cờ bằng toán tử {@code |} (bitwise OR).
 * <p>
 * 4. EMBEDDED FLAG EXPRESSIONS
 * - Bật cờ ngay trong chuỗi regex, không cần tham số:
 * <pre>
 *   CASE_INSENSITIVE   (?i)
 *   COMMENTS           (?x)
 *   MULTILINE          (?m)
 *   DOTALL             (?s)
 *   UNICODE_CASE       (?u)
 *   UNIX_LINES         (?d)
 * </pre>
 * - Đặt ở đầu regex là áp dụng cho cả biểu thức.
 * - Khi nào dùng dạng nhúng: khi chuỗi regex đến từ file cấu hình hay DB, nơi không truyền
 * được tham số cờ. Code bình thường thì dùng tham số cho rõ.
 * <p>
 * 5. VÀI METHOD KHÁC TRANG GỐC NÊU
 * - {@code quote(String s)} - "Returns a literal pattern String for the specified String.
 * Metacharacters or escape sequences in the input sequence will be given no special
 * meaning." Đã gặp ở bài t2 mục 4.
 * - {@code toString()} - "Returns the String representation of this pattern. This is the
 * regular expression from which this pattern was compiled."
 * <p>
 * 6. CÁC METHOD TƯƠNG ĐƯƠNG BÊN LỚP STRING
 * - {@code String.matches(String regex)}
 * - {@code String.split(String regex)} và bản có {@code limit}
 * - {@code String.replace(CharSequence, CharSequence)} - lưu ý cái này KHÔNG phải regex,
 * nó thay nghĩa đen. Cái dùng regex là {@code replaceAll}/{@code replaceFirst} (bài t9).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. COMPILE MỘT LẦN - QUY TẮC PRODUCTION SỐ MỘT CỦA CẢ SECTION
 * - {@code Pattern.compile()} phải phân tích cú pháp regex và dựng một cây máy trạng thái.
 * Việc này tốn hơn hẳn một lần so khớp.
 * - {@code Pattern.matches()}, {@code String.matches()}, {@code String.split()},
 * {@code String.replaceAll()} đều GỌI {@code compile()} BÊN TRONG, mỗi lần gọi một lần.
 * Đặt chúng trong vòng lặp hay trong method của một service xử lý mỗi request là lãng phí
 * âm thầm, không bao giờ báo lỗi, chỉ thấy khi đo.
 * - Cách đúng trong Spring:
 * {@code private static final Pattern PHONE = Pattern.compile("...");}
 * rồi trong method gọi {@code PHONE.matcher(input)}. {@code Pattern} thread-safe nên
 * {@code static final} hoàn toàn an toàn (bài t1 mục 6).
 * - Ngoại lệ hợp lý: {@code String.split(" ")} với chuỗi phân tách một ký tự đơn giản được
 * JDK tối ưu riêng, không thực sự compile. Nhưng đừng dựa vào chi tiết đó, cứ theo quy tắc.
 * <p>
 * 8. BẪY STRING.SPLIT - CẮT MẤT PHẦN TỬ RỖNG Ở CUỐI
 * - {@code "a,b,,".split(",")} trả về mảng 2 phần tử, KHÔNG phải 4. Mặc định {@code limit}
 * bằng 0 nghĩa là bỏ hết phần tử rỗng ở đuôi.
 * - Muốn giữ đủ thì truyền {@code limit} âm: {@code "a,b,,".split(",", -1)} ra 4 phần tử.
 * - Đây là bug kinh điển khi parse CSV hay file cấu hình: dòng cuối thiếu cột thì phần tử
 * biến mất, code lấy theo chỉ số ném {@code ArrayIndexOutOfBoundsException} ở dòng thứ 9000.
 * - Phần tử rỗng ở ĐẦU thì không bị bỏ. Quy tắc chỉ áp dụng cho đuôi.
 * <p>
 * 9. API STREAM - JAVA 8 VÀ 11, TRANG GỐC KHÔNG NHẮC
 * - {@code pattern.splitAsStream(input)} - tách ra {@code Stream<String>} thay vì mảng.
 * Nối thẳng được vào {@code map}/{@code filter} (nối lại với section Stream đã học).
 * - {@code pattern.asPredicate()} (Java 8) - trả về {@code Predicate<String>} dùng
 * {@code find()} bên trong, tức là "CÓ CHỨA".
 * - {@code pattern.asMatchPredicate()} (Java 11) - cũng là {@code Predicate} nhưng dùng
 * {@code matches()}, tức là "KHỚP TOÀN BỘ".
 * - Hai cái này khác nhau đúng ở chỗ đã nói ở bài t1 mục 7. Dùng nhầm trong
 * {@code stream.filter(...)} là lỗi im lặng. Khi lọc danh sách, hầu như lúc nào ý định
 * cũng là {@code asMatchPredicate()}.
 * <p>
 * 10. COMMENTS VÀ TEXT BLOCK - CÁCH VIẾT REGEX DÀI MÀ ĐỌC ĐƯỢC
 * - Regex quá 40 ký tự là không ai đọc nổi. Bật {@code COMMENTS} rồi viết nhiều dòng trong
 * một text block ba nháy kép (JDK 15+), mỗi dòng một thành phần kèm chú thích sau dấu #.
 * - Nhớ: khi bật {@code COMMENTS} thì dấu cách trong regex BỊ BỎ QUA. Muốn khớp dấu cách
 * thật phải viết {@code \s} hoặc escape nó.
 * - Text block KHÔNG tắt escape của String, nên backslash vẫn phải nhân đôi (bài t1 mục 5).
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - Bảng cờ đối chiếu: {@code i} ↔ {@code CASE_INSENSITIVE}, {@code m} ↔ {@code MULTILINE},
 * {@code s} ↔ {@code DOTALL}, {@code u} ↔ gần với {@code UNICODE_CASE} cộng
 * {@code UNICODE_CHARACTER_CLASS}. Cờ {@code g} và {@code y} của JS không có tương đương
 * trực tiếp - Java thay bằng vòng {@code find()} và bằng {@code \G}.
 * - JS không có {@code COMMENTS}, nên regex dài bên TS chỉ còn cách nối chuỗi thủ công.
 * - {@code split} khác nhau ở đúng bẫy mục 8: JS GIỮ phần tử rỗng ở cuối, Java BỎ. Cùng một
 * chuỗi CSV, frontend đếm ra 4 cột mà backend đếm 2 - đây là bug tích hợp rất khó nhìn ra.
 * - {@code String.replace} bên JS (không dùng regex) chỉ thay LẦN ĐẦU; bên Java thay HẾT
 * nhưng theo nghĩa đen. Hai method trùng tên, hai hành vi khác nhau ở cả hai chiều.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code Pattern.matches()} hỏi "có chứa" hay "khớp toàn bộ"?
 * - Vì sao {@code String.matches()} trong vòng lặp là có vấn đề? Viết lại cho đúng.
 * - Kể 6 cờ compile và nói mỗi cờ đổi nghĩa của ký tự nào đã học ở các bài trước.
 * - Ghép hai cờ với nhau bằng toán tử gì?
 * - Viết dạng nhúng của MULTILINE và DOTALL.
 * - {@code "a,b,,".split(",")} trả mảng mấy phần tử? Muốn đủ 4 thì làm sao?
 * - {@code asPredicate()} và {@code asMatchPredicate()} khác nhau chỗ nào? Lọc danh sách
 * thì dùng cái nào?
 * - Bật {@code COMMENTS} thì dấu cách trong regex có còn khớp dấu cách không?
 * - {@code String.replace} có dùng regex không?
 * - Hành vi {@code split} của Java và JS khác nhau ở điểm nào?
 */
public class t8_PatternClass {

    void main() {
        // TODO 1 (mục 2, 8): Chạy đoạn dưới. Giải thích vì sao hai dòng cuối ra số khác nhau.
        //
        System.out.println(Arrays.toString("one:two:three".split(":")));
        System.out.println("limit mac dinh : " + "a,b,,".split(",").length);
        System.out.println("limit -1       : " + "a,b,,".split(",", -1).length);

        // TODO 2 (mục 1, 7): Đo thời gian 1 triệu lần gọi "abc123".matches("\\w+") so với
        //   1 triệu lần PATTERN.matcher("abc123").matches() dùng một static final Pattern.
        //   Ghi lại tỉ lệ chênh lệch.
        //
        // TODO 3 (mục 3): Với input "Dong1\nDONG2", chạy regex "^dong\\d" lần lượt với:
        //   không cờ, CASE_INSENSITIVE, MULTILINE, và cả hai cờ ghép bằng |.
        //   Lập bảng 4 dòng kết quả find().
        //
        // TODO 4 (mục 3, 4): Làm lại TODO 3 bằng embedded flag "(?im)^dong\\d".
        //   Xác nhận kết quả giống hệt bản truyền tham số.
        //
        // TODO 5 (mục 3): Thử CASE_INSENSITIVE một mình trên cặp "Ế" và "ế".
        //   Rồi ghép thêm UNICODE_CASE và chạy lại. Ghi lại khác biệt.
        //
        // TODO 6 (mục 3): Dùng CANON_EQ để khớp hai biến thể NFC và NFD của "Thiện"
        //   (tạo bằng java.text.Normalizer như ở bài t3 TODO 8). Xác nhận cờ này xử lý được.
        //
        // TODO 7 (mục 3): So sánh Pattern.compile("a.c", Pattern.LITERAL) với
        //   Pattern.compile(Pattern.quote("a.c")). Chạy cả hai trên "abc" và "a.c".
        //   Kết luận: hai cách này tương đương không?
        //
        // TODO 8 (mục 9): Viết hai bộ lọc danh sách email bằng asPredicate() và
        //   asMatchPredicate() với cùng regex "\\w+@\\w+\\.com".
        //   Truyền vào danh sách có "xin chao a@b.com nhe". Ghi lại cái nào cho lọt.
        //
        // TODO 9 (mục 9): Dùng splitAsStream tách "1,2,3,4,5" rồi map sang int và tính tổng.
        //   So sánh độ dài code với cách dùng split() ra mảng.
        //
        // TODO 10 (mục 10): Viết regex validate số điện thoại VN dài, dùng text block
        //   và cờ COMMENTS, mỗi dòng một phần kèm chú thích #.
        //   Nhớ xử lý dấu cách bị bỏ qua.
    }
}
