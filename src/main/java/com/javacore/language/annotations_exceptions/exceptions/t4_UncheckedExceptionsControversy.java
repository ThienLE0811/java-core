package com.javacore.language.annotations_exceptions.exceptions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.function.Consumer;

/**
 * Unchecked Exceptions — The Controversy
 * <p>
 * 0. Vị trí bài này
 * - Bước 18/41, bài 4/4 trong section Exceptions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Nguyên tắc vàng: Dùng Checked Exception khi caller có thể dự đoán và phục hồi hợp lý; dùng Unchecked Exception khi là lỗi lập trình hoặc caller bất khả kháng.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/annotations-exceptions/exceptions/unchecked-exception-controversy/">dev.java - Unchecked Exceptions — The Controversy</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bên dưới biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - TRANH LUẬN VỀ UNCHECKED EXCEPTION (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. TRANH LUẬN VỀ UNCHECKED EXCEPTION (THE CONTROVERSY)
 * - Do Java không bắt buộc phải catch hay specify đối với {@code RuntimeException} và {@code Error},
 *   nhiều lập trình viên bị cám dỗ chuyển toàn bộ ngoại lệ sang Unchecked Exception để né lỗi compiler.
 * - Vì sao các nhà thiết kế Java lại bắt buộc khai báo Checked Exception?
 *   "Any Exception that can be thrown by a method is part of the method's public programming interface."
 *   Ngoại lệ mà hàm có thể ném ra là một phần của HỢP ĐỒNG API (giống như kiểu tham số và kiểu trả về).
 *   Người gọi hàm CẦN BIẾT trước để đưa ra quyết định xử lý phù hợp.
 * - Vì sao RuntimeException không bị bắt buộc khai báo?
 *   Vì lỗi runtime (như chia cho 0, null pointer, vượt chỉ mục mảng) có thể xuất hiện ở bất cứ dòng code nào.
 *   Nếu dòng nào cũng bắt khai báo thì chữ ký hàm sẽ bị rác (clutter) và làm mất tính rõ ràng của chương trình.
 * <p>
 * 2. NGUYÊN TẮC VÀNG ĐỂ CHỌN (THE BOTTOM LINE GUIDELINE)
 * - "If a client can reasonably be expected to recover from an exception, make it a checked exception."
 *   (Nếu caller có thể dự đoán và có biện pháp phục hồi hợp lý → DÙNG CHECKED EXCEPTION).
 *   Ví dụ: Nhập sai tên file ({@code FileNotFoundException}) → bắt lỗi và nhắc người dùng chọn file khác.
 * - "If a client cannot do anything to recover from the exception, make it an unchecked exception."
 *   (Nếu caller không thể làm gì để cứu vãn, hoặc đó là lỗi lập trình → DÙNG UNCHECKED EXCEPTION).
 *   Ví dụ: Truyền đối số null vào hàm không cho phép ({@code NullPointerException}) → cần sửa code chứ không phải cứu vãn.
 * <p>
 * 3. BA ƯU ĐIỂM LỚN CỦA EXCEPTION SO VỚI MÃ LỖI CỔ ĐIỂN
 * - (Advantages of Exceptions)
 * - Ưu điểm 1: Tách biệt chi tiết xử lý lỗi khỏi luồng code nghiệp vụ (Separating Error-Handling Code from "Regular" Code).
 *   Tránh biến code thành mớ bòng bong spaghetti với hàng tá câu lệnh kiểm tra mã lỗi {@code if (err != 0)}.
 * - Ưu điểm 2: Lan truyền lỗi tự động lên Call Stack (Propagating Errors Up the Call Stack).
 *   Các hàm trung gian không cần bận tâm về việc chuyển tiếp mã lỗi nếu chúng không có nhu cầu xử lý.
 * - Ưu điểm 3: Phân nhóm và phân cấp lỗi theo kế thừa hướng đối tượng (Grouping and Differentiating Error Types).
 *   Có thể bắt cụ thể từng loại lỗi (như {@code FileNotFoundException}) hoặc bắt chung cả nhóm (như {@code IOException}).
 * <p>
 * 4. CẢNH BÁO BẮT EXCEPTION QUÁ TỔNG QUÁT (OVERLY BROAD EXCEPTION)
 * - Viết {@code catch (Exception e)} hoặc bắt {@code Throwable} có thể vô tình nuốt cả những lỗi runtime
 *   không lường trước (như {@code NullPointerException}), biến bug tiềm ẩn thành lỗi ngầm khó phát hiện.
 * - Luôn luôn bắt ngoại lệ CÀNG CỤ THỂ CÀNG TỐT (as specific as possible).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BƯỚC CHUYỂN DỊCH THỰC TẾ TRONG NGÀNH (THE INDUSTRY SHIFT)
 * - Dù Java thiết kế ban đầu thiên về Checked Exception, các framework lớn hiện đại (Spring, Hibernate)
 *   đã chuyển dịch hầu hết sang Unchecked Exception (ví dụ: Spring bọc {@code SQLException} thành {@code DataAccessException}).
 * - Hạn chế lớn nhất của Checked Exception với Java hiện đại:
 *   KHÔNG TƯƠNG THÍCH VỚI LAMBDA VÀ STREAM API!
 *   Ví dụ: {@code list.forEach(item -> readFile(item));} sẽ KHÔNG BIÊN DỊCH ĐƯỢC nếu {@code readFile} ném Checked Exception,
 *   bởi vì {@code Consumer<T>} trong JDK không khai báo {@code throws Exception}.
 * <p>
 * 6. BỐN ANTI-PATTERNS KINH ĐIỂN KHI XỬ LÝ NGOẠI LỆ
 * - Anti-pattern 1: NUỐT NGOẠI LỆ (Silent catch / Swallowed exception)
 *   {@code catch (Exception e) { /* để trống *\/ }} → Lỗi chết người, hệ thống sai nhưng không ai biết.
 * - Anti-pattern 2: LOG RỒI NÉM TIẾP (Log and Rethrow)
 *   {@code catch (Exception e) { logger.error("Lỗi", e); throw e; }} → Khiến 1 lỗi bị in log lặp lại 5-10 lần ở mọi tầng.
 *   QUY TẮC: Hoặc là log và xử lý xong, hoặc là ném tiếp cho tầng trên (kèm cause). ĐỪNG LÀM CẢ HAI!
 * - Anti-pattern 3: BẮT THROWABLE HOẶC ERROR
 *   {@code catch (Throwable t)} → Bắt nhầm cả {@code OutOfMemoryError}, làm JVM chạy trong trạng thái què quặt.
 * - Anti-pattern 4: DÙNG EXCEPTION ĐỂ ĐIỀU KHIỂN LUỒNG (Control Flow)
 *   Dùng try-catch thay vì kiểm tra {@code if (list.isEmpty())} → Làm giảm hiệu năng hàng trăm lần do chi phí dựng stack trace.
 * <p>
 * 7. ĐỐI CHIẾU VỚI CÁC NGÔN NGỮ HIỆN ĐẠI
 * - C#, TypeScript, Python: KHÔNG CÓ Checked Exception từ lúc ra đời.
 * - Kotlin (chạy trên chính JVM): BỎ HOÀN TOÀN Checked Exception của Java.
 * - Rust, Go: Không dùng Exception mà dùng kiểu trả về tường minh ({@code Result<T, E>}).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Phát biểu nguyên tắc vàng của Oracle khi chọn giữa Checked và Unchecked Exception.
 * - Kể tên 3 ưu điểm vượt trội của Exception so với kiểm tra mã lỗi truyền thống (Error Codes).
 * - Vì sao các framework như Spring Boot hầu như chỉ dùng Unchecked Exception?
 * - Tại sao Checked Exception lại gây khó khăn khi dùng chung với Stream API và Lambda?
 * - Kể tên 4 Anti-patterns phổ biến nhất khi xử lý ngoại lệ và cách khắc phục từng cái.
 * - Vì sao không bao giờ nên viết catch (Throwable t)?
 */
