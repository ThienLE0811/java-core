/**
 * Phần 6 - Concurrency.
 *
 * <p>Bài 34 ({@code chuyentien}): race condition, synchronized, AtomicInteger,
 * ReentrantLock, deadlock và lock ordering.
 *
 * <p>Bài 35 ({@code taifile}, {@code demtu}): ExecutorService, Future,
 * invokeAll, shutdown đúng cách, ConcurrentHashMap và virtual thread.
 *
 * <p>Bài 36 ({@code dashboard}): CompletableFuture, thenApply/thenCompose/
 * thenCombine/allOf, exceptionally, orTimeout và completeOnTimeout.
 *
 * <p>Khác các package trước: phần lớn nội dung ở đây phải <em>chạy và quan sát</em>
 * mới thấy, nên trọng tâm nằm ở các class {@code ...Demo} chứ không phải ở test.
 * Test chỉ kiểm những bất biến luôn đúng bất kể thread xen kẽ ra sao.
 */
package com.javacore.practice.javacore33.concurrency;
