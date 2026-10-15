package com.javacore.language.oop.interfaces;

/**
 * Implementing an Interface
 * <p>
 * 0. Vị trí bài này
 * - Bước 11/41, bài 2/3 trong section Interfaces của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Java cấm đa kế thừa class để tránh rắc rối về trạng thái (state),
 * nhưng cho phép một class triển khai (implements) KHÔNG GIỚI HẠN số lượng interface;
 * và khi các default method bị xung đột, Java có đúng 3 quy tắc phân xử rõ ràng.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/oop/interfaces/examples/">dev.java - Implementing an Interface</a>
 * - Mọi kết quả dán trong ghi chú này đều biên dịch và chạy thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - IMPLEMENTING AN INTERFACE (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. CÚ PHÁP VÀ QUY TẮC CÀI ĐẶT
 * - Cú pháp:
 * public class MyClass extends SuperClass implements InterfaceA, InterfaceB { ... }
 * + Nếu có extends, từ khóa "extends" BẮT BUỘC phải đứng trước "implements".
 * - QUY TẮC MODIFIER SỐNG CÒN:
 * Mọi method trong interface ngầm định là "public". Khi class con ghi đè (@Override),
 * BẮT BUỘC phải khai báo là "public". Nếu quên từ khóa public (trở thành package-private),
 * javac sẽ báo lỗi ngay:
 * error: attempting to assign weaker access privileges; was public
 * (Quy tắc Liskov: class con không được phép thu hẹp quyền truy cập).
 * <p>
 * 2. ĐA TRIỂN KHAI (MULTIPLE INTERFACE IMPLEMENTATION)
 * - Một class chỉ có duy nhất một cha trực tiếp (Single Inheritance of Class), nhưng
 * có thể đảm nhận nhiều vai trò (Multiple Inheritance of Type) bằng cách implements
 * nhiều interface ngăn cách bởi dấu phẩy:
 * class Duck extends Bird implements Flyable, Swimmable { ... }
 * - Do interface trước Java 8 không chứa trạng thái (fields non-static) và không chứa mã
 * thực thi, việc đa kế thừa kiểu hoàn toàn không gặp phải bài toán "Deadly Diamond of Death".
 * <p>
 * 3. XUNG ĐỘT DEFAULT METHOD VÀ 3 QUY TẮC PHÂN XỬ (CONFLICT RESOLUTION)
 * Khi Java 8 cho phép interface có default method, bài toán xung đột method xuất hiện.
 * Java xử lý bằng 3 quy tắc theo thứ tự ưu tiên:
 * <p>
 * - QUY TẮC 1: CLASS LUÔN THẮNG (Classes Win)
 * "Instance methods are preferred over interface default methods."
 * Nếu một class kế thừa một method từ Superclass và đồng thời implement một interface
 * có default method trùng tên và cùng chữ ký, phiên bản của SUPERCLASS LUÔN ĐƯỢC CHỌN.
 * Default method của interface bị bỏ qua hoàn toàn.
 * <p>
 * - QUY TẮC 2: INTERFACE CON THẮNG (Sub-interfaces Win)
 * "Methods that are already overridden by other candidates are ignored."
 * Nếu Interface B extends Interface A, và cả hai đều có default method `hello()`, thì
 * khi class C implements B (hoặc cả A và B), phiên bản của B (cụ thể hơn) sẽ thắng.
 * <p>
 * - QUY TẮC 3: XUNG ĐỘT NGANG HÀNG PHẢI PHÂN XỬ RÕ RÀNG (Explicit Disambiguation)
 * Nếu 2 interface độc lập (không họ hàng) cùng có default method trùng chữ ký:
 * interface Left { default void print() { ... } }
 * interface Right { default void print() { ... } }
 * Compiler KHÔNG TỰ ĐOÁN và sẽ báo lỗi biên dịch:
 * error: class C inherits unrelated defaults for print() from types Left and Right
 * Cách giải quyết: Lập trình viên BẮT BUỘC phải @Override method đó trong class con,
 * và nếu muốn tái sử dụng code của interface nào, dùng cú pháp:
 * InterfaceName.super.methodName();
 * Ví dụ: Left.super.print(); hoặc Right.super.print(); hoặc gọi cả hai!
 * <p>
 * 4. XUNG ĐỘT GIỮA DEFAULT METHOD VÀ ABSTRACT METHOD
 * - Nếu Interface A có default `void run()`, còn Interface B có abstract `void run()`,
 * class C implements cả A và B thì bản default KHÔNG tự động thắng. Class C vẫn BẮT BUỘC
 * phải @Override method `run()` để thỏa mãn tính trừu tượng của Interface B.
 * <p>
 * 5. TIẾN HÓA INTERFACE (EVOLVING INTERFACES)
 * - Khi phát hành thư viện cho bên thứ ba dùng, muốn bổ sung thêm chức năng:
 * + Thêm abstract method: Gãy toàn bộ code của khách hàng khi họ nâng cấp thư viện.
 * + Thêm default method: An toàn 100%, khách hàng có thể dùng ngay mà không cần sửa code cũ.
 * <p>
 * ======================================================================
 * GÓC NHÌN PRODUCTION & BEST PRACTICES
 * ======================================================================
 * <p>
 * 6. NGUYÊN LÝ PHÂN TÁCH INTERFACE (INTERFACE SEGREGATION PRINCIPLE - ISP)
 * - "Clients should not be forced to depend upon interfaces that they do not use."
 * - Thay vì tạo một interface khổng lồ với 20 methods, hãy chia thành nhiều interface nhỏ
 * (2-3 methods mỗi cái). Class nào cần chức năng nào thì implements interface đó.
 * Ví dụ chuẩn trong Java SDK: Readable, Appendable, Closeable, AutoCloseable, Flushable.
 */
