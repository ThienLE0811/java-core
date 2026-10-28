package com.javacore.practice.javacore33.oop.donhang;

/**
 * Demo bài 12 - enum {@link OrderStatus} với máy trạng thái đơn hàng.
 *
 * <p>Class này là nơi <em>duy nhất</em> trong package có {@code catch} và có
 * {@code System.out}. {@link OrderStatus}, {@link Order} và
 * {@link InvalidStatusTransitionException} hoàn toàn câm lặng - chúng chỉ tính toán và ném.
 * Đó đúng là ranh giới giữa tầng nghiệp vụ và tầng hiển thị mà một ứng dụng Spring sẽ có:
 * service ném, {@code @RestControllerAdvice} mới quyết định in gì ra cho client.
 *
 * <p>Ba phần, mỗi phần trả lời một câu hỏi khác nhau:
 * <ol>
 *   <li>{@link #inMaTranChuyenTrangThai()} - <em>luật</em> là gì. In bảng 5x5 để soát toàn bộ
 *       {@link OrderStatus#canTransitionTo(OrderStatus)} bằng mắt, nhanh hơn đọc {@code switch}.</li>
 *   <li>{@link #demoLuongThanhCong()} - luật đúng thì chạy ra sao.</li>
 *   <li>{@link #demoCacCaLoi()} - luật sai thì hỏng thế nào, và quan trọng hơn:
 *       <em>hỏng mà không làm bẩn object</em>.</li>
 * </ol>
 *
 * <p>Demo không thay thế test. Nó để người đọc chạy một phát thấy toàn cảnh; việc khẳng định
 * đúng/sai thuộc về {@code OrderStatusTest} và {@code OrderTest}. Vì vậy ở đây không có dòng
 * nào kiểu {@code if (ketQua != mongDoi)} - demo chỉ in, không phán xét.
 */
public final class DonHangDemo {

    private DonHangDemo() {
        // Utility class
    }

    /**
     * Điểm chạy của chương trình demo.
     *
     * @param args không dùng tới - bài này không nhận dữ liệu đầu vào dạng chuỗi
     */
    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  BÀI 12: ENUM OrderStatus & MÁY TRẠNG THÁI ĐƠN HÀNG");
        System.out.println("===============================================================");

