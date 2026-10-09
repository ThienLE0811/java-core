package com.javacore.t06_final_enum_record_nested_class_object_lifecycle;

import java.util.EnumMap;
import java.util.EnumSet;

public class EnumDemo {

    public static void main(String[] args) {
        System.out.println("=== Enum cơ bản: tập giá trị cố định, an toàn hơn dùng int/String tùy tiện ===");
        for (Direction d : Direction.values()) {
            System.out.println(d + " (ordinal=" + d.ordinal() + ") -> đối diện là " + d.opposite());
        }

        System.out.println("\n=== Enum có field + constructor: gắn dữ liệu cho từng hằng số ===");
        Order order = new Order(OrderStatus.NEW);
        System.out.println(order.describe());
        order.setStatus(OrderStatus.PAID);
        System.out.println(order.describe());

        System.out.println("\n=== Enum có method riêng cho từng hằng số (constant-specific method) ===");
        for (Operation op : Operation.values()) {
            System.out.println("3 " + op.symbol() + " 2 = " + op.apply(3, 2));
        }

        System.out.println("\n=== switch trên enum: compiler biết hết các case, dễ phát hiện thiếu case ===");
        System.out.println("PAID có phải trạng thái cuối? " + isFinalStatus(OrderStatus.PAID));
        System.out.println("SHIPPED có phải trạng thái cuối? " + isFinalStatus(OrderStatus.SHIPPED));

        System.out.println("\n=== EnumSet / EnumMap: tập hợp và map tối ưu riêng cho enum ===");
        EnumSet<Direction> horizontal = EnumSet.of(Direction.EAST, Direction.WEST);
        System.out.println("Hướng ngang: " + horizontal);

        EnumMap<OrderStatus, String> vietnameseLabel = new EnumMap<>(OrderStatus.class);
        for (OrderStatus s : OrderStatus.values()) {
            vietnameseLabel.put(s, s.getLabel());
        }
        System.out.println("Nhãn tiếng Việt: " + vietnameseLabel);
    }

    private static boolean isFinalStatus(OrderStatus status) {
        switch (status) {
            case SHIPPED:
            case CANCELLED:
                return true;
            case NEW:
            case PAID:
                return false;
            default:
                // default vẫn nên có để code an toàn nếu sau này thêm hằng số mới mà quên sửa switch này
                throw new IllegalStateException("Chưa xử lý status: " + status);
        }
    }

    enum Direction {
        NORTH, EAST, SOUTH, WEST;

        Direction opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case SOUTH:
                    return NORTH;
                case EAST:
                    return WEST;
                default:
                    return EAST;
            }
        }
    }

    // Enum với field riêng: mỗi hằng số gắn 1 label tiếng Việt, gán qua constructor (constructor của enum luôn private/package-private)
    enum OrderStatus {
        NEW("Mới tạo"),
        PAID("Đã thanh toán"),
        SHIPPED("Đã giao"),
        CANCELLED("Đã hủy");

        private final String label;

        OrderStatus(String label) {
            this.label = label;
        }

        String getLabel() {
            return label;
        }
    }

    static class Order {
        private OrderStatus status;

        Order(OrderStatus status) {
            this.status = status;
        }

        void setStatus(OrderStatus status) {
            this.status = status;
        }

        String describe() {
            return "Order[" + status + " - " + status.getLabel() + "]";
        }
    }

    // Enum với abstract method: mỗi hằng số BẮT BUỘC tự cung cấp thân method riêng,
    // thay cho if/switch dài dòng khi xử lý logic khác nhau theo từng loại
    enum Operation {
        ADD("+") {
            @Override
            int apply(int a, int b) {
                return a + b;
            }
        },
        SUBTRACT("-") {
            @Override
            int apply(int a, int b) {
                return a - b;
            }
        },
        MULTIPLY("*") {
            @Override
            int apply(int a, int b) {
                return a * b;
            }
        };

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        String symbol() {
            return symbol;
        }

        abstract int apply(int a, int b);
    }
}
