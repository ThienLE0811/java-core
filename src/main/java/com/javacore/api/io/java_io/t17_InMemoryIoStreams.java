package com.javacore.api.io.java_io;

/**
 * In Memory I/O Streams
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 17/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Bài cuối của nhóm File Operations Basics; sau bài này là t18 ghép tất cả lại.
 * - Một câu cho cả bài: bốn cặp lớp cho phép dùng ĐÚNG API I/O nhưng nguồn và đích nằm trong
 * RAM chứ không phải trên đĩa - thứ cần khi phải biết TRƯỚC số byte sẽ ghi ra.
 * - Trả lời luôn câu hỏi của bài t1: "file may reside in memory" nghĩa là thế này.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/reading-writing/in-memory/">dev.java - In Memory I/O Streams</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - IN MEMORY I/O STREAMS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. GIỚI THIỆU
 * - (Introducing I/O Streams on In-Memory Structures)
 * - API I/O có sẵn các lớp đọc ghi trên cấu trúc trong bộ nhớ: mảng ký tự, mảng byte, chuỗi.
 * - LÝ DO TỒN TẠI, và đây là phần đáng nhớ nhất của bài: nhiều định dạng file đòi phải ghi
 * ĐỘ DÀI của một khối dữ liệu TRƯỚC khi ghi chính khối đó (ví dụ JPEG). Mà độ dài thì nhiều
 * khi không tính trước được.
 * - Nhất là khi dữ liệu còn bị NÉN: tính trước số byte của dữ liệu đã gzip khó hơn hẳn tính
 * số byte dữ liệu thô.
 * - Lời giải: ghi vào một mảng byte trong RAM trước, hỏi nó dài bao nhiêu, rồi mới ghi
 * độ dài cộng dữ liệu ra file thật. Bài t18 dùng đúng kỹ thuật này.
 * <p>
 * 2. ĐỌC GHI MẢNG KÝ TỰ
 * - (Reading and Writing Arrays of Characters)
 * - {@code CharArrayReader} (thừa kế {@code Reader}) và {@code CharArrayWriter}
 * (thừa kế {@code Writer}).
 * - Bọc quanh một mảng {@code char} truyền vào constructor.
 * - KHÔNG thêm method nào ngoài những gì lớp cha đã có.
 * <p>
 * 3. ĐỌC GHI CHUỖI KÝ TỰ
 * - (Reading and Writing Strings of Characters)
 * - {@code StringReader} (thừa kế {@code Reader}): bọc một {@code String} truyền vào constructor.
 * - {@code StringWriter} (thừa kế {@code Writer}): bọc một {@code StringBuffer} nội bộ,
 * lấy kết quả ra bằng {@code getBuffer()} hoặc {@code toString()}.
 * - BẪY: {@code getBuffer()} trả về CHÍNH bộ đệm nội bộ, không sao chép phòng vệ - sửa nó là
 * sửa luôn dữ liệu bên trong. Muốn an toàn thì dùng {@code toString()}.
 * <p>
 * 4. ĐỌC GHI MẢNG BYTE
 * - (Reading and Writing Arrays of Bytes)
 * - {@code ByteArrayInputStream}: đọc từ một mảng {@code byte} như một {@code InputStream}.
 * - {@code ByteArrayOutputStream}: ghi vào một mảng byte TỰ ĐỘNG LỚN DẦN. Ba method lấy
 * kết quả:
 * + {@code size()} - số byte đã ghi. Đây chính là thứ mục 1 cần.
 * + {@code toByteArray()} - trả về một BẢN SAO, không phải mảng gốc.
 * + {@code toString()} - ba overload, khác nhau ở cách nhận bảng mã. Không chỉ định thì
 * dùng bảng mã mặc định.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. BỐN LỚP NÀY LẤP ĐẦY BẢNG 2x2 CỦA BÀI T1
 * <p>
 * .                 nguồn trong RAM           đích trong RAM
 * .  ký tự          CharArrayReader           CharArrayWriter
 * .                 StringReader              StringWriter
 * .  byte           ByteArrayInputStream      ByteArrayOutputStream
 * <p>
 * - Đây chính là lời giải cho bài tập 4 của bài t1 ("lớp nào đọc từ mảng byte trong bộ nhớ").
 * - Chúng là NGUỒN THẬT chứ không phải decorator (bài t16 mục 1): constructor nhận dữ liệu,
 * không nhận stream khác.
 * - Vì nằm trong RAM nên {@code close()} trên chúng KHÔNG làm gì cả - nhưng vẫn nên đóng cho
 * nhất quán, và vì các lớp bọc bên ngoài thì CÓ việc phải làm lúc đóng.
 * <p>
 * 6. BA CÔNG DỤNG THỰC TẾ
 * - ĐO TRƯỚC KÍCH THƯỚC: đúng kịch bản mục 1. Ghi vào {@code ByteArrayOutputStream}, gọi
 * {@code size()}, rồi mới ghi ra đích thật.
 * - LÀM TEST KHÔNG CẦN FILE: hàm của bạn nhận {@code InputStream} thì test truyền
 * {@code new ByteArrayInputStream("du lieu".getBytes(UTF_8))} - không đụng đĩa, test chạy
 * nhanh và chạy song song được. Hàm nhận {@code OutputStream} thì truyền
 * {@code ByteArrayOutputStream} rồi assert trên {@code toByteArray()}.
 * Đây là lý do nên khai báo tham số kiểu {@code InputStream} thay vì {@code Path}.
 * - GỬI QUA MẠNG: dựng nội dung file trong RAM rồi đẩy thẳng vào response hoặc vào SDK của
 * S3/MinIO, khỏi tạo file tạm.
 * <p>
 * 7. GIỚI HẠN VÀ RỦI RO
 * - Tất cả nằm trong heap. File 500MB dựng trong {@code ByteArrayOutputStream} là 500MB
 * heap, cộng thêm một bản sao nữa mỗi lần gọi {@code toByteArray()}.
 * - {@code ByteArrayOutputStream} lớn dần bằng cách cấp mảng mới gấp đôi rồi chép sang, nên
 * đỉnh bộ nhớ có lúc gấp ba lần dữ liệu. Biết trước cỡ thì truyền vào constructor
 * ({@code new ByteArrayOutputStream(16384)}) để tránh chép đi chép lại.
 * - Giới hạn cứng khoảng 2GB vì độ dài mảng Java là {@code int}.
 * - Luật: chỉ dùng cho dữ liệu mình biết chắc là nhỏ. Dữ liệu người dùng thì dùng file tạm
 * (bài t8 mục 5) hoặc stream thẳng.
 * <p>
 * 8. HAI LỚP HIỆN ĐẠI HƠN KHÔNG CÓ TRONG TRANG GỐC
 * - {@code InputStream.nullInputStream()} và {@code OutputStream.nullOutputStream()}
 * (Java SE 11): stream rỗng và thùng rác - tiện cho test và cho nhánh "không làm gì".
 * - {@code new String(baos.toByteArray(), UTF_8)} nên viết thành
 * {@code baos.toString(StandardCharsets.UTF_8)} (Java SE 10+) - overload nhận {@code Charset},
 * tránh được overload cũ {@code toString(int)} vốn đã deprecated và có ngữ nghĩa hoàn toàn khác.
 * <p>
 * 9. ĐỐI CHIẾU TYPESCRIPT
 * - {@code ByteArrayInputStream} ≈ {@code Readable.from(buffer)} của Node;
 * {@code ByteArrayOutputStream} ≈ gom các chunk rồi {@code Buffer.concat(chunks)}.
 * - Node KHÔNG có lớp sẵn cho chuyện gom - thường phải tự viết, hoặc dùng gói
 * {@code concat-stream}. Chỗ này Java có sẵn và gọn hơn.
 * - {@code StringReader} ≈ {@code Readable.from("chuoi")}.
 * - Mẹo test ở mục 6 giống hệt bên Node: nhận {@code Readable} thay vì nhận đường dẫn file
 * để test khỏi chạm đĩa. Cùng một nguyên tắc thiết kế, không phụ thuộc ngôn ngữ.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 10. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao API lại cần stream chạy trên bộ nhớ, nêu kịch bản gốc của trang tài liệu.
 * - Điền bốn lớp vào bảng 2x2 nguồn/đích × ký tự/byte.
 * - {@code ByteArrayOutputStream} có ba method lấy kết quả, mỗi cái trả về gì?
 * - {@code toByteArray()} trả bản sao hay mảng gốc? Còn {@code getBuffer()} của
 * {@code StringWriter}?
 * - Các lớp này là nguồn thật hay decorator, nhận ra bằng cách nào?
 * - {@code close()} trên {@code ByteArrayOutputStream} làm gì?
 * - Vì sao nên để hàm nhận {@code InputStream} thay vì nhận {@code Path}?
 * - Đỉnh bộ nhớ khi ghi 100MB vào {@code ByteArrayOutputStream} là bao nhiêu, vì sao?
 */
