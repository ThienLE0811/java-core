/**
 * Phần 6 - JVM và những cái bẫy.
 *
 * <p>Bài 37 ({@code BayJvmDemo}): Integer cache, String pool, chi phí nối chuỗi
 * bằng toán tử {@code +} trong vòng lặp, và Java truyền tham số theo giá trị.
 *
 * <p>Bài 37 phần 3 ({@code TranBoNhoDemo}): stack so với heap, qua hai lỗi
 * {@code StackOverflowError} và {@code OutOfMemoryError}; kèm một khối GC cơ
 * bản - cùng lượng cấp phát, giữ tham chiếu thì tràn heap còn vứt đi thì không.
 * Class này cần VM option {@code -Xmx16m}.
 *
 * <p>Giống package {@code concurrency}: nội dung ở đây phải <em>chạy và quan
 * sát</em> mới thấy, nên không có test - trọng tâm nằm ở output của các class
 * {@code ...Demo}. Cách dùng: đọc đề, tự ghi dự đoán ra giấy, rồi mới chạy.
 */
package com.javacore.practice.javacore33.jvm;
