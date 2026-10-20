/**
 * Introduction to Java Reflection
 *
 * <p>Bước 26/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/reflection-method-handles/introduction_to_java_reflection/">https://dev.java/learn/api/reflection-method-handles/introduction_to_java_reflection/</a>
 *
 * <p>Section chỉ có một trang gốc (one-page article do Dr Heinz M. Kabutz viết), nên một
 * file duy nhất:
 * <ol>
 *   <li>{@code t1_IntroductionToJavaReflection} - lớp {@code Class} và ba cách lấy nó,
 *       {@code ClassLoader}, shallow reflection (liệt kê và gọi member public),
 *       deep reflection ({@code setAccessible}, sửa field private final),
 *       và giới hạn của Java Module System</li>
 * </ol>
 *
 * <p>Đây là bài MỞ ĐẦU của nhóm Reflection and Method Handles. Nó cố ý nông và vui -
 * mục tiêu là thấy được reflection làm được gì. Bước 27 ({@code reflection}) mới là
 * tài liệu tham khảo đầy đủ, bước 28 ({@code introduction_to_method_handles}) là cách
 * làm cùng việc đó nhưng nhanh hơn.
 *
 * <p>Những chủ đề production mà trang gốc KHÔNG dạy, được gom vào phần "ĐIỂM NGOÀI TRANG
 * GỐC" của bài: khác biệt {@code getX} với {@code getDeclaredX}, giá phải trả của
 * reflection (hiệu năng, mất kiểm tra lúc biên dịch), {@code InaccessibleObjectException}
 * từ JDK 16, và nơi bạn thật sự gặp reflection hằng ngày - Spring, Jackson, Hibernate,
 * JUnit, cùng hệ quả khi build GraalVM native image.
 */
package com.javacore.api.reflection_method_handles.introduction_to_java_reflection;
