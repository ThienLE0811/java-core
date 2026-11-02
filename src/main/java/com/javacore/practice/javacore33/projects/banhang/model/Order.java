package com.javacore.practice.javacore33.projects.banhang.model;

import com.javacore.practice.javacore33.oop.donhang.InvalidStatusTransitionException;
import com.javacore.practice.javacore33.oop.donhang.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {

    private final String id;
    private final String customerId;
    private final List<OrderLine> dongHang;
    private final LocalDateTime thoiGianDat;
    private OrderStatus status;

    public Order(String id, String customerId, List<OrderLine> dongHang, LocalDateTime thoiGianDat) {
        this.id = Objects.requireNonNull(id, "Mã đơn không được null");
        this.customerId = Objects.requireNonNull(customerId, "Mã khách hàng không được null");
        Objects.requireNonNull(dongHang, "Danh sách dòng hàng không được null");
        if (dongHang.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một dòng hàng");
        }
        // Copy rồi mới giữ: List.copyOf cắt đứt liên hệ với list của người gọi, nên họ có sửa
        // tiếp cũng không chạm được vào đơn đã tạo.
        this.dongHang = List.copyOf(dongHang);
        this.thoiGianDat = Objects.requireNonNull(thoiGianDat, "Thời gian đặt không được null");
        this.status = OrderStatus.PENDING;   // R6
    }

    public String getId() {
        return this.id;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public List<OrderLine> getDongHang() {
        return this.dongHang;
    }

    public LocalDateTime getThoiGianDat() {
        return this.thoiGianDat;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    // R6: luật chuyển trạng thái nằm ở enum bài 12, không viết lại ở đây.
    public void updateStatus(OrderStatus next) {
        Objects.requireNonNull(next, "Trạng thái không được null");
        if (!this.status.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(this.status, next);
        }
        this.status = next;
    }

    public long tongTien() {
        return this.dongHang.stream().mapToLong(OrderLine::thanhTien).sum();
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', khach='%s', soDong=%d, tongTien=%d, status=%s}",
                id, customerId, dongHang.size(), tongTien(), status.name());
    }
}
