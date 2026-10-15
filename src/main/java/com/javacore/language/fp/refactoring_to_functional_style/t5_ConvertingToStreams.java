package com.javacore.language.fp.refactoring_to_functional_style;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Converting Data Sources to Streams
 * <p>
 * 0. Vị trí bài này
 * - Bước 16/41, bài 5/5 trong section Refactoring to Functional Style của lộ trình dev.java/learn.
 * - Một câu cho cả bài: Biến nguồn dữ liệu bên ngoài (file, regex, collection) thành Stream để tận dụng pipeline chức năng thay vì vòng lặp while/for thủ công.
 * - Tài liệu gốc: <a href="https://dev.java/learn/language/fp/refactoring-to-functional-style/converting-to-streams/">dev.java - Converting Data Sources to Streams</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CONVERTING DATA SOURCES TO STREAMS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. TƯ DUY THEO DÒNG CHẢY STREAM
 * - (Thinking in Streams)
 * - Các bài trước dùng {@code range()}, {@code rangeClosed()}, {@code iterate()} để tạo stream
 * từ một khoảng số cố định hoặc duyệt mảng/collection.
 * - Nhưng trong thực tế, dữ liệu thường đến từ TÀI NGUYÊN BÊN NGOÀI (external resources), ví dụ từ FILE.
 * - "Whenever you're working with a collection of data from an external resource,
 * ask if there is a way to get a stream of data over the contents of that resource."
 * - Khi có Stream, ta có thể áp dụng toàn bộ sức mạnh của pipeline: {@code filter()}, {@code map()},
 * {@code count()}, {@code reduce()}, v.v.
 * <p>
 * 2. TỪ IMPERATIVE SANG FUNCTIONAL KHI ĐỌC FILE
 * - (From Imperative to Functional Style)
 * - Bài toán: Đếm số dòng trong một file mã nguồn có chứa một từ khóa (ví dụ "public").
 * - Imperative:
 * + Mở {@code BufferedReader} qua {@code Files.newBufferedReader(path)}.
 * + Dùng vòng lặp {@code while ((line = reader.readLine()) != null)}.
 * + Lồng {@code if (line.contains(word))} để tăng biến đếm {@code count++}.
 * - Functional:
 * + Thay vì đọc từng dòng thủ công (external iterator), biến file thành Stream bằng {@code Files.lines(path)}.
 * + Lọc các dòng khớp từ khóa bằng {@code filter(line -> line.contains(word))}.
 * + Đếm kết quả bằng terminal operation {@code count()}.
 * {@code try (Stream<String> lines = Files.lines(path)) { long count = lines.filter(line -> line.contains(word)).count(); }}
 * - Lợi ích: loại bỏ hoàn toàn các biến đếm thừa, phép gán lặp đi lặp lại trong while, và cú pháp rườm rà.
 * <p>
 * 3. BẢNG ÁNH XẠ
 * - (Mappings)
 * - Đọc file dòng-theo-dòng bằng BufferedReader        → {@code Files.lines(path)} hoặc {@code reader.lines()}
 * - Kiểm tra điều kiện {@code if} trên từng dòng        → {@code .filter(...)}
 * - Tăng biến đếm thủ công khi thỏa mãn điều kiện     → {@code .count()}
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. BẮT BUỘC ĐÓNG STREAM KHI GẮN VỚI TÀI NGUYÊN I/O
 * - Bình thường Stream từ Collection không cần close. Nhưng Stream từ I/O như {@code Files.lines(path)},
 * {@code Files.list(path)}, {@code Files.walk(path)} giữ file handle/descriptor bên dưới hệ điều hành!
 * - {@code Stream} implement {@code AutoCloseable}. Do đó, BẮT BUỘC dùng trong {@code try-with-resources}
 * để tránh leak file handle:
 * {@code try (Stream<String> lines = Files.lines(path)) { ... }}
 * <p>
 * 5. Files.lines() VS Files.readAllLines()
 * - {@code Files.readAllLines(path)}: nạp TOÀN BỘ file vào RAM dưới dạng {@code List<String>}.
 * Nếu file vài GB → gây OutOfMemoryError ngay lập tức!
 * - {@code Files.lines(path)}: stream LAZY (lười biếng), chỉ đọc từng dòng khi terminal operation kéo,
 * mức tiêu thụ bộ nhớ RAM giữ mức O(1) bất kể kích thước file.
 * <p>
 * 6. CÁC NGUỒN DỮ LIỆU KHÁC TRONG JDK CÓ THỂ ĐỔI THÀNH STREAM
 * - {@code BufferedReader.lines()}: stream các dòng từ bất kỳ Reader nào.
 * - {@code Files.list(path)}, {@code Files.walk(path)}, {@code Files.find(path)}: duyệt cây thư mục dạng Stream.
 * - {@code Pattern.compile(regex).splitAsStream(CharSequence)}: tách chuỗi regex thành Stream.
 * - {@code CharSequence.chars()}, {@code CharSequence.codePoints()}: biến String thành {@code IntStream}.
 * - {@code Random.ints()}, {@code Random.doubles()}: sinh số ngẫu nhiên dạng Stream.
 * - {@code Arrays.stream(array)}: chuyển mảng bất kỳ thành Stream.
 * <p>
 * 7. ĐỐI CHIẾU VỚI TYPESCRIPT / NODE.JS
 * - Trong Node.js: tương đương với {@code readline.createInterface({ input: fs.createReadStream(...) })}
 * kết hợp với cú pháp lặp bất đồng bộ {@code for await (const line of rl)}.
 * - Java tận dụng cơ chế lazy pull của Stream API giúp xử lý file lớn thanh thoát mà vẫn đồng bộ (synchronous).
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 8. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao Stream tạo bởi Files.lines() BẮT BUỘC phải đặt trong try-with-resources?
 * - Files.lines() và Files.readAllLines() khác nhau căn bản thế nào về mức độ ngốn RAM?
 * - Kể tên 4 nguồn tài nguyên trong JDK ngoài Collection có thể tạo thành Stream.
 * - Khi refactor một đoạn code đọc file sang functional style, biến đếm số dòng khớp được thay bằng hàm nào?
 * - Làm thế nào để biến một chuỗi ký tự String thành một IntStream trong Java?
 */
