package com.javacore.language.annotations_exceptions.exceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * What Is an Exception?
 * <p>
 * 0. Vị trí bài này
 * - Bước 18/41, bài 1/4 trong section Exceptions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Exception là sự kiện làm gián đoạn luồng chạy bình thường; Java phân làm Checked Exception (bắt buộc Catch hoặc Specify) và Unchecked Exception (Error và RuntimeException).
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/annotations-exceptions/exceptions/what-is-an-exception/">dev.java - What Is an Exception?</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bên dưới biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - WHAT IS AN EXCEPTION? (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. ĐỊNH NGHĨA NGOẠI LỆ & CALL STACK
 * - "The term exception is shorthand for the phrase 'exceptional event'."
 * - Định nghĩa: "An exception is an event, which occurs during the execution of a program,
 *   that disrupts the normal flow of the program's instructions."
 * - Khi lỗi xảy ra trong một method, method đó tạo ra một "Exception Object" và chuyển giao
 *   cho hệ thống runtime. Hành động này gọi là THROWING AN EXCEPTION (ném ngoại lệ).
 * - Exception Object chứa đầy đủ thông tin: kiểu lỗi, thông điệp lỗi và trạng thái của
 *   chương trình tại thời điểm xảy ra lỗi (Call Stack).
 * - CALL STACK (ngăn xếp lời gọi hàm): danh sách có thứ tự các method được gọi nối tiếp
 *   nhau để dẫn đến method bị ném ngoại lệ.
 * - Hệ thống runtime lội ngược Call Stack (từ method lỗi trở về trước) để tìm kiếm
 *   một khối mã có khả năng xử lý lỗi gọi là EXCEPTION HANDLER.
 * - Khi tìm thấy handler có kiểu ngoại lệ khớp với exception object thrown → ngoại lệ được CATCH (bắt).
 * - Nếu lội hết Call Stack mà không tìm thấy handler nào phù hợp → Thread bị lỗi sẽ bị tiêu diệt
 *   (terminates). Nếu thread đó là main thread → toàn bộ chương trình TERMINATES (bị crash).
 * <p>
 * 2. QUY TẮC BẮT BUỘC "CATCH OR SPECIFY"
 * - (The Catch or Specify Requirement)
 * - Mọi đoạn mã Java hợp lệ bắt buộc phải tuân theo quy tắc Catch or Specify đối với các ngoại lệ nhất định:
 *   + Hoặc CATCH: Bọc trong khối {@code try} và cung cấp khối {@code catch} để xử lý.
 *   + Hoặc SPECIFY: Khai báo rõ ràng trong chữ ký hàm bằng từ khóa {@code throws} danh sách các ngoại lệ có thể ném ra.
 * - Đoạn mã vi phạm quy tắc này sẽ KHÔNG THỂ BIÊN DỊCH (Compile-time error).
 * <p>
 * 3. BA NHÓM NGOẠI LỆ TRONG JAVA (THE THREE KINDS OF EXCEPTIONS)
 * - Sơ đồ phả hệ cây ngoại lệ trong Java:
 * <pre>
 *                     java.lang.Throwable
 *                        /          \
 *            java.lang.Error      java.lang.Exception
 *                                   /              \
 *                 RuntimeException             Checked Exceptions
 *                 (Unchecked)                  (IOException, SQLException, ...)
 * </pre>
 * - Nhóm 1: CHECKED EXCEPTION
 *   + Là các điều kiện bất thường mà một ứng dụng được viết tốt NÊN dự đoán trước (anticipate)
 *     và có khả năng phục hồi (recover). Ví dụ: mở file không tồn tại ({@code FileNotFoundException}),
 *     mất kết nối mạng ({@code SocketException}), lỗi SQL ({@code SQLException}).
 *   + BẮT BUỘC tuân theo quy tắc "Catch or Specify".
 *   + Tất cả các lớp con của {@code Exception} (trừ {@code RuntimeException} và các con của nó) đều là Checked Exception.
 * - Nhóm 2: ERROR
 *   + Là các điều kiện nghiêm trọng ngoại cảnh mà ứng dụng thường KHÔNG THỂ dự đoán hoặc phục hồi.
 *     Ví dụ: hết bộ nhớ RAM ({@code OutOfMemoryError}), tràn ngăn xếp đệ quy ({@code StackOverflowError}),
 *     hỏng phần cứng/hệ thống ({@code IOError}, {@code VirtualMachineError}).
 *   + KHÔNG bắt buộc tuân theo Catch or Specify (là Unchecked).
 * - Nhóm 3: RUNTIME EXCEPTION
 *   + Là các điều kiện bất thường nội bộ do lỗi lập trình (programming bugs, logic errors),
 *     hoặc sử dụng sai API. Ví dụ: truy cập null ({@code NullPointerException}), vượt quá chỉ mục mảng
 *     ({@code ArrayIndexOutOfBoundsException}), chia cho 0 ({@code ArithmeticException}).
 *   + KHÔNG bắt buộc tuân theo Catch or Specify (là Unchecked). Ứng dụng nên sửa triệt để bug thay vì cố bắt.
 * - TỔNG HỢP: Error và RuntimeException được gọi chung là UNCHECKED EXCEPTIONS.
 * <p>
 * 4. LÁCH LUẬT CATCH OR SPECIFY (BYPASSING CATCH OR SPECIFY)
 * - Một số lập trình viên coi Checked Exception là phiền hà và né tránh bằng cách đổi hết sang Unchecked Exception.
 * - Bài viết nhắc nhở: Không khuyến nghị lạm dụng việc này. Cần hiểu đúng mục đích thiết kế của từng loại
 *   (sẽ bàn sâu ở bài 4: Unchecked Exceptions — The Controversy).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. CHI PHÍ HIỆU NĂNG CỦA EXCEPTION
 * - Việc tạo một đối tượng Exception tốn kém hiệu năng chủ yếu do phương thức native {@code fillInStackTrace()}.
 *   Nó phải duyệt toàn bộ Call Stack của Thread tại thời điểm `new Exception()` để chụp lại snapshot.
 * - QUY TẮC VÀNG: Không bao giờ dùng Exception để điều khiển luồng logic thông thường (Control Flow)
 *   trong các vòng lặp hay tác vụ hiệu năng cao.
 * <p>
 * 6. VỊ TRÍ LƯU TRỮ: HEAP VS STACK
 * - Bản thân đối tượng Exception là một Java Object nằm trên HEAP.
 * - Nhưng nó chứa thuộc tính {@code StackTraceElement[]} mô tả các frame trên CALL STACK của Thread.
 * <p>
 * 7. ĐỐI CHIẾU VỚI TYPESCRIPT / C# / PYTHON
 * - TypeScript, JavaScript, C#, Python, C++: KHÔNG CÓ Checked Exception!
 *   Mọi exception đều là Unchecked. Trình biên dịch không bao giờ bắt buộc bạn phải try/catch hay throws.
 * - Java là ngôn ngữ hiếm hoi đưa Checked Exception vào ngôn ngữ cốt lõi nhằm tăng tính bền bỉ (robustness),
 *   nhưng cũng tạo ra tranh luận lớn nhất trong lịch sử thiết kế ngôn ngữ lập trình.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Exception là gì? Quá trình ném và bắt ngoại lệ qua Call Stack diễn ra như thế nào?
 * - Nếu không có method nào trong Call Stack bắt ngoại lệ thì hậu quả là gì?
 * - Quy tắc "Catch or Specify Requirement" yêu cầu lập trình viên phải làm gì khi gọi hàm ném Checked Exception?
 * - Phân biệt 3 nhóm: Checked Exception, Error và RuntimeException (kế thừa từ đâu, cái nào là unchecked)?
 * - Cho 3 ví dụ về Checked Exception và 3 ví dụ về Unchecked Exception hay gặp nhất.
 * - Vì sao nói "Không nên dùng Exception để điều khiển luồng chương trình"?
 * - TypeScript / C# có Checked Exception giống Java không?
 */
