package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Creating Your Own Collector
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 8/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Bài 7 dùng collector có sẵn. Bài này tự ghép collector bằng {@code Collector.of()}
 * và các collector tổ hợp. Bài 9 mới đi tới mức cài đặt thẳng interface.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/custom-collectors/">dev.java - Creating Your Own Collector</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - CREATING YOUR OWN COLLECTOR (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. MỘT COLLECTOR GỒM NHỮNG GÌ
 * - (Understanding How a Collector Works)
 * - Bốn thành phần:
 * supplier ({@code Supplier}) tạo container;
 * accumulator ({@code BiConsumer}) nhét phần tử vào container;
 * combiner ({@code BinaryOperator}) gộp hai container khi chạy SONG SONG;
 * finisher ({@code Function}, tùy chọn) biến container thành kết quả cuối.
 * - Dựng bằng các factory {@code Collector.of(...)}.
 * <p>
 * 2. GOM KIỂU NGUYÊN THỦY VÀO COLLECTION
 * - (Collecting Primitive Types in a Collection)
 * - {@code IntStream} có {@code collect(Supplier, ObjIntConsumer, BiConsumer)} -
 * ba tham số rời chứ không nhận Collector.
 * <p>
 * 3. GOM KIỂU NGUYÊN THỦY THÀNH CHUỖI
 * - (Collecting Primitive Types in a String)
 * - Không dồn thẳng vào {@code String} vì String bất biến, mỗi lần nối là tạo object mới.
 * Dùng {@code StringBuffer} làm container rồi mới chuyển thành String.
 * <p>
 * 4. FINISHER ĐỂ HẬU XỬ LÝ
 * - (Using a Finisher to Post-Process a Collector)
 * - "A finisher is an instance of {@code Function} that takes the container in which the
 * elements were accumulated and transforms it to something else."
 * - {@code Collectors.collectingAndThen(downstream, finisher)}: mẫu kinh điển để gom xong
 * rồi bọc thành bất biến, ví dụ {@code collectingAndThen(toList(), List::copyOf)}.
 * <p>
 * 5. TEEING - GỘP KẾT QUẢ HAI COLLECTOR
 * - (Combining the Results of Two Collectors with the Teeing Collector)
 * - {@code Collectors.teeing(c1, c2, merger)} (Java SE 12): chạy hai collector trên CÙNG MỘT
 * lượt duyệt rồi gộp bằng một {@code BiFunction}.
 * - Dùng khi cần hai số liệu từ một stream mà không muốn duyệt hai lần (stream chỉ dùng một lần).
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. COMBINER CÓ THỂ KHÔNG BAO GIỜ ĐƯỢC GỌI
 * - Chạy tuần tự thì combiner không chạy, nên combiner viết sai vẫn qua mặt được mọi test
 * cho tới ngày ai đó thêm {@code parallel()}. Viết xong phải test song song.
 * <p>
 * 7. COLLECTINGANDTHEN LÀ CÁCH NGẮN NHẤT ĐỂ RA KẾT QUẢ BẤT BIẾN
 * - Mẫu hay gặp trong service layer: {@code groupingBy(f, collectingAndThen(toList(), List::copyOf))}
 * để Map trả ra ngoài không bị caller sửa.
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - JS không có khái niệm tương đương; muốn "teeing" phải duyệt mảng hai lần hoặc tự viết reduce
 * trả về object hai trường. Ở Java việc tách supplier/accumulator/combiner tồn tại chính là
 * để chạy song song được.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Bốn thành phần của một collector, mỗi cái làm gì?
 * - Combiner được gọi lúc nào? Không chạy song song thì nó có chạy không?
 * - Vì sao gom chuỗi phải dùng StringBuffer chứ không phải String?
 * - collectingAndThen giải quyết bài toán gì?
 * - teeing hơn gì so với việc gọi collect hai lần?
 */
public class t8_CustomCollectors {

    void main() {
        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

        // teeing: lấy min và max trong MỘT lượt duyệt.
        String minMax = numbers.stream().collect(
                Collectors.teeing(
                        Collectors.minBy(Integer::compare),
                        Collectors.maxBy(Integer::compare),
                        (min, max) -> min.orElseThrow() + ".." + max.orElseThrow()));
        IO.println("khoang: " + minMax);

        // collectingAndThen: gom xong thì khóa lại, caller không sửa được.
        List<Integer> immutable = numbers.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
        IO.println(immutable);

        // TODO 1 (mục 1): Viết lại Collectors.toList() bằng Collector.of(ArrayList::new,
        //   List::add, (a, b) -> { a.addAll(b); return a; }). Chạy thử trên stream thường.
        //
        // TODO 2 (mục 6): Chạy lại collector ở TODO 1 trên parallelStream với list lớn.
        //   Sau đó cố tình viết combiner sai (ví dụ chỉ return a) và ghi lại kết quả sai thế nào.
        //
        // TODO 3 (mục 2): Dùng IntStream.collect(ArrayList::new, List::add, List::addAll)
        //   để gom IntStream thành List<Integer>.
        //
        // TODO 4 (mục 3): Viết collector nối các số thành chuỗi "1-2-3" dùng StringBuffer làm container.
        //
        // TODO 5 (mục 5): Dùng teeing để tính trung bình cộng = tổng / số lượng trong một lượt duyệt.
        //
        // TODO 6 (mục 7): Thử add() vào list trả về bởi collectingAndThen ở trên.
        //   Dán exception nhận được vào đây.
    }
}
