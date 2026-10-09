package com.javacore.t05_inheritance_abstraction_interface_polymorphism;

public class InheritanceDemo {

    public static void main(String[] args) {
        Dog dog = new Dog("Lu", 2);
        // super(name, age) trong constructor Dog đã gọi lên Animal để khởi tạo phần chung
        System.out.println(dog.introduce());
        dog.makeSound();       // Dog override -> chạy bản của Dog
        dog.breathe();         // Dog không override -> dùng thẳng bản của Animal
        dog.fetch();           // method riêng, chỉ Dog mới có

        System.out.println();
        Cat cat = new Cat("Mèo mun", 1);
        cat.makeSound();       // Cat override, có gọi thêm super.makeSound() bên trong

        System.out.println();
        // Field KHÔNG có tính đa hình (polymorphism) như method -> field được resolve theo KIỂU KHAI BÁO,
        // không phải kiểu thực sự của object lúc runtime (khác hẳn với method overriding bên dưới)
        Animal ref = new Dog("Lu", 2);
        System.out.println("ref.label (kiểu khai báo Animal) = " + ref.label); // lấy field của Animal
        Dog dogRef = (Dog) ref;
        System.out.println("dogRef.label (kiểu khai báo Dog) = " + dogRef.label); // field cùng tên bị "che" (hide), không phải override

        // final method: Animal.breathe() được khai báo final -> class con KHÔNG override được
        // (thử override sẽ lỗi compile, xem comment trong Animal)

        // final class: nếu Cat được khai báo "final class Cat", sẽ không class nào extends Cat được nữa
    }

    static class Animal {
        protected String name; // protected: class con (kể cả khác package) truy cập được, ngoài thì không
        protected int age;
        String label = "Animal"; // field này sẽ bị "hide" (không phải override) khi Dog khai báo field cùng tên

        Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // method có thể override: class con định nghĩa lại hành vi
        void makeSound() {
            System.out.println(name + " kêu: ...");
        }

        // final method: KHÔNG cho class con override, dùng khi muốn đảm bảo hành vi cố định xuyên suốt
        final void breathe() {
            System.out.println(name + " đang thở");
        }

        String introduce() {
            return "Animal{name=" + name + ", age=" + age + "}";
        }
    }

    // "extends" -> Dog kế thừa toàn bộ field/method non-private của Animal, và có thể thêm/ghi đè
    static class Dog extends Animal {
        String label = "Dog"; // field hiding: KHÔNG liên quan gì tới label của Animal, chỉ là field trùng tên

        Dog(String name, int age) {
            // super(...) gọi constructor của lớp cha, PHẢI là dòng đầu tiên trong constructor
            // (nếu không viết, Java tự chèn super() không tham số -> lỗi compile nếu Animal không có no-arg constructor)
            super(name, age);
        }

        // Override: cùng tên, cùng tham số, cùng (hoặc covariant) kiểu trả về với method của Animal
        @Override
        void makeSound() {
            System.out.println(name + " sủa: Gâu gâu!");
        }

        void fetch() {
            System.out.println(name + " chạy nhặt bóng");
        }

        // Thử bỏ comment dòng dưới sẽ lỗi compile vì breathe() ở Animal là final:
        // @Override
        // void breathe() { System.out.println("khác"); }
    }

    static class Cat extends Animal {
        Cat(String name, int age) {
            super(name, age);
        }

        @Override
        void makeSound() {
            // super.makeSound(): gọi lại bản implementation của lớp cha trước khi thêm hành vi riêng
            super.makeSound();
            System.out.println(name + " kêu thêm: Meo meo!");
        }
    }
}
