package com.javacore.api.virtual_threads;

/**
 * Capturing Task Results
 * <p>
 * 0. Vị trí bài này
 * - Bước 29/41, bài 4/8 - mục thứ tư của trang Virtual Threads.
 * - Bài này là chỗ virtual thread "trả công" cho bạn: thứ bị coi là xấu suốt 20 năm -
 * gọi {@code future.get()} rồi đứng chờ - nay trở thành cách viết ĐÚNG.
 * - Một câu cho cả bài: khi blocking rẻ, bạn không cần {@code CompletableFuture} nữa cho
 * phần lớn ca dùng; nộp tác vụ rồi {@code get()} lần lượt là đủ và dễ đọc hơn nhiều.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/virtual-threads/#task-results">dev.java - Virtual Threads, mục Capturing Task Results</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung dần
 * bên dưới theo các bài tập trong main(), biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CAPTURING TASK RESULTS (mục 1 đến 3)
 * ======================================================================
 * <p>
 * 1. NỘP TÁC VỤ KHÁC NHAU, LẤY KẾT QUẢ TỪNG CÁI
 * - Khung code: {@code submit()} vài tác vụ khác loại việc, giữ lại các {@code Future}, rồi
 * gọi {@code get()} trên từng cái để gộp kết quả.
 * <pre>{@code
 * try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
 *     Future<String> f1 = executor.submit(() -> layThongTinUser(id));
 *     Future<String> f2 = executor.submit(() -> layDonHang(id));
 *     return new KetQua(f1.get(), f2.get());
 * }
 * }</pre>
 * - Điểm cần thấy: hai tác vụ chạy SONG SONG vì đã submit cả hai TRƯỚC khi get cái nào.
 * Thứ tự get không ảnh hưởng tổng thời gian - tổng thời gian là của tác vụ CHẬM NHẤT.
 * - Nếu viết {@code executor.submit(a).get()} rồi mới {@code executor.submit(b).get()} thì
 * thành TUẦN TỰ. Đây là lỗi dễ mắc nhất khi copy nhanh.
 * <p>
 * 2. NHIỀU TÁC VỤ CÙNG LOẠI - INVOKE ALL
 * - Khi có một tập tác vụ CÙNG kiểu kết quả, dùng {@code ExecutorService.invokeAll(tasks)}:
 * nhận {@code Collection<Callable<T>>}, trả về {@code List<Future<T>>} theo đúng thứ tự.
 * <pre>{@code
 * List<Callable<String>> tasks = ids.stream().map(id -> (Callable<String>) () -> goi(id)).toList();
 * try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
 *     for (Future<String> f : executor.invokeAll(tasks)) {
 *         System.out.println(f.get());
 *     }
 * }
 * }</pre>
 * - Đặc tính quan trọng của {@code invokeAll}: nó CHẶN cho tới khi MỌI tác vụ xong (hoặc bị
 * hủy). Khi nó trả về, mọi {@code Future} trong danh sách đều đã hoàn tất - nên
 * {@code get()} sau đó không còn chờ nữa.
 * <p>
 * 3. CÂU CHỐT CỦA MỤC NÀY
 * - Nguyên văn, nên thuộc: "Before virtual threads, you might have felt bad about the
 * blocking get calls. But now blocking is cheap."
 * - Đổi lại trong đầu: trước đây mỗi lần {@code get()} là giam một OS thread vài MB. Giờ nó
 * chỉ unmount một virtual thread khỏi carrier. Cái "tội" đã biến mất, nên thói quen né
 * tránh nó cũng nên bỏ.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 4. BA METHOD CỦA EXECUTORSERVICE - CHỌN ĐÚNG CÁI
 * - {@code submit(task)} - nộp một cái, trả {@code Future} ngay, KHÔNG chặn.
 * - {@code invokeAll(tasks)} - nộp cả tập, CHẶN tới khi tất cả xong. Dùng khi cần ĐỦ kết quả.
 * - {@code invokeAny(tasks)} - CHẶN tới khi cái ĐẦU TIÊN xong, trả về giá trị đó và HỦY phần
 * còn lại. Dùng khi hỏi nhiều nguồn và chỉ cần một câu trả lời (ví dụ ba mirror tải file).
 * - Cả hai method invoke đều có bản nạp chồng nhận timeout. Với {@code invokeAll}, hết
 * timeout thì các tác vụ chưa xong bị HỦY và {@code Future} của chúng ném
 * {@code CancellationException} khi gọi {@code get()}.
 * <p>
 * 5. XỬ LÝ LỖI - CHỖ KHÁC NHAU GIỮA SUBMIT VÀ GỌI TRỰC TIẾP
 * - Nếu tác vụ ném exception, {@code Future.get()} ném {@code ExecutionException} BỌC NGOÀI
 * exception thật. Luôn phải {@code e.getCause()} mới ra nguyên nhân gốc.
 * - Nếu thread đang chờ bị interrupt, {@code get()} ném {@code InterruptedException}. Bắt
 * được thì phải đặt lại cờ: {@code Thread.currentThread().interrupt();} - quy tắc muôn đời
 * của Java, không liên quan virtual thread nhưng rất hay bị quên.
 * - Bẫy im lặng cần nhớ: nếu bạn {@code submit()} rồi KHÔNG BAO GIỜ gọi {@code get()},
 * exception của tác vụ bị NUỐT HOÀN TOÀN - không log, không stack trace, không gì cả. Nó
 * nằm im trong {@code Future} đó. Dùng {@code execute()} thay {@code submit()} nếu không
 * cần kết quả, vì {@code execute()} để exception nổi lên
 * {@code UncaughtExceptionHandler}.
 * - {@code f.get(5, SECONDS)} hết giờ thì ném {@code TimeoutException} nhưng KHÔNG hủy tác
 * vụ. Muốn hủy phải tự gọi {@code f.cancel(true)}.
 * <p>
 * 6. STRUCTURED CONCURRENCY - CHỖ MỤC NÀY ĐANG ĐI TỚI
 * - Vấn đề mà {@code Future} chưa giải được: nếu tác vụ 1 hỏng, tác vụ 2 vẫn chạy tiếp vô
 * ích; và không có gì buộc vòng đời tác vụ con nằm gọn trong phạm vi của method cha.
 * - Structured Concurrency ({@code StructuredTaskScope}, gói {@code java.util.concurrent})
 * giải đúng chỗ đó: mọi tác vụ con BẮT BUỘC kết thúc trước khi khối code thoát; một con
 * hỏng thì các con khác bị hủy ngay; stack trace nối được từ con lên cha.
 * - TRẠNG THÁI PHIÊN BẢN - phải nhớ cho đúng: tính năng này VẪN LÀ PREVIEW ở JDK 25
 * (JEP 505, Structured Concurrency - Fifth Preview). Muốn biên dịch và chạy phải thêm
 * {@code --enable-preview}, và API còn đổi giữa các bản.
 * - Vì repo này biên dịch mặc định không bật preview nên code trong bài chỉ dùng
 * {@code ExecutorService}. Structured Concurrency ghi lại ở đây để biết hướng đi, và để
 * khỏi bị bất ngờ khi đọc bài blog mới.
 * - Quan hệ với bài này cho dễ nhớ: {@code invokeAll} là bản "nghèo" của structured
 * concurrency - nó cũng chờ hết mọi tác vụ, nhưng không hủy lan khi một tác vụ hỏng.
 * <p>
 * 7. SO VỚI TYPESCRIPT - BẢNG ĐỐI CHIẾU NGẮN
 * - {@code Promise.all([p1, p2])} tương ứng {@code invokeAll(tasks)} rồi get hết - NHƯNG
 * khác một chỗ lớn: {@code Promise.all} fail-fast (một promise reject là cả cụm reject
 * ngay), còn {@code invokeAll} vẫn chờ MỌI tác vụ xong rồi mới trả về.
 * - {@code Promise.any} / {@code Promise.race} tương ứng {@code invokeAny}.
 * - {@code Promise.allSettled} mới thật sự giống {@code invokeAll}: chờ hết, rồi mới xét
 * từng cái thành công hay thất bại.
 * - Nhớ cặp này để khỏi dịch sai: allSettled ~ invokeAll, all ~ StructuredTaskScope.
 * <p>
 * 8. KHI NÀO VẪN NÊN DÙNG COMPLETABLEFUTURE
 * - Virtual thread không xóa sổ {@code CompletableFuture}. Nó vẫn hơn ở ba chỗ:
 * + Cần GHÉP NỐI phi tuyến: {@code thenCompose}, {@code thenCombine}, {@code exceptionally}
 * tạo được đồ thị phụ thuộc mà code tuần tự khó diễn đạt.
 * + Cần một API trả {@code CompletableFuture} để người gọi tự quyết chờ hay không.
 * + Đang dùng thư viện đã trả sẵn {@code CompletableFuture} (nhiều HTTP client).
 * - Ngược lại, nếu đồ thị phụ thuộc của bạn chỉ là "chạy N việc rồi gộp", thì bản viết bằng
 * virtual thread + {@code get()} ngắn hơn và debug dễ hơn hẳn. Đó là 90% trường hợp thật.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Vì sao {@code submit(a); submit(b); a.get(); b.get();} là song song còn
 * {@code submit(a).get(); submit(b).get();} là tuần tự?
 * - {@code invokeAll} chặn tới khi nào? Khi nó trả về thì {@code get()} còn chờ không?
 * - Phân biệt {@code submit}, {@code invokeAll}, {@code invokeAny} - mỗi cái một ca dùng.
 * - Tác vụ ném exception thì {@code get()} ném gì? Lấy nguyên nhân gốc bằng cách nào?
 * - Chuyện gì xảy ra với exception nếu bạn submit mà không bao giờ get?
 * - {@code get(5, SECONDS)} hết giờ thì tác vụ có bị hủy không?
 * - Structured Concurrency ở JDK 25 là chính thức hay preview? JEP số mấy?
 * - {@code Promise.all} giống {@code invokeAll} hay giống {@code allSettled}? Vì sao?
 */
