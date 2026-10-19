package com.javacore.api.io.java_io;

/**
 * Decorating I/O Streams
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 16/18 trong section The Java I/O API của lộ trình dev.java/learn.
 * - Thuộc nhóm File Operations Basics của section.
 * - Một câu cho cả bài: đây là bài giải thích VÌ SAO API I/O có nhiều lớp đến thế - mỗi lớp
 * là một lớp áo BỌC quanh stream khác, thêm đúng một năng lực (đệm, dịch ký tự, nén, kiểu
 * nguyên thủy); ghép chúng lại là dựng được đúng thứ mình cần.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/reading-writing/decorating/">dev.java - Decorating I/O Streams</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - DECORATING I/O STREAMS (mục 1 đến 4)
 * ======================================================================
 * <p>
 * 1. MỤC ĐÍCH CỦA DECORATION
 * - (The Purpose of Decoration)
 * - Decorator Pattern: mở rộng hoặc thay đổi hành vi của một lớp bằng cách BỌC một đối tượng
 * cùng kiểu vào trong, rồi ủy thác lời gọi cho nó.
 * - Phân biệt hai tầng trong cây thừa kế của {@code Reader}:
 * + Lớp cài đặt NGUỒN THẬT: {@code CharArrayReader}, {@code StringReader},
 * {@code InputStreamReader}, {@code FileReader}. Chúng biết dữ liệu nằm ở đâu.
 * + Lớp TRANG TRÍ: {@code BufferedReader}, {@code LineNumberReader}. Chúng nhận một
 * {@code Reader} khác vào constructor và thêm năng lực lên trên.
 * - Nhận ra qua constructor: nhận {@code Reader}/{@code InputStream} làm tham số thì đó là
 * decorator.
 * <p>
 * 2. GHI VÀ ĐỌC KÝ TỰ QUA STREAM NHỊ PHÂN
 * - (Writing and Reading Characters to Binary Streams)
 * - {@code InputStreamReader} và {@code OutputStreamWriter} là CÂY CẦU giữa thế giới byte và
 * thế giới ký tự - đúng hai ô của bảng 2x2 ở bài t1.
 * - "The OutputStreamWriter is a Writer object that writes to the OutputStream" mà
 * {@code Files} tạo ra.
 * - Cả hai nên đặt trong try-with-resources để đảm bảo thứ tự đẩy đệm (flush) đúng; thiếu
 * điều đó thì ký tự còn kẹt trong bộ đệm, file ra thiếu nội dung.
 * - Ví dụ trang gốc: ghi một bài sonnet của Shakespeare ra {@code sonnet.txt} bằng
 * {@code OutputStreamWriter}, rồi đọc lại bằng {@code InputStreamReader} bọc trong
 * {@code BufferedReader}, gom dòng bằng {@code Collectors.joining()}.
 * <p>
 * 3. XỬ LÝ STREAM NÉN
 * - (Handling Compressed Binary Streams)
 * - {@code GZIPInputStream} và {@code GZIPOutputStream}.
 * - Nguyên văn: "These two classes are extensions of the base classes InputStream and
 * OutputStream. They just override the reading and the writing of bytes, without adding any
 * method." Tức là decorator thuần túy - KHÔNG thêm API mới, chỉ đổi hành vi.
 * - Hệ quả thực tế rất đẹp: thêm nén vào code có sẵn chỉ là chèn một lớp bọc, phần còn lại
 * không đổi một dòng. File sinh ra đọc được bằng lệnh {@code gzip} thông thường.
 * <p>
 * 4. XỬ LÝ STREAM KIỂU NGUYÊN THỦY
 * - (Handling Streams of Primitive Types)
 * - {@code DataOutputStream} / {@code DataInputStream} - decorator THÊM API mới, khác với
 * nhóm gzip ở mục 3.
 * - Ghi: {@code writeByte()}, {@code writeBoolean()}, {@code writeChar()}, {@code writeShort()},
 * {@code writeInt()}, {@code writeLong()}, {@code writeFloat()}, {@code writeDouble()};
 * với chuỗi có {@code writeBytes()}, {@code writeChars()}, {@code writeUTF()}.
 * - Đọc: {@code readBoolean()}, {@code readChar()}, {@code readShort()}, {@code readInt()},
 * {@code readLong()}, {@code readFloat()}, {@code readDouble()}, {@code readUnsignedByte()},
 * {@code readUnsignedShort()}, {@code readUTF()}, {@code readFully()}.
 * - BẪY CẮT BỚT BIT: {@code writeByte(int)} chỉ ghi 8 bit THẤP, bỏ 24 bit cao;
 * {@code writeBytes(String)} bỏ 8 bit cao của mỗi ký tự.
 * - {@code readFully()} lấp ĐẦY mảng, chặn nếu cần, và ném {@code EOFException} nếu stream
 * hết sớm - đây là lời giải cho bẫy "read(byte[]) không lấp đầy mảng" ở bài t15 mục 4.
 * - Ví dụ trang gốc: ghi sáu số nguyên (24 byte) rồi đọc lại.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 5. CÁCH ĐỌC MỘT CHUỖI DECORATOR
 * - Đọc từ TRONG RA NGOÀI: lớp trong cùng nói dữ liệu Ở ĐÂU, các lớp ngoài nói XỬ LÝ THẾ NÀO.
 * - Ví dụ đầy đủ của bài t18:
 * <p>
 * .  new BufferedReader(          &lt;- gom thành dòng, có đệm
 * .    new InputStreamReader(     &lt;- byte thành ký tự, theo charset
 * .      new GZIPInputStream(     &lt;- giải nén
 * .        new ByteArrayInputStream(data))))   &lt;- nguồn: mảng byte trong RAM
 * <p>
 * - Thứ tự bọc KHÔNG đổi chỗ được. Giải nén phải xảy ra trước khi dịch ký tự, vì dữ liệu nén
 * không phải văn bản.
 * - Chiều GHI là ảnh gương của chiều ĐỌC: ghi bằng {@code PrintWriter} ->
 * {@code GZIPOutputStream} -> {@code ByteArrayOutputStream} thì đọc phải theo đúng thứ tự
 * ngược lại.
 * <p>
 * 6. HAI LOẠI DECORATOR - PHÂN BIỆT ĐƯỢC LÀ HIỂU BÀI
 * - Loại GIỮ NGUYÊN API, chỉ đổi hành vi: {@code BufferedInputStream}, {@code GZIPInputStream}.
 * Bọc vào rồi vẫn dùng {@code read()} như cũ, nên bọc được bao nhiêu lớp tùy thích.
 * - Loại THÊM API: {@code DataInputStream} (thêm {@code readInt}...),
 * {@code BufferedReader} (thêm {@code readLine}), {@code LineNumberReader} (thêm
 * {@code getLineNumber}), {@code PrintWriter} (thêm {@code println}).
 * Loại này phải nằm NGOÀI CÙNG, vì bọc thêm lớp nữa là mất API mới.
 * - Luật nhớ: lớp cho bạn method mới thì để ngoài cùng.
 * <p>
 * 7. ĐÓNG MỘT LẦN LÀ ĐỦ
 * - Đóng lớp ngoài cùng sẽ đóng dây chuyền toàn bộ các lớp bên trong. Nên try-with-resources
 * chỉ cần khai báo biến ngoài cùng.
 * - Nhưng nếu dựng từng lớp vào biến riêng thì phải khai báo HẾT trong try-with-resources,
 * vì lỗi xảy ra giữa chừng việc dựng chuỗi có thể để lại lớp trong chưa đóng.
 * - {@code flush()} cũng lan xuống: {@code flush()} ở lớp ngoài đẩy đệm của mọi lớp trong.
 * Đây là lý do bài t18 nói dùng try-with-resources có ích "ngay cả khi không có tài nguyên
 * I/O nào" - chỉ để được flush đúng lúc.
 * <p>
 * 8. BỐN DECORATOR HAY DÙNG NHẤT NGOÀI TRANG GỐC
 * - {@code PrintWriter} / {@code PrintStream}: thêm {@code println}, {@code printf}.
 * BẪY: chúng NUỐT {@code IOException}, phải hỏi {@code checkError()} mới biết có lỗi.
 * {@code System.out} chính là một {@code PrintStream}.
 * - {@code ObjectOutputStream} / {@code ObjectInputStream}: serialization của Java.
 * Trong code hiện đại nên TRÁNH - nó là nguồn của cả một họ lỗ hổng thực thi mã từ xa.
 * Dùng JSON hoặc một định dạng tường minh thay thế.
 * - {@code ZipInputStream} / {@code ZipOutputStream}: nhiều entry, khác {@code GZIP} vốn chỉ
 * nén MỘT luồng byte.
 * - {@code CipherInputStream} / {@code CipherOutputStream}: mã hóa, ghép ngay vào chuỗi này.
 * <p>
 * 9. GÓC NHÌN PRODUCTION
 * - Decorator là lý do API I/O trông rối với người mới, nhưng nó khiến các năng lực GHÉP
 * được với nhau. "Đọc file log nén, giải nén, dịch UTF-8, đọc từng dòng" là bốn lớp bọc và
 * không cần thư viện ngoài nào.
 * - Trong Spring Boot, trả file nén về client: bọc
 * {@code new GZIPOutputStream(response.getOutputStream())} - đúng một dòng.
 * - Luôn có {@code Buffered...} trong chuỗi nếu bạn đọc/ghi từng phần nhỏ. Vị trí hợp lý
 * thường là sát ngay lớp chạm đĩa.
 * - Lớp của bạn cũng decorate được: thừa kế {@code BufferedReader} rồi thêm logic phân tích
 * là mẫu mà bài t18 dùng, và nó vẫn chạy với try-with-resources.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - Decorator của Java ≈ {@code pipe} của Node:
 * {@code fs.createReadStream(f).pipe(zlib.createGunzip()).pipe(split2())}.
 * Cùng ý tưởng ghép năng lực, chỉ khác cú pháp - Java bọc bằng constructor, Node nối bằng
 * {@code pipe}.
 * - Chuỗi {@code pipe} của Node đọc từ TRÁI SANG PHẢI theo dòng chảy dữ liệu; chuỗi
 * constructor của Java đọc từ TRONG RA NGOÀI. Cùng một thứ tự, trình bày ngược nhau.
 * - {@code zlib.createGunzip()} ≈ {@code GZIPInputStream}.
 * - Node không có tương đương {@code DataInputStream}; đọc số nguyên nhị phân phải dùng
 * {@code buffer.readInt32BE()} thủ công.
 * - Chú ý: "decorator" ở đây là DESIGN PATTERN, không liên quan gì tới cú pháp
 * {@code @Decorator} của TypeScript. Lại một bẫy trùng tên như I/O stream với Stream API.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Nhìn vào constructor thì biết lớp đó là nguồn thật hay là decorator bằng cách nào?
 * - Hai lớp nào là cầu nối giữa byte và ký tự?
 * - {@code GZIPInputStream} có thêm method mới nào không, điều đó có ý nghĩa gì?
 * - Hai loại decorator khác nhau ra sao, loại nào bắt buộc nằm ngoài cùng?
 * - Đọc chuỗi bốn lớp bọc ở mục 5 từ hướng nào, vì sao không đổi được thứ tự?
 * - Đóng chuỗi decorator thì phải đóng mấy lớp?
 * - {@code readFully()} giải quyết bẫy nào của bài t15?
 * - Vì sao {@code PrintWriter} nguy hiểm khi ghi file quan trọng?
 */
