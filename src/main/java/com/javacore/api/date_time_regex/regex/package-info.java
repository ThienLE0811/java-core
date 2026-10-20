/**
 * Regular Expressions
 *
 * <p>Bước 25/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/date-time-regex/regex/">https://dev.java/learn/api/date-time-regex/regex/</a>
 *
 * <p>Section gồm 10 trang gốc chia 3 nhóm, mỗi trang một file {@code tN_*.java}:
 *
 * <p><b>Mở đầu</b> - từ vựng và bộ đồ nghề
 * <ol>
 *   <li>{@code t1_IntroducingRegex} - ba lớp Pattern/Matcher/PatternSyntaxException,
 *   hỗ trợ Unicode, test harness. Bẫy backslash nhân đôi và chuyện thread-safe</li>
 * </ol>
 *
 * <p><b>Cú pháp</b> - sáu bài xây dần một biểu thức
 * <ol start="2">
 *   <li>{@code t2_StringLiterals} - chỉ số start/end, metacharacter, {@code \Q \E},
 *   {@code Pattern.quote}</li>
 *   <li>{@code t3_CharacterClasses} - liệt kê, phủ định, dải, hợp, giao, trừ;
 *   bẫy {@code [a-z]} không chứa chữ tiếng Việt</li>
 *   <li>{@code t4_PredefinedCharacterClasses} - {@code . \d \s \w} và phần bù;
 *   bẫy chỉ-ASCII và cờ UNICODE_CHARACTER_CLASS</li>
 *   <li>{@code t5_Quantifiers} - greedy, reluctant, possessive; zero-length match;
 *   catastrophic backtracking và ReDoS. BÀI QUAN TRỌNG NHẤT</li>
 *   <li>{@code t6_CapturingGroups} - đánh số group, backreference, named group,
 *   non-capturing, lookahead và lookbehind</li>
 *   <li>{@code t7_Boundaries} - {@code ^ $ \b \B \A \G \Z \z}; MULTILINE đổi nghĩa
 *   {@code ^ $}; bẫy bảo mật {@code $} bỏ qua xuống dòng cuối</li>
 * </ol>
 *
 * <p><b>API</b> - hai lớp và một exception
 * <ol start="8">
 *   <li>{@code t8_PatternClass} - compile một lần là quy tắc production số một;
 *   các cờ compile; split và bẫy mất phần tử rỗng ở đuôi; API stream</li>
 *   <li>{@code t9_MatcherClass} - matches/lookingAt/find; nhóm replace và bẫy dấu
 *   {@code $}; Matcher có trạng thái; {@code results()} của Java 9</li>
 *   <li>{@code t10_PatternSyntaxException} - lỗi lúc chạy chứ không lúc biên dịch,
 *   và phần TỔNG KẾT cả section</li>
 * </ol>
 *
 * <p>Những chủ đề production mà dev.java KHÔNG dạy, được rải vào các bài liên quan:
 * ReDoS và cách phòng (t5, t10), named group và lookaround (t6), API Java 9+ như
 * {@code results()} và {@code replaceAll(Function)} (t8, t9), xử lý tiếng Việt với
 * NFC/NFD và {@code \p{L}} (t3, t4, t7), regex trong Spring và trong Postgres/Oracle (t10).
 */
package com.javacore.api.date_time_regex.regex;
