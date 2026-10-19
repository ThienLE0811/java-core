package com.javacore.api.io.java_io;

/**
 * Reading and Writing Text Files
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 14/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File Operations Basics của section.
 * - Một câu cho cả bài: file text đọc ghi qua {@code BufferedReader}/{@code BufferedWriter} do
 * {@code Files} tạo ra - và chuyện sống còn ở đây là BẢNG MÃ, vì text trên đĩa là byte còn
 * {@code String} trong Java là Unicode.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/reading-writing/buffered-text/">dev.java - Reading and Writing Text Files</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING AND WRITING TEXT FILES (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. XỬ LÝ KÝ TỰ
 * - (Understanding the Character Handling)
 * - Java lưu ký tự trong bộ nhớ bằng Unicode.
 * - Character stream ({@code Reader}/{@code Writer}) TỰ ĐỘNG dịch qua lại giữa Unicode và
 * bảng mã bên ngoài. Đây chính là điểm khác biệt với byte stream ở bài t1.
 * - Mốc quan trọng: TỪ JAVA SE 18, bảng mã mặc định là UTF-8. "Until Java SE 17, a program
 * that uses character streams automatically adapts to the local character set" - trước đó
 * bảng mã mặc định đổi theo locale của máy.
 * - Hệ quả: cùng một đoạn code chạy trên hai máy khác locale có thể ra hai kết quả khác nhau
 * nếu dùng JDK cũ hơn 18.
 * <p>
 * 2. ĐỌC FILE TEXT CÓ ĐỆM
 * - (Reading a Text File by Using Buffered Stream I/O)
 * - {@code Files.newBufferedReader(path, charset)} trả về {@code BufferedReader}.
 * - Đọc từng dòng bằng {@code readLine()} trong vòng lặp. "The line string does not contain
 * the line termination characters" - chuỗi trả về KHÔNG chứa ký tự xuống dòng.
 * - {@code readLine()} trả về {@code null} khi hết file. Đây là điều kiện dừng vòng lặp.
 * - Từ Java SE 8 có {@code reader.lines()} trả về {@code Stream<String>}.
 * - {@code Stream} cài đặt {@code AutoCloseable} nên đặt được trong try-with-resources
 * (bài t12).
 * <p>
 * 3. GHI FILE TEXT CÓ ĐỆM
 * - (Writing a Text File by Using Buffered Stream I/O)
 * - {@code Files.newBufferedWriter(path, charset, OpenOption...)} trả về {@code BufferedWriter}.
 * - Nhận cả bảng mã lẫn {@code OpenOption} (bài t13 mục 3), nên ghi nối hay ghi đè đều được.
 * - Ví dụ trang gốc dùng bảng mã US-ASCII.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. "CÓ ĐỆM" NGHĨA LÀ GÌ - VÀ VÌ SAO NÓ QUAN TRỌNG ĐẾN THẾ
 * - Không đệm: mỗi lời gọi {@code read()} là một lần gọi xuống hệ điều hành (system call),
 * tốn hàng nghìn chu kỳ CPU cho một ký tự.
 * - Có đệm: JDK đọc sẵn một khối (mặc định 8192 ký tự) vào mảng trong RAM, các lời gọi sau
 * lấy từ mảng đó. Chênh lệch tốc độ khi đọc từng ký tự là hàng trăm lần.
 * - Luật: bọc đệm quanh MỌI stream mà bạn đọc/ghi từng phần nhỏ. Đây là ví dụ kinh điển của
 * decorator pattern - bài t16 sẽ mổ xẻ.
 * - Phía GHI, đệm còn kéo theo một nghĩa vụ: dữ liệu nằm trong bộ đệm chưa xuống đĩa cho tới
 * khi {@code flush()} hoặc {@code close()}. Quên đóng {@code BufferedWriter} là MẤT phần
 * cuối file - một lỗi im lặng, file vẫn tồn tại nhưng thiếu nội dung.
 * <p>
 * 5. BA CÁCH ĐỌC TEXT THEO DÒNG - KHÁC NHAU Ở ĐÂU
 * <p>
 * .  cách                              nạp vào RAM    phải đóng   dùng khi
 * .  Files.readAllLines(p)             TOÀN BỘ        không       file nhỏ, cần truy cập lại
 * .  Files.lines(p)                    từng dòng      CÓ          file lớn, xử lý một lượt
 * .  Files.newBufferedReader(p)        từng dòng      CÓ          cần kiểm soát vòng lặp
 * <p>
 * - {@code Files.lines(p)} thực chất là {@code newBufferedReader} rồi gọi {@code lines()},
 * cộng thêm việc đăng ký đóng reader khi stream đóng. Dùng nó cho gọn.
 * - Vẫn nhắc lại bẫy bài t9/t12: {@code Files.lines} TRẢ STREAM PHẢI ĐÓNG.
 * <p>
 * 6. BẢNG MÃ - PHẦN DỄ SAI NHẤT, ĐẶC BIỆT VỚI TIẾNG VIỆT
 * - LUÔN TRUYỀN CHARSET TƯỜNG MINH: {@code StandardCharsets.UTF_8}. Đừng dựa vào mặc định,
 * kể cả trên JDK 18+, vì file có thể do hệ thống khác sinh ra.
 * - Đọc file UTF-8 bằng bảng mã sai KHÔNG ném exception theo mặc định - nó âm thầm thay ký tự
 * hỏng bằng {@code U+FFFD} (hiển thị là dấu hỏi trong ô vuông). Lỗi dữ liệu im lặng,
 * phát hiện rất muộn.
 * - Muốn nó BÁO LỖI thay vì nuốt: dựng {@code CharsetDecoder} với
 * {@code CodingErrorAction.REPORT} rồi bọc qua {@code InputStreamReader} (bài t16).
 * - BOM (Byte Order Mark): file UTF-8 do Notepad/Excel trên Windows sinh ra hay có 3 byte
 * {@code EF BB BF} ở đầu. Java KHÔNG tự bỏ BOM, nên ký tự đầu tiên đọc được sẽ là
 * {@code U+FEFF} - hay gây lỗi khi parse CSV hoặc JSON. Phải tự cắt.
 * - Lời khuyên: tiếng Việt thì chỉ dùng UTF-8, và nếu sinh file cho Excel mở thì cân nhắc
 * GHI THÊM BOM, vì Excel trên Windows mặc định không đoán ra UTF-8.
 * <p>
 * 7. KÝ TỰ XUỐNG DÒNG
 * - {@code readLine()} nhận cả ba kiểu kết thúc dòng: {@code \n} (Unix), {@code \r\n}
 * (Windows), {@code \r} (Mac cổ) - và bỏ hết chúng khỏi chuỗi trả về.
 * - Chiều GHI thì không tự động: {@code write("abc")} không xuống dòng.
 * {@code writer.newLine()} dùng ký tự xuống dòng của hệ điều hành đang chạy
 * ({@code System.lineSeparator()}), nên file sinh ra trên Windows khác trên Linux.
 * - Nếu file sẽ được máy khác đọc hoặc đưa vào git, nên GHI CỨNG {@code "\n"} thay vì
 * {@code newLine()} để kết quả ổn định.
 * <p>
 * 8. GÓC NHÌN PRODUCTION
 * - Đọc file CSV do người dùng upload: {@code Files.lines} + {@code StandardCharsets.UTF_8},
 * trong try-with-resources, có giới hạn số dòng. Đừng dùng {@code readAllLines} (bài t13 mục 8).
 * - Trong Spring Boot, đọc file nằm trong jar phải qua {@code ClassPathResource} /
 * {@code ResourceLoader} chứ KHÔNG qua {@code Path}: khi đóng gói thành jar, entry bên trong
 * không phải file trên đĩa. Đây là lỗi kinh điển "chạy trong IDE nhưng hỏng khi deploy".
 * (Có thể mở jar như một file system - bài t4 mục 5 - nhưng dùng {@code Resource} gọn hơn.)
 * - Đặt {@code -Dfile.encoding=UTF-8} chỉ là vá tạm. Sửa gốc là truyền charset ở mọi lời gọi.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.newBufferedReader(p, UTF_8)} ≈ {@code fs.createReadStream(p, "utf8")} kết hợp
 * {@code readline}.
 * - Node mặc định UTF-8 ở khắp nơi và không có khái niệm "bảng mã theo locale", nên lập trình
 * viên TS hầu như không gặp lớp lỗi ở mục 6. Chuyển sang Java phải tập thói quen truyền
 * charset.
 * - Node cũng không bỏ BOM giúp - lỗi CSV có BOM là lỗi chung của cả hai hệ sinh thái.
 * - {@code BufferedWriter} ≈ {@code fs.createWriteStream}; nghĩa vụ {@code flush}/{@code close}
 * ở mục 4 giống hệt chuyện phải chờ sự kiện {@code "finish"} của Node.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Từ JDK nào thì bảng mã mặc định là UTF-8, trước đó phụ thuộc vào cái gì?
 * - {@code readLine()} có trả về ký tự xuống dòng không, hết file thì trả về gì?
 * - "Có đệm" tiết kiệm được cái gì, vì sao chênh lệch lớn đến vậy?
 * - Quên đóng {@code BufferedWriter} thì file bị sao?
 * - Ba cách đọc text theo dòng khác nhau ở điểm nào, cái nào phải đóng?
 * - Đọc file UTF-8 bằng bảng mã sai thì nhận exception hay nhận dữ liệu hỏng?
 * - BOM là gì, vì sao nó phá parse CSV?
 * - {@code newLine()} khác việc ghi cứng {@code "\n"} ở đâu, khi nào nên chọn cái nào?
 */
