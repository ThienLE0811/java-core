package com.javacore.language.annotations_exceptions.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Annotations
 * <p>
 * 0. Vị trí bài này
 * - Bước 17/41, section Annotations của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Annotation là siêu dữ liệu (metadata) gắn vào mã nguồn, không trực tiếp làm thay đổi luồng thực thi nhưng cung cấp dữ liệu cho compiler, công cụ sinh mã và runtime reflection.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/annotations-exceptions/annotations/">dev.java - Annotations</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bên dưới biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - ANNOTATIONS (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. KHÁI NIỆM & VAI TRÒ
 * - "Annotations is a form of metadata, provide data about a program that is not part of the program itself."
 * - "Annotations have no direct effect on the operation of the code they annotate."
 * - Ba công dụng chính:
 *   + Information for the compiler: phát hiện lỗi compile (như {@code @Override}) hoặc dập cảnh báo (như {@code @SuppressWarnings}).
 *   + Compile-time and deployment-time processing: công cụ phần mềm xử lý annotation để tự động sinh code, file XML, tài liệu (như Lombok, Dagger, MapStruct).
 *   + Runtime processing: một số annotation được giữ lại đến lúc chạy và được đọc thông qua Reflection API (như Spring, Hibernate, JUnit).
 * <p>
 * 2. CÚ PHÁP ĐỊNH DẠNG (THE FORMAT OF AN ANNOTATION)
 * - Ký tự {@code @} báo cho trình biên dịch biết phía sau là một annotation.
 * - Element (thuộc tính):
 *   + Dạng có tên: {@code @Author(name = "Benjamin", date = "3/27/2003")}
 *   + Dạng rút gọn: nếu chỉ có duy nhất một element tên là {@code value}, ta có thể bỏ qua tên element:
 *     {@code @SuppressWarnings("unchecked")} (thay vì {@code @SuppressWarnings(value = "unchecked")}).
 *   + Không có element: bỏ qua cặp ngoặc đơn: {@code @Override}.
 *   + Element dạng mảng: {@code @SuppressWarnings({"unchecked", "deprecation"})}.
 *   + Nếu mảng chỉ có 1 phần tử, có thể bỏ dấu ngoặc nhọn {@code {}}: {@code @SuppressWarnings("unchecked")}.
 * <p>
 * 3. VỊ TRÍ SỬ DỤNG: KHAI BÁO & KIỂU DỮ LIỆU
 * - (Where Annotations Can Be Used)
 * - Trước Java 8: chỉ dùng trên khai báo (declarations): class, field, method, constructor, parameter, local variable, package.
 * - Từ Java 8: dùng được trên cả kiểu dữ liệu (Type Annotations - {@code ElementType.TYPE_USE}):
 *   + Khởi tạo đối tượng: {@code new @NonEmpty List<@NonNull String>()}
 *   + Ép kiểu (cast): {@code (@NonNull String) str}
 *   + Mệnh đề implements: {@code class UnmodifiableList<T> implements @ReadOnly List<T>}
 *   + Mệnh đề throws: {@code void read() throws @Critical IOException}
 * - Type annotations kết hợp với Pluggable Type Systems (như Checker Framework) giúp phát hiện NullPointerException
 *   hoặc lỗi kiểu dữ liệu ngay từ lúc compile time.
 * <p>
 * 4. TỰ ĐỊNH NGHĨA ANNOTATION (@interface)
 * - Cú pháp: dùng từ khóa {@code @interface}.
 * - Thân annotation chứa các element trông giống khai báo phương thức nhưng không có thân hàm.
 * - Có thể cung cấp giá trị mặc định bằng từ khóa {@code default}:
 *   {@code String date() default "N/A";}
 * - Các kiểu dữ liệu hợp lệ cho element trong annotation:
 *   + Kiểu nguyên thủy (primitive: int, double, boolean, ...)
 *   + {@code String}
 *   + {@code Class} (hoặc {@code Class<?>})
 *   + {@code enum}
 *   + Kiểu annotation khác
 *   + Mảng 1 chiều của các kiểu trên (ví dụ {@code String[]})
 *   + KHÔNG ĐƯỢC dùng: Object bất kỳ, List/Map/Collection, mảng đa chiều, hoặc kiểu đệ quy chính nó.
 * <p>
 * 5. CÁC ANNOTATION CÓ SẴN (PREDEFINED ANNOTATION TYPES)
 * - Thuộc gói {@code java.lang}:
 *   + {@code @Deprecated}: đánh dấu phần tử đã lỗi thời, không nên dùng. Compiler cảnh báo khi có code gọi tới.
 *     Từ Java 9 bổ sung thêm thuộc tính: {@code since} (phiên bản bắt đầu deprecated) và
 *     {@code forRemoval} (boolean, đánh dấu có bị xóa trong phiên bản tương lai hay không).
 *   + {@code @Override}: thông báo cho compiler rằng phương thức này ghi đè từ superclass/interface.
 *     Nếu viết sai tên method hoặc sai tham số, compiler lập tức báo lỗi.
 *   + {@code @SuppressWarnings}: dập cảnh báo compiler. 4 nhóm chính trong Java Spec:
 *     {@code "unchecked"}, {@code "deprecation"}, {@code "removal"}, {@code "preview"}.
 *   + {@code @SafeVarargs}: áp dụng cho method/constructor static, final, hoặc private có varargs generic,
 *     khẳng định phương thức an toàn và không gây heap pollution.
 *   + {@code @FunctionalInterface}: (Java 8+) khẳng định interface là Single Abstract Method (SAM).
 * <p>
 * 6. META-ANNOTATIONS (java.lang.annotation)
 * - Annotation dùng để chú thích lên chính một annotation khác:
 *   + {@code @Retention}: xác định vòng đời lưu trữ của annotation:
 *     * {@code RetentionPolicy.SOURCE}: chỉ tồn tại trong mã nguồn Java, bị compiler xóa khi dịch sang file .class.
 *     * {@code RetentionPolicy.CLASS}: được lưu trong file .class nhưng JVM KHÔNG nạp vào bộ nhớ lúc chạy (đây là MẶC ĐỊNH!).
 *     * {@code RetentionPolicy.RUNTIME}: được JVM nạp vào bộ nhớ lúc chạy, có thể đọc qua Reflection API.
 *   + {@code @Target}: giới hạn vị trí mà annotation có thể được đặt vào:
 *     {@code ElementType.TYPE}, {@code METHOD}, {@code FIELD}, {@code CONSTRUCTOR}, {@code PARAMETER},
 *     {@code LOCAL_VARIABLE}, {@code ANNOTATION_TYPE}, {@code PACKAGE}, {@code MODULE}, {@code RECORD_COMPONENT},
 *     {@code TYPE_PARAMETER}, {@code TYPE_USE}.
 *   + {@code @Documented}: chỉ định thông tin annotation này sẽ được công cụ Javadoc đưa vào tài liệu HTML sinh ra.
 *   + {@code @Inherited}: cho phép class con tự động kế thừa annotation từ class cha (chỉ có tác dụng với Class,
 *     không kế thừa qua interface hay method).
 *   + {@code @Repeatable}: (Java 8+) đánh dấu annotation có thể xuất hiện nhiều lần trên cùng một phần tử.
 * <p>
 * 7. REPEATING ANNOTATIONS & ĐỌC QUA REFLECTION
 * - Để một annotation có thể lặp lại (ví dụ {@code @Schedule}):
 *   + Bước 1: Khai báo {@code @Repeatable(Schedules.class)} trên {@code @interface Schedule}.
 *   + Bước 2: Tạo container annotation {@code @interface Schedules} chứa thuộc tính {@code Schedule[] value();}.
 * - Đọc qua Reflection:
 *   + {@code element.getAnnotation(Class)}: chỉ trả về 1 instance hoặc container.
 *   + {@code element.getAnnotationsByType(Class)}: (Java 8+) tự động mở container và trả về toàn bộ mảng annotation lặp lại.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. SO SÁNH SOURCE VS CLASS VS RUNTIME THỰC CHIẾN
 * - SOURCE: Dùng cho linter, checker, code generation lúc build (Lombok, MapStruct, {@code @Override}).
 *   Ưu điểm: Không làm phình to kích thước file .class.
 * - CLASS: Giá trị mặc định nếu quên ghi {@code @Retention}! File .class có chứa, nhưng lúc chạy
 *   gọi {@code method.getAnnotation()} sẽ trả về NULL! Thường dùng cho các thư viện thao tác bytecode
 *   lúc load class (như ByteBuddy, cglib, ASM, AspectJ).
 * - RUNTIME: Bắt buộc cho mọi framework Dependency Injection, ORM, Web: Spring Boot ({@code @Component},
 *   {@code @Autowired}), Hibernate ({@code @Entity}, {@code @Table}), Jackson ({@code @JsonProperty}).
 * <p>
 * 9. BẢN CHẤT DƯỚI JVM CỦA ANNOTATION
 * - Trình biên dịch tạo ra một interface kế thừa ngầm từ {@code java.lang.annotation.Annotation}.
 * - Khi gọi {@code getAnnotation()}, JVM tự động tạo một đối tượng Dynamic Proxy (thông qua
 *   {@code sun.reflect.annotation.AnnotationInvocationHandler}) cài đặt interface đó và đọc các byte
 *   trong bảng {@code RuntimeVisibleAnnotations} của bytecode class.
 * <p>
 * 10. ĐỐI CHIẾU VỚI TYPESCRIPT / C#
 * - C#: Dùng {@code [Attribute]}, cơ chế Reflection tương đương Java ({@code Attribute.GetCustomAttribute}).
 * - TypeScript: Dùng Decorator ({@code @decorator}). Khác biệt mấu chốt: Decorator trong TypeScript là
 *   MỘT HÀM THỰC THI (chạy ngay khi file được nạp và có khả năng wrap/thay đổi method gốc). Ngược lại,
 *   Annotation trong Java hoàn toàn là METADATA BỊ ĐỘNG (passive data), bản thân nó không thực thi lệnh nào
 *   nếu không có công cụ bên ngoài quét qua và đọc nó.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Annotation có trực tiếp làm thay đổi hành vi thực thi của code không?
 * - Nếu một annotation không khai báo @Retention thì giá trị mặc định là gì? Lúc chạy gọi getAnnotation() có lấy được không?
 * - Khi nào có thể viết tắt @MyAnno("abc") mà không cần ghi rõ tên thuộc tính?
 * - Kể tên các kiểu dữ liệu ĐƯỢC PHÉP và KHÔNG ĐƯỢC PHÉP dùng trong phần tử của annotation.
 * - @Deprecated từ Java 9 có thêm hai thuộc tính nào? Ý nghĩa của forRemoval là gì?
 * - Để tạo một Repeatable Annotation cần thực hiện hai bước nào?
 * - Type annotation (ElementType.TYPE_USE) khác gì với Declaration annotation truyền thống?
 * - Khác biệt căn bản giữa Java Annotation và TypeScript Decorator là gì?
 */
