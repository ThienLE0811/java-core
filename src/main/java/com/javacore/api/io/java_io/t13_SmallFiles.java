package com.javacore.api.io.java_io;

/**
 * Reading and Writing Small Files
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 13/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File Operations Basics của section.
 * - Một câu cho cả bài: đây là bài BẢNG TRA - chọn method đọc/ghi theo KÍCH THƯỚC file và
 * KIỂU dữ liệu; nhóm {@code readAllBytes} / {@code readAllLines} tiện nhưng nạp hết vào RAM
 * nên chỉ dành cho file nhỏ.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/reading-writing/small-files/">dev.java - Reading and Writing Small Files</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - READING AND WRITING SMALL FILES (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. CHỌN METHOD ĐỌC
 * - (Choosing the Right Method for Reading)
 * - Sáu method, xếp từ tiện nhất tới linh hoạt nhất:
 * <p>
 * .  Files.readAllBytes(p)      -> byte[]                file nhỏ, nhị phân
 * .  Files.readAllLines(p)      -> List&lt;String&gt;    file nhỏ, text
 * .  Files.lines(p)             -> Stream&lt;String&gt;  text, xử lý LƯỜI từng dòng
 * .  Files.newBufferedReader(p) -> BufferedReader        text, có đệm (bài t14)
 * .  Files.newInputStream(p)    -> InputStream           nhị phân, không đệm (bài t15)
 * .  Files.newByteChannel(p)    -> SeekableByteChannel   truy cập ngẫu nhiên
 * <p>
 * - Hai method đầu "designed for simple, common use cases" - tự mở và tự đóng stream giúp bạn.
 * - {@code Files.lines()} cho "efficient line by line processing" nhờ đánh giá lười, nên dùng
 * được cả với file lớn.
 * <p>
 * 2. CHỌN METHOD GHI
 * - (Choosing the Right Method for Writing)
 * - Đối xứng với mục 1:
 * <p>
 * .  Files.write(p, byte[], OpenOption...)                    -> Path
 * .  Files.write(p, Iterable&lt;CharSequence&gt;, Charset, OpenOption...) -> Path
 * .  Files.newBufferedWriter(p, ...)  -> BufferedWriter
 * .  Files.newOutputStream(p, ...)    -> OutputStream
 * .  Files.newByteChannel(p, ...)     -> SeekableByteChannel
 * <p>
 * - Hai overload {@code write} trả về chính {@code Path} để nối chuỗi lời gọi.
 * <p>
 * 3. THAM SỐ OPENOPTION
 * - (The OpenOptions Parameter)
 * - Enum {@code StandardOpenOption}, truyền dạng varargs:
 * + {@code READ} - mở để đọc.
 * + {@code WRITE} - mở để ghi.
 * + {@code APPEND} - ghi nối vào CUỐI file. Phải đi kèm {@code WRITE} hoặc {@code CREATE}.
 * + {@code TRUNCATE_EXISTING} - xóa sạch nội dung cũ. Phải đi kèm {@code WRITE}.
 * + {@code CREATE} - tạo nếu chưa có, không có thì dùng file sẵn.
 * + {@code CREATE_NEW} - tạo mới, NÉM EXCEPTION nếu file đã tồn tại.
 * + {@code DELETE_ON_CLOSE} - xóa file khi đóng stream.
 * + {@code SPARSE} - gợi ý tạo sparse file, tùy nền tảng (hiệu lực trên NTFS).
 * + {@code SYNC} / {@code DSYNC} - mỗi lần ghi phải xuống đĩa thật mới trả về.
 * <p>
 * 4. CÁC METHOD HAY DÙNG CHO FILE NHỎ
 * - (Commonly Used Methods for Small Files)
 * - (Reading All Bytes or Lines from a File): {@code Files.readAllBytes(p)},
 * {@code Files.readAllLines(p, charset)}. Nguyên văn: các method này "take care of most of the
 * work for you, such as opening and closing the stream, but are not intended for handling
 * large files".
 * - (Writing All Bytes or Lines to a File): hai overload của {@code Files.write}, một cho
 * mảng byte, một cho tập hợp dòng text.
 * <p>
 * 5. TẠO FILE THƯỜNG VÀ FILE TẠM
 * - (Methods for Creating Regular and Temporary Files)
 * - (Creating Files): {@code Files.createFile(path, FileAttribute...)}. Nguyên văn: "In a single
 * atomic operation, the createFile() method checks for the existence of the file and creates
 * that file with the specified attributes, which makes the process more secure against
 * malicious code." Ném exception nếu file đã tồn tại.
 * - (Creating Temporary Files): hai overload
 * {@code Files.createTempFile(dir, prefix, suffix, FileAttribute...)} và
 * {@code Files.createTempFile(prefix, suffix, FileAttribute...)}. "The specific format of the
 * temporary file name is platform specific."
 * <p>
 * 6. TRUY CẬP NGẪU NHIÊN
 * - (Random Access Files)
 * - {@code SeekableByteChannel} "extends channel I/O with the notion of a current position" -
 * đọc ghi được ở vị trí bất kỳ, không bắt buộc tuần tự.
 * - Các method: {@code position()}, {@code position(long)}, {@code read(ByteBuffer)},
 * {@code write(ByteBuffer)}, {@code truncate(long)}, {@code size()}.
 * - Lấy bằng {@code Files.newByteChannel(...)}. Ép về {@code FileChannel} để dùng tính năng
 * nâng cao như memory mapping hay khóa một vùng file.
 * - Ví dụ trang gốc: mở đọc-ghi, nhảy vị trí, đọc 12 byte, ghi đè "I was here!", nhảy về
 * cuối rồi ghi nối.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. HAI METHOD TIỆN NHẤT LẠI KHÔNG NẰM TRONG TRANG GỐC
 * - {@code Files.readString(path)} và {@code Files.writeString(path, text, OpenOption...)}
 * (Java SE 11) là thứ bạn sẽ dùng nhiều nhất trong thực tế, nhưng trang gốc không nhắc.
 * - Mặc định của chúng là UTF-8, không phụ thuộc locale - an toàn hơn hẳn các API cũ.
 * - Vẫn là nhóm "file nhỏ": {@code readString} nạp toàn bộ vào một {@code String}, và ném
 * {@code OutOfMemoryError} nếu file vượt quá bộ nhớ (giới hạn cứng khoảng 2GB vì độ dài mảng).
 * <p>
 * 8. "FILE NHỎ" LÀ BAO NHIÊU - VÀ VÌ SAO PHẢI QUAN TÂM
 * - Không có con số chính thức. Luật thực dụng: nếu file có thể do NGƯỜI DÙNG cung cấp thì coi
 * như nó LỚN, dù hôm nay nó nhỏ.
 * - {@code readAllBytes} trên file 2GB không chỉ chậm - nó làm sập cả JVM bằng
 * {@code OutOfMemoryError}, kéo theo mọi request khác đang chạy. Đây là một vector tấn công
 * từ chối dịch vụ rất rẻ.
 * - Thay thế an toàn: {@code Files.lines} (lười, xử lý từng dòng) cho text,
 * {@code Files.newInputStream} cho nhị phân. Cả hai đều PHẢI đóng (bài t12 mục 7).
 * - Dùng {@code readAllBytes}/{@code readString} cho file CẤU HÌNH, template, resource đóng gói
 * trong jar - những thứ mình kiểm soát kích thước.
 * <p>
 * 9. BẢNG TRA CUỐI CÙNG - THUỘC BẢNG NÀY LÀ XONG CẢ NHÓM BÀI
 * <p>
 * .                  file NHỎ, mình kiểm soát     file LỚN hoặc không rõ
 * .  text            Files.readString             Files.lines / newBufferedReader
 * .  nhị phân        Files.readAllBytes           Files.newInputStream
 * .  ghi text        Files.writeString            Files.newBufferedWriter
 * .  ghi nhị phân    Files.write                  Files.newOutputStream
 * .  nhảy vị trí     -                            Files.newByteChannel
 * <p>
 * 10. BẪY OPENOPTION
 * - KHÔNG truyền option nào khi GHI thì mặc định là {@code CREATE} + {@code TRUNCATE_EXISTING}
 * + {@code WRITE}. Nghĩa là nội dung cũ BỊ XÓA SẠCH. Rất nhiều người mất dữ liệu vì tưởng
 * mặc định là ghi nối.
 * - Ghi log phải nhớ {@code StandardOpenOption.APPEND} - và nhớ kèm {@code CREATE} nếu file
 * có thể chưa tồn tại, vì chỉ {@code APPEND} thôi sẽ ném {@code NoSuchFileException}.
 * - {@code CREATE_NEW} là cách khóa chống đua an toàn: nó nguyên tử, nên dùng để tạo file
 * lock thay cho {@code if (!exists) create} (nhắc lại bài t5 mục 10).
 * - {@code SYNC}/{@code DSYNC} chậm hơn nhiều lần. Chỉ dùng khi mất dữ liệu lúc cúp điện là
 * không chấp nhận được, ví dụ ghi nhật ký giao dịch.
 * <p>
 * 11. ĐỐI CHIẾU TYPESCRIPT
 * - {@code Files.readString} ≈ {@code fs.readFile(p, "utf8")};
 * {@code Files.readAllBytes} ≈ {@code fs.readFile(p)} (trả {@code Buffer}).
 * Đây đúng là ranh giới Reader/InputStream mà bài t1 đã nêu.
 * - {@code Files.writeString} ≈ {@code fs.writeFile}; {@code APPEND} ≈ {@code fs.appendFile}
 * hoặc cờ {@code {flag: "a"}}. Bộ cờ {@code "r"}, {@code "w"}, {@code "a"}, {@code "wx"} của
 * Node chính là {@code OpenOption} của Java - {@code "wx"} ≈ {@code CREATE_NEW}.
 * - {@code Files.lines} ≈ {@code readline.createInterface(...)} - cùng ý tưởng xử lý lười,
 * nhưng Java gọn hơn nhiều.
 * - {@code SeekableByteChannel} ≈ {@code fs.read(fd, buffer, offset, length, position)}.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Đọc một file config 2KB dạng text thì dùng method nào, đọc một file log 5GB thì dùng gì?
 * - Vì sao {@code readAllLines} nguy hiểm với file do người dùng tải lên?
 * - {@code Files.write} không truyền {@code OpenOption} thì nội dung cũ ra sao?
 * - Ghi nối vào file log cần những option nào, thiếu {@code CREATE} thì lỗi gì?
 * - {@code CREATE} khác {@code CREATE_NEW} ở đâu, cái nào dùng làm khóa chống đua?
 * - {@code Files.readString} mặc định dùng bảng mã nào?
 * - Method nào cho phép nhảy tới byte thứ 1000 của file mà không đọc 999 byte đầu?
 * - Trong năm method ở mục 1, cái nào tự đóng stream, cái nào bắt bạn tự đóng?
 */
