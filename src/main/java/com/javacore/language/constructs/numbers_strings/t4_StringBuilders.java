package com.javacore.language.constructs.numbers_strings;

/**
 * String Builders
 * <p>
 * 1. StringBuilder là gì
 * - Là đối trọng MUTABLE của String: nội dung và độ dài đều sửa được sau khi
 * tạo, thông qua lời gọi method.
 * - Hình dung đúng nhất theo tài liệu: một MẢNG KÝ TỰ CÓ ĐỘ DÀI THAY ĐỔI. Nắm
 * được hình ảnh này là nắm được cả bài, mọi method còn lại chỉ là thao tác
 * trên mảng đó.
 * - Lớp này tồn tại để trả lời đúng một câu hỏi của bài trước: String không
 * sửa được, vậy khi cần sửa thì làm sao.
 * <p>
 * 2. Khác biệt với String
 * - String bất biến, mọi thao tác trả về chuỗi mới. StringBuilder sửa TẠI CHỖ,
 * không tạo object mới mỗi lần.
 * - Tài liệu ghi rằng từ Java SE 9 phép nối chuỗi đã được tối ưu, nhanh hơn cả
 * việc tự dùng StringBuilder. Câu này ĐÚNG NHƯNG HẸP: tối ưu áp dụng cho MỘT
 * BIỂU THỨC, vì compiler dịch phép cộng thành lời gọi StringConcatFactory.
 * - Nó KHÔNG áp dụng xuyên qua vòng lặp: mỗi vòng là một biểu thức riêng, vẫn
 * sinh một String mới, độ phức tạp vẫn là bình phương.
 * - Kết luận đúng: nối chuỗi trong MỘT DÒNG thì dùng dấu cộng, nối chuỗi QUA
 * NHIỀU LẦN LẶP thì dùng StringBuilder.
 * <p>
 * 3. Length và Capacity - khái niệm mới so với String
 * - length là số ký tự ĐANG CÓ trong chuỗi.
 * - capacity là chỗ ĐÃ CẤP PHÁT, luôn lớn hơn hoặc bằng length và tự nở ra
 * khi cần.
 * - Mỗi lần hết chỗ, Java phải cấp mảng mới rồi chép lại toàn bộ. Biết trước
 * kích thước thì nên khai báo sẵn để tránh việc chép đi chép lại này.
 * - ensureCapacity cấp trước một dung lượng tối thiểu.
 * <p>
 * 4. Bốn constructor
 * - Không tham số: builder rỗng, capacity mặc định 16.
 * - Nhận int: builder rỗng với capacity chỉ định.
 * - Nhận String hoặc CharSequence: khởi tạo từ nội dung đó, capacity bằng độ
 * dài nội dung CỘNG 16.
 * <p>
 * 5. Nhóm thao tác sửa tại chỗ
 * - append thêm vào cuối, nhận mọi kiểu primitive và object.
 * - insert chèn vào vị trí offset chỉ định.
 * - delete xóa từ start tới end trừ một; deleteCharAt xóa một ký tự.
 * - replace thay một đoạn bằng chuỗi mới.
 * - setCharAt đổi một ký tự tại chỉ số.
 * - reverse đảo ngược toàn bộ chuỗi ký tự.
 * - setLength cắt bớt hoặc kéo dài, phần kéo thêm được điền ký tự rỗng.
 * - toString kết tinh thành String.
 * <p>
 * 6. Ví dụ đảo chuỗi - bài học thật sự của trang
 * - Tài liệu đặt cạnh nhau hai cách đảo một chuỗi: cách dùng mảng char mất
 * khoảng mười dòng với hai vòng lặp, cách dùng StringBuilder mất HAI DÒNG.
 * - Thông điệp không phải là học method reverse, mà là CHỌN ĐÚNG KIỂU DỮ LIỆU
 * THÌ CODE TỰ NGẮN LẠI.
 * <p>
 * 7. StringBuffer - biết để bỏ qua
 * - Là bản đồng bộ của StringBuilder, các method đều synchronized.
 * - Tài liệu nói thẳng: trừ khi thật sự cần một lớp an toàn đa luồng, không
 * cần dùng StringBuffer.
 * - Thực tế trường hợp đó gần như không tồn tại, vì builder hiếm khi được
 * chia sẻ giữa các luồng. Gặp StringBuffer trong code cũ thì đó là di sản
 * chứ không phải chủ ý.
 * <p>
 * 8. Ghi chú ngoài tài liệu
 * - StringBuilder KHÔNG override equals và hashCode, nên nó dùng bản mặc định
 * của Object tức so sánh THAM CHIẾU. Hai builder cùng nội dung vẫn không
 * bằng nhau. Tuyệt đối không dùng làm key của Map hay phần tử của Set; muốn
 * so sánh thì gọi toString trước, hoặc dùng compareTo có từ Java 11.
 * - append TRẢ VỀ CHÍNH NÓ nên nối chuỗi lời gọi được, viết liền một mạch.
 * - println tự gọi toString nên in thẳng builder được, không cần chuyển.
 * - toString tạo ra một bản SAO của nội dung, nên gọi nó trong vòng lặp là
 * làm mất hết lợi ích của builder.
 * - Khi nối nhiều phần tử có DẤU PHÂN CÁCH, String.join hoặc StringJoiner
 * gọn và rõ ý hơn hẳn việc tự append rồi cắt dấu phẩy thừa ở cuối.
 * - StringBuilder không an toàn đa luồng, dùng chung giữa các luồng mà không
 * đồng bộ sẽ cho kết quả sai hoặc ném exception, không chỉ là chậm.
 * <p>
 * Best practice: mặc định dùng String, chuyển sang StringBuilder ngay khi việc
 * nối chuỗi nằm trong vòng lặp hoặc trải qua nhiều bước. Tạo builder cục bộ
 * trong method rồi toString một lần ở cuối - đó là cách dùng an toàn và đúng ý
 * đồ nhất. Không bao giờ chọn StringBuffer trừ khi có lý do đo đạc cụ thể.
 */
public class t4_StringBuilders {

    void main() {
        StringBuilder sb = new StringBuilder("Java");
        System.out.println(sb.length());     // 4
        System.out.println(sb.capacity());   // 20 = 4 + 16

        // Sửa TẠI CHỖ, và append trả về chính nó nên nối lời gọi được
        sb.append(" core").append('!');
        sb.insert(0, ">> ");
        System.out.println(sb);              // ">> Java core!"  (println tự gọi toString)

        sb.deleteCharAt(sb.length() - 1);
        sb.reverse();
        System.out.println(sb);              // "eroc avaJ >>"

        // Bẫy: StringBuilder không override equals, so sánh tham chiếu
        System.out.println(new StringBuilder("a").equals(new StringBuilder("a")));  // false
        System.out.println(new StringBuilder("a").toString().equals("a"));          // true
    }
}
