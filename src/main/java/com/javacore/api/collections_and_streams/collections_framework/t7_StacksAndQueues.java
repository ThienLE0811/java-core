package com.javacore.api.collections_and_streams.collections_framework;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Storing Elements in Stacks and Queues
 * <p>
 * 0. Vị trí bài này
 * - Bước 19/41, bài 7/12 trong section The Collections Framework của lộ trình dev.java/learn.
 * - Một câu cho cả bài: (điền sau khi đọc xong trang gốc)
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/api/collections-and-streams/collections-framework/stacks-queues/">dev.java - Storing Elements in Stacks and Queues</a>
 * - Các đoạn trong ngoặc kép là trích nguyên văn từ trang gốc. Code thực hành bổ sung
 * dần bên dưới theo các TODO, biên dịch trên JDK 25.
 * <p>
 * ======================================================================
 * TRANG GỐC - STORING ELEMENTS IN STACKS AND QUEUES (mục 1 đến 7)
 * ======================================================================
 * <p>
 * 1. ĐỊNH VỊ TRONG CÂY QUEUE
 * - (Finding Your Way in the Queue Hierarchy)
 * - {@code Queue} có từ Java SE 5, {@code Deque} có từ Java SE 6. Cả hai mở rộng Collection.
 * <p>
 * 2. PUSH, POP VÀ PEEK
 * - (Pushing, Popping and Peeking)
 * - Ba thao tác nền của FIFO và LIFO: thêm vào, lấy ra và xóa, xem mà không xóa.
 * <p>
 * 3. MÔ HÌNH HÓA QUEUE VÀ STACK
 * - (Modeling Queues and Stacks)
 * - Hai trường hợp biên: hàng đầy và hàng rỗng.
 * - Mỗi thao tác có HAI bản: một bản ném exception, một bản trả về giá trị đặc biệt
 * (null hoặc false). Đây là ý chính của cả bài.
 * <p>
 * 4. QUEUE - HÀNG ĐỢI FIFO
 * - (Modeling FIFO Queues with Queue)
 * - Thêm: {@code add()} ném exception, {@code offer()} trả về false.
 * - Lấy và xóa: {@code remove()} ném exception, {@code poll()} trả về null.
 * - Xem: {@code element()} ném exception, {@code peek()} trả về null.
 * <p>
 * 5. DEQUE - DÙNG ĐƯỢC CẢ LIFO VÀ FIFO
 * - (Modeling LIFO Stacks and FIFO Queues with Deque)
 * - Bản hai đầu của mọi thao tác: {@code addFirst()}/{@code addLast()},
 * {@code removeFirst()}/{@code removeLast()}, {@code pollFirst()}/{@code pollLast()},
 * {@code peekFirst()}/{@code peekLast()}, cùng {@code push()}/{@code pop()}.
 * <p>
 * 6. CÁC CÀI ĐẶT
 * - (Implementing Queue and Deque)
 * - {@code ArrayDeque}, {@code LinkedList}, {@code PriorityQueue} - đặc tính từng loại.
 * - PriorityQueue giữ phần tử NHỎ NHẤT ở đầu, theo Comparable hoặc Comparator.
 * <p>
 * 7. TRÁNH CLASS STACK
 * - (Staying Away from the Stack Class)
 * - Stack kế thừa Vector nên bị đồng bộ hóa thừa; dùng ArrayDeque thay thế.
 * - Lưu ý thứ tự duyệt của Stack ngược với Deque - một nguồn lỗi kinh điển.
 * <p>
 * ======================================================================
 * ĐIỂM NGOÀI TRANG GỐC - ĐÁNG GHI LẠI
 * ======================================================================
 * <p>
 * 8. ArrayDeque KHÔNG NHẬN NULL
 * - Vì null được dùng làm tín hiệu "hàng rỗng" cho poll/peek, nên ArrayDeque cấm null.
 * - PriorityQueue duyệt bằng for-each KHÔNG ra thứ tự ưu tiên; chỉ poll mới đúng thứ tự.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 9. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - add khác offer ở đâu? remove khác poll ở đâu? element khác peek ở đâu?
 * - Khi nào nên chọn bản ném exception, khi nào chọn bản trả giá trị đặc biệt?
 * - Vì sao ArrayDeque cấm null?
 * - push() của Deque thêm vào đầu hay cuối?
 * - In một PriorityQueue bằng System.out.println có ra thứ tự ưu tiên không? Vì sao?
 */
public class t7_StacksAndQueues {

    void main() {
        // TODO 1 (mục 4): Với một Queue rỗng, gọi lần lượt remove(), poll(), element(), peek().
        //   Ghi lại cái nào ném exception, cái nào trả null.
        //
        // TODO 2 (mục 5): Dùng ArrayDeque làm stack (push/pop) rồi làm queue (offer/poll).
        //   In sau mỗi thao tác để thấy đầu nào được dùng.
        //
        // Cùng một kiểu ArrayDeque, khác nhau ở chỗ BỎ VÀO đầu nào.
        Deque<String> stack = new ArrayDeque<>();
        stack.push("1");
        stack.push("2");
        stack.push("3");
        IO.println("stack : " + stack);
        IO.println("pop   : " + stack.pop());

        Deque<String> queue = new ArrayDeque<>();
        queue.offer("1");
        queue.offer("2");
        queue.offer("3");
        IO.println("queue : " + queue);
        IO.println("poll  : " + queue.poll());
        //
        // TODO 3 (mục 6): Tạo PriorityQueue<String> theo độ dài chuỗi. In trực tiếp queue,
        //   rồi poll hết ra. So sánh hai thứ tự và giải thích.
        //
        // TODO 4 (mục 7): Đẩy 1,2,3 vào java.util.Stack và vào ArrayDeque, rồi duyệt for-each
        //   cả hai. Ghi lại khác biệt thứ tự.
        //
        // TODO 5 (mục 8): Thử offer(null) vào ArrayDeque và vào LinkedList. Ghi lại kết quả.
    }
}