public class t13_SmallFiles {

    void main() {
        // TODO 1 (mục 1, 7): Tạo file text nhỏ. Đọc lại bằng cả ba cách: Files.readString,
        //   Files.readAllLines, Files.lines (nhớ try-with-resources cho cách thứ ba).
        //   So sánh kiểu trả về của ba cách.
        //
        // TODO 2 (mục 3, 10): Ghi "dong 1" vào file bằng Files.writeString KHÔNG truyền option.
        //   Ghi tiếp "dong 2" cũng không truyền option. Đọc lại và xác nhận "dong 1" đã mất.
        //
        // TODO 3 (mục 3, 10): Làm lại TODO 2 với StandardOpenOption.CREATE và APPEND.
        //   Xác nhận lần này giữ được cả hai dòng.
        //
        // TODO 4 (mục 3, 10): Gọi Files.writeString chỉ với APPEND trên một file CHƯA tồn tại.
        //   Dán lại exception.
        //
        // TODO 5 (mục 3, 10): Gọi Files.write hai lần với CREATE_NEW trên cùng đường dẫn.
        //   Dán lại exception lần hai và giải thích vì sao nó dùng làm khóa chống đua được.
        //
        // TODO 6 (mục 5): Tạo file bằng Files.createFile hai lần liên tiếp. Dán exception.
        //   Rồi dùng createTempFile("bao-cao-", ".csv") và in tên file sinh ra.
        //
        // TODO 7 (mục 6): Dùng Files.newByteChannel với READ+WRITE: ghi chuỗi "0123456789",
        //   nhảy position(3), đọc 4 byte, rồi ghi đè "XY" tại vị trí đó. Đọc lại cả file.
        //
        // TODO 8 (mục 8): Tạo một file lớn (ví dụ 200MB) bằng vòng lặp ghi nối. Chạy
        //   Files.readAllBytes trên nó với -Xmx128m và dán lại lỗi nhận được.
        //   Sau đó làm lại bằng Files.lines và xác nhận chạy được.
        //
        // TODO 9 (mục 3): Mở file bằng newOutputStream với DELETE_ON_CLOSE, ghi gì đó, đóng lại,
        //   rồi kiểm tra Files.exists. Nêu một tình huống thực tế dùng option này.
    }
}
