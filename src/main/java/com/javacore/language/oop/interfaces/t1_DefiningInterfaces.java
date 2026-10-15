package com.javacore.language.oop.interfaces;

/**
 * Defining Interfaces
 * <p>
 * 0. Vị trí bài này
 * - Bước 11/41, bài 1/3 trong section Interfaces của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Interface là HỢP ĐỒNG (contract) quy định một lớp
 * "phải làm được gì" mà hoàn toàn không quan tâm lớp đó "làm điều đó thế nào",
 * đồng thời từ Java 8/9 đã mở rộng thêm default, static và private method để
 * hỗ trợ tiến hóa API an toàn.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/oop/interfaces/defining-interfaces/">dev.java - Interfaces</a>
 * - Mọi ví dụ và kết quả dán trong ghi chú này đều biên dịch và chạy thật trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - DEFINING AN INTERFACE (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. HỢP ĐỒNG (CONTRACT) VÀ API SPECIFICATION
 * - "There are a number of situations in software engineering when it is important
 * for disparate groups of programmers to agree to a 'contract' that spells out how
 * their software interacts. Each group should be able to write their code without
 * any knowledge of how the other group's code is written. Generally speaking,
 * interfaces are such contracts."
 * - Ví dụ Robotic Car của Oracle:
 * + Nhóm sản xuất ô tô (Toyota, Chevrolet) viết mã điều khiển xe vật lý (rẽ trái,
 * đạp phanh, tăng tốc).
 * + Nhóm sản xuất thiết bị định vị (guidance systems) nhận dữ liệu vệ tinh GPS
 * và phát lệnh điều khiển xe tự hành.
 * + Hai bên không cần biết mã nguồn của nhau (giữ bí mật thương mại độc quyền),
 * chỉ cần thống nhất một interface công khai OperateCar. Ô tô triển khai (implements)
 * interface, còn hệ thống GPS gọi các method trong interface đó.
 * - Tương tự, trong các hệ thống thương mại (commercial software products), công ty
 * bán thư viện xử lý ảnh (Image Processing) chỉ cung cấp interface công khai cho
 * khách hàng, còn mã thuật toán bên trong được bảo mật.
 * <p>
 * 2. CÚ PHÁP KHAI BÁO VÀ ACCESS MODIFIER
 * - Cú pháp:
 * [public] interface InterfaceName [extends SuperInterface1, ...] { ... }
 * - Quyền truy cập:
 * + public: Interface có thể được truy cập bởi mọi class ở bất kỳ package nào.
 * + package-private (không ghi modifier): Chỉ các class cùng package mới thấy.
 * - Đặc tính cốt lõi: Interface KHÔNG THỂ khởi tạo trực tiếp (cannot be instantiated
 * via new), chỉ có thể được triển khai bởi class hoặc được kế thừa bởi interface khác.
 * <p>
 * 3. HẰNG SỐ TRONG INTERFACE (INTERFACE CONSTANTS)
 * - Mọi biến khai báo trong interface ĐỀU TỰ ĐỘNG là "public static final", dù có gõ
 * từ khóa hay không.
 * - Interface KHÔNG CÓ instance state (không có field non-static non-final thuộc về
 * từng đối tượng). Đây là khác biệt bản chất nhất giữa interface và abstract class.
 * - Cảnh báo Clean Code (Joshua Bloch - Effective Java Item 22):
 * "Constant Interface" (tạo interface chỉ để chứa hằng số) là một ANTI-PATTERN.
 * Interface sinh ra để định nghĩa KIỂU (type), nếu chỉ cần gom hằng số hẵng dùng
 * final class với private constructor hoặc enum.
 * <p>
 * 4. ABSTRACT METHOD
 * - Khai báo method chỉ gồm tên, kiểu trả về và tham số, kết thúc bằng dấu chấm phẩy (;).
 * - Mọi abstract method trong interface ĐỀU TỰ ĐỘNG là "public abstract".
 * Không cần và không nên viết thừa "public abstract".
 * <p>
 * 5. DEFAULT METHOD (JAVA 8+) - TIẾN HÓA INTERFACE AN TOÀN
 * - Cú pháp: default returnType methodName(args) { ... }
 * - Lý do ra đời: Trước Java 8, nếu thêm một abstract method vào interface đã phát hành,
 * MỌI class đang implement interface đó trên toàn thế giới sẽ bị LỖI BIÊN DỊCH ngay lập
 * tức (vì chưa cài đặt method mới). Default method cho phép bổ sung hành vi mới kèm thân
 * hàm mặc định mà KHÔNG LÀM HỎNG mã nguồn cũ (backward compatibility).
 * - Ví dụ kinh điển: Java 8 thêm method forEach(), stream() vào interface Collection
 * mà hàng triệu class cũ của lập trình viên vẫn biên dịch bình thường.
 * <p>
 * 6. STATIC METHOD (JAVA 8+) VÀ PRIVATE METHOD (JAVA 9+)
 * - Static method trong interface:
 * + Cung cấp các hàm tiện ích (utility / factory methods) gắn liền với interface.
 * + Ví dụ chuẩn: List.of(), Map.of(), Comparator.comparing().
 * + LƯU Ý SỐNG CÒN: Static method của interface KHÔNG ĐƯỢC kế thừa vào class triển khai.
 * Luôn phải gọi qua: TenInterface.staticMethod().
 * - Private & Private Static method (Java 9+):
 * + Thân hàm default method nếu dài hoặc trùng lặp có thể tách ra private method để tái sử dụng.
 * + Không lộ ra ngoài API công khai của interface, hoàn thiện nguyên lý đóng gói (encapsulation).
 * <p>
 * ======================================================================
 * THỰC HÀNH VÀ BẪY THƯỜNG GẶP TRONG DỰ ÁN
 * ======================================================================
 * <p>
 * 7. BẢNG TỔNG KẾT THÀNH VIÊN TRONG INTERFACE TỪ JAVA 9 TRỞ ĐI
 * - Constants:              public static final (ngầm định)
 * - Abstract methods:       public abstract (ngầm định, không có thân hàm)
 * - Default methods:        public default (có thân hàm, instance)
 * - Static methods:         public static hoặc private static (có thân hàm)
 * - Private methods:        private (có thân hàm, chỉ gọi nội bộ trong interface)
 * - Nested types:           public static (ngầm định: class, interface, record, enum bên trong)
 * <p>
 * BỊ CẤM: protected, package-private method, instance variables, constructor, initializer blocks.
 */
