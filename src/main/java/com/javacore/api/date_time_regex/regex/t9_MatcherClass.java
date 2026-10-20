package com.javacore.api.date_time_regex.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Matcher Class
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 9/10 trong section Regular Expressions.
 * - {@code Pattern} là cái khuôn, {@code Matcher} là cái máy chạy khuôn đó trên một chuỗi
 * cụ thể. Mọi kết quả bạn thực sự cần đều lấy từ đây.
 * - Một câu cho cả bài: {@code Matcher} có TRẠNG THÁI - nó nhớ mình đang đứng ở đâu và
 * match gần nhất là gì, nên thứ tự gọi method có ý nghĩa.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/matchers/">dev.java - The Matcher Class</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THE MATCHER CLASS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. INDEX METHODS - MATCH NẰM Ở ĐÂU
 * - {@code start()} - "Returns the start index of the previous match."
 * - {@code start(int group)} - "Returns the start index of the subsequence captured by the
 * given group during the previous match operation."
 * - {@code end()} - "Returns the offset after the last character matched."
 * - {@code end(int group)} - "Returns the offset after the last character of the subsequence
 * captured by the given group during the previous match operation."
 * - Chú ý cụm "the previous match" lặp lại ở cả bốn mô tả: các method này chỉ có nghĩa SAU
 * KHI một lần tìm đã thành công. Gọi trước đó thì ném {@code IllegalStateException}.
 * <p>
 * 2. STUDY METHODS - CÓ KHỚP KHÔNG
 * - {@code matches()} - "Attempts to match the entire region against the pattern."
 * - {@code lookingAt()} - "Attempts to match the input sequence, starting at the beginning
 * of the region, against the pattern."
 * - {@code find()} - "Attempts to find the next subsequence of the input sequence that
 * matches the pattern."
 * - {@code find(int start)} - "Resets this matcher and then attempts to find the next
 * subsequence starting at the specified index."
 * - PHÂN BIỆT QUAN TRỌNG NHẤT CỦA BÀI, trích nguyên văn: "matches() requires the entire
 * input sequence to be matched, while lookingAt() does not. Both methods always start at
 * the beginning of the input string."
 * - Bảng ba dòng nên thuộc, với regex {@code foo} và input "foobar":
 * + {@code matches()} → false (còn thừa "bar")
 * + {@code lookingAt()} → true (bắt đầu đúng là đủ)
 * + {@code find()} → true (ở đâu cũng được)
 * <p>
 * 3. REPLACEMENT METHODS
 * - {@code replaceAll(String replacement)} - "Replaces every subsequence of the input
 * sequence that matches the pattern with the given replacement string."
 * - {@code replaceFirst(String replacement)} - "Replaces the first subsequence of the input
 * sequence that matches the pattern with the given replacement string."
 * - {@code appendReplacement(StringBuffer sb, String replacement)} - bước thay thế KHÔNG
 * kết thúc, dùng trong vòng lặp.
 * - {@code appendTail(StringBuilder sb)} - bước kết thúc, nối phần đuôi còn lại.
 * - {@code quoteReplacement(String s)} - "Returns a literal replacement String for the
 * specified String. Slashes and dollar signs will be given no special meaning."
 * - Ví dụ trang gốc: pattern {@code a*b} trên "aabfooaabfooabfoob", thay bằng "-", ra
 * "-foo-foo-foo-".
 * - Cặp {@code appendReplacement}/{@code appendTail} cho kết quả giống {@code replaceAll}
 * nhưng bạn kiểm soát được từng lần thay - dùng khi chuỗi thay thế phụ thuộc nội dung đã
 * khớp.
 * <p>
 * 4. CÁC METHOD TƯƠNG ĐƯƠNG BÊN LỚP STRING
 * - {@code String.replaceFirst(String regex, String replacement)} - "Replaces the first
 * substring of this string that matches the given regular expression with the given
 * replacement."
 * - {@code String.replaceAll(String regex, String replacement)} - "Replaces each substring
 * of this string that matches the given regular expression with the given replacement."
 * - Trang gốc nói hai method này cho kết quả y hệt bản
 * {@code Pattern.compile().matcher().replaceAll()}. Khác biệt duy nhất là hiệu năng
 * (bài t8 mục 7).
 * <p>
 * 5. VÍ DỤ TRANG GỐC DÙNG LẠI
 * - {@code \bdog\b} để xác nhận "dog" là từ trọn vẹn, lấy vị trí bằng {@code start()},
 * và {@code end()} trả về vị trí ngay sau ký tự cuối (nối lại với bài t7).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. DẤU $ TRONG CHUỖI THAY THẾ - BẪY NÉM EXCEPTION LÚC CHẠY
 * - Trong chuỗi replacement, {@code $1} nghĩa là "nội dung group 1", và backslash là escape.
 * Nên chuỗi thay thế KHÔNG phải văn bản thuần.
 * - Hệ quả: thay một đoạn bằng giá trị người dùng nhập mà giá trị đó chứa {@code $} sẽ ném
 * {@code IllegalArgumentException} hoặc chèn nhầm nội dung group. Giá tiền "100$" là đủ.
 * - Cách đúng: bọc bằng {@code Matcher.quoteReplacement(s)}.
 * - Nhớ cặp đôi: {@code Pattern.quote} cho phía PATTERN (bài t2), {@code
 * Matcher.quoteReplacement} cho phía REPLACEMENT. Hai hàm khác nhau, hai lớp khác nhau.
 * - Với named group (bài t6 mục 5), trong replacement viết {@code ${ten}}.
 * <p>
 * 7. MATCHER CÓ TRẠNG THÁI - BA HỆ QUẢ THỰC TẾ
 * - {@code find()} tiếp tục từ chỗ dừng lần trước. Gọi {@code find()} hai lần trên cùng một
 * Matcher là tìm hai match KHÁC NHAU.
 * - Muốn chạy lại từ đầu thì gọi {@code reset()}, hoặc tạo Matcher mới.
 * - Không dùng lại một Matcher cho nhiều luồng hay nhiều input - tạo mới rất rẻ, chỉ có
 * {@code compile()} mới đắt.
 * - Lỗi hay gặp trong code thật: viết {@code if (m.find()) { ... m.group() ... }} rồi sau
 * đó lại viết {@code while (m.find())} ở dưới, tưởng duyệt từ đầu nhưng thực ra đã bỏ qua
 * match đầu tiên.
 * <p>
 * 8. API JAVA 9+ - NGẮN HƠN VÀ AN TOÀN HƠN
 * - {@code matcher.results()} trả về {@code Stream<MatchResult>}. Thay cho vòng
 * {@code while (find())} khi chỉ cần gom kết quả.
 * {@code m.results().map(MatchResult::group).toList()}
 * - {@code matcher.replaceAll(Function<MatchResult, String>)} - chuỗi thay thế tính bằng
 * hàm thay vì bằng chuỗi có {@code $1}. Đây là bản thay thế hiện đại cho cặp
 * {@code appendReplacement}/{@code appendTail}, ngắn hơn nhiều và không dính bẫy mục 6.
 * - {@code appendReplacement} từ Java 9 có thêm bản nhận {@code StringBuilder} thay vì
 * {@code StringBuffer} (bản cũ dùng StringBuffer vì lý do lịch sử, có đồng bộ hóa thừa).
 * <p>
 * 9. REGION - GIỚI HẠN VÙNG TÌM KIẾM
 * - {@code matcher.region(start, end)} thu hẹp vùng mà Matcher được phép nhìn. Mọi method
 * sau đó chỉ làm việc trong vùng đó.
 * - Lợi ích so với {@code input.substring(...)}: không copy chuỗi, và các boundary như
 * {@code ^} vẫn tính theo mép vùng.
 * - Dùng khi phải quét một file lớn theo từng khối mà không muốn cấp phát chuỗi con.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - {@code m.results()} ≈ {@code str.matchAll(regex)} của JS. Cả hai trả về luồng/iterator
 * các match thay vì phải lặp tay.
 * - {@code m.replaceAll(Function)} ≈ truyền hàm vào {@code str.replace(regex, fn)} bên JS.
 * Ý tưởng y hệt, Java có muộn hơn (Java 9 so với ES3).
 * - {@code $1} trong replacement giống nhau hai bên. Nhưng JS không có
 * {@code quoteReplacement}; bên JS phải tự thay {@code $} thành {@code $$}.
 * - Trạng thái: Matcher của Java tương ứng với thuộc tính {@code lastIndex} của RegExp có
 * cờ {@code g} bên JS. Khác biệt then chốt: {@code lastIndex} nằm trên chính RegExp object
 * nên tái sử dụng một regex literal có cờ g giữa nhiều chỗ là bug kinh điển của JS. Java
 * tách Matcher ra khỏi Pattern nên không có vấn đề đó.
 * - {@code matches()} của Java ≈ gói regex trong {@code ^...$} bên JS rồi gọi
 * {@code test()}. JS không có method riêng cho "khớp toàn bộ".
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Với regex "foo" và input "foobar": {@code matches()}, {@code lookingAt()},
 * {@code find()} trả về gì? Giải thích từng cái.
 * - Gọi {@code start()} trước khi tìm thành công thì sao?
 * - Gọi {@code find()} hai lần liên tiếp cho kết quả gì? Muốn về đầu thì làm sao?
 * - Trong chuỗi replacement, {@code $1} nghĩa là gì? Còn named group thì viết thế nào?
 * - Chuỗi thay thế chứa ký tự {@code $} do người dùng nhập gây chuyện gì? Phòng bằng hàm nào?
 * - Phân biệt {@code Pattern.quote} và {@code Matcher.quoteReplacement}.
 * - {@code replaceAll} nhận {@code Function} có lợi gì so với bản nhận String?
 * - Khi nào vẫn cần {@code appendReplacement}/{@code appendTail}?
 * - {@code region()} khác gì so với cắt chuỗi bằng {@code substring()}?
 * - {@code Stream<MatchResult>} lấy ra từ method nào?
 */
