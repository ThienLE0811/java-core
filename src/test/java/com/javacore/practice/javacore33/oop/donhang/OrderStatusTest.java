package com.javacore.practice.javacore33.oop.donhang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bài 12 - {@link OrderStatus}: bảng luật chuyển trạng thái và field {@code description}.
 *
 * <p>Thứ cần đóng đinh ở đây không phải "switch có chạy không" mà là <b>đúng 5 cạnh trong tổng
 * số 25 cặp</b> được phép. Loại bug mà bài này sinh ra là gõ nhầm một hằng -
 * {@code next == PENDING} thay vì {@code next == CONFIRMED} - và compiler không thể bắt, vì cả
 * hai đều là {@code OrderStatus} hợp lệ. Chỉ có việc duyệt hết ma trận mới lộ ra.
 *
 * <p>Vì vậy {@link #BANG_LUAT} ở dưới là một <em>phát biểu lại độc lập</em> của luật, không
 * phải đọc từ code nghiệp vụ. Nó trùng ý với {@code switch} trong {@link OrderStatus} - đó là
 * chủ ý: sửa một bên mà quên bên kia thì test đỏ, buộc người sửa phải xác nhận là mình cố tình.
 *
 * <p>Khác bài 13: ở đó phần khó là <em>Java im lặng không báo lỗi</em> nên phải test hành vi
 * gốc của JDK. Ở đây Java không im lặng chỗ nào cả; phần khó nằm hoàn toàn ở luật nghiệp vụ.
 */
class OrderStatusTest {

    /**
     * Bảng luật, viết lại từ đề bài chứ không chép từ {@link OrderStatus#canTransitionTo}.
     *
     * <p>Dùng {@link EnumSet} chứ không phải {@link java.util.HashSet}: nó là bitmap trên
     * {@code ordinal}, nhanh hơn và in ra theo đúng thứ tự khai báo nên thông báo lỗi dễ đọc.
     * Lưu ý đây là chỗ <em>duy nhất</em> trong bài dính tới {@code ordinal}, và là chi tiết
     * cài đặt của JDK chứ không phải logic của ta.
     */
    private static final Map<OrderStatus, Set<OrderStatus>> BANG_LUAT = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));

    // --- bảng luật: toàn bộ 25 cặp ----------------------------------------

    /**
     * Duyệt hết ma trận 5x5. Đây là test quan trọng nhất của file.
     *
     * <p>Không chỉ kiểm tra các cặp hợp lệ trả {@code true}, mà cả 20 cặp còn lại phải trả
     * {@code false}. Một test chỉ khẳng định chiều {@code true} sẽ vẫn xanh khi
     * {@code canTransitionTo} được viết thành {@code return true;}.
     */
    @Test
    void canTransitionTo_dungChinhXac5CanhTrong25Cap() {
        for (OrderStatus from : OrderStatus.values()) {
            for (OrderStatus to : OrderStatus.values()) {
                boolean mongDoi = BANG_LUAT.get(from).contains(to);

                assertEquals(mongDoi, from.canTransitionTo(to),
                        () -> from + " → " + to + " phải là " + (mongDoi ? "hợp lệ" : "không hợp lệ"));
            }
        }
    }

    /** Năm cạnh hợp lệ, viết thẳng ra để đọc được bảng luật mà không cần giải mã vòng lặp. */
    @ParameterizedTest
    @CsvSource({
            "PENDING,   CONFIRMED",
            "PENDING,   CANCELLED",
            "CONFIRMED, SHIPPED",
            "CONFIRMED, CANCELLED",
            "SHIPPED,   DELIVERED"
    })
    void canTransitionTo_choPhepCacCanhHopLe(OrderStatus from, OrderStatus to) {
        assertTrue(from.canTransitionTo(to));
    }

    /**
     * Không trạng thái nào chuyển sang chính nó.
     *
     * <p>Đây là một <em>quyết định thiết kế</em>, không phải hệ quả tự nhiên: hoàn toàn có thể
     * cho {@code CONFIRMED → CONFIRMED} là hợp lệ để caller gọi lại cho tiện (idempotency).
     * Ta chọn không, và test này khoá lựa chọn đó lại.
     */
    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void canTransitionTo_khongChuyenSangChinhNo(OrderStatus status) {
        assertFalse(status.canTransitionTo(status));
    }

    /** Luồng xuôi phải thông suốt - nếu đứt một mắt thì đơn hàng không bao giờ giao được. */
    @Test
    void canTransitionTo_luongXuoiThongSuot() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
        assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.SHIPPED));
        assertTrue(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED));
    }

    /** Hàng đã rời kho thì không huỷ được nữa - luật nghiệp vụ, không phải thiếu sót. */
    @Test
    void canTransitionTo_daGuiHangThiKhongHuyDuoc() {
        assertFalse(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED));
    }

    // --- trạng thái cuối ---------------------------------------------------

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DELIVERED", "CANCELLED"})
    void trangThaiCuoi_khongDiDuocToiBatKyDau(OrderStatus cuoi) {
        for (OrderStatus to : OrderStatus.values()) {
            assertFalse(cuoi.canTransitionTo(to), () -> cuoi + " không được đi tới " + to);
        }
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DELIVERED", "CANCELLED"})
    void isFinal_dungVoiHaiTrangThaiCuoi(OrderStatus cuoi) {
        assertTrue(cuoi.isFinal());
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "CONFIRMED", "SHIPPED"})
    void isFinal_saiVoiTrangThaiDangChay(OrderStatus dangChay) {
        assertFalse(dangChay.isFinal());
    }

    /**
     * {@code isFinal()} và bảng luật phải nhất quán: "là trạng thái cuối" đúng bằng "không đi
     * được tới đâu".
     *
     * <p>Hai thứ này được cài đặt rời nhau - {@code isFinal} liệt kê hai hằng, bảng luật nằm
     * trong {@code switch} - nên chúng có thể trôi lệch nhau khi sửa code. Test này cột chúng
     * lại với nhau.
     */
    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void isFinal_nhatQuanVoiBangLuat(OrderStatus status) {
        boolean khongDiDuocDau = true;
        for (OrderStatus to : OrderStatus.values()) {
            if (status.canTransitionTo(to)) {
                khongDiDuocDau = false;
            }
        }

        assertEquals(khongDiDuocDau, status.isFinal(),
                () -> status + ": isFinal() không khớp với bảng luật");
    }

    // --- null ---------------------------------------------------------------

    /**
     * {@code canTransitionTo(null)} trả {@code false} chứ không ném.
     *
     * <p>Đây là hệ quả của việc so sánh bằng {@code ==}: mọi vế đều {@code false} khi
     * {@code next} là {@code null}. Hành vi này được chấp nhận và <em>phải được khoá lại</em>,
     * vì {@link Order#updateStatus(OrderStatus)} dựa vào đó mà quyết định tự chặn {@code null}
     * trước bằng {@code Objects.requireNonNull} - nếu không, message lỗi sẽ dính chữ
     * {@code null}.
     */
    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void canTransitionTo_nullTraVeFalseChuKhongNem(OrderStatus status) {
        assertFalse(status.canTransitionTo(null));
    }

    // --- description --------------------------------------------------------

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void getDescription_khongRongVaKhacTenHang(OrderStatus status) {
        assertFalse(status.getDescription().isBlank(),
                () -> status + " thiếu mô tả tiếng Việt");
        assertNotEquals(status.name(), status.getDescription(),
                () -> status + ": description phải là mô tả, không phải chép lại name()");
    }

    @Test
    void getDescription_dungNoiDungDaChot() {
        assertEquals("Chờ xác nhận", OrderStatus.PENDING.getDescription());
        assertEquals("Đã xác nhận", OrderStatus.CONFIRMED.getDescription());
        assertEquals("Đã gửi hàng", OrderStatus.SHIPPED.getDescription());
        assertEquals("Đã giao hàng", OrderStatus.DELIVERED.getDescription());
        assertEquals("Hủy bỏ", OrderStatus.CANCELLED.getDescription());
    }

    /**
     * {@code toString()} mặc định của enum trả về {@code name()}, không phải
     * {@code description}.
     *
     * <p>Chốt lại vì có thể bạn sẽ bị cám dỗ override {@code toString()} để in mô tả cho đẹp.
     * Đừng: {@code toString()} của enum được dùng ở những chỗ không ngờ tới - log, key của
     * {@code Map}, thông báo của JUnit - và đổi nó là đổi ngầm hành vi của tất cả.
     */
    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void toString_vanLaNameChuKhongPhaiDescription(OrderStatus status) {
        assertEquals(status.name(), status.toString());
    }

    // --- tripwire -----------------------------------------------------------

    /**
     * Chốt số lượng hằng.
     *
     * <p>{@code switch} không có {@code default} đã bảo vệ {@link OrderStatus#canTransitionTo}
     * khi thêm hằng mới - compiler sẽ báo lỗi. Nhưng {@link #BANG_LUAT} trong file test này thì
     * <em>không</em> được compiler bảo vệ: thêm một hằng mà quên cập nhật bảng thì
     * {@code BANG_LUAT.get(from)} trả {@code null} và test nổ bằng NPE khó hiểu. Dòng này làm
     * nó đỏ sớm với thông báo rõ ràng.
     */
    @Test
    void tripwire_themTrangThaiMoiThiPhaiCapNhatBangLuat() {
        assertEquals(5, OrderStatus.values().length,
                "Thêm/bớt trạng thái thì phải cập nhật BANG_LUAT trong file test này");
        assertEquals(OrderStatus.values().length, BANG_LUAT.size());
    }

    /**
     * {@code valueOf} phân biệt hoa thường và mỗi hằng chỉ tồn tại đúng một instance.
     *
     * <p>Đó là lý do dùng {@code ==} với enum là đúng, chứ không phải thói quen xấu - khác hẳn
     * với {@link String}.
     */
    @Test
    void moiHangChiCoDungMotInstance() {
        assertSame(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
    }
}
