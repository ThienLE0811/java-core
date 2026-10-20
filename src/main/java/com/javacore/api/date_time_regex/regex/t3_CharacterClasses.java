package com.javacore.api.date_time_regex.regex;

import java.util.regex.Pattern;

/**
 * Character Classes
 * <p>
 * 0. Vị trí bài này
 * - Bước 25/41, bài 3/10 trong section Regular Expressions.
 * - Bài đầu tiên dạy cú pháp thật. Character class là viên gạch dùng nhiều nhất trong mọi
 * regex thực tế.
 * - Một câu cho cả bài: ngoặc vuông mô tả MỘT ký tự được phép là những gì - luôn luôn chỉ
 * một ký tự, bất kể bên trong liệt kê bao nhiêu.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/date-time-regex/regex/character-classes/">dev.java - Character Classes</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CHARACTER CLASSES (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA
 * - "In the context of regular expressions, a character class is a set of characters enclosed
 * within square brackets."
 * - "It specifies the characters that will successfully match a single character from a given
 * input string."
 * - CHỮ "SINGLE" LÀ TRỌNG TÂM: {@code [abc]} khớp đúng MỘT ký tự, không phải chuỗi "abc".
 * Người mới hay nhầm chỗ này. Muốn nhiều ký tự phải thêm quantifier (bài t5).
 * - Lưu ý từ vựng: trang gốc nói rõ "character class" ở đây KHÔNG phải "class" theo nghĩa
 * lớp trong Java - trùng tên thôi.
 * <p>
 * 2. BẢNG CẤU TRÚC - HỌC THUỘC BẢNG NÀY
 * <pre>
 *   [abc]          a, b, or c (simple class)
 *   [^abc]         Any character except a, b, or c (negation)
 *   [a-zA-Z]       a through z, or A through Z, inclusive (range)
 *   [a-d[m-p]]     a through d, or m through p: [a-dm-p] (union)
 *   [a-z&amp;&amp;[def]]   d, e, or f (intersection)
 *   [a-z&amp;&amp;[^bc]]   a through z, except for b and c: [ad-z] (subtraction)
 *   [a-z&amp;&amp;[^m-p]]  a through z, and not m through p: [a-lq-z] (subtraction)
 * </pre>
 * <p>
 * 3. SIMPLE CLASSES
 * - Liệt kê thẳng các ký tự trong ngoặc vuông.
 * - Ví dụ trang gốc: {@code [bcr]at} khớp "bat", "cat", hoặc "rat".
 * <p>
 * 4. NEGATION
 * - Đặt dấu {@code ^} ngay SAU dấu mở ngoặc vuông để đảo ngược tập hợp.
 * - "The match is successful only if the first character of the input string does not contain
 * any of the characters defined by the character class."
 * - Dấu {@code ^} chỉ mang nghĩa phủ định ở vị trí đầu tiên. {@code [a^b]} là ba ký tự
 * a, dấu mũ, b - hoàn toàn bình thường.
 * <p>
 * 5. RANGES
 * - Dùng dấu {@code -} giữa hai ký tự đầu/cuối: {@code [1-5]} hoặc {@code [a-h]}.
 * - Ghép nhiều dải trong một ngoặc: {@code [a-zA-Z]} "will match any letter of the alphabet".
 * - Dải tính theo THỨ TỰ CODE POINT, không phải theo bảng chữ cái tiếng Việt. Nên
 * {@code [a-z]} KHÔNG chứa ă, â, đ, ê, ô, ơ, ư. Xem mục 9.
 * <p>
 * 6. UNIONS
 * - Lồng một class vào trong một class khác để hợp hai tập.
 * - Ví dụ trang gốc: {@code [0-4[6-8]]} "creates a single character class that matches the
 * numbers 0, 1, 2, 3, 4, 6, 7, and 8." Chú ý số 5 bị loại vì không thuộc dải nào.
 * <p>
 * 7. INTERSECTIONS VÀ SUBTRACTION
 * - Giao dùng toán tử {@code &&}: {@code [0-5&&[3-9]]} "creates a single character class
 * matching only the numbers common to both character classes: 3, 4, and 5."
 * - Trừ là giao với một tập phủ định: {@code [0-9&&[^345]]} "creates a single character
 * class that matches everything from 0 to 9, except the numbers 3, 4, and 5."
 * - Nói cách khác: Java KHÔNG có toán tử trừ riêng. "Trừ" chỉ là {@code &&} cộng {@code ^}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. BÊN TRONG NGOẶC VUÔNG LÀ MỘT THẾ GIỚI KHÁC
 * - Đa số metacharacter MẤT phép thuật khi vào trong ngoặc vuông. {@code [.]} khớp đúng dấu
 * chấm, {@code [*+?]} khớp ba ký tự đó theo nghĩa đen. Không cần escape.
 * - Chỉ còn bốn ký tự giữ nghĩa đặc biệt bên trong: {@code ^} (khi ở đầu), {@code -} (khi ở
 * giữa hai ký tự), {@code ]} (đóng class), và {@code \} (escape).
 * - Muốn khớp chính dấu {@code -} thì đặt nó ở ĐẦU hoặc CUỐI: {@code [-a-z]} hoặc
 * {@code [a-z-]}. Đặt giữa mà không escape là tạo ra một dải ngoài ý muốn.
 * - Muốn khớp chính dấu {@code ]} thì escape: {@code [\]]}.
 * <p>
 * 9. DẢI A-Z VÀ TIẾNG VIỆT - BẪY THỰC TẾ SỐ MỘT
 * - {@code [a-zA-Z]} không khớp ký tự có dấu. Regex validate họ tên kiểu
 * {@code "^[a-zA-Z ]+$"} sẽ loại sạch tên tiếng Việt - lỗi này gặp ở rất nhiều form.
 * - Hai cách sửa, theo thứ tự ưu tiên:
 * + Dùng thuộc tính Unicode: {@code \p{L}} cho mọi chữ cái mọi ngôn ngữ (xem bài t1 mục 3).
 * + Hoặc {@code [\p{L}\p{M}]} nếu dữ liệu có thể ở dạng tổ hợp NFD (chữ cái + dấu rời).
 * - Chuỗi tiếng Việt có hai dạng Unicode khác nhau: NFC (một code point cho "ế") và
 * NFD ("e" + hai dấu rời). Trông giống nhau trên màn hình nhưng regex xử lý khác nhau.
 * Chuẩn hóa bằng {@code java.text.Normalizer} TRƯỚC khi so khớp, hoặc dùng cờ
 * {@code CANON_EQ} (bài t8).
 * - Với dữ liệu từ DB: Postgres và Oracle cũng có khái niệm collation riêng. Đừng giả định
 * chuỗi lấy từ DB đã ở dạng NFC.
 * <p>
 * 10. UNION LỒNG NHAU DỄ ĐỌC NHẦM
 * - {@code [a-d[m-p]]} là HỢP, nhưng nhìn rất giống cú pháp lồng của ngôn ngữ khác.
 * Trong POSIX hay JS thì {@code [} bên trong class chỉ là ký tự thường, không lồng được.
 * - Khuyến nghị thực tế: ghép dải thẳng ({@code [a-dm-p]}) thay vì lồng. Ngắn hơn, không ai
 * đọc nhầm, và chuyển sang ngôn ngữ khác vẫn chạy. Chỉ dùng lồng khi cần {@code &&}.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - {@code [abc]}, {@code [^abc]}, {@code [a-z]} giống hệt nhau hai bên.
 * - Union lồng {@code [a-d[m-p]]} và giao {@code &&} là ĐẶC SẢN Java, JS không có. Copy
 * regex Java có {@code &&} sang JS sẽ không báo lỗi mà chạy SAI - nó hiểu {@code &} là ký
 * tự thường. Đây là bẫy khi chia sẻ một chuỗi regex giữa backend Java và frontend TS.
 * - Nếu cần một regex dùng chung cho cả hai phía, giữ ở tập con chung: chỉ dùng liệt kê,
 * phủ định, dải, và các predefined class của bài t4.
 * - {@code \p{L}} bên JS cần bật cờ {@code u} mới hiểu; Java hiểu sẵn không cần cờ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code [abc]} khớp được bao nhiêu ký tự của input? Vì sao không phải ba?
 * - {@code [a^b]} và {@code [^ab]} khác nhau thế nào?
 * - Viết class khớp chữ số trừ 3, 4, 5 bằng cú pháp Java.
 * - Java có toán tử trừ tập riêng không? Nếu không thì trừ bằng cách nào?
 * - Bên trong ngoặc vuông, bốn ký tự nào còn giữ nghĩa đặc biệt?
 * - Muốn khớp chính dấu gạch ngang thì đặt nó ở đâu trong class?
 * - {@code "^[a-zA-Z ]+$"} hỏng chỗ nào với tên "Lê Văn Thiện"? Sửa ra sao?
 * - NFC và NFD khác nhau thế nào, xử lý trước khi so khớp bằng gì?
 * - Toán tử {@code &&} chạy thế nào khi copy regex sang JavaScript?
 */