public class t1_WhatIsAnException {

    // ==================================================================
    // MỤC 1: MINH HỌA CALL STACK KHI XẢY RA NGOẠI LỆ
    // ==================================================================

    static void methodC() {
        IO.println("Đang trong methodC, chuẩn bị chia cho 0...");
        // Gây ra ArithmeticException (RuntimeException - Unchecked)
        int result = 10 / 0;
        IO.println("Dòng này sẽ không bao giờ được chạy: " + result);
    }

    static void methodB() {
        IO.println("Đang trong methodB, chuẩn bị gọi methodC()...");
        methodC();
        IO.println("Kết thúc methodB");
    }

    static void methodA() {
        IO.println("Đang trong methodA, chuẩn bị gọi methodB()...");
        methodB();
        IO.println("Kết thúc methodA");
    }

    // ==================================================================
    // MỤC 2: QUY TẮC CATCH OR SPECIFY VỚI CHECKED EXCEPTION
    // ==================================================================

    // Cách 1: SPECIFY - Khai báo 'throws FileNotFoundException' trên chữ ký hàm
    static void openFileSpecify(String path) throws FileNotFoundException {
        FileReader reader = new FileReader(new File(path));
    }

    // Cách 2: CATCH - Bọc trong khối try-catch để tự xử lý
    static void openFileCatch(String path) {
        try {
            FileReader reader = new FileReader(new File(path));
        } catch (FileNotFoundException e) {
            IO.println("Bắt được Checked Exception: " + e.getMessage());
        }
    }