public class t5_ConvertingToStreams {

    void main() throws IOException {
        Path currentFilePath = Path.of("src/main/java/com/javacore/language/fp/refactoring_to_functional_style/t5_ConvertingToStreams.java");
        String wordOfInterest = "public";

        // ==================================================================
        // MỤC 2 - ĐỌC FILE VÀ ĐẾM SỐ DÒNG: IMPERATIVE VS FUNCTIONAL
        // ==================================================================

        // 1. Imperative: Dùng BufferedReader + while + if + tăng biến đếm thủ công.
        long imperativeCount = 0;
        if (Files.exists(currentFilePath)) {
            try (BufferedReader reader = Files.newBufferedReader(currentFilePath)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(wordOfInterest)) {
                        imperativeCount++;
                    }
                }
            }
        }
        IO.println("Imperative count: " + imperativeCount);

        // 2. Functional: Nhìn nguồn dữ liệu như một dòng chảy (Stream) qua Files.lines().
        // Bắt buộc bọc trong try-with-resources vì Stream này giữ tài nguyên I/O cần giải phóng.
        long functionalCount = 0;
        if (Files.exists(currentFilePath)) {
            try (Stream<String> lines = Files.lines(currentFilePath)) {
                functionalCount = lines.filter(line -> line.contains(wordOfInterest))
                        .count();
            }
        }
        IO.println("Functional count: " + functionalCount);

        // ==================================================================
        // MỤC 6 - CÁC NGUỒN DỮ LIỆU KHÁC BIẾN THÀNH STREAM
        // ==================================================================

        // Chuỗi String biến thành IntStream của các mã ký tự
        "Hello FP".chars()
                .filter(ch -> ch != ' ')
                .forEach(ch -> IO.print((char) ch));
        IO.println();
    }

    // TODO 1 (mục 2): Thử dùng Files.lines() đọc file t1_SimpleLoops.java cùng thư mục,
    //   lọc ra tất cả các dòng bắt đầu bằng "//", và in ra màn hình.
    //
    // TODO 2 (mục 4): Tạo một Stream từ Files.lines() mà KHÔNG bọc trong try-with-resources.
    //   Thử mở liên tục hàng nghìn file trong vòng lặp xem lỗi gì xảy ra (Too many open files).
    //
    // TODO 3 (mục 5): Viết đoạn code dùng Pattern.compile("\\s+").splitAsStream("Java Functional Programming")
    //   và in ra từng từ viết hoa.
    //
    // TODO 4 (mục 6): Dùng Files.list(Path.of(".")) để liệt kê toàn bộ các file/folder
    //   trong thư mục hiện tại dưới dạng Stream<Path>.
}
