package com.javacore.t04_class_object_constructor_encapsulation;

public class ClassAndObjectDemo {

    public static void main(String[] args) {
        // Class là bản thiết kế (blueprint), object là thực thể được tạo ra từ class đó bằng "new"
        Student s1 = new Student();
        s1.name = "An";
        s1.age = 20;

        Student s2 = new Student();
        s2.name = "Bình";
        s2.age = 22;

        // Mỗi object có vùng nhớ field riêng -> đổi s1 không ảnh hưởng s2
        System.out.println(s1.introduce());
        System.out.println(s2.introduce());

        // Biến s1, s2 chỉ là reference trỏ tới object trên heap (giống biến object trong JS)
        Student s3 = s1;
        s3.name = "An (đã đổi qua s3)";
        System.out.println("s1.name cũng đổi theo vì s1 và s3 cùng trỏ 1 object: " + s1.name);

        // static field: thuộc về class, dùng chung cho MỌI object, không tách riêng theo từng instance
        System.out.println("Tổng số student đã tạo: " + Student.totalCreated);

        // static method: gọi qua tên class, không cần object
        Student.printTotal();

        // "this" trong method dùng để trỏ tới chính object đang gọi method đó
        s1.setAge(21); // bên trong setAge dùng "this.age" để phân biệt field với tham số cùng tên
        System.out.println("s1 sau khi setAge: " + s1.introduce());
    }

    // Định nghĩa class Student: gồm field (trạng thái) và method (hành vi)
    static class Student {
        // static field: chia sẻ chung cho tất cả object, tăng mỗi khi có object mới được tạo
        static int totalCreated = 0;

        // instance field: mỗi object có bản sao riêng
        String name;
        int age;

        // Constructor mặc định (implicit) sẽ tự chạy nếu không viết constructor nào khác;
        // ở đây ta khai báo tường minh để tăng bộ đếm totalCreated mỗi khi tạo object mới
        Student() {
            totalCreated++;
        }

        // instance method: cần có object mới gọi được, bên trong truy cập trực tiếp field của object đó
        String introduce() {
            return "Student{name=" + name + ", age=" + age + "}";
        }

        void setAge(int age) {
            // "age" (tham số) đang che (shadow) "this.age" (field) vì trùng tên
            // -> phải dùng từ khóa "this" để chỉ rõ đang gán vào field của object
            this.age = age;
        }

        static void printTotal() {
            System.out.println("Student.totalCreated = " + totalCreated);
        }
    }
}
