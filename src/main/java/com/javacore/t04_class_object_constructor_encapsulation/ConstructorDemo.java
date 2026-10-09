package com.javacore.t04_class_object_constructor_encapsulation;

public class ConstructorDemo {

    public static void main(String[] args) {
        // Gọi constructor không tham số
        Product p1 = new Product();
        System.out.println("p1: " + p1);

        // Gọi constructor có tham số (constructor overloading: nhiều constructor khác signature)
        Product p2 = new Product("Bàn phím", 250_000);
        System.out.println("p2: " + p2);

        // Constructor này gọi constructor khác qua this(...) để tái sử dụng logic khởi tạo
        Product p3 = new Product("Chuột");
        System.out.println("p3 (giá mặc định vì chưa truyền price): " + p3);

        // Thứ tự chạy khi khởi tạo object: static block (1 lần duy nhất, lúc class được load)
        // -> instance initializer block -> constructor. Tạo object thứ 2 sẽ KHÔNG chạy lại static block.
        System.out.println("--- Tạo object đầu tiên của Order ---");
        new Order();
        System.out.println("--- Tạo object thứ hai của Order ---");
        new Order();

        // Mở rộng với kế thừa: thứ tự chạy đầy đủ khi có Parent/Child là
        // static Parent -> static Child (1 lần duy nhất, lúc class con được load)
        // -> field init + instance block Parent -> constructor Parent
        // -> field init + instance block Child -> constructor Child
        System.out.println("--- Tạo object đầu tiên của Child ---");
        new Child();
        System.out.println("--- Tạo object thứ hai của Child (static block KHÔNG chạy lại) ---");
        new Child();
    }

    static class Product {
        String name;
        long price;

        // Constructor không tham số (no-arg constructor): nếu không viết constructor nào,
        // Java tự cấp 1 constructor mặc định tương tự thế này (rỗng); nhưng viết constructor khác
        // rồi thì Java KHÔNG tự cấp nữa -> phải tự viết nếu vẫn cần no-arg constructor
        Product() {
            this.name = "Chưa đặt tên";
            this.price = 0;
        }

        // Constructor overloading: cùng tên Product, khác số lượng tham số
        Product(String name, long price) {
            this.name = name;
            this.price = price;
        }

        // Constructor chaining: dùng this(...) để gọi constructor khác trong CÙNG class,
        // tránh lặp lại logic khởi tạo. Lệnh this(...) phải là dòng đầu tiên trong constructor.
        Product(String name) {
            this(name, 100_000); // gọi Product(String, long) với price mặc định
        }

        @Override
        public String toString() {
            return "Product{name=" + name + ", price=" + price + "}";
        }
    }

    static class Order {
        // static initializer block: chỉ chạy 1 lần duy nhất khi class được load lần đầu tiên,
        // dùng để khởi tạo static field phức tạp
        static {
            System.out.println("1. Static block chạy (chỉ 1 lần cho cả class)");
        }

        // instance initializer block: chạy mỗi khi tạo object mới, TRƯỚC phần thân constructor,
        // ít dùng trong thực tế (thường đưa logic này vào thẳng constructor) nhưng cần biết để đọc hiểu code
        {
            System.out.println("2. Instance initializer block chạy (mỗi lần tạo object)");
        }

        Order() {
            System.out.println("3. Constructor chạy");
        }
    }

    // Ví dụ đầy đủ thứ tự khởi tạo khi có kế thừa (Child extends Parent)
    static class Parent {
        static {
            System.out.println("1. Static block của Parent (1 lần, khi class được load)");
        }

        // field initializer chạy CÙNG thứ tự với instance block, theo đúng vị trí xuất hiện trong code
        int parentField = initParentField();

        {
            System.out.println("3. Instance block của Parent");
        }

        Parent() {
            System.out.println("4. Constructor của Parent");
        }

        private int initParentField() {
            System.out.println("2. Field initializer của Parent (parentField)");
            return 0;
        }
    }

    static class Child extends Parent {
        static {
            System.out.println("1b. Static block của Child (1 lần, khi class được load)");
        }

        int childField = initChildField();

        {
            System.out.println("6. Instance block của Child");
        }

        Child() {
            // Java tự chèn super() ở đầu constructor nếu không tự gọi -> Parent luôn khởi tạo XONG
            // trước khi Child bắt đầu khởi tạo field/instance block/constructor của chính nó
            System.out.println("7. Constructor của Child");
        }

        private int initChildField() {
            System.out.println("5. Field initializer của Child (childField)");
            return 0;
        }
    }
}
