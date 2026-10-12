package com.javacore.language.constructs.basics;

public class t7_ControlFlow {

    public static void main(String[] args) {

        String searchMe = "Look for a substring in me";
        String substring = "sub";
        boolean foundIt = false;

        int max = searchMe.length() -
                substring.length();

        test:
        for (int i = 0; i <= max; i++) {
            int n = substring.length();
            int j = i;
            int k = 0;
            while (n-- != 0) {
                if (searchMe.charAt(j++) != substring.charAt(k++)) {
                    continue test;
                }
            }
            foundIt = true;
            break test;
        }
        IO.println(foundIt ? "Found it" : "Didn't find it");
    }
}
/**
 * Control Flow Statements
 * <p>
 * 1. if-then
 * - Lệnh điều khiển cơ bản nhất: chỉ chạy thân lệnh khi điều kiện đúng.
 * - Ngoặc nhọn được phép bỏ khi thân chỉ có một lệnh, nhưng tài liệu khuyên
 * LUÔN viết để tránh lỗi khi sau này thêm lệnh thứ hai.
 * <p>
 * 2. if-then-else
 * - Cung cấp nhánh chạy thay thế khi điều kiện sai.
 * - Nối nhiều else if thành chuỗi: khi một điều kiện khớp, các điều kiện phía
 * sau KHÔNG được đánh giá nữa - thứ tự các nhánh vì thế rất quan trọng.
 * <p>
 * 3. while và do-while
 * - while kiểm tra điều kiện ở ĐẦU vòng lặp, có thể không chạy lần nào.
 * - do-while kiểm tra ở CUỐI vòng lặp nên thân luôn chạy ÍT NHẤT MỘT LẦN.
 * - do-while kết thúc bằng dấu chấm phẩy sau điều kiện.
 * <p>
 * 4. for
 * - Ba thành phần: khởi tạo, điều kiện dừng, biểu thức tăng/giảm.
 * - Biến khai báo ở phần khởi tạo chỉ sống trong vòng lặp, ra ngoài là hết.
 * - Cả ba thành phần đều có thể bỏ trống, bỏ hết thì thành vòng lặp vô hạn.
 * <p>
 * 5. Enhanced for - for mỗi phần tử
 * - Cú pháp: for (Kiểu phần_tử : tập_hợp) - duyệt mảng hoặc Collection.
 * - Tài liệu khuyến nghị DÙNG DẠNG NÀY thay cho for thường bất cứ khi nào có thể,
 * vì gọn và không sai chỉ số.
 * - Hạn chế: không có chỉ số, và không được sửa cấu trúc tập hợp khi đang duyệt
 * (thêm/xoá phần tử sẽ ném ConcurrentModificationException lúc chạy).
 * <p>
 * 6. break
 * - break không nhãn: thoát khỏi switch/for/while/do-while GẦN NHẤT bao quanh nó.
 * - break có nhãn: thoát khỏi statement được gán nhãn, dùng để thoát một lúc
 * nhiều tầng vòng lặp.
 * - Lưu ý tài liệu nhấn mạnh: break CHẤM DỨT statement được gán nhãn, KHÔNG phải
 * nhảy tới vị trí đặt nhãn - nó không phải goto.
 * <p>
 * 7. continue
 * - Bỏ qua phần còn lại của vòng lặp hiện tại, đi tiếp vòng kế.
 * - continue có nhãn: bỏ qua vòng lặp hiện tại của vòng lặp ĐƯỢC GÁN NHÃN,
 * không phải vòng lặp trong cùng.
 * <p>
 * 8. return và yield
 * - return thoát khỏi method hiện tại, kèm giá trị trả về nếu method không void.
 * - yield thoát khỏi switch expression đang đứng trong đó và trả ra giá trị cho
 * biểu thức - chỉ dùng trong switch expression, học kỹ ở bài sau.
 * <p>
 * 9. Ghi chú ngoài tài liệu - khác biệt với JavaScript
 * - Java KHÔNG có truthiness: điều kiện bắt buộc là boolean. Viết if (count) hay
 * if (str) đều lỗi compile "incompatible types ... cannot be converted to boolean".
 * Phải viết rõ: count != 0, str != null, list.isEmpty() ...
 * - Hệ quả: null-check phải viết tay, Java không có toán tử ?. như JS.
 * - Enhanced for tương đương for...of; Java KHÔNG có for...in vì không có object
 * literal - duyệt Map thì dùng entrySet().
 * - Java không có hoisting: biến phải khai báo trước khi dùng, hết ngoặc nhọn là mất.
 * - switch KHÔNG nằm trong bài này, dev.java tách thành hai bài riêng:
 * Branching with Switch Statements và Branching with Switch Expressions.
 * <p>
 * Best practice: luôn viết ngoặc nhọn cho thân lệnh kể cả khi chỉ có một dòng.
 * Ưu tiên enhanced for khi không cần chỉ số. Dùng nhãn tiết kiệm - vòng lặp
 * lồng nhiều tầng tới mức cần nhãn thường là dấu hiệu nên tách ra method riêng.
 */
