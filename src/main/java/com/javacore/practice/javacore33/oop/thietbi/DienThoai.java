package com.javacore.practice.javacore33.oop.thietbi;

/**
 * Điện thoại - vừa sạc được, vừa kết nối được.
 *
 * <p>Đây là class trọng tâm của bài 10: nó mang <em>hai</em> năng lực cùng lúc, thứ mà
 * {@code extends} không làm được vì một class chỉ có đúng một cha. Ở bài 9
 * {@link com.javacore.practice.javacore33.oop.hinh.HinhTron} chỉ có thể là một {@code Hinh}
 * và không gì khác.
 *
 * <p>Nhìn từ {@link Chargeable#sacTatCa(Chargeable[])} thì điện thoại và
 * {@link PinDuPhong} không khác gì nhau - cùng một dòng {@code danhSach[i].sac()}, nhưng
 * điện thoại nhảy {@value #TOC_DO_SAC}% còn pin dự phòng chỉ 5%. Phần {@link Connectable}
 * của nó hoàn toàn vô hình ở đó.
 */
public class DienThoai implements Chargeable, Connectable {

    /**
     * Mỗi nhịp sạc tăng được bao nhiêu phần trăm.
     *
     * <p>{@code private} vì đây là đặc tính riêng của điện thoại - sạc nhanh hơn pin dự phòng.
     * Người dùng {@link Chargeable} không cần biết con số này, họ chỉ gọi {@link #sac()} và
     * nhận về kết quả khác nhau tuỳ loại thiết bị. Đó chính là đa hình.
     */
    private static final int TOC_DO_SAC = 20;

    /** Không {@code final}: thay đổi sau mỗi lần {@link #sac()}. */
    private int phanTramPin;

    /**
     * Không {@code final}: thay đổi theo {@link #ketNoi()} và {@link #ngatKetNoi()}.
     *
     * <p>Trùng tên với method {@link #dangKetNoi()} là hợp lệ - Java phân biệt field và method
     * ở hai không gian tên riêng. Cùng kiểu với {@code phanTramPin} ở trên.
     */
    private boolean dangKetNoi;

    /**
     * Tạo một chiếc điện thoại với mức pin cho sẵn, ban đầu chưa kết nối.
     *
     * @param phanTramPin mức pin ban đầu, trong khoảng 0..{@value Chargeable#PIN_TOI_DA}
     * @throws IllegalArgumentException nếu mức pin nằm ngoài khoảng trên
     */
    public DienThoai(int phanTramPin) {
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
    public void ketNoi() {
        dangKetNoi = true;
    }

    @Override
    public void ngatKetNoi() {
        dangKetNoi = false;
    }

    @Override
    public boolean dangKetNoi() {
        return dangKetNoi;
    }

    @Override
    public String toString() {
        return String.format("DienThoai{phanTramPin=%d, dangKetNoi=%b}", phanTramPin, dangKetNoi);
    }
}