public class t1_Annotations {

    // ==================================================================
    // MỤC 4 & 6: TỰ ĐỊNH NGHĨA ANNOTATION VỚI ĐỦ CÁC META-ANNOTATION
    // ==================================================================

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Priority {
        enum Level { LOW, MEDIUM, HIGH }
        Level level() default Level.MEDIUM;
        String description() default "";
    }

    // ==================================================================
    // MỤC 7: REPEATING ANNOTATION (CẦN CONTAINER ANNOTATION)
    // ==================================================================

    // 1. Annotation lặp lại: đánh dấu @Repeatable trỏ tới container
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(Schedules.class)
    public @interface Schedule {
        String dayOfMonth() default "first";
        String dayOfWeek() default "Mon";
        int hour() default 12;
    }

    // 2. Container annotation: chứa mảng Schedule[] với thuộc tính tên value()
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Schedules {
        Schedule[] value();
    }

    // ==================================================================
    // MỤC 3: TYPE ANNOTATION (ElementType.TYPE_USE)
    // ==================================================================

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Validated {}

    // ==================================================================
    // LỚP VÍ DỤ SỬ DỤNG CÁC ANNOTATION
    // ==================================================================

    @Priority(level = Priority.Level.HIGH, description = "Dịch vụ quan trọng")
    static class TaskService {

