package com.javacore.practice.javacore33.oop.donhang;

import java.util.Objects;

public class Order {

    private final String maDon;
    private OrderStatus status;

    public Order(String maDon) {
        Objects.requireNonNull(maDon, "Mã đơn không được null");
        if (maDon.isBlank()) {
            throw new IllegalArgumentException("Mã đơn không được trống");
        }
        this.maDon = maDon;
        this.status = OrderStatus.PENDING;
    }

    public String getMaDon() {
        return this.maDon;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public void updateStatus(OrderStatus next) {
        Objects.requireNonNull(next, "Trạng thái không được null");
        if (!status.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(status, next);
        }
        this.status = next;
    }

    @Override
    public String toString() {
        return String.format("Order{maDon='%s', status=%s (%s)}",
                maDon, status.name(), status.getDescription());
    }
}
