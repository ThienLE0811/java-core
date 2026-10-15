package com.javacore.language.annotations_exceptions.exceptions;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Catching and Handling Exceptions
 * <p>
 * 0. Vị trí bài này
 * - Bước 18/41, bài 2/4 trong section Exceptions của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Xử lý ngoại lệ với try-catch-finally, giải phóng tài nguyên an toàn bằng try-with-resources (AutoCloseable) và tránh nuốt lỗi nhờ suppressed exceptions.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/annotations-exceptions/exceptions/catching-handling/">dev.java - Catching and Handling Exceptions</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bên dưới biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CATCHING AND HANDLING EXCEPTIONS (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. KHỐI TRY VÀ NGUYÊN TẮC HOẠT ĐỘNG
 * - (The Try Block)
 * - Khối {@code try} bao bọc các câu lệnh có thể ném ngoại lệ.
 * - Khi một ngoại lệ phát sinh tại một dòng trong {@code try}, các dòng lệnh còn lại trong {@code try}
 *   lập tức BỊ HỦY BỎ; luồng điều khiển nhảy thẳng tới khối {@code catch} phù hợp hoặc khối {@code finally}.
 * <p>
 * 2. KHỐI CATCH VÀ THỨ TỰ BẮT NGOẠI LỆ
 * - (The Catch Blocks)
 * - Đi liền ngay sau {@code try}, không được có câu lệnh nào chen giữa {@code try} và {@code catch}.
 * - Một khối {@code try} có thể đi kèm nhiều khối {@code catch} liên tiếp.
 * - Runtime kiểm tra các khối {@code catch} từ trên xuống dưới theo thứ tự xuất hiện. Khối nào khớp kiểu
 *   đầu tiên (hoặc ngoại lệ ném ra là lớp con của kiểu trong catch) sẽ được chọn thực thi.
 * - QUY TẮC BẮT BUỘC VỀ THỨ TỰ: Ngoại lệ con (cụ thể hơn) PHẢI ĐẶT TRƯỚC ngoại lệ cha (tổng quát hơn).
 *   Ví dụ: {@code FileNotFoundException} phải đứng trước {@code IOException}. Nếu đảo ngược,
 *   compiler báo lỗi ngay: "exception ... has already been caught".
 * - Từ JDK 22+: hỗ trợ biến không tên (unnamed pattern variable {@code _}) khi không dùng tới biến ngoại lệ:
 *   {@code catch (NumberFormatException _)}.
 * <p>
 * 3. MULTI-CATCH (Java 7+)
 * - (Multi-Catching Exceptions)
 * - Cho phép một khối catch xử lý nhiều loại ngoại lệ khác nhau, phân cách bằng dấu gạch đứng {@code |}:
 *   {@code catch (IOException | SQLException ex)}.
 * - Giảm lặp code, tránh cám dỗ bắt kiểu {@code catch (Exception e)} quá rộng.
 * - HAI RÀNG BUỘC BẮT BUỘC:
 *   + Biến ngoại lệ trong multi-catch ngầm định là {@code final}, không thể gán lại giá trị trong thân catch.
 *   + Các kiểu trong multi-catch PHẢI RỜI NHAU (disjoint) - không được có quan hệ kế thừa cha-con.
 *     (Viết {@code catch (FileNotFoundException | IOException ex)} sẽ lỗi compile: "Types in multi-catch must be disjoint").
 * <p>
 * 4. KHỐI FINALLY VÀ BẪY NUỐT LỖI TRUYỀN THỐNG
 * - (The Finally Block)
 * - Luôn luôn được thực thi khi thoát khỏi khối {@code try}, bất kể:
 *   + Thực thi thành công bình thường.
 *   + Gặp ngoại lệ đã được catch xử lý.
 *   + Gặp ngoại lệ chưa được catch và đang văng ngược lên Call Stack.
 *   + Trong {@code try} hoặc {@code catch} có gọi {@code return}, {@code break}, {@code continue}.
 * - Trường hợp DUY NHẤT {@code finally} không chạy: JVM bị tắt cưỡng bức ({@code System.exit()}, crash JVM, mất điện).
 * - BẪY NGUY HIỂM CỦA FINALLY TRUYỀN THỐNG (Masked Exception):
 *   Nếu khối {@code try} ném Exception A, rồi trong {@code finally} gọi {@code close()} lại ném Exception B,
 *   thì Exception B sẽ đè bẹp và XÓA SẠCH Exception A! Lập trình viên hoàn toàn mất dấu nguyên nhân gốc.
 * <p>
 * 5. TRY-WITH-RESOURCES (Java 7+) & GIAO DIỆN AUTOCLOSEABLE
 * - (The try-with-resources Statement)
 * - Cú pháp: {@code try (ResourceType res = new ResourceType()) { ... }}
 * - Tự động gọi {@code close()} khi kết thúc khối try (dù thành công hay ném ngoại lệ).
 * - Mọi lớp cài đặt interface {@code java.lang.AutoCloseable} (hoặc {@code java.io.Closeable}) đều dùng được.
 *   + {@code AutoCloseable}: phương thức {@code void close() throws Exception;}
 *   + {@code Closeable}: kế thừa AutoCloseable, thu hẹp ngoại lệ thành {@code void close() throws IOException;}
 * - Mở nhiều tài nguyên: phân cách bằng dấu chấm phẩy {@code ;}.
 *   THỨ TỰ ĐÓNG: Tự động đóng theo THỨ TỰ NGƯỢC LẠI với thứ tự mở (LIFO - cái mở sau đóng trước).
 * - Từ Java 9: cho phép truyền biến effectively final đã khởi tạo từ trước vào trong ngoặc {@code try (res) { ... }}.
 * <p>
 * 6. SUPPRESSED EXCEPTIONS (NGOẠI LỆ BỊ KÌM NÉN)
 * - (Suppressed Exceptions)
 * - Khắc phục triệt để bẫy nuốt lỗi của finally truyền thống:
 * - Khi cả khối {@code try} ném ngoại lệ A VÀ quá trình tự động đóng tài nguyên trong try-with-resources
 *   ném ngoại lệ B:
 *   + Ngoại lệ A (nguyên nhân gốc trong try) là ngoại lệ chính được ném ra ngoài!
 *   + Ngoại lệ B (phát sinh khi close) được tự động gắn vào A dưới dạng SUPPRESSED EXCEPTION.
 * - Truy xuất danh sách qua: {@code e.getSuppressed()} (trả về mảng {@code Throwable[]}).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BẪY RETURN TRONG FINALLY - "CODE SMELL" CỰC KỲ NGUY HIỂM
 * - Nếu bạn viết {@code return} bên trong khối {@code finally}, nó sẽ GHI ĐÈ toàn bộ giá trị return
 *   của try/catch VÀ NUỐT LUÔN mọi Exception chưa bắt mà không báo lỗi!
 *   TUYỆT ĐỐI KHÔNG BAO GIỜ đặt lệnh {@code return} trong {@code finally}.
 * <p>
 * 8. THỨ TỰ THỰC THI TOÀN DIỆN KHI KẾT HỢP
 * - Khi có cả: try-with-resources + catch + finally:
 *   1. Mã trong thân {@code try} chạy.
 *   2. TẤT CẢ TÀI NGUYÊN trong ngoặc {@code try(...)} được đóng tự động (gọi {@code close()}).
 *   3. Khối {@code catch} chạy (nếu có ngoại lệ từ bước 1 hoặc 2).
 *   4. Khối {@code finally} chạy cuối cùng.
 * <p>
 * 9. ĐỐI CHIẾU VỚI CÁC NGÔN NGỮ KHÁC
 * - Python: Dùng cú pháp {@code with open(...) as f:} (Context Managers dựa trên {@code __enter__} và {@code __exit__}).
 * - C#: Dùng cú pháp {@code using (var res = new ...) { }} (dựa trên interface {@code IDisposable}).
 * - TypeScript: Hỗ trợ cú pháp {@code using res = new ...;} (Explicit Resource Management - TC39 Stage 3).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao khối catch con phải đặt trước khối catch cha? Nếu đặt ngược lại compiler báo lỗi gì?
 * - Hai quy tắc bắt buộc của Multi-catch là gì? Biến exception trong multi-catch có gán lại được không?
 * - Khối finally luôn luôn chạy trong những trường hợp nào? Trường hợp nào finally KHÔNG chạy?
 * - Vì sao try-with-resources tốt hơn việc gọi close() trong finally truyền thống?
 * - Suppressed Exception là gì? Lấy ra bằng phương thức nào?
 * - Thứ tự đóng các tài nguyên trong try-with-resources là cùng chiều hay ngược chiều thứ tự mở?
 * - Viết return trong khối finally gây ra hậu quả tai hại gì?
 */
