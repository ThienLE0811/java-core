package com.javacore.practice.javacore33.oop.donhang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 12 - {@link Order}: ràng buộc lúc tạo và hành vi của
 * {@link Order#updateStatus(OrderStatus)}.
 *
 * <p>Bảng luật đã được {@code OrderStatusTest} soát hết 25 cặp, nên file này <b>không lặp lại
 * việc đó</b>. Nó trả lời ba câu khác:
 * <ol>
 *   <li>Đơn mới tạo có đúng ở {@code PENDING} không.</li>
 *   <li>{@code updateStatus} có <em>dùng</em> bảng luật không - chứ không phải tự chế luật riêng.</li>
 *   <li><b>Khi ném thì object có còn nguyên vẹn không.</b> Đây là tính chất quan trọng nhất và
 *       cũng là thứ đọc code dễ bỏ sót: chỉ cần đảo hai dòng (gán trước, kiểm tra sau) là nó
 *       hỏng mà mọi test khác vẫn xanh.</li>
 * </ol>
 *
 * <p>Ba loại exception được test riêng chứ không gom: {@link InvalidStatusTransitionException}
 * cho bước chuyển sai, {@link NullPointerException} cho tham số {@code null},
 * {@link IllegalArgumentException} cho mã đơn rỗng. Gom lại thành
 * {@code assertThrows(RuntimeException.class, ...)} thì test vẫn xanh kể cả khi code ném nhầm
 * loại - và phân biệt được ba loại này chính là một nửa nội dung thiết kế của bài.
 */
class OrderTest {

    private static final String MA_DON = "DH-001";

    /** Đơn đã đi hết luồng tới {@code DELIVERED}. Tạo mới mỗi lần gọi vì {@code Order} có trạng thái. */
    private static Order donDaGiao() {
        Order don = new Order("DH-DELIVERED");
        don.updateStatus(OrderStatus.CONFIRMED);
        don.updateStatus(OrderStatus.SHIPPED);
        don.updateStatus(OrderStatus.DELIVERED);
        return don;
    }

    // --- khởi tạo -----------------------------------------------------------

    @Test
    void khoiTao_luonBatDauOPending() {
        Order don = new Order(MA_DON);

        assertEquals(OrderStatus.PENDING, don.getStatus());
        assertEquals(MA_DON, don.getMaDon());
    }

    /**
     * {@code null} thì {@link NullPointerException}.
     *
     * <p>Không dùng {@code @NullSource} gộp chung với ca chuỗi rỗng ở dưới, vì hai ca này ném
     * <em>hai loại khác nhau</em> - và đó chính là điều cần chốt.
     */
    @Test
    void khoiTao_maDonNullThiNemNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Order(null));
    }

    /**
     * Không {@code null} nhưng không hợp lệ thì {@link IllegalArgumentException}.
     *
     * <p>Quy ước của JDK và của chính repo này (xem {@code Product} bài 11):
     * <b>{@code null} → NPE; không null nhưng sai → IAE</b>. Dùng NPE cho chuỗi rỗng là nói dối
     * người đọc log, và làm code gọi phải so sánh chuỗi message mới phân biệt được hai ca.
     *
     * <p>{@code "\t"} và {@code "\n"} có mặt ở đây vì {@link String#isBlank()} coi chúng là
     * rỗng, còn {@link String#isEmpty()} thì không - chọn nhầm method là lọt ngay.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void khoiTao_maDonRongThiNemIllegalArgumentException(String maDonRong) {
        assertThrows(IllegalArgumentException.class, () -> new Order(maDonRong));
    }

    /** Khoảng trắng hai đầu không bị cắt - {@code Order} không tự ý sửa dữ liệu của caller. */
    @Test
    void khoiTao_giuNguyenMaDonKhongTrim() {
        assertEquals(" DH-001 ", new Order(" DH-001 ").getMaDon());
    }

    // --- updateStatus: ca hợp lệ ---------------------------------------------

    @ParameterizedTest
    @CsvSource({
            "PENDING,   CONFIRMED",
            "PENDING,   CANCELLED",
            "CONFIRMED, SHIPPED",
            "CONFIRMED, CANCELLED",
            "SHIPPED,   DELIVERED"
    })
    void updateStatus_doiTrangThaiKhiHopLe(OrderStatus from, OrderStatus to) {
        Order don = donOTrangThai(from);

        don.updateStatus(to);

        assertEquals(to, don.getStatus());
    }

    @Test
    void updateStatus_chayHetLuongGiaoHang() {
        Order don = new Order(MA_DON);

        don.updateStatus(OrderStatus.CONFIRMED);
        don.updateStatus(OrderStatus.SHIPPED);
        don.updateStatus(OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, don.getStatus());
        assertTrue(don.getStatus().isFinal());
    }

    // --- updateStatus: ca sai -------------------------------------------------

    /**
     * Tính chất quan trọng nhất của bài: <b>ném xong thì trạng thái không đổi</b>.
     *
     * <p>Kiểm tra trước, gán sau. Nếu ai đó đảo hai dòng trong
     * {@link Order#updateStatus(OrderStatus)} thì mọi test khác vẫn xanh - chỉ dòng
     * {@code assertEquals} cuối cùng ở đây đỏ. Đó là lý do nó tồn tại.
     */
    @Test
    void updateStatus_nemThiTrangThaiKhongDoi() {
        Order don = new Order(MA_DON);

        assertThrows(InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.DELIVERED));

        assertEquals(OrderStatus.PENDING, don.getStatus(),
                "Phải kiểm tra trước rồi mới gán, nếu không object bị để lại ở trạng thái rác");
    }

    @Test
    void updateStatus_nhayCocThiNem() {
        Order don = new Order(MA_DON);

        assertThrows(InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.SHIPPED));
    }

    @Test
    void updateStatus_diLuiTuTrangThaiCuoiThiNem() {
        Order don = donDaGiao();

        assertThrows(InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.PENDING));
        assertEquals(OrderStatus.DELIVERED, don.getStatus());
    }

    /** Đơn đã huỷ thì không hồi sinh được - không trạng thái nào đi ra từ {@code CANCELLED}. */
    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void updateStatus_donDaHuyThiKhongChuyenDiDau(OrderStatus dich) {
        Order don = donOTrangThai(OrderStatus.CANCELLED);

        assertThrows(InvalidStatusTransitionException.class, () -> don.updateStatus(dich));
        assertEquals(OrderStatus.CANCELLED, don.getStatus());
    }

    @Test
    void updateStatus_chuyenSangChinhNoThiNem() {
        Order don = new Order(MA_DON);

        assertThrows(InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.PENDING));
    }

    /**
     * {@code null} ném {@link NullPointerException}, <em>không</em> phải
     * {@link InvalidStatusTransitionException}.
     *
     * <p>Chi tiết này không hiển nhiên: {@code OrderStatus.canTransitionTo(null)} trả
     * {@code false} chứ không ném, nên nếu {@link Order#updateStatus(OrderStatus)} quên
     * {@code Objects.requireNonNull} thì luồng vẫn chạy tới nhánh ném
     * {@code InvalidStatusTransitionException} - và message in ra sẽ dính chữ {@code null},
     * hoặc tệ hơn là NPE ở trong {@code taoMessage} với stack trace vô nghĩa. Test này khoá
     * đúng thứ tự hai lời gọi đó.
     */
    @Test
    void updateStatus_nullNemNullPointerExceptionChuKhongPhaiLoiChuyenTrangThai() {
        Order don = new Order(MA_DON);

        assertThrows(NullPointerException.class, () -> don.updateStatus(null));
        assertEquals(OrderStatus.PENDING, don.getStatus());
    }

    // --- nội dung exception ----------------------------------------------------

    @Test
    void exception_mangTheoFromVaTo() {
        Order don = new Order(MA_DON);

        InvalidStatusTransitionException loi = assertThrows(
                InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.DELIVERED));

        assertSame(OrderStatus.PENDING, loi.getFrom());
        assertSame(OrderStatus.DELIVERED, loi.getTo());
    }

    /**
     * Message phải đủ để đọc log mà không cần mở code: có cả mô tả tiếng Việt lẫn tên hằng của
     * hai đầu.
     *
     * <p>Assert trên {@code contains} chứ không so khớp cả câu - so khớp cả câu thì sửa một
     * dấu phẩy cũng làm test đỏ, mà điều đó không nói lên lỗi gì.
     */
    @Test
    void exception_messageChuaCaDescriptionVaTenHang() {
        Order don = new Order(MA_DON);

        InvalidStatusTransitionException loi = assertThrows(
                InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.DELIVERED));

        String message = loi.getMessage();
        assertTrue(message.contains(OrderStatus.PENDING.getDescription()), message);
        assertTrue(message.contains(OrderStatus.PENDING.name()), message);
        assertTrue(message.contains(OrderStatus.DELIVERED.getDescription()), message);
        assertTrue(message.contains(OrderStatus.DELIVERED.name()), message);
    }

    /** Hai ca lỗi khác nhau thì message phải khác nhau, nếu không tách nhánh làm gì. */
    @Test
    void exception_phanBietTrangThaiCuoiVoiNhayCoc() {
        Order donMoi = new Order(MA_DON);
        Order donDaGiao = donDaGiao();

        String messageNhayCoc = assertThrows(InvalidStatusTransitionException.class,
                () -> donMoi.updateStatus(OrderStatus.DELIVERED)).getMessage();
        String messageDaKetThuc = assertThrows(InvalidStatusTransitionException.class,
                () -> donDaGiao.updateStatus(OrderStatus.PENDING)).getMessage();

        assertTrue(messageDaKetThuc.contains("kết thúc"), messageDaKetThuc);
        assertTrue(!messageNhayCoc.contains("kết thúc"), messageNhayCoc);
    }

    /**
     * Khoá lại quan hệ kế thừa, vì nó là một phần của hợp đồng công khai.
     *
     * <p>Code gọi hoàn toàn có thể viết {@code catch (IllegalStateException e)} để bắt chung
     * mọi lỗi "thao tác sai với trạng thái hiện tại". Đổi class cha sau này là làm hỏng những
     * chỗ đó mà không có lỗi biên dịch nào báo.
     */
    @Test
    void exception_laMotIllegalStateException() {
        Order don = new Order(MA_DON);

        InvalidStatusTransitionException loi = assertThrows(
                InvalidStatusTransitionException.class,
                () -> don.updateStatus(OrderStatus.DELIVERED));

        assertInstanceOf(IllegalStateException.class, loi);
    }

    // --- toString -----------------------------------------------------------------

    @Test
    void toString_chuaMaDonVaTrangThai() {
        Order don = new Order(MA_DON);

        String ketQua = don.toString();

        assertTrue(ketQua.contains(MA_DON), ketQua);
        assertTrue(ketQua.contains(OrderStatus.PENDING.name()), ketQua);
        assertTrue(ketQua.contains(OrderStatus.PENDING.getDescription()), ketQua);
    }

    // --- helper -------------------------------------------------------------------

    /**
     * Tạo đơn rồi lái nó tới {@code dich} bằng các bước hợp lệ.
     *
     * <p>Cố ý <em>không</em> thêm constructor nhận sẵn {@code OrderStatus} vào {@link Order}
     * chỉ để phục vụ test: làm vậy là mở một cửa hậu cho code thật tạo ra đơn hàng ở trạng thái
     * bất kỳ, bỏ qua toàn bộ bảng luật. Đi đường vòng trong test đắt hơn vài dòng, nhưng giữ
     * được bất biến của class.
     */
    private static Order donOTrangThai(OrderStatus dich) {
        Order don = new Order("DH-" + dich.name());
        switch (dich) {
            case PENDING -> {
                // đã ở sẵn
            }
            case CONFIRMED -> don.updateStatus(OrderStatus.CONFIRMED);
            case SHIPPED -> {
                don.updateStatus(OrderStatus.CONFIRMED);
                don.updateStatus(OrderStatus.SHIPPED);
            }
            case DELIVERED -> {
                don.updateStatus(OrderStatus.CONFIRMED);
                don.updateStatus(OrderStatus.SHIPPED);
                don.updateStatus(OrderStatus.DELIVERED);
            }
            case CANCELLED -> don.updateStatus(OrderStatus.CANCELLED);
        }
        return don;
    }
}
