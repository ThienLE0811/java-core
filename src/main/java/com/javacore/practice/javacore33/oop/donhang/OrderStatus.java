package com.javacore.practice.javacore33.oop.donhang;

public enum OrderStatus {

    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    SHIPPED("Đã gửi hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Hủy bỏ");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case OrderStatus.PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case OrderStatus.CONFIRMED -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case OrderStatus.SHIPPED -> next == OrderStatus.DELIVERED;
            case OrderStatus.DELIVERED -> false;   // trạng thái cuối, không đi đâu được
            case OrderStatus.CANCELLED -> false;
        };
    }

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
