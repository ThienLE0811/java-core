/**
 * Modules
 *
 * <p>Bước 30/41 trong lộ trình dev.java/learn.
 *
 * <p>Tài liệu: <a href="https://dev.java/learn/organizing/modules/">https://dev.java/learn/organizing/modules/</a>
 *
 * <p><b>Cấu trúc:</b> dev.java chia section này thành 12 trang con. Repo tách thành 12 file
 * {@code tN_*.java} theo ĐÚNG thứ tự và tên chương gốc.
 *
 * <p><b>Nền tảng cần nắm TRƯỚC khi vào section</b> - trang gốc coi như đã biết:
 * <ul>
 *   <li>{@code package}, classpath, cách {@code javac} / {@code java} tìm class</li>
 *   <li>Đóng gói JAR, phân biệt thư viện và ứng dụng</li>
 *   <li>Reflection ở mức cơ bản (bước 27) - cần cho bài t2, t10, t11</li>
 * </ul>
 *
 * <p><b>Mười hai bài, mỗi bài một file:</b>
 *
 * <p><b>Nhóm 1 - Nền tảng</b>
 * <ol>
 *   <li>{@code t1_IntroductionToModules} - module là gì, {@code module-info.java},
 *   {@code requires} / {@code exports}, module path so với class path, readability và
 *   strong encapsulation. Bài dài nhất và quan trọng nhất section</li>
 * </ol>
 *
 * <p><b>Nhóm 2 - Tinh chỉnh khai báo trong module-info</b>
 * <ol start="2">
 *   <li>{@code t2_ReflectiveAccessWithOpenModules} - {@code opens} và {@code open module},
 *   vì sao {@code exports} KHÔNG đủ cho framework dùng reflection (Spring, Jackson, JPA)</li>
 *   <li>{@code t3_OptionalDependenciesWithRequiresStatic} - phụ thuộc chỉ cần lúc biên dịch,
 *   lúc chạy có thể vắng mặt</li>
 *   <li>{@code t4_ImpliedReadabilityWithRequiresTransitive} - đọc một module thì đọc luôn
 *   được module nó kéo theo; dùng khi kiểu dữ liệu của module khác lộ ra trong API</li>
 *   <li>{@code t5_QualifiedExportsAndOpens} - {@code exports ... to} và {@code opens ... to},
 *   mở cửa cho ĐÚNG một vài module thay vì cho tất cả</li>
 * </ol>
 *
 * <p><b>Nhóm 3 - Tách rời phụ thuộc</b>
 * <ol start="6">
 *   <li>{@code t6_DecouplingModulesWithServices} - {@code provides ... with}, {@code uses},
 *   {@code ServiceLoader}: phụ thuộc vào interface, không phụ thuộc vào module cài đặt</li>
 * </ol>
 *
 * <p><b>Nhóm 4 - Di cư dần từ thế giới classpath</b>
 * <ol start="7">
 *   <li>{@code t7_TheUnnamedModule} - mọi thứ còn nằm trên class path gom vào một "unnamed
 *   module": nó đọc được tất cả, nhưng không module tường minh nào đọc được nó</li>
 *   <li>{@code t8_AutomaticModules} - đặt JAR CHƯA modular hóa lên module path thì nó thành
 *   automatic module; đây là đường di cư từng bước cho dự án cũ</li>
 * </ol>
 *
 * <p><b>Nhóm 5 - Dòng lệnh và các cờ phá rào</b>
 * <ol start="9">
 *   <li>{@code t9_BuildingModulesOnTheCommandLine} - biên dịch, đóng gói, chạy module bằng
 *   {@code javac} / {@code jar} / {@code java} thuần, không qua Maven. Bài thực hành gõ tay</li>
 *   <li>{@code t10_StrongEncapsulationOfJdkInternals} - vì sao {@code sun.misc.Unsafe} và
 *   các API nội bộ JDK không còn truy cập được; lịch sử siết dần qua các bản Java</li>
 *   <li>{@code t11_CircumventingStrongEncapsulation} - {@code --add-exports} và
 *   {@code --add-opens}: lối thoát hiểm khi thư viện bên thứ ba còn chọc vào API nội bộ</li>
 *   <li>{@code t12_ExtendingTheModuleGraph} - {@code --add-modules} và {@code --add-reads}:
 *   thêm module vào đồ thị và thêm quan hệ đọc mà không sửa {@code module-info.java}</li>
 * </ol>
 */
package com.javacore.organizing.modules;
