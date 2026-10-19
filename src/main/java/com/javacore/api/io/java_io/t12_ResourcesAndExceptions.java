package com.javacore.api.io.java_io;

/**
 * Releasing Resources and Catching Exceptions
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 12/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Bài mở đầu nhóm File Operations Basics - năm bài còn lại của nhóm đều mở stream, nên bài này
 * dựng luật chơi chung trước: mở cái gì thì phải đóng cái đó, và hỏng thì bắt lỗi ra sao.
 * - Một câu cho cả bài: mọi tài nguyên I/O đều {@code Closeable}, nên luôn dùng
 * try-with-resources; và {@code FileSystemException} nói được NGUYÊN NHÂN chứ không chỉ
 * một dòng message chung chung.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/reading-writing/common-operations/">dev.java - Releasing Resources and Catching Exceptions</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - RELEASING RESOURCES AND CATCHING EXCEPTIONS (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. GIẢI PHÓNG TÀI NGUYÊN HỆ THỐNG
 * - (Releasing System Resources)
 * - Nhiều tài nguyên trong API I/O cài đặt {@code java.io.Closeable}, nghĩa là có method
 * {@code close()}. Mở xong mà không đóng thì hiệu năng ứng dụng đi xuống, để lâu thì ứng dụng
 * sập hẳn.
 * - try-with-resources tự động gọi {@code close()} khi ra khỏi khối, dù ra bình thường hay
 * ra vì exception.
 * - Hai exception hay gặp: {@code IOException} là lỗi chung của mọi thao tác I/O;
 * {@code FileNotFoundException} khi mở file đọc mà không thấy.
 * <p>
 * 2. ĐÓNG NHIỀU TÀI NGUYÊN
 * - (Closing Several Resources)
 * - Khi bọc stream này trong stream kia (bài t16), có NHIỀU tài nguyên trung gian và phải đóng
 * ĐÚNG THỨ TỰ - đóng từ ngoài vào trong.
 * - try-with-resources khai báo nhiều biến cách nhau dấu chấm phẩy; JDK tự đóng theo thứ tự
 * NGƯỢC với thứ tự khai báo, tức là đúng thứ tự cần thiết.
 * - Từ Java SE 7 trở đi, các factory method của lớp {@code Files} dựng sẵn chuỗi bọc cho bạn,
 * nên thường chỉ còn một tài nguyên phải quản lý.
 * <p>
 * 3. BẮT EXCEPTION
 * - (Catching Exceptions)
 * - I/O luôn có yếu tố bất định: file có thể biến mất, quyền có thể bị đổi, và không phải
 * file system nào cũng hỗ trợ đủ mọi tính năng.
 * - "the compiler automatically generates the code to close the resource(s)" - đó là lý do
 * try-with-resources được coi là thực hành tốt nhất, hơn hẳn try-catch-finally.
 * - {@code FileSystemException} (lớp con của {@code IOException}) cho bốn method chẩn đoán:
 * + {@code getFile()} - file gây lỗi.
 * + {@code getOtherFile()} - file thứ hai liên quan, ví dụ đích của một phép copy.
 * + {@code getReason()} - mô tả nguyên nhân.
 * + {@code getMessage()} - thông điệp tổng hợp.
 * - try-catch-finally vẫn dùng được nhưng là lối cũ, dễ viết sót.
 * <p>
 * 4. VARARGS
 * - (Using Varargs)
 * - Ký hiệu ba chấm cho phép truyền số lượng tham số tùy ý, hoặc truyền thẳng một mảng.
 * - Trong API này nó xuất hiện ở {@code CopyOption...}, {@code OpenOption...},
 * {@code LinkOption...} (đã gặp ở bài t5, t6; sẽ gặp lại ở t13).
 * <p>
 * 5. NỐI CHUỖI LỜI GỌI
 * - (Method Chaining)
 * - Gọi liên tiếp trên giá trị trả về, cho "compact code and enables you to avoid declaring
 * temporary variables".
 * - Ví dụ quen thuộc: {@code Path.of("a").resolve("b").toAbsolutePath().normalize()}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. VÌ SAO try-with-resources KHÔNG CHỈ LÀ CÚ PHÁP ĐẸP - BẪY EXCEPTION BỊ NUỐT
 * - Viết tay bằng try-finally có một lỗi kín: nếu thân {@code try} ném exception VÀ
 * {@code close()} trong {@code finally} cũng ném, thì exception của {@code finally} ĐÈ LÊN,
 * nuốt mất nguyên nhân thật. Đây là lỗi rất khó truy khi đọc log.
 * - try-with-resources giải đúng chuyện đó: exception của thân được giữ làm chính, exception
 * lúc đóng được gắn vào làm SUPPRESSED, đọc lại bằng {@code e.getSuppressed()}.
 * - Hệ quả: kể cả khi chỉ có một tài nguyên, try-with-resources vẫn hơn try-finally.
 * <p>
 * 7. BA LUẬT THỰC DỤNG
 * - MỞ THÌ ĐÓNG, NHẬN THÌ KHÔNG. Cái gì mình gọi ra ({@code Files.newBufferedReader},
 * {@code Files.newInputStream}, {@code Files.list}, {@code Files.walk}) thì mình đóng.
 * {@code System.in} và {@code System.out} KHÔNG đóng - đóng là mất luôn đầu ra của cả chương trình.
 * - CÁC METHOD TRẢ {@code Stream} CỦA {@code Files} ĐỀU PHẢI ĐÓNG. {@code Files.lines},
 * {@code Files.list}, {@code Files.walk}, {@code Files.find} - đây là bẫy hay gặp nhất của cả
 * section, vì nhìn giống {@code List.stream()} vốn không cần đóng (nhắc lại bài t9 mục 5).
 * - KHÔNG NUỐT EXCEPTION. {@code catch (IOException e) {}} rỗng là tội nặng nhất trong code I/O.
 * Tối thiểu phải log kèm {@code getFile()} và {@code getReason()}.
 * <p>
 * 8. CÂY EXCEPTION CẦN NHỚ
 * <p>
 * .  IOException
 * .   +- FileNotFoundException            (API cũ java.io, khi mở file để đọc)
 * .   +- EOFException                     (hết dữ liệu giữa chừng - bài t16)
 * .   +- UncheckedIOException             (thật ra là RuntimeException bọc IOException)
 * .   +- FileSystemException              (API mới java.nio.file - có getFile/getReason)
 * .       +- NoSuchFileException
 * .       +- FileAlreadyExistsException
 * .       +- DirectoryNotEmptyException
 * .       +- NotLinkException
 * .       +- FileSystemLoopException
 * .       +- AtomicMoveNotSupportedException
 * <p>
 * - Chú ý {@code NoSuchFileException} (API mới) KHÔNG phải lớp con của
 * {@code FileNotFoundException} (API cũ). Bắt nhầm cái kia là không bắt được.
 * - {@code UncheckedIOException} tồn tại vì lambda trong Stream API không khai báo throws
 * được. Nó là thứ {@code Files.lines} / {@code Files.walk} ném ra khi lỗi xảy ra giữa chừng
 * stream - và vì unchecked nên trình biên dịch KHÔNG nhắc bạn bắt.
 * <p>
 * 9. GÓC NHÌN PRODUCTION
 * - Trong Spring Boot, tầng service thường không nên để {@code IOException} rò lên controller.
 * Bọc lại thành exception nghiệp vụ của ứng dụng và để {@code @RestControllerAdvice} dịch
 * sang mã HTTP. Giữ nguyên exception gốc làm {@code cause} để không mất dấu vết.
 * - Đừng bắt {@code Exception} cho gọn; bắt {@code NoSuchFileException} riêng để trả 404,
 * {@code AccessDeniedException} riêng để trả 403 - đúng tinh thần "nói được nguyên nhân"
 * mà bài t1 đã nêu khi chê {@code File.delete()} trả về {@code false}.
 * - Rò file handle không gây lỗi ngay. Nó tích lũy và nổ sau nhiều giờ chạy với
 * {@code Too many open files}. Vì thế đây là loại bug phải chặn lúc viết code, không thể
 * trông vào test.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - JS/TS KHÔNG có try-with-resources. Tương đương gần nhất là {@code try/finally} với
 * {@code await fileHandle.close()}, và nó dính đúng bẫy nuốt exception ở mục 6.
 * - Phần lớn code Node dùng {@code fs.readFile}/{@code fs.writeFile} vốn tự đóng, nên lập
 * trình viên TS ít khi nghĩ tới chuyện đóng - chuyển sang Java phải tập lại thói quen này.
 * - {@code await using} (TC39 explicit resource management, TypeScript 5.2+) chính là
 * try-with-resources phiên bản JS, ra sau Java 15 năm.
 * - Java phân biệt checked và unchecked exception; JS không có khái niệm checked, nên
 * {@code UncheckedIOException} ở mục 8 không có tương đương.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Interface nào đánh dấu "cái này phải đóng"?
 * - try-with-resources đóng nhiều tài nguyên theo thứ tự nào?
 * - Thân try ném exception, {@code close()} cũng ném - try-with-resources giữ cái nào,
 * cái còn lại đi đâu?
 * - Bốn method chẩn đoán của {@code FileSystemException} là gì?
 * - Kể bốn method của {@code Files} trả về {@code Stream} và phải đóng.
 * - {@code NoSuchFileException} có phải con của {@code FileNotFoundException} không?
 * - {@code UncheckedIOException} sinh ra để giải quyết vấn đề gì?
 * - Có nên đóng {@code System.out} không, vì sao?
 */