public class t2_CatchingHandling {

    // Tài nguyên tự chế để kiểm chứng cơ chế đóng tự động và thứ tự đóng
    static class CustomResource implements AutoCloseable {
        private final String name;
        private final boolean throwOnClose;

        public CustomResource(String name, boolean throwOnClose) {
            this.name = name;
            this.throwOnClose = throwOnClose;
            IO.println("  -> Mở tài nguyên: " + name);
        }

        public void doWork(boolean throwError) {
            IO.println("  -> " + name + " đang thực hiện công việc...");
            if (throwError) {
                throw new RuntimeException("Lỗi xảy ra trong thân try khi đang dùng " + name);
            }
        }

        @Override
        public void close() throws Exception {
            IO.println("  -> Đang đóng tài nguyên: " + name);
            if (throwOnClose) {
                throw new IOException("Lỗi phát sinh khi đóng tài nguyên " + name);
            }
        }
    }

    // ==================================================================
    // MỤC 2 & 4: TRY-CATCH-FINALLY TRUYỀN THỐNG VÀ THỨ TỰ CHẠY
    // ==================================================================

    static int testFinallyOrder(boolean hasError) {
        IO.println("Bắt đầu testFinallyOrder (hasError=" + hasError + "):");
        try {
            if (hasError) {
                throw new ArithmeticException("Lỗi chia 0");
            }
            IO.println("1. Trong try thành công");
            return 10;
        } catch (ArithmeticException e) {
            IO.println("2. Bắt lỗi trong catch: " + e.getMessage());
            return 20;
        } finally {
            // finally LUÔN chạy trước khi kết quả return được trả về caller!
            IO.println("3. Khối finally LUÔN chạy!");
        }
    }