public class t9_MatcherClass {

    void main() {
        // TODO 1 (mục 2): Chạy đoạn dưới. Giải thích từng ô của bảng ba cột.
        //   Rồi đổi input thành "barfoo" và chạy lại, ghi lại ô nào đổi.
        //
        Pattern p = Pattern.compile("foo");
        String input = "foobar";

        System.out.println("matches()   : " + p.matcher(input).matches());
        System.out.println("lookingAt() : " + p.matcher(input).lookingAt());
        System.out.println("find()      : " + p.matcher(input).find());

        // TODO 2 (mục 1): Với regex "\\bdog\\b" và input "The dog plays", in start(), end()
        //   và input.substring(start, end). Xác nhận substring ra đúng "dog".
        //
        // TODO 3 (mục 1): Gọi m.start() NGAY SAU khi tạo Matcher, chưa find().
        //   Dán lại exception và tên lớp của nó.
        //
        // TODO 4 (mục 3): Kiểm chứng ví dụ trang gốc: pattern "a*b" trên
        //   "aabfooaabfooabfoob" thay bằng "-". Xác nhận ra "-foo-foo-foo-".
        //
        // TODO 5 (mục 7): Trên cùng MỘT Matcher, gọi if (m.find()) rồi tiếp while (m.find()).
        //   Đếm tổng số match in ra và so với số match thật. Sau đó thêm m.reset() và chạy lại.
        //
        // TODO 6 (mục 6): Dùng replaceAll thay "GIA" bằng một chuỗi người dùng nhập là "100$".
        //   Dán lại exception. Rồi sửa bằng Matcher.quoteReplacement và xác nhận chạy đúng.
        //
        // TODO 7 (mục 3, 6): Dùng group reference trong replacement để đổi "Thien Le"
        //   thành "Le, Thien". Làm hai bản: dùng $1 $2 và dùng named group ${ho} ${ten}.
        //
        // TODO 8 (mục 3, 8): Viết hàm che số thẻ "1234-5678-9012-3456" thành
        //   "****-****-****-3456". Làm hai bản: appendReplacement/appendTail và
        //   replaceAll(Function). So sánh số dòng code.
        //
        // TODO 9 (mục 8): Dùng m.results() để lấy danh sách tất cả số trong chuỗi
        //   "don 12, don 345, don 6". Trả về List<Integer>. So sánh với vòng while(find()).
        //
        // TODO 10 (mục 9): Với một chuỗi dài, dùng region() để chỉ tìm trong 50 ký tự đầu.
        //   So sánh kết quả với cách dùng substring(0, 50). Chú ý hành vi của "^".
    }
}