public class t16_DecoratingIoStreams {

    void main() {
        // TODO 1 (mục 1): Mở javadoc của Reader, liệt kê các lớp con trực tiếp. Xếp chúng vào
        //   hai cột: NGUỒN THẬT và DECORATOR, dựa vào constructor. Ghi kết quả vào đây.
        //   (Nối tiếp TODO 4 của bài t1.)
        //
        // TODO 2 (mục 2): Dùng Files.newOutputStream + OutputStreamWriter (charset UTF_8) ghi
        //   vài dòng có dấu tiếng Việt. Đọc lại bằng newInputStream + InputStreamReader +
        //   BufferedReader, gom bằng Collectors.joining("\n"). Tất cả trong try-with-resources.
        //
        // TODO 3 (mục 3): Lấy đúng code TODO 2, chèn thêm GZIPOutputStream và GZIPInputStream
        //   vào chuỗi. Xác nhận phần code còn lại KHÔNG đổi một dòng nào.
        //
        // TODO 4 (mục 3): So sánh Files.size() của file ở TODO 2 và TODO 3. Rồi mở file nén
        //   bằng 7-Zip hoặc lệnh gzip -d để xác nhận nó là gzip chuẩn.
        //
        // TODO 5 (mục 4): Dùng DataOutputStream ghi 6 số int, kiểm tra Files.size() đúng 24 byte.
        //   Đọc lại bằng DataInputStream.readInt() trong vòng lặp cho tới khi EOFException.
        //
        // TODO 6 (mục 4): Gọi writeByte(300) rồi readUnsignedByte(). Dán lại giá trị đọc được
        //   và giải thích theo bẫy cắt bit ở mục 4.
        //
        // TODO 7 (mục 4): Ghi một mảng 10 byte rồi đọc lại bằng readFully(new byte[20]).
        //   Dán lại exception và đối chiếu với hành vi của read(byte[]) ở bài t15 TODO 2.
        //
        // TODO 8 (mục 5, 6): Dựng chuỗi 4 lớp như sơ đồ ở mục 5 và chạy được. Sau đó thử ĐẢO
        //   thứ tự GZIPInputStream với InputStreamReader, dán lại lỗi.
        //
        // TODO 9 (mục 8): Ghi file bằng PrintWriter bọc một OutputStream đã đóng trước đó.
        //   Xác nhận KHÔNG có exception nào, rồi gọi checkError() để phát hiện lỗi.
    }
}
