/**
 * Phần 3 - Generics &amp; Collections (bài 17-23): Cấu trúc dữ liệu, cơ chế bộ nhớ và cạm bẫy thực tế.
 *
 * <p>Cây package được phân chia theo từng chủ đề cấu trúc dữ liệu:
 * <ul>
 *   <li>{@code list_set}: {@link com.javacore.practice.javacore33.collections.list_set.ListDemo} (bài 17),
 *       {@link com.javacore.practice.javacore33.collections.list_set.SetDemo} (bài 22).</li>
 *   <li>{@code map}: {@link com.javacore.practice.javacore33.collections.map.WordCounter},
 *       {@link com.javacore.practice.javacore33.collections.map.MapDemo} (bài 18).</li>
 *   <li>{@code queue}: {@link com.javacore.practice.javacore33.collections.queue.HospitalTriageQueue},
 *       {@link com.javacore.practice.javacore33.collections.queue.Patient},
 *       {@link com.javacore.practice.javacore33.collections.queue.Priority},
 *       {@link com.javacore.practice.javacore33.collections.queue.QueueDemo} (bài 20).</li>
 *   <li>{@code deque}: {@link com.javacore.practice.javacore33.collections.deque.BrowserHistory},
 *       {@link com.javacore.practice.javacore33.collections.deque.DequeDemo} (bài 23).</li>
 * </ul>
 *
 * <h2>1. BẢNG TRA CỨU: CHỌN ĐÚNG CẤU TRÚC DỮ LIỆU</h2>
 * <table border="1" cellpadding="5">
 *   <tr>
 *     <th>Nhu cầu bài toán</th>
 *     <th>Cấu trúc khuyên dùng</th>
 *     <th>Độ phức tạp</th>
 *     <th>Bản chất bên dưới</th>
 *   </tr>
 *   <tr>
 *     <td>Danh sách tuần tự, truy cập ngẫu nhiên qua index</td>
 *     <td>{@link java.util.ArrayList}</td>
 *     <td>get: O(1), add cuối: O(1) amotized</td>
 *     <td>Mảng liên tục (contiguous array), thân thiện CPU cache.</td>
 *   </tr>
 *   <tr>
 *     <td>Lọc trùng, kiểm tra tồn tại {@code contains()} cực nhanh</td>
 *     <td>{@link java.util.HashSet}</td>
 *     <td>contains/add: O(1)</td>
 *     <td>Bảng băm (Hash Table), không đảm bảo bất kỳ thứ tự nào.</td>
 *   </tr>
 *   <tr>
 *     <td>Lọc trùng nhưng <em>bắt buộc giữ nguyên thứ tự chèn</em> (UI/JSON)</td>
 *     <td>{@link java.util.LinkedHashSet}</td>
 *     <td>contains/add: O(1)</td>
 *     <td>Bảng băm kết hợp danh sách liên kết kép theo thứ tự chèn (giống Set của JS/TS).</td>
 *   </tr>
 *   <tr>
 *     <td>Tra cứu cặp Key-Value, nhóm dữ liệu, khử vòng lặp O(n*m)</td>
 *     <td>{@link java.util.HashMap}</td>
 *     <td>get/put: O(1)</td>
 *     <td>Bảng băm (mảng buckets), chuyển sang Red-Black Tree khi va chạm &gt; 8 node.</td>
 *   </tr>
 *   <tr>
 *     <td>Xử lý theo mức độ ưu tiên (việc gấp làm trước)</td>
 *     <td>{@link java.util.PriorityQueue}</td>
 *     <td>peek: O(1), poll/offer: O(log n)</td>
 *     <td>Binary Min-Heap (không có tính ổn định, cần tie-breaker).</td>
 *   </tr>
 *   <tr>
 *     <td>Ngăn xếp (LIFO), hàng đợi 2 đầu, lịch sử điều hướng, Undo/Redo</td>
 *     <td>{@link java.util.ArrayDeque}</td>
 *     <td>push/pop/peek: O(1)</td>
 *     <td>Mảng tròn co giãn (Circular Array), không synchronized, nhanh hơn Stack cũ.</td>
 *   </tr>
 * </table>
 *
 * <h2>2. CƠ CHẾ BÊN DƯỚI (UNDER THE HOOD) CẦN NẮM VỮNG</h2>
 * <ul>
 *   <li><b>HashMap hoạt động thế nào:</b>
 *       Mảng các thùng {@code Node<K,V>[] table}. Khi {@code put(k, v)}, vị trí thùng được tính bằng
 *       {@code index = (n - 1) & hash}. Nếu nhiều key rơi vào cùng một thùng (hash collision),
 *       Java dùng Singly Linked List nối các node lại. Từ Java 8, khi một thùng có từ 8 node trở lên
 *       (và dung lượng bảng &gt;= 64), linked list tự động chuyển hóa thành cây đỏ-đen (Red-Black Tree),
 *       giúp tìm kiếm giảm từ O(n) xuống O(log n).</li>
 *   <li><b>Tại sao ArrayList luôn thắng LinkedList:</b>
 *       Lý thuyết nói LinkedList chèn giữa O(1), nhưng thực tế để tìm đến vị trí chèn đã tốn O(n).
 *       Quan trọng hơn là kiến trúc phần cứng: {@code ArrayList} nằm liên tiếp trong bộ nhớ nên
 *       tận dụng tối đa <em>CPU Cache Locality (L1/L2/L3 cache)</em> và lệnh copy mảng ở mức phần cứng.
 *       Trong khi {@code LinkedList} cấp phát từng Node rời rạc khắp Heap, tốn thêm 24-32 bytes overhead
 *       mỗi node và liên tục gây CPU Cache Miss.</li>
 * </ul>
 *
 * <h2>3. NĂM CẠM BẪY CHẾT NGƯỜI TRONG PRODUCTION</h2>
 * <ol>
 *   <li><b>Thảm họa Mutable Key/Element trong Set và Map:</b>
 *       Khi bỏ một object vào {@code HashSet} hoặc làm Key của {@code HashMap}, đối tượng đó được đặt vào
 *       thùng tương ứng với {@code hashCode()} tại thời điểm đó. Nếu bạn gọi setter sửa trường dùng trong
 *       hashCode: mã băm đổi sang giá trị mới -&gt; khi gọi {@code get()} hoặc {@code contains()}, Java tìm
 *       ở thùng mới và trả về null/false -&gt; đối tượng vĩnh viễn "tàng hình" trong Map/Set, không xóa
 *       được và gây rò rỉ bộ nhớ (Memory Leak). <em>Quy tắc: Key của Map và phần tử của Set PHẢI BẤT BIẾN!</em></li>
 *   <li><b>TreeSet âm thầm nuốt dữ liệu vì lờ đi {@code equals()}:</b>
 *       {@code TreeSet} không dùng {@code equals()} mà xác định trùng bằng {@code compareTo() == 0}.
 *       Nếu comparator viết thiếu trường (ví dụ 2 người khác nhau nhưng cùng điểm số), {@code TreeSet}
 *       sẽ kết luận là trùng nhau và vứt bỏ người thứ hai mà không hề báo lỗi.</li>
 *   <li><b>PriorityQueue không có tính ổn định (Unstable Heap):</b>
 *       Nếu hai phần tử có cùng độ ưu tiên, binary heap không đảm bảo phần tử đến trước sẽ ra trước.
 *       Trong các bài toán thực tế (hàng đợi bệnh viện, máy in), bắt buộc phải có một trường phụ
 *       {@code arrivalOrder} (số thứ tự đến) làm tie-breaker trong Comparator.</li>
 *   <li><b>Bẫy duyệt {@code iterator()} / {@code for-each} trên PriorityQueue:</b>
 *       Duyệt bằng {@code for-each} in ra các phần tử theo thứ tự mảng heap nội bộ (lộn xộn),
 *       chứ không theo thứ tự ưu tiên. Muốn lấy theo thứ tự ưu tiên phải rút bằng {@code poll()}.</li>
 *   <li><b>Xóa phần tử khi đang duyệt bằng for-each:</b>
 *       Gọi {@code list.remove(x)} trong vòng lặp for-each sẽ ném {@link java.util.ConcurrentModificationException}.
 *       Luôn dùng {@link java.util.Collection#removeIf(java.util.function.Predicate)}.</li>
 * </ol>
 *
 * <h2>4. CÁC CLASS LỖI THỜI (ANTI-PATTERNS) CẦN TRÁNH</h2>
 * <ul>
 *   <li>❌ <b>{@code java.util.Vector} và {@code java.util.Stack}:</b> Từ Java 1.0 (1996), mọi method bị
 *       đồng bộ hóa ({@code synchronized}) gây chậm vô ích. Thay bằng {@link java.util.ArrayList}
 *       và {@link java.util.ArrayDeque}.</li>
 *   <li>❌ <b>{@code java.util.Hashtable}:</b> Thay bằng {@link java.util.HashMap} (đơn luồng)
 *       hoặc {@link java.util.concurrent.ConcurrentHashMap} (đa luồng).</li>
 *   <li>❌ <b>{@code java.util.LinkedList}:</b> Gần như không có trường hợp sử dụng trong backend hiện đại.
 *       Cần danh sách thông thường dùng {@link java.util.ArrayList}; cần queue/stack hai đầu dùng
 *       {@link java.util.ArrayDeque}.</li>
 * </ul>
 */
package com.javacore.practice.javacore33.collections;
