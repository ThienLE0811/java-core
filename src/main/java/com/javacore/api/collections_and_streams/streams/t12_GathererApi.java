package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.stream.Gatherers;

/**
 * The Gatherer API
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 12/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài dài nhất section. Gatherer là với INTERMEDIATE operation cái mà Collector là với
 * TERMINAL operation: cho phép tự viết toán tử trung gian.
 * - Cần JDK 24 trở lên (dự án đang để source/target 25 nên dùng thẳng được, không cần preview).
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/gatherers/">dev.java - The Gatherer API</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - THE GATHERER API (mục 1 đến 12)
 * ======================================================================
 * <p>
 * 1. GIỚI THIỆU GATHERER
 * - (Introducing Gatherers / Organization of the Gatherer API / When to Use Gatherers?)
 * - Từ JDK 24, {@code Gatherer} mô hình hóa intermediate operation, giống cách
 * {@code Collector} mô hình hóa terminal operation.
 * - "The Gatherer API brings simpler patterns than the Spliterator API, with excellent
 * support for parallelism." Dùng qua {@code Stream.gather(gatherer)}.
 * - DÙNG KHI: cần toán tử Stream API không có sẵn (gom theo cụm cố định, distinct tùy biến,
 * gộp map và filter vào một bước). KHÔNG dùng để thay {@code map()} hay {@code filter()} thường.
 * <p>
 * 2. INTEGRATOR - ĐẨY PHẦN TỬ XUỐNG DOWNSTREAM
 * - (Integrating Elements in a Downstream)
 * - {@code Integrator} nhận phần tử từ upstream và đẩy kết quả bằng {@code downstream.push()}.
 * - Giá trị boolean trả về nghĩa là "còn muốn nhận thêm phần tử nữa không".
 * <p>
 * 3. VIẾT MAP, FILTER, FLATMAP BẰNG GATHERER
 * - (Using an Integrator to Map / to Filter / to Flat-Map a Stream)
 * - {@code Gatherer.of(integrator)} dựng gatherer không trạng thái.
 * - Map: push một phần tử đã biến đổi. Filter: chỉ push khi hợp predicate.
 * Flat-map: push nhiều phần tử cho một đầu vào.
 * <p>
 * 4. GỘP NHIỀU PHÉP VÀO MỘT GATHERER
 * - (Fusing Mappings, Filterings and Flat-Mappings / Writing a Correct Flat-Mapper)
 * - Gộp giúp bớt object Stream trung gian, đổi lại khó đọc hơn chuỗi method thường.
 * - Flat-mapper viết ĐÚNG phải: gọi {@code .sequential()} trên stream con, dùng
 * try-with-resources để đóng, và xử lý trường hợp hàm trả về null.
 * <p>
 * 5. TRẠNG THÁI KHẢ BIẾN NỘI BỘ
 * - (Managing an Internal Mutable State)
 * - Gatherer có trạng thái cần một {@code Supplier} initializer tạo object state,
 * integrator nhận state đó và sửa nó.
 * - {@code Gatherer.ofSequential()} dành cho gatherer chỉ chạy tuần tự được.
 * - Ví dụ trong trang: tự cài {@code dropWhile()} (dùng một Box boolean) và
 * {@code distinct()} (dùng {@code HashSet.add()} trả về true/false).
 * <p>
 * 6. NGẮT STREAM GIỮA CHỪNG
 * - (Interrupting a Stream / Limiting the Number of Elements / Fixing the Distinct Gatherer /
 * Pushing to a Rejecting Downstream)
 * - Dừng sớm qua giá trị trả về của {@code downstream.push()} hoặc kiểm tra
 * {@code downstream.isRejecting()}.
 * - Tự cài {@code limit()}: đếm trong state, trả thẳng kết quả của {@code push()}.
 * - Luật: khi có push thì TRẢ VỀ kết quả của push; khi bỏ qua phần tử thì trả về true.
 * Tín hiệu từ chối chỉ đi một chiều từ dưới lên.
 * - Đẩy vào downstream đang từ chối thì phần tử bị bỏ im lặng, không exception, chỉ tốn công.
 * <p>
 * 7. GREEDY INTEGRATOR
 * - (Creating Greedy Integrators)
 * - {@code Integrator.ofGreedy()} khai báo integrator không bao giờ TỰ ngắt stream,
 * chỉ truyền lại tín hiệu từ chối của downstream. Khai đúng thì API tối ưu được thêm.
 * <p>
 * 8. FINISHER
 * - (Adding a Finisher / Creating a Finisher with no State /
 * Creating a Sorting Distinct Gatherer / Fixing the Flat-Mapper / Finisher and Downstream Rejecting State)
 * - Finisher là một {@code BiConsumer} chạy SAU khi upstream hết phần tử, gọi đúng một lần.
 * Dùng cho thao tác phải đệm xong mới đẩy được, ví dụ sắp xếp.
 * - Finisher không trạng thái thì dùng tham số kiểu {@code Void}.
 * - Ví dụ: gatherer sort-distinct đệm vào {@code TreeSet} rồi đẩy ra trong finisher,
 * dùng {@code takeWhile()} để tôn trọng trạng thái từ chối.
 * - Finisher NÊN kiểm tra {@code downstream.isRejecting()} để khỏi tính toán vô ích;
 * integrator thì không cần.
 * <p>
 * 9. GATHERER TRONG STREAM SONG SONG
 * - (Executing Gatherers in Parallel Streams / Creating Parallel Gatherers)
 * - {@code Gatherer.ofSequential()} chạy đơn luồng NGAY TRONG parallel stream: phần upstream
 * vẫn song song, tới gatherer thì tuần tự. Đây là điểm Gatherer khác mọi toán tử Stream khác.
 * - Gatherer song song: {@code Gatherer.of(...)} cho loại không trạng thái, hoặc
 * {@code Gatherer.of(initializer, integrator, combiner, finisher)} cho loại có trạng thái,
 * với {@code BinaryOperator} combiner gộp các state.
 * <p>
 * 10. NỐI CÁC GATHERER
 * - (Chaining Gatherers)
 * - {@code andThen()} ghép hai gatherer thành một, kiểu đầu ra của cái trước phải khớp
 * kiểu đầu vào của cái sau.
 * <p>
 * 11. LỚP FACTORY GATHERERS
 * - (Creating Gatherers with the Gatherers Factory Class)
 * - Làm sẵn: {@code fold()}, {@code scan()}, {@code mapConcurrent()}, và hai loại windowing.
 * <p>
 * 12. FOLD, SCAN, MAPCONCURRENT, WINDOW
 * - (Folding / Scanning / Mapping Concurrently / Windowing Gatherers / Fixed Window / Sliding Window)
 * - {@code Gatherers.fold(initializer, operator)}: gộp từ trái sang phải, KHÔNG đòi tính kết hợp
 * và không cần phần tử đơn vị như {@code reduce()} (so lại bài 4).
 * - {@code Gatherers.scan(initializer, operator)}: giống fold nhưng đẩy kết quả TRUNG GIAN
 * ở từng bước thay vì một giá trị cuối.
 * - {@code Gatherers.mapConcurrent(maxConcurrency, mapper)}: map trên virtual thread có giới hạn,
 * bản thân nó là gatherer tuần tự.
 * - {@code Gatherers.windowFixed(n)}: các cụm RỜI NHAU, cụm cuối có thể ngắn hơn.
 * - {@code Gatherers.windowSliding(n)}: các cụm GỐI NHAU, dịch một phần tử mỗi lần.
 * - Cả hai trả về {@code List} không sửa được.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 13. BA THỨ ĐÃ TỪNG THIẾU, GIỜ CÓ
 * - Trước JDK 24, muốn chia list thành cụm n phần tử phải dùng {@code IntStream.range()} cộng
 * {@code subList()}; giờ là {@code gather(Gatherers.windowFixed(n))}.
 * - {@code mapConcurrent()} là chỗ Stream API gặp virtual thread (JDK 21): map I/O song song
 * mà không đụng tới commonPool của parallel stream (so lại bài 11 mục 9).
 * <p>
 * 14. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code scan()} chính là {@code scan} của RxJS; {@code fold()} là {@code reduce}.
 * Gatherer API gần với tư duy toán tử của reactive stream hơn là với mảng JS.
 * - {@code mapConcurrent(n, f)} gần với {@code p-limit} / {@code Promise.all} có giới hạn đồng thời.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 15. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Gatherer đứng ở vị trí nào so với Collector trong một pipeline?
 * - Bốn thành phần của một gatherer là gì, cái nào bắt buộc?
 * - Giá trị boolean mà integrator trả về mang nghĩa gì? Quy tắc trả về đúng là gì?
 * - fold khác reduce ở ràng buộc nào? scan khác fold ở chỗ nào?
 * - windowFixed và windowSliding cho ra bao nhiêu cụm trên list 10 phần tử với n = 3?
 * - Vì sao ofSequential vẫn chạy được bên trong parallel stream?
 */
