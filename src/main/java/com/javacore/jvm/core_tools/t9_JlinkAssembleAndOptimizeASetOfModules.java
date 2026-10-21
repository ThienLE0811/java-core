package com.javacore.jvm.core_tools;

import java.util.spi.ToolProvider;

/**
 * Jlink - Assemble and Optimize a Set of Modules
 * <p>
 * 0. Vị trí bài này - ĐỌC TRƯỚC KHI ĐỌC TIẾP
 * - Bài 9/12 của section The Core JDK Tools.
 * - Tài liệu gốc:
 * <a href="https://dev.java/learn/jvm/core-tools/jlink/">dev.java - Jlink (bản man page)</a>
 * - BÀI NÀY CỐ TÌNH NGẮN. Toàn bộ lý thuyết {@code jlink} đã nằm ở
 * {@code com.javacore.organizing.jlink.t1_CreatingRuntimeAndApplicationImagesWithJLink} -
 * bài của bước 31/41, dựa trên trang
 * <a href="https://dev.java/learn/organizing/jlink/">dev.java - Organizing / JLink</a>.
 * - Hai trang dev.java nói về cùng một tool nhưng khác thể loại:
 * + Trang Organizing: hướng dẫn có mạch, 8 mục, giải thích VÌ SAO.
 * + Trang Core Tools (trang của bài này): man page, 7 mục, chỉ liệt kê option và plugin.
 * - Nên ở đây chỉ ghi phần KHÔNG có trong bài cũ: vai trò của {@code jlink} trong bộ tool,
 * và những gì bản JDK 25 đã đổi so với trang gốc.
 * <p>
 * ======================================================================
 * PHẦN RIÊNG CỦA TRANG CORE TOOLS
 * ======================================================================
 * <p>
 * 1. VỊ TRÍ TRONG DÂY CHUYỀN - CÁCH NHỚ CẢ SECTION
 * - {@code javac} ra {@code .class} -&gt; {@code jar}/{@code jmod} gói lại -&gt;
 * {@code jdeps} cho biết cần module nào -&gt; {@code jlink} ghép thành runtime -&gt;
 * {@code jpackage} bọc thành installer của hệ điều hành.
 * - {@code jlink} là mắt xích DUY NHẤT tạo ra thứ chạy được mà không cần cài sẵn JDK.
 * - Đầu vào bắt buộc: module path ({@code -p}) và tập module gốc ({@code --add-modules}).
 * Thiếu một trong hai là không chạy.
 * <p>
 * 2. PLUGIN - MỤC MÀ TRANG ORGANIZING KHÔNG CÓ
 * - Trang man page có hẳn một mục "Plugins": {@code jlink} được dựng trên một chuỗi plugin,
 * và các cờ tối ưu thực chất là bật/tắt plugin.
 * - {@code jlink --list-plugins} liệt kê toàn bộ. Đây là cách tra đúng tên cờ cho BẢN JDK
 * ĐANG DÙNG, thay vì tin vào tài liệu có thể đã cũ.
 * - Plugin hay dùng: {@code --strip-debug}, {@code --no-header-files},
 * {@code --no-man-pages}, {@code --compress}, {@code --include-locales},
 * {@code --order-resources}.
 * <p>
 * 3. {@code --compress} ĐÃ ĐỔI CÚ PHÁP - ĐO THẬT TRÊN JDK 25.0.4.1
 * - Tài liệu cũ hay viết {@code --compress=0|1|2} (0 = không nén, 1 = chia sẻ chuỗi,
 * 2 = zip).
 * - {@code jlink --help} trên JDK 25 nói nguyên văn: giá trị nhận vào là
 * {@code zip-[0-9]}, {@code zip-0} là không nén, {@code zip-9} nén mạnh nhất, và mặc
 * định là {@code zip-6}.
 * - Cú pháp số cũ VẪN CHẠY nhưng đã bị đánh dấu bỏ. Đo thật với
 * {@code jlink --compress=1}:
 * {@code Warning: The 1 argument for --compress is deprecated and may be removed in a
 * future release} - mã thoát vẫn 0.
 * - Tức là đây là giai đoạn chuyển tiếp: script cũ chưa gãy, nhưng sẽ gãy. Luôn tra
 * {@code --help} của bản đang chạy trước khi chép lệnh từ blog.
 * <p>
 * 4. BA CÁI BẪY ĐÃ GHI Ở BÀI CŨ - NHẮC LẠI MỘT DÒNG MỖI CÁI
 * - {@code --output} trỏ vào thư mục ĐÃ TỒN TẠI là lỗi ngay, phải trỏ vào đường dẫn chưa có.
 * - {@code --bind-services} kéo theo rất nhiều module không cần; mặc định đừng bật.
 * - {@code jlink} KHÔNG làm việc được với automatic module - nên một ứng dụng Spring Boot
 * điển hình không {@code jlink} thẳng được. Cách khả dĩ là {@code jlink} một runtime gọn
 * rồi chạy fat jar trên runtime đó.
 * - Chi tiết, số đo và thông báo lỗi đầy đủ: xem bài {@code organizing.jlink.t1_...} mục
 * 10 đến 13.
 * <p>
 * ======================================================================
 * CHECKLIST TỰ KIỂM TRA
 * ======================================================================
 * <p>
 * 5. TRẢ LỜI ĐƯỢC KHÔNG CẦN TRA THÌ COI NHƯ XONG BÀI
 * - Dây chuyền 5 tool từ {@code .java} tới installer gồm những gì, theo thứ tự?
 * - Hai cờ bắt buộc của {@code jlink}?
 * - {@code --compress} trên JDK 25 nhận giá trị gì?
 * - Lệnh nào liệt kê các plugin có thật trên bản JDK đang dùng?
 * - Vì sao Spring Boot không {@code jlink} thẳng được?
 */
public class t9_JlinkAssembleAndOptimizeASetOfModules {

    void main() {
        ToolProvider jlink = ToolProvider.findFirst("jlink").orElseThrow(
                () -> new IllegalStateException("không thấy jlink - đang chạy trên JRE?"));

        // 1. Tra cú pháp --compress của CHÍNH bản JDK đang chạy, thay vì tin tài liệu (mục 3).
        System.out.println("=== jlink --help, phần --compress ===");
        String help = capture(jlink, "--help");
        boolean printing = false;
        for (String line : help.lines().toList()) {
            if (line.contains("--compress")) {
                printing = true;
            } else if (printing && line.strip().startsWith("--")) {
                break;
            }
            if (printing) {
                System.out.println(line);
            }
        }

        // 2. Liệt kê plugin có thật trên bản này (mục 2). Chỉ in tên cho gọn.
        System.out.println("\n=== jlink --list-plugins (chỉ tên plugin) ===");
        capture(jlink, "--list-plugins").lines()
                .filter(line -> line.strip().startsWith("--"))
                .map(line -> "  " + line.strip())
                .forEach(System.out::println);

        System.out.println("""

                Lý thuyết đầy đủ + số đo kích thước image: xem
                com.javacore.organizing.jlink.t1_CreatingRuntimeAndApplicationImagesWithJLink""");
    }

    private static String capture(ToolProvider tool, String... args) {
        var out = new java.io.StringWriter();
        try (var writer = new java.io.PrintWriter(out)) {
            tool.run(writer, writer, args);
        }
        return out.toString();
    }
}