public class t12_ResourcesAndExceptions {

    void main() {
        // TODO 1 (mục 1): Mở một file bằng Files.newBufferedReader trong try-with-resources,
        //   đọc một dòng rồi in ra. Sau đó viết lại đúng đoạn đó bằng try-catch-finally
        //   và đếm số dòng của hai cách.
        //
        // TODO 2 (mục 6): Viết một lớp nhỏ cài đặt AutoCloseable mà close() luôn ném exception.
        //   Dùng nó trong try-with-resources với thân try cũng ném exception. In ra
        //   e.getMessage() và e.getSuppressed() để xác nhận exception nào được giữ.
        //
        // TODO 3 (mục 6): Làm lại TODO 2 bằng try-finally viết tay. Xác nhận exception của thân
        //   try bị NUỐT MẤT. Đây là lý do của cả mục 6.
        //
        // TODO 4 (mục 3): Gọi Files.delete trên một file không tồn tại, bắt FileSystemException
        //   và in đủ getFile(), getReason(), getOtherFile(), getMessage(). So sánh lượng thông tin.
        //
        // TODO 5 (mục 3, 8): Gọi Files.copy từ file không tồn tại sang một đích. Bắt
        //   FileSystemException và xem getOtherFile() trả về gì.
        //
        // TODO 6 (mục 8): Viết hai khối catch, một bắt FileNotFoundException, một bắt
        //   NoSuchFileException. Gây lỗi bằng Files.newBufferedReader trên file không tồn tại
        //   và xem khối nào chạy.
        //
        // TODO 7 (mục 7, 8): Gọi Files.lines(...) KHÔNG dùng try-with-resources, lặp 100000 lần
        //   trên một file nhỏ. Quan sát chương trình hỏng ở đâu và với exception gì.
        //   (Chạy riêng, có thể làm treo máy - đặt số vòng nhỏ trước rồi tăng dần.)
        //
        // TODO 8 (mục 5): Viết một chuỗi nối lời gọi dài trên Path (ít nhất 4 method) rồi viết
        //   lại bằng biến tạm. Nêu khi nào chuỗi nối làm code KHÓ đọc hơn.
    }
}
