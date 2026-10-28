package com.javacore.practice.javacore33.oop.donhang;

public class InvalidStatusTransitionException extends IllegalStateException {

    private final OrderStatus from;
    private final OrderStatus to;

    public InvalidStatusTransitionException(OrderStatus from, OrderStatus to) {
        super(taoMessage(from, to));
        this.from = from;
        this.to = to;
    }

    public OrderStatus getFrom() {
        return this.from;
    }

    public OrderStatus getTo() {
        return this.to;
    }

    private static String taoMessage(OrderStatus from, OrderStatus to) {
        if (from.isFinal()) {
            return String.format(
                    "Đơn hàng đã kết thúc ở trạng thái '%s' (%s), không thể chuyển sang '%s' (%s).",
                    from.getDescription(), from.name(), to.getDescription(), to.name());
        }
        return String.format(
                "Không thể chuyển đơn hàng từ '%s' (%s) sang '%s' (%s).",
                from.getDescription(), from.name(), to.getDescription(), to.name());
    }
}