public class t4_UncheckedExceptionsControversy {

    // ==================================================================
    // MỤC 2: KHI NÀO DÙNG CHECKED VS UNCHECKED
    // ==================================================================

    // 1. DÙNG CHECKED: Tình huống người dùng có thể phục hồi (nhập lại tên file)
    static void readUserConfig(String fileName) throws FileNotFoundException {
        if (!fileName.endsWith(".json")) {
            throw new FileNotFoundException("Tệp cấu hình không tồn tại hoặc sai định dạng: " + fileName);
        }
        IO.println("Đọc cấu hình thành công từ " + fileName);
    }

    // 2. DÙNG UNCHECKED: Lỗi lập trình vi phạm tiền điều kiện (truyền tham số null)
    static void processUserData(String username) {
        if (username == null || username.isBlank()) {
            // Lỗi do lập trình viên gọi hàm sai -> ném Unchecked (IllegalArgumentException)
            throw new IllegalArgumentException("Username không được rỗng hoặc null!");
        }
        IO.println("Đang xử lý dữ liệu cho user: " + username);
    }

    // ==================================================================
    // MỤC 5: XUNG ĐỘT GIỮA CHECKED EXCEPTION VÀ STREAM API / LAMBDA
    // ==================================================================

    // Hàm tiện ích bọc Checked Exception thành Unchecked để dùng mượt mà trong Lambda
    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    public static <T> Consumer<T> unchecked(ThrowingConsumer<T, Exception> consumer) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception e) {
                // Bọc Checked Exception thành RuntimeException để Stream không bị lỗi compile
                throw new RuntimeException("Lỗi xảy ra trong Stream pipeline: " + e.getMessage(), e);
            }
        };
    }

    // ==================================================================
    // MỤC 6: MINH HỌA CÁC ANTI-PATTERNS KINH ĐIỂN
    // ==================================================================

    static void demonstrateAntiPatterns() {
        IO.println("\n--- MINH HỌA XỬ LÝ LỖI CHUẨN MỰC THAY VÌ ANTI-PATTERNS ---");

        // Anti-pattern 1: Nuốt ngoại lệ (ĐÃ SỬA: Luôn log hoặc xử lý)
        try {
            int val = Integer.parseInt("abc");
        } catch (NumberFormatException e) {
            // ĐÚNG: Ghi nhận lỗi rõ ràng thay vì để catch block rỗng
            IO.println("  [Handled]: Không thể chuyển đổi số: " + e.getMessage());
        }

        // Anti-pattern 2: Dùng exception thay if (ĐÃ SỬA: Dùng if kiểm tra trước)
        String text = null;
        // SAI: try { text.length(); } catch (NullPointerException e) { ... }
        // ĐÚNG:
        if (text != null) {
            IO.println("Độ dài: " + text.length());
        } else {
            IO.println("  [Handled]: Chuỗi text là null, bỏ qua an toàn.");
        }
    }

    // ==================================================================
    // CHƯƠNG TRÌNH CHÍNH
    // ==================================================================

    void main() {
        // 1. Phục hồi từ Checked Exception
        IO.println("--- 1. PHỤC HỒI TỪ CHECKED EXCEPTION ---");
        String badFileName = "config.xml";
        try {
            readUserConfig(badFileName);
        } catch (FileNotFoundException e) {
            IO.println("Phát hiện lỗi: " + e.getMessage());
            IO.println("Biện pháp phục hồi: Chuyển sang đọc file dự phòng mặc định (default_config.json)");
            try {
                readUserConfig("default_config.json");
            } catch (FileNotFoundException _) {}
        }

        // 2. Unchecked Exception: phát hiện bug lập trình
        IO.println("\n--- 2. BẮT UNCHECKED EXCEPTION (LỖI LẬP TRÌNH) ---");
        try {
            processUserData("");
        } catch (IllegalArgumentException e) {
            IO.println("Bắt được lỗi tham số: " + e.getMessage());
        }

        // 3. Lambda Stream với Exception bọc
        IO.println("\n--- 3. SỬ DỤNG STREAM API VỚI CHECKED EXCEPTION BỌC ---");
        List<String> files = List.of("app.json", "bad.xml", "data.json");
        try {
            files.forEach(unchecked(f -> readUserConfig(f)));
        } catch (RuntimeException e) {
            IO.println("Stream bắt được lỗi bọc: " + e.getMessage());
            IO.println("Nguyên nhân gốc (Cause): " + e.getCause().getClass().getSimpleName());
        }

        // 4. Các anti-patterns
        demonstrateAntiPatterns();
    }

    // TODO 1 (mục 2): Viết một phương thức login(username, password). Hãy quyết định:
    //   Khi nào thì nên ném UserNotFoundException (Checked hay Unchecked)?
    //   Khi nào thì ném NullPointerException? Giải thích lý do chọn.
    //
    // TODO 2 (mục 5): Viết một Stream đọc danh sách 3 file bằng FileReader.
    //   Quan sát lỗi biên dịch nếu không bọc try-catch bên trong lambda.
    //
    // TODO 3 (mục 6): Hãy giải thích vì sao đoạn code sau vi phạm nguyên tắc "Log and Rethrow":
    //   catch (SQLException e) { System.err.println(e); throw e; }
    //
    // TODO 4 (mục 7): Tìm hiểu xem trong ngôn ngữ Kotlin hoặc C#, tại sao các nhà thiết kế
    //   lại quyết định khai tử (loại bỏ hoàn toàn) tính năng Checked Exception của Java.
}
