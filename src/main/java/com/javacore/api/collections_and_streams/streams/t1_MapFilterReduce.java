package com.javacore.api.collections_and_streams.streams;

import java.util.List;

/**
 * Processing Data in Memory Using the Stream API
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 1/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài mở màn: dựng khung tư duy map-filter-reduce, chưa đi sâu API. Các bài sau mổ xẻ từng phần.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/map-filter-reduce/">dev.java - Processing Data in Memory Using the Stream API</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - PROCESSING DATA IN MEMORY USING THE STREAM API (mục 1 đến 11)
 * ======================================================================
 * <p>
 * 1. GIỚI THIỆU STREAM API
 * - (Introducing the Stream API)
 * - Stream API cài đặt thuật toán map-filter-reduce, là "người bạn đồng hành" của
 * Collections Framework. Nguồn dữ liệu không chỉ là collection, có thể là I/O.
 * <p>
 * 2. THUẬT TOÁN MAP-FILTER-REDUCE
 * - (Introducing the Map-Filter-Reduce Algorithm)
 * - Ba bước: lọc theo tiêu chí, ánh xạ sang giá trị khác ({@code Function}), gộp lại thành kết quả.
 * - Trang gốc dùng record {@code Sale} để tính tổng doanh thu.
 * <p>
 * 3. MÔ TẢ KẾT QUẢ THAY VÌ LẬP TRÌNH THUẬT TOÁN
 * - (Specifying a Result Instead of Programming an Algorithm)
 * - Vòng for là mệnh lệnh (imperative): mô tả CÁCH làm. Stream là khai báo (declarative):
 * mô tả CÁI cần, giống SQL, để runtime tự do tối ưu.
 * <p>
 * 4. ÁNH XẠ ĐỐI TƯỢNG SANG ĐỐI TƯỢNG KHÁC
 * - (Mapping Objects to Other Objects or Values)
 * - Quan hệ 1-1, dùng {@code Function}. Giữ nguyên SỐ LƯỢNG phần tử, có thể đổi KIỂU.
 * <p>
 * 5. LỌC BỎ ĐỐI TƯỢNG
 * - (Filtering out Objects)
 * - Dùng {@code Predicate}. Giữ nguyên KIỂU, chỉ giảm SỐ LƯỢNG.
 * <p>
 * 6. GỘP ĐỐI TƯỢNG ĐỂ RA KẾT QUẢ
 * - (Reducing Objects to Produce a Result)
 * - Tương ứng COUNT, SUM, MIN, MAX, AVERAGE trong SQL.
 * - {@code collect()} nhận một {@code Collector} để dựng cấu trúc phức tạp hơn một con số.
 * <p>
 * 7. TỐI ƯU THUẬT TOÁN MAP-FILTER-REDUCE
 * - (Optimizing the Map-Filter-Reduce Algorithm)
 * - Stream KHÔNG tạo collection trung gian, xử lý từng phần tử chạy hết pipeline rồi mới tới phần tử sau.
 * - "A stream is an object that does not store any data."
 * <p>
 * 8. DỰNG PIPELINE BẰNG INTERMEDIATE OPERATION
 * - (Creating a Pipeline with Intermediate Operations)
 * - Intermediate operation trả về một {@code Stream} khác. Gọi bao nhiêu cũng KHÔNG xử lý gì
 * cho tới khi có terminal operation.
 * <p>
 * 9. TÍNH KẾT QUẢ BẰNG TERMINAL OPERATION
 * - (Computing a Result with a Terminal Operation)
 * - Terminal operation không trả về stream; gọi nó mới kích hoạt tiêu thụ dữ liệu.
 * - Gọi thêm thao tác trên cùng một stream instance thì ném {@code IllegalStateException}.
 * <p>
 * 10. TRÁNH BOXING BẰNG STREAM SỐ CHUYÊN DỤNG
 * - (Avoiding Boxing with Specialized Streams of Numbers)
 * - {@code IntStream}, {@code LongStream}, {@code DoubleStream} làm việc trên kiểu nguyên thủy.
 * - Có {@code sum()}, {@code min()}, {@code max()}, {@code average()}, {@code summaryStatistics()}
 * mà {@code Stream} thường không có.
 * <p>
 * 11. THỰC HÀNH TỐT
 * - (Following Good Practices)
 * - Tạo stream rồi tiêu thụ ngay, không cất vào field/biến dùng lại.
 * - Không sửa nguồn dữ liệu trong lúc chạy stream, tránh side effect trong các toán tử.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 12. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code arr.filter().map().reduce()} của JS chạy EAGER và tạo mảng trung gian ở mỗi bước.
 * - Stream của Java chạy LAZY, một phần tử đi hết pipeline rồi mới tới phần tử kế.
 * - Hệ quả: với JS, {@code .filter(...)[0]} vẫn duyệt hết mảng; với Java,
 * {@code .filter(...).findFirst()} dừng ngay khi gặp phần tử đầu khớp.
 * <p>
 * 13. STREAM KHÔNG PHẢI COLLECTION
 * - Stream không lưu dữ liệu, không sửa được nguồn, không tua lại được. Nó là một "kế hoạch tính toán".
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 14. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Ba bước map-filter-reduce mỗi bước đổi KIỂU hay đổi SỐ LƯỢNG phần tử?
 * - Vì sao nói stream không lưu dữ liệu?
 * - Phân biệt intermediate và terminal operation bằng dấu hiệu nào ở chữ ký method?
 * - Chuyện gì xảy ra nếu gọi hai terminal operation trên cùng một stream?
 * - Khi nào nên dùng IntStream thay vì {@code Stream<Integer>}?
 */
