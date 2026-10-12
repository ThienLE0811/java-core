package com.javacore.language.constructs.numbers_strings;

/**
 * Strings
 * <p>
 * 1. Tạo String
 * - Cách thường dùng nhất là string literal đặt trong hai dấu nháy kép; gặp
 * literal, compiler tự tạo một object String mang giá trị đó.
 * - Lớp String còn có 13 constructor, dùng khi nguồn dữ liệu là mảng char
 * hoặc mảng byte chứ không phải literal.
 * - Tính chất quan trọng nhất: String IMMUTABLE. Tạo xong không sửa được.
 * Mọi method trông có vẻ "sửa chuỗi" thật ra đều TRẢ VỀ MỘT CHUỖI MỚI và để
 * nguyên chuỗi gốc.
 * <p>
 * 2. Độ dài
 * - length trả về số ký tự. Lưu ý đây là method, khác với mảng dùng field
 * length không có ngoặc.
 * <p>
 * 3. Nối chuỗi
 * - concat trả về chuỗi mới ghép hai chuỗi.
 * - Toán tử cộng dùng phổ biến hơn, nhất là trong lệnh in. Object không phải
 * String nằm trong phép cộng sẽ được gọi toString tự động.
 * - Text Block từ Java SE 15: mở đầu bằng BA dấu nháy kép rồi phải xuống dòng
 * ngay, dùng cho chuỗi nhiều dòng như SQL, JSON, HTML.
 * <p>
 * 4. Tạo chuỗi đã format
 * - String.format là bản static trả về CHUỖI, khác printf vốn in thẳng ra
 * màn hình. Nhờ vậy kết quả tái sử dụng được thay vì chỉ in một lần.
 * - Toàn bộ converter và flag học ở bài Numbers dùng lại nguyên vẹn ở đây.
 * <p>
 * 5. Chuỗi sang số
 * - Mỗi lớp bao số đều có valueOf nhận String và trả về OBJECT lớp bao.
 * - Và có parseXxx nhận String nhưng trả về PRIMITIVE, nên trực tiếp hơn.
 * - Chuỗi không hợp lệ thì cả hai đều ném NumberFormatException, không trả về
 * giá trị đặc biệt nào.
 * <p>
 * 6. Số sang chuỗi
 * - Ba cách: String.valueOf nhận số, nối với chuỗi rỗng, hoặc gọi toString của
 * lớp bao tương ứng.
 * - Cách nối chuỗi rỗng ngắn nhất nhưng khó đọc ý đồ; ưu tiên String.valueOf.
 * <p>
 * 7. Lấy ký tự và chuỗi con theo chỉ số
 * - charAt lấy ký tự tại một chỉ số, ĐẾM TỪ 0.
 * - substring có hai bản: bản hai tham số lấy từ beginIndex tới endIndex TRỪ
 * MỘT, tức cận trên KHÔNG được lấy; bản một tham số lấy từ vị trí đó tới
 * hết chuỗi.
 * - getChars chép một đoạn ký tự sang mảng char có sẵn, bắt đầu từ vị trí
 * đích chỉ định; toCharArray chép toàn bộ sang một mảng mới.
 * <p>
 * 8. Nhóm method biến đổi khác
 * - split cắt chuỗi thành mảng, có bản thêm tham số limit để giới hạn số phần
 * tử của mảng kết quả.
 * - trim bỏ khoảng trắng ở đầu và cuối.
 * - toLowerCase và toUpperCase đổi hoa thường toàn chuỗi.
 * - replace có bản đổi ký tự thành ký tự, và bản đổi chuỗi con thành chuỗi.
 * - subSequence trả về CharSequence thay vì String.
 * <p>
 * 9. Tìm kiếm
 * - indexOf tìm XUÔI từ đầu chuỗi, lastIndexOf tìm NGƯỢC từ cuối chuỗi.
 * - Cả hai đều có bản nhận thêm fromIndex để bắt đầu tìm từ một vị trí.
 * - Cả hai đều có bản nhận ký tự và bản nhận chuỗi con.
 * - KHÔNG tìm thấy thì trả về âm 1, không ném exception. Quên kiểm tra âm 1
 * rồi đem vào substring là nguồn lỗi kinh điển.
 * - contains chỉ trả boolean, dùng khi không quan tâm vị trí.
 * <p>
 * 10. Thay thế
 * - replace thay TẤT CẢ các lần xuất hiện, làm việc trên chuỗi thường.
 * - replaceAll và replaceFirst làm việc trên BIỂU THỨC CHÍNH QUY, thay tất cả
 * hoặc chỉ lần đầu tiên.
 * <p>
 * 11. So sánh chuỗi
 * - equals so sánh nội dung, có phân biệt hoa thường; equalsIgnoreCase thì
 * không phân biệt.
 * - compareTo trả số âm, 0 hoặc dương theo thứ tự từ điển; compareToIgnoreCase
 * là bản bỏ qua hoa thường.
 * - startsWith và endsWith kiểm tra tiền tố và hậu tố; startsWith còn có bản
 * nhận offset để kiểm tra từ giữa chuỗi.
 * - regionMatches so sánh một ĐOẠN của chuỗi này với một đoạn của chuỗi khác,
 * có bản nhận cờ boolean để bỏ qua hoa thường.
 * - matches kiểm tra TOÀN BỘ chuỗi có khớp một biểu thức chính quy không.
 * <p>
 * 12. Ghi chú ngoài tài liệu - những chỗ thực sự dễ sai
 * - SO SÁNH CHUỖI BẰNG == LÀ SAI. Đây là lỗi số một của người mới. String
 * literal được gom vào String Pool nên hai literal giống nhau trỏ cùng một
 * object và == tình cờ trả true; nhưng chuỗi tạo bằng new String hoặc chuỗi
 * ghép lúc chạy là object khác, == trả false dù nội dung y hệt. Luôn dùng
 * equals. Cũng vì vậy đừng bao giờ viết new String của một literal.
 * - Vì immutable, nối chuỗi TRONG VÒNG LẶP tạo ra một object mới mỗi vòng,
 * độ phức tạp thành bình phương. Phải dùng StringBuilder. Riêng phép cộng
 * đứng một mình thì compiler đã tối ưu sẵn nên không cần lo.
 * - split và replaceAll nhận REGEX chứ không phải chuỗi thường. Tách theo dấu
 * chấm mà viết thẳng dấu chấm sẽ ra mảng rỗng, vì trong regex dấu chấm là
 * "mọi ký tự". Phải escape hoặc dùng Pattern.quote.
 * - split mặc định BỎ các phần tử rỗng ở CUỐI mảng, nên tách chuỗi kết thúc
 * bằng dấu phân cách sẽ thiếu phần tử. Truyền limit bằng âm 1 để giữ lại.
 * - trim cắt theo quy tắc cũ, chỉ coi ký tự có mã nhỏ hơn hoặc bằng khoảng
 * trắng là khoảng trắng. Từ Java 11 dùng strip vì nó hiểu khoảng trắng theo
 * Unicode, xử lý đúng khoảng trắng toàn chiều rộng của tiếng Nhật, Trung.
 * - isEmpty chỉ kiểm tra độ dài bằng 0; isBlank (Java 11) coi chuỗi toàn
 * khoảng trắng cũng là rỗng. Validate input nên dùng isBlank.
 * - toLowerCase và toUpperCase không tham số dùng LOCALE MẶC ĐỊNH của JVM. Ở
 * locale Thổ Nhĩ Kỳ chữ i cho kết quả khác, làm hỏng so sánh. Logic nội bộ
 * phải truyền Locale.ROOT tường minh.
 * - compareTo so theo giá trị UTF-16 chứ không theo thứ tự từ điển của NGÔN
 * NGỮ. Sắp xếp danh sách tiếng Việt bằng compareTo sẽ sai thứ tự chữ có
 * dấu; muốn đúng phải dùng java.text.Collator theo locale.
 * - substring và charAt làm việc trên chỉ số CHAR. Chuỗi chứa emoji có thể bị
 * cắt giữa một surrogate pair, tạo ra ký tự hỏng. Đây là hệ quả trực tiếp
 * của phần code point đã học ở bài Characters.
 * - Gọi equals trên biến có thể null sẽ ném NullPointerException. Đặt hằng số
 * ở trước, hoặc dùng Objects.equals để an toàn với cả hai vế.
 * - Vài method tiện dụng thêm từ Java 11 trở đi: repeat lặp chuỗi, lines tách
 * theo dòng, String.join nối một tập hợp bằng dấu phân cách, formatted là
 * bản instance của String.format.
 * - Mật khẩu KHÔNG nên giữ trong String: vì immutable nên không xóa chủ động
 * được, giá trị nằm lại trong heap tới khi bị thu gom và có thể lộ qua heap
 * dump. Dùng mảng char rồi chủ động ghi đè sau khi dùng xong.
 * <p>
 * Best practice: mặc định dùng String cho mọi thao tác văn bản, chuyển sang
 * StringBuilder ngay khi có vòng lặp nối chuỗi. So sánh luôn bằng equals, đổi
 * hoa thường luôn kèm Locale.ROOT, validate input bằng isBlank thay vì isEmpty,
 * và nhớ rằng mọi method nhận regex (split, matches, replaceAll, replaceFirst)
 * đều đòi escape ký tự đặc biệt - còn replace thì không.
 */
