package com.javacore.language.annotations_exceptions.exceptions;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Throwing Exceptions
 * <p>
 * 0. Vị trí bài này
 * - Bước 18/41, bài 3/4 trong section Exceptions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Ném ngoại lệ chủ động bằng throw, bàn giao trách nhiệm cho cấp gọi bằng throws, tự tạo Custom Exception nghiệp vụ và giữ trọn vết lỗi gốc bằng Chained Exceptions (cause).
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/annotations-exceptions/exceptions/throwing/">dev.java - Throwing Exceptions</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bên dưới biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THROWING EXCEPTIONS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. KHAI BÁO NGOẠI LỆ TRÊN CHỮ KÝ HÀM (THROWS CLAUSE)
 * - (Specifying the Exceptions Thrown by a Method)
 * - Khi một method phát sinh ngoại lệ mà bản thân nó không có đủ ngữ cảnh hoặc không nên tự xử lý
 *   (ví dụ: class trong thư viện tái sử dụng), nó nên để các method ở tầng cao hơn trong Call Stack xử lý.
 * - Khai báo bằng từ khóa {@code throws} kèm danh sách các ngoại lệ phân tách bằng dấu phẩy:
 *   {@code public void writeData() throws IOException, SQLException { ... }}
 * - Vị trí: Đặt sau danh sách tham số và trước dấu mở ngoặc nhọn {@code {} của thân hàm.
 * - BẮT BUỘC đối với Checked Exception; KHÔNG BẮT BUỘC đối với Unchecked Exception (nhưng có thể ghi để tài liệu hóa).
 * <p>
 * 2. CÂU LỆNH NÉM NGOẠI LỆ (THE THROW STATEMENT)
 * - Tất cả các ngoại lệ trong Java đều được ném ra bằng câu lệnh {@code throw}.
 * - Cú pháp: {@code throw throwableObject;}
 *   Ví dụ: {@code throw new IllegalArgumentException("Tham số không được âm");}
 * - Điều kiện tiên quyết: Đối tượng được ném BẮT BUỘC phải kế thừa từ {@code java.lang.Throwable}.
 * - Ngay khi gặp lệnh {@code throw}, luồng chạy hiện tại lập tức DỪNG LẠI và bắt đầu quá trình
 *   lội ngược Call Stack tìm kiếm handler.
 * <p>
 * 3. PHÂN BIỆT RÕ RÀNG: THROW VS THROWS
 * - {@code throw}  (Động từ hành động): Dùng BÊN TRONG thân hàm để chủ động ném ra 1 đối tượng ngoại lệ cụ thể (instance).
 * - {@code throws} (Lời khai báo cam kết): Dùng TRÊN CHỮ KÝ HÀM để cảnh báo caller biết method này có thể ném ra những Class ngoại lệ nào.
 * <p>
 * 4. CHUỖI NGOẠI LỆ (CHAINED EXCEPTIONS)
 * - (Chained Exceptions)
 * - Tình huống kinh điển: Một ứng dụng bắt được ngoại lệ tầng thấp (ví dụ {@code SQLException} hoặc {@code IOException}),
 *   sau đó muốn ném ra ngoại lệ nghiệp vụ tầng cao hơn (ví dụ {@code OrderProcessingException}) để caller dễ hiểu.
 * - VẤN ĐỀ: Nếu ném ngoại lệ mới mà không đính kèm ngoại lệ cũ, toàn bộ nguyên nhân gốc (root cause)
 *   và Call Stack ban đầu sẽ BỊ MẤT DẤU VẾT!
 * - GIẢI PHÁP - Chained Exceptions:
 *   + Constructor có tham số {@code cause}: {@code new CustomException("Lỗi xử lý đơn hàng", originalException);}
 *   + Phương thức: {@code initCause(Throwable cause)} (dùng khi constructor không hỗ trợ truyền cause).
 *   + Lấy lại nguyên nhân gốc: {@code e.getCause()}.
 *   + Khi in {@code printStackTrace()}, JVM sẽ tự động in chuỗi liên hoàn: "Caused by: java.sql.SQLException...".
 * <p>
 * 5. TRUY XUẤT THÔNG TIN NGĂN XẾP (STACK TRACE)
 * - (Accessing Stack Trace Information)
 * - Định nghĩa: Stack trace cung cấp thông tin lịch sử thực thi của Thread tại thời điểm ngoại lệ xảy ra.
 * - {@code e.getStackTrace()}: trả về mảng {@code StackTraceElement[]}.
 *   Mỗi phần tử đại diện cho một stack frame, gồm: {@code getClassName()}, {@code getMethodName()},
 *   {@code getFileName()}, {@code getLineNumber()}.
 * - Logging chuyên nghiệp: Sử dụng thư viện Log (như SLF4J / Logback) truyền trực tiếp Exception vào
 *   ({@code logger.error("Chi tiết lỗi", e);}) để thư viện tự định dạng vết ngăn xếp tối ưu.
 * <p>
 * 6. TỰ TẠO CUSTOM EXCEPTION (CREATING EXCEPTION CLASSES)
 * - Khi nào nên tự viết Exception?
 *   + Cần loại lỗi riêng phản ánh chính xác nghiệp vụ mà JDK chưa có (ví dụ: {@code InsufficientBalanceException}).
 *   + Cần gắn thêm thuộc tính nghiệp vụ (ví dụ: {@code errorCode}, {@code accountId}, {@code timestamp}).
 *   + Giúp người dùng thư viện dễ dàng phân biệt lỗi của package bạn viết với lỗi chung của Java.
 * - Quy tắc thiết kế chuẩn:
 *   + Luôn có hậu tố {@code Exception} trong tên class (ví dụ {@code UserNotFoundException}).
 *   + Thường kế thừa trực tiếp từ {@code Exception} (nếu muốn là Checked) hoặc {@code RuntimeException} (nếu muốn là Unchecked).
 *   + TUYỆT ĐỐI KHÔNG kế thừa từ {@code Throwable} hay {@code Error}.
 *   + Cung cấp đủ 4 constructor chuẩn mực của Java Exception.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. NÊN KẾ THỪA EXCEPTION HAY RUNTIMEXCEPTION CHO CUSTOM EXCEPTION?
 * - Trong các framework Java hiện đại (Spring Boot, Hibernate, Quarkus), 95% Custom Exception
 *   được thiết kế kế thừa từ {@code RuntimeException} (Unchecked Exception).
 * - Lý do: Checked Exception làm bẩn chữ ký hàm (polluted signatures), phải sửa code từ DAO lên Controller
 *   nếu thêm exception mới, và KHÔNG tương thích với Lambda/Stream API (Functional Interface không throws checked exception).
 * <p>
 * 8. ANTI-PATTERN: NÉM LẠI MÀ QUÊN KÈM CAUSE (RETHROWING WITHOUT CAUSE)
 * - Sai:   {@code catch (SQLException e) { throw new ServiceException("Lỗi CSDL"); }}  -> Mất toàn bộ nguyên nhân!
 * - Đúng:  {@code catch (SQLException e) { throw new ServiceException("Lỗi CSDL", e); }} -> Bảo toàn nguyên nhân gốc.
 * <p>
 * 9. ĐỐI CHIẾU VỚI TYPESCRIPT / JS
 * - Trong TypeScript/JavaScript: Tạo custom error bằng {@code class CustomError extends Error { ... }}.
 * - Từ ECMAScript 2022 (ES2022), JavaScript cũng đã chính thức hỗ trợ Chained Exception chuẩn hóa
 *   thông qua thuộc tính {@code cause}: {@code throw new Error("Lỗi nghiệp vụ", { cause: err });}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Phân biệt từ khóa throw và throws (vị trí đặt, đối số nhận vào)?
 * - Chained Exception là gì? Nếu không truyền biến nguyên nhân gốc vào constructor thì dùng hàm nào?
 * - getCause() trả về cái gì? Trong chuỗi log thì đoạn nào thể hiện Chained Exception?
 * - Khi nào một class được phép dùng trong câu lệnh throw?
 * - Kể 4 constructor tiêu chuẩn nên có khi viết một Custom Exception.
 * - Vì sao trong các ứng dụng Spring hiện đại người ta ưu tiên kế thừa RuntimeException hơn Exception?
 * - JavaScript ES2022 hỗ trợ Chained Exception qua cú pháp nào?
 */
public class t3_ThrowingException {

    // ==================================================================
    // MỤC 6: CUSTOM EXCEPTION TIÊU CHUẨN (KẾ THỪA RUNTIMEEXCEPTION)
    // ==================================================================

    public static class InsufficientBalanceException extends RuntimeException {
        private final String accountId;
        private final double requiredAmount;
        private final double currentBalance;

        // 1. Constructor mặc định
        public InsufficientBalanceException() {
            super("Số dư tài khoản không đủ để thực hiện giao dịch");
            this.accountId = "N/A";
            this.requiredAmount = 0;
            this.currentBalance = 0;
        }

        // 2. Constructor nhận message
        public InsufficientBalanceException(String message) {
            super(message);
            this.accountId = "N/A";
            this.requiredAmount = 0;
            this.currentBalance = 0;
        }

        // 3. Constructor hỗ trợ Chained Exception: nhận message + cause
        public InsufficientBalanceException(String message, Throwable cause) {
            super(message, cause);
            this.accountId = "N/A";
            this.requiredAmount = 0;
            this.currentBalance = 0;
        }

        // 4. Constructor nhận cause
        public InsufficientBalanceException(Throwable cause) {
            super(cause);
            this.accountId = "N/A";
            this.requiredAmount = 0;
            this.currentBalance = 0;
        }

        // 5. Constructor đặc thù nghiệp vụ gắn thêm các trường dữ liệu
        public InsufficientBalanceException(String accountId, double requiredAmount, double currentBalance) {
            super("Tài khoản " + accountId + " không đủ tiền: Cần " + requiredAmount + " nhưng chỉ có " + currentBalance);
            this.accountId = accountId;
            this.requiredAmount = requiredAmount;
            this.currentBalance = currentBalance;
        }

        public String getAccountId() { return accountId; }
        public double getRequiredAmount() { return requiredAmount; }
        public double getCurrentBalance() { return currentBalance; }
    }

    // Custom Checked Exception (kế thừa trực tiếp Exception)
    public static class PaymentGatewayException extends Exception {
        public PaymentGatewayException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ==================================================================
    // MỤC 1 & 2: SỬ DỤNG THROW VÀ THROWS
    // ==================================================================

    // Rút tiền: chủ động ném InsufficientBalanceException (Unchecked)
    static void withdraw(String accountId, double balance, double amount) {
        if (amount <= 0) {
            // throw trực tiếp một exception có sẵn của JDK
            throw new IllegalArgumentException("Số tiền rút phải lớn hơn 0: " + amount);
        }
        if (amount > balance) {
            // throw Custom Exception nghiệp vụ
            throw new InsufficientBalanceException(accountId, amount, balance);
        }
        IO.println("Rút thành công " + amount + " từ tài khoản " + accountId);
    }

    // ==================================================================
    // MỤC 4: MINH HỌA CHAINED EXCEPTION (BỌC LỖI TẦNG THẤP)
    // ==================================================================

    // Giả lập tầng kết nối cổng thanh toán ném IOException
    static void callExternalBankService() throws IOException {
        throw new IOException("Connection timed out tới máy chủ ngân hàng Vietcombank");
    }

    // Tầng Service: bắt IOException tầng dưới, bọc vào PaymentGatewayException tầng trên
    static void processPayment() throws PaymentGatewayException {
        try {
            callExternalBankService();
        } catch (IOException e) {
            // CHAINED EXCEPTION: Truyền 'e' làm tham số cause vào PaymentGatewayException
            throw new PaymentGatewayException("Thanh toán thất bại do sự cố đường truyền ngân hàng", e);
        }
    }

    // ==================================================================
    // CHƯƠNG TRÌNH CHÍNH
    // ==================================================================

    void main() {
        // 1. Kiểm tra ném Custom Exception nghiệp vụ
        IO.println("--- 1. TEST NÉM CUSTOM EXCEPTION NGHIỆP VỤ ---");
        try {
            withdraw("ACC-9999", 500.0, 1200.0);
        } catch (InsufficientBalanceException e) {
            IO.println("Bắt được ngoại lệ: " + e.getMessage());
            IO.println("Chi tiết nghiệp vụ: Account=" + e.getAccountId()
                    + ", Cần=" + e.getRequiredAmount()
                    + ", Hiện có=" + e.getCurrentBalance());
        }

        // 2. Kiểm tra Chained Exception và truy xuất nguyên nhân gốc (getCause)
        IO.println("\n--- 2. TEST CHAINED EXCEPTION (TRUY XUẤT NGUYÊN NHÂN GỐC) ---");
        try {
            processPayment();
        } catch (PaymentGatewayException e) {
            IO.println("Bắt được ngoại lệ tầng cao: " + e.getClass().getSimpleName() + " - " + e.getMessage());

            // Lấy nguyên nhân gốc ban đầu
            Throwable rootCause = e.getCause();
            if (rootCause != null) {
                IO.println("Nguyên nhân gốc (Root Cause): " + rootCause.getClass().getSimpleName() + " - " + rootCause.getMessage());
            }

            // 3. Đọc Stack Trace
            IO.println("\n--- 3. TRUY XUẤT STACK TRACE THỦ CÔNG ---");
            StackTraceElement[] stack = e.getStackTrace();
            if (stack.length > 0) {
                StackTraceElement top = stack[0];
                IO.println("Ngoại lệ phát sinh tại: " + top.getClassName() + "." + top.getMethodName()
                        + " (" + top.getFileName() + ":" + top.getLineNumber() + ")");
            }
        }
    }

    // TODO 1 (mục 6): Tạo một Custom Exception tên là ResourceNotFoundException kế thừa
    //   RuntimeException. Thêm trường resourceName (String) và resourceId (Object).
    //
    // TODO 2 (mục 4): Viết một chuỗi 3 tầng ngoại lệ lồng nhau:
    //   SQLException -> DaoException -> ServiceException.
    //   Dùng vòng lặp while (cause != null) để in ra toàn bộ cây nguyên nhân từ ngọn tới gốc.
    //
    // TODO 3 (mục 4): Thử nghiệm phương thức initCause(): Tạo một Exception bằng constructor
    //   mặc định (không truyền cause), sau đó dùng e.initCause(originalError) để gắn nguyên nhân.
    //
    // TODO 4 (mục 8): Hãy giải thích vì sao đoạn code sau là một anti-pattern nguy hiểm:
    //   try { doDb(); } catch (SQLException e) { throw new RuntimeException("DB Error"); }
}