    // ==================================================================
    // CHƯƠNG TRÌNH CHÍNH
    // ==================================================================

    void main() {
        IO.println("--- 1. BẮT NGOẠI LỆ VÀ IN CALL STACK ---");
        try {
            // Chuỗi gọi hàm: main -> methodA -> methodB -> methodC
            methodA();
        } catch (ArithmeticException e) {
            IO.println("Đã bắt ngoại lệ tại main(): " + e.getClass().getSimpleName() + " - " + e.getMessage());
            IO.println("Vết ngăn xếp (Call Stack Trace):");
            for (StackTraceElement element : e.getStackTrace()) {
                IO.println("  tại " + element.getClassName() + "." + element.getMethodName()
                        + " (" + element.getFileName() + ":" + element.getLineNumber() + ")");
            }
        }

        IO.println("\n--- 2. MINH HỌA CHECKED EXCEPTION ---");
        openFileCatch("non_existent_file.txt");

        IO.println("\n--- 3. KIỂM TRA PHÂN CẤP THROWABLE BẰNG INSTANCEOF ---");
        Exception ex = new IllegalArgumentException("Tham số sai");
        IO.println("IllegalArgumentException là RuntimeException? " + (ex instanceof RuntimeException)); // true
        IO.println("IllegalArgumentException là Exception? " + (ex instanceof Exception));               // true
        IO.println("IllegalArgumentException là Throwable? " + (ex instanceof Throwable));               // true

        Error err = new OutOfMemoryError("Giả lập hết RAM");
        IO.println("OutOfMemoryError là Exception? " + ((Object) err instanceof Exception));             // false
        IO.println("OutOfMemoryError là Error? " + (err instanceof Error));                              // true
        IO.println("OutOfMemoryError là Throwable? " + (err instanceof Throwable));                      // true
    }

    // TODO 1 (mục 1): Hãy tạo một chuỗi 3 method gọi nhau, ném một NullPointerException
    //   ở method sâu nhất. In ra toàn bộ Call Stack và giải thích thứ tự xuất hiện của các method.
    //
    // TODO 2 (mục 2): Viết một method gọi FileReader mà KHÔNG có try-catch và KHÔNG có throws.
    //   Dán lỗi biên dịch (compiler error) vào đây để ghi nhớ quy tắc Catch or Specify.
    //
    // TODO 3 (mục 3): Liệt kê 5 class kế thừa trực tiếp từ RuntimeException và 5 class
    //   kế thừa trực tiếp từ Exception (nhưng không qua RuntimeException).
    //
    // TODO 4 (mục 5): Viết đoạn benchmark nhỏ: so sánh thời gian thực hiện 1.000.000 phép kiểm tra
    //   if (obj == null) so với việc bắt try { obj.toString(); } catch (NullPointerException e).
}
