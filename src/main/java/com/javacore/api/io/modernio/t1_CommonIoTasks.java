package com.javacore.api.io.modernio;

/**
 * Common I/O Tasks in Modern Java
 * <p>
 * 0. Vị trí bài này
 * - Bước 22/41, bài 1/1 trong section Common I/O Tasks in Modern Java của lộ trình dev.java/learn.
 * - Một câu cho cả bài: section trước dạy CƠ CHẾ (stream, decorator, charset); bài này dạy
 * cách VIẾT NGẮN NHẤT cho các việc thường gặp - phần lớn là một lời gọi {@code Files}, không
 * cần stream, không cần vòng lặp.
 * - Đọc bài này ngay sau t18 là đúng thứ tự: phải biết cơ chế bên dưới rồi mới hiểu vì sao
 * các shortcut ở đây an toàn, và quan trọng hơn - khi nào chúng KHÔNG an toàn.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/modernio/">dev.java - Common I/O Tasks in Modern Java</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - COMMON I/O TASKS IN MODERN JAVA (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. MỞ ĐẦU
 * - (Introduction)
 * - API I/O đã cải tiến nhiều từ Java 8, và mốc lớn nhất là Java 18 lấy UTF-8 làm mặc định.
 * - Tuyên bố mạnh nhất của trang, nguyên văn: "The java.io.File and java.io.BufferedReader
 * classes are now thoroughly obsolete, even though they appear frequently in web searches
 * and AI chats."
 * - Lý do trang gốc nói thêm câu "web searches and AI chats": phần lớn ví dụ trên mạng vẫn
 * là code Java 6. Gặp {@code new BufferedReader(new FileReader(...))} thì đó là dấu hiệu
 * code cũ. Xem mục 9 để biết câu này đúng tới đâu.
 * <p>
 * 2. ĐỌC FILE TEXT
 * - (Reading Text Files)
 * - Đọc cả file thành một chuỗi: {@code String s = Files.readString(path);} - một dòng,
 * không stream, không reader, không vòng lặp.
 * - Đọc thành danh sách dòng: {@code Files.readAllLines(path)} trả {@code List<String>}.
 * - Đọc lười từng dòng: {@code Files.lines(path)} trả {@code Stream<String>} và PHẢI ĐÓNG
 * trong try-with-resources, nếu không là rò tài nguyên (nhắc lại bài t14 mục 5).
 * - Từ Java 18 các method này mặc định UTF-8 nên không cần truyền charset nữa. So sánh với
 * bài t14 mục 6 ở mục 7 bên dưới - hai lời khuyên NGHE thì ngược nhau.
 * - Tách chuỗi thành phần: {@code Scanner}, hoặc {@code split} rồi {@code Integer.parseInt} /
 * {@code Double.parseDouble}.
 * - CẢNH BÁO nguyên văn: "Be careful when parsing numbers from text files, since their format
 * may be locale-dependent." Máy ở Việt Nam hoặc Đức đọc "1.234" ra kết quả khác máy ở Mỹ.
 * Cần đọc theo locale cụ thể thì dùng {@code java.text.NumberFormat}.
 * <p>
 * 3. GHI FILE TEXT
 * - (Writing Text Files)
 * - Ghi một chuỗi: {@code Files.writeString(path, content);}
 * - Ghi nhiều dòng: {@code Files.write(path, lines);} với {@code lines} là
 * {@code Iterable<? extends CharSequence>} - tự chèn ký tự xuống dòng.
 * - Cả hai nhận {@code OpenOption} nên ghi nối được bằng {@code StandardOpenOption.APPEND}
 * (bài t13 mục 3).
 * - Cần định dạng kiểu {@code printf} thì vẫn phải dùng {@code PrintWriter}.
 * - Nguyên văn một chỗ khó chịu của API: "Weirdly enough, as of Java 21, there is no
 * PrintWriter constructor with a Path parameter." Đã kiểm chứng trên JDK 25: vẫn chưa có -
 * {@code PrintWriter} nhận {@code String} và {@code File}, tức là nhận đúng cái lớp mà mục 6
 * bảo tránh. Cách đi vòng: {@code new PrintWriter(Files.newBufferedWriter(path))}.
 * <p>
 * 4. ĐỌC TỪ INPUT STREAM
 * - (Reading From an Input Stream)
 * - Lấy dữ liệu từ URL nhanh gọn: {@code url.openStream()}. Cần đọc header hoặc đặt timeout
 * thì dùng {@code HttpClient}.
 * - Có stream rồi thì {@code readAllBytes()} lấy hết, hoặc {@code transferTo(out)} đổ thẳng
 * sang đích (bài t15 mục 4).
 * - LỜI KHUYÊN THIẾT KẾ, nguyên văn: "This is better than passing an input stream to the read
 * method, because the library can use additional information from the URL to determine the
 * image type." Thư viện nào nhận thẳng URL hoặc Path thì đưa cái đó, đừng tự mở stream rồi
 * đưa stream - bạn làm mất thông tin ngữ cảnh mà thư viện cần.
 * <p>
 * 5. FILES API
 * - (The Files API)
 * <p>
 * - 5a. DUYỆT THƯ MỤC (Traversing Entries in Directories and Subdirectories)
 * + {@code Files.list(dir)} - một tầng, trả {@code Stream<Path>}, PHẢI ĐÓNG.
 * + {@code Files.walk(dir)} - đệ quy mọi tầng, cũng trả {@code Stream<Path>}, PHẢI ĐÓNG.
 * + {@code Files.find(dir, depth, matcher)} - như {@code walk} nhưng lọc luôn theo
 * {@code BasicFileAttributes}, khỏi phải gọi lại {@code Files} cho từng phần tử.
 * + {@code Files.walkFileTree} với {@code FileVisitor} - chỉ cần khi phải kiểm soát việc
 * vào/ra từng thư mục hoặc xử lý lỗi theo từng entry (bài t10).
 * + {@code Files.newDirectoryStream} - bản thấp hơn, trả {@code DirectoryStream}.
 * + Tất cả thay cho {@code File.listFiles()}, vốn trả {@code null} khi lỗi thay vì ném
 * exception - đúng kiểu API cũ khiến lỗi đi xa khỏi chỗ phát sinh.
 * <p>
 * - 5b. LÀM VIỆC VỚI FILE ZIP (Working with ZIP Files)
 * + Đừng dùng {@code ZipInputStream}/{@code ZipOutputStream} nữa. Mở zip NHƯ MỘT FILE SYSTEM:
 * {@code try (FileSystem zipfs = FileSystems.newFileSystem(zipPath))}
 * + Bên trong đó dùng lại TOÀN BỘ API {@code Files} quen thuộc: {@code readString},
 * {@code readAllBytes}, {@code writeString}, {@code delete}, {@code walk}...
 * + Đây chính là thứ bài t18 dựng thủ công, và là lý do bài t18 kết luận "đừng tự phát minh
 * định dạng nhị phân" (t18 mục 9). Cũng là ứng dụng của bài t4 mục 5 - Java có nhiều
 * file system chứ không chỉ cái trên đĩa.
 * <p>
 * - 5c. FILE VÀ THƯ MỤC TẠM (Creating Temporary Files and Directories)
 * + {@code Files.createTempFile(prefix, suffix)} và {@code Files.createTempDirectory(prefix)}.
 * + Để JDK chọn vị trí hợp lý theo hệ điều hành, khỏi tự ghép đường dẫn {@code /tmp} (bài t8).
 * <p>
 * 6. KẾT LUẬN - SÁU LỜI KHUYÊN
 * - (Conclusion)
 * + "You don't need a loop to read or write strings or byte arrays."
 * + "You may not even need a stream, reader or writer."
 * + Thuộc các method {@code Files} để tạo, chép, di chuyển, xóa file và thư mục.
 * + Duyệt thư mục bằng {@code Files.list()} hoặc {@code Files.walk()}.
 * + Xử lý file ZIP bằng ZIP file system.
 * + "Stay away from the legacy File class."
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BÀI NÀY NGHE NHƯ MÂU THUẪN VỚI T14 - GIẢI QUYẾT THẾ NÀO
 * - Trang này: từ Java 18 mặc định đã là UTF-8, khỏi truyền charset.
 * - Bài t14 mục 6: LUÔN truyền {@code StandardCharsets.UTF_8} tường minh.
 * - Cả hai đều đúng, vì nói về hai rủi ro khác nhau:
 * + Rủi ro trang này đã dẹp: cùng code chạy hai máy khác locale ra hai kết quả. Từ JDK 18
 * không còn chuyện đó.
 * + Rủi ro còn nguyên: FILE bạn đọc do hệ thống khác sinh ra và KHÔNG phải UTF-8. Mặc định
 * của JDK không cứu được, và đọc sai charset thì không ném exception, chỉ âm thầm ra
 * {@code U+FFFD}.
 * - Kết luận thực dụng: file do chính ứng dụng mình sinh ra thì dựa vào mặc định cũng được;
 * file từ ngoài vào thì vẫn truyền tường minh. Truyền thừa không mất gì, thiếu thì mất dữ liệu.
 * <p>
 * 8. "KHÔNG CẦN VÒNG LẶP" CÓ TRẦN CỦA NÓ
 * - {@code readString}, {@code readAllBytes}, {@code readAllLines} đều nạp TOÀN BỘ vào heap.
 * Trang gốc không nhắc chuyện này vì nó nói về sự gọn gàng, không nói về tải.
 * - Phép nhân phải nhẩm mỗi lần dùng: heap tốn = kích thước mỗi lần x số lần chạy đồng thời.
 * - Chặn được cả hai vế thì dùng thoải mái: đọc config, template, file SQL, resource trong
 * classpath lúc khởi động - đây là chỗ các method này tỏa sáng, viết thành stream mới là dở.
 * - Không chặn được vế nào thì quay về {@code Files.lines} hoặc {@code transferTo}: file do
 * người dùng upload, blob trong DB, file export tăng theo dữ liệu.
 * - Giới hạn cứng: mảng Java đánh chỉ số bằng {@code int} nên {@code readAllBytes} chịu thua
 * ở mức khoảng 2GB dù heap có dư.
 * <p>
 * 9. "THOROUGHLY OBSOLETE" ĐÚNG TỚI ĐÂU
 * - {@code java.io.File}: đúng hoàn toàn. {@code Path} hơn mọi mặt, và {@code File} có thói
 * trả {@code null} hoặc {@code false} thay vì ném exception nên lỗi bị nuốt.
 * - {@code BufferedReader}: chỉ đúng MỘT NỬA. Lỗi thời khi dùng nó để ĐỌC FILE
 * ({@code new BufferedReader(new FileReader(f))} - đã có {@code Files.lines}).
 * KHÔNG lỗi thời khi dùng nó làm DECORATOR trên một {@code Reader} bất kỳ: đọc từ socket,
 * từ response HTTP, từ {@code StringReader} trong test. Chuỗi decorator của bài t16 vẫn
 * nguyên giá trị - chỉ là bạn ít khi phải tự dựng nó cho file trên đĩa.
 * - Nói cách khác: section trước không hề phí. Nó dạy tầng dưới, bài này dạy tầng tắt cho
 * trường hợp phổ biến nhất. Biết tầng dưới mới dám đi tắt đúng chỗ.
 * <p>
 * 10. GÓC NHÌN PRODUCTION
 * - Trong Spring Boot, file nằm trong jar vẫn KHÔNG đọc được bằng {@code Path} -
 * {@code Files.readString(Path.of("config/x.json"))} chạy trong IDE nhưng hỏng khi deploy.
 * Dùng {@code ClassPathResource} (nhắc lại bài t14 mục 8). Lời khuyên "dùng Files" của trang
 * này chỉ áp dụng cho file thật trên đĩa.
 * - {@code Files.writeString} ghi đè không hỏi. File quan trọng thì ghi ra file tạm cùng thư
 * mục rồi {@code ATOMIC_MOVE} (bài t5 mục 10).
 * - ZIP file system là món lợi hại nhất của bài: xuất báo cáo nhiều file, đọc file .xlsx hay
 * .docx (đều là zip), gói log - không cần thư viện ngoài nào.
 * - Mọi {@code Stream<Path>} đều phải đóng. Lỗi này không nổ ngay, nó tích lũy cho tới khi
 * hết file descriptor, thường vào lúc tải cao.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.readString(p)} ≈ {@code await fs.readFile(p, "utf8")} - cùng tinh thần
 * một-dòng, và cùng cái bẫy nạp hết vào RAM ở mục 8.
 * - {@code Files.writeString(p, s)} ≈ {@code await fs.writeFile(p, s)}.
 * - {@code Files.walk(dir)} ≈ {@code fs.readdir} với tùy chọn {@code recursive} (Node 20+).
 * - ZIP file system thì Node KHÔNG có tương đương trong thư viện chuẩn - phải cài
 * {@code adm-zip} hoặc {@code yauzl}. Đây là chỗ Java mạnh hơn rõ rệt.
 * - Điều thú vị: lộ trình "API cũ rườm rà rồi thêm shortcut gọn" là chung cho cả hai hệ.
 * {@code fs.promises} của Node ra đời đúng vì lý do mà {@code Files} ra đời ở Java.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Đọc cả file text thành một chuỗi, viết bằng mấy dòng, gọi method nào?
 * - Ba cách đọc text của bài này khác nhau ra sao, cái nào phải đóng?
 * - Trang gốc nói hai lớp nào đã lỗi thời, câu đó đúng tới đâu với {@code BufferedReader}?
 * - Từ Java 18 khỏi truyền charset - vậy khi nào vẫn nên truyền, vì sao?
 * - {@code Files.list} khác {@code Files.walk} ở chỗ nào, cả hai có gì chung phải nhớ?
 * - Mở file ZIP theo cách hiện đại thì viết thế nào, lợi ở đâu so với {@code ZipInputStream}?
 * - {@code readString} gọn như vậy, khi nào KHÔNG được dùng nó?
 * - Sáu lời khuyên ở phần kết luận, nêu được mấy cái?
 */
