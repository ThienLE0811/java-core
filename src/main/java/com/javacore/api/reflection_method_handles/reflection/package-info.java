/**
 * The Reflection API
 *
 * <p>Bước 27/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/reflection-method-handles/reflection/">https://dev.java/learn/api/reflection-method-handles/reflection/</a>
 *
 * <p>Section gồm 13 trang gốc chia 4 nhóm, mỗi trang một file {@code tN_*.java}:
 *
 * <p><b>Mở đầu</b> - cân nhắc trước khi dùng
 * <ol>
 *   <li>{@code t1_IntroducingTheReflectionApi} - ba ca dùng chính đáng và ba cái giá phải
 *   trả. Bài ngắn nhất section nhưng quyết định bạn có nên dùng reflection hay không</li>
 * </ol>
 *
 * <p><b>Lấy và đọc thông tin lớp</b> - vào cửa và đọc biển tên
 * <ol start="2">
 *   <li>{@code t2_RetrievingClasses} - năm cách lấy {@code Class}: {@code getClass()},
 *   {@code .class}, {@code forName}, {@code forPrimitiveName} (Java 22), {@code Wrapper.TYPE};
 *   và đi ngang qua lớp cha, interface, member class, declaring so với enclosing class</li>
 *   <li>{@code t3_ReadingClassNames} - bốn kiểu tên ({@code getName},
 *   {@code getSimpleName}, {@code getCanonicalName}, {@code getTypeName}) và cú pháp mã hóa
 *   tên mảng kiểu {@code [[I}. Bẫy: chỉ {@code getName} mới nạp lại được lớp</li>
 *   <li>{@code t4_ReadingModifiers} - {@code Modifier} trên lớp và trên mọi member</li>
 * </ol>
 *
 * <p><b>Tác động lên member</b> - phần lõi, dùng hằng ngày
 * <ol start="5">
 *   <li>{@code t5_ReadingAndWritingFields} - {@code Field}, {@code get}/{@code set},
 *   {@code setAccessible}, generic type của field</li>
 *   <li>{@code t6_InvokingMethods} - {@code Method}, {@code invoke}, nạp chồng, ghi đè,
 *   bridge method, {@code InvocationTargetException}</li>
 *   <li>{@code t7_InvokingConstructors} - {@code Constructor}, {@code newInstance}, và vì
 *   sao {@code Class.newInstance()} bị deprecate</li>
 *   <li>{@code t8_WorkingWithArrays} - lớp {@code java.lang.reflect.Array}</li>
 *   <li>{@code t9_WorkingWithEnumerations} - hằng enum qua reflection</li>
 *   <li>{@code t10_WorkingWithRecords} - {@code isRecord}, {@code getRecordComponents}</li>
 *   <li>{@code t11_ReadingAnnotations} - {@code RetentionPolicy}, annotation lặp lại,
 *   annotation trên kiểu</li>
 * </ol>
 *
 * <p><b>Ráp lại</b> - hai bài dự án nhỏ, đây là nơi cả section có nghĩa
 * <ol start="12">
 *   <li>{@code t12_CreatingAnInterceptor} - dựng interceptor bằng annotation, chính là
 *   nguyên lý của {@code @Transactional} và Spring AOP</li>
 *   <li>{@code t13_CreatingADependencyInjectionFramework} - dựng một DI container thu nhỏ,
 *   chính là nguyên lý của {@code @Autowired}. Kèm phần TỔNG KẾT cả section</li>
 * </ol>
 *
 * <p>Học sau bước 26 ({@code introduction_to_java_reflection}). Bước 26 cho cảm giác
 * reflection làm được gì; section này là tài liệu tham khảo đầy đủ.
 *
 * <p>Những chủ đề production mà dev.java KHÔNG dạy, được rải vào các bài liên quan:
 * cache {@code Field}/{@code Method} vào {@code static final} (t5, t6), type erasure và
 * nhánh API generic (t2, t5), {@code InaccessibleObjectException} trên JDK 17+ (t5),
 * {@code Proxy} và {@code InvocationHandler} (t12), reflection trong Spring/Jackson/
 * Hibernate và hệ quả với GraalVM native image (t13).
 */
package com.javacore.api.reflection_method_handles.reflection;
