package com.javacore.api.collections_and_streams.streams;

import java.util.List;
import java.util.Optional;

/**
 * Reducing a Stream
 * <p>
 * 0. Vị trí bài này
 * - Bước 20/41, bài 4/12 trong section The Stream API của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/streams/reducing/">dev.java - Reducing a Stream</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - REDUCING A STREAM (mục 1 đến 5)
 * ======================================================================
 * <p>
 * 1. GỌI TERMINAL OPERATION
 * - (Calling a Terminal Operation on a Stream)
 * - Hai luật xương sống: stream không có terminal operation thì KHÔNG xử lý gì;
 * một stream instance chỉ dùng được MỘT lần, gọi thêm là {@code IllegalStateException}.
 * <p>
 * 2. REDUCE VỚI BINARY OPERATOR
 * - (Using a Binary Operator to Reduce a Stream)
 * - {@code reduce(BinaryOperator)} gộp dần từng cặp: gộp hai phần tử đầu, lấy kết quả gộp
 * tiếp với phần tử sau, cứ thế.
 * <p>
 * 3. CHỌN BINARY OPERATOR DÙNG ĐƯỢC SONG SONG
 * - (Choosing a Binary Operator That Can Be Used in Parallel)
 * - Toán tử phải có tính KẾT HỢP (associative): {@code (a op b) op c == a op (b op c)}.
 * - Không kết hợp thì KHÔNG có lỗi biên dịch, không có exception, chỉ có kết quả sai
 * khác nhau giữa các lần chạy. Đây là loại bug khó chịu nhất ở phần này.
 * <p>
 * 4. TOÁN TỬ KHÔNG CÓ PHẦN TỬ ĐƠN VỊ
 * - (Managing Binary Operators That Do Have Any Identity Element)
 * - MIN, MAX không có phần tử đơn vị nên {@code reduce(BinaryOperator)} trả về {@code Optional}.
 * - Stream rỗng thì được {@code Optional.empty()}; tự quyết cách xử lý bằng
 * {@code orElseThrow()}, {@code orElse()}... (bài 10 đi sâu Optional).
 * <p>
 * 5. BA OVERLOAD CỦA REDUCE
 * - (Exploring the Reduction Methods of the Stream API)
 * - {@code reduce(T identity, BinaryOperator<T>)}: có đơn vị, trả về T.
 * - {@code reduce(BinaryOperator<T>)}: không đơn vị, trả về {@code Optional<T>}.
 * - {@code reduce(U identity, BiFunction<U,T,U> accumulator, BinaryOperator<U> combiner)}:
 * gộp luôn bước map vào bước reduce, combiner dùng khi chạy song song.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 6. PHẦN TỬ ĐƠN VỊ PHẢI THẬT SỰ LÀ ĐƠN VỊ
 * - {@code identity} phải thỏa {@code op(identity, x) == x} với mọi x.
 * - Truyền 1 làm identity cho phép cộng, hay truyền 0 cho phép nhân, là sai âm thầm;
 * chạy tuần tự có khi vẫn "gần đúng", chạy song song thì sai hẳn vì identity được dùng
 * nhiều lần, mỗi sub-stream một lần.
 * <p>
 * 7. REDUCE SO VỚI COLLECT
 * - {@code reduce} dành cho gộp BẤT BIẾN (immutable): mỗi bước tạo giá trị mới.
 * - {@code collect} dành cho gộp vào container KHẢ BIẾN (mutable). Dùng reduce để nối chuỗi
 * hay dồn vào List là sai về hiệu năng, bài 7 và 8 chỉ cách đúng.
 * <p>
 * 8. ĐỐI CHIẾU VỚI TYPESCRIPT
 * - {@code Array.prototype.reduce(fn, initial)} gần với overload hai tham số, nhưng JS cho
 * accumulator khác kiểu phần tử ngay trong một callback.
 * - Java tách thành accumulator và combiner chính vì phải chạy song song được.
 * - JS reduce trên mảng rỗng mà không có initial thì ném TypeError; Java trả về Optional.empty().
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Tính kết hợp là gì, vì sao reduce bắt buộc cần nó?
 * - Vì sao reduce một tham số trả về Optional còn hai tham số thì không?
 * - Combiner trong overload ba tham số được gọi lúc nào?
 * - Cho một ví dụ toán tử KHÔNG kết hợp và chỉ ra kết quả sai nó gây ra.
 * - Khi nào nên dùng collect thay vì reduce?
 */
public class t4_ReducingAStream {

    void main() {
        List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

        // Có identity: luôn trả về giá trị, stream rỗng thì trả về chính identity.
        int sum = numbers.stream().reduce(0, Integer::sum);
        IO.println("tong: " + sum);

        // Không identity: trả về Optional vì stream có thể rỗng.
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        IO.println("max : " + max.orElseThrow());

        // TODO 1 (mục 2): Thêm print vào trong BinaryOperator để xem từng cặp được gộp.
        //   Ghi lại thứ tự gộp khi chạy tuần tự.
        //
        // TODO 2 (mục 3): Dùng phép trừ (a, b) -> a - b làm reduce trên cùng một list,
        //   chạy stream() rồi parallelStream(). Ghi lại hai kết quả và giải thích.
        //
        // TODO 3 (mục 4): Gọi reduce(Integer::max) trên một List rỗng. Kết quả là gì,
        //   khác gì so với reduce(0, Integer::max)?
        //
        // TODO 4 (mục 5): Dùng overload ba tham số để tính tổng ĐỘ DÀI của một List<String>
        //   mà không gọi map trước đó.
        //
        // TODO 5 (mục 6): Truyền identity sai (ví dụ 1 cho phép cộng) rồi chạy song song.
        //   Kết quả lệch bao nhiêu, vì sao lệch đúng số đó?
    }
}