public class t2_ImplementingInterfaces {

    // --- CÁC INTERFACE MÔ TẢ HÀNH VI ---

    public interface Flyable {
        void fly();

        String PREFIX = "[APP]";

        default void cruise() {
            IO.println("Flyable: Đang bay ở độ cao ổn định.");
        }
    }

    public interface Swimmable {
        void swim();

        default void dive() {
            IO.println("Swimmable: Đang lặn dưới nước.");
        }
    }

    /**
     * Minh họa Đa triển khai: Duck vừa bay được, vừa bơi được
     */
    public static class Duck implements Flyable, Swimmable {
        private final String name;

        public Duck(String name) {
            this.name = name;
        }

        // BẮT BUỘC phải có public (không được bỏ trống thành package-private)
        @Override
        public void fly() {
            IO.println(name + " đang vỗ cánh bay.");
        }

        @Override
        public void swim() {
            IO.println(name + " đang bơi trên mặt hồ.");
        }
    }

    // --- MINH HỌA 3 QUY TẮC XỬ LÝ XUNG ĐỘT DEFAULT METHOD ---

    // 1. Quy tắc 1: Class thắng Interface
    public static class ParentGreeting {
        public void greet() {
            IO.println("ParentGreeting: Xin chào từ CLASS CHA!");
        }
    }

    public interface InterfaceGreeting {
        default void greet() {
            IO.println("InterfaceGreeting: Xin chào từ INTERFACE DEFAULT!");
        }
    }

    public static class ChildClassWins extends ParentGreeting implements InterfaceGreeting {
        // Không cần override greet(), class cha tự động thắng interface!
    }

    // 2. Quy tắc 2: Interface con thắng Interface cha
    public interface BaseNotifier {
        default void notifyUser(String msg) {
            IO.println("BaseNotifier: " + msg);
        }
    }

    public interface AdvancedNotifier extends BaseNotifier {
        @Override
        default void notifyUser(String msg) {
            IO.println("AdvancedNotifier (ghi đè cụ thể hơn): [QUAN TRỌNG] " + msg);
        }
    }

    public static class SystemAlert implements AdvancedNotifier {
        // Tự động dùng bản của AdvancedNotifier
    }

    // 3. Quy tắc 3: Hai interface ngang hàng độc lập xung đột default method
    public interface LeftUI {
        default void render() {
            IO.println("LeftUI: Render layout căn lề TRÁI.");
        }
    }

    public interface RightUI {
        default void render() {
            IO.println("RightUI: Render layout căn lề PHẢI.");
        }
    }

    public static class SplitLayout implements LeftUI, RightUI {
        // BẮT BUỘC PHẢI @Override vì LeftUI và RightUI xung đột method render()
        @Override
        public void render() {
            IO.println("SplitLayout: Tự xử lý xung đột bằng cách kết hợp cả hai:");
            IO.print("   -> ");
            LeftUI.super.render();   // Chỉ định gọi bản của LeftUI
            IO.print("   -> ");
            RightUI.super.render();  // Chỉ định gọi bản của RightUI
        }
    }

    void main() {
        IO.println("=== 1. ĐA TRIỂN KHAI (MULTIPLE INTERFACES) ===");
        Duck duck = new Duck("Vịt Donald");
        duck.fly();
        duck.cruise();
        duck.swim();
        duck.dive();

        IO.println("\n=== 2. QUY TẮC 1: CLASS LUÔN THẮNG DEFAULT CỦA INTERFACE ===");
        ChildClassWins c1 = new ChildClassWins();
        c1.greet(); // Kết quả: ParentGreeting (Class thắng)

        IO.println("\n=== 3. QUY TẮC 2: INTERFACE CON THẮNG INTERFACE CHA ===");
        SystemAlert alert = new SystemAlert();
        alert.notifyUser("Ổ đĩa sắp đầy!");

        IO.println("\n=== 4. QUY TẮC 3: GIẢI QUYẾT XUNG ĐỘT NGANG HÀNG (InterfaceName.super) ===");
        SplitLayout layout = new SplitLayout();
        layout.render();

        // TODO 1 (Kiểm chứng Modifier): Hãy thử xóa từ khóa 'public' ở method 'fly()' trong Duck
        //   rồi chạy 'mvn test-compile'. Đọc thông báo lỗi về access privileges của compiler.
        //
        // TODO 2 (Kiểm chứng Xung đột): Trong SplitLayout, thử comment method render() lại.
        //   Xem compiler báo lỗi "inherits unrelated defaults" như thế nào.
    }
}
