/**
 * Introduction to Method Handles
 *
 * <p>Bước 28/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/reflection-method-handles/introduction_to_method_handles/">https://dev.java/learn/api/reflection-method-handles/introduction_to_method_handles/</a>
 *
 * <p>Section chỉ có một trang gốc (one-page article do Nataliia Dziubenko viết), nên một
 * file duy nhất:
 * <ol>
 *   <li>{@code t1_IntroductionToMethodHandles} - MethodHandle, MethodType, Lookup và cơ chế
 *       kiểm tra quyền truy cập lúc tạo, tra cứu member (findVirtual, findStatic, findConstructor),
 *       ba cách gọi (invokeExact, invoke, invokeWithArguments), truy cập field và mảng,
 *       bọc ngoại lệ (catchException, tryFinally), các bộ biến đổi adapter (asType, permuteArguments,
 *       insertArguments, filterArguments, foldArguments, filterReturnValue), đối chiếu với
 *       Reflection API và chuyển đổi hai chiều (unreflect, reflectAs).</li>
 * </ol>
 *
 * <p>Đây là bài CUỐI CÙNG của nhóm Reflection and Method Handles:
 * <ul>
 *   <li>Bước 26 ({@code introduction_to_java_reflection}): Khởi động trực quan, hiểu Reflection làm được gì.</li>
 *   <li>Bước 27 ({@code reflection}): Cẩm nang chi tiết toàn bộ Reflection API (13 bài).</li>
 *   <li>Bước 28 ({@code introduction_to_method_handles}): Cơ chế cấp thấp (low-level), nhanh hơn,
 *       linh hoạt hơn qua biến đổi pipeline, và là nền tảng cho invokedynamic cùng các tính năng
 *       Java hiện đại (lambda, chuỗi nối String Concat, VarHandle).</li>
 * </ul>
 *
 * <p>Những chủ đề production mà trang gốc KHÔNG dạy, được gom vào phần "ĐIỂM NGOÀI TRANG GỐC"
 * của bài:
 * <ul>
 *   <li>{@code @PolymorphicSignature} - cơ chế ma thuật của javac khi biên dịch lời gọi {@code invokeExact}/{@code invoke}.</li>
 *   <li>JIT inlining và quy tắc vàng {@code static final MethodHandle} để đạt hiệu năng tương đương gọi trực tiếp.</li>
 *   <li>Nền tảng của {@code invokedynamic}: {@code LambdaMetafactory} (Java 8+) và chuỗi nối tự động (JEP 280, Java 9+).</li>
 *   <li>Java Module System (JPMS) và {@code MethodHandles.privateLookupIn(Class, Lookup)}.</li>
 *   <li>Tổng quan về {@code VarHandle} (thay thế an toàn cho {@code sun.misc.Unsafe} và AtomicFieldUpdater).</li>
 * </ul>
 */
package com.javacore.api.reflection_method_handles.introduction_to_method_handles;
