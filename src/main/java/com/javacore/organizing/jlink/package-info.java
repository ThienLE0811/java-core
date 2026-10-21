/**
 * Creating Runtime and Application Images with JLink
 *
 * <p>Bước 31/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/organizing/jlink/">https://dev.java/learn/organizing/jlink/</a>
 *
 * <p><b>Cấu trúc:</b> dev.java để cả section này trong MỘT trang duy nhất, nên repo cũng chỉ
 * một file {@code t1_*.java}.
 *
 * <p><b>Nền tảng cần nắm TRƯỚC khi vào section</b> - trang gốc coi như đã biết:
 * <ul>
 *   <li>Toàn bộ section Modules (bước 30), đặc biệt {@code --add-modules} (t12),
 *   automatic module (t8) và services (t6) - ba thứ này quyết định {@code jlink} chạy
 *   được hay không</li>
 *   <li>Ba lệnh {@code javac} / {@code jar} / {@code java} ở t9</li>
 * </ul>
 *
 * <p><b>Một bài, một file:</b>
 * <ol>
 *   <li>{@code t1_CreatingRuntimeAndApplicationImagesWithJLink} - {@code jlink} cắt JDK
 *   thành runtime image và application image; {@code --launcher}, service binding,
 *   cross-OS, các cờ tối ưu. Kèm phần ngoài trang gốc: số đo thật trên JDK 25,
 *   {@code jdeps}, {@code jpackage}, và cách dùng với Docker</li>
 * </ol>
 *
 * <p><b>Câu mang đi:</b> {@code jlink} chỉ LINK bytecode, không biên dịch sang mã máy - nó
 * không phải GraalVM native-image. Giá trị thật của nó với backend là cắt base image Docker.
 */
package com.javacore.organizing.jlink;
