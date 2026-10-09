/**
 * Virtual Threads
 *
 * <p>Bước 29/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/virtual-threads/">https://dev.java/learn/api/virtual-threads/</a>
 *
 * <p>Nội dung cần thực hành:
 * <ul>
 *   <li>Vì sao cần virtual thread, bài toán thread-per-request</li>
 *   <li>Tạo virtual thread: Thread.ofVirtual, newVirtualThreadPerTaskExecutor</li>
 *   <li>Thay đổi trong Thread API</li>
 *   <li>Thu kết quả tác vụ và Structured Concurrency</li>
 *   <li>Rate limiting bằng Semaphore</li>
 *   <li>Pinning: nguyên nhân và cách tránh</li>
 *   <li>ThreadLocal với virtual thread</li>
 *   <li>Virtual thread so với platform thread</li>
 *   <li>Nền tảng cần nắm trước: Thread, Runnable, Callable, Future</li>
 *   <li>synchronized, volatile, race condition, deadlock</li>
 *   <li>ExecutorService và CompletableFuture</li>
 *   <li>Concurrent collections và các lớp Atomic</li>
 * </ul>
 */
package com.javacore.api.virtual_threads;