public class t4_CapturingTaskResults {

    void main() {
        // TODO 1 (mục 1): Viết hai "dịch vụ" giả, mỗi cái sleep 2 giây rồi trả một chuỗi.
        //   Gọi chúng theo hai cách: (a) submit cả hai rồi get cả hai, (b) submit-get rồi
        //   submit-get. Đo thời gian hai bản. Phải ra ~2 giây và ~4 giây. Dán số vào note.
        //
        // TODO 2 (mục 2): Dùng invokeAll chạy 100 tác vụ, mỗi tác vụ sleep ngẫu nhiên 100-500ms
        //   rồi trả về chỉ số của mình. In kết quả theo thứ tự danh sách trả về. Xác nhận thứ
        //   tự ĐÚNG như lúc nộp, không phải thứ tự hoàn thành.
        //
        // TODO 3 (mục 4): Lặp lại TODO 2 với invokeAny. In giá trị trả về và chạy 5 lần.
        //   Giá trị có khác nhau không? Vì sao?
        //
        // TODO 4 (mục 5): Submit một tác vụ ném IllegalStateException. Gọi get() và in cả
        //   e.getClass() lẫn e.getCause().getClass(). Sau đó làm lại nhưng KHÔNG gọi get() -
        //   xác nhận chương trình im lặng hoàn toàn. Đây là bẫy đáng nhớ nhất bài.
        //
        // TODO 5 (mục 5): Submit một tác vụ sleep 10 giây. Gọi get(1, SECONDS), bắt
        //   TimeoutException, rồi in xem tác vụ còn chạy không (dùng một AtomicBoolean đặt ở
        //   cuối tác vụ). Sau đó thêm cancel(true) và kiểm tra lại.
        //
        // TODO 6 (mục 4): Dùng invokeAll bản có timeout với 10 tác vụ, trong đó 3 tác vụ
        //   chạy lâu hơn timeout. Gọi get() trên cả 10 Future và ghi lại 10 kết quả/exception.
        //
        // TODO 7 (mục 7): Viết lại TODO 1 bằng CompletableFuture.supplyAsync + thenCombine.
        //   Đếm số dòng của hai bản. Rồi ném exception ở một nhánh và so sánh stack trace.
        //   Tự kết luận bản nào bạn muốn đọc lúc 2 giờ sáng khi đang xử lý sự cố.
        //
        // TODO 8 (mục 6): Đọc JEP 505, viết lại TODO 1 bằng StructuredTaskScope và chạy với
        //   --enable-preview. Quan sát điều mà invokeAll KHÔNG làm được: cho một nhánh ném
        //   exception và xác nhận nhánh còn lại bị hủy NGAY, không chạy hết 2 giây.
    }
}