        inMaTranChuyenTrangThai();
        demoLuongThanhCong();
        demoCacCaLoi();
    }

    /**
     * In bảng 5x5 mọi cặp {@code (from, to)} kèm kết quả
     * {@link OrderStatus#canTransitionTo(OrderStatus)}.
     *
     * <p>Dùng {@link OrderStatus#values()} chứ không liệt kê tay: thêm một hằng vào enum thì
     * bảng tự nở ra, không ai phải nhớ sửa chỗ này.
     */
    private static void inMaTranChuyenTrangThai() {
        System.out.println("\n--- 1. Ma trận chuyển trạng thái (OK = hợp lệ) ---\n");

        System.out.printf("%-12s", "từ / sang");
        for (OrderStatus cot : OrderStatus.values()) {
            System.out.printf("%-12s", cot.name());
        }
        System.out.println();

        for (OrderStatus hang : OrderStatus.values()) {
            System.out.printf("%-12s", hang.name());
            for (OrderStatus cot : OrderStatus.values()) {
                System.out.printf("%-12s", hang.canTransitionTo(cot) ? "OK" : ".");
            }
            System.out.println("   " + hang.getDescription() + (hang.isFinal() ? " (cuối)" : ""));
        }
    }

    /**
     * Hai luồng hợp lệ: giao hàng trọn vẹn, và huỷ đơn khi còn đang chờ.
     *
     * <p>Mỗi bước in {@link OrderStatus#getDescription()} chứ không phải
     * {@link OrderStatus#name()} - đó là lý do field {@code description} tồn tại.
     */
    private static void demoLuongThanhCong() {
        System.out.println("\n--- 2. Luồng hợp lệ ---\n");

        Order donGiaoThanhCong = new Order("DH-001");
        System.out.println("Tạo mới: " + donGiaoThanhCong);
        chuyen(donGiaoThanhCong, OrderStatus.CONFIRMED);
        chuyen(donGiaoThanhCong, OrderStatus.SHIPPED);
        chuyen(donGiaoThanhCong, OrderStatus.DELIVERED);
        System.out.println("  -> kết thúc: " + donGiaoThanhCong
                + ", isFinal() = " + donGiaoThanhCong.getStatus().isFinal());

        System.out.println();

        Order donBiHuy = new Order("DH-002");
        System.out.println("Tạo mới: " + donBiHuy);
        chuyen(donBiHuy, OrderStatus.CANCELLED);
        System.out.println("  -> kết thúc: " + donBiHuy
                + ", isFinal() = " + donBiHuy.getStatus().isFinal());
    }

    /**
     * Sáu tình huống lỗi, mỗi tình huống một kiểu - không lặp lại cùng một ca.
     *
     * <p>Bốn ca đầu ném {@link InvalidStatusTransitionException}; hai ca cuối ném
     * {@link NullPointerException} và {@link IllegalArgumentException}. Đặt cạnh nhau để thấy
     * rõ: không phải lỗi nào của đơn hàng cũng là lỗi chuyển trạng thái, và đó là chủ ý.
     */
    private static void demoCacCaLoi() {
        System.out.println("\n--- 3. Các ca lỗi ---\n");

        thuChuyenSai("Nhảy cóc: chưa xác nhận đã đòi gửi hàng",
                new Order("DH-003"), OrderStatus.SHIPPED);

        thuChuyenSai("Đi lùi: đơn đã giao muốn quay về chờ xác nhận",
                taoDonDaGiao("DH-004"), OrderStatus.PENDING);

        thuChuyenSai("Hồi sinh: đơn đã huỷ muốn xác nhận lại",
                taoDonDaHuy("DH-005"), OrderStatus.CONFIRMED);

        thuChuyenSai("Tự chuyển sang chính mình",
                new Order("DH-006"), OrderStatus.PENDING);

        thuChuyenSai("Trạng thái null", new Order("DH-007"), null);

        System.out.println("Mã đơn rỗng");
        try {
            new Order("   ");
        } catch (IllegalArgumentException e) {
            System.out.println("  [x] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Chuyển trạng thái và in kết quả - dùng cho các bước <em>được kỳ vọng là hợp lệ</em>.
     *
     * <p>Cố ý không có {@code try} ở đây: nếu một bước trong luồng hợp lệ mà ném, demo phải vỡ
     * ra cho thấy, chứ không được nuốt.
     */
    private static void chuyen(Order don, OrderStatus next) {
        don.updateStatus(next);
        System.out.println("  [v] -> " + next.getDescription() + " (" + next.name() + ")");
    }

    /**
     * Thử một bước chuyển <em>được kỳ vọng là sai</em>, bắt lỗi và in ra.
     *
     * <p>Hai khối {@code catch} tách riêng, và thứ tự là bắt buộc: đi từ <em>cụ thể</em> đến
     * <em>tổng quát</em>. {@link InvalidStatusTransitionException} kế thừa
     * {@link IllegalStateException}, nên nếu đảo ngược thì khối cha nuốt mất khối con - và ta
     * mất luôn {@code getFrom()}/{@code getTo()}, thứ chỉ class con mới có.
     *
     * <p>Không bắt {@code Exception}: làm vậy là nuốt luôn {@link NullPointerException} do lập
     * trình viên viết sai, biến bug thành dòng log vô hại. Ở đây {@code NPE} được bắt
     * <em>có chủ đích</em> vì nó là hành vi đã thiết kế của
     * {@link Order#updateStatus(OrderStatus)} khi nhận {@code null}.
     *
     * <p>In {@link Throwable#getMessage()} chứ không {@code printStackTrace()} - message đã
     * được dựng đủ thông tin, stack trace ở demo chỉ làm rối.
     */
    private static void thuChuyenSai(String tinhHuong, Order don, OrderStatus next) {
        System.out.println(tinhHuong + " - " + don);
        OrderStatus truocKhiGoi = don.getStatus();
        try {
            don.updateStatus(next);
            System.out.println("  (không ném - xem lại bảng luật)");
        } catch (InvalidStatusTransitionException e) {
            System.out.println("  [x] " + e.getClass().getSimpleName() + ": " + e.getMessage());
            System.out.println("      getFrom() = " + e.getFrom() + ", getTo() = " + e.getTo());
        } catch (NullPointerException e) {
            System.out.println("  [x] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        // Tính chất quan trọng nhất của updateStatus, nhìn code không thấy được: ném xong thì
        // object vẫn nguyên vẹn, không bị để lại ở trạng thái dở dang.
        System.out.println("      trạng thái sau khi ném: " + don.getStatus()
                + (don.getStatus() == truocKhiGoi ? " (không đổi)" : " (ĐÃ BỊ ĐỔI!)"));
        System.out.println();
    }

    /**
     * Đơn đã đi hết luồng tới {@link OrderStatus#DELIVERED}, dùng làm dữ liệu cho ca lỗi.
     */
    private static Order taoDonDaGiao(String maDon) {
        Order don = new Order(maDon);
        don.updateStatus(OrderStatus.CONFIRMED);
        don.updateStatus(OrderStatus.SHIPPED);
        don.updateStatus(OrderStatus.DELIVERED);
        return don;
    }

    /**
     * Đơn đã huỷ ngay từ {@link OrderStatus#PENDING}, dùng làm dữ liệu cho ca lỗi.
     */
    private static Order taoDonDaHuy(String maDon) {
        Order don = new Order(maDon);
        don.updateStatus(OrderStatus.CANCELLED);
        return don;
    }
}
