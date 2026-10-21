/**
 * Sân tập - bài tự luyện, KHÔNG thuộc lộ trình 41 bước của dev.java.
 *
 * <p>Khác với {@code first_steps}, {@code language}, {@code api}, {@code organizing}: cây
 * package ở những chỗ đó mirror đúng URL dev.java, còn ở đây chia theo chủ đề tự chọn.
 *
 * <p>Quy ước của package này:
 * <ul>
 *   <li>Logic nằm ở {@code src/main}, kiểm chứng nằm ở {@code src/test} với package TRÙNG TÊN.</li>
 *   <li>Không dùng {@code main()} + {@code println} để xem kết quả - dùng assertion của JUnit.
 *       Lý do: {@code mvn test} chạy được cả trăm bài một lượt và tự báo sai, còn {@code println}
 *       thì phải đọc bằng mắt từng dòng.</li>
 *   <li>Mỗi nhánh quyết định trong code phải có một test tương ứng.</li>
 * </ul>
 *
 * <p>Chạy: {@code mvn test}
 */
package com.javacore.practice;
