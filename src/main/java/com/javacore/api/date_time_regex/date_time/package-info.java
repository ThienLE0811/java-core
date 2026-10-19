/**
 * The Date Time API
 *
 * <p>Bước 24/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/api/date-time-regex/date-time/">https://dev.java/learn/api/date-time-regex/date-time/</a>
 *
 * <p>Section gồm 13 trang gốc chia 5 nhóm, mỗi trang một file {@code tN_*.java}:
 *
 * <p><b>Mở đầu</b> - từ vựng và bản đồ
 * <ol>
 *   <li>{@code t1_DateTimeApiOverview} - ISO-8601, CLDR, TZDB, bốn nguyên tắc thiết kế,
 *   bảng tiền tố method (of/from/parse/with/plus/at/to)</li>
 *   <li>{@code t2_StandardCalendar} - human time so với machine time, bảng tra 13 lớp,
 *   chọn lớp nào cho bài toán nào</li>
 * </ol>
 *
 * <p><b>Các lớp biểu diễn một ĐIỂM thời gian</b>
 * <ol start="3">
 *   <li>{@code t3_DayOfWeekAndMonthEnums} - enum 1-based, TextStyle, Locale</li>
 *   <li>{@code t4_DateClasses} - LocalDate, YearMonth, MonthDay, Year; bẫy plusMonths cắt ngày</li>
 *   <li>{@code t5_DateAndTimeClasses} - LocalTime, LocalDateTime; "Local" nghĩa là KHÔNG có múi giờ</li>
 *   <li>{@code t6_TimeZoneAndOffset} - ZoneId so với ZoneOffset, ZonedDateTime,
 *   OffsetDateTime, gap và overlap DST. BÀI QUAN TRỌNG NHẤT</li>
 *   <li>{@code t7_InstantClass} - thời gian máy, epoch, giới hạn đơn vị lịch</li>
 * </ol>
 *
 * <p><b>Biên giới hệ thống</b>
 * <ol start="8">
 *   <li>{@code t8_ParsingAndFormatting} - DateTimeFormatter là hằng static final;
 *   bẫy YYYY, mm, DD, hh</li>
 * </ol>
 *
 * <p><b>Tầng nền và LƯỢNG thời gian</b>
 * <ol start="9">
 *   <li>{@code t9_TemporalPackage} - Temporal, ChronoField, ChronoUnit, TemporalAdjusters,
 *   TemporalQuery</li>
 *   <li>{@code t10_PeriodAndDuration} - Period đếm lịch, Duration đếm giây,
 *   ChronoUnit.between; bẫy getDays()</li>
 *   <li>{@code t11_Clock} - trang gốc chỉ là stub nhưng đây là bài giá trị production cao
 *   nhất: tiêm Clock để test được logic phụ thuộc thời gian</li>
 * </ol>
 *
 * <p><b>Tương thích</b>
 * <ol start="12">
 *   <li>{@code t12_NonIsoDateConversion} - java.time.chrono, lịch Nhật/Hijrah/Minguo/Thái;
 *   đọc một lần là đủ</li>
 *   <li>{@code t13_LegacyDateTimeCode} - sáu method cầu nối sang java.util.Date/Calendar,
 *   chiến lược refactor, và phần TỔNG KẾT cả section</li>
 * </ol>
 *
 * <p>Ba chủ đề production mà dev.java KHÔNG dạy, được rải vào các bài liên quan:
 * map java.time trong JPA và kiểu cột Postgres/Oracle (t2, t6, t7), cấu hình Jackson (t7, t13),
 * lịch âm Việt Nam (t12).
 */
package com.javacore.api.date_time_regex.date_time;
