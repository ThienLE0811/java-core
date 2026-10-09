package com.javacore.t05_inheritance_abstraction_interface_polymorphism;

public class PolymorphismDemo {

    public static void main(String[] args) {
        // Upcasting: gán object con cho biến kiểu cha -> LUÔN an toàn, Java tự làm ngầm định
        Animal a1 = new Dog();
        Animal a2 = new Cat();

        // Runtime polymorphism (dynamic dispatch): cùng gọi a.makeSound(), nhưng JVM chọn đúng bản
        // override dựa vào KIỂU THỰC SỰ của object lúc chạy, không phải kiểu khai báo của biến (Animal)
        Animal[] animals = {a1, a2, new Dog()};
        for (Animal a : animals) {
            a.makeSound(); // in ra khác nhau tùy object thực sự là Dog hay Cat
        }

        System.out.println();
        // Downcasting: từ kiểu cha ép về kiểu con -> KHÔNG an toàn mặc định, phải tự đảm bảo đúng kiểu
        // thực sự, nếu không sẽ ném ClassCastException lúc runtime
        Animal someAnimal = new Dog();
        if (someAnimal instanceof Dog) {
            Dog dog = (Dog) someAnimal; // downcast tường minh, an toàn vì đã kiểm tra instanceof trước
            dog.fetch();
        }

        // Pattern matching cho instanceof (Java 16+): kiểm tra kiểu VÀ gán biến cùng lúc, gọn hơn
        if (someAnimal instanceof Dog dog) {
            dog.fetch();
        }

        // Downcast sai kiểu -> ClassCastException lúc runtime (compiler không phát hiện được lỗi này)
        try {
            Animal catAsAnimal = new Cat();
            Dog wrongCast = (Dog) catAsAnimal; // Cat không phải Dog -> lỗi khi chạy, không phải khi compile
            wrongCast.fetch();
        } catch (ClassCastException e) {
            System.out.println("Bắt được ClassCastException: " + e.getMessage());
        }

        System.out.println();
        // Field KHÔNG có runtime polymorphism (đã minh họa kỹ ở InheritanceDemo.java) -> chỉ method mới có
        // Overloading (compile-time polymorphism, xem thêm MethodsDemo.java t03) khác hẳn overriding:
        // - Overloading: cùng tên, KHÁC signature, chọn method lúc COMPILE TIME dựa vào kiểu tham số truyền vào
        // - Overriding: cùng tên, CÙNG signature, chọn method lúc RUNTIME dựa vào kiểu thực sự của object
        Greeter greeter = new Greeter();
        greeter.greet("An");     // overload theo kiểu tham số String
        greeter.greet(25);       // overload theo kiểu tham số int -> quyết định lúc compile
    }

    static class Animal {
        void makeSound() {
            System.out.println("Animal: ...");
        }
    }

    static class Dog extends Animal {
        @Override
        void makeSound() {
            System.out.println("Dog: Gâu gâu!");
        }

        void fetch() {
            System.out.println("Dog: chạy nhặt bóng");
        }
    }

    static class Cat extends Animal {
        @Override
        void makeSound() {
            System.out.println("Cat: Meo meo!");
        }
    }

    static class Greeter {
        void greet(String name) {
            System.out.println("Xin chào, " + name);
        }

        void greet(int age) {
            System.out.println("Bạn " + age + " tuổi");
        }
    }
}
