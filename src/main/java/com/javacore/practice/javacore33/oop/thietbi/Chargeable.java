package com.javacore.practice.javacore33.oop.thietbi;

import java.util.Objects;

/**
 * Bài 10 - năng lực "sạc được" của một thiết bị.
 *
 * <p>Điểm của bài này nằm ở {@link #sacTatCa(Chargeable[])}: tham số là một <em>năng lực</em>,
 * không phải một họ thiết bị. Mọi thứ sạc được đều lọt vào, bất kể có kết nối mạng được hay
 * không - còn thứ không sạc được thì compiler chặn ngay, không đợi tới lúc chạy.
 *
 * <p>Tách khỏi {@link Connectable} chứ không gộp thành một {@code ThietBi} có đủ bốn method:
 * {@link Router} cắm điện trực tiếp, không có pin. Ép nó cài {@code sac()} thì chỉ còn hai
 * đường, cả hai đều tệ hơn việc viết thêm một file interface - thân rỗng (nói dối: chữ ký bảo
 * sạc được, thực tế không làm gì), hoặc ném {@code UnsupportedOperationException} (đẩy lỗi từ
 * compile xuống runtime).
 *
 * <p>Khác bài 9: {@link com.javacore.practice.javacore33.oop.hinh.Hinh} là abstract class nên
 * một hình chỉ được làm con của đúng một họ. Interface thì {@link DienThoai} mang được cả hai
 * năng lực cùng lúc - đó là thứ {@code extends} không làm được.
 */
public interface Chargeable {

    /**
     * Mức pin tối đa, dùng chung cho {@link #dayPin()} và phần chặn trần của mọi lớp cài đặt.
     *
     * <p>Không viết {@code public static final}: mọi field trong interface ngầm định đã là
     * như vậy. Đây là hằng số thuộc về hợp đồng, không phải trạng thái của thiết bị nào.
     */
    int PIN_TOI_DA = 100;

    /**
     * Mức pin hiện tại, luôn trong khoảng {@code 0..}{@value #PIN_TOI_DA}.
     *
     * <p>Để {@code abstract}: đây là <em>dữ liệu</em>, chỉ bản thân thiết bị mới biết.
     *
     * @return phần trăm pin hiện tại
     */
    int phanTramPin();

    /**
     * Sạc thêm một nhịp. Tốc độ do từng loại thiết bị quyết định.
     *
     * <p>Đã đầy thì gọi tiếp không làm gì - thiết bị tự chặn trần {@value #PIN_TOI_DA}, người
     * gọi không phải hỏi {@link #dayPin()} trước. Chỗ gọi không sửa lỗi hộ chỗ được gọi.
     */
    void sac();

    /**
     * Pin đã đầy chưa.
     *
     * <p>Để {@code default} chứ không {@code abstract}: giá trị này suy ra được hoàn toàn từ
     * {@link #phanTramPin()}. Bắt mỗi lớp tự cài thì chỉ cần một chỗ viết {@code >} thay vì
     * {@code >=} là lệch - cùng một quy tắc mà ba câu trả lời khác nhau.
     *
     * <p>Dùng {@code >=} chứ không {@code ==}: nếu một lớp cài {@link #sac()} sai và để pin
     * vọt quá {@value #PIN_TOI_DA}, {@code ==} sẽ trả {@code false} vĩnh viễn - pin đầy tràn
     * mà hệ thống tưởng chưa đầy, sạc mãi không dừng.
     *
     * @return {@code true} nếu pin đạt {@value #PIN_TOI_DA}
     */
    default boolean dayPin() {
        return phanTramPin() >= PIN_TOI_DA;
    }

    /**
     * Chặn mức pin vô nghĩa, trả lại chính giá trị đó nếu hợp lệ.
     *
     * <p>Đặt ở đây vì quy tắc "phần trăm pin nằm trong 0..{@value #PIN_TOI_DA}" sinh ra từ
     * chính {@link #PIN_TOI_DA} của hợp đồng này, và mọi lớp cài đặt đều cần đúng một quy tắc
     * đó ở constructor. Để mỗi lớp chép một bản thì sửa một chỗ sẽ quên các chỗ còn lại.
     *
     * <p>Lưu ý khác với lần gộp hai interface thành một {@code ThietBi}: {@code static} method
     * trong interface <em>không</em> được kế thừa xuống lớp con, nên nó không thêm nghĩa vụ
     * nào cho ai cả. Đây chỉ là một hàm tiện ích được đặt cạnh hằng số mà nó dùng - không làm
     * hợp đồng béo lên, {@link Router} vẫn không liên quan gì.
     *
     * @param giaTri mức pin cần kiểm tra
     * @return chính {@code giaTri} nếu hợp lệ, để gọi lồng ngay trong constructor
     * @throws IllegalArgumentException nếu {@code giaTri} nằm ngoài 0..{@value #PIN_TOI_DA}
     */
    static int kiemTraPhanTram(int giaTri) {
        if (giaTri < 0 || giaTri > PIN_TOI_DA) {
            throw new IllegalArgumentException(
                    "Phần trăm pin phải trong khoảng 0.." + PIN_TOI_DA + ", nhận được: " + giaTri);
        }
        return giaTri;
    }

    /**
     * Sạc một nhịp cho mọi thiết bị trong mảng.
     *
     * <p>Chỉ gọi {@link #sac()}, không hỏi {@link #dayPin()} trước: thiết bị đã hứa tự chặn
     * trần. Kiểm tra hộ ở đây vừa thừa, vừa che mất lỗi của lớp nào quên chặn.
     *
     * <p>Để {@code static} chứ không {@code default} vì nó thao tác trên cả <em>tập hợp</em>,
     * không cần biết {@code this} là thiết bị nào - {@code dienThoai.sacTatCa(mang)} là một
     * câu vô nghĩa. Cùng lý do với {@code Hinh.tongDienTich} ở bài 9.
     *
     * <p>Mảng rỗng thì không làm gì - đó là kết quả đúng, không phải lỗi.
     *
     * @param danhSach mảng thiết bị, không được null và không được chứa phần tử null
     * @throws NullPointerException nếu {@code danhSach} là null, hoặc chứa phần tử null
     */
    static void sacTatCa(Chargeable[] danhSach) {
        Objects.requireNonNull(danhSach, "danhSach không được null");

        // Duyệt theo chỉ số thay vì for-each để báo được vị trí phần tử null:
        // "phần tử thứ 3 là null" giúp tìm ra chỗ sai nhanh hơn hẳn một NPE trống trơn.
        for (int i = 0; i < danhSach.length; i++) {
            if (danhSach[i] == null) {
                throw new NullPointerException("Phần tử tại vị trí " + i + " là null");
            }
            danhSach[i].sac();
        }

    }
}
