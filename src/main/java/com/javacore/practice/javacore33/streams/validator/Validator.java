package com.javacore.practice.javacore33.streams.validator;

import java.util.Objects;

/**
 * Bài 14+24 - hợp đồng "kiểm tra một giá trị có hợp lệ không".
 *
 * <p>Interface này không chứa luật nghiệp vụ nào. Nó tồn tại để <em>có một cái tên</em> cho
 * lambda bám vào: Java không có kiểu hàm như TypeScript, nên {@code s -> !s.isBlank()} tự thân
 * không mang kiểu gì cả - nó luôn phải mượn tên một interface có đúng một method abstract.
 *
 * <p>{@link FunctionalInterface} không làm code chạy khác đi; nó khoá lời hứa "mãi chỉ có một
 * method abstract" tại chính file này. Thiếu nó, người thêm method abstract thứ hai vẫn biên
 * dịch sạch, và lỗi mới nổ ở mọi chỗ đang truyền lambda - xa chỗ sai vài file.
 *
 * <p><b>Ba method {@code default} không phải tiện ích.</b> Chúng là lý do interface này tốt hơn
 * một method {@code static boolean kiemTra(...)}: validator ghép được với nhau, và mỗi lần ghép
 * trả về một validator mới chứ không chạy gì ngay. Đây đúng là hình dáng của
 * {@link java.util.function.Predicate} trong JDK - bài 24 chỉ việc đối chiếu.
 *
 * @param <T> kiểu giá trị được kiểm tra
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * Giá trị có hợp lệ không.
     *
     * <p><b>Chính sách null của toàn bộ hợp đồng này:</b> {@code null} là lỗi lập trình, không
     * phải dữ liệu không hợp lệ - nên ném chứ không trả {@code false}. Trả {@code false} sẽ
     * trộn "người dùng nhập sai" với "code quên khởi tạo" làm một, và chỗ gọi mất luôn khả
     * năng phân biệt.
     *
     * <p>Quyết định này nằm ở interface chứ không để mỗi lớp cài đặt tự chọn: cùng một hợp
     * đồng mà ba câu trả lời khác nhau cho cùng một input thì không còn là hợp đồng.
     *
     * @param value giá trị cần kiểm tra, không được null
     * @return {@code true} nếu hợp lệ
     * @throws NullPointerException nếu {@code value} là null
     */
    boolean validate(T value);

    /**
     * Ghép với một validator khác: hợp lệ khi <em>cả hai</em> cùng hợp lệ.
     *
     * <p>Trả về {@code Validator<T>} chứ không phải {@code boolean} - lúc gọi {@code and} chưa
     * có giá trị nào để kiểm tra cả, nó chỉ dựng ra một validator mới. Giống
     * {@code arr.filter().map()} bên JS: mỗi bước trả về thứ ghép tiếp được.
     *
     * <p>{@link Objects#requireNonNull} đặt ở thân {@code and}, <b>không</b> đặt trong lambda
     * trả về: nổ ngay lúc ghép thì stack trace chỉ thẳng vào dòng ghép sai. Để trong lambda
     * thì lỗi lùi tới tận lúc gọi {@link #validate(Object)}, có khi ở file khác.
     *
     * <p>Dùng {@code &&} chứ không {@code &}: validator thứ hai chỉ chạy khi cái đầu đã pass.
     * Ở bài này vô hại, nhưng khi validator thứ hai là một truy vấn database thì {@code &} là
     * một query thừa cho mỗi request.
     *
     * @param other validator ghép thêm, không được null
     * @return validator mới đại diện cho "cả hai cùng đúng"
     * @throws NullPointerException nếu {@code other} là null
     */
    default Validator<T> and(Validator<T> other) {
        Objects.requireNonNull(other, "other không được null");
        return value -> validate(value) && other.validate(value);
    }

    /**
     * Ghép với một validator khác: hợp lệ khi <em>ít nhất một</em> bên hợp lệ.
     *
     * <p>Cùng khuôn với {@link #and(Validator)}; {@code ||} cũng short-circuit, cái đầu đã
     * pass thì cái sau không chạy.
     *
     * @param other validator ghép thêm, không được null
     * @return validator mới đại diện cho "một trong hai đúng"
     * @throws NullPointerException nếu {@code other} là null
     */
    default Validator<T> or(Validator<T> other) {
        Objects.requireNonNull(other, "other không được null");
        return value -> validate(value) || other.validate(value);
    }

    /**
     * Đảo kết quả của validator hiện tại.
     *
     * <p>Không có {@code requireNonNull} vì không có tham số nào để mà null. Lưu ý: nếu
     * {@link #validate(Object)} ném NPE vì {@code value} null thì bản đảo cũng ném y hệt -
     * {@code negate} chỉ đảo {@code true}/{@code false}, không biến exception thành
     * {@code true}.
     *
     * @return validator mới trả ngược lại kết quả của validator này
     */
    default Validator<T> negate() {
        return value -> !validate(value);
    }
}
