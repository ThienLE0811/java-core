package com.javacore.api.io.java_io;

/**
 * Putting it All Together
 * <p>
 * 0. Vị trí bài này
 * - Bước 21/41, bài 18/18 - bài CUỐI của section The Java I/O API.
 * - Là một nhóm riêng trong section, không thuộc File System Basics hay File Operations Basics.
 * - Một câu cho cả bài: một bài thực hành dài - tự thiết kế một ĐỊNH DẠNG FILE NHỊ PHÂN lưu
 * 154 bài sonnet của Shakespeare dạng nén, truy cập ngẫu nhiên được; mọi thứ học từ t12 đến
 * t17 được ghép lại trong một chương trình.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/io/java-io/putting-it-all-together/">dev.java - Putting it All Together</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PUTTING IT ALL TOGETHER (mục 1 đến 6)
 * ======================================================================
 * <p>
 * 1. ĐỊNH DẠNG FILE CẦN DỰNG
 * - (Introducing the Shakespeare Sonnet Example)
 * - Bài toán: lưu 154 bài sonnet vào MỘT file, nén lại, nhưng vẫn đọc được bài số 42 mà
 * không phải giải nén 41 bài trước đó.
 * - Cấu trúc file:
 * + Một số nguyên: TỔNG SỐ bài sonnet.
 * + Với mỗi bài: hai số nguyên - OFFSET và ĐỘ DÀI.
 * + Sau đó là toàn bộ phần text đã nén GZIP.
 * - BẪY nguyên văn: "The offsets are computed from the start of the array containing all the
 * compressed sonnets, not the start of the file" - offset tính từ đầu VÙNG DỮ LIỆU, không
 * phải từ đầu file. Muốn offset tuyệt đối thì phải tự cộng thêm phần header.
 * <p>
 * 2. ĐỌC FILE TEXT NGUỒN
 * - (Reading the Sonnets Text File)
 * - Hai lối: tải về máy rồi đọc bằng {@code Files}, hoặc đọc thẳng qua mạng bằng
 * {@code HttpClient}.
 * - Cả hai đều cho ra một {@code InputStream}, từ đó chuyển tiếp thành {@code Reader} -
 * đúng vai trò cây cầu của {@code InputStreamReader} ở bài t16 mục 2.
 * - Trang gốc ghi rõ đoạn minh họa đã lược bỏ phần bắt exception và phần đóng tài nguyên.
 * <p>
 * 3. PHÂN TÍCH FILE TEXT
 * - (Analyzing the Sonnets Text File)
 * - Kỹ thuật: THỪA KẾ {@code BufferedReader} để thêm logic phân tích mà vẫn giữ nguyên
 * năng lực đệm và {@code readLine()} - decorator do mình tự viết (bài t16 mục 9).
 * - Ba việc lớp con cần làm:
 * + Bỏ qua các dòng header ở đầu file.
 * + Bỏ qua số thứ tự bài (chữ số La Mã) và dòng trống theo sau.
 * + Đọc phần text của một bài cho tới khi gặp dấu hiệu kết thúc.
 * - "If a blank line is met, then the sonnet has been fully read" - dòng trống là ranh giới
 * giữa các bài.
 * - Dựng một lớp {@code Sonnet} bọc quanh {@code List<String>}: "makes your code more readable
 * and maintainable" so với truyền list trần đi khắp nơi.
 * - Vì {@code SonnetReader} thừa kế {@code BufferedReader} nên nó tự động dùng được với
 * try-with-resources.
 * <p>
 * 4. GHI MỘT BÀI SONNET ĐÃ NÉN
 * - (Writing a Single Compressed Sonnet)
 * - Chuỗi decorator ba lớp:
 * {@code PrintWriter} -> {@code GZIPOutputStream} -> {@code ByteArrayOutputStream}.
 * - BẪY ĐÁNG NHỚ NHẤT CỦA BÀI, nguyên văn: "Even if no I/O resource is used in this method,
 * using a try-with-resources statement is still very useful: it will flush for you the
 * internal buffers". Không có tài nguyên hệ thống nào ở đây cả, nhưng không đóng thì dữ liệu
 * còn kẹt trong bộ đệm của {@code PrintWriter} và {@code GZIPOutputStream}, kết quả ra thiếu.
 * - Ghi xong thì hỏi {@code ByteArrayOutputStream.size()} để biết bài này nén xong dài bao
 * nhiêu byte - đúng kịch bản lý do tồn tại ở bài t17 mục 1.
 * <p>
 * 5. GHI TOÀN BỘ
 * - (Writing all the Sonnets)
 * - Chuỗi decorator: {@code DataOutputStream} -> {@code BufferedOutputStream} -> đích.
 * - Quy trình:
 * + Lặp qua mọi bài, nén từng bài riêng theo mục 4.
 * + Ghi lại offset và độ dài của từng bài vào hai danh sách số nguyên.
 * + Ghi ra file theo đúng cấu trúc ở mục 1: tổng số, rồi các cặp offset/độ dài, rồi vùng
 * dữ liệu nén.
 * - {@code DataOutputStream} lo phần ghi số nguyên (bài t16 mục 4),
 * {@code BufferedOutputStream} lo hiệu năng.
 * <p>
 * 6. ĐỌC LẠI MỘT BÀI
 * - (Reading a Single Sonnet)
 * - Chuỗi decorator khi đọc, từ trong ra ngoài:
 * {@code ByteArrayInputStream} -> {@code GZIPInputStream} -> {@code InputStreamReader} ->
 * {@code BufferedReader}.
 * - BẪY SỐNG CÒN, nguyên văn: "When you call the skip(n) method, the system may have not
 * skipped the correct amount of bytes" - {@code skip()} có thể nhảy ÍT HƠN số byte yêu cầu,
 * phải kiểm tra giá trị trả về và lặp lại cho tới đủ.
 * - Điều tương tự đúng với đọc: "It is possible that the amount of bytes read by the input
 * stream is lesser than what you asked for" - đúng bẫy đã nêu ở bài t15 mục 4.
 * Lời giải sẵn có: {@code readFully()} của {@code DataInputStream} (bài t16 mục 4), hoặc
 * {@code readNBytes()}.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 7. BÀI NÀY ÔN LẠI NHỮNG GÌ - BẢNG TRUY NGƯỢC
 * <p>
 * .  kỹ thuật dùng trong bài              học ở bài
 * .  Path, Files đọc file nguồn           t2, t3, t13
 * .  try-with-resources, flush đúng lúc   t12
 * .  BufferedReader, readLine, charset    t14
 * .  InputStream/OutputStream, skip, read t15
 * .  GZIP, DataOutput, tự viết decorator  t16
 * .  ByteArrayOutput để đo kích thước     t17
 * <p>
 * - Nếu chỗ nào trong bài này thấy lạ, quay lại đúng bài tương ứng rồi trở lại.
 * <p>
 * 8. BA BÀI HỌC THIẾT KẾ RÚT RA ĐƯỢC
 * - HEADER TRƯỚC, DỮ LIỆU SAU. Mọi định dạng nhị phân truy cập ngẫu nhiên đều theo khuôn này:
 * một bảng mục lục cỡ cố định ở đầu, dữ liệu kích thước thay đổi ở sau. Đây là cách ZIP,
 * PDF, class file của Java đều hoạt động.
 * - NÉN TỪNG KHỐI RIÊNG, KHÔNG NÉN CẢ KHỐI. Nén cả file thì tỉ lệ nén tốt hơn, nhưng mất
 * khả năng nhảy thẳng tới một bài. Đây là đánh đổi kinh điển dung lượng đổi lấy tốc độ
 * truy cập.
 * - TẠO KIỂU RIÊNG THAY VÌ DÙNG KIỂU CHUNG. Lớp {@code Sonnet} thay cho
 * {@code List<String>} là ví dụ nhỏ của nguyên tắc tránh "primitive obsession" - code đọc
 * ra ý nghĩa thay vì đọc ra cấu trúc dữ liệu.
 * <p>
 * 9. NẾU VIẾT LẠI BẰNG CÔNG CỤ SẴN CÓ
 * - Chuyện bài này làm thủ công thì định dạng ZIP đã làm sẵn: mỗi entry nén riêng, có bảng
 * mục lục, truy cập ngẫu nhiên được. Dùng {@code ZipOutputStream} hoặc mở zip như một file
 * system (bài t4 mục 5) là xong.
 * - Nhưng tự dựng một lần là cách duy nhất hiểu VÌ SAO ZIP lại có cấu trúc đó. Đây là giá
 * trị của bài, không phải ở sản phẩm cuối.
 * - Trong production: đừng tự phát minh định dạng nhị phân. Dùng ZIP, Parquet, Avro hoặc
 * Protobuf - chúng đã giải xong các bẫy về offset, phiên bản và endianness.
 * <p>
 * 10. ĐỐI CHIẾU TYPESCRIPT
 * - Toàn bộ bài này bên Node sẽ là {@code zlib.gzipSync} cộng {@code Buffer.writeInt32BE} và
 * {@code buffer.subarray(offset, offset + len)} - gọn hơn vì {@code Buffer} cho cắt lát trực
 * tiếp, không cần cả chuỗi decorator.
 * - Đổi lại, Node không có {@code readFully}, nên bẫy "đọc thiếu byte" ở mục 6 phải tự xử lý
 * bằng tay trong mọi trường hợp.
 * - Chuyện {@code skip()} nhảy thiếu byte là đặc tính của hệ điều hành, không phải của Java -
 * {@code fs.read} của Node cũng trả về số byte thực đọc, với lý do y hệt.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 11. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Mô tả cấu trúc file sonnet theo đúng thứ tự các phần.
 * - Offset trong file được tính từ đâu, nếu hiểu nhầm thì hậu quả là gì?
 * - Vì sao nén từng bài riêng thay vì nén cả file một lượt?
 * - Vì sao cần {@code ByteArrayOutputStream} ở bước ghi, không ghi thẳng ra file được à?
 * - Chuỗi decorator khi GHI gồm những lớp nào, khi ĐỌC gồm những lớp nào?
 * - Vì sao cần try-with-resources cho đoạn code không hề mở tài nguyên hệ thống nào?
 * - {@code skip(n)} có đảm bảo nhảy đúng n byte không, xử lý thế nào cho đúng?
 * - Dùng gì thay cho vòng lặp đọc thủ công để chắc chắn đọc đủ số byte?
 * - Lợi ích của việc dựng lớp {@code Sonnet} thay vì dùng thẳng {@code List<String>}?
 */
