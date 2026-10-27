package com.javacore.practice.javacore33.oop.thietbi;

/**
 * Pin dự phòng - thiết bị sạc được nhưng không kết nối mạng.
 *
 * <p>Class này tồn tại để chứng minh {@link Chargeable#sacTatCa(Chargeable[])} không đòi hỏi
 * gì ngoài năng lực sạc: nó chỉ {@code implements Chargeable}, không hề biết
 * {@link Connectable} là gì, vậy mà vẫn vào được mảng.
 *
 * <p>Khác {@link com.javacore.practice.javacore33.oop.hinh.HinhTron} ở bài 9: bán kính là
 * {@code final} vì hình đã tạo thì không méo đi được, còn mức pin thì đổi sau mỗi lần
 * {@link #sac()}. Đây là class có trạng thái biến đổi đầu tiên kể từ
 * {@link com.javacore.practice.javacore33.oop.TaiKhoanNganHang}.
 */
public class PinDuPhong implements Chargeable {

    /**
     * Mỗi nhịp sạc tăng được bao nhiêu phần trăm.
     *
     * <p>{@code private} vì đây là đặc tính riêng của pin dự phòng - sạc chậm hơn điện thoại.
     * Người dùng {@link Chargeable} không cần biết con số này, họ chỉ gọi {@link #sac()} và
     * nhận về kết quả khác nhau tuỳ loại thiết bị. Đó chính là đa hình.
     */
    private static final int TOC_DO_SAC = 5;

    /** Không {@code final}: đây là thứ duy nhất thay đổi trong vòng đời của object. */
    private int phanTramPin;

    /**
     * Tạo một cục pin dự phòng với mức pin cho sẵn.
     *
     * @param phanTramPin mức pin ban đầu, trong khoảng 0..{@value Chargeable#PIN_TOI_DA}
     * @throws IllegalArgumentException nếu mức pin nằm ngoài khoảng trên
     */
    public PinDuPhong(int phanTramPin) {
        this.phanTramPin = Chargeable.kiemTraPhanTram(phanTramPin);
    }

    @Override
    public int phanTramPin() {
        return phanTramPin;
    }

    /**
     * Sạc thêm một nhịp, không vượt quá {@value Chargeable#PIN_TOI_DA}.
     *
     * <p>Dùng {@link Math#min} thay cho {@code if}: chặn trần bản chất là phép lấy giá trị nhỏ
     * hơn giữa hai số, viết thẳng ra thì ngắn và không có nhánh nào để sai.
     */
    @Override
    public void sac() {
        phanTramPin = Math.min(phanTramPin + TOC_DO_SAC, PIN_TOI_DA);
    }

    @Override
    public String toString() {
        return String.format("PinDuPhong{phanTramPin=%d}", phanTramPin);
    }
}
