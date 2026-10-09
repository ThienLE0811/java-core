package com.javacore.t06_final_enum_record_nested_class_object_lifecycle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ObjectLifecycleDemo {

    public static void main(String[] args) {
        System.out.println("=== Thứ tự khởi tạo: static block (1 lần/class) -> instance block -> constructor, từ cha xuống con ===");
        new Child();
        System.out.println("--- tạo Child lần 2: static block KHÔNG chạy lại vì class đã được load ---");
        new Child();

        System.out.println("\n=== equals/hashCode/toString: quyết định object có được coi là 'bằng nhau' và dùng được trong Set/Map hay không ===");
        Money m1 = new Money(100_000, "VND");
        Money m2 = new Money(100_000, "VND");
        System.out.println("m1 = " + m1 + ", m2 = " + m2);
        System.out.println("m1.equals(m2) = " + m1.equals(m2) + " (so sánh theo giá trị field, không phải địa chỉ)");
        System.out.println("m1 == m2 -> " + (m1 == m2));
        System.out.println("m1.hashCode() == m2.hashCode() -> " + (m1.hashCode() == m2.hashCode()));

        Set<Money> wallet = new HashSet<>();
        wallet.add(m1);
        wallet.add(m2); // nếu equals/hashCode đúng, m2 bị coi là trùng m1 -> không thêm mới
        System.out.println("Số phần tử trong Set sau khi add cả m1, m2: " + wallet.size());

        System.out.println("\n=== try-with-resources: giải phóng tài nguyên xác định ngay lập tức, không phụ thuộc vào GC ===");
        try (FileHandle handle = new FileHandle("data.txt")) {
            handle.use();
        }
        // close() vừa rồi được JVM tự gọi ngay khi thoát khối try, kể cả khi có exception xảy ra bên trong;
        // đây là lý do try-with-resources được dùng thay cho finalize() (đã deprecated, không đảm bảo thời điểm chạy)

        System.out.println("\n=== Vòng đời & GC: object chỉ bị dọn khi 'unreachable', nhưng THỜI ĐIỂM dọn do JVM tự quyết ===");
        Object obj = new Object();
        System.out.println("obj đang được biến 'obj' tham chiếu -> chưa đủ điều kiện bị GC thu hồi");
        obj = null; // bỏ tham chiếu cuối cùng -> object cũ trở thành unreachable, ĐỦ ĐIỀU KIỆN để GC dọn
        System.gc(); // chỉ là lời "gợi ý" cho JVM chạy GC, KHÔNG đảm bảo chạy ngay hay chạy luôn
        System.out.println("Đã bỏ tham chiếu; object cũ có thể bị GC thu hồi bất kỳ lúc nào sau đó, không xác định chính xác khi nào");
    }

    static class Parent {
        static {
            System.out.println("Parent: static block");
        }

        {
            System.out.println("Parent: instance block");
        }

        Parent() {
            System.out.println("Parent: constructor");
        }
    }

    static class Child extends Parent {
        static {
            System.out.println("Child: static block");
        }

        {
            System.out.println("Child: instance block");
        }

        Child() {
            super(); // luôn được gọi (ngầm định nếu không viết) trước bất kỳ dòng nào khác trong constructor này
            System.out.println("Child: constructor");
        }
    }

    static final class Money {
        private final long amount;
        private final String currency;

        Money(long amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Money)) {
                return false;
            }
            Money other = (Money) o;
            return amount == other.amount && currency.equals(other.currency);
        }

        @Override
        public int hashCode() {
            // hashCode PHẢI đồng nhất với equals: 2 object equals() = true thì hashCode() phải bằng nhau,
            // nếu không HashSet/HashMap sẽ coi chúng là 2 phần tử khác nhau dù equals() nói là bằng
            return Objects.hash(amount, currency);
        }

        @Override
        public String toString() {
            return amount + " " + currency;
        }
    }

    static class FileHandle implements AutoCloseable {
        private final String name;

        FileHandle(String name) {
            this.name = name;
            System.out.println("Mở resource: " + name);
        }

        void use() {
            System.out.println("Đang dùng resource: " + name);
        }

        @Override
        public void close() {
            System.out.println("Đóng resource: " + name);
        }
    }
}
