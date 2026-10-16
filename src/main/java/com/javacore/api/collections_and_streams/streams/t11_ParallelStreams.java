package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Parallelizing Streams
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 11/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài này trả lời câu hỏi "thêm parallel() có nhanh hơn không". Câu trả lời ngắn: thường là không.
 * Đọc kỹ để biết VÌ SAO, dựa trên đặc tính stream ở bài 6.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/parallel-streams/">dev.java - Parallelizing Streams</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PARALLELIZING STREAMS (mục 1 đến 8)
 * ======================================================================
 * <p>
 * 1. TỐI ƯU PHÉP TÍNH TRÊN STREAM
 * - (Optimizing Stream Computations)
 * - Gọi {@code parallel()} trên stream bất kỳ là xong về mặt cú pháp, nhưng KHÔNG đảm bảo nhanh hơn.
 * - Chỉ tối ưu khi có yêu cầu hiệu năng thật, và phải đo trong môi trường giống production.
 * <p>
 * 2. CÁCH SONG SONG HÓA ĐƯỢC CÀI ĐẶT
 * - (Parallelization Implementation)
 * - Chia để trị đệ quy trên Fork/Join Framework (JDK 7): chẻ dữ liệu thành phần nhỏ,
 * mỗi core xử lý một phần.
 * - Chi phí chẻ và gộp phải NHỎ so với phần tính toán, nếu không thì song song còn chậm hơn.
 * <p>
 * 3. TÍNH CỤC BỘ CỦA DỮ LIỆU
 * - (Understanding Data Locality)
 * - Dữ liệu từ RAM lên cache CPU theo dòng (thường 64 byte).
 * - {@code int[]}: locality tuyệt vời. {@code Integer[]}: kém vì phải theo con trỏ.
 * {@code LinkedList<Integer>}: tệ nhất, chạy lòng vòng theo con trỏ.
 * - Đây là lý do {@code IntStream} hơn hẳn {@code Stream<Integer>} khi chạy song song.
 * <p>
 * 4. CHẺ NGUỒN DỮ LIỆU
 * - (Splitting a Source of Data)
 * - {@code ArrayList} chẻ nhanh và đều. {@code LinkedList} phải duyệt mới chẻ được.
 * {@code HashSet} chẻ không đều. {@code TreeSet} chẻ đều nhưng vẫn phải theo con trỏ.
 * - {@code Files.lines()}, {@code Pattern.splitAsStream()}, {@code Stream.generate()}
 * rất khó hoặc không chẻ được.
 * <p>
 * 5. CHẺ VÀ PHÂN PHỐI CÔNG VIỆC
 * - (Splitting and Dispatching Work)
 * - Fork/Join quản lý thread pool và dùng cơ chế work-stealing: thread rảnh lấy việc
 * từ hàng đợi của thread khác.
 * - Hệ quả: thứ tự xử lý không đoán trước được.
 * <p>
 * 6. XỬ LÝ MỘT SUB-STREAM
 * - (Processing a Substream)
 * - Đụng vào state khả biến bên ngoài từ nhiều thread là race condition;
 * cùng ghi vào một {@code ArrayList} là MẤT dữ liệu, không exception.
 * - Các toán tử "stateful" như {@code limit()}, {@code skip()}, {@code findFirst()}
 * tốn thêm chi phí vì phải chia sẻ bộ đếm nội bộ.
 * <p>
 * 7. CHI PHÍ CỦA VIỆC CHẠY SONG SONG
 * - (Understanding the Overhead of Computing a Stream in Parallel)
 * - Gồm: chẻ dữ liệu, chia đều, tránh đụng state ngoài, và GỘP kết quả từng phần.
 * - Gộp số nguyên thì rẻ; gộp collection hay HashMap thì đắt.
 * <p>
 * 8. LUẬT DÙNG PARALLEL STREAM
 * - (Stating Some Rules to Use Parallel Streams Properly)
 * - Chỉ tối ưu khi chưa đạt yêu cầu hiệu năng.
 * - Chọn nguồn dữ liệu chẻ tốt.
 * - Không sửa state bên ngoài.
 * - ĐO chứ đừng đoán.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 9. CHUNG MỘT COMMON FORKJOINPOOL
 * - Mặc định mọi parallel stream trong JVM dùng chung {@code ForkJoinPool.commonPool()},
 * kích thước bằng số core trừ một.
 * - Trong ứng dụng web, một parallel stream chạy tác vụ chặn (gọi I/O, gọi DB) sẽ
 * làm nghẽn pool dùng chung của cả tiến trình. Đây là lý do thực tế hay cấm parallel stream
 * ở service layer.
 * <p>
 * 10. NHẮC LẠI TÍNH KẾT HỢP TỪ BÀI 4
 * - Toán tử reduce không có tính kết hợp chỉ sai khi chạy SONG SONG. Thêm một chữ
 * {@code parallel()} có thể làm hỏng code vốn đang chạy đúng mà không báo lỗi gì.
 * <p>
 * 11. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - JS chạy một luồng, không có khái niệm tương đương; muốn song song phải dùng Worker
 * và truyền thông điệp. Java cho song song gần như miễn phí về cú pháp, nên cái bẫy
 * ở đây không phải viết khó mà là viết dễ quá.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 12. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Parallel stream chạy trên framework nào, chẻ dữ liệu theo nguyên tắc gì?
 * - Vì sao ArrayList chẻ tốt hơn LinkedList?
 * - Data locality là gì, ảnh hưởng tới lựa chọn IntStream so với Stream ra sao?
 * - Ba thứ làm parallel stream chậm đi thay vì nhanh lên?
 * - Vì sao không nên chạy tác vụ chặn trong parallel stream?
 */
public class t11_ParallelStreams {

    void main() {
        // Nguồn chẻ tốt (range trên kiểu nguyên thủy) + toán tử kết hợp được = trường hợp lý tưởng.
        long sum = IntStream.rangeClosed(1, 1_000_000).parallel().asLongStream().sum();
        IO.println("tong 1..1tr: " + sum);

        List<Integer> small = List.of(1, 2, 3, 4, 5);
        IO.println("song song tren list nho (thuong cham hon tuan tu): "
                + small.parallelStream().mapToInt(Integer::intValue).sum());

        // TODO 1 (mục 1 + 8): Đo thời gian sum trên 10 triệu phần tử với stream() và parallelStream().
        //   Chạy mỗi bản vài lần, ghi lại con số thật thay vì cảm giác.
        //
        // TODO 2 (mục 3 + 4): Lặp lại TODO 1 với ArrayList<Integer> và LinkedList<Integer>.
        //   Ghi lại chênh lệch và giải thích bằng data locality.
        //
        // TODO 3 (mục 6): Dùng parallelStream().forEach() để add vào một ArrayList dùng chung.
        //   Chạy nhiều lần, ghi lại kích thước list mỗi lần. Sau đó sửa lại bằng collect().
        //
        // TODO 4 (mục 10): Lấy lại ví dụ reduce phép trừ ở bài 4, chạy parallel và ghi kết quả.
        //
        // TODO 5 (mục 6): So sánh thời gian findFirst() và findAny() trên parallel stream lớn.
        //
        // TODO 6 (mục 9): In ra Thread.currentThread().getName() bên trong một parallel stream
        //   để thấy tên các thread của commonPool.
    }
}