    // ==================================================================
    // MỤC 3: MULTI-CATCH
    // ==================================================================

    static void testMultiCatch(int type) {
        try {
            if (type == 1) {
                throw new IOException("Lỗi đọc đĩa");
            } else if (type == 2) {
                throw new SQLException("Lỗi kết nối CSDL");
            } else {
                IO.println("Thực hiện bình thường");
            }
        } catch (IOException | SQLException ex) {
            // ex là implicitly final, không thể gán lại: ex = new IOException(); // LỖI BIÊN DỊCH
            IO.println("Multi-catch bắt được: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    // ==================================================================
    // MỤC 5 & 6: TRY-WITH-RESOURCES & SUPPRESSED EXCEPTIONS
    // ==================================================================

    static void testTryWithResourcesAndSuppressed() {
        IO.println("\n--- TEST TRY-WITH-RESOURCES (THỨ TỰ ĐÓNG VÀ SUPPRESSED EXCEPTION) ---");
        try (
                // Mở res1 trước, res2 sau -> res2 sẽ được đóng trước, res1 đóng sau (LIFO)
                CustomResource res1 = new CustomResource("Resource-1", true);
                CustomResource res2 = new CustomResource("Resource-2", true)
        ) {
            // Ném ngoại lệ chính trong thân try
            res1.doWork(false);
            res2.doWork(true); // Ném RuntimeException
        } catch (Exception e) {
            // Ngoại lệ chính được ném ra là ngoại lệ từ thân try
            IO.println("Bắt được ngoại lệ CHÍNH: " + e.getMessage());

            // Lấy danh sách các ngoại lệ bị kìm nén phát sinh từ quá trình gọi close()
            Throwable[] suppressed = e.getSuppressed();
            IO.println("Số lượng ngoại lệ bị kìm nén (suppressed): " + suppressed.length);
            for (Throwable s : suppressed) {
                IO.println("  [Suppressed]: " + s.getClass().getSimpleName() + " - " + s.getMessage());
            }
        }
    }

    // ==================================================================
    // CHƯƠNG TRÌNH CHÍNH
    // ==================================================================

    void main() {
        // 1. Kiểm tra luồng finally khi không lỗi và có lỗi
        int resSuccess = testFinallyOrder(false);
        IO.println("Kết quả trả về: " + resSuccess);

        IO.println();
        int resError = testFinallyOrder(true);
        IO.println("Kết quả trả về: " + resError);

        // 2. Kiểm tra multi-catch
        IO.println("\n--- TEST MULTI-CATCH ---");
        testMultiCatch(1);
        testMultiCatch(2);

        // 3. Kiểm tra try-with-resources và suppressed exception
        testTryWithResourcesAndSuppressed();
    }

    // TODO 1 (mục 2): Viết ví dụ có 2 catch block: catch (IOException e) và
    //   catch (FileNotFoundException e). Đặt IOException lên trước và dán lỗi compiler vào đây.
    //
    // TODO 2 (mục 3): Thử viết multi-catch với 2 class có quan hệ cha-con:
    //   catch (FileNotFoundException | IOException e). Dán lỗi compiler vào đây.
    //
    // TODO 3 (mục 7): Viết một method có try { return 1; } finally { return 2; }.
    //   Chạy thử xem kết quả in ra là gì và giải thích vì sao đây là bẫy nguy hiểm.
    //
    // TODO 4 (mục 5): Dùng try-with-resources để đọc dòng đầu tiên của file t1_WhatIsAnException.java
    //   bằng BufferedReader. Không cần viết finally để đóng file.
}