public class t14_BufferedTextFiles {

    void main() {
        // TODO 1 (mục 2): Dùng Files.newBufferedWriter ghi 5 dòng text (có dấu tiếng Việt) với
        //   StandardCharsets.UTF_8. Đọc lại bằng vòng lặp readLine() cho tới khi nhận null.
        //
        // TODO 2 (mục 2): Đọc lại chính file đó bằng reader.lines() và bằng Files.lines().
        //   Cả hai đều đặt trong try-with-resources. So sánh số dòng code.
        //
        // TODO 3 (mục 4): Ghi file bằng BufferedWriter nhưng CỐ TÌNH không đóng và không flush
        //   (đừng dùng try-with-resources). Đọc lại file ngay sau đó và dán lại nội dung thiếu.
        //
        // TODO 4 (mục 6): Ghi một file chứa chữ "Xin chào" bằng UTF_8, rồi đọc lại bằng
        //   StandardCharsets.US_ASCII. Dán lại chuỗi nhận được và đếm xem có exception không.
        //
        // TODO 5 (mục 6): Làm lại TODO 4 nhưng dùng CharsetDecoder với
        //   CodingErrorAction.REPORT bọc trong InputStreamReader. Lần này phải nhận được
        //   MalformedInputException - dán lại.
        //
        // TODO 6 (mục 6): Tự ghi 3 byte BOM (0xEF 0xBB 0xBF) rồi ghi "ten,tuoi" bằng
        //   newOutputStream. Đọc lại bằng newBufferedReader(UTF_8) và in
        //   (int) dong.charAt(0) để nhìn thấy ký tự 65279. Viết đoạn code cắt BOM.
        //
        // TODO 7 (mục 4): Đo thời gian đọc một file 50MB theo hai cách: Files.newInputStream
        //   đọc từng byte một, và cùng stream đó bọc trong BufferedInputStream.
        //   Dán lại hai con số.
        //
        // TODO 8 (mục 7): Ghi file bằng writer.newLine() và một file khác ghi cứng "\n".
        //   Đọc kích thước hai file bằng Files.size và giải thích chênh lệch trên Windows.
    }
}