public class t3_Strings {

    void main() {
        String path = "/home/thienlv/bao-cao.txt";

        System.out.println(path.length());              // 25
        System.out.println(path.charAt(6));             // 't'  (đếm từ 0)
        System.out.println(path.contains("bao-cao"));   // true

        // Tách tên file: tìm NGƯỢC từ cuối để lấy dấu / cuối cùng
        int lastSlash = path.lastIndexOf('/');
        String fileName = path.substring(lastSlash + 1);
        System.out.println(fileName);                   // "bao-cao.txt"

        // Tách phần mở rộng: substring(begin) lấy từ đó tới hết chuỗi
        int dot = fileName.lastIndexOf('.');
        String extension = fileName.substring(dot + 1);
        System.out.println(extension);                  // "txt"

        // Không tìm thấy thì trả về -1, KHÔNG ném exception
        System.out.println(path.indexOf("khong-co"));   // -1

        // String immutable: toUpperCase trả chuỗi MỚI, path giữ nguyên
        System.out.println(path.toUpperCase());         // "/HOME/THIENLV/BAO-CAO.TXT"
        System.out.println(path);                       // "/home/thienlv/bao-cao.txt"

        // So sánh nội dung bằng equals, không bao giờ dùng ==
        String copy = new String("txt");
        System.out.println(extension.equals(copy));     // true
        System.out.println(extension == copy);          // false
    }
}