public class t12_GathererApi {

    void main() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7);

        // windowFixed: các cụm rời nhau, cụm cuối ngắn hơn nếu không chia hết.
        IO.println(numbers.stream().gather(Gatherers.windowFixed(3)).toList());

        // windowSliding: các cụm gối nhau, dịch một phần tử mỗi lần.
        IO.println(numbers.stream().gather(Gatherers.windowSliding(3)).toList());

        // scan: đẩy ra kết quả cộng dồn ở từng bước.
        IO.println(numbers.stream().gather(Gatherers.scan(() -> 0, Integer::sum)).toList());

        // TODO 1 (mục 12): Dùng Gatherers.fold để nối các số thành chuỗi "1-2-3...".
        //   So sánh với reduce ở bài 4: fold không đòi hỏi điều gì mà reduce đòi?
        //
        // TODO 2 (mục 3): Tự viết một gatherer map bằng Gatherer.of(Integrator.of(...)),
        //   chạy thử và so với .map() thường.
        //
        // TODO 3 (mục 5): Tự cài distinct() bằng Gatherer.ofSequential với state là HashSet.
        //
        // TODO 4 (mục 6): Tự cài limit(n) bằng gatherer có state đếm, trả về đúng kết quả
        //   của downstream.push(). Kiểm tra nó có short-circuit trên Stream.iterate vô hạn không.
        //
        // TODO 5 (mục 8): Viết gatherer sort-distinct dùng TreeSet đệm trong state và
        //   đẩy ra trong finisher.
        //
        // TODO 6 (mục 10): Nối gatherer ở TODO 2 và TODO 3 bằng andThen().
        //
        // TODO 7 (mục 12): Dùng Gatherers.mapConcurrent(4, ...) với một mapper có Thread.sleep,
        //   đo thời gian so với map() thường trên 12 phần tử.
        //
        // TODO 8 (mục 1): Thử viết lại Stream.of(...).filter(...) bằng gatherer rồi tự đánh giá:
        //   trường hợp này gatherer có đáng dùng không, vì sao trang gốc khuyên không nên.
    }
}
