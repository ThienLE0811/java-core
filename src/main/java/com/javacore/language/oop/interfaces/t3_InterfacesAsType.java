package com.javacore.language.oop.interfaces;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Using an Interface as a Type
 * <p>
 * 0. Vị trí bài này
 * - Bước 11/41, bài 3/3, khép lại section Interfaces của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Interface định nghĩa một KIỂU DỮ LIỆU THAM CHIẾU (Reference Type),
 * mở ra sức mạnh của tính ĐA HÌNH (Polymorphism) và là nền tảng cho nguyên lý cốt lõi:
 * "Program to an interface, not an implementation" (Lập trình theo giao diện, không phụ
 * thuộc cài đặt cụ thể).
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/oop/interfaces/interfaces-as-a-type/">dev.java - Using an Interface as a Type</a>
 * - LƯU Ý VỀ TRANG GỐC: trang này RẤT NGẮN (3 đoạn văn, ~200 từ, 3 đoạn code) và
 * "Last update: September 14, 2021" nhưng nội dung kế thừa từ Java Tutorials thời
 * TRƯỚC GENERICS (trước Java 5). Vì vậy ví dụ Relatable là GIÁO CỤ để hiểu khái niệm,
 * KHÔNG PHẢI mẫu để bắt chước. Xem mục 4 để biết cách làm hiện đại.
 * - Mọi kết quả dán trong ghi chú này đều biên dịch và chạy thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - INTERFACES AS A TYPE (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. INTERFACE LÀ MỘT REFERENCE DATA TYPE
 * - "When you define a new interface, you are defining a new reference data type.
 * You can use interface names anywhere you can use any other data type name.
 * If you define a reference variable whose type is an interface, any object you assign
 * to it must be an instance of a class that implements the interface."
 * - Dùng tên interface làm: kiểu biến, kiểu tham số, kiểu trả về, kiểu phần tử collection.
 * List<String> list = new ArrayList<>();
 * - HỆ QUẢ QUAN TRỌNG NHẤT (trang gốc không nói thẳng): biến kiểu interface chỉ "NHÌN THẤY"
 * các method của interface, không thấy method riêng của class:
 * Relatable r = new RectanglePlus(10, 20);
 * r.isLargerThan(other); // OK
 * r.getArea(); // LỖI BIÊN DỊCH, dù object thật sự có method này
 * Người mới tưởng đây là hạn chế. Thực ra đây là TOÀN BỘ MỤC ĐÍCH: tự trói tay mình
 * để code không thể lén phụ thuộc vào chi tiết cài đặt.
 * <p>
 * 2. VÍ DỤ CHUẨN DEV.JAVA: INTERFACE RELATABLE
 * - Bản gốc trên trang nhận tham số Object rồi ÉP KIỂU BẰNG TAY:
 * public Object findLargest(Object object1, Object object2) {
 * Relatable obj1 = (Relatable) object1; // nổ ClassCastException nếu sai kiểu
 * Relatable obj2 = (Relatable) object2;
 * if ((obj1).isLargerThan(obj2) > 0) return object1; else return object2;
 * }
 * - "By casting object1 to a Relatable type, it can invoke the isLargerThan() method."
 * - GIỚI HẠN ĐƯỢC CHÍNH ORACLE THỪA NHẬN trong trang (câu dễ bị đọc lướt nhất):
 * "...can be compared with the findLargest() method - PROVIDED THAT BOTH OBJECTS
 * ARE OF THE SAME CLASS."
 * => Relatable KHÔNG so sánh được chéo kiểu. Hình chữ nhật không so được với hình tròn.
 * Lý do: isLargerThan(Relatable other) nhận MỌI Relatable, nên bên trong buộc phải
 * instanceof + cast, và không có cách nào xử lý đúng khi gặp kiểu lạ.
 * - Lợi ích mà trang muốn nhấn mạnh:
 * "These methods work for any 'relatable' objects, no matter what their class inheritance
 * is... This gives them SOME OF THE ADVANTAGES OF MULTIPLE INHERITANCE, where they can
 * have behavior from both a superclass and an interface."
 * => Một class chỉ extends được MỘT class, nhưng có thể LÀ nhiều kiểu cùng lúc.
 * Ví dụ String đồng thời là CharSequence, Comparable<String>, Serializable.
 * <p>
 * 3. ÉP KIỂU (CASTING), INSTANCEOF VÀ MỘT CÁI BẪY
 * - Pattern Matching for instanceof (Java 16+) gộp kiểm tra và ép kiểu làm một:
 * if (obj instanceof RectanglePlus r) { ... } // r dùng được ngay, không cần cast
 * - Ép kiểu sai sẽ quăng ClassCastException lúc CHẠY, compiler không chặn được.
 * - BẪY CHẾT NGƯỜI: khi isLargerThan() gặp kiểu lạ mà trả về 0 ("coi như bằng nhau"),
 * chương trình cho ĐÁP ÁN SAI TRONG IM LẶNG - không exception, không log. Đây là loại
 * lỗi nguy hiểm nhất trong production. Luôn ném exception thay vì nuốt lỗi (xem mục 5
 * trong main() để thấy kết quả chạy thật).
 * - TRANG GỐC CÓ MỘT BUG THẬT, đáng ghi nhớ như lời nhắc "tài liệu chính thức cũng sai":
 * public boolean isEqual(Relatable object1, Relatable object2) {
 * if ((object1).isLargerThan(object1) == 0) return true; // so object1 với CHÍNH NÓ
 * else return false;
 * }
 * => Luôn trả về true, tham số object2 không hề được dùng. Luôn tự chạy thử, đừng tin mắt.
 * <p>
 * ======================================================================
 * NỘI DUNG MỞ RỘNG THỰC TẾ (THEO PACKAGE-INFO)
 * ======================================================================
 * <p>
 * 4. TỪ RELATABLE (TIỀN GENERICS) ĐẾN COMPARABLE<T> VÀ COMPARATOR<T>
 * - Gốc rễ vấn đề của Relatable: tham số là chính interface đó, nên "mở cửa cho mọi kiểu"
 * rồi phải cầu may bằng instanceof.
 * - Java 5 giải quyết triệt để bằng cách đẩy KIỂU vào trong interface:
 * public interface Comparable<T> { int compareTo(T o); } // T, không phải Comparable
 * class RectangleModern implements Comparable<RectangleModern> {
 * public int compareTo(RectangleModern o) { ... } // không cast, không instanceof
 * }
 * => Compiler chặn ngay lúc viết. Không còn nhánh "kiểu lạ" để nuốt lỗi.
 * - PHÂN BIỆT (câu hỏi phỏng vấn Java gần như chắc chắn gặp):
 * + Comparable<T>: thứ tự TỰ NHIÊN, gắn liền vào chính class (chỉ có MỘT cách sắp xếp).
 * Dùng khi class có một thứ tự mặc định hiển nhiên (String theo alphabet, Integer theo giá trị).
 * + Comparator<T>: thứ tự BÊN NGOÀI, tách rời khỏi class (có BAO NHIÊU cách sắp xếp tùy ý).
 * Dùng khi cần nhiều tiêu chí, hoặc khi KHÔNG SỬA ĐƯỢC class (thư viện bên thứ ba).
 * Comparator.comparingDouble(Shape::getArea).reversed()
 * + Comparator còn giải được bài toán SO SÁNH CHÉO KIỂU mà Relatable bó tay: chỉ cần
 * các class cùng implement một interface cung cấp dữ liệu (ví dụ Shape.getArea()).
 * <p>
 * 5. KHI NÀO DÙNG INTERFACE, KHI NÀO DÙNG ABSTRACT CLASS?
 * - DÙNG INTERFACE KHI:
 * + Muốn định nghĩa HÀNH VI (behavior) cho các lớp KHÔNG LIÊN QUAN HỌ HÀNG với nhau
 * (ví dụ: Comparable được implement bởi cả String, Integer, Date, File...).
 * + Cần tận dụng ĐA KẾ THỪA KIỂU (Multiple inheritance of type).
 * + Chỉ quan tâm "làm cái gì", không cần quản lý trạng thái biến của đối tượng.
 * - DÙNG ABSTRACT CLASS KHI:
 * + Cần CHIA SẺ TRẠNG THÁI (instance fields non-static như createdAt, id, username).
 * + Các lớp có HỌ HÀNG GẦN GŨI (Is-A relationship) và có chung Constructor.
 * + Muốn kiểm soát quyền truy cập protected nội bộ trong họ hàng.
 * + Cần áp dụng mẫu thiết kế Template Method (khóa khung sườn thuật toán bằng final method).
 * - LỜI KHUYÊN VÀNG: "Mặc định hãy chọn Interface. Chỉ dùng Abstract Class khi bắt buộc
 * phải chia sẻ trạng thái hoặc constructor."
 * <p>
 * 6. FUNCTIONAL INTERFACE (@FunctionalInterface)
 * - Là interface chỉ có DUY NHẤT MỘT abstract method (Single Abstract Method - SAM).
 * - Có thể có thêm nhiều default method hoặc static method tùy ý.
 * - Nền tảng của biểu thức Lambda và Method Reference trong Java:
 * Validator<String> notEmpty = s -> s != null && !s.isBlank();
 * - Annotation @FunctionalInterface giúp compiler kiểm tra nếu vô tình thêm method
 * abstract thứ 2 sẽ báo lỗi biên dịch ngay.
 * <p>
 * 7. MARKER INTERFACE (TAGGING INTERFACE)
 * - Là interface HOÀN TOÀN RỖNG (không có method, không có field nào).
 * - Mục đích: "Gắn nhãn" cho lớp để JVM hoặc framework nhận biết một tính chất đặc biệt.
 * - Các marker interface nổi tiếng trong JDK:
 * + java.io.Serializable: Báo hiệu object này được phép chuyển đổi thành luồng byte để lưu file/gửi mạng.
 * + java.lang.Cloneable: Báo hiệu object cho phép gọi method clone() mà không ném CloneNotSupportedException.
 * + java.util.RandomAccess: Báo hiệu collection (như ArrayList) hỗ trợ truy xuất theo chỉ số O(1) cực nhanh.
 * - Ngày nay, trong các framework hiện đại, người ta thường dùng Annotation thay cho Marker Interface
 * khi không cần ép kiểu reference.
 */
public class t3_InterfacesAsType {

    /**
     * 1. Interface Relatable từ ví dụ chuẩn của Oracle dev.java.
     * Tham số là chính Relatable -> nhận mọi kiểu -> buộc phải instanceof bên trong.
     * Đây chính là gốc rễ của giới hạn "both objects must be of the same class".
     */
    public interface Relatable {
        // Trả về: >0 nếu this lớn hơn other; <0 nếu this nhỏ hơn other; 0 nếu bằng nhau
        int isLargerThan(Relatable other);
    }

    /**
     * Thuật toán tìm đối tượng lớn hơn, hoạt động với BẤT KỲ đối tượng nào miễn là
     * nó có kiểu Relatable (tính đa hình). Viết một lần, dùng được với cả những class
     * chưa tồn tại tại thời điểm viết -> Open/Closed Principle.
     * <p>
     * Lưu ý: bản gốc trên trang dev.java nhận Object rồi cast tay. Bản dưới đây nhận
     * thẳng Relatable nên an toàn hơn (compiler chặn sai kiểu ngay).
     */
    public static Relatable findLargest(Relatable object1, Relatable object2) {
        if (object1.isLargerThan(object2) > 0) {
            return object1;
        } else {
            return object2;
        }
    }

    /**
     * Bản SAO Y NGUYÊN trang gốc, giữ lại để thấy rủi ro của việc nhận Object + cast tay.
     * Truyền vào object không implement Relatable -> ClassCastException lúc CHẠY.
     */
    public static Object findLargestLegacy(Object object1, Object object2) {
        Relatable obj1 = (Relatable) object1;
        Relatable obj2 = (Relatable) object2;
        if (obj1.isLargerThan(obj2) > 0) {
            return object1;
        } else {
            return object2;
        }
    }

    /**
     * Lớp RectanglePlus triển khai Relatable dựa trên diện tích hình chữ nhật
     */
    public static class RectanglePlus implements Relatable {
        public int width = 0;
        public int height = 0;

        public RectanglePlus(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getArea() {
            return width * height;
        }

        @Override
        public int isLargerThan(Relatable other) {
            if (other instanceof RectanglePlus otherRect) {
                return Integer.compare(this.getArea(), otherRect.getArea());
            }
            // KHÔNG trả 0 ở đây! Trả 0 nghĩa là "bằng nhau" -> cho đáp án SAI trong im lặng.
            // Ném exception để lỗi kêu to ngay tại chỗ gây ra nó.
            throw new IllegalArgumentException(
                    "RectanglePlus chỉ so sánh được với RectanglePlus, nhận được: "
                            + other.getClass().getSimpleName());
        }

        @Override
        public String toString() {
            return "RectanglePlus[" + width + "x" + height + ", S=" + getArea() + "]";
        }
    }

    /**
     * Lớp CirclePlus triển khai Relatable dựa trên diện tích hình tròn
     */
    public static class CirclePlus implements Relatable {
        public double radius;

        public CirclePlus(double radius) {
            this.radius = radius;
        }

        public double getArea() {
            return Math.PI * radius * radius;
        }

        @Override
        public int isLargerThan(Relatable other) {
            if (other instanceof CirclePlus otherCircle) {
                return Double.compare(this.getArea(), otherCircle.getArea());
            }
            throw new IllegalArgumentException(
                    "CirclePlus chỉ so sánh được với CirclePlus, nhận được: "
                            + other.getClass().getSimpleName());
        }

        @Override
        public String toString() {
            return "CirclePlus[r=" + radius + ", S=" + String.format("%.2f", getArea()) + "]";
        }
    }

    /**
     * 2. CÁCH LÀM HIỆN ĐẠI - Comparable<T>: kiểu nằm NGAY TRONG interface.
     * So sánh với Relatable ở trên: không cast, không instanceof, không có nhánh "kiểu lạ".
     */
    public static class RectangleModern implements Comparable<RectangleModern> {
        private final int width;
        private final int height;

        public RectangleModern(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getArea() {
            return width * height;
        }

        @Override
        public int compareTo(RectangleModern other) {
            return Integer.compare(this.getArea(), other.getArea());
        }

        @Override
        public String toString() {
            return "RectangleModern[" + width + "x" + height + ", S=" + getArea() + "]";
        }
    }

    /**
     * 3. Interface cung cấp dữ liệu để Comparator so sánh CHÉO KIỂU - thứ Relatable bó tay.
     */
    public interface Shape {
        double getArea();
    }

    public record Square(double side) implements Shape {
        @Override
        public double getArea() {
            return side * side;
        }
    }

    public record Disk(double radius) implements Shape {
        @Override
        public double getArea() {
            return Math.PI * radius * radius;
        }
    }

    /**
     * 4. Minh họa Functional Interface: Chỉ có 1 method abstract duy nhất
     */
    @FunctionalInterface
    public interface Validator<T> {
        boolean isValid(T target);

        // Có thể có default method mà vẫn là Functional Interface
        default Validator<T> and(Validator<T> other) {
            return target -> this.isValid(target) && other.isValid(target);
        }
    }

    /**
     * 5. Minh họa Marker Interface (không có bất kỳ method nào)
     */
    public interface Auditable {
        // Marker rỗng để gắn nhãn
    }

    public static class SensitiveInvoice implements Auditable {
        public double amount;

        public SensitiveInvoice(double amount) {
            this.amount = amount;
        }
    }

    public static void auditSecurity(Object obj) {
        if (obj instanceof Auditable) {
            IO.println("-> [AUDIT LOG]: Phát hiện đối tượng nhạy cảm cần ghi nhật ký kiểm toán: " + obj.getClass().getSimpleName());
        } else {
            IO.println("-> Đối tượng thông thường, bỏ qua kiểm toán.");
        }
    }

    void main() {
        IO.println("=== 1. ĐA HÌNH VỚI INTERFACE (VÍ DỤ RELATABLE CỦA DEV.JAVA) ===");
        RectanglePlus r1 = new RectanglePlus(10, 20); // Diện tích 200
        RectanglePlus r2 = new RectanglePlus(15, 15); // Diện tích 225

        // Gán vào biến kiểu Interface (Lập trình theo giao diện)
        Relatable obj1 = r1;
        Relatable obj2 = r2;

        Relatable winner = findLargest(obj1, obj2);
        IO.println("Hình chữ nhật lớn hơn là: " + winner);

        CirclePlus c1 = new CirclePlus(5.0); // Diện tích ~78.5
        CirclePlus c2 = new CirclePlus(7.0); // Diện tích ~153.9
        IO.println("Hình tròn lớn hơn là: " + findLargest(c1, c2));

        // Biến kiểu interface chỉ "nhìn thấy" method của interface:
        // obj1.getArea();  // LỖI BIÊN DỊCH - bỏ comment để tự kiểm chứng
        IO.println("Muốn gọi getArea() phải ép kiểu: " + ((RectanglePlus) obj1).getArea());

        IO.println("\n=== 2. GIỚI HẠN CỦA RELATABLE: \"BOTH OBJECTS OF THE SAME CLASS\" ===");
        try {
            findLargest(r1, c1); // Hình chữ nhật so với hình tròn
        } catch (IllegalArgumentException e) {
            IO.println("-> Bị chặn đúng lúc: " + e.getMessage());
        }
        IO.println("   (Nếu nhánh kiểu lạ trả về 0 thay vì ném exception, chương trình sẽ");
        IO.println("    âm thầm báo hình tròn S=78.54 LỚN HƠN hình chữ nhật S=200 mà không ai biết.)");

        IO.println("\n=== 3. BẢN GỐC DÙNG Object + CAST TAY: LỖI CHỈ LỘ RA LÚC CHẠY ===");
        IO.println("findLargestLegacy(r1, r2) = " + findLargestLegacy(r1, r2));
        try {
            findLargestLegacy("tôi không phải Relatable", r1);
        } catch (ClassCastException e) {
            IO.println("-> ClassCastException: compiler không hề cảnh báo trước!");
        }

        IO.println("\n=== 4. PATTERN MATCHING FOR INSTANCEOF VỚI INTERFACE ===");
        List<Relatable> shapes = List.of(r1, c1, r2, c2);
        for (Relatable shape : shapes) {
            if (shape instanceof RectanglePlus rect) {
                IO.println("-> Là Hình chữ nhật: w=" + rect.width + ", h=" + rect.height);
            } else if (shape instanceof CirclePlus circle) {
                IO.println("-> Là Hình tròn: r=" + circle.radius);
            }
        }

        IO.println("\n=== 5. CÁCH LÀM HIỆN ĐẠI: Comparable<T> (KHÔNG CAST, KHÔNG INSTANCEOF) ===");
        List<RectangleModern> rects = new ArrayList<>(List.of(
                new RectangleModern(10, 20),
                new RectangleModern(3, 4),
                new RectangleModern(15, 15)));
        rects.sort(Comparator.naturalOrder()); // thứ tự tự nhiên do compareTo định nghĩa
        IO.println("Sắp xếp theo thứ tự tự nhiên (Comparable): " + rects);
        // new RectangleModern(1, 1).compareTo(c1);  // LỖI BIÊN DỊCH - compiler chặn ngay

        IO.println("\n=== 6. Comparator<T>: SO SÁNH CHÉO KIỂU - THỨ RELATABLE BÓ TAY ===");
        List<Shape> mixed = new ArrayList<>(List.of(
                new Square(10),      // S = 100
                new Disk(5),         // S ~ 78.54
                new Square(3),       // S = 9
                new Disk(7)));       // S ~ 153.94
        mixed.sort(Comparator.comparingDouble(Shape::getArea).reversed());
        IO.println("Trộn vuông và tròn, sắp theo diện tích giảm dần:");
        for (Shape s : mixed) {
            IO.println(String.format("   %-18s S=%.2f", s, s.getArea()));
        }

        IO.println("\n=== 7. FUNCTIONAL INTERFACE VỚI BIỂU THỨC LAMBDA ===");
        Validator<String> notNull = s -> s != null;
        Validator<String> notEmpty = s -> !s.trim().isEmpty();
        Validator<String> validEmail = notNull.and(notEmpty).and(s -> s.contains("@"));

        String emailTest = "developer@dev.java";
        IO.println("Email '" + emailTest + "' hợp lệ? " + validEmail.isValid(emailTest));
        IO.println("Email '' hợp lệ? " + validEmail.isValid(""));

        IO.println("\n=== 8. MARKER INTERFACE TRONG THỰC TẾ ===");
        auditSecurity(new SensitiveInvoice(50000.0));
        auditSecurity("Chuỗi thông thường");

        // TODO 1 (Tái hiện bug của tài liệu gốc): Viết lại method isEqual y hệt trang dev.java
        //   (so object1 với CHÍNH NÓ) rồi chạy thử với 2 hình khác diện tích. Quan sát nó luôn
        //   trả về true. Sau đó sửa lại cho đúng. Bài học: tài liệu chính thức cũng có lỗi.
        //
        // TODO 2 (Comparable vs Comparator): Thêm nhiều tiêu chí sắp xếp cho RectangleModern
        //   (theo width, theo chu vi, theo diện tích giảm dần). Trả lời: vì sao Comparable
        //   không làm được việc này còn Comparator thì được?
        //
        // TODO 3 (Functional Interface): Thử thêm một abstract method thứ hai vào Validator<T>
        //   (ví dụ 'void printError();') và quan sát thông báo lỗi của @FunctionalInterface.
    }
}