        // Áp dụng lặp lại @Schedule 2 lần nhờ cơ chế Repeatable
        @Schedule(dayOfMonth = "last")
        @Schedule(dayOfWeek = "Fri", hour = 23)
        public void doPeriodicCleanup() {
            IO.println("Thực hiện dọn dẹp định kỳ...");
        }

        // Predefined annotation: @Deprecated với forRemoval = true
        @Deprecated(since = "2.0", forRemoval = true)
        public void oldMethod() {
            IO.println("Phương thức cũ sắp bị xóa!");
        }

        // Type annotation dùng trên kiểu dữ liệu cục bộ
        public void process() {
            @Validated String status = "OK";
            IO.println("Trạng thái: " + status);
        }
    }

    // ==================================================================
    // CHƯƠNG TRÌNH CHÍNH: ĐỌC ANNOTATION BẰNG REFLECTION
    // ==================================================================

    void main() throws NoSuchMethodException {
        Class<TaskService> clazz = TaskService.class;

        // 1. Đọc annotation trên Class
        if (clazz.isAnnotationPresent(Priority.class)) {
            Priority priority = clazz.getAnnotation(Priority.class);
            IO.println("Class Priority: " + priority.level() + " - " + priority.description());
        }

        // 2. Đọc Repeating Annotation trên Method
        Method cleanupMethod = clazz.getMethod("doPeriodicCleanup");

        // Cách Java 8+: getAnnotationsByType tự bóc tách container Schedules
        Schedule[] schedules = cleanupMethod.getAnnotationsByType(Schedule.class);
        IO.println("Số lượng lịch @Schedule tìm thấy: " + schedules.length);
        for (Schedule s : schedules) {
            IO.println(" -> Schedule: dayOfMonth=" + s.dayOfMonth() + ", dayOfWeek=" + s.dayOfWeek() + ", hour=" + s.hour());
        }

        // 3. Kiểm tra @Deprecated
        Method oldMethod = clazz.getMethod("oldMethod");
        if (oldMethod.isAnnotationPresent(Deprecated.class)) {
            Deprecated dep = oldMethod.getAnnotation(Deprecated.class);
            IO.println("Method oldMethod is deprecated since: " + dep.since() + ", forRemoval: " + dep.forRemoval());
        }

        // 4. SuppressWarnings ví dụ
        @SuppressWarnings({"unchecked", "rawtypes"})
        java.util.List list = new java.util.ArrayList();
        list.add("test");
        IO.println("List raw type kích thước: " + list.size());
    }

    // TODO 1 (mục 4 & 6): Định nghĩa một annotation @AuditLog có RetentionPolicy.RUNTIME,
    //   Target là METHOD, gồm 2 element: action (String) và roles (String[] default {"USER"}).
    //
    // TODO 2 (mục 6): Viết thử một annotation @SourceOnly với RetentionPolicy.SOURCE và
    //   @ClassOnly với RetentionPolicy.CLASS. Dùng Reflection đọc thử hai annotation này
    //   lúc runtime để chứng minh kết quả trả về là null.
    //
    // TODO 3 (mục 6): Thử nghiệm @Inherited: Tạo annotation @Tracked có @Inherited. Đặt nó
    //   trên SuperClass, sau đó kiểm tra SubClass.isAnnotationPresent(Tracked.class) xem
    //   SubClass có tự động nhận được không.
    //
    // TODO 4 (mục 3): Viết một method nhận tham số có gắn Type Annotation:
    //   void printLength(@Validated String text). Tìm hiểu cách đọc Type Annotation
    //   thông qua Method.getAnnotatedParameterTypes().
}
