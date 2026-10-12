package com.javacore.language.constructs.basics;

public class t6_ExpressionsStatementsBlocks {
}
/**
 * Expressions, Statements and Blocks
 * <p>
 * 1. Expressions - biểu thức
 * - Là cấu trúc gồm biến, toán tử và lời gọi method, ĐÁNH GIÁ RA MỘT GIÁ TRỊ duy nhất.
 * - Giá trị đó luôn có kiểu, nên expression lắp được vào expression khác.
 * - Kiểu trả về phụ thuộc thành phần: phép gán trả kiểu của toán hạng trái,
 * phép so sánh trả boolean, nối chuỗi trả String.
 * <p>
 * 2. Compound Expressions - biểu thức ghép
 * - Ghép nhiều expression nhỏ thành một expression lớn, miễn kiểu dữ liệu khớp nhau.
 * - Thứ tự đánh giá đổi thì kết quả đổi: x + y / z khác (x + y) / z vì chia có
 * độ ưu tiên cao hơn cộng.
 * - Khi không có ngoặc, toán tử ưu tiên cao chạy trước.
 * <p>
 * 3. Floating Point Arithmetic - số thực
 * - Phép toán số thực có sai số làm tròn, kết quả không chính xác tuyệt đối:
 * 0.1 + 0.2 ra 0.30000000000000004 chứ không phải 0.3.
 * - Hệ quả 1: KHÔNG so sánh double bằng ==, phải so bằng sai số cho phép
 * (ví dụ Math.abs(a - b) nhỏ hơn 0.000001).
 * - Hệ quả 2: KHÔNG dùng float/double cho tiền tệ - dùng BigDecimal.
 * Cộng 0.1 mười lần ra 0.9999999999999999, số tiền sẽ lệch dần.
 * <p>
 * 4. Statements - câu lệnh
 * - Là một đơn vị thực thi hoàn chỉnh, kết thúc bằng dấu chấm phẩy.
 * - CHỈ 4 loại expression được phép thành statement khi thêm dấu chấm phẩy:
 * phép gán, dùng tăng giảm một đơn vị, gọi method, tạo object bằng new.
 * - Ngoài 4 loại đó là lỗi compile "not a statement" - ví dụ viết x + y; vì
 * tính xong rồi vứt kết quả đi, không gây tác động gì nên compiler chặn.
 * - Hai loại statement khác: declaration statement (khai báo biến) và
 * control flow statement (điều khiển luồng chạy - học ở bài kế tiếp).
 * <p>
 * 5. Blocks - khối lệnh
 * - Là nhóm gồm KHÔNG hoặc NHIỀU statement nằm giữa cặp ngoặc nhọn cân đối.
 * - Dùng được ở bất cứ chỗ nào cho phép một statement đơn - vì vậy
 * if (x) doA(); và if (x) { doA(); doB(); } đều hợp lệ.
 * - Biến khai báo bên trong block chỉ sống trong block đó, hết ngoặc nhọn là
 * biến biến mất, bên ngoài không truy cập được.
 * <p>
 * Best practice: đóng ngoặc cho biểu thức ghép để người đọc không phải nhớ bảng
 * độ ưu tiên. Luôn viết ngoặc nhọn cho thân if/for/while kể cả khi chỉ có một
 * lệnh, tránh bug khi sau này thêm lệnh thứ hai vào mà quên mở ngoặc.
 */
