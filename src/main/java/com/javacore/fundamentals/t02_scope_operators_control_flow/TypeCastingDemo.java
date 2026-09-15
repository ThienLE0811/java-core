package com.javacore.fundamentals.t02_scope_operators_control_flow;

public class TypeCastingDemo {

    public static void main(String[] args) {
        // 1. Ép kiểu giữa các primitive: đã xem ở VariablesAndTypesDemo (widening/narrowing)
        // Ở đây tập trung vào ép kiểu giữa các reference type (upcasting/downcasting)

        // Upcasting: chuyển từ kiểu con lên kiểu cha, luôn an toàn, xảy ra ngầm định (implicit)
        Dog dog = new Dog();
        Animal animal = dog; // upcasting ngầm định, không cần ép kiểu tường minh
        animal.makeSound();

        // Downcasting: chuyển từ kiểu cha xuống kiểu con, PHẢI ép kiểu tường minh (explicit)
        // vì trình biên dịch không biết chắc animal có thực sự là Dog hay không
        if (animal instanceof Dog) {
            Dog castedDog = (Dog) animal; // downcasting an toàn vì đã kiểm tra instanceof trước
            castedDog.fetch();
        }

        // Downcasting sai kiểu sẽ ném ClassCastException lúc runtime, dù compile vẫn qua
        Animal cat = new Cat();
        try {
            Dog wrongCast = (Dog) cat; // biên dịch OK vì Cat và Dog cùng kế thừa Animal, nhưng runtime lỗi
            wrongCast.fetch();
        } catch (ClassCastException e) {
            System.out.println("Lỗi ép kiểu sai: " + e.getMessage());
        }

        // Pattern matching cho instanceof (Java 16+): vừa kiểm tra vừa ép kiểu trong một bước
        printIfDog(animal);
        printIfDog(cat);
    }

    private static void printIfDog(Animal animal) {
        if (animal instanceof Dog dog) { // gộp kiểm tra + downcasting, tránh phải ép kiểu thủ công
            System.out.println("Đây là Dog, có thể fetch: ");
            dog.fetch();
        } else {
            System.out.println("Không phải Dog, bỏ qua fetch()");
        }
    }

    // Các class hỗ trợ minh họa upcasting/downcasting
    static class Animal {
        void makeSound() {
            System.out.println("Animal tạo ra một âm thanh nào đó");
        }
    }

    static class Dog extends Animal {
        @Override
        void makeSound() {
            System.out.println("Dog: Woof!");
        }

        void fetch() {
            System.out.println("Dog đang tha bóng");
        }
    }

    static class Cat extends Animal {
        @Override
        void makeSound() {
            System.out.println("Cat: Meow!");
        }
    }
}