public class t1_DefiningInterfaces {

    /**
     * 1. Ví dụ chuẩn từ tài liệu dev.java: Hợp đồng điều khiển xe tự hành.
     */
    public interface OperateCar {
        // Constant: ngầm định public static final
        int MAX_SPEED = 120;

        // Abstract methods: ngầm định public abstract
        void startEngine();

        void stopEngine();

        int turn(String direction, double radius, double startSpeed, double endSpeed);

        int changeLanes(String direction, double startSpeed, double endSpeed);

        int signalTurn(String direction, boolean signalOn);

        // Default method: thêm tính năng mới mà không làm hỏng implementing classes cũ
        default void emergencyBrake() {
            IO.println("-> [OperateCar default] Phanh khẩn cấp kích hoạt tự động!");
        }
    }

    /**
     * 2. Ví dụ toàn diện về Default, Static và Private Methods trong Interface.
     */
    public interface AppLogger {
        // Hằng số định dạng
        String PREFIX = "[APP-LOG]";

        // Abstract method: class con bắt buộc phải cài đặt cách ghi (ra console, ra file, gửi qua mạng...)
        void writeRaw(String rawMessage);

        // Default method 1: Log cấp INFO
        default void logInfo(String message) {
            String formatted = formatMessage("INFO", message);
            writeRaw(formatted);
        }

