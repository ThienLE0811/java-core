/**
 * The Core JDK Tools
 *
 * <p>Bước 32/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/jvm/core-tools/">https://dev.java/learn/jvm/core-tools/</a>
 *
 * <p>Trang gốc có 12 trang con, repo giữ ánh xạ 1-1 thành 12 file {@code t1_} đến
 * {@code t12_} (cùng quy ước với section Reflection và section JLink):
 * <ul>
 *   <li>{@code t1_} Introducing the Core JDK Tools - bản đồ cả section</li>
 *   <li>{@code t2_} Javac - the Compiler</li>
 *   <li>{@code t3_} Javap - the Disassembler</li>
 *   <li>{@code t4_} Javadoc - the Documentation Generator</li>
 *   <li>{@code t5_} Java - Your Application Launcher</li>
 *   <li>{@code t6_} JShell - The Java Shell Tool</li>
 *   <li>{@code t7_} Advanced JShell Usage</li>
 *   <li>{@code t8_} Jar - the Archive Tool</li>
 *   <li>{@code t9_} Jlink - trỏ sang bài đầy đủ ở {@code organizing.jlink}</li>
 *   <li>{@code t10_} Jmod - Create JMOD archives</li>
 *   <li>{@code t11_} Jdeps - Analyze your Java Classes Dependencies</li>
 *   <li>{@code t12_} Jdeprscan - Deprecated API Elements Scanner</li>
 * </ul>
 *
 * <p>Lưu ý chung cho cả section: phần lớn các trang gốc là bản chép man page, nên note
 * trong từng file cố tình KHÔNG chép lại bảng option. Thứ được ghi lại là những gì không
 * đoán được, cùng kết quả đo thật trên JDK 25.0.4.1 / Windows.
 *
 * <p>Bốn bài {@code t5_}, {@code t6_}, {@code t7_}, {@code t12_} phải gọi tool bằng
 * {@code ProcessBuilder} vì {@code java}, {@code jshell}, {@code jdeprscan} không có
 * {@code java.util.spi.ToolProvider} - xem {@code t1_} mục 5.
 */
package com.javacore.jvm.core_tools;