public class t1_CommonIoTasks {

    void main() {
        // TODO 1 (mục 2): Ghi sẵn một file có dấu tiếng Việt, rồi đọc lại bằng cả ba cách:
        //   Files.readString, Files.readAllLines, Files.lines. Đếm số dòng code mỗi cách và
        //   đánh dấu cách nào bắt buộc try-with-resources.
        //
        // TODO 2 (mục 2): Viết một file chứa "1.234" rồi parse bằng Double.parseDouble và bằng
        //   NumberFormat.getInstance(Locale.GERMANY). Dán lại hai kết quả khác nhau.
        //
        // TODO 3 (mục 3): Ghi file bằng Files.writeString, rồi ghi tiếp bằng chính nó lần nữa.
        //   Xác nhận nội dung cũ mất. Làm lại với StandardOpenOption.APPEND.
        //
        // TODO 4 (mục 3): Thử new PrintWriter(path) xem có biên dịch được không. Dán lại lỗi,
        //   rồi viết lại bằng new PrintWriter(Files.newBufferedWriter(path)) và dùng printf.
        //
        // TODO 5 (mục 5a): Dùng Files.walk đếm số file .java trong src/main/java của repo này.
        //   Viết lại bằng Files.find và so sánh. Cả hai trong try-with-resources.
        //
        // TODO 6 (mục 5b): Tạo một file zip bằng FileSystems.newFileSystem, ghi 3 file text
        //   vào trong bằng Files.writeString, đóng lại. Mở lại và Files.walk liệt kê nội dung.
        //
        // TODO 7 (mục 5b): So sánh code TODO 6 với cách dùng ZipOutputStream ở bài t16 mục 8.
        //   Đếm số dòng của hai cách.
        //
        // TODO 8 (mục 5c): Tạo file tạm bằng Files.createTempFile, in đường dẫn ra để xem JDK
        //   đặt nó ở đâu trên Windows. Thử thêm deleteOnExit và xác nhận nó biến mất.
        //
        // TODO 9 (mục 7, 8): Ghi một file bằng charset ISO-8859-1 có chứa ký tự tiếng Việt,
        //   rồi đọc lại bằng Files.readString (mặc định UTF-8). Dán lại chuỗi nhận được -
        //   đây là lý do mục 7 nói vẫn nên truyền charset cho file từ ngoài vào.
    }
}