public class t1_MapFilterReduce {

    record Sale(String seller, int amount) {
    }

    void main() {
        // Một pipeline tối giản để thấy đủ ba phần: nguồn - intermediate - terminal.
        List<String> words = List.of("alpha", "beta", "gamma", "delta", "epsilon");
        List<Sale> sales = List.of(
                new Sale("an", 100),
                new Sale("binh", 250),
                new Sale("an", 400));

        long count = words.stream()          // nguồn
                .filter(w -> w.length() == 5) // intermediate
                .count();                     // terminal
        IO.println("so tu dai 5 ky tu: " + count);

        // TODO 1: đủ ba bước filter - map - reduce.
        String seller = "an";
        int sumSale = sales.stream()
                .filter(s -> seller.equals(s.seller())) // giữ nguyên KIỂU, giảm SỐ LƯỢNG
                .mapToInt(Sale::amount)                 // giữ nguyên SỐ LƯỢNG, đổi KIỂU
                .sum();                                 // terminal, gộp thành một con số
        IO.println("tong amount cua " + seller + ": " + sumSale);

        // TODO 1 (mục 2): Tạo record Sale(String seller, int amount), dựng một List<Sale>,
        //   rồi tính tổng amount của một seller cụ thể bằng map-filter-reduce.
        //
        // TODO 2 (mục 3): Viết lại TODO 1 bằng vòng for thuần. So sánh số dòng và chỗ nào
        //   mô tả CÁCH làm, chỗ nào mô tả CÁI cần.
        //
        // TODO 3 (mục 7): Thêm print vào trong filter và map, chạy thử rồi ghi lại THỨ TỰ
        //   các dòng in ra. Nó chứng minh điều gì về cách stream xử lý từng phần tử?
        //
        // TODO 4 (mục 9): Gán stream vào một biến, gọi count() rồi gọi tiếp forEach().
        //   Dán nguyên văn exception nhận được vào đây.
        //
        // TODO 5 (mục 10): So sánh words.stream().map(String::length) với
        //   words.stream().mapToInt(String::length). Kiểu trả về khác nhau thế nào,
        //   cái nào gọi được sum() mà không cần collector?
    }
}
