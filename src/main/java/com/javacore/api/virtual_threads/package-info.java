/**
 * Virtual Threads
 *
 * <p>Bước 29/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/virtual-threads/">https://dev.java/learn/api/virtual-threads/</a>
 *
 * <p><b>Lưu ý về cấu trúc:</b> khác mọi section trước, dev.java gói cả section này vào MỘT
 * trang duy nhất, chia làm 8 mục. Repo tách thành 8 file {@code tN_*.java} theo đúng 8 mục
 * đó, giữ nguyên thứ tự và tên mục gốc. Tác giả trang: Cay Horstmann.
 *
 * <p><b>Vì sao có mặt trong nhóm "Mastering the API"</b> - virtual thread không thêm cú pháp
 * mới nào. Nó chỉ làm cho một thứ cũ rẻ đi: CHẶN (blocking). Cả section xoay quanh một câu:
 * khi blocking rẻ, mô hình lập trình đồng bộ quay lại đúng, và chuỗi callback trở nên thừa.
 *
 * <p><b>Nền tảng cần nắm TRƯỚC khi vào section</b> - trang gốc coi như bạn đã biết:
 * <ul>
 *   <li>{@code Thread}, {@code Runnable}, {@code Callable}, {@code Future}</li>
 *   <li>{@code synchronized}, {@code volatile}, race condition, deadlock</li>
 *   <li>{@code ExecutorService} và {@code CompletableFuture}</li>
 *   <li>Concurrent collections và các lớp {@code Atomic}</li>
 * </ul>
 * Thiếu phần nào thì bù trước, vì section này KHÔNG dạy lại.
 *
 * <p><b>Tám bài, mỗi bài một file:</b>
 *
 * <p><b>Nhóm 1 - Vì sao và tạo thế nào</b>
 * <ol>
 *   <li>{@code t1_WhyVirtualThreads} - bài toán thread-per-request, giá của platform thread,
 *   vì sao callback không phải lời giải tốt, cơ chế mount/unmount. Bài quan trọng nhất
 *   section: hiểu sai ở đây thì 7 bài sau chỉ là cú pháp</li>
 *   <li>{@code t2_CreatingVirtualThreads} - {@code newVirtualThreadPerTaskExecutor},
 *   {@code Thread.ofVirtual()}, {@code Thread.Builder}, {@code ThreadFactory}</li>
 * </ol>
 *
 * <p><b>Nhóm 2 - API và thu kết quả</b>
 * <ol start="3">
 *   <li>{@code t3_ThreadApiChanges} - {@code isVirtual()}, {@code threadId()},
 *   {@code join(Duration)}, các method bị gỡ, và 5 hành vi virtual thread KHÁC hẳn
 *   platform thread (daemon, priority, thread group...)</li>
 *   <li>{@code t4_CapturingTaskResults} - {@code Future.get()}, {@code invokeAll}, và vì sao
 *   "blocking get" từ chỗ đáng xấu hổ thành chuyện bình thường. Kèm Structured Concurrency</li>
 * </ol>
 *
 * <p><b>Nhóm 3 - Ba cái bẫy khi lên production</b>
 * <ol start="5">
 *   <li>{@code t5_RateLimiting} - ĐỪNG pool virtual thread; chặn ở từng tài nguyên bằng
 *   {@code Semaphore} và connection pool. Bài sát production nhất</li>
 *   <li>{@code t6_Pinning} - carrier thread bị ghim. CHÚ Ý: trang gốc mô tả JDK 21-23;
 *   JEP 491 (JDK 24) đã đổi luật. File ghi rõ cả hai thời kỳ</li>
 *   <li>{@code t7_ThreadLocals} - vì sao {@code ThreadLocal} từ giải pháp thành vấn đề, và
 *   {@code ScopedValue} (JEP 506, chính thức ở JDK 25) thay thế thế nào</li>
 * </ol>
 *
 * <p><b>Nhóm 4 - Chốt lại</b>
 * <ol start="8">
 *   <li>{@code t8_Conclusion} - 5 gạch đầu dòng của trang gốc, kèm phần TỔNG KẾT cả section
 *   và checklist đưa virtual thread vào một dịch vụ Spring Boot thật</li>
 * </ol>
 *
 * <p><b>Những chủ đề production mà dev.java KHÔNG dạy, được rải vào các bài liên quan:</b>
 * định luật Little và vì sao pool 200 thread chặn throughput (t1), Spring Boot
 * {@code spring.threads.virtual.enabled} (t1, t8), bẫy try-with-resources trên
 * {@code ExecutorService} (t2), virtual thread luôn là daemon nên JVM không chờ (t3),
 * Structured Concurrency JEP 505 (t4), HikariCP thành nút cổ chai thật sự (t5),
 * JEP 491 và bốn nguyên nhân pinning còn lại trên JDK 24+ (t6), {@code ScopedValue} và
 * {@code SecurityContextHolder}/MDC (t7).
 */
package com.javacore.api.virtual_threads;