        // Default method 2: Log cấp ERROR
        default void logError(String message, Throwable error) {
            String details = message + (error != null ? " | Exception: " + error.getMessage() : "");
            String formatted = formatMessage("ERROR", details);
            writeRaw(formatted);
        }

        // Java 9+: Private method dùng chung để tránh trùng lặp code giữa các default methods
        private String formatMessage(String level, String msg) {
            return PREFIX + " [" + level + "] " + timestamp() + " : " + msg;
        }

        // Java 9+: Private static method hỗ trợ cho static hoặc default method
        private static String timestamp() {
            return java.time.LocalTime.now().toString();
        }

        // Java 8+: Static factory method cung cấp sẵn một logger đơn giản
        static AppLogger consoleLogger() {
            return raw -> IO.println(raw);
        }
    }

    /**
     * Class mẫu triển khai AppLogger
     */
    public static class ConsoleLogger implements AppLogger {
        @Override
        public void writeRaw(String rawMessage) {
            IO.println("ConsoleLogger -> " + rawMessage);
        }
    }

    /**
     * Class mẫu xe Toyota triển khai OperateCar
     */
    public static class ToyotaRoboticCar implements OperateCar {
        private int currentSpeed = 0;

        @Override
        public void startEngine() {
            IO.println("Toyota: Khởi động động cơ Hybrid.");
        }

        @Override
        public void stopEngine() {
            IO.println("Toyota: Tắt máy an toàn.");
        }

        @Override
        public int turn(String direction, double radius, double startSpeed, double endSpeed) {
            IO.println("Toyota: Bẻ lái hướng " + direction + ", bán kính=" + radius + "m");
            return 0;
        }

        @Override
        public int changeLanes(String direction, double startSpeed, double endSpeed) {
            IO.println("Toyota: Chuyển làn sang " + direction);
            return 0;
        }

        @Override
        public int signalTurn(String direction, boolean signalOn) {
            IO.println("Toyota: Đèn xi-nhan " + direction + ": " + (signalOn ? "BẬT" : "TẮT"));
            return 0;
        }
    }

    void main() {
        IO.println("=== 1. VÍ DỤ ROBOTIC CAR (DEV.JAVA) ===");
        OperateCar car = new ToyotaRoboticCar();
        car.startEngine();
        car.signalTurn("TRÁI", true);
        car.turn("TRÁI", 15.0, 30.0, 20.0);
        // Gọi default method được thừa hưởng từ interface
        car.emergencyBrake();
        IO.println("Hằng số OperateCar.MAX_SPEED = " + OperateCar.MAX_SPEED + " km/h");

        IO.println("\n=== 2. VÍ DỤ DEFAULT, STATIC VÀ PRIVATE METHOD TRONG INTERFACE ===");
        // Dùng factory method static của interface
        AppLogger standardLogger = AppLogger.consoleLogger();
        standardLogger.logInfo("Hệ thống khởi chạy thành công.");
        standardLogger.logError("Lỗi kết nối bộ đệm cảm biến", new RuntimeException("Timeout 5000ms"));

        // Dùng class tùy biến
        AppLogger customLogger = new ConsoleLogger();
        customLogger.logInfo("Người dùng đã đăng nhập.");

        // TODO 1 (Kiểm chứng modifier): Thử thêm vào interface OperateCar một field non-final
        //   như 'int currentGear = 1;' và sửa giá trị của nó từ bên ngoài. Quan sát thông báo
        //   lỗi 'cannot assign a value to static final variable' từ compiler.
        //
        // TODO 2 (Java 9 Private method): Thử gọi method formatMessage(...) từ đối tượng 'car'
        //   hoặc 'standardLogger'. Giải thích vì sao private method chỉ được gọi nội bộ bên trong
        //   thân interface.
    }
}
