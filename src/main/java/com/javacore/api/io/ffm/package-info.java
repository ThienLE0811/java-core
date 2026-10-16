/**
 * The Foreign Function and Memory API
 *
 * <p>Bước 23/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/io/ffm/">https://dev.java/learn/api/io/ffm/</a>
 *
 * <p>Section gồm 6 trang gốc, mỗi trang một file {@code tN_*.java}:
 * <ol>
 *   <li>{@code t1_MemorySegmentAndArena} - MemorySegment, Arena, bộ nhớ ngoài heap</li>
 *   <li>{@code t2_InvokeCLibraryFunction} - Linker, SymbolLookup, FunctionDescriptor</li>
 *   <li>{@code t3_AccessNativeDataTypes} - struct, MemoryLayout, VarHandle</li>
 *   <li>{@code t4_Upcall} - native gọi ngược lại code Java</li>
 *   <li>{@code t5_Troubleshoot} - lỗi thường gặp khi gọi hàm native</li>
 *   <li>{@code t6_Jextract} - sinh binding Java từ header C</li>
 * </ol>
 *
 * <p>Khác các section trước: từ {@code t2} trở đi cần thư viện native và cờ
 * {@code --enable-native-access=ALL-UNNAMED} khi chạy, nếu không JVM sẽ in cảnh báo
 * (JDK 24+ còn có thể chặn). Nếu không dựng được môi trường thì ghi note lý thuyết trước,
 * code thực hành bổ sung sau.
 */
package com.javacore.api.io.ffm;