public class t3_CharacterClasses {

    void main() {
        // TODO 1 (mục 1, 3): Chạy đoạn dưới. Giải thích vì sao "bcrat" KHÔNG khớp [bcr]at
        //   dù chứa đủ mọi ký tự cần thiết.
        //
        String[] inputs = {"bat", "cat", "rat", "hat", "bcrat"};

        for (String input : inputs) {
            System.out.println(input + " -> " + Pattern.matches("[bcr]at", input));
        }

        // TODO 2 (mục 4): So sánh "[^bcr]at" trên cùng mảng input trên. Lập bảng hai cột
        //   kết quả của [bcr]at và [^bcr]at cạnh nhau.
        //
        // TODO 3 (mục 6): Kiểm chứng [0-4[6-8]] bằng cách chạy nó trên từng chữ số 0..9.
        //   In ra đúng danh sách chữ số khớp và đối chiếu với trang gốc.
        //
        // TODO 4 (mục 7): Làm tương tự với [0-5&&[3-9]] và [0-9&&[^345]].
        //   Viết một hàm dùng lại được: nhận regex, in ra các chữ số 0..9 nào khớp.
        //
        // TODO 5 (mục 8): Chạy Pattern.matches("[.]", ".") và Pattern.matches("[.]", "a").
        //   Kết luận: dấu chấm trong ngoặc vuông có cần escape không?
        //
        // TODO 6 (mục 8): So sánh ba regex "[a-z-]", "[-a-z]" và "[a-z]" trên input "-".
        //   Rồi thử "[a-z-0]" và giải thích kết quả.
        //
        // TODO 7 (mục 9): Chạy "^[a-zA-Z ]+$" trên "Le Van Thien" và "Lê Văn Thiện".
        //   Sửa lại bằng \\p{L} và xác nhận cả hai đều khớp.
        //
        // TODO 8 (mục 9): Dùng java.text.Normalizer tạo hai biến thể NFC và NFD của chuỗi
        //   "Thiện". In length của mỗi biến thể và chạy regex "^\\p{L}+$" trên cả hai.
        //   Ghi lại cái nào trượt và vì sao.
        //
        // TODO 9 (mục 10): Viết cùng một tập ký tự bằng hai cách: union lồng và ghép dải
        //   thẳng. Xác nhận hai regex cho kết quả giống nhau trên toàn bộ bảng chữ cái.
    }
}
