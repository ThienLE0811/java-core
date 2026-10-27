package com.javacore.practice.javacore33.oop.thietbi;

/**
 * Router - thiết bị cắm điện trực tiếp, không có pin.
 *
 * <p>Class này tồn tại để chứng minh phần còn lại của bài 10. Nó chỉ
 * {@code implements Connectable}, nên {@link Chargeable#sacTatCa(Chargeable[])} <em>không
 * nhận</em> nó - sai bị chặn ngay lúc compile, không đợi tới lúc chạy:
 *
 * <pre>{@code
 * Chargeable.sacTatCa(new Chargeable[] { new Router() });
 * // error: incompatible types: Router cannot be converted to Chargeable
 * }</pre>
 *
 * <p>Đây cũng là lý do {@link Chargeable} và {@link Connectable} là hai interface riêng. Gộp
 * chúng thành một {@code ThietBi} đủ bốn method thì Router buộc phải cài {@code sac()}, và chỉ
 * còn hai đường - thân rỗng (nói dối: chữ ký bảo sạc được, thực tế không làm gì) hoặc ném
 * {@code UnsupportedOperationException} (đẩy lỗi từ compile xuống runtime). Lúc đó dòng trên
 * sẽ biên dịch trót lọt và "sạc" một cái router.
 */
public class Router implements Connectable {

    /** Không {@code final}: thay đổi theo {@link #ketNoi()} và {@link #ngatKetNoi()}. */
    private boolean dangKetNoi;

    /** Tạo một router, ban đầu chưa kết nối. */
    public Router() {
        // Không có trạng thái nào cần khởi tạo: boolean mặc định đã là false.
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
        return String.format("Router{dangKetNoi=%b}", dangKetNoi);
    }
}