public class t18_PuttingItAllTogether {

    void main() {
        // Bài này nên làm tuần tự từ TODO 1 tới TODO 8, mỗi TODO chạy được rồi mới sang bước sau.
        // File nguồn: bản text 154 sonnet của Shakespeare (Project Gutenberg), tải về đặt cạnh
        // thư mục này hoặc đọc qua HttpClient.
        //
        // TODO 1 (mục 2): Đọc file text nguồn bằng Files.newInputStream + InputStreamReader +
        //   BufferedReader. In 20 dòng đầu để nhìn thấy cấu trúc header và cách đánh số bài.
        //
        // TODO 2 (mục 3): Dựng record Sonnet(int so, List<String> cacDong) với method
        //   toText(). Chưa cần đọc file, chỉ cần dựng tay một đối tượng và in ra.
        //
        // TODO 3 (mục 3): Viết lớp SonnetReader thừa kế BufferedReader, có method
        //   readSonnet() trả về Sonnet hoặc null khi hết file. Xử lý ba việc ở mục 3.
        //   Dùng nó trong try-with-resources và đếm xem đọc được đúng 154 bài không.
        //
        // TODO 4 (mục 4): Viết hàm byte[] nen(Sonnet s): PrintWriter -> GZIPOutputStream ->
        //   ByteArrayOutputStream, tất cả trong try-with-resources. In size() của bài số 1.
        //
        // TODO 5 (mục 4): Cố tình BỎ try-with-resources ở TODO 4 (chỉ gọi toByteArray mà không
        //   đóng). So sánh size() với lần trước và dán lại chênh lệch. Đây là minh chứng cho
        //   câu trích ở mục 4.
        //
        // TODO 6 (mục 1, 5): Viết hàm ghi toàn bộ ra file: DataOutputStream ->
        //   BufferedOutputStream -> newOutputStream. Ghi tổng số, các cặp offset/độ dài,
        //   rồi vùng dữ liệu nén. In Files.size() của file kết quả.
        //
        // TODO 7 (mục 6): Viết hàm Sonnet doc(Path file, int so) đọc đúng MỘT bài: đọc header,
        //   tính vị trí, skip tới nơi, đọc đúng số byte, giải nén. Dùng readFully hoặc
        //   readNBytes chứ ĐỪNG dùng read(byte[]) trần.
        //
        // TODO 8 (mục 6): Trong hàm ở TODO 7, thay readFully bằng một lời gọi skip(n) duy nhất
        //   không kiểm tra giá trị trả về. Tìm một bài sonnet mà cách này cho kết quả sai hoặc
        //   ném exception, rồi dán lại.
        //
        // TODO 9 (mục 9): Làm lại toàn bộ bài toán bằng ZipOutputStream, mỗi sonnet là một
        //   entry. So sánh kích thước file và số dòng code với bản tự dựng.
    }
}
