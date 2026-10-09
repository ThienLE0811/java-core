package com.javacore.t06_final_enum_record_nested_class_object_lifecycle;

import java.util.function.IntSupplier;

public class FinalDemo {

    // final static field: hằng số toàn cục, gán 1 lần, dùng chung cho cả class (quy ước viết hoa)
    static final double VAT_RATE = 0.1;

    public static void main(String[] args) {
        // final local variable: gán 1 lần, gán lại sẽ không compile
        final int quota = 10;
        // quota = 20; // ❌ không compile vì quota là final

        System.out.println("VAT_RATE = " + VAT_RATE + ", quota = " + quota);

        System.out.println("\n=== final field trong object: gán 1 lần trong constructor, không có setter ===");
        ImmutablePoint p = new ImmutablePoint(3, 4);
        System.out.println("p = (" + p.getX() + ", " + p.getY() + ")");
        // p.x = 99; // ❌ không compile: x là private final, không setter nào được sinh ra

        System.out.println("\n=== final method: subclass không override được ===");
        Base base = new Sub();
        base.greet(); // luôn là bản của Base, vì greet() là final
        base.hello(); // là bản của Sub, vì hello() không final -> override bình thường

        System.out.println("\n=== effectively final: biến local không final nhưng không bị gán lại sau khi khai báo ===");
        int discountPercent = 15; // không có từ khóa final, nhưng chỉ gán 1 lần -> "effectively final"
        IntSupplier discountSupplier = () -> discountPercent; // lambda chỉ được capture biến effectively final
        System.out.println("discount từ lambda: " + discountSupplier.getAsInt() + "%");
        // discountPercent = 20; // nếu bỏ comment dòng này, lambda phía trên sẽ KHÔNG compile được nữa
    }

    // final class: không class nào được extends ImmutablePoint -> đảm bảo bất biến (immutable) không bị phá vỡ
    // bởi 1 subclass thêm field mutable hoặc override method theo cách sai
    static final class ImmutablePoint {
        private final int x;
        private final int y;

        ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }
    }

    static class Base {
        // final method: khóa chặt hành vi này, subclass không được phép định nghĩa lại
        // (dùng khi hành vi là quy tắc cố định, không muốn subclass "lách" sửa đổi)
        final void greet() {
            System.out.println("Base.greet(): xin chào (hành vi cố định, không override được)");
        }

        void hello() {
            System.out.println("Base.hello()");
        }
    }

    static class Sub extends Base {
        // @Override
        // void greet() { } // ❌ không compile vì greet() ở Base đã là final

        @Override
        void hello() {
            System.out.println("Sub.hello(): override bình thường vì hello() không final");
        }
    }
}
