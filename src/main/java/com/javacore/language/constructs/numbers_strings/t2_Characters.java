package com.javacore.language.constructs.numbers_strings;

/**
 * Characters
 * <p>
 * 1. Lớp Character - lớp bao của char
 * - Một object Character chứa ĐÚNG MỘT field kiểu char.
 * - Immutable: tạo xong không đổi được giá trị bên trong.
 * - Dùng khi cần char ở chỗ đòi Object, điển hình là collection.
 * - Giá trị thực dụng của lớp này không nằm ở việc bọc giá trị, mà ở hơn 50
 * static method tiện ích nó cung cấp.
 * <p>
 * 2. Autoboxing và Unboxing cho char
 * - Cơ chế y hệt bên Numbers: truyền char vào chỗ cần Character thì compiler
 * tự bọc, truyền Character vào chỗ cần char thì compiler tự mở.
 * <p>
 * 3. Nhóm method kiểm tra - static, trả về boolean
 * - isLetter kiểm tra chữ cái, isDigit kiểm tra chữ số, isWhitespace kiểm tra
 * khoảng trắng, isUpperCase và isLowerCase kiểm tra hoa thường.
 * - Toàn bộ nhóm này hiểu UNICODE chứ không chỉ ASCII: isLetter của ký tự
 * tiếng Việt có dấu trả true, isDigit của chữ số Ả Rập cũng trả true.
 * <p>
 * 4. Nhóm chuyển đổi và so sánh
 * - Static: toUpperCase và toLowerCase trả về char tương ứng, toString trả về
 * chuỗi một ký tự.
 * - Instance: charValue lấy char bên trong, compareTo so sánh thứ tự, equals
 * so sánh bằng.
 * <p>
 * 5. Unicode và code point - phần cốt lõi của bài
 * - char rộng 16 BIT, biểu diễn được từ U+0000 tới U+FFFF, gọi là BMP
 * (Basic Multilingual Plane).
 * - Ký tự nằm ngoài khoảng đó gọi là SUPPLEMENTARY CHARACTER: emoji, một số
 * chữ Hán hiếm, ký tự cổ. Chúng KHÔNG nhét vừa một char, phải dùng int để
 * giữ code point.
 * - Phiên bản Unicode gắn liền với phiên bản JDK: Java SE 24 hỗ trợ Unicode
 * 16.0, Java SE 26 hỗ trợ Unicode 17.0.
 * <p>
 * 6. Escape sequence
 * - Tab, backspace, xuống dòng, về đầu dòng, sang trang lần lượt là gạch chéo
 * ngược kèm t, b, n, r, f.
 * - Muốn in chính dấu nháy đơn, nháy kép hoặc gạch chéo ngược thì đặt một
 * gạch chéo ngược phía trước.
 * <p>
 * 7. Ghi chú ngoài tài liệu - những chỗ thực sự dễ sai
 * - Constructor new Character đã LỖI THỜI: từ Java 9 nó bị đánh dấu
 * Deprecated kèm forRemoval. Dùng Character.valueOf hoặc để autoboxing lo.
 * - Character cũng CÓ CACHE cho khoảng 0 tới 127, nên cái bẫy so sánh bằng ==
 * lặp lại y như Integer ở bài trước.
 * - Emoji làm vỡ length: một emoji là surrogate pair nên length trả về 2, còn
 * codePointCount mới trả về 1. charAt lúc này lấy ra nửa ký tự, in riêng ra
 * là vô nghĩa. Hệ quả thực chiến: ràng buộc độ dài trên tên người dùng là
 * đếm char chứ không đếm ký tự.
 * - Tiếng Việt nằm trong BMP nên length đúng, NHƯNG chỉ khi chuỗi ở dạng NFC.
 * Client macOS hay gửi dạng NFD, khi đó một chữ có dấu tách thành chữ cái
 * cộng các dấu rời, và equals trả false dù nhìn giống hệt nhau. Cách xử lý
 * là chuẩn hóa bằng Normalizer về Form.NFC ngay tại tầng nhận request.
 * - Mỗi method isXxx có HAI BẢN: bản nhận char và bản nhận int code point.
 * Bản char KHÔNG xử lý được supplementary character. Khi duyệt chuỗi có thể
 * chứa emoji thì dùng codePoints rồi làm việc trên int.
 * - toUpperCase phụ thuộc LOCALE. Bản của String dùng locale mặc định của JVM,
 * mà ở locale Thổ Nhĩ Kỳ chữ i thường chuyển thành I có dấu chấm, làm hỏng
 * mọi so sánh không phân biệt hoa thường. Logic nội bộ phải truyền
 * Locale.ROOT tường minh.
 * - char là số nguyên 16 bit KHÔNG DẤU nên làm số học được: chữ a cộng 1 ra
 * 98 và kết quả mang kiểu int chứ không phải char.
 * - Cái bẫy kỳ quặc nhất của Java: escape gạch chéo ngược kèm chữ u và bốn
 * chữ số hex được xử lý ở giai đoạn TIỀN XỬ LÝ, trước cả khi chia token.
 * Nghĩa là viết nó trong một comment vẫn có tác dụng thật và có thể làm vỡ
 * source. Khác hẳn các escape khác, vốn chỉ có nghĩa bên trong chuỗi.
 * <p>
 * Best practice: dùng char cho ký tự đơn và chỉ bọc thành Character khi API
 * bắt buộc là object. Khi xử lý văn bản do người dùng nhập, mặc định coi chuỗi
 * có thể chứa emoji và chữ có dấu: duyệt bằng code point chứ không bằng chỉ số
 * char, chuẩn hóa NFC ngay ở biên hệ thống, và luôn truyền Locale.ROOT khi đổi
 * hoa thường cho mục đích so sánh.
 */
public class t2_Characters {

    void main() {
        String input = "Don hang A12 da giao";

        int chuCai = 0, chuSo = 0, khoangTrang = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) chuCai++;
            else if (Character.isDigit(c)) chuSo++;
            else if (Character.isWhitespace(c)) khoangTrang++;
        }
        System.out.println(chuCai + " chu cai, " + chuSo
                + " chu so, " + khoangTrang + " khoang trang");  // 14 chu cai, 2 chu so, 4 khoang trang

        // Nhóm kiểm tra hiểu Unicode, không chỉ ASCII
        System.out.println(Character.isLetter('ă'));        // true
        System.out.println(Character.isUpperCase('Đ'));     // true

        // Nhóm chuyển đổi: trả về char hoặc String mới
        System.out.println(Character.toUpperCase('a'));     // 'A'
        System.out.println(Character.toString('x'));        // "x"

        // char là số nguyên 16 bit, làm số học được - kết quả mang kiểu int
        System.out.println('a' + 1);                        // 98
        System.out.println('z' - 'a');                      // 25
        System.out.println((char) ('a' + 1));               // 'b'  (ép lại về char)

        // Character cũng có cache cho khoảng 0..127, nên == là cái bẫy
        Character c1 = 'A', c2 = 'A';
        System.out.println(c1 == c2);                       // true  - do cache
        System.out.println(c1.equals(c2));                  // true  - cách đúng

        // Ký tự ngoài BMP không nhét vừa một char: 1 code point = 2 char
        String emoji = new String(Character.toChars(0x1F600));
        System.out.println(emoji.length());                          // 2  (!)
        System.out.println(emoji.codePointCount(0, emoji.length()));  // 1
    }
}
