package com.javacore.api.io.java_io;

/**
 * Reading and Writing Binary Files
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 15/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File Operations Basics của section.
 * - Một câu cho cả bài: nhánh BYTE của bảng 2x2 ở bài t1 - {@code newInputStream} /
 * {@code newOutputStream} đọc ghi TUẦN TỰ từng byte, còn {@code newByteChannel} đọc ghi
 * theo KHỐI và nhảy được tới vị trí bất kỳ.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/reading-writing/binary-files/">dev.java - Reading and Writing Binary Files</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING AND WRITING BINARY FILES (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. ĐỌC FILE BẰNG STREAM I/O
 * - (Reading a File by Using Stream I/O)
 * - {@code Files.newInputStream(path, OpenOption...)} - nguyên văn: "returns an unbuffered
 * input stream for reading bytes from the file".
 * - Chữ UNBUFFERED là điểm chính: KHÔNG có đệm, nên đọc từng byte sẽ rất chậm. Muốn nhanh
 * phải tự bọc {@code BufferedInputStream} (bài t14 mục 4, bài t16).
 * <p>
 * 2. TẠO VÀ GHI FILE BẰNG STREAM I/O
 * - (Creating and Writing a File by Using Stream I/O)
 * - {@code Files.newOutputStream(path, OpenOption...)}.
 * - Nguyên văn về mặc định: "If no open options are specified, and the file does not exist,
 * a new file is created. If the file exists, it is truncated." Tức là không truyền gì thì
 * tương đương {@code CREATE} + {@code TRUNCATE_EXISTING} - nội dung cũ MẤT SẠCH
 * (nhắc lại bài t13 mục 10).
 * - Muốn ghi nối thì truyền {@code StandardOpenOption.APPEND}.
 * - Ví dụ trang gốc: ghi file log.
 * <p>
 * 3. ĐỌC GHI BẰNG CHANNEL I/O
 * - (Reading and Writing Files by Using Channel I/O)
 * - Câu phân biệt cốt lõi: "While stream I/O reads a character at a time, channel I/O reads
 * a buffer at a time."
 * - Hai interface: {@code ByteChannel} (tuần tự) và {@code SeekableByteChannel} (nhảy được
 * vị trí, đổi được kích thước, hỏi được {@code size()}).
 * - Bốn cách mở:
 * + {@code Files.newByteChannel(path, OpenOption...)}
 * + {@code Files.newByteChannel(path, Set<? extends OpenOption>, FileAttribute...)}
 * + {@code FileChannel.open(path, OpenOption...)}
 * + {@code FileChannel.open(path, Set, FileAttribute...)}
 * - BẪY KHÁC VỚI STREAM: với channel, option {@code READ} là BẮT BUỘC khi muốn đọc. Không
 * truyền option nào thì channel mở ở chế độ đọc.
 * - Trên file system mặc định, ép được về {@code FileChannel} để dùng tính năng nâng cao:
 * memory mapping và khóa một vùng file.
 * - Ví dụ trang gốc: tạo file log trên hệ POSIX kèm quyền cụ thể - đây là lý do tồn tại của
 * overload nhận {@code Set} cộng {@code FileAttribute} (varargs không cho trộn hai loại).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. BA MỨC ĐỘ ĐỌC MỘT INPUTSTREAM - PHẢI PHÂN BIỆT
 * - {@code read()} trả về {@code int} từ 0 đến 255, hoặc {@code -1} khi hết file. KHÔNG phải
 * {@code byte} - đây là lý do phải khai báo biến {@code int}, viết
 * {@code byte b = in.read()} là sai logic vì mất khả năng phân biệt -1 với byte 0xFF.
 * - {@code read(byte[])} trả về SỐ BYTE THỰC SỰ ĐỌC ĐƯỢC, có thể ÍT HƠN độ dài mảng dù chưa
 * hết file. Đây là lỗi kinh điển: đừng giả định nó lấp đầy mảng. Luôn viết
 * {@code while ((n = in.read(buf)) != -1)} rồi dùng đúng {@code n} byte đầu.
 * - {@code readAllBytes()} / {@code readNBytes(n)} (Java SE 9+) và {@code transferTo(out)}
 * (Java SE 9+) làm sẵn vòng lặp đó. {@code in.transferTo(out)} là cách chép stream gọn nhất.
 * <p>
 * 5. STREAM I/O SO VỚI CHANNEL I/O
 * <p>
 * .                      InputStream/OutputStream   SeekableByteChannel
 * .  đơn vị              byte hoặc mảng byte        ByteBuffer
 * .  chiều đi            một chiều, tuần tự         đọc ghi, nhảy vị trí tự do
 * .  đệm                 phải tự bọc Buffered...    ByteBuffer đã là đệm
 * .  độ phức tạp         thấp                       cao (flip/clear/rewind)
 * .  dùng khi            99% trường hợp             sửa tại chỗ, file lớn, memory map
 * <p>
 * - Luật thực dụng: mặc định dùng stream. Chỉ sang channel khi THỰC SỰ cần nhảy vị trí hoặc
 * cần {@code FileChannel.map()} cho file rất lớn.
 * - {@code ByteBuffer} có vòng đời trạng thái riêng ({@code position}, {@code limit},
 * {@code capacity}, và {@code flip()} để chuyển từ chế độ ghi sang chế độ đọc). Quên
 * {@code flip()} là lỗi số một của người mới dùng channel.
 * <p>
 * 6. VÌ SAO NHÁNH BYTE QUAN TRỌNG
 * - Mọi thứ không phải text đều đi đường này: ảnh, PDF, file nén, file Excel, dữ liệu mạng.
 * - Và TEXT cũng đi đường này ở tầng dưới - {@code Reader} chỉ là lớp bọc dịch byte thành
 * ký tự (bài t16 mục 2).
 * - Trong Spring Boot, {@code MultipartFile.getInputStream()} và
 * {@code ResponseEntity} trả file đều là {@code InputStream}. Nguyên tắc: ĐỪNG gọi
 * {@code getBytes()} nạp hết vào RAM; hãy {@code transferTo} thẳng sang đích.
 * <p>
 * 7. GÓC NHÌN PRODUCTION
 * - Chép file hoặc đẩy file về client: dùng {@code in.transferTo(out)} hoặc
 * {@code Files.copy(in, path)} (bài t5 mục 5) thay vì tự viết vòng lặp.
 * - Dung lượng upload phải có trần. {@code InputStream} không cho biết trước độ dài, nên phải
 * tự đếm byte và cắt khi vượt ngưỡng, hoặc tin vào giới hạn của tầng servlet.
 * - Ghi file quan trọng: {@code newOutputStream} ghi ra file tạm cùng thư mục rồi
 * {@code ATOMIC_MOVE} (bài t5 mục 10). {@code SYNC}/{@code DSYNC} chỉ khi mất dữ liệu lúc cúp
 * điện là không chấp nhận được.
 * - {@code FileChannel.map()} giữ vùng nhớ ánh xạ cho tới khi bộ thu gom rác dọn - trên
 * Windows điều đó khiến file KHÔNG XÓA ĐƯỢC dù đã đóng channel. Là lý do nên tránh memory
 * mapping trừ khi thật cần.
 * <p>
 * 8. ĐỐI CHIẾU TYPESCRIPT
 * - {@code InputStream} ≈ {@code Readable} stream của Node; {@code OutputStream} ≈
 * {@code Writable}.
 * - {@code in.transferTo(out)} ≈ {@code readable.pipe(writable)} - cùng một ý tưởng, Java
 * đồng bộ còn Node bất đồng bộ.
 * - {@code byte[]} của Java ≈ {@code Buffer} của Node. Nhưng {@code byte} của Java CÓ DẤU
 * (-128..127) còn phần tử {@code Buffer} không dấu (0..255) - chỗ này port code rất dễ sai,
 * phải dùng {@code b & 0xFF} để đổi sang dạng không dấu.
 * - {@code ByteBuffer} với {@code flip()} không có tương đương trong Node; {@code Buffer}
 * của Node đơn giản hơn vì không mang trạng thái con trỏ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - {@code Files.newInputStream} có đệm sẵn không, muốn có đệm thì làm gì?
 * - {@code newOutputStream} không truyền option thì nội dung cũ ra sao?
 * - Vì sao {@code read()} trả {@code int} chứ không trả {@code byte}?
 * - {@code read(byte[])} trả về gì, vì sao không được giả định nó lấp đầy mảng?
 * - Câu nào phân biệt stream I/O với channel I/O?
 * - Với channel, muốn đọc thì phải truyền option gì, khác stream ở chỗ nào?
 * - Khi nào nên chọn channel thay vì stream?
 * - Chép toàn bộ một {@code InputStream} sang {@code OutputStream} bằng một dòng - viết thế nào?
 */