public class t17_InMemoryIoStreams {

    void main() {
        // TODO 1 (mục 4, 6): Ghi chuỗi tiếng Việt vào ByteArrayOutputStream qua một
        //   OutputStreamWriter(UTF_8). In size() và so với chuỗi.length(). Giải thích chênh lệch.
        //
        // TODO 2 (mục 1, 4): Ghi cùng chuỗi đó qua GZIPOutputStream bọc ByteArrayOutputStream.
        //   In size() của cả hai cách và tính tỉ lệ nén. Đây là kịch bản gốc ở mục 1.
        //
        // TODO 3 (mục 1): Viết hàm ghi ra file theo định dạng: 4 byte độ dài (DataOutputStream)
        //   rồi tới dữ liệu đã nén. Dùng ByteArrayOutputStream để biết độ dài trước.
        //   Viết luôn hàm đọc ngược lại.
        //
        // TODO 4 (mục 4): Gọi toByteArray() hai lần, sửa một phần tử của mảng thứ nhất rồi in
        //   mảng thứ hai. Xác nhận nó KHÔNG đổi - chứng minh đó là bản sao.
        //
        // TODO 5 (mục 3): Dùng StringWriter, ghi vài dòng, lấy getBuffer() rồi append thêm chữ
        //   vào buffer đó. In toString() của writer và xác nhận nó ĐÃ ĐỔI.
        //
        // TODO 6 (mục 6): Viết hàm demNguyenAm(InputStream in) đếm số nguyên âm. Test nó bằng
        //   ByteArrayInputStream, không tạo file nào. Nêu vì sao test này nhanh hơn test dùng file.
        //
        // TODO 7 (mục 2, 3): Đọc cùng một nội dung bằng cả StringReader và CharArrayReader,
        //   mỗi cái bọc trong BufferedReader để dùng readLine(). So sánh code hai cách.
        //
        // TODO 8 (mục 7): Đo heap trước và sau khi ghi 100MB vào ByteArrayOutputStream
        //   (dùng Runtime.getRuntime().totalMemory() - freeMemory()). Chạy lại với constructor
        //   có truyền sẵn dung lượng và so sánh.
        //
        // TODO 9 (mục 8): Thay new String(baos.toByteArray(), UTF_8) bằng
        //   baos.toString(StandardCharsets.UTF_8). Rồi thử gọi baos.toString(0) và đọc javadoc
        //   xem overload deprecated đó thực sự làm gì.
    }
}
