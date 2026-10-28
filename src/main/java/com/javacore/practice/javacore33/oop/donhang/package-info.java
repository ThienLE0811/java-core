/**
 * Bài 12 - enum {@link com.javacore.practice.javacore33.oop.donhang.OrderStatus}: năm trạng
 * thái đơn hàng, mỗi trạng thái mang mô tả tiếng Việt và tự biết mình đi tiếp được tới đâu.
 *
 * <p>Tách thành package riêng cùng lý do với {@code oop.hinh} (bài 9) và {@code oop.maytinh}
 * (bài 13): đây là một cụm bốn type đi liền nhau, không phải một class đứng độc lập.
 *
 * <p><b>Bài này thực chất là một máy trạng thái.</b> Đề bài nói "enum có field và method",
 * nhưng thứ đáng học không phải cú pháp đó - mà là việc <em>luật nghiệp vụ nằm ở đâu</em>.
 * Cách viết sai phổ biến là rải {@code if (status.equals("PENDING") && ...)} khắp tầng service.
 * Ở đây luật nằm gọn trong một {@code switch} duy nhất, ngay bên trong kiểu dữ liệu mà nó nói
 * về; {@link com.javacore.practice.javacore33.oop.donhang.Order} chỉ hỏi chứ không tự quyết.
 *
 * <p><b>Cái bẫy mà bài này tồn tại để dựng lên.</b> Phản xạ đầu tiên là nhét danh sách trạng
 * thái kế tiếp vào constructor của enum. Thử trên JDK 25:
 * <pre>
 * PENDING("Chờ xác nhận", OrderStatus.CONFIRMED),
 *                         ^-------------------^
 * error: illegal forward reference
 * </pre>
 * Hằng enum không tham chiếu được tới hằng khai báo <em>sau</em> nó. Phần khó chịu hơn: chiều
 * ngược lại thì biên dịch bình thường, nên ai thử đúng một ca sẽ tưởng là mình làm sai cú pháp
 * chứ không nhận ra đây là ràng buộc nền tảng. Mà luồng đơn hàng đi xuôi
 * {@code PENDING → CONFIRMED → SHIPPED → DELIVERED}, nghĩa là <em>mọi</em> cạnh đều là forward
 * reference. Hướng này chết hẳn, không cứu được.
 *
 * <p><b>Ba hướng còn lại, và vì sao chọn hướng đã chọn:</b>
 * <ol>
 *   <li><em>{@code switch} expression trong {@code canTransitionTo}</em> - đã chọn. Không dính
 *       thứ tự khởi tạo, và quan trọng hơn: <b>không có nhánh {@code default}</b>. Thêm một
 *       hằng mới vào enum là biên dịch hỏng ngay tại chỗ cần sửa. Viết thêm
 *       {@code default -> false} là tự tay vứt bỏ lợi ích lớn nhất của enum trong switch.</li>
 *   <li><em>{@link java.util.EnumSet} gán trong khối {@code static}</em> - chạy được, nhưng
 *       field {@code allowed} phải bỏ {@code final} nên mất tính bất biến. Đây là cách sẽ gặp
 *       trong production khi bảng luật to ra; với năm trạng thái thì là over-engineer.</li>
 *   <li><em>Thân riêng cho từng hằng</em> - mỗi hằng thành một anonymous subclass. Dài, và luật
 *       bị xé lẻ ra năm chỗ thay vì nằm chung một bảng đọc được bằng mắt.</li>
 * </ol>
 *
 * <p><b>Bảng luật.</b> Hai trạng thái cuối không đi đâu được nữa; không trạng thái nào chuyển
 * sang chính nó.
 * <pre>
 * PENDING    →  CONFIRMED, CANCELLED
 * CONFIRMED  →  SHIPPED, CANCELLED
 * SHIPPED    →  DELIVERED        (đã rời kho thì không huỷ nữa)
 * DELIVERED  →  -
 * CANCELLED  →  -
 * </pre>
 *
 * <p><b>Ba loại lỗi, ba kiểu exception - cố ý không gom làm một:</b>
 * <ul>
 *   <li>{@link com.javacore.practice.javacore33.oop.donhang.InvalidStatusTransitionException}
 *       cho bước chuyển sai. Nó kế thừa {@link java.lang.IllegalStateException} chứ không phải
 *       {@link java.lang.IllegalArgumentException}: bản thân {@code DELIVERED} không sai, cái
 *       sai là <em>cặp</em> (trạng thái hiện tại, đích đến). Trong Spring, đúng loại lỗi này
 *       map sang HTTP <b>409 Conflict</b>, không phải 400.</li>
 *   <li>{@link java.lang.NullPointerException} cho tham số {@code null}.</li>
 *   <li>{@link java.lang.IllegalArgumentException} cho mã đơn rỗng - không {@code null} nhưng
 *       không hợp lệ. Quy ước này khớp với {@code Product} ở bài 11.</li>
 * </ul>
 *
 * <p><b>Một tính chất không nhìn thấy khi đọc code</b> nhưng là thứ quan trọng nhất của
 * {@link com.javacore.practice.javacore33.oop.donhang.Order#updateStatus(OrderStatus)}: kiểm
 * tra xong mới gán, nên khi exception bay ra thì object vẫn nguyên vẹn. Gán trước rồi mới ném
 * sẽ để lại một đơn hàng ở trạng thái mà nghiệp vụ không cho phép - và không ai biết, vì
 * exception đã bị bắt ở tầng trên rồi.
 *
 * <p><b>Ghi chú cho giai đoạn Spring sau này.</b> Enum này khi vào JPA phải là
 * {@code @Enumerated(EnumType.STRING)}, <b>không bao giờ</b> {@code ORDINAL} - mặc định của JPA
 * lại chính là {@code ORDINAL}. Lưu theo thứ tự khai báo nghĩa là chèn thêm một trạng thái vào
 * giữa sẽ làm toàn bộ dữ liệu cũ trong database mang ý nghĩa khác đi, âm thầm, không lỗi nào
 * báo. Cùng lý do đó, không chỗ nào trong package này dùng
 * {@link java.lang.Enum#ordinal()}.
 *
 * <p>{@link com.javacore.practice.javacore33.oop.donhang.DonHangDemo} có {@code main}: in ma
 * trận 5x5 toàn bộ bảng luật, chạy hai luồng hợp lệ và sáu ca lỗi. Nó là nơi <em>duy nhất</em>
 * trong package có {@code catch} và {@code System.out} - ba class còn lại chỉ tính và ném,
 * đúng vai của tầng service.
 */
package com.javacore.practice.javacore33.oop.donhang;