public class t15_BinaryFiles {

    void main() {
        // TODO 1 (mục 1, 4): Tạo file nhị phân nhỏ bằng Files.write(p, new byte[]{1,2,3,-1}).
        //   Đọc lại bằng newInputStream với vòng lặp read() và in giá trị từng lần đọc.
        //   Giải thích vì sao byte -1 lại in ra 255.
        //
        // TODO 2 (mục 4): Đọc một file bằng read(byte[] buf) với buf 1024 byte. In ra giá trị
        //   trả về của MỖI lần gọi. Tìm một file mà có lần trả về ít hơn 1024 dù chưa hết file.
        //
        // TODO 3 (mục 2): Ghi "A" vào file bằng newOutputStream không truyền option, rồi ghi "B"
        //   cũng không truyền option. Đọc lại và xác nhận chỉ còn "B".
        //
        // TODO 4 (mục 2): Làm lại TODO 3 với StandardOpenOption.CREATE và APPEND.
        //
        // TODO 5 (mục 4, 7): Chép một file bằng ba cách: vòng lặp read/write tự viết,
        //   in.transferTo(out), và Files.copy. Đếm số dòng code của mỗi cách.
        //
        // TODO 6 (mục 3, 5): Mở file bằng Files.newByteChannel với READ và WRITE.
        //   Ghi 10 byte, gọi position(0), đọc lại vào một ByteBuffer, nhớ gọi flip() trước khi
        //   đọc dữ liệu ra. Thử BỎ flip() và ghi lại chuyện gì xảy ra.
        //
        // TODO 7 (mục 3): Mở channel KHÔNG truyền option nào rồi thử ghi vào đó.
        //   Dán lại exception và đối chiếu với hành vi mặc định của newOutputStream ở TODO 3.
        //
        // TODO 8 (mục 3, 5): Dùng SeekableByteChannel ghi đè 4 byte ở GIỮA một file có sẵn mà
        //   không làm hỏng phần còn lại. Giải thích vì sao OutputStream không làm được việc này.
        //
        // TODO 9 (mục 8): Đọc một byte giá trị 0xFF từ file, in ra cả b và (b & 0xFF).
        //   Dán hai giá trị - đây là bẫy khi port code từ Node sang Java.
    }
}
